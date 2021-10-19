package com.varanegar.vaslibrary.ui.report.review.adapter;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.webapi.reviewreport.OrderReviewReportView;
import com.varanegar.vaslibrary.webapi.reviewreport.OrderReviewReportViewModel;

/**
 * Created by A.Torabi on 7/7/2018.
 */

public class OrderReviewReportAdapter extends SimpleReportAdapter<OrderReviewReportViewModel> {
    public OrderReviewReportAdapter(MainVaranegarActivity activity) {
        super(activity, OrderReviewReportViewModel.class);
    }

    public OrderReviewReportAdapter(VaranegarFragment fragment) {
        super(fragment, OrderReviewReportViewModel.class);
    }

    @Override
    public void bind(ReportColumns columns, OrderReviewReportViewModel entity) {
        columns.add(bind(entity, OrderReviewReportView.RecordId, getActivity().getString(R.string.row)).setWeight(0.5f));
        columns.add(bind(entity, OrderReviewReportView.CustomerCode, getActivity().getString(R.string.customer_code)).setFrizzed());
        columns.add(bind(entity, OrderReviewReportView.CustomerName, getActivity().getString(R.string.customer_name)).setWeight(2).setFrizzed());
        columns.add(bind(entity, OrderReviewReportView.StoreName, getActivity().getString(R.string.store_name)).setWeight(2));
        columns.add(bind(entity, OrderReviewReportView.OrderDate, getActivity().getString(R.string.order_date)).sendToDetail());
        columns.add(bind(entity, OrderReviewReportView.OrderAmount, getActivity().getString(R.string.order_amount)).sendToDetail().calcTotal());
        columns.add(bind(entity, OrderReviewReportView.OrderStatus, getActivity().getString(R.string.status)).sendToDetail());
        columns.add(bind(entity, OrderReviewReportView.PaymentUsanceTitle, getActivity().getString(R.string.payment_type)).sendToDetail());
        columns.add(bind(entity, OrderReviewReportView.Comment, getActivity().getString(R.string.comment)).sendToDetail());
        columns.add(bind(entity, OrderReviewReportView.OrderNo, getActivity().getString(R.string.request_no)).sendToDetail());
    }
}
