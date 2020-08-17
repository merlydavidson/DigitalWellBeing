package com.project.digitalwellbeing.data;

import android.app.usage.UsageStats;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.project.digitalwellbeing.DashboardActivity;
import com.project.digitalwellbeing.data.model.AppDataBase;
import com.project.digitalwellbeing.data.model.BlockedApps;
import com.project.digitalwellbeing.data.model.CallDetails;
import com.project.digitalwellbeing.data.model.DigitalWellBeingDao;
import com.project.digitalwellbeing.data.model.GoogleFitDetails;
import com.project.digitalwellbeing.data.model.LockUnlock;
import com.project.digitalwellbeing.data.model.LogDetails;
import com.project.digitalwellbeing.data.model.TaskDetails;
import com.project.digitalwellbeing.data.model.UserDetails;
import com.project.digitalwellbeing.remote.Communicator;
import com.project.digitalwellbeing.utils.CommonDataArea;
import com.project.digitalwellbeing.utils.CommonFunctionArea;
import com.project.digitalwellbeing.utils.FCMMessages;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.project.digitalwellbeing.utils.CommonDataArea.APP_BLOCK_PIN;
import static com.project.digitalwellbeing.utils.CommonDataArea.BLOCKAPPS;
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
        Log.d("FABDATA2>>",remoteMessage.getNotification().getTitle());
        Log.d("FABDATA2>>",remoteMessage.toString());
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
        } else if (remoteMessage.getNotification().getTitle().equalsIgnoreCase("7")) {//update task details to db
            UpdateTaskDetails(remoteMessage.getNotification().getBody());
        } else if (remoteMessage.getNotification().getTitle().equalsIgnoreCase("8")) {//update appusage details to db
            UpdateApplicationUsage(remoteMessage.getNotification().getBody());
        } else if (remoteMessage.getNotification().getTitle().equalsIgnoreCase("8_A")) {//update appusage details to db
            UpdateApplicationUsageStatus(remoteMessage.getNotification().getBody());
        } else if (remoteMessage.getNotification().getTitle().equalsIgnoreCase("7_A")) {//update task details to db
            UpdateTaskDetailsStatus(remoteMessage.getNotification().getBody());
        } else if (remoteMessage.getNotification().getTitle().equalsIgnoreCase("3_A")) {
            insertLogDetailsStatus(remoteMessage.getNotification().getBody());
        } else if (remoteMessage.getNotification().getTitle().equalsIgnoreCase("4_A")) { //add task details to db ack
            insertTaskDetailsAck(remoteMessage.getNotification().getBody());
        } else if (remoteMessage.getNotification().getTitle().equalsIgnoreCase("6_A")) {//add call details to db ack
            insertCallDetailsAck(remoteMessage.getNotification().getBody());
        } else if (remoteMessage.getNotification().getTitle().equalsIgnoreCase("5_A")) {//lock unlock ack
            lockUnlockAck(remoteMessage.getNotification().getBody());
        } else if (remoteMessage.getNotification().getTitle().equalsIgnoreCase("9")) {//block apps
            UpdateBlockeAppStatus(remoteMessage.getNotification().getBody());
        }else if (remoteMessage.getNotification().getTitle().equalsIgnoreCase("9_A")) {//block apps ack
            UpdateBlockeAppStatusAck(remoteMessage.getNotification().getBody());
        }else if (remoteMessage.getNotification().getTitle().equalsIgnoreCase("10")) {//block apps ack
            InsertGoogleFitData(remoteMessage.getNotification().getBody());
        }else if (remoteMessage.getNotification().getTitle().equalsIgnoreCase("10_A")) {//block apps ack
            UpdateGoogleFitAck(remoteMessage.getNotification().getBody());
        } else if (remoteMessage.getNotification().getTitle().equalsIgnoreCase("11")) {//lock unlock ack
            lockUnlockUpdate(remoteMessage.getNotification().getBody());
        }else if (remoteMessage.getNotification().getTitle().equalsIgnoreCase("11_A")) {//lock unlock ack
            lockUnlockUpdateAck(remoteMessage.getNotification().getBody());
        }
    }

    private void lockUnlockUpdateAck(String body) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<LockUnlock>>() {
        }.getType();
        List<LockUnlock> list = gson.fromJson(body, listType);
        if(!list.isEmpty()) {
            for(LockUnlock l:list){
                if(l.getChildId().equals(CommonFunctionArea.getDeviceUUID(context))){
                    AppDataBase appDataBase = AppDataBase.getInstance(context);
                    DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
                    if (digitalWellBeingDao.LockUnLock1(l.getId(),l.getChildId())) {
                        digitalWellBeingDao.updateLockUnlockAck(l.getId(),"1");
                    }
                }

            }

        }
    }

    private void lockUnlockUpdate(String body) {//received at par
        Gson gson = new Gson();
        Type listType = new TypeToken<List<LockUnlock>>() {
        }.getType();
        List<LockUnlock> list = gson.fromJson(body, listType);
        if(!list.isEmpty()) {
            for (LockUnlock l : list) {

                AppDataBase appDataBase = AppDataBase.getInstance(context);
                DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
                String childId="";
                if (digitalWellBeingDao.LockUnLock1(l.getId(),l.getChildId())) {
                    childId=l.getChildId();
                    digitalWellBeingDao.updateLockUnlock(l.getChildId(), l.isLocked(), l.getPassword(), "1");
                }

                new Communicator(context).sendMessage(FCMMessages.LockAck2(list, childId));
            }
        }
    }

    private void UpdateGoogleFitAck(String body) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<GoogleFitDetails>>() {
        }.getType();
        List<GoogleFitDetails> list = gson.fromJson(body, listType);
        AppDataBase appDataBase = AppDataBase.getInstance(context);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();

        for (GoogleFitDetails t : list) {

            if(digitalWellBeingDao.googleFitExists(t.getGoogleFitID(),t.getChildId()))
                digitalWellBeingDao.updateGooglefitAck(t.getGoogleFitID(),"1",t.getChildId());
        }
    }

    private void InsertGoogleFitData(String body) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<GoogleFitDetails>>() {
        }.getType();
        List<GoogleFitDetails> list = gson.fromJson(body, listType);
        AppDataBase appDataBase = AppDataBase.getInstance(context);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        String child_id = "";
        for (GoogleFitDetails t : list) {
            child_id = t.getChildId();
            if(!digitalWellBeingDao.googleFitExists(t.getGoogleFitID(),t.getChildId()))
                digitalWellBeingDao.insertGoogleDetails(t);
        }
        new Communicator(context).sendMessage(FCMMessages.googleFitAck(list, child_id));
    }

    private void UpdateBlockeAppStatusAck(String body) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<BlockedApps>>() {
        }.getType();
        List<BlockedApps> list = gson.fromJson(body, listType);
        AppDataBase appDataBase = AppDataBase.getInstance(context);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        for(BlockedApps b:list) {
            if (digitalWellBeingDao.ifAppDetailsExists(b.getPackagename(), b.getChildId())) {
                digitalWellBeingDao.updateBlockStatus2(b.getChecked(), b.getPackagename(), "1", b.getChildId());
            }
        }
    }

    private void lockUnlockAck(String body) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<LockUnlock>>() {
        }.getType();
        List<LockUnlock> list = gson.fromJson(body, listType);

        AppDataBase appDataBase = AppDataBase.getInstance(context);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        for (LockUnlock l : list) {
            if (digitalWellBeingDao.LockUnLock1(l.getId(), l.getChildId()))
                digitalWellBeingDao.updateLockUnlockAck(l.getId(), "1");
        }
    }

    private void insertCallDetailsAck(String body) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<CallDetails>>() {
        }.getType();
        List<CallDetails> list = gson.fromJson(body, listType);
        AppDataBase appDataBase = AppDataBase.getInstance(context);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        String child_id = "";
        for (CallDetails t : list) {
            child_id = t.getChildId();
            if (getCallEntry(t.getCallerId(), t.getChildId())) {
                digitalWellBeingDao.updateCallDetailStatus(t.getCallerId(), "1", t.getChildId());
            }
        }
       // new Communicator(context).sendMessage(FCMMessages.sendCallDetailsAck(list, child_id));
    }

    private void insertTaskDetailsAck(String body) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<TaskDetails>>() {
        }.getType();
        List<TaskDetails> list = gson.fromJson(body, listType);
        AppDataBase appDataBase = AppDataBase.getInstance(context);

        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        for (TaskDetails taskDetails : list) {
            if (digitalWellBeingDao.taskExists(taskDetails.getLogId(), taskDetails.getChildId())) {
                digitalWellBeingDao.updateTaskdetailsStatus(taskDetails.getLogId(), "1", taskDetails.getChildId());
            }
        }
    }

    private void insertLogDetailsStatus(String body) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<LogDetails>>() {
        }.getType();
        List<LogDetails> list = gson.fromJson(body, listType);
       /* if (response.isOnline()) {
            response.setOnline(false);
        } else
            response.setOnline(true);*/
        AppDataBase appDataBase = AppDataBase.getInstance(context);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        String child_id = "";
        for (LogDetails l : list) {
            if (digitalWellBeingDao.checkLogdetails(l.getChildId(), l.getLogId()))
                digitalWellBeingDao.updateLogdetailsAck(l.logId, "1", l.getChildId());
        }


    }

    private void UpdateTaskDetailsStatus(String body) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<TaskDetails>>() {
        }.getType();
        List<TaskDetails> list = gson.fromJson(body, listType);

        AppDataBase appDataBase = AppDataBase.getInstance(context);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        for (TaskDetails taskDetails : list) {
            if (digitalWellBeingDao.taskExists(taskDetails.getLogId(), taskDetails.getChildId())) {
                digitalWellBeingDao.updateTaskdetails(taskDetails.getLogId(), "1", taskDetails.getStatus());
            }
        }
    }

    private void UpdateApplicationUsage(String body) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<BlockedApps>>() {
        }.getType();
        String child_id = "";
        List<BlockedApps> list = gson.fromJson(body, listType);
        AppDataBase appDataBase = AppDataBase.getInstance(context);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        for (BlockedApps u : list) {
            if (!digitalWellBeingDao.ifAppDetailsExists(u.getPackagename(), u.getChildId())) {
                BlockedApps blockedApps = new BlockedApps();
                blockedApps.setPackagename(u.getPackagename());
                blockedApps.setLastTimeUsed(u.getLastTimeUsed());
                blockedApps.setTotalTimeInForeground(u.getTotalTimeInForeground());
                blockedApps.setChildId(u.getChildId());
                blockedApps.setChecked(false);
                blockedApps.setAcknowlwdgement("1");
                child_id = u.getChildId();
                digitalWellBeingDao.insertAppDta(blockedApps);
            } else {

                long t2 = u.getTotalTimeInForeground();
                int istrue = digitalWellBeingDao.updateAppDetails(t2,
                        u.getPackagename(), "1", u.getChildId());

            }
            if (!child_id.equals(""))
                new Communicator(context).sendMessage(FCMMessages.sendAppdataAck(list, child_id));

        }
    }

    private void UpdateBlockeAppStatus(String body) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<BlockedApps>>() {
        }.getType();
        String child_id = "";
        List<BlockedApps> list = gson.fromJson(body, listType);
        List<BlockedApps> list2 = new ArrayList<>();
        AppDataBase appDataBase = AppDataBase.getInstance(context);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        for (BlockedApps u : list) {
            if (u.getChildId().equals(CommonFunctionArea.getDeviceUUID(context))) {
                list2.add(u);
                if (digitalWellBeingDao.ifAppDetailsExists(u.getPackagename(), u.getChildId())) {
                    digitalWellBeingDao.updateBlockStatus3(u.getChecked(), u.getPackagename(), u.getChildId());
                }
            }

        }
        if (!CommonDataArea.PARENT_UUID.contains(CommonFunctionArea.getDeviceUUID(context) )
                && !CommonDataArea.PARENT_UUID.equals("/topics/"))
            new Communicator(context).sendMessage(FCMMessages.BlockAppsAck(list2, CommonDataArea.PARENT_UUID));
    }

    private void UpdateApplicationUsageStatus(String body) {//updating acknowledge status in child
        Gson gson = new Gson();
        Type listType = new TypeToken<List<BlockedApps>>() {
        }.getType();
        String child_id = "";
        List<BlockedApps> list = gson.fromJson(body, listType);
        AppDataBase appDataBase = AppDataBase.getInstance(context);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        for (BlockedApps u : list) {


            long t2 = u.getTotalTimeInForeground();
            int istrue = digitalWellBeingDao.updateAppDetails(t2,
                    u.getPackagename(), "1", u.getChildId());


        }
    }

    private void UpdateTaskDetails(String body) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<TaskDetails>>() {
        }.getType();
        List<TaskDetails> list = gson.fromJson(body, listType);
        String child_id = "";
        AppDataBase appDataBase = AppDataBase.getInstance(context);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        for (TaskDetails taskDetails : list) {
            if (digitalWellBeingDao.taskExists(taskDetails.getLogId(), taskDetails.getChildId())) {
                child_id = taskDetails.getChildId();
                digitalWellBeingDao.updateTaskdetails2(taskDetails.getChildId(), "1", taskDetails.getStatus(), taskDetails.getLogId());
            }
        }
        new Communicator(context).sendMessage(FCMMessages.updateTaskDetailsStatus(list, child_id));

    }

    private void lockUnlock(String body) {

        Gson gson = new Gson();
        Type listType = new TypeToken<List<LockUnlock>>() {
        }.getType();
        List<LockUnlock> list = gson.fromJson(body, listType);
        List<LockUnlock> list2 = new ArrayList<>();
        AppDataBase appDataBase = AppDataBase.getInstance(context);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        if(list!=null && !list.isEmpty()){
        for (LockUnlock l : list) {
            if (l.getChildId().equals(CommonDataArea.CURRENTCHILDID)) {
                list2.add(l);
                if (digitalWellBeingDao.LockUnLock(CommonDataArea.CURRENTCHILDID)) {
                    digitalWellBeingDao.updateLockUnlock(l.getChildId(), l.isLocked(), l.getPassword(), "1");
                } else if (!digitalWellBeingDao.LockUnLock(CommonDataArea.CURRENTCHILDID)) {
                   l.setAcknowledgement("1");
                    digitalWellBeingDao.insertLockUnlockData(l);
                }
            }
        }}
        if ((!CommonDataArea.PARENT_UUID.contains(CommonFunctionArea.getDeviceUUID(context)) ||
                !CommonDataArea.PARENT_UUID.equals("/topics/")) && !list2.isEmpty())
            new Communicator(context).sendMessage(FCMMessages.LockUnlockAck(list2, CommonDataArea.PARENT_UUID));

        /* */
    }

    private void insertTaskDetails(String body) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<TaskDetails>>() {
        }.getType();
        List<TaskDetails> list = gson.fromJson(body, listType);
        List<TaskDetails> list2 = new ArrayList<>();
        String child_id = "";
        AppDataBase appDataBase = AppDataBase.getInstance(context);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        for (TaskDetails taskDetails : list) {

            if (taskDetails.getChildId().equals(CommonDataArea.CURRENTCHILDID)) {
                list2.add(taskDetails);
                child_id = taskDetails.getChildId();
                if (!digitalWellBeingDao.taskExists(taskDetails.getLogId(), CommonDataArea.CURRENTCHILDID)) {
                    digitalWellBeingDao.insertTaskDetails(taskDetails);

                }
            }
        }
        if ((!CommonDataArea.PARENT_UUID.contains(CommonFunctionArea.getDeviceUUID(context)) ||
                !CommonDataArea.PARENT_UUID.equals("/topics/") ) && !list2.isEmpty())
            new Communicator(context).sendMessage(FCMMessages.sendTasksAck(list2, CommonDataArea.PARENT_UUID));
        list2.clear();


    }

    private void insertCallDetails(String body) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<CallDetails>>() {
        }.getType();
        List<CallDetails> list = gson.fromJson(body, listType);
        AppDataBase appDataBase = AppDataBase.getInstance(context);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        String child_id = "";
        for (CallDetails t : list) {
            child_id = t.getChildId();
            if (!getCallEntry(t.getCallerId(), t.getChildId())) {
                digitalWellBeingDao.insertCallDetails(t);
            }
        }
        new Communicator(context).sendMessage(FCMMessages.sendCallDetailsAck(list, child_id));

        // digitalWellBeingDao.insertTaskDetails(response);
    }

    public boolean getCallEntry(String callId, String chId) {
        boolean isExist = false;

        AppDataBase appDataBase = AppDataBase.getInstance(context);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        List<CallDetails> callDetails = digitalWellBeingDao.getaCallDetails2(callId, chId);
        if (callDetails.size() > 0)
            isExist = true;
        return isExist;

    }

    private void insertLogDetails(String body) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<LogDetails>>() {
        }.getType();
        List<LogDetails> list = gson.fromJson(body, listType);
       /* if (response.isOnline()) {
            response.setOnline(false);
        } else
            response.setOnline(true);*/
        AppDataBase appDataBase = AppDataBase.getInstance(context);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        String child_id = "";
        for (LogDetails l : list) {
            if (!digitalWellBeingDao.checkLogExists(l.getChildId(), l.getLogId())) {
                digitalWellBeingDao.insertLogDetails(l);
                child_id = l.getChildId();
            }
        }
        if (!child_id.equals(""))
            new Communicator(context).sendMessage(FCMMessages.sendLogsAck(list, child_id));

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
            String body = remoteMessage.getNotification().getBody();
            editor.putString(PARENT, body);
            editor.apply();
//            Toast.makeText(context, "Paired with "+remoteMessage.getNotification().getBody(), Toast.LENGTH_SHORT).show();
            CommonDataArea.FIREBASETOPIC = "/topics/" + remoteMessage.getNotification().getBody();
            new Communicator(context).sendMessage(new FCMMessages(context).sendBackChildDetails());
        }
    }

}
