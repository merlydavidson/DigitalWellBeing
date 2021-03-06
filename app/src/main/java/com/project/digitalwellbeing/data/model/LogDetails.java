package com.project.digitalwellbeing.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class LogDetails {
    @PrimaryKey(autoGenerate = true)
    public int logId;

    @ColumnInfo(name ="location")
    @SerializedName("location")
    public String location;

    public LogDetails() {
    }

    public LogDetails(String location, String date, String timeStamp, String app_details, String childId, boolean isOnline) {
        this.location = location;
        this.date = date;
        this.timeStamp = timeStamp;
        this.app_details = app_details;
        this.childId = childId;
        this.isOnline = isOnline;
    }

    public String getApp_details() {
        return app_details;
    }

    public void setApp_details(String app_details) {
        this.app_details = app_details;
    }

    @ColumnInfo(name ="date")
    @SerializedName("date")
    public String date;

    @ColumnInfo(name ="timestamp")
    @SerializedName("timestamp")
    public String timeStamp;

    @ColumnInfo(name ="uuid")
    @SerializedName("uuid")
    public String uuid;

    @ColumnInfo(name ="app_details")
    @SerializedName("app_details")
    public String app_details;

    @ColumnInfo(name = "childId")
    @SerializedName("childId")
    String childId;

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    @ColumnInfo(name ="online")
    @SerializedName("online")
    public boolean isOnline;

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
