package com.project.digitalwellbeing;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.digitalwellbeing.adapter.GenericAdapter;
import com.project.digitalwellbeing.data.model.AppDataBase;
import com.project.digitalwellbeing.data.model.CallDetails;
import com.project.digitalwellbeing.data.model.DigitalWellBeingDao;
import com.project.digitalwellbeing.utils.CommonDataArea;

import java.util.Date;
import java.util.List;

import static com.project.digitalwellbeing.utils.CommonDataArea.context;

public class ContactListActivity extends AppCompatActivity {
    static ProgressBar progressBar;
    RecyclerView callLogRecycler;
    private LinearLayoutManager layoutManager;
    private GenericAdapter mAdapter;

    public  CallDetails getLastCallDetails(Context context) {

        CallDetails callDetails = new CallDetails();

        Uri contacts = CallLog.Calls.CONTENT_URI;
        try {

            Cursor managedCursor = context.getContentResolver().query(contacts, null, null, null, android.provider.CallLog.Calls.DATE + " DESC ;");

            if (managedCursor != null) {
                if (managedCursor.moveToFirst()) { // added line

                    while (managedCursor.moveToNext()) {

                        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
                        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
                        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
                        int id = managedCursor.getColumnIndex(CallLog.Calls._ID);
                        int type = managedCursor.getColumnIndex(String.valueOf(CallLog.Calls.INCOMING_TYPE));
                        String callType;
                        int idNumber = managedCursor.getInt(id);
                        String phNumber = managedCursor.getString(number);
                        String callerName = getContactName(phNumber, context);
//                        if (incomingtype == -1) {
//                            callType = "incoming";
//                        } else {
//                            callType = "outgoing";
//                        }
                        String callDate = managedCursor.getString(date);
                        String callDayTime = new Date(Long.valueOf(callDate)).toString();

                        String callDuration = managedCursor.getString(duration);

                        callDetails.setCallerName(callerName);
                        callDetails.setCallerNumber(phNumber);
                        callDetails.setCallDuration(callDuration);
                        callDetails.setCallType(type);
                        callDetails.setCallTimeStamp(callDayTime);
                        callDetails.setCallerLogId(idNumber);
                        if (!getCallEntry(idNumber)) {
                            insertCallDetails(callDetails);
                        }

                    }
                }

            }
            managedCursor.close();

        } catch (SecurityException e) {
            Log.e("Security Exception", "User denied call log permission");

        }

        return callDetails;
    }

    public static String getContactName(final String phoneNumber, Context context) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        String contactName = "";
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(0);
            }
            cursor.close();
        }

        return contactName;
    }

    public void insertCallDetails(CallDetails callDetails) {
        AppDataBase appDataBase = AppDataBase.getInstance(ContactListActivity.this);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        digitalWellBeingDao.insertCallDetails(callDetails);
    }

    public boolean getCallEntry(int callId) {
        boolean isExist = false;

        AppDataBase appDataBase = AppDataBase.getInstance(ContactListActivity.this);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        List<CallDetails> callDetails = digitalWellBeingDao.getaCallDetails(callId);
        if (callDetails.size() > 0)
            isExist = true;
        return isExist;

    }

    public List<CallDetails> getcallDetails() {
        AppDataBase appDataBase = AppDataBase.getInstance(ContactListActivity.this);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        List<CallDetails> callDetails = digitalWellBeingDao.getCallDetails();

        return callDetails;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (CommonDataArea.ROLE == 1)
                    getLastCallDetails(ContactListActivity.this);
            }
        }).start();

        callLogRecycler = findViewById(R.id.genericRecyclerLayout);
        RecyclerView recyclerViewCall = (RecyclerView) findViewById(R.id.genericRecyclerLayout);
        recyclerViewCall.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerViewCall.setLayoutManager(layoutManager);
        mAdapter = new GenericAdapter(getcallDetails(), ContactListActivity.this);
        recyclerViewCall.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();


    }
}
