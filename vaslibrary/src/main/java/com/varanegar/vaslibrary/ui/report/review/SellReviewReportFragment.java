package com.varanegar.vaslibrary.ui.report.review;

import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.ui.report.review.adapter.SellReviewReportAdapter;
import com.varanegar.vaslibrary.webapi.reviewreport.ReviewReportApi;
import com.varanegar.vaslibrary.webapi.reviewreport.SellReviewReportViewModel;

import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Torabi on 5/16/2018.
 */

public class SellReviewReportFragment extends BaseReviewReportFragment<SellReviewReportViewModel> {

    @Override
    protected Call<List<SellReviewReportViewModel>> reportApi() {
        return new ReviewReportApi(getContext()).sell(getDealerId().toString(), getStartDateString(), getEndDateString());
    }

    @Override
    protected SimpleReportAdapter<SellReviewReportViewModel> createAdapter() {
        return new SellReviewReportAdapter(getVaranegarActvity());
    }

    @Override
    protected String getTitle() {
        return getString(R.string.sell_invoice);
    }

    @Override
    protected String isEnabled() {
        return null;
    }


}
