package com.project.digitalwellbeing.data.model;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.project.digitalwellbeing.utils.CommonDataArea;

@Database(version = 6, entities = {UserInfo.class,UserDetails.class,LogDetails.class,TaskDetails.class,CallDetails.class,BlockedApps.class,LockUnlock.class, GoogleFitDetails.class})
public abstract class AppDataBase extends RoomDatabase {
    public abstract DigitalWellBeingDao userDetailsDao();


    private static AppDataBase appDataBase;

    public static AppDataBase getInstance(Context context) {
        if (null == appDataBase) {
            appDataBase = buildDatabaseInstance(context);
        }
        return appDataBase;
    }

    private static AppDataBase buildDatabaseInstance(Context context) {
        return Room.databaseBuilder(context,
                AppDataBase.class,
                CommonDataArea.DB_NAME)
                .allowMainThreadQueries().build();
    }


    @NonNull
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @NonNull
    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

    @Override
    public void clearAllTables() {

    }
}
