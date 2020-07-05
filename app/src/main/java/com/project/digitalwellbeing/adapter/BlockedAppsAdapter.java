package com.project.digitalwellbeing.adapter;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.project.digitalwellbeing.R;

import com.project.digitalwellbeing.utils.CustomUsageStats;

import java.util.List;


public class BlockedAppsAdapter extends RecyclerView.Adapter<BlockedAppsAdapter.ViewHolder>{
    List<CustomUsageStats> blockedAppDeails;

    // RecyclerView recyclerView;  
    public BlockedAppsAdapter(List<CustomUsageStats> blockedAppDeails) {
        this.blockedAppDeails = blockedAppDeails;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.usage_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (blockedAppDeails.size() > 0) {
            

        }
    }


    @Override
    public int getItemCount() {
        return blockedAppDeails.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mPackageName;
        private final TextView mLastTimeUsed;
        private final ImageView mAppIcon;
        private final TextView mPercentage;
        private final ProgressBar pb ;
        CheckBox selector;
        public ViewHolder(View v) {
            super(v);
            mPackageName = (TextView) v.findViewById(R.id.textview_package_name);
            mLastTimeUsed = (TextView) v.findViewById(R.id.textview_total_time);
            mAppIcon = (ImageView) v.findViewById(R.id.app_icon);
            mPercentage=(TextView) v.findViewById(R.id.percentage);
            pb=(ProgressBar) v.findViewById(R.id.pb);
            selector=v.findViewById(R.id.selector);
        }
    }
}  