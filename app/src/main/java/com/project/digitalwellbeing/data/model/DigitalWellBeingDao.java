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

    @Query("SELECT * FROM BlockedApps where childId =:uuid AND acknowlwdgement =:ack")
    List<BlockedApps> getAppDataAck(String uuid,String ack);

    @Query("SELECT * FROM LogDetails where childId =:id")
    List<LogDetails> getLogDetails(String id);

    @Query("SELECT * FROM LogDetails where childId =:id AND acknowlwdgement =:ack")
    List<LogDetails> getLogDetails2(String id,String ack);

    @Query("SELECT * FROM CallDetails where acknowlwdgement =:ack")
    List<CallDetails> getCallDetails(String ack);

    @Query("SELECT * FROM LockUnlock where childId =:id")
   LockUnlock getLockUnlockDetails(String id);

    @Query("SELECT * FROM LockUnlock where acknowledgement =:ack AND childId =:uuid")
   List<LockUnlock> getLockUnlockDetailsList(String ack,String uuid);

    @Query("SELECT * FROM CallDetails where childId =:id")
    List<CallDetails> getCallDetails2(String id);

    @Query("SELECT * FROM CallDetails where callerLogId = :id and childId =:chId")
    List<CallDetails> getaCallDetails(int id,String chId);

    @Query("SELECT * FROM TaskDetails where acknowlwdgement =:ack and childId =:uuid")
    List<TaskDetails> getaTaskDetails(String ack,String uuid);

    @Query("SELECT * FROM TaskDetails where childId =:id")
    List<TaskDetails> getaTaskDetails2(String id);

    @Query("SELECT * FROM TaskDetails where childId =:id AND acknowlwdgement =:ack")
    List<TaskDetails> getaTaskDetailsAck(String id,String ack);

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

    @Query("SELECT EXISTS ( SELECT * FROM LockUnlock where childId = :chid AND id =:id)")
    Boolean LockUnLock1(int id,String chid);

    @Query("SELECT EXISTS ( SELECT * FROM logdetails where childId = :id and logId =:logid)")
    Boolean checkLogExists(String id,int logid);

    @Query("SELECT EXISTS ( SELECT * FROM UserInfo where phoneNumber = :phNo AND password = :password)")
    Boolean checkLoginppDetails(String phNo,String password);

    @Query("SELECT EXISTS ( SELECT * FROM LogDetails where childId = :chid AND logId = :logid)")
    Boolean checkLogdetails(String chid,int logid);

    @Query("SELECT * FROM BlockedApps where isChecked = :checked")
    List<BlockedApps> getBlockedApps(boolean checked);

    @Query("SELECT * FROM BlockedApps where acknowlwdgement = :ack AND childId =:uuid")
    List<BlockedApps> getBlockedAppsAck(String ack,String uuid);

    @Query("DELETE FROM BlockedApps WHERE packagename = :packagename")
    void deleteByPackagename(String packagename);

    @Query("DELETE FROM GoogleFitDetails WHERE date = :date")
    void deleteGoogleDetails(String date);

    @Query("SELECT * FROM TaskDetails where date = :date AND status = :status")
    List<TaskDetails> getCompletedTasks(String date,int status);

    @Query("UPDATE CallDetails SET acknowlwdgement =:ack WHERE callerId = :logid AND childId =:chid")
    public abstract int updateCallDetailStatus(int logid,String ack, String chid);

    @Query("UPDATE TaskDetails SET status= :status , acknowlwdgement =:ack WHERE logId = :logid")
    public abstract int updateTaskdetails(int logid,String ack, int status);

    @Query("UPDATE TaskDetails SET acknowlwdgement =:ack WHERE logId = :logid AND childId =:chid")
    public abstract int updateTaskdetailsStatus(int logid,String ack, String chid);

    @Query("UPDATE TaskDetails SET status= :status , acknowlwdgement =:ack WHERE childId = :chid AND logId =:logId")
    public abstract int updateTaskdetails2(String chid,String ack, int status,int logId);

    @Query("UPDATE LockUnlock SET isLocked= :status , password =:pass , acknowledgement =:ack WHERE childId = :id")
    public abstract int updateLockUnlock(String id, boolean status,String pass,String ack);

    @Query("UPDATE LockUnlock SET acknowledgement =:ack WHERE childId = :id AND id =:id")
    public abstract int updateLockUnlockAck(int id, String ack);

    @Query("UPDATE BlockedApps SET totalTimeInForeground =:forTime , acknowlwdgement =:ack  WHERE packagename = :name AND childId =:chid")
    public abstract int updateAppDetails(long forTime,String name,String ack,String chid);

    @Query("UPDATE BlockedApps SET isChecked =:checked , acknowlwdgement =:ack WHERE packagename = :name")
    public abstract int updateBlockStatus(boolean checked, String name,String ack);

    @Query("UPDATE BlockedApps SET isChecked =:checked , acknowlwdgement =:ack WHERE packagename = :name AND childId =:chid")
    public abstract int updateBlockStatus2(boolean checked, String name,String ack,String chid);

    @Query("UPDATE BlockedApps SET isChecked =:checked  WHERE packagename = :name AND childId =:chid")
    public abstract int updateBlockStatus3(boolean checked, String name,String chid);

    @Query("UPDATE LogDetails SET  acknowlwdgement =:ack WHERE logId = :logid AND childId =:chid")
    public abstract int updateLogdetailsAck(int logid,String ack, String chid);
}
