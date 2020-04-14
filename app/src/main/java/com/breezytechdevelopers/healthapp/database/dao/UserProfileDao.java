package com.breezytechdevelopers.healthapp.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.breezytechdevelopers.healthapp.database.entities.UserProfile;

@Dao
public interface UserProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserProfile userProfile);

    @Query("DELETE FROM userprofile")
    void deleteUserProfile();

    @Query("SELECT * from userprofile")
    LiveData<UserProfile> getUserProfile();
}
