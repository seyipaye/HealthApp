package com.breezytechdevelopers.healthapp.network.ApiBodies;

import com.breezytechdevelopers.healthapp.database.entities.FirstAidTip;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FirstAidTipBody {

    public class Response {
        private String message;
        @SerializedName("data")
        private List<FirstAidTip> firstAidTips;
        private boolean success;

        public String getMessage() {
            return message;
        }

        public List<FirstAidTip> getFirstAidTips() {
            return firstAidTips;
        }

        public boolean getSuccess() {
            return success;
        }
    }
}
