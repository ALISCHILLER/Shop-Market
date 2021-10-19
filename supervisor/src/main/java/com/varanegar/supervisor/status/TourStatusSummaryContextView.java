package com.varanegar.supervisor.status;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.component.ProgressView;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.ItemContextView;
import com.varanegar.framework.util.report.CustomViewHolder;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.ReportView;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.supervisor.R;
import com.varanegar.supervisor.webapi.SupervisorApi;
import com.varanegar.supervisor.webapi.TourCustomerSummaryView;
import com.varanegar.supervisor.webapi.TourCustomerSummaryViewModel;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.webapi.tour.TourStatus;
import com.varanegar.supervisor.webapi.TourStatusSummaryViewModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;

import java.util.List;

import okhttp3.Request;
import okhttp3.ResponseBody;
import timber.log.Timber;

/**
 * Created by A.Torabi on 7/4/2018.
 */

class TourStatusSummaryContextView extends ItemContextView<TourStatusSummaryViewModel> {
    private MainVaranegarActivity activity;
    private ReportView reportView;
    private ProgressView progressView;
    private TextView tourNoTextView;
    private TextView visitorNameTextView;
    private View cancelBtn;
    private View sendBtn;
    public OnTourChanged onTourChanged;

    public interface OnTourChanged {
        void run();
    }

    public TourStatusSummaryContextView(BaseRecyclerAdapter<TourStatusSummaryViewModel> adapter, MainVaranegarActivity context) {
        super(adapter, context);
        activity = context;
    }

    @Override
    public void onStart() {
        super.onStart();
        final TourStatusSummaryViewModel tourStatusSummaryViewModel = adapter.get(getPosition());
        if (tourStatusSummaryViewModel == null || tourStatusSummaryViewModel.UniqueId == null) {
            Timber.e("Tour status view model is null!");
        }
        if (tourStatusSummaryViewModel.TourStatusUniqueId.equals(TourStatus.InProgress)
                || tourStatusSummaryViewModel.TourStatusUniqueId.equals(TourStatus.Received))
            sendBtn.setEnabled(true);
        else
            sendBtn.setEnabled(false);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                dialog.setTitle(R.string.sending_tour);
                dialog.setMessage(R.string.are_you_sure);
                dialog.setIcon(Icon.Warning);
                dialog.setPositiveButton(R.string.yes, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progressView.setMessage(R.string.sending_tour);
                        progressView.start();
                        SupervisorApi api = new SupervisorApi(getContext());
                        api.runWebRequest(api.replicateTour(tourStatusSummaryViewModel.UniqueId.toString())
                                , new WebCallBack<ResponseBody>() {
                                    @Override
                                    protected void onFinish() {
                                        progressView.finish();
                                    }

                                    @Override
                                    protected void onSuccess(ResponseBody result, Request request) {
                                        CuteMessageDialog dialog1 = new CuteMessageDialog(getContext());
                                        dialog1.setIcon(Icon.Success);
                                        dialog1.setPositiveButton(R.string.ok, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                closeContextView();
                                                onTourChanged();
                                            }
                                        });
                                        dialog1.setTitle(R.string.tour_sent);
                                        dialog1.show();
                                    }

                                    @Override
                                    protected void onApiFailure(ApiError error, Request request) {
                                        String err = WebApiErrorBody.log(error, getContext());
                                        showErrorDialog(err);
                                    }

                                    @Override
                                    protected void onNetworkFailure(Throwable t, Request request) {
                                        Timber.e(t);
                                        showErrorDialog(getContext().getString(R.string.error_connecting_to_server));
                                    }
                                });
                    }
                });
                dialog.setNegativeButton(R.string.no, null);
                dialog.show();
            }
        });

        if (tourStatusSummaryViewModel.TourStatusUniqueId.equals(TourStatus.ReadyToSend)
                || tourStatusSummaryViewModel.TourStatusUniqueId.equals(TourStatus.Sent)
                || tourStatusSummaryViewModel.TourStatusUniqueId.equals(TourStatus.InProgress)
                || tourStatusSummaryViewModel.TourStatusUniqueId.equals(TourStatus.Received))
            cancelBtn.setEnabled(true);
        else
            cancelBtn.setEnabled(false);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                dialog.setTitle(R.string.canceling_tour);
                dialog.setMessage(R.string.are_you_sure);
                dialog.setIcon(Icon.Warning);
                dialog.setPositiveButton(R.string.yes, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progressView.setMessage(R.string.canceling_tour);
                        progressView.start();
                        SupervisorApi api = new SupervisorApi(getContext());
                        api.runWebRequest(api.deactivateTour(tourStatusSummaryViewModel.UniqueId.toString())
                                , new WebCallBack<ResponseBody>() {
                                    @Override
                                    protected void onFinish() {
                                        progressView.finish();
                                    }

                                    @Override
                                    protected void onSuccess(ResponseBody result, Request request) {
                                        CuteMessageDialog dialog1 = new CuteMessageDialog(getContext());
                                        dialog1.setIcon(Icon.Success);
                                        dialog1.setPositiveButton(R.string.ok, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                closeContextView();
                                                onTourChanged();
                                            }
                                        });
                                        dialog1.setTitle(R.string.tour_canceled);
                                        dialog1.show();
                                    }

                                    @Override
                                    protected void onApiFailure(ApiError error, Request request) {
                                        String err = WebApiErrorBody.log(error, getContext());
                                        showErrorDialog(err);
                                    }

                                    @Override
                                    protected void onNetworkFailure(Throwable t, Request request) {
                                        Timber.e(t);
                                        showErrorDialog(getContext().getString(R.string.error_connecting_to_server));
                                    }
                                });
                    }
                });
                dialog.setNegativeButton(R.string.no, null);
                dialog.show();
            }
        });
        tourNoTextView.setText(String.valueOf(tourStatusSummaryViewModel.TourNo));
        visitorNameTextView.setText(tourStatusSummaryViewModel.AgentName);
        progressView.start();
        SupervisorApi api = new SupervisorApi(getContext());
        api.runWebRequest(api.tourCustomers(tourStatusSummaryViewModel.UniqueId.toString()), new WebCallBack<List<TourCustomerSummaryViewModel>>() {
            @Override
            protected void onFinish() {
                progressView.finish();
            }

            @Override
            protected void onSuccess(List<TourCustomerSummaryViewModel> result, Request request) {
                SimpleReportAdapter<TourCustomerSummaryViewModel> tourCustomerAdapter = new SimpleReportAdapter<TourCustomerSummaryViewModel>(activity, TourCustomerSummaryViewModel.class) {
                    @Override
                    public void bind(ReportColumns columns, TourCustomerSummaryViewModel entity) {
                        bindRowNumber(columns);
                        columns.add(bind(entity, TourCustomerSummaryView.CustomerCode, getContext().getString(R.string.customer_code)).setSortable());
                        columns.add(bind(entity, TourCustomerSummaryView.CustomerName, getContext().getString(R.string.customer_name)).setWeight(1.5f).setSortable());
                        columns.add(bind(entity, TourCustomerSummaryView.StoreName, getContext().getString(R.string.store_name)).setWeight(1.5f));
                        columns.add(bind(entity, TourCustomerSummaryView.VisitStatusName, getContext().getString(R.string.status)).setSortable());
                        columns.add(bind(entity, TourCustomerSummaryView.NoSaleReasonName, getContext().getString(R.string.no_sales_reasone_name)).setWeight(2));
                        columns.add(bind(entity, TourCustomerSummaryView.DistributionPDate, getContext().getString(R.string.distribution_date)).setWeight(2));
                        columns.add(bind(entity, TourCustomerSummaryView.IsActive, getContext().getString(R.string.active)).setCustomViewHolder(new CustomViewHolder<TourCustomerSummaryViewModel>() {
                            @Override
                            public void onBind(View view, TourCustomerSummaryViewModel entity) {
                                CheckBox checkBox = (CheckBox) view;
                                checkBox.setChecked(entity.IsActive);
                            }

                            @Override
                            public View onCreateView(LayoutInflater inflater, ViewGroup parent) {
                                return inflater.inflate(R.layout.customer_is_active_layout, parent, false);
                            }
                        }));
                    }

                    @Override
                    protected ItemContextView<TourCustomerSummaryViewModel> onCreateContextView() {
                        TourCustomerSummaryContextView contextView = new TourCustomerSummaryContextView(getAdapter(), getContext());
                        return contextView;
                    }
                };
                tourCustomerAdapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));
                tourCustomerAdapter.create(result, null);
                reportView.setAdapter(tourCustomerAdapter);
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, getContext());
                showErrorDialog(err);
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                showErrorDialog(getContext().getString(R.string.connection_to_server_failed));
            }
        });
    }

    private void onTourChanged() {
        onTourChanged.run();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, int position) {
        View view = inflater.inflate(R.layout.fragment_supervisor_tour_status_context_layout, viewGroup, false);
        reportView = view.findViewById(R.id.tour_customer_report_view);
        progressView = view.findViewById(R.id.progress_view);
        tourNoTextView = view.findViewById(R.id.tour_no_text_view);
        visitorNameTextView = view.findViewById(R.id.visitor_name_text_view);
        cancelBtn = view.findViewById(R.id.cancel_btn);
        sendBtn = view.findViewById(R.id.send_btn);
        return view;
    }

    private void showErrorDialog(String err) {
        Context context = getContext();
        if (context != null) {
            CuteMessageDialog dialog = new CuteMessageDialog(context);
            dialog.setTitle(R.string.error);
            dialog.setIcon(Icon.Error);
            dialog.setMessage(err);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        }
    }
}
