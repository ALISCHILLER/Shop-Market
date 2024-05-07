package com.varanegar.vaslibrary.action;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.util.fragment.extendedlist.ActionsAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasActivity;
import com.varanegar.vaslibrary.manager.locationmanager.LocationManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.OwnerKeysWrapper;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.location.LocationModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Torabi on 8/19/2017.
 */

public abstract class CheckDistanceAction extends CheckBarcodeAction {
    SysConfigManager sysConfigManager = new SysConfigManager(getActivity());
    OwnerKeysWrapper ownerKeys = sysConfigManager.readOwnerKeys();

    public CheckDistanceAction(MainVaranegarActivity activity, ActionsAdapter adapter, UUID selectedId) {
        super(activity, adapter, selectedId);
    }

    @Override
    @CallSuper
    @Nullable
    public String isEnabled() {
        String error = super.isEnabled();
        if (error != null)
            return error;

        if (checkCloudConfig(ConfigKey.CheckDistance, false) || getCustomer().IgnoreLocation > 0)
            return null;

//        CustomerCallManager customerCallManager = new CustomerCallManager(getActivity());
//        List<CustomerCallModel> calls = customerCallManager.getItems(new Query().from(CustomerCall.CustomerCallTbl).whereAnd(Criteria.equals(CustomerCall.CustomerId, getCustomer().UniqueId)));
//        if (calls.size() > 0) {
//            String statuse = customerCallManager.getName(calls, VaranegarApplication.is(VaranegarApplication.AppId.Contractor));
//            if (!(statuse.equals(getActivity().getString(R.string.not_visited_call_status))))
//                return null;
//        }

        SharedPreferences customerSharedPreferences = getActivity().getSharedPreferences("customerStatusShared", Context.MODE_PRIVATE);
        if (customerSharedPreferences.getBoolean(getCustomer().UniqueId.toString(), false))
            return null;

        // NGT-2183 check start and end time for distance control
        SysConfigModel startTimeControlConfig = getCloudConfig(ConfigKey.StartTimeControlDash);
        SysConfigModel endTimeControlConfig = getCloudConfig(ConfigKey.EndTimeControlDash);
        if (startTimeControlConfig != null && endTimeControlConfig != null &&
                startTimeControlConfig.Value != null && endTimeControlConfig.Value != null &&
                !startTimeControlConfig.Value.isEmpty() && !endTimeControlConfig.Value.isEmpty()
                && !startTimeControlConfig.Value.equals("00:00:00") && !endTimeControlConfig.Value.equals("00:00:00")) {
            String[] startTime = startTimeControlConfig.Value.split(":");
            String[] endTime = endTimeControlConfig.Value.split(":");
            if (startTime.length == 3 && endTime.length == 3) {
                int startHour = Integer.parseInt(startTime[0]);
                int startMinute = Integer.parseInt(startTime[1]);
                int endHour = Integer.parseInt(endTime[0]);
                int endMinute = Integer.parseInt(endTime[1]);
                Calendar start = Calendar.getInstance();
                start.set(Calendar.HOUR_OF_DAY, startHour);
                start.set(Calendar.MINUTE, startMinute);
                Date startDate = start.getTime();
                Calendar end = Calendar.getInstance();
                end.set(Calendar.HOUR_OF_DAY, endHour);
                end.set(Calendar.MINUTE, endMinute);
                Date endDate = end.getTime();
                Date now = new Date();
                if (now.compareTo(startDate) < 0 || now.compareTo(endDate) > 0) {
                    Timber.d("Time is beyond StartTimeControl: " + startTimeControlConfig.Value + " and EndTimeControl: " + endTimeControlConfig.Value);
                    return null;
                }
            }
        }

        SysConfigModel maxDistanceConfig = getCloudConfig(ConfigKey.MaxDistance);
        float maxDistance = 100;
        if (maxDistanceConfig != null) {
            try {
                maxDistance = Float.parseFloat(maxDistanceConfig.Value);
            } catch (Exception ex) {
                Timber.e(ex);
            }
        }

        if (getCustomer() == null)
            Timber.wtf("Customer not found");
        LocationModel customerLocation = getCustomer().getLocation();

        if (customerLocation == null)
            return null;

        Location lastLocation = ((VasActivity) getActivity()).getLastLocation();
        if (lastLocation == null) {
            Timber.d("Last location is not available");
            return getActivity().getString(R.string.your_location_is_not_available);
        }
        float distance = new LocationManager(getActivity()).distance(lastLocation, customerLocation);
        if (ownerKeys.isPoober()) {
            if (distance > maxDistance && ((VasActionsAdapter) getAdapter()).isCustomerIsInVisitDayPath()) {
                UUID taskId = getTaskUniqueId() == null ? UUID.randomUUID() : getTaskUniqueId();
                if (taskId.equals(UUID.fromString("c81c3571-6f8f-4a53-bb64-4742fbf64f3a")))
                    Timber.e("Distance is larger than limit for customerId " + getCustomer().UniqueId + ": last location is: (" + LocationManager.getLocationInfo(lastLocation) + " Accuracy: " + lastLocation.getAccuracy() + ") and customer location is: Latitude: (" + customerLocation.Latitude + " Longitude: " + customerLocation.Longitude + "), distance is " + distance + " and maxDistance is " + maxDistance);
                return getActivity().getString(R.string.distance_is_larger_than_limit);
            } else
                return null;
        }
        else if (checkCloudConfig(ConfigKey.LocationCheckRadiusOffTrack, false)
                && !(((VasActionsAdapter)getAdapter()).isCustomerIsInVisitDayPath())){
            return null;
        } else {
            if (distance > maxDistance) {
                UUID taskId = getTaskUniqueId() == null ? UUID.randomUUID() : getTaskUniqueId();
                if (taskId.equals(UUID.fromString("c81c3571-6f8f-4a53-bb64-4742fbf64f3a")))
                    Timber.e("Distance is larger than limit for customerId " + getCustomer().UniqueId + ": last location is: (" + LocationManager.getLocationInfo(lastLocation) + " Accuracy: " + lastLocation.getAccuracy() + ") and customer location is: Latitude: (" + customerLocation.Latitude + " Longitude: " + customerLocation.Longitude + "), distance is " + distance + " and maxDistance is " + maxDistance);
                return getActivity().getString(R.string.distance_is_larger_than_limit);
            } else
                return null;
        }
    }

}
