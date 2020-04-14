package com.breezytechdevelopers.healthapp.ui.main;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.breezytechdevelopers.healthapp.BuildConfig;
import com.breezytechdevelopers.healthapp.R;
import com.breezytechdevelopers.healthapp.databinding.ActivityMainBinding;
import com.breezytechdevelopers.healthapp.utils.FragmentVisibleInterface;
import com.breezytechdevelopers.healthapp.ui.firstAid.FirstAidInfoFragment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity implements FirstAidInfoFragment.InfoCallback,
        FragmentVisibleInterface {

    private ActivityMainBinding binding;
    private MainViewModel mainViewModel;
    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {
            showSplashScreen(0);
        } else {
            showSplashScreen(3);
        }
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    private void continueActivity() {
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(binding.bottomNavView, navController);
    }

    private Handler handler = new Handler();
    private void showSplashScreen(int seconds) {
        SplashScreenFragment splashScreenFragment;
        splashScreenFragment = new SplashScreenFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.top_container, splashScreenFragment, "Splash");
        ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out).commit();
        handler.postDelayed(this::hideSplashScreen, (seconds * 1000));
    }

    private void hideSplashScreen() {
        continueActivity();
        handler.removeCallbacksAndMessages(null);
        SplashScreenFragment fragment = (SplashScreenFragment) getSupportFragmentManager().findFragmentByTag("Splash");
        if(fragment != null) {
            FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
            ft1.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            ft1.remove(fragment).commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onInfoCanceled(Boolean messageDoctor) {
        if (messageDoctor) { binding.bottomNavView.setSelectedItemId(R.id.navigation_ping); }
    }

    @Override
    public void onFragmentVisible(int id) {
        switch (id) {
            case R.layout.fragment_first_aid:
                binding.callFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));
                break;
            case R.layout.fragment_profile:
                binding.callFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));
                break;
            case R.layout.fragment_ping:
                binding.callFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                //if (mainViewModel.getUser().getValue() == null) showLogin();
                break;
        }
    }

    public void onFabClicked(View view) {
        binding.bottomNavView.setSelectedItemId(R.id.navigation_ping);
    }

    public void onProgressBgClicked(View view) {} // Do nothing

    @Override
    protected void onDestroy() {
        mainViewModel.deleteTimers();
        super.onDestroy();
    }
}