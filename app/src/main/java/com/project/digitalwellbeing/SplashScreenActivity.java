package com.project.digitalwellbeing;

import android.annotation.SuppressLint;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.project.digitalwellbeing.utils.CommonDataArea;

import static com.project.digitalwellbeing.utils.CommonDataArea.prefName;
import static com.project.digitalwellbeing.utils.CommonDataArea.sharedPreferences;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        sharedPreferences = getSharedPreferences(
                CommonDataArea.prefName, Context.MODE_PRIVATE);
        int role = sharedPreferences.getInt(CommonDataArea.ROLESTR, 0);
        CommonDataArea.editor = sharedPreferences.edit();

        new Handler().postDelayed(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void run() {
                if (sharedPreferences.getBoolean(CommonDataArea.ISLOGIN, false)) {
                    if (role == 0) {
                        Intent intent = new Intent(SplashScreenActivity.this, DashboardActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(SplashScreenActivity.this, GoogleFit.class);
                        startActivity(intent);
                    }
                    finish();
                } else {
                    Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        }, 2000);
    }
}