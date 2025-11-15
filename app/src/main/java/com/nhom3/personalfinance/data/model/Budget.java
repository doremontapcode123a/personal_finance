package com.nhom3.personalfinance.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "BUDGET",
        foreignKeys = @ForeignKey(
                entity = SubCategory.class,
                parentColumns = "id",
                childColumns = "SUB_CATEGORYid",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = "SUB_CATEGORYid")}
)
public class Budget {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public double budget;

    @ColumnInfo(name = "SUB_CATEGORYid")
    public int SUB_CATEGORYid;
    public int USERid;

    // --- Constructor mặc định (Room yêu cầu) ---
    public Budget() {
    }

    // --- Constructor đầy đủ ---
    public Budget(double budget, int SUB_CATEGORYid) {
        this.budget = budget;
        this.SUB_CATEGORYid = SUB_CATEGORYid;
    }

    // --- Getter & Setter ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public int getSUB_CATEGORYid() {
        return SUB_CATEGORYid;
    }

    public void setSUB_CATEGORYid(int SUB_CATEGORYid) {
        this.SUB_CATEGORYid = SUB_CATEGORYid;
    }

    // --- toString() để debug ---
    @Override
    public String toString() {
        return "Budget{" +
                "id=" + id +
                ", budget=" + budget +
                ", SUB_CATEGORYid=" + SUB_CATEGORYid +
                '}';
    }
}
