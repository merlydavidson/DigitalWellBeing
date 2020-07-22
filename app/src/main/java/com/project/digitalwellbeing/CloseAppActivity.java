package com.project.digitalwellbeing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.project.digitalwellbeing.data.model.AppDataBase;
import com.project.digitalwellbeing.data.model.DigitalWellBeingDao;
import com.project.digitalwellbeing.data.model.LockUnlock;
import com.project.digitalwellbeing.service.DigitalWellBeingService;
import com.project.digitalwellbeing.utils.CommonDataArea;

import static com.project.digitalwellbeing.utils.CommonDataArea.*;

//launcher
public class CloseAppActivity extends AppCompatActivity {
    TextView title;
    EditText pin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_close_app);
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        String action = b.getString(TASK);
        Button close = findViewById(R.id.close);
        title=findViewById(R.id.title);
        pin=findViewById(R.id.pin);
        if (action.equalsIgnoreCase("tasks")) {
            title.setText("You have no access over other apps while a task is pending");
            close.setText("Close");
            pin.setVisibility(View.GONE);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startMain);
                }
            });
        }else if(action.equalsIgnoreCase("lock")){
            title.setText("Please enter the pin to unlock apps");
            close.setText("Unlock");
            pin.setVisibility(View.VISIBLE);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sharedPreferences = getSharedPreferences(
                            CommonDataArea.prefName, Context.MODE_PRIVATE);
                    String pinno = sharedPreferences.getString(APP_BLOCK_PIN, "0");
                    AppDataBase appDataBase = AppDataBase.getInstance(CloseAppActivity.this);
                    DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
                    LockUnlock lockUnlock = digitalWellBeingDao.getLockUnlockDetails(CommonDataArea.CURRENTCHILDID);
                    if(lockUnlock.getPassword().equals(pin.getText().toString())){
                        digitalWellBeingDao.updateLockUnlock(CommonDataArea.CURRENTCHILDID, false, "");

                        finish();
                        Toast.makeText(CloseAppActivity.this, "Apps unlocked successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        pin.setError("Wrong PIN");
                        Toast.makeText(CloseAppActivity.this, "Wrong PIN", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            });
        }else if(action.equalsIgnoreCase("block_selected_apps")){
            pin.setVisibility(View.GONE);
            title.setText("Use of current app is restricted by the parent");
            close.setText("Close");
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startMain);
                }
            });
        }
    }
}
