package com.project.digitalwellbeing.adapter;


import android.app.Dialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;


import com.project.digitalwellbeing.BarChartView;
import com.project.digitalwellbeing.R;
import com.project.digitalwellbeing.data.model.AppDataBase;
import com.project.digitalwellbeing.data.model.BlockedApps;
import com.project.digitalwellbeing.data.model.DigitalWellBeingDao;
import com.project.digitalwellbeing.data.model.TaskDetails;
import com.project.digitalwellbeing.utils.CommonDataArea;
import com.project.digitalwellbeing.utils.CustomUsageStats;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class UsageListAdapter extends RecyclerView.Adapter<UsageListAdapter.ViewHolder> {

    private List<BlockedApps> mCustomUsageStatsList = new ArrayList<>();
    int flag1=0;
    int flag2=0;
    private Context context;
    private long total;
    ArrayList<Float> xdata;
    ArrayList<String>ydata;
    DigitalWellBeingDao digitalWellBeingDao;
    public UsageListAdapter(Context c)
    {
        context=c;
        AppDataBase appDataBase = AppDataBase.getInstance(context);
        digitalWellBeingDao = appDataBase.userDetailsDao();
       xdata=new ArrayList<>();
       ydata=new ArrayList<>();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mPackageName;
        private final TextView mLastTimeUsed;
        private final ImageView mAppIcon;
        private final TextView mPercentage;
        private final ProgressBar pb ;
        CheckBox selector;
        private Context mContext;
        ImageView lock;
        public ViewHolder(View v) {
            super(v);

            mPackageName = (TextView) v.findViewById(R.id.textview_package_name);
            mLastTimeUsed = (TextView) v.findViewById(R.id.textview_total_time);
            mAppIcon = (ImageView) v.findViewById(R.id.app_icon);
            mPercentage=(TextView) v.findViewById(R.id.percentage);
            lock=(ImageView) v.findViewById(R.id.lock);
            pb=(ProgressBar) v.findViewById(R.id.pb);
            selector=v.findViewById(R.id.selector);
        }


        public TextView getLastTimeUsed() {
            return mLastTimeUsed;
        }

        public TextView getPackageName() {
            return mPackageName;
        }

        public ImageView getAppIcon() {
            return mAppIcon;
        }

        public TextView getPercentage() {
            return mPercentage;
        }
        public ProgressBar getProgressBar() {
            return pb;
        }
    }

    public UsageListAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.appusage_list_item, viewGroup, false);

        return new ViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        double j;

        final PackageManager pm= context.getPackageManager();
        ApplicationInfo ai=null;
        try {
            ai=pm.getApplicationInfo(mCustomUsageStatsList.get(position).getPackagename(), 0);

        }catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
        viewHolder.getPackageName().setText(applicationName);

       ydata.add(applicationName);

        final long timeInForeground = mCustomUsageStatsList.get(position).getTotalTimeInForeground();
          viewHolder.getLastTimeUsed().setText(calculateTime(timeInForeground));
        double percent=timeInForeground*100.0/(double)total;
        viewHolder.getPercentage().setText(calculatePercent(timeInForeground));
       if(total!=0)
        j=(timeInForeground*100.0)/(double) total;
        else
            j=0;
        xdata.add((float) j);
        BarChartView.addDataSet(xdata,ydata);
        //BarChartView.addDataSet();
        Drawable icon = null;
        try {
            icon = context.getPackageManager().getApplicationIcon(mCustomUsageStatsList.get(position).getPackagename());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        viewHolder.getAppIcon().setImageDrawable(icon);
        if(percent>10)
        {
            viewHolder.getProgressBar().setProgressTintList(ColorStateList.valueOf(Color.RED));
        }else{
            viewHolder.getProgressBar().setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        }
        if (CommonDataArea.ROLE == 1){
            //TODO:viewHolder.selector.setVisibility(View.GONE);
          //  viewHolder.selector.setVisibility(View.GONE);
        }

        viewHolder.getProgressBar().setProgress((int)percent);
        viewHolder.selector.setOnCheckedChangeListener(null);

        BlockedApps blockedApps = digitalWellBeingDao.getBlockedAppDetail(mCustomUsageStatsList.get(position).getPackagename());
        if(blockedApps.getChecked()) {
            viewHolder.lock.setVisibility(View.VISIBLE);
            viewHolder.selector.setVisibility(View.GONE);
        }else{
            viewHolder.lock.setVisibility(View.GONE);
            viewHolder.selector.setVisibility(View.VISIBLE);
        }


        viewHolder.selector.setChecked(mCustomUsageStatsList.get(position).getChecked());
        viewHolder.selector.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCustomUsageStatsList.get(position).setChecked(isChecked);
                if(isChecked)
                    viewHolder.lock.setVisibility(View.VISIBLE);
                else
                    viewHolder.lock.setVisibility(View.GONE);
            }
        });

        /* onItemClickListener() */
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DateFormat dateFormat= SimpleDateFormat.getDateTimeInstance();
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.app_dialog);
                dialog.setTitle("Usage Details");
                TextView text = (TextView) dialog.findViewById(R.id.appname);
                text.setText(applicationName);
                TextView lastused = (TextView) dialog.findViewById(R.id.last_used);
                lastused.setText("Last Used : "+dateFormat.format(new Date(mCustomUsageStatsList.get(position).getLastTimeUsed())));
                TextView totalused = (TextView) dialog.findViewById(R.id.total_used);
                totalused.setText("Total time used : "+timeInForeground/1000 +" sec");
                ImageView image = (ImageView) dialog.findViewById(R.id.image_icon);
                Drawable icon = null;
                try {
                    icon = context.getPackageManager().getApplicationIcon(mCustomUsageStatsList.get(position).getPackagename());
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                image.setImageDrawable(icon);

                Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private long totalTime(List<BlockedApps> list){
        long total=0;
        for(BlockedApps app:list){
            total+=app.getTotalTimeInForeground();
        }
        return total;
    }
    public List<BlockedApps> getSelectedItems(){

        return mCustomUsageStatsList;
    }
    public void updateViews(){
        for(BlockedApps s:mCustomUsageStatsList){
            if(s.getChecked()){
                s.setChecked(false);
            }
        }
        notifyDataSetChanged();
    }
    private String calculateTime(long ms)
    { String total="";
        long sec=ms/1000;
        long day;
        long hour;
        long min;
        if(sec>=(86400)){
            day=sec/86400;
            sec=sec%86400;
            total=total+day+"d";
        }
        if(sec>=3600){
            hour=sec/3600;
            sec=sec%3600;
            total=total+hour+"h";
        }
        if(sec>=60){
            min=sec/60;
            sec=sec%60;
            total=total+min+"m";
        }
        if(sec>0)
        {
            total=total+sec+"s";
        }
        return total;
    }

    private String calculatePercent(long ms) {
        DecimalFormat f = new DecimalFormat("##.00");
        return f.format(ms*100.0/(double)total)+"%";
    }
    @Override
    public int getItemCount() {
        return mCustomUsageStatsList.size();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setCustomUsageStatsList(List<BlockedApps> customUsageStats) {
        mCustomUsageStatsList = customUsageStats;
        total=totalTime(mCustomUsageStatsList);
        System.out.println("total time :"+total);
    }
    public ArrayList<String> getYdata(){
        return ydata;
    }
    public ArrayList<Float> getXdata(){
        return xdata;
    }
}