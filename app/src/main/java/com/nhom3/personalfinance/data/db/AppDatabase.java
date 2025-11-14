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

import java.util.concurrent.Executors;

@Database(entities = {
        User.class,
        Wallet.class,
        Category.class,
        SubCategory.class,
        Transaction.class,
        Budget.class
},
        version = 2,
        exportSchema = false
)
@TypeConverters({Converters.class})
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
                // CHỈ TẠO KHUNG XƯƠNG GỐC (THU/CHI)
                // Dùng SQL trực tiếp để đảm bảo nó chạy trước mọi logic khác
                db.execSQL("INSERT OR IGNORE INTO CATEGORY (id, name) VALUES (1, 'Thu')");
                db.execSQL("INSERT OR IGNORE INTO CATEGORY (id, name) VALUES (2, 'Chi')");
            });
        }
    };
}