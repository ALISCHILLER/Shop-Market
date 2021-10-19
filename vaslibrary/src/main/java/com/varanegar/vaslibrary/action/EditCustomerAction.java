package com.varanegar.vaslibrary.action;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.fragment.extendedlist.ActionsAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.OwnerKeysWrapper;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.ui.dialog.EditCustomerFragmentDialog;
import com.varanegar.vaslibrary.ui.dialog.EditCustomerZarFragmentDialog;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 01/21/2017.
 */

public class EditCustomerAction extends CheckBarcodeAction {

    @Nullable
    @Override
    public UUID getTaskUniqueId() {
        return UUID.fromString("CD63A5C1-2DA4-41D8-A597-68E61B0B7681");
    }

    public EditCustomerAction(MainVaranegarActivity activity, ActionsAdapter adapter, UUID selectedId) {
        super(activity, adapter, selectedId);
        icon = R.drawable.ic_mode_edit_black_24dp;
    }

    @Override
    public String getName() {
        return getActivity().getString(R.string.edit_customer_label);
    }

    @Override
    public String isEnabled() {
        String error = super.isEnabled();
        if (error != null)
            return error;

        if (getCallManager().isDataSent(getCalls(), null))
            return getActivity().getString(R.string.customer_operation_is_sent_already);

        if (checkCloudConfig(ConfigKey.AllowEditCustomer, false))
            return getActivity().getString(R.string.not_allow_edit_customer);

        if (getCallManager().isLackOfVisit(getCalls()))
            return getActivity().getString(R.string.customer_is_not_visited);

        return null;
    }

    @Override
    public boolean isDone() {
        return getCallManager().isEdited(getCalls());
    }

    @Nullable
    @Override
    protected String isMandatory() {
        int p = getTaskPriority();
        if (p > 0 && Linq.findFirstIndex(getTaskPriorities().values(), item -> item.Priority > p) > 0)
            return getActivity().getString(R.string.editing_customer_is_mandatory);
        else
            return null;
    }


    @Override
    public void run() {
        SysConfigManager sysConfigManager = new SysConfigManager(getActivity());
        OwnerKeysWrapper ownerKeysWrapper = sysConfigManager.readOwnerKeys();
        if (ownerKeysWrapper.isZarMakaron()) {
            EditCustomerZarFragmentDialog editCustomerFragmentDialog = new EditCustomerZarFragmentDialog();
            editCustomerFragmentDialog.onCustomerEditedCallBack = this::runActionCallBack;
            Bundle bundle = new Bundle();
            bundle.putString("68565e5e-d407-4858-bc5f-fd52b9318734", getSelectedId().toString());
            editCustomerFragmentDialog.setArguments(bundle);
            editCustomerFragmentDialog.show(getActivity().getSupportFragmentManager(), "EditCustomerFragmentDialog");
        } else {
            EditCustomerFragmentDialog editCustomerFragmentDialog = new EditCustomerFragmentDialog();
            editCustomerFragmentDialog.onCustomerEditedCallBack = this::runActionCallBack;
            Bundle bundle = new Bundle();
            bundle.putString("68565e5e-d407-4858-bc5f-fd52b9318734", getSelectedId().toString());
            editCustomerFragmentDialog.setArguments(bundle);
            editCustomerFragmentDialog.show(getActivity().getSupportFragmentManager(), "EditCustomerFragmentDialog");
        }
    }
}
