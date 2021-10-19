package com.varanegar.vaslibrary.ui.dialog;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.PopupFragment;
import com.varanegar.framework.network.Connectivity;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.component.PairedItems;
import com.varanegar.framework.util.component.SimpleToolbar;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.locationmanager.LocationManager;
import com.varanegar.vaslibrary.manager.locationmanager.LogLevel;
import com.varanegar.vaslibrary.manager.locationmanager.LogType;
import com.varanegar.vaslibrary.manager.locationmanager.TrackingLicense;
import com.varanegar.vaslibrary.manager.locationmanager.TrackingLogManager;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.ping.PingApi;
import com.varanegar.vaslibrary.webapi.tracking.CompanyDeviceAppData;
import com.varanegar.vaslibrary.webapi.tracking.DeviceSaveResponse;
import com.varanegar.vaslibrary.webapi.tracking.LicenseRequestBody;
import com.varanegar.vaslibrary.webapi.tracking.TrackingApi;

import java.util.Date;
import java.util.Locale;

import okhttp3.Request;

/**
 * Created by A.Torabi on 8/12/2017.
 */

public class TrackingLicenseFragment extends PopupFragment {
    View view;
    private PairedItems expireDatePairedItems;
    private TextView statusTextView;
    private ProgressDialog progressDialog;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int phonePermission = getContext().checkSelfPermission(Manifest.permission_group.PHONE);
            if (phonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getVaranegarActvity(),
                        new String[]{
                                Manifest.permission.READ_PHONE_STATE
                        }, 12456);
            }
        }
        PairedItems deviceIdPairedItems = (PairedItems) view.findViewById(R.id.device_id_paired_items);
        final String deviceId = TrackingLicense.getDeviceId(getContext());
        deviceIdPairedItems.setValue(deviceId);
        view.findViewById(R.id.copy_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("device id", deviceId);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(), getString(R.string.copy_to_clipboard), Toast.LENGTH_SHORT).show();
            }
        });
        PairedItems deviceModelPairedItems = (PairedItems) view.findViewById(R.id.device_model_paired_items);
        PairedItems userNamePairedItems = (PairedItems) view.findViewById(R.id.user_name_paired_items);
        statusTextView = (TextView) view.findViewById(R.id.no_tracking_text_view);
        expireDatePairedItems = (PairedItems) view.findViewById(R.id.expire_date_paired_items);
        deviceModelPairedItems.setValue(Build.MODEL);
        userNamePairedItems.setValue(UserManager.readFromFile(getContext()).UserName);
        ((SimpleToolbar) view.findViewById(R.id.toolbar)).setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVaranegarActvity().popFragment();
            }
        });
        previewLicense();
        view.findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Connectivity.isConnected(getActivity())) {
                    ConnectionSettingDialog connectionSettingDialog = new ConnectionSettingDialog();
                    connectionSettingDialog.show(getActivity().getSupportFragmentManager(), "ConnectionSettingDialog");
                    return;
                }
                progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage(getString(R.string.please_wait));
                progressDialog.show();
                PingApi pingApi = new PingApi();
                pingApi.refreshBaseServerUrl(getContext(), new PingApi.PingCallback() {
                    @Override
                    public void done(String ipAddress) {
                        TrackingApi trackingApi = new TrackingApi(getContext());
                        String deviceId = TrackingLicense.getDeviceId(getContext());
                        if (deviceId == null) {
                            getVaranegarActvity().showSnackBar(R.string.device_id_is_not_available, MainVaranegarActivity.Duration.Short);
                            return;
                        }
                        LicenseRequestBody licenseRequestBody = new LicenseRequestBody();
                        licenseRequestBody.companyDeviceAppData = new CompanyDeviceAppData();
                        licenseRequestBody.companyDeviceAppData.IMEI = deviceId;
                        licenseRequestBody.companyDeviceAppData.DeviceModelName = Build.MODEL;
                        licenseRequestBody.companyDeviceAppData.UserName = UserManager.readFromFile(getContext()).UserName;
                        trackingApi.runWebRequest(trackingApi.requestLicense(licenseRequestBody), new WebCallBack<DeviceSaveResponse>() {
                            @Override
                            protected void onFinish() {
                                dismissProgressDialog();
                            }

                            @Override
                            protected void onSuccess(DeviceSaveResponse result, Request request) {
                                if (isResumed()) {
                                    CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                                    if (result.type == 1) {
                                        dialog.setMessage(R.string.request_has_been_sent);
                                        dialog.setIcon(Icon.Success);
                                    } else {
                                        dialog.setMessage(result.message);
                                        dialog.setTitle(R.string.error);
                                        dialog.setIcon(Icon.Error);
                                    }
                                    dialog.setPositiveButton(R.string.ok, null);
                                    dialog.show();
                                }
                            }

                            @Override
                            protected void onApiFailure(ApiError error, Request request) {
                                if (isResumed()) {
                                    String err = WebApiErrorBody.log(error, getContext());
                                    CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                                    dialog.setTitle(R.string.error);
                                    dialog.setIcon(Icon.Error);
                                    dialog.setMessage(err);
                                    dialog.setPositiveButton(R.string.ok, null);
                                    dialog.show();
                                }
                            }

                            @Override
                            protected void onNetworkFailure(Throwable t, Request request) {
                                if (isResumed()) {
                                    CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                                    dialog.setMessage(R.string.network_error);
                                    dialog.setPositiveButton(R.string.ok, null);
                                    dialog.setIcon(Icon.Error);
                                    dialog.setTitle(R.string.error);
                                    dialog.show();
                                }
                            }
                        });
                    }

                    @Override
                    public void failed() {
                        if (isResumed()) {
                            dismissProgressDialog();
                            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                            dialog.setMessage(R.string.network_error);
                            dialog.setPositiveButton(R.string.ok, null);
                            dialog.setIcon(Icon.Error);
                            dialog.setTitle(R.string.error);
                            dialog.show();
                        }
                    }
                });

            }
        });
        view.findViewById(R.id.download_license_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Connectivity.isConnected(getActivity())) {
                    ConnectionSettingDialog connectionSettingDialog = new ConnectionSettingDialog();
                    connectionSettingDialog.show(getActivity().getSupportFragmentManager(), "ConnectionSettingDialog");
                    return;
                }
                final String deviceId = TrackingLicense.getDeviceId(getContext());
                if (deviceId == null) {
                    getVaranegarActvity().showSnackBar(R.string.device_id_is_not_available, MainVaranegarActivity.Duration.Short);
                    return;
                }
                TrackingLogManager.addLog(getActivity(), LogType.LICENSE, LogLevel.Info, "Device IMEI = " + deviceId);
                progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage(getString(R.string.please_wait));
                progressDialog.show();
                LocationManager locationManager = new LocationManager(getContext());
                locationManager.downloadTrackingLicense(deviceId, new LocationManager.DownloadCallBack() {
                    @Override
                    public void done() {
                        if (isResumed()) {
                            dismissProgressDialog();
                            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                            dialog.setMessage(R.string.downloading_license_file_succeeded);
                            dialog.setPositiveButton(R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //previewLicense();
                                    getActivity().finish();
                                }
                            });
                            dialog.setIcon(Icon.Success);
                            dialog.show();
                        }
                    }

                    @Override
                    public void failed(String error) {
                        if (isResumed()) {
                            dismissProgressDialog();
                            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                            dialog.setMessage(error);
                            dialog.setPositiveButton(R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    previewLicense();
                                }
                            });
                            dialog.setIcon(Icon.Error);
                            dialog.setTitle(R.string.error);
                            dialog.show();
                        }
                    }
                });
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_tracking_license, container, false);
        return view;
    }
    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing() && isResumed())
            progressDialog.dismiss();
    }

    private void previewLicense() {
        String deviceId = TrackingLicense.getDeviceId(getContext());
        if (deviceId != null) {
            TrackingLicense trackingLicense = TrackingLicense.readLicense(getContext());
            if (trackingLicense == null) {
                statusTextView.setVisibility(View.VISIBLE);
                statusTextView.setText(R.string.there_is_no_tracking_license);
            } else if (trackingLicense.isExpired(getContext())) {
                statusTextView.setVisibility(View.VISIBLE);
                statusTextView.setText(R.string.license_is_expired);
            } else {
                statusTextView.setVisibility(View.GONE);
                Date date = trackingLicense.getExpireDate();
                if (date != null)
                    expireDatePairedItems.setValue(DateHelper.toString(date, DateFormat.Date, Locale.getDefault()));
            }

        } else
            getVaranegarActvity().showSnackBar(R.string.device_id_is_not_available, MainVaranegarActivity.Duration.Short);

    }

}
