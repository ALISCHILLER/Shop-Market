package com.varanegar.contractor;

import com.varanegar.vaslibrary.ui.fragment.LoginFragment;
import com.varanegar.vaslibrary.ui.fragment.TourReportFragment;

public class ContractorLoginFragment extends LoginFragment {
    @Override
    protected int getAppIconId() {
        return R.mipmap.ic_launcher;
    }

    @Override
    protected TourReportFragment getTourReportFragment() {
        return new ContractorTourReportFragment();
    }
}
