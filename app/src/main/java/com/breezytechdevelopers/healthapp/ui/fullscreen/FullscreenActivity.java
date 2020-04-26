package com.breezytechdevelopers.healthapp.ui.fullscreen;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.breezytechdevelopers.healthapp.AppRepository;
import com.breezytechdevelopers.healthapp.R;
import com.breezytechdevelopers.healthapp.database.entities.User;

public class FullscreenActivity extends AppCompatActivity {

    private String TAG = "FullscreenActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);

        User user = (User) getIntent().getSerializableExtra("user");
        NavController navController = Navigation.findNavController(this, R.id.fullscreen_host_fragment);

        Log.i(TAG, "onCreate: Motive: " + getIntent().getStringExtra("motive"));

        if (getIntent().getStringExtra("motive") != null) {
            Log.i(TAG, "onCreate: Motive: " + getIntent().getStringExtra("motive"));
            switch (getIntent().getStringExtra("motive")) {
                case AppRepository.OTP:
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user", user);
                    bundle.putString("motive", AppRepository.OTP);
                    navController.navigate(R.id.action_welcomeFragment_to_otpFragment, bundle);
                    break;
                case AppRepository.CHANGEPASS:
                    Bundle bundle2 = new Bundle();
                    bundle2.putSerializable("user", user);
                    bundle2.putString("motive", AppRepository.CHANGEPASS);
                    navController.navigate(R.id.action_welcomeFragment_to_chgPassFragment, bundle2);
                    break;
                case AppRepository.PINGHISTORY:
                    navController.navigate(R.id.action_welcomeFragment_to_pingHistoryFragment);
                    break;
            }
        }
        /*authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        authViewModel.startActivity().observe(this, startActivityModel -> {
            startActivityForResult(startActivityModel.getIntent(), startActivityModel.getRequestCode());
        });

        authViewModel.shouldHideKeyboard().observe(this, hideKeyboard -> {
            if (hideKeyboard) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                Log.i(TAG, "subscribeUI: hideKey");
            }
        });

*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //authViewModel.onAuthActivityResult(requestCode, resultCode, data);
    }

    public void onProgressBgClicked(View view) {} // Do nothing
}
