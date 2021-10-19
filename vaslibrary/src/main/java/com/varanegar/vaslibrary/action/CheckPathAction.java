package com.varanegar.vaslibrary.action;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.util.fragment.extendedlist.ActionsAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 9/6/2017.
 */

public abstract class CheckPathAction extends BaseAction {


    public CheckPathAction(MainVaranegarActivity activity, ActionsAdapter adapter, UUID selectedId) {
        super(activity, adapter, selectedId);
    }

    @Nullable
    @Override
    @CallSuper
    public String isEnabled() {
        String error = super.isEnabled();
        if (error != null)
            return error;

        String isCustomerAvailable = ((VasActionsAdapter)getAdapter()).getIsCustomerAvailable();
        if (isCustomerAvailable != null)
            return isCustomerAvailable;

        if (checkCloudConfig(ConfigKey.VisitCustomersNotInPath, false) && !(((VasActionsAdapter)getAdapter()).isCustomerIsInVisitDayPath()))
            return getActivity().getString(R.string.can_not_do_for_other_pathes);
        else
            return null;
    }
}
