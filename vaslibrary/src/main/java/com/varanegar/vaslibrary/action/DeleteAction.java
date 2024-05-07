package com.varanegar.vaslibrary.action;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.util.fragment.extendedlist.ActionsAdapter;
import com.varanegar.framework.util.prefs.Preferences;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.customeractiontimemanager.CustomerActionTimeManager;
import com.varanegar.vaslibrary.manager.customeractiontimemanager.CustomerActions;
import com.varanegar.vaslibrary.manager.customercall.CustomerPrintCountManager;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.locationmanager.LocationManager;
import com.varanegar.vaslibrary.manager.locationmanager.LogLevel;
import com.varanegar.vaslibrary.manager.locationmanager.LogType;
import com.varanegar.vaslibrary.manager.locationmanager.OnSaveLocation;
import com.varanegar.vaslibrary.manager.locationmanager.TrackingLogManager;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.DeleteEventViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.DeleteOperationLocationViewModel;
import com.varanegar.vaslibrary.manager.printer.CancelInvoiceManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.location.LocationModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;

import java.util.GregorianCalendar;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Torabi on 8/26/2017.
 */

public class DeleteAction extends CheckPathAction {
    @Nullable
    @Override
    public UUID getTaskUniqueId() {
        return null;
    }

    public DeleteAction(MainVaranegarActivity activity, ActionsAdapter adapter, UUID selectedId) {
        super(activity, adapter, selectedId);
        icon = R.drawable.ic_close_black_24dp;
    }

    @Nullable
    @Override
    protected String isMandatory() {
        return null;
    }

    @Override
    public String getName() {
        return getActivity().getString(R.string.remove_operation);
    }

    @Nullable
    @Override
    public String isEnabled() {
        String error = super.isEnabled();
        if (error != null)
            return error;

        if (getCallManager().isDataSent(getCalls(), null))
            return getActivity().getString(R.string.customer_operation_is_sent_already);

        if (canNotEditOperationAfterPrint())
            return getActivity().getString(R.string.can_not_edit_customer_operation_after_print);

        return null;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    private boolean hasDelivery() {
        return getCallManager().hasDeliveryCall(getCalls(), null, null);
    }

    @Override
    public void run() {
        setRunning(true);
        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            SysConfigModel cancelRegistration = getCloudConfig(ConfigKey.CancelRegistration);
            if (!hasDelivery() || SysConfigManager.compare(cancelRegistration, true) || getPrintCounts() == 0) {
                String errorMessage = getActivity().getString(R.string.are_you_deleting_actions_this_is_unrecoverable);
                if (getPrintCounts() > 0)
                    errorMessage = errorMessage + "\n" + getActivity().getString(R.string.customer_invoice_is_printed);
                CuteMessageDialog dialog = new CuteMessageDialog(getActivity());
                dialog.setMessage(errorMessage);
                dialog.setTitle(R.string.warning);
                dialog.setIcon(Icon.Warning);
                dialog.setPositiveButton(R.string.yes, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        delete();
                    }
                });
                dialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setRunning(false);
                    }
                });
                dialog.show();
            } else {
                setRunning(false);
                showErrorMessage(R.string.canceling_invoice_is_not_possible);
            }
        } else {
            String errorMessage = getActivity().getString(R.string.are_you_deleting_actions_this_is_unrecoverable);
            if (getPrintCounts() > 0)
                errorMessage = errorMessage + "\n" + getActivity().getString(R.string.customer_invoice_is_printed);
            CuteMessageDialog dialog = new CuteMessageDialog(getActivity());
            dialog.setMessage(errorMessage);
            dialog.setTitle(R.string.warning);
            dialog.setIcon(Icon.Warning);
            dialog.setPositiveButton(R.string.yes, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    delete();
                }
            });
            dialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setRunning(false);
                }
            });
            dialog.show();
        }

    }

    private void delete() {
        final CustomerPrintCountManager customerPrintCountManager = new CustomerPrintCountManager(getActivity());
        final CustomerCallManager callManager = new CustomerCallManager(getActivity());
        try {
            if (getPrintCounts() > 0) {
                CancelInvoiceManager cancelInvoiceManager = new CancelInvoiceManager(getActivity());
                cancelInvoiceManager.addCancelInvoice(getSelectedId());
                customerPrintCountManager.resetCount(getSelectedId());
            }
            callManager.removeAllCalls(getSelectedId());
            callManager.removeCalls(getSelectedId());
            new CustomerActionTimeManager(getActivity()).delete(getSelectedId(), CustomerActions.CustomerCallEnd);
            Preferences preferences = new Preferences(getActivity());
            preferences.putBoolean(Preferences.InvoiceSelection, getSelectedId(), null, null, false);
            setRunning(false);
            runActionCallBack();
            TrackingLogManager.addLog(getActivity(), LogType.ORDER_EVENT, LogLevel.Info, "حذف عملیات مشتری " + getCustomer().CustomerCode + " (" + getCustomer().CustomerName + ")");
            DeleteOperationLocationViewModel locationViewModel = new DeleteOperationLocationViewModel();
            locationViewModel.eventData = new DeleteEventViewModel();
            locationViewModel.eventData.Time = DateHelper.toString(new GregorianCalendar(), DateFormat.MicrosoftDateTime);
            locationViewModel.eventData.CustomerId = getSelectedId();
            locationViewModel.eventData.CustomerName = getCustomer().CustomerName;
            locationViewModel.eventData.CustomerCode = getCustomer().CustomerCode;
            locationViewModel.eventData.Description = "خذف عملیات مشتری " + getCustomer().CustomerCode + " (" + getCustomer().CustomerName + ")";
            TrackingLogManager.addLog(getActivity(), LogType.WIFI_OFF, LogLevel.Info);
            final LocationManager locationManager = new LocationManager(getActivity());

            if (VaranegarApplication
                    .is(VaranegarApplication.AppId.PreSales)&&
                    checkCloudConfig(ConfigKey.VisitCustomersNotInPath, true)
                    && !(((VasActionsAdapter) getAdapter()).isCustomerIsInVisitDayPath())){

                SharedPreferences sharedPreferences = getActivity()
                        .getSharedPreferences("CountVisitCustomersNotIn", Context.MODE_PRIVATE);
                int countVisitCustomersNotInInt = sharedPreferences.getInt("1a1b45d8-b331-45db-ab18-15cc665ecfb3", 0);
                countVisitCustomersNotInInt -=1;
                sharedPreferences.edit()
                        .putInt("1a1b45d8-b331-45db-ab18-15cc665ecfb3", countVisitCustomersNotInInt)
                        .apply();
            }
            locationManager.addTrackingPoint(locationViewModel, new OnSaveLocation() {
                @Override
                public void onSaved(LocationModel location) {
                    locationManager.tryToSendItem(location);
                }

                @Override
                public void onFailed(String error) {

                }
            });
        } catch (Exception ex) {
            setRunning(false);
            Timber.e(ex);
            CuteMessageDialog dialog = new CuteMessageDialog(getActivity());
            dialog.setIcon(Icon.Error);
            dialog.setTitle(R.string.error);
            dialog.setMessage(R.string.error_saving_request);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        }
    }

    void showErrorMessage(@StringRes int error) {
        MainVaranegarActivity activity = getActivity();
        if (activity != null && !activity.isFinishing()) {
            showErrorMessage(activity.getString(error));
        }
    }

    void showErrorMessage(String error) {
        MainVaranegarActivity activity = getActivity();
        if (activity != null && !activity.isFinishing()) {
            CuteMessageDialog dialog = new CuteMessageDialog(activity);
            dialog.setTitle(R.string.error);
            dialog.setMessage(error);
            dialog.setIcon(Icon.Error);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        }
    }
}
