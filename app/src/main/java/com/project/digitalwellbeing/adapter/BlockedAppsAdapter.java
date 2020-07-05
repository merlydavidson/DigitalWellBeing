package com.project.digitalwellbeing.adapter;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.project.digitalwellbeing.R;

import com.project.digitalwellbeing.data.model.BlockedApps;
import com.project.digitalwellbeing.utils.CustomUsageStats;

import java.util.List;


public class BlockedAppsAdapter extends RecyclerView.Adapter<BlockedAppsAdapter.ViewHolder> {
    List<BlockedApps> blockedAppDeails;
    Context mContext;

    // RecyclerView recyclerView;
    public BlockedAppsAdapter(List<BlockedApps> blockedAppDeails, Context mContext) {
        this.blockedAppDeails = blockedAppDeails;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.usage_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (blockedAppDeails.size() > 0) {
            try {
                Drawable icon = mContext.getPackageManager().getApplicationIcon(blockedAppDeails.get(position).getPackagename());
                holder.mAppIcon.setImageDrawable(icon);
                final PackageManager pm = mContext.getPackageManager();
                ApplicationInfo ai;
                ai = pm.getApplicationInfo(blockedAppDeails.get(position).getPackagename(), 0);
                final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
                holder.mPackageName.setText(applicationName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

        }
        holder.selector.setOnCheckedChangeListener(null);
        holder.selector.setChecked(blockedAppDeails.get(position).getChecked());
        holder.selector.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                blockedAppDeails.get(position).setChecked(isChecked);
            }
        });
    }

    public List<BlockedApps> getUnblocklist(){
        return blockedAppDeails;
    }
    public void deleteFromList(){
        for(BlockedApps b:blockedAppDeails){

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
        private final ProgressBar pb;
        CheckBox selector;

        public ViewHolder(View v) {
            super(v);
            mPackageName = (TextView) v.findViewById(R.id.textview_package_name);
            mLastTimeUsed = (TextView) v.findViewById(R.id.textview_total_time);
            mAppIcon = (ImageView) v.findViewById(R.id.app_icon);
            mPercentage = (TextView) v.findViewById(R.id.percentage);
            pb = (ProgressBar) v.findViewById(R.id.pb);
            selector = v.findViewById(R.id.selector);
        }
    }
}  