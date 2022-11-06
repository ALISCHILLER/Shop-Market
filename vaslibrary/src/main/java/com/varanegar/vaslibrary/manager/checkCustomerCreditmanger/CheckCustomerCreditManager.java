package com.varanegar.vaslibrary.manager.checkCustomerCreditmanger;

import android.content.Context;

import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.newmodel.checkCustomerCredits.CheckCustomerCreditModel;
import com.varanegar.vaslibrary.model.newmodel.checkCustomerCredits.CheckCustomerCreditModelRepository;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.apiNew.ApiNew;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import retrofit2.Call;
import timber.log.Timber;

public class CheckCustomerCreditManager extends BaseManager<CheckCustomerCreditModel> {
    public CheckCustomerCreditManager(@NonNull Context context) {
        super(context, new CheckCustomerCreditModelRepository());
    }

   private Call<List<CheckCustomerCreditModel>> call;
    public void sync(final UpdateCall updateCall) {
        try {
            List<String> customerCode =new ArrayList<>();
            deleteAll();
            ApiNew apiNew=new ApiNew(getContext());
            CustomerManager customerManager=new CustomerManager(getContext());
            List<CustomerModel> customerModels=customerManager.getAll();
            for (CustomerModel customerModel:
                    customerModels) {
                customerCode.add(customerModel.CustomerCode);
            }
           call=apiNew.CheckCustomerCredits(customerCode);

            apiNew.runWebRequest(call, new WebCallBack<List<CheckCustomerCreditModel>>() {
                @Override
                protected void onFinish() {

                }

                @Override
                protected void onSuccess(List<CheckCustomerCreditModel> result, Request request) {

                    if (result.size()>0){
                        try {
                            deleteAll();
                            insertOrUpdate(result);
                        } catch (ValidationException e) {
                            Timber.e(e);
                            updateCall.failure(getContext().getString(R.string.data_validation_failed));
                        } catch (DbException e) {
                            Timber.e(e);
                            updateCall.failure(getContext().getString(R.string.data_error));
                        }

                    }
                    updateCall.success();
                }

                @Override
                protected void onApiFailure(ApiError error, Request request) {
                    String err = WebApiErrorBody.log(error, getContext());
                    updateCall.failure(err);
                }

                @Override
                protected void onNetworkFailure(Throwable t, Request request) {
                    Timber.e(t);
                    updateCall.failure(getContext().getString(R.string.network_error));
                }
            });
        }catch (DbException e) {
            Timber.e(e);
            updateCall.failure(getContext().getString(R.string.error_deleting_old_data));
        }
    }

    public void cancelSync() {
        if (call != null && !call.isCanceled() && call.isExecuted())
            call.cancel();
    }


}
