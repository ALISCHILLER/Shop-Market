package com.varanegar.vaslibrary.action;

import androidx.annotation.Nullable;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.util.fragment.extendedlist.ActionsAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.ui.fragment.settlement.SettlementFragment;

import java.util.UUID;

/**
 * Created by A.Torabi on 12/31/2017.
 */

public class PaymentAction extends CheckDistanceAction {
    public PaymentAction(MainVaranegarActivity activity, ActionsAdapter adapter, UUID selectedId) {
        super(activity, adapter, selectedId);
        icon = R.drawable.ic_cash_payment_black_24dp;
    }

    private boolean isLackOfVisit() {
        return getCallManager().isLackOfVisit(getCalls());
    }

    @Nullable
    @Override
    public String isEnabled() {
        String error = super.isEnabled();
        if (error != null)
            return error;

        if (getCallManager().isDataSent(getCalls(), null))
            return getActivity().getString(R.string.customer_operation_is_sent_already);

        if (VaranegarApplication.is(VaranegarApplication.AppId.HotSales) && checkCloudConfig(ConfigKey.ScientificVisit, true))
            return getActivity().getString(R.string.payment_is_disabled);
        if (canNotEditOperationAfterPrint())
            return getActivity().getString(R.string.can_not_edit_customer_operation_after_print);
        if (isLackOfVisit())
            return getActivity().getString(R.string.customer_is_not_visited);
        else
            return null;
    }

    @Nullable
    @Override
    public UUID getTaskUniqueId() {
        return null;
    }

    @Override
    public String getName() {
        return getActivity().getString(R.string.settlement);
    }

    @Override
    public boolean isDone() {
        return getCallManager().hasPaymentCall(getCalls());
    }

    @Nullable
    @Override
    protected String isMandatory() {
        return null;
    }

    @Override
    public void run() {
        SettlementFragment fragment = new SettlementFragment();
        fragment.setCustomerId(getSelectedId());
        getActivity().pushFragment(fragment);
    }
}
