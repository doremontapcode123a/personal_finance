package com.nhom3.personalfinance.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.nhom3.personalfinance.data.model.Category;
import com.nhom3.personalfinance.data.model.SubCategory;

import java.util.List;

@Dao
public interface CategoryDao {

    // --- Chèn Category cha ("Thu", "Chi") - Dùng khi khởi tạo DB ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCategory(Category category);

    // --- Thêm danh mục con (Thu/Chi cụ thể) ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSubCategory(SubCategory subCategory);

    // --- Sửa danh mục con ---
    @Update
    void updateSubCategory(SubCategory subCategory);

    // --- Xóa danh mục con ---
    @Delete
    void deleteSubCategory(SubCategory subCategory);

    // --- Lấy danh mục con theo loại ---
    // --- SỬA HÀM NÀY ---
    @Query("SELECT * FROM SUB_CATEGORY WHERE CATEGORYid = :categoryId AND USERid = :userId ORDER BY id ASC")
    List<SubCategory> getSubCategoriesByCategoryId(int categoryId, int userId); // <-- THÊM , int userId

    // --- Lấy toàn bộ danh mục cha (Thu, Chi) ---
    @Query("SELECT * FROM CATEGORY ORDER BY id ASC")
    List<Category> getAllCategories();
}
