package com.project.digitalwellbeing;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.digitalwellbeing.adapter.GenericAdapter;
import com.project.digitalwellbeing.data.model.AppDataBase;
import com.project.digitalwellbeing.data.model.CallDetails;
import com.project.digitalwellbeing.data.model.DigitalWellBeingDao;
import com.project.digitalwellbeing.data.model.LogDetails;
import com.project.digitalwellbeing.utils.CommonDataArea;
import com.project.digitalwellbeing.utils.CommonFunctionArea;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LocationActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private GenericAdapter mAdapter;
    Button go;
    EditText dateFrom,dateTo;
    private DatePickerDialog picker;
    ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_layout);
        dateFrom=findViewById(R.id.date_from);
        dateTo=findViewById(R.id.date_to);
        go=findViewById(R.id.result);
        dateFrom.setText(CommonDataArea.getDAte("dd/MM/yyyy"));
        dateTo.setText(CommonDataArea.getDAte("dd/MM/yyyy"));
        recyclerView = (RecyclerView) findViewById(R.id.genericRecyclerLayout);
        RecyclerView recyclerViewCall = (RecyclerView) findViewById(R.id.genericRecyclerLayout);
        recyclerViewCall.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerViewCall.setLayoutManager(layoutManager);
        mAdapter = new GenericAdapter(LocationActivity.this, getcallDetails(), 2);
        recyclerViewCall.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        sortbyDate();
    }

    public List<LogDetails> getcallDetails() {
        AppDataBase appDataBase = AppDataBase.getInstance(LocationActivity.this);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        List<LogDetails> logDetails = digitalWellBeingDao.getLogDetails(CommonDataArea.CURRENTCHILDID);

        return logDetails;
    }

    private void sortbyDate(){
        dateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(LocationActivity.this,
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
                picker = new DatePickerDialog(LocationActivity.this,
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
                progress = ProgressDialog.show(LocationActivity.this, "Loading..",
                        "Please wait...", true);
                progress.show();
                List<LogDetails> sortedList=new ArrayList<>();
                String datefrom=dateFrom.getText().toString()+" 01:00";
                String dateto=dateTo.getText().toString()+" 23:59";
                if(CommonFunctionArea.compareDateTimes("dd/MM/yyyy",datefrom,dateto)){
                    List<LogDetails> details=getcallDetails();
                    for(LogDetails d:details){
                        if(CommonFunctionArea.compareDateTimes("dd/MM/yyyy HH:mm",datefrom,d.getTimeStamp()) &&
                                CommonFunctionArea.compareDateTimes("dd/MM/yyyy HH:mm",d.getTimeStamp(),dateto)){
                            sortedList.add(d);
                        }
                    }
                    mAdapter = new GenericAdapter(LocationActivity.this, sortedList, 2);
                    recyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();

                }else{
                    Toast.makeText(LocationActivity.this, "Date from must be less than date to..", Toast.LENGTH_SHORT).show();
                }
                progress.dismiss();
            }
        });
    }
}
