package com.varanegar.dist.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.varanegar.dist.DistDrawerAdapter;
import com.varanegar.dist.DistTourReportFragment;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.vaslibrary.ui.fragment.CustomersFragment;
import com.varanegar.vaslibrary.ui.fragment.TourReportFragment;

import java.util.UUID;

public class DistCustomersFragment extends CustomersFragment {
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setDrawerAdapter(new DistDrawerAdapter(getVaranegarActvity()));
    }

    @Override
    protected VaranegarFragment getSendTourFragment() {
        return new DistSendTourFragment();
    }

    @Override
    protected TourReportFragment getProfileFragment() {
        return new DistTourReportFragment();
    }

    @Override
    protected VaranegarFragment getContentFragment(UUID selectedItem) {
        return new DistCustomerContentFragment();
    }

    @Override
    protected VaranegarFragment getContentTargetFragment() {
        return null;
    }

    @Override
    protected VaranegarFragment getContentTargetDetailFragment() {
        return null;
    }

}
