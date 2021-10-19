package com.varanegar.warmsales;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.vaslibrary.ui.fragment.CustomersFragment;
import com.varanegar.vaslibrary.ui.fragment.TourReportFragment;

import java.util.UUID;

public class WarmSalesCustomersFragment extends CustomersFragment {
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setDrawerAdapter(new WarmSalesDrawerAdapter(getVaranegarActvity()));
    }

    @Override
    protected VaranegarFragment getSendTourFragment() {
        return new WarmSalesSendTourFragment();
    }

    @Override
    protected TourReportFragment getProfileFragment() {
        return new WarmSalesTourReportFragment();
    }

    @Override
    protected VaranegarFragment getContentFragment(UUID selectedItem) {
        return new WarmSalesCustomerContentFragment();
    }

    @Override
    protected VaranegarFragment getContentTargetFragment() {
        return new WarmSalesCustomerTargetFragment();
    }

    @Override
    protected VaranegarFragment getContentTargetDetailFragment() {
        return new WarmSalesCustomerTargetDetailFragment();
    }
}

