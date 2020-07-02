package com.project.digitalwellbeing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.project.digitalwellbeing.data.model.AppDataBase;
import com.project.digitalwellbeing.data.model.DigitalWellBeingDao;
import com.project.digitalwellbeing.data.model.UserInfo;
import com.project.digitalwellbeing.utils.CommonDataArea;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.project.digitalwellbeing.utils.CommonDataArea.editor;
import static com.project.digitalwellbeing.utils.CommonDataArea.sharedPreferences;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {
    private static View view;
    private EditText fullName;
    private EditText emailId;
    private EditText mobileNumber;
    private EditText password;
    private EditText confirmPassword;
    private Button signUpButton;
    private TextView login;
    private CheckBox terms_conditions;
    private RadioGroup radioGroup;
    UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        userInfo = new UserInfo();
        initViews();
        setListeners();
    }


    private void initViews() {
        fullName = (EditText) findViewById(R.id.fullName);
        emailId = (EditText) findViewById(R.id.userEmailId);
        mobileNumber = (EditText) findViewById(R.id.mobileNumber);
        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirmPassword);
        signUpButton = (Button) findViewById(R.id.signUpBtn);
        login = (TextView) findViewById(R.id.already_user);
        terms_conditions = (CheckBox) findViewById(R.id.terms_conditions);
        radioGroup = (RadioGroup) findViewById(R.id.role);


    }


    // Set Listeners
    private void setListeners() {
        signUpButton.setOnClickListener(this);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signUpBtn:

                // Call checkValidation method
                checkValidation();
                break;

            case R.id.already_user:
                finish();
                break;
        }

    }

    private void checkValidation() {

        // Get all edittext texts
        String getFullName = fullName.getText().toString();
        String getEmailId = emailId.getText().toString();
        String getMobileNumber = mobileNumber.getText().toString();
        String getPassword = password.getText().toString();
        String getConfirmPassword = confirmPassword.getText().toString();
        int role = radioGroup.getCheckedRadioButtonId();

        // Pattern match for email id
        Pattern p = Pattern.compile(CommonDataArea.regEx);
        Matcher m = p.matcher(getEmailId);

        // Check if all strings are null or not
        if (getFullName.equals("") || getFullName.length() == 0
                || getEmailId.equals("") || getEmailId.length() == 0
                || getMobileNumber.equals("") || getMobileNumber.length() == 0
                || getPassword.equals("") || getPassword.length() == 0
                || getConfirmPassword.equals("")
                || getConfirmPassword.length() == 0)

            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();

            //TODO:// Check if email id valid or not
       /* else if (!getEmailId.matches(CommonDataArea.regEx))
            Toast.makeText(this, "Invalid EmailID", Toast.LENGTH_SHORT).show();*/


            // Check if both password should be equal
        else if (!getConfirmPassword.equals(getPassword))
            Toast.makeText(this, "Password mismatch", Toast.LENGTH_SHORT).show();


            // Make sure user should check Terms and Conditions checkbox
        else if (!terms_conditions.isChecked())
            Toast.makeText(this, "Please select terms and conditions", Toast.LENGTH_SHORT).show();


            // Else do signup or do your stuff
        else {
            userInfo.setUsername(getFullName);
            userInfo.setEmail(getEmailId);
            userInfo.setPassword(getPassword);
            userInfo.setPhoneNumber(getMobileNumber);
            sharedPreferences = getSharedPreferences(
                    CommonDataArea.prefName, Context.MODE_PRIVATE);
            CommonDataArea.editor = sharedPreferences.edit();
            editor.putBoolean(CommonDataArea.ISLOGIN, true);
            editor.putString(CommonDataArea.USERNAME, getFullName);
            editor.putString(CommonDataArea.PHONENUMBER, getMobileNumber);
            if (role == R.id.role_parent) {
                editor.putInt(CommonDataArea.ROLESTR, 0);

                userInfo.setRole(0);
            } else {
                editor.putInt(CommonDataArea.ROLESTR, 1);
                userInfo.setRole(1);
            }
            AppDataBase appDataBase = AppDataBase.getInstance(SignupActivity.this);
            DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
            digitalWellBeingDao.insertUserInfo(userInfo);
            editor.commit();

            if (role == R.id.role_parent) {
                Intent intent = new Intent(SignupActivity.this, ChildActivity.class);
                startActivity(intent);
            } else {

                Intent intent = new Intent(SignupActivity.this, DashboardActivity.class);
                startActivity(intent);

            }
        }


    }
}
