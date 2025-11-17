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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// ViewModel chỉ xử lý logic ĐĂNG KÝ và khởi tạo dữ liệu mẫu
public class RegisterViewModel extends AndroidViewModel {

    private final ExecutorService executor;
    private final Handler mainHandler;
    private final UserDao userDao;
    private final WalletDao walletDao;
    private final CategoryDao categoryDao;
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z]).{8,}$");

    public interface AuthCallback {
        void onResult(User user, String message);
    }

    public RegisterViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        executor = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        userDao = db.userDao();
        walletDao = db.walletDao();
        categoryDao = db.categoryDao();
    }

    private boolean isPasswordValid(String password) {
        Matcher matcher = PASSWORD_PATTERN.matcher(password);
        return matcher.matches();
    }

    /**
     * Xử lý logic ĐĂNG KÝ.
     */
    public void register(String username, String password, AuthCallback callback) {
        executor.execute(() -> {
            // --- BƯỚC 1: KIỂM TRA MẬT KHẨU ---
            if (!isPasswordValid(password)) {
                mainHandler.post(() -> callback.onResult(null, "Mật khẩu phải dài tối thiểu 8 ký tự, bao gồm cả chữ và số."));
                return;
            }

            try {
                // --- BƯỚC 2: KIỂM TRA USERNAME ĐÃ TỒN TẠI ---
                User existingUser = userDao.getUserByUsername(username);
                if (existingUser != null) {
                    mainHandler.post(() -> callback.onResult(null, "Username đã tồn tại"));
                } else {

                    // --- BƯỚC 3: TẠO USER VÀ DỮ LIỆU MẪU ---
                    User newUser = new User(username, password);
                    userDao.insertUser(newUser);

                    User createdUser = userDao.getUserByUsername(username);

                    if (createdUser != null) {
                        ensureParentCategoriesExist();
                        createDefaultDataForUser(createdUser.id); // 3. TẠO DỮ LIỆU MẪU (Ví và Nhóm)

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

    private void ensureParentCategoriesExist() {
        try {
            Category thu = new Category(); thu.id = 1; thu.name = "Thu";
            categoryDao.insertCategory(thu);

            Category chi = new Category(); chi.id = 2; chi.name = "Chi";
            categoryDao.insertCategory(chi);
        } catch (Exception e) {
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
}