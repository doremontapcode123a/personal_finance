package com.nhom3.personalfinance.ui.main;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;
import com.nhom3.personalfinance.R;
import com.nhom3.personalfinance.ui.main.AccountFragment;
import com.nhom3.personalfinance.notification.NotificationHelper; // <-- ĐÃ THÊM: Import lớp NotificationHelper

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private static final int CONTAINER_ID = R.id.fragment_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(this);

        // Tải HomeFragment mặc định
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }

        // ĐÃ THÊM: THIẾT LẬP THÔNG BÁO HÀNG NGÀY
        NotificationHelper.createNotificationChannel(this);
        NotificationHelper.setDailyAlarm(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        int itemId = item.getItemId();

        if (itemId == R.id.nav_account) {
            //  CHUYỂN SANG TRANG TÀI KHOẢN
            selectedFragment = new AccountFragment();
        } else if (itemId == R.id.nav_home) {
            selectedFragment = new HomeFragment();
        }
        // ... (xử lý các icon khác) ...

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(CONTAINER_ID, selectedFragment)
                    .commit();
        }

        return true;
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(CONTAINER_ID, fragment)
                .commit();
    }
}