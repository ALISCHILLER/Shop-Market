package com.varanegar.warmsales;

import com.varanegar.vaslibrary.ui.fragment.LoginFragment;
import com.varanegar.vaslibrary.ui.fragment.TourReportFragment;

public class WarmSalesLoginFragment extends LoginFragment {
    @Override
    protected int getAppIconId() {
        return R.mipmap.ic_launcher;
    }

    @Override
    protected TourReportFragment getTourReportFragment() {
        return new WarmSalesTourReportFragment();
    }
}
