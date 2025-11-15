package com.nhom3.personalfinance.data.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.nhom3.personalfinance.data.db.dao.*;
import com.nhom3.personalfinance.data.model.*;
 // Đảm bảo bạn dùng đúng tên file Converter

import java.util.concurrent.Executors;

@Database(entities = {
        User.class,
        Wallet.class,
        Category.class,
        SubCategory.class,
        Transaction.class,
        Budget.class
},
        version = 3, // Giữ nguyên version 3 (hoặc tăng lên 4 nếu bạn đã gỡ cài đặt)
        exportSchema = false
)
@TypeConverters({Converters.class}) // Giữ nguyên tên file Converter của bạn
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract TransactionDao transactionDao();
    public abstract CategoryDao categoryDao();
    public abstract WalletDao walletDao();
    public abstract BudgetDao budgetDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "personal_finance_db")
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)

                            // --- THÊM DÒNG NÀY ĐỂ SỬA LỖI CRASH ---
                            .allowMainThreadQueries()
                            // ------------------------------------

                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // --- Callback này chỉ chạy 1 lần khi CSDL được tạo ---
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Executors.newSingleThreadExecutor().execute(() -> {
                // Chỉ tạo khung xương Thu/Chi
                try {
                    db.execSQL("INSERT OR IGNORE INTO CATEGORY (id, name) VALUES (1, 'Thu')");
                    db.execSQL("INSERT OR IGNORE INTO CATEGORY (id, name) VALUES (2, 'Chi')");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    };
}