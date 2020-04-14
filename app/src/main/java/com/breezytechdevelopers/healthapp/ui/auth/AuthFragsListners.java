package com.breezytechdevelopers.healthapp.ui.auth;

import com.breezytechdevelopers.healthapp.database.entities.User;

public class AuthFragsListners {

    public interface Signup {
        public void onStarted ();
        public void onSuccess (User user);
        public void onFailure (String message);
    }

    public interface Login {
        public void onAuthStarted ();
        public void onAuthSuccess (String message);
        public void onAuthFailure (String message);
    }
}
