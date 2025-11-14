package com.nhom3.personalfinance.data.dto;

/**
 * Data Transfer Object (DTO) để chứa dữ liệu cho biểu đồ đường.
 * Nó nhóm tổng thu và tổng chi theo năm và tháng.
 */
public class MonthlyTransactionDto {
    public int year;
    public int month;
    public double totalIncome;
    public double totalExpense;
}
