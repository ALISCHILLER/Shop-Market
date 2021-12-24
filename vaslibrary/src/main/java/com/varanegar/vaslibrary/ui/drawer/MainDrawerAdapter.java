package com.varanegar.vaslibrary.ui.drawer;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.telephony.TelephonyManager;
import android.view.View;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.network.Connectivity;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.component.drawer.DrawerAdapter;
import com.varanegar.framework.util.component.drawer.DrawerItem;
import com.varanegar.framework.util.component.drawer.DrawerSectionItem;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.catalogue.CatalogueHelper;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.updatemanager.PriceUpdateFlow;
import com.varanegar.vaslibrary.manager.updatemanager.ProductUpdateFlow;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.ui.dialog.ConnectionSettingDialog;
import com.varanegar.vaslibrary.ui.dialog.TrackingLicenseFragment;
import com.varanegar.vaslibrary.ui.fragment.CustomersFragment;
import com.varanegar.vaslibrary.ui.report.report_new.invoice_balance.InvoiceBalanceReportFragment;
import com.varanegar.vaslibrary.ui.report.report_new.customer_group_sales_summary.CustomerGroupSalesSummaryFragment;
import com.varanegar.vaslibrary.ui.report.report_new.products_purchase_history_report.ProductsPurchaseHistoryReportFragment;
import com.varanegar.vaslibrary.ui.report.review.OrderReviewReportFragment;
import com.varanegar.vaslibrary.ui.report.review.ProductReviewReportFragment;
import com.varanegar.vaslibrary.ui.report.review.SellReturnReviewReportFragment;
import com.varanegar.vaslibrary.ui.report.review.SellReviewReportFragment;
import com.varanegar.vaslibrary.ui.report.review.TargetReviewReportFragment;
import com.varanegar.vaslibrary.ui.report.review.TourReviewReportFragment;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.device.CompanyDeviceAppResult;
import com.varanegar.vaslibrary.webapi.device.DeviceApi;
import com.varanegar.vaslibrary.webapi.ping.PingApi;
import com.varanegar.vaslibrary.webapi.rs.RecSysConfig;
import com.varanegar.vaslibrary.webapi.rs.RecommenderSystemApi;
import com.varanegar.vaslibrary.webapi.tracking.CompanyDeviceAppData;
import com.varanegar.vaslibrary.webapi.tracking.LicenseRequestBody;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by atp on 1/29/2017.
 */

public class MainDrawerAdapter extends DrawerAdapter {
    protected DrawerSectionItem reports;
    protected DrawerSectionItem reviewReports;
    protected DrawerSectionItem updates;
    private ProgressDialog recommendationDialog;

    protected void gotoReportFragment(VaranegarFragment fragment, boolean checkConnection) {
        if (!Connectivity.isConnected(getActivity()) && checkConnection) {
            ConnectionSettingDialog dialog = new ConnectionSettingDialog();
            dialog.show(getActivity().getSupportFragmentManager(), "ConnectionSettingDialog");
            return;
        }
        Fragment currentFragment = getActivity().getCurrentFragment();
        if (currentFragment instanceof CustomersFragment)
            getActivity().pushFragment(fragment);
        else
            getActivity().putFragment(fragment);
        getActivity().toggleDrawer();
    }

    public MainDrawerAdapter(final MainVaranegarActivity activity) {

        super(activity);

        add(new UserProfileDrawerItem(getActivity()) {
            @Override
            protected void onClick() {
                activity.closeDrawer();
                getActivity().pushFragment(new UserProfileFragment());
            }
        });
        add(new DrawerItem(activity, R.string.tracking_license, R.drawable.ic_tracking_black_24dp).setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.closeDrawer();
                TrackingLicenseFragment trackingLicenseFragment = new TrackingLicenseFragment();
                activity.pushFragment(trackingLicenseFragment);
            }
        }));
        if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            add(new DrawerItem(activity, R.string.recommended_products_configs, R.drawable.ic_recommender_system_black_24dp).setClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.closeDrawer();
                    recommendationDialog = new ProgressDialog(getActivity());
                    recommendationDialog.setTitle(R.string.please_wait);
                    recommendationDialog.setMessage(getActivity().getString(R.string.downloading_recommended_products_config));
                    recommendationDialog.show();
                    PingApi pingApi = new PingApi();
                    pingApi.refreshBaseServerUrl(getActivity(), new PingApi.PingCallback() {
                        @Override
                        public void done(String ipAddress) {
                            RecommenderSystemApi api = new RecommenderSystemApi(getActivity());
                            api.runWebRequest(api.getConfigs(), new WebCallBack<RecSysConfig>() {
                                @Override
                                protected void onFinish() {

                                }

                                @Override
                                protected void onSuccess(RecSysConfig result, Request request) {
                                    try {
                                        if (result != null && result.BiUrl != null) {
                                            SysConfigManager sysConfigManager = new SysConfigManager(getActivity());
                                            sysConfigManager.save(ConfigKey.RecSysServer, result.BiUrl, SysConfigManager.local);
                                            getRecommendationsLicense();
                                        } else {
                                            Timber.d("Recommendation server ip is not found");
                                            if (recommendationDialog != null && recommendationDialog.isShowing())
                                                recommendationDialog.dismiss();
                                            showErrorMessage(R.string.config_not_found);
                                        }
                                    } catch (Exception e) {
                                        Timber.e(e);
                                        if (recommendationDialog != null && recommendationDialog.isShowing())
                                            recommendationDialog.dismiss();
                                        showErrorMessage(R.string.error_saving_request);
                                    }
                                }

                                @Override
                                protected void onApiFailure(ApiError error, Request request) {
                                    WebApiErrorBody.log(error, getActivity());
                                    if (recommendationDialog != null && recommendationDialog.isShowing())
                                        recommendationDialog.dismiss();
                                    showErrorMessage(R.string.error_saving_request);
                                }

                                @Override
                                protected void onNetworkFailure(Throwable t, Request request) {
                                    Timber.e(t);
                                    if (recommendationDialog != null && recommendationDialog.isShowing())
                                        recommendationDialog.dismiss();
                                    showErrorMessage(R.string.network_error);
                                }
                            });
                        }

                        @Override
                        public void failed() {
                            if (recommendationDialog != null && recommendationDialog.isShowing())
                                recommendationDialog.dismiss();
                            showErrorMessage(R.string.network_error);
                        }
                    });

                }

                private void getRecommendationsLicense() {

                    String deviceId = getDeviceId();
                    if (deviceId == null) {
                        if (recommendationDialog != null && recommendationDialog.isShowing())
                            recommendationDialog.dismiss();
                        showErrorMessage(R.string.device_id_is_not_available);
                        return;
                    }
                    DeviceApi deviceApi = new DeviceApi(getActivity());
                    CompanyDeviceAppData data = new CompanyDeviceAppData();
                    data.DeviceModelName = Build.MODEL;
                    data.IMEI = deviceId;
                    data.UserName = UserManager.readFromFile(getActivity()).UserName;
                    data.IsRecommendedSystem = true;
                    LicenseRequestBody body = new LicenseRequestBody();
                    body.companyDeviceAppData = data;
                    deviceApi.runWebRequest(deviceApi.checkLicense(body), new WebCallBack<CompanyDeviceAppResult>() {
                        @Override
                        protected void onFinish() {
                            if (recommendationDialog != null && recommendationDialog.isShowing())
                                recommendationDialog.dismiss();
                        }

                        @Override
                        protected void onSuccess(CompanyDeviceAppResult result, Request request) {
                            Timber.d("Result for : " + request.body().toString());
                            Timber.d("Type: " + result.Type + " Message: " + result.Message);
                            if (result.Type == 200) {
                                try {
                                    final SysConfigManager sysConfigManager = new SysConfigManager(getActivity());
                                    sysConfigManager.save(ConfigKey.RecSysLicense, "1", SysConfigManager.local);
                                } catch (Exception e) {
                                    Timber.e(e);
                                    showErrorMessage(R.string.error_saving_request);
                                }
                            } else {
                                showErrorMessage(result.Message);
                            }
                        }

                        @Override
                        protected void onApiFailure(ApiError error, Request request) {
                            String e = WebApiErrorBody.log(error, getActivity());
                            showErrorMessage(e);
                        }

                        @Override
                        protected void onNetworkFailure(Throwable t, Request request) {
                            Timber.e(t);
                            showErrorMessage(R.string.network_error);
                        }
                    });

                }

                private void showErrorMessage(@StringRes int message) {
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        CuteMessageDialog dialog = new CuteMessageDialog(getActivity());
                        dialog.setMessage(message);
                        dialog.setPositiveButton(R.string.ok, null);
                        dialog.setIcon(Icon.Error);
                        dialog.setTitle(R.string.error);
                        dialog.show();
                    }
                }

                private void showErrorMessage(String message) {
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        CuteMessageDialog dialog = new CuteMessageDialog(getActivity());
                        dialog.setMessage(message);
                        dialog.setPositiveButton(R.string.ok, null);
                        dialog.setIcon(Icon.Error);
                        dialog.setTitle(R.string.error);
                        dialog.show();
                    }
                }

            @Nullable
            private String getDeviceId() {
                String deviceId = "";
                TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
                try {
                    if (Build.VERSION.SDK_INT >= 29) {
                        deviceId = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
                    } else {
                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                            Timber.e("Manifest.permission.READ_PHONE_STATE Permission not granted");
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_PHONE_STATE}, 123);
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
            }));
        }

        reports = new DrawerSectionItem(activity, this, R.string.reports, R.drawable.ic_report_24dp);

        /**
         * گزارشات قبلی
         */
          //        if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
//            reports.addItem(new DrawerItem(activity, R.string.rep3013).setClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Rep3013 fragment = new Rep3013();
//                    gotoReportFragment(fragment, false);
//                }
//            }));
//            reports.addItem(new DrawerItem(activity, R.string.product_sales_report).setClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    ProductReportFragment fragment = new ProductReportFragment();
//                    gotoReportFragment(fragment, false);
//                }
//            }));
//
//            reports.addItem(new DrawerItem(activity, R.string.return_report_report).setClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    ReturnReportFragment fragment = new ReturnReportFragment();
//                    gotoReportFragment(fragment, false);
//                }
//            }));
//
////            reports.addItem(new DrawerItem(activity, R.string.target_report).setClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View view) {
////                    TargetHeaderReport fragment = new TargetHeaderReport();
////                    fragment.setCostumer("");
////                    gotoReportFragment(fragment, false);
////                }
////            }));
//        }

        /**
         * گزارشات جدید
         */

        if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
            reports.addItem(new DrawerItem(activity, "گزارش مانده فاکتور").setClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InvoiceBalanceReportFragment fragment = new InvoiceBalanceReportFragment();
                    gotoReportFragment(fragment, true);
                }
            }));

            reports.addItem(new DrawerItem(activity, "گزارش خلاصه فروش کالا").setClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProductsPurchaseHistoryReportFragment fragment = new ProductsPurchaseHistoryReportFragment();
                    gotoReportFragment(fragment, true);
                }
            }));

            reports.addItem(new DrawerItem(activity, "گزارش خلاصه فروش گروه مشتری").setClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomerGroupSalesSummaryFragment fragment = new CustomerGroupSalesSummaryFragment();
                    gotoReportFragment(fragment, true);
                }
            }));
        }
        if (!VaranegarApplication.is(VaranegarApplication.AppId.Contractor))
            add(reports);

        reviewReports = new DrawerSectionItem(activity, this, R.string.review_reports, R.drawable.ic_review_report_black_24dp);
        reviewReports.addItem(new DrawerItem(activity, R.string.sell_request).setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderReviewReportFragment fragment = new OrderReviewReportFragment();
                gotoReportFragment(fragment, true);
            }
        }));
        reviewReports.addItem(new DrawerItem(activity, R.string.sell_invoice).setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SellReviewReportFragment fragment = new SellReviewReportFragment();
                gotoReportFragment(fragment, true);
            }
        }));
        reviewReports.addItem(new DrawerItem(activity, R.string.product_sale).setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProductReviewReportFragment fragment = new ProductReviewReportFragment();
                gotoReportFragment(fragment, true);
            }
        }));
//                 TODO: 5/21/2018 temporarily do not show!!
//                reviewReports.addItem(new DrawerItem(activity, R.string.product_group_sale).setClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        ProductGroupReviewReportFragment fragment = new ProductGroupReviewReportFragment();
//                        gotoReportFragment(fragment, true);
//                    }
//                }));
        reviewReports.addItem(new DrawerItem(activity, R.string.return_report).setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SellReturnReviewReportFragment fragment = new SellReturnReviewReportFragment();
                gotoReportFragment(fragment, true);
            }
        }));
        reviewReports.addItem(new DrawerItem(activity, R.string.tour_report).setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TourReviewReportFragment fragment = new TourReviewReportFragment();
                gotoReportFragment(fragment, true);
            }
        }));
        reviewReports.addItem(new DrawerItem(activity, R.string.target_report).setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TargetReviewReportFragment fragment = new TargetReviewReportFragment();
                gotoReportFragment(fragment, true);
            }
        }));
        if (!VaranegarApplication.is(VaranegarApplication.AppId.Contractor) && !VaranegarApplication.is(VaranegarApplication.AppId.Dist))
            add(reviewReports);

        updates = new DrawerSectionItem(activity, this, R.string.updates, R.drawable.ic_autorenew_white_24dp);
        if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            updates.addItem(new DrawerItem(activity, R.string.Inventory_update).setClickListener(new View.OnClickListener() {
                ProgressDialog progressDialog;

                private void showProgressDialog() {
                    if (progressDialog == null) {
                        progressDialog = new ProgressDialog(activity);
                        progressDialog.setMessage(activity.getString(com.varanegar.vaslibrary.R.string.Inventory_update));
                        progressDialog.setCancelable(false);
                    }
                    progressDialog.show();
                }

                private void dismissProgressDialog() {
                    if (activity.isFinishing())
                        return;
                    if (progressDialog != null && progressDialog.isShowing()) {
                        try {
                            progressDialog.dismiss();
                        } catch (Exception ignored) {

                        }
                    }
                }

                @Override
                public void onClick(View view) {
                    activity.closeDrawer();
                    if (!Connectivity.isConnected(activity)) {
                        ConnectionSettingDialog connectionSettingDialog = new ConnectionSettingDialog();
                        connectionSettingDialog.show(activity.getSupportFragmentManager(), "ConnectionSettingDialog");
                        return;
                    }


                    PingApi pingApi = new PingApi();
                    showProgressDialog();
                    pingApi.refreshBaseServerUrl(activity, new PingApi.PingCallback() {
                        @Override
                        public void done(String ipAddress) {
                            ProductUpdateFlow productUpdateFlow = new ProductUpdateFlow(getActivity());
                            productUpdateFlow.syncProductsAndInitPromotionDb(new UpdateCall() {
                                @Override
                                protected void onSuccess() {
                                    dismissProgressDialog();
                                    if (!activity.isFinishing()) {
                                        CuteMessageDialog dialog = new CuteMessageDialog(activity);
                                        dialog.setMessage(R.string.updating_products_completed);
                                        dialog.setTitle(R.string.done);
                                        dialog.setIcon(Icon.Success);
                                        dialog.setPositiveButton(R.string.ok, null);
                                        dialog.show();
                                    }
                                }

                                @Override
                                protected void onFailure(String error) {
                                    dismissProgressDialog();
                                    if (!activity.isFinishing()) {
                                        CuteMessageDialog dialog = new CuteMessageDialog(activity);
                                        dialog.setMessage(error);
                                        dialog.setTitle(R.string.error);
                                        dialog.setIcon(Icon.Error);
                                        dialog.setPositiveButton(R.string.ok, null);
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
            }));
        }
        final SysConfigManager sysConfigManager = new SysConfigManager(activity);
        SysConfigModel advanceDealerCredit = sysConfigManager.read(ConfigKey.DealerAdvanceCreditControl, SysConfigManager.cloud);
        if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist) && SysConfigManager.compare(advanceDealerCredit, true)) {
            updates.addItem(new DrawerItem(activity, R.string.update_advance_dealer_credit).setClickListener(new View.OnClickListener() {

                ProgressDialog progressDialog;

                private void showProgressDialog() {
                    if (progressDialog == null) {
                        progressDialog = new ProgressDialog(activity);
                        progressDialog.setMessage(activity.getString(R.string.updating_advance_dealer_credit));
                    }
                    progressDialog.show();
                }

                private void dismissProgressDialog() {
                    if (!activity.isFinishing())
                        if (progressDialog != null && progressDialog.isShowing())
                            try {
                                progressDialog.dismiss();
                            } catch (Exception ignored) {

                            }
                }

                @Override
                public void onClick(View view) {
                    activity.closeDrawer();
                    if (!Connectivity.isConnected(activity)) {
                        ConnectionSettingDialog connectionSettingDialog = new ConnectionSettingDialog();
                        connectionSettingDialog.show(activity.getSupportFragmentManager(), "ConnectionSettingDialog");
                        return;
                    }
                    PingApi pingApi = new PingApi();
                    showProgressDialog();
                    pingApi.refreshBaseServerUrl(activity, new PingApi.PingCallback() {
                        @Override
                        public void done(String ipAddress) {
                            sysConfigManager.syncAdvanceDealerCredit(new UpdateCall() {
                                @Override
                                protected void onSuccess() {
                                    dismissProgressDialog();
                                    if (!activity.isFinishing()) {
                                        CuteMessageDialog dialog = new CuteMessageDialog(activity);
                                        dialog.setMessage(R.string.updated_advance_dealer_credit);
                                        dialog.setTitle(R.string.done);
                                        dialog.setIcon(Icon.Success);
                                        dialog.setPositiveButton(R.string.ok, null);
                                        dialog.show();
                                    }
                                }

                                @Override
                                protected void onFailure(String error) {
                                    dismissProgressDialog();
                                    if (!activity.isFinishing()) {
                                        CuteMessageDialog dialog = new CuteMessageDialog(activity);
                                        dialog.setMessage(error);
                                        dialog.setTitle(R.string.error);
                                        dialog.setIcon(Icon.Error);
                                        dialog.setPositiveButton(R.string.ok, null);
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
            }));
        }
        SysConfigModel sysConfigModel = sysConfigManager.read(ConfigKey.AllowUpdatePrice, SysConfigManager.cloud);
        if (SysConfigManager.compare(sysConfigModel, true) && !VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            updates.addItem(new DrawerItem(activity, R.string.update_prices).setClickListener(new View.OnClickListener() {
                ProgressDialog progressDialog;

                private void showProgressDialog() {
                    if (progressDialog == null) {
                        progressDialog = new ProgressDialog(activity);
                        progressDialog.setMessage(activity.getString(R.string.updating_prices));
                    }
                    progressDialog.show();
                }

                private void dismissProgressDialog() {
                    if (!activity.isFinishing())
                        if (progressDialog != null && progressDialog.isShowing())
                            try {
                                progressDialog.dismiss();
                            } catch (Exception ignored) {

                            }
                }

                @Override
                public void onClick(View view) {
                    activity.closeDrawer();
                    if (!Connectivity.isConnected(activity)) {
                        ConnectionSettingDialog connectionSettingDialog = new ConnectionSettingDialog();
                        connectionSettingDialog.show(activity.getSupportFragmentManager(), "ConnectionSettingDialog");
                        return;
                    }
                    PingApi pingApi = new PingApi();
                    showProgressDialog();
                    pingApi.refreshBaseServerUrl(activity, new PingApi.PingCallback() {
                        @Override
                        public void done(String ipAddress) {
                            PriceUpdateFlow priceUpdateFlow = new PriceUpdateFlow(getActivity());
                            priceUpdateFlow.syncPriceAndInitPromotionDb(new UpdateCall() {
                                @Override
                                protected void onSuccess() {
                                    dismissProgressDialog();
                                    if (!activity.isFinishing()) {
                                        CuteMessageDialog dialog = new CuteMessageDialog(activity);
                                        dialog.setMessage(R.string.updated_prices_list);
                                        dialog.setTitle(R.string.done);
                                        dialog.setIcon(Icon.Success);
                                        dialog.setPositiveButton(R.string.ok, null);
                                        dialog.show();
                                    }
                                }

                                @Override
                                protected void onFailure(String error) {
                                    dismissProgressDialog();
                                    if (!activity.isFinishing()) {
                                        CuteMessageDialog dialog = new CuteMessageDialog(activity);
                                        dialog.setMessage(error);
                                        dialog.setTitle(R.string.error);
                                        dialog.setIcon(Icon.Error);
                                        dialog.setPositiveButton(R.string.ok, null);
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

            }));
        }

        if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            updates.addItem(new DrawerItem(activity, R.string.download_product_images).setClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.closeDrawer();
                    new CatalogueHelper(activity).startDownload();

                }
            }));
        }
        add(updates);
//        }
    }
}