package com.nhom3.personalfinance.ui.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.nhom3.personalfinance.R;
import com.nhom3.personalfinance.data.db.AppDatabase;
import com.nhom3.personalfinance.data.db.dao.UserDao;
import com.nhom3.personalfinance.ui.account.ChangePasswordActivity;
import com.nhom3.personalfinance.ui.auth.WelcomeActivity;
import com.nhom3.personalfinance.viewmodel.AccountViewModel;
import com.nhom3.personalfinance.viewmodel.AccountViewModelFactory;

public class AccountFragment extends Fragment {
    private AccountViewModel viewModel;

    private static final String PREF_NAME = "AUTH_PREFS";
    private static final String PREF_USER_ID = "current_user_id";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Context context = requireContext();
        int currentUserId = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getInt(PREF_USER_ID, -1);
        if (currentUserId == -1) {
            Toast.makeText(context, "Lỗi: Vui lòng đăng nhập.", Toast.LENGTH_SHORT).show();
            return;
        }

        UserDao userDao = AppDatabase.getDatabase(context).userDao();
        AccountViewModelFactory factory = new AccountViewModelFactory(userDao, currentUserId);
        viewModel = new ViewModelProvider(this, factory).get(AccountViewModel.class);

        // ÁNH XẠ
        TextView usernameTextView = view.findViewById(R.id.tvUsername);
        TextView changePasswordTextView = view.findViewById(R.id.tvChangePassword);
        TextView logoutTextView = view.findViewById(R.id.tvLogout);
        TextView deleteAccountTextView = view.findViewById(R.id.tvDeleteAccount);

        // Cập nhật thông tin người dùng
        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                usernameTextView.setText(user.getUsername());
            }
        });

        // điều hướng khi Đăng xuất/Xóa thành công
        observeNavigation();

        changePasswordTextView.setOnClickListener(v -> startActivity(new Intent(getActivity(), ChangePasswordActivity.class)));

        logoutTextView.setOnClickListener(v -> showLogoutConfirmationDialog(context));

        deleteAccountTextView.setOnClickListener(v -> showDeleteAccountConfirmationDialog(context));
    }

    /**
     * Phương thức lắng nghe LiveData từ ViewModel để xử lý điều hướng an toàn.
     */
    private void observeNavigation() {
        viewModel.getNavigateToWelcome().observe(getViewLifecycleOwner(), shouldNavigate -> {
            if (Boolean.TRUE.equals(shouldNavigate)) {

                // 1. Xóa ID người dùng khỏi SharedPreferences (Xóa session)
                requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                        .edit()
                        .remove(PREF_USER_ID)
                        .apply();

                // 2. Chuyển hướng về WelcomeActivity
                Intent intent = new Intent(requireActivity(), WelcomeActivity.class);
                // FLAG QUAN TRỌNG: Xóa tất cả Activity đang chạy
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);

                // 3. Kết thúc
                if (getActivity() != null) getActivity().finish();
            }
        });
    }

    private void showLogoutConfirmationDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("Xác nhận Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    // Gọi ViewModel để xử lý logic
                    viewModel.logoutUser();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showDeleteAccountConfirmationDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("Xóa tài khoản")
                .setMessage("Cảnh báo: Bạn có chắc chắn muốn xóa tài khoản không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    // Gọi ViewModel để xử lý xóa DB
                    viewModel.deleteCurrentAccount();
                    Toast.makeText(context, "Đang xử lý xóa tài khoản...", Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}