package com.varanegar.presale.fragment;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.presale.R;
import com.varanegar.presale.ui.PreSalesTourReportFragment;
import com.varanegar.vaslibrary.ui.fragment.LoginFragment;
import com.varanegar.vaslibrary.ui.fragment.TourReportFragment;

/**
 * Created by s.foroughi on 11/03/2017.
 */
public class PresalesLoginFragment extends LoginFragment {
    @Override
    protected int getAppIconId() {
        return R.mipmap.ic_launcher;
    }

    @Override
    protected TourReportFragment getTourReportFragment() {
        return new PreSalesTourReportFragment();
    }
}
