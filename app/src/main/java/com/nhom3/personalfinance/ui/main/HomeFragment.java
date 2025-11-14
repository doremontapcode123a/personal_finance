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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom3.personalfinance.R;
import com.nhom3.personalfinance.data.model.Transaction;
import com.nhom3.personalfinance.ui.main.BaoCaoThuChiActivity;
import com.nhom3.personalfinance.ui.main.DanhSachGiaoDichActivity;
import com.nhom3.personalfinance.viewmodel.HomeViewModel;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * HomeFragment được viết lại để hoạt động với HomeViewModel phức tạp và layout hiện có.
 */
public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private RecyclerView transactionListView;
    private GiaoDichAdapter adapter;
    private TextView tvTotalBalance; // Thêm TextView cho tổng số dư
    private TextView tvTotalIncome;
    private TextView tvTotalExpense;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Khởi tạo ViewModel, kết nối Fragment với ViewModel
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- Ánh xạ Views ---
        tvTotalBalance = view.findViewById(R.id.tvSoDu); // ID TextView trong CardView
        tvTotalIncome = view.findViewById(R.id.btnTongThu); // Thay 'tvThuNhap' bằng ID đúng trong layout của bạn
        tvTotalExpense = view.findViewById(R.id.btnTongChi);
        transactionListView = view.findViewById(R.id.transactionList); // ID RecyclerView
        Button btnXemBaoCao = view.findViewById(R.id.btnXemBaoCao);
        Button btnXemTatCa = view.findViewById(R.id.btnXemTatCa);

        // --- Thiết lập RecyclerView và Adapter ---
        setupRecyclerView();

        // --- Thiết lập các sự kiện click ---
        setupClickListeners(btnXemBaoCao, btnXemTatCa);

        // --- Quan sát (observe) dữ liệu từ ViewModel ---
        observeViewModel();
    }

    // THÊM HÀM onResume ĐỂ CẬP NHẬT DỮ LIỆU KHI QUAY LẠI MÀN HÌNH
    @Override
    public void onResume() {
        super.onResume();
        // Yêu cầu ViewModel tải lại dữ liệu (tổng số dư và giao dịch)
        homeViewModel.refreshData();
    }

    private void setupRecyclerView() {
        adapter = new GiaoDichAdapter(getContext(), new ArrayList<>());
        transactionListView.setLayoutManager(new LinearLayoutManager(getContext()));
        transactionListView.setAdapter(adapter);
    }

    private void setupClickListeners(Button btnXemBaoCao, Button btnXemTatCa) {
        btnXemTatCa.setOnClickListener(v -> startActivity(new Intent(getActivity(), DanhSachGiaoDichActivity.class)));
        btnXemBaoCao.setOnClickListener(v -> startActivity(new Intent(getActivity(), BaoCaoThuChiActivity.class)));
    }

    private void observeViewModel() {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        // 1. Quan sát TỔNG SỐ DƯ
        homeViewModel.getTotalBalance().observe(getViewLifecycleOwner(), balance -> {
           if (balance != null) {
                // Định dạng số tiền sang dạng tiền tệ VNĐ cho đẹp
                tvTotalBalance.setText(currencyFormatter.format(balance));
            }
        });
        // 2. Quan sát TỔNG THU

        homeViewModel.getTotalIncome().observe(getViewLifecycleOwner(), income -> {
            if (income != null) {
                tvTotalIncome.setText(currencyFormatter.format(income));
            }
        });

        // 3. Quan sát TỔNG CHI
        homeViewModel.getTotalExpense().observe(getViewLifecycleOwner(), expense -> {
            if (expense != null) {
                // Giá trị expense là số âm, định dạng như bình thường
                tvTotalExpense.setText(currencyFormatter.format(expense));
            }
        });

        // 2. Quan sát DANH SÁCH GIAO DỊCH (đã được lọc bởi ViewModel)
        homeViewModel.getFilteredTransactions().observe(getViewLifecycleOwner(), transactions -> {
            if (transactions != null) {
                // Chuyển đổi từ List<Transaction> của database sang List<GiaoDich> cho Adapter
                List<GiaoDich> giaoDichListForAdapter = new ArrayList<>();
                for (Transaction t : transactions) {
                    giaoDichListForAdapter.add(new GiaoDich(t));
                }
                // Cập nhật dữ liệu mới cho Adapter để hiển thị lên RecyclerView
                adapter.updateData(giaoDichListForAdapter);
            }
        });
    }
}


// --- CÁC LỚP PHỤ (Model, Adapter, ViewHolder) ---
// Các lớp này giúp hiển thị dữ liệu lên RecyclerView

class GiaoDich {
    final Transaction transaction;

    public GiaoDich(Transaction transaction) {
        this.transaction = transaction;
    }
}

class GiaoDichAdapter extends RecyclerView.Adapter<GiaoDichAdapter.GiaoDichViewHolder> {
    private final List<GiaoDich> danhSachGiaoDich;
    private final Context context;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public GiaoDichAdapter(Context context, List<GiaoDich> danhSachGiaoDich) {
        this.context = context;
        this.danhSachGiaoDich = danhSachGiaoDich;
    }

    // Phương thức quan trọng để cập nhật dữ liệu từ Fragment
    public void updateData(List<GiaoDich> newList) {
        this.danhSachGiaoDich.clear();
        this.danhSachGiaoDich.addAll(newList);
        notifyDataSetChanged(); // Báo cho RecyclerView biết để vẽ lại danh sách
    }

    @NonNull
    @Override
    public GiaoDichViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_giao_dich, parent, false);
        return new GiaoDichViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GiaoDichViewHolder holder, int position) {
        holder.bind(danhSachGiaoDich.get(position), dateFormat);
    }

    @Override
    public int getItemCount() {
        return danhSachGiaoDich.size();
    }

    static class GiaoDichViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTenGiaoDich, tvNgayGiaoDich, tvSoTien;
        private final ImageView ivIcon;

        public GiaoDichViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTenGiaoDich = itemView.findViewById(R.id.tvTenGiaoDich);
            tvNgayGiaoDich = itemView.findViewById(R.id.tvNgayGiaoDich);
            tvSoTien = itemView.findViewById(R.id.tvSoTien);
            ivIcon = itemView.findViewById(R.id.ivIcon);
        }

        public void bind(GiaoDich giaoDich, SimpleDateFormat dateFormat) {
            Transaction t = giaoDich.transaction;
            tvTenGiaoDich.setText(t.name);
            tvNgayGiaoDich.setText(dateFormat.format(t.date));

            String formattedAmount = String.format("%,.0f ₫", t.amount);
            tvSoTien.setText(formattedAmount);

            if (t.amount < 0) {
                tvSoTien.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.holo_red_dark));
                ivIcon.setImageResource(R.drawable.ic_coin_24);
            } else {
                tvSoTien.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.holo_green_dark));
                tvSoTien.setText("+" + formattedAmount);
                ivIcon.setImageResource(R.drawable.ic_wallet_24);
            }
        }
    }
}
