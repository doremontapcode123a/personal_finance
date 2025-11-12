package com.nhom3.personalfinance.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.nhom3.personalfinance.data.db.dao.UserDao;
import com.nhom3.personalfinance.data.model.User;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginViewModel extends ViewModel {

    private final UserDao userDao;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
    private final MutableLiveData<String> loginMessage = new MutableLiveData<>();

    // Getters
    public LiveData<User> getCurrentUser() { return currentUser; }
    public LiveData<String> getLoginMessage() { return loginMessage; }

    public LoginViewModel(UserDao userDao) {
        this.userDao = userDao;
    }

    public void attemptLogin(String username, String passwordAttempt) {
        executorService.execute(() -> {
            User user = userDao.getUserByUsername(username);

            // TODO: So sánh passwordAttempt (đã hash) với user.getPassword()
            if (user != null && user.getPassword().equals(passwordAttempt)) {
                currentUser.postValue(user);
                loginMessage.postValue(null);
            } else {
                currentUser.postValue(null);
                loginMessage.postValue("Tên đăng nhập hoặc mật khẩu không đúng.");
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}