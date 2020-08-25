package com.project.digitalwellbeing;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.project.digitalwellbeing.data.model.AppDataBase;
import com.project.digitalwellbeing.data.model.DigitalWellBeingDao;
import com.project.digitalwellbeing.data.model.UserInfo;
import com.project.digitalwellbeing.utils.CommonDataArea;

import static com.project.digitalwellbeing.utils.CommonDataArea.editor;
import static com.project.digitalwellbeing.utils.CommonDataArea.sharedPreferences;

public class LoginActivity extends AppCompatActivity {
    Button loginButton, signupButton;
    EditText phNo, password;
    TextView forgotButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        loginButton = (Button) findViewById(R.id.login_btn);
        signupButton = (Button) findViewById(R.id.signup_btn);
        phNo = findViewById(R.id.mob_edt);
        password = findViewById(R.id.passwd_edt);
        forgotButton = findViewById(R.id.frgt_psswd_txt);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
        forgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,ForgotPasswordActivity.class));

            }
        });
        // new CommonFunctionArea().sendMessage(LoginActivity.this);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phNo.getText().toString().length() < 0) {
                    phNo.setError("Enter phone no ");

                } else if (password.getText().toString().length() < 0) {
                    password.setError("Enter password");

                } else if (phNo.getText().toString() != null && phNo.getText().length() > 0 && password.getText().toString() != null && password.getText().length() > 0) {
                    AppDataBase appDataBase = AppDataBase.getInstance(LoginActivity.this);
                    DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
                    if (digitalWellBeingDao.checkLoginppDetails(phNo.getText().toString().trim(), password.getText().toString().trim())) {
                        UserInfo user = digitalWellBeingDao.getUserData(phNo.getText().toString().trim(), password.getText().toString().trim());

                        sharedPreferences = getSharedPreferences(
                                CommonDataArea.prefName, Context.MODE_PRIVATE);
                        CommonDataArea.editor = sharedPreferences.edit();
                        editor.putBoolean(CommonDataArea.ISLOGIN, true);
                        editor.putString(CommonDataArea.USERNAME, user.getUsername());
                        editor.putString(CommonDataArea.PHONENUMBER, user.getPhoneNumber());
                        editor.putInt(CommonDataArea.ROLESTR, user.getRole());
                        editor.commit();
                        if (user.getRole() == 1)
                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                        else if (user.getRole() == 0) {
                            Intent intent = new Intent(LoginActivity.this, ChildActivity.class);
                            startActivity(intent);
                        }
                        finish();
                    }
                }
            }
        });
    }
}
