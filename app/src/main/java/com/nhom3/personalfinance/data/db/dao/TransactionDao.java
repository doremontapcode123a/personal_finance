package com.nhom3.personalfinance.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.nhom3.personalfinance.data.dto.CategoryPieChartDto;
import com.nhom3.personalfinance.data.dto.MonthlyTransactionDto;
import com.nhom3.personalfinance.data.model.Transaction;

import java.util.Date;
import java.util.List;

@Dao
public interface TransactionDao {

    // --- Thêm khoản thu/chi ---
    // Tránh lỗi trùng id khi import dữ liệu hoặc restore
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTransaction(Transaction transaction);

    // --- Sửa khoản thu/chi ---
    @Update
    void updateTransaction(Transaction transaction);

    // --- Xóa khoản thu/chi ---
    @Delete
    void deleteTransaction(Transaction transaction);

    // --- Lấy giao dịch trong khoảng thời gian (Thống kê) ---
    // --- SỬA HÀM NÀY ---
    @Query("SELECT * FROM `TRANSACTION` WHERE date BETWEEN :startDate AND :endDate AND USERid = :userId ORDER BY date DESC")
    List<Transaction> getTransactionsBetweenDates(Date startDate, Date endDate, int userId); // <-- THÊM , int userId

    // --- SỬA HÀM NÀY ---
    @Query("SELECT * FROM `TRANSACTION` WHERE USERid = :userId ORDER BY date DESC")
    List<Transaction> getAllTransactions(int userId); // <-- THÊM int userId


    @Query("SELECT * FROM `TRANSACTION` WHERE USERid = :userId ORDER BY date DESC LIMIT 10")
    LiveData<List<Transaction>> getRecentTransactions(int userId);

    // --- SỬA HÀM NÀY ---
    @Query("SELECT * FROM `TRANSACTION` WHERE date BETWEEN :startDate AND :endDate AND amount >= 0 AND USERid = :userId ORDER BY date DESC")
    List<Transaction> getIncomeTransactionsBetweenDates(Date startDate, Date endDate, int userId); // <-- THÊM , int userId

    // --- SỬA HÀM NÀY ---
    @Query("SELECT * FROM `TRANSACTION` WHERE date BETWEEN :startDate AND :endDate AND amount < 0 AND USERid = :userId ORDER BY date DESC")
    List<Transaction> getExpenseTransactionsBetweenDates(Date startDate, Date endDate, int userId); // <-- THÊM , int userId

    @Query("SELECT " +
            "strftime('%Y', date / 1000, 'unixepoch') as year, " +
            "strftime('%m', date / 1000, 'unixepoch') as month, " +
            "SUM(CASE WHEN amount > 0 THEN amount ELSE 0 END) as totalIncome, " +
            "SUM(CASE WHEN amount < 0 THEN amount ELSE 0 END) as totalExpense " +
            "FROM `TRANSACTION` " +
            "WHERE USERid = :userId AND date BETWEEN :startDate AND :endDate " +
            "GROUP BY year, month " +
            "ORDER BY year, month")
    List<MonthlyTransactionDto> getMonthlyTransactionsForChart(int userId, Date startDate, Date endDate);

    /**
     * Lấy tổng thu theo từng danh mục cho biểu đồ tròn (Pie Chart).
     * Nối (JOIN) bảng TRANSACTION với bảng SUB_CATEGORY để lấy tên danh mục.
     * Chỉ tính các giao dịch có amount > 0.
     */
    @Query("SELECT sc.name as categoryName, SUM(t.amount) as totalAmount " +
            "FROM `TRANSACTION` t JOIN SUB_CATEGORY sc ON t.SUB_CATEGORYid = sc.id " +
            "WHERE t.USERid = :userId AND t.amount > 0 AND t.date BETWEEN :startDate AND :endDate " +
            "GROUP BY sc.name " +
            "HAVING totalAmount > 0") // Chỉ lấy các danh mục có tổng thu > 0
    List<CategoryPieChartDto> getIncomeByCategoryForPieChart(int userId, Date startDate, Date endDate);

    /**
     * Lấy tổng chi theo từng danh mục cho biểu đồ tròn (Pie Chart).
     * Nối (JOIN) bảng TRANSACTION với bảng SUB_CATEGORY để lấy tên danh mục.
     * Chỉ tính các giao dịch có amount < 0.
     */
    @Query("SELECT sc.name as categoryName, SUM(t.amount) as totalAmount " +
            "FROM `TRANSACTION` t JOIN SUB_CATEGORY sc ON t.SUB_CATEGORYid = sc.id " +
            "WHERE t.USERid = :userId AND t.amount < 0 AND t.date BETWEEN :startDate AND :endDate " +
            "GROUP BY sc.name " +
            "HAVING totalAmount < 0") // Chỉ lấy các danh mục có tổng chi < 0
    List<CategoryPieChartDto> getExpenseByCategoryForPieChart(int userId, Date startDate, Date endDate);
    @Query("SELECT * FROM `TRANSACTION` WHERE USERid = :userId ORDER BY date DESC LIMIT 10")
    LiveData<List<Transaction>> getTop10RecentTransactions(int userId);

}
