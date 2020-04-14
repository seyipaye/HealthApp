package com.breezytechdevelopers.healthapp.network.ApiBodies;

import com.breezytechdevelopers.healthapp.ui.auth.AuthResponseModel;

public class AuthBody {



    public class Response {
        private Data data;
        private int status;
        private String message;
        private String token;
        private boolean success;

        public Data getData() {
            return data;
        }

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public String getToken() {
            return token;
        }

        public boolean getSuccess() {
            return success;
        }

        public class Data {
            private String id;

            public String getId() {
                return id;
            }
        }
    }

}
