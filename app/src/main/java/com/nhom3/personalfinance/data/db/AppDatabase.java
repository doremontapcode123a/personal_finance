package com.nhom3.personalfinance.data.db;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.nhom3.personalfinance.data.db.dao.*;
import com.nhom3.personalfinance.data.model.*;

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
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "personal_finance_db"
                            )
                            // .addCallback(sRoomDatabaseCallback) // nếu muốn chèn dữ liệu mẫu
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /*
    // --- (Tùy chọn) Callback khởi tạo dữ liệu mẫu ---
    private static final RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback() {
                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                    super.onCreate(db);
                    Executors.newSingleThreadExecutor().execute(() -> {
                        CategoryDao dao = INSTANCE.categoryDao();

                        Category thu = new Category();
                        thu.name = "Thu";
                        dao.insertCategory(thu);

                        Category chi = new Category();
                        chi.name = "Chi";
                        dao.insertCategory(chi);
                    });
                }
            };
    */
}
