package com.project.digitalwellbeing.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.firebase.messaging.RemoteMessage;
import com.project.digitalwellbeing.R;
import com.project.digitalwellbeing.data.FCMActions;
import com.project.digitalwellbeing.remote.Communicator;

public class Popup extends Activity {
    TextView popupTitle, popupMessage;
    Button btnOK, yesBtn, noBtn;
    EditText pairEdit;
    Context context;

    public Popup(Context context) {
        this.context = context;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void singleChoice(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_single_view, null);
        popupTitle = (TextView) layout.findViewById(R.id.title);
        popupMessage = (TextView) layout.findViewById(R.id.message);
        btnOK = (Button) layout.findViewById(R.id.okbtn);
        popupTitle.setText(title);
        popupMessage.setText(message);
        builder.setView(layout);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }

    public void doubleChoice(String title, String message, final RemoteMessage remoteMessage, final int callingFrom) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_double_view, null);
        popupTitle = (TextView) layout.findViewById(R.id.dtitle);
        popupMessage = (TextView) layout.findViewById(R.id.dmessage);
        yesBtn = (Button) layout.findViewById(R.id.dyesbtn);
        noBtn = (Button) layout.findViewById(R.id.dnobtn);
        popupTitle.setText(title);
        popupMessage.setText(message);
        builder.setView(layout);
        final AlertDialog alertDialog = builder.create();

//        new Handler().postDelayed(new Runnable() {
//            public void run() {
                alertDialog.show();
//            }
//        }, 100);

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callingFrom == 1) {
                    CommonDataArea.context = Popup.this;
                    new FCMActions().registerParent(remoteMessage);
                }
            }
        });
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    public void SingleEditTextChoice(String title, final int callingFrom, final Context contextCurr) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_edit_view, null);
        popupTitle = (TextView) layout.findViewById(R.id.dtitle);
        pairEdit = (EditText) layout.findViewById(R.id.edt_reg_uuid);
        yesBtn = (Button) layout.findViewById(R.id.dyesbtn);
        noBtn = (Button) layout.findViewById(R.id.dnobtn);
        popupTitle.setText(title);
        builder.setView(layout);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callingFrom == 1) {
                    CommonDataArea.FIREBASETOPIC = "/topics/" + pairEdit.getText();


                    new Communicator(contextCurr).sendMessage(new FCMMessages(context).registerMessage());
                    alertDialog.dismiss();
                }
            }
        });
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }


}
