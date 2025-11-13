package com.nhom3.personalfinance.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nhom3.personalfinance.data.db.AppDatabase;
import com.nhom3.personalfinance.data.db.dao.WalletDao;
import com.nhom3.personalfinance.data.model.Wallet;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WalletViewModel extends AndroidViewModel {
    private int currentUserId; // <-- Biến mới
    private WalletDao walletDao;
    private ExecutorService executorService;

    // Dùng MutableLiveData vì DAO của bạn không trả về LiveData
    private MutableLiveData<List<Wallet>> allWallets = new MutableLiveData<>();

    public WalletViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getDatabase(application);
        walletDao = database.walletDao();
        executorService = Executors.newSingleThreadExecutor();
        // --- BƯỚC SỬA: ĐỌC USER ID ---
        SharedPreferences prefs = application.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        currentUserId = prefs.getInt("LOGGED_IN_USER_ID", -1); // Lấy User ID đã lưu
        // --- HẾT ---
        // Tải danh sách ví ban đầu
        loadWallets();
    }

    // Hàm tải/tải lại
    // SỬA HÀM NÀY
    public void loadWallets() {
        executorService.execute(() -> {
            // SỬA: Dùng currentUserId thay vì getAllWallets() (giả sử)
            List<Wallet> wallets = walletDao.getWalletsByUserId(currentUserId);
            allWallets.postValue(wallets);
        });
    }
    // Getter cho Activity
    public LiveData<List<Wallet>> getAllWallets() {
        return allWallets;
    }

    // --- Hành động CRUD ---
    // SỬA HÀM NÀY
    public void insertWallet(Wallet wallet) {
        executorService.execute(() -> {
            wallet.USERid = currentUserId; // SỬA: Dùng currentUserId thay vì "1"
            walletDao.insertWallet(wallet);
            loadWallets();
        });
    }

    public void updateWallet(Wallet wallet) {
        executorService.execute(() -> {
            walletDao.updateWallet(wallet);
            loadWallets(); // Tải lại
        });
    }

    public void deleteWallet(Wallet wallet) {
        executorService.execute(() -> {
            walletDao.deleteWallet(wallet);
            loadWallets(); // Tải lại
        });
    }
}