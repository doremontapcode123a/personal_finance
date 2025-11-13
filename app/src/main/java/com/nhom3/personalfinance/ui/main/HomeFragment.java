package com.nhom3.personalfinance.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment; // QUAN TRỌNG: Import đúng Fragment của AndroidX
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom3.personalfinance.R;

import java.util.ArrayList;
import java.util.List;

/**
 * HomeFragment hiển thị màn hình chính, bao gồm danh sách giao dịch gần đây
 * và các nút chức năng.
 *
 * SỬA LỖI: Lớp này phải kế thừa từ androidx.fragment.app.Fragment, không phải AppCompatActivity.
 */
public class HomeFragment extends Fragment {

    /**
     * Phương thức này được gọi để tạo và trả về View cho Fragment.
     * Đây là nơi bạn "thổi phồng" (inflate) file layout XML của mình.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout XML cho fragment này và trả về View
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    /**
     * Phương thức này được gọi ngay sau khi onCreateView() hoàn tất.
     * Toàn bộ logic xử lý View (ánh xạ, setOnClickListener,...) phải được đặt ở đây.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // SỬA LỖI: Trong Fragment, phải dùng đối tượng 'view' được cung cấp để tìm các View con.
        RecyclerView transactionListView = view.findViewById(R.id.transactionList);
        Button btnXemBaoCao = view.findViewById(R.id.btnXemBaoCao);
        Button btnXemTatCa = view.findViewById(R.id.btnXemTatCa);

        // Thiết lập LayoutManager cho RecyclerView
        // SỬA LỖI: Sử dụng getContext() để lấy Context trong Fragment
        transactionListView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Thiết lập sự kiện khi người dùng bấm vào nút "Xem tất cả"
        btnXemTatCa.setOnClickListener(v -> {
            // SỬA LỖI: Sử dụng getActivity() để lấy Context cho Intent
            Intent intent = new Intent(getActivity(), DanhSachGiaoDichActivity.class);
            startActivity(intent);
        });

        // Thiết lập sự kiện click cho nút "Xem báo cáo"
        btnXemBaoCao.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), BaoCaoThuChiActivity.class))
        );

        // Tạo dữ liệu mẫu
        List<GiaoDich> danhSachGiaoDich = new ArrayList<>();
        danhSachGiaoDich.add(new GiaoDich("14/08/2025", "Ăn sáng", -35000));
        danhSachGiaoDich.add(new GiaoDich("14/08/2025", "Ăn trưa", -60000));
        danhSachGiaoDich.add(new GiaoDich("13/08/2025", "Đi Grab", -45000));
        danhSachGiaoDich.add(new GiaoDich("07/08/2025", "Lương part-time", 2800000));

        // Thiết lập Adapter cho RecyclerView
        GiaoDichAdapter adapter = new GiaoDichAdapter(getContext(), danhSachGiaoDich);
        transactionListView.setAdapter(adapter);
    }
}

// --- CÁC LỚP PHỤ (Model, Adapter, ViewHolder) ---
// Không có thay đổi lớn ở các lớp này, nhưng để chúng ở file riêng sẽ tốt hơn cho việc quản lý.

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

class GiaoDichAdapter extends RecyclerView.Adapter<GiaoDichAdapter.GiaoDichViewHolder> {

    private final List<GiaoDich> danhSachGiaoDich;
    private final Context context; // Giữ context để dùng

    public GiaoDichAdapter(Context context, List<GiaoDich> danhSachGiaoDich) {
        this.context = context;
        this.danhSachGiaoDich = danhSachGiaoDich;
    }

    @NonNull
    @Override
    public GiaoDichViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_giao_dich, parent, false);
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

            String formattedAmount = String.format("%,d ₫", giaoDich.soTien);
            tvSoTien.setText(formattedAmount);

            if (giaoDich.soTien < 0) {
                tvSoTien.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.holo_red_dark));
                ivIcon.setImageResource(R.drawable.ic_launcher_background);
            } else {
                tvSoTien.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.holo_green_dark));
                tvSoTien.setText("+" + formattedAmount);
                ivIcon.setImageResource(R.drawable.ic_launcher_foreground);
            }
        }
    }
}
