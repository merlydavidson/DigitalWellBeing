package com.project.digitalwellbeing;

import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

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
import java.util.List;

public class TaskActivity extends AppCompatActivity {

    FloatingActionButton floatingActionButtonAddtask;
    private Toolbar toolbar;
    private LinearLayout linearLayoutDashBoard;
    private int count = 0;
    private long startMillis = 0;
    private PieChart pieChart;
    private RecyclerView callLogRecycler;
    private LinearLayoutManager layoutManager;
    private GenericAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        //drawPieChart();
        floatingActionButtonAddtask = (FloatingActionButton) findViewById(R.id.floatingActionButton_task);
        if (CommonDataArea.ROLE == 1)
            //TODO://disable floatin button for child
           // floatingActionButtonAddtask.setVisibility(View.GONE);
        floatingActionButtonAddtask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Popup(TaskActivity.this).taskPopup("Add task", TaskActivity.this);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
       // callLogRecycler = findViewById(R.id.task_recyclerview);
        RecyclerView recyclerViewCall = (RecyclerView) findViewById(R.id.task_recyclerview);
        recyclerViewCall.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerViewCall.setLayoutManager(layoutManager);
        mAdapter = new GenericAdapter(TaskActivity.this, 3,getTaskDetails() );
        recyclerViewCall.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    public void drawPieChart() {
      //  pieChart = findViewById(R.id.piechart);

        ArrayList<PieEntry> xEntrys = new ArrayList<>();
        ArrayList<String> yEntrys = new ArrayList<>();




//        ArrayList NoOfEmp = new ArrayList();
//
//        NoOfEmp.add(new Entry(945f, 0));
//        NoOfEmp.add(new Entry(1040f, 1));
//        NoOfEmp.add(new Entry(1133f, 2));
//        NoOfEmp.add(new Entry(1240f, 3));
//        NoOfEmp.add(new Entry(1369f, 4));
//        NoOfEmp.add(new Entry(1487f, 5));
//        NoOfEmp.add(new Entry(1501f, 6));
//        NoOfEmp.add(new Entry(1645f, 7));
//        NoOfEmp.add(new Entry(1578f, 8));
//        NoOfEmp.add(new Entry(1695f, 9));
//        PieDataSet dataSet = new PieDataSet(NoOfEmp, "Number Of Employees");
//
//        ArrayList year = new ArrayList();
//
//        year.add("2008");
//        year.add("2009");
//        year.add("2010");
//        year.add("2011");
//        year.add("2012");
//        year.add("2013");
//        year.add("2014");
//        year.add("2015");
//        year.add("2016");
//        year.add("2017");
//        PieData data = new PieData(year, dataSet);
//        pieChart.setData(data);
//        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
//        pieChart.animateXY(5000, 5000);

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
        List<TaskDetails> taskDetails = digitalWellBeingDao.getaTaskDetails();

        return taskDetails;
    }


}
