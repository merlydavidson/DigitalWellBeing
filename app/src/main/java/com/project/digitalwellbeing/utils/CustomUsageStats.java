package com.project.digitalwellbeing.utils;


import android.app.usage.UsageStats;
import android.graphics.drawable.Drawable;


public class CustomUsageStats {
    public UsageStats usageStats;
    public Drawable appIcon;
    public boolean isChecked;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
