package com.varanegar.vaslibrary.action;

import android.content.Context;
import android.location.Location;

import androidx.annotation.Nullable;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.util.fragment.extendedlist.ActionsAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasActivity;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.customer.Customer;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.location.LocationModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.ui.fragment.CustomerLocationFragment;

import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 01/21/2017.
 */


public class SetCustomerLocationAction extends CheckDistanceAction {

    @Nullable
    @Override
    public UUID getTaskUniqueId() {
        return UUID.fromString("4403FD84-6C1A-4B70-9A1B-7B58E834A766");
    }

    public SetCustomerLocationAction(MainVaranegarActivity activity, ActionsAdapter adapter, UUID selectedId) {
        super(activity, adapter, selectedId);
        icon = R.drawable.ic_edit_location_white_24dp;
        CustomerCallManager callManager = new CustomerCallManager(getActivity());
        boolean isChanged = callManager.isLocationChanged(getCalls());
        if (!isChanged) {
            CustomerModel currentCustomer = getCustomer();
            if (currentCustomer.ParentCustomerId != null && currentCustomer.Latitude == 0 && currentCustomer.Longitude == 0) {
                CustomerManager customerManager = new CustomerManager(getActivity());
                CustomerModel customer = customerManager.getItem(new Query().from(Customer.CustomerTbl)
                        .whereAnd(Criteria.equals(Customer.ParentCustomerId, currentCustomer.ParentCustomerId))
                        .whereAnd(Criteria.notIsNull(Customer.Latitude).and(Criteria.notEquals(Customer.Latitude, 0)))
                        .whereAnd(Criteria.notIsNull(Customer.Longitude).and(Criteria.notEquals(Customer.Longitude, 0)))
                        .whereAnd(Criteria.notEquals(Customer.UniqueId, currentCustomer.UniqueId)));
                if (customer != null) {
                    currentCustomer.Latitude = customer.Latitude;
                    currentCustomer.Longitude = customer.Longitude;
                    try {
                        customerManager.update(currentCustomer);
                        callManager.saveLocationCall(currentCustomer.UniqueId);
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }
            }
        }
    }

    @Override
    public String getName() {
        return getActivity().getString(R.string.set_location);
    }

    @Override
    public boolean isDone() {
        CustomerCallManager callManager = new CustomerCallManager(getActivity());
        return callManager.isLocationChanged(getCalls());
    }

    @Nullable
    @Override
    public String isEnabled() {
        String error = super.isEnabled();
        if (error != null)
            return error;

        if (getCallManager().isDataSent(getCalls(), null))
            return getActivity().getString(R.string.customer_operation_is_sent_already);

        int priority = getTaskPriority();
        if (priority == -1) {
            SysConfigModel sysConfigModel = getCloudConfig(ConfigKey.SetCustomerLocation);
            if (!SysConfigManager.compare(sysConfigModel, true))
                return getActivity().getString(R.string.the_action_is_disabled_for_you);
        }

        LocationModel customerLocation = getCustomer().getLocation();
        if (customerLocation != null && !getCallManager().isLocationChanged(getCalls()))
            return getActivity().getString(R.string.customer_location_is_set);

        return null;
    }

    @Nullable
    @Override
    protected String isMandatory() {
//        TaskPriorityModel taskPriorityModel = getTaskPriorities().get(getTaskUniqueId());
//        if (taskPriorityModel == null || !taskPriorityModel.IsEnabled)
//            return null;

        if (getCustomer().IgnoreLocation > 0)
            return null;
        SysConfigModel sysConfigModel = getCloudConfig(ConfigKey.SetCustomerLocation);
        if (SysConfigManager.compare(sysConfigModel, true) && getCustomer().Latitude == 0 && getCustomer().Longitude == 0 && !getCallManager().isLackOfVisit(getCalls()))
            return getActivity().getString(R.string.please_set_customer_location);
        else
            return null;
    }

    @Override
    public void run() {
        setRunning(true);
        android.location.LocationManager locationManager1 = (android.location.LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager1.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
        if (!gps) {
            getActivity().showSnackBar(R.string.please_turn_on_location, MainVaranegarActivity.Duration.Short);
            setRunning(false);
            return;
        }
        Location lastLocation = ((VasActivity) getActivity()).getLastLocation();
        if (lastLocation == null) {
            getActivity().showSnackBar(R.string.location_is_not_available_please_try_again, MainVaranegarActivity.Duration.Short);
            setRunning(false);
            return;
        }
        CustomerLocationFragment customerLocationFragment = new CustomerLocationFragment();
        customerLocationFragment.setCustomerId(getSelectedId());
        getActivity().pushFragment(customerLocationFragment);
        setRunning(false);
    }
}
