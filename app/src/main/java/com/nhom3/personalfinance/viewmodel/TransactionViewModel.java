package com.nhom3.personalfinance.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData; // <-- IMPORT MỚI

import com.nhom3.personalfinance.data.db.AppDatabase;
import com.nhom3.personalfinance.data.db.dao.CategoryDao;
import com.nhom3.personalfinance.data.db.dao.TransactionDao;
import com.nhom3.personalfinance.data.db.dao.WalletDao;
import com.nhom3.personalfinance.data.model.SubCategory;
import com.nhom3.personalfinance.data.model.Transaction;
import com.nhom3.personalfinance.data.model.Wallet;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionViewModel extends AndroidViewModel {

    private TransactionDao transactionDao;
    private WalletDao walletDao;
    private CategoryDao categoryDao;
    private ExecutorService executorService;

    // SỬA: Dùng MutableLiveData vì DAO của bạn không trả về LiveData
    private MutableLiveData<List<Wallet>> allWallets = new MutableLiveData<>();
    private MutableLiveData<List<SubCategory>> incomeCategories = new MutableLiveData<>();
    private MutableLiveData<List<SubCategory>> expenseCategories = new MutableLiveData<>();
    private MutableLiveData<List<Transaction>> allTransactions = new MutableLiveData<>();

    public TransactionViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getDatabase(application);
        transactionDao = database.transactionDao();
        walletDao = database.walletDao();
        categoryDao = database.categoryDao();
        executorService = Executors.newSingleThreadExecutor();

        // Tải dữ liệu ban đầu cho các Spinner
        loadInitialData();
        loadAllTransactions();
    }

    // HÀM MỚI: Tải dữ liệu từ DAO (không phải LiveData)
    private void loadInitialData() {
        executorService.execute(() -> {
            // 1. Lấy ví (Dùng hàm getAllWallets)
            List<Wallet> wallets = walletDao.getAllWallets();
            allWallets.postValue(wallets);

            // 2. Lấy danh mục Thu (Dùng hàm getSubCategoriesByCategoryId)
            List<SubCategory> incomes = categoryDao.getSubCategoriesByCategoryId(1); // 1 = ID của "Thu"
            incomeCategories.postValue(incomes);

            // 3. Lấy danh mục Chi (Dùng hàm getSubCategoriesByCategoryId)
            List<SubCategory> expenses = categoryDao.getSubCategoriesByCategoryId(2); // 2 = ID của "Chi"
            expenseCategories.postValue(expenses);
        });
    }
    // --- HÀM MỚI ---
    // Hàm này để tải tất cả giao dịch
    public void loadAllTransactions() {
        executorService.execute(() -> {
            List<Transaction> transactions = transactionDao.getAllTransactions();
            allTransactions.postValue(transactions);
        });
    }

    // --- GETTER MỚI ---
    public LiveData<List<Transaction>> getAllTransactions() {
        return allTransactions;
    }
    // --- HẾT PHẦN MỚI ---
    // --- Getters cho các Spinner (trả về LiveData) ---
    public LiveData<List<Wallet>> getAllWallets() {
        return allWallets;
    }

    public LiveData<List<SubCategory>> getIncomeCategories() {
        return incomeCategories;
    }

    public LiveData<List<SubCategory>> getExpenseCategories() {
        return expenseCategories;
    }

    // --- SỬA: Hành động Insert (Dùng hàm insertTransaction và updateWallet) ---
    public void insertTransaction(Transaction transaction, Wallet selectedWallet) {
        executorService.execute(() -> {
            // 1. Thêm giao dịch (Dùng hàm insertTransaction)
            transactionDao.insertTransaction(transaction);

            // 2. Cập nhật số dư ví (Dùng getWalletById và updateWallet)
            // Lấy lại thông tin ví mới nhất từ CSDL
            Wallet walletToUpdate = walletDao.getWalletById(selectedWallet.id);
            if (walletToUpdate != null) {
                // transaction.amount đã là (+) cho Thu và (-) cho Chi
                double newBalance = walletToUpdate.balance + transaction.amount;
                walletToUpdate.balance = newBalance;

                // Cập nhật toàn bộ object Wallet
                walletDao.updateWallet(walletToUpdate);
            }
            // --- CẬP NHẬT MỚI ---
            // Sau khi thêm, tải lại danh sách giao dịch
            loadAllTransactions();
            // --- HẾT CẬP NHẬT MỚI ---
        });
    }

}