package com.breezytechdevelopers.healthapp.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.breezytechdevelopers.healthapp.database.entities.Timer;

@Dao
public interface TimerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Timer ping_otp_timer);
    
    @Update
    void update(Timer timer);

    @Query("DELETE FROM timer")
    void deleteTimers();

    @Query("SELECT timerMillisLeft " +
            "from timer WHERE timerID LIKE :timerID")
    LiveData<Long> getPingTimerMillisLeft(String timerID);

    @Query("SELECT timerMillisLeft " +
            "from timer WHERE timerID LIKE :timerID")
    LiveData<Long> getOtpTimerMillisLeft(String timerID);
}
