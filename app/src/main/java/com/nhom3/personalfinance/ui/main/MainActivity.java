package com.nhom3.personalfinance.ui.main;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nhom3.personalfinance.R;

// Đảm bảo bạn đã import 4 Fragment rỗng
import com.nhom3.personalfinance.ui.main.HomeFragment;
import com.nhom3.personalfinance.ui.main.TransactionListFragment;
import com.nhom3.personalfinance.ui.main.StatisticsFragment; // Fragment cho tab "Báo cáo"
import com.nhom3.personalfinance.ui.main.AccountFragment; // Fragment cho tab "Tài khoản"

import com.nhom3.personalfinance.ui.transaction.AddEditTransactionActivity;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fabAddTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        fabAddTransaction = findViewById(R.id.fab_add_transaction);

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        setupBottomNavigation();
        setupFab();
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();

                // SỬA LỖI TYPO CỦA TÔI TẠI ĐÂY:
            } else if (itemId == R.id.nav_transactions) { // Phải là dấu "."
                selectedFragment = new TransactionListFragment();
            } else if (itemId == R.id.nav_statistics) {
                selectedFragment = new StatisticsFragment();
            } else if (itemId == R.id.nav_account) { // Phải là "nav_account"
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
        // Nút tròn ở giữa sẽ gọi hàm này
        fabAddTransaction.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditTransactionActivity.class);
            startActivity(intent);
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.nav_host_fragment, fragment);
        transaction.commit();
    }
}