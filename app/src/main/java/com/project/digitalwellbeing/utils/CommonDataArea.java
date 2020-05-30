package com.project.digitalwellbeing.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.widget.Toolbar;

import com.google.firebase.messaging.RemoteMessage;

public class CommonDataArea {
    public static final String PHONENUMBER = "";
    public static final String DB_NAME = "DigitalWellBeing";
    public static String FIREBASETOPIC="";
    public static RemoteMessage remoteMessage=null;
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;
    public static String prefName="DigitalWellBeing";
    public static String isRegisterd="REGISTER";
    public static String PARENT="parent";

    public static final String regEx = "^\\w+@[a-zA-Z_]+?\\.[a-zA-Z]{2,3}$";


    public static Context context;
    public static String ISLOGIN="isLogin";
    public static String USERNAME="username";
    public static String ROLESTR="Role";
    public static int ROLE;
}
