package com.varanegar.vaslibrary.ui.fragment;

import static com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey.CheckCustomerStock;
import static varanegar.com.discountcalculatorlib.utility.JalaliCalendar.jalaliToGregorian;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarActivity;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.PairedItems;
import com.varanegar.framework.util.component.ProgressView;
import com.varanegar.framework.util.component.SimpleToolbar;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.util.fragment.extendedlist.Action;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.action.BaseAction;
import com.varanegar.vaslibrary.action.BaseReturnAction;
import com.varanegar.vaslibrary.action.CameraAction;
import com.varanegar.vaslibrary.action.CustomerInventoryReportAction;
import com.varanegar.vaslibrary.action.SendOperationAction;
import com.varanegar.vaslibrary.action.VpnAction;
import com.varanegar.vaslibrary.action.confirm.ConfirmAction;
import com.varanegar.vaslibrary.action.CustomerInventoryAction;
import com.varanegar.vaslibrary.action.CustomerQuestionnaireAction;
import com.varanegar.vaslibrary.action.DeleteAction;
import com.varanegar.vaslibrary.action.DistReturnAction;
import com.varanegar.vaslibrary.action.EditCustomerAction;
import com.varanegar.vaslibrary.action.InvoiceAction;
import com.varanegar.vaslibrary.action.NonDeliveryAction;
import com.varanegar.vaslibrary.action.NonOrderAction;
import com.varanegar.vaslibrary.action.NonVisitAction;
import com.varanegar.vaslibrary.action.PaymentAction;
import com.varanegar.vaslibrary.action.PrintAction;
import com.varanegar.vaslibrary.action.SaveOrderAction;
import com.varanegar.vaslibrary.action.SetCustomerLocationAction;
import com.varanegar.vaslibrary.action.VasActionsAdapter;
import com.varanegar.vaslibrary.manager.CustomerPathViewManager;
import com.varanegar.vaslibrary.manager.customer.CustomerActivityManager;
import com.varanegar.vaslibrary.manager.customer.CustomerCategoryManager;
import com.varanegar.vaslibrary.manager.customer.CustomerLevelManager;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.customeractiontimemanager.CustomerActionTimeManager;
import com.varanegar.vaslibrary.manager.customeractiontimemanager.CustomerActions;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallOrderPreviewManager;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.customercallmanager.TaskPriorityManager;
import com.varanegar.vaslibrary.manager.oldinvoicemanager.CustomerOldInvoiceHeaderManager;
import com.varanegar.vaslibrary.manager.productorderviewmanager.ProductOrderViewManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.UnknownBackOfficeException;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.call.CustomerCallOrderPreviewModel;
import com.varanegar.vaslibrary.model.customer.CustomerActivityModel;
import com.varanegar.vaslibrary.model.customer.CustomerCategoryModel;
import com.varanegar.vaslibrary.model.customer.CustomerLevelModel;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.customercall.CustomerCallModel;
import com.varanegar.vaslibrary.model.customercall.TaskPriorityModel;
import com.varanegar.vaslibrary.model.customeroldInvoice.CustomerOldInvoiceHeaderModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.model.tour.TourModel;
import com.varanegar.vaslibrary.ui.dialog.EditCustomerZarFragmentDialog;
import com.varanegar.vaslibrary.ui.dialog.InsertPinDialog;
import com.varanegar.vaslibrary.ui.drawer.CustomerReportsDrawerAdapter;
import com.varanegar.vaslibrary.ui.fragment.new_fragment.edit_new_zar.Edit_New_Customer_ZarFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;
import varanegar.com.discountcalculatorlib.utility.JalaliCalendar;

/**
 * Created by atp on 5/2/2017.
 * صفحه  مشتری
 * updated by moji
 */

public class CustomersContentFragment extends VaranegarFragment {
    CustomerModel customer;
    private VasActionsAdapter actionsAdapter;
    private SimpleToolbar simpleToolbar;
    private MapView mapView;
    private PairedItems code_naghsh_paired_item;

    protected VasActionsAdapter getActionsAdapter() {
        return actionsAdapter;
    }

    private boolean isLoading;
    private RecyclerView actionsRecyclerView;

    public UUID getSelectedId() {
        try {
            if (getArguments() == null)
                return null;

            return UUID.fromString(getArguments()
                    .getString("a129ef75-77ce-432a-8982-6bcab0bf7b51"));

        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("a129ef75-77ce-432a-8982-6bcab0bf7b51", getSelectedId().toString());
    }

    @Override
    public void onPause() {
        super.onPause();
        loading(true);
        if (mapView != null)
            mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getContext() == null) return;
        if (getVaranegarActvity() == null) return;
        if (getView() == null) return;
        PairedItems customerTime = getView().findViewById(R.id.customer_time_paired_item);

        new Handler().postDelayed(() -> {
            try {
                if (actionsAdapter != null && isResumed() && !isRemoving()) {
                    List<Action> actions = new ArrayList<>();
                    addActions(actions);
                    sortActions(actions);
                    actionsAdapter.setActions(actions, () -> {
                        if (isResumed()) {
                            actionsRecyclerView.setAdapter(actionsAdapter);
                            loading(false);
                        }
                    });
                } else {
                    loading(false);
                }

                boolean isConfirmed = new CustomerCallManager(getContext()).isConfirmed(getCalls());
                if (!isConfirmed) {
                    CustomerActionTimeManager customerActionTimeManager =
                            new CustomerActionTimeManager(getContext());
                    Date startTime = customerActionTimeManager.get(
                            getSelectedId(),
                            CustomerActions.CustomerCallStart);
                    if (startTime == null)
                        customerActionTimeManager.save(
                                getSelectedId(),
                                CustomerActions.CustomerCallStart);
                    CustomerActionTimeManager.startVisitTimer(
                            getVaranegarActvity(),
                            getSelectedId(),
                            (timeOffset, timeOffsetStr) -> {
                                if (isVisible()) {
                                    VaranegarActivity activity = getVaranegarActvity();
                                    if (activity != null)
                                        activity.runOnUiThread(() -> {
                                            if (isVisible()) {

                                                customerTime.setValue(timeOffsetStr);
                                            }
                                        });
                                }
                            });
                } else {
                    long time = CustomerActionTimeManager.getCustomerCallTime(
                            getContext(),
                            getSelectedId());
                    if (isVisible())
                        customerTime.setValue(DateHelper.getTimeSpanString(time));
                }
            } catch (Exception ex) {
                Timber.e(ex);
            }
        }, 300);
        new Handler().postDelayed(this::updateCustomer, 600);
    }

    private List<CustomerCallModel> getCalls() {
        return getActionsAdapter().getCalls();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mapView != null)
            mapView.onStop();
        Runtime.getRuntime().gc();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CustomerActionTimeManager.stopVisitTimer();
        if (mapView != null)
            mapView.onDestroy();
        Runtime.getRuntime().gc();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null)
            mapView.onLowMemory();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setDrawerAdapter(new CustomerReportsDrawerAdapter(getVaranegarActvity(), getSelectedId()));
    }

    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getContext() == null || getVaranegarActvity() == null) return;

        new Handler().postDelayed(() -> {
            if (isResumed() && !isRemoving()) {
                mapView = view.findViewById(R.id.map_view);
                mapView.onCreate(savedInstanceState);
                mapView.onResume(); // needed to getUnits the map to display immediately
                mapView.getMapAsync(googleMap -> {
                    FloatingActionButton googleMapFab = view.findViewById(R.id.google_map_fab);
                    FloatingActionButton wazeFab = view.findViewById(R.id.waze_fab);
                    if (customer.Longitude != 0 && customer.Latitude != 0) {
                        view.findViewById(R.id.location_text_view).setVisibility(View.GONE);
                        LatLng latLng = new LatLng(customer.Latitude, customer.Longitude);
                        MarkerOptions options = new MarkerOptions().position(latLng);
                        options.flat(false);
                        Marker marker = googleMap.addMarker(options);
                        if (marker != null) {
                            marker.setZIndex(10);
                            CustomerCallManager callManager = new CustomerCallManager(getContext());
                            int icon = callManager.getIcon(getCalls());
                            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(icon);
                            marker.setTitle(customer.CustomerName);
                            marker.setIcon(bitmap);
                            marker.setTag(customer.UniqueId == null
                                    ? ""
                                    : customer.UniqueId.toString());
                        }

                        CameraPosition cameraPosition = new CameraPosition
                                .Builder()
                                .target(latLng)
                                .zoom(17)
                                .build();
                        googleMap.animateCamera(CameraUpdateFactory
                                .newCameraPosition(cameraPosition));
                        googleMap.setOnMarkerClickListener(marker1 -> {
                            UserLocationFragment userLocationFragment = new UserLocationFragment();
                            userLocationFragment.setCustomerId(getSelectedId());

                            getVaranegarActvity().pushFragment(userLocationFragment);
                            return true;
                        });
                        googleMapFab.setOnClickListener(v -> {
                            try {
                                String map = "http://maps.google.com/maps?daddr=" +
                                        customer.Latitude + "," +
                                        customer.Longitude;
                                Intent intent = new Intent(Intent.ACTION_VIEW,
                                        Uri.parse(map));
                                getContext().startActivity(intent);
                            } catch (Exception ignored) {

                            }

                        });
                        wazeFab.setOnClickListener(v -> {
                            try {
                                String waze = "waze://?ll=" + customer.Latitude + ", " +
                                        customer.Longitude + "&navigate=yes";
                                Intent intent = new Intent(Intent.ACTION_VIEW,
                                        Uri.parse(waze));
                                getContext().startActivity(intent);
                            } catch (Exception ignored) {

                            }

                        });
                    } else {
                        googleMapFab.setVisibility(View.GONE);
                        wazeFab.setVisibility(View.GONE);
                        view.findViewById(R.id.location_text_view)
                                .setVisibility(View.VISIBLE);
                    }
                });
            }
        }, 1000);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup viewGroup,
                             Bundle savedInstanceState) {
        final Context context = getContext();
        final Activity activity = getActivity();
        final MainVaranegarActivity mainVarActivity = getVaranegarActvity();
        if (context == null || activity == null || mainVarActivity == null) return null;

        actionsAdapter = new VasActionsAdapter(mainVarActivity, getSelectedId());
        customer = new CustomerManager(context).getItem(getSelectedId());
        if (customer == null) {
            Timber.wtf("customer id not found : ", getSelectedId().toString());
            throw new NullPointerException("customer id not found : " + getSelectedId().toString());
        }
        View view = inflater.inflate(R.layout.layout_customer_detail, viewGroup, false);

        /*
          برای نمایش پیام ویزیتور به موزع استفاده از Arguments
           در کلاس ExtendedListFragment در متد setOnItemClickListener
         */
        if (getArguments() != null) {
            String message = getArguments()
                    .getString("is from customer list", "false");
            if (customer.Description != null && !customer.Description.isEmpty() &&
                    message.equals("true")) {
                getArguments().putString("is from customer list", "false");
                CuteMessageDialog dialog = new CuteMessageDialog(activity);
                dialog.setTitle(getString(R.string.description));
                dialog.setMessage(customer.Description);
                dialog.setIcon(Icon.Info);
                dialog.setPositiveButton(R.string.ok, null);
                dialog.show();
            }
        }

        SysConfigManager sysConfigManager = new SysConfigManager(context);
        Button refreshButton = view.findViewById(R.id.refresh_customer_actions);
        SysConfigModel checkDistanceConfig = sysConfigManager.read(
                ConfigKey.CheckDistance,
                SysConfigManager.cloud);
        if (SysConfigManager.compare(checkDistanceConfig, true)) {
            refreshButton.setOnClickListener(view1 -> updateItem());
        } else {
            refreshButton.setVisibility(View.GONE);
        }
        BackOfficeType backOfficeType;
        try {
            backOfficeType = sysConfigManager.getBackOfficeType();
            if (backOfficeType == BackOfficeType.Rastak) {
                ((PairedItems) view.findViewById(R.id.customer_category_paired_item))
                        .setTitle(getString(R.string.customer_group));
                ((PairedItems) view.findViewById(R.id.customer_level_paired_item))
                        .setTitle(getString(R.string.sub_group_1));
                ((PairedItems) view.findViewById(R.id.customer_activity_paired_item))
                        .setTitle(getString(R.string.sub_group_2));
            }
        } catch (UnknownBackOfficeException e) {
            e.printStackTrace();
        }

        simpleToolbar = view.findViewById(R.id.toolbar);
        simpleToolbar.setOnBackClickListener(view12 -> onBackPressed());
        simpleToolbar.setOnMenuClickListener(view13 -> onMenuClicked());

        addOnItemUpdateListener(() -> {
            CustomerCallManager customerCallManager = new CustomerCallManager(context);
            boolean isConfirmed = customerCallManager.isConfirmed(
                    customerCallManager.loadCalls(getSelectedId()));
            if (isConfirmed)
                CustomerActionTimeManager.stopVisitTimer();
            else {
                CustomerActionTimeManager.startVisitTimer(
                        mainVarActivity,
                        getSelectedId(),
                        (timeOffset, timeOffsetStr) -> {
                            if (isVisible()) {
                                mainVarActivity.runOnUiThread(() -> {
                                    if (isVisible() && getView() != null)
                                        ((PairedItems) getView()
                                                .findViewById(R.id.customer_time_paired_item))
                                                .setValue(timeOffsetStr);
                                });
                            }
                        });
            }
        });

        actionsRecyclerView = view.findViewById(R.id.actions_recycler_view);
        actionsRecyclerView.setLayoutManager(new LinearLayoutManager(
                activity,
                LinearLayoutManager.VERTICAL,
                false));

        final ImageView actionsExpandImageView = view.findViewById(R.id.actions_expand_image_view);
        if (actionsAdapter.isCollapsed())
            actionsExpandImageView
                    .setImageResource(com.varanegar.framework.R.drawable.ic_expand_white_24dp);
        else
            actionsExpandImageView
                    .setImageResource(com.varanegar.framework.R.drawable.ic_collapse_white_24dp);
        actionsExpandImageView.setOnClickListener(view14 -> {
            if (actionsAdapter.isCollapsed()) {
                actionsAdapter.expand();
                actionsExpandImageView
                        .setImageResource(com.varanegar.framework.R.drawable.ic_collapse_white_24dp);
            } else {
                actionsAdapter.collapse();
                actionsExpandImageView
                        .setImageResource(com.varanegar.framework.R.drawable.ic_expand_white_24dp);
            }
        });

        return view;
    }

    protected void onMenuClicked() {
        if (getVaranegarActvity() != null)
            getVaranegarActvity().toggleDrawer();
    }

    public void updateItem() {
        loading(true);
        actionsAdapter.refresh(() -> {
            if (isResumed()) {
                for (OnItemUpdateListener listener : onItemUpdateListeners) {
                    listener.run();
                }
                loading(false);
            }
        });
    }

    private void loading(boolean isLoading) {
        this.isLoading = isLoading;
        ProgressView progressView = ((ProgressView) getView());
        if (progressView != null) {
            if (isLoading)
                progressView.start();
            else
                progressView.finish();
        }
    }

    protected void updateCustomer() {
        if (isResumed() && !isRemoving()) {
            final View view = getView();
            if (getContext() == null) return;
            if (view == null) return;
            ExecutorService pool = Executors.newSingleThreadExecutor();
            pool.execute(() -> {
                if (customer.UniqueId == null) {
                    Timber.e("Customer UniqueId is null.");
                } else {
                    customer = new CustomerPathViewManager(getContext()).getItem(customer.UniqueId);

                    final CustomerCallManager callManager = new CustomerCallManager(getContext());
                    ProductOrderViewManager manager = new ProductOrderViewManager(getContext());
                    final Boolean spd = manager.getSPD(getSelectedId());
                    CustomerCallOrderPreviewManager previewManager =
                            new CustomerCallOrderPreviewManager(getContext());
                    assert customer.UniqueId != null;
                    final List<CustomerCallOrderPreviewModel> orders =
                            previewManager.getCustomerCallOrders(customer.UniqueId);
                    Currency total = Currency.ZERO;

                    for (CustomerCallOrderPreviewModel order :
                            orders) {
                        if (order.TotalPrice != null)
                            total = total.add(order.TotalPrice);
                    }

                    final Currency finalTotal = total;
                    MainVaranegarActivity activity = getVaranegarActvity();
                    if (activity != null && !activity.isFinishing()) {
                        activity.runOnUiThread(() -> {
                            if (isResumed() && isAdded()) {
                                simpleToolbar.setTitle(customer.CustomerName);
                                ((PairedItems) view
                                        .findViewById(R.id.customer_name_paired_item))
                                        .setValue(customer.CustomerName);
                                ((PairedItems) view
                                        .findViewById(R.id.customer_code_paired_item))
                                        .setValue(customer.CustomerCode);

                                if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
                                    code_naghsh_paired_item = view.
                                            findViewById(R.id.code_naghsh_paired_item);
                                    code_naghsh_paired_item.setVisibility(View.VISIBLE);
                                    if (customer.CodeNaghsh != null && !customer.CodeNaghsh.isEmpty()) {
                                        code_naghsh_paired_item.setValue(customer.CodeNaghsh);
                                    } else {
                                        code_naghsh_paired_item.setBackgroundColor(getContext()
                                                .getResources().getColor(R.color.red));
                                        SharedPreferences sharedPreferences = getContext()
                                                .getSharedPreferences("preferred_local", Context.MODE_PRIVATE);
                                        String un3 = sharedPreferences.getString(customer.UniqueId.toString()
                                                , "");
                                        sharedPreferences.edit()
                                                .putString(customer.UniqueId.toString(), "").apply();
                                        if (un3 != null && un3.isEmpty() && un3.equals("")) {
                                            //showEditDialog();
                                        } else {
                                            // onBackPressed();
                                        }
                                    }
                                }
                                ((PairedItems) view
                                        .findViewById(R.id.customer_address_paired_item))
                                        .setValue(customer.Address);
                                ((PairedItems) view
                                        .findViewById(R.id.customer_status_paired_item))
                                        .setValue(callManager.getName(
                                                getCalls(),
                                                VaranegarApplication.is(
                                                        VaranegarApplication.AppId.Contractor)));
                                ((PairedItems) view
                                        .findViewById(R.id.customer_total_order_paired_item))
                                        .setValue("");
                                ((PairedItems) view
                                        .findViewById(R.id.customer_national_code_paired_item))
                                        .setValue(customer.NationalCode);
                                ((PairedItems) view
                                        .findViewById(R.id.customer_mobile_paired_item))
                                        .setValue(customer.Mobile);
                                ((PairedItems) view
                                        .findViewById(R.id.customer_tel_paired_item))
                                        .setValue(customer.Phone);
                                ((PairedItems) view
                                        .findViewById(R.id.customer_store_name_paired_item))
                                        .setValue(customer.StoreName);

                                if (spd == null)
                                    ((PairedItems) view
                                            .findViewById(R.id.spd_paired_item))
                                            .setValue("-");
                                else if (spd)
                                    ((PairedItems) view
                                            .findViewById(R.id.spd_paired_item))
                                            .setValue(getString(R.string.check_sign));
                                else
                                    ((PairedItems) view
                                            .findViewById(R.id.spd_paired_item))
                                            .setValue(getString(R.string.multiplication_sign));

                                if (customer.CustomerActivityId != null) {
                                    CustomerActivityManager customerActivityManager =
                                            new CustomerActivityManager(getContext());
                                    final CustomerActivityModel customerActivity =
                                            customerActivityManager
                                                    .getItem(customer.CustomerActivityId);
                                    if (customerActivity != null)
                                        ((PairedItems) view
                                                .findViewById(R.id.customer_activity_paired_item))
                                                .setValue(customerActivity.CustomerActivityName);
                                }
                                if (customer.CustomerLevelId != null) {
                                    CustomerLevelManager customerLevelManager =
                                            new CustomerLevelManager(getContext());
                                    final CustomerLevelModel customerLevel =
                                            customerLevelManager.getItem(customer.CustomerLevelId);
                                    if (customerLevel != null)
                                        ((PairedItems) view
                                                .findViewById(R.id.customer_level_paired_item))
                                                .setValue(customerLevel.CustomerLevelName);
                                }
                                if (customer.CustomerCategoryId != null) {
                                    CustomerCategoryManager customerCategoryManager =
                                            new CustomerCategoryManager(getContext());
                                    final CustomerCategoryModel customerCategory =
                                            customerCategoryManager
                                                    .getItem(customer.CustomerCategoryId);
                                    if (customerCategory != null)
                                        ((PairedItems) view
                                                .findViewById(R.id.customer_category_paired_item))
                                                .setValue(customerCategory.CustomerCategoryName);
                                }
                                CustomerOldInvoiceHeaderManager customerOldInvoiceHeaderManager =
                                        new CustomerOldInvoiceHeaderManager(getContext());
                                final CustomerOldInvoiceHeaderModel customerOldInvoiceHeader =
                                        customerOldInvoiceHeaderManager
                                                .getItem(CustomerOldInvoiceHeaderManager
                                                        .getCustomerInvoiceDesc(customer.UniqueId));
                                if (customerOldInvoiceHeader != null) {
                                    ((PairedItems) view
                                            .findViewById(
                                                    R.id.customer_last_invoice_date_paired_item))
                                            .setValue(customerOldInvoiceHeader.SalePDate);
                                    JalaliCalendar.YearMonthDate date =
                                            jalaliToGregorian(new JalaliCalendar
                                                    .YearMonthDate(Integer.parseInt(
                                                    customerOldInvoiceHeader
                                                            .SalePDate.substring(0, 4)),
                                                    Integer.parseInt(
                                                            customerOldInvoiceHeader
                                                                    .SalePDate.substring(5, 7)) - 1,
                                                    Integer.parseInt(
                                                            customerOldInvoiceHeader
                                                                    .SalePDate.substring(8, 10))));
                                    final String DATE_FORMAT = "d/M/yyyy";
                                    SimpleDateFormat dateFormat =
                                            new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);

                                    try {
                                        Date startDate = dateFormat.parse(
                                                date.getDate() + "/" +
                                                        date.getMonth() + "/" +
                                                        date.getYear());
                                        if (startDate == null)
                                            throw new ParseException("startDate is null", 0);
                                        long numberOfDays = TimeUnit.DAYS
                                                .convert(new Date().getTime() -
                                                                startDate.getTime(),
                                                        TimeUnit.MILLISECONDS);
                                        ((PairedItems) view
                                                .findViewById(
                                                        R.id.customer_last_invoice_days_paired_item))
                                                .setValue(numberOfDays + " روز");
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                                ((PairedItems) view
                                        .findViewById(R.id.customer_total_order_paired_item))
                                        .setValue(HelperMethods.currencyToString(finalTotal));
                                if (customer.CustomerMessage != null &&
                                        !customer.CustomerMessage.isEmpty())
                                    view
                                            .findViewById(R.id.other_information_btn)
                                            .setOnClickListener(v -> {
                                                CustomerOtherInfoDialog dialog =
                                                        new CustomerOtherInfoDialog();
                                                dialog.CustomerMessage = customer.CustomerMessage;
                                                dialog.show(getChildFragmentManager(),
                                                        "CustomerOtherInfoDialog");
                                            });
                                else
                                    view.findViewById(R.id.other_information_btn)
                                            .setVisibility(View.GONE);
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (getContext() == null || getVaranegarActvity() == null) return;
        if (!this.isLoading) {
            List<Action> actions = actionsAdapter.getActions();
            for (Action action :
                    actions) {
                String error = action.isForce();
                if (error != null && !action.getIsDone()) {
                    CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                    dialog.setMessage(error);
                    dialog.setTitle(R.string.error);
                    dialog.setIcon(Icon.Error);
                    dialog.setPositiveButton(R.string.ok, null);
                    dialog.show();
                    return;
                }

            }
            UpdateManager updateManager = new UpdateManager(getContext());
            updateManager.removeBarcode();
            CustomerCallManager customerCallManager = new CustomerCallManager(getContext());
            boolean isConfirmed = customerCallManager.isConfirmed(getCalls());
            if (!isConfirmed) {
                CustomerActionTimeManager.clearVisitTimer(getContext(), getSelectedId());
            }
            super.onBackPressed();
        } else {
            getVaranegarActvity().showSnackBar(R.string.please_wait,
                    MainVaranegarActivity.Duration.Short);
        }
    }

    @CallSuper
    protected void addActions(@NonNull List<Action> actions) {
        //  دکمه ویرایش مشتری در صحه نمایش مشتری درpresale
        if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
            EditCustomerAction editCustomerAction =
                    new EditCustomerAction(getVaranegarActvity(), actionsAdapter, getSelectedId());
            editCustomerAction.setActionCallBack(() -> {
                updateCustomer();
                updateItem();
            });
            actions.add(editCustomerAction);
        }
        actions.add(new SetCustomerLocationAction(
                getVaranegarActvity(),
                getActionsAdapter(),
                getSelectedId()));

        /**
         * دکمه ثبت سفارش و تحوبل سفارش
         */
        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist))
            actions.add(new InvoiceAction(
                    getVaranegarActvity(),
                    getActionsAdapter(),
                    getSelectedId()));
        else
            actions.add(new SaveOrderAction(
                    getVaranegarActvity(),
                    getActionsAdapter(),
                    getSelectedId()));

        if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            NonVisitAction nonVisitAction =
                    new NonVisitAction(
                            getVaranegarActvity(),
                            getActionsAdapter(),
                            getSelectedId());
            nonVisitAction.setActionCallBack(() -> {
                updateCustomer();
                updateItem();
            });
            actions.add(nonVisitAction);
        }

        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            NonDeliveryAction nonDeliveryAction = new NonDeliveryAction(
                    getVaranegarActvity(), getActionsAdapter(), getSelectedId());
            nonDeliveryAction.setActionCallBack(() -> {
                updateCustomer();
                updateItem();
            });
            actions.add(nonDeliveryAction);

            DistReturnAction returnAction = new DistReturnAction(
                    getVaranegarActvity(), getActionsAdapter(), getSelectedId());
            returnAction.setActionCallBack(() -> {
                updateCustomer();
                updateItem();
            });
            actions.add(returnAction);

        } else {
            NonOrderAction nonOrderAction = new NonOrderAction(
                    getVaranegarActvity(),
                    getActionsAdapter(),
                    getSelectedId());
            nonOrderAction.setActionCallBack(() -> {
                updateCustomer();
                updateItem();
            });
            actions.add(nonOrderAction);
        }
        if (!VaranegarApplication.is(VaranegarApplication.AppId.PreSales))
            actions.add(new PaymentAction(
                    getVaranegarActvity(),
                    getActionsAdapter(),
                    getSelectedId()));

        PrintAction printAction = new PrintAction(
                getVaranegarActvity(),
                actionsAdapter,
                getSelectedId());
        printAction.setActionCallBack(this::updateItem);
        actions.add(printAction);


        if (!getActionsAdapter().getCloudConfigs().compare(ConfigKey.SimplePresale, true)) {
            actions.add(new CameraAction(
                    getVaranegarActvity(),
                    getActionsAdapter(),
                    getSelectedId()));

            SysConfigManager sysConfigManager = new SysConfigManager(getContext());
            SysConfigModel sysConfigModel = sysConfigManager
                    .read(CheckCustomerStock, SysConfigManager.cloud);

            if (SysConfigManager.compare(sysConfigModel, true)) {
                actions.add(new CustomerInventoryAction(
                        getVaranegarActvity(),
                        actionsAdapter,
                        getSelectedId()));
            }

            actions.add(new CustomerQuestionnaireAction(
                    getVaranegarActvity(),
                    actionsAdapter,
                    getSelectedId()));

            // گزارش موجودی مشتری
            actions.add(new CustomerInventoryReportAction(
                    getVaranegarActvity(),
                    actionsAdapter,
                    getSelectedId()));
        }


        ConfirmAction confirmAction = new ConfirmAction(
                getVaranegarActvity(),
                actionsAdapter,
                getSelectedId());
        confirmAction.setActionCallBack(() -> {
            updateItem();
            updateCustomer();
        });
        actions.add(confirmAction);

        DeleteAction deleteAction = new DeleteAction(
                getVaranegarActvity(),
                actionsAdapter,
                getSelectedId());
        deleteAction.setActionCallBack(() -> {
            updateItem();
            updateCustomer();
        });
        actions.add(deleteAction);

        /**
         * درخواست برگشتی
         * ثبت برگشتی
         */
        actions.add(new BaseReturnAction(
                getVaranegarActvity(),
                getActionsAdapter(),
                getSelectedId()));

        SendOperationAction sendOperationAction = new SendOperationAction(
                getVaranegarActvity(),
                actionsAdapter,
                getSelectedId());
        sendOperationAction.setActionCallBack(() -> {
            updateItem();
            updateCustomer();
        });
        actions.add(sendOperationAction);

        VpnAction vpnAction = new VpnAction(
                getVaranegarActvity(),
                actionsAdapter,
                getSelectedId());
        vpnAction.setActionCallBack(new Action.ActionCallBack() {
            @Override
            public void done() {

            }
        });
        actions.add(vpnAction);

    }

    private void sortActions(List<Action> actions) {
        if (getContext() == null) return;
        final List<TaskPriorityModel> tasks = new TaskPriorityManager(getContext()).getAll();
        Linq.sort(actions, (o, t1) -> {
            final BaseAction action1 = (BaseAction) o;
            final BaseAction action2 = (BaseAction) t1;
            if (action1.getTaskUniqueId() == null && action2.getTaskUniqueId() == null)
                return 0;
            else if (action1.getTaskUniqueId() == null && action2.getTaskUniqueId() != null)
                return 1;
            else if (action1.getTaskUniqueId() != null && action2.getTaskUniqueId() == null)
                return -1;
            TaskPriorityModel task1 = Linq.findFirst(tasks, item ->
                    item.DeviceTaskUniqueId.equals(action1.getTaskUniqueId()));
            TaskPriorityModel task2 = Linq.findFirst(tasks, item ->
                    item.DeviceTaskUniqueId.equals(action2.getTaskUniqueId()));
            if (task1 == null && task2 == null)
                return 0;
            else if (task1 == null)
                return 1;
            else if (task2 == null)
                return -1;
            return Integer.compare(task1.Priority, task2.Priority);
        });
    }

    private final ArrayList<OnItemUpdateListener> onItemUpdateListeners = new ArrayList<>();

    public interface OnItemUpdateListener {
        void run();
    }


    public void addOnItemUpdateListener(OnItemUpdateListener onItemUpdateListener) {
        onItemUpdateListeners.add(onItemUpdateListener);
    }


    private String pin;

    public void toeditFragment() {

        /**
         * گرفتن pincode4 برای ویرایش مشتری در presale
         */

        final TourModel tourModel = new TourManager(getActivity()).loadTour();
        for (int i = 0; i < tourModel.Pins.size(); i++) {
            if (getSelectedId().equals(tourModel.Pins.get(i).CustomerId)) {
                pin = tourModel.Pins.get(i).PinCode4;
            }
        }
        if (pin != null) {
            InsertPinDialog dialog = new InsertPinDialog();
            dialog.setCancelable(false);
            dialog.setClosable(false);
            dialog.setValues(pin);
            dialog.setValuesRequst("pin4", getSelectedId(),
                    null, null,
                    "لظفا پین را وارد کنید و وارد صفحه ویراش مشتری شود برای گرفتن کدنقش");
            dialog.setOnResult(new InsertPinDialog.OnResult() {
                @Override
                public void done() {
                    //showEditDialog();
                }

                @Override
                public void failed(String error) {
                    Timber.e(error);
                    if (error.equals(getActivity()
                            .getString(R.string.pin_code_in_not_correct))) {
                        printFailed(getActivity(), error);
                    } else {
                        //saveSettlementFailed(getContext(), error);
                    }
                }
            });
            dialog.show(getActivity().getSupportFragmentManager(), "InsertPinDialog");
        } else {
            pin = "1111";
            InsertPinDialog dialog = new InsertPinDialog();
            dialog.setCancelable(false);
            dialog.setClosable(false);
            dialog.setValues(pin);
            dialog.settype("true");
            dialog.setValuesRequst("pin4", getSelectedId(),
                    null, null, getActivity().getString(R.string.please_insert_pin_code));
            dialog.setOnResult(new InsertPinDialog.OnResult() {
                @Override
                public void done() {
                    // showEditDialog();
                }

                @Override
                public void failed(String error) {
                    Timber.e(error);
                    if (error.equals(getActivity()
                            .getString(R.string.pin_code_in_not_correct))) {
                        printFailed(getActivity(), error);
                    } else {
                        //saveSettlementFailed(getContext(), error);
                    }
                }
            });
            dialog.show(getActivity().getSupportFragmentManager(), "InsertPinDialog");
        }
    }


    private void showEditDialog() {
//        EditCustomerZarFragmentDialog editCustomerFragmentDialog =
//                new EditCustomerZarFragmentDialog();
//       // editCustomerFragmentDialog.onCustomerEditedCallBack = this::runActionCallBack;
//        Bundle bundle = new Bundle();
//        bundle.putString("68565e5e-d407-4858-bc5f-fd52b9318734", getSelectedId().toString());
//        editCustomerFragmentDialog.setArguments(bundle);
//        editCustomerFragmentDialog.show(getActivity().getSupportFragmentManager(),
//                "EditCustomerFragmentDialog");


        Edit_New_Customer_ZarFragment edit_new_customer_zar = new Edit_New_Customer_ZarFragment();
        Bundle bundle = new Bundle();
        bundle.putString("68565e5e-d407-4858-bc5f-fd52b9318734", getSelectedId().toString());
        edit_new_customer_zar.setArguments(bundle);
        getVaranegarActvity().pushFragment(edit_new_customer_zar);
    }

    private void printFailed(Context context, String error) {
        try {
            CuteMessageDialog dialog = new CuteMessageDialog(context);
            dialog.setIcon(Icon.Warning);
            dialog.setTitle(R.string.DeliveryReasons);
            dialog.setMessage(error);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        } catch (Exception e1) {
            Timber.e(e1);
        }
    }
}
