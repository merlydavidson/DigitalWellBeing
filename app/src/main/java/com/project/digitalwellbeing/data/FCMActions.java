package com.project.digitalwellbeing.data;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.project.digitalwellbeing.data.model.AppDataBase;
import com.project.digitalwellbeing.data.model.DigitalWellBeingDao;
import com.project.digitalwellbeing.data.model.LogDetails;
import com.project.digitalwellbeing.data.model.UserDetails;
import com.project.digitalwellbeing.remote.Communicator;
import com.project.digitalwellbeing.utils.CommonDataArea;
import com.project.digitalwellbeing.utils.FCMMessages;

import static com.project.digitalwellbeing.utils.CommonDataArea.PARENT;
import static com.project.digitalwellbeing.utils.CommonDataArea.context;
import static com.project.digitalwellbeing.utils.CommonDataArea.editor;
import static com.project.digitalwellbeing.utils.CommonDataArea.isRegisterd;
import static com.project.digitalwellbeing.utils.CommonDataArea.sharedPreferences;

public class FCMActions {
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void parseResult(RemoteMessage remoteMessage, Context context) {
        CommonDataArea.remoteMessage = remoteMessage;
        CommonDataArea.context = context;
        if (remoteMessage.getNotification().getTitle().equalsIgnoreCase("1")) {

            registerParent(remoteMessage);
        } else if (remoteMessage.getNotification().getTitle().equalsIgnoreCase("2")) {
            inserChildDetails(remoteMessage.getNotification().getBody());
        } else if (remoteMessage.getNotification().getTitle().equalsIgnoreCase("3")) {
            insertLogDetails(remoteMessage.getNotification().getBody());
        }
    }

    private void insertLogDetails(String body) {
        Gson gson = new Gson();
        LogDetails response = gson.fromJson(body, LogDetails.class);


        AppDataBase appDataBase = AppDataBase.getInstance(context);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        digitalWellBeingDao.insertLogDetails(response);
    }

    private void inserChildDetails(String body) {
        Gson gson = new Gson();
        UserDetails response = gson.fromJson(body, UserDetails.class);


        AppDataBase appDataBase = AppDataBase.getInstance(context);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        digitalWellBeingDao.insertUserDetails(response);
    }

    public void registerParent(RemoteMessage remoteMessage) {
        if (CommonDataArea.ROLE == 1) {
            sharedPreferences = CommonDataArea.context.getSharedPreferences(
                    CommonDataArea.prefName, Context.MODE_PRIVATE);
            CommonDataArea.editor = sharedPreferences.edit();
            editor.putBoolean(isRegisterd, true);
            editor.putString(PARENT, remoteMessage.getNotification().getBody());
            editor.commit();
//            Toast.makeText(context, "Paired with "+remoteMessage.getNotification().getBody(), Toast.LENGTH_SHORT).show();
            CommonDataArea.FIREBASETOPIC = "/topics/" + remoteMessage.getNotification().getBody();
            new Communicator(context).sendMessage(new FCMMessages(context).sendBackChildDetails());
        }
    }

}
