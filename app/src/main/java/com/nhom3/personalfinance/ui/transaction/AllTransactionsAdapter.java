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
import com.nhom3.personalfinance.data.model.Transaction;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Adapter để hiển thị danh sách các giao dịch trong một RecyclerView.
 * Lớp này được thiết kế để làm việc trực tiếp với đối tượng Transaction từ database.
 */
public class AllTransactionsAdapter extends RecyclerView.Adapter<AllTransactionsAdapter.TransactionViewHolder> {

    private final List<Transaction> transactionList;
    private final Context context;
    // Định dạng ngày tháng để hiển thị cả giờ và phút cho rõ ràng
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault());

    public AllTransactionsAdapter(Context context, List<Transaction> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    /**
     * Phương thức quan trọng để cập nhật dữ liệu mới cho RecyclerView từ Activity/Fragment.
     * @param newList Danh sách giao dịch mới được lấy từ ViewModel.
     */
    public void updateData(List<Transaction> newList) {
        this.transactionList.clear();
        this.transactionList.addAll(newList);
        notifyDataSetChanged(); // Báo cho RecyclerView biết để vẽ lại toàn bộ danh sách
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo view cho mỗi item từ file layout item_giao_dich.xml
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_giao_dich, parent, false);
        return new TransactionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        // Lấy dữ liệu tại vị trí 'position' và gán cho ViewHolder để hiển thị
        Transaction transaction = transactionList.get(position);
        holder.bind(transaction, dateFormat);
    }

    @Override
    public int getItemCount() {
        // Trả về tổng số item trong danh sách
        return transactionList.size();
    }

    /**
     * ViewHolder chứa các view con của một item và logic để gán dữ liệu vào chúng.
     */
    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTenGiaoDich, tvNgayGiaoDich, tvSoTien;
        private final ImageView ivIcon;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ các view từ layout item_giao_dich.xml
            tvTenGiaoDich = itemView.findViewById(R.id.tvTenGiaoDich);
            tvNgayGiaoDich = itemView.findViewById(R.id.tvNgayGiaoDich);
            tvSoTien = itemView.findViewById(R.id.tvSoTien);
            ivIcon = itemView.findViewById(R.id.ivIcon);
        }

        /**
         * Gán dữ liệu từ một đối tượng Transaction vào các view.
         * @param transaction Đối tượng giao dịch chứa dữ liệu.
         * @param dateFormat Đối tượng để định dạng ngày tháng.
         */
        public void bind(Transaction transaction, SimpleDateFormat dateFormat) {
            tvTenGiaoDich.setText(transaction.name);
            tvNgayGiaoDich.setText(dateFormat.format(transaction.date));

            // Định dạng số tiền có dấu phân cách hàng nghìn và đơn vị tiền tệ
            String formattedAmount = String.format(Locale.GERMAN, "%,.0f ₫", transaction.amount);
            tvSoTien.setText(formattedAmount);

            // Thay đổi màu sắc và icon dựa trên loại giao dịch (thu/chi)
            if (transaction.amount < 0) {
                // Khoản chi
                tvSoTien.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.holo_red_dark));
                ivIcon.setImageResource(R.drawable.ic_coin_24);
            } else {
                // Khoản thu
                tvSoTien.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.holo_green_dark));
                tvSoTien.setText("+" + formattedAmount); // Thêm dấu '+' cho đẹp
                ivIcon.setImageResource(R.drawable.ic_wallet_24);
            }
        }
    }
}
