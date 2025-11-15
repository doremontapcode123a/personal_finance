package com.nhom3.personalfinance.ui.auth;

import android.content.Intent; // 🔥 BỔ SUNG: Import Intent
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView; // 🔥 BỔ SUNG: Import TextView
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.nhom3.personalfinance.R;
import com.nhom3.personalfinance.data.db.AppDatabase;
import com.nhom3.personalfinance.data.db.dao.UserDao;

import com.nhom3.personalfinance.viewmodel.RegisterViewModel;
import com.nhom3.personalfinance.viewmodel.RegisterViewModelFactory;

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

        // --- KHỞI TẠO REGISTER VIEWMODEL ---
        UserDao userDao = AppDatabase.getDatabase(this).userDao();
        RegisterViewModelFactory factory = new RegisterViewModelFactory(userDao);
        viewModel = new ViewModelProvider(this, factory).get(RegisterViewModel.class);

        // --- Ánh xạ View ---
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        // --- Sự kiện đăng ký ---
        btnRegister.setOnClickListener(v -> performRegister());

        // 🔥 LOGIC CHUYỂN SANG MÀN HÌNH ĐĂNG NHẬP 🔥
        tvLogin.setOnClickListener(v -> {
            // Tạo Intent để chuyển sang LoginActivity
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
        // ------------------------------------------

        // --- QUAN SÁT TRẠNG THÁI ĐĂNG KÝ (LIVE DATA) ---
        observeRegistrationStatus();
    }

    private void observeRegistrationStatus() {
        // Lắng nghe thông báo kết quả đăng ký từ ViewModel
        viewModel.getRegistrationMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();

                // Kiểm tra xem đăng ký có thành công không
                if (message.contains("Đăng ký thành công")) {
                    finish(); // Quay lại LoginActivity
                }
            }
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

        viewModel.register(username, password);
    }
}