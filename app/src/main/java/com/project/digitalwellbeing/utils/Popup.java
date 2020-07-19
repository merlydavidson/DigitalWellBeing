package com.project.digitalwellbeing.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.project.digitalwellbeing.R;
import com.project.digitalwellbeing.data.model.AppDataBase;
import com.project.digitalwellbeing.data.model.DigitalWellBeingDao;
import com.project.digitalwellbeing.data.model.TaskDetails;
import com.project.digitalwellbeing.remote.Communicator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Popup extends Activity {
    TextView popupTitle, popupMessage;
    Button btnOK, yesBtn, noBtn;
    EditText pairEdit;
    Context context;
    private DatePickerDialog picker;
    String fromTime,toTime;
    String adjFDate,adjTDate;
    boolean appdisabled=true;
    public Popup(Context context) {
        this.context = context;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    }

    public void singleChoice(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    public void doubleChoice(String title, String message, final int callingFrom,Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
               activity.startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                alertDialog.dismiss();
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
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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


    public void taskPopup(String title, final Context contextCurr) {
         long startMilliSeconds =0;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_task_view, null);
        // popupTitle = (TextView) layout.findViewById(R.id.dtitle);
        EditText activityEdt = (EditText) layout.findViewById(R.id.activity_edt);
        CheckBox disableApps = (CheckBox) layout.findViewById(R.id.disable_apps);
        EditText calenderEdt = (EditText) layout.findViewById(R.id.calender_edt);
        EditText startClockEdt = (EditText) layout.findViewById(R.id.start_clock_edt);
        EditText endClockEdt = (EditText) layout.findViewById(R.id.end_clock_edt);
        Button submitBtn = (Button) layout.findViewById(R.id.activity_submit_btn);
        ImageView closePopup=(ImageView)layout.findViewById(R.id.close_poup);


        builder.setView(layout);
        final AlertDialog alertDialog = builder.create();
        Window view=((AlertDialog)alertDialog).getWindow();
        view.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

       // alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);//new line added for max width
        alertDialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

        lp.copyFrom(alertDialog.getWindow().getAttributes());
        lp.width = 900;
        lp.height = 1200;
//        lp.x=-170;
//        lp.y=100;
        alertDialog.getWindow().setAttributes(lp);

        closePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        calenderEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activityEdt.getText().toString().equals(""))
                    activityEdt.setError("Enter task name");
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                if(monthOfYear<10 && dayOfMonth<10)
                                    calenderEdt.setText("0"+dayOfMonth + "/" +"0"+(monthOfYear + 1) + "/" + year);
                                else if(dayOfMonth<10)
                                calenderEdt.setText("0"+dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                else if(monthOfYear<10)
                                    calenderEdt.setText(dayOfMonth + "/" +"0"+ (monthOfYear + 1) + "/" + year);
                                else
                                    calenderEdt.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });
        disableApps.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
                if (isChecked){
                    appdisabled=true;
                }else{
                    appdisabled=false;
                }
            }
        });
        startClockEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);


                TimePickerDialog mTimePicker;

                mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                       /* Calendar calendar = Calendar.getInstance();
                        calendar.set(timePicker.getHour(),timePicker.getMinute());*/
                        //startMilliSeconds=calendar.getTimeInMillis();


                        if(selectedHour<10 && selectedMinute<10) {
                            fromTime = calenderEdt.getText().toString() + " 0" + selectedHour + ":0" + selectedMinute;
                                adjFDate="0" + selectedHour + ":0" + selectedMinute;
                        } else if(selectedHour<10) {
                            fromTime = calenderEdt.getText().toString() + " 0" + selectedHour + ":" + selectedMinute;
                            adjFDate="0" + selectedHour + ":" + selectedMinute;
                        }else if(selectedMinute<10) {adjFDate="0" + selectedHour + ":" + selectedMinute;
                            fromTime = calenderEdt.getText().toString() + " " + selectedHour + ":0" + selectedMinute;
                            adjFDate=selectedHour + ":0" + selectedMinute;
                        }else {
                            fromTime = calenderEdt.getText().toString() + " " + selectedHour + ":" + selectedMinute;
                            adjFDate=selectedHour + ":" + selectedMinute;
                        }
                        startClockEdt.setText(adjFDate);
                    }
                }, hour, minute, true);//Yes 24 hour starttime
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        endClockEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);


                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        if(selectedHour<10 && selectedMinute<10) {
                            toTime = calenderEdt.getText().toString() + " 0" + selectedHour + ":0" + selectedMinute;
                            adjTDate="0" + selectedHour + ":0" + selectedMinute;
                        } else if(selectedHour<10) {
                            toTime = calenderEdt.getText().toString() + " 0" + selectedHour + ":" + selectedMinute;
                            adjTDate="0" + selectedHour + ":" + selectedMinute;
                        }else if(selectedMinute<10) {adjFDate="0" + selectedHour + ":" + selectedMinute;
                            toTime = calenderEdt.getText().toString() + " " + selectedHour + ":0" + selectedMinute;
                            adjTDate=selectedHour + ":0" + selectedMinute;
                        }else {
                            toTime = calenderEdt.getText().toString() + " " + selectedHour + ":" + selectedMinute;
                            adjTDate=selectedHour + ":" + selectedMinute;
                        }

                       if( compareDates(fromTime,toTime))
                           endClockEdt.setText(adjTDate);
                        else{
                           endClockEdt.setText("");
                            Toast.makeText(context, "Start time must be less than end Time", Toast.LENGTH_SHORT).show();
                    }}
                }, hour, minute, true);//Yes 24 hour starttime
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!activityEdt.getText().toString().equalsIgnoreCase("") && !calenderEdt.getText().toString().equalsIgnoreCase("") && !startClockEdt.getText().toString().equalsIgnoreCase("") && !endClockEdt.getText().toString().equalsIgnoreCase("")) {
                    TaskDetails taskDetails = new TaskDetails();
                    taskDetails.setTaskName(activityEdt.getText().toString());
                    taskDetails.setDate(calenderEdt.getText().toString());
                    taskDetails.setStarttime(startClockEdt.getText().toString());
                    taskDetails.setEndtime(endClockEdt.getText().toString());
                    taskDetails.setUpload(0);
                    taskDetails.setStatus(0);
                    taskDetails.setEnableApps(appdisabled);
                    taskDetails.setChildId(CommonDataArea.CURRENTCHILDID);

                    AppDataBase appDataBase = AppDataBase.getInstance(context);
                    DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
                    digitalWellBeingDao.insertTaskDetails(taskDetails);
                    alertDialog.dismiss();
                   // Toast.makeText(getApplicationContext(), "Task added successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    public static boolean compareDates(String d1,String d2)
    {
        try{

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date date1 = sdf.parse(d1);
            Date date2 = sdf.parse(d2);

            System.out.println("Date1"+sdf.format(date1));
            System.out.println("Date2"+sdf.format(date2));System.out.println();

            if(date1.getTime()<date2.getTime())
            return true;
            else return false;


        }
        catch(ParseException ex){
            ex.printStackTrace();
        }
        return false;
    }

}
