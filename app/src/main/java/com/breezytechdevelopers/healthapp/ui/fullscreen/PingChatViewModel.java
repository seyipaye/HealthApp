package com.breezytechdevelopers.healthapp.ui.fullscreen;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.breezytechdevelopers.healthapp.AppRepository;
import com.breezytechdevelopers.healthapp.R;
import com.breezytechdevelopers.healthapp.database.entities.UserProfile;
import com.breezytechdevelopers.healthapp.utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class PingChatViewModel extends AndroidViewModel {
    private final AppRepository appRepository;
    private final LiveData<UserProfile> userProfileLiveData;
    public MutableLiveData<Bitmap> mutableAvatar;
    private UserProfile userProfile;

    public PingChatViewModel(@NonNull Application application) {
        super(application);
        appRepository = new AppRepository(application);
        userProfileLiveData = appRepository.getUserProfile();
        mutableAvatar = new MutableLiveData<>();
        userProfileLiveData.observeForever(userProfile -> {
            this.userProfile = userProfile;
            if (userProfile != null) {
                getAvatarThumbnail(userProfile.getImage());
            }
        });
    }

    private void getAvatarThumbnail(String image) {
        Glide.with(getApplication())
                .asBitmap()
                .load(image)
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .circleCrop()
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        mutableAvatar.setValue(Utils.getScaledBitmap(100, resource));
                    }
                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) { }
                });
    }

    public MutableLiveData<Bitmap> getMutableAvatar() {
        return mutableAvatar;
    }
    // TODO: Implement the ViewModel
}
