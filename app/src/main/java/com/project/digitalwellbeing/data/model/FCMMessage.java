package com.project.digitalwellbeing.data.model;

import com.google.gson.annotations.SerializedName;

public class FCMMessage {

    @SerializedName("to")
   public String to;
    @SerializedName("notification")
    public  FCMMessageNotification fcmMessageNotification;
    @SerializedName("data")
    public FCMMessageData fcmMessageData;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public FCMMessageNotification getFcmMessageNotification() {
        return fcmMessageNotification;
    }

    public void setFcmMessageNotification(FCMMessageNotification fcmMessageNotification) {
        this.fcmMessageNotification = fcmMessageNotification;
    }

    public FCMMessageData getFcmMessageData() {
        return fcmMessageData;
    }

    public void setFcmMessageData(FCMMessageData fcmMessageData) {
        this.fcmMessageData = fcmMessageData;
    }
}
