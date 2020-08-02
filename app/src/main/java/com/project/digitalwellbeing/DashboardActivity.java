package com.project.digitalwellbeing;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.project.digitalwellbeing.data.model.AppDataBase;
import com.project.digitalwellbeing.data.model.DigitalWellBeingDao;
import com.project.digitalwellbeing.data.model.LockUnlock;
import com.project.digitalwellbeing.data.model.TaskDetails;
import com.project.digitalwellbeing.service.DigitalWellBeingService;
import com.project.digitalwellbeing.utils.BrowserObserver;
import com.project.digitalwellbeing.utils.CommonDataArea;
import com.project.digitalwellbeing.utils.CommonFunctionArea;
import com.project.digitalwellbeing.utils.Popup;
import com.project.digitalwellbeing.utils.TaskCompletedDialog;

import java.util.ArrayList;
import java.util.List;

import me.everything.providers.android.browser.BrowserProvider;
import me.everything.providers.android.browser.Search;
import me.everything.providers.core.Data;

import static com.project.digitalwellbeing.utils.CommonDataArea.editor;
import static com.project.digitalwellbeing.utils.CommonDataArea.sharedPreferences;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout pairKeyLinearLayout, taskLinearLayout, googleFitLinearLayout, webHistoryLinearLayout, callLogLinearLayout, locationLinearLayout, appUsageLinearLayout, recentActivitiesLinearLayout, lockDeviceLinearLayout;
    TextView pairKeytext, tasktext, googleFitText, webHistoryText, callLogText, locationText, appusageText, recentActivitiesText;
    LinearLayout lockDeviceText;
    List<TaskDetails> completedTaskList;
    private Toolbar toolbar;
    private BrowserObserver browserObserver;
    private DigitalWellBeingService mDigitalWellBeingService;
    private Intent mServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard2);
        sharedPreferences = getSharedPreferences(
                CommonDataArea.prefName, Context.MODE_PRIVATE);
        int role = sharedPreferences.getInt(CommonDataArea.ROLESTR, 0);
        String parent = sharedPreferences.getString(CommonDataArea.PARENT, "");
        if (role == 1) {
            CommonDataArea.CURRENTCHILDID = CommonFunctionArea.getDeviceUUID(this);
            CommonDataArea.PARENT_UUID = "/topics/" + parent;
            Log.d("CurrentTopic>>", CommonDataArea.PARENT_UUID);
        }
        /****************************************************************************/
        requestPermission();
        /****************************************************************************/


    }

    private void requestPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.READ_CALL_LOG,
                        // Manifest.permission.ACTIVITY_RECOGNITION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            // Toast.makeText(getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
                            doTasks();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
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

    public void usageAccessSettingsPage() {//permission for reading foreground task
        try {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (intent.resolveActivity(getPackageManager()) != null) {
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                this.startActivity(intent);
            }


        } catch (Exception e) {
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void doTasks() {
        if (!hasPermission()) {
            usageAccessSettingsPage();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                if (intent.resolveActivity(getPackageManager()) != null)
                    startActivityForResult(intent, 0);
            }
        }
        initViews();
        getAllData();

        new Handler().postDelayed(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void run() {
                mDigitalWellBeingService = new DigitalWellBeingService();
                mServiceIntent = new Intent(DashboardActivity.this, mDigitalWellBeingService.getClass());
                if (!isMyServiceRunning(mDigitalWellBeingService.getClass())) {
                    startService(mServiceIntent);
                }
                Log.d("I'mahandler", "hIiii>>");

            }
        }, 2000);

        showTaskCompletionDialog();
        pairKeytext.setText(CommonDataArea.CURRENTCHILDID);
    }

    private void showTaskCompletionDialog() {
        AppDataBase appDataBase = AppDataBase.getInstance(DashboardActivity.this);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        completedTaskList = new ArrayList<>();
        List<TaskDetails> taskDetails = digitalWellBeingDao.getCompletedTasks(CommonDataArea.getDAte("dd/MM/yyyy"), 0);
        for (TaskDetails t : taskDetails) {
            String taskEndTime = t.getDate() + " " + t.getEndtime();
            String currentTime = CommonDataArea.getDAte("dd/MM/yyyy HH:mm");

            if (CommonFunctionArea.compareDateTimes("dd/MM/yyyy HH:mm", taskEndTime, currentTime)) {

                completedTaskList.add(t);
            }
        }
        if (completedTaskList != null && completedTaskList.size() > 0) {
            new TaskCompletedDialog().showDialog(this, this, completedTaskList);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                logout();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        sharedPreferences = getSharedPreferences(
                CommonDataArea.prefName, Context.MODE_PRIVATE);
        CommonDataArea.editor = sharedPreferences.edit();
        editor.putBoolean(CommonDataArea.ISLOGIN, false);
        editor.commit();
        finish();
        startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
    }

    public void getAllData() {
        final Uri BOOKMARKS_URI = Uri.parse("content://com.android.chrome.browser/bookmarks");
        CommonDataArea.FIREBASETOPIC = "/topics/" + CommonFunctionArea.getDeviceUUID(this);
        new CommonFunctionArea().subscribeTopic();
        sharedPreferences = getSharedPreferences(
                CommonDataArea.prefName, Context.MODE_PRIVATE);
        CommonDataArea.editor = sharedPreferences.edit();
        CommonDataArea.ROLE = sharedPreferences.getInt(CommonDataArea.ROLESTR, 0);
        //    browserObserver = new BrowserObserver(new Handler(),DashboardActivity.this);
//        getContentResolver().registerContentObserver(BOOKMARKS_URI, true, browserObserver);
    }

    public void initViews() {

        toolbar = findViewById(R.id.dashboard_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle("Digital Well Being");
        toolbar.setTitleTextColor(getResources().getColor(R.color.dark_bg));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        pairKeyLinearLayout = (LinearLayout) findViewById(R.id.uuid_layout);
        taskLinearLayout = (LinearLayout) findViewById(R.id.task_layout);
        googleFitLinearLayout = (LinearLayout) findViewById(R.id.google_fit_layout);
        callLogLinearLayout = (LinearLayout) findViewById(R.id.call_layout);
      //  webHistoryLinearLayout = (LinearLayout) findViewById(R.id.web_layout);
        locationLinearLayout = (LinearLayout) findViewById(R.id.location_layout);
        appUsageLinearLayout = (LinearLayout) findViewById(R.id.apps_layout);
        recentActivitiesLinearLayout = (LinearLayout) findViewById(R.id.log_layout);
        lockDeviceLinearLayout = (LinearLayout) findViewById(R.id.lock_layout);

        if (CommonDataArea.ROLE == 1)
            lockDeviceLinearLayout.setVisibility(View.GONE);

        pairKeyLinearLayout.setOnClickListener(this);
        taskLinearLayout.setOnClickListener(this);
        googleFitLinearLayout.setOnClickListener(this);
        callLogLinearLayout.setOnClickListener(this);
//        webHistoryLinearLayout.setOnClickListener(this);
        locationLinearLayout.setOnClickListener(this);
        appUsageLinearLayout.setOnClickListener(this);
        recentActivitiesLinearLayout.setOnClickListener(this);
        lockDeviceLinearLayout.setOnClickListener(this);


        pairKeytext = (TextView) findViewById(R.id.txt_pair_key);
        tasktext = (TextView) findViewById(R.id.txt_task);
        googleFitText = (TextView) findViewById(R.id.txt_google_fit);
        callLogText = (TextView) findViewById(R.id.txt_call);
        // webHistoryText = (TextView) findViewById(R.id.txt_web);
        locationText = (TextView) findViewById(R.id.txt_location);
        appusageText = (TextView) findViewById(R.id.txt_app);
        recentActivitiesText = (TextView) findViewById(R.id.txt_log);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.uuid_layout:
                break;
            case R.id.task_layout:
                Intent intent = new Intent(DashboardActivity.this, TaskActivity.class);
                startActivity(intent);
                break;
            case R.id.google_fit_layout:
                Intent googleIntent = new Intent(DashboardActivity.this, GoogleFit.class);
                startActivity(googleIntent);
                break;
            case R.id.call_layout:
                Intent callIntent = new Intent(DashboardActivity.this, ContactListActivity.class);
                startActivity(callIntent);
                break;
            //case R.id.web_layout:
            //  getWebHistory();
            // break;
            case R.id.location_layout:
                Intent locationIntent = new Intent(DashboardActivity.this, LocationActivity.class);
                startActivity(locationIntent);
                break;
            case R.id.apps_layout:
                if (CommonDataArea.ROLE == 1) {
                    if (!getAppUsagePermissionStatus()) {
                        new Popup(DashboardActivity.this).doubleChoice("App Usage Permission", "You need to allow App usage permission", 2, DashboardActivity.this);
                    } else {
                        Intent appUsageIntent = new Intent(DashboardActivity.this, AppusageActivity.class);
                        startActivity(appUsageIntent);
                    }
                } else {
                    Intent appUsageIntent = new Intent(DashboardActivity.this, AppusageActivity.class);
                    startActivity(appUsageIntent);
                }
                break;
            case R.id.log_layout:
                Intent log = new Intent(DashboardActivity.this, RecentActivity.class);
                startActivity(log);
                break;
            case R.id.lock_layout:
                ViewDialog alert = new ViewDialog();
                alert.showDialog(this, this);
                break;

        }
    }

    private void getWebHistory() {
        BrowserProvider browserProvider = new BrowserProvider(this);
        Data<Search> data = browserProvider.getSearches();
        List<Search> history = data.getList();
        for (Search s : history) {
            String uri = s.search;
            long date = s.date;

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //  getContentResolver().unregisterContentObserver(browserObserver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public boolean checkPermission() {
        boolean check = true;

        String[] stringPerm = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG};
        for (String permis : stringPerm) {
            if (!(ActivityCompat.checkSelfPermission(this, permis) == PackageManager.PERMISSION_GRANTED)) {

                check = false;
            }
        }

        if (!check)
            ActivityCompat.requestPermissions(this, stringPerm, 1);
        return check;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        String permissionTxt = "";
        if (permissions.length == 0) {
            return;
        }
        try {


            boolean allPermissionsGranted = true;
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        allPermissionsGranted = false;
                        break;
                    }
                }
            }
            if (!allPermissionsGranted) {
                boolean somePermissionsForeverDenied = false;
                for (String permission : permissions) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                        //denied
                        Log.e("denied", permission);
                    } else {
                        if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                            //allowed
                            Log.e("allowed", permission);
                        } else {
                            if (permission.equalsIgnoreCase("android.permission.ACCESS_COARSE_LOCATION")) {
                                if (permissionTxt.equalsIgnoreCase("")) {
                                    permissionTxt += "Location";
                                } else {
                                    permissionTxt += ",Location";
                                }
                            }
//                            if (permission.equalsIgnoreCase("android.permission.WRITE_EXTERNAL_STORAGE")) {
//                                if (permissionTxt.equalsIgnoreCase("")) {
//                                    permissionTxt += "Storage";
//                                } else {
//                                    permissionTxt += ",Storage";
//                                }
//                            }
                            if (permission.equalsIgnoreCase("android.permission.READ_CONTACTS")) {
                                if (permissionTxt.equalsIgnoreCase("")) {
                                    permissionTxt += "Read Contacts";
                                } else {
                                    permissionTxt += ",Read Contacts";
                                }
                            }
                            if (permission.equalsIgnoreCase("android.permission.READ_CALL_LOG")) {
                                if (permissionTxt.equalsIgnoreCase("")) {
                                    permissionTxt += "Call Log";
                                } else {
                                    permissionTxt += ",Call Log";
                                }
                            }
                            //set to never ask again
                            Log.e("set to never ask again", permission);
                            somePermissionsForeverDenied = true;
                        }
                    }
                }
                if (somePermissionsForeverDenied) {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setTitle(R.string.permissionReq)
                            .setMessage(getResources().getString(R.string.permissionAccess) + "\n\n" + getResources().getString(R.string.permission) + "(" + permissionTxt + ")")
                            .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                            Uri.fromParts("package", getPackageName(), null));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setCancelable(false)
                            .create()
                            .show();
                }
            } else {

                CommonDataArea.editor.putBoolean("Locperm", true);
                CommonDataArea.editor.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean getAppUsagePermissionStatus() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("Service status", "Running");
                return true;
            }
        }
        Log.i("Service status", "Not running");
        return false;
    }

    public class ViewDialog {

        public void showDialog(Activity activity, Context context) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.lock_app_dialog);
            dialog.setCancelable(true);
            EditText text = (EditText) dialog.findViewById(R.id.password);
            sharedPreferences = getSharedPreferences(
                    CommonDataArea.prefName, Context.MODE_PRIVATE);
            CommonDataArea.editor = sharedPreferences.edit();

            Button dialogButton = (Button) dialog.findViewById(R.id.block);
            AppDataBase appDataBase = AppDataBase.getInstance(context);
            DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
            LockUnlock lockUnlock = digitalWellBeingDao.getLockUnlockDetails(CommonDataArea.CURRENTCHILDID);
            // boolean  isBlocked = sharedPreferences.getBoolean(BLOCKAPPS, false);
            boolean isBlocked = false;
            if (lockUnlock != null) {
                isBlocked = lockUnlock.isLocked();
                if (isBlocked)
                    dialogButton.setText("Unblock");
                else
                    dialogButton.setText("Block");
            }

            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialogButton.getText().toString().equalsIgnoreCase("Block")) {
                        int role = sharedPreferences.getInt(CommonDataArea.ROLESTR, 0);
                        if (role == 0) {
                            if (text.getText().toString() != null && text.getText().toString().length() > 0) {
                                if (digitalWellBeingDao.LockUnLock(CommonDataArea.CURRENTCHILDID)) {
                                    digitalWellBeingDao.updateLockUnlock(CommonDataArea.CURRENTCHILDID,
                                            true, text.getText().toString(), "0");
                                    dialogButton.setText("Unblock");
                                    Toast.makeText(DashboardActivity.this, "Blocked successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    LockUnlock unlock = new LockUnlock();
                                    unlock.setChildId(CommonDataArea.CURRENTCHILDID);
                                    unlock.setPassword(text.getText().toString());
                                    unlock.setLocked(true);
                                    unlock.setAcknowledgement("0");
                                    digitalWellBeingDao.insertLockUnlockData(unlock);
                                    dialogButton.setText("Unblock");
                                }

                            }
                        } else {
                            Toast.makeText(DashboardActivity.this, "Not allowed", Toast.LENGTH_SHORT).show();
                        }
                    } else if (dialogButton.getText().toString().equalsIgnoreCase("Unblock")) {

                        if (digitalWellBeingDao.LockUnLock(CommonDataArea.CURRENTCHILDID)) {
                            if (digitalWellBeingDao.getLockUnlockDetails(CommonDataArea.CURRENTCHILDID).getPassword().
                                    equalsIgnoreCase(text.getText().toString())) {
                                digitalWellBeingDao.updateLockUnlock(CommonDataArea.CURRENTCHILDID, false, "", "0");

                                dialogButton.setText("Block");
                            }
                        }

                        // editor.commit();
                        Toast.makeText(DashboardActivity.this, "Unlocked successfully", Toast.LENGTH_SHORT).show();

                    }
                    dialog.dismiss();
                }
            });

            dialog.show();

        }
    }

}
