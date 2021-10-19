package com.varanegar.vaslibrary.ui.report.review;

import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.ui.report.review.adapter.ProductGroupReviewReportAdapter;
import com.varanegar.vaslibrary.webapi.reviewreport.ProductGroupReviewReportViewModel;
import com.varanegar.vaslibrary.webapi.reviewreport.ReviewReportApi;

import java.util.List;

import retrofit2.Call;

/**
 * Created by A.Torabi on 5/16/2018.
 */

public class ProductGroupReviewReportFragment extends BaseReviewReportFragment<ProductGroupReviewReportViewModel> {
    @Override
    protected Call<List<ProductGroupReviewReportViewModel>> reportApi() {
        return new ReviewReportApi(getContext()).productGroup(getDealerId().toString(), getStartDateString(), getEndDateString());
    }

    @Override
    protected SimpleReportAdapter<ProductGroupReviewReportViewModel> createAdapter() {
        return new ProductGroupReviewReportAdapter(getVaranegarActvity());
    }

    @Override
    protected String getTitle() {
        return getString(R.string.product_group_sale);
    }

    @Override
    protected String isEnabled() {
        return null;
    }
}
