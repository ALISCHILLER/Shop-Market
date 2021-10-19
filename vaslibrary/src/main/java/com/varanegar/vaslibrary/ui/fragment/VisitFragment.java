package com.varanegar.vaslibrary.ui.fragment;

import androidx.annotation.NonNull;

import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.vaslibrary.manager.customeractiontimemanager.CustomerActionTimeManager;

import java.util.UUID;

/**
 * Created by A.Torabi on 5/7/2018.
 */

public abstract class VisitFragment extends VaranegarFragment {
    @NonNull
    protected abstract UUID getCustomerId();
    @Override
    public void onResume() {
        super.onResume();
        CustomerActionTimeManager.startVisitTimer(getVaranegarActvity(), getCustomerId(), null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CustomerActionTimeManager.stopVisitTimer();
    }

}
