package com.breezytechdevelopers.healthapp.network;

import com.breezytechdevelopers.healthapp.database.entities.User;
import com.breezytechdevelopers.healthapp.network.ApiBodies.AuthBody;
import com.breezytechdevelopers.healthapp.network.ApiBodies.ChgPassBody;
import com.breezytechdevelopers.healthapp.network.ApiBodies.FirstAidTipBody;
import com.breezytechdevelopers.healthapp.network.ApiBodies.PingBody;
import com.breezytechdevelopers.healthapp.network.ApiBodies.PingHistoryBody;
import com.breezytechdevelopers.healthapp.network.ApiBodies.UserProfileBody;
import com.breezytechdevelopers.healthapp.ui.auth.AuthResponseModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AppAPI {

    @POST("student/register")
    Call<AuthBody.Response> signupUser(
            @Body User user);

    @POST("student/login")
    Call<AuthBody.Response> loginUser(
            @Body User user);

    @PATCH("student/reset-password")
    Call<ChgPassBody.Response> resetPassword(
            @Body ChgPassBody.Request requestBody);

    @FormUrlEncoded
    @PATCH("student/confirm/{id}")
    Call<AuthResponseModel> confirmOtp(
            @Path("id") String id,
            @Field("otp") String otp);

    @PATCH("student/confirm/{id}")
    Call<AuthResponseModel> resendOtp(
            @Path("id") String id);

    @GET("student/profile")
    Call<UserProfileBody.Response> fetchUserProfile(
            @Header("Authorization") String authToken);

    @PATCH("student/profile")
    Call<UserProfileBody.Response> updateUserProfile(
            @Header("Authorization") String authToken,
            @Body UserProfileBody.Request requestBody);

    @GET("firstaid/tip")
    Call<FirstAidTipBody.Response> fetchFirstAidTips();

    @GET("firstaid/tip?")
    Call<FirstAidTipBody.Response> searchTipsInApi(
            @Query("query") String query);

    @FormUrlEncoded
    @POST("student/ping")
    Call<PingBody.Response> sendPing(
            @Header("Authorization") String authToken,
            @Field("message") String pingMessage
    );

    @GET("student/ping")
    Call<PingHistoryBody.Response> fetchPingHistories(
            @Header("Authorization") String authToken
    );
}
