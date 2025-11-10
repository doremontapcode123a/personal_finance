package com.nhom3.personalfinance.ui.main;
// Sửa lại package cho đúng với dự án của bạn

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

//import com.example.login_qltk.R;
import com.nhom3.personalfinance.R;
import java.util.ArrayList;
import java.util.List;

// Lớp chính của Activity
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ánh xạ View
        RecyclerView transactionListView = findViewById(R.id.transactionList);
        Button btnXemBaoCao = findViewById(R.id.btnXemBaoCao);
        Button btnXemTatCa = findViewById(R.id.btnXemTatCa);

        // Thiết lập sự kiện khi người dùng bấm vào nút
        btnXemTatCa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo một Intent để mở màn hình DanhSachGiaoDichActivity
                Intent intent = new Intent(MainActivity.this, DanhSachGiaoDichActivity.class);

                // Khởi động Activity mới
                startActivity(intent);
            }
        });

        // Tạo dữ liệu mẫu
        List<GiaoDich> danhSachGiaoDich = new ArrayList<>();
        danhSachGiaoDich.add(new GiaoDich("14/08/2025", "Ăn sáng", -35000));
        danhSachGiaoDich.add(new GiaoDich("14/08/2025", "Ăn trưa", -60000));
        danhSachGiaoDich.add(new GiaoDich("13/08/2025", "Đi Grab", -45000));
        danhSachGiaoDich.add(new GiaoDich("07/08/2025", "Lương part-time", 2800000));

        // Thiết lập Adapter cho RecyclerView
        GiaoDichAdapter adapter = new GiaoDichAdapter(this, danhSachGiaoDich);
        transactionListView.setAdapter(adapter);

        // Thiết lập sự kiện click cho nút
        btnXemBaoCao.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, BaoCaoThuChiActivity.class))
        );
    }
}

// --- Các lớp phụ giúp quản lý dữ liệu và RecyclerView ---

// 1. Lớp Model để chứa dữ liệu của một Giao Dịch
class GiaoDich {
    String ngay;
    String ten;
    long soTien;

    public GiaoDich(String ngay, String ten, long soTien) {
        this.ngay = ngay;
        this.ten = ten;
        this.soTien = soTien;
    }
}

// 2. Lớp Adapter để kết nối dữ liệu với RecyclerView
class GiaoDichAdapter extends RecyclerView.Adapter<GiaoDichAdapter.GiaoDichViewHolder> {

    private final List<GiaoDich> danhSachGiaoDich;
    private final LayoutInflater inflater;

    public GiaoDichAdapter(Context context, List<GiaoDich> danhSachGiaoDich) {
        this.inflater = LayoutInflater.from(context);
        this.danhSachGiaoDich = danhSachGiaoDich;
    }

    @NonNull
    @Override
    public GiaoDichViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_giao_dich, parent, false);
        return new GiaoDichViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GiaoDichViewHolder holder, int position) {
        GiaoDich current = danhSachGiaoDich.get(position);
        holder.bind(current);
    }

    @Override
    public int getItemCount() {
        return danhSachGiaoDich.size();
    }

    // 3. Lớp ViewHolder để giữ các tham chiếu đến View của một item
    static class GiaoDichViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTenGiaoDich;
        private final TextView tvNgayGiaoDich;
        private final TextView tvSoTien;
        private final ImageView ivIcon;

        public GiaoDichViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTenGiaoDich = itemView.findViewById(R.id.tvTenGiaoDich);
            tvNgayGiaoDich = itemView.findViewById(R.id.tvNgayGiaoDich);
            tvSoTien = itemView.findViewById(R.id.tvSoTien);
            ivIcon = itemView.findViewById(R.id.ivIcon);
        }

        public void bind(GiaoDich giaoDich) {
            tvTenGiaoDich.setText(giaoDich.ten);
            tvNgayGiaoDich.setText(giaoDich.ngay);

            // Định dạng số tiền và đặt màu
            String formattedAmount = String.format("%,d ₫", giaoDich.soTien);
            tvSoTien.setText(formattedAmount);

            if (giaoDich.soTien < 0) {
                // Khoản chi
                tvSoTien.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.holo_red_dark));
                ivIcon.setImageResource(R.drawable.ic_launcher_background); // Thay bằng icon chi tiêu
            } else {
                // Khoản thu
                tvSoTien.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.holo_green_dark));
                tvSoTien.setText("+" + formattedAmount); // Thêm dấu cộng cho số dương
                ivIcon.setImageResource(R.drawable.ic_launcher_foreground); // Thay bằng icon thu nhập
            }
        }
    }
}