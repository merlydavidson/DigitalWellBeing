package com.project.digitalwellbeing.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.digitalwellbeing.R;
import com.project.digitalwellbeing.data.model.CallDetails;
import com.project.digitalwellbeing.data.model.LogDetails;
import com.project.digitalwellbeing.data.model.TaskDetails;
import com.project.digitalwellbeing.utils.CommonDataArea;
import com.project.digitalwellbeing.utils.CommonFunctionArea;
import com.project.digitalwellbeing.utils.TaskCompletedDialog;

import java.util.ArrayList;
import java.util.List;

public class GenericAdapter extends RecyclerView.Adapter<GenericAdapter.ViewHolder> {
    List<CallDetails> callDetails;
    Context context;
    List<LogDetails> logDetails;
    int callingfrom = 0;
    List<TaskDetails> taskDetails;
    Activity activity;

    public GenericAdapter(List<CallDetails> callDetails, Context context, int callingfrom) {
        this.callDetails = callDetails;
        this.context = context;
        this.callingfrom = callingfrom;
        notifyDataSetChanged();
    }

    public GenericAdapter(Context context, List<LogDetails> logDetails, int callingfrom) {
        this.context = context;
        this.logDetails = logDetails;
        this.callingfrom = callingfrom;
        notifyDataSetChanged();
    }

    public GenericAdapter(Context context, int callingfrom, List<TaskDetails> taskDetails,Activity activity) {
        this.context = context;
        this.callingfrom = callingfrom;
        this.taskDetails = taskDetails;
        this.activity=activity;
        notifyDataSetChanged();
    }
public void updateList(TaskDetails c){
        taskDetails.add(0,c);
        notifyItemChanged(0);
}
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = null;
        if (callingfrom == 1) {
            listItem = layoutInflater.inflate(R.layout.call_view, parent, false);
        } else if (callingfrom == 2) {
            listItem = layoutInflater.inflate(R.layout.location_view, parent, false);
        } else if (callingfrom == 3) {
            listItem = layoutInflater.inflate(R.layout.task_view, parent, false);
        }else if (callingfrom == 4) {
            listItem = layoutInflater.inflate(R.layout.recent_activity_item, parent, false);
        }

        ViewHolder viewHolder = new ViewHolder(listItem, callingfrom);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (callingfrom == 1)
            bindCalldetails(holder, position);
        else if (callingfrom == 2)
            bindLocationDetails(holder, position);
        else if (callingfrom == 3)
            bindTaskDetails(holder, position);
        else if (callingfrom == 4)
            bindRecentDetails(holder, position);


    }

    private void bindRecentDetails(ViewHolder holder, int position) {
        if (logDetails.size() > 0) {
            if(logDetails.get(position).isOnline()) {
                holder.locationText.setText("Offline");
               // holder.locationText.setTextColor(Color.parseColor("3DA26B"));
            }
            if(!logDetails.get(position).isOnline()) {
                holder.locationText.setText("Online");
             //   holder.locationText.setTextColor(Color.parseColor("#D81B60"));
            }
            holder.timeText.setText(logDetails.get(position).getTimeStamp());
            final PackageManager pm = context.getPackageManager();
            ApplicationInfo ai;
            try {
                ai = pm.getApplicationInfo(logDetails.get(position).getApp_details(), 0);

                final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");

                holder.app_text.setText(logDetails.get(position).getAppname());
                Drawable icon = context.getPackageManager().getApplicationIcon(logDetails.get(position).getApp_details());
                holder.icon.setImageDrawable(icon);
            } catch (Exception e) {
            }
        }
    }


    private void bindTaskDetails(ViewHolder holder, int position) {
        if (taskDetails.size() > 0) {
            TaskDetails taskDetail = taskDetails.get(position);
            holder.taskName.setText(taskDetails.get(position).getTaskName());
            holder.taskDate.setText(taskDetails.get(position).getDate());
            Log.d("debuggenericadapter", taskDetail.getTaskName());
            holder.taskTime.setText(taskDetails.get(position).getStarttime() + " - " + taskDetails.get(position).getEndtime());
            if (taskDetails.get(position).getStatus() == 0)
                holder.taskStatus.setText("Status : Pending");
            else if (taskDetails.get(position).getStatus() == 1)
                holder.taskStatus.setText("Status : Completed");
            else if (taskDetails.get(position).getStatus() == 2)
                holder.taskStatus.setText("Status : InComplete");

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (taskDetails.get(position).getDate().equals(CommonDataArea.getDAte("dd/MM/yyyy")))
//                        {
                    String taskEndTime = taskDetails.get(position).getDate() + " " + taskDetails.get(position).getEndtime();
                    String currentTime = CommonDataArea.getDAte("dd/MM/yyyy HH:mm");

                    if (CommonFunctionArea.compareDateTimes("dd/MM/yyyy HH:mm",taskEndTime, currentTime)) {
                        List<TaskDetails> list=new ArrayList<>();
                        list.add(taskDetails.get(position));
                        new TaskCompletedDialog().showDialog(activity,context,list);
                    }
               // }
            }
        });
    }

    private void bindLocationDetails(ViewHolder holder, int position) {

        if (logDetails.size() > 0) {
            holder.locationText.setText(logDetails.get(position).getLocation());
            holder.timeText.setText(logDetails.get(position).getTimeStamp());

        }
    }

    @Override
    public int getItemCount() {
       /* if (callDetails != null)
            return callDetails.size();
        else
            return 0;*/
        if (callingfrom == 3)
            return taskDetails.size();
        else if (callingfrom == 1)
            return callDetails.size();
        else if (callingfrom == 2)
            return logDetails.size();
        else if (callingfrom == 4)
            return logDetails.size();
        else return 0;
    }

    public void bindCalldetails(ViewHolder holder, int position) {
        if (callDetails.size() > 0) {
            if (callDetails.get(position).getCallerName().equalsIgnoreCase(""))
                holder.nameText.setText("Unkown");
            else
                holder.nameText.setText(callDetails.get(position).getCallerName());
            holder.numberText.setText(callDetails.get(position).getCallerNumber());
            int totalSecs = Integer.parseInt(callDetails.get(position).getCallDuration());
            if (totalSecs != 0) {
                int hours = totalSecs / 3600;
                int minutes = (totalSecs % 3600) / 60;
                int seconds = totalSecs % 60;
                holder.durationtext.setText("Call Duration : " + hours + ":" + minutes + ":" + seconds);
            } else
                holder.durationtext.setText("Call Duration : 00:00:00");
            holder.timeText.setText(callDetails.get(position).getCallTimeStamp());
            if (callDetails.get(position).getCallType() != null && callDetails.get(position).getCallType().equalsIgnoreCase("INCOMING"))
                holder.callTypeimage.setBackgroundResource(R.drawable.ic_call_incoming);
            else if (callDetails.get(position).getCallType() != null && callDetails.get(position).getCallType().equalsIgnoreCase("OUTGOING"))
                holder.callTypeimage.setBackgroundResource(R.drawable.ic_call_outgoing);
            else if (callDetails.get(position).getCallType() != null && callDetails.get(position).getCallType().equalsIgnoreCase("MISSED"))
                holder.callTypeimage.setBackgroundResource(R.drawable.ic_call_missed);


        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView app_text, nameText, numberText, durationtext, timeText, locationText, taskName, taskDate, taskTime, taskStatus;
        ImageView callTypeimage, icon;


        public ViewHolder(@NonNull View itemView, int callingFrom) {
            super(itemView);
            if (callingFrom == 1)
                initCallViews(itemView);
            if (callingFrom == 2)
                initlocationViews(itemView);
            if (callingFrom == 3)
                initTaskViews(itemView);
            if (callingFrom == 4)
                initRecentActivitiesView(itemView);

        }

        private void initTaskViews(View itemView) {
            taskName = (TextView) itemView.findViewById(R.id.taskName);
            taskDate = (TextView) itemView.findViewById(R.id.taskDate);
            taskTime = (TextView) itemView.findViewById(R.id.taskTime);
            taskStatus = (TextView) itemView.findViewById(R.id.taskStatus);
        }

        public void initCallViews(View view) {
            nameText = (TextView) view.findViewById(R.id.name_txt);
            numberText = (TextView) view.findViewById(R.id.number_txt);
            durationtext = (TextView) view.findViewById(R.id.duration_txt);
            timeText = (TextView) view.findViewById(R.id.time_txt);
            callTypeimage = (ImageView) view.findViewById(R.id.type_image);
        }

        public void initlocationViews(View view) {
            locationText = (TextView) view.findViewById(R.id.location_loc);
            timeText = (TextView) view.findViewById(R.id.time_loc);

        }
        public void initRecentActivitiesView(View view){
            locationText = (TextView) view.findViewById(R.id.location_loc);
            timeText = (TextView) view.findViewById(R.id.time_loc);
            app_text = (TextView) view.findViewById(R.id.appname);
            icon = (ImageView) view.findViewById(R.id.icon);
        }

    }


}
