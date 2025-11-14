package com.nhom3.personalfinance.ui.transaction;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.nhom3.personalfinance.R;
import com.nhom3.personalfinance.data.model.Transaction;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactions = new ArrayList<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    // --- INTERFACE CHO CLICK LISTENER ---
    public interface OnItemClickListener {
        void onItemClick(Transaction transaction);
        void onItemLongClick(Transaction transaction);
    }
    private final OnItemClickListener listener;

    // --- Sửa Hàm Constructor ---
    public TransactionAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction currentTransaction = transactions.get(position);

        holder.name.setText(currentTransaction.name);
        holder.date.setText(dateFormat.format(currentTransaction.date));
        String formattedAmount = currencyFormat.format(currentTransaction.amount);
        holder.amount.setText(formattedAmount);

        if (currentTransaction.amount < 0) {
            holder.amount.setTextColor(Color.RED);
        } else {
            holder.amount.setTextColor(Color.rgb(0, 150, 0));
        }

        // --- Thêm sự kiện Click và LongClick ---
        holder.itemView.setOnClickListener(v -> listener.onItemClick(currentTransaction));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onItemLongClick(currentTransaction);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    class TransactionViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView date;
        private TextView amount;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_view_transaction_name);
            date = itemView.findViewById(R.id.text_view_transaction_date);
            amount = itemView.findViewById(R.id.text_view_transaction_amount);
        }
    }
}