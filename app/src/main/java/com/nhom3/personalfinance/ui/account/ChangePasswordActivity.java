package com.nhom3.personalfinance.ui.account;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.nhom3.personalfinance.R;
import com.nhom3.personalfinance.data.db.AppDatabase;
import com.nhom3.personalfinance.data.db.dao.UserDao;
import com.nhom3.personalfinance.data.model.User;
import com.nhom3.personalfinance.viewmodel.AccountViewModel;
import com.nhom3.personalfinance.viewmodel.AccountViewModelFactory;

public class ChangePasswordActivity extends AppCompatActivity {
    private AccountViewModel viewModel;

    private static final String PREF_NAME = "AppPrefs";
    private static final String PREF_USER_ID = "LOGGED_IN_USER_ID";

    private EditText currentPasswordInput, newPasswordInput, confirmPasswordInput;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Lấy ID người dùng đã đăng nhập. Nếu không tìm thấy, mặc định là -1.
        int currentUserId = getSharedPreferences(PREF_NAME, MODE_PRIVATE).getInt(PREF_USER_ID, -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "Lỗi: Phiên đăng nhập không hợp lệ.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        UserDao userDao = AppDatabase.getDatabase(this).userDao();
        // Khởi tạo Factory và ViewModel bằng ID người dùng hợp lệ
        AccountViewModelFactory factory = new AccountViewModelFactory(userDao, currentUserId);
        viewModel = new ViewModelProvider(this, factory).get(AccountViewModel.class);

        currentPasswordInput = findViewById(R.id.edit_text_old_password);
        newPasswordInput = findViewById(R.id.edit_text_new_password);
        confirmPasswordInput = findViewById(R.id.edit_text_confirm_password);
        saveButton = findViewById(R.id.button_save_password);


        saveButton.setEnabled(false); // Vô hiệu hóa cho đến khi dữ liệu người dùng được tải

        //  1. Observer để kích hoạt nút sau khi dữ liệu tải thành công
        viewModel.getCurrentUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    saveButton.setEnabled(true);
                    viewModel.getCurrentUser().removeObserver(this);
                }
            }
        });

        //  2. Observer để nhận thông báo kết quả
        viewModel.getPasswordChangeMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();

                // Nếu thành công, đóng Activity và quay về trang trước
                if (message.equals("Đổi mật khẩu thành công!")) {
                    finish();
                }
            }
        });

        //  3. Gắn Listener vào nút
        saveButton.setOnClickListener(v -> {
            String currentPass = currentPasswordInput.getText().toString();
            String newPass = newPasswordInput.getText().toString();
            String confirmPass = confirmPasswordInput.getText().toString();

            // Kiểm tra UI: Mật khẩu mới và xác nhận có khớp không
            if (newPass.isEmpty() || currentPass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPass.equals(confirmPass)) {
                Toast.makeText(this, "Mật khẩu mới và xác nhận mật khẩu không khớp.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gọi phương thức mới trong ViewModel để xử lý logic đổi mật khẩu
            viewModel.validateAndChangePassword(currentPass, newPass);
        });
    }
}