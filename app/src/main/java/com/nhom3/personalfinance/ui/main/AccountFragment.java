package com.nhom3.personalfinance.ui.main;

import android.content.Intent;
import com.nhom3.personalfinance.ui.category.ManageCategoriesActivity;
import com.nhom3.personalfinance.ui.account.ManageWalletsActivity;
import com.nhom3.personalfinance.ui.auth.LoginActivity;
import com.nhom3.personalfinance.ui.account.ChangePasswordActivity;
import com.nhom3.personalfinance.viewmodel.AuthViewModel;

import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import androidx.appcompat.app.AlertDialog; // <-- IMPORT MỚI
import androidx.lifecycle.ViewModelProvider;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nhom3.personalfinance.R;
// Import các Activity chúng ta sẽ tạo ở bước sau
// import com.nhom3.personalfinance.ui.category.ManageCategoriesActivity;
// import com.nhom3.personalfinance.ui.account.ChangePasswordActivity;

// Import Activity Đăng nhập
import com.nhom3.personalfinance.ui.auth.LoginActivity;

public class AccountFragment extends Fragment {

    private TextView btnManageWallets, btnManageCategories, btnChangePassword, btnLogout, btnDeleteAccount;
    private AuthViewModel authViewModel;
    private TextView textUsername;
    private String currentUsername;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);


        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        textUsername = view.findViewById(R.id.text_view_username);
        btnManageWallets = view.findViewById(R.id.btn_manage_wallets);
        btnManageCategories = view.findViewById(R.id.btn_manage_categories);
        btnChangePassword = view.findViewById(R.id.btn_change_password);
        btnLogout = view.findViewById(R.id.btn_logout);
        btnDeleteAccount = view.findViewById(R.id.btn_delete_account);

        // ---  ĐỌC USERNAME TỪ SESSION ---
        SharedPreferences prefs = getActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        currentUsername = prefs.getString("LOGGED_IN_USER", "Lỗi User"); // Lấy username đã lưu
        textUsername.setText(currentUsername); // Cập nhật TextView "admin"
        // --- KẾT THÚC BƯỚC SỬA ---

        setupListeners();

        return view;
    }

    private void setupListeners() {
        // Nút "Nhóm" (Quản lý Danh mục)
        btnManageCategories.setOnClickListener(v -> {
            // Sẽ mở ManageCategoriesActivity ở bước sau
            Intent intent = new Intent(getActivity(), ManageCategoriesActivity.class);
            startActivity(intent);
        });

        // Nút "Đổi mật khẩu"
        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            // intent.putExtra("USERNAME", currentUsername);
            startActivity(intent);
        });

        // Nút "Đăng xuất"
        btnLogout.setOnClickListener(v -> {
            SharedPreferences prefs = getActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            prefs.edit().remove("LOGGED_IN_USER").apply();
            // Quay về màn hình Đăng nhập
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        });

        // Nút "Xóa tài khoản"
        btnDeleteAccount.setOnClickListener(v -> {
            showConfirmDeleteDialog();
        });

        btnManageWallets.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ManageWalletsActivity.class);
            startActivity(intent);
        });
    }
    // --- HÀM  HIỂN THỊ DIALOG XÁC NHẬN XÓA ---
    private void showConfirmDeleteDialog() {
        final EditText inputPassword = new EditText(getContext());
        inputPassword.setHint("Nhập mật khẩu để xác nhận");
        inputPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa tài khoản này? Mọi dữ liệu sẽ bị mất vĩnh viễn.")
                .setView(inputPassword) // Thêm ô nhập mật khẩu
                .setPositiveButton("Xóa", (dialog, which) -> {
                    String password = inputPassword.getText().toString();
                    if (password.isEmpty()) {
                        Toast.makeText(getContext(), "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    authViewModel.deleteAccount(currentUsername, password, (user, message) -> { // Đổi "success" thành "user"
                        if (getActivity() == null) {
                            return;
                        }
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            if (user != null) { // Đổi "if (success)" thành "if (user != null)"
                                // Nếu xóa thành công, đăng xuất
                                btnLogout.performClick();
                            }
                        });
                    });

                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}