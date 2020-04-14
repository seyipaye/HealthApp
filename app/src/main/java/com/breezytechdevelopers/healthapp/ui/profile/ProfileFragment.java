package com.breezytechdevelopers.healthapp.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.breezytechdevelopers.healthapp.R;
import com.breezytechdevelopers.healthapp.AppRepository;
import com.breezytechdevelopers.healthapp.database.entities.UserProfile;
import com.breezytechdevelopers.healthapp.databinding.FragmentProfileBinding;
import com.breezytechdevelopers.healthapp.ui.auth.AuthActivity;
import com.breezytechdevelopers.healthapp.utils.FragmentVisibleInterface;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static com.breezytechdevelopers.healthapp.ui.profile.ProfileViewModel.user;

public class ProfileFragment extends Fragment implements View.OnClickListener, PopupMenu.OnMenuItemClickListener, TextWatcher {

    private ProfileViewModel profileViewModel;
    private FragmentProfileBinding binding;
    private PopupMenu popupMenu;
    private String TAG = "ProfileFragment";
    private ProfileViewModel.ProfileState profileState;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        binding.more.setOnClickListener(this);
        binding.nameLabel.setOnClickListener(this);
        binding.editAvatar.setOnClickListener(this);

        popupMenu = new PopupMenu(getActivity(), binding.more, Gravity.END);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.getMenuInflater().inflate(R.menu.profile_more_menu, popupMenu.getMenu());

        populateDepartment();
        subscribeUI();
        return binding.getRoot();
    }

    private void populateDepartment() {
        String[] departments = new String[]
                {"ELE", "MTE", "MCE",
                        "ABE", "CVE", "BCH",
                        "MCB", "PAZ", "PAB",
                        "EMT",
                        "WMA",
                        "AQFM",
                        "FWM",
                        "AGAD",
                        "AEFM",
                        "AERD",
                        "ECO",
                        "ETS",
                        "ACC",
                        "BFN",
                        "BAM",
                        "CHM",
                        "CSC",
                        "MTS",
                        "PHS",
                        "STS",
                        "ABG",
                        "ANN",
                        "ANP",
                        "APH",
                        "PRM",
                        "SSLM",
                        "PPCP",
                        "PBST",
                        "HRT",
                        "CPT", "FST", "HSM",
                        "NTD", "HTM", "VET"};

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        getContext(),
                        R.layout.item_department_dropdown,
                        departments);
        binding.departmentEntry.setAdapter(adapter);
    }

    private void subscribeUI() {
        profileViewModel.getProfileState().observe(getViewLifecycleOwner(), profileState -> {
            this.profileState = profileState;
            switch (profileState) {
                case UNREGISTERED:
                    popupMenu.getMenu().clear();
                    popupMenu.getMenu().add(Menu.NONE, R.id.menu_login,
                            Menu.NONE, R.string.log_in);
                    binding.verifyNotif.setVisibility(View.GONE);
                    populateDisplay(new UserProfile());
                    break;
                case UNAUTHENTICATED:
                    // TODO Add Log out
                    popupMenu.getMenu().clear();
                    popupMenu.getMenu().add(Menu.NONE, R.id.menu_verify_account,
                            Menu.NONE, R.string.verify_account);
                    binding.verifyNotif.setVisibility(View.VISIBLE);
                    binding.verifyBtn.setOnClickListener(v -> showOtp());
                    if (user != null) {
                        binding.nameLabel.setText(user.getName());
                        binding.emailLabel.setText(user.getEmail());
                    }
                    populateDisplay(new UserProfile());
                    break;
                case AUTHENTICATED:
                    popupMenu.getMenu().clear();
                    popupMenu.getMenu().add(Menu.NONE, R.id.menu_ping_history,
                            Menu.NONE, R.string.ping_history);
                    popupMenu.getMenu().add(Menu.NONE, R.id.menu_chg_password,
                            Menu.NONE, R.string.change_password);
                    popupMenu.getMenu().add(Menu.NONE, R.id.menu_logOut,
                            Menu.NONE, R.string.log_out);
                    binding.verifyNotif.setVisibility(View.GONE);
                        populateDisplay(profileViewModel.userProfile);
                    break;
            }
        });

    }

    private void populateDisplay(UserProfile userProfile) {
        if (userProfile.getFirst_name() != null)
            binding.nameLabel.setText(String.format("%s %s", userProfile.getFirst_name(), userProfile.getLast_name()));
        else
            binding.nameLabel.setText(R.string.click_here);
        binding.emailLabel.setText(userProfile.getEmail());
        binding.matricNoEntry.setText(userProfile.getMatric_number());
        binding.mobileNoEntry.setText(userProfile.getMobile_number());
        binding.departmentEntry.setText(userProfile.getDepartment());
        binding.clinicNoEntry.setText(userProfile.getClinic_number());
        binding.lastVisitEntry.setText(userProfile.getFreshTime());
        addWatcher();
        if (userProfile.getImage() != null) {
            Glide.with(requireActivity())
                    .load(userProfile.getImage())
                    .placeholder(R.drawable.ic_account_circle_black_24dp)
                    .circleCrop()
                    .into(binding.avatar);
        } else {
            binding.avatar.setImageResource(R.drawable.ic_account_circle_black_24dp);
        }
    }

    private void addWatcher() {
        binding.updateButton.setVisibility(View.GONE);
        binding.matricNoEntry.addTextChangedListener(this);
        binding.mobileNoEntry.addTextChangedListener(this);
        binding.departmentEntry.addTextChangedListener(this);
        binding.clinicNoEntry.addTextChangedListener(this);
        binding.lastVisitEntry.addTextChangedListener(this);
    }

    private void updateInputs() {
        if (user != null && profileViewModel.userProfile != null) {
            UserProfile newUP = new UserProfile(binding.departmentEntry.getText().toString(), binding.clinicNoEntry.getText().toString(), binding.matricNoEntry.getText().toString(), binding.mobileNoEntry.getText().toString());
            //userProfile.setLast_login(binding.lastVisitEntry.getText().toString());
            profileViewModel.updateUserProfile(user.getToken(), newUP, binding.progressRing);
        }
    }

    public void showPopup(View v) {
        popupMenu.show();
    }

    private void logOut(MenuItem item) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);

        mGoogleSignInClient.signOut()
                .addOnCompleteListener(getActivity(), task -> {
                    profileViewModel.logOut();
                    Toast.makeText(getActivity(), "Logged out successfully", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onClick(View v) {
        hideKeyBoard();
        switch (v.getId()) {
            case R.id.more:
                showPopup(v);
                break;
            case R.id.update_button:
                if (profileState == ProfileViewModel.ProfileState.AUTHENTICATED) {
                    updateInputs();
                }
                break;
            case R.id.nameLabel:
                if (profileState == ProfileViewModel.ProfileState.UNREGISTERED) {
                    startActivity(new Intent(getActivity(), AuthActivity.class));
                }
                break;
            case R.id.edit_avatar:
                if (profileState == ProfileViewModel.ProfileState.AUTHENTICATED) {
                    profileViewModel.editAvatar(this, binding);
                } else {
                    Toast.makeText(requireActivity(), "User not Verified yet", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        FragmentVisibleInterface fragVisInterface = (FragmentVisibleInterface) getActivity();
        fragVisInterface.onFragmentVisible(R.layout.fragment_profile);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Log.i(TAG, "onMenuItemClick: " + item.getTitle());
        Log.i(TAG, "onMenuItemClick: " + (R.id.menu_login));
        switch (item.getItemId()) {
            case R.id.menu_logOut:
                logOut(item);
                return true;
            case R.id.menu_chg_password:
                changePassword();
                return true;
            case R.id.menu_login:
                startActivity(new Intent(getContext(), AuthActivity.class));
                return true;
            case R.id.menu_verify_account:
                showOtp();
                return true;
            case R.id.menu_ping_history:
                showPingHistory();
                return true;
            default:
                return false;
        }
    }

    private void showPingHistory() {
        Intent myIntent = new Intent(requireActivity(), AuthActivity.class);
        myIntent.putExtra("motive", AppRepository.PINGHISTORY);
        requireActivity().startActivity(myIntent);
    }

    private void changePassword() {
        Intent myIntent = new Intent(requireActivity(), AuthActivity.class);
        if (user != null && user.isAuthenticated()) {
            myIntent.putExtra("user", user);
            myIntent.putExtra("motive", AppRepository.CHANGEPASS);
        }
        requireActivity().startActivity(myIntent);
    }

    private void showOtp() {
        Intent myIntent = new Intent(requireActivity(), AuthActivity.class);
        myIntent.putExtra("user", user);
        myIntent.putExtra("motive", AppRepository.OTP);
        requireActivity().startActivity(myIntent);
    }

    private void hideKeyBoard() {
        binding.matricNoEntry.onEditorAction(EditorInfo.IME_ACTION_DONE);
        binding.mobileNoEntry.onEditorAction(EditorInfo.IME_ACTION_DONE);
        binding.departmentEntry.onEditorAction(EditorInfo.IME_ACTION_DONE);
        binding.clinicNoEntry.onEditorAction(EditorInfo.IME_ACTION_DONE);
        binding.lastVisitEntry.onEditorAction(EditorInfo.IME_ACTION_DONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        profileViewModel.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
    @Override
    public void afterTextChanged(Editable s) {

        binding.updateButton.setVisibility(View.VISIBLE);
        binding.updateButton.setOnClickListener(this);
    }
}