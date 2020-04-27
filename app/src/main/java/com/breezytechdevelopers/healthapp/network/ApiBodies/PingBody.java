package com.breezytechdevelopers.healthapp.network.ApiBodies;

public class PingBody {

    public class Response {
        private int status;
        private String message;
        private Data data;
        private boolean success;

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public Data getData() {
            return data;
        }

        public boolean getSuccess() {
            return success;
        }

        public class Data {
            private String status;
            private String created_at;
            private String location;
            private String message;
            private String id;

            public String getStatus() {
                return status;
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
        }
    }
}
