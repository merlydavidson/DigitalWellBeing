package com.project.digitalwellbeing.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class CallDetails {

    @ColumnInfo(name = "callerId")
    @SerializedName("callerId")
    String callerId;

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

    public String getCallDate() {
        return callDate;
    }

    public void setCallDate(String callDate) {
        this.callDate = callDate;
    }

    @ColumnInfo(name = "callDate")
    @SerializedName("callDate")
    String callDate;

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

    @ColumnInfo(defaultValue = "0",name ="acknowlwdgement")
    @SerializedName("acknowlwdgement")
    public String acknowlwdgement;

    public String getAcknowlwdgement() {
        return acknowlwdgement;
    }

    public void setAcknowlwdgement(String acknowlwdgement) {
        this.acknowlwdgement = acknowlwdgement;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @ColumnInfo(name = "date")
    @SerializedName("date")
    String date;

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    @ColumnInfo(name = "childId")
    @SerializedName("childId")
    String childId;


    public String getCallerId() {
        return callerId;
    }

    public void setCallerId(String callerId) {
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
