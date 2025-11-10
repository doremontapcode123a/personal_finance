package com.nhom3.personalfinance.ui.auth;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.nhom3.personalfinance.R;
import com.nhom3.personalfinance.viewmodel.AuthViewModel;

public class RegisterActivity extends AppCompatActivity {

    private AuthViewModel viewModel;
    private EditText edtUsername;
    private EditText edtPassword;
    private Button btnRegister;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // --- Khởi tạo ViewModel ---
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // --- Ánh xạ View ---
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnRegister = findViewById(R.id.btnRegister);

        // --- Sự kiện đăng ký ---
        btnRegister.setOnClickListener(v -> performRegister());
    }

    // --- Hàm xử lý đăng ký ---
    private void performRegister() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ tài khoản và mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gọi ViewModel.register (chạy background thread)
        viewModel.register(username, password, (success, message) -> runOnUiThread(() -> {
            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
            if (success) {
                finish(); // trở về LoginActivity
            }
        }));
    }
}
