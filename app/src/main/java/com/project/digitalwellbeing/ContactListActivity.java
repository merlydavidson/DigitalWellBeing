package com.project.digitalwellbeing;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.digitalwellbeing.adapter.GenericAdapter;
import com.project.digitalwellbeing.data.model.AppDataBase;
import com.project.digitalwellbeing.data.model.CallDetails;
import com.project.digitalwellbeing.data.model.DigitalWellBeingDao;
import com.project.digitalwellbeing.data.model.TaskDetails;
import com.project.digitalwellbeing.remote.Communicator;
import com.project.digitalwellbeing.utils.CommonDataArea;
import com.project.digitalwellbeing.utils.CommonFunctionArea;
import com.project.digitalwellbeing.utils.FCMMessages;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ContactListActivity extends AppCompatActivity {
    static ProgressBar progressBar;
    ProgressDialog progress;
    RecyclerView callLogRecycler;
    private LinearLayoutManager layoutManager;
    private GenericAdapter mAdapter;
    Button go;
    EditText dateFrom,dateTo;
    private DatePickerDialog picker;
    RecyclerView recyclerViewCall;
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
       // new Communicator(getApplicationContext()).sendMessage(FCMMessages.sendLogs(cityName, CommonDataArea.getDAte("dd/MM/yyyy HH:mm"), new CommonFunctionArea().getDeviceLocked(getApplicationContext()),currentForegrounApp));

    }

    public boolean getCallEntry(int callId) {
        boolean isExist = false;

        AppDataBase appDataBase = AppDataBase.getInstance(ContactListActivity.this);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        List<CallDetails> callDetails = digitalWellBeingDao.getaCallDetails(callId,CommonDataArea.CURRENTCHILDID);
        if (callDetails.size() > 0)
            isExist = true;
        return isExist;

    }

    public List<CallDetails> getcallDetails() {
        AppDataBase appDataBase = AppDataBase.getInstance(ContactListActivity.this);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        List<CallDetails> callDetails = digitalWellBeingDao.getCallDetails2(CommonDataArea.CURRENTCHILDID);

        return callDetails;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_layout);
        progress = ProgressDialog.show(this, "Loading..",
                "Please wait...", true);
        progress.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (CommonDataArea.ROLE == 1)
                   // getLastCallDetails(ContactListActivity.this);
                    getCallDetails();
            }
        }).start();
        dateFrom=findViewById(R.id.date_from);
        dateTo=findViewById(R.id.date_to);
        go=findViewById(R.id.result);
        dateFrom.setText(CommonDataArea.getDAte("dd/MM/yyyy"));
        dateTo.setText(CommonDataArea.getDAte("dd/MM/yyyy"));
        callLogRecycler = findViewById(R.id.genericRecyclerLayout);
         recyclerViewCall = (RecyclerView) findViewById(R.id.genericRecyclerLayout);
        recyclerViewCall.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerViewCall.setLayoutManager(layoutManager);
        mAdapter = new GenericAdapter(getcallDetails(), ContactListActivity.this, 1);
        recyclerViewCall.setAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();
        dateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(ContactListActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                if(monthOfYear<10 && dayOfMonth<10)
                                    dateTo.setText("0"+dayOfMonth + "/" +"0"+(monthOfYear + 1) + "/" + year);
                                else if(dayOfMonth<10)
                                    dateTo.setText("0"+dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                else if(monthOfYear<10)
                                    dateTo.setText(dayOfMonth + "/" +"0"+ (monthOfYear + 1) + "/" + year);
                                else
                                    dateTo.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });
        dateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(ContactListActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                if(monthOfYear<10 && dayOfMonth<10)
                                    dateFrom.setText("0"+dayOfMonth + "/" +"0"+(monthOfYear + 1) + "/" + year);
                                else if(dayOfMonth<10)
                                    dateFrom.setText("0"+dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                else if(monthOfYear<10)
                                    dateFrom.setText(dayOfMonth + "/" +"0"+ (monthOfYear + 1) + "/" + year);
                                else
                                    dateFrom.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<CallDetails> sortedList=new ArrayList<>();
                String datefrom=dateFrom.getText().toString()+" 01:00";
                String dateto=dateTo.getText().toString()+" 23:59";
                if(CommonFunctionArea.compareDateTimes("dd/MM/yyyy",datefrom,dateto)){
                    List<CallDetails> details=getcallDetails();
                    for(CallDetails d:details){
                        if(CommonFunctionArea.compareDateTimes("dd/MM/yyyy HH:mm",datefrom,d.getCallTimeStamp()) &&
                                CommonFunctionArea.compareDateTimes("dd/MM/yyyy HH:mm",d.getCallTimeStamp(),dateto)){
                            sortedList.add(d);
                        }
                    }
                    mAdapter = new GenericAdapter(sortedList, ContactListActivity.this, 1);
                    recyclerViewCall.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(ContactListActivity.this, "Date from must be less than date to..", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter = new GenericAdapter(getcallDetails(), ContactListActivity.this, 1);
        recyclerViewCall.setAdapter(mAdapter);
        progress.dismiss();
    }

    private void getCallDetails() {
        try {
            CallDetails callDetails = new CallDetails();
            StringBuffer sb = new StringBuffer();
            Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null, null, null, null);
            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
            int id = managedCursor.getColumnIndex(CallLog.Calls._ID);
            sb.append("Call Details :");
            while (managedCursor.moveToNext()) {
                String phNumber = managedCursor.getString(number);
                String callType = managedCursor.getString(type);
                String callDate = managedCursor.getString(date);
                Date callDayTime = new Date(Long.valueOf(callDate));
                String callDuration = managedCursor.getString(duration);
                int idNumber = managedCursor.getInt(id);
                String dir = null;
                int dircode = Integer.parseInt(callType);
                switch (dircode) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        dir = "OUTGOING";
                        break;

                    case CallLog.Calls.INCOMING_TYPE:
                        dir = "INCOMING";
                        break;

                    case CallLog.Calls.MISSED_TYPE:
                        dir = "MISSED";
                        break;
                    default:
                        dir = "MISSED";
                        break;
                }
                sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- " + dir + " \nCall Date:--- " + callDayTime + " \nCall duration in sec :--- " + callDuration);
                sb.append("\n----------------------------------");
                callDetails.setCallerName(getContactName(phNumber, ContactListActivity.this));
                callDetails.setCallerNumber(phNumber);
                callDetails.setCallDuration(callDuration);
                callDetails.setCallType(dir);
                String formateDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(callDayTime);
                callDetails.setCallTimeStamp(formateDate);
                callDetails.setCallerLogId(idNumber);
                callDetails.setAcknowlwdgement("0");
                callDetails.setDate(CommonDataArea.getDAte("dd/MM/yyyy"));
                callDetails.setChildId(CommonFunctionArea.getDeviceUUID(this));
                if (!getCallEntry(idNumber)) {
                    insertCallDetails(callDetails);
                }

            }
            managedCursor.close();
            Log.i("Calllog>>", sb.toString());

        } catch (Exception e) {
        }
    }
}
