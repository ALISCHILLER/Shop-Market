package com.varanegar.supervisor;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dd.processbutton.iml.ActionProcessButton;
import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.base.account.AccountManager;
import com.varanegar.framework.base.account.OnError;
import com.varanegar.framework.base.account.OnTokenAcquired;
import com.varanegar.framework.base.account.Token;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.network.Connectivity;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.component.SearchBox;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.validation.ValidationError;
import com.varanegar.framework.validation.ValidationListener;
import com.varanegar.framework.validation.Validator;
import com.varanegar.framework.validation.annotations.NotEmpty;
import com.varanegar.framework.validation.annotations.NotEmptyChecker;
import com.varanegar.vaslibrary.base.BackupManager;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.locationmanager.LogLevel;
import com.varanegar.vaslibrary.manager.locationmanager.LogType;
import com.varanegar.vaslibrary.manager.locationmanager.TrackingLicense;
import com.varanegar.vaslibrary.manager.locationmanager.TrackingLogManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.sysconfig.SysConfig;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.model.user.User;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.model.user.UserModelRepository;
import com.varanegar.vaslibrary.ui.dialog.ConnectionSettingDialog;
import com.varanegar.vaslibrary.ui.dialog.ImportDialogFragment;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.device.CompanyDeviceAppResult;
import com.varanegar.vaslibrary.webapi.device.DeviceApi;
import com.varanegar.vaslibrary.webapi.ping.PingApi;
import com.varanegar.vaslibrary.webapi.tracking.CompanyDeviceAppData;
import com.varanegar.vaslibrary.webapi.tracking.LicenseRequestBody;

import java.util.Date;
import java.util.List;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by A.Torabi on 6/7/2018.
 */

public class SupervisorLoginFragment extends VaranegarFragment implements ValidationListener {
    private static final int REQUEST_CODE = 101;
    private Validator validator;
    @NotEmpty
    private EditText passwordEditText;
    @NotEmpty
    private EditText userNameEditText;
    private ActionProcessButton loginButton;
    private ImageView settingsImageView;
    private ImageView usersImageView;

    private void setEnabled(boolean enabled) {
        loginButton.setEnabled(enabled);
        settingsImageView.setEnabled(enabled);
        userNameEditText.setEnabled(enabled);
        passwordEditText.setEnabled(enabled);
        usersImageView.setEnabled(enabled);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        validator = new Validator();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_supervisor_login, container, false);

        // region apk name and version
        try {
            String packageName = getContext().getApplicationInfo().packageName;
            String version = getContext().getPackageManager().getPackageInfo(packageName, 0).versionName;
            TextView versionTextView = view.findViewById(R.id.version_text_view);
            versionTextView.setText(getString(R.string.app_name) + " " + version);
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e(e);
        }
        // endregion

        view.findViewById(R.id.logo_image_view).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (BackupManager.getList(getContext(), BackupManager.BackupType.Full).size() > 0) {
                    ImportDialogFragment importDialog = new ImportDialogFragment();
                    importDialog.setBackupType(BackupManager.BackupType.Full);
                    importDialog.show(getChildFragmentManager(), "ImportDialogFragment");
                } else {
                    CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                    dialog.setTitle(com.varanegar.vaslibrary.R.string.error);
                    dialog.setMessage(com.varanegar.vaslibrary.R.string.there_is_no_backup_file);
                    dialog.setIcon(Icon.Alert);
                    dialog.setPositiveButton(com.varanegar.vaslibrary.R.string.ok, null);
                    dialog.show();
                }
                return true;
            }
        });
        // region username and password
        passwordEditText = view.findViewById(R.id.password_edit_text);
        userNameEditText = view.findViewById(R.id.user_name_edit_text);
        settingsImageView = view.findViewById(R.id.settings_image_view);
        usersImageView = view.findViewById(R.id.user_name_image_view);
        usersImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SysConfigModel serverAddress = new SysConfigManager(getContext()).read(ConfigKey.LocalServerAddress, SysConfigManager.local);
                if (serverAddress == null) {
                    CuteMessageDialog alert = new CuteMessageDialog(getContext());
                    alert.setIcon(Icon.Error);
                    alert.setTitle(R.string.error);
                    alert.setMessage(R.string.please_set_settings);
                    alert.setPositiveButton(R.string.ok, null);
                    alert.show();
                } else {
                    final SearchBox<UserModel> searchBox = new SearchBox<>();
                    searchBox.setRepository(
                            new UserModelRepository(),
                            new SearchBox.SearchQuery() {
                                @Override
                                public Query onSearch(String text) {
                                    return UserManager.getAll(text);
                                }
                            });
                    searchBox.setOnItemSelectedListener(new SearchBox.OnItemSelectedListener<UserModel>() {
                        @Override
                        public void run(int position, UserModel item) {
                            searchBox.dismiss();
                            userNameEditText.setText(item.UserName);
                        }
                    });
                    searchBox.show(getChildFragmentManager(), "SearchBox");
                }
            }
        });
        settingsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SupervisorSettingDialogFragment settingDialogFragment = new SupervisorSettingDialogFragment();
                settingDialogFragment.setCancelable(false);
                settingDialogFragment.setConfigListener(new SupervisorSettingDialogFragment.SettingsUpdateListener() {
                    @Override
                    public void onSettingsUpdated() {
                        Intent intent = getVaranegarActvity().getIntent();
                        getVaranegarActvity().finish();
                        startActivity(intent);
                    }
                });
                settingDialogFragment.show(getChildFragmentManager(), "SettingDialogFragment");
            }
        });


        loginButton = view.findViewById(R.id.ok_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userNameEditText.setError(null);
                passwordEditText.setError(null);
                validator.validate(SupervisorLoginFragment.this);
            }
        });
        // endregion


        return view;
    }

    @Override
    public void onValidationSucceeded() {


        MainVaranegarActivity activity = getVaranegarActvity();
        if (activity == null || activity.isFinishing() || !isResumed())
            return;

        if (!Connectivity.isConnected(activity)) {
            ConnectionSettingDialog connectionSettingDialog = new ConnectionSettingDialog();
            connectionSettingDialog.show(getChildFragmentManager(), "ConnectionSettingDialog");
            return;
        }


        SysConfigModel serverAddressConfig = new SysConfigManager(getContext()).read(ConfigKey.LocalServerAddress, SysConfigManager.local);
        if (serverAddressConfig == null) {
            CuteMessageDialog alert = new CuteMessageDialog(getContext());
            alert.setMessage(R.string.please_set_settings);
            alert.setTitle(R.string.error);
            alert.setIcon(Icon.Error);
            alert.setPositiveButton(R.string.ok, null);
            alert.show();
            return;
        }
        PingApi pingApi = new PingApi();
        setEnabled(false);
        loginButton.setProgress(1);
        pingApi.refreshBaseServerUrl(getContext(), new PingApi.PingCallback() {
            @Override
            public void done(String ipAddress) {

                final UserManager userManager = new UserManager(getContext());
                final String username = userNameEditText.getText().toString().trim();
                final String password = HelperMethods.convertToEnglishNumbers(passwordEditText.getText().toString().trim());

                if (username == null || username.isEmpty() || password == null || password.isEmpty())
                {
                    showErrorMessage(R.string.please_enter_user_name_and_password);
                    setEnabled(true);
                    loginButton.setProgress(0);
                    return;
                }


                String deviceId = getDeviceId();
                if (deviceId == null) {
                    showErrorMessage(R.string.device_id_is_not_available);
                    setEnabled(true);
                    loginButton.setProgress(0);
                    return;
                }

                DeviceApi deviceApi = new DeviceApi(getContext());
                CompanyDeviceAppData data = new CompanyDeviceAppData();
                data.DeviceModelName = Build.MODEL;
                data.IMEI = deviceId;
                data.UserName = username;
                data.IsSupervisor = true;
                LicenseRequestBody body = new LicenseRequestBody();
                body.companyDeviceAppData = data;
                deviceApi.runWebRequest(deviceApi.checkLicense(body), new WebCallBack<CompanyDeviceAppResult>() {
                    @Override
                    protected void onFinish() {

                    }

                    @Override
                    protected void onSuccess(CompanyDeviceAppResult result, Request request) {
//                        if (result.Type == 200)
                            userManager.login(username, password
                                    , new OnTokenAcquired() {
                                        @Override
                                        public void run(Token token) {
                                            try {
                                                AccountManager accountManager = new AccountManager();
                                                accountManager.writeToFile(token, getContext(), "user.token");
                                                final UserModel user = userManager.getUser(username);
                                                user.LoginDate = new Date();
                                                UserManager.writeToFile(user, getContext());
                                                VaranegarApplication.getInstance().getDbHandler().emptyAllTablesExcept(User.UserTbl, SysConfig.SysConfigTbl);
                                                MainVaranegarActivity activity = getVaranegarActvity();
                                                if (activity != null && !activity.isFinishing() && isResumed())
                                                    activity.putFragment(new MainFragment());
                                                setEnabled(true);
                                                loginButton.setProgress(0);
                                                getTrackingLicense();
                                                SysConfigManager sysConfigManager = new SysConfigManager(getContext());
                                                sysConfigManager.sync(new UpdateCall() {
                                                    @Override
                                                    protected void onFinish() {
                                                    }
                                                });
                                            } catch (Exception e) {
                                                Timber.e(e);
                                                if (isResumed()) {
                                                    setEnabled(true);
                                                    loginButton.setProgress(0);
                                                    MainVaranegarActivity activity = getVaranegarActvity();
                                                    if (activity != null && !activity.isFinishing())
                                                        activity.showSnackBar(getContext().getString(R.string.login_failed), MainVaranegarActivity.Duration.Short);
                                                }
                                            }
                                        }
                                    }, new OnError() {
                                        @Override
                                        public void onAuthenticationFailure(String error, String description) {
                                            MainVaranegarActivity activity = getVaranegarActvity();
                                            if (activity != null && !activity.isFinishing() && isResumed())
                                                activity.showSnackBar(description, MainVaranegarActivity.Duration.Short);
                                            setEnabled(true);
                                            loginButton.setProgress(0);
                                        }

                                        @Override
                                        public void onNetworkFailure(Throwable t) {
                                            MainVaranegarActivity activity = getVaranegarActvity();
                                            if (activity != null && !activity.isFinishing() && isResumed())
                                                activity.showSnackBar(R.string.connection_to_server_failed, MainVaranegarActivity.Duration.Short);
                                            setEnabled(true);
                                            loginButton.setProgress(0);
                                        }
                                    });
//                        else {
//                            showErrorMessage(result.Message);
//                            setEnabled(true);
//                            loginButton.setProgress(0);
//                        }

                    }

                    @Override
                    protected void onApiFailure(ApiError error, Request request) {
                        String e = WebApiErrorBody.log(error, getContext());
                        setEnabled(true);
                        showErrorMessage(e);
                        loginButton.setProgress(0);
                    }

                    @Override
                    protected void onNetworkFailure(Throwable t, Request request) {
                        setEnabled(true);
                        loginButton.setProgress(0);
                        Timber.e(t);
                        showErrorMessage(R.string.network_error);
                    }
                });
            }

            @Override
            public void failed() {
                if (isResumed()) {
                    setEnabled(true);
                    loginButton.setProgress(-1);
                    loginButton.setText(R.string.ip_addresses_are_not_found);
                }
            }
        });


//
//
//        Token token = new Token();
//        token.accessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJuYW1laWQiOiJiM2VkMjBlMC1mZTdhLTRlYTgtYmFmNS05ZTZiMjdmMjliMDgiLCJ1bmlxdWVfbmFtZSI6Itix2KfYrdmE2Ycg2YXYudi42YXZiiIsImh0dHA6Ly9zY2hlbWFzLm1pY3Jvc29mdC5jb20vYWNjZXNzY29udHJvbHNlcnZpY2UvMjAxMC8wNy9jbGFpbXMvaWRlbnRpdHlwcm92aWRlciI6IkFTUC5ORVQgSWRlbnRpdHkiLCJBc3BOZXQuSWRlbnRpdHkuU2VjdXJpdHlTdGFtcCI6ImIzZWQyMGUwLWZlN2EtNGVhOC1iYWY1LTllNmIyN2YyOWIwOCIsInJvbGUiOlsiVXNlciIsIkFwcFVzZXIiXSwiRlRFIjoiMCIsImlzcyI6Imh0dHA6Ly9sb2NhbGhvc3QiLCJhdWQiOiI0MTRlMTkyN2EzODg0ZjY4YWJjNzlmNzI4MzgzN2ZkMSIsImV4cCI6MTU5NzU3MTA5NSwibmJmIjoxNTY2MDM1MDk1fQ.eofK_GlB91CULDJeEEuQW-7DbmmyiVqR5I6OMdWf1Pw";
//        token.expiresIn = 360000000;
//        token.tokenType = "Bearer";
//
//        AccountManager accountManager = new AccountManager();
//        accountManager.writeToFile(token,getContext(),"user.token");
//
//        UserModel userModel = new UserModel();
//        userModel.UserName = "Ali torabi";
//        userModel.UniqueId = UUID.fromString("B3ED20E0-FE7A-4EA8-BAF5-9E6B27F29B08");
//        userModel.LoginDate = new Date();
//        UserManager.writeToFile(userModel,getContext());
//        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
//        SysConfigModel sysConfigModel = new SysConfigModel();
//        sysConfigModel.Value = "79A0D598-0BD2-45B1-BAAA-0A9CF9EFF240:3784C8E6-B379-4812-9F72-5AF14DB5B92D:7E11D8B1-E96B-4738-8117-41F2E75BF48E";
//        sysConfigModel.Name = "OwnerKeys";
//        sysConfigModel.Scope = SysConfigManager.base;
//        sysConfigModel.UniqueId = UUID.randomUUID();
//
//        SysConfigModel languageConfig = new SysConfigModel();
//        languageConfig.Name = "ServerLanguage";
//        languageConfig.Scope = SysConfigManager.cloud;
//        languageConfig.Value = "E612912E-9A2C-4B69-B2FF-90731873683D";
//        languageConfig.UniqueId = UUID.randomUUID();
//
//        try {
//            sysConfigManager.insert(sysConfigModel);
//            sysConfigManager.insert(languageConfig);
//            MainFragment mainFragment = new MainFragment();
//            getVaranegarActvity().pushFragment(mainFragment);
//        } catch (ValidationException e) {
//            e.printStackTrace();
//        } catch (DbException e) {
//            e.printStackTrace();
//        }


    }

    private void showErrorMessage(@StringRes int message) {
        Activity activity1 = getVaranegarActvity();
        if (activity1 != null && !activity1.isFinishing() && isResumed()) {
            CuteMessageDialog dialog = new CuteMessageDialog(activity1);
            dialog.setMessage(message);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.setIcon(Icon.Error);
            dialog.setTitle(R.string.error);
            dialog.show();
        }
    }

    private void showErrorMessage(String message) {
        Activity activity1 = getVaranegarActvity();
        if (activity1 != null && !activity1.isFinishing() && isResumed()) {
            CuteMessageDialog dialog = new CuteMessageDialog(activity1);
            dialog.setMessage(message);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.setIcon(Icon.Error);
            dialog.setTitle(R.string.error);
            dialog.show();
        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error :
                errors) {
            String errorMessage = getString(com.varanegar.vaslibrary.R.string.error);
            if (error.getViolation().equals(NotEmptyChecker.class))
                errorMessage = getString(com.varanegar.vaslibrary.R.string.not_empty);

            if (error.getField() instanceof EditText) {
                ((EditText) error.getField()).setError(errorMessage);
                ((EditText) error.getField()).requestFocus();
            } else
                getVaranegarActvity().showSnackBar(errorMessage, MainVaranegarActivity.Duration.Short);
        }
    }


    @Nullable
    public String getDeviceId() {
        String deviceId = "";
        TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        try {
            if (Build.VERSION.SDK_INT >= 29) {
                deviceId = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
            } else {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    Timber.e("Manifest.permission.READ_PHONE_STATE Permission not granted");
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE);
                } else {
                    if (telephonyManager != null)
                        deviceId = telephonyManager.getDeviceId();
                    else
                        Timber.e("telephonyManager is null!");
                }
                if (deviceId == null || deviceId.isEmpty()) {
                    Timber.e("Device Id " + deviceId + " is wrong!!");
                    return null;
                }
            }
            return deviceId;
        } catch (Exception ex) {
            Timber.e(ex);
            return null;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 12456) {
            if (permissions[0].equals(Manifest.permission_group.PHONE) && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                getTrackingLicense();
        }
    }

    private void getTrackingLicense() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M && Build.VERSION.SDK_INT < 29) {
            int phonePermission = getActivity().checkSelfPermission(Manifest.permission_group.PHONE);
            if (phonePermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.READ_PHONE_STATE
                }, 12456);
                return;
            }
        }

        if (TrackingLicense.readLicense(getContext()) == null) {
            final String deviceId = TrackingLicense.getDeviceId(getContext());
            if (deviceId == null) {
                getVaranegarActvity().showSnackBar(R.string.device_id_is_not_available, MainVaranegarActivity.Duration.Short);
                return;
            }
            TrackingLogManager.addLog(getActivity(), LogType.LICENSE, LogLevel.Info, "Device IMEI = " + deviceId);
            com.varanegar.vaslibrary.manager.locationmanager.LocationManager locationManager = new com.varanegar.vaslibrary.manager.locationmanager.LocationManager(getContext());
            locationManager.downloadTrackingLicense(deviceId, new com.varanegar.vaslibrary.manager.locationmanager.LocationManager.DownloadCallBack() {
                @Override
                public void done() {

                }

                @Override
                public void failed(String error) {

                }
            });
        }
    }
}
