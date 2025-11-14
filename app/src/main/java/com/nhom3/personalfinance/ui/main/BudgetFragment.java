package com.nhom3.personalfinance.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.nhom3.personalfinance.R;
import com.nhom3.personalfinance.data.model.Budget;
import com.nhom3.personalfinance.data.model.SubCategory;
import com.nhom3.personalfinance.viewmodel.BudgetViewModel;

import java.util.ArrayList;
import java.util.List;

public class BudgetFragment extends Fragment {

    private BudgetViewModel viewModel;
    private RecyclerView recyclerView;
    private BudgetAdapter adapter;
    private Spinner spinnerCategory;
    private TextInputEditText editTextAmount;
    private Button buttonAddBudget;

    private List<SubCategory> expenseCategoriesList = new ArrayList<>();
    private ArrayAdapter<SubCategory> spinnerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget, container, false);

        viewModel = new ViewModelProvider(this).get(BudgetViewModel.class);

        findViews(view);
        setupSpinner();
        setupRecyclerView();
        setupListeners();
        observeViewModel();

        return view;
    }

    private void findViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_budgets);
        spinnerCategory = view.findViewById(R.id.spinner_budget_category);
        editTextAmount = view.findViewById(R.id.edit_text_budget_amount);
        buttonAddBudget = view.findViewById(R.id.button_add_budget);
    }

    private void setupSpinner() {
        spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, expenseCategoriesList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(spinnerAdapter);
    }

    private void setupRecyclerView() {
        adapter = new BudgetAdapter(viewModel, budget -> {
            // Long click để Xóa
            showDeleteDialog(budget);
        });
        recyclerView.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getExpenseCategories().observe(getViewLifecycleOwner(), categories -> {
            expenseCategoriesList.clear();
            expenseCategoriesList.addAll(categories);
            spinnerAdapter.notifyDataSetChanged();
            adapter.setCategoryMap(categories);
        });

        viewModel.getAllBudgets().observe(getViewLifecycleOwner(), budgets -> {
            adapter.setBudgets(budgets);
        });
    }

    // --- HÀM ĐÃ SỬA LỖI ---
    private void setupListeners() {
        buttonAddBudget.setOnClickListener(v -> {
            String amountStr = editTextAmount.getText().toString();
            SubCategory selectedCategory = (SubCategory) spinnerCategory.getSelectedItem();

            if (selectedCategory == null) {
                Toast.makeText(getContext(), "Vui lòng chọn nhóm", Toast.LENGTH_SHORT).show();
                return;
            }

            double budgetAmount;
            try {
                // Thử chuyển đổi số
                budgetAmount = Double.parseDouble(amountStr);
                if (budgetAmount <= 0) {
                    Toast.makeText(getContext(), "Số tiền phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                // Bắt lỗi nếu người dùng nhập rỗng hoặc nhập chữ
                Toast.makeText(getContext(), "Vui lòng nhập số tiền hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            // Nếu mọi thứ OK, tiếp tục
            Budget newBudget = new Budget();
            newBudget.budget = budgetAmount;
            newBudget.SUB_CATEGORYid = selectedCategory.id;

            viewModel.insertBudget(newBudget);

            Toast.makeText(getContext(), "Đã thiết lập ngân sách!", Toast.LENGTH_SHORT).show();
            editTextAmount.setText("");
        });
    }
    // --- KẾT THÚC SỬA LỖI ---

    private void showDeleteDialog(Budget budget) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có muốn xóa ngân sách này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    viewModel.deleteBudget(budget);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.loadBudgets();
        viewModel.loadExpenseCategories();
    }
}