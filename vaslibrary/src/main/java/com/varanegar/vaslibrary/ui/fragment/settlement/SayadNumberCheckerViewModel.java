package com.varanegar.vaslibrary.ui.fragment.settlement;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Created by e.hashemzadeh on 10/10/2021.
 */
public class SayadNumberCheckerViewModel extends ViewModel {
    private final MutableLiveData<Boolean> sayadNumberCheckerLiveData = new MutableLiveData<>();

    public void setSayadNumberCheckerLiveData(Boolean aBoolean) {
        sayadNumberCheckerLiveData.setValue(aBoolean);
    }

    public LiveData<Boolean> getSayadNumberCheckerLiveData() {
        return sayadNumberCheckerLiveData;
    }
}
