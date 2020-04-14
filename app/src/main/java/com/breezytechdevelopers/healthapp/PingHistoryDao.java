package com.breezytechdevelopers.healthapp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.breezytechdevelopers.healthapp.database.entities.PingHistory;

import java.util.List;

@Dao
public
interface PingHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<PingHistory> pingHistories);

    @Query("DELETE FROM pinghistory")
    void deletePingHistory();

    @Query("SELECT * from pinghistory")
    LiveData<List<PingHistory>> getPingHistories();
}
