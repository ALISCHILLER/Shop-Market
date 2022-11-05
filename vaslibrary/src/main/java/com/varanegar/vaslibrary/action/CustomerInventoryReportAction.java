package com.varanegar.vaslibrary.action;

import androidx.annotation.Nullable;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.util.fragment.extendedlist.ActionsAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.ui.report.report_new.customer_inventory_report.BaseCustomerInventoryReportFragment;
import com.varanegar.vaslibrary.ui.report.report_new.customer_inventory_report.CustomerInventoryReportFragment;

import java.util.UUID;

/**
 * Create by Mehrdad Latifi on 8/23/2022
 */

public class CustomerInventoryReportAction extends CheckDistanceAction{

    public CustomerInventoryReportAction(MainVaranegarActivity activity, ActionsAdapter adapter, UUID selectedId) {
        super(activity, adapter, selectedId);
        icon = R.drawable.ic_report_24dp;
    }

    @Override
    public String getName() {
        return getActivity().getString(R.string.customerInventoryReport);
    }

    @Override
    protected boolean isDone() {
        return false;
    }

    @Override
    public void run() {
        CustomerInventoryReportFragment fragment = new CustomerInventoryReportFragment();
        fragment.setCustomerId(getSelectedId());
        getActivity().pushFragment(fragment);
    }


    @Nullable
    @Override
    public UUID getTaskUniqueId() {
        return UUID.fromString("30ABF5E3-9E75-47CA-9432-B00E606D5F7F");
    }

    @Nullable
    @Override
    protected String isMandatory() {
        return null;
    }
}
