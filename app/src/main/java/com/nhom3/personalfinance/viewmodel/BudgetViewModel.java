package com.nhom3.personalfinance.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nhom3.personalfinance.data.db.AppDatabase;
import com.nhom3.personalfinance.data.db.dao.BudgetDao;
import com.nhom3.personalfinance.data.db.dao.CategoryDao;
import com.nhom3.personalfinance.data.db.dao.TransactionDao;
import com.nhom3.personalfinance.data.model.Budget;
import com.nhom3.personalfinance.data.model.SubCategory;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BudgetViewModel extends AndroidViewModel {

    private BudgetDao budgetDao;
    private CategoryDao categoryDao;
    private TransactionDao transactionDao;
    private ExecutorService executorService;
    private int currentUserId;

    // LiveData cho Spinner (chỉ lấy danh mục CHI)
    private MutableLiveData<List<SubCategory>> expenseCategories = new MutableLiveData<>();
    // LiveData cho danh sách Ngân sách
    private MutableLiveData<List<Budget>> allBudgets = new MutableLiveData<>();

    public BudgetViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getDatabase(application);
        budgetDao = database.budgetDao();
        categoryDao = database.categoryDao();
        transactionDao = database.transactionDao();
        executorService = Executors.newSingleThreadExecutor();

        SharedPreferences prefs = application.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        currentUserId = prefs.getInt("LOGGED_IN_USER_ID", -1);

        loadExpenseCategories();
        loadBudgets();
    }

    // Tải danh mục Chi (để cho vào Spinner)
    public void loadExpenseCategories() {
        executorService.execute(() -> {
            List<SubCategory> expenses = categoryDao.getSubCategoriesByCategoryId(2, currentUserId); // 2 = ID của "Chi"
            expenseCategories.postValue(expenses);
        });
    }

    // Tải các ngân sách đã đặt
    public void loadBudgets() {
        executorService.execute(() -> {
            List<Budget> budgets = budgetDao.getAllBudgetsByUserId(currentUserId);
            allBudgets.postValue(budgets);
        });
    }

    // --- Getters ---
    public LiveData<List<SubCategory>> getExpenseCategories() { return expenseCategories; }
    public LiveData<List<Budget>> getAllBudgets() { return allBudgets; }

    // --- Hành động ---
    public void insertBudget(Budget budget) {
        executorService.execute(() -> {
            budget.USERid = currentUserId;
            budgetDao.insertBudget(budget);
            loadBudgets(); // Tải lại
        });
    }

    public void deleteBudget(Budget budget) {
        executorService.execute(() -> {
            budgetDao.deleteBudget(budget);
            loadBudgets(); // Tải lại
        });
    }

    // --- HÀM TÍNH TOÁN (Quan trọng) ---
    // Trả về số tiền đã chi cho một danh mục trong tháng này
    public double getSpentAmountForCategory(int subCategoryId) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = cal.getTime();
        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.DAY_OF_YEAR, -1);
        Date endDate = cal.getTime();

        // Hàm này chạy trên main thread, không lý tưởng
        // nhưng chấp nhận được cho 1 câu lệnh SUM
        // (Để làm đúng, bạn cần dùng callback hoặc LiveData)

        // GỌI HÀM DAO MỚI
        // Vì SUM(amount) trả về số âm (vd: -50000), chúng ta nhân -1
        return transactionDao.getSumOfExpenseForCategory(subCategoryId, currentUserId, startDate, endDate) * -1;
    }
}