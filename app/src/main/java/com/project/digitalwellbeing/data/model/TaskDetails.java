package com.project.digitalwellbeing.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

@Entity
public class TaskDetails {
    @PrimaryKey(autoGenerate = true)
    public int logId;

    @ColumnInfo(name ="taskName")
    @SerializedName("taskName")
    public String taskName;

    @ColumnInfo(name ="date")
    @SerializedName("date")
    public String date;

    @ColumnInfo(name ="time")
    @SerializedName("time")
    public String time;

    @ColumnInfo(name ="timeStamp")
    @SerializedName("timeStamp")
    public String timeStamp;

    @ColumnInfo(name ="upload")
    @SerializedName("upload")
    public int upload;


    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getUpload() {
        return upload;
    }

    public void setUpload(int upload) {
        this.upload = upload;
    }
}
