package com.varanegar.vaslibrary.action;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.fragment.extendedlist.Action;
import com.varanegar.framework.util.fragment.extendedlist.ActionsAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.SubsystemType;
import com.varanegar.vaslibrary.base.SubsystemTypeId;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerPrintCountManager;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigMap;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.customercall.CustomerCallModel;
import com.varanegar.vaslibrary.model.customercall.TaskPriorityModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 9/27/2017.
 */

public abstract class BaseAction extends Action {

    private final ActionData actionData;
    private CustomerCallManager callManager;
    private BaseAction leveragedAction;
    private int printCounts;

    public BaseAction(MainVaranegarActivity activity, ActionsAdapter adapter, UUID selectedId) {
        super(activity, adapter, selectedId);
        callManager = new CustomerCallManager(getActivity());
        actionData = new ActionData(getActivity(), new CustomerManager(getActivity()).getItem(selectedId), getCloudConfigs());

    }

    @Override
    public void refresh() {
        CustomerPrintCountManager customerPrintCountManager = new CustomerPrintCountManager(getActivity());
        printCounts = customerPrintCountManager.getCount(getSelectedId());
        super.refresh();
    }

    public CustomerModel getCustomer() {
        return ((VasActionsAdapter) getAdapter()).getCustomer();
    }

    public List<CustomerCallModel> getCalls() {
        return ((VasActionsAdapter) getAdapter()).getCalls();
    }

    public List<Action> getActions() {
        return getAdapter().getActions();
    }

    @Nullable
    public abstract UUID getTaskUniqueId();

    protected int getTaskPriority() {
        if (!((VasActionsAdapter) getAdapter()).checkPriorities())
            return 0;
        TaskPriorityModel taskPriority = getTaskPriorities().get(getTaskUniqueId());
        if (taskPriority == null)
            return 0;
        else if (!taskPriority.IsEnabled)
            return -1;
        else
            return taskPriority.Priority;
    }

    public HashMap<UUID, TaskPriorityModel> getTaskPriorities() {
        return ((VasActionsAdapter) getAdapter()).getTaskPriorities();
    }

    @Nullable
    @Override
    @CallSuper
    public String isEnabled() {
        if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
            SysConfigManager sysConfigManager = new SysConfigManager(getActivity());
            SysConfigModel inactiveCustomers = sysConfigManager
                    .read(ConfigKey.SendInactiveCustomers, SysConfigManager.cloud);
            SysConfigModel TakeOrderFromInactiveCustomers = sysConfigManager
                    .read(ConfigKey.TakeOrderFromInactiveCustomers, SysConfigManager.cloud);
            if (!getCustomer().IsActive && (SysConfigManager.compare(inactiveCustomers, false)) &&
                    SysConfigManager.compare(TakeOrderFromInactiveCustomers, false))
                return getActivity().getString(R.string.the_action_is_disabled_for_you);
        }
        UUID taskId = getTaskUniqueId();
        if (taskId == null)
            return null;
        final int priority = getTaskPriority();
        if (priority == 0)
            return null;

        final List<TaskPriorityModel> tasks = Linq.findAll(getTaskPriorities().values(), item -> !item.DeviceTaskUniqueId.equals(taskId) && item.Priority < priority && item.Priority > 0);

        if (tasks.size() == 0)
            return null;
        else {
            TaskPriorityModel task = Linq.findFirst(tasks, t -> {
                BaseAction action = (BaseAction) Linq.findFirst(getActions(), act -> t.DeviceTaskUniqueId.equals(((BaseAction) act).getTaskUniqueId()));
                if (action != null && !action.getIsDone() && action.isMandatory() != null) {
                    leveragedAction = action;
                    return true;
                } else
                    return false;
            });
            if (task != null) {
                if (currentAction == null)
                    currentAction = leveragedAction;
                return getActivity().getString(R.string.please_honor_priorities) + "    " + leveragedAction.isMandatory();
            } else
                return null;
        }
    }

    @Nullable
    @Override
    public String isForce() {
        boolean isConfirmed = callManager.isConfirmed(getCalls());
        if (getCalls().size() == 0 || isConfirmed)
            return null;
        return isMandatory();
    }

    @Nullable
    protected abstract String isMandatory();

    protected CustomerCallManager getCallManager() {
        return callManager;
    }

    protected boolean checkCloudConfig(ConfigKey configKey, @NonNull String value) {
        return ((VasActionsAdapter) getAdapter()).getCloudConfigs().compare(configKey, value);
    }

    protected boolean checkCloudConfig(ConfigKey configKey, boolean value) {
        return ((VasActionsAdapter) getAdapter()).getCloudConfigs().compare(configKey, value);

    }

    protected boolean checkCloudConfig(ConfigKey configKey, @NonNull UUID value) {
        return ((VasActionsAdapter) getAdapter()).getCloudConfigs().compare(configKey, value);
    }

    protected SysConfigModel getCloudConfig(ConfigKey configKey) {
        return ((VasActionsAdapter) getAdapter()).getCloudConfigs().get(configKey);
    }

    protected ConfigMap getCloudConfigs() {
        return ((VasActionsAdapter) getAdapter()).getCloudConfigs();
    }

    public int getPrintCounts() {
        return printCounts;
    }

    @SubsystemType(id = SubsystemTypeId.Dist)
    private boolean hasDelivery() {
        return getCallManager().hasDeliveryCall(getCalls(), null, null);
    }

    public boolean canNotEditOperationAfterPrint() {
        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            SysConfigModel cancelRegistration = getCloudConfig(ConfigKey.CancelRegistration);
            if (hasDelivery() && !SysConfigManager.compare(cancelRegistration, true) && getPrintCounts() > 0) {
                return true;
            }
        }
        if (VaranegarApplication.is(VaranegarApplication.AppId.HotSales)) {
            boolean unSubmitCancellation = SysConfigManager.compare(getCloudConfig(ConfigKey.UnSubmitCancellation), true);
            return (unSubmitCancellation && (getPrintCounts() > 0));
        }
        return false;

    }

    public ActionData getActionData() {
        return actionData;
    }
}
