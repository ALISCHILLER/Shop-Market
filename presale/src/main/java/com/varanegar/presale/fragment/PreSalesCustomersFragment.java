package com.varanegar.presale.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.network.Connectivity;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.component.drawer.DrawerItem;
import com.varanegar.framework.util.component.drawer.DrawerSectionItem;
import com.varanegar.framework.util.component.toolbar.CuteToolbar;
import com.varanegar.presale.ui.PreSalesTourReportDrawerItem;
import com.varanegar.presale.ui.PreSalesTourReportFragment;
import com.varanegar.vaslibrary.catalogue.CatalogueHelper;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.updatemanager.PriceUpdateFlow;
import com.varanegar.vaslibrary.manager.updatemanager.ProductUpdateFlow;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.ui.dialog.ConnectionSettingDialog;
import com.varanegar.vaslibrary.ui.fragment.TourReportFragment;
import com.varanegar.vaslibrary.ui.fragment.clean.CustomersFragment;
import com.varanegar.vaslibrary.ui.report.ProductReportFragment;
import com.varanegar.vaslibrary.ui.report.report_new.customerNoSaleReport.CustomerNoSaleReportFragment;
import com.varanegar.vaslibrary.ui.report.report_new.customer_group_sales_summary.CustomerGroupSalesSummaryFragment;
import com.varanegar.vaslibrary.ui.report.report_new.invoice_balance.InvoiceBalanceReportFragment;
import com.varanegar.vaslibrary.ui.report.report_new.orderReturn_report.RerurnReportFragment;
import com.varanegar.vaslibrary.ui.report.report_new.orderStatus_Report.OrderReportFragment;
import com.varanegar.vaslibrary.ui.report.report_new.products_purchase_history_report.ProductsPurchaseHistoryReportFragment;
import com.varanegar.vaslibrary.webapi.ping.PingApi;

import java.util.UUID;

public class PreSalesCustomersFragment extends CustomersFragment {
    
    //---------------------------------------------------------------------------------------------- onActivityCreated
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addReportToCutePresaleToolbar();
        addUpdateToCutePresaleToolbar();
        setVisibleImageViewMenuIcon(View.GONE);
        getButtonsToolbar().getLinearLayoutToolbarProfile().addView(new PreSalesTourReportDrawerItem(getVaranegarActvity()));
//        setDrawerAdapter(new PreSalesDrawerAdapter(getVaranegarActvity()));
    }
    //---------------------------------------------------------------------------------------------- onActivityCreated


    //---------------------------------------------------------------------------------------------- getSendTourFragment
    @Override
    protected VaranegarFragment getSendTourFragment() {
        return new PreSalesSendTourFragment();
    }
    //---------------------------------------------------------------------------------------------- getSendTourFragment


    //---------------------------------------------------------------------------------------------- getProfileFragment
    @Override
    protected TourReportFragment getProfileFragment() {
        return new PreSalesTourReportFragment();
    }
    //---------------------------------------------------------------------------------------------- getProfileFragment


    //---------------------------------------------------------------------------------------------- getContentFragment
    @Override
    protected VaranegarFragment getContentFragment(UUID selectedItem) {
        return new PreSalesCustomerContentFragment();
    }
    //---------------------------------------------------------------------------------------------- getContentFragment


    //---------------------------------------------------------------------------------------------- getContentTargetFragment
    @Override
    protected VaranegarFragment getContentTargetFragment() {
        return new PreSalesCustomerTargetFragment();
    }
    //---------------------------------------------------------------------------------------------- getContentTargetFragment


    //---------------------------------------------------------------------------------------------- getContentTargetDetailFragment
    @Override
    protected VaranegarFragment getContentTargetDetailFragment() {
        return new PreSalesCustomerTargetDetailFragment();
    }
    //---------------------------------------------------------------------------------------------- getContentTargetDetailFragment


    //---------------------------------------------------------------------------------------------- addReportToCutePresaleToolbar
    private void addReportToCutePresaleToolbar() {
        final MainVaranegarActivity activity = getVaranegarActvity();
        if (activity == null)
            return;
        CuteToolbar cuteToolbar = getButtonsToolbar();

        DrawerSectionItem reports = new DrawerSectionItem(activity, null, com.varanegar.vaslibrary.R.string.reports, com.varanegar.vaslibrary.R.drawable.ic_report_24dp);


        reports.addItem(new DrawerItem(activity, "گزارش مانده فاکتور").setClickListener(v -> {
            InvoiceBalanceReportFragment fragment = new InvoiceBalanceReportFragment();
            activity.pushFragment(fragment, true);
        }));

        reports.addItem(new DrawerItem(activity, "گزارش وضعیت سفارش ها").setClickListener(v -> {
            OrderReportFragment fragment = new OrderReportFragment();
            activity.pushFragment(fragment, true);
        }));

        reports.addItem(new DrawerItem(activity, "گزارش برگشتی").setClickListener(v -> {
            RerurnReportFragment fragment = new RerurnReportFragment();
            activity.pushFragment(fragment, true);
        }));

        reports.addItem(new DrawerItem(activity, "گزارش خلاصه فروش کالا").setClickListener(v -> {
            ProductsPurchaseHistoryReportFragment fragment = new ProductsPurchaseHistoryReportFragment();
            activity.pushFragment(fragment, true);
        }));

        reports.addItem(new DrawerItem(activity, "گزارش خلاصه فروش گروه مشتری").setClickListener(v -> {
            CustomerGroupSalesSummaryFragment fragment = new CustomerGroupSalesSummaryFragment();
            activity.pushFragment(fragment, true);
        }));

        reports.addItem(new DrawerItem(activity, com.varanegar.vaslibrary.R.string.product_sales_report).setClickListener(view -> {
            ProductReportFragment fragment = new ProductReportFragment();
            activity.pushFragment(fragment, false);
        }));

        reports.addItem(new DrawerItem(activity, "مشتریان بدون خرید ماه جاری").setClickListener(v -> {
            CustomerNoSaleReportFragment fragment = new CustomerNoSaleReportFragment();
            activity.pushFragment(fragment, true);
        }));

        reports.closeItems();
        cuteToolbar.getLinearLayoutToolbarReport().addView(reports);
    }
    //---------------------------------------------------------------------------------------------- addReportToCutePresaleToolbar


    //---------------------------------------------------------------------------------------------- addUpdateToCutePresaleToolbar
    private void addUpdateToCutePresaleToolbar() {
        final MainVaranegarActivity activity = getVaranegarActvity();
        if (activity == null)
            return;
        CuteToolbar cuteToolbar = getButtonsToolbar();
        DrawerSectionItem updates = new DrawerSectionItem(activity, null, com.varanegar.vaslibrary.R.string.updates, com.varanegar.vaslibrary.R.drawable.ic_autorenew_white_24dp);

        updates.addItem(new DrawerItem(activity, com.varanegar.vaslibrary.R.string.Inventory_update).setClickListener(new View.OnClickListener() {
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
                                    dialog.setMessage(com.varanegar.vaslibrary.R.string.updating_products_completed);
                                    dialog.setTitle(com.varanegar.vaslibrary.R.string.done);
                                    dialog.setIcon(Icon.Success);
                                    dialog.setPositiveButton(com.varanegar.vaslibrary.R.string.ok, null);
                                    dialog.show();
                                }
                            }

                            @Override
                            protected void onFailure(String error) {
                                dismissProgressDialog();
                                if (!activity.isFinishing()) {
                                    CuteMessageDialog dialog = new CuteMessageDialog(activity);
                                    dialog.setMessage(error);
                                    dialog.setTitle(com.varanegar.vaslibrary.R.string.error);
                                    dialog.setIcon(Icon.Error);
                                    dialog.setPositiveButton(com.varanegar.vaslibrary.R.string.ok, null);
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
                            dialog.setPositiveButton(com.varanegar.vaslibrary.R.string.ok, null);
                            dialog.setTitle(com.varanegar.vaslibrary.R.string.error);
                            dialog.setMessage(com.varanegar.vaslibrary.R.string.network_error);
                            dialog.setIcon(Icon.Error);
                            dialog.show();
                        }
                    }
                });

            }
        }));

        SysConfigManager sysConfigManager = new SysConfigManager(activity);
        SysConfigModel sysConfigModel = sysConfigManager.read(ConfigKey.AllowUpdatePrice, SysConfigManager.cloud);

        if (SysConfigManager.compare(sysConfigModel, true))
            updates.addItem(new DrawerItem(activity, com.varanegar.vaslibrary.R.string.update_prices).setClickListener(new View.OnClickListener() {
                ProgressDialog progressDialog;

                private void showProgressDialog() {
                    if (progressDialog == null) {
                        progressDialog = new ProgressDialog(activity);
                        progressDialog.setMessage(activity.getString(com.varanegar.vaslibrary.R.string.updating_prices));
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
                                        dialog.setMessage(com.varanegar.vaslibrary.R.string.updated_prices_list);
                                        dialog.setTitle(com.varanegar.vaslibrary.R.string.done);
                                        dialog.setIcon(Icon.Success);
                                        dialog.setPositiveButton(com.varanegar.vaslibrary.R.string.ok, null);
                                        dialog.show();
                                    }
                                }

                                @Override
                                protected void onFailure(String error) {
                                    dismissProgressDialog();
                                    if (!activity.isFinishing()) {
                                        CuteMessageDialog dialog = new CuteMessageDialog(activity);
                                        dialog.setMessage(error);
                                        dialog.setTitle(com.varanegar.vaslibrary.R.string.error);
                                        dialog.setIcon(Icon.Error);
                                        dialog.setPositiveButton(com.varanegar.vaslibrary.R.string.ok, null);
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
                                dialog.setPositiveButton(com.varanegar.vaslibrary.R.string.ok, null);
                                dialog.setTitle(com.varanegar.vaslibrary.R.string.error);
                                dialog.setMessage(com.varanegar.vaslibrary.R.string.network_error);
                                dialog.setIcon(Icon.Error);
                                dialog.show();
                            }
                        }
                    });
                }

            }));

        SysConfigModel advanceDealerCredit = sysConfigManager.read(ConfigKey.DealerAdvanceCreditControl, SysConfigManager.cloud);
        if (SysConfigManager.compare(advanceDealerCredit, true))
            updates.addItem(new DrawerItem(activity, com.varanegar.vaslibrary.R.string.update_advance_dealer_credit).setClickListener(new View.OnClickListener() {

                ProgressDialog progressDialog;

                private void showProgressDialog() {
                    if (progressDialog == null) {
                        progressDialog = new ProgressDialog(activity);
                        progressDialog.setMessage(activity.getString(com.varanegar.vaslibrary.R.string.updating_advance_dealer_credit));
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
                                        dialog.setMessage(com.varanegar.vaslibrary.R.string.updated_advance_dealer_credit);
                                        dialog.setTitle(com.varanegar.vaslibrary.R.string.done);
                                        dialog.setIcon(Icon.Success);
                                        dialog.setPositiveButton(com.varanegar.vaslibrary.R.string.ok, null);
                                        dialog.show();
                                    }
                                }

                                @Override
                                protected void onFailure(String error) {
                                    dismissProgressDialog();
                                    if (!activity.isFinishing()) {
                                        CuteMessageDialog dialog = new CuteMessageDialog(activity);
                                        dialog.setMessage(error);
                                        dialog.setTitle(com.varanegar.vaslibrary.R.string.error);
                                        dialog.setIcon(Icon.Error);
                                        dialog.setPositiveButton(com.varanegar.vaslibrary.R.string.ok, null);
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
                                dialog.setPositiveButton(com.varanegar.vaslibrary.R.string.ok, null);
                                dialog.setTitle(com.varanegar.vaslibrary.R.string.error);
                                dialog.setMessage(com.varanegar.vaslibrary.R.string.network_error);
                                dialog.setIcon(Icon.Error);
                                dialog.show();
                            }
                        }
                    });
                }
            }));

        updates.addItem(new DrawerItem(activity, com.varanegar.vaslibrary.R.string.download_product_images).setClickListener(view -> {
            activity.closeDrawer();
            new CatalogueHelper(activity).startDownload();

        }));

        updates.closeItems();
        cuteToolbar.getLinearLayoutToolbarReport().addView(updates);
    }
    //---------------------------------------------------------------------------------------------- addUpdateToCutePresaleToolbar


}
