package com.varanegar.vaslibrary.action.confirm;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.network.Connectivity;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.fragment.extendedlist.Action;
import com.varanegar.framework.util.fragment.extendedlist.ActionsAdapter;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.action.CheckPathAction;
import com.varanegar.vaslibrary.action.IActionUtilityCallBack;
import com.varanegar.vaslibrary.action.VasActionsAdapter;
import com.varanegar.vaslibrary.base.BackupManager;
import com.varanegar.vaslibrary.base.SubsystemType;
import com.varanegar.vaslibrary.base.SubsystemTypeId;
import com.varanegar.vaslibrary.manager.CustomerCallOrderOrderViewManager;
import com.varanegar.vaslibrary.manager.RequestReportViewManager;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.customeractiontimemanager.CustomerActionTimeManager;
import com.varanegar.vaslibrary.manager.customeractiontimemanager.CustomerActions;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallOrderManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallOrderPreviewManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallReturnManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerPrintCountManager;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.locationmanager.LocationManager;
import com.varanegar.vaslibrary.manager.locationmanager.LogLevel;
import com.varanegar.vaslibrary.manager.locationmanager.LogType;
import com.varanegar.vaslibrary.manager.locationmanager.OnSaveLocation;
import com.varanegar.vaslibrary.manager.locationmanager.TrackingLicense;
import com.varanegar.vaslibrary.manager.locationmanager.TrackingLogManager;
import com.varanegar.vaslibrary.manager.paymentmanager.PaymentManager;
import com.varanegar.vaslibrary.manager.paymentmanager.exceptions.ControlPaymentException;
import com.varanegar.vaslibrary.manager.paymentmanager.exceptions.ThirdPartyControlPaymentChangedException;
import com.varanegar.vaslibrary.manager.picture.PictureCustomerViewManager;
import com.varanegar.vaslibrary.manager.printer.CancelInvoiceManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.UnknownBackOfficeException;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.model.RequestReportView.RequestReportViewModel;
import com.varanegar.vaslibrary.model.call.CustomerCallInvoiceModel;
import com.varanegar.vaslibrary.model.call.CustomerCallOrderModel;
import com.varanegar.vaslibrary.model.call.CustomerCallOrderPreviewModel;
import com.varanegar.vaslibrary.model.call.CustomerCallReturnModel;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.customercall.CustomerCallType;
import com.varanegar.vaslibrary.model.location.LocationModel;
import com.varanegar.vaslibrary.model.newmodel.checkCustomerCredits.CheckCustomerCreditModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.ui.dialog.ConnectionSettingDialog;
import com.varanegar.vaslibrary.ui.fragment.settlement.CustomerPayment;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.apiNew.ApiNew;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import okhttp3.Request;
import retrofit2.Call;
import timber.log.Timber;

/**
 * Created by atp on 5/30/2017.
 * edited by moji
 */

public class ConfirmAction extends CheckPathAction {
    private static final String TAG = "ConfirmAction";
    @Nullable
    @Override
    public UUID getTaskUniqueId() {
        return null;
    }

    public ConfirmAction(MainVaranegarActivity activity, ActionsAdapter adapter, UUID selectedId) {
        super(activity, adapter, selectedId);
        icon = R.drawable.ic_check_box_black_24dp;
        setAnimation(true);
    }

    private boolean isConfirmed() {
        return getCallManager().isConfirmed(Linq.remove(getCalls(),
                it -> it.CallType == CustomerCallType.SendData));
    }

    @Override
    public String getName() {
        if (isConfirmed())
            return getActivity().getString(R.string.un_confirm_operation);
        else
            return getActivity().getString(R.string.confirm_operation);
    }

    @Override
    public String isEnabled() {
        String error = super.isEnabled();
        if (error != null)
            return error;

        if (getCalls().size() == 0)
            return getActivity().getString(R.string.customer_status_is_uknown);

        boolean isDataSent = getCallManager().isDataSent(getCalls(), true);
        if (isDataSent)
            return getActivity().getString(R.string.customer_operation_is_sent_already);

        List<Action> actions = getAdapter().getActions();
        for (Action action :
                actions) {
            error = action.isForce();
            if (error != null && !action.getIsDone() && !(action instanceof ConfirmAction)) {
                return error;
            }
        }

        if (getCustomer().IgnoreLocation == 0) {
            SysConfigModel sysConfigModel = getCloudConfig(ConfigKey.SetCustomerLocation);
            if ((SysConfigManager.compare(sysConfigModel, true)
                    && getCustomer().Latitude == 0
                    && getCustomer().Longitude == 0
                    && !getCallManager().isLackOfVisit(getCalls())) ||
                    (getCustomer().Latitude == 0 &&
                            getCustomer().Longitude == 0 &&
                            getCustomer().IsNewCustomer &&
                            VaranegarApplication.is(VaranegarApplication.AppId.Contractor)))
                return getActivity().getString(R.string.please_set_customer_location);
        }

        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            if (isLackOfVisit() || isCompleteLackOfDelivery() || isCompleteReturnDelivery())
                return null;
            List<CustomerCallInvoiceModel> invoices =
                    ((VasActionsAdapter) getAdapter())
                            .getInvoices();
            for (CustomerCallInvoiceModel invoice : invoices) {
                if (invoice.UniqueId != null &&
                        !getCallManager().hasDistCall(getCalls(), invoice.UniqueId))
                    return getActivity().getString(R.string.customer_has_undeciced_order);
            }
            return null;
        } else {
            if (isLackOfVisit() || isLackOfOrder() || hasOrder())
                return null;
            else
                return getActivity().getString(R.string.customer_status_is_uknown);
        }
    }

    private boolean hasOrder() {
        return getCallManager().hasOrderOrReturnCall(getCalls());
    }

    private boolean hasDelivery() {
        return getCallManager().hasDeliveryCall(getCalls(), null, null);
    }

    @SubsystemType(id = SubsystemTypeId.Dist)
    private boolean isCompleteLackOfDelivery() {
        return getCallManager().isCompleteLackOfDelivery(getCalls());
    }

    @SubsystemType(id = SubsystemTypeId.Dist)
    private boolean isCompleteReturnDelivery() {
        return getCallManager().isCompleteReturnDelivery(getCalls());
    }

    private boolean isLackOfOrder() {
        return getCallManager().isLackOfOrder(getCalls());
    }

    private boolean isLackOfVisit() {
        return getCallManager().isLackOfVisit(getCalls());
    }

    @Nullable
    @Override
    protected String isMandatory() {
        if (!isConfirmed())
            return getActivity().getString(R.string.you_should_save_order_or_cancel_it);
        else
            return null;
    }

    @Override
    public boolean isDone() {
        return isConfirmed();
    }

    @Override
    public void run() {
        setRunning(true);




        if (!isConfirmed()) {


            List<CustomerCallOrderPreviewModel> callOrderPreviewModels =new
                    CustomerCallOrderPreviewManager(getActivity()).getCustomerCallOrders(getSelectedId());

            if (callOrderPreviewModels != null &&
                    callOrderPreviewModels.size() > 0 &&
                    callOrderPreviewModels.get(0).TotalPrice!=null) {
                CustomerModel customerModel=new CustomerManager(getActivity()).getItem(getSelectedId());
                List<String> customerCode= Collections.singletonList(customerModel.CustomerCode);
              //  List<String> customerCode= Collections.singletonList("0014032092");
                ApiNew apiNew =new ApiNew(getActivity());
                Call<List<CheckCustomerCreditModel>> call= apiNew.CheckCustomerCredits(customerCode);
                apiNew.runWebRequest(call, new WebCallBack<List<CheckCustomerCreditModel>>() {
                    @Override
                    protected void onFinish() {

                    }

                    @Override
                    protected void onSuccess(List<CheckCustomerCreditModel> result, Request request) {

                        if (result.size() > 0) {
                            Currency total = Currency.ZERO;
                            if (callOrderPreviewModels.get(0).TotalPrice != null) {
                                total = total.add(callOrderPreviewModels.get(0).TotalPrice);
                                final Currency finalTotal = total;
                                int totalePrice = HelperMethods.currencyToInt(finalTotal);
                                int customerCreditLimit = HelperMethods
                                        .currencyToInt(result.get(0).CustomerCreditLimit);
                               if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales)){
                                    if (totalePrice > customerCreditLimit) {
                                        showErrorMessage(R.string.customerCredit);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    protected void onApiFailure(ApiError error, Request request) {
                        Timber.e(String.valueOf(error));
                        String err = WebApiErrorBody.log(error, getActivity());
                        showErrorMessage(err);
                    }

                    @Override
                    protected void onNetworkFailure(Throwable t, Request request) {
                        Timber.e(t);
                        showErrorMessage(R.string.network_error);
                        setRunning(false);
                        return;
                    }
                });

            }

            if (SysConfigManager.hasTracking(getActivity()) &&
                    TrackingLicense.isValid(getActivity())) {
                android.location.LocationManager manager =
                        (android.location.LocationManager) getActivity()
                                .getSystemService(Context.LOCATION_SERVICE);
                boolean gps = manager
                        .isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
                if (!gps) {
                    TrackingLogManager.addLog(
                            getActivity(),
                            LogType.PROVIDER,
                            LogLevel.Error, "خاموش بودن جی پی اس در زمان تایید عملیات!");
                    showErrorMessage(R.string.please_turn_on_location);
                    setRunning(false);
                    return;
                }
                if (android.os.Build.VERSION.SDK_INT >=
                        android.os.Build.VERSION_CODES.M) {
                    int locationPermission =
                            getActivity()
                                    .checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
                    if (locationPermission != PackageManager.PERMISSION_GRANTED) {
                        TrackingLogManager.addLog(
                                getActivity(),
                                LogType.LOCATION_SETTINGS,
                                LogLevel.Error,
                                "مجوز دسترسی به موقعیت در زمان تایید عملبات وجود ندارد!");
                        showErrorMessage(R.string.coearse_location_permission_denied);
                        setRunning(false);
                        return;
                    }
                }
                new LocationManager(getActivity())
                        .getLocation(new LocationManager.OnLocationUpdated() {
                            @Override
                            public void onSucceeded(LocationModel location) {
                                confirm();
                            }

                            @Override
                            public void onFailed(String error) {
                                TrackingLogManager.addLog(
                                        getActivity(),
                                        LogType.POINT,
                                        LogLevel.Error,
                                        " در زمان تایید عملیات پوینت دریافت نشد!",
                                        error);
                                setRunning(false);
                                showErrorMessage(error);
                            }
                        });
            } else confirm();
        } else {
            SysConfigModel cancelRegistration =
                    getCloudConfig(ConfigKey.CancelRegistration);
            if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
                if (!hasDelivery() ||
                        SysConfigManager.compare(cancelRegistration, true) ||
                        getPrintCounts() == 0) {
                    cancelConfirm();
                } else {
                    setRunning(false);
                    showErrorMessage(R.string.canceling_invoice_is_not_possible);
                }
            } else {
                cancelConfirm();
            }
        }
    }

    private void confirm() {
        if (getCustomer().IgnoreLocation == 0) {
            SysConfigModel sysConfigModel =
                    getCloudConfig(ConfigKey.SetCustomerLocation);
            if (SysConfigManager.compare(sysConfigModel, true) &&
                    getCustomer().Latitude == 0 &&
                    getCustomer().Longitude == 0 &&
                    !getCallManager().isLackOfVisit(getCalls())) {
                showErrorMessage(R.string.please_set_customer_location);
                setRunning(false);
                return;
            }
        }
        if (!isLackOfVisit()) {
            CustomerCallOrderManager callOrderManager =
                    new CustomerCallOrderManager(getActivity());
            List<CustomerCallOrderModel> callOrderModels =
                    callOrderManager.getCustomerCallOrders(getSelectedId());
            List<CustomerCallOrderModel> confirmedCallOrders =
                    Linq.findAll(callOrderModels, customerCallOrderModel ->
                            customerCallOrderModel.UniqueId != null && Linq.exists(getCalls(),
                                    customerCallModel ->
                                            customerCallOrderModel.UniqueId.toString()
                                                    .equals(customerCallModel.ExtraField1)));


            boolean paymentTypeIsEmpty = false;
            for (CustomerCallOrderModel customerCallOrderModel : callOrderModels)
                if (customerCallOrderModel.OrderPaymentTypeUniqueId == null) {
                    paymentTypeIsEmpty = true;
                    break;
                }

            if (paymentTypeIsEmpty) {
                showErrorMessage(R.string.payment_types_can_not_be_empty);
                setRunning(false);
                return;
            }


            if (callOrderModels.size() > 0 &&
                    confirmedCallOrders.size() != callOrderModels.size()) {
                showErrorMessage(R.string.there_is_an_unconfirmed_order);
                setRunning(false);
                return;
            }


            CustomerCallReturnManager callReturnManager =
                    new CustomerCallReturnManager(getActivity());
            List<CustomerCallReturnModel> callReturnModels =
                    callReturnManager.getReturnCalls(
                            getSelectedId(),
                            null,
                            null);
            boolean withRef = Linq.exists(getCalls(),
                    item -> item.CallType == CustomerCallType.SaveReturnRequestWithRef);
            boolean withoutRef = Linq.exists(getCalls(),
                    item -> item.CallType == CustomerCallType.SaveReturnRequestWithoutRef);
            boolean returnError = false;
            for (CustomerCallReturnModel callReturnModel : callReturnModels) {
                if (callReturnModel.BackOfficeInvoiceId == null && !withoutRef)
                    returnError = true;
                else if (callReturnModel.BackOfficeInvoiceId != null && !withRef)
                    returnError = true;
            }
            if (returnError) {
                showErrorMessage(R.string.there_is_an_unconfirmed_return_request);
                setRunning(false);
                return;
            }

            String pictureError =
                    new PictureCustomerViewManager(
                            getActivity())
                            .checkMandatoryPicture(getSelectedId(), getCalls());
            if (pictureError != null && !isLackOfVisit()) {
                showErrorMessage(pictureError);
                setRunning(false);
                return;
            }
        }

        if (Connectivity.isConnected(getActivity())) {
            new Thread(() -> {
                TourManager tourManager = new TourManager(getActivity());
                tourManager.populateAndSendFiles(getSelectedId(), new TourManager.TourCallBack() {
                    @Override
                    public void onSuccess() {
                        Timber.d("Pictures sent for customer id = " + getSelectedId());
                    }

                    @Override
                    public void onFailure(String error) {
                        Timber.e("Sending pictures failed for customer id = " +
                                getSelectedId());
                        Timber.e(error);
                    }

                    @Override
                    public void onProgressChanged(String progress) {
                        Timber.d(progress);
                    }
                });
            }).start();
        }

        if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
            RequestReportViewManager requestReportViewManager =
                    new RequestReportViewManager(getActivity());
            List<RequestReportViewModel> requestReportViewModels =
                    requestReportViewManager
                            .getItems(RequestReportViewManager
                                    .getCurrentCustomer(getCustomer().UniqueId));
            CustomerCallOrderManager customerCallOrderManager =
                    new CustomerCallOrderManager(getActivity());
            final SysConfigModel orderBedLimit = getCloudConfig(ConfigKey.OrderBedLimit);
            final SysConfigModel orderAsnLimit = getCloudConfig(ConfigKey.OrderAsnLimit);
            String errorMessage = customerCallOrderManager
                    .checkCustomerCredits(
                            requestReportViewModels,
                            getCustomer(),
                            orderBedLimit,
                            orderAsnLimit);
            if (errorMessage.isEmpty()) {
                controlReturn();
            } else {
                try {
                    setRunning(false);
                    boolean save =
                            !SysConfigManager.compare(orderBedLimit, "2") &&
                                    !SysConfigManager.compare(orderAsnLimit, "2");
                    BackOfficeType backOfficeType =
                            new SysConfigManager(getActivity()).getBackOfficeType();
                    if (backOfficeType == BackOfficeType.ThirdParty)
                        save = true;
                    CuteMessageDialog alert = new CuteMessageDialog(getActivity());
                    if (save) {
                        alert.setTitle(R.string.alert);
                        alert.setIcon(Icon.Alert);
                    } else {
                        alert.setTitle(R.string.error);
                        alert.setIcon(Icon.Error);
                    }
                    alert.setMessage(errorMessage);
                    if (save)
                        alert.setPositiveButton(R.string.ok, v -> controlReturn());
                    alert.setNegativeButton(R.string.cancel, null);
                    alert.show();
                } catch (UnknownBackOfficeException e) {
                    setRunning(false);
                    showErrorMessage(e.getMessage());
                }
            }
        } else {
            CustomerCallOrderManager customerCallOrderManager =
                    new CustomerCallOrderManager(getActivity());

            if (getCustomer().UniqueId == null) return;
            List<CustomerCallOrderModel> customerCallOrderModels =
                    customerCallOrderManager
                            .getCustomerCallOrders(getCustomer().UniqueId);
            try {
                controlAndSavePayments(customerCallOrderModels);
            } catch (ControlPaymentException e) {
                showErrorMessage(e.getMessage());
                setRunning(false);
            } catch (ThirdPartyControlPaymentChangedException e) {
                showErrorMessage(getActivity()
                        .getString(R.string.control_payments_again_for_usance_day) + "\n" +
                        e.getMessage());
                setRunning(false);
            } catch (Exception e) {
                showErrorMessage(R.string.error_saving_request);
                setRunning(false);
            }
        }
    }

    private void controlAndSavePayments(
            List<CustomerCallOrderModel> customerCallOrderModels) throws ControlPaymentException,
            DbException, ValidationException, ThirdPartyControlPaymentChangedException,
            UnknownBackOfficeException {
        if (!VaranegarApplication.is(VaranegarApplication.AppId.HotSales) ||
                !checkCloudConfig(ConfigKey.ScientificVisit, true)) {
            PaymentManager paymentManager = new PaymentManager(getActivity());
            CustomerPayment customerPayment =
                    paymentManager.calculateCustomerPayment(getSelectedId());
            controlPayments(getCustomer(),
                    customerCallOrderModels, customerPayment);
            if (customerPayment.getTotalAmount(false).compareTo(Currency.ZERO) > 0) {
                CustomerCallManager customerCallManager =
                        new CustomerCallManager(getActivity());
                customerCallManager.savePaymentCall(getSelectedId());
            }
        }
        controlReturn();
    }

    private void cancelConfirm() {
        setRunning(false);
        CuteMessageDialog dialog = new CuteMessageDialog(getActivity());
        dialog.setTitle(R.string.warning);
        dialog.setMessage(R.string.are_you_sure);
        dialog.setIcon(Icon.Warning);
        dialog.setPositiveButton(R.string.yes, v -> {
            final CustomerCallManager callManager =
                    new CustomerCallManager(getActivity());
            try {
                callManager.unConfirmAllCalls(getCustomer().UniqueId);
                final CustomerActionTimeManager customerActionTimeManager =
                        new CustomerActionTimeManager(getActivity());
                customerActionTimeManager.delete(getSelectedId(),
                        CustomerActions.CustomerCallEnd);
                CustomerPrintCountManager customerPrintCountManager =
                        new CustomerPrintCountManager(getActivity());
                SysConfigModel unSubmitCancellationConfig =
                        getCloudConfig(ConfigKey.UnSubmitCancellation);
                if (getPrintCounts() > 0 &&
                        SysConfigManager.compare(unSubmitCancellationConfig, false)) {
                    CancelInvoiceManager cancelInvoiceManager =
                            new CancelInvoiceManager(getActivity());
                    cancelInvoiceManager.addCancelInvoice(getSelectedId());
                    customerPrintCountManager.resetCount(getSelectedId());
                }
                runActionCallBack();
            } catch (Exception ex) {
                showErrorMessage(R.string.un_confirm_failed);
            }
        });
        dialog.setNegativeButton(R.string.no, null);
        dialog.show();
    }

    private void controlReturn() {
        setRunning(true);
        ControlReturnUtility controlReturnUtility =
                new ControlReturnUtility();
        controlReturnUtility.run(getActionData(),
                new IActionUtilityCallBack() {
                    @Override
                    public void onDone() {
                        saveAndSendConfirm();
                    }

                    @Override
                    public void onFailed(String error) {
                        setRunning(false);
                        showErrorMessage(error);
                    }

                    @Override
                    public void onCancel() {
                        setRunning(false);
                    }
                });
    }

    private void saveAndSendConfirm() {
        try {
            if (getCustomer().UniqueId == null) return;
            CustomerActionTimeManager customerActionTimeManager =
                    new CustomerActionTimeManager(getActivity());
            customerActionTimeManager.save(getCustomer().UniqueId,
                    CustomerActions.CustomerCallEnd);
            CustomerCallManager customerCallManager =
                    new CustomerCallManager(getActivity());
            LocationManager locationManager =
                    new LocationManager(getActivity());
            locationManager.createOrderTracking(
                    getCustomer(),
                    new OnSaveLocation() {
                        @Override
                        public void onSaved(LocationModel location) {
                            try {
                                customerCallManager.confirmAll(getSelectedId());
                                if (location != null)
                                    locationManager.tryToSendItem(location);
                                backup();
                                runActionCallBack();
                                if (VaranegarApplication
                                        .is(VaranegarApplication.AppId.PreSales) &&
                                        checkCloudConfig(ConfigKey.AutoSynch, true))
                                    sendCustomerCalls();
                                else if(VaranegarApplication
                                        .is(VaranegarApplication.AppId.Dist) &&
                                        checkCloudConfig(ConfigKey.DistAutoSync, true))
                                    sendCustomerCalls();
                                else
                                    setRunning(false);

                            } catch (Exception ex) {
                                showErrorMessage(R.string.error);
                                setRunning(false);
                            }
                        }

                        @Override
                        public void onFailed(String error) {
                            showErrorMessage(error);
                            setRunning(false);
                        }
                    });
        } catch (Exception ex) {
            showErrorMessage(R.string.error);
            setRunning(false);
        }
    }

    private ProgressDialog progressDialog;

    void showProgressDialog() {
        MainVaranegarActivity activity = getActivity();
        if (activity != null && !activity.isFinishing()) {
            if (progressDialog == null)
                progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle(getActivity()
                    .getString(R.string.please_wait));
            progressDialog.setMessage(getActivity()
                    .getString(R.string.sending_customer_action));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    void dismissProgressDialog() {
        MainVaranegarActivity activity = getActivity();
        if (activity != null && !activity.isFinishing()) {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
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

    // Same of this method is in SendCustomerActionsAction class,
    // Ali jan inaro be zudi yeki mikonam negaran bash !!!
    private void sendCustomerCalls() {
        if (!Connectivity.isConnected(getActivity())) {
            ConnectionSettingDialog connectionSettingDialog =
                    new ConnectionSettingDialog();
            connectionSettingDialog.show(getActivity()
                    .getSupportFragmentManager(), "ConnectionSettingDialog");
            setRunning(false);
            return;
        }

        showProgressDialog();
        TourManager tourManager = new TourManager(getActivity());
        tourManager.verifyData(new TourManager.VerifyCallBack() {
            @Override
            public void onFailure(String error) {
                setRunning(false);
                dismissProgressDialog();
                showErrorMessage(error);
            }

            @Override
            public void onSuccess(final List<UUID> result) {
                if (result != null && result.size() > 0) {
                    dismissProgressDialog();
                    CuteMessageDialog dialog = new CuteMessageDialog(getActivity());
                    dialog.setTitle(R.string.some_customer_data_is_not_saved);
                    CustomerManager customerManager = new CustomerManager(getActivity());
                    List<CustomerModel> customerModels = customerManager.getCustomers(result);
                    StringBuilder msg = new StringBuilder(getActivity()
                            .getString(R.string.do_you_want_to_restore_these_customers));
                    for (CustomerModel customerModel :
                            customerModels) {
                        msg.append(System.getProperty("line.separator"))
                                .append(customerModel.CustomerName);
                    }
                    Timber.d(msg.toString());
                    dialog.setMessage(msg.toString());
                    dialog.setPositiveButton(R.string.yes, view -> {
                        CustomerCallManager customerCallManager =
                                new CustomerCallManager(getActivity());
                        for (UUID customerId :
                                result) {
                            try {
                                customerCallManager
                                        .removeCall(CustomerCallType.SendData, customerId);
                            } catch (DbException e) {
                                Timber.e(e);
                            }
                        }
                    });
                    dialog.setNeutralButton(R.string.no, null);
                    dialog.setIcon(Icon.Error);
                    dialog.show();
                    setRunning(false);
                } else {
                    Thread thread = new Thread(() -> {
                        TourManager tourManager1 = new TourManager(getActivity());
                        tourManager1.populatedAndSendTour(
                                getSelectedId(),
                                new TourManager.TourCallBack() {
                                    @Override
                                    public void onSuccess() {
                                        Timber.i("data tour for customer id = " +
                                                getSelectedId().toString() +
                                                " was sent successfully");
                                        getActivity().runOnUiThread(() -> {
                                            setRunning(false);
                                            progressDialog.dismiss();
                                            CuteMessageDialog dialog =
                                                    new CuteMessageDialog(getActivity());
                                            dialog.setIcon(Icon.Success);
                                            dialog.setMessage(R.string.customer_actions_sent);
                                            dialog.setPositiveButton(R.string.ok, null);
                                            dialog.show();
                                            runActionCallBack();
                                        });
                                    }

                                    @Override
                                    public void onFailure(final String error) {
                                        Timber.e(error);
                                        getActivity().runOnUiThread(() -> {
                                            setRunning(false);
                                            progressDialog.dismiss();
                                            showErrorMessage(error);
                                        });
                                    }

                                    @Override
                                    public void onProgressChanged(String progress) {

                                    }
                                });
                    });
                    thread.start();
                }
            }
        });
    }

    private void controlPayments(CustomerModel customerModel,
                                 List<CustomerCallOrderModel> customerCallOrderModels,
                                 CustomerPayment customerPayment)
            throws ControlPaymentException,
            ThirdPartyControlPaymentChangedException,
            UnknownBackOfficeException {
        BackOfficeType backOfficeType =
                new SysConfigManager(getActivity()).getBackOfficeType();
        PaymentManager paymentManager = new PaymentManager(getActivity());
        if (backOfficeType.equals(BackOfficeType.ThirdParty))
            paymentManager.thirdPartyControlPayments(
                    customerModel,
                    customerCallOrderModels,
                    customerPayment,
                    true);
        else
            paymentManager.controlPayments(
                    customerModel,
                    customerCallOrderModels,
                    customerPayment,
                    getCloudConfigs());
    }

    private void backup() {
        Activity activity = getActivity();
        if (activity != null)
            BackupManager.exportDataAsync(activity, false);
    }
}
