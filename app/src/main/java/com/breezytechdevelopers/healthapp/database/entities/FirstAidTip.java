package com.breezytechdevelopers.healthapp.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "FirstAidTipsTable")
public class FirstAidTip {

    @PrimaryKey
    @NonNull
    private String id;
    private int views;
    private String donts;
    private String dos;
    private String causes;
    private String symptoms;
    private String ailment;
    private String updated_at;
    private String created_at;

    public FirstAidTip () {}
    @Ignore
    public FirstAidTip(@NonNull String id, int views, String donts, String dos, String causes, String symptoms, String ailment, String updated_at, String created_at) {
        this.id = id;
        this.views = views;
        this.donts = donts;
        this.dos = dos;
        this.causes = causes;
        this.symptoms = symptoms;
        this.ailment = ailment;
        this.updated_at = updated_at;
        this.created_at = created_at;
    }

    public int getViews() {
        return views;
    }

    public String getDonts() {
        return donts;
    }

    public String getDos() {
        return dos;
    }

    public String getCauses() {
        return causes;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public String getAilment() {
        return ailment;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public void setDonts(String donts) {
        this.donts = donts;
    }

    public void setDos(String dos) {
        this.dos = dos;
    }

    public void setCauses(String causes) {
        this.causes = causes;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public void setAilment(String ailment) {
        this.ailment = ailment;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
