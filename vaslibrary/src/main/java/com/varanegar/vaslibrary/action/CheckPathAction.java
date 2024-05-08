package com.varanegar.vaslibrary.action;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.fragment.extendedlist.ActionsAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.customercall.CustomerCallType;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 9/6/2017.
 */

public abstract class CheckPathAction extends BaseAction {


    public CheckPathAction(MainVaranegarActivity activity, ActionsAdapter adapter, UUID selectedId) {
        super(activity, adapter, selectedId);
    }

    private boolean isConfirmed() {
        return getCallManager().isConfirmed(Linq.remove(getCalls(),
                it -> it.CallType == CustomerCallType.SendData));
    }

    @Nullable
    @Override
    @CallSuper
    public String isEnabled() {
        String error = super.isEnabled();
        if (error != null)
            return error;

        String isCustomerAvailable = ((VasActionsAdapter) getAdapter()).getIsCustomerAvailable();
        if (isCustomerAvailable != null)
            return isCustomerAvailable;


        SharedPreferences sharedPreferences = getActivity()
                .getSharedPreferences("CountVisitCustomersNotIn", Context.MODE_PRIVATE);
        int countVisitCustomersNotInInt = sharedPreferences.getInt("1a1b45d8-b331-45db-ab18-15cc665ecfb3", 0);
        SysConfigManager sysConfigManager = new SysConfigManager(getActivity());
        SysConfigModel countVisit = sysConfigManager.read(ConfigKey.CountVisitCustomersNotIn, SysConfigManager.cloud);
        int countVisitInt = 0;
        if (countVisit != null) {
            countVisitInt = Integer.parseInt(countVisit.Value);
        }

        if (checkCloudConfig(ConfigKey.VisitCustomersNotInPath, true)
                && !(((VasActionsAdapter) getAdapter()).isCustomerIsInVisitDayPath())
                && countVisitCustomersNotInInt >= countVisitInt
                && countVisitInt != 0
                &&!isConfirmed())
            return getActivity().getString(R.string.countVisitInt_error,String.valueOf(countVisitInt));
        else if (checkCloudConfig(ConfigKey.VisitCustomersNotInPath, false)
                && !(((VasActionsAdapter) getAdapter()).isCustomerIsInVisitDayPath()))
            return getActivity().getString(R.string.can_not_do_for_other_pathes);
        else
            return null;
    }
}
