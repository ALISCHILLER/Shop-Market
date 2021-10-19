package com.varanegar.vaslibrary.webapi.ping;

import android.content.Context;
import androidx.annotation.Nullable;

import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import timber.log.Timber;

/**
 * Created by A.Torabi on 8/21/2017.
 */

public class PingApi {
    private Call<Void> api;

    public Call<Void> ping(String baseUrl) {
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(baseUrl);
        return builder.build().create(IPingApi.class).ping();
    }

    public interface PingCallback {
        void done(String ipAddress);

        void failed();
    }

    public void cancelPing() {
        if (api != null && api.isExecuted() && !api.isCanceled())
            api.cancel();
    }

    public void refreshBaseServerUrl(final Context context, @Nullable final PingCallback callback) {
        final SysConfigManager sysConfigManager = new SysConfigManager(context);
        final SysConfigModel localIpConfig = sysConfigManager.read(ConfigKey.LocalServerAddress, SysConfigManager.local);
        if (localIpConfig != null && localIpConfig.Value != null && !localIpConfig.Value.isEmpty()) {
            Timber.i("Pinging local server ip : " + localIpConfig.Value);
            try {
                api = ping(localIpConfig.Value);
                api.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(final Call<Void> call, Response<Void> response) {
                        Timber.i("Local server ping was successful");
                        try {
                            sysConfigManager.save(ConfigKey.BaseAddress, localIpConfig.Value, SysConfigManager.local);
                            Timber.i("local ip address was set to " + localIpConfig.Value);
                            if (callback != null)
                                callback.done(localIpConfig.Value);
                        } catch (Exception ex) {
                            Timber.e("Setting base address to " + localIpConfig.Value + " failed.");
                            if (callback != null)
                                callback.failed();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        if (call.isCanceled()) {
                            Timber.d("Request canceled");
                            if (callback != null)
                                callback.failed();
                            return;
                        }
                        Timber.i("Pinging local server ip " + localIpConfig.Value + " failed. We will try the valid ip!");
                        final SysConfigModel serverIp = sysConfigManager.read(ConfigKey.ValidServerAddress, SysConfigManager.local);
                        if (serverIp != null && serverIp.Value != null && !serverIp.Value.isEmpty()) {
                            try {
                                final String[] ips = serverIp.Value.split(",");
                                if (ips.length == 1) {
                                    sysConfigManager.save(ConfigKey.BaseAddress, serverIp.Value, SysConfigManager.local);
                                    Timber.i("ip valid " + serverIp.Value + " used as base address");
                                    if (callback != null)
                                        callback.done(serverIp.Value);
                                } else {
                                    api = ping(ips[0]);
                                    api.enqueue(new Callback<Void>() {

                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> response) {
                                            Timber.i("first valid server ping was successful");
                                            try {
                                                sysConfigManager.save(ConfigKey.BaseAddress, ips[0], SysConfigManager.local);
                                                Timber.i("ip valid " + ips[0] + " used as base address");
                                                if (callback != null)
                                                    callback.done(ips[0]);
                                            } catch (Exception ex) {
                                                Timber.e("Setting base address to " + ips[0] + " failed.");
                                                if (callback != null)
                                                    callback.failed();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {
                                            if (call.isCanceled()) {
                                                Timber.d("Request canceled");
                                                if (callback != null)
                                                    callback.failed();
                                                return;
                                            }
                                            Timber.i("Pinging valid server ip " + ips[0] + " failed. We will try the second valid ip!");
                                            try {
                                                sysConfigManager.save(ConfigKey.BaseAddress, ips[1], SysConfigManager.local);
                                                Timber.i("ip valid " + ips[1] + " used as base address");
                                                if (callback != null)
                                                    callback.done(ips[1]);
                                            } catch (Exception e) {
                                                Timber.e("Setting base address to " + ips[1] + " failed.");
                                                if (callback != null)
                                                    callback.failed();
                                            }
                                        }
                                    });
                                }
                            } catch (Exception ex) {
                                Timber.e("Setting base address to " + serverIp.Value + " failed.");
                                if (callback != null)
                                    callback.failed();
                            }
                        } else {
                            Timber.d("valid ip address of the server not found");
                            if (callback != null)
                                callback.failed();
                        }
                    }
                });
            } catch (IllegalArgumentException ex) {
                Timber.e(ex);
                if (callback != null)
                    callback.failed();
            }
        } else {
            Timber.e("Local server address not found");
            if (callback != null)
                callback.failed();
        }
    }
}
