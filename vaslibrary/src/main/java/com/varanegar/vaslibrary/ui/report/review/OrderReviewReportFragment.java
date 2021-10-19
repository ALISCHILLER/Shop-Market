package com.varanegar.vaslibrary.ui.report.review;

import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.ui.report.review.adapter.OrderReviewReportAdapter;
import com.varanegar.vaslibrary.webapi.reviewreport.OrderReviewReportViewModel;
import com.varanegar.vaslibrary.webapi.reviewreport.ReviewReportApi;

import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Torabi on 5/16/2018.
 */

public class OrderReviewReportFragment extends BaseReviewReportFragment<OrderReviewReportViewModel> {
    @Override
    protected Call<List<OrderReviewReportViewModel>> reportApi() {
        return new ReviewReportApi(getContext()).order(getDealerId().toString(), getStartDateString(), getEndDateString());
    }

    @Override
    protected SimpleReportAdapter<OrderReviewReportViewModel> createAdapter() {
        return new OrderReviewReportAdapter(this);
    }

    @Override
    protected String getTitle() {
        return getString(R.string.sell_request);
    }

    @Override
    protected String isEnabled() {
        return null;
    }
}
