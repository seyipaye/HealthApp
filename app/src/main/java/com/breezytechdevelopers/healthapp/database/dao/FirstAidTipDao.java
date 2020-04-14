package com.breezytechdevelopers.healthapp.database.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.breezytechdevelopers.healthapp.database.entities.FirstAidTip;

import java.util.List;

@Dao
public interface FirstAidTipDao {

    // allowing the insert of the same word multiple times by passing a
    // conflict resolution strategy
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<FirstAidTip> firstAidTips);

    @Query("DELETE FROM firstaidtipstable")
    void deleteAll();

    @Query("SELECT * from firstaidtipstable ORDER BY views DESC")
    LiveData<List<FirstAidTip>> getAllTips();

    @Query("SELECT firstaidtipstable.* FROM firstaidtipstable JOIN " +
            "firstaidtipstablefts ON (firstaidtipstable.id = firstaidtipstablefts.id) "
            + "WHERE firstaidtipstablefts MATCH :query")
    LiveData<List<FirstAidTip>> searchAllTips(String query);
}