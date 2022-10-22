package com.varanegar.vaslibrary.ui.report.report_new.orderReturn_report;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.component.PairedItems;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.ui.report.report_new.orderReturn_report.model.ReturnDealerModel;
import com.varanegar.vaslibrary.ui.report.report_new.orderStatus_Report.model.OrderStatusModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.supervisor.SupervisorApi;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import okhttp3.Request;
import timber.log.Timber;

public class RerurnReportFragment extends VaranegarFragment {
    private TreeNode root;
    private LinearLayoutCompat containerView;
    private LinearLayout layout_header_return,layout_header_order;
    private PairedItems startDatePairedItems;
    private PairedItems endDatePairedItems;
    private Date startDate;
    private Date endDate;
    private ProgressDialog progressDialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_status_report,
                container, false);
        startDatePairedItems = view.findViewById(R.id.start_date_item);
        endDatePairedItems = view.findViewById(R.id.end_date_item);
        containerView = view.findViewById(R.id.container_view);
        layout_header_return= view.findViewById(R.id.layout_header_return);
        layout_header_order= view.findViewById(R.id.layout_header_order);
        LinearLayoutCompat linear_view = view.findViewById(R.id.linear_view);

        view.findViewById(R.id.start_calendar_image_view).setOnClickListener(view1 ->
                DateHelper.showDatePicker(Objects.requireNonNull(getVaranegarActvity()),
                        VasHelperMethods.getSysConfigLocale(getContext()), calendar -> {
                            if (calendar.getTime().after(new Date())) {
                                showErrorDialog(getString(R.string.date_could_not_be_after_now));
                                return;
                            }
                            if (endDate != null && endDate.before(calendar.getTime())) {
                                showErrorDialog(getString(R.string.end_date_could_not_be_before_start_date));
                                return;
                            }
                            startDate = calendar.getTime();
                            startDatePairedItems.setValue(DateHelper.toString
                                    (startDate, DateFormat.Date,
                                            VasHelperMethods.getSysConfigLocale(getContext())));
                        }));


        view.findViewById(R.id.end_calendar_image_view).setOnClickListener(view13 ->
                DateHelper.showDatePicker(Objects.requireNonNull(getVaranegarActvity()),
                VasHelperMethods.getSysConfigLocale(getContext()), calendar -> {
            if (calendar.getTime().after(new Date())) {
                showErrorDialog(getString(R.string.date_could_not_be_after_now));
                return;
            }
            if (startDate != null && startDate.after(calendar.getTime())) {
                showErrorDialog(getString(R.string.start_date_could_not_be_after_end_date));
                return;
            }
            endDate = calendar.getTime();
            endDatePairedItems.setValue(DateHelper.toString(endDate, DateFormat.Date, VasHelperMethods.getSysConfigLocale(getContext())));
        }));


        view.findViewById(R.id.buttonReport).setOnClickListener(view12 -> refreshapi());





        return view;
    }


    private void showErrorDialog(String error) {
        if (isResumed()) {
            CuteMessageDialog dialog = new CuteMessageDialog(requireContext());
            dialog.setTitle(com.varanegar.vaslibrary.R.string.error);
            dialog.setMessage(error);
            dialog.setIcon(Icon.Error);
            dialog.setPositiveButton(com.varanegar.vaslibrary.R.string.ok, null);
            dialog.show();
        }
    }


    private void refreshapi() {
        Context context = getContext();
        if (context != null) {

            layout_header_return.setVisibility(View.VISIBLE);
            layout_header_order.setVisibility(View.GONE);
            List<String> dealersId = new ArrayList<>();

            dealersId.add(String.valueOf(Objects.requireNonNull(UserManager.readFromFile(getContext())).UniqueId));
            String startDate=DateHelper.toString(getStartDate(), DateFormat.Date, VasHelperMethods.getSysConfigLocale(getContext()));
            String  toDate =DateHelper.toString(getEndDate(), DateFormat.Date, VasHelperMethods.getSysConfigLocale(getContext()));
            OrderStatusModel orderStatusModel = new OrderStatusModel();
            orderStatusModel.setDealersId(dealersId);
            orderStatusModel.setStartdata(startDate);
            orderStatusModel.setEndDate(toDate);

            progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle(R.string.please_wait);
            progressDialog.setMessage(getContext().getString(R.string.downloading_data));
            progressDialog.show();
            SupervisorApi supervisorApi = new SupervisorApi(getContext());
            supervisorApi.runWebRequest(supervisorApi.GetReturnReport(orderStatusModel),
                    new WebCallBack<List<ReturnDealerModel>>() {
                        @Override
                        protected void onFinish() {
                            dismissProgressDialog();
                        }

                        @Override
                        protected void onSuccess(List<ReturnDealerModel> result, Request request) {
                            containerView.removeAllViews();
                            root = TreeNode.root();
                            ReturnFlatGenerator g = new ReturnFlatGenerator(
                                    getContext(),
                                    root,
                                    result
                            );
                            g.build();
                            root = g.getRoot();
                            AndroidTreeView tView2 = new AndroidTreeView(getActivity(), root);
                            tView2.setDefaultNodeClickListener(returnNodeClickListener);
                            containerView.addView(tView2.getView());
                            dismissProgressDialog();
                        }

                        @Override
                        protected void onApiFailure(ApiError error, Request request) {
                            if (isResumed()) {

                                Activity activity = getActivity();
                                if (activity != null && !activity.isFinishing()) {
                                    String err = WebApiErrorBody.log(error, getContext());
                                    showErrorDialog(err);
                                    dismissProgressDialog();
                                }
                            }
                        }

                        @Override
                        protected void onNetworkFailure(Throwable t, Request request) {
                            if (isResumed()) {

                                Activity activity = getActivity();
                                if (activity != null && !activity.isFinishing()) {
                                    Timber.e(t);
                                    showErrorDialog(requireContext().getString(R.string.error_connecting_to_server));
                                }
                            }
                        }
                    });


        }
    }




    int childCount = 0;
    private final TreeNode.TreeNodeClickListener returnNodeClickListener = (node, value) -> {
        if(node.getLevel() == 1){
            LinearLayout  layout_header_sub=node.getViewHolder().getView().findViewById(R.id.layout_header_sub);
            AppCompatTextView add_item=node.getViewHolder().getView().findViewById(R.id.add_item);
            if (node.isExpanded()){
                add_item.setText("+");
                add_item.setTextColor(Color.parseColor("#4DFF56"));
                layout_header_sub.setVisibility(View.GONE);
            }else {
                add_item.setText("-");
                add_item.setTextColor(Color.parseColor("#FF00F2"));
                layout_header_sub.setVisibility(View.VISIBLE);
            }
        }

        Log.d("orderreportfragment", "begin load: " + childCount+ "node level = "+node.getId());
        if (node.getLevel() == 2) {
            LinearLayout  layout_header_sub=node.getViewHolder().getView().findViewById(R.id.layout_header_sub);
            AppCompatTextView addite_dleer=node.getViewHolder().getView().findViewById(R.id.addite_dleer);

            //    totalCount = ((OrderStatusReportFlat) value).getChilds().size();

            if (node.isExpanded()){
                addite_dleer.setText("+");
                addite_dleer.setTextColor(Color.parseColor("#4DFF56"));
                layout_header_sub.setVisibility(View.GONE);

            }else {
                addite_dleer.setText("-");
                addite_dleer.setTextColor(Color.parseColor("#FF00F2"));
                containerView.setEnabled(false);
                layout_header_sub.setVisibility(View.VISIBLE);

            }



        }
    };



    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            try {
                progressDialog.dismiss();
            } catch (Exception ignored) {

            }
        }
    }

    protected Date getEndDate() {
        return endDate == null ? new Date() : endDate;
    }
    protected Date getStartDate() {
        return startDate == null ? DateHelper.Min : startDate;
    }
}


