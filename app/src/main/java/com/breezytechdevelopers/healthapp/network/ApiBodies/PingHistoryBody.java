package com.breezytechdevelopers.healthapp.network.ApiBodies;

import com.breezytechdevelopers.healthapp.database.entities.PingHistory;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PingHistoryBody {

    public class Response {

        private String message;
        @SerializedName("data")
        private List<PingHistory> pingHistories;
        private boolean success;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public List<PingHistory> getPingHistories() {
            return pingHistories;
        }

        public void setPingHistories(List<PingHistory> pingHistories) {
            this.pingHistories = pingHistories;
        }

        public boolean getSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }
    }
}
