package com.nhom3.personalfinance.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.nhom3.personalfinance.data.db.dao.UserDao;
import com.nhom3.personalfinance.data.model.User;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class AccountViewModel extends ViewModel {

    private final UserDao userDao;
    private final LiveData<User> currentUserLiveData;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final MutableLiveData<String> passwordChangeMessage = new MutableLiveData<>();

    private final MutableLiveData<Boolean> navigateToWelcome = new MutableLiveData<>();

    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^.{6,}$");

    // Getters
    public LiveData<String> getPasswordChangeMessage() { return passwordChangeMessage; }
    public LiveData<Boolean> getNavigateToWelcome() { return navigateToWelcome; }

    public AccountViewModel(UserDao userDao, int currentUserId) {
        this.userDao = userDao;
        this.currentUserLiveData = userDao.getUserById(currentUserId);
    }
    public LiveData<User> getCurrentUser() {
        return currentUserLiveData;
    }
    public User getCurrentUserValue() {
        return currentUserLiveData.getValue();
    }
    private boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            passwordChangeMessage.postValue("Mật khẩu mới không được để trống.");
            return false;
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            passwordChangeMessage.postValue("Mật khẩu phải dài ít nhất " + MIN_PASSWORD_LENGTH + " ký tự.");
            return false;
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            return true;
        }
        return true;
    }
    public void validateAndChangePassword(String currentPass, String newPass) {
        passwordChangeMessage.postValue(null);
        if (!isValidPassword(newPass)) {
            return;
        }
        executorService.execute(() -> {
            User userToUpdate = currentUserLiveData.getValue();
            if (userToUpdate == null) {
                passwordChangeMessage.postValue("Lỗi: Dữ liệu người dùng không hợp lệ.");
                return;
            }
            if (!userToUpdate.getPassword().equals(currentPass)) {
                passwordChangeMessage.postValue("Mật khẩu cũ không đúng.");
                return;
            }
            if (newPass.equals(currentPass)) {
                passwordChangeMessage.postValue("Mật khẩu mới không được trùng với mật khẩu cũ.");
                return;
            }
            String newPasswordHash = newPass;
            userToUpdate.setPassword(newPasswordHash);
            userDao.updateUser(userToUpdate);
            passwordChangeMessage.postValue("Đổi mật khẩu thành công!");
        });
    }

    //đăng xuất.
    public void logoutUser() {
        navigateToWelcome.postValue(true);
    }

    public void deleteCurrentAccount() {
        User userToDelete = currentUserLiveData.getValue();
        if (userToDelete != null) {
            executorService.execute(() -> {
                userDao.deleteUser(userToDelete);
                // 🔥 Báo hiệu điều hướng khi xóa thành công
                navigateToWelcome.postValue(true);
            });
        }
    }
}