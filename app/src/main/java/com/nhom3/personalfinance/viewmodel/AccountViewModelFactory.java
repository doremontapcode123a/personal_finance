package com.nhom3.personalfinance.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.nhom3.personalfinance.data.db.dao.UserDao;

public class AccountViewModelFactory implements ViewModelProvider.Factory {

    private final UserDao userDao;
    private final int currentUserId;

    public AccountViewModelFactory(UserDao userDao, int currentUserId) {
        this.userDao = userDao;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AccountViewModel.class)) {
            return (T) new AccountViewModel(userDao, currentUserId);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}