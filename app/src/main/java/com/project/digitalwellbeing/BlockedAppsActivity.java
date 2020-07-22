package com.project.digitalwellbeing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;

import com.project.digitalwellbeing.adapter.BlockedAppsAdapter;
import com.project.digitalwellbeing.adapter.GenericAdapter;
import com.project.digitalwellbeing.data.model.AppDataBase;
import com.project.digitalwellbeing.data.model.BlockedApps;
import com.project.digitalwellbeing.data.model.DigitalWellBeingDao;

import java.util.List;

public class BlockedAppsActivity extends AppCompatActivity {
    RecyclerView blockedRecycler;
    Button unblock;
    List<BlockedApps> blockedAppsList;
    BlockedAppsAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked_apps);
        blockedRecycler=findViewById(R.id.blocked_recycler);
        unblock=findViewById(R.id.unblock);
        AppDataBase appDataBase = AppDataBase.getInstance(this);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        blockedAppsList=digitalWellBeingDao.getBlockedApps();
        blockedRecycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        blockedRecycler.setLayoutManager(layoutManager);
        mAdapter = new BlockedAppsAdapter(blockedAppsList ,this);
        blockedRecycler.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

    }
}
