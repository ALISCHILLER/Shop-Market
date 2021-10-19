package com.varanegar.dist.fragment;

import com.varanegar.dist.DistTourReportFragment;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.vaslibrary.ui.fragment.SendTourFragment;

/**
 * Created by A.Torabi on 12/1/2018.
 */

public class DistSendTourFragment extends SendTourFragment{
    @Override
    protected VaranegarFragment getCustomersFragment() {
        return new DistCustomersFragment();
    }

    @Override
    protected VaranegarFragment getTourReportFragment() {
        return new DistTourReportFragment();
    }
}
