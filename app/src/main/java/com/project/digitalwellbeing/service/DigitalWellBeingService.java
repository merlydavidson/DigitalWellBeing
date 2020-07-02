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

import com.project.digitalwellbeing.CloseAppActivity;
import com.project.digitalwellbeing.R;
import com.project.digitalwellbeing.TaskActivity;
import com.project.digitalwellbeing.data.model.AppDataBase;
import com.project.digitalwellbeing.data.model.DigitalWellBeingDao;
import com.project.digitalwellbeing.data.model.TaskDetails;
import com.project.digitalwellbeing.remote.Communicator;
import com.project.digitalwellbeing.utils.CommonDataArea;
import com.project.digitalwellbeing.utils.CommonFunctionArea;
import com.project.digitalwellbeing.utils.FCMMessages;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class DigitalWellBeingService extends Service {
    public static final String BROADCAST_ACTION = "Digital Well Being";
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    public int counter = 0;
    public LocationManager locationManager;
    public DWBLocationListener listener;
    public Location previousBestLocation = null;
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
        checkTaskActivity();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new DWBLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, (LocationListener) listener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, listener);
        }


    }

    private void checkTaskActivity() {
        AppDataBase appDataBase = AppDataBase.getInstance(this);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
                                  @Override
                                  public void run() {


                                      List<TaskDetails> taskDetails = digitalWellBeingDao.getTasks(CommonDataArea.getDAte("dd/MM/yyyy"));
                                      String[] current_time = CommonDataArea.getDAte("dd/MM/yyyy HH:mm").split(" ");
                                      for (int i = 0; i < taskDetails.size(); i++) {
                                          String startTime = taskDetails.get(i).getStarttime();
                                          String endTime = taskDetails.get(i).getEndtime();
                                          boolean block = taskDetails.get(i).getEnableApps();
                                          if (compareDates(startTime,current_time[1]) && compareDates(current_time[1], endTime) && block) {
                                              blockApps = true;
                                              break;
                                          } else
                                              blockApps = false;
                                      }
                                      if (blockApps) {
                                          String package_name = foregroundApplication();
                                          String apppackage=CommonDataArea.APP_PACKAGE_NAME;
                                          String launcher=CommonDataArea.LAUNCHER_PACKAGE_NAME;
                                          if (!package_name.equalsIgnoreCase(CommonDataArea.APP_PACKAGE_NAME) &&
                                                  !package_name.equalsIgnoreCase(CommonDataArea.LAUNCHER_PACKAGE_NAME)) {
                                              Intent i = new Intent(DigitalWellBeingService.this, CloseAppActivity.class);
                                              i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                              DigitalWellBeingService.this.startActivity(i);
                                              //showDialog();
                                          }
                                      }
                                  }
                              },
                0,
                2000);
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate() {
        super.onCreate();
        if (!hasPermission()) {
            usageAccessSettingsPage();
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
        startTimer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stoptimertask();
        locationManager.removeUpdates(listener);
//        Intent broadcastIntent = new Intent();
//        broadcastIntent.setAction("restartservice");
//        broadcastIntent.setClass(this, Restarter.class);
//        this.sendBroadcast(broadcastIntent);
    }

    public void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                if (CommonFunctionArea.getRole(getApplicationContext()) == 1) {
                    CommonDataArea.FIREBASETOPIC = "/topics/" + CommonFunctionArea.getparentId(getApplicationContext());
                    Log.d("Merly", "from service 194");
                    Log.d("Merly", "city name " + cityName);
                    Log.d("Merly", "time stamp " + new CommonFunctionArea().getTimeStamp());
                    Log.d("Merly", "uuid " + CommonFunctionArea.getDeviceUUID(getApplicationContext()));
                    Log.d("Merly", "online " + new CommonFunctionArea().getDeviceLocked(getApplicationContext()));

                    new Communicator(getApplicationContext()).sendMessage(FCMMessages.sendLogs(cityName, new CommonFunctionArea().getTimeStamp(), new CommonFunctionArea().getDeviceLocked(getApplicationContext())));
                }
                //                Log.i("Count", "=========  "+ (counter++));
            }
        };
        timer.schedule(timerTask, 10000, 10000); //
    }

    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class DWBLocationListener implements LocationListener {

        public void onLocationChanged(final Location loc) {
            Log.i("*****", "Location changed");
            if (isBetterLocation(loc, previousBestLocation)) {
                loc.getLatitude();
                loc.getLongitude();
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(addresses!=null && addresses.size()>0){
                cityName = addresses.get(0).getAddressLine(0);
                if (CommonFunctionArea.getRole(getApplicationContext()) == 1) {
                    CommonDataArea.FIREBASETOPIC = "/topics/" + CommonFunctionArea.getparentId(getApplicationContext());
                    Log.d("Merly", "from service 194");
                    Log.d("Merly", "city name " + cityName);
                    Log.d("Merly", "time stamp " + new CommonFunctionArea().getTimeStamp());
                    Log.d("Merly", "uuid " + CommonFunctionArea.getDeviceUUID(getApplicationContext()));
                    Log.d("Merly", "online " + new CommonFunctionArea().getDeviceLocked(getApplicationContext()));
                    new Communicator(getApplicationContext()).sendMessage(FCMMessages.sendLogs(cityName, new CommonFunctionArea().getTimeStamp(), new CommonFunctionArea().getDeviceLocked(getApplicationContext())));
                }}
//                intent.putExtra("Latitude", ""+loc.getLatitude());
//                intent.putExtra("Longitude", ""+loc.getLongitude());
//                intent.putExtra("Provider", loc.getProvider());
//                sendBroadcast(intent);


            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
        }


        public void onProviderEnabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        startService(restartServiceIntent);
        super.onTaskRemoved(rootIntent);
    }

    private String foregroundApplication() {

        String currentApp = "NULL";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            currentApp = tasks.get(0).processName;
        }
        Log.i("package>>", currentApp);
        return currentApp;

    }

    public void usageAccessSettingsPage() {
      try {
          Intent intent = new Intent();
          intent.setAction(Settings.ACTION_USAGE_ACCESS_SETTINGS);
          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          if(intent.resolveActivity(getPackageManager()) != null){
              Uri uri = Uri.fromParts("package", this.getPackageName(), null);
              intent.setData(uri);
              DigitalWellBeingService.this.startActivity(intent);
          }



      }catch(Exception e){}
    }

    public static boolean compareDates(String d1, String d2) {
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date date1 = sdf.parse("01/06/2020 "+d1);
            Date date2 = sdf.parse("01/06/2020 "+d2);

            System.out.println("Date1" + sdf.format(date1));
            System.out.println("Date2" + sdf.format(date2));
            System.out.println();

            if (date1.getTime() < date2.getTime())
                return true;
            else
                return false;


        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return false;
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


}
