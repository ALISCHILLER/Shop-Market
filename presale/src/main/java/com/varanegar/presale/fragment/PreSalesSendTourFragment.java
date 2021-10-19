package com.varanegar.presale.fragment;

import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.presale.ui.PreSalesTourReportFragment;
import com.varanegar.vaslibrary.ui.fragment.SendTourFragment;

/**
 * Created by A.Torabi on 11/17/2018.
 */

public class PreSalesSendTourFragment extends SendTourFragment {
    @Override
    protected VaranegarFragment getCustomersFragment() {
        return new PreSalesCustomersFragment();
    }

    @Override
    protected VaranegarFragment getTourReportFragment() {
        return new PreSalesTourReportFragment();
    }
}
