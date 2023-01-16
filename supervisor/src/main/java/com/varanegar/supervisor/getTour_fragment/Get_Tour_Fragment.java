package com.varanegar.supervisor.getTour_fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.varanegar.framework.base.AppVersionInfo;
import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.component.ProgressView;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.supervisor.DataManager;
import com.varanegar.supervisor.IMainPageFragment;
import com.varanegar.supervisor.MainFragment;
import com.varanegar.supervisor.R;
import com.varanegar.supervisor.webapi.SupervisorApi;
import com.varanegar.vaslibrary.base.BackupManager;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.ui.dialog.TrackingLicenseFragment;
import com.varanegar.vaslibrary.ui.fragment.vpnfragment.VpnDialogFragment;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.appversion.ApkDownloadCallBack;
import com.varanegar.vaslibrary.webapi.appversion.AppVersionApi;
import com.varanegar.vaslibrary.webapi.appversion.VersionApiCallBack;
import com.varanegar.vaslibrary.webapi.ping.PingApi;

import java.io.File;
import java.util.UUID;

import okhttp3.Request;
import timber.log.Timber;

public class Get_Tour_Fragment extends VaranegarFragment {
    private View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_get_tour_supervisor, container, false);
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        SysConfigModel localServerAddressConfig = sysConfigManager.read(ConfigKey.LocalServerAddress, SysConfigManager.local);
        SysConfigModel validServerAddressConfig = sysConfigManager.read(ConfigKey.ValidServerAddress, SysConfigManager.local);
        if (localServerAddressConfig != null)
            ((TextView) (view.findViewById(R.id.local_ip_text_view))).setText(localServerAddressConfig.Value);
        if (validServerAddressConfig != null)
            ((TextView) (view.findViewById(R.id.ip_text_view))).setText(validServerAddressConfig.Value);

        UserModel userModel = UserManager.readFromFile(getContext());
        if (userModel != null) {
            ((TextView) (view.findViewById(R.id.user_name_text_view))).setText(userModel.UserName);
        }

        ImageView trackingLicenseImageView = view.findViewById(R.id.tracking_license_image_view);
        trackingLicenseImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainVaranegarActivity activity = getVaranegarActvity();
                if (activity != null && !activity.isFinishing() && isResumed()) {
                    TrackingLicenseFragment fragment = new TrackingLicenseFragment();
                    activity.pushFragment(fragment);
                }
            }
        });

        view.findViewById(R.id.log_out_image_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                dialog.setTitle(R.string.are_you_sure);
                dialog.setIcon(Icon.Warning);
                dialog.setMessage(R.string.do_you_want_to_log_out);
                dialog.setNegativeButton(R.string.no, null);
                dialog.setPositiveButton(R.string.yes, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getContext().getSharedPreferences("TrackingConfig", Context.MODE_PRIVATE).edit().clear().apply();
                        getContext().getSharedPreferences("TourStatusConfig", Context.MODE_PRIVATE).edit().clear().apply();
                        getContext().getSharedPreferences("ReportConfig", Context.MODE_PRIVATE).edit().clear().apply();
                        UserManager.logout(getVaranegarActvity());
                    }
                });
                dialog.show();
            }
        });

        view.findViewById(R.id.vpn_image_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VpnDialogFragment vpnDialogFragment = new VpnDialogFragment();
                vpnDialogFragment.show(getChildFragmentManager(), "SettingDialogFragment");
            }
        });

        view.findViewById(R.id.backup_image_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startProgress(getString(R.string.backing_up_database));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            BackupManager.exportData(getContext(), true);
                            if (isResumed()) {
                                getVaranegarActvity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        endProgress();
                                        CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                                        dialog.setMessage(R.string.backup_finished_successfully);
                                        dialog.setIcon(Icon.Success);
                                        dialog.setPositiveButton(R.string.close, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                            }
                                        });
                                        dialog.setNeutralButton(R.string.send_backup, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                startProgress(getString(R.string.sending_backup));
                                                BackupManager.uploadBackup(getContext()," "," ", new BackupManager.IUploadCallBack() {
                                                    @Override
                                                    public void onSuccess() {
                                                        if (isResumed()) {
                                                            endProgress();
                                                            showMessage(null, getString(com.varanegar.vaslibrary.R.string.backup_sent_successfully));
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(String error) {
                                                        if (isResumed()) {
                                                            endProgress();
                                                            showError(getString(com.varanegar.vaslibrary.R.string.sending_backup_failed));
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                        dialog.show();
                                    }
                                });

                            }
                        } catch (Exception e) {
                            Timber.e(e);
                            if (isResumed()) {
                                CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                                dialog.setTitle(R.string.error);
                                dialog.setMessage(R.string.error_saving_request);
                                dialog.setIcon(Icon.Error);
                                dialog.setPositiveButton(R.string.ok, null);
                                dialog.show();
                            }
                        }
                    }
                }).start();

            }
        });

        try {
            String packageName = getContext().getApplicationInfo().packageName;
            String version = getContext().getPackageManager().getPackageInfo(packageName, 0).versionName;
            ((TextView) view.findViewById(R.id.version_text_view)).setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e(e);
        }

        view.findViewById(R.id.send_tour).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage(getString(R.string.downloading_data));
                progressDialog.show();
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("SupervisorId", Context.MODE_PRIVATE);
                UUID userModel = UUID.fromString(sharedPreferences.getString("SupervisorIduniqueId", null));
                SupervisorApi supervisorApi=new SupervisorApi(getContext());
                supervisorApi.runWebRequest(supervisorApi.tourreceived(userModel), new WebCallBack<Void>() {
                    @Override
                    protected void onFinish() {

                    }

                    @Override
                    protected void onSuccess(Void result, Request request) {

                    }

                    @Override
                    protected void onApiFailure(ApiError error, Request request) {
                        String err = WebApiErrorBody.log(error, getContext());
                        showError(err);
                        if (progressDialog != null && progressDialog.isShowing()) {
                            try {
                                progressDialog.dismiss();
                            } catch (Exception ignored) {

                            }
                        }
                    }

                    @Override
                    protected void onNetworkFailure(Throwable t, Request request) {
                        showError(getContext().getString(R.string.error_connecting_to_server));
                        if (progressDialog != null && progressDialog.isShowing()) {
                            try {
                                progressDialog.dismiss();
                            } catch (Exception ignored) {

                            }
                        }
                    }
                });

            }
        });
        view.findViewById(R.id.get_tour_image_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage(getString(R.string.downloading_data));
                progressDialog.show();
                DataManager.getVisitor(getContext(), new DataManager.Callback() {
                    @Override
                    public void onSuccess() {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            try {
                                progressDialog.dismiss();
                                MainVaranegarActivity activity = getVaranegarActvity();
                                if (activity != null && !activity.isFinishing() && isResumed())
                                    activity.putFragment(new MainFragment());
                            } catch (Exception ignored) {

                            }
                        }
                    }

                    @Override
                    public void onError(String error) {
                        showError(error);
                        if (progressDialog != null && progressDialog.isShowing()) {
                            try {
                                progressDialog.dismiss();
                            } catch (Exception ignored) {

                            }
                        }
                    }
                });

            }
        });
        view.findViewById(R.id.download_apk_view).setOnClickListener(v -> {
            DownloadApk();
        });
        return view;
    }
    private void endProgress() {
        if (isResumed()) {
            View backupProgressLayout = getView().findViewById(R.id.backup_progress_layout);
            View backupLayout = getView().findViewById(R.id.backup_layout);
            backupProgressLayout.setVisibility(View.GONE);
            backupLayout.setVisibility(View.VISIBLE);
        }
    }

    private void startProgress(String msg) {
        if (isResumed()) {
            View backupProgressLayout = getView().findViewById(R.id.backup_progress_layout);
            View backupLayout = getView().findViewById(R.id.backup_layout);
            backupProgressLayout.setVisibility(View.VISIBLE);
            backupLayout.setVisibility(View.GONE);
            TextView textView = getView().findViewById(R.id.progress_message_text_view);
            textView.setText(msg);
        }
    }

    private void showMessage(String title, String msg) {
        Context context = getContext();
        if (isResumed() && context != null) {
            CuteMessageDialog dialog = new CuteMessageDialog(context);
            dialog.setTitle(title);
            dialog.setMessage(msg);
            dialog.setIcon(Icon.Info);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        }
    }

    private void showError(String error) {
        Context context = getContext();
        if (isResumed() && context != null) {
            CuteMessageDialog dialog = new CuteMessageDialog(context);
            dialog.setTitle(R.string.error);
            dialog.setMessage(error);
            dialog.setIcon(Icon.Error);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
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
    private void DownloadApk(){


        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.downloading_apk));
        progressDialog.show();
        PingApi pingApi = new PingApi();
        pingApi.refreshBaseServerUrl(getContext(), new PingApi.PingCallback() {

            @Override
            public void done(String ipAddress) {
                final MainVaranegarActivity activity = getVaranegarActvity();
                if (activity != null && !activity.isFinishing()) {
                    AppVersionApi appVersionApi = new AppVersionApi(activity);
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
                                        dialog.setTitle(com.varanegar.vaslibrary.R.string.new_version_exist);
                                        dialog.setMessage(com.varanegar.vaslibrary.R.string.please_download_the_newest_version);
                                        dialog.setPositiveButton(com.varanegar.vaslibrary.R.string.download, view -> {
                                            startProgress(com.varanegar.vaslibrary.R.string.please_wait, com.varanegar.vaslibrary.R.string.downloading_apk);
                                            appVersionApi.downloadAndSave(versionInfo.FileName, new ApkDownloadCallBack() {
                                                @Override
                                                public void onSuccess(String apkPath) {
                                                    progressDialog.dismiss();
                                                    MainVaranegarActivity activity1 = getVaranegarActvity();
                                                    if (activity1 != null && !activity1.isFinishing()) {
                                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                                                            intent.setDataAndType(Uri.fromFile(new File(apkPath)), "application/vnd.android.package-archive");
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        } else {
                                                            Uri fileUri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".provider", new File(apkPath));
                                                            intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                        }
                                                        startActivity(intent);
                                                    }
                                                }

                                                @Override
                                                public void onFailure(String err) {
                                                    progressDialog.dismiss();
                                                    MainVaranegarActivity activity1 = getVaranegarActvity();
                                                    if (activity1 != null && !activity1.isFinishing()) {
                                                        CuteMessageDialog dialog1 = new CuteMessageDialog(activity1);
                                                        dialog1.setIcon(Icon.Error);
                                                        dialog1.setTitle(com.varanegar.vaslibrary.R.string.error);
                                                        dialog1.setMessage(com.varanegar.vaslibrary.R.string.error_downloading_apk);
                                                        dialog1.setPositiveButton(com.varanegar.vaslibrary.R.string.ok, null);
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
                                        progressDialog.dismiss();
                                        MainVaranegarActivity activity1 = getVaranegarActvity();
                                        if (activity1 != null && !activity1.isFinishing()) {
                                            CuteMessageDialog dialog1 = new CuteMessageDialog(activity1);
                                            dialog1.setIcon(Icon.Error);
                                            dialog1.setTitle(com.varanegar.vaslibrary.R.string.error);
                                            dialog1.setMessage(com.varanegar.vaslibrary.R.string.not_download_apk);
                                            dialog1.setPositiveButton(com.varanegar.vaslibrary.R.string.ok, null);
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
                            progressDialog.dismiss();
                            MainVaranegarActivity activity1 = getVaranegarActvity();
                            if (activity1 != null && !activity1.isFinishing()) {
                                CuteMessageDialog dialog1 = new CuteMessageDialog(activity1);
                                dialog1.setIcon(Icon.Error);
                                dialog1.setTitle(com.varanegar.vaslibrary.R.string.error);
                                dialog1.setMessage(com.varanegar.vaslibrary.R.string.not_download_apk);
                                dialog1.setPositiveButton(com.varanegar.vaslibrary.R.string.ok, null);
                                dialog1.show();
                            }
                        }

                        @Override
                        public void onCancel() {
                            progressDialog.dismiss();
                            MainVaranegarActivity activity = getVaranegarActvity();
                            if (activity != null && !activity.isFinishing());

                        }
                    });
                }
            }

            @Override
            public void failed() {
                progressDialog.dismiss();
                MainVaranegarActivity activity = getVaranegarActvity();
                if (activity != null && !activity.isFinishing()) {
                    if (isResumed()) {
                        CuteMessageDialog dialog = new CuteMessageDialog(activity);
                        dialog.setIcon(Icon.Error);
                        dialog.setTitle(com.varanegar.vaslibrary.R.string.error);
                        dialog.setMessage(com.varanegar.vaslibrary.R.string.error_connecting_to_server);
                        dialog.setPositiveButton(com.varanegar.vaslibrary.R.string.ok, null);
                        dialog.show();
                    }
                }
            }
        });

    }

}
