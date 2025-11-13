package com.nhom3.personalfinance.ui.account;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.content.Context; // <-- IMPORT MỚI
import android.content.SharedPreferences; // <-- IMPORT MỚI

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.nhom3.personalfinance.R;
import com.nhom3.personalfinance.viewmodel.AuthViewModel;

public class ChangePasswordActivity extends AppCompatActivity {

    private AuthViewModel viewModel;
    private TextInputEditText edtOldPass, edtNewPass, edtConfirmPass;
    private Button btnSavePassword;

    // Giả sử bạn lưu username khi đăng nhập, nếu không hãy dùng "admin"
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // --- THÊM DÒNG NÀY VÀO ---
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        // --- HẾT ---

        edtOldPass = findViewById(R.id.edit_text_old_password);
        edtNewPass = findViewById(R.id.edit_text_new_password);
        edtConfirmPass = findViewById(R.id.edit_text_confirm_password);
        btnSavePassword = findViewById(R.id.button_save_password);
        // --- BƯỚC SỬA: ĐỌC USERNAME TỪ SESSION ---
        // ĐỌC USERNAME TỪ "PHIÊN LÀM VIỆC"
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        currentUsername = prefs.getString("LOGGED_IN_USER", null); // Lấy username đã lưu
        // --- KẾT THÚC BƯỚC SỬA ---

        btnSavePassword.setOnClickListener(v -> handleChangePassword());
    }

    private void handleChangePassword() {
        String oldPass = edtOldPass.getText().toString();
        String newPass = edtNewPass.getText().toString();
        String confirmPass = edtConfirmPass.getText().toString();

        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(confirmPass)) {
            Toast.makeText(this, "Mật khẩu mới không khớp!", Toast.LENGTH_SHORT).show();
            return;
        }

        // SỬA LỖI Ở ĐÂY
        viewModel.changePassword(currentUsername, oldPass, newPass, (user, message) -> { // Đổi "success" thành "user"
            runOnUiThread(() -> {
                Toast.makeText(ChangePasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                if (user != null) { // Đổi "if (success)" thành "if (user != null)"
                    finish(); // Đóng màn hình nếu thành công
                }
            });
        });
    }
}