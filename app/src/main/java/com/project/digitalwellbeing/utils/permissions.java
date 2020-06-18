package com.project.digitalwellbeing.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class permissions {
    Context context;
    Activity activity;

    public permissions(Context context, Activity activity) {
        this.context = context;
        this.activity=activity;
    }

    public boolean checkLocationPermission() {
        boolean check = true;

        String[] stringPerm = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.READ_CALL_LOG,Manifest.permission.READ_CONTACTS};
        for (String permis : stringPerm) {
            if (!(ActivityCompat.checkSelfPermission(activity, permis) == PackageManager.PERMISSION_GRANTED)) {

                check = false;
            }
        }

        ActivityCompat.requestPermissions(activity, stringPerm, 1);


        return check;

    }

}
