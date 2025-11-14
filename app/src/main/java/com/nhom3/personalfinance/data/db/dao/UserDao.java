package com.nhom3.personalfinance.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.nhom3.personalfinance.data.model.User;

@Dao
public interface UserDao {

    // --- Dùng cho chức năng Đăng ký ---
    // Thêm OnConflictStrategy.ABORT để tránh trùng username
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertUser(User user);

    // --- Dùng cho chức năng Đăng nhập ---
    @Query("SELECT * FROM USER WHERE username = :username AND password = :password LIMIT 1")
    User getUserByUsernameAndPassword(String username, String password);
    @Query("SELECT * FROM USER WHERE username = :username LIMIT 1")
    User getUserByUsername(String username);

    @Query("SELECT * FROM USER WHERE id = :userId LIMIT 1")
    LiveData<User> getUserById(int userId);





    @Update
    void updateUser(User user);

    // --- Dùng cho chức năng Xóa tài khoản ---
    @Delete
    void deleteUser(User user);
}
