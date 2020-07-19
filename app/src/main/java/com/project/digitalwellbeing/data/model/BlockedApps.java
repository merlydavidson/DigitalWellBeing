package com.project.digitalwellbeing.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
@Entity
public class BlockedApps {
    @PrimaryKey(autoGenerate = true)
    int id;

    @ColumnInfo(name = "packagename")
    @SerializedName("packagename")
    String packagename;

    @ColumnInfo(name = "date")
    @SerializedName("date")
    String date;


    boolean isChecked;

    public boolean getChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public BlockedApps(String packagename, String date, String childId) {
        this.packagename = packagename;
        this.date = date;
        this.childId = childId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPackagename() {
        return packagename;
    }

    public void setPackagename(String packagename) {
        this.packagename = packagename;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    @ColumnInfo(name = "childId")
    @SerializedName("childId")
    String childId;
}
