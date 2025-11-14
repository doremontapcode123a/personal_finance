package com.nhom3.personalfinance.ui.category;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.nhom3.personalfinance.R;
import com.nhom3.personalfinance.data.model.SubCategory;
import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<SubCategory> categories = new ArrayList<>();

    // --- Interface cho Click Listener ---
    public interface OnCategoryClickListener {
        void onCategoryClick(SubCategory category); // Click để Sửa
        void onCategoryLongClick(SubCategory category); // Long click để Xóa
    }
    private final OnCategoryClickListener listener;

    public CategoryAdapter(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        SubCategory currentCategory = categories.get(position);
        holder.name.setText(currentCategory.name);

        // --- Gán sự kiện ---
        holder.itemView.setOnClickListener(v -> listener.onCategoryClick(currentCategory));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onCategoryLongClick(currentCategory);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void setCategories(List<SubCategory> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView name;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_view_category_name);
        }
    }
}