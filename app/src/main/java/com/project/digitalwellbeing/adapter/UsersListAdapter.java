package com.project.digitalwellbeing.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.digitalwellbeing.GoogleFit;
import com.project.digitalwellbeing.R;
import com.project.digitalwellbeing.data.model.LogDetails;
import com.project.digitalwellbeing.data.model.UserDetails;

import java.util.List;

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.ViewHolder> {
    Context context;
    List<UserDetails> userDetails;
    LogDetails logDetails;

    public UsersListAdapter(Context context, List<UserDetails> userDetails, LogDetails logDetails) {
        this.context = context;
        this.userDetails = userDetails;
        this.logDetails = logDetails;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.child_view, parent, false);

        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.childName.setText(userDetails.get(position).getChildName());
        holder.childPlace.setText(logDetails.getLocation());

        if (logDetails.isOnline())
            holder.onoffline.setBackgroundResource(R.drawable.online);
        else
            holder.onoffline.setBackgroundResource(R.drawable.offline);

        holder.linearLayoutChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, GoogleFit.class);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return userDetails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView childName, childPlace;
        ImageView onoffline;
        LinearLayout linearLayoutChild;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            childName = (TextView) itemView.findViewById(R.id.child_name_tv);
            childPlace = (TextView) itemView.findViewById(R.id.child_place_tv);
            onoffline = (ImageView) itemView.findViewById(R.id.onoff_iv);
            linearLayoutChild = (LinearLayout) itemView.findViewById(R.id.child_layout);
        }
    }
}
