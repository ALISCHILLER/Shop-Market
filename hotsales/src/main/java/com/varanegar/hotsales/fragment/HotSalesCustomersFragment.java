package com.varanegar.hotsales.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.hotsales.HotSalesTourReportFragment;
import com.varanegar.hotsales.ui.HotSalesDrawerAdapter;
import com.varanegar.vaslibrary.ui.fragment.CustomersFragment;
import com.varanegar.vaslibrary.ui.fragment.TourReportFragment;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 3/6/2018.
 */

public class HotSalesCustomersFragment extends CustomersFragment {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setDrawerAdapter(new HotSalesDrawerAdapter(getVaranegarActvity()));
    }

    @Override
    protected VaranegarFragment getSendTourFragment() {
        return new HotSalesSendTourFragment();
    }


    @Override
    protected TourReportFragment getProfileFragment() {
        return new HotSalesTourReportFragment();
    }

    @Override
    protected VaranegarFragment getContentFragment(UUID selectedItem) {
        return new HotSalesCustomersContentFragment();
    }

    @Override
    protected VaranegarFragment getContentTargetFragment() {
        return new HotSalesCustomersTargetFragment();
    }

    @Override
    protected VaranegarFragment getContentTargetDetailFragment() {
        return new HotSalesCustomersTargetDetailFragment();
    }
}
