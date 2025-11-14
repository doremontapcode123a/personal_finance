package com.nhom3.personalfinance.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "WALLET",
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "id",
                childColumns = "USERid",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = "USERid")} // Tăng tốc truy vấn theo USERid
)
public class Wallet {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String name;

    private double balance;

    @ColumnInfo(name = "USERid")
    private int USERid;

    // --- Constructor mặc định (Room yêu cầu) ---
    public Wallet() {
    }

    // --- Constructor đầy đủ ---
    public Wallet(@NonNull String name, double balance, int USERid) {
        this.name = name;
        this.balance = balance;
        this.USERid = USERid;
    }

    // --- Getter & Setter ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public int getUSERid() {
        return USERid;
    }

    public void setUSERid(int USERid) {
        this.USERid = USERid;
    }

    // --- toString() để debug ---
    @Override
    public String toString() {
        return "Wallet{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", balance=" + balance +
                ", USERid=" + USERid +
                '}';
    }
}

