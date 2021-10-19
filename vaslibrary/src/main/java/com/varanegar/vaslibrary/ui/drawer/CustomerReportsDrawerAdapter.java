package com.varanegar.vaslibrary.ui.drawer;

import android.app.ProgressDialog;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.View;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.network.Connectivity;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.component.drawer.BaseDrawerItem;
import com.varanegar.framework.util.component.drawer.DrawerAdapter;
import com.varanegar.framework.util.component.drawer.DrawerItem;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.customercardex.CustomerCardexManager;
import com.varanegar.vaslibrary.manager.oldinvoicemanager.OldInvoiceManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.ui.dialog.ConnectionSettingDialog;
import com.varanegar.vaslibrary.ui.dialog.FromDateToDateDialog;
import com.varanegar.vaslibrary.ui.fragment.CustomersContentFragment;
import com.varanegar.vaslibrary.ui.fragment.order.CustomerSaveOrderFragment;
import com.varanegar.vaslibrary.ui.report.CustomerCardexReportFragment;
import com.varanegar.vaslibrary.ui.report.CustomerOpenInvoicesReportFragment;
import com.varanegar.vaslibrary.ui.report.CustomerStatuseSummaryReportFragment;
import com.varanegar.vaslibrary.ui.report.OldInvoiceDetailReportFragment;
import com.varanegar.vaslibrary.ui.report.OldInvoicesReportFragment;
import com.varanegar.vaslibrary.ui.report.target.TargetHeaderReport;
import com.varanegar.vaslibrary.webapi.ping.PingApi;

import java.util.Date;
import java.util.UUID;

/**
 * Created by s.foroughi on 12/04/2017.
 */

public class CustomerReportsDrawerAdapter extends DrawerAdapter {

    private void gotoReportFragment(VaranegarFragment fragment) {
        Fragment currentFragment = getActivity().getCurrentFragment();
        if (currentFragment instanceof CustomerSaveOrderFragment || currentFragment instanceof CustomersContentFragment)
            getActivity().pushFragment(fragment);
        else
            getActivity().putFragment(fragment);
        getActivity().toggleDrawer();
    }

    public CustomerReportsDrawerAdapter(final MainVaranegarActivity activity, final UUID customerId) {
        super(activity);
        final SysConfigManager sysConfigManager = new SysConfigManager(activity);
        SysConfigModel sysConfigModel = sysConfigManager.read(ConfigKey.ViewCustomerCardexReport, SysConfigManager.cloud);


        add(new BaseDrawerItem(activity) {
            @Override
            protected void onCreateView() {
                activity.getLayoutInflater().inflate(R.layout.customer_adapter_header_layout, this);
            }
        });
        if (SysConfigManager.compare(sysConfigModel, true))
            add(new DrawerItem(activity, R.string.customercardex, R.drawable.ic_report_24dp).setClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CustomerCardexReportFragment fragment = new CustomerCardexReportFragment();
                    fragment.addArgument("ac2208bc-a990-4c28-bbfc-6f143a6aa9c2", String.valueOf(customerId));
                    gotoReportFragment(fragment);
                }
            }));
        sysConfigModel = sysConfigManager.read(ConfigKey.ViewCustomerOpenInvoicesReport, SysConfigManager.cloud);
        if (SysConfigManager.compare(sysConfigModel, true))
            add(new DrawerItem(activity, R.string.open_invoices, R.drawable.ic_report_24dp).setClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CustomerOpenInvoicesReportFragment fragment = new CustomerOpenInvoicesReportFragment();
                    fragment.addArgument("ac2208bc-a990-4c28-bbfc-6f143a6aa9c2", String.valueOf(customerId));
                    gotoReportFragment(fragment);
                }
            }));

        sysConfigModel = sysConfigManager.read(ConfigKey.ViewCustomerInvoicesReport, SysConfigManager.cloud);
        if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist) && SysConfigManager.compare(sysConfigModel, true))
            add(new DrawerItem(activity, R.string.last_purchases, R.drawable.ic_order_history_24dp).setClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OldInvoicesReportFragment fragment = new OldInvoicesReportFragment();
                    fragment.addArgument("ac2208bc-a990-4c28-bbfc-6f143a6aa9c2", String.valueOf(customerId));
                    gotoReportFragment(fragment);
                }
            }));

        sysConfigModel = sysConfigManager.read(ConfigKey.ViewCustomerSaleHistoryReport, SysConfigManager.cloud);
        if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist) && SysConfigManager.compare(sysConfigModel, true))
            add(new DrawerItem(activity, R.string.buy_summury, R.drawable.ic_purchase_summary_24dp).setClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OldInvoiceDetailReportFragment fragment = new OldInvoiceDetailReportFragment();
                    fragment.addArgument("ac2208bc-a990-4c28-bbfc-6f143a6aa9c2", String.valueOf(customerId));
                    gotoReportFragment(fragment);
                }
            }));
        sysConfigModel = sysConfigManager.read(ConfigKey.ViewCustomerFinanceData, SysConfigManager.cloud);
        if (SysConfigManager.compare(sysConfigModel, true))
            add(new DrawerItem(activity, R.string.statuse_summary, R.drawable.ic_customer_status_24dp).setClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CustomerStatuseSummaryReportFragment fragment = new CustomerStatuseSummaryReportFragment();
                    fragment.addArgument("ac2208bc-a990-4c28-bbfc-6f143a6aa9c2", String.valueOf(customerId));
                    gotoReportFragment(fragment);
                }
            }));
        if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist))
            add(new DrawerItem(activity, R.string.target_report, R.drawable.ic_purchase_summary_24dp).setClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TargetHeaderReport fragment = new TargetHeaderReport();
                    fragment.setCostumer(String.valueOf(customerId));
                    gotoReportFragment(fragment);
                }
            }));
        add(new DrawerItem(activity, R.string.update_customer_reports, R.drawable.ic_autorenew_white_24dp).setClickListener(new View.OnClickListener() {
            ProgressDialog progressDialog;

            private void showProgressDialog() {
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(activity);
                    progressDialog.setMessage(activity.getString(com.varanegar.vaslibrary.R.string.customer_reports_update));
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
                FromDateToDateDialog fromDateToDateDialog = new FromDateToDateDialog();
                fromDateToDateDialog.setCallBack(new FromDateToDateDialog.StartEndDateCallBack() {
                    @Override
                    public void setStartAndEndDate(final Date startDate, final Date endDate) {
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
                                OldInvoiceManager oldInvoiceManager = new OldInvoiceManager(getActivity());
                                oldInvoiceManager.sync(false,false, startDate, endDate, customerId, new UpdateCall() {
                                    @Override
                                    protected void onFinish() {

                                    }

                                    @Override
                                    protected void onSuccess() {
                                        CustomerCardexManager customerCardexManager = new CustomerCardexManager(getActivity());
                                        customerCardexManager.sync(customerId, startDate, endDate, new UpdateCall() {
                                            @Override
                                            protected void onFinish() {

                                            }

                                            @Override
                                            protected void onSuccess() {
                                                dismissProgressDialog();
                                                try {
                                                    sysConfigManager.save(ConfigKey.ReportsPeriod, getActivity().getString(R.string.report_period) + " " + (DateHelper.toString(startDate, DateFormat.MicrosoftDateTime, VasHelperMethods.getSysConfigLocale(getActivity()))).substring(0, 10) + " " + getActivity().getString(R.string.to) + " " + (DateHelper.toString(endDate, DateFormat.MicrosoftDateTime, VasHelperMethods.getSysConfigLocale(getActivity()))).substring(0, 10), SysConfigManager.local);
                                                } catch (ValidationException e) {
                                                    e.printStackTrace();
                                                } catch (DbException e) {
                                                    e.printStackTrace();
                                                }
                                                if (!activity.isFinishing()) {
                                                    CuteMessageDialog dialog = new CuteMessageDialog(activity);
                                                    dialog.setMessage(R.string.updating_customer_reports);
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
                });
                FragmentManager fragmentManager = ((getActivity()).getSupportFragmentManager());
                fromDateToDateDialog.show(fragmentManager, "CheckDialog");
            }
        }));
    }


}
