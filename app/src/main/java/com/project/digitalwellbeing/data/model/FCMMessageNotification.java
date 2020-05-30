package com.project.digitalwellbeing.data.model;

import com.google.gson.annotations.SerializedName;

public class FCMMessageNotification {
    @SerializedName("body")
    String body;
    @SerializedName("content_available")
    String content_available;
    @SerializedName("priority")
    String priority;
    @SerializedName("title")
    String title;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getContent_available() {
        return content_available;
    }

    public void setContent_available(String content_available) {
        this.content_available = content_available;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
