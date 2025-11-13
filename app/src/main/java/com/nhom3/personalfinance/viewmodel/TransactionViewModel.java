package com.nhom3.personalfinance.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nhom3.personalfinance.data.db.AppDatabase;
import com.nhom3.personalfinance.data.db.dao.CategoryDao;
import com.nhom3.personalfinance.data.db.dao.TransactionDao;
import com.nhom3.personalfinance.data.db.dao.WalletDao;
import com.nhom3.personalfinance.data.model.SubCategory;
import com.nhom3.personalfinance.data.model.Transaction;
import com.nhom3.personalfinance.data.model.Wallet;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionViewModel extends AndroidViewModel {

    private TransactionDao transactionDao;
    private WalletDao walletDao;
    private CategoryDao categoryDao;
    private ExecutorService executorService;
    // --- BIẾN MỚI ĐỂ LƯU NGÀY TÙY CHỈNH ---
    private Date customStartDate;
    private Date customEndDate;
    // --- HẾT BIẾN MỚI ---

    // LiveData cho Spinner (không đổi)
    private MutableLiveData<List<Wallet>> allWallets = new MutableLiveData<>();
    private MutableLiveData<List<SubCategory>> incomeCategories = new MutableLiveData<>();
    private MutableLiveData<List<SubCategory>> expenseCategories = new MutableLiveData<>();

    // --- SỬA: LiveData cho Danh sách Giao dịch ---
    // Sẽ giữ danh sách đã được lọc
    private MutableLiveData<List<Transaction>> filteredTransactions = new MutableLiveData<>();
    // Giữ tổng số dư (cho TextView trên cùng)
    private MutableLiveData<Double> totalBalance = new MutableLiveData<>();

    // Biến giữ bộ lọc hiện tại
    private String currentDateFilter = "this_month";
    private String currentTypeFilter = "all";

    public TransactionViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getDatabase(application);
        transactionDao = database.transactionDao();
        walletDao = database.walletDao();
        categoryDao = database.categoryDao();
        executorService = Executors.newSingleThreadExecutor();

        loadInitialData(); // Tải data cho Spinner
        loadFilteredTransactions(); // Tải data cho danh sách
    }

    private void loadInitialData() {
        executorService.execute(() -> {
            // Tải ví (dùng cho Spinner và tính tổng số dư)
            List<Wallet> wallets = walletDao.getAllWallets();
            allWallets.postValue(wallets);

            // Tính tổng số dư
            double total = 0;
            for (Wallet w : wallets) {
                total += w.balance;
            }
            totalBalance.postValue(total);

            // Tải danh mục cho Spinner
            List<SubCategory> incomes = categoryDao.getSubCategoriesByCategoryId(1);
            incomeCategories.postValue(incomes);
            List<SubCategory> expenses = categoryDao.getSubCategoriesByCategoryId(2);
            expenseCategories.postValue(expenses);
        });
    }

    // --- HÀM LỌC CHÍNH ---
    public void loadFilteredTransactions() {
        executorService.execute(() -> {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0); // 00:00:00
            cal.clear(Calendar.MINUTE);
            cal.clear(Calendar.SECOND);
            cal.clear(Calendar.MILLISECOND);

            Date startDate, endDate;

            // 1. Tính toán Ngày
            if ("this_month".equals(currentDateFilter)) {
                cal.set(Calendar.DAY_OF_MONTH, 1);
                startDate = cal.getTime();

                cal.add(Calendar.MONTH, 1);
                cal.add(Calendar.DAY_OF_YEAR, -1);
                endDate = cal.getTime();
            } else if ("last_month".equals(currentDateFilter)) {
                cal.add(Calendar.MONTH, -1);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                startDate = cal.getTime();

                cal.add(Calendar.MONTH, 1);
                cal.add(Calendar.DAY_OF_YEAR, -1);
                endDate = cal.getTime();
            } else { // --- THÊM PHẦN XỬ LÝ NÀY ---
                // "custom"
                startDate = customStartDate;
                endDate = customEndDate;
            }
            // (Bạn có thể thêm "Tùy chọn" sau)

            // 2. Lấy dữ liệu từ DAO theo bộ lọc
            List<Transaction> transactions;
            if ("income".equals(currentTypeFilter)) {
                transactions = transactionDao.getIncomeTransactionsBetweenDates(startDate, endDate);
            } else if ("expense".equals(currentTypeFilter)) {
                transactions = transactionDao.getExpenseTransactionsBetweenDates(startDate, endDate);
            } else { // "all"
                transactions = transactionDao.getTransactionsBetweenDates(startDate, endDate);
            }

            filteredTransactions.postValue(transactions);
        });
    }
    // --- HÀM MỚI ĐỂ FRAGMENT GỌI ---
    public void setCustomDateFilter(Date startDate, Date endDate) {
        this.currentDateFilter = "custom";
        this.customStartDate = startDate;
        this.customEndDate = endDate;
        // (Không cần gọi loadFilteredTransactions() ở đây,
        // vì hàm setFilter(date, type) bên dưới sẽ gọi)
    }
    // --- HẾT HÀM MỚI ---
    // --- HÀM MỚI ĐỂ FRAGMENT GỌI ---
    public void setFilter(String dateFilter, String typeFilter) {
        this.currentDateFilter = dateFilter;
        this.currentTypeFilter = typeFilter;
        loadFilteredTransactions(); // Tải lại dữ liệu với bộ lọc mới
    }

    // --- Getters (Sửa lại) ---
    public LiveData<List<Transaction>> getFilteredTransactions() {
        return filteredTransactions;
    }

    public LiveData<Double> getTotalBalance() {
        return totalBalance;
    }

    public LiveData<List<Wallet>> getAllWallets() { return allWallets; }
    public LiveData<List<SubCategory>> getIncomeCategories() { return incomeCategories; }
    public LiveData<List<SubCategory>> getExpenseCategories() { return expenseCategories; }

    // --- Cập nhật hàm Insert ---
    public void insertTransaction(Transaction transaction, Wallet selectedWallet) {
        executorService.execute(() -> {
            transactionDao.insertTransaction(transaction);

            // Cập nhật ví
            Wallet walletToUpdate = walletDao.getWalletById(selectedWallet.id);
            if (walletToUpdate != null) {
                double newBalance = walletToUpdate.balance + transaction.amount;
                walletToUpdate.balance = newBalance;
                walletDao.updateWallet(walletToUpdate);

                // Tải lại tổng số dư
                totalBalance.postValue(totalBalance.getValue() + transaction.amount);
            }

            // Tải lại danh sách
            loadFilteredTransactions();
        });
    }

    // --- HÀM MỚI ĐỂ XÓA ---
    public void deleteTransaction(Transaction transaction) {
        executorService.execute(() -> {
            // 1. Xóa giao dịch
            transactionDao.deleteTransaction(transaction);

            // 2. Hoàn tiền/Trừ tiền vào ví
            Wallet walletToUpdate = walletDao.getWalletById(transaction.WALLETid);
            if (walletToUpdate != null) {
                // Trừ đi số tiền (nếu là thu thì amount là +, trừ đi là đúng)
                // (nếu là chi thì amount là -, trừ đi (--) thành (+), là đúng)
                double newBalance = walletToUpdate.balance - transaction.amount;
                walletToUpdate.balance = newBalance;
                walletDao.updateWallet(walletToUpdate);

                // Tải lại tổng số dư
                totalBalance.postValue(totalBalance.getValue() - transaction.amount);
            }

            // Tải lại danh sách
            loadFilteredTransactions();
        });
    }
}