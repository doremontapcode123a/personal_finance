package com.nhom3.personalfinance.ui.auth;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.nhom3.personalfinance.R;
import com.nhom3.personalfinance.viewmodel.AuthViewModel;
import android.content.Intent;

public class RegisterActivity extends AppCompatActivity {

    private AuthViewModel viewModel;
    private EditText edtUsername;
    private EditText edtPassword;
    private Button btnRegister;
    private TextView signupTextView;

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
        signupTextView = findViewById(R.id.tvLogin);

        // --- Sự kiện đăng ký ---
        btnRegister.setOnClickListener(v -> performRegister());

        // --- Sự kiện "Tôi có tài khoản" ---
        // Chỉ sửa chỗ này: đặt listener ở onCreate()
        signupTextView.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    // --- Hàm xử lý đăng ký ---
    private void performRegister() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ tài khoản và mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.register(username, password, (user, message) -> {
            runOnUiThread(() -> {
                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();

                // Kiểm tra nếu đăng ký thành công
                if (user != null) { // Đổi "if (success)" thành "if (user != null)"

                    // Khai báo Intent để chuyển sang LoginActivity
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);

                    // Bắt đầu Activity mới
                    startActivity(intent);

                    // Đóng màn hình đăng ký hiện tại
                    finish();
                }
            });
        });
    }
}
