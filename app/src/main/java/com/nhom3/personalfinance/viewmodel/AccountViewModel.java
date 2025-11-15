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

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");

    public LiveData<String> getPasswordChangeMessage() { return passwordChangeMessage; }

    public AccountViewModel(UserDao userDao, int currentUserId) {
        this.userDao = userDao;
        this.currentUserLiveData = userDao.getUserById(currentUserId);
    }

    public LiveData<User> getCurrentUser() {
        return currentUserLiveData;
    }


    // Validate mật khẩu
    private boolean isValidPassword(String password) {

        if (password == null || password.isEmpty()) {
            passwordChangeMessage.postValue("Mật khẩu mới không được để trống.");
            return false;
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            passwordChangeMessage.postValue(
                    "Mật khẩu phải có ít nhất 8 ký tự, bao gồm cả chữ và số."
            );
            return false;
        }

        return true;
    }

    // Đổi mật khẩu
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

            userToUpdate.setPassword(newPass);
            userDao.updateUser(userToUpdate);

            passwordChangeMessage.postValue("Đổi mật khẩu thành công!");
        });
    }
}
