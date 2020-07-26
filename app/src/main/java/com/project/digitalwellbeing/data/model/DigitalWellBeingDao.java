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
    void insertGoogleDetails(GoogleFitDetails googleFitDetails);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCallDetails(CallDetails callDetails);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSelectedAppps(BlockedApps blockedApps);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLockUnlockData(LockUnlock lockUnlock);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAppDta(BlockedApps blockedApps);

    @Query("SELECT * FROM UserInfo where phoneNumber = :phNo AND password = :password")
    UserInfo getUserData(String phNo,String password);

    @Query("SELECT * FROM GoogleFitDetails where date = :date")
   List<GoogleFitDetails>  getGoogleData(String date);

    @Query("SELECT * FROM UserDetails")
    List<UserDetails> getUserDetails();

    @Query("SELECT * FROM BlockedApps where childId =:uuid")
    List<BlockedApps> getAppData(String uuid);

    @Query("SELECT * FROM LogDetails where childId =:id")
    List<LogDetails> getLogDetails(String id);

    @Query("SELECT * FROM CallDetails where date =:date")
    List<CallDetails> getCallDetails(String date);

    @Query("SELECT * FROM LockUnlock where childId =:id")
   LockUnlock getLockUnlockDetails(String id);

    @Query("SELECT * FROM LockUnlock where childId =:id")
   List<LockUnlock> getLockUnlockDetailsList(String id);

    @Query("SELECT * FROM CallDetails where childId =:id")
    List<CallDetails> getCallDetails2(String id);

    @Query("SELECT * FROM CallDetails where callerLogId = :id and childId =:chId")
    List<CallDetails> getaCallDetails(int id,String chId);

    @Query("SELECT * FROM TaskDetails ")
    List<TaskDetails> getaTaskDetails();

    @Query("SELECT * FROM TaskDetails where childId =:id")
    List<TaskDetails> getaTaskDetails2(String id);

    @Query("SELECT * FROM UserInfo ")
    List<UserInfo> getUserInfo();

    @Query("SELECT * FROM TaskDetails where date = :date")
    List<TaskDetails> getTasks(String date);

    @Query("SELECT EXISTS ( SELECT * FROM BlockedApps where packagename = :name)")
   Boolean getBlockedAppDetails(String name);

    @Query("SELECT EXISTS ( SELECT * FROM BlockedApps where packagename = :name and childId =:chid)")
    Boolean ifAppDetailsExists(String name,String chid);

    @Query("SELECT * FROM BlockedApps where packagename = :pName ")
    BlockedApps getBlockedAppDetail(String pName);

    @Query("SELECT EXISTS ( SELECT * FROM TaskDetails where logId = :id and childId =:cId)")
    Boolean taskExists(int id,String cId);

    @Query("SELECT EXISTS ( SELECT * FROM LockUnlock where childId = :id)")
    Boolean LockUnLock(String id);

    @Query("SELECT EXISTS ( SELECT * FROM logdetails where childId = :id and logId =:logid)")
    Boolean checkLogExists(String id,int logid);

    @Query("SELECT EXISTS ( SELECT * FROM UserInfo where phoneNumber = :phNo AND password = :password)")
    Boolean checkLoginppDetails(String phNo,String password);

    @Query("SELECT * FROM BlockedApps where isChecked = :checked")
    List<BlockedApps> getBlockedApps(boolean checked);

    @Query("DELETE FROM BlockedApps WHERE packagename = :packagename")
    void deleteByPackagename(String packagename);

    @Query("DELETE FROM GoogleFitDetails WHERE date = :date")
    void deleteGoogleDetails(String date);

    @Query("SELECT * FROM TaskDetails where date = :date AND status = :status")
    List<TaskDetails> getCompletedTasks(String date,int status);



    @Query("UPDATE TaskDetails SET status= :status WHERE logId = :logid")
    public abstract int updateTaskdetails(int logid, int status);

    @Query("UPDATE LockUnlock SET isLocked= :status AND password =:pass WHERE childId = :id")
    public abstract int updateLockUnlock(String id, boolean status,String pass);

    @Query("UPDATE BlockedApps SET totalTimeInForeground =:forTime  WHERE packagename = :name AND childId =:chid")
    public abstract int updateAppDetails(long forTime,String name,String chid);

    @Query("UPDATE BlockedApps SET isChecked =:checked WHERE packagename = :name")
    public abstract int updateBlockStatus(boolean checked, String name);
}
