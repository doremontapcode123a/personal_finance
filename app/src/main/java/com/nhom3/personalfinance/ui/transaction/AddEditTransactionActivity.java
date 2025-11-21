package com.nhom3.personalfinance.ui.transaction;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar; // Đã thêm import
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.nhom3.personalfinance.R;
import com.nhom3.personalfinance.data.model.SubCategory;
import com.nhom3.personalfinance.data.model.Transaction;
import com.nhom3.personalfinance.data.model.Wallet;
import com.nhom3.personalfinance.viewmodel.TransactionViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddEditTransactionActivity extends AppCompatActivity {

    private TransactionViewModel viewModel;

    private Toolbar toolbar; // Đã thêm biến Toolbar
    private TabLayout tabLayoutType;
    private Spinner spinnerWallet;
    private Spinner spinnerCategory;
    private TextInputEditText editTextAmount;
    private TextInputEditText editTextName;
    private TextInputEditText editTextNote;
    private TextView textViewDate;
    private Button buttonSave;

    private ArrayAdapter<Wallet> walletAdapter;
    private ArrayAdapter<SubCategory> incomeCategoryAdapter;
    private ArrayAdapter<SubCategory> expenseCategoryAdapter;

    private List<Wallet> walletList = new ArrayList<>();
    private List<SubCategory> incomeCategoryList = new ArrayList<>();
    private List<SubCategory> expenseCategoryList = new ArrayList<>();

    private Calendar selectedDate = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private int selectedType = 0; // Mặc định là "Khoản chi"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_transaction);

        viewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        findViews();
        setupAdapters();
        observeViewModel();
        setupListeners();
        updateDateLabel();
    }

    private void findViews() {
        // ÁNH XẠ TOOLBAR
        toolbar = findViewById(R.id.toolbar); // Cần đảm bảo ID này tồn tại trong XML

        tabLayoutType = findViewById(R.id.tab_layout_type);
        spinnerWallet = findViewById(R.id.spinner_wallet);
        spinnerCategory = findViewById(R.id.spinner_category);
        editTextAmount = findViewById(R.id.edit_text_amount);
        editTextName = findViewById(R.id.edit_text_name);
        editTextNote = findViewById(R.id.edit_text_note);
        textViewDate = findViewById(R.id.text_view_date);
        buttonSave = findViewById(R.id.button_save);
    }

    private void setupAdapters() {
        walletAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, walletList);
        walletAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWallet.setAdapter(walletAdapter);

        incomeCategoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, incomeCategoryList);
        incomeCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        expenseCategoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, expenseCategoryList);
        expenseCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerCategory.setAdapter(expenseCategoryAdapter);
    }

    private void observeViewModel() {
        viewModel.getAllWallets().observe(this, wallets -> {
            walletList.clear();
            walletList.addAll(wallets);
            walletAdapter.notifyDataSetChanged();
        });

        viewModel.getIncomeCategories().observe(this, subCategories -> {
            incomeCategoryList.clear();
            incomeCategoryList.addAll(subCategories);
            if (selectedType == 1) {
                incomeCategoryAdapter.notifyDataSetChanged();
            }
        });

        viewModel.getExpenseCategories().observe(this, subCategories -> {
            expenseCategoryList.clear();
            expenseCategoryList.addAll(subCategories);
            if (selectedType == 0) {
                expenseCategoryAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setupListeners() {

        // --- BỔ SUNG CHỨC NĂNG NÚT ĐÓNG/QUAY LẠI TRÊN TOOLBAR ---
        setSupportActionBar(toolbar);
        // Hiển thị icon quay lại
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Tắt tiêu đề mặc định nếu đã dùng TextView custom trong Toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Sự kiện nhấn icon quay lại
        toolbar.setNavigationOnClickListener(v -> {
            finish(); // Đóng Activity và quay về màn hình trước đó
        });
        // -----------------------------------------------------------

        // Sự kiện chọn Tab Thu/Chi
        tabLayoutType.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) { // Tab "Khoản chi"
                    selectedType = 0;
                    spinnerCategory.setAdapter(expenseCategoryAdapter);
                } else { // Tab "Khoản thu"
                    selectedType = 1;
                    spinnerCategory.setAdapter(incomeCategoryAdapter);
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) { }
            @Override public void onTabReselected(TabLayout.Tab tab) { }
        });

        // Sự kiện chọn Ngày
        textViewDate.setOnClickListener(v -> showDatePickerDialog());

        // Sự kiện nhấn "Lưu"
        buttonSave.setOnClickListener(v -> saveTransaction());
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, month);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateLabel();
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void updateDateLabel() {
        textViewDate.setText(dateFormat.format(selectedDate.getTime()));
    }

    private void saveTransaction() {
        String amountStr = editTextAmount.getText().toString();
        String name = editTextName.getText().toString();

        // --- Validate dữ liệu ---
        if (amountStr.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ Số tiền và Tên giao dịch", Toast.LENGTH_SHORT).show();
            return;
        }

        Wallet selectedWallet = (Wallet) spinnerWallet.getSelectedItem();
        SubCategory selectedSubCategory = (SubCategory) spinnerCategory.getSelectedItem();

        if (selectedWallet == null || selectedSubCategory == null) {
            Toast.makeText(this, "Vui lòng chọn ví và nhóm", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- Xử lý dữ liệu ---
        double amount = Double.parseDouble(amountStr);
        if (selectedType == 0) { // Nếu là "Khoản chi"
            amount = -amount; // Chuyển thành số âm
        }

        String note = editTextNote.getText().toString();
        java.util.Date date = selectedDate.getTime();

        // --- Tạo đối tượng Transaction ---
        Transaction newTransaction = new Transaction();
        newTransaction.amount = amount;
        newTransaction.name = name;
        newTransaction.note = note;
        newTransaction.date = date;

        newTransaction.WALLETid = selectedWallet.id;
        newTransaction.SUB_CATEGORYid = selectedSubCategory.id;

        // --- Gọi ViewModel để lưu ---
        viewModel.insertTransaction(newTransaction, selectedWallet); // Truyền object selectedWallet vào

        Toast.makeText(this, "Đã lưu giao dịch!", Toast.LENGTH_SHORT).show();
        finish(); // Đóng Activity sau khi lưu
    }
}