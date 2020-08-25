package com.project.digitalwellbeing;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {
    Button resetButton;
    EditText resetEdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        resetButton = findViewById(R.id.reset_btn);
        resetEdt = findViewById(R.id.reset_edt);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!resetEdt.getText().toString().equalsIgnoreCase(""))
                    Toast.makeText(ForgotPasswordActivity.this, "Password reset link sent to your email successfully", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(ForgotPasswordActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();

            }
        });

    }
}
