package com.project.digitalwellbeing.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class GoogleFitDetails {

    @ColumnInfo(name = "walking")
    @SerializedName("walking")
    public float walking;

    @ColumnInfo(name = "running")
    @SerializedName("running")
    public float running;

    @ColumnInfo(name = "still")
    @SerializedName("still")
    public float still;


    @ColumnInfo(name = "totalCalorie")
    @SerializedName("totalCalorie")
    public float totalCalorie;

    @ColumnInfo(name = "totalSteps")
    @SerializedName("totalSteps")
    public int totalSteps;

    @ColumnInfo(name = "date")
    @SerializedName("date")
    public String date;

    @ColumnInfo(name = "acknowledgement")
    @SerializedName("acknowledgement")
    public String acknowledgement;

    public String getAcknowledgement() {
        return acknowledgement;
    }

    public void setAcknowledgement(String acknowledgement) {
        this.acknowledgement = acknowledgement;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    @ColumnInfo(name = "childId")
    @SerializedName("childId")
    public String childId;


    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "googleFitID")
    @SerializedName("googleFitID")
    String googleFitID;

    public float getWalking() {
        return walking;
    }

    public void setWalking(float walking) {
        this.walking = walking;
    }

    public float getRunning() {
        return running;
    }

    public void setRunning(float running) {
        this.running = running;
    }

    public float getStill() {
        return still;
    }

    public void setStill(float still) {
        this.still = still;
    }

    public float getTotalCalorie() {
        return totalCalorie;
    }

    public void setTotalCalorie(float totalCalorie) {
        this.totalCalorie = totalCalorie;
    }

    public int getTotalSteps() {
        return totalSteps;
    }

    public void setTotalSteps(int totalSteps) {
        this.totalSteps = totalSteps;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getGoogleFitID() {
        return googleFitID;
    }

    public void setGoogleFitID(String googleFitID) {
        this.googleFitID = googleFitID;
    }
}
