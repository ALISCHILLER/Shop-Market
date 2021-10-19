package com.varanegar.vaslibrary.ui.report.review.adapter;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.webapi.reviewreport.SellReturnReviewReportView;
import com.varanegar.vaslibrary.webapi.reviewreport.SellReturnReviewReportViewModel;

/**
 * Created by A.Torabi on 7/7/2018.
 */

public class SellReturnReviewReportAdapter extends SimpleReportAdapter<SellReturnReviewReportViewModel> {
    public SellReturnReviewReportAdapter(MainVaranegarActivity activity) {
        super(activity, SellReturnReviewReportViewModel.class);
    }

    public SellReturnReviewReportAdapter(VaranegarFragment fragment) {
        super(fragment, SellReturnReviewReportViewModel.class);
    }
    @Override
    public void bind(ReportColumns columns, SellReturnReviewReportViewModel entity) {
        columns.add(bind(entity, SellReturnReviewReportView.RecordId, getActivity().getString(R.string.row)).setWeight(.5f));
        columns.add(bind(entity, SellReturnReviewReportView.CustomerCode, getActivity().getString(R.string.customer_code)).setFrizzed());
        columns.add(bind(entity, SellReturnReviewReportView.CustomerName, getActivity().getString(R.string.customer_name)).setWeight(2).setFrizzed());
        columns.add(bind(entity, SellReturnReviewReportView.StoreName, getActivity().getString(R.string.store_name)));
        columns.add(bind(entity, SellReturnReviewReportView.SellReturnDate, getActivity().getString(R.string.return_date)));
        columns.add(bind(entity, SellReturnReviewReportView.SellReturnNo, getActivity().getString(R.string.sell_return_no)).sendToDetail().setWeight(1.5f));
        columns.add(bind(entity, SellReturnReviewReportView.TSaleNo, getActivity().getString(R.string.invoice_ref_no)).sendToDetail().setWeight(1.5f));
        columns.add(bind(entity, SellReturnReviewReportView.ReturnTypeName, getActivity().getString(R.string.return_type)).sendToDetail());
        columns.add(bind(entity, SellReturnReviewReportView.ReturnReasonName, getActivity().getString(R.string.return_reason)).sendToDetail());
        columns.add(bind(entity, SellReturnReviewReportView.ReturnTypeName, getActivity().getString(R.string.return_type)).sendToDetail());
        columns.add(bind(entity, SellReturnReviewReportView.SellReturnAmount, getActivity().getString(R.string.sell_return_gross_amount)).setWeight(2).sendToDetail().calcTotal());
        columns.add(bind(entity, SellReturnReviewReportView.SellReturnDiscountAmount, getActivity().getString(R.string.sell_return_discount_amount)).sendToDetail().setWeight(1.5f).calcTotal());
        columns.add(bind(entity, SellReturnReviewReportView.SellReturnAddAmount, getActivity().getString(R.string.sell_return_add_amount)).sendToDetail().setWeight(1.5f).calcTotal());
        columns.add(bind(entity, SellReturnReviewReportView.SellReturnNetAmount, getActivity().getString(R.string.sell_return_net_amount)).sendToDetail().setWeight(1.5f).calcTotal());
        columns.add(bind(entity, SellReturnReviewReportView.DistNo, getActivity().getString(R.string.dist_no)).sendToDetail());
        columns.add(bind(entity, SellReturnReviewReportView.DistributerName, getActivity().getString(R.string.distributer_name)).sendToDetail());
        columns.add(bind(entity, SellReturnReviewReportView.Comment, getActivity().getString(R.string.comment)).sendToDetail());
    }
}
