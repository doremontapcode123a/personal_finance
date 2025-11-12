package com.nhom3.personalfinance.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.nhom3.personalfinance.R;
import com.nhom3.personalfinance.data.db.AppDatabase;
import com.nhom3.personalfinance.data.db.dao.UserDao;
import com.nhom3.personalfinance.ui.main.MainActivity;

// 🔥 ĐÃ SỬA: Import LoginViewModel và LoginViewModelFactory
import com.nhom3.personalfinance.viewmodel.LoginViewModel;
import com.nhom3.personalfinance.viewmodel.LoginViewModelFactory;

public class LoginActivity extends AppCompatActivity {
    private LoginViewModel viewModel;
    private static final String PREF_USER_ID = "current_user_id";
    private static final String PREF_NAME = "AUTH_PREFS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo ViewModel
        UserDao userDao = AppDatabase.getDatabase(this).userDao();
        LoginViewModelFactory factory = new LoginViewModelFactory(userDao); // 🔥 ĐÃ SỬA: Dùng LoginViewModelFactory
        viewModel = new ViewModelProvider(this, factory).get(LoginViewModel.class); // 🔥 ĐÃ SỬA: Dùng LoginViewModel

        // ÁNH XẠ ID CHÍNH XÁC
        EditText usernameInput = findViewById(R.id.edtUsername);
        EditText passwordInput = findViewById(R.id.edtPassword);
        Button loginButton = findViewById(R.id.btnLogin);
        TextView signUpTextView = findViewById(R.id.tvSignup);

        // --- Logic Đăng nhập ---
        loginButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString();
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.attemptLogin(username, password); // Gọi DAO trên luồng nền
        });

        // 💡 BỔ SUNG: Logic chuyển sang màn hình Đăng ký
        signUpTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
        // ------------------------------------------

        // --- Quan sát trạng thái ---
        viewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                // Lưu ID và chuyển Activity
                getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit().putInt(PREF_USER_ID, user.getId()).apply();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        viewModel.getLoginMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });
        // --------------------------
    }
}