package com.project.digitalwellbeing.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.project.digitalwellbeing.CloseAppActivity;
import com.project.digitalwellbeing.ContactListActivity;
import com.project.digitalwellbeing.R;
import com.project.digitalwellbeing.TaskActivity;
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
import com.project.digitalwellbeing.utils.CommonFunctionArea;
import com.project.digitalwellbeing.utils.CommonFunctionArea.*;
import com.project.digitalwellbeing.utils.FCMMessages;

import java.io.IOException;
import java.util.List;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static com.project.digitalwellbeing.utils.CommonDataArea.TASK;
import static com.project.digitalwellbeing.utils.CommonDataArea.context;
import static com.project.digitalwellbeing.utils.CommonDataArea.sharedPreferences;

public class DigitalWellBeingService extends Service{
    public static final String BROADCAST_ACTION = "Digital Well Being";
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    public int counter = 0;
    private static final String TAG = "TESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 100;
    private static final float LOCATION_DISTANCE = 10f;
    boolean blockApps = false;
    Intent intent;
    private String cityName = "";
    private Timer timer;
    private TimerTask timerTask;

    public static Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {

                }
            }
        };
        t.start();
        return t;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        //TODO:check role ..if role ==children check for tasks
        sharedPreferences = getSharedPreferences(
                CommonDataArea.prefName, Context.MODE_PRIVATE);
        int role = sharedPreferences.getInt(CommonDataArea.ROLESTR, 0);


        if (role == 1)
            sendToParent();
         if (role == 0)
        sendDatatoChild();

    }

    private void sendToParent() {

         t1 = new Timer();
        t1.scheduleAtFixedRate(new TimerTask() {
                                  @Override
                                  public void run() {
                                      sendApplicationUsage();
                                      sendUpdatedTaskDetails();
                                      sendLocationDetails();
                                      sendCallDetailsToParent();
                                      lookForTasks();//High priority
                                      if (!blockApps) {
                                          lookForscreenLock();  //medium priority
                                          if (!isBlocked)
                                              lookForBlockedApps();//low priority
                                      }
                                  }
                              },
                0,
                1000*30*1);
    }
    Timer t1,t2;
    private void sendDatatoChild() {

      t2  = new Timer();
        t2.scheduleAtFixedRate(new TimerTask() {
                                  @Override
                                  public void run() {
                                      blockAllApps();
                                      sendTasks();
                                  }
                              },
                0,
                1000*30*1);
    }

    private void sendApplicationUsage(){
        AppDataBase appDataBase = AppDataBase.getInstance(this);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        List<BlockedApps> appdetails=digitalWellBeingDao.getAppData(CommonFunctionArea.getDeviceUUID(this));
        String uuid="/topics/"+CommonFunctionArea.getDeviceUUID(DigitalWellBeingService.this);
        if(!CommonDataArea.FIREBASETOPIC.equals("/topics/") && !CommonDataArea.FIREBASETOPIC.equals( uuid))
            new Communicator(this).sendMessage(FCMMessages.sendAppdata(appdetails, CommonDataArea.FIREBASETOPIC));


    }
private void sendUpdatedTaskDetails(){
    AppDataBase appDataBase = AppDataBase.getInstance(this);
    DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
    List<TaskDetails> taskDetails = digitalWellBeingDao.getaTaskDetails2(CommonDataArea.CURRENTCHILDID);
    String uuid="/topics/"+CommonFunctionArea.getDeviceUUID(DigitalWellBeingService.this);
    if(!CommonDataArea.FIREBASETOPIC.equals("/topics/") && !CommonDataArea.FIREBASETOPIC.equals( uuid))
        new Communicator(this).sendMessage(FCMMessages.updateTaskDetails(taskDetails, CommonDataArea.FIREBASETOPIC));

}
    private void blockAllApps() {
        AppDataBase appDataBase = AppDataBase.getInstance(this);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        List<LockUnlock> taskDetails = digitalWellBeingDao.getLockUnlockDetailsList(CommonDataArea.CURRENTCHILDID);
        List<UserDetails> userDetails=digitalWellBeingDao.getUserDetails();
        for(UserDetails user: userDetails) {
            if (userDetails!=null && taskDetails != null) {

                new Communicator(this).sendMessage(FCMMessages.LockUnlock(taskDetails, "/topics/" + user.getChildDeviceUUID()));

            }
        }
   }
    private void sendLocationDetails(){
        AppDataBase appDataBase = AppDataBase.getInstance(this);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        List<LogDetails> logDetails = digitalWellBeingDao.getLogDetails(CommonDataArea.CURRENTCHILDID);
        if(!CommonDataArea.FIREBASETOPIC.equals("/topics/") && !CommonDataArea.FIREBASETOPIC.equals("/topics/"+CommonFunctionArea.getDeviceUUID(DigitalWellBeingService.this) ))
         new Communicator(getApplicationContext()).sendMessage(FCMMessages.sendLogs(logDetails,CommonDataArea.FIREBASETOPIC));

    }
    private void sendTasks() {
        AppDataBase appDataBase = AppDataBase.getInstance(this);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        List<TaskDetails> taskDetails = digitalWellBeingDao.getaTaskDetails();
        List<UserDetails> userDetails=digitalWellBeingDao.getUserDetails();
        for(UserDetails user: userDetails) {
            if (userDetails!=null && taskDetails != null) {

                    new Communicator(this).sendMessage(FCMMessages.sendTasks(taskDetails, "/topics/" + user.getChildDeviceUUID()));

            }
        }
    }

    private void sendCallDetailsToParent() {
        AppDataBase appDataBase = AppDataBase.getInstance(this);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        List<CallDetails> calldetails = digitalWellBeingDao.getCallDetails(CommonDataArea.getDAte("dd/MM/yyyy"));
        if(!CommonDataArea.FIREBASETOPIC.equals("/topics/") && !CommonDataArea.FIREBASETOPIC.equals("/topics/"+CommonFunctionArea.getDeviceUUID(DigitalWellBeingService.this) ))
                new Communicator(this).sendMessage(FCMMessages.sendCallDetails(calldetails, CommonDataArea.FIREBASETOPIC));
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate() {
        super.onCreate();
        if (!hasPermission()) {
            usageAccessSettingsPage();
        }
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "1";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        //startTimer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stoptimertask();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }



    public void stoptimertask() {

        if(t1!=null){
            t1.cancel();
            t1 = null;
        }
        if(t2!=null){
            t2.cancel();
            t2 = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        startService(restartServiceIntent);
        super.onTaskRemoved(rootIntent);
    }


    public void usageAccessSettingsPage() {//permission for reading foreground task
        try {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (intent.resolveActivity(getPackageManager()) != null) {
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                DigitalWellBeingService.this.startActivity(intent);
            }


        } catch (Exception e) {
        }
    }


    public void showDialog() {

        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
                        | WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSPARENT);

        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.format = PixelFormat.TRANSLUCENT;

        params.gravity = Gravity.TOP;

        LinearLayout ly = new LinearLayout(this);
        ly.setBackgroundColor(Color.BLACK);
        ly.setOrientation(LinearLayout.VERTICAL);

       /* ImageView iv = new ImageView(this);
        iv.setImageResource(R.drawable.ic_launcher);*/

        //ly.addView(iv);

        TextView tv1 = new TextView(this);
        tv1.setWidth(params.width);
        tv1.setBackgroundColor(Color.BLUE);

        ly.addView(tv1);

        wm.addView(ly, params);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean hasPermission() {
        try {
            PackageManager packageManager = getApplicationContext().getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getApplicationContext().getPackageName(), 0);
            AppOpsManager appOpsManager = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                appOpsManager = (AppOpsManager) getApplicationContext().getSystemService(Context.APP_OPS_SERVICE);
            }
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void lookForTasks() {
        AppDataBase appDataBase = AppDataBase.getInstance(this);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        List<TaskDetails> taskDetails = digitalWellBeingDao.getTasks(CommonDataArea.getDAte("dd/MM/yyyy"));

        if (taskDetails != null && taskDetails.size() > 0) {
            String[] current_time = CommonDataArea.getDAte("dd/MM/yyyy HH:mm").split(" ");
            for (int i = 0; i < taskDetails.size(); i++) {
                String startTime = taskDetails.get(i).getStarttime();
                String endTime = taskDetails.get(i).getEndtime();
                boolean block = taskDetails.get(i).getEnableApps();
                if (CommonFunctionArea.compareTimes(startTime, current_time[1]) && CommonFunctionArea.compareTimes(current_time[1], endTime) && block) {
                    blockApps = true;
                    break;
                } else
                    blockApps = false;
            }
            if (blockApps) {
                String package_name = CommonFunctionArea.foregroundApplication(this);
                String apppackage = CommonDataArea.APP_PACKAGE_NAME;
                String launcher = CommonDataArea.LAUNCHER_PACKAGE_NAME;
                if (!package_name.equalsIgnoreCase(CommonDataArea.APP_PACKAGE_NAME) &&
                        !package_name.equalsIgnoreCase(CommonDataArea.LAUNCHER_PACKAGE_NAME)) {
                    Intent i = new Intent(DigitalWellBeingService.this, CloseAppActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(TASK, "tasks");
                    i.putExtras(bundle);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    DigitalWellBeingService.this.startActivity(i);
                    //showDialog();
                }

            }
        }
    }

    boolean isBlocked = false;

    private void lookForBlockedApps() {
        AppDataBase appDataBase = AppDataBase.getInstance(this);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        String package_name = CommonFunctionArea.foregroundApplication(this);
       BlockedApps b = digitalWellBeingDao.getBlockedAppDetail(package_name);
        if(b!=null) {
            if (b.getChecked()) {
                Intent i = new Intent(DigitalWellBeingService.this, CloseAppActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(TASK, "block_selected_apps");
                i.putExtras(bundle);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                DigitalWellBeingService.this.startActivity(i);

            }
        }
    }

    private void lookForscreenLock() {

        AppDataBase appDataBase = AppDataBase.getInstance(this);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        if (digitalWellBeingDao.LockUnLock(CommonDataArea.CURRENTCHILDID)) {
            LockUnlock lockUnlock = digitalWellBeingDao.getLockUnlockDetails(CommonDataArea.CURRENTCHILDID);
            if (lockUnlock != null) {
                if (lockUnlock != null && lockUnlock.isLocked()) {
                    String package_name = CommonFunctionArea.foregroundApplication(this);
                    if (!package_name.equalsIgnoreCase(CommonDataArea.APP_PACKAGE_NAME) &&
                            !package_name.equalsIgnoreCase(CommonDataArea.LAUNCHER_PACKAGE_NAME)) {
                        Intent i = new Intent(DigitalWellBeingService.this, CloseAppActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(TASK, "lock");
                        i.putExtras(bundle);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        DigitalWellBeingService.this.startActivity(i);
                        //showDialog();

                    }
                }
            }
        }
    }

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(DigitalWellBeingService.this, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String currentForegrounApp = CommonFunctionArea.foregroundApplication(DigitalWellBeingService.this);
                AppDataBase appDataBase = AppDataBase.getInstance(context);
                DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
                LogDetails logDetails=new LogDetails();
                logDetails.setLocation(city);
                logDetails.setTimeStamp(CommonDataArea.getDAte("dd/MM/yyyy HH:mm"));
                logDetails.setOnline(new CommonFunctionArea().getDeviceLocked(getApplicationContext()));
                logDetails.setApp_details(currentForegrounApp);
                logDetails.setChildId(CommonDataArea.CURRENTCHILDID);
                digitalWellBeingDao.insertLogDetails(logDetails);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }
    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };
    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
    }



