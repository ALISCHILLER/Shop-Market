package com.varanegar.vaslibrary.ui.report.review;

import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.model.targetcustomerproductview.TargetCustomerProductViewModel;
import com.varanegar.vaslibrary.ui.report.review.adapter.TargetReviewReportAdapter;
import com.varanegar.vaslibrary.webapi.reviewreport.ReviewReportApi;
import com.varanegar.vaslibrary.webapi.reviewreport.TargetReviewReportViewModel;

import java.util.List;

import retrofit2.Call;

/**
 * Created by e.hashemzadeh on 20/06/21.
 */

public class TargetReviewReportFragment extends BaseReviewReportWithoutPeriosFragment<TargetReviewReportViewModel> {
    @Override
    protected Call<List<TargetReviewReportViewModel>> reportApi() {
        return new ReviewReportApi(getContext()).target(getDealerId().toString());
    }

    @Override
    protected SimpleReportAdapter<TargetReviewReportViewModel> createAdapter() {
        return new TargetReviewReportAdapter(this);
    }

    @Override
    protected String getTitle() {
        return getString(R.string.target_report);
    }

    @Override
    protected String isEnabled() {
        return null;
    }
}
