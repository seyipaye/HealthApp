package com.breezytechdevelopers.healthapp.database.entities;

import androidx.room.Entity;
import androidx.room.Fts4;

@Entity(tableName = "FirstAidTipsTableFts")
@Fts4(contentEntity = FirstAidTip.class)
public class FirstAidTipFts {
    private String id;
    private String donts;
    private String dos;
    private String causes;
    private String symptoms;
    private String ailment;

    public FirstAidTipFts(String id, String donts, String dos,
                          String causes, String symptoms,
                          String ailment) {
        this.donts = donts;
        this.dos = dos;
        this.causes = causes;
        this.symptoms = symptoms;
        this.ailment = ailment;
        this.id = id;
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

    public String getId() {
        return id;
    }
}
