package com.varanegar.supervisor.customreport.orderstatus;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;

import com.varanegar.supervisor.IMainPageFragment;
import com.varanegar.supervisor.R;
import com.varanegar.supervisor.VisitorFilter;

import com.varanegar.supervisor.customreport.orderreturn.ReturnFlatGenerator;
import com.varanegar.supervisor.customreport.orderreturn.model.ReturnDealerModel;
import com.varanegar.supervisor.customreport.orderreturn.model.ReturnReportFlat;
import com.varanegar.supervisor.customreport.orderstatus.model.CustomerItem;
import com.varanegar.supervisor.customreport.orderstatus.model.DealersItem;

import com.varanegar.supervisor.customreport.orderstatus.model.OrderStatusReport;
import com.varanegar.supervisor.customreport.orderstatus.model.OrderStatusReportFlat;
import com.varanegar.supervisor.customreport.orderstatus.model.orderStatusModel;
import com.varanegar.supervisor.webapi.SupervisorApi;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Request;
import timber.log.Timber;

public class OrderReportFragment extends IMainPageFragment {
    private TreeNode root;
    private View view;
    private orderStatusModel orderStatusModel;
    private LinearLayoutCompat containerView;
    private OrderReportConfig config;
    private LinearLayout layout_header_return,layout_header_order;
    private int typeReport;
    private LinearLayoutCompat linear_view;


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeAllViews();
            }
        }
    }
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (view != null) {
//            ViewGroup parent = (ViewGroup) view.getParent();
//            if (parent != null) {
//                parent.removeAllViews();
//            }
//        }
//    }

    @Override
    public void onPause() {
        super.onPause();
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeAllViews();
            }
        }
    }
    @Override
    protected View onCreateContentView(@NonNull LayoutInflater inflater,
                                       @Nullable ViewGroup container,
                                       @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order_status_report,
                container, false);

        containerView = view.findViewById(R.id.container_view);
        layout_header_return=view.findViewById(R.id.layout_header_return);
        layout_header_order=view.findViewById(R.id.layout_header_order);
        linear_view=view.findViewById(R.id.linear_view);
        view.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderReportConfigDialog orderConfigDialog = new OrderReportConfigDialog();
                orderConfigDialog.onReportConfigUpdate = new OrderReportConfigDialog.OnReportConfigUpdate() {
                    @Override
                    public void run() {

                        if (isResumed()) {
                            Activity activity = getActivity();
                            if (activity != null && !activity.isFinishing() && isResumed()) {

                                orderConfigDialog.dismiss();
                                config = new OrderReportConfig(activity);
                                List<String> dealersId = new ArrayList<>();
                                dealersId = VisitorFilter.getList(getContext());

                                typeReport=config.getToReport();

                                if (dealersId == null || typeReport==0) {
                                    showErrorDialog("لطفا یک ویزیتور و یک نوع گزارش را انتخاب نمایید");
                                } else
                                   refreshapi();
                            }
                        }
                    }
                };
                orderConfigDialog.show(getChildFragmentManager(), "orderConfigDialog");
            }
        });





        return view;
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


    private void refreshapi() {
        Context context = getContext();
        if (context != null) {

            if (typeReport == 1) {
                layout_header_return.setVisibility(View.GONE);
                layout_header_order.setVisibility(View.VISIBLE);
                final String fromDate = DateHelper.toString(config.getFromDate(), DateFormat.Date, VasHelperMethods.getSysConfigLocale(getContext()));
                final String toDate = DateHelper.toString(config.getToDate(), DateFormat.Date, VasHelperMethods.getSysConfigLocale(getContext()));
                List<String> dealersId = new ArrayList<>();
                dealersId = VisitorFilter.getList(getContext());
                List<String> product_list = new ArrayList<>();
                product_list = VisitorFilter.getproduct_group(getContext());

                orderStatusModel = new orderStatusModel();
                orderStatusModel.setDealersId(dealersId);
                orderStatusModel.setStartdata(fromDate);
                orderStatusModel.setEndDate(toDate);
                startProgress(R.string.please_wait, R.string.connecting_to_the_server);
                SupervisorApi supervisorApi = new SupervisorApi(getContext());
                supervisorApi.runWebRequest(supervisorApi.OrderStatusReport(orderStatusModel),
                        new WebCallBack<List<OrderStatusReport>>() {
                            @Override
                            protected void onFinish() {
                                finishProgress();
                            }

                            @Override
                            protected void onSuccess(List<OrderStatusReport> result, Request request) {
                                containerView.removeAllViews();
                                root = TreeNode.root();
                                List<OrderStatusReportFlat> data = generateTreeData(result);
                                generateTreeFromData(data);
                                AndroidTreeView tView = new AndroidTreeView(getActivity(), root);
                                tView.setDefaultNodeClickListener(nodeClickListener);
                                containerView.addView(tView.getView());
                                finishProgress();
                            }

                            @Override
                            protected void onApiFailure(ApiError error, Request request) {
                                if (isResumed()) {
                                    finishProgress();
                                    Activity activity = getActivity();
                                    if (activity != null && !activity.isFinishing()) {
                                        String err = WebApiErrorBody.log(error, getContext());
                                        showErrorDialog(err);
                                    }
                                }
                            }

                            @Override
                            protected void onNetworkFailure(Throwable t, Request request) {
                                if (isResumed()) {
                                    finishProgress();
                                    Activity activity = getActivity();
                                    if (activity != null && !activity.isFinishing()) {
                                        Timber.e(t);
                                        showErrorDialog(getContext().getString(R.string.error_connecting_to_server));
                                    }
                                }
                            }
                        });
            } else if (typeReport == 2) {
                layout_header_return.setVisibility(View.VISIBLE);
                layout_header_order.setVisibility(View.GONE);
                final String fromDate = DateHelper.toString(config.getFromDate(), DateFormat.Date, VasHelperMethods.getSysConfigLocale(getContext()));
                final String toDate = DateHelper.toString(config.getToDate(), DateFormat.Date, VasHelperMethods.getSysConfigLocale(getContext()));
                List<String> dealersId = new ArrayList<>();
                dealersId = VisitorFilter.getList(getContext());
                List<String> product_list = new ArrayList<>();
                product_list = VisitorFilter.getproduct_group(getContext());

                orderStatusModel = new orderStatusModel();
                orderStatusModel.setDealersId(dealersId);
                orderStatusModel.setStartdata(fromDate);
                orderStatusModel.setEndDate(toDate);
                startProgress(R.string.please_wait, R.string.connecting_to_the_server);

                SupervisorApi supervisorApi = new SupervisorApi(getContext());
                supervisorApi.runWebRequest(supervisorApi.GetReturnReport(orderStatusModel),
                        new WebCallBack<List<ReturnDealerModel>>() {
                            @Override
                            protected void onFinish() {
                                finishProgress();
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
                                finishProgress();
                            }

                            @Override
                            protected void onApiFailure(ApiError error, Request request) {
                                if (isResumed()) {
                                    finishProgress();
                                    Activity activity = getActivity();
                                    if (activity != null && !activity.isFinishing()) {
                                        String err = WebApiErrorBody.log(error, getContext());
                                        showErrorDialog(err);
                                    }
                                }
                            }

                            @Override
                            protected void onNetworkFailure(Throwable t, Request request) {
                                if (isResumed()) {
                                    finishProgress();
                                    Activity activity = getActivity();
                                    if (activity != null && !activity.isFinishing()) {
                                        Timber.e(t);
                                        showErrorDialog(getContext().getString(R.string.error_connecting_to_server));
                                    }
                                }
                            }
                        });
            }
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
            List<OrderStatusReportFlat> dealers = new ArrayList<>();
            for (DealersItem dealer : report.getDealersItems()) {
                OrderStatusReportFlat deal = new OrderStatusReportFlat(
                        2,
                        dealer.getDate(),
                        dealer.getOrderWeight(),
                        dealer.getPendingOrderWeight(),
                        dealer.getInProgressOrderWeight(),
                        dealer.getUndeliverdOrderWeight(),
                        0d,
                        dealer.getDealerName(),
                        dealer.getDealerCode(),
                        dealer.getDeliverdOrderWeight(),
                        "",
                        ""
                );
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
                deal.setChilds(customers);
                dealers.add(deal);
            }
            rep.setChilds(dealers);
            reports.add(rep);
        }
        return reports;
    }


    private void generateTreeFromData(List<OrderStatusReportFlat> treeData) {
        for (OrderStatusReportFlat level1 : treeData) {
          TreeNode l1 = new TreeNode(level1).setViewHolder(new TreeNodeHolder(getContext(),
                    null));

            for (OrderStatusReportFlat level2 : level1.getChilds()) {
                TreeNode l2 = new TreeNode(level2).setViewHolder(new TreeNodeHolder(getContext(),
                        null));

                for (OrderStatusReportFlat level3 : level2.getChilds()) {
                    TreeNode l3 = new TreeNode(level3).setViewHolder(new TreeNodeHolder(getContext(), (parentNode) -> {

                        childCount++;
                        Log.d("orderreportfragment", "loading: " + childCount);
                        if (childCount >= totalCount) {
                            Log.d("orderreportfragment", "end load. ");
                            containerView.setEnabled(true);
                            childCount = 0;
                            totalCount = 0;
                        }
                    }));
                    l2.addChild(l3);
                }
                l1.addChild(l2);
            }
            root.addChild(l1);
        }
    }

    int childCount = 0;
    int totalCount = 0;
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
}
