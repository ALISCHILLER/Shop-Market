package com.varanegar.hotsales.fragment;

import com.varanegar.hotsales.HotSalesTourReportFragment;
import com.varanegar.hotsales.R;
import com.varanegar.vaslibrary.ui.fragment.LoginFragment;
import com.varanegar.vaslibrary.ui.fragment.TourReportFragment;

/**
 * Created by A.Jafarzadeh on 3/6/2018.
 */

public class HotSalesLoginFragment extends LoginFragment {
    @Override
    protected int getAppIconId() {
        return R.mipmap.ic_launcher;
    }

    @Override
    protected TourReportFragment getTourReportFragment() {
        return new HotSalesTourReportFragment();
    }
}
