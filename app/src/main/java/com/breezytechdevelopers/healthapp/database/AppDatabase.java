package com.breezytechdevelopers.healthapp.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.breezytechdevelopers.healthapp.PingHistoryDao;
import com.breezytechdevelopers.healthapp.database.dao.FirstAidTipDao;
import com.breezytechdevelopers.healthapp.database.dao.TimerDao;
import com.breezytechdevelopers.healthapp.database.dao.UserDao;
import com.breezytechdevelopers.healthapp.database.dao.UserProfileDao;
import com.breezytechdevelopers.healthapp.database.entities.FirstAidTip;
import com.breezytechdevelopers.healthapp.database.entities.FirstAidTipFts;
import com.breezytechdevelopers.healthapp.database.entities.PingHistory;
import com.breezytechdevelopers.healthapp.database.entities.Timer;
import com.breezytechdevelopers.healthapp.database.entities.User;
import com.breezytechdevelopers.healthapp.database.entities.UserProfile;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {FirstAidTip.class, FirstAidTipFts.class, User.class, UserProfile.class, PingHistory.class, Timer.class},
        version = 1,
        exportSchema = false
)

public abstract class AppDatabase extends RoomDatabase {

    public abstract FirstAidTipDao firstAidTipDao();
    public abstract UserProfileDao userProfileDao();
    public abstract PingHistoryDao pingHistoryDao();
    public abstract UserDao userDao();
    public abstract TimerDao timerDao();

    private static AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService dbWriteExecutor = Executors.newScheduledThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase (Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "HealthAppDatabase")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}
