package com.project.digitalwellbeing.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class UserDetails {

    @PrimaryKey(autoGenerate = true)
    public int childID;
    @ColumnInfo(name ="child_name")
    @SerializedName("username")
    public String childName;
    @ColumnInfo(name ="uuid")
    @SerializedName("uuid")
    public String childDeviceUUID;

    @ColumnInfo(name ="child_phonrnumber")
    @SerializedName("phoneNumber")
    public String childPhoneNumber;

    @ColumnInfo(name ="email")
    @SerializedName("email")
    public String childEmail;

    @ColumnInfo(name ="child_password")
    @SerializedName("password")
    public String childPassword;


    public int getChildID() {
        return childID;
    }

    public void setChildID(int childID) {
        this.childID = childID;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public String getChildDeviceUUID() {
        return childDeviceUUID;
    }

    public void setChildDeviceUUID(String childDeviceUUID) {
        this.childDeviceUUID = childDeviceUUID;
    }

    public String getChildPhoneNumber() {
        return childPhoneNumber;
    }

    public void setChildPhoneNumber(String childPhoneNumber) {
        this.childPhoneNumber = childPhoneNumber;
    }
}
