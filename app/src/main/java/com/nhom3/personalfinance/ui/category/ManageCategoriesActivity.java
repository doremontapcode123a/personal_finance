package com.nhom3.personalfinance.ui.category;

import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.nhom3.personalfinance.R;
import com.nhom3.personalfinance.data.model.SubCategory;
import com.nhom3.personalfinance.viewmodel.CategoryViewModel;

import java.util.List;

public class ManageCategoriesActivity extends AppCompatActivity {

    private CategoryViewModel viewModel;
    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private TabLayout tabLayout;
    private TextInputEditText editTextCategoryName;
    private Button buttonAddCategory;

    private int currentTab = 0; // 0 = Chi, 1 = Thu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_categories);

        viewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        findViews();
        setupRecyclerView();
        setupListeners();
        observeViewModel();
    }

    private void findViews() {
        recyclerView = findViewById(R.id.recycler_view_categories);
        tabLayout = findViewById(R.id.tab_layout_categories);
        editTextCategoryName = findViewById(R.id.edit_text_category_name);
        buttonAddCategory = findViewById(R.id.button_add_category);
    }

    private void setupRecyclerView() {
        adapter = new CategoryAdapter(new CategoryAdapter.OnCategoryClickListener() {
            @Override
            public void onCategoryClick(SubCategory category) {
                showEditDialog(category);
            }

            @Override
            public void onCategoryLongClick(SubCategory category) {
                showDeleteDialog(category);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getExpenseCategories().observe(this, subCategories -> {
            if (currentTab == 0) adapter.setCategories(subCategories);
        });

        viewModel.getIncomeCategories().observe(this, subCategories -> {
            if (currentTab == 1) adapter.setCategories(subCategories);
        });
    }

    private void setupListeners() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                if (currentTab == 0) {
                    List<SubCategory> expenses = viewModel.getExpenseCategories().getValue();
                    adapter.setCategories(expenses != null ? expenses : List.of());
                } else {
                    List<SubCategory> incomes = viewModel.getIncomeCategories().getValue();
                    adapter.setCategories(incomes != null ? incomes : List.of());
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        buttonAddCategory.setOnClickListener(v -> {
            String name = editTextCategoryName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên nhóm", Toast.LENGTH_SHORT).show();
                return;
            }

            SubCategory newCategory = new SubCategory();
            newCategory.name = name;
            newCategory.CATEGORYid = (currentTab == 0) ? 2 : 1;

            // ViewModel sẽ tự lo việc gán USERid
            viewModel.insertSubCategory(newCategory);

            editTextCategoryName.setText("");
            Toast.makeText(this, "Đã thêm nhóm!", Toast.LENGTH_SHORT).show();
        });
    }

    private void showEditDialog(SubCategory category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sửa tên nhóm");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(category.name);
        builder.setView(input);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                category.name = newName;
                viewModel.updateSubCategory(category);
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showDeleteDialog(SubCategory category) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa nhóm '" + category.name + "'?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    viewModel.deleteSubCategory(category);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.loadCategories();
    }
}