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

    // Biến cờ, 0 = Chi, 1 = Thu
    private int selectedType = 0; // Mặc định là "Khoản chi"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_transaction);

        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        // Ánh xạ View
        findViews();

        // Cài đặt Adapter cho các Spinner
        setupAdapters();

        // Lắng nghe dữ liệu từ ViewModel
        observeViewModel();

        // Cài đặt sự kiện
        setupListeners();

        // Cập nhật ngày mặc định
        updateDateLabel();
    }

    private void findViews() {
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
        // Adapter cho Ví
        walletAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, walletList);
        walletAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWallet.setAdapter(walletAdapter);

        // Adapter cho Nhóm Thu
        incomeCategoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, incomeCategoryList);
        incomeCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Adapter cho Nhóm Chi
        expenseCategoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, expenseCategoryList);
        expenseCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Mặc định hiển thị adapter Khoản Chi
        spinnerCategory.setAdapter(expenseCategoryAdapter);
    }

    private void observeViewModel() {
        // Lấy danh sách Ví
        viewModel.getAllWallets().observe(this, wallets -> {
            walletList.clear();
            walletList.addAll(wallets);
            walletAdapter.notifyDataSetChanged();
        });

        // Lấy danh sách Nhóm Thu
        viewModel.getIncomeCategories().observe(this, subCategories -> {
            incomeCategoryList.clear();
            incomeCategoryList.addAll(subCategories);
            // Cập nhật adapter nếu đang ở tab Thu
            if (selectedType == 1) {
                incomeCategoryAdapter.notifyDataSetChanged();
            }
        });

        // Lấy danh sách Nhóm Chi
        viewModel.getExpenseCategories().observe(this, subCategories -> {
            expenseCategoryList.clear();
            expenseCategoryList.addAll(subCategories);
            // Cập nhật adapter nếu đang ở tab Chi
            if (selectedType == 0) {
                expenseCategoryAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setupListeners() {
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
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên giao dịch", Toast.LENGTH_SHORT).show();
            return;
        }

        // LỖI 1: BẠN BỊ THIẾU CÁC DÒNG LẤY DỮ LIỆU TỪ SPINNER
        Wallet selectedWallet = (Wallet) spinnerWallet.getSelectedItem();
        SubCategory selectedSubCategory = (SubCategory) spinnerCategory.getSelectedItem();

        if (selectedWallet == null || selectedSubCategory == null) {
            Toast.makeText(this, "Vui lòng chọn ví và nhóm", Toast.LENGTH_SHORT).show();
            return;
        }
        // KẾT THÚC SỬA LỖI 1

        // --- Xử lý dữ liệu ---
        double amount = Double.parseDouble(amountStr);
        if (selectedType == 0) { // Nếu là "Khoản chi"
            amount = -amount; // Chuyển thành số âm
        }

        String note = editTextNote.getText().toString();

        // LỖI 2: SỬA LẠI KIỂU DỮ LIỆU DATE
        // File Transaction.java của bạn cần kiểu Date, không phải long
        java.util.Date date = selectedDate.getTime();
        // KẾT THÚC SỬA LỖI 2

        // --- Tạo đối tượng Transaction ---
        Transaction newTransaction = new Transaction();
        newTransaction.amount = amount;
        newTransaction.name = name;
        newTransaction.note = note;
        newTransaction.date = date; // <-- Đã sửa

        // Lấy ID từ các object đã chọn ở trên
        newTransaction.WALLETid = selectedWallet.id;
        newTransaction.SUB_CATEGORYid = selectedSubCategory.id;

        // --- Gọi ViewModel để lưu ---
        // Truyền object "selectedWallet" vào (code cũ của bạn bị thiếu)
        viewModel.insertTransaction(newTransaction, selectedWallet);

        Toast.makeText(this, "Đã lưu giao dịch!", Toast.LENGTH_SHORT).show();
        finish(); // Đóng Activity sau khi lưu
    }
}