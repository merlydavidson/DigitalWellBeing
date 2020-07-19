package com.project.digitalwellbeing.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
@Entity
public class LockUnlock {
    @PrimaryKey(autoGenerate = true)
    int id;

    @ColumnInfo(name = "childId")
    @SerializedName("childId")
    String childId;

    @ColumnInfo(name = "password")
    @SerializedName("password")
    String password;

    public LockUnlock(String childId, String password, boolean isLocked) {
        this.childId = childId;
        this.password = password;
        this.isLocked = isLocked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    @ColumnInfo(name = "isLocked")
    @SerializedName("isLocked")
    boolean isLocked;
}
