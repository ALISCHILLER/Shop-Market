package com.varanegar.vaslibrary.webapi.apiNew;

import com.varanegar.vaslibrary.model.CheckCustomerCreditsModel;
import com.varanegar.vaslibrary.model.newmodel.checkCustomerCredits.CheckCustomerCreditModel;
import com.varanegar.vaslibrary.model.newmodel.customergrouplastsalesreport.CustomerGroupLastSalesReportModel;
import com.varanegar.vaslibrary.ui.fragment.news_fragment.model.NewsData_Model;
import com.varanegar.vaslibrary.webapi.apiNew.modelNew.PinRequestViewModel;
import com.varanegar.vaslibrary.webapi.apiNew.modelNew.RoleCodeCustomerRequestViewModel;
import com.varanegar.vaslibrary.webapi.apiNew.modelNew.RoleCodeViewModel;
import com.varanegar.vaslibrary.webapi.apiNew.modelNew.customer_not_allowed_product.CustomerNotAllowProductModel;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface InApiNew {

    @POST("api/v2/ngt/customer/pinrequest")
    Call<String> sendPinCode(
            @Body PinRequestViewModel pinRequestViewModel
            );

    @GET("api/v2/ngt/newsletter/sync/loaddata")
    Call<List<NewsData_Model>>getNewsData();


    @POST("api/v2/ngt/customer/getrolecode")
    Call<List<RoleCodeViewModel>>getCodeNaghsh(
            @Body RoleCodeCustomerRequestViewModel roleCodeCustomerViewModel
    );

    @POST("api/v2/ngt/customer/GetCustomerProductBlackList")
    Call<List<CustomerNotAllowProductModel>>getProductNotAllowed(
            @Body List<String> customerId
    );

    @GET("api/v2/ngt/customer/CheckCustomerCredits")
    Call<List<CheckCustomerCreditModel>>CheckCustomerCredits (
            @Query("CustomersId") List<String> customerCode);

    @GET("api/v2/ngt/ReviewReport/CustomerGroupLastsalesReport")
    Call<List<CustomerGroupLastSalesReportModel>>CustomerLastSaleReport (
            @Query("DealersId") List<String> dealersId,
            @Query("StartDate") String startDate,
            @Query("EndDate") String endDate);
}
