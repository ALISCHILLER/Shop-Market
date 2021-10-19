package com.varanegar.vaslibrary.ui.report.review.adapter;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.webapi.reviewreport.ProductReviewReportView;
import com.varanegar.vaslibrary.webapi.reviewreport.ProductReviewReportViewModel;

/**
 * Created by A.Torabi on 7/7/2018.
 */

public class ProductReviewReportAdapter extends SimpleReportAdapter<ProductReviewReportViewModel> {
    public ProductReviewReportAdapter(MainVaranegarActivity activity) {
        super(activity, ProductReviewReportViewModel.class);
    }

    public ProductReviewReportAdapter(VaranegarFragment fragment) {
        super(fragment, ProductReviewReportViewModel.class);
    }
    @Override
    public void bind(ReportColumns columns, ProductReviewReportViewModel entity) {
        columns.add(bind(entity, ProductReviewReportView.RecordId, getActivity().getString(R.string.row)).setWeight(.5f));
        columns.add(bind(entity, ProductReviewReportView.ProductCode, getActivity().getString(R.string.product_code)).setFrizzed());
        columns.add(bind(entity, ProductReviewReportView.ProductName, getActivity().getString(R.string.product_name)).setWeight(2).setFrizzed());
        columns.add(bind(entity, ProductReviewReportView.SellCount, getActivity().getString(R.string.sell_qty)).calcTotal());
        columns.add(bind(entity, ProductReviewReportView.SellUnitName, getActivity().getString(R.string.product_unit_name)).sendToDetail());
        columns.add(bind(entity, ProductReviewReportView.SellQty, getActivity().getString(R.string.total_qty)).sendToDetail().calcTotal());
        columns.add(bind(entity, ProductReviewReportView.ProductGroupName, getActivity().getString(R.string.product_group)).sendToDetail());
        columns.add(bind(entity, ProductReviewReportView.PrizeQty, getActivity().getString(R.string.prize_qty_smallest_unit)).setWeight(2).sendToDetail().calcTotal());
        columns.add(bind(entity, ProductReviewReportView.FreeReasonQty, getActivity().getString(R.string.free_qty_smallest_unit)).setWeight(2).sendToDetail().calcTotal());
        columns.add(bind(entity, ProductReviewReportView.SellAmount, getActivity().getString(R.string.sell_amount)).sendToDetail().setWeight(1.5f).calcTotal());
        columns.add(bind(entity, ProductReviewReportView.SellDiscountAmount, getActivity().getString(R.string.discount_amount)).sendToDetail().calcTotal());
        columns.add(bind(entity, ProductReviewReportView.SellAddAmount, getActivity().getString(R.string.add_amount)).sendToDetail().calcTotal());
        columns.add(bind(entity, ProductReviewReportView.SellNetAmount, getActivity().getString(R.string.sell_net_amount)).sendToDetail().setWeight(1.5f).calcTotal());
        columns.add(bind(entity, ProductReviewReportView.SellReturnQty, getActivity().getString(R.string.return_qty_count)).sendToDetail().calcTotal());
        columns.add(bind(entity, ProductReviewReportView.HealthySellReturnQty, getActivity().getString(R.string.healthy_return_qty)).setWeight(2).sendToDetail().calcTotal());
        columns.add(bind(entity, ProductReviewReportView.UnHealthySellReturnQty, getActivity().getString(R.string.waste_return_qty)).setWeight(2).sendToDetail().calcTotal());
        columns.add(bind(entity, ProductReviewReportView.SellReturnNetAmount, getActivity().getString(R.string.sell_return_net_amount)).sendToDetail().setWeight(2).calcTotal());
        columns.add(bind(entity, ProductReviewReportView.AmountWithoutReturn, getActivity().getString(R.string.amount_without_return)).sendToDetail().setWeight(2).calcTotal());
    }
}
