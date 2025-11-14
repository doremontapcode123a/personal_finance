package com.nhom3.personalfinance.viewmodel;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.nhom3.personalfinance.data.db.AppDatabase;
import com.nhom3.personalfinance.data.db.dao.CategoryDao;
import com.nhom3.personalfinance.data.db.dao.UserDao;
import com.nhom3.personalfinance.data.db.dao.WalletDao;
import com.nhom3.personalfinance.data.model.Category;
import com.nhom3.personalfinance.data.model.SubCategory;
import com.nhom3.personalfinance.data.model.User;
import com.nhom3.personalfinance.data.model.Wallet;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuthViewModel extends AndroidViewModel {

    private final ExecutorService executor;
    private final Handler mainHandler;
    private final UserDao userDao;
    private final WalletDao walletDao;
    private final CategoryDao categoryDao;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        executor = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        userDao = db.userDao();
        walletDao = db.walletDao();
        categoryDao = db.categoryDao();
    }

    public interface AuthCallback {
        void onResult(User user, String message);
    }

    // --- LOGIC ĐĂNG KÝ VÀ TẠO DỮ LIỆU MẪU ---
    public void register(String username, String password, AuthCallback callback) {
        executor.execute(() -> {
            try {
                User existingUser = userDao.getUserByUsername(username);
                if (existingUser != null) {
                    mainHandler.post(() -> callback.onResult(null, "Username đã tồn tại"));
                } else {
                    // 1. Tạo User mới
                    User newUser = new User(username, password);
                    userDao.insertUser(newUser);

                    User createdUser = userDao.getUserByUsername(username);

                    if (createdUser != null) {
                        // 2. QUAN TRỌNG: Kiểm tra xem Thu/Chi đã có chưa (Phòng hờ)
                        ensureParentCategoriesExist();

                        // 3. Tạo dữ liệu mẫu riêng cho User này
                        createDefaultDataForUser(createdUser.id);

                        mainHandler.post(() -> callback.onResult(createdUser, "Đăng ký thành công"));
                    } else {
                        mainHandler.post(() -> callback.onResult(null, "Lỗi tạo user"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> callback.onResult(null, "Lỗi: " + e.getMessage()));
            }
        });
    }

    // Đảm bảo bảng CATEGORY (Thu/Chi) không bị trống
    private void ensureParentCategoriesExist() {
        try {
            Category thu = new Category(); thu.id = 1; thu.name = "Thu";
            categoryDao.insertCategory(thu);

            Category chi = new Category(); chi.id = 2; chi.name = "Chi";
            categoryDao.insertCategory(chi);
        } catch (Exception e) {
            // Nếu đã có rồi thì bỏ qua, không sao cả
        }
    }

    // Tạo Ví và Nhóm mẫu cho User
    private void createDefaultDataForUser(int userId) {
        try {
            // A. Tạo 1 Ví tiền mặt (0đ)
            Wallet wallet = new Wallet();
            wallet.name = "Tiền mặt";
            wallet.balance = 0;
            wallet.USERid = userId;
            walletDao.insertWallet(wallet);

            // B. Tạo Nhóm mẫu cho THU (Category ID = 1)
            createSubCat("Lương", 1, userId);
            createSubCat("Thưởng", 1, userId);

            // C. Tạo Nhóm mẫu cho CHI (Category ID = 2)
            createSubCat("Ăn uống", 2, userId);
            createSubCat("Đi lại", 2, userId);
            createSubCat("Mua sắm", 2, userId);
            createSubCat("Sinh hoạt", 2, userId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createSubCat(String name, int parentId, int userId) {
        SubCategory sub = new SubCategory();
        sub.name = name;
        sub.CATEGORYid = parentId;
        sub.USERid = userId;
        categoryDao.insertSubCategory(sub);
    }

    // ... (Các hàm Login, ChangePassword, Delete giữ nguyên)
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

    public void changePassword(String username, String oldPassword, String newPassword, AuthCallback callback) {
        executor.execute(() -> {
            try {
                User user = userDao.getUserByUsernameAndPassword(username, oldPassword);
                if (user == null) {
                    mainHandler.post(() -> callback.onResult(null, "Mật khẩu cũ không chính xác!"));
                    return;
                }
                user.password = newPassword;
                userDao.updateUser(user);
                mainHandler.post(() -> callback.onResult(user, "Đổi mật khẩu thành công!"));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onResult(null, "Lỗi: " + e.getMessage()));
            }
        });
    }

    public void deleteAccount(String username, String password, AuthCallback callback) {
        executor.execute(() -> {
            try {
                User user = userDao.getUserByUsernameAndPassword(username, password);
                if (user == null) {
                    mainHandler.post(() -> callback.onResult(null, "Xác thực thất bại!"));
                    return;
                }
                userDao.deleteUser(user);
                mainHandler.post(() -> callback.onResult(user, "Xóa tài khoản thành công!"));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onResult(null, "Lỗi: " + e.getMessage()));
            }
        });
    }
}