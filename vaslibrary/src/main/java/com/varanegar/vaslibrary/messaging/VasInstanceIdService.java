package com.varanegar.vaslibrary.messaging;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.telephony.TelephonyManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.OwnerKeysNotFoundException;
import com.varanegar.vaslibrary.manager.sysconfigmanager.OwnerKeysWrapper;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.device.DeviceApi;
import com.varanegar.vaslibrary.webapi.device.UserDeviceTokenViewModel;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by A.Torabi on 5/29/2018.
 */

public class VasInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String token = FirebaseInstanceId.getInstance().getToken();
        if (UserManager.readFromFile(this) != null)
            registerTokenToServer(this, token, getToken(this), null);
    }

    public static void registerTokenToServer(final Context context, String newToken,
                                             final String oldToken,
                                             @Nullable final TokenRefreshCallBack refreshCallBack) {
        if (newToken == null)
            return;
        newToken = newToken.trim().replace(" ", "");
        try {
            SysConfigManager sysConfigManager = new SysConfigManager(context);
            OwnerKeysWrapper ownerKeysWrapper = sysConfigManager.readOwnerKeys();

            final DeviceApi userDeviceTokenApi = new DeviceApi(context);
            final String finalNewToken = newToken;

            UserDeviceTokenViewModel userDeviceTokenViewModel = new UserDeviceTokenViewModel();
            userDeviceTokenViewModel.OwnerId = ownerKeysWrapper.DataOwnerKey;
            userDeviceTokenViewModel.CenterId = ownerKeysWrapper.DataOwnerCenterKey;
            UserModel userModel = UserManager.readFromFile(context);
            if (userModel != null)
                userDeviceTokenViewModel.PersonnelId = userModel.UniqueId.toString();
            userDeviceTokenViewModel.DeviceName = Build.DEVICE;
            userDeviceTokenViewModel.DeviceType = Build.MODEL;
            userDeviceTokenViewModel.OS = "Android";
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            try {
                if (Build.VERSION.SDK_INT >= 29)
                    userDeviceTokenViewModel.IMEI = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                else
                    userDeviceTokenViewModel.IMEI = telephonyManager.getDeviceId();
            } catch (Throwable ex) {
                userDeviceTokenViewModel.IMEI = null;
            }
            userDeviceTokenViewModel.FcmToken = newToken;
            userDeviceTokenViewModel.OldFcmToken = oldToken;
            userDeviceTokenViewModel.DeviceModel = Build.MODEL;
            userDeviceTokenViewModel.OsVersion = Build.VERSION.RELEASE;
            userDeviceTokenViewModel.Manufacturer = Build.MANUFACTURER;
            try {
                PackageInfo packageInfo = ((PackageManager) context.getPackageManager()).getPackageInfo(context.getPackageName(), 0);
                userDeviceTokenViewModel.SoftwareVersion = packageInfo.versionName;
                userDeviceTokenViewModel.SoftwareName = packageInfo.packageName;
            } catch (PackageManager.NameNotFoundException e) {
                Timber.e(e);
            }

            userDeviceTokenApi.
                    runWebRequest(userDeviceTokenApi.
                            registerDeviceToken(userDeviceTokenViewModel),
                            new WebCallBack<Void>() {
                @Override
                protected void onFinish() {

                }

                @Override
                protected void onSuccess(Void result, Request request) {
                    saveToken(context, finalNewToken);
                    Timber.d("New fcm token successfully sent to server");
                    Timber.d("New token = " + finalNewToken);
                    Timber.d("Old token = " + oldToken);
                    if (refreshCallBack != null)
                        refreshCallBack.onSuccess(finalNewToken);
                }


                @Override
                protected void onApiFailure(ApiError error, Request request) {
                    String err = WebApiErrorBody.log(error, context);
                    if (refreshCallBack != null)
                        refreshCallBack.onFailure(finalNewToken, err);
                }

                @Override
                protected void onNetworkFailure(Throwable t, Request request) {
                    Timber.e(t);
                    if (refreshCallBack != null)
                        refreshCallBack.onFailure(finalNewToken, t.getMessage());
                }
            });

        } catch (OwnerKeysNotFoundException e) {
            Timber.d(e.getMessage());
            if (refreshCallBack != null)
                refreshCallBack.onFailure(newToken, e.getMessage());
        }


    }

    private static void saveToken(Context context, String token) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("FirebaseInstanceId", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.apply();
    }

    @Nullable
    public static String getToken(Context context) {
        if (context == null)
            return null;
        SharedPreferences sharedPreferences = context.getSharedPreferences("FirebaseInstanceId", MODE_PRIVATE);
        return sharedPreferences.getString("token", null);
    }

    public static void refreshToken(final Context context, @Nullable final TokenRefreshCallBack callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (context != null) {
                    try {
                        String token = FirebaseInstanceId.getInstance().getToken("559235839122", "FCM");
                        Timber.i("fcm token = " + token);
                        String oldToken = getToken(context);
                        registerTokenToServer(context, token, oldToken, callBack);
                    } catch (Throwable e) {
                        Timber.e(e);
                        if (callBack != null)
                            callBack.onFailure(null, e.getMessage());
                    }
                }
            }
        }).start();
    }

    public interface TokenRefreshCallBack {
        void onSuccess(@NonNull String token);

        void onFailure(@Nullable String token, String error);
    }
}
