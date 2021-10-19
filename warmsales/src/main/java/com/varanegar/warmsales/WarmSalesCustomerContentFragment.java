package com.varanegar.warmsales;

import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.vaslibrary.ui.fragment.CustomerTargetReportDetailContentFragment;

public class WarmSalesCustomerContentFragment extends CustomerTargetReportDetailContentFragment {
    @Override
    protected VaranegarFragment getContentFragment() {
        return new WarmSalesCustomerContentFragment();
    }
}