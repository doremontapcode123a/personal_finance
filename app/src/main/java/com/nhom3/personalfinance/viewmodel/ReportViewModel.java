package com.nhom3.personalfinance.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nhom3.personalfinance.data.db.AppDatabase;
import com.nhom3.personalfinance.data.db.dao.TransactionDao;
import com.nhom3.personalfinance.data.dto.CategoryPieChartDto;
import com.nhom3.personalfinance.data.dto.MonthlyTransactionDto;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ViewModel dành riêng cho màn hình BaoCaoThuChiActivity.
 */
public class ReportViewModel extends AndroidViewModel {

    private final TransactionDao transactionDao;
    private final ExecutorService executorService;
    private final int currentUserId;

    // LiveData cho biểu đồ đường (Line Chart)
    private final MutableLiveData<List<MonthlyTransactionDto>> monthlyTransactions = new MutableLiveData<>();
    // LiveData cho biểu đồ tròn Thu (Income Pie Chart)
    private final MutableLiveData<List<CategoryPieChartDto>> incomeByCategory = new MutableLiveData<>();
    // LiveData cho biểu đồ tròn Chi (Expense Pie Chart)
    private final MutableLiveData<List<CategoryPieChartDto>> expenseByCategory = new MutableLiveData<>();

    public ReportViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getDatabase(application);
        transactionDao = database.transactionDao();
        executorService = Executors.newSingleThreadExecutor();

        SharedPreferences prefs = application.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        currentUserId = prefs.getInt("LOGGED_IN_USER_ID", -1);

        // Tải dữ liệu cho báo cáo ngay khi ViewModel được tạo
        loadReportData();
    }

    public void loadReportData() {
        // Lấy dữ liệu cho 12 tháng gần nhất
        Calendar cal = Calendar.getInstance();
        Date endDate = cal.getTime();
        cal.add(Calendar.YEAR, -1);
        Date startDate = cal.getTime();

        executorService.execute(() -> {
            // Tải dữ liệu cho biểu đồ đường
            List<MonthlyTransactionDto> lineData = transactionDao.getMonthlyTransactionsForChart(currentUserId, startDate, endDate);
            monthlyTransactions.postValue(lineData);

            // Tải dữ liệu cho biểu đồ tròn Thu
            List<CategoryPieChartDto> pieIncomeData = transactionDao.getIncomeByCategoryForPieChart(currentUserId, startDate, endDate);
            incomeByCategory.postValue(pieIncomeData);

            // Tải dữ liệu cho biểu đồ tròn Chi
            List<CategoryPieChartDto> pieExpenseData = transactionDao.getExpenseByCategoryForPieChart(currentUserId, startDate, endDate);
            expenseByCategory.postValue(pieExpenseData);
        });
    }

    // Cung cấp LiveData cho Activity
    public LiveData<List<MonthlyTransactionDto>> getMonthlyTransactions() {
        return monthlyTransactions;
    }

    public LiveData<List<CategoryPieChartDto>> getIncomeByCategory() {
        return incomeByCategory;
    }

    public LiveData<List<CategoryPieChartDto>> getExpenseByCategory() {
        return expenseByCategory;
    }
}
