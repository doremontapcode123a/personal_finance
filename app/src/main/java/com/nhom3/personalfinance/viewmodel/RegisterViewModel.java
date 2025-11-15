package com.nhom3.personalfinance.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.nhom3.personalfinance.data.db.dao.UserDao;
import com.nhom3.personalfinance.data.model.User;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class RegisterViewModel extends ViewModel {

    private final UserDao userDao;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final MutableLiveData<String> registrationMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isRegistrationComplete = new MutableLiveData<>();

    private static final int MIN_PASSWORD_LENGTH = 6;

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^.{6,}$"); // ✅ Sửa Regex


    // Getters
    public LiveData<String> getRegistrationMessage() { return registrationMessage; }


    public RegisterViewModel(UserDao userDao) {
        this.userDao = userDao;
    }

    private boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            registrationMessage.postValue("Mật khẩu không được để trống.");
            return false;
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            registrationMessage.postValue("Mật khẩu phải dài ít nhất " + MIN_PASSWORD_LENGTH + " ký tự.");
            return false;
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            registrationMessage.postValue("Mật khẩu không hợp lệ.");
            return false;
        }
        return true;
    }


    public void register(String username, String password) {
        // Đặt lại giá trị cho thông báo và cờ điều hướng
        registrationMessage.postValue(null);
        isRegistrationComplete.postValue(false);

        // --- 1. KIỂM TRA ĐIỀU KIỆN ĐẦU VÀO ---
        if (username == null || username.trim().isEmpty()) {
            registrationMessage.postValue("Tên đăng nhập không được để trống.");
            return;
        }

        if (!isValidPassword(password)) {
            return;
        }

        executorService.execute(() -> {
            try {
                // TODO: HASH MẬT KHẨU TRƯỚC KHI LƯU (BẮT BUỘC!)
                String hashedPassword = password;

                // Kiểm tra tên đăng nhập đã tồn tại trong DB
                if (userDao.getUserByUsername(username) != null) {
                    registrationMessage.postValue("Tên đăng nhập đã tồn tại. Vui lòng chọn tên khác.");
                    return;
                }

                User newUser = new User(username, hashedPassword);
                long result = userDao.insertUser(newUser);

                if (result > 0) {
                    registrationMessage.postValue("Đăng ký thành công!");
                    isRegistrationComplete.postValue(true);
                } else {
                    registrationMessage.postValue("Đăng ký thất bại: Không thể chèn vào CSDL.");
                }

            } catch (Exception e) {
                registrationMessage.postValue("Đăng ký thất bại do lỗi hệ thống.");
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}