package com.nhom3.personalfinance.data.model;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(
        tableName = "USER",
        indices = {@Index(value = {"username"}, unique = true)}
)
public class User {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String username;

    @NonNull
    public String password;

    // --- Constructor mặc định (Room yêu cầu) ---
    public User() {
    }

    // --- Constructor đầy đủ ---
    public User(@NonNull String username, @NonNull String password) {
        this.username = username;
        this.password = password;
    }

    // --- Getter & Setter ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    @NonNull
    public String getPassword() {
        return password;
    }

    public void setPassword(@NonNull String password) {
        this.password = password;
    }

    // --- toString() để debug ---
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
