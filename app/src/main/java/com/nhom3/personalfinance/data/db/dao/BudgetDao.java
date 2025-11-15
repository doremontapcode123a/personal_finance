package com.nhom3.personalfinance.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.nhom3.personalfinance.data.model.Budget;

import java.util.List;

@Dao
public interface BudgetDao {

    // --- Thêm hoặc cập nhật ngân sách ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBudget(Budget budget);

    // --- Sửa ngân sách ---
    @Update
    void updateBudget(Budget budget);

    // --- Xóa ngân sách ---
    @Delete
    void deleteBudget(Budget budget);

    // --- Lấy toàn bộ ngân sách ---
    @Query("SELECT * FROM BUDGET ORDER BY id ASC")
    List<Budget> getAllBudgets();

    // --- Lấy ngân sách theo danh mục con ---
    @Query("SELECT * FROM BUDGET WHERE SUB_CATEGORYid = :subCategoryId LIMIT 1")
    Budget getBudgetBySubCategoryId(int subCategoryId);
    @Query("SELECT * FROM BUDGET WHERE USERid = :userId ORDER BY id ASC")
    List<Budget> getAllBudgetsByUserId(int userId);
}
