package com.nhom3.personalfinance.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "SUB_CATEGORY",
        foreignKeys = @ForeignKey(
                entity = Category.class,
                parentColumns = "id",
                childColumns = "CATEGORYid",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = "CATEGORYid")} // giúp truy vấn nhanh hơn
)
public class SubCategory {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String name;

    @ColumnInfo(name = "CATEGORYid")
    public int CATEGORYid;

    // --- Constructor mặc định (Room yêu cầu) ---
    public SubCategory() {
    }

    // --- Constructor đầy đủ ---
    public SubCategory(@NonNull String name, int CATEGORYid) {
        this.name = name;
        this.CATEGORYid = CATEGORYid;
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

    public int getCATEGORYid() {
        return CATEGORYid;
    }

    public void setCATEGORYid(int CATEGORYid) {
        this.CATEGORYid = CATEGORYid;
    }

    // ... bên trong class SubCategory ...
    @Override
    public String toString() {
        return name; // Trả về tên của danh mục con
    }
}
