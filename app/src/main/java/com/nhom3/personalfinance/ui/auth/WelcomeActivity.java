package com.nhom3.personalfinance.ui.auth; // Đặt trong package auth hoặc ui.main

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.nhom3.personalfinance.R;

// Lưu ý: Đảm bảo bạn đã có LoginActivity và RegisterActivity
import com.nhom3.personalfinance.ui.auth.LoginActivity;
import com.nhom3.personalfinance.ui.auth.RegisterActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Thiết lập layout bạn đã cung cấp
        setContentView(R.layout.activity_welcome);

        Button loginButton = findViewById(R.id.btnLoginMain);
        Button signupButton = findViewById(R.id.btnSignupMain);

        // --- Chuyển hướng đến LoginActivity ---
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // --- Chuyển hướng đến RegisterActivity ---
        signupButton.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}