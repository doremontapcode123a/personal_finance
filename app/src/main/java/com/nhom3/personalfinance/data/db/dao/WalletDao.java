package com.nhom3.personalfinance.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.nhom3.personalfinance.data.model.Wallet;

import java.util.List;

@Dao
public interface WalletDao {

    // --- Thêm ví ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertWallet(Wallet wallet);

    // --- Sửa ví ---
    @Update
    void updateWallet(Wallet wallet);

    // --- Xóa ví ---
    @Delete
    void deleteWallet(Wallet wallet);

    // --- Lấy tất cả ví ---
    @Query("SELECT * FROM WALLET ORDER BY id ASC")
    List<Wallet> getAllWallets();

    // --- Lấy ví theo ID ---
    @Query("SELECT * FROM WALLET WHERE id = :walletId LIMIT 1")
    Wallet getWalletById(int walletId);
}
