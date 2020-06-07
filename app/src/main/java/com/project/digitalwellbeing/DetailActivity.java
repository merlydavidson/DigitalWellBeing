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

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.project.digitalwellbeing.utils.CommonDataArea;
import com.project.digitalwellbeing.utils.CommonFunctionArea;
import com.project.digitalwellbeing.utils.Popup;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    FloatingActionButton floatingActionButtonAddtask;
    private Toolbar toolbar;
    private LinearLayout linearLayoutDashBoard;
    private int count = 0;
    private long startMillis = 0;
    private PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        // toolbar = findViewById(R.id.det_toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

//        toolbar.setTitle("Summary");
//        toolbar.inflateMenu(R.menu.menu);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        CommonDataArea.FIREBASETOPIC = "/topics/" + CommonFunctionArea.getDeviceUUID(this);
//        new CommonFunctionArea().subscribeTopic();
        linearLayoutDashBoard = (LinearLayout) findViewById(R.id.det_layout);
        linearLayoutDashBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long time = System.currentTimeMillis();


                //if it is the first starttime, or if it has been more than 3 seconds since the first tap ( so it is like a new try), we reset everything
                if (startMillis == 0 || (time - startMillis > 3000)) {
                    startMillis = time;
                    count = 1;
                }
                //it is not the first, and it has been  less than 3 seconds since the first
                else { //  starttime-startMillis< 3000
                    count++;
                }

                if (count == 5) {
                    String uuid = CommonFunctionArea.getDeviceUUID(DetailActivity.this);
                    new Popup(DetailActivity.this).singleChoice("UUID", uuid);
                }
            }
        });
        drawPieChart();
        floatingActionButtonAddtask = (FloatingActionButton) findViewById(R.id.floatingActionButton_task);
        if (CommonDataArea.ROLE == 1)
            floatingActionButtonAddtask.setVisibility(View.GONE);
        floatingActionButtonAddtask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Popup(DetailActivity.this).taskPopup("Add task", DetailActivity.this);
            }
        });

    }

    public void drawPieChart() {
        pieChart = findViewById(R.id.piechart);
        ArrayList NoOfEmp = new ArrayList();

        NoOfEmp.add(new Entry(945f, 0));
        NoOfEmp.add(new Entry(1040f, 1));
        NoOfEmp.add(new Entry(1133f, 2));
        NoOfEmp.add(new Entry(1240f, 3));
        NoOfEmp.add(new Entry(1369f, 4));
        NoOfEmp.add(new Entry(1487f, 5));
        NoOfEmp.add(new Entry(1501f, 6));
        NoOfEmp.add(new Entry(1645f, 7));
        NoOfEmp.add(new Entry(1578f, 8));
        NoOfEmp.add(new Entry(1695f, 9));
        PieDataSet dataSet = new PieDataSet(NoOfEmp, "Number Of Employees");

        ArrayList year = new ArrayList();

        year.add("2008");
        year.add("2009");
        year.add("2010");
        year.add("2011");
        year.add("2012");
        year.add("2013");
        year.add("2014");
        year.add("2015");
        year.add("2016");
        year.add("2017");
        PieData data = new PieData(year, dataSet);
        pieChart.setData(data);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieChart.animateXY(5000, 5000);

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
                String uuid = CommonFunctionArea.getDeviceUUID(DetailActivity.this);
                new Popup(DetailActivity.this).singleChoice("UUID", uuid);

                break;

            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

}
