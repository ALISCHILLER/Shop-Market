package com.varanegar.contractor;

import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.vaslibrary.ui.fragment.SendTourFragment;

public class ContractorSendTourFragment extends SendTourFragment {
    @Override
    protected VaranegarFragment getCustomersFragment() {
        return new ContractorCustomersFragment();
    }

    @Override
    protected VaranegarFragment getTourReportFragment() {
        return new ContractorTourReportFragment();
    }
}
