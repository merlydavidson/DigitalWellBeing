package com.project.digitalwellbeing.data.model;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

public class WebHistory {
    @PrimaryKey(autoGenerate = true)
    int id;

    @ColumnInfo(name = "childId")
    @SerializedName("childId")
    int childId;

    @ColumnInfo(name = "title")
    @SerializedName("title")
    String title;

    @ColumnInfo(name = "url")
    @SerializedName("url")
    String url;

    public WebHistory(int childId, String title, String url) {
        this.childId = childId;
        this.title = title;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getChildId() {
        return childId;
    }

    public void setChildId(int childId) {
        this.childId = childId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
