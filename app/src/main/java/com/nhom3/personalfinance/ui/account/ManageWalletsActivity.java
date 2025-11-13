package com.nhom3.personalfinance.ui.account; // (Hoặc package bạn muốn)

import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nhom3.personalfinance.R;
import com.nhom3.personalfinance.data.model.Wallet;
import com.nhom3.personalfinance.viewmodel.WalletViewModel;

public class ManageWalletsActivity extends AppCompatActivity {

    private WalletViewModel viewModel;
    private RecyclerView recyclerView;
    private WalletAdapter adapter;
    private FloatingActionButton fabAddWallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_wallets);

        viewModel = new ViewModelProvider(this).get(WalletViewModel.class);

        findViews();
        setupRecyclerView();
        setupListeners();
        observeViewModel();
    }

    private void findViews() {
        recyclerView = findViewById(R.id.recycler_view_wallets);
        fabAddWallet = findViewById(R.id.fab_add_wallet);
    }

    private void setupRecyclerView() {
        adapter = new WalletAdapter(new WalletAdapter.OnWalletClickListener() {
            @Override
            public void onWalletClick(Wallet wallet) {
                // Click để Sửa
                showAddEditDialog(wallet);
            }

            @Override
            public void onWalletLongClick(Wallet wallet) {
                // Long click để Xóa
                showDeleteDialog(wallet);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getAllWallets().observe(this, wallets -> {
            adapter.setWallets(wallets);
        });
    }

    private void setupListeners() {
        fabAddWallet.setOnClickListener(v -> {
            // Mở dialog để Thêm mới (truyền null)
            showAddEditDialog(null);
        });
    }

    // Dialog Thêm / Sửa
    private void showAddEditDialog(Wallet wallet) {
        boolean isEdit = (wallet != null);
        String title = isEdit ? "Sửa ví" : "Thêm ví mới";

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        // Layout cho Dialog
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);

        // Input Tên Ví
        final EditText inputName = new EditText(this);
        inputName.setHint("Tên ví (VD: Tiền mặt)");
        inputName.setInputType(InputType.TYPE_CLASS_TEXT);
        if (isEdit) inputName.setText(wallet.name);
        layout.addView(inputName);

        // Input Số Dư (Chỉ cho phép khi Thêm mới)
        final EditText inputBalance = new EditText(this);
        if (!isEdit) {
            inputBalance.setHint("Số dư ban đầu (VD: 100000)");
            inputBalance.setInputType(InputType.TYPE_CLASS_NUMBER);
            layout.addView(inputBalance);
        }

        builder.setView(layout);

        // Nút Lưu/Thêm
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String name = inputName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Tên ví không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isEdit) {
                // --- Logic Sửa ---
                wallet.name = name;
                // (Không cho sửa số dư ở đây để tránh mất mát dữ liệu)
                viewModel.updateWallet(wallet);
            } else {
                // --- Logic Thêm ---
                String balanceStr = inputBalance.getText().toString();
                double balance = 0;
                if (!balanceStr.isEmpty()) {
                    balance = Double.parseDouble(balanceStr);
                }

                Wallet newWallet = new Wallet();
                newWallet.name = name;
                newWallet.balance = balance;
                viewModel.insertWallet(newWallet);
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // Dialog Xóa
    private void showDeleteDialog(Wallet wallet) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa ví '" + wallet.name + "'?\n(Các giao dịch liên quan có thể bị ảnh hưởng)")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    viewModel.deleteWallet(wallet);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}