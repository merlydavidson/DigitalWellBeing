package com.project.digitalwellbeing.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class CallDetails {

    @PrimaryKey(autoGenerate = true)
    int callerId;

    public int getCallerLogId() {
        return callerLogId;
    }

    public void setCallerLogId(int callerLogId) {
        this.callerLogId = callerLogId;
    }

    @ColumnInfo(name = "callerLogId")
    @SerializedName("callerLogId")
    int callerLogId;
    @ColumnInfo(name = "callerName")
    @SerializedName("callerName")
    String callerName;
    @ColumnInfo(name = "callerNumber")
    @SerializedName("callerNumber")
    String callerNumber;
    @ColumnInfo(name = "callDuration")
    @SerializedName("callDuration")
    String callDuration;
    @ColumnInfo(name = "callType")
    @SerializedName("callType")
    String callType;
    @ColumnInfo(name = "callTimeStamp")
    @SerializedName("callTimeStamp")
    String callTimeStamp;

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    @ColumnInfo(name = "childId")
    @SerializedName("childId")
    String childId;


    public int getCallerId() {
        return callerId;
    }

    public void setCallerId(int callerId) {
        this.callerId = callerId;
    }

    public String getCallerName() {
        return callerName;
    }

    public void setCallerName(String callerName) {
        this.callerName = callerName;
    }

    public String getCallerNumber() {
        return callerNumber;
    }

    public void setCallerNumber(String callerNumber) {
        this.callerNumber = callerNumber;
    }

    public String getCallDuration() {
        return callDuration;
    }

    public void setCallDuration(String callDuration) {
        this.callDuration = callDuration;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getCallTimeStamp() {
        return callTimeStamp;
    }

    public void setCallTimeStamp(String callTimeStamp) {
        this.callTimeStamp = callTimeStamp;
    }
}
