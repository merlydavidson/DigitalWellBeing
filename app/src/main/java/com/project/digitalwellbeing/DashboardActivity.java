package com.project.digitalwellbeing;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.project.digitalwellbeing.adapter.UsersListAdapter;
import com.project.digitalwellbeing.data.model.AppDataBase;
import com.project.digitalwellbeing.data.model.DigitalWellBeingDao;
import com.project.digitalwellbeing.data.model.UserDetails;
import com.project.digitalwellbeing.remote.Communicator;
import com.project.digitalwellbeing.service.DigitalWellBeingService;
import com.project.digitalwellbeing.utils.CommonDataArea;
import com.project.digitalwellbeing.utils.CommonFunctionArea;
import com.project.digitalwellbeing.utils.Popup;

import java.util.ArrayList;
import java.util.List;

import static com.project.digitalwellbeing.utils.CommonDataArea.sharedPreferences;

public class
DashboardActivity extends AppCompatActivity {
    FloatingActionButton floatingActionButton;
    RecyclerView childRecyclerview;
    private RecyclerView.LayoutManager layoutManager;

    private Toolbar toolbar;
    private SwipeRefreshLayout pullToRefresh;
    private UsersListAdapter mAdapter;
    private DigitalWellBeingService mDigitalWellBeingService;
    private Intent mServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        sharedPreferences = getSharedPreferences(
                CommonDataArea.prefName, Context.MODE_PRIVATE);
        CommonDataArea.editor = sharedPreferences.edit();
        CommonDataArea.ROLE = sharedPreferences.getInt(CommonDataArea.ROLESTR, 0);
        childRecyclerview = (RecyclerView) findViewById(R.id.child_rv);
        childRecyclerview.setHasFixedSize(true);
        pullToRefresh = (SwipeRefreshLayout) findViewById(R.id.pullToRefresh);
        layoutManager = new LinearLayoutManager(this);
        childRecyclerview.setLayoutManager(layoutManager);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        if (CommonDataArea.ROLE != 0) {
            floatingActionButton.setVisibility(View.GONE);
        }
        toolbar = findViewById(R.id.pres_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        toolbar.setTitle("Digital Well Being");
        toolbar.setBackgroundColor(getResources().getColor(R.color.dark_bg));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.inflateMenu(R.menu.menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        CommonDataArea.FIREBASETOPIC = "/topics/" + CommonFunctionArea.getDeviceUUID(this);
        new CommonFunctionArea().subscribeTopic();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Popup(DashboardActivity.this).SingleEditTextChoice("Pair Child", 1, DashboardActivity.this);
            }
        });
        mDigitalWellBeingService = new DigitalWellBeingService();
        mServiceIntent = new Intent(this, mDigitalWellBeingService.getClass());
        if (!isMyServiceRunning(mDigitalWellBeingService.getClass())) {
            startService(mServiceIntent);
        }
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("Service status", "Running");
                return true;
            }
        }
        Log.i ("Service status", "Not running");
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CommonDataArea.ROLE == 0 && getStimulationList().size() > 0) {
            mAdapter = new UsersListAdapter(this, getStimulationList(),new CommonFunctionArea().getLogList(DashboardActivity.this));
            childRecyclerview.setAdapter(mAdapter);

            pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                int Refreshcounter = 1; //Counting how many times user have refreshed the layout

                @Override
                public void onRefresh() {
                    //Here you can update your data from internet or from local SQLite data
                    //   communicator.checkPrescriptionDelete(CommonDataArea.patientID);
                    mAdapter = new UsersListAdapter(DashboardActivity.this, getStimulationList(),new CommonFunctionArea().getLogList(DashboardActivity.this));
                    childRecyclerview.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    pullToRefresh.setRefreshing(false);
                }
            });
        }
    }


    public List<UserDetails> getStimulationList() {
        List<UserDetails> userDetails = null;
        try {
            AppDataBase appDataBase = AppDataBase.getInstance(DashboardActivity.this);
            DigitalWellBeingDao stimulationSessionsDao = appDataBase.userDetailsDao();
            userDetails = stimulationSessionsDao.getUserDetails();
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
        return userDetails;
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
                String uuid = CommonFunctionArea.getDeviceUUID(DashboardActivity.this);
                new Popup(DashboardActivity.this).singleChoice("UUID", uuid);

                break;

            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }


}
