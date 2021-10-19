package com.varanegar.vaslibrary.ui.report.review.adapter;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.base.VaranegarFragment;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.webapi.reviewreport.ProductGroupReviewReportView;
import com.varanegar.vaslibrary.webapi.reviewreport.ProductGroupReviewReportViewModel;

/**
 * Created by A.Torabi on 7/7/2018.
 */

public class ProductGroupReviewReportAdapter extends SimpleReportAdapter<ProductGroupReviewReportViewModel> {
    public ProductGroupReviewReportAdapter(MainVaranegarActivity activity) {
        super(activity, ProductGroupReviewReportViewModel.class);
    }

    public ProductGroupReviewReportAdapter(VaranegarFragment fragment) {
        super(fragment, ProductGroupReviewReportViewModel.class);
    }

    @Override
    public void bind(ReportColumns columns, ProductGroupReviewReportViewModel entity) {
        columns.add(bind(entity, ProductGroupReviewReportView.RecordId, getActivity().getString(R.string.row)).setWeight(.5f));
        columns.add(bind(entity, ProductGroupReviewReportView.ProductGroupName, getActivity().getString(R.string.product_group)).setWeight(1.5f));
        columns.add(bind(entity, ProductGroupReviewReportView.SellQty, getActivity().getString(R.string.total_qty)).sendToDetail());
        columns.add(bind(entity, ProductGroupReviewReportView.SellPackQty, getActivity().getString(R.string.total_pack_qty)).setWeight(2).sendToDetail());
        columns.add(bind(entity, ProductGroupReviewReportView.SellAmount, getActivity().getString(R.string.request_amount)).sendToDetail());
        columns.add(bind(entity, ProductGroupReviewReportView.PrizeQty, getActivity().getString(R.string.prize_qty_smallest_unit)).setWeight(2).sendToDetail());
        columns.add(bind(entity, ProductGroupReviewReportView.FreeReasonQty, getActivity().getString(R.string.free_qty_smallest_unit)).setWeight(2).sendToDetail());
        columns.add(bind(entity, ProductGroupReviewReportView.SellAmount, getActivity().getString(R.string.gross_amount)).sendToDetail());
        columns.add(bind(entity, ProductGroupReviewReportView.SellDiscountAmount, getActivity().getString(R.string.discount_amount)).sendToDetail());
        columns.add(bind(entity, ProductGroupReviewReportView.SellAddAmount, getActivity().getString(R.string.add_amount)).sendToDetail());
        columns.add(bind(entity, ProductGroupReviewReportView.SellNetAmount, getActivity().getString(R.string.sell_net_amount)));
        columns.add(bind(entity, ProductGroupReviewReportView.SellReturnQty, getActivity().getString(R.string.return_qty_count)).sendToDetail());
        columns.add(bind(entity, ProductGroupReviewReportView.HealthySellReturnQty, getActivity().getString(R.string.healthy_return_qty)).setWeight(2).sendToDetail());
        columns.add(bind(entity, ProductGroupReviewReportView.UnHealthySellReturnQty, getActivity().getString(R.string.waste_return_qty)).setWeight(2).sendToDetail());
        columns.add(bind(entity, ProductGroupReviewReportView.SellReturnNetAmount, getActivity().getString(R.string.sell_return_net_amount)).setWeight(2).sendToDetail());
        columns.add(bind(entity, ProductGroupReviewReportView.AmountWithoutReturn, getActivity().getString(R.string.amount_without_return)).setWeight(3).sendToDetail());
    }
}
