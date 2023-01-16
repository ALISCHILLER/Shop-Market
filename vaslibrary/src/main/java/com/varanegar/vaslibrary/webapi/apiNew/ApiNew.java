package com.varanegar.vaslibrary.webapi.apiNew;

import android.content.Context;

import com.varanegar.vaslibrary.manager.customergrouplastsalesreportmanager.ReviewReportRequestViewModel;
import com.varanegar.vaslibrary.manager.newmanager.CustomerSumMoneyAndWeightReport.CustomerSumMoneyAndWeightReportModel;
import com.varanegar.vaslibrary.manager.newmanager.customerGroupSimilarProductsalesReport.CustomerGroupSimilarProductsalesReportModel;
import com.varanegar.vaslibrary.manager.newmanager.customerXmounthsalereport.CustomerXMounthSaleReportModel;
import com.varanegar.vaslibrary.manager.newmanager.dealercommission.DealerCommissionDataModel;
import com.varanegar.vaslibrary.model.CheckCustomerCreditsModel;
import com.varanegar.vaslibrary.model.newmodel.checkCustomerCredits.CheckCustomerCreditModel;
import com.varanegar.vaslibrary.model.newmodel.commodity_rationing.CommodityRationingModel;
import com.varanegar.vaslibrary.model.newmodel.customergrouplastsalesreport.CustomerGroupLastSalesReportModel;
import com.varanegar.vaslibrary.model.product.ProductModel;
import com.varanegar.vaslibrary.ui.fragment.news_fragment.model.NewsData_Model;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.TokenType;
import com.varanegar.vaslibrary.webapi.apiNew.modelNew.PinRequestViewModel;
import com.varanegar.vaslibrary.webapi.apiNew.modelNew.RoleCodeCustomerRequestViewModel;
import com.varanegar.vaslibrary.webapi.apiNew.modelNew.RoleCodeViewModel;
import com.varanegar.vaslibrary.webapi.apiNew.modelNew.customer_not_allowed_product.CustomerNotAllowProductModel;
import com.varanegar.vaslibrary.webapi.customer.ICustomerApi;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;

public class ApiNew extends BaseApi implements InApiNew {
    public ApiNew(Context context) {
        super(context);
    }

    @Override
    public Call<String> sendPinCode(PinRequestViewModel pinRequestViewModel) {
        InApiNew api = getRetrofitBuilder(TokenType.UserToken)
                .build().create(InApiNew.class);
        return api.sendPinCode(pinRequestViewModel);
    }

    @Override
    public Call<List<NewsData_Model>> getNewsData() {
        InApiNew api = getRetrofitBuilder(TokenType.UserToken)
                .build().create(InApiNew.class);
        return api.getNewsData();
    }

    @Override
    public Call<List<RoleCodeViewModel>> getCodeNaghsh(RoleCodeCustomerRequestViewModel roleCodeCustomerViewModel) {
        InApiNew api = getRetrofitBuilder(TokenType.UserToken)
                .build().create(InApiNew.class);
        return api.getCodeNaghsh(roleCodeCustomerViewModel);
    }

    @Override
    public Call<List<CustomerNotAllowProductModel>> getProductNotAllowed(List<String> customerId) {
        InApiNew api = getRetrofitBuilder(TokenType.UserToken)
                .build().create(InApiNew.class);
        return api.getProductNotAllowed(customerId);
    }

    @Override
    public Call<List<CheckCustomerCreditModel>> CheckCustomerCredits(List<String> customerCode) {
        InApiNew api = getRetrofitBuilder(TokenType.UserToken)
                .build().create(InApiNew.class);
        return api.CheckCustomerCredits(customerCode);
    }

    @Override
    public Call<List<CustomerGroupLastSalesReportModel>> CustomerLastSaleReport(List<String> customerCode) {
        InApiNew api = getRetrofitBuilder(TokenType.UserToken)
                .build().create(InApiNew.class);
        return api.CustomerLastSaleReport(customerCode);
    }

    @Override
    public Call<List<CommodityRationingModel>> getCommodityRationin(String dealersId) {
        InApiNew api = getRetrofitBuilder(TokenType.UserToken)
                .build().create(InApiNew.class);
        return api.getCommodityRationin(dealersId);
    }

    @Override
    public Call<DealerCommissionDataModel> getDealerCommissionData(String dealersId) {
        InApiNew api = getRetrofitBuilder(TokenType.UserToken)
                .build().create(InApiNew.class);
        return api.getDealerCommissionData(dealersId);
    }

    @Override
    public Call<List<CustomerXMounthSaleReportModel>> CustomerXMounthSaleReport(List<String> customersCode) {
        InApiNew api = getRetrofitBuilder(TokenType.UserToken)
                .build().create(InApiNew.class);
        return api.CustomerXMounthSaleReport(customersCode);
    }

    @Override
    public Call<List<CustomerGroupSimilarProductsalesReportModel>> CustomerGroupSimilarProductsalesReport(List<String> customersCode) {
        InApiNew api = getRetrofitBuilder(TokenType.UserToken)
                .build().create(InApiNew.class);
        return api.CustomerGroupSimilarProductsalesReport(customersCode);
    }

    @Override
    public Call<List<CustomerSumMoneyAndWeightReportModel>> CustomerSumMoneyAndWeightReport(List<String> customersCode) {
        InApiNew api = getRetrofitBuilder(TokenType.UserToken)
                .build().create(InApiNew.class);
        return api.CustomerSumMoneyAndWeightReport(customersCode);
    }


}
