package com.varanegar.warmsales;

import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.vaslibrary.ui.fragment.CustomerTargetContentFragment;

public class WarmSalesCustomerTargetFragment extends CustomerTargetContentFragment {
    @Override
    protected VaranegarFragment getContentFragment() {
        return new WarmSalesCustomerContentFragment();
    }
}
