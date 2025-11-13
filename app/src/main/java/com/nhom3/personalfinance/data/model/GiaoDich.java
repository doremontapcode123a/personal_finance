
package com.nhom3.personalfinance.data.model;
public class GiaoDich {
    public String ten;
    public String ngay;
    public double soTien;

    public GiaoDich(String ten, String ngay, double soTien) {
        this.ten = ten;
        this.ngay = ngay;
        this.soTien = soTien;
    }

    // Getters
    public String getTen() { return ten; }
    public String getNgay() { return ngay; }
    public double getSoTien() { return soTien; }
}
