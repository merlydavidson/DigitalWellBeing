package com.project.digitalwellbeing.fragment;


import android.app.ProgressDialog;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.project.digitalwellbeing.BlockedAppsActivity;
import com.project.digitalwellbeing.R;
import com.project.digitalwellbeing.adapter.UsageListAdapter;
import com.project.digitalwellbeing.data.model.AppDataBase;
import com.project.digitalwellbeing.data.model.BlockedApps;
import com.project.digitalwellbeing.data.model.DigitalWellBeingDao;
import com.project.digitalwellbeing.utils.CommonDataArea;
import com.project.digitalwellbeing.utils.CommonFunctionArea;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


public class AppUsageStatisticsFragment extends Fragment {
    private static final String TAG = AppUsageStatisticsFragment.class.getSimpleName();
    ProgressDialog progress;
    //VisibleForTesting for variables below
    UsageStatsManager mUsageStatsManager;
    UsageListAdapter mUsageListAdapter;
    RecyclerView mRecyclerView;
    Button mOpenUsageSettingButton, block;
    Spinner mSpinnerTimeSpan;
    Spinner mSpinnerSort;
    FloatingActionMenu materialDesignFAM;
    FloatingActionButton floatingActionButton;
    List<BlockedApps> usageStatsList;
    List<BlockedApps> selectedItems;
    private GridLayoutManager mGridLayoutManager;

    public AppUsageStatisticsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment {@link AppUsageStatisticsFragment}.
     */
    public static AppUsageStatisticsFragment newInstance() {
        AppUsageStatisticsFragment fragment = new AppUsageStatisticsFragment();
        return fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mUsageStatsManager = (UsageStatsManager) getActivity()
                .getSystemService(Context.USAGE_STATS_SERVICE);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_app_usage_statistics, container, false);
    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);
        block = rootView.findViewById(R.id.blockapps);
        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
        mUsageListAdapter = new UsageListAdapter(getContext());
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_app_usage);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        //mRecyclerView.scrollToPosition(0);
        mRecyclerView.setAdapter(mUsageListAdapter);
        mOpenUsageSettingButton = (Button) rootView.findViewById(R.id.button_open_usage_setting);
        progress = ProgressDialog.show(getActivity(), "Loading..",
                "Please wait...", true);
        progress.show();
        mSpinnerTimeSpan = (Spinner) rootView.findViewById(R.id.spinner_time_span);
        if (CommonDataArea.ROLE == 1) {
            floatingActionButton.setVisibility(View.GONE);
            block.setVisibility(View.GONE);
        }
        block.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                selectedItems = mUsageListAdapter.getSelectedItems();
                if (selectedItems == null || selectedItems.size() == 0) {
                    Toast.makeText(getActivity(), "No application selected", Toast.LENGTH_SHORT).show();
                } else {
                    AppDataBase appDataBase = AppDataBase.getInstance(getActivity());
                    DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();

                    for (BlockedApps b : selectedItems) {
                        // CustomUsageStats states = b;
                        if (b.getChecked()) {
                            digitalWellBeingDao.updateBlockStatus2(true, b.getPackagename(), "0", b.getChildId());
                               /* if (!digitalWellBeingDao.getBlockedAppDetails(b.getPackagename())) {
                                    BlockedApps blockedApps = new BlockedApps();
                                    blockedApps.setPackagename(b.getPackagename());
                                    blockedApps.setDate(CommonDataArea.getDAte("dd/MM/yyyy"));
                                    blockedApps.setChildId( CommonDataArea.FIREBASETOPIC);
                                    digitalWellBeingDao.insertSelectedAppps(blockedApps);
                                }*/
                        }
                    }
                    // Toast.makeText(getActivity(), "Apps Blocked Successfully", Toast.LENGTH_SHORT).show();
                    mUsageListAdapter.updateViews();
                }
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), BlockedAppsActivity.class));
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();

        setmSpinnerTimeSpanAdapter();


    }

    public void setmSpinnerTimeSpanAdapter() {
        SpinnerAdapter timespan_spinnerAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.action_timespan, android.R.layout.simple_spinner_dropdown_item);
        mSpinnerTimeSpan.setAdapter(timespan_spinnerAdapter);
        mSpinnerTimeSpan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] strings = getResources().getStringArray(R.array.action_timespan);
                StatsUsageInterval statsUsageInterval = StatsUsageInterval
                        .getValue(strings[position]);
                if (statsUsageInterval != null) {
                    usageStatsList = getUsageStatistics(statsUsageInterval.mInterval);
                    Collections.sort(usageStatsList, new timeInForegroundComparator());
                    updateAppsList(usageStatsList);
                    progress.dismiss();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public List<BlockedApps> getUsageStatistics(int intervalType) {
        // Get the app statistics since one year ago from the current time.
        List<UsageStats> queryUsageStats = new ArrayList<UsageStats>();
        AppDataBase appDataBase = AppDataBase.getInstance(getActivity());
        DigitalWellBeingDao digitalWellBeingDao = appDataBase.userDetailsDao();
        int role = CommonDataArea.ROLE;
        if (role == 1) {
            Calendar cal = Calendar.getInstance();
            if (intervalType == UsageStatsManager.INTERVAL_DAILY) {
                cal.add(Calendar.DATE, -1);
                cal.set(Calendar.MILLISECOND, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.HOUR_OF_DAY, 0);
            } else if (intervalType == UsageStatsManager.INTERVAL_WEEKLY)
                cal.add(Calendar.DATE, -7);
            else if (intervalType == UsageStatsManager.INTERVAL_MONTHLY)
                cal.add(Calendar.MONTH, -1);
            else if (intervalType == UsageStatsManager.INTERVAL_YEARLY)
                cal.add(Calendar.YEAR, -1);

            Map<String, UsageStats> queryUsageStatsMap = mUsageStatsManager
                    .queryAndAggregateUsageStats(cal.getTimeInMillis(),
                            System.currentTimeMillis());

            for (Map.Entry<String, UsageStats> stat : queryUsageStatsMap.entrySet()) {
                queryUsageStats.add(stat.getValue());
            }


            if (queryUsageStats.isEmpty()) {
                Log.i(TAG, "The user may not allow the access to apps usage. ");
                Toast.makeText(getActivity(),
                        getString(R.string.explanation_access_to_appusage_is_not_enabled),
                        Toast.LENGTH_LONG).show();
                mOpenUsageSettingButton.setVisibility(View.VISIBLE);
                mOpenUsageSettingButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                    }
                });
            } else {

                for (UsageStats u : queryUsageStats) {
                    long time=u.getTotalTimeInForeground();
                    int h=(int)(time/1000/60/60);
                    int m=(int)((time/1000/60) % 60);
                    float ti=Float.parseFloat(h+"."+m);
                    if (!digitalWellBeingDao.getBlockedAppDetails(u.getPackageName())
                            ) {
                        if(ti>0.30) {
                            BlockedApps blockedApps = new BlockedApps();
                            blockedApps.setPackagename(u.getPackageName());
                            blockedApps.setLastTimeUsed(u.getLastTimeUsed());
                            blockedApps.setTotalTimeInForeground(u.getTotalTimeInForeground());
                            blockedApps.setChildId(CommonDataArea.CURRENTCHILDID);
                            blockedApps.setChecked(false);
                            PackageManager pm = getActivity().getPackageManager();
                            try {
                                ApplicationInfo ai = pm.getApplicationInfo(u.getPackageName(), 0);
                                final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
                                blockedApps.setAppname(applicationName);
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }

                            digitalWellBeingDao.insertAppDta(blockedApps);
                        }
                    } else {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                            long t = u.getTotalTimeVisible();
                            long t1 = u.getLastTimeForegroundServiceUsed();
                        }

                        long t2 = u.getTotalTimeInForeground();
                        digitalWellBeingDao.updateAppDetails(t2,
                                u.getPackageName(), "0", CommonFunctionArea.getDeviceUUID(getActivity()));

                    }
                }
            }
        }
        return digitalWellBeingDao.getAppData(CommonDataArea.CURRENTCHILDID);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void updateAppsList(List<BlockedApps> usageStatsList) {

        mUsageListAdapter.setCustomUsageStatsList(usageStatsList);
        mUsageListAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(0);
    }


    static enum StatsUsageInterval {
        DAILY("Daily", UsageStatsManager.INTERVAL_DAILY),
        WEEKLY("Weekly", UsageStatsManager.INTERVAL_WEEKLY),
        MONTHLY("Monthly", UsageStatsManager.INTERVAL_MONTHLY),
        YEARLY("Yearly", UsageStatsManager.INTERVAL_YEARLY);

        private int mInterval;
        private String mStringRepresentation;

        StatsUsageInterval(String stringRepresentation, int interval) {
            mStringRepresentation = stringRepresentation;
            mInterval = interval;
        }

        static StatsUsageInterval getValue(String stringRepresentation) {
            for (StatsUsageInterval statsUsageInterval : values()) {
                if (statsUsageInterval.mStringRepresentation.equals(stringRepresentation)) {
                    return statsUsageInterval;
                }
            }
            return null;
        }
    }

    private class timeInForegroundComparator implements Comparator<BlockedApps> {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public int compare(BlockedApps left, BlockedApps right) {
            return Long.compare(right.getTotalTimeInForeground(), left.getTotalTimeInForeground());
        }
    }

    private class AlphabeticComparator implements Comparator<UsageStats> {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public int compare(UsageStats left, UsageStats right) {
            PackageManager pm = getActivity().getPackageManager();
            ApplicationInfo ai_left = null;
            ApplicationInfo ai_right = null;
            try {
                ai_left = pm.getApplicationInfo(left.getPackageName(), 0);
                ai_right = pm.getApplicationInfo(right.getPackageName(), 0);

            } catch (final PackageManager.NameNotFoundException e) {
                ai_left = null;
                ai_right = null;
            }
            final String applicationNameLeft = (String) (ai_left != null ? pm.getApplicationLabel(ai_left) : "(unknown)");
            final String applicationNameRight = (String) (ai_right != null ? pm.getApplicationLabel(ai_right) : "(unknown)");
            return applicationNameLeft.compareTo(applicationNameRight);

        }
    }

}
