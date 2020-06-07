package com.project.digitalwellbeing;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.project.digitalwellbeing.adapter.UsersListAdapter;
import com.project.digitalwellbeing.data.model.AppDataBase;
import com.project.digitalwellbeing.data.model.DigitalWellBeingDao;
import com.project.digitalwellbeing.data.model.UserDetails;
import com.project.digitalwellbeing.utils.CommonDataArea;
import com.project.digitalwellbeing.utils.CommonFunctionArea;
import com.project.digitalwellbeing.utils.Popup;

import java.util.List;

import static com.project.digitalwellbeing.utils.CommonDataArea.sharedPreferences;

public class
ChildActivity extends AppCompatActivity {
    FloatingActionButton floatingActionButton;
    RecyclerView childRecyclerview;
    private RecyclerView.LayoutManager layoutManager;

    private Toolbar toolbar;
    private SwipeRefreshLayout pullToRefresh;
    private UsersListAdapter mAdapter;


    LinearLayout linearLayoutDashBoard;
    private int count = 0;
    private long startMillis = 0;

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
//        toolbar = findViewById(R.id.pres_toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//
//        toolbar.setTitle("Digital Well Being");
//        toolbar.inflateMenu(R.menu.menu);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        CommonDataArea.FIREBASETOPIC = "/topics/" + CommonFunctionArea.getDeviceUUID(this);
        new CommonFunctionArea().subscribeTopic();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Popup(ChildActivity.this).SingleEditTextChoice("Pair Child", 1, ChildActivity.this);
            }
        });

      //  CommonFunctionArea.getBrowserHistory(ChildActivity.this);
       // new CommonFunctionArea.FetchCategoryTask(ChildActivity.this).execute();

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (CommonDataArea.ROLE == 0 && getStimulationList().size() > 0) {
            mAdapter = new UsersListAdapter(this, getStimulationList(), new CommonFunctionArea().getLogList(ChildActivity.this));
            childRecyclerview.setAdapter(mAdapter);

            pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                int Refreshcounter = 1; //Counting how many times user have refreshed the layout

                @Override
                public void onRefresh() {
                    //Here you can update your data from internet or from local SQLite data
                    //   communicator.checkPrescriptionDelete(CommonDataArea.patientID);
                    mAdapter = new UsersListAdapter(ChildActivity.this, getStimulationList(), new CommonFunctionArea().getLogList(ChildActivity.this));
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
            AppDataBase appDataBase = AppDataBase.getInstance(ChildActivity.this);
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

            case R.id.uuid:
                String uuid = CommonFunctionArea.getDeviceUUID(ChildActivity.this);
                new Popup(ChildActivity.this).singleChoice("UUID", uuid);

                break;

            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }


}
