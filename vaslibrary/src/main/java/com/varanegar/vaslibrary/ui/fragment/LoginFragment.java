package com.varanegar.vaslibrary.ui.fragment;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dd.processbutton.iml.ActionProcessButton;
import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.PopupFragment;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.base.account.AccountManager;
import com.varanegar.framework.base.account.OnError;
import com.varanegar.framework.base.account.OnTokenAcquired;
import com.varanegar.framework.base.account.Token;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.network.Connectivity;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.LocaleHelper;
import com.varanegar.framework.util.component.SearchBox;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.validation.ValidationError;
import com.varanegar.framework.validation.ValidationListener;
import com.varanegar.framework.validation.Validator;
import com.varanegar.framework.validation.annotations.NotEmpty;
import com.varanegar.framework.validation.annotations.NotEmptyChecker;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.BackupManager;
import com.varanegar.vaslibrary.base.LocalModel;
import com.varanegar.vaslibrary.base.SelectLanguageDialog;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.locationmanager.LogLevel;
import com.varanegar.vaslibrary.manager.locationmanager.LogType;
import com.varanegar.vaslibrary.manager.locationmanager.TrackingLicense;
import com.varanegar.vaslibrary.manager.locationmanager.TrackingLogManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.messaging.VasInstanceIdService;
import com.varanegar.vaslibrary.model.TrackingLog;
import com.varanegar.vaslibrary.model.location.Location;
import com.varanegar.vaslibrary.model.sysconfig.SysConfig;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.model.user.User;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.model.user.UserModelRepository;
import com.varanegar.vaslibrary.ui.dialog.ConnectionSettingDialog;
import com.varanegar.vaslibrary.ui.dialog.ImportDialogFragment;
import com.varanegar.vaslibrary.webapi.ping.PingApi;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by atp on 3/7/2017.
 */

public abstract class LoginFragment extends PopupFragment implements ValidationListener {

    Validator validator;
    @NotEmpty
    EditText userNameEditText;
    @NotEmpty
    EditText passwordEditText;
    ActionProcessButton loginButton;
    ImageView settingsImageView;
    ImageView usersImageView;


    @DrawableRes
    protected abstract int getAppIconId();

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

    @Override
    public void onResume() {
        super.onResume();
        MainVaranegarActivity activity = getVaranegarActvity();
        if (activity != null && !activity.isFinishing() && UserManager.readFromFile(activity) != null) {
            activity.putFragment(getTourReportFragment());
        }
    }

    protected abstract TourReportFragment getTourReportFragment();

    int clickCount;
    int maxCount = 8;
    private Toast countToast;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        TextView localeTextView = (TextView) view.findViewById(R.id.language_text_view);
        final Locale locale = LocaleHelper.getPreferredLocal(getContext());
        if (locale != null)
            localeTextView.setText(locale.getLanguage());
        localeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectLanguageDialog dialog = new SelectLanguageDialog();
                dialog.onLanguageSelected = new SelectLanguageDialog.OnLanguageSelected() {
                    @Override
                    public void onSelected(Locale local) {
                        MainVaranegarActivity activity = getVaranegarActvity();
                        if (local != null && activity != null && !activity.isFinishing()) {
                            LocaleHelper.setPreferredLocal(getContext(), local);
                            LocaleHelper.setLocale(getContext(), local.getLanguage());
                            Intent intent = activity.getIntent();
                            getVaranegarActvity().finish();
                            startActivity(intent);
                        }
                    }
                };
                dialog.show(getChildFragmentManager(), "SelectLanguageDialog");
            }
        });

        try {
            String packageName = getContext().getApplicationInfo().packageName;
            String version = getContext().getPackageManager().getPackageInfo(packageName, 0).versionName;
            TextView versionTextView = (TextView) view.findViewById(R.id.version_text_view);
            versionTextView.setText(getString(R.string.app_name) + " " + version);
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e(e);
        }
        countToast = Toast.makeText(getContext(), "Press " + (maxCount - clickCount) + " more times to enable restore option!", Toast.LENGTH_SHORT);
        ((ImageView) view.findViewById(R.id.logo_image_view)).setImageResource(getAppIconId());
        view.findViewById(R.id.logo_image_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCount++;
                if (maxCount - clickCount == 0) {
                    clickCount = 0;
                    countToast.cancel();
                    if (BackupManager.getList(getContext(), BackupManager.BackupType.Full).size() > 0) {
                        ImportDialogFragment importDialog = new ImportDialogFragment();
                        importDialog.setBackupType(BackupManager.BackupType.Full);
                        importDialog.show(getChildFragmentManager(), "ImportDialogFragment");
                    } else {
                        CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                        dialog.setTitle(R.string.error);
                        dialog.setMessage(R.string.there_is_no_backup_file);
                        dialog.setIcon(Icon.Alert);
                        dialog.setPositiveButton(R.string.ok, null);
                        dialog.show();
                    }
                } else if (clickCount >= 4) {
                    countToast.setText("Press " + (maxCount - clickCount) + " more times to enable restore option!");
                    countToast.show();
                }

            }
        });
        userNameEditText = (EditText) view.findViewById(R.id.user_name_edit_text);
        passwordEditText = (EditText) view.findViewById(R.id.password_edit_text);
        loginButton = (ActionProcessButton) view.findViewById(R.id.ok_button);
        loginButton.setMode(ActionProcessButton.Mode.ENDLESS);
        usersImageView = (ImageView) view.findViewById(R.id.user_name_image_view);
        settingsImageView = (ImageView) view.findViewById(R.id.settings_image_view);
        usersImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(usersImageView.getWindowToken(), 0);
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
        settingsImageView.setOnClickListener(view1 -> {
            SettingDialogFragment settingDialogFragment = new SettingDialogFragment();
            settingDialogFragment.setCancelable(false);
            settingDialogFragment.setConfigListener(new SettingDialogFragment.SettingsUpdateListener() {
                @Override
                public void onSettingsUpdated() {
                    Locale locale1 = getLanguage(getContext());
                    LocaleHelper.setLocale(getContext(), locale1.getLanguage());
                    Intent intent = getVaranegarActvity().getIntent();
                    getVaranegarActvity().finish();
                    startActivity(intent);
                }
            });
            settingDialogFragment.show(getChildFragmentManager(), "SettingDialogFragment");
        });
        loginButton.setOnClickListener(view12 -> {
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/download/" + "Sepehr_varanegar.apk")), "application/vnd.android.package-archive");
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
            userNameEditText.setError(null);
            passwordEditText.setError(null);
            validator.validate(LoginFragment.this);
        });
        return view;
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
                //try{
                final UserManager userManager = new UserManager(getContext());
                final String username = userNameEditText.getText().toString().trim();
                final UserModel user = userManager.getUsers(username);
                final String password = HelperMethods.convertToEnglishNumbers(passwordEditText.getText().toString().trim());
                if (user != null) {
                    userManager.login(user.UserName, password
                            , new OnTokenAcquired() {
                                @Override
                                public void run(Token token) {
                                    try {
                                        BackupManager.exportData(getContext(), true);
                                        AccountManager accountManager = new AccountManager();
                                        accountManager.writeToFile(token, getContext(), "user.token");
                                        user.LoginDate = new Date();
                                        UserManager.writeToFile(user, getContext());
                                        VaranegarApplication.getInstance().getDbHandler().emptyAllTablesExcept(User.UserTbl, SysConfig.SysConfigTbl, Location.LocationTbl, TrackingLog.TrackingLogTbl);
                                        MainVaranegarActivity activity = getVaranegarActvity();
                                        if (activity != null && !activity.isFinishing() && isResumed())
                                            activity.putFragment(getTourReportFragment());
                                        setEnabled(true);
                                        loginButton.setProgress(0);
                                        getTrackingLicense();
                                        VasInstanceIdService.refreshToken(getContext(), new VasInstanceIdService.TokenRefreshCallBack() {
                                            @Override
                                            public void onSuccess(@NonNull String token) {
                                                Timber.d("Token update succeeded. Token = " + token);
                                            }

                                            @Override
                                            public void onFailure(@Nullable String token, String error) {
                                                Timber.d("Token update failed. Error=" + error + "  Token=" + token);
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
                } else {
                    MainVaranegarActivity activity = getVaranegarActvity();
                    if (activity != null && !activity.isFinishing() && isResumed())
                        activity.showSnackBar(R.string.user_not_found, MainVaranegarActivity.Duration.Short);
                    setEnabled(true);
                    loginButton.setProgress(0);
                }
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
            int phonePermission = getContext().checkSelfPermission(Manifest.permission_group.PHONE);
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

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        MainVaranegarActivity activity = getVaranegarActvity();
        if (activity != null && isResumed()) {
            for (ValidationError error :
                    errors) {
                String errorMessage = getString(R.string.error);
                if (error.getViolation().equals(NotEmptyChecker.class))
                    errorMessage = getString(R.string.not_empty);

                if (error.getField() instanceof EditText) {
                    ((EditText) error.getField()).setError(errorMessage);
                    ((EditText) error.getField()).requestFocus();
                } else
                    activity.showSnackBar(errorMessage, MainVaranegarActivity.Duration.Short);
            }
        }
    }

}
