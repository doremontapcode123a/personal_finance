package com.nhom3.personalfinance.ui.auth;

import android.content.Context;
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

import com.nhom3.personalfinance.viewmodel.LoginViewModel;
import com.nhom3.personalfinance.viewmodel.LoginViewModelFactory;

public class LoginActivity extends AppCompatActivity {
    private LoginViewModel viewModel;
    // Khai báo các khóa và tên tệp SharedPreferences phải KHỚP với AccountFragment
    private static final String PREF_USER_ID = "current_user_id";
    private static final String PREF_USERNAME_KEY = "LOGGED_IN_USER"; // Khóa USERNAME (Phải khớp)
    private static final String PREF_FILE_NAME = "AppPrefs";          // Tên tệp SharedPreferences (Phải khớp)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo ViewModel
        UserDao userDao = AppDatabase.getDatabase(this).userDao();
        LoginViewModelFactory factory = new LoginViewModelFactory(userDao);
        viewModel = new ViewModelProvider(this, factory).get(LoginViewModel.class);

        // Ánh xạ ID
        EditText usernameInput = findViewById(R.id.edtUsername);
        EditText passwordInput = findViewById(R.id.edtPassword);
        Button loginButton = findViewById(R.id.btnLogin);
        TextView signUpTextView = findViewById(R.id.tvSignup);

        // Logic Đăng nhập
        loginButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString();
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.attemptLogin(username, password); // Gọi DAO trên luồng nền
        });

        // Logic chuyển sang màn hình Đăng ký
        signUpTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Quan sát trạng thái
        viewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                // *** SỬA LỖI QUAN TRỌNG: LƯU CẢ ID VÀ USERNAME ***
                getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE)
                        .edit()
                        .putInt(PREF_USER_ID, user.getId())
                        .putString(PREF_USERNAME_KEY, user.getUsername()) // Lưu username vào khóa LOGGED_IN_USER
                        .apply();

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
    }
}