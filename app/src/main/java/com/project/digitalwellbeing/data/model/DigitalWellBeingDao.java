package com.project.digitalwellbeing.data.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DigitalWellBeingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUserInfo(UserInfo userInfo);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUserDetails(UserDetails userDetails);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLogDetails(LogDetails logDetails);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTaskDetails(TaskDetails taskDetails);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCallDetails(CallDetails callDetails);

    @Query("SELECT * FROM UserInfo")
    List<UserInfo> getUserInfo();

    @Query("SELECT * FROM UserDetails")
    List<UserDetails> getUserDetails();

    @Query("SELECT * FROM LogDetails")
    List<LogDetails> getLogDetails();

    @Query("SELECT * FROM CallDetails")
    List<CallDetails> getCallDetails();

    @Query("SELECT * FROM CallDetails where callerLogId = :id")
    List<CallDetails> getaCallDetails(int id);

    @Query("SELECT * FROM TaskDetails ")
    List<TaskDetails> getaTaskDetails();
}
