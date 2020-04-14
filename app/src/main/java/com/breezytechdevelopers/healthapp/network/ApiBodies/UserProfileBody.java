package com.breezytechdevelopers.healthapp.network.ApiBodies;

import com.breezytechdevelopers.healthapp.database.entities.UserProfile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;

public class UserProfileBody {

    public UserProfileBody.Request request () {
        return new Request();
    }

    public class Response {

        private String message;
        @SerializedName("data")
        private UserProfile data;
        private boolean success = false;

        public String getMessage() {
            return message;
        }

        public UserProfile getData() {
            return data;
        }

        public boolean getSuccess() {
            return success;
        }

        public String toString() {
            ObjectMapper mapper = new ObjectMapper(); //

            try {
                // get Employee object as a json string
                String jsonStr = mapper.writeValueAsString(this);
                return jsonStr;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "";
            }
        }

    }

    public static class Request {
        private String image;
        private String matric_number;
        private String mobile_number;
        private String department;
        private String clinic_number;
        private String last_login;

        public Request() {}

        public Request(String image) {
            this.image = image;
        }

        public void setMatric_number(String matric_number) {
            this.matric_number = matric_number;
        }

        public void setMobile_number(String mobile_number) {
            this.mobile_number = mobile_number;
        }

        public void setDepartment(String department) {
            this.department = department;
        }

        public void setClinic_number(String clinic_number) {
            this.clinic_number = clinic_number;
        }

        public void setLast_login(String last_login) {
            this.last_login = last_login;
        }

        public UserProfileBody.Request nullifySameObj (UserProfile formerUP) {
            if (shouldMakeNull(matric_number, formerUP.getMatric_number())) {
                matric_number = null;
            }
            if (shouldMakeNull(mobile_number, formerUP.getMobile_number())) {
                mobile_number = null;
            }
            if (shouldMakeNull(department, formerUP.getDepartment())) {
                department = null;
            }
            if (shouldMakeNull(clinic_number, formerUP.getClinic_number())) {
                clinic_number = null;
            }
            if (shouldMakeNull(last_login, formerUP.getLast_login())) {
                last_login = null;
            }
            return this;
        }

        public String toString() {
            ObjectMapper mapper = new ObjectMapper(); //

            try {
                // get Employee object as a json string
                String jsonStr = mapper.writeValueAsString(this);
                return jsonStr;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "";
            }
        }

        private boolean shouldMakeNull(String first, String second) {

            if (first != null && second != null) {
                return first.matches(second);
            } else if (first != null) {
                return first.matches("");
            }
            return false;
        }

        public String getMatric_number() {
            return matric_number;
        }

        public String getMobile_number() {
            return mobile_number;
        }

        public String getDepartment() {
            return department;
        }

        public String getClinic_number() {
            return clinic_number;
        }

        public String getLast_login() {
            return last_login;
        }

        public String getImage() {
            return image;
        }
    }
}
