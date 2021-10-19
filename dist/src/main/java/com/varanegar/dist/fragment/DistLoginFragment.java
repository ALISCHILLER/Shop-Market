package com.varanegar.dist.fragment;

import com.varanegar.dist.DistTourReportFragment;
import com.varanegar.dist.R;
import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.vaslibrary.ui.fragment.LoginFragment;
import com.varanegar.vaslibrary.ui.fragment.TourReportFragment;

/**
 * Created by atp on 3/10/2017.
 */
public class DistLoginFragment extends LoginFragment {
    @Override
    protected int getAppIconId() {
        return R.mipmap.ic_launcher;
    }

    @Override
    protected TourReportFragment getTourReportFragment() {
        return new DistTourReportFragment();
    }
}
