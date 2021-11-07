package com.varanegar.presale.fragment;

import androidx.annotation.NonNull;

import com.varanegar.framework.util.fragment.extendedlist.Action;
import com.varanegar.vaslibrary.ui.fragment.CustomersContentFragment;

import java.util.List;

/**
 * Created by atp on 5/2/2017.
 */

public class PreSalesCustomerContentFragment extends CustomersContentFragment {

    @Override
    protected void addActions(@NonNull List<Action> actions) {
        super.addActions(actions);

//        actions.add(new PaymentAction(getVaranegarActvity(), getActionsAdapter(), getSelectedId()));

        SendCustomerActionsAction sendCustomerActionsAction = new SendCustomerActionsAction(getVaranegarActvity(), getActionsAdapter(), getSelectedId());
        sendCustomerActionsAction.setActionCallBack(new Action.ActionCallBack() {
            @Override
            public void done() {
                updateItem();
                updateCustomer();
            }
        });
        actions.add(sendCustomerActionsAction);

    }
}
