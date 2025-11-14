package com.nhom3.personalfinance.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

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
    @Query("SELECT * FROM `TRANSACTION` WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    List<Transaction> getTransactionsBetweenDates(Date startDate, Date endDate);

    // --- Lấy toàn bộ giao dịch ---
    @Query("SELECT * FROM `TRANSACTION` ORDER BY date DESC")
    List<Transaction> getAllTransactions();
}
