package com.breezytechdevelopers.healthapp.network.ApiBodies;

public class PingBody {

    public class Response {
        private int status;
        private String message;
        private boolean success;

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public boolean getSuccess() {
            return success;
        }
    }
}
