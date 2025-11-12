package com.nhom3.personalfinance.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.nhom3.personalfinance.data.db.dao.UserDao;

public class LoginViewModelFactory implements ViewModelProvider.Factory {
    private final UserDao userDao;

    public LoginViewModelFactory(UserDao userDao) {
        this.userDao = userDao;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(userDao);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}