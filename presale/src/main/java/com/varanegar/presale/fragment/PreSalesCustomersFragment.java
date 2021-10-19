package com.varanegar.presale.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.presale.ui.PreSalesDrawerAdapter;
import com.varanegar.presale.ui.PreSalesTourReportFragment;
import com.varanegar.vaslibrary.ui.fragment.TourReportFragment;
import com.varanegar.vaslibrary.ui.fragment.CustomersFragment;

import java.util.UUID;

public class PreSalesCustomersFragment extends CustomersFragment {
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setDrawerAdapter(new PreSalesDrawerAdapter(getVaranegarActvity()));
    }

    @Override
    protected VaranegarFragment getSendTourFragment() {
        return new PreSalesSendTourFragment();
    }

    @Override
    protected TourReportFragment getProfileFragment() {
        return new PreSalesTourReportFragment();
    }

    @Override
    protected VaranegarFragment getContentFragment(UUID selectedItem) {
        return new PreSalesCustomerContentFragment();
    }

    @Override
    protected VaranegarFragment getContentTargetFragment() {
        return new PreSalesCustomerTargetFragment();
    }

    @Override
    protected VaranegarFragment getContentTargetDetailFragment() {
        return new PreSalesCustomerTargetDetailFragment();
    }
}
