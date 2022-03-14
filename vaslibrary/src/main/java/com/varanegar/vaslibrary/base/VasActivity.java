package com.varanegar.vaslibrary.base;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarActivity;
import com.varanegar.framework.base.logging.LogConfig;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.util.LocaleHelper;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.broadcasts.ConnectivityReceiver;
import com.varanegar.vaslibrary.broadcasts.GpsReceiver;
import com.varanegar.vaslibrary.broadcasts.PowerReceiver;
import com.varanegar.vaslibrary.broadcasts.TEPBroadCast;
import com.varanegar.vaslibrary.manager.ProductManager;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.customer.CustomerBarcodeManager;
import com.varanegar.vaslibrary.manager.locationmanager.LogLevel;
import com.varanegar.vaslibrary.manager.locationmanager.LogType;
import com.varanegar.vaslibrary.manager.locationmanager.TrackingLicense;
import com.varanegar.vaslibrary.manager.locationmanager.TrackingLogManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.messaging.VasInstanceIdService;
import com.varanegar.vaslibrary.model.product.Product;
import com.varanegar.vaslibrary.model.product.ProductModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.print.PrinterManager;
import com.varanegar.vaslibrary.ui.fragment.settlement.PdaDeviceCardReader.EasyHelper;
import com.varanegar.vaslibrary.ui.fragment.settlement.PdaDeviceCardReader.I9000SCardReader;
import com.varanegar.vaslibrary.ui.fragment.settlement.PdaDeviceCardReader.A910CardReader;
import com.varanegar.vaslibrary.ui.fragment.settlement.PdaDeviceCardReader.N910CardReader;
import com.varanegar.vaslibrary.ui.fragment.settlement.PdaDeviceCardReader.SamanKishCardReader;
import com.varanegar.vaslibrary.ui.fragment.settlement.PdaDeviceCardReader.SepehrCardReader;
import com.varanegar.vaslibrary.ui.fragment.settlement.PdaDeviceCardReader.TejaratElectronicParsianCardReader;
import com.varanegar.vaslibrary.webapi.timezone.TimeApi;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import timber.log.Timber;
import varanegar.com.discountcalculatorlib.utility.GlobalVariables;

import static ir.ikccc.externalpayment.Library.REQUEST_CODE;


/**
 * Created by A.Jafarzadeh on 11/8/2017.
 */

public abstract class VasActivity extends MainVaranegarActivity
        implements EasyHelper.Receiver {

    private GoogleApiClient client;
    private static final int GPS_SETTiNGS_REQUEST_CODE = 9234;
    private TEPBroadCast tepBroadCast;
    private static final int TIME_SETTiNGS_REQUEST_CODE = 9834;

    @Nullable
    public Location getLastLocation() {
        return lastLocation;
    }

    private Location lastLocation = null;


    private Boolean isLowMemory() {
        try {
            ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);
            return memoryInfo.lowMemory;
        } catch (Error e) {
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Locale locale = getLanguage(this);
        LocaleHelper.setLocale(this, locale.getLanguage());


//        ProductManager productManager = new ProductManager(this);
//        List<ProductModel> productModels = productManager.getItems(new Query().from(Product.ProductTbl));
//        for (ProductModel product :
//                productModels) {
//            product.IsForSale = true;
//        }
//        try {
//            productManager.update(productModels);
//        } catch (ValidationException e) {
//            e.printStackTrace();
//        } catch (DbException e) {
//            e.printStackTrace();
//        }

        setAlarmForWaitEnd();
        final com.varanegar.vaslibrary.manager.locationmanager.LocationManager locationManager = new
                com.varanegar.vaslibrary.manager.locationmanager.LocationManager(this);
        locationManager.removeSemaphore();

        // just for ahad
        if (savedInstanceState == null) {
            if (!isLowMemory()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            File file = BackupManager.getLast(VasActivity.this);
                            if (file == null || file.lastModified() < (new Date().getTime() - (8 * 1000 * 3600))) {
                                BackupManager.exportData(VasActivity.this, true);
                            }

                        } catch (Exception e) {
                            Timber.e(e);
                        }
                        BackupManager.wipeOldDataBaseOnQty(VasActivity.this);
                    }
                }).start();
            }
        }

        if (Build.MODEL.equals("A930")) {
            Handler receiverHandler = new Handler();
            try {
                EasyHelper.getStatus(EasyHelper.GET_STATUS, receiverHandler, this);
            } catch (EasyHelper.PahpatException e) {
                e.printStackTrace();
                Timber.e(e);
            }
        }

//        if (UserManager.readFromFile(this) != null)
//            VasInstanceIdService.refreshToken(this,
//            new VasInstanceIdService.TokenRefreshCallBack() {
//                @Override
//                public void onSuccess(@NonNull String token) {
//                    Timber.d("Token update succeeded. Token = " + token);
//                }
//
//                @Override
//                public void onFailure(@Nullable String token, String error) {
//                    Timber.d("Token update failed. Error=" + error + "  Token=" + token);
//                }
//            });

        checkLocationSettings();
        LocationManager locationManager1 = ((LocationManager) getSystemService(LOCATION_SERVICE));
        if (locationManager1 != null) {
            boolean isOn = locationManager1.isProviderEnabled(LocationManager.GPS_PROVIDER);
            Boolean isOnFromPoints = new com.varanegar.vaslibrary.manager.locationmanager.LocationManager(this).getGpsStatus(new Date().getTime());
            if (isOnFromPoints != null && isOnFromPoints != isOn) {
                com.varanegar.vaslibrary.manager.locationmanager.LocationManager.checkGpsProvider(this);
            }
        }
        if (Build.MODEL.equals("P1000")) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.pec.ThirdCompany");
            tepBroadCast = new TEPBroadCast();
            tepBroadCast.setOnReceivedListener(new TEPBroadCast.OnReceivedListener() {
                @Override
                public void onReceived(Intent intent) {
                    tejaratElectronicParsianCardReaderListener.onReceiveResult(VasActivity.this,
                            0, 0, intent, null);
                }
            });
            registerReceiver(tepBroadCast, intentFilter);
        }
    }

    private void setAlarmForWaitEnd() {
        TrackingLicense trackingLicense = TrackingLicense.readLicense(this);
        if (trackingLicense != null && TrackingLicense.isValid(this)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, trackingLicense.getEndWorkingHour(this));
            calendar.set(Calendar.MINUTE, 00);
            Intent intent = new Intent(this, WaitAlarmReceiver.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
            AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, alarmIntent);
        }
    }

    private void checkLocationSettings() {
        if (TrackingLicense.getLicensePolicy(this) == 1 || !TrackingLicense.isValid(this) || !SysConfigManager.hasTracking(this))
            return;
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int result = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (result == ConnectionResult.SUCCESS) {
            TrackingLogManager.addLog(this, LogType.LOCATION_SETTINGS, LogLevel.Info, "Google api client is available");
            LocationRequest locationRequest = com.varanegar.vaslibrary.manager.locationmanager.LocationManager.getLocationRequest(this);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
            SettingsClient settingsClient = LocationServices.getSettingsClient(this);
            settingsClient.checkLocationSettings(builder.build()).addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    TrackingLogManager.addLog(VasActivity.this, LogType.LOCATION_SETTINGS, LogLevel.Info, "Location settings satisfied");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    TrackingLogManager.addLog(VasActivity.this, LogType.LOCATION_SETTINGS, LogLevel.Error, "Location settings not satisfied");
                    if (e instanceof ResolvableApiException) {
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(VasActivity.this, GPS_SETTiNGS_REQUEST_CODE);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                    }
                }
            });
        } else {
            CuteMessageDialog errorDialog = new CuteMessageDialog(this);
            errorDialog.setIcon(Icon.Error);
            errorDialog.setTitle(R.string.error);
            if (result == ConnectionResult.API_UNAVAILABLE) {
                TrackingLogManager.addLog(this, LogType.LOCATION_SETTINGS, LogLevel.Error, "Google api client is not available");
                errorDialog.setMessage(R.string.google_api_client_is_not_available);
            } else if (result == ConnectionResult.SERVICE_DISABLED) {
                TrackingLogManager.addLog(this, LogType.LOCATION_SETTINGS, LogLevel.Error, "Google api client is disabled");
                errorDialog.setMessage(R.string.google_api_client_is_disabled);
            } else if (result == ConnectionResult.SERVICE_MISSING) {
                TrackingLogManager.addLog(this, LogType.LOCATION_SETTINGS, LogLevel.Error, "Google api client is not installed");
                errorDialog.setMessage(R.string.google_api_client_is_not_installed);
            } else if (result == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {
                TrackingLogManager.addLog(this, LogType.LOCATION_SETTINGS, LogLevel.Error, "Google api client version is old. update is needed");
                errorDialog.setMessage(R.string.google_api_client_is_not_updated);
            } else {
                TrackingLogManager.addLog(this, LogType.LOCATION_SETTINGS, LogLevel.Error, "Google api client error code = " + result);
                errorDialog.setMessage(R.string.google_api_client_error_occured);
            }
            errorDialog.setPositiveButton(R.string.ok, null);
            errorDialog.show();
        }

    }

    @Override
    protected void attachBaseContext(Context base) {
        Locale locale = getLanguage(base);
        Context context = LocaleHelper.setLocale(base, locale.getLanguage());
        super.attachBaseContext(ViewPumpContextWrapper.wrap(context));
        if (locale.getLanguage().equals("fa")) {
            ViewPump.init(ViewPump.builder()
                    .addInterceptor(new CalligraphyInterceptor(
                            new CalligraphyConfig.Builder()
                                    .setDefaultFontPath("fonts/anatoli.ttf")
                                    .build()))
                    .build());
        }
    }

    public Locale getLanguage(Context context) {
        Locale preferredLocale = LocaleHelper.getPreferredLocal(context);
        if (preferredLocale != null)
            return preferredLocale;
        SysConfigManager sysConfigManager = new SysConfigManager(context);
        SysConfigModel languageConfig = sysConfigManager.read(ConfigKey.ServerLanguage, SysConfigManager.cloud);
        UUID languageId = SysConfigManager.getUUIDValue(languageConfig);
        if (languageId == null)
            return Locale.getDefault();
        Locale locale = LocalModel.getLocal(languageId);
        if (locale != null)
            return locale;
        return Locale.getDefault();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Wifi on and off BroadCast
        BroadcastReceiver wifiReceiver = new ConnectivityReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        this.registerReceiver(wifiReceiver, filter);

        // GPS on and off Broadcast
        BroadcastReceiver gpsReceiver = new GpsReceiver();
        IntentFilter filter1 = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        this.registerReceiver(gpsReceiver, filter1);

        // Power Broadcast
        BroadcastReceiver powerReceiver = new PowerReceiver();
        IntentFilter filter2 = new IntentFilter(Intent.ACTION_BATTERY_LOW);
        this.registerReceiver(powerReceiver, filter2);

        // Updating printers list when connected
        PrinterManager printerManager = new PrinterManager(this);
        printerManager.registerScanner();
        printerManager.startScanner();

        this.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                VaranegarActivity activity = VasActivity.this;
                final android.location.LocationManager manager = (android.location.LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                if (manager != null) {
                    if (manager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER))
                        startLocationUpdate();
                    else
                        checkLocationSettings();
                }
            }
        }, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Build.MODEL.equals("P1000")) {
            unregisterReceiver(tepBroadCast);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdate();
        checkTime();
    }

    private void checkTime() {
        if (TrackingLicense.getLicensePolicy(this) == 1 || !TrackingLicense.isValid(this) || !SysConfigManager.hasTracking(this))
            return;
        TimeApi timeApi = new TimeApi(VasActivity.this);
        timeApi.checkTime(this::changeTimeSettings);
    }

    private void changeTimeSettings(String log) {
        Timber.e(log);
        CuteMessageDialog dialog = new CuteMessageDialog(this);
        dialog.setIcon(Icon.Error);
        dialog.setMessage(R.string.time_settings_is_wrong);
        dialog.setTitle(R.string.error);
        dialog.setPositiveButton(R.string.settings, v -> startActivityForResult(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS), TIME_SETTiNGS_REQUEST_CODE));
        dialog.setNegativeButton(R.string.close, v -> checkTime());
        dialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (client != null && client.isConnected())
            client.disconnect();
    }

    void startLocationUpdate() {
        client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        if (!VasActivity.this.isFinishing()) {
                            try {
                                LocationRequest request = LocationRequest.create();
                                request.setInterval(5000);
                                request.setSmallestDisplacement(5);
                                if (client.isConnected())
                                    LocationServices.FusedLocationApi.requestLocationUpdates(client, request, new LocationListener() {
                                        @Override
                                        public void onLocationChanged(Location location) {
                                            if (location == null)
                                                return;
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                                if (!location.isFromMockProvider())
                                                    lastLocation = location;
                                                else {
                                                    lastLocation = null;
                                                    Timber.e("Mock location received! latitude = " + location.getLatitude() + " longitude = " + location.getLongitude());
                                                }
                                            } else
                                                lastLocation = location;
                                        }
                                    });
                            } catch (SecurityException ex) {
                                Timber.e(ex);
                            } catch (Exception ex) {
                                Timber.e(ex);
                            }
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Timber.d("location connection suspended");
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Timber.e(connectionResult.getErrorMessage());
                    }
                })
                .build();
        client.connect();

    }

    @Override
    protected LogConfig createLogConfig() {
        try {
            return VasApplication.createLogConfig(this);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected boolean checkStoragePermission() {
        return true;
    }

    @Override
    protected boolean checkCameraPermission() {
        return true;
    }

    @Override
    protected boolean checkLocationPermission() {
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Timber.i("onSaveInstanceState");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPS_SETTiNGS_REQUEST_CODE)
            checkLocationSettings();
        if (requestCode == I9000SCardReader.i9000S_requestCode && i9000sCardReaderListener != null)
            i9000sCardReaderListener.onReceiveResult(this, requestCode, resultCode, data, null);
        else if (requestCode == CustomerBarcodeManager.barcodeRequestCode && customerBarcodeManagerListener != null)
            customerBarcodeManagerListener.onReceiveResult(this, requestCode, resultCode, data, null);
        else if (requestCode == REQUEST_CODE && a910CardReaderListener != null)
            a910CardReaderListener.onReceiveResult(this, requestCode, resultCode, data, null);
        else if (requestCode == N910CardReader.requestCode && n910CardReader != null)
            n910CardReader.onReceiveResult(this, requestCode, resultCode, data, null);
        else if (requestCode == SepehrCardReader.requestCode)
            sepehrCardReader.onReceiveResult(this, requestCode, resultCode, data, null);
        else if (requestCode == TIME_SETTiNGS_REQUEST_CODE)
            checkTime();
    }

    //
    public TejaratElectronicParsianCardReader tejaratElectronicParsianCardReaderListener;
    public I9000SCardReader i9000sCardReaderListener;
    public CustomerBarcodeManager customerBarcodeManagerListener;
    public A910CardReader a910CardReaderListener;
    public SepehrCardReader sepehrCardReader;
    public N910CardReader n910CardReader;

    //Pahpat SamanKish
    @Override
    public void onReceiveResult(int serviceId, int resultCode, Bundle resultData) {
        if (serviceId == EasyHelper.GET_STATUS) {
            if (resultCode == 1 && resultData != null) {
                boolean result = resultData.getBoolean("Result");
                if (result) {
                    GlobalVariables.setPahpatStatus(true);
                }
            }
        } else if (samanKishCardReader != null)
            samanKishCardReader.onReceiveResult(this, serviceId, resultCode, null, resultData);
    }

    public SamanKishCardReader samanKishCardReader;
}
