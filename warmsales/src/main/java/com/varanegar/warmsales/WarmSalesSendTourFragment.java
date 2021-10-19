package com.varanegar.warmsales;

import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.vaslibrary.ui.fragment.SendTourFragment;

public class WarmSalesSendTourFragment extends SendTourFragment {
    @Override
    protected VaranegarFragment getCustomersFragment() {
        return new WarmSalesCustomersFragment();
    }

    @Override
    protected VaranegarFragment getTourReportFragment() {
        return new WarmSalesTourReportFragment();
    }
}
