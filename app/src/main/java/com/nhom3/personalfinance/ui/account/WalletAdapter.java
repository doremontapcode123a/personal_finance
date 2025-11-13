package com.nhom3.personalfinance.ui.account; // (Hoặc package bạn muốn)

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.nhom3.personalfinance.R;
import com.nhom3.personalfinance.data.model.Wallet;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WalletAdapter extends RecyclerView.Adapter<WalletAdapter.WalletViewHolder> {

    private List<Wallet> wallets = new ArrayList<>();
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    // --- Interface cho Click Listener ---
    public interface OnWalletClickListener {
        void onWalletClick(Wallet wallet); // Click để Sửa
        void onWalletLongClick(Wallet wallet); // Long click để Xóa
    }
    private final OnWalletClickListener listener;

    public WalletAdapter(OnWalletClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public WalletViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wallet, parent, false);
        return new WalletViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WalletViewHolder holder, int position) {
        Wallet currentWallet = wallets.get(position);
        holder.name.setText(currentWallet.name);
        holder.balance.setText(currencyFormat.format(currentWallet.balance));

        // --- Gán sự kiện ---
        holder.itemView.setOnClickListener(v -> listener.onWalletClick(currentWallet));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onWalletLongClick(currentWallet);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return wallets.size();
    }

    public void setWallets(List<Wallet> wallets) {
        this.wallets = wallets;
        notifyDataSetChanged();
    }

    class WalletViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView balance;

        public WalletViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_view_wallet_name);
            balance = itemView.findViewById(R.id.text_view_wallet_balance);
        }
    }
}