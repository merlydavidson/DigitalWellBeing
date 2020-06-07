package com.project.digitalwellbeing.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

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

    @ColumnInfo(name ="starttime")
    @SerializedName("starttime")
    public String starttime;

    @ColumnInfo(name ="starttimeStamp")
    @SerializedName("starttimeStamp")
    public String starttimeStamp;

    @ColumnInfo(name ="endtime")
    @SerializedName("endtime")
    public String endtime;

    @ColumnInfo(name ="endtimeStamp")
    @SerializedName("endtimeStamp")
    public String endtimeStamp;

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

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getStarttimeStamp() {
        return starttimeStamp;
    }

    public void setStarttimeStamp(String starttimeStamp) {
        this.starttimeStamp = starttimeStamp;
    }

    public int getUpload() {
        return upload;
    }

    public void setUpload(int upload) {
        this.upload = upload;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getEndtimeStamp() {
        return endtimeStamp;
    }

    public void setEndtimeStamp(String endtimeStamp) {
        this.endtimeStamp = endtimeStamp;
    }
}
