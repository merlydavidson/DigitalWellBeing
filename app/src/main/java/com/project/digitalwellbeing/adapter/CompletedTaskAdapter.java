package com.project.digitalwellbeing.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.project.digitalwellbeing.R;
import com.project.digitalwellbeing.TaskActivity;
import com.project.digitalwellbeing.data.model.AppDataBase;
import com.project.digitalwellbeing.data.model.DigitalWellBeingDao;
import com.project.digitalwellbeing.data.model.LogDetails;
import com.project.digitalwellbeing.data.model.TaskDetails;

import java.util.List;

public class CompletedTaskAdapter extends RecyclerView.Adapter<CompletedTaskAdapter.ViewHolder>{
    List<TaskDetails> logDetails;
Context mContext;
    // RecyclerView recyclerView;  
    public CompletedTaskAdapter(List<TaskDetails> logDetails,Context mContext) {
        this.logDetails = logDetails;
        this.mContext=mContext;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.item_completed_tasks, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (logDetails.size() > 0) {
            TaskDetails taskDetail = logDetails.get(position);
            holder.task_name.setText(logDetails.get(position).getTaskName());
            holder.task_date.setText(logDetails.get(position).getDate());
            holder.time_from_to.setText("End Time : "+taskDetail.getEndtime());
            if (logDetails.get(position).getStatus() == 0){
                holder.status.setText("Pending");

            }
            else if (logDetails.get(position).getStatus() == 1){
                holder.status.setText("Completed");
                holder.c.setChecked(true);}
            else if (logDetails.get(position).getStatus() == 2){
                holder.status.setText("InComplete");
                holder.nc.setChecked(true);
            }

        }

        AppDataBase appDataBase = AppDataBase.getInstance(mContext);
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();


        holder.radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                 if(checkedId == R.id.completed) {
                    holder.status.setText("Completed");
                    digitalWellBeingDao.updateTaskdetails(logDetails.get(position).getLogId(),1);

                } else if(checkedId == R.id.incomplete){
                    holder.status.setText("Incomplete");
                    digitalWellBeingDao.updateTaskdetails(logDetails.get(position).getLogId(),2);
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return logDetails.size();
    }

    /*public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView
       public  EditText radio_group;

        public ViewHolder(View itemView) {
            super(itemView);
            task_name=itemView.findViewById(R.id.task_name);
            task_date=itemView.findViewById(R.id.task_date);
            time_from_to=itemView.findViewById(R.id.time_from_to);
            status=itemView.findViewById(R.id.tsk_status);
            //radio_group=(RadioGroup) itemView.findViewById(R.id.statusgroup);
        }
    }*/
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView task_name,task_date,time_from_to,status;
        public RadioButton c,nc;
        public RadioGroup radio_group;

        public ViewHolder(View itemView) {
            super(itemView);
            this.task_name=itemView.findViewById(R.id.task_name);
            this.task_date=itemView.findViewById(R.id.task_date);
            this.time_from_to=itemView.findViewById(R.id.time_from_to);
            this.status=itemView.findViewById(R.id.tsk_status);
            this.radio_group=(RadioGroup) itemView.findViewById(R.id.statusgroup);

            this.c=itemView.findViewById(R.id.completed);
            this.nc=itemView.findViewById(R.id.incomplete);
        }
    }
} 
