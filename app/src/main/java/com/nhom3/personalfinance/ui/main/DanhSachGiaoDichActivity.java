// File: DanhSachGiaoDichActivity.java
package com.nhom3.personalfinance.ui.main; // Thêm package cho đúng chuẩn

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom3.personalfinance.R;
// Giả sử đường dẫn đúng của bạn là như thế này
import com.nhom3.personalfinance.ui.transaction.GiaoDichAdapter;
import com.nhom3.personalfinance.data.model.GiaoDich;

import java.util.ArrayList;

// Lớp bắt đầu ở đây
public class DanhSachGiaoDichActivity extends AppCompatActivity {

    // Toàn bộ code phải nằm trong lớp này
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danh_sach_giao_dich);

        // --- BẮT ĐẦU CODE XỬ LÝ TOOLBAR (BẠN CẦN THÊM VÀO) ---
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> {
            finish(); // Đóng activity khi nhấn nút quay lại
        });
        // --- KẾT THÚC CODE XỬ LÝ TOOLBAR ---


        // --- BẮT ĐẦU CODE XỬ LÝ RECYCLERVIEW ---
        RecyclerView recyclerView = findViewById(R.id.recycler_view_all_transactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // TẠO DỮ LIỆU MẪU
        ArrayList<GiaoDich> danhSach = new ArrayList<>();
        danhSach.add(new GiaoDich("Ăn trưa", "10/11/2025", -50000));
        danhSach.add(new GiaoDich("Tiền lương tháng 11", "05/11/2025", 10000000));
        danhSach.add(new GiaoDich("Đổ xăng", "09/11/2025", -70000));
        danhSach.add(new GiaoDich("Mua sắm online", "08/11/2025", -550000));

        // KHỞI TẠO ADAPTER VÀ GÁN VÀO RECYCLERVIEW
        // Hãy chắc chắn đường dẫn `import` của GiaoDichAdapter là đúng
        GiaoDichAdapter adapter = new GiaoDichAdapter(this, danhSach);
        recyclerView.setAdapter(adapter);
        // --- KẾT THÚC CODE XỬ LÝ RECYCLERVIEW ---
    }
} // Lớp kết thúc ở đây
