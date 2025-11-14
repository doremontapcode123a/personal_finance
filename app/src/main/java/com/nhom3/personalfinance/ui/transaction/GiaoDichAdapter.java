
package com.nhom3.personalfinance.ui.transaction;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.nhom3.personalfinance.R;
import com.nhom3.personalfinance.data.model.GiaoDich; // Import model vừa tạo
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class GiaoDichAdapter extends RecyclerView.Adapter<GiaoDichAdapter.GiaoDichViewHolder> {

    private Context context;
    private ArrayList<GiaoDich> danhSachGiaoDich;

    // 1. Hàm khởi tạo để nhận dữ liệu
    public GiaoDichAdapter(Context context, ArrayList<GiaoDich> danhSachGiaoDich) {
        this.context = context;
        this.danhSachGiaoDich = danhSachGiaoDich;
    }

    // 2. Tạo ViewHolder từ layout item_giao_dich.xml
    @NonNull
    @Override
    public GiaoDichViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_giao_dich, parent, false);
        return new GiaoDichViewHolder(view);
    }

    // 3. Gán dữ liệu từ đối tượng GiaoDich vào các View của ViewHolder
    @Override
    public void onBindViewHolder(@NonNull GiaoDichViewHolder holder, int position) {
        GiaoDich giaoDichHienTai = danhSachGiaoDich.get(position);

        holder.tvTenGiaoDich.setText(giaoDichHienTai.getTen());
        holder.tvNgayGiaoDich.setText(giaoDichHienTai.getNgay());

        // Định dạng tiền tệ
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(localeVN);
        String soTienFormatted = currencyFormatter.format(giaoDichHienTai.getSoTien());
        holder.tvSoTien.setText(soTienFormatted);

        // Đổi màu số tiền (nếu âm thì màu đỏ, dương thì màu xanh)
        if (giaoDichHienTai.getSoTien() < 0) {
            holder.tvSoTien.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
            // holder.ivIcon.setImageResource(R.drawable.ic_expense); // Ví dụ đổi icon
        } else {
            holder.tvSoTien.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
            // holder.ivIcon.setImageResource(R.drawable.ic_income); // Ví dụ đổi icon
        }

        // Xử lý sự kiện bấm vào một item (nếu cần)
        holder.itemView.setOnClickListener(v -> {
            // Ví dụ: Mở chi tiết giao dịch
            // Toast.makeText(context, "Bạn đã bấm vào: " + giaoDichHienTai.getTen(), Toast.LENGTH_SHORT).show();
        });
    }

    // Trả về tổng số item trong danh sách
    @Override
    public int getItemCount() {
        return danhSachGiaoDich.size();
    }

    // Lớp ViewHolder: nắm giữ các View của file item_giao_dich.xml
    public static class GiaoDichViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTenGiaoDich;
        TextView tvNgayGiaoDich;
        TextView tvSoTien;

        public GiaoDichViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            tvTenGiaoDich = itemView.findViewById(R.id.tvTenGiaoDich);
            tvNgayGiaoDich = itemView.findViewById(R.id.tvNgayGiaoDich);
            tvSoTien = itemView.findViewById(R.id.tvSoTien);
        }
    }
}
