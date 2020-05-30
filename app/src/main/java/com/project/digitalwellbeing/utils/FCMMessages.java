package com.project.digitalwellbeing.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.project.digitalwellbeing.SignupActivity;
import com.project.digitalwellbeing.data.model.AppDataBase;
import com.project.digitalwellbeing.data.model.DigitalWellBeingDao;
import com.project.digitalwellbeing.data.model.FCMMessage;
import com.project.digitalwellbeing.data.model.FCMMessageData;
import com.project.digitalwellbeing.data.model.FCMMessageNotification;
import com.project.digitalwellbeing.data.model.LogDetails;
import com.project.digitalwellbeing.data.model.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static java.lang.String.*;

public class FCMMessages {
    Context context;

    public FCMMessages(Context context) {
        this.context = context;
    }

    public FCMMessage registerMessage() {
        FCMMessage fcmMessage = new FCMMessage();
        FCMMessageData fcmMessageData = new FCMMessageData();
        FCMMessageNotification fcmMessageNotification = new FCMMessageNotification();
        fcmMessage.setTo(CommonDataArea.FIREBASETOPIC);
        fcmMessageData.setBody("test");
        fcmMessageData.setContent_available("yes");
        fcmMessageData.setPriority("yes");
        fcmMessageData.setTitle("test");
        fcmMessageNotification.setBody(CommonFunctionArea.getDeviceUUID(context));
        fcmMessageNotification.setContent_available("yes");
        fcmMessageNotification.setPriority("yes");
        fcmMessageNotification.setTitle("1");
        fcmMessage.setFcmMessageData(fcmMessageData);
        fcmMessage.setFcmMessageNotification(fcmMessageNotification);
        return fcmMessage;
    }

    public FCMMessage sendBackChildDetails() {
        FCMMessage fcmMessage = new FCMMessage();
        FCMMessageData fcmMessageData = new FCMMessageData();
        FCMMessageNotification fcmMessageNotification = new FCMMessageNotification();

        AppDataBase appDataBase = AppDataBase.getInstance(context);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        List<UserInfo> userInfos = digitalWellBeingDao.getUserInfo();

        for (UserInfo userInfo : userInfos) {
            JSONObject jsonObj = null;
            userInfo.setUuid(CommonFunctionArea.getDeviceUUID(context));
            Gson gson = new GsonBuilder().create();
            String json = gson.toJson(userInfo);
            fcmMessage.setTo(CommonDataArea.FIREBASETOPIC);
            fcmMessageData.setBody("test");
            fcmMessageData.setContent_available("yes");
            fcmMessageData.setPriority("yes");
            fcmMessageData.setTitle("test");
            fcmMessageNotification.setBody(json);
            fcmMessageNotification.setContent_available("yes");
            fcmMessageNotification.setPriority("yes");
            fcmMessageNotification.setTitle("2");
            fcmMessage.setFcmMessageData(fcmMessageData);
            fcmMessage.setFcmMessageNotification(fcmMessageNotification);

        }


        return fcmMessage;
    }

    public static FCMMessage sendLogs(String location,String timeStamp,boolean isOnline) {
        FCMMessage fcmMessage = new FCMMessage();
        FCMMessageData fcmMessageData = new FCMMessageData();
        FCMMessageNotification fcmMessageNotification = new FCMMessageNotification();

        LogDetails logDetails=new LogDetails();
        logDetails.setLocation(location);
        logDetails.setTimeStamp(timeStamp);
        logDetails.setOnline(isOnline);
            JSONObject jsonObj = null;
            Gson gson = new GsonBuilder().create();
            String json = gson.toJson(logDetails);
            fcmMessage.setTo(CommonDataArea.FIREBASETOPIC);
            fcmMessageData.setBody("test");
            fcmMessageData.setContent_available("yes");
            fcmMessageData.setPriority("yes");
            fcmMessageData.setTitle("log");
            fcmMessageNotification.setBody(json);
            fcmMessageNotification.setContent_available("yes");
            fcmMessageNotification.setPriority("yes");
            fcmMessageNotification.setTitle("3");
            fcmMessage.setFcmMessageData(fcmMessageData);
            fcmMessage.setFcmMessageNotification(fcmMessageNotification);




        return fcmMessage;
    }

}
