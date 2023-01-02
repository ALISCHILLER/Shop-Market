package com.varanegar.vaslibrary.manager.newmanager.dataCustomersContentManager;

import android.content.Context;
import android.util.Log;

import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.newmanager.CustomerSumMoneyAndWeightReport.CustomerSumMoneyAndWeightReportManager;
import com.varanegar.vaslibrary.manager.newmanager.CustomerSumMoneyAndWeightReport.CustomerSumMoneyAndWeightReportModel;
import com.varanegar.vaslibrary.manager.newmanager.customerGroupSimilarProductsalesReport.CustomerGroupSimilarProductsalesReportManager;
import com.varanegar.vaslibrary.manager.newmanager.customerXmounthsalereport.CustomerXMounthSaleReportManager;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.product.ProductModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.apiNew.ApiNew;

import java.util.List;
import okhttp3.Request;
import retrofit2.Call;

public class DataCustomerContentManager {

    public interface Callback {
        void onSuccess();

        void onError(String error);
    }

    public static void getCustomerXMounthSaleRepor(final Context context,
                                                   CustomerModel customer,final Callback callback) {


        ApiNew apiNew=new ApiNew(context);
        Call<List<ProductModel>> call = apiNew
                .CustomerXMounthSaleReport(customer.CustomerCode);

        apiNew.runWebRequest(call, new WebCallBack<List<ProductModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<ProductModel> result, Request request) {
                CustomerXMounthSaleReportManager customerXMounthSaleReportManager=
                        new CustomerXMounthSaleReportManager(context);
                if (result.size() > 0) {
                    customerXMounthSaleReportManager.save(customer.UniqueId, result);
                }
                CustomerGroupSimilarProductsalesReport(context, callback, customer);
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, context);
                callback.onError(err);
                Log.e("err", String.valueOf(err));
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                callback.onError(context.getString(R.string.connection_to_server_failed));
            }
        });

    }



    public static void CustomerGroupSimilarProductsalesReport(final Context context,
                                                   final Callback callback, CustomerModel customer) {
        ApiNew apiNew=new ApiNew(context);
        Call<List<ProductModel>> call = apiNew
                .CustomerGroupSimilarProductsalesReport(customer.CustomerCode);

        apiNew.runWebRequest(call, new WebCallBack<List<ProductModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<ProductModel> result, Request request) {
                CustomerGroupSimilarProductsalesReportManager customerGroupSimilarProductsalesReportManager
                        =new CustomerGroupSimilarProductsalesReportManager(context);
                if (result.size() > 0) {
                    customerGroupSimilarProductsalesReportManager.save(customer.UniqueId,result);
                }
                CustomerSumMoneyAndWeightReport(context, callback, customer);
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, context);
                callback.onError(err);
                Log.e("err", String.valueOf(err));
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                callback.onError(context.getString(R.string.connection_to_server_failed));
            }
        });
    }

    public static void CustomerSumMoneyAndWeightReport(final Context context,
                                                              final Callback callback, CustomerModel customer) {
        ApiNew apiNew=new ApiNew(context);
        Call<List<CustomerSumMoneyAndWeightReportModel>> call = apiNew
                .CustomerSumMoneyAndWeightReport(customer.CustomerCode);

        apiNew.runWebRequest(call, new WebCallBack<List<CustomerSumMoneyAndWeightReportModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<CustomerSumMoneyAndWeightReportModel> result, Request request) {
                CustomerSumMoneyAndWeightReportManager  customerSumMoneyAndWeightReportManager=
                        new CustomerSumMoneyAndWeightReportManager(context);
                if (result.size() > 0) {
                    customerSumMoneyAndWeightReportManager.save(customer.UniqueId,result);
                }
                callback.onSuccess();
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, context);
                callback.onError(err);
                Log.e("err", String.valueOf(err));
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                callback.onError(context.getString(R.string.connection_to_server_failed));
            }
        });
    }


}
