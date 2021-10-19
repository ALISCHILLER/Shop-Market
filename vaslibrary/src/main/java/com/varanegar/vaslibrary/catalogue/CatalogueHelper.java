package com.varanegar.vaslibrary.catalogue;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import android.view.View;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.network.Connectivity;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.catalogue.groupcatalog.GroupCatalogFragment;
import com.varanegar.vaslibrary.catalogue.productcatalog.AlbumFragment;
import com.varanegar.vaslibrary.manager.ProductGroupCatalogManager;
import com.varanegar.vaslibrary.manager.catalogmanager.CatalogManager;
import com.varanegar.vaslibrary.manager.image.ImageManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.UpdateKey;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.ui.dialog.ConnectionSettingDialog;
import com.varanegar.vaslibrary.webapi.ping.PingApi;

import java.util.Date;
import java.util.UUID;

/**
 * Created by A.Torabi on 1/13/2018.
 */

public class CatalogueHelper {
    private final MainVaranegarActivity activity;
    private ProgressDialog progressDialog;

    public CatalogueHelper(MainVaranegarActivity activity) {
        this.activity = activity;
    }

    private void showProgressDialog(@StringRes int title) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setTitle(title);
            progressDialog.setMessage(activity.getString(R.string.please_wait));
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception ignored) {

        }
    }

    public void startDownload() {
        if (!Connectivity.isConnected(activity)) {
            ConnectionSettingDialog connectionSettingDialog = new ConnectionSettingDialog();
            connectionSettingDialog.show(activity.getSupportFragmentManager(), "ConnectionSettingDialog");
            return;
        }
        showProgressDialog(R.string.downloading_pictures);
        PingApi pingApi = new PingApi();
        pingApi.refreshBaseServerUrl(activity, new PingApi.PingCallback() {
            @Override
            public void done(String ipAddress) {
                ProductGroupCatalogManager productGroupCatalogManager = new ProductGroupCatalogManager(activity);
                productGroupCatalogManager.sync(new UpdateCall() {
                    @Override
                    protected void onFinish() {
                        super.onFinish();
                    }

                    @Override
                    protected void onSuccess() {
                        CatalogManager catalogManager = new CatalogManager(activity);
                        catalogManager.sync(new UpdateCall() {
                            @Override
                            protected void onFinish() {
                                super.onFinish();
                            }

                            @Override
                            protected void onSuccess() {
                                SysConfigManager sysConfigManager = new SysConfigManager(activity);
                                final SysConfigModel catalogType = sysConfigManager.read(ConfigKey.CatalogType, SysConfigManager.cloud);
                                if (catalogType == null) {
                                    activity.showSnackBar(R.string.catalog_type_not_found_in_settings, MainVaranegarActivity.Duration.Short);
                                    return;
                                }
                                final CatalogManager manager = new CatalogManager(activity);
                                final UUID type = UUID.fromString(catalogType.Value);
                                final boolean groupCatalog = type.equals(CatalogManager.BASED_ON_PRODUCT_GROUP);
                                manager.syncPhotos(groupCatalog, false, new ImageManager.ImageDownloadCallBack() {
                                    @Override
                                    public void downloaded(int count, int total) {

                                    }

                                    @Override
                                    public void notFound(int count, int total) {

                                    }

                                    @Override
                                    public void apiFailure(int count, int total) {

                                    }

                                    @Override
                                    public void networkFailure(int count, int total) {

                                    }

                                    @Override
                                    public void saveFailure(int count, int total) {

                                    }

                                    @Override
                                    public void total(final int downloaded, final int notFound, final int apiFailure, final int networkFailure, final int saveFailure, final int total) {
                                        if (!activity.isFinishing()) {
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (progressDialog != null && progressDialog.isShowing()) {
                                                        String message = downloaded + " " + activity.getString(R.string.from) + " " + total + " " + activity.getString(R.string.ta) + " " + activity.getString(R.string.image_downloaded);
                                                        if ((notFound + apiFailure + networkFailure + saveFailure) > 0)
                                                            message += "\n" + activity.getString(R.string.download) + " " + (notFound + apiFailure + networkFailure + saveFailure) + " " + activity.getString(R.string.image_failed);
                                                        progressDialog.setMessage(message);
                                                    }
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void finished(final int total) {
                                        activity.getSharedPreferences("CatalogueHelper", Context.MODE_PRIVATE).edit().clear().apply();
                                        if (!activity.isFinishing()) {
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    dismissProgressDialog();
                                                    if (total > 0)
                                                        activity.showSnackBar(R.string.album_downloaded, MainVaranegarActivity.Duration.Short);
                                                    else
                                                        activity.showSnackBar(R.string.empty_album, MainVaranegarActivity.Duration.Short);
                                                }
                                            });
                                        }

                                    }
                                });
                            }

                            @Override
                            protected void onFailure(String error) {
                                super.onFailure(error);
                                dismissProgressDialog();
                                if (!activity.isFinishing()) {
                                    CuteMessageDialog dialog = new CuteMessageDialog(activity);
                                    dialog.setPositiveButton(R.string.ok, null);
                                    dialog.setTitle(R.string.error);
                                    dialog.setMessage(error);
                                    dialog.setIcon(Icon.Error);
                                    dialog.show();
                                }
                            }
                        });
                    }

                    @Override
                    protected void onFailure(String error) {
                        super.onFailure(error);
                        dismissProgressDialog();
                        if (!activity.isFinishing()) {
                            CuteMessageDialog dialog = new CuteMessageDialog(activity);
                            dialog.setPositiveButton(R.string.ok, null);
                            dialog.setTitle(R.string.error);
                            dialog.setMessage(error);
                            dialog.setIcon(Icon.Error);
                            dialog.show();
                        }
                    }
                });


            }

            @Override
            public void failed() {
                dismissProgressDialog();
                if (!activity.isFinishing()) {
                    CuteMessageDialog dialog = new CuteMessageDialog(activity);
                    dialog.setPositiveButton(R.string.ok, null);
                    dialog.setTitle(R.string.error);
                    dialog.setMessage(R.string.network_error);
                    dialog.setIcon(Icon.Error);
                    dialog.show();
                }
            }
        });
    }

    public void openCatalogue(@Nullable final UUID callOrderId, @Nullable final UUID customerId) {
        final CatalogManager catalogManager = new CatalogManager(activity);
        SysConfigManager sysConfigManager = new SysConfigManager(activity);
        SysConfigModel catalogType = sysConfigManager.read(ConfigKey.CatalogType, SysConfigManager.cloud);
        if (catalogType == null) {
            dismissProgressDialog();
            activity.showSnackBar(R.string.catalog_type_not_found_in_settings, MainVaranegarActivity.Duration.Short);
            return;
        }
        UUID type = UUID.fromString(catalogType.Value);
        final boolean groupCatalog = type.equals(CatalogManager.BASED_ON_PRODUCT_GROUP);
        UpdateManager updateManager = new UpdateManager(activity);
        Date date = updateManager.getLog(UpdateKey.CatalogFiles);
        if (!date.equals(UpdateManager.MIN_DATE) || catalogManager.hasFile()) {
            final SharedPreferences sp = activity.getSharedPreferences("CatalogueHelper", Context.MODE_PRIVATE);
            if (sp.getBoolean("IsPrepared", false)) {
                if (!activity.isFinishing())
                    gotoCatalogue(groupCatalog, callOrderId, customerId);
            } else {
                showProgressDialog(R.string.preparing_catalogues_for_the_first_time);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        catalogManager.prepareCatalogue(groupCatalog);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                sp.edit().putBoolean("IsPrepared", true).apply();
                                if (!activity.isFinishing()) {
                                    dismissProgressDialog();
                                    gotoCatalogue(groupCatalog, callOrderId, customerId);
                                }
                            }
                        });
                    }
                }).start();
            }

        } else {
            CuteMessageDialog dialog = new CuteMessageDialog(activity);
            dialog.setNegativeButton(com.varanegar.vaslibrary.R.string.cancel, null);
            dialog.setPositiveButton(com.varanegar.vaslibrary.R.string.download, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new CatalogueHelper(activity).startDownload();
                }
            });
            dialog.setTitle(com.varanegar.vaslibrary.R.string.please_download_catalog);
            dialog.setMessage(R.string.album_not_found_please_download);
            dialog.setIcon(Icon.Alert);
            dialog.setCancelable(false);
            dialog.show();
        }

    }

    private void gotoCatalogue(boolean groupCatalog, @Nullable final UUID callOrderId, @Nullable final UUID customerId) {
        if (groupCatalog) {
            GroupCatalogFragment albumFragment = new GroupCatalogFragment();
            if (callOrderId != null && customerId != null) {
                albumFragment.addArgument("1c886632-a88a-4e73-9164-f6656c219917", callOrderId.toString());
                albumFragment.addArgument("3af8c4e9-c5c7-4540-8678-4669879caa79", customerId.toString());
            }
            activity.pushFragment(albumFragment);
        } else {
            AlbumFragment albumFragment = new AlbumFragment();
            if (callOrderId != null && customerId != null) {
                albumFragment.addArgument("1c886632-a88a-4e73-9164-f6656c219917", callOrderId.toString());
                albumFragment.addArgument("3af8c4e9-c5c7-4540-8678-4669879caa79", customerId.toString());
            }
            activity.pushFragment(albumFragment);
        }
    }


}
