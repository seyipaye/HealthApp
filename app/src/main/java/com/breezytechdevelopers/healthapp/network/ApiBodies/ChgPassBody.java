package com.breezytechdevelopers.healthapp.network.ApiBodies;

public class ChgPassBody {

    public static class Request {

        private String password_again;
        private String password;
        private String otp;
        private String email;

        public Request(String email) {
            this.email = email;
        }

        public Request(String email, String otp, String password, String password_again) {
            this.email = email;
            this.otp = otp;
            this.password = password;
            this.password_again = password_again;
        }

        public String getPassword_again() {
            return password_again;
        }

        public void setPassword_again(String password_again) {
            this.password_again = password_again;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getOtp() {
            return otp;
        }

        public void setOtp(String otp) {
            this.otp = otp;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public class Response {
        private String message;
        private boolean success;

        public String getMessage() {
            return message;
        }

        public boolean getSuccess() {
            return success;
        }
    }
}
