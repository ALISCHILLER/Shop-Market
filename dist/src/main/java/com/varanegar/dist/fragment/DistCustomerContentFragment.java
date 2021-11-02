package com.varanegar.dist.fragment;

import androidx.annotation.NonNull;

import com.varanegar.framework.util.fragment.extendedlist.Action;
import com.varanegar.vaslibrary.action.PaymentAction;
import com.varanegar.vaslibrary.action.SendCustomerActionsAction;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.UnknownBackOfficeException;
import com.varanegar.vaslibrary.ui.fragment.CustomersContentFragment;

import java.util.List;

import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 3/5/2018.
 */

public class DistCustomerContentFragment extends CustomersContentFragment {
    @Override
    protected void addActions(@NonNull List<Action> actions) {
        try {
            super.addActions(actions);
            BackOfficeType backOfficeType = new SysConfigManager(getContext()).getBackOfficeType();
            if (backOfficeType == BackOfficeType.ThirdParty) {
                SendCustomerActionsAction sendCustomerActionsAction = new SendCustomerActionsAction(getVaranegarActvity(), getActionsAdapter(), getSelectedId());
                sendCustomerActionsAction.setActionCallBack(() -> {
                    updateItem();
                    updateCustomer();
                });
                actions.add(sendCustomerActionsAction);
            }
        } catch (UnknownBackOfficeException e) {
            Timber.e(e);
        }
    }
}
