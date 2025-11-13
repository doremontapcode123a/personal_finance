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
import com.nhom3.personalfinance.R;
import com.nhom3.personalfinance.data.model.Transaction;
import com.nhom3.personalfinance.ui.transaction.TransactionAdapter;
import com.nhom3.personalfinance.viewmodel.TransactionViewModel;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.google.android.material.datepicker.MaterialDatePicker; // <-- IMPORT MỚI
import androidx.core.util.Pair; // <-- IMPORT MỚI
import java.util.Date; // <-- IMPORT MỚI

public class TransactionListFragment extends Fragment {

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

        // Chips
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

        // --- Cập nhật Adapter: Thêm Listener ---
        adapter = new TransactionAdapter(new TransactionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Transaction transaction) {
                // Hiển thị dialog "Chi tiết giao dịch"
                showDetailDialog(transaction);
            }

            @Override
            public void onItemLongClick(Transaction transaction) {
                // Hiển thị dialog "Xác nhận xóa"
                showDeleteConfirmDialog(transaction);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);

        // Lắng nghe danh sách giao dịch đã lọc
        viewModel.getFilteredTransactions().observe(getViewLifecycleOwner(), transactions -> {
            adapter.setTransactions(transactions);
        });

        // Lắng nghe tổng số dư
        viewModel.getTotalBalance().observe(getViewLifecycleOwner(), balance -> {
            if (balance != null) {
                tvTotalBalance.setText(currencyFormat.format(balance));
            }
        });
    }

    private void setupFilterListeners() {
        // Lắng nghe thay đổi bộ lọc NGÀY
        chipGroupDate.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chip_date_this_month) {
                currentDateFilter = "this_month";
            } else if (checkedId == R.id.chip_date_last_month) {
                currentDateFilter = "last_month";
            } else if (checkedId == R.id.chip_date_custom) {
                currentDateFilter = "custom"; // (Chưa làm, tạm thời)
                // Mở bộ chọn khoảng ngày
                showDateRangePicker();
                // --- HẾT CODE MỚI ---
            }
            applyFilters();
        });

        // Lắng nghe thay đổi bộ lọc LOẠI
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

    // Hàm gọi ViewModel để áp dụng bộ lọc
    private void applyFilters() {
        viewModel.setFilter(currentDateFilter, currentTypeFilter);
    }
    private void showDateRangePicker() {
        // Tạo Date Picker
        MaterialDatePicker.Builder<Pair<Long, Long>> builder =
                MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Chọn khoảng ngày");

        // Build
        MaterialDatePicker<Pair<Long, Long>> datePicker = builder.build();

        // Thêm listener khi nhấn "OK"
        datePicker.addOnPositiveButtonClickListener(selection -> {
            // Lấy 2 ngày (dưới dạng Long timestamp)
            Long startDateLong = selection.first;
            Long endDateLong = selection.second;

            // Chuyển thành Date
            Date startDate = new Date(startDateLong);
            Date endDate = new Date(endDateLong);

            // Gửi 2 ngày này cho ViewModel
            viewModel.setCustomDateFilter(startDate, endDate);

            // Cập nhật bộ lọc hiện tại
            currentDateFilter = "custom";

            // Tải lại danh sách
            applyFilters();
        });

        // Thêm listener khi nhấn "Cancel" (Để chọn lại chip "Tháng này")
        datePicker.addOnNegativeButtonClickListener(v -> {
            // Nếu người dùng Hủy, quay lại chọn "Tháng này"
            chipThisMonth.setChecked(true);
            currentDateFilter = "this_month";
            applyFilters();
        });

        // Hiển thị
        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }
    // --- HẾT HÀM MỚI ---
    // --- Dialog Chi tiết (Giống PDF) ---

    private void showDetailDialog(Transaction transaction) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String details = "Tên giao dịch: " + transaction.name + "\n"
                + "Số tiền: " + currencyFormat.format(transaction.amount) + "\n"
                + "Ngày: " + sdf.format(transaction.date) + "\n"
                + "Ghi chú: " + (transaction.note.isEmpty() ? "Không có" : transaction.note) + "\n"
                + "Loại: " + (transaction.amount < 0 ? "Chi Tiêu" : "Thu Nhập");
        // (Bạn có thể truy vấn CSDL để lấy tên Danh mục, Ví...)

        new AlertDialog.Builder(getContext())
                .setTitle("Chi tiết giao dịch")
                .setMessage(details)
                .setPositiveButton("Đóng", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // --- Dialog Xóa (Giống PDF) ---
    private void showDeleteConfirmDialog(Transaction transaction) {
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
        // Tải lại dữ liệu khi quay lại tab
        viewModel.loadFilteredTransactions();
    }
}