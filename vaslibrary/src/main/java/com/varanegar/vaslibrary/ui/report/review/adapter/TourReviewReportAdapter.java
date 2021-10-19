package com.varanegar.vaslibrary.ui.report.review.adapter;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.util.recycler.ItemContextView;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.model.TourStatusSummaryView;
import com.varanegar.vaslibrary.model.TourStatusSummaryViewModel;

/**
 * Created by A.Jafarzadeh on 9/9/2018.
 */

public class TourReviewReportAdapter extends SimpleReportAdapter<TourStatusSummaryViewModel> {
    public TourReviewReportAdapter(MainVaranegarActivity activity) {
        super(activity, TourStatusSummaryViewModel.class);
    }

    @Override
    public void bind(ReportColumns columns, TourStatusSummaryViewModel entity) {
        super.bind(columns, entity);
        columns.add(bind(entity, TourStatusSummaryView.TourNo, getActivity().getString(R.string.tour_no)).setFrizzed().setSortable());
        columns.add(bind(entity, TourStatusSummaryView.TourPDate, getActivity().getString(R.string.tour_date)).setSortable());
        columns.add(bind(entity, TourStatusSummaryView.AgentName, getActivity().getString(R.string.agent_name)).setFrizzed().setFilterable().setSortable());
        columns.add(bind(entity, TourStatusSummaryView.TourStatusName, getActivity().getString(R.string.status)).setSortable().setFrizzed());
        columns.add(bind(entity, TourStatusSummaryView.StartPTime, getActivity().getString(R.string.start_time)).setWeight(2).setSortable());
        columns.add(bind(entity, TourStatusSummaryView.EndPTime, getActivity().getString(R.string.end_time)).setWeight(2).setSortable());
        columns.add(bind(entity, TourStatusSummaryView.PathTitle, getActivity().getString(R.string.path_title)).setWeight(1.5f));
        columns.add(bind(entity, TourStatusSummaryView.CustomerCount, getActivity().getString(R.string.customers_count)).setWeight(1.5f).setSortable());
        columns.add(bind(entity, TourStatusSummaryView.ReturnInvoiceCount, getActivity().getString(R.string.return_invoice_count)).setWeight(2).setSortable());
        columns.add(bind(entity, TourStatusSummaryView.OrderCount, getActivity().getString(R.string.order_count)).setWeight(1.5f).setSortable());
        columns.add(bind(entity, TourStatusSummaryView.NoOrderCount, getActivity().getString(R.string.lack_of_order_count)).setWeight(1.5f).setSortable());
        columns.add(bind(entity, TourStatusSummaryView.VisitCount, getActivity().getString(R.string.visit_count)).setSortable());
        columns.add(bind(entity, TourStatusSummaryView.NoVisitCount, getActivity().getString(R.string.lack_of_visit_count)).setWeight(1.5f).setSortable());
        columns.add(bind(entity, TourStatusSummaryView.ReturnInvoiceRequestCount, getActivity().getString(R.string.return_invoice_request_count)).setWeight(2).setSortable());
        columns.add(bind(entity, TourStatusSummaryView.SuccessVisitAvg, getActivity().getString(R.string.success_visit_avg)).setWeight(1.5f).setSortable());
        columns.add(bind(entity, TourStatusSummaryView.TotalOrderAmount, getActivity().getString(R.string.total_orders_amount)).setWeight(1.5f).setSortable());
        columns.add(bind(entity, TourStatusSummaryView.TotalInvoiceAmount, getActivity().getString(R.string.total_invoices_amount)).setWeight(1.5f).setSortable());
        columns.add(bind(entity, TourStatusSummaryView.InvoiceLineAvg, getActivity().getString(R.string.invoice_line_avg)).setWeight(1.5f).setSortable());
        columns.add(bind(entity, TourStatusSummaryView.SaleVolume, getActivity().getString(R.string.sale_volume)).setSortable());
        columns.add(bind(entity, TourStatusSummaryView.TotalSaleAvg, getActivity().getString(R.string.total_sale_avg)).setWeight(1.5f).setSortable());
        columns.add(bind(entity, TourStatusSummaryView.InStoreTimeStr, getActivity().getString(R.string.in_store_time)).setWeight(1.5f));
        columns.add(bind(entity, TourStatusSummaryView.InStoreTimeAvgStr, getActivity().getString(R.string.in_store_time_avg)).setWeight(2));
        columns.add(bind(entity, TourStatusSummaryView.OutOfStoreTimeStr, getActivity().getString(R.string.out_of_store_time)).setWeight(1.5f));
        columns.add(bind(entity, TourStatusSummaryView.OutOfStoreTimeAvgStr, getActivity().getString(R.string.out_of_store_time_avg)).setWeight(2));
        columns.add(bind(entity, TourStatusSummaryView.BetweenTwoStoresTimeAvgStr, getActivity().getString(R.string.between_two_stores_time_avg)).setWeight(2.5f));
    }

    @Override
    protected ItemContextView<TourStatusSummaryViewModel> onCreateContextView() {
        TourStatusSummaryContextView contextView = new TourStatusSummaryContextView(getAdapter(), getActivity());
        contextView.onTourChanged = new TourStatusSummaryContextView.OnTourChanged() {
            @Override
            public void run() {
                TourReviewReportAdapter.this.refresh();
            }
        };
        return contextView;
    }
}
