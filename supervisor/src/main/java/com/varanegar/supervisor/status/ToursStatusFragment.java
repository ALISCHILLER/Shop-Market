package com.varanegar.supervisor.status;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.varanegar.framework.network.gson.VaranegarGsonBuilder;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.util.report.CustomViewHolder;
import com.varanegar.framework.util.report.ReportAdapter;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.ReportView;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.supervisor.IMainPageFragment;
import com.varanegar.supervisor.R;
import com.varanegar.supervisor.VisitorFilter;
import com.varanegar.supervisor.model.StatusConfigModel;
import com.varanegar.supervisor.model.changeOrdersStatus.ChangeOrdersStatusmModel;
import com.varanegar.supervisor.model.reviewreport.ReviewreportModel;
import com.varanegar.supervisor.model.reviewreport.ReviewreportView;
import com.varanegar.supervisor.model.reviewreport.items;
import com.varanegar.supervisor.utill.dialog.BackMessageDialog;
import com.varanegar.supervisor.webapi.SupervisorApi;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.model.customer.SupervisorCustomer;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.Request;
import okhttp3.ResponseBody;
import timber.log.Timber;

/**
 * Created by A.Torabi on 6/23/2018.
 */

public class ToursStatusFragment extends IMainPageFragment {
    private ReportView summaryReportView;
    private SimpleReportAdapter adapter;
    private TextView errorTextView;
    ArrayList<String> arr = new ArrayList();
    private FloatingActionButton fab_send;
    private String status_options;
    @Override
    protected View onCreateContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tour_status_layout, container, false);
        errorTextView = view.findViewById(R.id.error_text_view);
        summaryReportView = view.findViewById(R.id.summary_report_view);
        fab_send=view.findViewById(R.id.fab_send);

        fab_send.setVisibility(View.GONE);

        fab_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arr.size()>0 && arr!=null){
                    SendDataOrdor();
                }else {
                    showErrorDialog("مشتری را انتخاب کنید ");
                }
            }
        });
        view.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final StatusConfigDialog dialog = new StatusConfigDialog();
                dialog.onConfigUpdated = new StatusConfigDialog.OnConfigUpdated() {
                    @Override
                    public void done() {
                        dialog.dismiss();

                        refresh();
                    }
                };
                dialog.show(getChildFragmentManager(), "StatusConfigDialog");
            }
        });
        return view;
    }

    private void refresh() {
        Context context = getContext();
        if (context != null && errorTextView != null) {
            startProgress(R.string.please_wait, R.string.connecting_to_the_server);
            StatusConfigModel statusConfigModel = new StatusConfigModel();
            List<String> dealersId = new ArrayList<>();
            dealersId = VisitorFilter.getList(getContext());
            TourStatusConfig config = new TourStatusConfig(context);
            HashMap<OptionId, RequestStatusOption> map = config.getMapOfRequestStatusOptions();
            List<String> statuses = new ArrayList<String>();
            if (map.size()>0) {
                if (map.get(OptionId.Verified).value == true) {
                    statuses.add(map.get(OptionId.Verified).nameid);
                    status_options = "D2";
                }
                if (map.get(OptionId.InProgress).value == true) {
                    statuses.add(map.get(OptionId.InProgress).nameid);
                    status_options = "R2";
                }
            }
//            if (statuses.size()==1){
//               // fab_send.setVisibility(View.VISIBLE);
//            }else {
//                fab_send.setVisibility(View.GONE);
//            }

            statusConfigModel.setDealersId(dealersId);
            statusConfigModel.setStatuses(statuses);
            statusConfigModel.setStartDate(DateHelper.toString(config.getFromDate(), DateFormat.MicrosoftDateTime, Locale.US));
            statusConfigModel.setEndDate(DateHelper.toString(config.getToDate(), DateFormat.MicrosoftDateTime, Locale.US));

             SupervisorApi api = new SupervisorApi(getContext());
            api.runWebRequest(api.Reviewreport(statusConfigModel), new WebCallBack<List<ReviewreportModel>>() {
                @Override
                protected void onFinish() {
                    finishProgress();
                }

                @Override
                protected void onSuccess(List<ReviewreportModel> result, Request request) {
                    adapter = new SimpleReportAdapter<ReviewreportModel>(getVaranegarActvity(),ReviewreportModel.class){
                        class PhoneCustomerViewHoder extends CustomViewHolder<ReviewreportModel>{


                            @Override
                            public void onBind(View view, ReviewreportModel entity) {
                                ImageView callImageView = null;
                                view.findViewById(R.id.phone_image_view).setVisibility(View.GONE);
                                view.findViewById(R.id.mobile_image_view).setVisibility(View.GONE);
                                view.findViewById(R.id.Questionnaire_image_view).setVisibility(View.GONE);
                                ImageView customercondition=view.findViewById(R.id.customer_condition_image_view);
                                LinearLayout linearLayout=view.findViewById(R.id.linear_view);
                                if (entity.orderStatus.equals("D2")) {
                                    customercondition.setImageResource(R.drawable.ic_baseline_block_24);
                                    linearLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                                }else {
                                    customercondition.setImageResource(R.drawable.ic_baseline_done_24);
                                    linearLayout.setBackgroundColor(Color.parseColor("#00BCD4"));
                                }
                                linearLayout.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (entity.orderStatus.equals("D2")) {
//                                            customercondition.setImageResource(R.drawable.ic_baseline_done_24);
//                                            linearLayout.setBackgroundColor(Color.parseColor("#00BCD4"));
                                            CuteMessageDialog builder = new CuteMessageDialog(getActivity());
                                            builder.setTitle(getActivity().getString(com.varanegar.vaslibrary.R.string.alert));

                                            if (entity.orderStatus.equals("D2")) {
                                                builder.setMessage("سفارش مشتری به حالت تایید تغییر می کند");
                                            } else if (!entity.orderStatus.equals("D2")){

                                            }
                                            builder.setPositiveButton(com.varanegar.vaslibrary.R.string.yes, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    arr.add(entity.orderNumber);
                                                    Log.e("Tag", String.valueOf(arr));
                                                    SendDataOrdor();
                                                }
                                            });
                                            builder.setNegativeButton(com.varanegar.vaslibrary.R.string.no, new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                }
                                            });
                                            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                                @Override
                                                public void onCancel(DialogInterface dialogInterface) {

                                                }
                                            });
                                            builder.show();




//                                            entity.orderStatus=" ";

                                        }else {
//                                            customercondition.setImageResource(R.drawable.ic_baseline_block_24);
//                                            linearLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
//                                            arr.remove(entity.orderNumber);
//                                            entity.orderStatus="D2";
//                                            Log.e("Tag", String.valueOf(arr));
                                        }
                                    }
                                });




                            }

                            @Override
                            public View onCreateView(LayoutInflater inflater, ViewGroup parent) {
                                View view = inflater.inflate(R.layout.supervisor_report_phone_layput, parent, false);

                                return view;
                            }
                        }
                        @Override
                        public void bind(ReportColumns columns, ReviewreportModel entity) {
                            columns.add(bind(entity, ReviewreportView.orderNumber,"شماره سفارش").setFrizzed().setSortable());
                            columns.add(bind(entity, ReviewreportView.orderStatus, "وضعیت سفارش").setCustomViewHolder(new PhoneCustomerViewHoder()).setWeight(0.70f));
                            columns.add(bind(entity, ReviewreportView.orderDate,"تاریخ سفارش"));
                            columns.add(bind(entity, ReviewreportView.dealerCode,"کد ویزیتور"));
                            columns.add(bind(entity, ReviewreportView.dealerName,"نام ویزیتور"));
                            columns.add(bind(entity, ReviewreportView.customerCode,"کد مشتری"));
                            columns.add(bind(entity, ReviewreportView.customerName,"نام مشتری"));
                            columns.add(bind(entity, ReviewreportView.paymentType,"نحوه تسویه"));
                            columns.add(bind(entity, ReviewreportView.comment,"توضیحات"));
                            columns.add(bind(entity, ReviewreportView.customerCategory,"نوع مشتری"));

                        }




                    };
                    adapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));
                    adapter.create(result, null);
                    adapter.setOnItemSelectListener(new ReportAdapter.OnItemSelectListener() {
                        @Override
                        public void onItemSelected(int idx) {
                            Log.e("t", String.valueOf(idx));
                            List<items> list=result.get(idx).items;
                            ArrayList<items> arrayList=new ArrayList<>(list.size());
                            arrayList.addAll(list);
//                            Bundle bundle = new Bundle();
//                            bundle.putSerializable("valuesStatus", arrayList);
                            SharedPreferences sharedPreferences = context.getSharedPreferences("valuesStatus", Context.MODE_PRIVATE);
                            String json = VaranegarGsonBuilder.build().create().toJson(arrayList);
                            sharedPreferences.edit().putString("Status", json).commit();

                            ToursStatusViewFragment nextFrag= new ToursStatusViewFragment();
                            getVaranegarActvity().pushFragment(nextFrag);
//                            getActivity().getSupportFragmentManager().beginTransaction()
//                                    .replace(R.id.Layout_container, nextFrag, "findThisFragment")
//                                    .addToBackStack("ToursStatusFragment")
//                                    .commit();

                        }

                        @Override
                        public void onItemDeSelected(int idx) {

                        }
                    });
                    summaryReportView.setAdapter(adapter);

                }

                @Override
                protected void onApiFailure(ApiError error, Request request) {
                    String err = WebApiErrorBody.log(error, getContext());
                    showErrorDialog(err);
                    Log.e("err", String.valueOf(err));

                }

                @Override
                protected void onNetworkFailure(Throwable t, Request request) {
                    showErrorDialog(getString(R.string.connection_to_server_failed));
                }
            });

       }
//        Context context = getContext();
//        if (context != null && errorTextView != null) {
//            startProgress(R.string.please_wait, R.string.connecting_to_the_server);
//            TourStatusConfig config = new TourStatusConfig(context);
//            if ("Tour".equals(config.getStatusType())) {
//                final SupervisorApi api = new SupervisorApi(context);
//                HashMap<OptionId, TourStatusOption> map = config.getMapOfTourStatusOptions();
//                api.runWebRequest(
//                        api.tour(0,
//                                map.get(OptionId.ReadyToSend).value,
//                                map.get(OptionId.Sent).value,
//                                map.get(OptionId.InProgress).value,
//                                map.get(OptionId.Received).value,
//                                map.get(OptionId.Finished).value,
//                                map.get(OptionId.Canceled).value,
//                                map.get(OptionId.Deactivated).value,
//                                DateHelper.toString(config.getFromDate(), DateFormat.MicrosoftDateTime, Locale.US),
//                                DateHelper.toString(config.getToDate(), DateFormat.MicrosoftDateTime, Locale.US),
//                                VisitorFilter.read(getContext()).UniqueId
//                        ), new WebCallBack<List<TourStatusSummaryViewModel>>() {
//                            @Override
//                            protected void onFinish() {
//                                finishProgress();
//                            }
//
//                            @Override
//                            protected void onSuccess(List<TourStatusSummaryViewModel> result, Request request) {
//                                Activity activity = getActivity();
//                                if (activity != null && !activity.isFinishing() && isResumed()) {
//                                    if (result.size() == 0) {
//                                        errorTextView.setVisibility(View.VISIBLE);
//                                        errorTextView.setText(R.string.no_tour_found_to_show);
//                                        summaryReportView.setVisibility(View.GONE);
//                                    } else {
//                                        errorTextView.setVisibility(View.GONE);
//                                        summaryReportView.setVisibility(View.VISIBLE);
//                                    }
//
//                                    adapter = new SimpleReportAdapter<TourStatusSummaryViewModel>(getVaranegarActvity(), TourStatusSummaryViewModel.class) {
//                                        @Override
//                                        public void bind(ReportColumns columns, TourStatusSummaryViewModel entity) {
//                                            bindRowNumber(columns);
//                                            columns.add(bind(entity, TourStatusSummaryView.TourNo, getString(R.string.tour_no)).setFrizzed().setSortable());
//                                            columns.add(bind(entity, TourStatusSummaryView.TourPDate, getString(R.string.tour_date)).setSortable());
//                                            columns.add(bind(entity, TourStatusSummaryView.AgentName, getString(R.string.agent_name)).setFrizzed().setFilterable().setSortable());
//                                            columns.add(bind(entity, TourStatusSummaryView.TourStatusName, getString(R.string.status)).setSortable().setFrizzed());
//                                            columns.add(bind(entity, TourStatusSummaryView.StartPTime, getString(R.string.start_time)).setWeight(2).setSortable());
//                                            columns.add(bind(entity, TourStatusSummaryView.EndPTime, getString(R.string.end_time)).setWeight(2).setSortable());
//                                            columns.add(bind(entity, TourStatusSummaryView.PathTitle, getString(R.string.path_title)).setWeight(1.5f).sendToDetail());
//                                            columns.add(bind(entity, TourStatusSummaryView.CustomerCount, getString(R.string.customers_count)).setWeight(1.5f).setSortable().sendToDetail());
//                                            columns.add(bind(entity, TourStatusSummaryView.ReturnInvoiceCount, getString(R.string.return_invoice_count)).setWeight(2).setSortable().sendToDetail());
//                                            columns.add(bind(entity, TourStatusSummaryView.OrderCount, getString(R.string.order_count)).setWeight(1.5f).setSortable().sendToDetail());
//                                            columns.add(bind(entity, TourStatusSummaryView.NoOrderCount, getString(R.string.lack_of_order_count)).setWeight(1.5f).setSortable().sendToDetail());
//                                            columns.add(bind(entity, TourStatusSummaryView.VisitCount, getString(R.string.visit_count)).setSortable().sendToDetail());
//                                            columns.add(bind(entity, TourStatusSummaryView.NoVisitCount, getString(R.string.lack_of_visit_count)).setWeight(1.5f).setSortable().sendToDetail());
//                                            columns.add(bind(entity, TourStatusSummaryView.ReturnInvoiceRequestCount, getString(R.string.return_invoice_request_count)).setWeight(2).setSortable().sendToDetail());
//                                            columns.add(bind(entity, TourStatusSummaryView.SuccessVisitAvg, getString(R.string.success_visit_avg)).setWeight(1.5f).setSortable().sendToDetail());
//                                            columns.add(bind(entity, TourStatusSummaryView.TotalOrderAmount, getString(R.string.total_orders_amount)).setWeight(1.5f).setSortable().sendToDetail());
//                                            columns.add(bind(entity, TourStatusSummaryView.TotalInvoiceAmount, getString(R.string.total_invoices_amount)).setWeight(1.5f).setSortable().sendToDetail());
//                                            columns.add(bind(entity, TourStatusSummaryView.InvoiceLineAvg, getString(R.string.invoice_line_avg)).setWeight(1.5f).setSortable().sendToDetail());
//                                            columns.add(bind(entity, TourStatusSummaryView.SaleVolume, getString(R.string.sale_volume)).setSortable().sendToDetail());
//                                            columns.add(bind(entity, TourStatusSummaryView.TotalSaleAvg, getString(R.string.total_sale_avg)).setWeight(1.5f).setSortable().sendToDetail());
//                                            columns.add(bind(entity, TourStatusSummaryView.InStoreTimeStr, getString(R.string.in_store_time)).setWeight(1.5f).sendToDetail());
//                                            columns.add(bind(entity, TourStatusSummaryView.InStoreTimeAvgStr, getString(R.string.in_store_time_avg)).setWeight(2).sendToDetail());
//                                            columns.add(bind(entity, TourStatusSummaryView.OutOfStoreTimeStr, getString(R.string.out_of_store_time)).setWeight(1.5f).sendToDetail());
//                                            columns.add(bind(entity, TourStatusSummaryView.OutOfStoreTimeAvgStr, getString(R.string.out_of_store_time_avg)).setWeight(2).sendToDetail());
//                                            columns.add(bind(entity, TourStatusSummaryView.BetweenTwoStoresTimeAvgStr, getString(R.string.between_two_stores_time_avg)).setWeight(2.5f).sendToDetail());
//                                        }
//
//                                        @Override
//                                        protected ItemContextView<TourStatusSummaryViewModel> onCreateContextView() {
//                                            TourStatusSummaryContextView contextView = new TourStatusSummaryContextView(adapter.getAdapter(), getVaranegarActvity());
//                                            contextView.onTourChanged = new TourStatusSummaryContextView.OnTourChanged() {
//                                                @Override
//                                                public void run() {
//                                                    ToursStatusFragment.this.refresh();
//                                                }
//                                            };
//                                            return contextView;
//                                        }
//                                    };
//                                    adapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));
//                                    adapter.create(result, null);
//                                    summaryReportView.setAdapter(adapter);
//                                }
//                            }
//
//                            @Override
//                            protected void onApiFailure(ApiError error, Request request) {
//                                Activity activity = getActivity();
//                                if (activity != null && !activity.isFinishing() && isResumed()) {
//                                    errorTextView.setText(R.string.no_tour_found_to_show);
//                                    errorTextView.setVisibility(View.VISIBLE);
//                                    summaryReportView.setVisibility(View.GONE);
//                                    String err = WebApiErrorBody.log(error, getContext());
//                                    showErrorDialog(err);
//                                }
//                            }
//
//                            @Override
//                            protected void onNetworkFailure(Throwable t, Request request) {
//                                Activity activity = getActivity();
//                                if (activity != null && !activity.isFinishing() && isResumed()) {
//                                    errorTextView.setText(R.string.no_tour_found_to_show);
//                                    errorTextView.setVisibility(View.VISIBLE);
//                                    summaryReportView.setVisibility(View.GONE);
//                                    Timber.e(t);
//                                    showErrorDialog(getString(R.string.connection_to_server_failed));
//                                }
//                            }
//                        });
//            } else {
//                final SupervisorApi api = new SupervisorApi(context);
//                HashMap<OptionId, RequestStatusOption> map = config.getMapOfRequestStatusOptions();
//                api.runWebRequest(
//                        api.customerCalls(map.get(OptionId.Confirmed).value,
//                                map.get(OptionId.NotConfirmed).value,
//                                map.get(OptionId.Revoked).value,
//                                DateHelper.toString(config.getFromDate(), DateFormat.MicrosoftDateTime, Locale.US),
//                                DateHelper.toString(config.getToDate(), DateFormat.MicrosoftDateTime, Locale.US),
//                                VisitorFilter.read(getContext()).UniqueId
//                        ), new WebCallBack<List<CustomerCallViewModel>>() {
//                            @Override
//                            protected void onFinish() {
//                                finishProgress();
//                            }
//
//                            @Override
//                            protected void onSuccess(List<CustomerCallViewModel> result, Request request) {
//                                Activity activity = getActivity();
//                                if (activity != null && !activity.isFinishing() && isResumed()) {
//                                    if (result.size() == 0) {
//                                        errorTextView.setText(R.string.no_request_found);
//                                        errorTextView.setVisibility(View.VISIBLE);
//                                        summaryReportView.setVisibility(View.GONE);
//                                    } else {
//                                        errorTextView.setVisibility(View.GONE);
//                                        summaryReportView.setVisibility(View.VISIBLE);
//                                    }
//
//                                    adapter = new SimpleReportAdapter<CustomerCallViewModel>(getVaranegarActvity(), CustomerCallViewModel.class) {
//                                        @Override
//                                        public void bind(ReportColumns columns, CustomerCallViewModel entity) {
//                                            bindRowNumber(columns);
//                                            columns.add(bind(entity, CustomerCallView.CustomerCode, getString(R.string.customer_code_label)).setFrizzed());
//                                            columns.add(bind(entity, CustomerCallView.CustomerName, getString(R.string.customer_name_label)).setFrizzed());
//                                            columns.add(bind(entity, CustomerCallView.StoreName, getString(R.string.store_name_label)));
//                                            columns.add(bind(entity, CustomerCallView.CallStatusName, getString(R.string.call_status)));
//                                            columns.add(bind(entity, CustomerCallView.TotalRequestAmount, getString(R.string.total_amount)));
//
//                                            columns.add(bind(entity, CustomerCallView.LocalPaperNoCollection, getString(R.string.ref_number)).sendToDetail());
//                                            columns.add(bind(entity, CustomerCallView.TourNo, getString(R.string.tour_no)).sendToDetail());
//                                            columns.add(bind(entity, CustomerCallView.DealerName, getString(R.string.dealer_name_label)).sendToDetail());
//                                            columns.add(bind(entity, CustomerCallView.OrderPaymentTypeName, getString(R.string.payment_type)).sendToDetail());
//                                            columns.add(bind(entity, CustomerCallView.Address, getString(R.string.address_label)).sendToDetail());
//                                            columns.add(bind(entity, CustomerCallView.Phone, getString(R.string.phone_number)).sendToDetail());
//                                            columns.add(bind(entity, CustomerCallView.StartPTime, getString(R.string.start_time)).sendToDetail());
//                                            columns.add(bind(entity, CustomerCallView.EndPTime, getString(R.string.end_time)).sendToDetail());
//                                            columns.add(bind(entity, CustomerCallView.VisitDurationStr, getString(R.string.visit_duration)).sendToDetail());
//                                            columns.add(bind(entity, CustomerCallView.BackOfficeReturnOrderNoCollection, getString(R.string.return_ref)).sendToDetail());
//
//                                        }
//
//                                        @Override
//                                        protected ItemContextView<CustomerCallViewModel> onCreateContextView() {
//                                            CustomerCallSummaryContextView contextView = new CustomerCallSummaryContextView(getAdapter(), getContext());
//                                            contextView.onCustomerCallChanged = new CustomerCallSummaryContextView.OnCustomerCallChanged() {
//                                                @Override
//                                                public void run() {
//                                                    ToursStatusFragment.this.refresh();
//                                                }
//                                            };
//                                            return contextView;
//                                        }
//                                    };
//                                    adapter.setLocale(VasHelperMethods.getSysConfigLocale(getContext()));
//                                    adapter.create(result, null);
//                                    summaryReportView.setAdapter(adapter);
//                                }
//                            }
//
//                            @Override
//                            protected void onApiFailure(ApiError error, Request request) {
//                                Activity activity = getActivity();
//                                if (activity != null && !activity.isFinishing() && isResumed()) {
//                                    errorTextView.setText(R.string.no_request_found);
//                                    errorTextView.setVisibility(View.VISIBLE);
//                                    summaryReportView.setVisibility(View.GONE);
//                                    String err = WebApiErrorBody.log(error, getContext());
//                                    showErrorDialog(err);
//
//                                }
//                            }
//
//                            @Override
//                            protected void onNetworkFailure(Throwable t, Request request) {
//                                Activity activity = getActivity();
//                                if (activity != null && !activity.isFinishing() && isResumed()) {
//                                    errorTextView.setText(R.string.no_request_found);
//                                    errorTextView.setVisibility(View.VISIBLE);
//                                    summaryReportView.setVisibility(View.GONE);
//                                    Timber.e(t);
//                                    showErrorDialog(getString(R.string.connection_to_server_failed));
//                                }
//                            }
//                        });
//            }
//        }


   }
   private void SendDataOrdor(){
       ChangeOrdersStatusmModel changeOrdersStatusmModel=new ChangeOrdersStatusmModel();
       changeOrdersStatusmModel.orderNumbers=arr;
       changeOrdersStatusmModel.status="r2";
       final ProgressDialog progressDialog = new ProgressDialog(getContext());
       progressDialog.setMessage("درحال ارسال اطلاعات");
       progressDialog.show();
       SupervisorApi api = new SupervisorApi(getContext());
       api.runWebRequest(api.senddataorder(changeOrdersStatusmModel), new WebCallBack<ResponseBody>() {
           @Override
           protected void onFinish() {

           }

           @Override
           protected void onSuccess(ResponseBody result, Request request) {
               Log.e("CustomerFragment", String.valueOf(result));
               progressDialog.dismiss();
               refresh();
           }

           @Override
           protected void onApiFailure(ApiError error, Request request) {
               String err = WebApiErrorBody.log(error, getContext());
               showErrorDialog(err);
               progressDialog.dismiss();
           }

           @Override
           protected void onNetworkFailure(Throwable t, Request request) {
               Timber.e(t);
               showErrorDialog(getContext().getString(R.string.error_connecting_to_server));
               progressDialog.dismiss();
           }
       });

       arr.clear();
   }
    private void showErrorDialog(String err) {
        if (isResumed()) {
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

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }
}
