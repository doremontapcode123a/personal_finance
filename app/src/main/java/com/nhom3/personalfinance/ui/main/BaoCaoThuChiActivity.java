package com.nhom3.personalfinance.ui.main; // SỬA: Đảm bảo package là ui.activity

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider; // Thêm import

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.nhom3.personalfinance.R;
import com.nhom3.personalfinance.data.dto.CategoryPieChartDto;
import com.nhom3.personalfinance.data.dto.MonthlyTransactionDto;
import com.nhom3.personalfinance.viewmodel.ReportViewModel; // Thêm import

import java.util.ArrayList;
import java.util.List;

public class BaoCaoThuChiActivity extends AppCompatActivity {

    private ReportViewModel reportViewModel;
    private LineChart lineChart;
    private PieChart pieThu, pieChi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bao_cao_thu_chi);

        // Khởi tạo ViewModel
        reportViewModel = new ViewModelProvider(this).get(ReportViewModel.class);

        // --- Cấu hình Toolbar ---
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // --- Ánh xạ các biểu đồ từ layout ---
        lineChart = findViewById(R.id.lineChartThuChi);
        pieThu = findViewById(R.id.pieChartThu);
        pieChi = findViewById(R.id.pieChartChi);

        // --- Bắt đầu quan sát dữ liệu từ ViewModel ---
        observeViewModel();
    }

    private void observeViewModel() {
        // Quan sát dữ liệu cho biểu đồ đường
        reportViewModel.getMonthlyTransactions().observe(this, this::setupLineChart);

        // Quan sát dữ liệu cho biểu đồ tròn Thu
        reportViewModel.getIncomeByCategory().observe(this, categoryData ->
                setupPieChart(pieThu, categoryData, "Tổng Thu", ColorTemplate.MATERIAL_COLORS));

        // Quan sát dữ liệu cho biểu đồ tròn Chi
        reportViewModel.getExpenseByCategory().observe(this, categoryData ->
                setupPieChart(pieChi, categoryData, "Tổng Chi", ColorTemplate.JOYFUL_COLORS));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // Xử lý khi nhấn nút back trên toolbar
        return true;
    }

    private void setupLineChart(List<MonthlyTransactionDto> data) {
        if (data == null || data.isEmpty()) {
            lineChart.clear(); // Xóa dữ liệu cũ nếu không có dữ liệu mới
            lineChart.setNoDataText("Không có dữ liệu cho biểu đồ.");
            lineChart.invalidate();
            return;
        }

        ArrayList<Entry> thuEntries = new ArrayList<>();
        ArrayList<Entry> chiEntries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            MonthlyTransactionDto dto = data.get(i);
            thuEntries.add(new Entry(i, (float) dto.totalIncome));
            // Lấy giá trị tuyệt đối của tổng chi để vẽ biểu đồ
            chiEntries.add(new Entry(i, (float) Math.abs(dto.totalExpense)));
            // Tạo nhãn dạng "Th01/2024"
            labels.add(String.format("Th%02d/%d", dto.month, dto.year));
        }

        LineDataSet setThu = new LineDataSet(thuEntries, "Khoản thu");
        setThu.setColor(Color.parseColor("#43A047")); // Xanh lá
        setThu.setLineWidth(2.5f);
        setThu.setCircleColor(Color.parseColor("#43A047"));
        setThu.setDrawValues(false);

        LineDataSet setChi = new LineDataSet(chiEntries, "Khoản chi");
        setChi.setColor(Color.parseColor("#E53935")); // Đỏ
        setChi.setLineWidth(2.5f);
        setChi.setCircleColor(Color.parseColor("#E53935"));
        setChi.setDrawValues(false);

        LineData lineData = new LineData(setThu, setChi);
        lineChart.setData(lineData);

        // Cấu hình trục X
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(-45); // Xoay nhãn để dễ đọc nếu có nhiều tháng

        lineChart.getDescription().setEnabled(false);
        lineChart.animateX(1500);
        lineChart.invalidate(); // Vẽ lại biểu đồ
    }

    private void setupPieChart(PieChart pieChart, List<CategoryPieChartDto> data, String centerText, int[] colors) {
        if (data == null || data.isEmpty()) {
            pieChart.clear(); // Xóa dữ liệu cũ
            pieChart.setNoDataText("Không có dữ liệu.");
            pieChart.invalidate();
            return;
        }

        ArrayList<PieEntry> entries = new ArrayList<>();
        for (CategoryPieChartDto dto : data) {
            // Dùng giá trị tuyệt đối để miếng bánh luôn dương
            entries.add(new PieEntry((float) Math.abs(dto.totalAmount), dto.categoryName));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);
        dataSet.setSliceSpace(2f);

        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new PercentFormatter(pieChart));

        pieChart.setData(pieData);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setEntryLabelColor(Color.BLACK); // Màu chữ của tên danh mục trên miếng bánh
        pieChart.setCenterText(centerText);
        pieChart.setCenterTextSize(16f);
        pieChart.getLegend().setEnabled(false); // Ẩn chú thích (legend) để gọn gàng hơn

        pieChart.animateY(1400);
        pieChart.invalidate(); // Vẽ lại biểu đồ
    }
}
