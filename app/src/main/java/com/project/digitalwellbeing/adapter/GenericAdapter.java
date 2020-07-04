package com.project.digitalwellbeing.adapter;

import android.content.Context;
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

import java.util.List;

public class GenericAdapter extends RecyclerView.Adapter<GenericAdapter.ViewHolder> {
    List<CallDetails> callDetails;
    Context context;
    List<LogDetails> logDetails;
    int callingfrom = 0;
    List<TaskDetails> taskDetails;

    public GenericAdapter(List<CallDetails> callDetails, Context context, int callingfrom) {
        this.callDetails = callDetails;
        this.context = context;
        this.callingfrom = callingfrom;
    }

    public GenericAdapter(Context context, List<LogDetails> logDetails, int callingfrom) {
        this.context = context;
        this.logDetails = logDetails;
        this.callingfrom = callingfrom;
    }

    public GenericAdapter(Context context, int callingfrom, List<TaskDetails> taskDetails) {
        this.context = context;
        this.callingfrom = callingfrom;
        this.taskDetails = taskDetails;

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

    }

    private void bindTaskDetails(ViewHolder holder, int position) {
        if (taskDetails.size() > 0) {
            TaskDetails taskDetail=taskDetails.get(position);
            holder.taskName.setText(taskDetails.get(position).getTaskName());
            holder.taskDate.setText(taskDetails.get(position).getDate());
            Log.d("debuggenericadapter",taskDetail.getTaskName());
            holder.taskTime.setText(taskDetails.get(position).getStarttime() + " - "+taskDetails.get(position).getEndtime());
            if(taskDetails.get(position).getStatus()==0)
            holder.taskStatus.setText("Status : Pending");
            else if(taskDetails.get(position).getStatus()==1)
                holder.taskStatus.setText("Status : Completed");
            else if(taskDetails.get(position).getStatus()==2)
                holder.taskStatus.setText("Status : InComplete");

        }
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
        if(callingfrom==3)
            return taskDetails.size();
        else if(callingfrom==1)
            return callDetails.size();
        else if(callingfrom==2)
            return  logDetails.size();
        else return 0;
    }

    public void bindCalldetails(ViewHolder holder, int position) {
        if (callDetails.size() > 0) {
            if (callDetails.get(position).getCallerName().equalsIgnoreCase(""))
                holder.nameText.setText("Unkown");
            else
                holder.nameText.setText(callDetails.get(position).getCallerName());
            holder.numberText.setText(callDetails.get(position).getCallerNumber());

            holder.durationtext.setText("Call Duration : " + callDetails.get(position).getCallDuration());
            holder.timeText.setText(callDetails.get(position).getCallTimeStamp());
            if (callDetails.get(position).getCallType()!=null && callDetails.get(position).getCallType() .equalsIgnoreCase("INCOMING"))
                holder.callTypeimage.setBackgroundResource(R.drawable.ic_call_incoming);
            else if (callDetails.get(position).getCallType()!=null && callDetails.get(position).getCallType().equalsIgnoreCase("OUTGOING"))
                holder.callTypeimage.setBackgroundResource(R.drawable.ic_call_outgoing);
            else if (callDetails.get(position).getCallType()!=null && callDetails.get(position).getCallType().equalsIgnoreCase("MISSED"))
                holder.callTypeimage.setBackgroundResource(R.drawable.ic_call_missed);


        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, numberText, durationtext, timeText, locationText, taskName, taskDate, taskTime, taskStatus;
        ImageView callTypeimage;

        public ViewHolder(@NonNull View itemView, int callingFrom) {
            super(itemView);
            if (callingFrom == 1)
                initCallViews(itemView);
            if (callingFrom == 2)
                initlocationViews(itemView);
            if (callingFrom == 3)
                initTaskViews(itemView);

        }

        private void initTaskViews(View itemView) {
            taskName = (TextView) itemView.findViewById(R.id.taskName);
            taskDate = (TextView) itemView.findViewById(R.id.taskDate);
            taskTime = (TextView) itemView.findViewById(R.id.taskTime);
            taskStatus = (TextView) itemView.findViewById(R.id.taskStatus);
        }

        public void initCallViews(View view) {
            nameText = (TextView) itemView.findViewById(R.id.name_txt);
            numberText = (TextView) itemView.findViewById(R.id.number_txt);
            durationtext = (TextView) itemView.findViewById(R.id.duration_txt);
            timeText = (TextView) itemView.findViewById(R.id.time_txt);
            callTypeimage = (ImageView) itemView.findViewById(R.id.type_image);
        }

        public void initlocationViews(View view) {
            locationText = (TextView) view.findViewById(R.id.location_loc);
            timeText = (TextView) view.findViewById(R.id.time_loc);
        }
    }
}
