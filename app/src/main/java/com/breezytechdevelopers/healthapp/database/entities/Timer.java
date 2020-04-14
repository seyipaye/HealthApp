package com.breezytechdevelopers.healthapp.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Timer {

    @PrimaryKey
    @NonNull
    private String timerID;
    private long timerMillisLeft;

    public Timer(@NonNull String timerID, long timerMillisLeft) {
        this.timerID = timerID;
        this.timerMillisLeft = timerMillisLeft;
    }

    @NonNull
    public String getTimerID() {
        return timerID;
    }

    public long getTimerMillisLeft() {
        return timerMillisLeft;
    }

    public Timer setTimerMillisLeft(long timerMillisLeft) {
        this.timerMillisLeft = timerMillisLeft;
        return this;
    }
}
