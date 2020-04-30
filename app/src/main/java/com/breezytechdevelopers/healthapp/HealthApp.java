package com.breezytechdevelopers.healthapp;

import android.app.Application;

import com.breezytechdevelopers.healthapp.database.AppDatabase;
import com.breezytechdevelopers.healthapp.utils.SocketManager;

public class HealthApp extends Application {

    private AppExecutors mAppExecutors;

    @Override
    public void onCreate() {
        super.onCreate();

        mAppExecutors = new AppExecutors();
    }

    public SocketManager getSocketManager() {
        return SocketManager.getInstance(this, mAppExecutors);
    }

    public AppExecutors getmAppExecutors() {
        return mAppExecutors;
    }

    /* public AppDatabase getDatabase() {
        return AppDatabase.getInstance(this, mAppExecutors);
    }

    public DataRepository getRepository() {
        return DataRepository.getInstance(getDatabase());
    }*/
}
