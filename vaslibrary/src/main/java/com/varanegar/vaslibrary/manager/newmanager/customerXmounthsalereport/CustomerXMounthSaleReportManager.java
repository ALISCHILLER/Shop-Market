package com.varanegar.vaslibrary.manager.newmanager.customerXmounthsalereport;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;

import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.from.From;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.validation.ValidationException;
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
import com.varanegar.vaslibrary.manager.newmanager.customerLastBill.CustomerLastBillModel;
import com.varanegar.vaslibrary.manager.newmanager.customerLastBill.CustomerLastBillModelRepository;
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
import timber.log.Timber;

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
        try {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("mldialog", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.commit();
        }catch (Exception e){
            Log.e("err", String.valueOf(e)+"خطا در ذخیره سازی CustomerXMounthSaleReport");
        }


        ApiNew apiNew=new ApiNew(getContext());
        Call<List<CustomerXMounthSaleReportModel>> call = apiNew
                .CustomerXMounthSaleReport(customersCode);

        apiNew.runWebRequest(call, new WebCallBack<List<CustomerXMounthSaleReportModel>>() {
            @Override
            protected void onFinish() {
            }

            @Override
            protected void onSuccess(List<CustomerXMounthSaleReportModel> result, Request request) {
                CustomerXMounthSaleReportManager customerXMounthSaleReportManager=
                        new CustomerXMounthSaleReportManager(getContext());


                CustomerXMounthSaleReportModelRepository repository=
                        new CustomerXMounthSaleReportModelRepository();
                if (result.size() > 0) {
                    try {
                        repository.deleteAll();
                        repository.insert(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Timber.e(e);
                        Log.e("err", String.valueOf(e)+"خطا در ذخیره سازی CustomerXMounthSaleReport");
                        updateCall.failure("خطا در ذخیره سازی CustomerXMounthSaleReport");
                    }
                }
                CustomerGroupSimilarProductsalesReport(getContext(), updateCall, null,customersCode);
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, getContext());
                Log.e("err", String.valueOf(err));
                updateCall.failure(err+" "+"CustomerXMounthSaleReport");

            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                updateCall.failure(getContext().getString(R.string.connection_to_server_failed));
            }
        });
//        DataCustomerContentManager.getCustomerXMounthSaleRepor(getContext(),
//                null,customersCode,
//                new DataCustomerContentManager.Callback() {
//                    @Override
//                    public void onSuccess() {
//                      updateCall.success();
//                    }
//
//                    @Override
//                    public void onError(String error) {
//                        updateCall.error(error);
//                    }
//                });
    }

    public static void CustomerGroupSimilarProductsalesReport(
            final Context context,
            UpdateCall callback,
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
                        Timber.e(e);
                        Log.e("err", String.valueOf(e));
                        callback.error("خطا در ذخیره سازی CustomerGroupSimilarProductsalesReport");
                    }
                }
                CustomerSumMoneyAndWeightReport(context, callback, customer,customersCode);
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, context);
                Log.e("err", String.valueOf(err));
                callback.failure(err+" "+"CustomerGroupSimilarProductsalesReport");

            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                callback.failure(context.getString(R.string.connection_to_server_failed));
            }
        });
    }


    public static void CustomerSumMoneyAndWeightReport(
            final Context context,
            UpdateCall callback,
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
                        Timber.e(e);
                        callback.failure("خطا در ذخیره سازی customerSumMoneyAndWeightReport");
                    }

                    CheckCustomerCredit(context, callback, customer,customersCode);
                }

            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, context);
                Log.e("err", String.valueOf(err));
                callback.failure(err+" "+"CustomerSumMoneyAndWeightReport");

            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                callback.failure(context.getString(R.string.connection_to_server_failed));
            }
        });
    }


    public static void CheckCustomerCredit(
            final Context context,
            UpdateCall callback,
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
                        Timber.e(e);
                        callback.failure("خطا در ذخیره سازی CheckCustomerCredit");
                    }
                }
                CustomerLastBill(context, callback, customer,customersCode);

            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, context);
                Log.e("err", String.valueOf(err));
                callback.failure(err+" "+"CheckCustomerCredit");

            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                callback.failure(context.getString(R.string.connection_to_server_failed));
            }
        });
    }

    public static void CustomerLastBill(
            final Context context,
            UpdateCall callback,
            CustomerModel customer,
            List<String> customersCode){

        ApiNew apiNew =new ApiNew(context);
        Call<List<CustomerLastBillModel>> call= apiNew.CustomerLastBill(customersCode);

        apiNew.runWebRequest(call, new WebCallBack<List<CustomerLastBillModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<CustomerLastBillModel> result, Request request) {

                CustomerLastBillModelRepository repository =
                        new CustomerLastBillModelRepository();
                if (result.size() > 0) {
                    try {
                        repository.deleteAll();
                        repository.insert(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Timber.e(e);
                        callback.failure("خطا در ذخیره سازی CustomerLastBill");
                    }
                }
                callback.success();
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, context);
                Log.e("err", String.valueOf(err));
                callback.failure(err+" "+"CustomerLastBill");

            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                callback.failure(context.getString(R.string.connection_to_server_failed));
            }
        });


    }



}
