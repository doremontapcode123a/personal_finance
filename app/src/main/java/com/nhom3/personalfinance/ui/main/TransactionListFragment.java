package com.nhom3.personalfinance.ui.main;

import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import androidx.core.util.Pair;
import com.nhom3.personalfinance.R;
import com.nhom3.personalfinance.data.model.Transaction;
import com.nhom3.personalfinance.ui.transaction.TransactionAdapter;
import com.nhom3.personalfinance.viewmodel.TransactionViewModel;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class    TransactionListFragment extends Fragment {

    private TransactionViewModel viewModel;
    private RecyclerView recyclerView;
    private TransactionAdapter adapter;

    private TextView tvTotalBalance;
    private ChipGroup chipGroupDate;
    private ChipGroup chipGroupType;
    private Chip chipThisMonth, chipLastMonth, chipCustom;
    private Chip chipAll, chipIncome, chipExpense;

    private String currentDateFilter = "this_month";
    private String currentTypeFilter = "all";
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction_list, container, false);

        findViews(view);
        setupRecyclerView();
        setupViewModel();
        setupFilterListeners();

        return view;
    }

    private void findViews(View view) {
        tvTotalBalance = view.findViewById(R.id.text_view_total_balance);
        recyclerView = view.findViewById(R.id.recycler_view_transactions);
        chipGroupDate = view.findViewById(R.id.chip_group_date_filter);
        chipGroupType = view.findViewById(R.id.chip_group_type_filter);
        chipThisMonth = view.findViewById(R.id.chip_date_this_month);
        chipLastMonth = view.findViewById(R.id.chip_date_last_month);
        chipCustom = view.findViewById(R.id.chip_date_custom);
        chipAll = view.findViewById(R.id.chip_type_all);
        chipIncome = view.findViewById(R.id.chip_type_income);
        chipExpense = view.findViewById(R.id.chip_type_expense);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        adapter = new TransactionAdapter(new TransactionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Transaction transaction) {
                showDetailDialog(transaction);
            }

            @Override
            public void onItemLongClick(Transaction transaction) {
                showDeleteConfirmDialog(transaction);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);

        viewModel.getFilteredTransactions().observe(getViewLifecycleOwner(), transactions -> {
            adapter.setTransactions(transactions);
        });

        viewModel.getTotalBalance().observe(getViewLifecycleOwner(), balance -> {
            if (balance != null) {
                tvTotalBalance.setText(currencyFormat.format(balance));
            }
        });
    }

    private void setupFilterListeners() {
        chipGroupDate.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chip_date_this_month) {
                currentDateFilter = "this_month";
                applyFilters();
            } else if (checkedId == R.id.chip_date_last_month) {
                currentDateFilter = "last_month";
                applyFilters();
            } else if (checkedId == R.id.chip_date_custom) {
                showDateRangePicker();
            }
        });

        chipGroupType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chip_type_all) {
                currentTypeFilter = "all";
            } else if (checkedId == R.id.chip_type_income) {
                currentTypeFilter = "income";
            } else if (checkedId == R.id.chip_type_expense) {
                currentTypeFilter = "expense";
            }
            applyFilters();
        });
    }

    private void applyFilters() {
        viewModel.setFilter(currentDateFilter, currentTypeFilter);
    }

    private void showDateRangePicker() {
        MaterialDatePicker.Builder<Pair<Long, Long>> builder =
                MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Chọn khoảng ngày");

        MaterialDatePicker<Pair<Long, Long>> datePicker = builder.build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            Date startDate = new Date(selection.first);
            Date endDate = new Date(selection.second);

            // Cập nhật endDate để bao gồm 23:59:59 của ngày cuối
            Calendar cal = Calendar.getInstance();
            cal.setTime(endDate);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            endDate = cal.getTime();

            viewModel.setCustomDateFilter(startDate, endDate);
            currentDateFilter = "custom";
            applyFilters();
        });

        datePicker.addOnNegativeButtonClickListener(v -> {
            chipThisMonth.setChecked(true);
            currentDateFilter = "this_month";
            applyFilters();
        });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    private void showDetailDialog(Transaction transaction) {
        // (code hàm này không đổi)
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String details = "Tên giao dịch: " + transaction.name + "\n"
                + "Số tiền: " + currencyFormat.format(transaction.amount) + "\n"
                + "Ngày: " + sdf.format(transaction.date) + "\n"
                + "Ghi chú: " + (transaction.note.isEmpty() ? "Không có" : transaction.note) + "\n"
                + "Loại: " + (transaction.amount < 0 ? "Chi Tiêu" : "Thu Nhập");

        new AlertDialog.Builder(getContext())
                .setTitle("Chi tiết giao dịch")
                .setMessage(details)
                .setPositiveButton("Đóng", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showDeleteConfirmDialog(Transaction transaction) {
        // (code hàm này không đổi)
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa giao dịch này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    viewModel.deleteTransaction(transaction);
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        // --- SỬA LỖI 1 ---
        // Tải lại cả danh sách và số dư
        viewModel.refreshData();
    }
}