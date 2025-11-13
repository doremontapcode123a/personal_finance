package com.nhom3.personalfinance.ui.main;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.nhom3.personalfinance.R;
import com.nhom3.personalfinance.ui.transaction.TransactionAdapter;
import com.nhom3.personalfinance.viewmodel.TransactionViewModel;

public class TransactionListFragment extends Fragment {

    private TransactionViewModel viewModel;
    private RecyclerView recyclerView;
    private TransactionAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout
        View view = inflater.inflate(R.layout.fragment_transaction_list, container, false);

        // Khởi tạo RecyclerView và Adapter
        recyclerView = view.findViewById(R.id.recycler_view_transactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        adapter = new TransactionAdapter();
        recyclerView.setAdapter(adapter);

        // Khởi tạo ViewModel (Dùng chung ViewModel với AddEdit)
        // Dùng requireActivity() để 2 màn hình chia sẻ 1 ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);

        // Lắng nghe dữ liệu
        viewModel.getAllTransactions().observe(getViewLifecycleOwner(), transactions -> {
            // Khi dữ liệu thay đổi (ví dụ: sau khi thêm mới),
            // cập nhật Adapter
            adapter.setTransactions(transactions);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Tải lại dữ liệu mỗi khi quay lại tab này
        viewModel.loadAllTransactions();
    }
}