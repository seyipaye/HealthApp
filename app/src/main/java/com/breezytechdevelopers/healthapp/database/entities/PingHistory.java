package com.breezytechdevelopers.healthapp.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@Entity
public class PingHistory {

    @PrimaryKey
    @NonNull
    private String id;
    private String created_at;
    private String location;
    private String message;
    private String customDate;

    public PingHistory(@NonNull String id, String created_at, String location, String message) {
        this.id = id;
        this.created_at = created_at;
        this.location = location;
        this.message = message;
        setCustomDate(null);
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getLocation() {
        return location;
    }

    public String getMessage() {
        return message;
    }

    public String getId() {
        return id;
    }

    public void setCustomDate(String customDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT +1"));
        Date date = null;
        try {
            date = simpleDateFormat.parse(created_at.replace("T", " "));

            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("MMMM dd, yyyy h:mm a", Locale.ENGLISH);
            this.customDate = simpleDateFormat2.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            this.customDate = created_at;
        }
    }

    public String getCustomDate() {
        return customDate;
    }
}
