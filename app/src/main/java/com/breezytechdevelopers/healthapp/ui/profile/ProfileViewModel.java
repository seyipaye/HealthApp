package com.breezytechdevelopers.healthapp.ui.profile;

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.breezytechdevelopers.healthapp.R;
import com.breezytechdevelopers.healthapp.AppRepository;
import com.breezytechdevelopers.healthapp.database.entities.User;
import com.breezytechdevelopers.healthapp.database.entities.UserProfile;
import com.breezytechdevelopers.healthapp.network.ApiCallbacks;
import com.breezytechdevelopers.healthapp.network.ApiBodies.UserProfileBody;
import com.breezytechdevelopers.healthapp.databinding.FragmentProfileBinding;
import com.breezytechdevelopers.healthapp.utils.Utils;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class ProfileViewModel extends AndroidViewModel {
    private LiveData<UserProfile> userProfileLiveData;
    private LiveData<User> userLiveData;
    private AppRepository appRepository;
    private MutableLiveData<ProfileState> profileState;
    final int RC_PERMISSION_READ_EXTERNAL_STORAGE = 1;
    final int RC_PERMISSION_WRITE_EXTERNAL_STORAGE = 3;
    final int RC_IMAGE_GALLERY = 2;
    static User user;
    String TAG = getClass().getSimpleName();
    UserProfile userProfile;
    private FragmentProfileBinding binding;
    private final FirebaseStorage storage;
    private final StorageReference storageRef;

    public enum ProfileState {
        UNREGISTERED,
        UNAUTHENTICATED,
        AUTHENTICATED
    }

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        appRepository = new AppRepository(application);
        profileState = new MutableLiveData<>();
        userLiveData = appRepository.getUser();
        userProfileLiveData = appRepository.getUserProfile();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("users_avatar");
        userLiveData.observeForever(user -> {
            ProfileViewModel.user = user;
            Log.i(TAG, "ProfileViewModel: user Changed ");
            if (user == null) {
                profileState.setValue(ProfileState.UNREGISTERED);
                return;
            }

            if (!user.isAuthenticated()) {
                profileState.setValue(ProfileState.UNAUTHENTICATED);
            }

            if (user.getToken() != null) {
                Log.i(TAG, "ProfileViewModel: getUserToken");
                fetchUserProfile(user.getToken());
            }
        });

        userProfileLiveData.observeForever(userProfile -> {
            this.userProfile = userProfile;
            Log.i(TAG, "ProfileViewModel: userProfileLiveData");
            if (userProfile != null) {
                profileState.setValue(ProfileState.AUTHENTICATED);
            }
        });
    }

    void fetchUserProfile (String token) {
        appRepository.fetchUserProfile(token, new ApiCallbacks.FetchUserProfile() {
            @Override
            public void onFetchStarted() {}
            @Override
            public void onFetchSuccess(UserProfileBody.Response userProfileResponse) {}

            @Override
            public void onFetchFailure(String message) {}
        });
    }

    void logOut() {
        appRepository.logOut();
    }

    public void editAvatar(ProfileFragment fragment, FragmentProfileBinding binding) {
        this.binding = binding;
        Log.i(TAG, "editAvatar: ");
        if (ContextCompat.checkSelfPermission(fragment.requireContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(fragment.requireActivity(),
                    new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, RC_PERMISSION_READ_EXTERNAL_STORAGE);
        } else if (ContextCompat.checkSelfPermission(fragment.requireContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(fragment.requireActivity(),
                    new String[] {android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, RC_PERMISSION_WRITE_EXTERNAL_STORAGE);
        } else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            fragment.startActivityForResult(intent, RC_IMAGE_GALLERY);
        }
    }

    void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult: ");
        if (requestCode == RC_IMAGE_GALLERY && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null) {
                Glide.with(getApplication())
                        .load(uri)
                        .placeholder(R.drawable.ic_account_circle_black_24dp)
                        .circleCrop()
                        .into(binding.avatar);
                uploadPicture(uri);
            } else {
                Toast.makeText(getApplication(), "Select a valid Image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadPicture(Uri uri) {
        try {
            binding.avatarSpinKit.setVisibility(View.VISIBLE);

            Bitmap scaledBitmap = Utils.getScaledBitmap(1080,
                    MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), uri));

            String filename = String.format("%s_%s.jpg", userProfile.getId(), userProfile.getFirst_name());
            StorageReference fileRef = storageRef.child(filename);
            fileRef.putFile(Utils.getImageUri(getApplication(), scaledBitmap, "Title"))
                    .addOnSuccessListener(taskSnapshot ->
                    fileRef.getDownloadUrl().addOnSuccessListener(uri1 -> {
                        String downloadUrl = uri1.toString();
                        Log.d(TAG, "uploadPicture: " + downloadUrl);
                        saveUrl(downloadUrl);
                    })).addOnFailureListener(e -> {failedImageUpload(e.getMessage());
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveUrl(String downloadUrl) {
        UserProfileBody.Request request = new UserProfileBody.Request(downloadUrl);
        appRepository.updateUserProfile(user.getToken(), request,
                new ApiCallbacks.FetchUserProfile() {
                    @Override
                    public void onFetchStarted() { binding.avatarSpinKit.setVisibility(View.VISIBLE); }
                    @Override
                    public void onFetchSuccess(UserProfileBody.Response userProfileResponse) {
                        binding.avatarSpinKit.setVisibility(View.GONE);
                        appRepository.saveUserProfile(userProfile.setImage(downloadUrl));
                        Toast.makeText(getApplication(), "Update Successful", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFetchFailure(String message) {
                        failedImageUpload(message);
                    }
        });
    }

    private void failedImageUpload(String errorMessage) {
        binding.avatarSpinKit.setVisibility(View.GONE);
        Toast.makeText(getApplication(), errorMessage, Toast.LENGTH_LONG).show();
        Log.i(this.getClass().getSimpleName(), "failedImageUpload: " + errorMessage);
        binding.avatar.setImageResource(R.drawable.ic_account_circle_black_24dp);
    }

    void updateUserProfile(String token,
                           UserProfile newUserProfile, FrameLayout progressRing) {
        appRepository.updateUserProfile(token,
                getUserProfileRequest(newUserProfile).nullifySameObj(userProfileLiveData.getValue()),
                new ApiCallbacks.FetchUserProfile() {
            @Override
            public void onFetchStarted() { progressRing.setVisibility(View.VISIBLE); }

            @Override
            public void onFetchSuccess(UserProfileBody.Response userProfileResponse) {
                appRepository.fetchUserProfile(user.getToken(), null);
                progressRing.setVisibility(View.GONE);
                Log.i(TAG, "onFetchSuccess: " + getUserProfileRequest(newUserProfile)
                        .nullifySameObj(userProfileLiveData.getValue()).toString());
                Toast.makeText(getApplication(), userProfileResponse.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFetchFailure(String message) {
                progressRing.setVisibility(View.GONE);
                Toast.makeText(getApplication(), message, Toast.LENGTH_LONG).show();
                Log.i(this.getClass().getSimpleName(), "onFetchFailure: " + message);
            }
        });
    }

    private UserProfileBody.Request getUserProfileRequest(UserProfile newUserProfile) {
        UserProfileBody.Request request = new UserProfileBody().request();
        request.setMatric_number(newUserProfile.getMatric_number());
        request.setMobile_number(newUserProfile.getMobile_number());
        request.setDepartment(newUserProfile.getDepartment());
        request.setClinic_number(newUserProfile.getClinic_number());
        request.setLast_login(newUserProfile.getLast_login());
        return request;
    }

    MutableLiveData<ProfileState> getProfileState() {
        return profileState;
    }
}