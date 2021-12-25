package com.varanegar.supervisor.report;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.util.report.ReportView;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.supervisor.IMainPageFragment;
import com.varanegar.supervisor.R;
import com.varanegar.supervisor.VisitorFilter;
import com.varanegar.supervisor.webapi.SupervisorApi;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.ui.list.ProductReturnWithoutRefListAdapter;
import com.varanegar.vaslibrary.ui.report.report_new.customer_group_sales_summary.CustomerGroupSalesSummaryAdapter;
import com.varanegar.vaslibrary.ui.report.report_new.invoice_balance.ProductInvoiceReportAdapter;
import com.varanegar.vaslibrary.ui.report.report_new.products_purchase_history_report.ProductsPurchaseHistoryReportAdapter;
import com.varanegar.vaslibrary.ui.report.report_new.webApi.ReportApi;
import com.varanegar.vaslibrary.ui.report.review.adapter.OrderReviewReportAdapter;
import com.varanegar.vaslibrary.ui.report.review.adapter.ProductReviewReportAdapter;
import com.varanegar.vaslibrary.ui.report.review.adapter.SellReturnReviewReportAdapter;
import com.varanegar.vaslibrary.ui.report.review.adapter.SellReviewReportAdapter;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.reviewreport.ReviewReportViewModel;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import retrofit2.Call;
import timber.log.Timber;

/**
 * Created by A.Torabi on 6/23/2018.
 */

public class ReportsFragment extends IMainPageFragment {

    private ReportConfig config;
    private ReportView reportView;
    private TabLayout reportsTabLayout;
    private TextView errorTextView;

    @Override
    protected View onCreateContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_supervisor_reports_layout, container, false);
        errorTextView = view.findViewById(R.id.error_text_view);
        reportsTabLayout = view.findViewById(R.id.reports_tab_layout);
        reportView = view.findViewById(R.id.report_view);
        Context context = getContext();
        if (context != null) {
            config = new ReportConfig(context);
            reportsTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    refresh(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
            view.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final ReportConfigDialog dialog = new ReportConfigDialog();
                    dialog.onReportConfigUpdate = new ReportConfigDialog.OnReportConfigUpdate() {
                        @Override
                        public void run() {
                            if (isResumed()) {
                                Activity activity = getActivity();
                                if (activity != null && !activity.isFinishing() && isResumed()) {
                                    dialog.dismiss();
                                    config = new ReportConfig(activity);
                                    if (config.getSelectedVisitorId() == null) {
                                        showErrorDialog(getString(R.string.please_select_a_visitor));
                                    } else
                                        refresh(reportsTabLayout.getSelectedTabPosition());
                                }
                            }
                        }
                    };
                    dialog.show(getChildFragmentManager(), "ReportConfigDialog");
                }
            });
            return view;
        } else return null;
    }

    private void refresh(final int tabPosition) {
        Context context = getContext();
        if (context != null) {
            ReportConfig reportConfig = new ReportConfig(context);
            final String fromDate = DateHelper.toString(config.getFromDate(), DateFormat.Date, VasHelperMethods.getSysConfigLocale(getContext()));
            final String toDate = DateHelper.toString(config.getToDate(), DateFormat.Date, VasHelperMethods.getSysConfigLocale(getContext()));
            List<String> dealersId = new ArrayList<>();
            dealersId = VisitorFilter.getList(getContext());
            Call reportApi = null;
            final ReportApi supervisorApi = new ReportApi(getContext());
            if (tabPosition == 0)
                reportApi = supervisorApi.product(dealersId, fromDate, toDate);
            else if (tabPosition == 1)
                reportApi = supervisorApi.CustomerGroupSales(dealersId, fromDate, toDate);
            else if (tabPosition == 2)
                reportApi = supervisorApi.ProductsPurchaseHistoryReport(dealersId, fromDate, toDate);
//            else
//                reportApi = supervisorApi.sellReturn(reportConfig.getSelectedVisitorId(), fromDate, toDate);


            startProgress(R.string.please_wait, R.string.connecting_to_the_server);
            supervisorApi.runWebRequest(reportApi, new WebCallBack<List<? extends ReviewReportViewModel>>() {
                @Override
                protected void onFinish() {
                    finishProgress();
                }

                @Override
                protected void onSuccess(List<? extends ReviewReportViewModel> result, Request request) {
                    if (isResumed()) {
                        Activity activity = getActivity();
                        if (activity != null && !activity.isFinishing()) {
                            if (result.size() == 0) {
                                errorTextView.setVisibility(View.VISIBLE);
                                reportView.setVisibility(View.GONE);
                            } else {
                                errorTextView.setVisibility(View.GONE);
                                reportView.setVisibility(View.VISIBLE);
                            }


                            SimpleReportAdapter adapter = null;
                            if (tabPosition == 0) {
                               // adapter = new OrderReviewReportAdapter(getVaranegarActvity());
                                adapter =new ProductInvoiceReportAdapter(getVaranegarActvity());
                            } else if (tabPosition == 1) {
                              //  adapter = new SellReviewReportAdapter(getVaranegarActvity());
                                adapter = new CustomerGroupSalesSummaryAdapter(getVaranegarActvity());

                            } else if (tabPosition == 2) {
                               // adapter = new ProductReviewReportAdapter(getVaranegarActvity());
                                adapter = new ProductsPurchaseHistoryReportAdapter(getVaranegarActvity());

                            } else if (tabPosition == 3) {
                                adapter = new SellReturnReviewReportAdapter(getVaranegarActvity());
                            }
                            if (adapter != null) {
                                adapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));
                                adapter.create(result, null);
                                reportView.setAdapter(adapter);
                            }
                        }
                    }
                }

                @Override
                protected void onApiFailure(ApiError error, Request request) {
                    if (isResumed()) {
                        Activity activity = getActivity();
                        if (activity != null && !activity.isFinishing()) {
                            errorTextView.setVisibility(View.VISIBLE);
                            reportView.setVisibility(View.GONE);
                            String err = WebApiErrorBody.log(error, getContext());
                            showErrorDialog(err);
                        }
                    }
                }

                @Override
                protected void onNetworkFailure(Throwable t, Request request) {
                    if (isResumed()) {
                        Activity activity = getActivity();
                        if (activity != null && !activity.isFinishing()) {
                            errorTextView.setVisibility(View.VISIBLE);
                            reportView.setVisibility(View.GONE);
                            Timber.e(t);
                            showErrorDialog(getContext().getString(R.string.error_connecting_to_server));
                        }
                    }
                }
            });
        }
    }

    private void showErrorDialog(String error) {
        if (isResumed()) {
            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
            dialog.setTitle(com.varanegar.vaslibrary.R.string.error);
            dialog.setMessage(error);
            dialog.setIcon(Icon.Error);
            dialog.setPositiveButton(com.varanegar.vaslibrary.R.string.ok, null);
            dialog.show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh(0);
    }


}
