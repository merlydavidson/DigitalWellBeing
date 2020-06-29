package com.project.digitalwellbeing.adapter;



import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;


import com.project.digitalwellbeing.BarChartView;
import com.project.digitalwellbeing.fragment.AppUsageStatisticsFragment;


public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                AppUsageStatisticsFragment tab1 = new AppUsageStatisticsFragment();
                return tab1;
            case 1:
                BarChartView tab2 = new BarChartView();
                return tab2;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
