package com.nhom3.personalfinance.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.nhom3.personalfinance.data.db.dao.UserDao;

public class RegisterViewModelFactory implements ViewModelProvider.Factory {
    private final UserDao userDao;

    public RegisterViewModelFactory(UserDao userDao) {
        this.userDao = userDao;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        // Factory này sẽ khởi tạo RegisterViewModel
        if (modelClass.isAssignableFrom(RegisterViewModel.class)) {
            return (T) new RegisterViewModel(userDao);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}