package com.varanegar.vaslibrary.manager.locationmanager;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.network.Connectivity;
import com.varanegar.framework.network.gson.VaranegarGsonBuilder;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.util.datetime.JalaliCalendar;
import com.varanegar.framework.util.jobscheduler.JobScheduler;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.jobscheduler.SendTrackingPointsServiceJob;
import com.varanegar.vaslibrary.jobscheduler.TrackingServiceJob;
import com.varanegar.vaslibrary.manager.CustomerCallOrderOrderViewManager;
import com.varanegar.vaslibrary.manager.CustomerPathViewManager;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.customeractiontimemanager.CustomerActionTimeManager;
import com.varanegar.vaslibrary.manager.customeractiontimemanager.CustomerActions;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallOrderManager;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.BaseEventLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.DeviceEventViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.DeviceReportLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.EditOrderActivityEventViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.EditOrderLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.EventLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.GpsProviderOffLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.GpsProviderOnLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.LackOfOrderActivityEventViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.LackOfOrderLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.LackOfVisitLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.OrderActivityEventViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.OrderLineActivityEventViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.OrderLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.WaitLocationViewModel;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.model.call.CustomerCallOrderModel;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderViewModel;
import com.varanegar.vaslibrary.model.customercall.CustomerCallModel;
import com.varanegar.vaslibrary.model.customerpathview.CustomerPathViewModel;
import com.varanegar.vaslibrary.model.location.Location;
import com.varanegar.vaslibrary.model.location.LocationModel;
import com.varanegar.vaslibrary.model.location.LocationModelRepository;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.model.tour.TourModel;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.timezone.TimeApi;
import com.varanegar.vaslibrary.webapi.tracking.CompanyDeviceAppData;
import com.varanegar.vaslibrary.webapi.tracking.LicenseRequestBody;
import com.varanegar.vaslibrary.webapi.tracking.LicenseResponse;
import com.varanegar.vaslibrary.webapi.tracking.TrackingApi;
import com.varanegar.vaslibrary.webapi.tracking.TrackingRequestModel;

import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import okhttp3.Request;
import timber.log.Timber;


/**
 * Created by A.Torabi on 8/9/2017.
 */

public class LocationManager extends BaseManager<LocationModel> {

    private static final int POINT_SEND_LIMIT = 60;
    private static Long waitTime;
    private static Boolean isWait;

    public static LocationRequest getLocationRequest(Context context) {
        SysConfigManager sysConfigManager = new SysConfigManager(context);
        SysConfigModel trackingInterval = sysConfigManager.read(ConfigKey.TrackingInterval, SysConfigManager.cloud);
        SysConfigModel trackingMaxWaitTime = sysConfigManager.read(ConfigKey.TrackingWaitTime, SysConfigManager.cloud);
        SysConfigModel trackingSmallestDisplacement = sysConfigManager.read(ConfigKey.TrackingSmallestDisplacement, SysConfigManager.cloud);
        int interval = SysConfigManager.getIntValue(trackingInterval, 40);
        int maxWaitTime = SysConfigManager.getIntValue(trackingMaxWaitTime, 300);
        int smallestDisplacement = SysConfigManager.getIntValue(trackingSmallestDisplacement, 20);
        TrackingLicense trackingLicense = TrackingLicense.readLicense(context);
        if (trackingLicense != null) {
            interval = Math.max(interval, trackingLicense.getTimeInterval());
            smallestDisplacement = Math.max(smallestDisplacement, trackingLicense.getMinDistance());
        }
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(interval * 1000);
        locationRequest.setMaxWaitTime(maxWaitTime * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(smallestDisplacement);
        return locationRequest;
    }

    public static String getLocationInfo(android.location.Location location) {
        return "Point time:" + DateHelper.toString(new Date(location.getTime()), DateFormat.Complete, Locale.US) + " Latitude: " + location.getLatitude() + " Longitude: " + location.getLongitude() + " Provider: " + location.getProvider();
    }

    @Nullable
    public Boolean getGpsStatus(long time) {

        LocationModel location = getItem(new Query().from(Location.LocationTbl)
                .whereAnd(Criteria.equals(Location.EventType, GpsProviderOffLocationViewModel.class.getName())
                        .and(Criteria.lesserThanOrEqual(Location.Date, new Date(time)))).orderByDescending(Location.rowid));
        if (location != null)
            lastOff = location.Date.getTime();


        LocationModel location2 = getItem(new Query().from(Location.LocationTbl)
                .whereAnd(Criteria.equals(Location.EventType, GpsProviderOnLocationViewModel.class.getName())
                        .and(Criteria.lesserThanOrEqual(Location.Date, new Date(time)))).orderByDescending(Location.rowid));
        if (location2 != null)
            lastOn = location2.Date.getTime();


        if (lastOff == null || lastOn == null)
            return null;

        return lastOff.compareTo(lastOn) < 0;
    }

    public LocationManager(@NonNull Context context) {
        super(context, new LocationModelRepository());
    }

    public LocationModel getLastLocation(Class<? extends BaseEventLocationViewModel> locationViewModelClass) {
        return getItem(new Query().from(Location.LocationTbl)
                .whereAnd(Criteria.equals(Location.EventType, locationViewModelClass.getName()))
                .orderByDescending(Location.rowid));
    }

    public List<LocationModel> getLocations(@NonNull UUID customerId, @Nullable UUID tourId) {
        if (tourId == null)
            return getItems(new Query().from(Location.LocationTbl)
                    .whereAnd(Criteria.equals(Location.CustomerId, customerId.toString())));
        else
            return getItems(new Query().from(Location.LocationTbl)
                    .whereAnd(Criteria.equals(Location.CustomerId, customerId.toString()))
                    .whereAnd(Criteria.equals(Location.TourId, tourId)));
    }

    public List<LocationModel> getLocations(Class<? extends BaseEventLocationViewModel> locationViewModelClass) {
        return getItems(new Query().from(Location.LocationTbl)
                .whereAnd(Criteria.equals(Location.EventType, locationViewModelClass.getName())));

    }

    public List<LocationModel> getLocationModel(){
        return getItems(new Query().from(Location.LocationTbl));
    }

    public LocationModel getLastPointLocationIsSend() {
        return getItem(
                new Query()
                        .from(Location.LocationTbl)
                        .whereAnd(Criteria.equals(Location.IsSend, true))
                        .orderByDescending(Location.Date)
        );
    }

    /**
     *آخرین point
     * @return
     */
    public LocationModel getLastPointLocation() {
        return getItem(
                new Query()
                        .from(Location.LocationTbl).orderByDescending(Location.Date));
    }
    public LocationModel getLastLocation() {
        return getItem(new Query().from(Location.LocationTbl).orderByDescending(Location.rowid));
    }

    public void createOrderTracking(@NonNull CustomerModel customer, @NonNull OnSaveLocation onSaveLocation) {
        if (!SysConfigManager.hasTracking(getContext())) {
            TrackingLogManager.addLog(getContext(), LogType.CONFIG, LogLevel.Error, "ترکینگ از کنسول غیرفعال است");
            onSaveLocation.onSaved(null);
            return;
        }

        if (!TrackingLicense.isValid(getContext())) {
            TrackingLogManager.addLog(getContext(), LogType.LICENSE, LogLevel.Error, "لایسنس ردیابی معتبر نمی باشد");
            onSaveLocation.onSaved(null);
            return;
        }

        CustomerPathViewModel customerPathViewModel = new CustomerPathViewManager(getContext()).getItem(CustomerPathViewManager.checkIsInDayVisitPath(customer, new TourManager(getContext()).loadTour().DayVisitPathId));
        boolean customerIsInVisitDayPath = customerPathViewModel != null;

        CustomerCallManager customerCallManager = new CustomerCallManager(getContext());
        List<CustomerCallModel> calls = customerCallManager.loadCalls(customer.UniqueId);
        CustomerActionTimeManager customerActionTimeManager = new CustomerActionTimeManager(getContext());
        Date endTime = customerActionTimeManager.get(customer.UniqueId, CustomerActions.CustomerCallEnd);
        Date startTime = customerActionTimeManager.get(customer.UniqueId, CustomerActions.CustomerCallStart);
        boolean sendOrderPoint;
        if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist))
            sendOrderPoint = customerCallManager.hasOrderOrReturnCall(calls);
        else
            sendOrderPoint = customerCallManager.hasDeliveryCall(calls, null, null);
        if (sendOrderPoint) {
            TrackingLogManager.addLog(getContext(), LogType.ORDER_EVENT, LogLevel.Info, "ثبت سفارش برای مشتری " + customer.CustomerCode + " (" + customer.CustomerName + ")");
            final OrderLocationViewModel orderLocationViewModel = new OrderLocationViewModel();
            orderLocationViewModel.CustomerId = customer.UniqueId;
            orderLocationViewModel.eventData = new OrderActivityEventViewModel();
            orderLocationViewModel.eventData.Address = customer.Address;
            orderLocationViewModel.eventData.CustomerCode = customer.CustomerCode;
            orderLocationViewModel.eventData.CustomerName = customer.CustomerName;
            orderLocationViewModel.eventData.StartTime = DateHelper.toString(startTime, DateFormat.MicrosoftDateTime, Locale.US);
            orderLocationViewModel.eventData.EndTime = DateHelper.toString(endTime, DateFormat.MicrosoftDateTime, Locale.US);
            orderLocationViewModel.eventData.StoreName = customer.StoreName;
            orderLocationViewModel.eventData.Phone = customer.Phone;
            orderLocationViewModel.eventData.CustomerId = customer.UniqueId;
            orderLocationViewModel.eventData.OrderLine = new ArrayList<>();
            orderLocationViewModel.eventData.IsInVisitDayPath = customerIsInVisitDayPath;
            CustomerCallOrderManager callOrderManager = new CustomerCallOrderManager(getContext());

            List<CustomerCallOrderModel> callOrderModels = callOrderManager.getCustomerCallOrders(customer.UniqueId);
            if (callOrderModels.size() > 0) {
                // FIXME: 2/19/2018 add customer call id
                orderLocationViewModel.eventData.UniqueId = callOrderModels.get(0).UniqueId;
                CustomerCallOrderOrderViewManager callOrderLineManager = new CustomerCallOrderOrderViewManager(getContext());
                BigDecimal totalQty = BigDecimal.ZERO;
                double totalAmount = 0;
                for (CustomerCallOrderModel callOrderModel :
                        callOrderModels) {
                    List<CustomerCallOrderOrderViewModel> lines =
                            callOrderLineManager.getLines(callOrderModel.UniqueId, null);
                    for (CustomerCallOrderOrderViewModel item :
                            lines) {
                        OrderLineActivityEventViewModel orderLine = new OrderLineActivityEventViewModel();
                        orderLine.Price = HelperMethods.currencyToDouble(item.UnitPrice);
                        orderLine.ProductGuid = item.ProductId;
                        orderLine.ProductName = item.ProductName;
                        orderLine.Qty = HelperMethods.bigDecimalToDouble(item.TotalQty);
                        orderLocationViewModel.eventData.OrderLine.add(orderLine);
                        if (item.TotalQty != null) {
                            totalQty = totalQty.add(item.TotalQty);
                            if (item.UnitPrice != null)
                                totalAmount += (item.UnitPrice.doubleValue() * item.TotalQty.doubleValue());
                        }
                    }
                }
                orderLocationViewModel.eventData.OrderQty = totalQty;
                orderLocationViewModel.eventData.OrderAmunt = BigDecimal.valueOf(totalAmount);
            }
            addTrackingPoint(orderLocationViewModel, onSaveLocation);
            TourModel tourModel = new TourManager(getContext()).loadTour();
            List<LocationModel> locationModels = getLocations(customer.UniqueId, tourModel != null ? tourModel.UniqueId : null);
            boolean hasOrderAlready = Linq.exists(locationModels, item -> {
                if (item.EventType != null)
                    return item.EventType.equals(OrderLocationViewModel.class.getName());
                return false;
            });

            if (hasOrderAlready) {
                TrackingLogManager.addLog(getContext(), LogType.ORDER_EVENT, LogLevel.Info, "ویرایش سفارش برای مشتری " + customer.CustomerCode + " (" + customer.CustomerName + ")");
                EditOrderLocationViewModel editOrderLocationViewModel = new EditOrderLocationViewModel();
                editOrderLocationViewModel.eventData = new EditOrderActivityEventViewModel();
                editOrderLocationViewModel.eventData.Address = customer.Address;
                editOrderLocationViewModel.eventData.CustomerCode = customer.CustomerCode;
                editOrderLocationViewModel.eventData.CustomerName = customer.CustomerName;
                editOrderLocationViewModel.eventData.Phone = customer.Phone;
                editOrderLocationViewModel.eventData.StoreName = customer.StoreName;
                editOrderLocationViewModel.eventData.Time = DateHelper.toString(new GregorianCalendar(), DateFormat.MicrosoftDateTime);
                editOrderLocationViewModel.eventData.PTime = DateHelper.toString(new JalaliCalendar(), DateFormat.MicrosoftDateTime);
                editOrderLocationViewModel.eventData.CustomerId = customer.UniqueId;
                editOrderLocationViewModel.eventData.StartTime = DateHelper.toString(startTime, DateFormat.MicrosoftDateTime, Locale.US);
                editOrderLocationViewModel.eventData.EndTime = DateHelper.toString(endTime, DateFormat.MicrosoftDateTime, Locale.US);
                editOrderLocationViewModel.eventData.Description = "ویرایش سفارش مشتری " + customer.CustomerName + "(" + customer.CustomerCode + ")";
                addTrackingPoint(editOrderLocationViewModel, new OnSaveLocation() {
                    @Override
                    public void onSaved(LocationModel location) {
                        tryToSendItem(location);
                    }

                    @Override
                    public void onFailed(String error) {

                    }
                });
            }
        } else if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist) && customerCallManager.isLackOfOrder(calls)) {
            TrackingLogManager.addLog(getContext(), LogType.ORDER_EVENT, LogLevel.Info, " عدم سفارش برای مشتری " + customer.CustomerCode + " (" + customer.CustomerName + ")");
            LackOfOrderLocationViewModel lackOfOrderLocationViewModel = new LackOfOrderLocationViewModel();
            lackOfOrderLocationViewModel.CustomerId = customer.UniqueId;
            lackOfOrderLocationViewModel.eventData = new LackOfOrderActivityEventViewModel();
            lackOfOrderLocationViewModel.eventData.Address = customer.Address;
            lackOfOrderLocationViewModel.eventData.CustomerCode = customer.CustomerCode;
            lackOfOrderLocationViewModel.eventData.CustomerName = customer.CustomerName;
            lackOfOrderLocationViewModel.eventData.Phone = customer.Phone;
            lackOfOrderLocationViewModel.eventData.StoreName = customer.StoreName;
            lackOfOrderLocationViewModel.eventData.Time = DateHelper.toString(new GregorianCalendar(), DateFormat.MicrosoftDateTime);
            lackOfOrderLocationViewModel.eventData.PTime = DateHelper.toString(new JalaliCalendar(), DateFormat.MicrosoftDateTime);
            lackOfOrderLocationViewModel.eventData.CustomerId = customer.UniqueId;
            lackOfOrderLocationViewModel.eventData.IsInVisitDayPath = customerIsInVisitDayPath;
            lackOfOrderLocationViewModel.eventData.StartTime = DateHelper.toString(startTime, DateFormat.MicrosoftDateTime, Locale.US);
            lackOfOrderLocationViewModel.eventData.EndTime = DateHelper.toString(endTime, DateFormat.MicrosoftDateTime, Locale.US);
            addTrackingPoint(lackOfOrderLocationViewModel, onSaveLocation);
        } else if (customerCallManager.isLackOfVisit(calls)) {
            TrackingLogManager.addLog(getContext(), LogType.ORDER_EVENT, LogLevel.Info, " عدم ویزیت برای مشتری " + customer.CustomerCode + " (" + customer.CustomerName + ")");
            LackOfVisitLocationViewModel lackOfVisitLocationViewModel = new LackOfVisitLocationViewModel();
            lackOfVisitLocationViewModel.CustomerId = customer.UniqueId;
            lackOfVisitLocationViewModel.eventData.Address = customer.Address;
            lackOfVisitLocationViewModel.eventData.CustomerName = customer.CustomerName;
            lackOfVisitLocationViewModel.eventData.CustomerCode = customer.CustomerCode;
            lackOfVisitLocationViewModel.eventData.Phone = customer.Phone;
            lackOfVisitLocationViewModel.eventData.PTime = DateHelper.toString(new JalaliCalendar(), DateFormat.MicrosoftDateTime);
            lackOfVisitLocationViewModel.eventData.Time = DateHelper.toString(new GregorianCalendar(), DateFormat.MicrosoftDateTime);
            lackOfVisitLocationViewModel.eventData.StoreName = customer.StoreName;
            lackOfVisitLocationViewModel.eventData.CustomerId = customer.UniqueId;
            lackOfVisitLocationViewModel.eventData.IsInVisitDayPath = customerIsInVisitDayPath;
            addTrackingPoint(lackOfVisitLocationViewModel, onSaveLocation);
        } else {
            onSaveLocation.onSaved(null);
        }
    }


    public interface OnLocationUpdated {
        void onSucceeded(LocationModel location);

        void onFailed(String error);
    }

    public List<LocationModel> getLocations(Date from, Date to, boolean isTracking, boolean isSend) {
        Query query;
//        if (ignoreWait) {
        query = new Query().from(Location.LocationTbl)
                .whereAnd(Criteria.between(Location.Date, from, to)).orderByAscending(Location.Date);
//        } else {
//            query = new Query().from(Location.LocationTbl)
//                    .whereAnd(Criteria.notEquals(Location.ActivityType, DetectedActivity.STILL)
//                            .or(Criteria.equals(Location.IsImportant, true)))
//                    .whereAnd(Criteria.between(Location.Date, from, to)).orderByAscending(Location.Date);
//        }
        if (isTracking)
            query.whereAnd(Criteria.equals(Location.Tracking, true));
        if (isSend)
            query.whereAnd(Criteria.equals(Location.IsSend, true));
        return getItems(query);

    }

    public List<LocationModel> getEventLocations(Date from, Date to, boolean isSend) {
        Query query;
//        if (ignoreWait) {
        query = new Query().from(Location.LocationTbl);
//        } else {
//            query = new Query().from(Location.LocationTbl)
//                    .whereAnd(Criteria.notEquals(Location.ActivityType, DetectedActivity.STILL)
//                            .or(Criteria.equals(Location.IsImportant, true)));
//        }
        query.whereAnd(Criteria.notIsNull(Location.EventType))
                .whereAnd(Criteria.between(Location.Date, from, to)).orderByAscending(Location.Date);
        if (isSend)
            query.whereAnd(Criteria.equals(Location.IsSend, true));

        return getItems(query);
    }

    private GoogleApiClient googleApiClient;

    public void getLocation(@NonNull final OnLocationUpdated onLocationUpdated) {
        final UserModel userModel = UserManager.readFromFile(getContext());
        final TourModel tourModel = new TourManager(getContext()).loadTour();
        if (userModel != null && tourModel != null) {
            final FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                onLocationUpdated.onFailed("Permission denied");
                return;
            }
            try {
                googleApiClient = new GoogleApiClient.Builder(getContext())
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(@Nullable Bundle bundle) {

                                fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, new CancellationToken() {
                                    @Override
                                    public boolean isCancellationRequested() {
                                        return false;
                                    }

                                    @NonNull
                                    @Override
                                    public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                                        return null;
                                    }
                                }).addOnSuccessListener(location -> {
                                    if (location != null) {
                                        if (location.getTime() < 1556668800000L) {
                                            TrackingLogManager.addLog(getContext(), LogType.POINT_TIME, LogLevel.Error, "{" + location.getLatitude() + "  " + location.getLongitude() + "} Gps Time is wrong : " + DateHelper.toString(new Date(location.getTime()), DateFormat.Complete, Locale.getDefault()));
                                            location.setTime(new Date().getTime());
                                        }

                                        if (Math.abs(location.getTime() - new Date().getTime()) / 1000 > 1800) {
                                            TrackingLogManager.addLog(getContext(), LogType.POINT_TIME, LogLevel.Error, "{" + location.getLatitude() + "  "
                                                    + location.getLongitude()
                                                    + "} Gps Time is different from Tablet time. Gps time = "
                                                    + DateHelper.toString(new Date(location.getTime()), DateFormat.Complete, Locale.getDefault())
                                                    + " Tablet Time : "
                                                    + DateHelper.toString(new Date(), DateFormat.Complete, Locale.getDefault()));
                                            TimeApi api = new TimeApi(getContext());
                                            api.checkTime(message -> TrackingLogManager.addLog(getContext(), LogType.INVALID_TIME, LogLevel.Info, message));
                                            location.setTime(new Date().getTime());
                                        }

                                        TrackingLogManager.addLog(getContext(), LogType.POINT, LogLevel.Info, "{" + location.getLatitude() + "  " + location.getLongitude() + "}");
                                        LocationModel locationModel = LocationModel.convert(getContext(), location, userModel, tourModel);
                                        onLocationUpdated.onSucceeded(locationModel);
                                    } else {
                                        TrackingLogManager.addLog(getContext(), LogType.POINT, LogLevel.Error, "پوینت دریافت نشد!");
                                        Timber.e("Could not get location. we use the latest point from database");
                                        final LocationModel lastLocation = getLastLocation();
                                        if (lastLocation != null && HelperMethods.isSameDay(new Date(), lastLocation.Date)) {
                                            onLocationUpdated.onSucceeded(lastLocation.convert(getContext(), userModel, tourModel));
                                        } else {
                                            Timber.e("Could not get the latest location from database.");
                                            onLocationUpdated.onFailed("Could not get point");
                                        }
                                    }
                                    if (googleApiClient != null && googleApiClient.isConnected())
                                        googleApiClient.disconnect();
                                }).addOnFailureListener(e -> {
                                    TrackingLogManager.addLog(getContext(), LogType.POINT, LogLevel.Error, "پوینت دریافت نشد!");
                                    Timber.e("Could not get location. we use the latest point from database");
                                    final LocationModel lastLocation = getLastLocation();
                                    if (lastLocation != null && HelperMethods.isSameDay(new Date(), lastLocation.Date)) {
                                        onLocationUpdated.onSucceeded(lastLocation.convert(getContext(), userModel, tourModel));
                                    } else {
                                        Timber.e("Could not get the latest location from database.");
                                        onLocationUpdated.onFailed("Could not get point");
                                    }
                                    if (googleApiClient != null && googleApiClient.isConnected())
                                        googleApiClient.disconnect();
                                }).addOnCanceledListener(() -> TrackingLogManager.addLog(getContext(), LogType.POINT, LogLevel.Error, " دریافت پوینت کنسل شد!"));
                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                TrackingLogManager.addLog(getContext(), LogType.POINT, LogLevel.Error, "پوینت دریافت نشد!");
                                Timber.e("Could not get location. we use the latest point from database");
                                final LocationModel lastLocation = getLastLocation();
                                if (lastLocation != null && HelperMethods.isSameDay(new Date(), lastLocation.Date)) {
                                    onLocationUpdated.onSucceeded(lastLocation.convert(getContext(), userModel, tourModel));
                                } else {
                                    Timber.e("Could not get the latest location from database.");
                                    onLocationUpdated.onFailed("Could not get point");
                                }
                                if (googleApiClient != null && googleApiClient.isConnected())
                                    googleApiClient.disconnect();
                            }
                        }).addOnConnectionFailedListener(connectionResult -> {
                            TrackingLogManager.addLog(getContext(), LogType.POINT, LogLevel.Error, "پوینت دریافت نشد!");
                            Timber.e("Could not get location. we use the latest point from database");
                            final LocationModel lastLocation = getLastLocation();
                            if (lastLocation != null && HelperMethods.isSameDay(new Date(), lastLocation.Date)) {
                                onLocationUpdated.onSucceeded(lastLocation.convert(getContext(), userModel, tourModel));
                            } else {
                                Timber.e("Could not get the latest location from database.");
                                onLocationUpdated.onFailed("Could not get point");
                            }
                        }).build();
                googleApiClient.connect();
            } catch (Error error) {
                Timber.e(error);
                TrackingLogManager.addLog(getContext(), LogType.POINT, LogLevel.Error, "پوینت دریافت نشد!", error.getMessage());
                onLocationUpdated.onFailed(error.getMessage());
            }

        } else {
            onLocationUpdated.onFailed("User has not signed in");
        }
    }

//    private void useLastLocationFromDb(UserModel userModel, OnLocationUpdated onLocationUpdated) {
//        LocationModel locationModel = getLastLocation(Accuracy.TrackingAccuracy);
//        if (locationModel != null) {
//            locationModel.TimeOffset = new Date().getTime() - locationModel.Date.getTime();
//            locationModel.DateAndTime = DateHelper.toString(locationModel.Date, DateFormat.Complete, Locale.getDefault());
//            locationModel.ActivityType = isWait() ? 0 : 1;
//            locationModel.UniqueId = UUID.randomUUID();
//            locationModel.IsSend = false;
//            TourModel tourModel = new TourManager(getContext()).loadTour();
//            locationModel.TourId = tourModel != null ? tourModel.UniqueId : null;
//            locationModel.TourRef = tourModel != null ? tourModel.TourNo : null;
//            locationModel.CompanyPersonnelId = userModel.UniqueId;
//            locationModel.CompanyPersonnelName = userModel.UserName;
//            onLocationUpdated.onSucceeded(locationModel);
//        } else
//            onLocationUpdated.onFailed("Location data is not available in database");
//    }

    public float distance(@NonNull android.location.Location location1, @NonNull LocationModel location2) {
        android.location.Location loc2 = new android.location.Location("location2");
        loc2.setLatitude(location2.Latitude);
        loc2.setLongitude(location2.Longitude);
        return location1.distanceTo(loc2);
    }

    public float distance(@NonNull LocationModel location1, @NonNull LocationModel location2) {
        android.location.Location loc1 = new android.location.Location("location1");
        loc1.setLatitude(location1.Latitude);
        loc1.setLongitude(location1.Longitude);
        android.location.Location loc2 = new android.location.Location("location2");
        loc2.setLatitude(location2.Latitude);
        loc2.setLongitude(location2.Longitude);
        return loc1.distanceTo(loc2);
    }

    public interface DownloadCallBack {
        void done();

        void failed(String error);
    }

    public void downloadTrackingLicense(String deviceId, @NonNull final DownloadCallBack callBack) {
        TrackingApi trackingApi = new TrackingApi(getContext());
        LicenseRequestBody body = new LicenseRequestBody();
        body.companyDeviceAppData = new CompanyDeviceAppData();
        body.companyDeviceAppData.IMEI = deviceId;
        trackingApi.runWebRequest(trackingApi.getLicense(body), new WebCallBack<LicenseResponse>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(LicenseResponse result, Request request) {
                if (result == null) {
                    TrackingLogManager.addLog(getContext(), LogType.LICENSE_FILE, LogLevel.Error, "فایل لایسنس دریافت نشد. جواب وب سرویس تهی بود!");
                    Timber.e("Downloading tracking license failed. Result is null");
                    callBack.failed(getContext().getString(R.string.downloading_license_file_failed));
                } else if (result.type == 1) {
                    JobScheduler.resetJob(TrackingServiceJob.class, getContext());
                    Timber.i("License File for tracking downloaded successfully");
                    TrackingLogManager.addLog(getContext(), LogType.LICENSE_FILE, LogLevel.Info, "فایل لایسنس با موفقیت دریافت شد", result.licenseStr);
                    SharedPreferences trackingLicenseSharedPref = getContext().getSharedPreferences("TRACKING_LICENSE", MODE_PRIVATE);
                    SharedPreferences.Editor editor = trackingLicenseSharedPref.edit();
                    editor.putString("licenseStr", result.licenseStr);
                    editor.apply();
                    callBack.done();
                } else {
                    TrackingLogManager.addLog(getContext(), LogType.LICENSE_FILE, LogLevel.Error, "Downloading tracking license failed. " + result.licenseStr);
                    Timber.e("Downloading tracking license failed. " + result.licenseStr);
                    callBack.failed(result.licenseStr);
                }
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, getContext());
                Timber.e(err);
                TrackingLogManager.addLog(getContext(), LogType.LICENSE_FILE, LogLevel.Error, err);
                callBack.failed(err);
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                Timber.e(t, "Downloading license file failed. network problem");
                TrackingLogManager.addLog(getContext(), LogType.LICENSE_FILE, LogLevel.Error, "دانلود لایسنس با خطا مواجه شد. خطا در برقراری ارتباط!", t.getMessage());
                callBack.failed(getContext().getString(R.string.network_error));
            }
        });
    }

    public synchronized LocationModel updateTrackingPoint(BaseEventLocationViewModel event, @NonNull LocationModel location) {
        if (!TrackingLicense.isValid(getContext())) {
            return null;
        }
        if (event instanceof EventLocationViewModel) {
            location.CustomerId = ((EventLocationViewModel) event).CustomerId;
        }
        location.Tracking = TrackingLicense.isValid(getContext());
        location.Event = VaranegarGsonBuilder.build().create().toJson(event);
        location.EventType = event.getClass().getName();
        location.UniqueId = event.UniqueId;
//        location.IsImportant = event.IsImportant();
        location.IsSend = false;
        try {
            update(location);
            return location;
        } catch (Exception e) {
            TrackingLogManager.addLog(getContext(), LogType.UPDATE_POINT, LogLevel.Error, "updating point event : " + location.EventType + " failed.", e.getMessage());
            return null;
        }
    }

    public synchronized void addTrackingPoint(@NonNull final BaseEventLocationViewModel event, @Nullable final OnSaveLocation callBack) {
        addTrackingPoint(true, true, event, null, callBack);
    }

    public synchronized void addTrackingPoint(@NonNull final BaseEventLocationViewModel event, @Nullable LocationModel locationModel, @Nullable final OnSaveLocation callBack) {
        addTrackingPoint(true, true, event, locationModel, callBack);
    }

    public synchronized void addTrackingPoint(boolean checkLicense, boolean checkConfig, @NonNull final BaseEventLocationViewModel event, @Nullable LocationModel locationModel, @Nullable final OnSaveLocation callBack) {
        if (checkConfig && !SysConfigManager.hasTracking(getContext())) {
            TrackingLogManager.addLog(getContext(), LogType.CONFIG, LogLevel.Error, "ترکینگ از کنسول غیرفعال است");
            if (callBack != null)
                callBack.onFailed("Tracking config is not found");
            return;
        }
        if (checkLicense && !TrackingLicense.isValid(getContext())) {
            if (callBack != null)
                callBack.onFailed("Tracking License is not valid");
            return;
        }
        if (locationModel != null) {
            if (event instanceof EventLocationViewModel) {
                locationModel.CustomerId = ((EventLocationViewModel) event).CustomerId;
            }
            locationModel.Tracking = TrackingLicense.isValid(getContext());
            locationModel.Event = VaranegarGsonBuilder.build().create().toJson(event);
            locationModel.EventType = event.getClass().getName();
            locationModel.UniqueId = UUID.randomUUID();
//                location.IsImportant = event.IsImportant();
            try {
                insert(locationModel);
                if (callBack != null)
                    callBack.onSaved(locationModel);
            } catch (Exception e) {
                TrackingLogManager.addLog(getContext(), LogType.EVENT, LogLevel.Error, locationModel.EventType + " failed.", e.getMessage());
                if (callBack != null)
                    callBack.onFailed(e.getMessage());
            }
        } else
            getLocation(new OnLocationUpdated() {
                @Override
                public void onSucceeded(LocationModel location) {
                    if (event instanceof EventLocationViewModel) {
                        location.CustomerId = ((EventLocationViewModel) event).CustomerId;
                    }
                    location.Tracking = TrackingLicense.isValid(getContext());
                    location.Event = VaranegarGsonBuilder.build().create().toJson(event);
                    location.EventType = event.getClass().getName();
                    location.UniqueId = UUID.randomUUID();
//                location.IsImportant = event.IsImportant();
                    try {
                        insert(location);
                        if (callBack != null)
                            callBack.onSaved(location);
                    } catch (Exception e) {
                        TrackingLogManager.addLog(getContext(), LogType.EVENT, LogLevel.Error, location.EventType + " failed.", e.getMessage());
                        if (callBack != null)
                            callBack.onFailed(e.getMessage());
                    }
                }

                @Override
                public void onFailed(String error) {
                    if (callBack != null) {
                        callBack.onFailed(error);
                    }
                }
            });
    }

    @Override
    public long insert(@NonNull LocationModel item) throws ValidationException, DbException {
        if (item.EventType != null && item.EventType.equals(GpsProviderOffLocationViewModel.class.getName()))
            lastOff = item.Date.getTime();
        if (item.EventType != null && item.EventType.equals(GpsProviderOnLocationViewModel.class.getName()))
            lastOn = item.Date.getTime();
        return super.insert(item);
    }
    public interface SendLocationListener{
        void onSendFailed();
    }

    public synchronized void tryToSendAll(@Nullable SendLocationListener callback) {
        if (hasSemaphore())
            return;
        setSemaphore();
        final TrackingLicense license = TrackingLicense.readLicense(getContext());
        if (license == null) {
            removeSemaphore();
            sendReport();
            return;
        }
        if (license.isExpired(getContext())) {
            removeSemaphore();
            sendReport();
            return;
        }
        final List<LocationModel> locations = getItems(
                new Query()
                        .from(Location.LocationTbl)
                        .whereAnd(Criteria.equals(Location.IsSend, false)
                                .and(Criteria.equals(Location.Tracking, true)))
//                        .whereAnd(Criteria.notEquals(Location.ActivityType, DetectedActivity.STILL)
//                                .or(Criteria.equals(Location.IsImportant, true)))
                        .whereAnd(Criteria.notEquals(Location.EventType, DeviceReportLocationViewModel.class.getName())
                                .or(Criteria.isNull(Location.EventType))).orderByDescending(Location.Date).take(POINT_SEND_LIMIT)
        );
        /* NGT-2611 ali razi nabood :( vali ahad anjam dad :)
        final List<LocationModel> locations = Linq.findAll(allLocations, new Linq.Criteria<LocationModel>() {
            @Override
            public boolean run(LocationModel item) {
                return item.EventType != null || item.Accuracy < 2000;
           }
        }); */
        if (locations.size() > 0) {
            if (!Connectivity.isConnected(getContext())) {
                TrackingLogManager.addLog(getContext(), LogType.SUBMIT_POINT, LogLevel.Error, "No Internet connection " + locations.size() + " نقطه ارسال نشد." + " تاریخ پوینت ها از " + DateHelper.toString(locations.get(0).Date, DateFormat.Complete, Locale.getDefault()) + " تا " + DateHelper.toString(locations.get(locations.size() - 1).Date, DateFormat.Complete, Locale.getDefault()));
                removeSemaphore();
                sendReport();
                return;
            }
            final TrackingRequestModel trackingRequestModel = createTrackingRequestModel(locations);
            TrackingApi trackingApi = new TrackingApi(getContext());
            trackingApi.runWebRequest(trackingApi.sendPoint(trackingRequestModel), new WebCallBack<Boolean>() {
                @Override
                protected void onFinish() {

                }

                @Override
                protected void onSuccess(Boolean result, Request request) {
                    Timber.i("LocationManager trackingApi onSuccess SendPoint1 " + request + "result = " +result);
                    TrackingLogManager.addLog(getContext(), LogType.SUBMIT_POINT, LogLevel.Info, locations.size() + " نقطه ارسال شد." + " تاریخ پوینت ها از " + DateHelper.toString(locations.get(0).Date, DateFormat.Complete, Locale.getDefault()) + " تا " + DateHelper.toString(locations.get(locations.size() - 1).Date, DateFormat.Complete, Locale.getDefault()));
                    Date date = new Date();
                    for (LocationModel locationModel :
                            locations) {
                        locationModel.IsSend = true;
                        locationModel.LastRetryTime = date;

                    }
                    try {
                        update(locations);
                        removeSemaphore();
                        sendReport();
                        if (locations.size() == POINT_SEND_LIMIT) // Probably we have more points to send se we start this task again
                            JobScheduler.resetJob(SendTrackingPointsServiceJob.class, getContext());
                    } catch (Exception e) {
                        Timber.i("LocationManager SendPoint1 Exception "+e.getMessage());
                        removeSemaphore();
                        sendReport();
                    }
                }

                @Override
                protected void onApiFailure(ApiError error, Request request) {
                    removeSemaphore();
                    TrackingLogManager.addLog(getContext(), LogType.SUBMIT_POINT, LogLevel.Error, locations.size() + " نقطه ارسال نشد." + " تاریخ پوینت ها از " + DateHelper.toString(locations.get(0).Date, DateFormat.Complete, Locale.getDefault()) + " تا " + DateHelper.toString(locations.get(locations.size() - 1).Date, DateFormat.Complete, Locale.getDefault()), error.getMessage());
                    sendReport();
                    Date date = new Date();
                    for (LocationModel location :
                            locations) {
                        location.LastRetryTime = date;
                    }
                    try {
                        update(locations);
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                    if(callback!=null)
                        callback.onSendFailed();
                }

                @Override
                protected void onNetworkFailure(Throwable t, Request request) {
                    removeSemaphore();
                    TrackingLogManager.addLog(getContext(), LogType.SUBMIT_POINT, LogLevel.Error, locations.size() + " نقطه ارسال نشد." + " تاریخ پوینت ها از " + DateHelper.toString(locations.get(0).Date, DateFormat.Complete, Locale.getDefault()) + " تا " + DateHelper.toString(locations.get(locations.size() - 1).Date, DateFormat.Complete, Locale.getDefault()), t.getMessage());
                    sendReport();
                    Date date = new Date();
                    for (LocationModel location :
                            locations) {
                        location.LastRetryTime = date;
                    }
                    try {
                        update(locations);
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }
            });
        } else {
            removeSemaphore();
            sendReport();
        }
    }

    private void sendReport() {
        List<LocationModel> locations = getLocations(DeviceReportLocationViewModel.class);
        UserModel userModel = UserManager.readFromFile(getContext());
        if (locations.size() > 0 && userModel != null) {
            tryToSendItems(locations, TrackingApi.getDefaultServer(getContext()), false);
        }
    }

    public synchronized void tryToSendItems(final List<LocationModel> locations) {
        tryToSendItems(locations, TrackingApi.getDefaultServer(getContext()), true);
    }

    public synchronized void tryToSendItems(final List<LocationModel> locations, String baseUrl, boolean verifyLicense) {
        if (hasSemaphore())
            return;
        setSemaphore();
        final List<LocationModel> filteredLocations = new ArrayList<>();
        for (LocationModel locationModel :
                locations) {
//            if (locationModel.ActivityType != DetectedActivity.STILL || locationModel.IsImportant) {
            if (!locationModel.IsSend)
                filteredLocations.add(locationModel);
//            }
        }

        if (filteredLocations.size() == 0) {
            removeSemaphore();
            return;
        }

        if (verifyLicense) {
            TrackingLicense license = TrackingLicense.readLicense(getContext());
            if (license == null) {
                removeSemaphore();
                return;
            }
            if (license.isExpired(getContext())) {
                removeSemaphore();
                return;
            }
        }

        if (!Connectivity.isConnected(getContext())) {
            TrackingLogManager.addLog(getContext(), LogType.SUBMIT_POINT, LogLevel.Error, "No Internet connection " + locations.size() + " نقطه ارسال نشد." + " تاریخ پوینت ها از " + DateHelper.toString(locations.get(0).Date, DateFormat.Complete, Locale.getDefault()) + " تا " + DateHelper.toString(locations.get(locations.size() - 1).Date, DateFormat.Complete, Locale.getDefault()));
            removeSemaphore();
            return;
        }
        final TrackingRequestModel trackingRequestModel = createTrackingRequestModel(filteredLocations);
        TrackingApi trackingApi = new TrackingApi(getContext());
        trackingApi.runWebRequest(trackingApi.sendPoint(baseUrl, trackingRequestModel), new WebCallBack<Boolean>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(Boolean result, Request request) {
                Timber.i("LocationManager trackingApi onSuccess SendPoint22 " + request + "result = " +result);
                TrackingLogManager.addLog(getContext(), LogType.SUBMIT_POINT, LogLevel.Info, locations.size() + " نقطه ارسال شد." + " تاریخ پوینت ها از " + DateHelper.toString(locations.get(0).Date, DateFormat.Complete, Locale.getDefault()) + " تا " + DateHelper.toString(locations.get(locations.size() - 1).Date, DateFormat.Complete, Locale.getDefault()));
                Date date = new Date();
                for (LocationModel locationModel :
                        filteredLocations) {
                    locationModel.IsSend = true;
                    locationModel.LastRetryTime = date;
                }
                try {
                    update(filteredLocations);
                    removeSemaphore();
                } catch (Exception e) {
                    Timber.i("LocationManager SendPoint22 Exception "+e.getMessage());
                    removeSemaphore();
                }
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                removeSemaphore();
                TrackingLogManager.addLog(getContext(), LogType.SUBMIT_POINT, LogLevel.Error, locations.size() + " نقطه ارسال نشد." + " تاریخ پوینت ها از " + DateHelper.toString(locations.get(0).Date, DateFormat.Complete, Locale.getDefault()) + " تا " + DateHelper.toString(locations.get(locations.size() - 1).Date, DateFormat.Complete, Locale.getDefault()), error.getMessage());
                Date date = new Date();
                for (LocationModel location :
                        locations) {
                    location.LastRetryTime = date;
                }
                try {
                    update(locations);
                } catch (Exception e) {
                    Timber.e(e);
                }
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                TrackingLogManager.addLog(getContext(), LogType.SUBMIT_POINT, LogLevel.Error, locations.size() + " نقطه ارسال نشد." + " تاریخ پوینت ها از " + DateHelper.toString(locations.get(0).Date, DateFormat.Complete, Locale.getDefault()) + " تا " + DateHelper.toString(locations.get(locations.size() - 1).Date, DateFormat.Complete, Locale.getDefault()), t.getMessage());
                removeSemaphore();
                Date date = new Date();
                for (LocationModel location :
                        locations) {
                    location.LastRetryTime = date;
                }
                try {
                    update(locations);
                } catch (Exception e) {
                    Timber.e(e);
                }
            }
        });
    }

    public synchronized void tryToSendItem(final LocationModel location) {
        tryToSendItem(location, TrackingApi.getDefaultServer(getContext()), true);
    }

    public synchronized void tryToSendItem(final LocationModel location, String baseUrl, boolean verifyLicense) {
        if (hasSemaphore())
            return;
        setSemaphore();
//        if (location.ActivityType == DetectedActivity.STILL && !location.IsImportant) {
//            removeSemaphore();
//            return;
//        }
        if (verifyLicense) {
            TrackingLicense license = TrackingLicense.readLicense(getContext());
            if (license == null) {
                removeSemaphore();
                return;
            }
            if (license.isExpired(getContext())) {
                removeSemaphore();
                return;
            }
        }

        if (!Connectivity.isConnected(getContext())) {
            TrackingLogManager.addLog(getContext(), LogType.SUBMIT_POINT, LogLevel.Error, "No Internet connection " + " نقطه ارسال نشد." + " تاریخ پوینت " + DateHelper.toString(location.Date, DateFormat.Complete, Locale.getDefault()));
            removeSemaphore();
            return;
        }
        final TrackingRequestModel trackingRequestModel = createTrackingRequestModel(location);
        TrackingApi trackingApi = new TrackingApi(getContext());
        trackingApi.runWebRequest(trackingApi.sendPoint(baseUrl, trackingRequestModel), new WebCallBack<Boolean>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(Boolean result, Request request) {
                Timber.i("LocationManager trackingApi onSuccess SendPoint2 " + request + "result = " +result);
                TrackingLogManager.addLog(getContext(), LogType.SUBMIT_POINT, LogLevel.Info, " نقطه ارسال شد ۱ ." + " تاریخ پوینت " + DateHelper.toString(location.Date, DateFormat.Complete, Locale.getDefault()));
                location.IsSend = true;
                location.LastRetryTime = new Date();
                try {
                    update(location);
                } catch (Exception e) {
                    Timber.i("LocationManager SendPoint2 Exception "+e.getMessage());
                    Timber.e(e);
                } finally {
                    removeSemaphore();
                }
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                TrackingLogManager.addLog(getContext(), LogType.SUBMIT_POINT, LogLevel.Error, " نقطه ارسال نشد." + " تاریخ پوینت " + DateHelper.toString(location.Date, DateFormat.Complete, Locale.getDefault()), error.getMessage());
                removeSemaphore();
                location.LastRetryTime = new Date();
                try {
                    update(location);
                } catch (Exception e) {
                    Timber.e(e);
                }
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                TrackingLogManager.addLog(getContext(), LogType.SUBMIT_POINT, LogLevel.Error, " نقطه ارسال نشد." + " تاریخ پوینت " + DateHelper.toString(location.Date, DateFormat.Complete, Locale.getDefault()), t.getMessage());
                removeSemaphore();
                location.LastRetryTime = new Date();
                try {
                    update(location);
                } catch (Exception e) {
                    Timber.e(e);
                }
            }
        });
    }

    private TrackingRequestModel createTrackingRequestModel(LocationModel location) {
        List<LocationModel> locationModels = new ArrayList<>(1);
        locationModels.add(location);
        return createTrackingRequestModel(locationModels);
    }

    private TrackingRequestModel createTrackingRequestModel(List<LocationModel> locations) {
        TrackingRequestModel trackingRequestModel = new TrackingRequestModel();
        trackingRequestModel.IMEI = TrackingLicense.getDeviceId(getContext());
        try {
            trackingRequestModel.BackOfficeType = new SysConfigManager(getContext()).getBackOfficeType().getName();
        } catch (Exception ignored) {

        }
        trackingRequestModel.ConsoleType = "ngt";
        trackingRequestModel.PersonnelDailyActivityVisitTypeId = VaranegarApplication.getInstance().getGrsAppId();
        for (LocationModel locationModel :
                locations) {
            if (locationModel.EventType == null || locationModel.EventType.isEmpty()) {
                BaseLocationViewModel locationViewModel = new BaseLocationViewModel();
                locationViewModel.setBaseInfo(locationModel);
                trackingRequestModel.pointEvent.add(locationViewModel);
            } else {
                Gson gson = VaranegarGsonBuilder.build().create();
                try {
                    JsonReader reader = new JsonReader(new StringReader(locationModel.Event));
                    reader.setLenient(true);
                    Object eventViewModel = gson.fromJson(reader, Class.forName(locationModel.EventType));
                    BaseEventLocationViewModel baseEventLocationViewModel = (BaseEventLocationViewModel) eventViewModel;
                    baseEventLocationViewModel.setBaseInfo(locationModel);
                    baseEventLocationViewModel.prepareForTracking(trackingRequestModel);
                } catch (Exception e) {
                    Timber.e(e);
                }
            }
        }
        return trackingRequestModel;
    }

    @Nullable
    public BaseLocationViewModel convert(@NonNull LocationModel locationModel) {
        if (locationModel.EventType == null || locationModel.EventType.isEmpty()) {
            BaseLocationViewModel locationViewModel = new BaseLocationViewModel();
            locationViewModel.setBaseInfo(locationModel);
            return locationViewModel;
        } else {
            Gson gson = VaranegarGsonBuilder.build().create();
            try {
                JsonReader reader = new JsonReader(new StringReader(locationModel.Event));
                reader.setLenient(true);
                Object eventViewModel = gson.fromJson(reader, Class.forName(locationModel.EventType));
                BaseEventLocationViewModel baseEventLocationViewModel = (BaseEventLocationViewModel) eventViewModel;
                baseEventLocationViewModel.setBaseInfo(locationModel);
                return baseEventLocationViewModel;
            } catch (Exception e) {
                Timber.e(e);
                return null;
            }
        }
    }

    @Nullable
    public <T extends BaseEventLocationViewModel> T convert(LocationModel locationModel, Class<T> clazz) {
        Gson gson = VaranegarGsonBuilder.build().create();
        try {
            JsonReader reader = new JsonReader(new StringReader(locationModel.Event));
            reader.setLenient(true);
            Object eventViewModel = gson.fromJson(reader, clazz);
            BaseEventLocationViewModel baseEventLocationViewModel = (BaseEventLocationViewModel) eventViewModel;
            baseEventLocationViewModel.setBaseInfo(locationModel);
            if (baseEventLocationViewModel != null)
                return (T) baseEventLocationViewModel;
            else
                return null;
        } catch (Exception e) {
            Timber.e(e);
            return null;
        }
    }

    private static final Date MIN_DATE = new Date(87, 3, 1);


    synchronized public void saveWait(boolean w) {
        SharedPreferences sharedpreferences = getContext().getSharedPreferences("LOCATION_MANAGER", Context.MODE_PRIVATE);
        if (isWait == null)
            isWait = sharedpreferences.getBoolean("isWait", false);
        if (w && !isWait) {
            waitTime = new Date().getTime();
            sharedpreferences.edit().putLong("wait_time", waitTime).apply();
        } else if (!w) {
            sharedpreferences.edit().remove("wait_time").apply();
            waitTime = null;
        }

        sharedpreferences.edit().putBoolean("isWait", w).apply();
        isWait = w;
    }

    @Nullable
    synchronized public Long getWaitTime() {
        SysConfigModel waitControl = new SysConfigManager(getContext()).read(ConfigKey.WaitingControl, SysConfigManager.cloud);
        if (!SysConfigManager.compare(waitControl, true))
            return null;
        if (waitTime == null) {
            SharedPreferences sharedpreferences = getContext().getSharedPreferences("LOCATION_MANAGER", Context.MODE_PRIVATE);
            waitTime = sharedpreferences.getLong("wait_time", new Date().getTime());
        }
        return waitTime;
    }

    synchronized public boolean isWait() {
        SysConfigModel waitControl = new SysConfigManager(getContext()).read(ConfigKey.WaitingControl, SysConfigManager.cloud);
        if (!SysConfigManager.compare(waitControl, true))
            return false;

        if (waitTime == null) {
            SharedPreferences sharedpreferences = getContext().getSharedPreferences("LOCATION_MANAGER", Context.MODE_PRIVATE);
            waitTime = sharedpreferences.getLong("wait_time", new Date().getTime());
        }
        if (isWait == null) {
            SharedPreferences sharedpreferences = getContext().getSharedPreferences("LOCATION_MANAGER", Context.MODE_PRIVATE);
            isWait = sharedpreferences.getBoolean("isWait", false);
        }
        return (new Date().getTime() - waitTime > (10 * 60 * 1000)) && isWait;
    }

    public void stopWait() {
        saveWait(false);
        TrackingLogManager.addLog(getContext(), LogType.END_WAIT, LogLevel.Info);
        LocationModel locationModel = getLastLocation(WaitLocationViewModel.class);
        if (locationModel != null) {
            final WaitLocationViewModel waitLocationViewModel = convert(locationModel, WaitLocationViewModel.class);
            if (waitLocationViewModel != null && waitLocationViewModel.eventData.EndTime == null) {
                waitLocationViewModel.eventData.EndTime = new Date();
                locationModel = updateTrackingPoint(waitLocationViewModel, locationModel);
                tryToSendItem(locationModel);
            }
        }
    }

    synchronized private void setSemaphore() {
        SharedPreferences sharedpreferences = getContext().getSharedPreferences("LOCATION_MANAGER", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putLong("SemaphoreTime", new Date().getTime());
        editor.apply();
    }

    synchronized public void removeSemaphore() {
        SharedPreferences sharedpreferences = getContext().getSharedPreferences("LOCATION_MANAGER", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove("SemaphoreTime");
        editor.apply();
    }

    synchronized private boolean hasSemaphore() {
        SharedPreferences sharedpreferences = getContext().getSharedPreferences("LOCATION_MANAGER", Context.MODE_PRIVATE);
        long time = sharedpreferences.getLong("SemaphoreTime", MIN_DATE.getTime());
        if (time == MIN_DATE.getTime())
            return false;
        else if (time + (10 * 60 * 1000) < new Date().getTime()) {
            removeSemaphore();
            return false;
        } else
            return true;
    }

    public long clearPoints() throws DbException {
        long time = new Date().getTime() - 1209600000;
        return delete(Criteria.lesserThan(Location.Date, new Date(time)));
    }

    private static Boolean gpsIsOn = null;
    private static Long lastOn;
    private static Long lastOff;

    public static void checkGpsProvider(final Context context) {
        if (TrackingLicense.getLicensePolicy(context) == 1)
            return;
        final android.location.LocationManager manager = (android.location.LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (manager != null) {
            if (manager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
                if (gpsIsOn == null || !gpsIsOn) {
                    long now = new Date().getTime();
                    if (lastOn != null && now - lastOn < 10000)
                        return;
                    lastOn = new Date().getTime();
                    final GpsProviderOnLocationViewModel gpsProviderOnLocationViewModel = new GpsProviderOnLocationViewModel();
                    gpsProviderOnLocationViewModel.eventData = new DeviceEventViewModel();
                    gpsProviderOnLocationViewModel.eventData.Time = DateHelper.toString(new GregorianCalendar(), DateFormat.MicrosoftDateTime);
                    gpsProviderOnLocationViewModel.eventData.EndTime = new Date();

                    TourModel tourModel = new TourManager(context).loadTour();
                    if (tourModel != null) {
                        LocationModel locationModel = new LocationManager(context).getLastLocation(GpsProviderOffLocationViewModel.class);
                        if (locationModel != null && locationModel.Date != null) {
                            if (tourModel.StartTime.getTime() > locationModel.Date.getTime())
                                gpsProviderOnLocationViewModel.eventData.StartTime = tourModel.StartTime;
                            else
                                gpsProviderOnLocationViewModel.eventData.StartTime = locationModel.Date;
                        }
                    }

                    gpsIsOn = true;
                    Timber.d("GPS is on");
                    JobScheduler.resetJob(TrackingServiceJob.class, context);
                    TrackingLogManager.addLog(context, LogType.GPS_ON, LogLevel.Info);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            final LocationManager locationManager = new LocationManager(context);
                            locationManager.addTrackingPoint(gpsProviderOnLocationViewModel, new OnSaveLocation() {
                                @Override
                                public void onSaved(LocationModel location) {
                                    locationManager.tryToSendItem(location);
                                }

                                @Override
                                public void onFailed(String error) {
                                    Timber.e(error);
                                }
                            });
                        }
                    }, 10000);
                }
            } else {
                if (gpsIsOn == null || gpsIsOn) {
                    long now = new Date().getTime();
                    if (lastOff != null && now - lastOff < 10000)
                        return;
                    lastOff = new Date().getTime();
                    final GpsProviderOffLocationViewModel gpsProviderOffLocationViewModel = new GpsProviderOffLocationViewModel();
                    gpsProviderOffLocationViewModel.eventData = new DeviceEventViewModel();
                    gpsProviderOffLocationViewModel.eventData.Time = DateHelper.toString(new GregorianCalendar(), DateFormat.MicrosoftDateTime);
                    gpsIsOn = false;
                    Timber.d("GPS is off");
                    TrackingLogManager.addLog(context, LogType.GPS_OFF, LogLevel.Info);
                    final LocationManager locationManager = new LocationManager(context);
                    locationManager.addTrackingPoint(gpsProviderOffLocationViewModel, new OnSaveLocation() {
                        @Override
                        public void onSaved(LocationModel location) {
                            locationManager.tryToSendItem(location);
                        }

                        @Override
                        public void onFailed(String error) {
                            Timber.e("Could not get location because gps is off. we use the latest point from database");
                        }
                    });

                }
            }
        }
    }
}
