package com.project.digitalwellbeing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
    List<BlockedApps> unblockList;
    BlockedAppsAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked_apps);
        blockedRecycler=findViewById(R.id.blocked_recycler);
        unblock=findViewById(R.id.unblock);
        AppDataBase appDataBase = AppDataBase.getInstance(this);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        blockedAppsList=digitalWellBeingDao.getBlockedApps(true);

        blockedRecycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        blockedRecycler.setLayoutManager(layoutManager);
        mAdapter = new BlockedAppsAdapter(blockedAppsList ,this);
        blockedRecycler.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        unblock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unblockList=mAdapter.getUnblocklist();
                if (unblockList != null){
                    for (int i=0; i<unblockList.size(); i++){
                        if (unblockList.get(i).getChecked()){
                           digitalWellBeingDao.updateBlockStatus(false,unblockList.get(i).getPackagename());

                        }
                    }
                }

                mAdapter.updatelist();
                 Toast.makeText(BlockedAppsActivity.this, "Apps Unblocked Successfully", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
