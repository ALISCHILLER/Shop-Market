package com.varanegar.hotsales.fragment;

import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.hotsales.HotSalesTourReportFragment;
import com.varanegar.vaslibrary.ui.fragment.SendTourFragment;

/**
 * Created by A.Torabi on 11/28/2018.
 */

public class HotSalesSendTourFragment extends SendTourFragment {
    @Override
    protected VaranegarFragment getCustomersFragment() {
        return new HotSalesCustomersFragment();
    }

    @Override
    protected VaranegarFragment getTourReportFragment() {
        return new HotSalesTourReportFragment();
    }
}
