package com.varanegar.vaslibrary.ui.report.report_new.orderStatus_Report;

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
import com.varanegar.vaslibrary.ui.report.report_new.orderStatus_Report.model.CustomerItem;
import com.varanegar.vaslibrary.ui.report.report_new.orderStatus_Report.model.DealersItem;
import com.varanegar.vaslibrary.ui.report.report_new.orderStatus_Report.model.OrderStatusModel;
import com.varanegar.vaslibrary.ui.report.report_new.orderStatus_Report.model.OrderStatusReport;
import com.varanegar.vaslibrary.ui.report.report_new.orderStatus_Report.model.OrderStatusReportFlat;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.supervisor.SupervisorApi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import okhttp3.Request;
import timber.log.Timber;

public class OrderReportFragment extends VaranegarFragment {
    private TreeNode root;
    private View view;
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
        view = inflater.inflate(R.layout.fragment_order_status_report,
                container, false);
        startDatePairedItems = view.findViewById(R.id.start_date_item);
        endDatePairedItems = view.findViewById(R.id.end_date_item);
        containerView = view.findViewById(R.id.container_view);
        layout_header_return=view.findViewById(R.id.layout_header_return);
        layout_header_order=view.findViewById(R.id.layout_header_order);

        view.findViewById(R.id.start_calendar_image_view).setOnClickListener(view ->
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


        view.findViewById(R.id.end_calendar_image_view).setOnClickListener(view ->
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
            endDatePairedItems.setValue(DateHelper.toString(endDate, DateFormat.Date,
                    VasHelperMethods.getSysConfigLocale(getContext())));
        }));


        view.findViewById(R.id.buttonReport).setOnClickListener(view -> refreshapi());





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
            List<String> dealersId = new ArrayList<>();

            layout_header_return.setVisibility(View.GONE);
            layout_header_order.setVisibility(View.VISIBLE);

            dealersId.add(String.valueOf(Objects.requireNonNull
                    (UserManager.readFromFile(getContext())).UniqueId));
            String startDate=DateHelper.toString(getStartDate(), DateFormat.Date,
                    VasHelperMethods.getSysConfigLocale(getContext()));
            String  toDate =DateHelper.toString(getEndDate(), DateFormat.Date,
                    VasHelperMethods.getSysConfigLocale(getContext()));

            OrderStatusModel orderStatusModel = new OrderStatusModel();
                orderStatusModel.setDealersId(dealersId);
                orderStatusModel.setStartdata(startDate);
                orderStatusModel.setEndDate(toDate);
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle(R.string.please_wait);
            progressDialog.setMessage(getContext().getString(R.string.downloading_data));
            progressDialog.show();

                SupervisorApi supervisorApi = new SupervisorApi(getContext());
                supervisorApi.runWebRequest(supervisorApi.OrderStatusReport(orderStatusModel),
                        new WebCallBack<List<OrderStatusReport>>() {
                            @Override
                            protected void onFinish() {
                                dismissProgressDialog();
                            }

                            @Override
                            protected void onSuccess(List<OrderStatusReport> result,
                                                     Request request) {
                                containerView.removeAllViews();
                                root = TreeNode.root();
                                List<OrderStatusReportFlat> data = generateTreeData(result);
                                generateTreeFromData(data);
                                AndroidTreeView tView = new AndroidTreeView(getActivity(), root);
                                tView.setDefaultNodeClickListener(nodeClickListener);
                                containerView.addView(tView.getView());
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
                                        showErrorDialog(requireContext()
                                                .getString(R.string.error_connecting_to_server));
                                        dismissProgressDialog();
                                    }
                                }
                            }
                        });


        }
    }


    private List<OrderStatusReportFlat> generateTreeData(List<OrderStatusReport> result) {
        List<OrderStatusReportFlat> reports = new ArrayList<>();
        for (OrderStatusReport report : result) {
            OrderStatusReportFlat rep = new OrderStatusReportFlat(
                    1,
                    report.getDate(),
                    report.getOrderWeight(),
                    report.getPendingOrderWeight(),
                    report.getInProgressOrderWeight(),
                    report.getUndeliverdOrderWeight(),
                    report.getFinalWeight(),
                    "",
                    "",
                    0d,
                    "",
                    ""
            );
            for (DealersItem dealer : report.getDealersItems()) {
                List<OrderStatusReportFlat> customers = new ArrayList<>();
                for (CustomerItem customer : dealer.getCustomerItems()) {
                    OrderStatusReportFlat cus = new OrderStatusReportFlat(
                            3,
                            "",
                            customer.getOrderWeight(),
                            customer.getPendingOrderWeight(),
                            customer.getInProgressOrderWeight(),
                            customer.getUndeliverdOrderWeight(),
                            customer.getFinalWeight(),
                            "",
                            customer.getDealerCode(),
                            0d,
                            customer.getCustomerName(),
                            customer.getCustomerCode()
                    );
                    customers.add(cus);
                }
                rep.setChilds(customers);
            }
            reports.add(rep);
        }
        return reports;
    }


    private void generateTreeFromData(List<OrderStatusReportFlat> treeData) {
        for (OrderStatusReportFlat level1 : treeData) {
            TreeNode l1 = new TreeNode(level1)
                    .setViewHolder(new TreeNodeHolder(getContext(),
                            treeData,null));

            for (OrderStatusReportFlat level2 : level1.getChilds()) {
                TreeNode l2 = new TreeNode(level2).setViewHolder(new TreeNodeHolder(getContext(),
                        null,null));
                l1.addChild(l2);
            }
            root.addChild(l1);
        }
    }

    int childCount = 0;
    int totalCount = 0;

    private final TreeNode.TreeNodeClickListener nodeClickListener = (node, value) -> {
        if(((OrderStatusReportFlat)value).getLevel() == 1){

            AppCompatTextView add_item=node.getViewHolder().getView().findViewById(R.id.add_item);
            LinearLayout  layout_header_sub=node.getViewHolder().getView().findViewById(R.id.layout_header_sub);

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
        if (((OrderStatusReportFlat)value).getLevel() == 2) {

            AppCompatTextView addite_dleer=node.getViewHolder().getView().findViewById(R.id.addite_dleer);
            LinearLayout  layout_header_sub=node.getViewHolder().getView().findViewById(R.id.layout_header_sub);
            totalCount = ((OrderStatusReportFlat) value).getChilds().size();

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

