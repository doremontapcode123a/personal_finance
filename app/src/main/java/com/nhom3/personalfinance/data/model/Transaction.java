package com.nhom3.personalfinance.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(
        tableName = "TRANSACTION",
        foreignKeys = {
                @ForeignKey(
                        entity = Wallet.class,
                        parentColumns = "id",
                        childColumns = "WALLETid",
                        onDelete = ForeignKey.SET_NULL
                ),
                @ForeignKey(
                        entity = SubCategory.class,
                        parentColumns = "id",
                        childColumns = "SUB_CATEGORYid",
                        onDelete = ForeignKey.SET_NULL
                )
        },
        indices = {
                @Index("WALLETid"),
                @Index("SUB_CATEGORYid")
        }
)
public class Transaction {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String name;

    public double amount;

    @Nullable
    public String note;

    @NonNull
    public Date date;

    @ColumnInfo(name = "WALLETid")
    @Nullable
    public Integer WALLETid;

    @ColumnInfo(name = "SUB_CATEGORYid")
    @Nullable
    public Integer SUB_CATEGORYid;

    // --- Constructor mặc định (Room yêu cầu) ---
    public Transaction() {
    }

    // --- Constructor đầy đủ ---
    public Transaction(@NonNull String name, double amount, @Nullable String note,
                       @NonNull Date date, @Nullable Integer WALLETid, @Nullable Integer SUB_CATEGORYid) {
        this.name = name;
        this.amount = amount;
        this.note = note;
        this.date = date;
        this.WALLETid = WALLETid;
        this.SUB_CATEGORYid = SUB_CATEGORYid;
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Nullable
    public String getNote() {
        return note;
    }

    public void setNote(@Nullable String note) {
        this.note = note;
    }

    @NonNull
    public Date getDate() {
        return date;
    }

    public void setDate(@NonNull Date date) {
        this.date = date;
    }

    @Nullable
    public Integer getWALLETid() {
        return WALLETid;
    }

    public void setWALLETid(@Nullable Integer WALLETid) {
        this.WALLETid = WALLETid;
    }

    @Nullable
    public Integer getSUB_CATEGORYid() {
        return SUB_CATEGORYid;
    }

    public void setSUB_CATEGORYid(@Nullable Integer SUB_CATEGORYid) {
        this.SUB_CATEGORYid = SUB_CATEGORYid;
    }

    // --- toString() để debug ---
    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", amount=" + amount +
                ", note='" + note + '\'' +
                ", date=" + date +
                ", WALLETid=" + WALLETid +
                ", SUB_CATEGORYid=" + SUB_CATEGORYid +
                '}';
    }
}
