package com.nhom3.personalfinance.ui.main;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.nhom3.personalfinance.ui.main.BudgetFragment;
// (Thay vì StatisticsFragment)

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nhom3.personalfinance.R;
import com.nhom3.personalfinance.notification.NotificationHelper;
import com.nhom3.personalfinance.ui.transaction.AddEditTransactionActivity;


public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fabAddTransaction;

    // --- BƯỚC 1: Thêm một ActivityResultLauncher để xử lý kết quả xin quyền ---
    private final ActivityResultLauncher<Intent> requestExactAlarmPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                // Sau khi người dùng quay lại từ màn hình cài đặt, kiểm tra lại quyền một lần nữa
                if (canScheduleExactAlarms()) {
                    // Nếu người dùng đã cấp quyền, bây giờ mới thực sự đặt báo thức
                    NotificationHelper.setDailyAlarm(this);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- BƯỚC 2: Sửa lại logic đặt báo thức ---
        // 1. Luôn tạo kênh thông báo
        NotificationHelper.createNotificationChannel(this);
        // 2. Gọi hàm mới để kiểm tra quyền và đặt báo thức một cách an toàn
        setupDailyNotificationWithPermissionCheck();
        // ------------------------------------------

        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        fabAddTransaction = findViewById(R.id.fab_add_transaction);

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        setupBottomNavigation();
        setupFab();
    }

    // --- BƯỚC 3: Thêm các hàm kiểm tra và yêu cầu quyền ---

    /**
     * Hàm này sẽ kiểm tra quyền trước khi đặt báo thức.
     */
    private void setupDailyNotificationWithPermissionCheck() {
        if (canScheduleExactAlarms()) {
            // Nếu đã có quyền, đặt báo thức ngay lập tức.
            NotificationHelper.setDailyAlarm(this);
        } else {
            // Nếu chưa có quyền, yêu cầu người dùng cấp quyền.
            requestExactAlarmPermission();
        }
    }

    /**
     * Kiểm tra xem ứng dụng có quyền đặt báo thức chính xác hay không.
     * @return true nếu có quyền, false nếu không.
     */
    private boolean canScheduleExactAlarms() {
        // Từ Android 12 (API 31) trở lên mới cần kiểm tra động
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            // Hàm của hệ thống để kiểm tra quyền
            return alarmManager.canScheduleExactAlarms();
        }
        // Các phiên bản Android cũ hơn luôn có quyền này theo mặc định
        return true;
    }

    /**
     * Mở màn hình cài đặt của hệ thống để người dùng cấp quyền đặt báo thức.
     */
    private void requestExactAlarmPermission() {
        // Chỉ thực hiện trên Android 12 (API 31) trở lên
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Tạo một Intent để yêu cầu mở màn hình cài đặt "Alarms & reminders"
            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            // Mở màn hình cài đặt và lắng nghe kết quả trả về thông qua Launcher đã tạo ở trên
            requestExactAlarmPermissionLauncher.launch(intent);
        }
    }


    // --- CÁC HÀM CŨ GIỮ NGUYÊN ---

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_transactions) {
                selectedFragment = new TransactionListFragment();
            } else if (itemId == R.id.nav_budget) {
                selectedFragment = new BudgetFragment();
                // (Thay vì nav_statistics và StatisticsFragment)
                // --- HẾT SỬA ---
            } else if (itemId == R.id.nav_account) {
                selectedFragment = new AccountFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });

        bottomNavigationView.setOnItemReselectedListener(item -> {});
    }

    private void setupFab() {
        fabAddTransaction.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditTransactionActivity.class);
            startActivity(intent);
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // Sửa lại ID container nếu bạn không dùng Navigation Component
        transaction.replace(R.id.nav_host_fragment, fragment);
        transaction.commit();
    }
}
