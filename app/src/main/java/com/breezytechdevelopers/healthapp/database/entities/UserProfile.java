package com.breezytechdevelopers.healthapp.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@Entity
public class UserProfile {

    @PrimaryKey
    @NonNull
    private String id;
    private String is_confirmed;
    private String department;
    private String image;
    private String clinic_number;
    private String matric_number;
    private String email;
    private String mobile_number;
    private String last_name;
    private String first_name;
    private String last_login;

    public UserProfile(@NonNull String id, String is_confirmed, String department, String image, String clinic_number, String matric_number, String email, String mobile_number, String last_name, String first_name, String last_login) {
        this.id = id;
        this.is_confirmed = is_confirmed;
        this.department = department;
        this.image = image;
        this.clinic_number = clinic_number;
        this.matric_number = matric_number;
        this.email = email;
        this.mobile_number = mobile_number;
        this.last_name = last_name;
        this.first_name = first_name;
        this.last_login = last_login;
    }

    @Ignore
    public UserProfile(String department, String clinic_number, String matric_number, String mobile_number) {
        this.department = department;
        this.clinic_number = clinic_number;
        this.matric_number = matric_number;
        this.mobile_number = mobile_number;
    }

    @Ignore
    public UserProfile() { }

    @NonNull
    public String getId() {
        return id;
    }

    public String getIs_confirmed() {
        return is_confirmed;
    }

    public String getDepartment() {
        return department;
    }

    public String getImage() {
        return image;
    }

    public String getClinic_number() {
        return clinic_number;
    }

    public String getMatric_number() {
        return matric_number;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_login() {
        return last_login;
    }

    public String getFreshTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT +1"));
        Date date = null;
        try {
            date = simpleDateFormat.parse(last_login.replace("T", " "));
            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("MMMM dd, yyyy h:mm a", Locale.ENGLISH);
            return simpleDateFormat2.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return last_login;
        }
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public UserProfile setImage(String image) {
        this.image = image;
        return this;
    }

    public void setClinic_number(String clinic_number) {
        this.clinic_number = clinic_number;
    }

    public void setMatric_number(String matric_number) {
        this.matric_number = matric_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public void setLast_login(String last_login) {
        this.last_login = last_login;
    }
}
