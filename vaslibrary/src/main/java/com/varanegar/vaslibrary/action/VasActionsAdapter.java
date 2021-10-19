package com.varanegar.vaslibrary.action;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.util.fragment.extendedlist.ActionsAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.SubsystemType;
import com.varanegar.vaslibrary.base.SubsystemTypeId;
import com.varanegar.vaslibrary.manager.CustomerPathViewManager;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallInvoiceManager;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.customercallmanager.TaskPriorityManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigMap;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.model.call.CustomerCallInvoiceModel;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.customercall.CustomerCallModel;
import com.varanegar.vaslibrary.model.customercall.TaskPriorityModel;
import com.varanegar.vaslibrary.model.customerpathview.CustomerPathViewModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.model.tour.TourModel;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 9/27/2017.
 */

public class VasActionsAdapter extends ActionsAdapter {

    private List<CustomerCallInvoiceModel> customerDistInvoices;

    public String getIsCustomerAvailable() {
        return isCustomerAvailable;
    }

    private String isCustomerAvailable;
    private final SysConfigModel checkPrioritiesConfig;

    public boolean isCustomerIsInVisitDayPath() {
        return customerIsInVisitDayPath;
    }

    private final boolean customerIsInVisitDayPath;

    public TourModel getTour() {
        return tour;
    }

    private final TourModel tour;

    public ConfigMap getCloudConfigs() {
        return configs;
    }

    private ConfigMap configs;

    public boolean checkPriorities() {
        return SysConfigManager.compare(checkPrioritiesConfig, true);
    }

    public CustomerModel getCustomer() {
        return customerModel;
    }

    private CustomerModel customerModel;

    public List<CustomerCallModel> getCalls() {
        return customerCalls;
    }

    private List<CustomerCallModel> customerCalls;

    public HashMap<UUID, TaskPriorityModel> getTaskPriorities() {
        return tasksMap;
    }

    private HashMap<UUID, TaskPriorityModel> tasksMap;

    public VasActionsAdapter(MainVaranegarActivity activity, UUID customerId) {
        super(activity, customerId);
        customerModel = new CustomerManager(activity).getItem(customerId);
        customerCalls = new CustomerCallManager(activity).loadCalls(customerId);
        List<TaskPriorityModel> tasks = new TaskPriorityManager(activity).getAll();
        tasksMap = new HashMap<>();
        for (TaskPriorityModel task :
                tasks) {
            tasksMap.put(task.DeviceTaskUniqueId, task);
        }
        checkPrioritiesConfig = new SysConfigManager(getActivity()).read(ConfigKey.DeviceTaskPriorityEnabled, SysConfigManager.cloud);
        configs = new SysConfigManager(getActivity()).read(SysConfigManager.cloud);

        CustomerPathViewManager customerPathViewManager = new CustomerPathViewManager(getActivity());
        TourManager tourManager = new TourManager(getActivity());
        tour = tourManager.loadTour();

        CustomerPathViewModel customerPathViewModel = customerPathViewManager.getItem(CustomerPathViewManager.checkIsInDayVisitPath(getCustomer(), tour.DayVisitPathId));
        customerIsInVisitDayPath = customerPathViewModel != null;

        SysConfigManager sysConfigManager = new SysConfigManager(getActivity());
        SysConfigModel sysConfigModel = sysConfigManager.read(ConfigKey.VisitingCustomersInOrderOfAttendingInRoute, SysConfigManager.cloud);
        if (SysConfigManager.compare(sysConfigModel, true)) {
            CustomerManager customerManager = new CustomerManager(getActivity());
            if (!(customerManager.isCustomerTurn(getCustomer())))
                isCustomerAvailable = getActivity().getString(R.string.visit_prev_customers);
        }
        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            CustomerCallInvoiceManager manager = new CustomerCallInvoiceManager(getActivity());
            customerDistInvoices = manager.getCustomerCallInvoices(getSelectedId());
        }
    }

    @SubsystemType(id = SubsystemTypeId.Dist)
    public List<CustomerCallInvoiceModel> getInvoices() {
        return customerDistInvoices;
    }

    @Override
    public void update() {
        customerModel = new CustomerManager(getActivity()).getItem(getSelectedId());
        customerCalls = new CustomerCallManager(getActivity()).loadCalls(getSelectedId());
    }


}
