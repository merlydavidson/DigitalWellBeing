package com.project.digitalwellbeing.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class UserInfo {
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @PrimaryKey(autoGenerate = true)
    public int userID;

    @SerializedName("username")
    @ColumnInfo(name = "username")
    String username;

    @SerializedName("role")
    @ColumnInfo(name = "role")
    int role;

    @SerializedName("phoneNumber")
    @ColumnInfo(name = "phoneNumber")
    String phoneNumber;

    @SerializedName("email")
    @ColumnInfo(name = "email")
    String email;

    @SerializedName("password")
    @ColumnInfo(name = "password")
    String password;

    @SerializedName("uuid")
    String uuid;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
