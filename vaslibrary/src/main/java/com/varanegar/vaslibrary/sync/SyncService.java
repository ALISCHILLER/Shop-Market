package com.varanegar.vaslibrary.sync;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;

import com.varanegar.framework.database.DbException;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.util.jobscheduler.JobScheduler;
import com.varanegar.vaslibrary.base.BackupManager;
import com.varanegar.vaslibrary.jobscheduler.TrackingServiceJob;
import com.varanegar.vaslibrary.jobscheduler.VasJobScheduler;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.locationmanager.LocationManager;
import com.varanegar.vaslibrary.manager.locationmanager.LogLevel;
import com.varanegar.vaslibrary.manager.locationmanager.LogType;
import com.varanegar.vaslibrary.manager.locationmanager.OnSaveLocation;
import com.varanegar.vaslibrary.manager.locationmanager.TrackingLicense;
import com.varanegar.vaslibrary.manager.locationmanager.TrackingLogManager;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.DeviceReportLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.DeviceReportViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.StartTourEventViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.StartTourLocationViewModel;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.OwnerKeysWrapper;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.manager.updatemanager.TourUpdateFlow;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.messaging.VasInstanceIdService;
import com.varanegar.vaslibrary.model.UpdateKey;
import com.varanegar.vaslibrary.model.location.LocationModel;
import com.varanegar.vaslibrary.model.tour.TourModel;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.webapi.tracking.TrackingApi;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by A.Torabi on 1/24/2018.
 */

public abstract class SyncService extends Service {
    final TourManager tourManager = new TourManager(SyncService.this);
    private boolean confirmSent;
    private TourUpdateFlow tourUpdateFlow;

    protected abstract TourUpdateFlow getTourUpdateFlow(Context context);


    public void startTourDownload() {
        confirmSent = false;
        tourUpdateFlow = getTourUpdateFlow(this);
        tourUpdateFlow.getTourAndInitPromotionDb(new UpdateCall() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess() {
                if (tourManager.isTourDownloading()) {
                    Timber.d("Trying to confirm tour started!");
                    confirmSent = true;
                    tourManager.confirmTourSend(new TourManager.TourCallBack() {
                        @Override
                        public void onSuccess() {
                            Timber.d("Tour confirmed!");
                            try {
                                tourManager.saveConfirm();
                            } catch (Throwable e) {
                                Timber.e(e);
                            } finally {
                                confirmSent = false;
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            VasJobScheduler.registerVasJobs(SyncService.this);
                                            UpdateManager updateManager = new UpdateManager(SyncService.this);
                                            updateManager.addLog(UpdateKey.TourStartTime);
                                            SysConfigManager sysConfigManager = new SysConfigManager(SyncService.this);
                                            try {
                                                sysConfigManager.delete(ConfigKey.IgnoreOldInvoice);
                                            } catch (DbException e) {
                                                Timber.e(e);
                                            }
                                            BackupManager.exportData(SyncService.this, true);
                                            TourModel tourModel = tourManager.loadTour();
                                            final UserModel userModel = UserManager.readFromFile(SyncService.this);
                                            StartTourLocationViewModel locationViewModel = new StartTourLocationViewModel();
                                            locationViewModel.eventData = new StartTourEventViewModel();
                                            locationViewModel.eventData.PersonnelId = userModel.UniqueId;
                                            locationViewModel.eventData.StartTime = DateHelper.toString(new Date(), DateFormat.MicrosoftDateTime, Locale.US);
                                            locationViewModel.eventData.TourId = tourModel.UniqueId;
                                            locationViewModel.eventData.TourNo = tourModel.TourNo;
                                            final LocationManager locationManager = new LocationManager(SyncService.this);
                                            TrackingLogManager.addLog(SyncService.this, LogType.EVENT, LogLevel.Info, "دریافت تور");
                                            locationManager.addTrackingPoint(locationViewModel, new OnSaveLocation() {
                                                @Override
                                                public void onSaved(LocationModel location) {
                                                    locationManager.tryToSendItem(location);
                                                }

                                                @Override
                                                public void onFailed(String error) {
                                                    Timber.e(error);
                                                }
                                            });


                                            DeviceReportLocationViewModel reportLocationViewModel = new DeviceReportLocationViewModel();
                                            reportLocationViewModel.eventData = new DeviceReportViewModel();
                                            reportLocationViewModel.eventData.Time = DateHelper.toString(new GregorianCalendar(), DateFormat.MicrosoftDateTime);
                                            reportLocationViewModel.eventData.ApkName = getApplicationInfo().packageName;
                                            reportLocationViewModel.eventData.ApkVersion = getPackageManager().getPackageInfo(getApplicationInfo().packageName, 0).versionName;
                                            reportLocationViewModel.eventData.ConsoleType = "ngt";
                                            try {
                                                reportLocationViewModel.eventData.BackOfficeType = sysConfigManager.getBackOfficeType().getName();
                                            } catch (Exception ignored) {

                                            }
                                            try {
                                                OwnerKeysWrapper ownerKeysWrapper = sysConfigManager.readOwnerKeys();
                                                reportLocationViewModel.eventData.DataOwnerCenterKey = ownerKeysWrapper.DataOwnerCenterKey;
                                                reportLocationViewModel.eventData.DataOwnerKey = ownerKeysWrapper.DataOwnerKey;
                                                reportLocationViewModel.eventData.OwnerKey = ownerKeysWrapper.OwnerKey;
                                            } catch (Exception ignored) {

                                            }
                                            reportLocationViewModel.eventData.DeviceApi = Build.VERSION.SDK_INT;
                                            reportLocationViewModel.eventData.DeviceVersionName = Build.VERSION.RELEASE;
                                            reportLocationViewModel.eventData.DeviceManufacturer = Build.MANUFACTURER;
                                            reportLocationViewModel.eventData.DeviceModel = Build.MODEL;
                                            reportLocationViewModel.eventData.HasTracking = TrackingLicense.readLicense(SyncService.this) != null;
                                            reportLocationViewModel.eventData.PersonnelId = userModel.UniqueId;
                                            reportLocationViewModel.eventData.PersonnelName = userModel.UserName;
                                            reportLocationViewModel.eventData.TourId = tourModel.UniqueId;
                                            reportLocationViewModel.eventData.TourNo = tourModel.TourNo;
                                            reportLocationViewModel.eventData.FirebaseToken = VasInstanceIdService.getToken(SyncService.this);
                                            locationManager.addTrackingPoint(reportLocationViewModel, new OnSaveLocation() {
                                                @Override
                                                public void onSaved(LocationModel location) {
                                                    locationManager.tryToSendItem(location, TrackingApi.getDefaultServer(SyncService.this), false);
                                                }

                                                @Override
                                                public void onFailed(String error) {
                                                    Timber.e(error);
                                                }
                                            });
                                        } catch (Throwable e) {
                                            Timber.e(e);
                                        }
                                    }
                                }).start();
                            }
                        }

                        @Override
                        public void onFailure(String error) {
                            Timber.e("Sending tour confirm failed. " + error);
                            tourManager.saveFailure(error);
                        }

                        @Override
                        public void onProgressChanged(String progress) {

                        }
                    });
                } else {
                    Timber.e("Receiving tour canceled");
                    tourManager.saveFailure("tour canceled");
                }
            }

            @Override
            protected void onFailure(String error) {
                Timber.e("Receiving tour failed. " + error);
                tourManager.saveFailure(error);
            }
        });
    }

    @Override
    public void onDestroy() {
        Timber.e("Sync service destroyed");
        confirmSent = false;
    }

    public void stopTourDownload() {
        if (!confirmSent && tourManager.isTourDownloading())
            tourUpdateFlow.stop();
    }

    public boolean isStopping() {
        return tourUpdateFlow.isStopping();
    }

    public class ServiceBinder extends Binder {
        public SyncService getService() {
            return SyncService.this;
        }
    }

    ServiceBinder serviceBinder = new ServiceBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return serviceBinder;
    }
}
