package com.varanegar.vaslibrary.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.emrekose.recordbutton.OnRecordListener;
import com.emrekose.recordbutton.RecordButton;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.varanegar.framework.base.AppVersionInfo;
import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.PopupFragment;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.network.Connectivity;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.component.PairedItems;
import com.varanegar.framework.util.component.ProgressView;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.util.recycler.DividerItemDecoration;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.BackupManager;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.customeractiontimemanager.CustomerActionTimeManager;
import com.varanegar.vaslibrary.manager.image.ImageType;
import com.varanegar.vaslibrary.manager.productorderviewmanager.ProductOrderViewManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.manager.updatemanager.TourUpdateLogManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateQueue;
import com.varanegar.vaslibrary.model.UpdateKey;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.model.tour.TourModel;
import com.varanegar.vaslibrary.model.update.TourUpdateLog;
import com.varanegar.vaslibrary.model.update.TourUpdateLogModel;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.print.SentTourInfoPrint.SentTourInfoPrintHelper;
import com.varanegar.vaslibrary.print.SentTourInfoPrint.TourInfo;
import com.varanegar.vaslibrary.sync.SyncService;
import com.varanegar.vaslibrary.ui.dialog.ConnectionSettingDialog;
import com.varanegar.vaslibrary.ui.dialog.ImportDialogFragment;
import com.varanegar.vaslibrary.ui.dialog.InsertPinDialog;
import com.varanegar.vaslibrary.ui.dialog.InsertTourNoSendRest;
import com.varanegar.vaslibrary.ui.dialog.TrackingLicenseFragment;
import com.varanegar.vaslibrary.ui.dialog.VirtualTourDialog;
import com.varanegar.vaslibrary.webapi.appversion.ApkDownloadCallBack;
import com.varanegar.vaslibrary.webapi.appversion.AppVersionApi;
import com.varanegar.vaslibrary.webapi.appversion.VersionApiCallBack;
import com.varanegar.vaslibrary.webapi.ping.PingApi;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by atp on 3/11/2017.
 *
 * صفحه دریافت تور
 */

public abstract class TourReportFragment extends PopupFragment implements VirtualTourDialog.tourNoListener {
    private UserModel userModel;
    private FragmentActivity activity;
    private ProgressBar totalProgressBar;
    private TourManager tourManager;
    private ImageView getTourImageView;
    private ImageView backupImageView;
    private ImageView downloadApk;
    private ImageView refreshtour;
    private ImageView logoutImageView;
    private ImageView trackingLicenseImageView;
    private RecordButton cancelTourRecordBtn;
    private List<TourUpdateLogGroup> logGroups = new ArrayList<>();
    private RefreshUiTask refreshUiTask;
    private CallLogRecyclerAdapter adapter;
    private View reportLayout;
    private RecyclerView logsRecyclerView;
    private View tourProgressLayout;
    private boolean isCanceled;


    private View view;
    private PingApi pingApi;
    private AppVersionApi appVersionApi;

    public static final String IS_VIRTUAL = "IS_VIRTUAL";
    public static final String IS_VIRTUAL_BOOLEAN = "IS_VIRTUAL";

    protected abstract CustomersFragment getCustomersFragment();

    protected abstract Class<? extends SyncService> getSyncService();

    private Thread timerThread;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = getActivity();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupPage() {
        setRetainInstance(true);
        try {
            String packageName = getContext().getApplicationInfo().packageName;
            String version = getContext().getPackageManager().getPackageInfo(packageName, 0).versionName;
            ((TextView) view.findViewById(R.id.version_text_view)).setText(version);
        } catch (Exception e) {
            Timber.e(e);
        }
        View userNameTextView = view.findViewById(R.id.user_name_text_view);
        ((TextView) userNameTextView).setText(userModel.UserName);

        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        SysConfigModel serverAddressConfig = sysConfigManager.read(ConfigKey.ValidServerAddress, SysConfigManager.local);
        if (serverAddressConfig != null)
            ((TextView) view.findViewById(R.id.ip_text_view)).setText(serverAddressConfig.Value);
        SysConfigModel localServerAddressConfig = sysConfigManager.read(ConfigKey.LocalServerAddress, SysConfigManager.local);
        if (localServerAddressConfig != null)
            ((TextView) view.findViewById(R.id.local_ip_text_view)).setText(localServerAddressConfig.Value);


        backupImageView.setOnLongClickListener(view -> {

            if (BackupManager.getList(getContext(), BackupManager.BackupType.Partial).size() > 0) {
                ImportDialogFragment importDialog = new ImportDialogFragment();
                importDialog.setBackupType(BackupManager.BackupType.Partial);
                importDialog.show(getChildFragmentManager(), "ImportDialogFragment");
            } else {
                CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                dialog.setTitle(R.string.error);
                dialog.setMessage(R.string.there_is_no_backup_file);
                dialog.setIcon(Icon.Alert);
                dialog.setPositiveButton(R.string.ok, null);
                dialog.show();
            }

            return true;
        });

        backupImageView.setOnClickListener(v -> {
            final BackupConfigAlertDialog alertDialog = new BackupConfigAlertDialog();
            alertDialog.onBackupConfig = new BackupConfigAlertDialog.OnBackupConfig() {
                @Override
                public void done(List<ImageType> imageTypes) {
                    alertDialog.dismiss();
                    runBackup(imageTypes);
                }
            };
            alertDialog.show(getChildFragmentManager(), "BackupConfigAlertDialog");
        });


        logoutImageView.setOnClickListener(view -> {
            if (!tourManager.isTourAvailable()) {
                CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                dialog.setIcon(Icon.Alert);
                dialog.setNegativeButton(R.string.no, null);
                dialog.setPositiveButton(R.string.yes, v -> {
                    UserManager.logout(getVaranegarActvity());
                    tourManager.deleteTourInfoFile();
                });
                dialog.setTitle(R.string.are_you_sure);
                dialog.show();
            } else {
                CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                dialog.setTitle(R.string.there_is_a_tour_already);
                dialog.setMessage(R.string.please_send_tour_or_cancel_it);
                dialog.setPositiveButton(R.string.ok, null);
                dialog.setIcon(Icon.Alert);
                dialog.show();
            }
        });


        trackingLicenseImageView.setOnClickListener(v -> {
            MainVaranegarActivity activity = getVaranegarActvity();
            if (activity != null && !activity.isFinishing() && isResumed()) {
                TrackingLicenseFragment fragment = new TrackingLicenseFragment();
                activity.pushFragment(fragment);
            }
        });

        final long[] then = {0};

        getTourImageView.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                then[0] = System.currentTimeMillis();
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if((System.currentTimeMillis() - then[0]) > 4444){
                    VirtualTourDialog dialog = new VirtualTourDialog();
                    dialog.setTargetFragment(TourReportFragment.this, 0);
                    dialog.show(getActivity().getSupportFragmentManager(), "VirtualTourDialog");
                    return true;
                }
            }
            return false;
        });

        /**
         * دانلود نسخه جدید
         */
        downloadApk.setOnClickListener(view -> {
            DownloadApk();
        });
        /**
         * سناریو ریست
         */
        refreshtour.setOnClickListener(view -> {
            InsertTourNoSendRest dialog = new InsertTourNoSendRest();
            dialog.setCancelable(false);
            dialog.setClosable(false);

            dialog.show(getChildFragmentManager(), "7b0b529b-3f9b-4399-a2c7-2a85bcf57c1c");
        });

        getTourImageView.setOnClickListener(view -> {
            SharedPreferences.Editor editor = getContext().getSharedPreferences(IS_VIRTUAL, MODE_PRIVATE).edit();
            editor.putBoolean(IS_VIRTUAL_BOOLEAN, false);
            editor.apply();
            getTour(null);
        });


        cancelTourRecordBtn.setRecordListener(new OnRecordListener() {
            @Override
            public void onRecord() {

            }

            @Override
            public void onRecordCancel() {

            }

            @Override
            public void onRecordFinish() {
                Timber.e("Cancel tour clicked");
                if (tourManager.isStopping()) {
                    CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                    dialog.setMessage(getString(R.string.canceling_request));
                    dialog.setTitle(R.string.please_wait);
                    dialog.setIcon(Icon.Alert);
                    dialog.setPositiveButton(R.string.ok, null);
                    dialog.show();
                } else if (tourManager.isTourDownloading()) {
                    isCanceled = true;
                    tourManager.stopDownload();
                }
            }
        });



        if (!tourManager.isTourDownloading()) {
            reportLayout.setVisibility(View.VISIBLE);
            createTourInfoView();
        } else
            reportLayout.setVisibility(View.GONE);

        refreshTourProgress();
    }

    private void getTour(final String tourNo) {
        getTourImageView.setEnabled(false);
        Timber.e("Get Tour Clicked!!");
        if (!tourManager.isTourAvailable()) {
            final SharedPreferences sharedPreferences = getContext().getSharedPreferences("InStockProducts", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if(VaranegarApplication.is(VaranegarApplication.AppId.HotSales)){
                editor.putBoolean("InStock", true);

            }else{
                editor.putBoolean("InStock", false);
            }
            editor.apply();
            SharedPreferences sharedPreferencesCustomerStatuse = getActivity().getSharedPreferences("customerStatusShared", MODE_PRIVATE);
            sharedPreferencesCustomerStatuse.edit().clear().apply();
            SharedPreferences sharedPreferencesUsanceDay = getActivity().getSharedPreferences("UsanceDaySharedPrefences", MODE_PRIVATE);
            sharedPreferencesUsanceDay.edit().clear().apply();
            if (!Connectivity.isConnected(getContext())) {
                ConnectionSettingDialog dialog = new ConnectionSettingDialog();
                dialog.show(getChildFragmentManager(), "ConnectionSettingDialog");
                getTourImageView.setEnabled(true);
            } else {
                final CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                dialog.setIcon(Icon.Alert);
                dialog.setMessage(R.string.do_you_update_old_invoices_in_last);
                dialog.setPositiveButton(R.string.yes, view -> saveGetOldInvoiceType("True", tourNo));
                dialog.setNegativeButton(R.string.no, view -> saveGetOldInvoiceType("False", tourNo));
                dialog.show();
                getTourImageView.setEnabled(false);
            }
        } else {
            if (isResumed()) {
                getTourImageView.setEnabled(true);
                CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                dialog.setIcon(Icon.Error);
                dialog.setTitle(R.string.error);
                dialog.setMessage(R.string.you_already_have_a_tour);
                dialog.setPositiveButton(R.string.ok, null);
                dialog.show();
            }
        }
    }

    private void saveGetOldInvoiceType(String DownloadOldInvoicePolicy, String tourNo) {
        try {
            SysConfigManager sysConfigManager = new SysConfigManager(getContext());
            sysConfigManager.save(ConfigKey.FirstTimeAfterGetTour, "True", SysConfigManager.local);
            sysConfigManager.save(ConfigKey.DownloadOldInvoicePolicy, DownloadOldInvoicePolicy, SysConfigManager.local);
            getTourFinally(tourNo);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void getTourFinally(final String tourNo) {
        startProgress(R.string.please_wait, R.string.connecting_to_the_server);
        pingApi = new PingApi();
        pingApi.refreshBaseServerUrl(getContext(), new PingApi.PingCallback() {
            void startTourDownload() {
                if (!activity.isFinishing()) {
                    tourManager.startDownload(tourNo, new TourManager.TourDownloadCallBack() {
                        @Override
                        public void onFailure(String error) {
                            finishProgress();
                            if (isResumed()) {
                                getTourImageView.setEnabled(true);
                                CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                                dialog.setMessage(error);
                                dialog.setTitle(R.string.error);
                                dialog.setPositiveButton(R.string.ok, null);
                                dialog.setIcon(Icon.Error);
                                dialog.show();
                            }
                        }

                        @Override
                        public void onStart() {
                            finishProgress();
                            getTourImageView.setEnabled(true);
                            refreshTourProgress();
                            refreshUiTask = new RefreshUiTask();
                            refreshUiTask.execute();
                        }
                    }, getSyncService());
                }
            }

            @Override
            public void done(String ipAddress) {
                final MainVaranegarActivity activity = getVaranegarActvity();
                if (activity != null && !activity.isFinishing()) {
                    appVersionApi = new AppVersionApi(activity);
                    appVersionApi.getLatestVersion(new VersionApiCallBack() {
                        @Override
                        public void onSuccess(final AppVersionInfo versionInfo) {
                            MainVaranegarActivity activity = getVaranegarActvity();
                            if (activity != null && !activity.isFinishing()) {
                                try {
                                    int versionCode = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionCode;
                                    if (versionInfo.VersionCode > versionCode) {
                                        CuteMessageDialog dialog = new CuteMessageDialog(activity);
                                        dialog.setIcon(Icon.Info);
                                        dialog.setCancelable(false);
                                        dialog.setTitle(R.string.new_version_exist);
                                        dialog.setMessage(R.string.please_download_the_newest_version);
                                        dialog.setPositiveButton(R.string.download, view -> {
                                            getTourImageView.setEnabled(true);
                                            startProgress(R.string.please_wait, R.string.downloading_apk);
                                            appVersionApi.downloadAndSave(versionInfo.FileName, new ApkDownloadCallBack() {
                                                @Override
                                                public void onSuccess(String apkPath) {
                                                    finishProgress();
                                                    MainVaranegarActivity activity1 = getVaranegarActvity();
                                                    if (activity1 != null && !activity1.isFinishing()) {
                                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                                                            intent.setDataAndType(Uri.fromFile(new File(apkPath)), "application/vnd.android.package-archive");
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        } else {
                                                            Uri fileUri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", new File(apkPath));
                                                            intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                        }
                                                        startActivity(intent);
                                                    }
                                                }

                                                @Override
                                                public void onFailure(String err) {
                                                    finishProgress();
                                                    MainVaranegarActivity activity1 = getVaranegarActvity();
                                                    if (activity1 != null && !activity1.isFinishing()) {
                                                        CuteMessageDialog dialog1 = new CuteMessageDialog(activity1);
                                                        dialog1.setIcon(Icon.Error);
                                                        dialog1.setTitle(R.string.error);
                                                        dialog1.setMessage(R.string.error_downloading_apk);
                                                        dialog1.setPositiveButton(R.string.ok, null);
                                                        dialog1.show();
                                                    }
                                                }

                                            });
                                        });
//                                        dialog.setNegativeButton(R.string.cancel, view -> {
//                                            finishProgress();
//                                            getTourImageView.setEnabled(true);
//                                        });
                                        dialog.show();
                                    } else
                                        startTourDownload();
                                } catch (PackageManager.NameNotFoundException e) {
                                    Timber.e(e);
                                    startTourDownload();
                                }
                            }
                        }

                        @Override
                        public void onFailure(String error) {
                            startTourDownload();
                        }

                        @Override
                        public void onCancel() {
                            finishProgress();
                            MainVaranegarActivity activity = getVaranegarActvity();
                            if (activity != null && !activity.isFinishing())
                                getTourImageView.setEnabled(true);
                        }
                    });
                }
            }

            @Override
            public void failed() {
                finishProgress();
                MainVaranegarActivity activity = getVaranegarActvity();
                if (activity != null && !activity.isFinishing()) {
                    if (isResumed()) {
                        getTourImageView.setEnabled(true);
                        CuteMessageDialog dialog = new CuteMessageDialog(activity);
                        dialog.setIcon(Icon.Error);
                        dialog.setTitle(R.string.error);
                        dialog.setMessage(R.string.error_connecting_to_server);
                        dialog.setPositiveButton(R.string.ok, null);
                        dialog.show();
                    }
                }
            }
        });
    }

    private void DownloadApk(){
        startProgress(R.string.please_wait, R.string.connecting_to_the_server);
        pingApi = new PingApi();
        pingApi.refreshBaseServerUrl(getContext(), new PingApi.PingCallback() {

            @Override
            public void done(String ipAddress) {
                final MainVaranegarActivity activity = getVaranegarActvity();
                if (activity != null && !activity.isFinishing()) {
                    appVersionApi = new AppVersionApi(activity);
                    appVersionApi.getLatestVersion(new VersionApiCallBack() {
                        @Override
                        public void onSuccess(final AppVersionInfo versionInfo) {
                            MainVaranegarActivity activity = getVaranegarActvity();
                            if (activity != null && !activity.isFinishing()) {
                                try {
                                    int versionCode = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionCode;
                                    if (versionInfo.VersionCode > versionCode) {
                                        CuteMessageDialog dialog = new CuteMessageDialog(activity);
                                        dialog.setIcon(Icon.Info);
                                        dialog.setCancelable(false);
                                        dialog.setTitle(R.string.new_version_exist);
                                        dialog.setMessage(R.string.please_download_the_newest_version);
                                        dialog.setPositiveButton(R.string.download, view -> {
                                            getTourImageView.setEnabled(true);
                                            startProgress(R.string.please_wait, R.string.downloading_apk);
                                            appVersionApi.downloadAndSave(versionInfo.FileName, new ApkDownloadCallBack() {
                                                @Override
                                                public void onSuccess(String apkPath) {
                                                    finishProgress();
                                                    MainVaranegarActivity activity1 = getVaranegarActvity();
                                                    if (activity1 != null && !activity1.isFinishing()) {
                                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                                                            intent.setDataAndType(Uri.fromFile(new File(apkPath)), "application/vnd.android.package-archive");
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        } else {
                                                            Uri fileUri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", new File(apkPath));
                                                            intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                        }
                                                        startActivity(intent);
                                                    }
                                                }

                                                @Override
                                                public void onFailure(String err) {
                                                    finishProgress();
                                                    MainVaranegarActivity activity1 = getVaranegarActvity();
                                                    if (activity1 != null && !activity1.isFinishing()) {
                                                        CuteMessageDialog dialog1 = new CuteMessageDialog(activity1);
                                                        dialog1.setIcon(Icon.Error);
                                                        dialog1.setTitle(R.string.error);
                                                        dialog1.setMessage(R.string.error_downloading_apk);
                                                        dialog1.setPositiveButton(R.string.ok, null);
                                                        dialog1.show();
                                                    }
                                                }

                                            });
                                        });
//                                        dialog.setNegativeButton(R.string.cancel, view -> {
//                                            finishProgress();
//                                            getTourImageView.setEnabled(true);
//                                        });
                                        dialog.show();
                                    } else{
                                        finishProgress();
                                        MainVaranegarActivity activity1 = getVaranegarActvity();
                                        if (activity1 != null && !activity1.isFinishing()) {
                                            CuteMessageDialog dialog1 = new CuteMessageDialog(activity1);
                                            dialog1.setIcon(Icon.Error);
                                            dialog1.setTitle(R.string.error);
                                            dialog1.setMessage(R.string.not_download_apk);
                                            dialog1.setPositiveButton(R.string.ok, null);
                                            dialog1.show();
                                        }

                                    }

                                } catch (PackageManager.NameNotFoundException e) {
                                    Timber.e(e);

                                }
                            }
                        }

                        @Override
                        public void onFailure(String error) {
                            finishProgress();
                            MainVaranegarActivity activity1 = getVaranegarActvity();
                            if (activity1 != null && !activity1.isFinishing()) {
                                CuteMessageDialog dialog1 = new CuteMessageDialog(activity1);
                                dialog1.setIcon(Icon.Error);
                                dialog1.setTitle(R.string.error);
                                dialog1.setMessage(R.string.not_download_apk);
                                dialog1.setPositiveButton(R.string.ok, null);
                                dialog1.show();
                            }
                        }

                        @Override
                        public void onCancel() {
                            finishProgress();
                            MainVaranegarActivity activity = getVaranegarActvity();
                            if (activity != null && !activity.isFinishing())
                                getTourImageView.setEnabled(true);
                        }
                    });
                }
            }

            @Override
            public void failed() {
                finishProgress();
                MainVaranegarActivity activity = getVaranegarActvity();
                if (activity != null && !activity.isFinishing()) {
                    if (isResumed()) {
                        getTourImageView.setEnabled(true);
                        CuteMessageDialog dialog = new CuteMessageDialog(activity);
                        dialog.setIcon(Icon.Error);
                        dialog.setTitle(R.string.error);
                        dialog.setMessage(R.string.error_connecting_to_server);
                        dialog.setPositiveButton(R.string.ok, null);
                        dialog.show();
                    }
                }
            }
        });

    }
    private void runBackup(final List<ImageType> imageTypes) {
        startProgress(R.string.please_wait, R.string.backing_up_database);
        final Handler handler = new Handler();
        Thread thread = new Thread(() -> {
            try {
                BackupManager.exportData(getContext(), true, imageTypes);
                handler.post(() -> {
                    MainVaranegarActivity activity = getVaranegarActvity();
                    if (activity != null && !activity.isFinishing()) {
                        CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                        dialog.setMessage(R.string.backup_finished_successfully);
                        dialog.setTitle(R.string.export_data);
                        dialog.setIcon(Icon.Success);
                        dialog.setPositiveButton(R.string.send_backup, view -> BackupManager.uploadBackup(getContext()," "," ", new BackupManager.IUploadCallBack() {
                            @Override
                            public void onSuccess() {
                                if (isResumed())
                                    getVaranegarActvity().showSnackBar(R.string.backup_sent_successfully, MainVaranegarActivity.Duration.Short);
                            }

                            @Override
                            public void onFailure(String error) {
                                if (isResumed())
                                    getVaranegarActvity().showSnackBar(R.string.sending_backup_failed, MainVaranegarActivity.Duration.Short);
                            }
                        }));
                        dialog.setNeutralButton(R.string.close, null);
                        dialog.show();
                    }
                });
            } catch (Exception e) {
                Timber.e(e);
                handler.post(() -> {
                    MainVaranegarActivity activity = getVaranegarActvity();
                    if (activity != null && !activity.isFinishing()) {
                        CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                        dialog.setMessage(R.string.backup_failed);
                        dialog.setTitle(R.string.export_data);
                        dialog.setIcon(Icon.Error);
                        dialog.setPositiveButton(R.string.ok, null);
                        dialog.show();
                    }
                });
            } finally {
                handler.post(this::finishProgress);
            }
        });
        thread.start();
    }

    protected void finishProgress() {
        if (view != null) {
            ProgressView progressView = ((ProgressView) (view));
            progressView.finish();
        }
    }

    protected void startProgress(int title, int message) {
        if (view != null) {
            ProgressView progressView = ((ProgressView) (view));
            progressView.setMessage(message);
            progressView.setTitle(title);
            progressView.setMessage(message);
            progressView.start();
        }
    }

    private void refreshTourProgress() {
        if (isResumed()) {
            adapter = new CallLogRecyclerAdapter();
            logsRecyclerView.setAdapter(adapter);
            logsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            logsRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), R.color.grey_light_light_light, 1));

            if (tourManager.isTourDownloading()) {
                reportLayout.setVisibility(View.GONE);
                tourProgressLayout.setVisibility(View.VISIBLE);
                cancelTourRecordBtn.setVisibility(View.VISIBLE);
                getTourImageView.setVisibility(View.GONE);
                logoutImageView.setEnabled(false);
                trackingLicenseImageView.setEnabled(false);
                backupImageView.setEnabled(false);
                downloadApk.setEnabled(false);
                refreshtour.setEnabled(false);
            } else {
                cancelTourRecordBtn.setVisibility(View.GONE);
                getTourImageView.setVisibility(View.VISIBLE);
                logoutImageView.setEnabled(true);
                trackingLicenseImageView.setEnabled(true);
                backupImageView.setEnabled(true);
                downloadApk.setEnabled(true);
                refreshtour.setEnabled(true);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tour_report, container, false);
        logsRecyclerView = view.findViewById(R.id.logs_recycler_view);
        backupImageView = view.findViewById(R.id.backup_image_view);
        downloadApk = view.findViewById(R.id.download_apk);
        refreshtour= view.findViewById(R.id.refresh_tour);
        logoutImageView = view.findViewById(R.id.log_out_image_view);
        trackingLicenseImageView = view.findViewById(R.id.tracking_license_image_view);
        getTourImageView = view.findViewById(R.id.get_tour_image_view);
        cancelTourRecordBtn = view.findViewById(R.id.stop_btn);
        reportLayout = view.findViewById(R.id.report_layout);
        tourProgressLayout = view.findViewById(R.id.tour_progress_layout);
        totalProgressBar = view.findViewById(R.id.total_progress_bar);
        tourManager = new TourManager(getContext());
        userModel = UserManager.readFromFile(getContext());
        if (userModel == null) {
            MainVaranegarActivity activity = getVaranegarActvity();
            if (activity != null)
                activity.finish();
            activity = null;
            return view;
        }
        return view;
    }

    @Override
    public void onBackPressed() {
        if (tourManager.isTourDownloading()) {
            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
            dialog.setMessage(getString(R.string.downloading_tour));
            dialog.setTitle(R.string.warning);
            dialog.setIcon(Icon.Warning);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        } else
            super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        setupPage();
        if (tourManager.isTourAvailable()) {
            boolean newTour = activity.getSharedPreferences("TourReportFragment", MODE_PRIVATE).getBoolean("NEW_TOUR", false);
            if (newTour) {
                MainVaranegarActivity activity = getVaranegarActvity();
                if (activity != null && !activity.isFinishing()) {
                    activity.putFragment(getCustomersFragment());
                    activity.getSharedPreferences("TourReportFragment", MODE_PRIVATE).edit().remove("NEW_TOUR").apply();
                }
            }
        }
        refreshTourProgress();
        if (tourManager.isTourDownloading()) {
            refreshUiTask = new RefreshUiTask();
            refreshUiTask.execute();
        } else if (tourManager.isTourAvailable()) {
            timerThread = new Thread(() -> {
                long totalVisitTime = CustomerActionTimeManager.getCustomerCallTimes(getContext());
                final String totalVisitTimeStr = DateHelper.getTimeSpanString(totalVisitTime);
                if (isVisible() && view != null) {
                    getActivity().runOnUiThread(() -> {
                        PairedItems visitTimePairedItems = (PairedItems) view.findViewById(R.id.total_visit_time_paired_items);
                        if (visitTimePairedItems != null)
                            visitTimePairedItems.setValue(totalVisitTimeStr);
                    });
                }
                UpdateManager updateManager = new UpdateManager(getContext());
                final Date date = updateManager.getLog(UpdateKey.TourStartTime);
                updateManager = null;
                while (!Thread.interrupted()) {
                    if (isVisible() && view != null) {
                        getActivity().runOnUiThread(() -> {
                            long t = new Date().getTime() - date.getTime();
                            ((PairedItems) view.findViewById(R.id.tour_time_paired_items)).setValue(DateHelper.getTimeSpanString(t / 1000));
                        });
                    }
                    SystemClock.sleep(1000);
                }

            });
            timerThread.start();
        } else if (tourManager.isTourSending()) {
            startProgress(R.string.sending_tour, R.string.please_wait);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (tourManager.isTourSending())
                        SystemClock.sleep(1000);
                    if (activity != null && !activity.isFinishing()) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                finishProgress();
                                if (tourManager.isTourAvailable()) {
                                    CuteMessageDialog dialog = new CuteMessageDialog(activity);
                                    dialog.setIcon(Icon.Error);
                                    dialog.setTitle(R.string.tour_is_not_sent);
                                    dialog.setMessage(R.string.sending_tour_failed);
                                    dialog.setPositiveButton(R.string.ok, null);
                                    dialog.show();
                                } else {
                                    CuteMessageDialog dialog = new CuteMessageDialog(activity);
                                    dialog.setIcon(Icon.Success);
                                    dialog.setTitle(R.string.tour_sent);
                                    dialog.setMessage(R.string.tour_is_sent);
                                    dialog.setPositiveButton(R.string.ok, view -> activity.finish());
                                    dialog.show();
                                }

                            }
                        });
                    }
                }
            }).start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (pingApi != null)
            pingApi.cancelPing();
        if (appVersionApi != null)
            appVersionApi.cancelCheckVersion();
        if (timerThread != null)
            timerThread.interrupt();
        if (refreshUiTask != null)
            refreshUiTask.cancel(true);
    }

    protected void createTourInfoView() {
        View tourInfoView = view.findViewById(R.id.tour_info_layout);
        TextView tourNoText = view.findViewById(R.id.profile_sent_tour_msg);
        String tourMsg;

        if (tourManager.isTourAvailable()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final TourInfo tourInfo = tourManager.createTourInfo();
                    MainVaranegarActivity activity = getVaranegarActvity();
                    if (activity != null && !activity.isFinishing() && isResumed()) {
                        activity.runOnUiThread(() -> {
                            if (tourInfo != null) {
                                presentTourData(tourInfo);
                                setPieCharts(tourInfo);
                            }
                        });
                    }
                }
            }).start();


        } else {
            TourInfo tourInfo = tourManager.loadTourFromFile();
            if (tourInfo != null) {
                tourInfoView.setVisibility(View.VISIBLE);
                LinearLayout printBtn = view.findViewById(R.id.print_btn_layout);
                tourMsg = getString(R.string.sent_tour_no) + tourInfo.TourNo;
                tourNoText.setText(tourMsg);
                tourNoText.setVisibility(View.VISIBLE);
                presentTourData(tourInfo);
                setPieCharts(tourInfo);

                SysConfigManager sysConfigManager = new SysConfigManager(getContext());
                SysConfigModel isPrintTourSummary = sysConfigManager.read(ConfigKey.PrintTourSummary, SysConfigManager.cloud);
                if (SysConfigManager.compare(isPrintTourSummary, true))
                    printBtn.setVisibility(View.VISIBLE);
                else
                    printBtn.setVisibility(View.GONE);
                printBtn.setOnClickListener(view -> {
                    SentTourInfoPrintHelper print = new SentTourInfoPrintHelper(getVaranegarActvity());
                    print.start(null);

                });
            } else {
                tourInfoView.setVisibility(View.GONE);
            }
            tourInfo = null;
        }

    }

    private void presentTourData(TourInfo tourInfo) {

        if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            ((PairedItems) view.findViewById(R.id.tour_no_paired_items)).setValue(String.valueOf(tourInfo.TourNo));
            ((PairedItems) view.findViewById(R.id.today_customers_count_paired_items)).setValue(String.valueOf(tourInfo.DayCustomersCount));
            ((PairedItems) view.findViewById(R.id.today_visited_customer_paired_items)).setValue(String.valueOf(tourInfo.DayVisitedCount));
            ((PairedItems) view.findViewById(R.id.today_ordered_customer_paired_items)).setValue(String.valueOf(tourInfo.DayOrderedCount));
            ((PairedItems) view.findViewById(R.id.today_lack_of_order_customer_paired_items)).setValue(String.valueOf(tourInfo.DayLackOfOrderCount));
            ((PairedItems) view.findViewById(R.id.lack_of_visit_customer_paired_items)).setValue(String.valueOf(tourInfo.DayLackOfVisitCount));
            ((PairedItems) view.findViewById(R.id.today_sum_of_ordered_paired_items)).setValue(HelperMethods.currencyToString(tourInfo.DayOrderSum));

            ((PairedItems) view.findViewById(R.id.total_customers_count_paired_items)).setValue(String.valueOf(tourInfo.TotalCustomersCount));
            ((PairedItems) view.findViewById(R.id.total_visited_customer_paired_items)).setValue(String.valueOf(tourInfo.TotalVisitedCount));
            ((PairedItems) view.findViewById(R.id.total_ordered_customer_paired_items)).setValue(String.valueOf(tourInfo.TotalOrderedCount));
            ((PairedItems) view.findViewById(R.id.total_lack_of_order_customer_paired_items)).setValue(String.valueOf(tourInfo.TotalLackOfOrderCount));
            ((PairedItems) view.findViewById(R.id.total_sum_of_ordered_paired_items)).setValue(HelperMethods.currencyToString(tourInfo.TotalOrderSum));

            view.findViewById(R.id.dist_no_paired_items).setVisibility(View.GONE);
            view.findViewById(R.id.total_deliveries_customer_paired_items).setVisibility(View.GONE);
            view.findViewById(R.id.total_partial_deliveries_customer_paired_items).setVisibility(View.GONE);
            view.findViewById(R.id.total_lack_of_deliveries_customer_paired_items).setVisibility(View.GONE);
            view.findViewById(R.id.total_lack_of_visits_customer_paired_items).setVisibility(View.GONE);
            view.findViewById(R.id.total_return_customer_paired_items).setVisibility(View.GONE);

            ((PairedItems) view.findViewById(R.id.total_visit_time_paired_items)).setValue(tourInfo.VisitTime);
            ((PairedItems) view.findViewById(R.id.tour_time_paired_items)).setValue(tourInfo.TourTime);


            ProductOrderViewManager productOrderViewManager = new ProductOrderViewManager(getContext());
            boolean spd = productOrderViewManager.getSPD();
            if (spd)
                ((PairedItems) view.findViewById(R.id.spd_paired_items)).setValue(getString(R.string.check_sign));
            else
                ((PairedItems) view.findViewById(R.id.spd_paired_items)).setValue(getString(R.string.multiplication_sign));

            DecimalFormat df = new DecimalFormat("#.00");
            ((PairedItems) view.findViewById(R.id.visit_to_customer_paired_items)).setValue(df.format(tourInfo.DayVisitRatio) + " %");
        } else {
            ((PairedItems) view.findViewById(R.id.tour_no_paired_items)).setValue(String.valueOf(tourInfo.TourNo));
            ((PairedItems) view.findViewById(R.id.dist_no_paired_items)).setValue(String.valueOf(tourInfo.DistNo));
            view.findViewById(R.id.today_customers_count_paired_items).setVisibility(View.GONE);
            view.findViewById(R.id.today_visited_customer_paired_items).setVisibility(View.GONE);
            view.findViewById(R.id.today_ordered_customer_paired_items).setVisibility(View.GONE);
            view.findViewById(R.id.today_lack_of_order_customer_paired_items).setVisibility(View.GONE);
            view.findViewById(R.id.lack_of_visit_customer_paired_items).setVisibility(View.GONE);
            view.findViewById(R.id.today_sum_of_ordered_paired_items).setVisibility(View.GONE);
            view.findViewById(R.id.total_ordered_customer_paired_items).setVisibility(View.GONE);
            view.findViewById(R.id.total_lack_of_order_customer_paired_items).setVisibility(View.GONE);
            view.findViewById(R.id.total_lack_of_visits_customer_paired_items).setVisibility(View.GONE);

            ((PairedItems) view.findViewById(R.id.total_customers_count_paired_items)).setValue(String.valueOf(tourInfo.TotalCustomersCount));
            ((PairedItems) view.findViewById(R.id.total_visited_customer_paired_items)).setValue(String.valueOf(tourInfo.TotalVisitedCount));

            ((PairedItems) view.findViewById(R.id.total_deliveries_customer_paired_items)).setValue(String.valueOf(tourInfo.DeliveriesCount));
            ((PairedItems) view.findViewById(R.id.total_partial_deliveries_customer_paired_items)).setValue(String.valueOf(tourInfo.PartialDeliveriesCount));
            ((PairedItems) view.findViewById(R.id.total_lack_of_deliveries_customer_paired_items)).setValue(String.valueOf(tourInfo.LackOfDeliveriesCount));
            ((PairedItems) view.findViewById(R.id.total_return_customer_paired_items)).setValue(String.valueOf(tourInfo.ReturnsCount));

            ((PairedItems) view.findViewById(R.id.total_sum_of_ordered_paired_items)).setValue(HelperMethods.currencyToString(tourInfo.TotalOrderSum));

            ((PairedItems) view.findViewById(R.id.total_visit_time_paired_items)).setValue(tourInfo.VisitTime);
            ((PairedItems) view.findViewById(R.id.tour_time_paired_items)).setValue(tourInfo.TourTime);


            view.findViewById(R.id.spd_paired_items).setVisibility(View.GONE);
            view.findViewById(R.id.spd_paired_items).setVisibility(View.GONE);
            view.findViewById(R.id.visit_to_customer_paired_items).setVisibility(View.GONE);
        }
    }

    private void setPieCharts(TourInfo tourInfo) {
        PieChart pieChart = (PieChart) view.findViewById(R.id.pie_chart);
        if (pieChart != null) {
            pieChart.setVisibility(View.VISIBLE);
            List<PieEntry> entries = new ArrayList<>();
            if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
                entries.add(new PieEntry(tourInfo.DeliveriesCount, getString(R.string.delivered)));
                entries.add(new PieEntry(tourInfo.PartialDeliveriesCount, getString(R.string.delivered_partially)));
                entries.add(new PieEntry(tourInfo.LackOfDeliveriesCount, getString(R.string.lack_of_delivery)));
                entries.add(new PieEntry(tourInfo.ReturnsCount, getString(R.string.complete_return)));

            } else {
                entries.add(new PieEntry(tourInfo.DayLackOfOrderCount, getString(R.string.lack_of_order)));
                entries.add(new PieEntry(tourInfo.DayLackOfVisitCount, getString(R.string.lack_of_visit)));
                entries.add(new PieEntry(tourInfo.DayOrderedCount, getString(R.string.ordered)));
            }
            PieDataSet pieDataSet = new PieDataSet(entries, "");
            pieDataSet.setValueTextSize(20);
            pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            Description description = new Description();
            description.setText("");
            pieChart.setDescription(description);
            pieChart.animateY(1500);
            PieData pieData = new PieData(pieDataSet);
            pieChart.setData(pieData);
        }
        pieChart = null;
    }

    private class RefreshUiTask extends AsyncTask<Void, List<TourUpdateLogModel>, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            TourModel tourModel = tourManager.loadTour();
            TourUpdateLogManager tourUpdateLogManager = new TourUpdateLogManager(getContext());
            while (tourManager.isTourDownloading()
                    ) {
                if (isCancelled())
                    break;
                SystemClock.sleep(1000);
                Query query = new Query().from(TourUpdateLog.TourUpdateLogTbl)
                        .whereAnd(Criteria.equals(TourUpdateLog.LocalTourId, tourModel.LocalId.toString()));
                List<TourUpdateLogModel> logs = tourUpdateLogManager.getItems(query);
                publishProgress(logs);
                tourModel = tourManager.loadTour();
            }
            tourModel = tourManager.loadTour();
            if (tourModel != null) {
                Query query = new Query().from(TourUpdateLog.TourUpdateLogTbl)
                        .whereAnd(Criteria.equals(TourUpdateLog.LocalTourId, tourModel.LocalId.toString()));
                List<TourUpdateLogModel> logs = tourUpdateLogManager.getItems(query);
                publishProgress(logs);
            }
            activity.getSharedPreferences("TourReportFragment", MODE_PRIVATE).edit().putBoolean("NEW_TOUR", true).apply();
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if (tourManager.isTourAvailable()) {
                MainVaranegarActivity activity = getVaranegarActvity();
                if (activity != null) {
                    if (activity.isVisible() && !activity.isFinishing()) {
                        activity.getSharedPreferences("TourReportFragment", MODE_PRIVATE).edit().putBoolean("NEW_TOUR", false).apply();
                        VaranegarFragment fragment = getCustomersFragment();
                        activity.putFragment(fragment);
                    }
                }
            } else {
                TourModel tourModel = tourManager.loadTour();
                String error = null;
                if (tourModel != null)

                    error = new TourUpdateLogManager(getContext())
                            .getLastError(tourModel);
                if (error == null)
                    error = TourManager.getLatestError();

                if (isResumed() && !isCanceled) {
                    CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                    dialog.setIcon(Icon.Error);
                    dialog.setTitle(R.string.error);
                    dialog.setMessage(error == null ? getString(R.string.error_downloading_the_tour) : error);
                    dialog.setPositiveButton(R.string.ok, null);
                    dialog.show();
                }
                isCanceled = false;
                refreshTourProgress();
            }
        }

        @Override
        protected void onProgressUpdate(List<TourUpdateLogModel>... values) {
            if (logGroups == null)
                logGroups = new ArrayList<>();
            if (logGroups.size() == 0) {
                for (String gName :
                        UpdateQueue.getGroups().keySet()) {
                    TourUpdateLogGroup logGroup = new TourUpdateLogGroup();
                    logGroup.size = UpdateQueue.getGroups().get(gName);
                    logGroup.name = gName;
                    logGroups.add(logGroup);
                }
                adapter.notifyDataSetChanged();
            } else {
                for (TourUpdateLogGroup group :
                        logGroups) {
                    group.lastFinishedTasks = group.finishedTasks;
                    group.finishedTasks = 0;
                    group.failedTasks = 0;
                }
            }
            if (values != null && values.length > 0) {
                List<TourUpdateLogModel> logs = values[0];
                for (final TourUpdateLogModel callLogModel :
                        logs) {
                    TourUpdateLogGroup group = Linq.findFirst(logGroups, item -> item.name.equals(callLogModel.GroupName));
                    if (group != null) {
                        if (callLogModel.FinishDate != null) {
                            if (callLogModel.Error == null)
                                group.finishedTasks++;
                            else
                                group.failedTasks++;
                        }
                    }
                }
                int progress = 0;
                int totalTasks = 0;
                for (int i = 0; i < logGroups.size(); i++) {
                    TourUpdateLogGroup group = logGroups.get(i);
                    progress += group.finishedTasks;
                    totalTasks += group.size;
                    if (group.finishedTasks != group.lastFinishedTasks)
                        adapter.notifyItemChanged(i);
                }

                totalProgressBar.setMax(totalTasks);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    totalProgressBar.setProgress(progress, true);
                } else {
                    totalProgressBar.setProgress(progress);
                }
            }
        }
    }


    private class CallLogViewHolder extends RecyclerView.ViewHolder {
        private final TextView logGrouptextView;
        private final CircularProgressBar downloadProgressView;
        private final ImageView resultImageView;
        private final TextView percentTextView;

        public CallLogViewHolder(View itemView) {
            super(itemView);
            percentTextView = itemView.findViewById(R.id.percent_text_view);
            logGrouptextView = itemView.findViewById(R.id.log_group_text_view);
            downloadProgressView = itemView.findViewById(R.id.download_progress_view);
            resultImageView = itemView.findViewById(R.id.result_image_view);
        }

        public void bind(int position) {
            TourUpdateLogGroup callLogModel = logGroups.get(position);
            float percent = (float) callLogModel.finishedTasks / (float) callLogModel.size;
            percentTextView.setText(String.format("%.0f", percent * 100) + " %");
            logGrouptextView.setText(callLogModel.name);
            if (callLogModel.totalTasks() == callLogModel.size) {
                downloadProgressView.setVisibility(View.GONE);
                resultImageView.setVisibility(View.VISIBLE);
                if (callLogModel.failedTasks > 0)
                    resultImageView.setImageResource(R.mipmap.ic_error_red_48dp);
                else
                    resultImageView.setImageResource(R.mipmap.ic_ok_green_48dp);
            } else {
                if (callLogModel.failedTasks > 0) {
                    downloadProgressView.setVisibility(View.GONE);
                    resultImageView.setVisibility(View.VISIBLE);
                    resultImageView.setImageResource(R.mipmap.ic_error_red_48dp);
                } else {
                    if (tourManager.isTourDownloading()) {
                        resultImageView.setVisibility(View.GONE);
                        downloadProgressView.setVisibility(View.VISIBLE);
                        downloadProgressView.setProgressWithAnimation(percent * 100);
                    } else {
                        downloadProgressView.setVisibility(View.GONE);
                        resultImageView.setVisibility(View.VISIBLE);
                        resultImageView.setImageResource(R.mipmap.ic_warning_yellow_48dp);
                    }
                }
            }


        }
    }

    class CallLogRecyclerAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_itme_layout, parent, false);
            return new CallLogViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            CallLogViewHolder viewHolder = (CallLogViewHolder) holder;
            viewHolder.bind(position);
        }

        @Override
        public int getItemCount() {
            return logGroups == null ? 0 : logGroups.size();
        }
    }

    private class TourUpdateLogGroup {
        public int size;
        public int lastFinishedTasks;
        public int finishedTasks;
        public int failedTasks;

        public int totalTasks() {
            return finishedTasks + failedTasks;
        }

        public String name;
    }

    @Override
    public void setTourNo(String tourNo) {
        getTour(tourNo);
        SharedPreferences.Editor editor = getContext().getSharedPreferences(IS_VIRTUAL, MODE_PRIVATE).edit();
        editor.putBoolean(IS_VIRTUAL_BOOLEAN, true);
        editor.apply();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        activity = null;
        totalProgressBar = null;
        tourManager = null;
        getTourImageView = null;
        backupImageView = null;
        downloadApk=null;
        refreshtour=null;
        logoutImageView = null;
        trackingLicenseImageView = null;
        cancelTourRecordBtn = null;
        logGroups = null;
        refreshUiTask = null;
        adapter = null;
        reportLayout = null;
        logsRecyclerView = null;
        tourProgressLayout = null;


        view = null;
        pingApi = null;
        appVersionApi = null;
    }

}
