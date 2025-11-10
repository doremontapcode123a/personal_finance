
package com.nhom3.personalfinance.ui.main;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar; // <-- Import Toolbar

//import com.example.login_qltk.R;
import com.nhom3.personalfinance.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;

public class BaoCaoThuChiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bao_cao_thu_chi);

        // --- Cấu hình Toolbar ---
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Bật nút quay lại (hình mũi tên) trên Toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // --- Ánh xạ các biểu đồ từ layout ---
        // (Nút btnQuayLai đã bị loại bỏ vì ta dùng Toolbar)
        LineChart lineChart = findViewById(R.id.lineChartThuChi);
        PieChart pieThu = findViewById(R.id.pieChartThu);
        PieChart pieChi = findViewById(R.id.pieChartChi);

        // --- Thiết lập dữ liệu và giao diện cho các biểu đồ ---
        setupLineChart(lineChart);
        setupPieChartThu(pieThu);
        setupPieChartChi(pieChi);
    }

    // --- Thêm phương thức này để xử lý sự kiện khi nhấn nút quay lại trên Toolbar ---
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Thiết lập và hiển thị dữ liệu cho Biểu đồ đường (LineChart)
     */
    private void setupLineChart(LineChart lineChart) {
        // --- Chuẩn hóa dữ liệu ---
        ArrayList<Entry> thuEntries = new ArrayList<>();
        thuEntries.add(new Entry(1, 15000000));
        thuEntries.add(new Entry(2, 0));
        thuEntries.add(new Entry(3, 0));
        thuEntries.add(new Entry(4, 0));
        thuEntries.add(new Entry(5, 2800000));
        thuEntries.add(new Entry(6, 0));
        thuEntries.add(new Entry(7, 0));
        thuEntries.add(new Entry(8, 0));
        thuEntries.add(new Entry(9, 0));
        thuEntries.add(new Entry(10, 10000000));

        ArrayList<Entry> chiEntries = new ArrayList<>();
        chiEntries.add(new Entry(1, 0));
        chiEntries.add(new Entry(2, 0));
        chiEntries.add(new Entry(3, 60000));
        chiEntries.add(new Entry(4, 0));
        chiEntries.add(new Entry(5, 0));
        chiEntries.add(new Entry(6, 45000));
        chiEntries.add(new Entry(7, 0));
        chiEntries.add(new Entry(8, 0));
        chiEntries.add(new Entry(9, 25000));
        chiEntries.add(new Entry(10, 0));

        // --- Cấu hình cho đường "Khoản thu" ---
        LineDataSet setThu = new LineDataSet(thuEntries, "Khoản thu");
        setThu.setColor(0xFF43A047); // Màu xanh lá
        setThu.setLineWidth(2.5f);
        setThu.setCircleColor(0xFF43A047);
        setThu.setCircleRadius(5f);
        setThu.setDrawValues(false); // Ẩn giá trị trên các điểm

        // --- Cấu hình cho đường "Khoản chi" ---
        LineDataSet setChi = new LineDataSet(chiEntries, "Khoản chi");
        setChi.setColor(0xFFE53935); // Màu đỏ
        setChi.setLineWidth(2.5f);
        setChi.setCircleColor(0xFFE53935);
        setChi.setCircleRadius(5f);
        setChi.setDrawValues(false);

        // --- Cấu hình chung cho biểu đồ ---
        LineData lineData = new LineData(setThu, setChi);
        lineChart.setData(lineData);
        lineChart.getDescription().setEnabled(false); // Ẩn mô tả mặc định của thư viện
        lineChart.setDrawGridBackground(false);

        // Cấu hình trục X (trục ngang)
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // Đảm bảo các giá trị trên trục X là số nguyên
        xAxis.setDrawGridLines(false);

        // Cấu hình trục Y (trục đứng)
        lineChart.getAxisLeft().setDrawGridLines(true);
        lineChart.getAxisRight().setEnabled(false); // Ẩn trục Y bên phải

        // Cấu hình chú thích (Legend)
        Legend legend = lineChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);

        // Thêm hiệu ứng và vẽ lại biểu đồ
        lineChart.animateX(1500);
        lineChart.invalidate();
    }

    /**
     * Thiết lập và hiển thị dữ liệu cho Biểu đồ tròn Khoản thu (PieChart)
     */
    private void setupPieChartThu(PieChart pieChart) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(85, "Lương"));
        entries.add(new PieEntry(15, "Thưởng"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(new int[]{0xFF43A047, 0xFF81C784});
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);
        dataSet.setSliceSpace(2f); // Khoảng cách giữa các miếng bánh

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart)); // Hiển thị dạng %

        // --- Cấu hình chung cho biểu đồ ---
        pieChart.setData(data);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true); // Vẽ lỗ ở giữa
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setEntryLabelColor(Color.BLACK); // Màu của nhãn "Lương", "Thưởng"

        pieChart.setCenterText("Tổng Thu");
        pieChart.setCenterTextSize(14f);
        pieChart.getLegend().setEnabled(false); // Ẩn chú thích (đã có nhãn trên miếng bánh)

        // Thêm hiệu ứng và vẽ lại
        pieChart.animateY(1000);
        pieChart.invalidate();
    }

    /**
     * Thiết lập và hiển thị dữ liệu cho Biểu đồ tròn Khoản chi (PieChart)
     */
    private void setupPieChartChi(PieChart pieChart) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(60, "Ăn uống"));
        entries.add(new PieEntry(25, "Đi lại"));
        entries.add(new PieEntry(15, "Khác"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(new int[]{0xFFE53935, 0xFFFF7043, 0xFFFFB74D});
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);
        dataSet.setSliceSpace(2f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));

        // --- Cấu hình chung cho biểu đồ ---
        pieChart.setData(data);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setEntryLabelColor(Color.BLACK);

        pieChart.setCenterText("Tổng Chi");
        pieChart.setCenterTextSize(14f);
        pieChart.getLegend().setEnabled(false);

        // Thêm hiệu ứng và vẽ lại
        pieChart.animateY(1000);
        pieChart.invalidate();
    }
}
