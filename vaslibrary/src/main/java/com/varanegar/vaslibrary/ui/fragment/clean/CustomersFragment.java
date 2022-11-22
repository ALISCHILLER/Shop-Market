package com.varanegar.vaslibrary.ui.fragment.clean;

import static android.content.Context.ACTIVITY_SERVICE;
import static com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey.DeviceWorkingHour;
import static com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey.EndDeviceWorkingHour;
import static com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey.StartDeviceWorkingHour;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarActivity;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.network.Connectivity;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.PairedItemsSpinner;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.component.toolbar.CuteButton;
import com.varanegar.framework.util.filter.Filter;
import com.varanegar.framework.util.fragment.extendedlist.DbListFragment;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.catalogue.CatalogueHelper;
import com.varanegar.vaslibrary.manager.CustomerPathViewManager;
import com.varanegar.vaslibrary.manager.Target.TargetMasterManager;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.customer.CustomerBarcodeManager;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.customercardex.CustomerCardexTempManager;
import com.varanegar.vaslibrary.manager.locationmanager.LogLevel;
import com.varanegar.vaslibrary.manager.locationmanager.LogType;
import com.varanegar.vaslibrary.manager.locationmanager.TrackingLogManager;
import com.varanegar.vaslibrary.manager.oldinvoicemanager.CustomerOldInvoiceDetailTempManager;
import com.varanegar.vaslibrary.manager.oldinvoicemanager.CustomerOldInvoiceHeaderTempManager;
import com.varanegar.vaslibrary.manager.oldinvoicemanager.OldInvoiceManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.OwnerKeysWrapper;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.UnknownBackOfficeException;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.manager.updatemanager.CustomersUpdateFlow;
import com.varanegar.vaslibrary.manager.updatemanager.PriceUpdateFlow;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.manager.visitday.VisitDayViewManager;
import com.varanegar.vaslibrary.model.UpdateKey;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.customercall.CustomerCall;
import com.varanegar.vaslibrary.model.customercall.CustomerCallType;
import com.varanegar.vaslibrary.model.customerpathview.CustomerPathViewModel;
import com.varanegar.vaslibrary.model.customerpathview.CustomerPathViewModelRepository;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.model.target.TargetMasterModel;
import com.varanegar.vaslibrary.model.tour.TourModel;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.model.visitday.VisitDayViewModel;
import com.varanegar.vaslibrary.ui.dialog.ConnectionSettingDialog;
import com.varanegar.vaslibrary.ui.drawer.UserProfileDrawerItem;
import com.varanegar.vaslibrary.ui.drawer.UserProfileFragment;
import com.varanegar.vaslibrary.ui.fragment.AddNewCustomerFragment;
import com.varanegar.vaslibrary.ui.fragment.AddNewCustomerZarFragment;
import com.varanegar.vaslibrary.ui.fragment.TourReportFragment;
import com.varanegar.vaslibrary.ui.fragment.UserLocationFragment;
import com.varanegar.vaslibrary.ui.fragment.new_fragment.helpfragment.Program_Help_fragment;
import com.varanegar.vaslibrary.ui.viewholders.CustomerSummaryMultipanViewHolder;
import com.varanegar.vaslibrary.ui.viewholders.CustomerSummaryViewHolder;
import com.varanegar.vaslibrary.webapi.ping.PingApi;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by atp on 1/14/2017.
 * صفحه لیست مشتریان
 * edited by moji
 * edit by latifi
 */

public abstract class CustomersFragment
        extends DbListFragment<CustomerPathViewModel,
        CustomerPathViewModelRepository,
        CustomersFragment.StatusFilter> {
    private static final String TAG = "CustomersFragment";
    private String barcode;
    private boolean multipan;
    BackOfficeType backOfficeType;

    protected abstract VaranegarFragment getSendTourFragment();

    protected abstract TourReportFragment getProfileFragment();

    protected abstract VaranegarFragment getContentFragment(UUID selectedItem);

    protected abstract VaranegarFragment getContentTargetFragment();

    protected abstract VaranegarFragment getContentTargetDetailFragment();

    //---------------------------------------------------------------------------------------------- onCreate
    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("Customers fragment created");
        Log.d(TAG, "onCreate: called");
        setTitle(getString(R.string.customers_list));
        setAdvancedSearch(0, null);

        if (savedInstanceState != null)
            barcode = savedInstanceState.getString("dd003d32-4f05-423f-b7ba-3ccc9f54fb39");

        if (!isLowMemory())
            wipeOldPriceData();
        try {
            backOfficeType = new SysConfigManager(getContext()).getBackOfficeType();
        } catch (UnknownBackOfficeException e) {
            Timber.e(e);
        }
    }
    //---------------------------------------------------------------------------------------------- onCreate



    //---------------------------------------------------------------------------------------------- onCreateContentFragment
    @Override
    @Nullable
    protected VaranegarFragment onCreateContentFragment(
            UUID selectedItem,
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        SysConfigModel deviceWorkingHour =
                sysConfigManager.read(DeviceWorkingHour, SysConfigManager.cloud);
        if (SysConfigManager.compare(deviceWorkingHour, true)) {
            SysConfigModel startConfig =
                    sysConfigManager.read(StartDeviceWorkingHour, SysConfigManager.cloud);
            SysConfigModel endConfig =
                    sysConfigManager.read(EndDeviceWorkingHour, SysConfigManager.cloud);
            Date startTime = SysConfigManager.getDateFromTime(startConfig, null);
            Date endTime = SysConfigManager.getDateFromTime(endConfig, null);
            if ((startTime != null && new Date().before(startTime)) ||
                    (endTime != null && new Date().after(endTime))) {
                if (getContext() != null) {
                    CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                    dialog.setTitle(R.string.error);
                    dialog.setMessage(R.string.you_are_not_allowed_at_this_time);
                    dialog.setIcon(Icon.Error);
                    dialog.setPositiveButton(R.string.close, null);
                    dialog.show();
                    return null;
                }
            }
        }
        if (!new TourManager(getContext()).isTourAvailable() && getContext() != null) {
            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
            dialog.setTitle(R.string.terrible_failure);
            dialog.setMessage(R.string.tour_is_not_available);
            dialog.setIcon(Icon.Error);
            dialog.setPositiveButton(R.string.close, view -> {
                MainVaranegarActivity activity = getVaranegarActvity();
                if (activity != null)
                    activity.finish();
            });
            dialog.show();
            return null;
        }

        if (getContext() != null) {
            UserModel usermodel = UserManager.readFromFile(getContext());
            if (usermodel != null) {
                SysConfigModel sysConfigModel =
                        sysConfigManager.read(ConfigKey.ViewCustomerTargetReport, SysConfigManager.cloud);
                List<TargetMasterModel> targets =
                        new TargetMasterManager(getContext())
                                .getItems(TargetMasterManager
                                        .getFilterTargets(usermodel.UniqueId, selectedItem));

                if (SysConfigManager.compare(sysConfigModel, true)) {
                    if (targets.size() > 0) {
                        if (targets.size() == 1) {
                            return getContentTargetDetailFragment();
                        } else {
                            return getContentTargetFragment();
                        }
                    } else {
                        return getContentFragment(selectedItem);
                    }
                } else
                    return getContentFragment(selectedItem);
            }
        }
        return null;
    }
    //---------------------------------------------------------------------------------------------- onCreateContentFragment


    //---------------------------------------------------------------------------------------------- onResume
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        refreshCustomerCalls();
        Timber.d("Customers fragment resumed");
        if (!new TourManager(getContext()).isTourAvailable()) {
            Objects.requireNonNull(getVaranegarActvity()).putFragment(getProfileFragment());
            return;
        }
        try {
            CustomerCardexTempManager cardexTempManager =
                    new CustomerCardexTempManager(Objects.requireNonNull(requireContext()));
            cardexTempManager.deleteAll();
            CustomerOldInvoiceHeaderTempManager customerOldInvoiceHeaderTempManager =
                    new CustomerOldInvoiceHeaderTempManager(requireContext());
            customerOldInvoiceHeaderTempManager.deleteAll();
            CustomerOldInvoiceDetailTempManager customerOldInvoiceDetailTempManager =
                    new CustomerOldInvoiceDetailTempManager(requireContext());
            customerOldInvoiceDetailTempManager.deleteAll();
            SysConfigManager sysConfigManager = new SysConfigManager(requireContext());
            sysConfigManager.delete(ConfigKey.ReportsPeriod);
        } catch (DbException e) {
            Timber.e(e);
        }
        refreshGetOldInvoice();
        getAdapter().notifyDataSetChanged();
    }
    //---------------------------------------------------------------------------------------------- onResume


    //---------------------------------------------------------------------------------------------- onDestroy
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getContext() != null) {
            SharedPreferences sharedPreferences = getContext()
                    .getSharedPreferences("valuesStatus", Context.MODE_PRIVATE);
            sharedPreferences.edit().putBoolean("loadFirstCustomerFragment", false).apply();
        }
        Timber.d("Customers fragment destroyed");
        remove("dd003d32-4f05-423f-b7ba-3ccc9f54fb39");
        Runtime.getRuntime().gc();
    }
    //---------------------------------------------------------------------------------------------- onDestroy


    //---------------------------------------------------------------------------------------------- onPause
    @Override
    public void onPause() {
        super.onPause();
        Timber.d("Customers fragment paused");
        Runtime.getRuntime().gc();
    }
    //---------------------------------------------------------------------------------------------- onPause



    //---------------------------------------------------------------------------------------------- onSaveInstanceState
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("loadFirstCustomerFragment", true);
        if (barcode != null && !barcode.isEmpty())
            outState.putString("dd003d32-4f05-423f-b7ba-3ccc9f54fb39", barcode);
    }
    //---------------------------------------------------------------------------------------------- onSaveInstanceState


    //---------------------------------------------------------------------------------------------- onMenuClicked
    @Override
    protected void onMenuClicked() {
        MainVaranegarActivity activity = getVaranegarActvity();
        if (activity != null && !activity.isFinishing())
            activity.toggleDrawer();
    }
    //---------------------------------------------------------------------------------------------- onMenuClicked


    //---------------------------------------------------------------------------------------------- createFilterOptions
    @Override
    protected List<Filter> createFilterOptions() {
        final ArrayList<Filter> list = new ArrayList<>();
        if (getContext() == null) return list;
        TourManager tourManager = new TourManager(getContext());
        TourModel tourModel = tourManager.loadTour();
        if (tourModel == null)
            return new ArrayList<>();

        VisitDayViewManager visitDayManager = new VisitDayViewManager(getContext());
        if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            int visitDayCount = visitDayManager
                    .getCustomersCount(tourModel.DayVisitPathId);
            list.add(
                    new Filter(getContext().getString(R.string.day_customer) +
                            " (" + visitDayCount + ") ",
                            tourModel.DayVisitPathId.toString(), true));
        }

        List<VisitDayViewModel> visitDayModels = visitDayManager.getAll();
        Linq.forEach(visitDayModels,
                item -> {
                    list.add(
                            new Filter(
                                    item.PathTitle +
                                            " (" + item.CustomerCount + ") ",
                                    item.UniqueId != null ? item.UniqueId.toString() : null));
                });
        if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist))
            list.add(new Filter(getContext().getString(R.string.all_customers),
                    CustomerPathViewManager.All_PATHS, true));
        return list;
    }
    //---------------------------------------------------------------------------------------------- createFilterOptions


    //---------------------------------------------------------------------------------------------- onCreateCuteToolbar
    @Override
    protected List<CuteButton> onCreateCuteToolbar() {

        List<CuteButton> buttons = new ArrayList<>();

        CuteButton mapBtn = new CuteButton();
        mapBtn.setTitle(R.string.map);
        mapBtn.setIcon(R.drawable.ic_map_black_36dp);
        mapBtn.setEnabled(() -> new TourManager(getContext()).isTourAvailable());
        mapBtn.setOnClickListener(() -> {
            final MainVaranegarActivity activity = getVaranegarActvity();
            if (activity == null || activity.isFinishing())
                return;
            LocationManager locationManager =
                    (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null &&
                    locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                UserLocationFragment fragment = new UserLocationFragment();
                activity.pushFragment(fragment);
            } else {
                activity.showSnackBar(
                                R.string.please_turn_on_location,
                                MainVaranegarActivity.Duration.Short);}
        });
        if (!VaranegarApplication
                .is(VaranegarApplication.AppId.Contractor))
            buttons.add(mapBtn);


        /*
         *بهینه سازی مسیر
         * حذف شد
         *  Ali Soleymani
         */
/*                CuteButton pathBtn = new CuteButton();
        pathBtn.setTitle(R.string.omptimizing_path);
        pathBtn.setIcon(R.drawable.ic_path_black_36dp);
        pathBtn.setEnabled(() ->
                new TourManager(getContext()).isTourAvailable());
        pathBtn.setOnClickListener(() -> {
            if (getActivity() == null || getActivity().isFinishing()) return;

            Location lastLocation = ((VasActivity) getActivity()).getLastLocation();
            if (lastLocation != null) {
                PathOptimizationDialog dialog = new PathOptimizationDialog();
                dialog.lastLocation = lastLocation;
                dialog.onResponse = new PathOptimizationDialog.IOnResponse() {
                    @Override
                    public void done(List<CustomerModel> customerModels, List<LatLng> points) {
                        if (getActivity() == null || getActivity().isFinishing()) return;

                        if (customerModels.size() > 0) {
                            try {
                                CustomerManager customerManager =
                                        new CustomerManager(getActivity());
                                customerManager.updateOPathIds(customerModels, points);
                                refresh();
                                CuteMessageDialog dialog = new CuteMessageDialog(getActivity());
                                dialog.setIcon(Icon.Success);
                                dialog.setMessage(R.string.customers_list_updated);
                                dialog.setPositiveButton(R.string.ok, null);
                                dialog.show();
                            } catch (Exception e) {
                                CuteMessageDialog dialog = new CuteMessageDialog(getActivity());
                                dialog.setIcon(Icon.Error);
                                dialog.setMessage(R.string.error_saving_request);
                                dialog.setPositiveButton(R.string.ok, null);
                                dialog.show();
                            }
                        } else
                            refresh();
                    }

                    @Override
                    public void failed(String error) {
                        if (getActivity() == null || getActivity().isFinishing()) return;

                        CuteMessageDialog dialog = new CuteMessageDialog(getActivity());
                        dialog.setIcon(Icon.Error);
                        dialog.setMessage(error);
                        dialog.setPositiveButton(R.string.ok, null);
                        dialog.show();
                    }
                };
                dialog.show(
                        getActivity().getSupportFragmentManager(),
                        "CuteAlertDialogOptimisation");
            } else {
                CuteMessageDialog dialog = new CuteMessageDialog(getActivity());
                dialog.setIcon(Icon.Error);
                dialog.setMessage(R.string.location_is_not_available);
                dialog.setPositiveButton(R.string.ok, null);
                dialog.show();
            }
        });
        buttons.add(pathBtn);*/

        CuteButton albumBtn = new CuteButton();
        albumBtn.setTitle(R.string.product_album);
        albumBtn.setIcon(R.drawable.ic_photo_album_black_36dp);
        albumBtn.setEnabled(() -> new TourManager(getContext()).isTourAvailable());
        albumBtn.setOnClickListener(() -> {
            final MainVaranegarActivity activity = getVaranegarActvity();
            if (activity == null || activity.isFinishing())
                return;
            new CatalogueHelper(activity).openCatalogue(null, null);
        });
        if (!VaranegarApplication.is(VaranegarApplication.AppId.Contractor))
            buttons.add(albumBtn);

        final SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        SysConfigModel allowAddNewCustomer = sysConfigManager
                .read(ConfigKey.AllowRegisterNewCustomer, SysConfigManager.cloud);
        // SysConfigManager.compare(allowAddNewCustomer, true) &&
        if (SysConfigManager.compare(allowAddNewCustomer, true)
                && !VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            CuteButton addCustomerBtn = new CuteButton();
            addCustomerBtn.setTitle(R.string.register_new_customer);
            addCustomerBtn.setIcon(R.drawable.ic_person_add_black_36dp);
            addCustomerBtn.setEnabled(() -> new TourManager(getContext()).isTourAvailable());
            addCustomerBtn.setOnClickListener(() -> {
                final MainVaranegarActivity activity = getVaranegarActvity();
                if (activity == null || activity.isFinishing())
                    return;



                android.location.LocationManager manager =
                        (android.location.LocationManager) getActivity()
                                .getSystemService(Context.LOCATION_SERVICE);
                boolean gps = manager
                        .isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
                if (!gps) {
                    TrackingLogManager.addLog(
                            getActivity(),
                            LogType.PROVIDER,
                            LogLevel.Error, "خاموش بودن جی پی اس در زمان ثبت مشتری!");
                    activity.showSnackBar(R.string.please_turn_on_location,
                            MainVaranegarActivity.Duration.Short);
                    return;
                }
                OwnerKeysWrapper ownerKeysWrapper = sysConfigManager.readOwnerKeys();
                if (ownerKeysWrapper.isZarMakaron()) {
                    AddNewCustomerZarFragment fragment = new AddNewCustomerZarFragment();
                    activity.pushFragment(fragment);
                } else {
                    AddNewCustomerFragment fragment = new AddNewCustomerFragment();
                    activity.pushFragment(fragment);
                }


            });
            buttons.add(addCustomerBtn);
        }

        final CuteButton cancelTourBtn = new CuteButton();
        cancelTourBtn.setTitle(R.string.cancel_tour);
        cancelTourBtn.setIcon(R.drawable.ic_cancel_black_36dp);
        cancelTourBtn.setEnabled(() -> new TourManager(getContext()).isTourAvailable());
        cancelTourBtn.setOnClickListener(() -> {
            final MainVaranegarActivity activity = getVaranegarActvity();
            if (activity == null || activity.isFinishing())
                return;
            TourManager tourManager = new TourManager(getContext());
            TourModel tourModel = tourManager.loadTour();
            if (!Connectivity.isConnected(activity) &&
                    tourModel != null &&
                    !tourModel.IsVirtual) {
                ConnectionSettingDialog connectionSettingDialog = new ConnectionSettingDialog();
                connectionSettingDialog
                        .show(
                                activity.getSupportFragmentManager(),
                                "ConnectionSettingDialog");
                return;
            }
            CuteMessageDialog confirmDialog = new CuteMessageDialog(activity);
            confirmDialog.setMessage(R.string.warning);
            confirmDialog.setMessage(
                    R.string.after_cancel_tour_you_do_can_not_change_anything);
            confirmDialog.setIcon(Icon.Alert);
            confirmDialog.setNeutralButton(R.string.no, null);
            confirmDialog.setPositiveButton(R.string.yes, v -> cancelTour(activity));
            confirmDialog.show();
        });
        buttons.add(cancelTourBtn);

        CuteButton sendTourBtn = new CuteButton();
        sendTourBtn.setTitle(R.string.send_tour);
        sendTourBtn.setIcon(R.drawable.ic_cloud_upload_black_36dp);
        sendTourBtn.setEnabled(() -> {
            TourManager tourManager = new TourManager(getContext());

            TourModel tm = tourManager.loadTour();
            return (tourManager.isTourAvailable() &&
                    tm != null && !tm.IsVirtual
            );
        });
        sendTourBtn.setOnClickListener(() -> {
            MainVaranegarActivity activity = getVaranegarActvity();
            if (activity != null && !activity.isFinishing()) {
                activity.putFragment(getSendTourFragment());
            }
        });
        sendTourBtn.setOnLongClickListener(() ->
                new TourManager(getContext()).testTourSyncViewModel());
        buttons.add(sendTourBtn);

        if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
            CuteButton sendOperationBtn = new CuteButton();
            sendOperationBtn.setTitle(R.string.send_operation);
            sendOperationBtn.setIcon(R.drawable.ic_cloud_upload_black_36dp);
            final SysConfigModel allowSendData =
                    sysConfigManager.read(
                            ConfigKey.OnlineSynch, SysConfigManager.cloud);
            sendOperationBtn.setEnabled(() ->
                    (new TourManager(getContext()).isTourAvailable() &&
                            SysConfigManager.compare(allowSendData, true)));
            sendOperationBtn.setOnClickListener(() -> {
                VaranegarActivity varanegarActivity = getVaranegarActvity();
                if (varanegarActivity != null && !varanegarActivity.isFinishing()) {
                    CuteMessageDialog confirmDialog =
                            new CuteMessageDialog(varanegarActivity);
                    confirmDialog.setTitle(R.string.warning);
                    confirmDialog.setMessage(R.string.send_confirmed_operation);
                    confirmDialog.setIcon(Icon.Alert);
                    confirmDialog.setNeutralButton(R.string.cancel, null);
                    confirmDialog.setPositiveButton(R.string.yes, v -> {
                        final MainVaranegarActivity activity = getVaranegarActvity();
                        final ProgressDialog progressDialog = new ProgressDialog(activity);
                        if (activity != null && !activity.isFinishing()) {
                            progressDialog
                                    .setTitle(activity.getString(R.string.please_wait));
                            progressDialog
                                    .setMessage(
                                            activity.getString(
                                                    R.string.sending_customers_action));
                            progressDialog.setCancelable(false);
                            progressDialog.show();

                            if (!Connectivity.isConnected(activity)) {
                                ConnectionSettingDialog connectionSettingDialog =
                                        new ConnectionSettingDialog();
                                connectionSettingDialog.show(
                                        activity.getSupportFragmentManager(),
                                        "ConnectionSettingDialog");
                                dismissProgressDialog(progressDialog);
                                return;
                            }

                            TourManager tourManager = new TourManager(activity);
                            tourManager.verifyData(new TourManager.VerifyCallBack() {
                                @Override
                                public void onFailure(String error) {
                                    activity.showSnackBar(
                                            error,
                                            MainVaranegarActivity.Duration.Short);
                                    dismissProgressDialog(progressDialog);
                                }

                                @Override
                                public void onSuccess(final List<UUID> result) {
                                    if (result != null && result.size() > 0) {
                                        CuteMessageDialog dialog =
                                                new CuteMessageDialog(activity);
                                        dialog.setTitle(
                                                R.string.some_customer_data_is_not_saved);
                                        CustomerManager customerManager =
                                                new CustomerManager(activity);
                                        List<CustomerModel> customerModels =
                                                customerManager.getCustomers(result);
                                        StringBuilder msg = new StringBuilder(activity
                                                .getString(
                                                        R.string.do_you_want_to_restore_these_customers));
                                        for (CustomerModel customerModel : customerModels) {
                                            msg.append(System.getProperty("line.separator"))
                                                    .append(customerModel.CustomerName);
                                        }
                                        Timber.d(msg.toString());
                                        dismissProgressDialog(progressDialog);
                                        dialog.setMessage(msg.toString());
                                        dialog.setPositiveButton(R.string.yes, view -> {
                                            CustomerCallManager customerCallManager =
                                                    new CustomerCallManager(activity);
                                            for (UUID customerId :
                                                    result) {
                                                try {
                                                    customerCallManager
                                                            .removeCall(
                                                                    CustomerCallType.SendData,
                                                                    customerId);
                                                } catch (DbException e) {
                                                    Timber.e(e);
                                                }
                                            }
                                        });
                                        dialog.setNeutralButton(R.string.no, null);
                                        dialog.setIcon(Icon.Error);
                                        dialog.show();
                                    } else {
                                        TourManager tourManager = new TourManager(activity);
                                        List<CustomerModel> customerModels =
                                                new CustomerManager(getContext())
                                                        .getCustomersWithCustomerCalls();
                                        tourManager.populatedAndSendTour(customerModels, new TourManager.TourCallBack() {
                                            @SuppressLint("NotifyDataSetChanged")
                                            @Override
                                            public void onSuccess() {
                                                if (getContext() == null) return;
                                                Timber.i("Sending tour data for customers " +
                                                        "who have confirmed operation finished.");
                                                dismissProgressDialog(progressDialog);
                                                refreshCustomerCalls();

                                                getAdapter().notifyDataSetChanged();
                                                MainVaranegarActivity activity =
                                                        getVaranegarActvity();
                                                if (activity != null && !activity.isFinishing()) {
                                                    CuteMessageDialog cuteMessageDialog =
                                                            new CuteMessageDialog(getContext());
                                                    cuteMessageDialog.setIcon(Icon.Success);
                                                    cuteMessageDialog.setMessage(R.string
                                                            .customers_data_sent_successfully);
                                                    cuteMessageDialog.setPositiveButton(
                                                            R.string.ok, null);
                                                    cuteMessageDialog.show();
                                                }
                                            }

                                            @Override
                                            public void onFailure(String error) {
                                                Timber.e(error);
                                                dismissProgressDialog(progressDialog);
                                                MainVaranegarActivity activity =
                                                        getVaranegarActvity();
                                                if (activity != null && !activity.isFinishing()) {
                                                    CuteMessageDialog dialog =
                                                            new CuteMessageDialog(activity);
                                                    dialog.setIcon(Icon.Error);
                                                    dialog.setTitle(R.string.error);
                                                    dialog.setMessage(error);
                                                    dialog.setPositiveButton(
                                                            R.string.ok,
                                                            null);
                                                    dialog.show();
                                                }
                                            }

                                            @Override
                                            public void onProgressChanged(String progress) {

                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });
                    confirmDialog.show();
                }
            });
            buttons.add(sendOperationBtn);
        }

        CuteButton updateCustomersBtn = new CuteButton();
        updateCustomersBtn.setIcon(R.drawable.ic_customers_black_36dp);
        updateCustomersBtn.setTitle(R.string.update_day_customers);
        updateCustomersBtn.setEnabled(() ->
                new TourManager(getContext()).isTourAvailable());
        updateCustomersBtn.setOnClickListener(() -> {
            final MainVaranegarActivity activity = getVaranegarActvity();
            if (activity == null || activity.isFinishing())
                return;
            if (!Connectivity.isConnected(activity)) {
                ConnectionSettingDialog connectionSettingDialog =
                        new ConnectionSettingDialog();
                connectionSettingDialog.show(
                        activity.getSupportFragmentManager(),
                        "ConnectionSettingDialog");
                return;
            }
            startProgress(R.string.please_wait, R.string.updating_day_customer);
            PingApi pingApi = new PingApi();
            pingApi.refreshBaseServerUrl(activity, new PingApi.PingCallback() {
                @Override
                public void done(String ipAddress) {
                    CustomersUpdateFlow flow = new CustomersUpdateFlow(activity, null);
                    flow.syncCustomersAndInitPromotionDb(new UpdateCall() {
                        @Override
                        protected void onSuccess() {
                            if (getContext() == null) return;
                            finishProgress();
                            MainVaranegarActivity activity = getVaranegarActvity();
                            if (activity != null && !activity.isFinishing()) {
                                CuteMessageDialog cuteMessageDialog =
                                        new CuteMessageDialog(getContext());
                                cuteMessageDialog.setIcon(Icon.Success);
                                cuteMessageDialog.setTitle(R.string.update_done);
                                cuteMessageDialog.setMessage(R.string.updated_day_customers);
                                cuteMessageDialog.setPositiveButton(R.string.ok, view -> refresh());
                                cuteMessageDialog.show();
                            }
                        }

                        @Override
                        protected void onFailure(String error) {
                            finishProgress();
                            MainVaranegarActivity activity = getVaranegarActvity();
                            if (activity != null && !activity.isFinishing()) {
                                CuteMessageDialog dialog = new CuteMessageDialog(activity);
                                dialog.setMessage(error);
                                dialog.setTitle(R.string.error);
                                dialog.setIcon(Icon.Error);
                                dialog.setPositiveButton(R.string.ok, null);
                                dialog.show();
                            }
                        }
                    });
                }

                @Override
                public void failed() {
                    finishProgress();
                    MainVaranegarActivity activity = getVaranegarActvity();
                    if (activity != null && !activity.isFinishing()) {
                        CuteMessageDialog dialog = new CuteMessageDialog(activity);
                        dialog.setMessage(R.string.network_error);
                        dialog.setTitle(R.string.error);
                        dialog.setIcon(Icon.Error);
                        dialog.setPositiveButton(R.string.ok, null);
                        dialog.show();
                    }
                }
            });
        });
        buttons.add(updateCustomersBtn);

       CuteButton oldInvoiceBtn = new CuteButton();
        oldInvoiceBtn.setTitle(R.string.update_customers_history);
        oldInvoiceBtn.setIcon(R.drawable.ic_old_invoice_white_36dp);
        oldInvoiceBtn.setEnabled(() -> new TourManager(getContext()).isTourAvailable());
        oldInvoiceBtn.setOnClickListener(() -> updateCustomerOldInvoices(
                false,
                false));

        buttons.add(oldInvoiceBtn);

        CuteButton program_help = new CuteButton();
        program_help.setIcon(R.drawable.ic_help_black_24dp);
        program_help.setEnabled(() -> new TourManager(getContext()).isTourAvailable());
        program_help.setTitle(R.string.program_help);
        program_help.setOnClickListener(() -> {
            Program_Help_fragment program_Help_fragment=new Program_Help_fragment();
            final MainVaranegarActivity activity = getVaranegarActvity();
            Bundle bundle = new Bundle();
            bundle.putInt("item_Expandable", 1);
            program_Help_fragment.setArguments(bundle);
            if (activity == null || activity.isFinishing())
                return;
                activity.pushFragment(program_Help_fragment);
        });
        buttons.add(program_help);



/*        CuteButton openVpn = new CuteButton();
        openVpn.setTitle(R.string.vpn_open);
        openVpn.setIcon(R.drawable.ic_baseline_vpn_lock_24);
        openVpn.setEnabled(() -> new TourManager(getContext()).isTourAvailable());
        openVpn.setOnClickListener(() -> {
            VpnDialogFragment vpnDialogFragment = new VpnDialogFragment();
            vpnDialogFragment.show(getChildFragmentManager(), "SettingDialogFragment");
        });
        buttons.add(openVpn);*/

        addProfileToCuteToolbar();

/*        if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
            addReportToCutePresaleToolbar();
            addUpdateToCutePresaleToolbar();
        }*/

        return buttons;
    }
    //---------------------------------------------------------------------------------------------- onCreateCuteToolbar



    //---------------------------------------------------------------------------------------------- addProfileToCuteToolbar
    public void addProfileToCuteToolbar() {
        final MainVaranegarActivity activity = getVaranegarActvity();
        if (activity == null)
            return;
        getButtonsToolbar().getLinearLayoutToolbarProfile().addView(new UserProfileDrawerItem(getContext()) {
            @Override
            protected void onClick() {
                activity.pushFragment(new UserProfileFragment());
            }
        });
    }
    //---------------------------------------------------------------------------------------------- addProfileToCuteToolbar


    //---------------------------------------------------------------------------------------------- createFiltersQuery
    @NonNull
    @Override
    protected Query createFiltersQuery(
            @Nullable String text,
            @Nullable List<Filter> filters,
            @Nullable Object spinnerFilterItem) {
        ArrayList<CustomerCallType> customerCallType = null;
        boolean unknownStatus = false;
        if (spinnerFilterItem != null) {
            StatusFilter statusFilter = (StatusFilter) spinnerFilterItem;
            customerCallType = statusFilter.customerCallType;
            unknownStatus = statusFilter.unknownStatus;
        }
        text = HelperMethods
                .convertToEnglishNumbers(HelperMethods.persian2Arabic(text));
        List<Filter> selectedPaths = new ArrayList<>();
        if (filters != null)
            selectedPaths = Linq.findAll(filters, item -> item.selected);
        if (selectedPaths.size() == 1 &&
                selectedPaths.get(0).khafan &&
                selectedPaths.get(0).value.equals(CustomerPathViewManager.All_PATHS)) {
            if (text != null && !text.isEmpty()) {
                if (unknownStatus)
                    return CustomerPathViewManager
                            .getAllWithUnknownStatus(
                                    text,
                                    setCheckConfirmStatus(spinnerFilterItem));
                else
                    return CustomerPathViewManager
                            .getAll(
                                    text,
                                    customerCallType,
                                    setCheckConfirmStatus(spinnerFilterItem));
            } else {
                if (unknownStatus)
                    return CustomerPathViewManager
                            .getAllWithUnknownStatus(
                                    setCheckConfirmStatus(spinnerFilterItem));
                else
                    return CustomerPathViewManager
                            .getAll(
                                    customerCallType,
                                    setCheckConfirmStatus(spinnerFilterItem));
            }
        } else {
            if (text != null && !text.isEmpty()) {
                if (unknownStatus)
                    return CustomerPathViewManager
                            .getAllWithUnknownVisitStatus(
                                    selectedPaths,
                                    text,
                                    setCheckConfirmStatus(spinnerFilterItem));
                else
                    return CustomerPathViewManager
                            .getAll(
                                    selectedPaths,
                                    text,
                                    customerCallType,
                                    setCheckConfirmStatus(spinnerFilterItem));
            } else {
                if (unknownStatus)
                    return CustomerPathViewManager
                            .getAllWithUnknownVisitStatus(
                                    selectedPaths,
                                    setCheckConfirmStatus(spinnerFilterItem));
                else
                    return CustomerPathViewManager
                            .getAll(
                                    selectedPaths,
                                    customerCallType,
                                    setCheckConfirmStatus(spinnerFilterItem));
            }
        }
    }
    //---------------------------------------------------------------------------------------------- createFiltersQuery


    //---------------------------------------------------------------------------------------------- getRepository
    @NonNull
    @Override
    protected CustomerPathViewModelRepository getRepository() {
        return new CustomerPathViewModelRepository();
    }
    //---------------------------------------------------------------------------------------------- getRepository


    /**
     * لیست مشتریان Adapter
     * row_coustomer_multipan layout
     */
    //---------------------------------------------------------------------------------------------- createListItemViewHolder
    @Override
    public BaseViewHolder<CustomerPathViewModel> createListItemViewHolder(
            ViewGroup parent,
            int viewType) {
        if (multipan) {
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.row_customer_multipan, parent, false);
            return new CustomerSummaryMultipanViewHolder(
                    view,
                    getAdapter(),
                    backOfficeType);
        } else {
            View view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.row_customer, parent, false);
            return new CustomerSummaryViewHolder(
                    view,
                    getAdapter(),
                    backOfficeType);
        }
    }
    //---------------------------------------------------------------------------------------------- createListItemViewHolder


    //---------------------------------------------------------------------------------------------- onActivityCreated
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        multipan = getResources().getBoolean(R.bool.multipane);
    }
    //---------------------------------------------------------------------------------------------- onActivityCreated


    //---------------------------------------------------------------------------------------------- refreshCustomerCalls
    protected void refreshCustomerCalls() {
        CustomerSummaryMultipanViewHolder.calls =
                new CustomerCallManager(requireContext())
                        .getItems(new Query().from(CustomerCall.CustomerCallTbl));
    }
    //---------------------------------------------------------------------------------------------- refreshCustomerCalls


    /**
     * بارکد سرچ اسکن کد بارکد و برگشت کد بارکد  در کلاس
     * ExtendedListFragment و چک در onResume() ExtendedListFragment
     */
    //---------------------------------------------------------------------------------------------- onClickCustomerBarcode
    private void onClickCustomerBarcode(View v) {
        if (getActivity() == null) return;
        CustomerBarcodeManager customerBarcodeManager =
                new CustomerBarcodeManager(getActivity());
        customerBarcodeManager.readBarcode();
    }
    //---------------------------------------------------------------------------------------------- onClickCustomerBarcode


    //---------------------------------------------------------------------------------------------- cancelTour
    private void cancelTour(Context context) {
        startProgress(R.string.please_wait, R.string.canceling_tour);
        TourManager tourManager = new TourManager(getContext());
        TourModel tourModel = tourManager.loadTour();
        if (tourModel != null && tourModel.IsVirtual) {
            tourManager.cancelVirtualTour();
            finishProgress();
            MainVaranegarActivity activity = getVaranegarActvity();
            if (activity != null && !activity.isFinishing() && activity.isVisible()) {
                TourReportFragment fragment = getProfileFragment();
                activity.putFragment(fragment);
            }
        } else {
            PingApi pingApi = new PingApi();
            pingApi.refreshBaseServerUrl(context, new PingApi.PingCallback() {
                @Override
                public void done(String ipAddress) {
                    MainVaranegarActivity activity = getVaranegarActvity();
                    if (activity != null && !activity.isFinishing()) {
                        tourManager.cancelTour(new TourManager.TourCallBack() {
                            @Override
                            public void onSuccess() {
                                finishProgress();
                                MainVaranegarActivity activity = getVaranegarActvity();
                                if (activity != null &&
                                        !activity.isFinishing() &&
                                        activity.isVisible()) {
                                    TourReportFragment fragment = getProfileFragment();
                                    activity.putFragment(fragment);
                                }
                            }

                            @Override
                            public void onFailure(String error) {
                                finishProgress();
                                MainVaranegarActivity activity = getVaranegarActvity();
                                if (activity != null && !activity.isFinishing()) {
                                    CuteMessageDialog dialog = new CuteMessageDialog(activity);
                                    dialog.setCancelable(false);
                                    dialog.setMessage(error);
                                    dialog.setTitle(R.string.error);
                                    dialog.setIcon(Icon.Error);
                                    dialog.setPositiveButton(R.string.ok,
                                            view -> refreshGetOldInvoice());
                                    dialog.show();
                                }
                            }

                            @Override
                            public void onProgressChanged(String progress) {

                            }
                        });
                    }
                }

                @Override
                public void failed() {
                    finishProgress();
                    MainVaranegarActivity activity = getVaranegarActvity();
                    if (activity != null && !activity.isFinishing()) {
                        CuteMessageDialog dialog = new CuteMessageDialog(activity);
                        dialog.setCancelable(false);
                        dialog.setMessage(R.string.error_connecting_to_server);
                        dialog.setTitle(R.string.error);
                        dialog.setIcon(Icon.Error);
                        dialog.setPositiveButton(R.string.ok, view -> refreshGetOldInvoice());
                        dialog.show();
                    }
                }
            });
        }
    }
    //---------------------------------------------------------------------------------------------- cancelTour


    //---------------------------------------------------------------------------------------------- wipeOldPriceData
    private void wipeOldPriceData() {
        new Thread() {
            @Override
            public void run() {
                Context context = getContext();
                if (context != null) {
                    SharedPreferences sharedPreferences =
                            context.getSharedPreferences(
                                    "DATA_RECOVERY", Context.MODE_PRIVATE);
                    long lastClearDate =
                            sharedPreferences.getLong("Last_Clear_Date", 0);
                    if (lastClearDate == 0 ||
                            ((new Date().getTime() - lastClearDate) / 1000) > 3600 * 24)
                        PriceUpdateFlow.clearAdditionalData(context);
                }
            }
        }.start();
    }
    //---------------------------------------------------------------------------------------------- wipeOldPriceData


    //---------------------------------------------------------------------------------------------- refreshGetOldInvoice
    private void refreshGetOldInvoice() {
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        SysConfigModel sysConfigModel =
                sysConfigManager.read(
                        ConfigKey.IgnoreOldInvoice, SysConfigManager.local);
        SysConfigModel firstTimeSysConfigModel =
                sysConfigManager.read(
                        ConfigKey.FirstTimeAfterGetTour, SysConfigManager.local);
        if (!SysConfigManager.compare(sysConfigModel, true) ||
                SysConfigManager.compare(firstTimeSysConfigModel, true)) {
            SysConfigModel DownloadOldInvoicePolicySysConfigModel =
                    sysConfigManager
                            .read(ConfigKey.DownloadOldInvoicePolicy, SysConfigManager.local);
            boolean onlyDiscount = SysConfigManager.compare(firstTimeSysConfigModel, true) &&
                    !SysConfigManager.compare(DownloadOldInvoicePolicySysConfigModel, true);
            getOldInvoices(firstTimeSysConfigModel, onlyDiscount, sysConfigManager);
        }
    }
    //---------------------------------------------------------------------------------------------- refreshGetOldInvoice


    //---------------------------------------------------------------------------------------------- getOldInvoices
    private void getOldInvoices(SysConfigModel firstTime,
                                boolean onlyDiscount,
                                final SysConfigManager sysConfigManager) {
        if (SysConfigManager.compare(firstTime, true)) {
            updateCustomerOldInvoices(true, onlyDiscount);
        } else {
            UpdateManager updateManager = new UpdateManager(getContext());
            Date oldInvoiceDate = updateManager.getLog(UpdateKey.CustomerOldInvoice);
            Date tourStartDate = updateManager.getLog(UpdateKey.TourStartTime);
            if (tourStartDate.after(oldInvoiceDate)) {
                final CuteMessageDialog dialog =
                        new CuteMessageDialog(requireContext());
                dialog.setIcon(Icon.Alert);
                dialog.setMessage(R.string.do_you_update_old_invoices);
                dialog.setPositiveButton(R.string.yes, view ->
                        updateCustomerOldInvoices(false, false));
                dialog.setNegativeButton(R.string.no, view -> {
                    try {
                        sysConfigManager.save(
                                ConfigKey.IgnoreOldInvoice,
                                "True",
                                SysConfigManager.local);
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                });
                dialog.show();
            }
        }
    }
    //---------------------------------------------------------------------------------------------- getOldInvoices


    //---------------------------------------------------------------------------------------------- isLowMemory
    private Boolean isLowMemory() {
        try {
            ActivityManager activityManager =
                    (ActivityManager) requireContext()
                            .getSystemService(ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);
            return memoryInfo.lowMemory;
        } catch (Error e) {
            return true;
        }
    }
    //---------------------------------------------------------------------------------------------- isLowMemory


    //---------------------------------------------------------------------------------------------- updateCustomerOldInvoices
    private void updateCustomerOldInvoices(final boolean firstTime, final boolean onlyDiscount) {
        final MainVaranegarActivity activity = getVaranegarActvity();
        if (activity == null || activity.isFinishing())
            return;
        if (!firstTime) {
            if (!Connectivity.isConnected(activity)) {
                ConnectionSettingDialog connectionSettingDialog =
                        new ConnectionSettingDialog();
                connectionSettingDialog.show(
                        activity.getSupportFragmentManager(),
                        "ConnectionSettingDialog");
                return;
            }
        }
        startProgress(R.string.please_wait, R.string.updating_customer_history);
        PingApi pingApi = new PingApi();
        pingApi.refreshBaseServerUrl(activity, new PingApi.PingCallback() {
            @Override
            public void done(String ipAddress) {
                final OldInvoiceManager oldInvoiceManager = new OldInvoiceManager(activity);
                oldInvoiceManager.sync(firstTime, onlyDiscount,
                        null, null, null, new UpdateCall() {
                            @Override
                            protected void onFinish() {
                                finishProgress();
                            }

                            @Override
                            protected void onSuccess() {
                                saveFirstTimeAfterGetTour(activity, true);
                                MainVaranegarActivity activity = getVaranegarActvity();
                                if (activity != null && !activity.isFinishing())
                                    activity.showSnackBar(R.string.customer_history_updated,
                                            MainVaranegarActivity.Duration.Short);
                            }

                            @Override
                            protected void onFailure(String error) {
                                saveFirstTimeAfterGetTour(activity, true);
                                MainVaranegarActivity activity = getVaranegarActvity();
                                if (activity != null && !activity.isFinishing()) {
                                    CuteMessageDialog dialog = new CuteMessageDialog(activity);
                                    dialog.setMessage(error);
                                    dialog.setTitle(error);
                                    dialog.setIcon(Icon.Error);
                                    dialog.setPositiveButton(R.string.ok, null);
                                    dialog.show();
                                }
                            }

                            @Override
                            protected void onError(String error) {
                                saveFirstTimeAfterGetTour(activity, false);
                                handleDiscountError(error, onlyDiscount);
                            }
                        });
            }

            @Override
            public void failed() {
                finishProgress();
                if (firstTime) {
                    saveFirstTimeAfterGetTour(activity, false);
                    String error = activity.getResources().getString(R.string.network_error);
                    handleDiscountError(error, onlyDiscount);
                } else {
                    MainVaranegarActivity activity = getVaranegarActvity();
                    if (activity != null && !activity.isFinishing() && isResumed()) {
                        CuteMessageDialog dialog = new CuteMessageDialog(activity);
                        dialog.setMessage(R.string.network_error);
                        dialog.setTitle(R.string.error);
                        dialog.setIcon(Icon.Error);
                        dialog.setPositiveButton(R.string.ok, null);
                        dialog.show();
                    }
                }
            }
        });
    }
    //---------------------------------------------------------------------------------------------- updateCustomerOldInvoices


    //---------------------------------------------------------------------------------------------- setupAdvancedFilter
    protected boolean setupAdvancedFilter(PairedItemsSpinner<StatusFilter> spinner) {
        if (getContext() == null) return false;
        StatusFilter filter0 = new StatusFilter();
        filter0.name = getContext().getString(R.string.all_customers);

        StatusFilter filter1 = new StatusFilter();
        filter1.name = getContext().getString(R.string.has_order);
        filter1.customerCallType = new ArrayList<>();
        filter1.customerCallType.add(CustomerCallType.SaveOrderRequest);

        StatusFilter filter2 = new StatusFilter();
        filter2.name = getContext().getString(R.string.lack_of_order);
        filter2.customerCallType = new ArrayList<>();
        filter2.customerCallType.add(CustomerCallType.LackOfOrder);

        StatusFilter filter3 = new StatusFilter();
        filter3.name = getContext().getString(R.string.lack_of_visit);
        filter3.customerCallType = new ArrayList<>();
        filter3.customerCallType.add(CustomerCallType.LackOfVisit);

        StatusFilter filter4 = new StatusFilter();
        filter4.name = getContext().getString(R.string.return_without_reference);
        filter4.customerCallType = new ArrayList<>();
        filter4.customerCallType.add(CustomerCallType.SaveReturnRequestWithoutRef);

        StatusFilter filter5 = new StatusFilter();
        filter5.name = getContext().getString(R.string.return_with_reference);
        filter5.customerCallType = new ArrayList<>();
        filter5.customerCallType.add(CustomerCallType.SaveReturnRequestWithRef);

        StatusFilter filter6 = new StatusFilter();
        filter6.name = getContext().getString(R.string.unKnown);
        filter6.customerCallType = new ArrayList<>();
        filter6.unknownStatus = true;

        StatusFilter filter7 = new StatusFilter();
        filter7.name = getContext().getString(R.string.sent);
        filter7.customerCallType = new ArrayList<>();
        filter7.customerCallType.add(CustomerCallType.SendData);

        StatusFilter filter8 = new StatusFilter();
        filter8.name = getContext().getString(R.string.lack_of_delivery);
        filter8.customerCallType = new ArrayList<>();
        filter8.customerCallType.add(CustomerCallType.CompleteLackOfDelivery);
        filter8.customerCallType.add(CustomerCallType.OrderLackOfDelivery);

        StatusFilter filter9 = new StatusFilter();
        filter9.name = getContext().getString(R.string.complete_return);
        filter9.customerCallType = new ArrayList<>();
        filter9.customerCallType.add(CustomerCallType.CompleteReturnDelivery);
        filter9.customerCallType.add(CustomerCallType.OrderReturn);

        StatusFilter filter10 = new StatusFilter();
        filter10.name = getContext().getString(R.string.incomplete_delivery_call_status);
        filter10.customerCallType = new ArrayList<>();
        filter10.customerCallType.add(CustomerCallType.OrderPartiallyDelivered);

        StatusFilter filter11 = new StatusFilter();
        filter11.name = getContext().getString(R.string.delivered);
        filter11.customerCallType = new ArrayList<>();
        filter11.customerCallType.add(CustomerCallType.OrderDelivered);

        StatusFilter filter12 = new StatusFilter();
        filter12.customerCallType = new ArrayList<>();
        filter12.name = getContext().getString(R.string.incomplete_operation);
        filter12.customerCallType.add(CustomerCallType.IncompleteOperation);

        List<StatusFilter> filterList = new ArrayList<>();
        filterList.add(filter0);
        if (!VaranegarApplication.is(VaranegarApplication.AppId.Contractor)) {
            filterList.add(filter3);
            filterList.add(filter12);
        }
        filterList.add(filter6);

        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            filterList.add(filter8);
            filterList.add(filter9);
            filterList.add(filter10);
            filterList.add(filter11);
        } else {
            filterList.add(filter1);
            if (!VaranegarApplication.is(VaranegarApplication.AppId.Contractor)) {
                filterList.add(filter2);
                filterList.add(filter4);
                filterList.add(filter5);
                filterList.add(filter7);
            }
        }

        spinner.setup(getChildFragmentManager(), filterList, null);
        return true;
    }
    //---------------------------------------------------------------------------------------------- setupAdvancedFilter


    //---------------------------------------------------------------------------------------------- dismissProgressDialog
    private void dismissProgressDialog(ProgressDialog progressDialog) {
        if (progressDialog != null &&
                progressDialog.isShowing() &&
                isResumed())
            try {
                progressDialog.dismiss();
            } catch (Exception ex) {
                Timber.d(ex);
            }
    }
    //---------------------------------------------------------------------------------------------- dismissProgressDialog


    //---------------------------------------------------------------------------------------------- handleDiscountError
    private void handleDiscountError(String errorMessage, final boolean onlyDiscount) {
        final MainVaranegarActivity activity = getVaranegarActvity();
        if (activity != null && !activity.isFinishing()) {
            CuteMessageDialog dialog = new CuteMessageDialog(activity);
            dialog.setCancelable(false);
            dialog.setMessage(errorMessage + "\n" +
                    getString(R.string.error_in_getting_discount));
            dialog.setTitle(R.string.error);
            dialog.setIcon(Icon.Error);
            dialog.setPositiveButton(R.string.retry,
                    view -> updateCustomerOldInvoices(true, onlyDiscount));
            dialog.setNegativeButton(R.string.cancel_tour,
                    view -> cancelTour(activity));
            dialog.show();
        }
    }
    //---------------------------------------------------------------------------------------------- handleDiscountError


    //---------------------------------------------------------------------------------------------- saveFirstTimeAfterGetTour
    private void saveFirstTimeAfterGetTour(Context context, boolean getDiscount) {
        try {
            SysConfigManager sysConfigManager = new SysConfigManager(context);
            if (getDiscount)
                sysConfigManager.save(
                        ConfigKey.FirstTimeAfterGetTour,
                        "False",
                        SysConfigManager.local);
            else
                sysConfigManager.save(
                        ConfigKey.FirstTimeAfterGetTour,
                        "True",
                        SysConfigManager.local);
        } catch (Exception e) {
            Timber.e(e);
            e.printStackTrace();
        }
    }
    //---------------------------------------------------------------------------------------------- saveFirstTimeAfterGetTour


    //---------------------------------------------------------------------------------------------- setCheckConfirmStatus
    public static Boolean setCheckConfirmStatus(@Nullable Object spinnerFilterItem) {
        if (VaranegarApplication.is(VaranegarApplication.AppId.Contractor))
            return null;
        else
            return !(spinnerFilterItem != null &&
                    ((StatusFilter) spinnerFilterItem).customerCallType != null &&
                    ((StatusFilter) spinnerFilterItem).customerCallType.contains(
                            CustomerCallType.IncompleteOperation));
    }
    //---------------------------------------------------------------------------------------------- setCheckConfirmStatus


    //---------------------------------------------------------------------------------------------- StatusFilter
    static class StatusFilter {
        public boolean unknownStatus = false;
        public ArrayList<CustomerCallType> customerCallType;
        public String name;

        @NonNull
        @Override
        public String toString() {
            return name;
        }
    }
    //---------------------------------------------------------------------------------------------- StatusFilter

}
