package com.breezytechdevelopers.healthapp.database.entities;

// Message.java
public class Message {
    private String text; // message body
    private boolean isForUser; // is this message sent by us?

    public Message (String text, boolean isForUser) {
        this.text = text;
        this.isForUser = isForUser;
    }

    public String getText() {
        return text;
    }

    public boolean isForUser() {
        return isForUser;
    }
}