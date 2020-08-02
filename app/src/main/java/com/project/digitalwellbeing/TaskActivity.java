package com.project.digitalwellbeing;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.project.digitalwellbeing.adapter.GenericAdapter;
import com.project.digitalwellbeing.data.model.AppDataBase;
import com.project.digitalwellbeing.data.model.DigitalWellBeingDao;
import com.project.digitalwellbeing.data.model.TaskDetails;
import com.project.digitalwellbeing.utils.CommonDataArea;
import com.project.digitalwellbeing.utils.CommonFunctionArea;
import com.project.digitalwellbeing.utils.Popup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TaskActivity extends AppCompatActivity {
    ProgressDialog progress;
    List<TaskDetails> task=new ArrayList<>();
    FloatingActionButton floatingActionButtonAddtask;
    private Toolbar toolbar;
    private LinearLayout linearLayoutDashBoard;
    private int count = 0;
    private long startMillis = 0;
    private PieChart pieChart;

    private RecyclerView callLogRecycler;
    private DatePickerDialog picker;
    private LinearLayoutManager layoutManager;
    private GenericAdapter mAdapter;
    Button go;
EditText dateFrom,dateTo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        dateFrom=findViewById(R.id.date_from);
        dateTo=findViewById(R.id.date_to);
        go=findViewById(R.id.result);
        task=getTaskDetails();
        recyclerViewCall = (RecyclerView) findViewById(R.id.task_recyclerview);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        dateFrom.setText(CommonDataArea.getDAte("dd/MM/yyyy"));
        dateTo.setText(CommonDataArea.getDAte("dd/MM/yyyy"));
        //drawPieChart();
        floatingActionButtonAddtask = (FloatingActionButton) findViewById(R.id.floatingActionButton_task);
        if (CommonDataArea.ROLE == 1)
            //TODO://disable floatin button for child
           floatingActionButtonAddtask.setVisibility(View.GONE);
        floatingActionButtonAddtask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Popup(TaskActivity.this).taskPopup("Add task", TaskActivity.this);
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
                picker = new DatePickerDialog(TaskActivity.this,
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

        dateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(TaskActivity.this,
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

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress = ProgressDialog.show(TaskActivity.this, "Loading..",
                        "Please wait...", true);
                progress.show();
                List<TaskDetails> sortedList=new ArrayList<>();
                String datefrom=dateFrom.getText().toString();
                String dateto=dateTo.getText().toString();
                if(CommonFunctionArea.compareDateTimes("dd/MM/yyyy",datefrom,dateto)){
                    List<TaskDetails> details=getTaskDetails();
                    for(TaskDetails d:details){
                        if(CommonFunctionArea.compareDateTimes("dd/MM/yyyy",datefrom,d.getDate()) &&
                                CommonFunctionArea.compareDateTimes("dd/MM/yyyy",d.getDate(),dateto)){
                            sortedList.add(d);
                        }
                    }
                    mAdapter = new GenericAdapter(TaskActivity.this, 3,sortedList ,TaskActivity.this);
                    recyclerViewCall.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();

                }else{
                    Toast.makeText(TaskActivity.this, "Date from must be less than date to..", Toast.LENGTH_SHORT).show();
                }
                progress.dismiss();
            }
        });
    }
    RecyclerView recyclerViewCall;
    @Override
    protected void onResume() {
        super.onResume();
        progress = ProgressDialog.show(TaskActivity.this, "Loading..",
                "Please wait...", true);
        progress.show();
       // callLogRecycler = findViewById(R.id.task_recyclerview);
        Log.d("LifeCycle","TaskActivity onresume called");
        recyclerViewCall.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerViewCall.setLayoutManager(layoutManager);

        mAdapter = new GenericAdapter(TaskActivity.this, 3,task,this);
        recyclerViewCall.setAdapter(mAdapter);
       // mAdapter.notifyItemChanged(0);
        mAdapter.notifyDataSetChanged();
        progress.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                finish();
                return true;
            case R.id.uuid:
                String uuid = CommonFunctionArea.getDeviceUUID(TaskActivity.this);
                new Popup(TaskActivity.this).singleChoice("UUID", uuid);

                break;

            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public List<TaskDetails> getTaskDetails() {
        AppDataBase appDataBase = AppDataBase.getInstance(TaskActivity.this);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        List<TaskDetails> taskDetails = digitalWellBeingDao.getaTaskDetails2(CommonDataArea.CURRENTCHILDID);

        return taskDetails;
    }
    public void addtaskToListView(TaskDetails t){

        mAdapter = new GenericAdapter(TaskActivity.this, 3,getTaskDetails() ,TaskActivity.this);
        recyclerViewCall.setAdapter(mAdapter);
    }

}
