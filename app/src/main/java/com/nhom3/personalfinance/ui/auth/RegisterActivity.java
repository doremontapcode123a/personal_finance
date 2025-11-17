package com.nhom3.personalfinance.ui.auth;

import android.content.Intent; // Cần thiết để chuyển Activity
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView; // Cần thiết để ánh xạ TextView
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.nhom3.personalfinance.R;
// Import LoginActivity nếu bạn muốn chuyển hẳn sang LoginActivity
import com.nhom3.personalfinance.ui.auth.LoginActivity;
import com.nhom3.personalfinance.viewmodel.RegisterViewModel;

public class RegisterActivity extends AppCompatActivity {

    private RegisterViewModel viewModel;
    private EditText edtUsername;
    private EditText edtPassword;
    private Button btnRegister;
    private TextView tvLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        // --- Ánh xạ  ---
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        // --- Sự kiện đăng ký ---
        btnRegister.setOnClickListener(v -> performRegister());

        // --- Sự kiện chuyển sang màn hình Đăng nhập ---
        tvLogin.setOnClickListener(v -> {
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
                if (user != null) {
                    // Đăng ký thành công, quay lại màn hình Login
                    finish();
                }
            });
        });
    }
}