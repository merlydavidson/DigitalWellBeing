package com.project.digitalwellbeing;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.digitalwellbeing.adapter.GenericAdapter;
import com.project.digitalwellbeing.data.model.AppDataBase;
import com.project.digitalwellbeing.data.model.DigitalWellBeingDao;
import com.project.digitalwellbeing.data.model.LogDetails;

import java.util.List;

public class LocationActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private GenericAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_layout);
        recyclerView = (RecyclerView) findViewById(R.id.genericRecyclerLayout);
        RecyclerView recyclerViewCall = (RecyclerView) findViewById(R.id.genericRecyclerLayout);
        recyclerViewCall.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerViewCall.setLayoutManager(layoutManager);
        mAdapter = new GenericAdapter(LocationActivity.this, getcallDetails(), 2);
        recyclerViewCall.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    public List<LogDetails> getcallDetails() {
        AppDataBase appDataBase = AppDataBase.getInstance(LocationActivity.this);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        List<LogDetails> logDetails = digitalWellBeingDao.getLogDetails();

        return logDetails;
    }
}
