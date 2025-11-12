package com.nhom3.personalfinance.viewmodel;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.nhom3.personalfinance.data.db.AppDatabase;
import com.nhom3.personalfinance.data.model.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuthViewModel extends AndroidViewModel {

    private final AppDatabase db;
    private final ExecutorService executor;
    private final Handler mainHandler;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getDatabase(application);
        executor = Executors.newSingleThreadExecutor();  // thread riêng chạy DB
        mainHandler = new Handler(Looper.getMainLooper()); // để update UI
    }

    // --- Đăng ký tài khoản ---
    public void register(String username, String password, AuthCallback callback) {
        executor.execute(() -> {
            User existingUser = db.userDao().getUserByUsername(username);
            if (existingUser != null) {
                mainHandler.post(() -> callback.onResult(false, "Username đã tồn tại"));
            } else {
                db.userDao().insertUser(new User(username, password));
                mainHandler.post(() -> callback.onResult(true, "Đăng ký thành công"));
            }
        });
    }

    // --- Đăng nhập tài khoản ---
    public void login(String username, String password, AuthCallback callback) {
        executor.execute(() -> {
            User user = db.userDao().getUserByUsernameAndPassword(username, password);
            if (user != null) {
                mainHandler.post(() -> callback.onResult(true, "Đăng nhập thành công"));
            } else {
                mainHandler.post(() -> callback.onResult(false, "Sai username hoặc password"));
            }
        });
    }

    // --- Callback để update UI ---
    public interface AuthCallback {
        void onResult(boolean success, String message);
    }
}