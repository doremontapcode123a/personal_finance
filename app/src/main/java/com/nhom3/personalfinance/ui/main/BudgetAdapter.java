package com.nhom3.personalfinance.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.nhom3.personalfinance.R;
import com.nhom3.personalfinance.data.model.Budget;
import com.nhom3.personalfinance.data.model.SubCategory;
import com.nhom3.personalfinance.viewmodel.BudgetViewModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {

    private List<Budget> budgets = new ArrayList<>();
    private Map<Integer, SubCategory> categoryMap; // Map để lấy Tên từ ID
    private BudgetViewModel viewModel; // Để lấy số tiền đã chi
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    // --- Interface cho Click ---
    public interface OnBudgetClickListener {
        void onBudgetLongClick(Budget budget); // Long click để Xóa
    }
    private final OnBudgetClickListener listener;

    public BudgetAdapter(BudgetViewModel viewModel, OnBudgetClickListener listener) {
        this.viewModel = viewModel;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_budget, parent, false);
        return new BudgetViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {
        Budget currentBudget = budgets.get(position);

        // 1. Lấy Tên Nhóm từ Map
        String categoryName = "Không rõ";
        if (categoryMap != null && categoryMap.containsKey(currentBudget.SUB_CATEGORYid)) {
            categoryName = categoryMap.get(currentBudget.SUB_CATEGORYid).name;
        }
        holder.categoryName.setText(categoryName);

        // 2. Lấy số tiền đã chi (CHẠY TRÊN MAIN THREAD - CẦN CẢI THIỆN NẾU CHẬM)
        double spent = viewModel.getSpentAmountForCategory(currentBudget.SUB_CATEGORYid);
        double total = currentBudget.budget;

        // 3. Cập nhật Text
        String amountText = currencyFormat.format(spent) + " / " + currencyFormat.format(total);
        holder.amount.setText(amountText);

        // 4. Cập nhật ProgressBar
        if (total > 0) {
            int progress = (int) ((spent / total) * 100);
            holder.progressBar.setProgress(progress);
        } else {
            holder.progressBar.setProgress(0);
        }

        // 5. Gán sự kiện
        holder.itemView.setOnLongClickListener(v -> {
            listener.onBudgetLongClick(currentBudget);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return budgets.size();
    }

    public void setBudgets(List<Budget> budgets) {
        this.budgets = budgets;
        notifyDataSetChanged();
    }

    // Hàm để Fragment cập nhật Map
    public void setCategoryMap(List<SubCategory> categories) {
        this.categoryMap = categories.stream()
                .collect(Collectors.toMap(cat -> cat.id, cat -> cat));
    }

    class BudgetViewHolder extends RecyclerView.ViewHolder {
        private TextView categoryName;
        private TextView amount;
        private ProgressBar progressBar;

        public BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.text_view_budget_category_name);
            amount = itemView.findViewById(R.id.text_view_budget_amount);
            progressBar = itemView.findViewById(R.id.progress_bar_budget);
        }
    }
}