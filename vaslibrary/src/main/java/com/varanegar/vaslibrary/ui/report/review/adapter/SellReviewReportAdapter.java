package com.varanegar.vaslibrary.ui.report.review.adapter;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.webapi.reviewreport.SellReviewReportView;
import com.varanegar.vaslibrary.webapi.reviewreport.SellReviewReportViewModel;

/**
 * Created by A.Torabi on 7/7/2018.
 */

public class SellReviewReportAdapter extends SimpleReportAdapter<SellReviewReportViewModel> {
    public SellReviewReportAdapter(MainVaranegarActivity activity) {
        super(activity, SellReviewReportViewModel.class);
    }

    public SellReviewReportAdapter(VaranegarFragment fragment) {
        super(fragment, SellReviewReportViewModel.class);
    }

    @Override
    public void bind(ReportColumns columns, SellReviewReportViewModel entity) {
        columns.add(bind(entity, SellReviewReportView.RecordId, getActivity().getString(R.string.row)).setWeight(0.5f));
        columns.add(bind(entity, SellReviewReportView.CustomerCode, getActivity().getString(R.string.customer_code)).setFrizzed());
        columns.add(bind(entity, SellReviewReportView.CustomerName, getActivity().getString(R.string.customer_name)).setWeight(2.5f));
        columns.add(bind(entity, SellReviewReportView.StoreName, getActivity().getString(R.string.store_name)).setWeight(2.5f));
        columns.add(bind(entity, SellReviewReportView.DistStatus, getActivity().getString(R.string.status)).sendToDetail());
        columns.add(bind(entity, SellReviewReportView.SellNo, getActivity().getString(R.string.invoice_no)).sendToDetail());
        columns.add(bind(entity, SellReviewReportView.SellDate, getActivity().getString(R.string.sell_date)).sendToDetail());
        columns.add(bind(entity, SellReviewReportView.VoucherNo, getActivity().getString(R.string.voucher_no)).sendToDetail());
        columns.add(bind(entity, SellReviewReportView.VoucherDate, getActivity().getString(R.string.voucher_date)).sendToDetail());
        columns.add(bind(entity, SellReviewReportView.PaymentUsanceTitle, getActivity().getString(R.string.payment_type)).sendToDetail());
        columns.add(bind(entity, SellReviewReportView.SellAmount, getActivity().getString(R.string.sell_amount)).sendToDetail().setWeight(1.5f).calcTotal());
        columns.add(bind(entity, SellReviewReportView.SellDiscountAmount, getActivity().getString(R.string.discount_amount)).sendToDetail().calcTotal());
        columns.add(bind(entity, SellReviewReportView.SellAddAmount, getActivity().getString(R.string.add_amount)).sendToDetail());
        columns.add(bind(entity, SellReviewReportView.SellNetAmount, getActivity().getString(R.string.sell_net_amount)).sendToDetail().setWeight(1.5f));
        columns.add(bind(entity, SellReviewReportView.DistDate, getActivity().getString(R.string.dist_date)).sendToDetail());
        columns.add(bind(entity, SellReviewReportView.DistributerName, getActivity().getString(R.string.distributer_name)).sendToDetail());
        columns.add(bind(entity, SellReviewReportView.DistNo, getActivity().getString(R.string.dist_no)).sendToDetail());
        columns.add(bind(entity, SellReviewReportView.Comment, getActivity().getString(R.string.comment)).sendToDetail());
    }
}
