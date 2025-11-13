package com.nhom3.personalfinance.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nhom3.personalfinance.data.db.AppDatabase;
import com.nhom3.personalfinance.data.db.dao.CategoryDao;
import com.nhom3.personalfinance.data.model.SubCategory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CategoryViewModel extends AndroidViewModel {

    private CategoryDao categoryDao;
    private ExecutorService executorService;

    private MutableLiveData<List<SubCategory>> incomeCategories = new MutableLiveData<>();
    private MutableLiveData<List<SubCategory>> expenseCategories = new MutableLiveData<>();
    private int currentUserId;

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getDatabase(application);
        categoryDao = database.categoryDao();
        executorService = Executors.newSingleThreadExecutor();

        SharedPreferences prefs = application.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        currentUserId = prefs.getInt("LOGGED_IN_USER_ID", -1);

        loadCategories();
    }

    public void loadCategories() {
        executorService.execute(() -> {
            List<SubCategory> incomes = categoryDao.getSubCategoriesByCategoryId(1, currentUserId);
            incomeCategories.postValue(incomes);

            List<SubCategory> expenses = categoryDao.getSubCategoriesByCategoryId(2, currentUserId);
            expenseCategories.postValue(expenses);
        });
    }

    public LiveData<List<SubCategory>> getIncomeCategories() { return incomeCategories; }
    public LiveData<List<SubCategory>> getExpenseCategories() { return expenseCategories; }

    public void insertSubCategory(SubCategory subCategory) {
        executorService.execute(() -> {
            subCategory.USERid = currentUserId; // GÁN USER ID CHUẨN
            categoryDao.insertSubCategory(subCategory);
            loadCategories();
        });
    }

    public void updateSubCategory(SubCategory subCategory) {
        executorService.execute(() -> {
            categoryDao.updateSubCategory(subCategory);
            loadCategories();
        });
    }

    public void deleteSubCategory(SubCategory subCategory) {
        executorService.execute(() -> {
            categoryDao.deleteSubCategory(subCategory);
            loadCategories();
        });
    }
}