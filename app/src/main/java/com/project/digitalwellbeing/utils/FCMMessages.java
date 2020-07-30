package com.project.digitalwellbeing.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.project.digitalwellbeing.SignupActivity;
import com.project.digitalwellbeing.data.model.AppDataBase;
import com.project.digitalwellbeing.data.model.BlockedApps;
import com.project.digitalwellbeing.data.model.CallDetails;
import com.project.digitalwellbeing.data.model.DigitalWellBeingDao;
import com.project.digitalwellbeing.data.model.FCMMessage;
import com.project.digitalwellbeing.data.model.FCMMessageData;
import com.project.digitalwellbeing.data.model.FCMMessageNotification;
import com.project.digitalwellbeing.data.model.LockUnlock;
import com.project.digitalwellbeing.data.model.LogDetails;
import com.project.digitalwellbeing.data.model.TaskDetails;
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
       // fcmMessageData.setBody("test");
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
           // fcmMessageData.setBody("test");
            fcmMessageData.setContent_available("yes");
            fcmMessageData.setPriority("yes");
            fcmMessageData.setTitle("2");
            fcmMessageNotification.setBody(json);
            fcmMessageNotification.setContent_available("yes");
            fcmMessageNotification.setPriority("yes");
            fcmMessageNotification.setTitle("2");
            fcmMessage.setFcmMessageData(fcmMessageData);
            fcmMessage.setFcmMessageNotification(fcmMessageNotification);

        }


        return fcmMessage;
    }

    public static FCMMessage sendLogs(List<LogDetails> logDetails,String topic) {
        FCMMessage fcmMessage = new FCMMessage();
        FCMMessageData fcmMessageData = new FCMMessageData();
        FCMMessageNotification fcmMessageNotification = new FCMMessageNotification();



            JSONObject jsonObj = null;
            Gson gson = new GsonBuilder().create();
            String json = gson.toJson(logDetails);
            fcmMessage.setTo(topic);
            //fcmMessageData.setBody("test");
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

    public static FCMMessage sendLogsAck(List<LogDetails> logDetails,String topic) {
        FCMMessage fcmMessage = new FCMMessage();
        FCMMessageData fcmMessageData = new FCMMessageData();
        FCMMessageNotification fcmMessageNotification = new FCMMessageNotification();



        JSONObject jsonObj = null;
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(logDetails);
        fcmMessage.setTo(topic);
        //fcmMessageData.setBody("test");
        fcmMessageData.setContent_available("yes");
        fcmMessageData.setPriority("yes");
        fcmMessageData.setTitle("log");
        fcmMessageNotification.setBody(json);
        fcmMessageNotification.setContent_available("yes");
        fcmMessageNotification.setPriority("yes");
        fcmMessageNotification.setTitle("3_A");
        fcmMessage.setFcmMessageData(fcmMessageData);
        fcmMessage.setFcmMessageNotification(fcmMessageNotification);




        return fcmMessage;
    }
    public static FCMMessage sendTasks(List<TaskDetails> taskDetails,String topic) {
        FCMMessage fcmMessage = new FCMMessage();
        FCMMessageData fcmMessageData = new FCMMessageData();
        FCMMessageNotification fcmMessageNotification = new FCMMessageNotification();


        JSONObject jsonObj = null;
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(taskDetails);
        fcmMessage.setTo(topic);
        //fcmMessageData.setBody("test");
        fcmMessageData.setContent_available("yes");
        fcmMessageData.setPriority("yes");
        fcmMessageData.setTitle("log");
        fcmMessageNotification.setBody(json);
        fcmMessageNotification.setContent_available("yes");
        fcmMessageNotification.setPriority("yes");
        fcmMessageNotification.setTitle("4");
        fcmMessage.setFcmMessageData(fcmMessageData);
        fcmMessage.setFcmMessageNotification(fcmMessageNotification);
       return fcmMessage;
     }

    public static FCMMessage sendTasksAck(List<TaskDetails> taskDetails,String topic) {
        FCMMessage fcmMessage = new FCMMessage();
        FCMMessageData fcmMessageData = new FCMMessageData();
        FCMMessageNotification fcmMessageNotification = new FCMMessageNotification();


        JSONObject jsonObj = null;
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(taskDetails);
        fcmMessage.setTo(topic);
        //fcmMessageData.setBody("test");
        fcmMessageData.setContent_available("yes");
        fcmMessageData.setPriority("yes");
        fcmMessageData.setTitle("log");
        fcmMessageNotification.setBody(json);
        fcmMessageNotification.setContent_available("yes");
        fcmMessageNotification.setPriority("yes");
        fcmMessageNotification.setTitle("4_A");
        fcmMessage.setFcmMessageData(fcmMessageData);
        fcmMessage.setFcmMessageNotification(fcmMessageNotification);
        return fcmMessage;
    }
    public static FCMMessage sendCallDetails(List<CallDetails> callDetails,String topic) {
        FCMMessage fcmMessage = new FCMMessage();
        FCMMessageData fcmMessageData = new FCMMessageData();
        FCMMessageNotification fcmMessageNotification = new FCMMessageNotification();


        JSONObject jsonObj = null;
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(callDetails);
        fcmMessage.setTo(topic);
        //fcmMessageData.setBody("test");
        fcmMessageData.setContent_available("yes");
        fcmMessageData.setPriority("yes");
        fcmMessageData.setTitle("log");
        fcmMessageNotification.setBody(json);
        fcmMessageNotification.setContent_available("yes");
        fcmMessageNotification.setPriority("yes");
        fcmMessageNotification.setTitle("6");
        fcmMessage.setFcmMessageData(fcmMessageData);
        fcmMessage.setFcmMessageNotification(fcmMessageNotification);
        return fcmMessage;
    }

    public static FCMMessage sendCallDetailsAck(List<CallDetails> callDetails,String topic) {
        FCMMessage fcmMessage = new FCMMessage();
        FCMMessageData fcmMessageData = new FCMMessageData();
        FCMMessageNotification fcmMessageNotification = new FCMMessageNotification();


        JSONObject jsonObj = null;
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(callDetails);
        fcmMessage.setTo(topic);
        //fcmMessageData.setBody("test");
        fcmMessageData.setContent_available("yes");
        fcmMessageData.setPriority("yes");
        fcmMessageData.setTitle("log");
        fcmMessageNotification.setBody(json);
        fcmMessageNotification.setContent_available("yes");
        fcmMessageNotification.setPriority("yes");
        fcmMessageNotification.setTitle("6_A");
        fcmMessage.setFcmMessageData(fcmMessageData);
        fcmMessage.setFcmMessageNotification(fcmMessageNotification);
        return fcmMessage;
    }
    public static FCMMessage updateTaskDetails(List<TaskDetails> taskDetails,String topic) {
        FCMMessage fcmMessage = new FCMMessage();
        FCMMessageData fcmMessageData = new FCMMessageData();
        FCMMessageNotification fcmMessageNotification = new FCMMessageNotification();


        JSONObject jsonObj = null;
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(taskDetails);
        fcmMessage.setTo(topic);
        //fcmMessageData.setBody("test");
        fcmMessageData.setContent_available("yes");
        fcmMessageData.setPriority("yes");
        fcmMessageData.setTitle("log");
        fcmMessageNotification.setBody(json);
        fcmMessageNotification.setContent_available("yes");
        fcmMessageNotification.setPriority("yes");
        fcmMessageNotification.setTitle("7");
        fcmMessage.setFcmMessageData(fcmMessageData);
        fcmMessage.setFcmMessageNotification(fcmMessageNotification);
        return fcmMessage;
    }
    public static FCMMessage updateTaskDetailsStatus(List<TaskDetails> taskDetails,String topic) {
        FCMMessage fcmMessage = new FCMMessage();
        FCMMessageData fcmMessageData = new FCMMessageData();
        FCMMessageNotification fcmMessageNotification = new FCMMessageNotification();


        JSONObject jsonObj = null;
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(taskDetails);
        fcmMessage.setTo(topic);
        //fcmMessageData.setBody("test");
        fcmMessageData.setContent_available("yes");
        fcmMessageData.setPriority("yes");
        fcmMessageData.setTitle("log");
        fcmMessageNotification.setBody(json);
        fcmMessageNotification.setContent_available("yes");
        fcmMessageNotification.setPriority("yes");
        fcmMessageNotification.setTitle("7_A");
        fcmMessage.setFcmMessageData(fcmMessageData);
        fcmMessage.setFcmMessageNotification(fcmMessageNotification);
        return fcmMessage;
    }
    public static FCMMessage sendAppdata(List<BlockedApps> blockedApps, String topic) {
        FCMMessage fcmMessage = new FCMMessage();
        FCMMessageData fcmMessageData = new FCMMessageData();
        FCMMessageNotification fcmMessageNotification = new FCMMessageNotification();


        JSONObject jsonObj = null;
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(blockedApps);
        fcmMessage.setTo(topic);
        //fcmMessageData.setBody("test");
        fcmMessageData.setContent_available("yes");
        fcmMessageData.setPriority("yes");
        fcmMessageData.setTitle("log");
        fcmMessageNotification.setBody(json);
        fcmMessageNotification.setContent_available("yes");
        fcmMessageNotification.setPriority("yes");
        fcmMessageNotification.setTitle("8");
        fcmMessage.setFcmMessageData(fcmMessageData);
        fcmMessage.setFcmMessageNotification(fcmMessageNotification);
        return fcmMessage;
    }
    public static FCMMessage sendAppdataAck(List<BlockedApps> blockedApps, String topic) {
        FCMMessage fcmMessage = new FCMMessage();
        FCMMessageData fcmMessageData = new FCMMessageData();
        FCMMessageNotification fcmMessageNotification = new FCMMessageNotification();


        JSONObject jsonObj = null;
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(blockedApps);
        fcmMessage.setTo(topic);
        //fcmMessageData.setBody("test");
        fcmMessageData.setContent_available("yes");
        fcmMessageData.setPriority("yes");
        fcmMessageData.setTitle("log");
        fcmMessageNotification.setBody(json);
        fcmMessageNotification.setContent_available("yes");
        fcmMessageNotification.setPriority("yes");
        fcmMessageNotification.setTitle("8_A");
        fcmMessage.setFcmMessageData(fcmMessageData);
        fcmMessage.setFcmMessageNotification(fcmMessageNotification);
        return fcmMessage;
    }
    public static FCMMessage LockUnlock(List<LockUnlock> lockUnlock,String topic) {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(lockUnlock);
        FCMMessage fcmMessage = new FCMMessage();
        FCMMessageData fcmMessageData = new FCMMessageData();
        FCMMessageNotification fcmMessageNotification = new FCMMessageNotification();
        fcmMessage.setTo(topic);
        fcmMessageData.setBody(json);
        fcmMessageData.setContent_available("yes");
        fcmMessageData.setPriority("yes");
        fcmMessageData.setTitle("test");
        //fcmMessageNotification.setBody(CommonFunctionArea.getDeviceUUID(context));
        fcmMessageNotification.setContent_available("yes");
        fcmMessageNotification.setPriority("yes");
        fcmMessageNotification.setTitle("5");
        fcmMessage.setFcmMessageData(fcmMessageData);
        fcmMessage.setFcmMessageNotification(fcmMessageNotification);
        return fcmMessage;
    }

    public static FCMMessage blockedApps(List<BlockedApps> lockUnlock,String topic) {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(lockUnlock);
        FCMMessage fcmMessage = new FCMMessage();
        FCMMessageData fcmMessageData = new FCMMessageData();
        FCMMessageNotification fcmMessageNotification = new FCMMessageNotification();
        fcmMessage.setTo(topic);
        fcmMessageData.setBody(json);
        fcmMessageData.setContent_available("yes");
        fcmMessageData.setPriority("yes");
        fcmMessageData.setTitle("test");
        //fcmMessageNotification.setBody(CommonFunctionArea.getDeviceUUID(context));
        fcmMessageNotification.setContent_available("yes");
        fcmMessageNotification.setPriority("yes");
        fcmMessageNotification.setTitle("9");
        fcmMessage.setFcmMessageData(fcmMessageData);
        fcmMessage.setFcmMessageNotification(fcmMessageNotification);
        return fcmMessage;
    }
    public static FCMMessage LockUnlockAck(List<LockUnlock> lockUnlock,String topic) {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(lockUnlock);
        FCMMessage fcmMessage = new FCMMessage();
        FCMMessageData fcmMessageData = new FCMMessageData();
        FCMMessageNotification fcmMessageNotification = new FCMMessageNotification();
        fcmMessage.setTo(topic);
        fcmMessageData.setBody(json);
        fcmMessageData.setContent_available("yes");
        fcmMessageData.setPriority("yes");
        fcmMessageData.setTitle("test");
        //fcmMessageNotification.setBody(CommonFunctionArea.getDeviceUUID(context));
        fcmMessageNotification.setContent_available("yes");
        fcmMessageNotification.setPriority("yes");
        fcmMessageNotification.setTitle("5_A");
        fcmMessage.setFcmMessageData(fcmMessageData);
        fcmMessage.setFcmMessageNotification(fcmMessageNotification);
        return fcmMessage;
    }
    public static FCMMessage BlockAppsAck(List<BlockedApps> lockUnlock,String topic) {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(lockUnlock);
        FCMMessage fcmMessage = new FCMMessage();
        FCMMessageData fcmMessageData = new FCMMessageData();
        FCMMessageNotification fcmMessageNotification = new FCMMessageNotification();
        fcmMessage.setTo(topic);
        fcmMessageData.setBody(json);
        fcmMessageData.setContent_available("yes");
        fcmMessageData.setPriority("yes");
        fcmMessageData.setTitle("test");
        //fcmMessageNotification.setBody(CommonFunctionArea.getDeviceUUID(context));
        fcmMessageNotification.setContent_available("yes");
        fcmMessageNotification.setPriority("yes");
        fcmMessageNotification.setTitle("9_A");
        fcmMessage.setFcmMessageData(fcmMessageData);
        fcmMessage.setFcmMessageNotification(fcmMessageNotification);
        return fcmMessage;
    }
}
