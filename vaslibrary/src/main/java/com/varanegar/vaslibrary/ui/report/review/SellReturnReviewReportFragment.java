package com.varanegar.vaslibrary.ui.report.review;

import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.ui.report.review.adapter.SellReturnReviewReportAdapter;
import com.varanegar.vaslibrary.webapi.reviewreport.ReviewReportApi;
import com.varanegar.vaslibrary.webapi.reviewreport.SellReturnReviewReportViewModel;

import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Torabi on 5/16/2018.
 */

public class SellReturnReviewReportFragment extends BaseReviewReportFragment<SellReturnReviewReportViewModel> {
    @Override
    protected Call<List<SellReturnReviewReportViewModel>> reportApi() {
        return new ReviewReportApi(getContext()).sellReturn(getDealerId().toString(), getStartDateString(), getEndDateString());
    }

    @Override
    protected SimpleReportAdapter<SellReturnReviewReportViewModel> createAdapter() {
        return new SellReturnReviewReportAdapter(getVaranegarActvity());
    }

    @Override
    protected String getTitle() {
        return getString(R.string.sell_return_report);
    }

    @Override
    protected String isEnabled() {
        return null;
    }
}
