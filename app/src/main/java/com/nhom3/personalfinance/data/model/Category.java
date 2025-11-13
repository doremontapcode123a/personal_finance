package com.nhom3.personalfinance.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "CATEGORY")
public class Category {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String name;

    // --- Constructor mặc định (Room yêu cầu) ---
    public Category() {
    }

    // --- Constructor đầy đủ ---
    public Category(@NonNull String name) {
        this.name = name;
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

    // --- toString() để debug ---
    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
