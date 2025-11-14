package com.nhom3.personalfinance.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.SharedPreferences; // <-- IMPORT MỚI

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.nhom3.personalfinance.R;
import com.nhom3.personalfinance.ui.main.MainActivity;
import com.nhom3.personalfinance.viewmodel.AuthViewModel;

public class LoginActivity extends AppCompatActivity {
    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        EditText edtUser = findViewById(R.id.edtUsername);
        EditText edtPass = findViewById(R.id.edtPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnRegister = findViewById(R.id.btnGoRegister);

        // ... (bên trong LoginActivity.java)
        btnLogin.setOnClickListener(v -> {
            String username = edtUser.getText().toString().trim();
            String password = edtPass.getText().toString().trim();

            // SỬA LẠI CALLBACK
            viewModel.login(username, password, (user, message) -> { // "user" thay vì "success"
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    if (user != null) { // Kiểm tra user != null

                        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        // LƯU CẢ 2: ID và USERNAME
                        editor.putInt("LOGGED_IN_USER_ID", user.id);
                        editor.putString("LOGGED_IN_USER", user.username);
                        editor.apply();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            });
        });

        btnRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }
}
