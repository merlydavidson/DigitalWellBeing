package com.project.digitalwellbeing.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.digitalwellbeing.R;
import com.project.digitalwellbeing.data.model.CallDetails;

import java.util.List;

public class GenericAdapter extends RecyclerView.Adapter<GenericAdapter.ViewHolder> {
    List<CallDetails> callDetails;
    Context context;

    public GenericAdapter(List<CallDetails> callDetails, Context context) {
        this.callDetails = callDetails;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.call_view, parent, false);

        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (callDetails.size() > 0) {
            holder.nameText.setText(callDetails.get(position).getCallerName());
            holder.numberText.setText(callDetails.get(position).getCallerNumber());
            holder.durationtext.setText(callDetails.get(position).getCallDuration());
            holder.timeText.setText(callDetails.get(position).getCallTimeStamp());
            if (callDetails.get(position).getCallType() == 1)
                holder.callTypeimage.setBackgroundResource(R.drawable.ic_call_incoming);
            else if (callDetails.get(position).getCallType() == 2)
                holder.callTypeimage.setBackgroundResource(R.drawable.ic_call_outgoing);
            else if (callDetails.get(position).getCallType() == 3)
                holder.callTypeimage.setBackgroundResource(R.drawable.ic_call_missed);


        }

    }

    @Override
    public int getItemCount() {
        return callDetails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, numberText, durationtext, timeText;
        ImageView callTypeimage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = (TextView) itemView.findViewById(R.id.name_txt);
            numberText = (TextView) itemView.findViewById(R.id.number_txt);
            durationtext = (TextView) itemView.findViewById(R.id.duration_txt);
            timeText = (TextView) itemView.findViewById(R.id.time_txt);
            callTypeimage = (ImageView) itemView.findViewById(R.id.type_image);
        }
    }
}
