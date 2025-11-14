package com.nhom3.personalfinance.ui.account;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class ConfirmDeleteDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext())
                .setTitle("Xóa tài khoản")
                .setMessage("Bạn có chắc muốn xóa tài khoản này không?")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    // TODO: Gọi AccountViewModel để xử lý xóa tài khoản
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .create();
    }
}
