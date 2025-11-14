package com.nhom3.personalfinance.ui.main;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom3.personalfinance.R;
import com.nhom3.personalfinance.ui.transaction.AllTransactionsAdapter; // Sửa: Import Adapter từ package mới
import com.nhom3.personalfinance.viewmodel.AllTransactionsViewModel; // Sửa: Import ViewModel từ package mới

import java.util.ArrayList;

public class DanhSachGiaoDichActivity extends AppCompatActivity {

    private AllTransactionsViewModel viewModel;
    private AllTransactionsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danh_sach_giao_dich);

        // Khởi tạo ViewModel từ file riêng
        viewModel = new ViewModelProvider(this).get(AllTransactionsViewModel.class);

        // --- Cấu hình Toolbar ---
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Giao dịch gần đây");
        }

        // --- Ánh xạ & Cấu hình RecyclerView ---
        RecyclerView recyclerView = findViewById(R.id.recycler_view_all_transactions);
        setupRecyclerView(recyclerView);

        // --- Bắt đầu quan sát dữ liệu từ ViewModel ---
        observeViewModel();
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Khởi tạo Adapter từ file riêng
        adapter = new AllTransactionsAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);
    }

    private void observeViewModel() {
        // Quan sát danh sách 10 giao dịch gần nhất
        viewModel.getTop10RecentTransactions().observe(this, transactions -> {
            if (transactions != null) {
                adapter.updateData(transactions);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Xử lý khi nhấn nút back trên toolbar
        finish();
        return true;
    }
}
