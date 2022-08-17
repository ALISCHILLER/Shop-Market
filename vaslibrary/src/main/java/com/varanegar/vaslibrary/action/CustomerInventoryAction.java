package com.varanegar.vaslibrary.action;

import androidx.annotation.Nullable;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.fragment.extendedlist.ActionsAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.ui.fragment.CustomerInventoryFragment;

import java.util.UUID;

/**
 * Created by A.Torabi on 9/12/2017.
 */

public class CustomerInventoryAction extends CheckDistanceAction {
    @Nullable
    @Override
    public UUID getTaskUniqueId() {
        return UUID.fromString("30ABF5E3-9E75-47CA-9432-B00E606D5F7F");
    }

    public CustomerInventoryAction(MainVaranegarActivity activity, ActionsAdapter adapter, UUID selectedId) {
        super(activity, adapter, selectedId);
        icon = R.drawable.ic_list_inventory_white_24dp;
    }

    @Nullable
    @Override
    protected String isMandatory() {
        int p = getTaskPriority();
        if (p > 0 && Linq.findFirstIndex(getTaskPriorities().values(), item -> item.Priority > p) > 0)
            return getActivity().getString(R.string.inventory_customer_is_mandatory);
        else
            return null;
    }

    @Nullable
    @Override
    public String isEnabled() {
        String err = super.isEnabled();
        if (err != null)
            return err;

        if (getCallManager().isDataSent(getCalls(), null))
            return getActivity().getString(R.string.customer_operation_is_sent_already);

        if (getCallManager().isLackOfVisit(getCalls()))
            return getActivity().getString(R.string.customer_is_not_visited);
        return null;
    }

    @Override
    public String getName() {
        return getActivity().getString(R.string.customer_inventory);
    }


    @Override
    public boolean isDone() {
        return getCallManager().hasInventoryCall(getCalls());
    }

    @Override
    public void run() {
        CustomerInventoryFragment fragment = new CustomerInventoryFragment();
        fragment.setCustomerId(getSelectedId());
        getActivity().pushFragment(fragment);
    }
}
