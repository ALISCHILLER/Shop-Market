package com.varanegar.vaslibrary.manager.newmanager.dataCustomersContentManager;

import android.content.Context;
import android.util.Log;

import com.varanegar.framework.database.DbException;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.newmanager.CustomerSumMoneyAndWeightReport.CustomerSumMoneyAndWeightReportManager;
import com.varanegar.vaslibrary.manager.newmanager.CustomerSumMoneyAndWeightReport.CustomerSumMoneyAndWeightReportModel;
import com.varanegar.vaslibrary.manager.newmanager.CustomerSumMoneyAndWeightReport.CustomerSumMoneyAndWeightReportModelRepository;
import com.varanegar.vaslibrary.manager.newmanager.customerGroupSimilarProductsalesReport.CustomerGroupSimilarProductsalesReportManager;
import com.varanegar.vaslibrary.manager.newmanager.customerGroupSimilarProductsalesReport.CustomerGroupSimilarProductsalesReportModel;
import com.varanegar.vaslibrary.manager.newmanager.customerGroupSimilarProductsalesReport.CustomerGroupSimilarProductsalesReportModelRepository;
import com.varanegar.vaslibrary.manager.newmanager.customerXmounthsalereport.CustomerXMounthSaleReportManager;
import com.varanegar.vaslibrary.manager.newmanager.customerXmounthsalereport.CustomerXMounthSaleReportModel;
import com.varanegar.vaslibrary.manager.newmanager.customerXmounthsalereport.CustomerXMounthSaleReportModelRepository;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.newmodel.checkCustomerCredits.CheckCustomerCreditModel;
import com.varanegar.vaslibrary.model.newmodel.checkCustomerCredits.CheckCustomerCreditModelRepository;
import com.varanegar.vaslibrary.model.product.ProductModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.apiNew.ApiNew;

import java.util.List;
import okhttp3.Request;
import retrofit2.Call;
import timber.log.Timber;

public class DataCustomerContentManager {

    public interface Callback {
        void onSuccess();

        void onError(String error);
    }

    public static void getCustomerXMounthSaleRepor(
            final Context context,
            CustomerModel customer,
            List<String> customersCode,
            final Callback callback) {


        ApiNew apiNew=new ApiNew(context);
        Call<List<CustomerXMounthSaleReportModel>> call = apiNew
                .CustomerXMounthSaleReport(customersCode);

        apiNew.runWebRequest(call, new WebCallBack<List<CustomerXMounthSaleReportModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<CustomerXMounthSaleReportModel> result, Request request) {
                CustomerXMounthSaleReportManager customerXMounthSaleReportManager=
                        new CustomerXMounthSaleReportManager(context);


                CustomerXMounthSaleReportModelRepository repository=
                new CustomerXMounthSaleReportModelRepository();
                if (result.size() > 0) {
                    try {
                        repository.deleteAll();
                        repository.insert(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("err", String.valueOf(e)+"خطا در ذخیره سازی CustomerXMounthSaleReport");
                        callback.onError("خطا در ذخیره سازی CustomerXMounthSaleReport");
                    }
                }
                CustomerGroupSimilarProductsalesReport(context, callback, customer,customersCode);
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



    public static void CustomerGroupSimilarProductsalesReport(
            final Context context,
            final Callback callback,
            CustomerModel customer,
            List<String> customersCode) {
        ApiNew apiNew=new ApiNew(context);
        Call<List<CustomerGroupSimilarProductsalesReportModel>> call = apiNew
                .CustomerGroupSimilarProductsalesReport(customersCode);

        apiNew.runWebRequest(call, new WebCallBack<List<CustomerGroupSimilarProductsalesReportModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<CustomerGroupSimilarProductsalesReportModel> result, Request request) {
                CustomerGroupSimilarProductsalesReportManager customerGroupSimilarProductsalesReportManager
                        =new CustomerGroupSimilarProductsalesReportManager(context);
                CustomerGroupSimilarProductsalesReportModelRepository repository=
                new CustomerGroupSimilarProductsalesReportModelRepository();
                if (result.size() > 0) {
                    try {
                        repository.deleteAll();
                        repository.insert(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("err", String.valueOf(e));
                        callback.onError("خطا در ذخیره سازی CustomerGroupSimilarProductsalesReport");
                    }
                }
                CustomerSumMoneyAndWeightReport(context, callback, customer,customersCode);
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

    public static void CustomerSumMoneyAndWeightReport(
            final Context context,
            final Callback callback,
            CustomerModel customer,
            List<String> customersCode) {
        ApiNew apiNew=new ApiNew(context);
        Call<List<CustomerSumMoneyAndWeightReportModel>> call = apiNew
                .CustomerSumMoneyAndWeightReport(customersCode);

        apiNew.runWebRequest(call, new WebCallBack<List<CustomerSumMoneyAndWeightReportModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<CustomerSumMoneyAndWeightReportModel> result, Request request) {
                CustomerSumMoneyAndWeightReportManager  customerSumMoneyAndWeightReportManager=
                        new CustomerSumMoneyAndWeightReportManager(context);
                CustomerSumMoneyAndWeightReportModelRepository sumMoneyAndWeightRepor =
                        new CustomerSumMoneyAndWeightReportModelRepository();
                if (result.size() > 0) {
                   // customerSumMoneyAndWeightReportManager.save(customer.UniqueId,result);
                    try {
                        sumMoneyAndWeightRepor.deleteAll();
                        sumMoneyAndWeightRepor.insert(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onError("خطا در ذخیره سازی customerSumMoneyAndWeightReport");
                    }

                    CheckCustomerCredit(context, callback, customer,customersCode);
                }

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

    public static void CheckCustomerCredit(
            final Context context,
            final Callback callback,
            CustomerModel customer,
            List<String> customersCode){

        ApiNew apiNew =new ApiNew(context);
        Call<List<CheckCustomerCreditModel>> call= apiNew.CheckCustomerCredits(customersCode);

        apiNew.runWebRequest(call, new WebCallBack<List<CheckCustomerCreditModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<CheckCustomerCreditModel> result, Request request) {

                CheckCustomerCreditModelRepository repository =
                        new CheckCustomerCreditModelRepository();
                if (result.size() > 0) {
                    try {
                        repository.deleteAll();
                        repository.insert(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onError("خطا در ذخیره سازی customerSumMoneyAndWeightReport");
                    }
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
