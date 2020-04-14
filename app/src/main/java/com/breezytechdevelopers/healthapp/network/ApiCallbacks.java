package com.breezytechdevelopers.healthapp.network;

import com.breezytechdevelopers.healthapp.network.ApiBodies.AuthBody;
import com.breezytechdevelopers.healthapp.network.ApiBodies.ChgPassBody;
import com.breezytechdevelopers.healthapp.network.ApiBodies.FirstAidTipBody;
import com.breezytechdevelopers.healthapp.network.ApiBodies.PingBody;
import com.breezytechdevelopers.healthapp.network.ApiBodies.PingHistoryBody;
import com.breezytechdevelopers.healthapp.network.ApiBodies.UserProfileBody;

public class ApiCallbacks {
    public interface FetchUserProfile {
        void onFetchStarted();
        void onFetchSuccess(UserProfileBody.Response userProfileResponse);
        void onFetchFailure(String message);
    }
    public interface FetchFirstAidTips {
        void onFetchStarted();
        void onFetchSuccess(FirstAidTipBody.Response firstAidTipResponse);
        void onFetchFailure(String message);
    }
    public interface SearchTipsInApi {
        void onQueryStarted();
        void onQuerySuccess(FirstAidTipBody.Response firstAidTipResponse);
        void onQueryFailure(String message);
    }
    public interface Ping {
        void onPingStarted();
        void onPingSuccess(PingBody.Response pingResponse);
        void onPingFailure(String message);
    }
    public interface ChgPass {
        void onStarted();
        void onSuccess(ChgPassBody.Response chgPassResponse);
        void onFailure(String message);
    }
    public interface Auth {
        void onAuthStarted();
        void onAuthSuccess(AuthBody.Response authResponse);
        void onAuthFailure(String message);
    }

    public interface PingHistory {
        void onFetchSuccess(PingHistoryBody.Response historyResponse);
        void onFetchFailure(String message);
    }
}
