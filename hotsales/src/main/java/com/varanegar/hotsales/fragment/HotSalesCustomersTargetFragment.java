package com.varanegar.hotsales.fragment;

import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.vaslibrary.ui.fragment.CustomerTargetContentFragment;

/**
 * Created by g.aliakbar on 11/04/2018.
 */

public class HotSalesCustomersTargetFragment extends CustomerTargetContentFragment {
    @Override
    protected VaranegarFragment getContentFragment() {
        return new HotSalesCustomersContentFragment();
    }
}
