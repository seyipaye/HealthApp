package com.breezytechdevelopers.healthapp.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class User implements Serializable {

    @PrimaryKey
    @NonNull
    private String email;
    private String token;
    private String password;
    private String authId;
    private String last_name;
    private String first_name;
    private boolean isAuthenticated;

    @Ignore
    public User(@NonNull String email) {
        this.email = email;
    }

    @Ignore
    public User(@NonNull String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Ignore
    public User(@NonNull String email, String password, String first_name, String last_name) {
        this.email = email;
        this.password = password;
        this.last_name = last_name;
        this.first_name = first_name;
    }

    public User(@NonNull String email, String token, String password, String authId, String last_name, String first_name, boolean isAuthenticated) {
        this.email = email;
        this.token = token;
        this.password = password;
        this.authId = authId;
        this.last_name = last_name;
        this.first_name = first_name;
        this.isAuthenticated = isAuthenticated;
    }

    @NonNull
    public String getToken() {
        return token;
    }

    public User setToken(@NonNull String token) {
        this.token = token;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public User setAuthenticated(boolean authenticated) {
        isAuthenticated = authenticated;
        return this;
    }

    public String getAuthId() {
        return authId;
    }

    public User setAuthId(String authId) {
        this.authId = authId;
        return this;
    }

    public String getName() {
        return String.format("%s %s", first_name, last_name);
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }
}
