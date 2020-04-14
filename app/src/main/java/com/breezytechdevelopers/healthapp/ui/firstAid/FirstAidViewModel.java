package com.breezytechdevelopers.healthapp.ui.firstAid;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.breezytechdevelopers.healthapp.R;
import com.breezytechdevelopers.healthapp.database.entities.FirstAidTip;
import com.breezytechdevelopers.healthapp.AppRepository;
import com.breezytechdevelopers.healthapp.network.ApiCallbacks;
import com.breezytechdevelopers.healthapp.network.ApiBodies.FirstAidTipBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FirstAidViewModel extends AndroidViewModel{

    public LiveData<List<FirstAidTip>> initialFirstAidTips;
    private MutableLiveData<List<FirstAidTip>> tipsRVData
            = new MutableLiveData<>();
    private AppRepository appRepository;
    final public MutableLiveData<TipsState> tipsState = new MutableLiveData<>();
    private String TAG = getClass().getSimpleName();

    public FirstAidViewModel(Application application) {
        super(application);
        this.appRepository = new AppRepository(application);
        this.initialFirstAidTips = appRepository.getTips();

        initialFirstAidTips.observeForever(firstAidTips -> {
            if (firstAidTips != null) {
                tipsRVData.setValue(firstAidTips);
                if (firstAidTips.size() == 0) {
                    fetchFirstAidTips();
                }
            } else {
                fetchFirstAidTips();
            }
        });
    }

    public MutableLiveData<List<FirstAidTip>> getTipsRVData() {
        return tipsRVData;
    }

    public enum TipsState {
        NORMAL,
        INTERNAL_SEARCH,
        API_SEARCH_LOADING,
        API_SEARCH_LOADED
    }

    public MutableLiveData<TipsState> getTipsState() {
        return tipsState; }

    public void populateSearch(AutoCompleteTextView firstAidSearchInput,
                               ImageButton searchButton,
                               Context context) {
        ArrayList<String> strings = new ArrayList<>();
                firstAidSearchInput.setOnFocusChangeListener((v, hasFocus) -> {
                    if (hasFocus) {
                        searchButton.setVisibility(View.VISIBLE);
                        searchButton.setOnClickListener(v1 -> {
                            firstAidSearchInput.onEditorAction(EditorInfo.IME_ACTION_DONE);
                            /*Intent intent = new Intent(context, AuthActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getApplication().startActivity(intent);*/

                        });
                    } else {
                        searchButton.setVisibility(View.GONE);
                        searchButton.setOnClickListener(null);
                    }
                });

        for (FirstAidTip fruit: Objects.requireNonNull(initialFirstAidTips.getValue())) {
            strings.add(fruit.getAilment());
            strings.add(fruit.getSymptoms());
            strings.add(fruit.getDos());
            strings.add(fruit.getCauses());
        }

        ArrayAdapter<String> betterAdapter = new ArrayAdapter<>(context,
                R.layout.item_department_dropdown, strings);
        firstAidSearchInput.setThreshold(1);
        firstAidSearchInput.setAdapter(betterAdapter);
    }

    public void searchInDB(String query) {
        if (query != null && !query.equals("")) {
            appRepository.searchTips("*" + query + "*")
                    .observeForever(firstAidTips -> {
                        tipsRVData.setValue(firstAidTips);
                        if (firstAidTips != null) {
                            tipsState.setValue(TipsState.INTERNAL_SEARCH);
                        }
            });
            Log.i(TAG, "searchInDB: " + query);
        } else {
            tipsRVData.setValue(initialFirstAidTips.getValue());
            tipsState.setValue(TipsState.NORMAL);
        }
    }

    public void searchInApi(String query) {
        tipsState.setValue(TipsState.API_SEARCH_LOADING);
        appRepository.searchTipsInApi(query, new ApiCallbacks.SearchTipsInApi() {
            @Override
            public void onQueryStarted() { tipsState.setValue(TipsState.API_SEARCH_LOADING); }

            @Override
            public void onQuerySuccess(FirstAidTipBody.Response firstAidTipResponse) {
                tipsState.setValue(TipsState.API_SEARCH_LOADED);
                tipsRVData.setValue(firstAidTipResponse.getFirstAidTips());
            }

            @Override
            public void onQueryFailure(String message) {
                Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show();
                Log.i(TAG, "onQueryFailure: " + message);
                tipsState.setValue(TipsState.INTERNAL_SEARCH);
            }
        });
    }

    private void fetchFirstAidTips() {
        appRepository.fetchFirstAidTips(new ApiCallbacks.FetchFirstAidTips() {
            @Override public void onFetchStarted() {}
            @Override
            public void onFetchSuccess(FirstAidTipBody.Response firstAidTipResponse) {
                appRepository.saveFirstAidTips(firstAidTipResponse.getFirstAidTips());
            }

            @Override
            public void onFetchFailure(String message) {
                Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show();
                Log.i(TAG, "onFetchFailure: " + message);
            }
        });
    }
}