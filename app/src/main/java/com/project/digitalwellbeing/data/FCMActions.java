package com.project.digitalwellbeing.data;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.project.digitalwellbeing.data.model.AppDataBase;
import com.project.digitalwellbeing.data.model.BlockedApps;
import com.project.digitalwellbeing.data.model.CallDetails;
import com.project.digitalwellbeing.data.model.DigitalWellBeingDao;
import com.project.digitalwellbeing.data.model.LockUnlock;
import com.project.digitalwellbeing.data.model.LogDetails;
import com.project.digitalwellbeing.data.model.TaskDetails;
import com.project.digitalwellbeing.data.model.UserDetails;
import com.project.digitalwellbeing.remote.Communicator;
import com.project.digitalwellbeing.utils.CommonDataArea;
import com.project.digitalwellbeing.utils.FCMMessages;

import java.lang.reflect.Type;
import java.util.List;

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
        } else if (remoteMessage.getNotification().getTitle().equalsIgnoreCase("4")) { //add task details to db
            insertTaskDetails(remoteMessage.getNotification().getBody());
        } else if (remoteMessage.getNotification().getTitle().equalsIgnoreCase("5")) {//lock unlock
            lockUnlock(remoteMessage.getNotification().getBody());
        } else if (remoteMessage.getNotification().getTitle().equalsIgnoreCase("6")) {//add call details to db
            insertCallDetails(remoteMessage.getNotification().getBody());
        }else if (remoteMessage.getNotification().getTitle().equalsIgnoreCase("7")) {//update task details to db
            UpdateTaskDetails(remoteMessage.getNotification().getBody());
        }else if (remoteMessage.getNotification().getTitle().equalsIgnoreCase("8")) {//update appusage details to db
            UpdateApplicationUsage(remoteMessage.getNotification().getBody());
        }
    }

    private void UpdateApplicationUsage(String body) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<BlockedApps>>() {
        }.getType();
        List<BlockedApps> list = gson.fromJson(body, listType);
        AppDataBase appDataBase = AppDataBase.getInstance(context);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        for (BlockedApps u : list) {
            if(!digitalWellBeingDao.ifAppDetailsExists(u.getPackagename(),CommonDataArea.CURRENTCHILDID)) {
                BlockedApps blockedApps = new BlockedApps();
                blockedApps.setPackagename(u.getPackagename());
                blockedApps.setLastTimeUsed(u.getLastTimeUsed());
                blockedApps.setTotalTimeInForeground( u.getTotalTimeInForeground());
                blockedApps.setChildId(CommonDataArea.CURRENTCHILDID);
                blockedApps.setChecked(false);
                digitalWellBeingDao.insertAppDta(blockedApps);
            }else{

                long t2=u.getTotalTimeInForeground();
                int istrue= digitalWellBeingDao.updateAppDetails(t2,
                        u.getPackagename(), CommonDataArea.CURRENTCHILDID);

            }}
    }

    private void UpdateTaskDetails(String body) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<TaskDetails>>() {
        }.getType();
        List<TaskDetails> list = gson.fromJson(body, listType);

        AppDataBase appDataBase = AppDataBase.getInstance(context);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        for (TaskDetails taskDetails : list) {
            if(digitalWellBeingDao.taskExists(taskDetails.getLogId(),taskDetails.getChildId())){
                digitalWellBeingDao.updateTaskdetails(taskDetails.getLogId(),taskDetails.getStatus());
            }
           }
    }

    private void lockUnlock(String body) {
        /*sharedPreferences =context.getSharedPreferences(
                CommonDataArea.prefName, Context.MODE_PRIVATE);
        CommonDataArea.editor = sharedPreferences.edit();
        if(!body.equals("")){
            String[] result=body.split("_");
            if(result[0].equals("lock")){
                editor.putBoolean(BLOCKAPPS, true);
                editor.putString(APP_BLOCK_PIN,result[1]);
                editor.commit();
            }else{
                editor.putBoolean(BLOCKAPPS, true);
                editor.putString(APP_BLOCK_PIN,"");
                editor.commit();
            }
        }*/
        Gson gson = new Gson();
        Type listType = new TypeToken<List<LockUnlock>>() {
        }.getType();
        List<LockUnlock> list = gson.fromJson(body, listType);
        AppDataBase appDataBase = AppDataBase.getInstance(context);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        if(list!=null)
        {
        for (LockUnlock l : list) {
            if (l.getChildId().equals(CommonDataArea.CURRENTCHILDID)) {
                if (digitalWellBeingDao.LockUnLock(CommonDataArea.CURRENTCHILDID)) {
                    digitalWellBeingDao.updateLockUnlock(l.getChildId(), l.isLocked(), l.getPassword());
                } else if (!digitalWellBeingDao.LockUnLock(CommonDataArea.CURRENTCHILDID)) {
                    digitalWellBeingDao.insertLockUnlockData(l);
                }
            }
        }
        }
        /* */
    }

    private void insertTaskDetails(String body) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<TaskDetails>>() {
        }.getType();
        List<TaskDetails> list = gson.fromJson(body, listType);

        AppDataBase appDataBase = AppDataBase.getInstance(context);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        for (TaskDetails taskDetails : list) {
            if (taskDetails.getChildId().equals(CommonDataArea.CURRENTCHILDID)) {
            if (!digitalWellBeingDao.taskExists(taskDetails.getLogId(), CommonDataArea.CURRENTCHILDID)) {
                digitalWellBeingDao.insertTaskDetails(taskDetails);

            }/*else if(digitalWellBeingDao.taskExists(taskDetails.getLogId(),CommonDataArea.CURRENTCHILDID)){
                digitalWellBeingDao.updateTaskdetails(taskDetails.getLogId(),taskDetails.getStatus());
            }*/
        }}
        // digitalWellBeingDao.insertTaskDetails(response);
    }

    private void insertCallDetails(String body) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<CallDetails>>() {}.getType();
        List<CallDetails> list = gson.fromJson(body, listType);
        AppDataBase appDataBase = AppDataBase.getInstance(context);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        for(CallDetails t:list) {
            if (!getCallEntry(t.getCallerLogId(),t.getChildId())) {
                digitalWellBeingDao.insertCallDetails(t);
            }
        }
        // digitalWellBeingDao.insertTaskDetails(response);
    }

    public boolean getCallEntry(int callId,String chId) {
        boolean isExist = false;

        AppDataBase appDataBase = AppDataBase.getInstance(context);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        List<CallDetails> callDetails = digitalWellBeingDao.getaCallDetails(callId,chId);
        if (callDetails.size() > 0)
            isExist = true;
        return isExist;

    }

    private void insertLogDetails(String body) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<LogDetails>>() {}.getType();
        List<LogDetails> list = gson.fromJson(body, listType);
       /* if (response.isOnline()) {
            response.setOnline(false);
        } else
            response.setOnline(true);*/
        AppDataBase appDataBase = AppDataBase.getInstance(context);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        for(LogDetails l:list){
            if(!digitalWellBeingDao.checkLogExists(l.getChildId(),l.logId)){
                digitalWellBeingDao.insertLogDetails(l);
            }
        }
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
            String body=remoteMessage.getNotification().getBody();
            editor.putString(PARENT, body);
            editor.apply();
//            Toast.makeText(context, "Paired with "+remoteMessage.getNotification().getBody(), Toast.LENGTH_SHORT).show();
            CommonDataArea.FIREBASETOPIC = "/topics/" + remoteMessage.getNotification().getBody();
            new Communicator(context).sendMessage(new FCMMessages(context).sendBackChildDetails());
        }
    }

}
