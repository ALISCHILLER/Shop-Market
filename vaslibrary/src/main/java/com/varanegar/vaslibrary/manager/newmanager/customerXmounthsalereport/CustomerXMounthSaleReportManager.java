package com.varanegar.vaslibrary.manager.newmanager.customerXmounthsalereport;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.BaseRepository;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.from.From;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.processor.annotations.Column;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.newmanager.CustomerSumMoneyAndWeightReport.CustomerSumMoneyAndWeightReportManager;
import com.varanegar.vaslibrary.manager.newmanager.CustomerSumMoneyAndWeightReport.CustomerSumMoneyAndWeightReportModel;
import com.varanegar.vaslibrary.manager.newmanager.CustomerSumMoneyAndWeightReport.CustomerSumMoneyAndWeightReportModelRepository;
import com.varanegar.vaslibrary.manager.newmanager.commodity_rationing.CommodityRationingView;
import com.varanegar.vaslibrary.manager.newmanager.commodity_rationing.CommodityRationingViewModel;
import com.varanegar.vaslibrary.manager.newmanager.customerGroupSimilarProductsalesReport.CustomerGroupSimilarProductsalesReportManager;
import com.varanegar.vaslibrary.manager.newmanager.customerGroupSimilarProductsalesReport.CustomerGroupSimilarProductsalesReportModel;
import com.varanegar.vaslibrary.manager.newmanager.customerGroupSimilarProductsalesReport.CustomerGroupSimilarProductsalesReportModelRepository;
import com.varanegar.vaslibrary.manager.newmanager.dataCustomersContentManager.DataCustomerContentManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.newmodel.checkCustomerCredits.CheckCustomerCreditModel;
import com.varanegar.vaslibrary.model.newmodel.checkCustomerCredits.CheckCustomerCreditModelRepository;
import com.varanegar.vaslibrary.model.product.ProductModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.apiNew.ApiNew;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.Request;
import retrofit2.Call;

public class CustomerXMounthSaleReportManager extends BaseManager<CustomerXMounthSaleReportModel> {

    public CustomerXMounthSaleReportManager(@NonNull Context context) {
        super(context, new CustomerXMounthSaleReportModelRepository());
    }




    public List<CustomerXMounthSaleReportModel> getAll(String customerUniqueId){
        Query query = new Query();
        query.from(From.table(CustomerXMounthSaleReport.CustomerXMounthSaleReportTbl))
                .whereAnd(Criteria.contains(CustomerXMounthSaleReport.customerCode,
                        String.valueOf(customerUniqueId)));
        return getItems(query);
    }
    public void save(UUID customerUniqueId, List<CustomerXMounthSaleReportModel> result){

        try {
            insertOrUpdate(result);
        } catch (ValidationException e) {
            e.printStackTrace();
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
    public void sync( final UpdateCall updateCall) {
        List<CustomerModel> customersCod=new  CustomerManager(getContext()).getAll();
        List<String> customersCode=new ArrayList<>();
        for (CustomerModel customerModel:customersCod) {
            customersCode.add(customerModel.CustomerCode);
        }
        DataCustomerContentManager.getCustomerXMounthSaleRepor(getContext(),
                null,customersCode,
                new DataCustomerContentManager.Callback() {
                    @Override
                    public void onSuccess() {
                      updateCall.success();
                    }

                    @Override
                    public void onError(String error) {
                        updateCall.error(error);
                    }
                });
    }




    public static void CustomerGroupSimilarProductsalesReport(
            final Context context,
            final DataCustomerContentManager.Callback callback,
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
            final DataCustomerContentManager.Callback callback,
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
                CustomerSumMoneyAndWeightReportManager customerSumMoneyAndWeightReportManager=
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
            final DataCustomerContentManager.Callback callback,
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
