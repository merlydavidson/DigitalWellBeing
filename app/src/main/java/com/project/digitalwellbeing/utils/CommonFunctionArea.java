package com.project.digitalwellbeing.utils;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.project.digitalwellbeing.data.model.AppDataBase;
import com.project.digitalwellbeing.data.model.DigitalWellBeingDao;
import com.project.digitalwellbeing.data.model.LogDetails;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.project.digitalwellbeing.utils.CommonDataArea.sharedPreferences;

public class CommonFunctionArea {

    public static String getDeviceUUID(Context context) {
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidId;
    }

    public static String getparentId(Context context) {
        sharedPreferences = context.getSharedPreferences(
                CommonDataArea.prefName, Context.MODE_PRIVATE);
        return sharedPreferences.getString(CommonDataArea.PARENT, "");
    }

    public static int getRole(Context context) {
        sharedPreferences = context.getSharedPreferences(
                CommonDataArea.prefName, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(CommonDataArea.ROLESTR, 0);
    }

    public void subscribeTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic(CommonDataArea.FIREBASETOPIC);

    }

    public boolean getDeviceLocked(Context context) {
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        return km.inKeyguardRestrictedInputMode();
    }

    public String getTimeStamp() {
        return new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(Calendar.getInstance().getTime());
    }

    public LogDetails getLogList(Context context,String id) {
        List<LogDetails> logDetails = null;
        LogDetails logDetails1 = null;
        try {
            AppDataBase appDataBase = AppDataBase.getInstance(context);
            DigitalWellBeingDao stimulationSessionsDao = appDataBase.userDetailsDao();
            logDetails = stimulationSessionsDao.getLogDetails(id);
            logDetails1 = logDetails.get(logDetails.size() - 1);
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
        return logDetails1;
    }

    public long getTimeStamp(String dateParse) throws ParseException {
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date = (Date) formatter.parse(dateParse);
        System.out.println("Today is " + date.getTime());

        return date.getTime();
    }

    public static void getBrowserHistory(Context context) {
        Log.d("Merly", "sfsd");

        final Uri BOOKMARKS_URI = Uri.parse("content://com.android.chrome.browser/bookmarks");
        final String[] HISTORY_PROJECTION = new String[]
                {
                        "_id", // 0
                        "url", // 1
                        "visits", // 2
                        "date", // 3
                        "bookmark", // 4
                        "title", // 5
                        "favicon", // 6
                        "thumbnail", // 7
                        "touch_icon", // 8
                        "user_entered", // 9
                };

        Cursor mCur = context.getContentResolver().query(BOOKMARKS_URI, HISTORY_PROJECTION, null, null, null);
        // context.start
        mCur.moveToFirst();

        String title = "";
        String url = "";
        String date = "";

        if (mCur.moveToFirst() && mCur.getCount() > 0) {
            while (mCur.isAfterLast() == false) {

                title = mCur.getString(mCur.getColumnIndex("title"));
                url = mCur.getString(mCur.getColumnIndex("url"));
                date = mCur.getString(mCur.getColumnIndex("date"));

                Log.d("Merly", title);
                Log.d("Merly", url);
                Log.d("Merly", date);

                mCur.moveToNext();
            }
        }

    }

    public static class FetchCategoryTask extends AsyncTask<Void, Void, Void> {
Context context;
        private final String TAG = FetchCategoryTask.class.getSimpleName();
        private PackageManager pm;
        public final static String GOOGLE_URL = "https://play.google.com/store/apps/details?id=";
        public static final String ERROR = "error";

        public FetchCategoryTask(Context context) {
            this.context = context;
        }


        @Override
        protected Void doInBackground(Void... errors) {
            String category;
            pm = context.getPackageManager();
            List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
            Iterator<ApplicationInfo> iterator = packages.iterator();
            while (iterator.hasNext()) {
                ApplicationInfo packageInfo = iterator.next();
                String query_url = GOOGLE_URL + packageInfo.packageName;
                Log.i(TAG, query_url);
                category = getCategory(query_url);
                // store category or do something else
            }
            return null;
        }


        private String getCategory(String query_url) {
            try {
                Document doc =  Jsoup.connect(query_url).get();
                Elements link = doc.select("a[class=\"hrTbp R8zArc\"]");
                Log.d("Merly","Category "+link.text());
                return link.text();
            } catch (Exception e) {
                Log.e("DOc", e.toString());
            }
            return "";
        }
    }
    public static boolean compareTimes(String d1, String d2) {
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date date1 = sdf.parse("01/06/2020 " + d1);
            Date date2 = sdf.parse("01/06/2020 " + d2);

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

    public static boolean compareDateTimes(String format,String d1, String d2) {
        try {

            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date date1 = sdf.parse(d1);
            Date date2 = sdf.parse(d2);

            System.out.println("Date1" + sdf.format(date1));
            System.out.println("Date2" + sdf.format(date2));
            System.out.println();

            if (date1.getTime() <= date2.getTime())
                return true;
            else
                return false;


        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    public static String foregroundApplication(Context context) {

        String currentApp = "NULL";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
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
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            currentApp = tasks.get(0).processName;
        }
        Log.i("package>>", currentApp);
        return currentApp;

    }
}
