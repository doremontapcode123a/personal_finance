package com.nhom3.personalfinance.viewmodel;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.nhom3.personalfinance.data.db.AppDatabase;
import com.nhom3.personalfinance.data.db.dao.UserDao;
import com.nhom3.personalfinance.data.model.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginViewModel extends AndroidViewModel {

    private final ExecutorService executor;
    private final Handler mainHandler;
    private final UserDao userDao;

    public interface AuthCallback {
        void onResult(User user, String message);
    }

    public LoginViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        executor = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        userDao = db.userDao();
    }

    /**
     * Xử lý logic ĐĂNG NHẬP.
     */
    public void login(String username, String password, AuthCallback callback) {
        executor.execute(() -> {
            try {
                User user = userDao.getUserByUsernameAndPassword(username, password);
                if (user != null) {
                    mainHandler.post(() -> callback.onResult(user, "Đăng nhập thành công!"));
                } else {
                    mainHandler.post(() -> callback.onResult(null, "Sai tên đăng nhập hoặc mật khẩu!"));
                }
            } catch (Exception e) {
                mainHandler.post(() -> callback.onResult(null, "Lỗi: " + e.getMessage()));
            }
        });
    }
}