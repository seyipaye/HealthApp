package com.breezytechdevelopers.healthapp.network;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SafeAPIRequest {
    String TAG = "AppRepository";

    public AppAPI apiRequest() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://curefb.herokuapp.com/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(AppAPI.class);
    }

    public String getError(String methodName, ResponseBody errorBody) {
        JSONObject errorJson = null;
        try {
            String errorString = errorBody.string();
            Log.e(TAG, String.format("%s: %s", methodName, errorString));
            errorJson = new JSONObject(errorString);
            JSONObject error = errorJson.getJSONObject("error");
            return error.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "Oops, An internal error occurred, try again later";
        } catch (JSONException e) {
            if (errorJson != null) {
                try {
                    return errorJson.getString("error");
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    return "Oops, An internal error occurred, try again later";
                }
            } else {
                return "Oops, An internal error occurred, try again later";
            }
        }
    }
}
