package com.nhom3.personalfinance.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

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

public class HomeViewModel extends AndroidViewModel {

    private TransactionDao transactionDao;
    private WalletDao walletDao;
    private CategoryDao categoryDao;
    private ExecutorService executorService;

    private MutableLiveData<List<Wallet>> allWallets = new MutableLiveData<>();
    private MutableLiveData<List<SubCategory>> incomeCategories = new MutableLiveData<>();
    private MutableLiveData<List<SubCategory>> expenseCategories = new MutableLiveData<>();
    private MutableLiveData<List<Transaction>> filteredTransactions = new MutableLiveData<>();
    private MutableLiveData<Double> totalBalance = new MutableLiveData<>();

    private MutableLiveData<Double> totalIncome = new MutableLiveData<>();
    private MutableLiveData<Double> totalExpense = new MutableLiveData<>();

    private String currentDateFilter = "this_month";
    private String currentTypeFilter = "all";
    private Date customStartDate;
    private Date customEndDate;
    private int currentUserId;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getDatabase(application);
        transactionDao = database.transactionDao();
        walletDao = database.walletDao();
        categoryDao = database.categoryDao();
        executorService = Executors.newSingleThreadExecutor();

        SharedPreferences prefs = application.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        currentUserId = prefs.getInt("LOGGED_IN_USER_ID", -1);

        loadInitialData();
        refreshData();
    }

    private void loadInitialData() {
        executorService.execute(() -> {
            List<Wallet> wallets = walletDao.getWalletsByUserId(currentUserId);
            allWallets.postValue(wallets);

            List<SubCategory> incomes = categoryDao.getSubCategoriesByCategoryId(1, currentUserId);
            incomeCategories.postValue(incomes);

            List<SubCategory> expenses = categoryDao.getSubCategoriesByCategoryId(2, currentUserId);
            expenseCategories.postValue(expenses);
        });
    }

    public void refreshData() {
        loadFilteredTransactions();
        recalculateTotalBalance();
    }

    private void recalculateTotalBalance() {
        executorService.execute(() -> {
            // SỬA LỖI: Chỉ lấy ví của user hiện tại để tính tổng
            List<Wallet> wallets = walletDao.getWalletsByUserId(currentUserId);
            double total = 0;
            for (Wallet w : wallets) {
                total += w.balance;
            }
            totalBalance.postValue(total);
        });
    }

    public void loadFilteredTransactions() {
        executorService.execute(() -> {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.clear(Calendar.MINUTE);
            cal.clear(Calendar.SECOND);
            cal.clear(Calendar.MILLISECOND);

            Date startDate, endDate;

            if ("this_month".equals(currentDateFilter)) {
                cal.set(Calendar.DAY_OF_MONTH, 1);
                startDate = cal.getTime();
                cal.add(Calendar.MONTH, 1);
                cal.add(Calendar.MILLISECOND, -1);
                endDate = cal.getTime();
            } else if ("last_month".equals(currentDateFilter)) {
                cal.add(Calendar.MONTH, -1);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                startDate = cal.getTime();
                cal.add(Calendar.MONTH, 1);
                cal.add(Calendar.MILLISECOND, -1);
                endDate = cal.getTime();
            } else {
                if (customStartDate == null || customEndDate == null) {
                    cal.set(Calendar.DAY_OF_MONTH, 1);
                    startDate = cal.getTime();
                    cal.add(Calendar.MONTH, 1);
                    cal.add(Calendar.MILLISECOND, -1);
                    endDate = cal.getTime();
                } else {
                    startDate = customStartDate;
                    endDate = customEndDate;
                }
            }

            List<Transaction> transactions;
            if ("income".equals(currentTypeFilter)) {
                transactions = transactionDao.getIncomeTransactionsBetweenDates(startDate, endDate, currentUserId);
            } else if ("expense".equals(currentTypeFilter)) {
                transactions = transactionDao.getExpenseTransactionsBetweenDates(startDate, endDate, currentUserId);
            } else {
                transactions = transactionDao.getTransactionsBetweenDates(startDate, endDate, currentUserId);
            }


            if ("income".equals(currentTypeFilter)) {
                transactions = transactionDao.getIncomeTransactionsBetweenDates(startDate, endDate, currentUserId);
            } else if ("expense".equals(currentTypeFilter)) {
                transactions = transactionDao.getExpenseTransactionsBetweenDates(startDate, endDate, currentUserId);
            } else {
                transactions = transactionDao.getTransactionsBetweenDates(startDate, endDate, currentUserId);
            }

            // 2. Tính toán Tổng thu và Tổng chi từ danh sách vừa lấy được
            double income = 0;
            double expense = 0;
            if (transactions != null) {
                for (Transaction t : transactions) {
                    if (t.amount > 0) {
                        income += t.amount;
                    } else {
                        expense += t.amount; // amount của chi tiêu đã là số âm
                    }
                }
            }

            // 3. Cập nhật giá trị cho tất cả LiveData
            filteredTransactions.postValue(transactions);
            totalIncome.postValue(income);
            totalExpense.postValue(expense);


        });
    }

    public void setFilter(String dateFilter, String typeFilter) {
        if (!"custom".equals(dateFilter)) {
            this.currentDateFilter = dateFilter;
        }
        this.currentTypeFilter = typeFilter;
        refreshData();
    }

    public void setCustomDateFilter(Date startDate, Date endDate) {
        this.currentDateFilter = "custom";
        this.customStartDate = startDate;
        this.customEndDate = endDate;
    }

    public LiveData<List<Transaction>> getFilteredTransactions() { return filteredTransactions; }
    public LiveData<Double> getTotalBalance() { return totalBalance; }
    public LiveData<Double> getTotalIncome() { return totalIncome; }
    public LiveData<Double> getTotalExpense() { return totalExpense; }
    public LiveData<List<Wallet>> getAllWallets() { return allWallets; }
    public LiveData<List<SubCategory>> getIncomeCategories() { return incomeCategories; }
    public LiveData<List<SubCategory>> getExpenseCategories() { return expenseCategories; }

    public void insertTransaction(Transaction transaction, Wallet selectedWallet) {
        executorService.execute(() -> {
            transaction.USERid = currentUserId; // Gán User ID
            transactionDao.insertTransaction(transaction);

            Wallet walletToUpdate = walletDao.getWalletById(selectedWallet.id);
            if (walletToUpdate != null) {
                double newBalance = walletToUpdate.balance + transaction.amount;
                walletToUpdate.balance = newBalance;
                walletDao.updateWallet(walletToUpdate);
            }
            refreshData();
        });
    }

    public void deleteTransaction(Transaction transaction) {
        executorService.execute(() -> {
            transactionDao.deleteTransaction(transaction);

            Wallet walletToUpdate = walletDao.getWalletById(transaction.WALLETid);
            if (walletToUpdate != null) {
                double newBalance = walletToUpdate.balance - transaction.amount;
                walletToUpdate.balance = newBalance;
                walletDao.updateWallet(walletToUpdate);
            }
            refreshData();
        });
    }
}