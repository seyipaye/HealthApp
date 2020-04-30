package com.breezytechdevelopers.healthapp;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.breezytechdevelopers.healthapp.database.AppDatabase;
import com.breezytechdevelopers.healthapp.database.entities.Message;
import com.breezytechdevelopers.healthapp.utils.SocketManager;

import java.util.ArrayList;
import java.util.List;

public class HealthApp extends Application {

    private AppExecutors mAppExecutors;
    private MutableLiveData<List<Message>> mutableMessageList;
    private List<Message> messageList;


    @Override
    public void onCreate() {
        super.onCreate();

        mAppExecutors = new AppExecutors();
        mutableMessageList = new MutableLiveData<>(new ArrayList<>());
        mutableMessageList.observeForever(mutMessageList -> {
            this.messageList = mutMessageList;
        });
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
    */

    public MutableLiveData<List<Message>> getMutableMessageList() {
        return mutableMessageList;
    }

    public void addToMessageList(Message message) {
        messageList.add(message);
        mutableMessageList.postValue(messageList);
        Log.i("App", "addToMessageList: ");
    }

    public AppRepository getRepository() {
        return new AppRepository(this);
    }
}
