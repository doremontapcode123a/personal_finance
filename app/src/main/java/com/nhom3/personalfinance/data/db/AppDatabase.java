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
        version = 1,
        exportSchema = false
)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    // --- Khai báo các DAO ---
    public abstract UserDao userDao();
    public abstract TransactionDao transactionDao();
    public abstract CategoryDao categoryDao();
    public abstract WalletDao walletDao();   // ✅ thêm dòng này
    public abstract BudgetDao budgetDao();   // ✅ thêm dòng này

    // --- Singleton Pattern ---
    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "personal_finance_db")
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback) // <-- DÒNG MỚI
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // --- CODE MỚI ĐỂ THÊM DỮ LIỆU MẪU ---
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            // Dùng Executor để chạy trên thread riêng
            Executors.newSingleThreadExecutor().execute(() -> {
                // Lấy các DAO
                UserDao userDao = INSTANCE.userDao();
                WalletDao walletDao = INSTANCE.walletDao();
                CategoryDao categoryDao = INSTANCE.categoryDao();

                // Tạo User admin (giả sử đã đăng nhập user id 1)
                User user = new User();
                user.username = "admin";
                user.password = "admin";
                userDao.insertUser(user); // User này sẽ có ID = 1

                // Tạo 1 ví "Tiền mặt" cho user 1
                Wallet wallet = new Wallet();
                wallet.name = "Tiền mặt";
                wallet.balance = 1000000; // Số dư ban đầu
                wallet.USERid = 1; // Của user admin
                walletDao.insertWallet(wallet);

                // TẠO 2 DANH MỤC CHA "THU" (ID 1) VÀ "CHI" (ID 2)
                // (Chèn trực tiếp bằng SQL vì chúng ta không code DAO cho Category)
                db.execSQL("INSERT INTO CATEGORY (id, name) VALUES (1, 'Thu')");
                db.execSQL("INSERT INTO CATEGORY (id, name) VALUES (2, 'Chi')");

                // TẠO CÁC DANH MỤC CON
                // Danh mục con cho "Thu" (CATEGORYid = 1)
                SubCategory luong = new SubCategory();
                luong.name = "Lương";
                luong.CATEGORYid = 1;
                categoryDao.insertSubCategory(luong);

                SubCategory thuong = new SubCategory();
                thuong.name = "Thưởng";
                thuong.CATEGORYid = 1;
                categoryDao.insertSubCategory(thuong);

                // Danh mục con cho "Chi" (CATEGORYid = 2)
                SubCategory anUong = new SubCategory();
                anUong.name = "Ăn uống";
                anUong.CATEGORYid = 2;
                categoryDao.insertSubCategory(anUong);

                SubCategory diLai = new SubCategory();
                diLai.name = "Đi lại";
                diLai.CATEGORYid = 2;
                categoryDao.insertSubCategory(diLai);
            });
        }
    };
}