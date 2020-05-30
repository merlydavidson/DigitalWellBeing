package com.project.digitalwellbeing.utils;

import android.app.KeyguardManager;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.project.digitalwellbeing.DashboardActivity;
import com.project.digitalwellbeing.data.model.AppDataBase;
import com.project.digitalwellbeing.data.model.DigitalWellBeingDao;
import com.project.digitalwellbeing.data.model.LogDetails;
import com.project.digitalwellbeing.data.model.UserDetails;
import com.project.digitalwellbeing.remote.Communicator;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static com.project.digitalwellbeing.utils.CommonDataArea.sharedPreferences;

public class CommonFunctionArea {

    public static String getDeviceUUID(Context context) {
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidId;
    }

    public void subscribeTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic(CommonDataArea.FIREBASETOPIC);

    }

    public boolean getDeviceLocked(Context context) {
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        return km.inKeyguardRestrictedInputMode();
    }

    public String getTimeStamp() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
    }

    public LogDetails getLogList(Context context) {
        List<LogDetails> logDetails = null;
        LogDetails logDetails1 = null;
        try {
            AppDataBase appDataBase = AppDataBase.getInstance(context);
            DigitalWellBeingDao stimulationSessionsDao = appDataBase.userDetailsDao();
            logDetails = stimulationSessionsDao.getLogDetails();
            logDetails1 = logDetails.get(logDetails.size() - 1);
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
        return logDetails1;
    }
    public static String getparentId(Context context)
    {
        sharedPreferences = context.getSharedPreferences(
                CommonDataArea.prefName, Context.MODE_PRIVATE);
        return  sharedPreferences.getString(CommonDataArea.PARENT,"");
    }

    public static int getRole(Context context)
    {
        sharedPreferences = context.getSharedPreferences(
                CommonDataArea.prefName, Context.MODE_PRIVATE);
        return  sharedPreferences.getInt(CommonDataArea.ROLESTR,0);
    }
}
