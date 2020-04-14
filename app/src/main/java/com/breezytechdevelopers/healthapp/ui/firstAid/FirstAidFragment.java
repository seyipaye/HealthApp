package com.breezytechdevelopers.healthapp.ui.firstAid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.breezytechdevelopers.healthapp.R;
import com.breezytechdevelopers.healthapp.databinding.FragmentFirstAidBinding;
import com.breezytechdevelopers.healthapp.ui.auth.AuthActivity;
import com.breezytechdevelopers.healthapp.utils.FragmentVisibleInterface;

import java.util.Locale;
import java.util.Objects;

public class FirstAidFragment extends Fragment {

    private FirstAidViewModel firstAidViewModel;
    FragmentFirstAidBinding binding;

    String TAG = getClass().getSimpleName();
    private FirstAidRVAdapter firstAidRVAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        firstAidViewModel = new ViewModelProvider(this).get(FirstAidViewModel.class);

        binding = FragmentFirstAidBinding.inflate(inflater, container, false);

        firstAidRVAdapter = new FirstAidRVAdapter(getParentFragmentManager());
        binding.firstAidRecyclerview.setAdapter(firstAidRVAdapter);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.firstAidSearchInput.setImeActionLabel("Search", KeyEvent.KEYCODE_ENTER);
        binding.firstAidSearchInput.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                binding.firstAidSearchInput.dismissDropDown();
                hideKeyboardFrom(requireContext(), v);
                firstAidViewModel.searchInDB(binding.firstAidSearchInput.getText().toString());
                handled = true;
            }
            return handled;
        });

        firstAidViewModel.getTipsRVData().observe(getViewLifecycleOwner(), firstAidTips -> {
            firstAidRVAdapter.setTips(firstAidTips);
            if (firstAidTips.size() > 1) {
                binding.resultCount
                        .setText(String.format(Locale.ENGLISH, "%d results", firstAidRVAdapter.getItemCount()));
            } else {
                binding.resultCount
                        .setText(String.format(Locale.ENGLISH, "%d result", firstAidRVAdapter.getItemCount()));
            }
            if (firstAidTips != null) {
                firstAidViewModel.populateSearch(binding.firstAidSearchInput, binding.searchButton, requireActivity());
            }
        });

        firstAidViewModel.getTipsState().observe(getViewLifecycleOwner(), tipsState -> {
            switch (tipsState) {
                case NORMAL:
                    binding.resultCount.setVisibility(View.GONE);
                    binding.progressBar.setVisibility(View.GONE);
                    binding.advancedSearch.setVisibility(View.GONE);
                    binding.advancedSearch.setOnClickListener(null);
                    break;
                case INTERNAL_SEARCH:
                    binding.resultCount.setVisibility(View.VISIBLE);
                    binding.progressBar.setVisibility(View.GONE);
                    binding.advancedSearch.setVisibility(View.VISIBLE);
                    binding.advancedSearch.
                            setOnClickListener(v -> searchInApi());
                    break;
                case API_SEARCH_LOADING:
                    binding.resultCount.setVisibility(View.VISIBLE);
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.advancedSearch.setVisibility(View.GONE);
                    binding.advancedSearch.setOnClickListener(null);
                    break;
                case API_SEARCH_LOADED:
                    binding.resultCount.setVisibility(View.VISIBLE);
                    binding.progressBar.setVisibility(View.GONE);
                    binding.advancedSearch.setVisibility(View.GONE);
                    binding.advancedSearch.setOnClickListener(null);
                    break;
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (firstAidViewModel.getTipsState().getValue()
                        != FirstAidViewModel.TipsState.NORMAL) {
                            firstAidRVAdapter.setTips(firstAidViewModel.initialFirstAidTips.getValue());
                            firstAidViewModel.getTipsState().setValue(FirstAidViewModel.TipsState.NORMAL);
                        } else {
                            Objects.requireNonNull(getActivity()).finish();
                        }
                    }
                });
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void searchInApi() {
        Log.i(TAG, "searchInApi: ");
        firstAidViewModel.searchInApi
                (binding.firstAidSearchInput.getText().toString());
    }

    private void showLogin() {
        startActivity(new Intent(getActivity(), AuthActivity.class));
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume: ");
        super.onResume();
        FragmentVisibleInterface fragVisInterface = (FragmentVisibleInterface) getActivity();
        fragVisInterface.onFragmentVisible(R.layout.fragment_first_aid);
    }
}