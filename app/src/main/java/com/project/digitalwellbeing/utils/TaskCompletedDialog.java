package com.project.digitalwellbeing.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.digitalwellbeing.DashboardActivity;
import com.project.digitalwellbeing.R;
import com.project.digitalwellbeing.TaskActivity;
import com.project.digitalwellbeing.adapter.CompletedTaskAdapter;
import com.project.digitalwellbeing.adapter.GenericAdapter;
import com.project.digitalwellbeing.data.model.TaskDetails;

import java.util.List;

import static com.project.digitalwellbeing.utils.CommonDataArea.APP_BLOCK_PIN;
import static com.project.digitalwellbeing.utils.CommonDataArea.BLOCKAPPS;
import static com.project.digitalwellbeing.utils.CommonDataArea.editor;
import static com.project.digitalwellbeing.utils.CommonDataArea.sharedPreferences;

public class TaskCompletedDialog {

    public void showDialog(Activity activity, Context context, List<TaskDetails> mList){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.task_completed_layout);

        RecyclerView recyclerView=dialog.findViewById(R.id.taskstatus_recycler);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        CompletedTaskAdapter mAdapter = new CompletedTaskAdapter(mList,context);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        Button dialogButton = (Button) dialog.findViewById(R.id.close_btn);


        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

    }
}
