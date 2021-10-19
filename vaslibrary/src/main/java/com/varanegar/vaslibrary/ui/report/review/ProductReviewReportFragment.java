package com.varanegar.vaslibrary.ui.report.review;

import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.ui.report.review.adapter.ProductReviewReportAdapter;
import com.varanegar.vaslibrary.webapi.reviewreport.ProductReviewReportViewModel;
import com.varanegar.vaslibrary.webapi.reviewreport.ReviewReportApi;

import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Torabi on 5/16/2018.
 */

public class ProductReviewReportFragment extends BaseReviewReportFragment<ProductReviewReportViewModel> {
    @Override
    protected Call<List<ProductReviewReportViewModel>> reportApi() {
        return new ReviewReportApi(getContext()).product(getDealerId().toString(), getStartDateString(), getEndDateString());
    }

    @Override
    protected SimpleReportAdapter<ProductReviewReportViewModel> createAdapter() {
        return new ProductReviewReportAdapter(getVaranegarActvity());
    }

    @Override
    protected String getTitle() {
        return getString(R.string.product_sale);
    }

    @Override
    protected String isEnabled() {
        return null;
    }
}
