package com.varanegar.vaslibrary.webapi.appversion;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import com.google.gson.GsonBuilder;
import com.varanegar.framework.base.AppVersionInfo;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.network.gson.VaranegarGsonBuilder;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.jobscheduler.JobSchedulerService;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

/**
 * Created by A.Torabi on 2/10/2018.
 */

public class AppVersionApi extends BaseApi implements IAppVersionApi {
    private Call<String> getLatestVersionCall;

    public void cancelCheckVersion() {
        if (getLatestVersionCall != null && getLatestVersionCall.isExecuted() && !getLatestVersionCall.isCanceled())
            getLatestVersionCall.cancel();
    }

    public AppVersionApi(Context context) {
        super(context);
    }

    @Override
    public Call<ResponseBody> downloadApk(String fileName) {
        GsonBuilder gsonBuilder = VaranegarGsonBuilder.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                .build();
        return retrofit.create(IAppVersionApi.class).downloadApk(fileName);
    }

    @Override
    public Call<String> getLatestVersion(String subSystemTypesId) {
        GsonBuilder gsonBuilder = VaranegarGsonBuilder.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                .build();
        return retrofit.create(IAppVersionApi.class).getLatestVersion(subSystemTypesId);
    }

    private AppVersionInfo regexApkName(String apkName) {
        Timber.d("apk name on the server is = " + apkName);
        if (apkName == null || apkName.isEmpty())
            return null;
        AppVersionInfo appVersionInfo = new AppVersionInfo();
        String[] splitedFullName = apkName.split("_");
        String versionInfo = splitedFullName[1];
        String[] splitedVersionInfo = versionInfo.split("-");
        String[] versionNumber = splitedVersionInfo[0].substring(1).split("\\.");
        appVersionInfo.VersionMajor = Integer.valueOf(versionNumber[0]);
        appVersionInfo.VersionMinor = Integer.valueOf(versionNumber[1]);
        appVersionInfo.VersionPatch = Integer.valueOf(versionNumber[2]);
        appVersionInfo.VersionBuild = Integer.valueOf(versionNumber[3]);
        if (splitedVersionInfo.length == 2) {
            appVersionInfo.VersionType = splitedVersionInfo[1];
        } else if (splitedVersionInfo.length == 3) {
            appVersionInfo.VersionType = splitedVersionInfo[1];
            appVersionInfo.VersionVariant = splitedVersionInfo[2];
        }
        appVersionInfo.VersionCode = getVersionCode(appVersionInfo);
        appVersionInfo.FileName = apkName + ".apk";
        return appVersionInfo;
    }

    public void getLatestVersion(@NonNull final VersionApiCallBack callBack) {
        final AppVersionApi appVersionApi = new AppVersionApi(getContext());
        getLatestVersionCall = appVersionApi.getLatestVersion(VaranegarApplication.getInstance().getAppId().toString().toUpperCase());
        appVersionApi.runWebRequest(getLatestVersionCall, new WebCallBack<String>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(String result, Request request) {
                try {
                    AppVersionInfo info = appVersionApi.regexApkName(result);
                    if (info == null)
                        callBack.onFailure(getContext().getString(R.string.no_new_version));
                    else
                        callBack.onSuccess(info);
                } catch (Exception ex) {
                    Timber.e(ex);
                    callBack.onFailure(getContext().getString(R.string.error_parsing_data));
                }

            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, getContext());
                callBack.onFailure(err);
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                Timber.e(t);
                callBack.onFailure(getContext().getString(R.string.network_error));
            }

            @Override
            public void onCancel(Request request) {
                super.onCancel(request);
                callBack.onCancel();
            }
        });
    }

    private static int getVersionCode(AppVersionInfo info) {
        return info.VersionMajor * 10000000 + info.VersionMinor * 100000 + info.VersionPatch * 1000 + info.VersionBuild;
    }

    public void downloadAndSave(final String fileName, final ApkDownloadCallBack callBack) {
        final NotificationManager notificationManager = (NotificationManager)
                getContext().getSystemService(NOTIFICATION_SERVICE);
        final String appName = getContext().getString(R.string.app_name);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), JobSchedulerService.ChannelId)
                .setContentTitle(getContext().getString(R.string.downloading_apk) + " " + appName)
                .setContentText(fileName)
                .setSmallIcon(R.mipmap.ic_launcher);

        Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification = builder.build();
        } else {
            notification = builder.getNotification();
        }
        notificationManager.notify(3, notification);

        runWebRequest(downloadApk(fileName), new WebCallBack<ResponseBody>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(ResponseBody result, Request request) {
                try {
                    Timber.d(fileName + " downloaded.");
                    String apkPath = saveToDownloadFolder(result, fileName);
                    Timber.d("apk saved to folder " + apkPath);
                    notificationManager.cancel(3);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
                        intent.setDataAndType(Uri.fromFile(new File(apkPath)), "application/vnd.android.package-archive");
                    else
                        intent.setDataAndType(FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", new File(apkPath)), "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent pIntent = PendingIntent.getActivity(getContext(), (int) System.currentTimeMillis(), intent, 0);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), JobSchedulerService.ChannelId)
                            .setContentTitle(appName + " " + getContext().getString(R.string.was_downloaded_successfully))
                            .setContentText(fileName)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentIntent(pIntent)
                            .setAutoCancel(true);
                    Notification notification = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        notification = builder.build();
                    } else {
                        notification = builder.getNotification();
                    }
                    notificationManager.notify(4, notification);
                    callBack.onSuccess(apkPath);
                } catch (IOException e) {
                    Timber.e(e);
                    callBack.onFailure(getContext().getString(R.string.storage_permission_denied));
                } catch (SecurityException ex) {
                    Timber.e(ex);
                    callBack.onFailure(getContext().getString(R.string.storage_permission_denied));
                }
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, getContext());
                callBack.onFailure(err);
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                Timber.e(t);
                callBack.onFailure(getContext().getString(R.string.network_error));
            }
        });
    }

    private String saveToDownloadFolder(ResponseBody body, String fileName) throws IOException {
        byte[] buffer = new byte[4096];
        InputStream inputStream = body.byteStream();
        String path1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        String path2 = getContext().getExternalFilesDir("apk").getAbsolutePath();
        path1 = path1 + "/" + fileName;
        path2 = path2 + "/" + fileName;
        OutputStream outputStream;
        String path;
        try {
            outputStream = new FileOutputStream(path1);
            path = path1;
        } catch (Exception ex) {
            removeOldApks();
            outputStream = new FileOutputStream(path2);
            path = path2;
        }
        while (true) {
            int read = inputStream.read(buffer);
            if (read == -1) {
                break;
            }
            outputStream.write(buffer, 0, read);
        }
        outputStream.flush();
        return path;
    }

    private void removeOldApks() {
        try {
            new File(getContext().getExternalFilesDir("apk").getAbsolutePath()).delete();
        } catch (Error ignored) {

        } finally {
            getContext().getExternalFilesDir("apk").mkdir();
        }
    }
}
