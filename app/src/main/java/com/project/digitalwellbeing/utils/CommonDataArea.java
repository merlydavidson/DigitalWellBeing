package com.project.digitalwellbeing.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.messaging.RemoteMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonDataArea {
    public static final String PHONENUMBER = "";
    public static final String DB_NAME = "DigitalWellBeing";
    public static final String ISGOOGLESUCCESS = "googleFitSignIn";
    public static String FIREBASETOPIC="";
    public static String PARENT_UUID="";
    public static String CURRENTCHILDID="";
    public static RemoteMessage remoteMessage=null;
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;
    public static String prefName="DigitalWellBeing";
    public static String isRegisterd="REGISTER";
    public static String PARENT="parent";
    public static String APP_PACKAGE_NAME="com.project.digitalwellbeing";
    public static String LAUNCHER_PACKAGE_NAME="com.sec.android.app.launcher";
    public static String TASK="task";

    public static final String regEx = "[a-zA-Z0-9._-]+@[a-z]+\\\\.+[a-z]+"; /*"^\\w+@[a-zA-Z_]+?\\.[a-zA-Z]{2,3}$";*/


    public static Context context;
    public static String ISLOGIN="isLogin";
    public static String APP_BLOCK_PIN="app_block_pin";
    public static String BLOCKAPPS="isblocked";
    public static String USERNAME="username";
    public static String ROLESTR="Role";
    public static int ROLE;

    public static String getDAte(String format){
        DateFormat dateFormat=new SimpleDateFormat(format);
        Date date=new Date();
        return dateFormat.format(date);
    }
}
