package com.nhom3.personalfinance.viewmodel; // Sửa lại package cho đúng

import android.app.Application;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.nhom3.personalfinance.data.db.AppDatabase;
import com.nhom3.personalfinance.data.db.dao.TransactionDao;
import com.nhom3.personalfinance.data.model.Transaction;

import java.util.List;

// Thêm "public"
public class AllTransactionsViewModel extends AndroidViewModel {
    private final TransactionDao transactionDao;
    private final LiveData<List<Transaction>> top10RecentTransactions;

    public AllTransactionsViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getDatabase(application);
        transactionDao = database.transactionDao();

        int currentUserId = application.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                .getInt("LOGGED_IN_USER_ID", -1);

        top10RecentTransactions = transactionDao.getTop10RecentTransactions(currentUserId);
    }

    public LiveData<List<Transaction>> getTop10RecentTransactions() {
        return top10RecentTransactions;
    }
}
