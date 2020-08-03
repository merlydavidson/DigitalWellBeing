package com.project.digitalwellbeing.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
@Entity
public class BlockedApps {
    @PrimaryKey(autoGenerate = true)
    int id;
//new comment
    @ColumnInfo(name = "packagename")
    @SerializedName("packagename")
    String packagename;

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    @ColumnInfo(name = "appname")
    @SerializedName("appname")
    String appname;

    @ColumnInfo(name = "date")
    @SerializedName("date")
    String date;

    @ColumnInfo(name = "lastTimeUsed")
    @SerializedName("lastTimeUsed")
    long lastTimeUsed;

    @ColumnInfo(name = "totalTimeInForeground")
    @SerializedName("totalTimeInForeground")
    long totalTimeInForeground;

    @ColumnInfo(defaultValue = "0",name ="acknowlwdgement")
    @SerializedName("acknowlwdgement")
    public String acknowlwdgement;

    public String getAcknowlwdgement() {
        return acknowlwdgement;
    }

    public void setAcknowlwdgement(String acknowlwdgement) {
        this.acknowlwdgement = acknowlwdgement;
    }

    boolean isChecked;

    public BlockedApps() {
    }

    public boolean getChecked() {
        return isChecked;
    }

    public long getLastTimeUsed() {
        return lastTimeUsed;
    }

    public void setLastTimeUsed(long lastTimeUsed) {
        this.lastTimeUsed = lastTimeUsed;
    }

    public long getTotalTimeInForeground() {
        return totalTimeInForeground;
    }

    public void setTotalTimeInForeground(long totalTimeInForeground) {
        this.totalTimeInForeground = totalTimeInForeground;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
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
