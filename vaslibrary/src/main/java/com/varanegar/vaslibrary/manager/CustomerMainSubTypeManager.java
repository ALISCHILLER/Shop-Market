package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.customer.CustomerMainSubTypeModel;
import com.varanegar.vaslibrary.model.customer.CustomerMainSubTypeModelRepository;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.customer.CustomerApi;

import java.util.List;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by A.Torabi on 12/23/2017.
 */

public class CustomerMainSubTypeManager extends BaseManager<CustomerMainSubTypeModel> {
    public CustomerMainSubTypeManager(@NonNull Context context) {
        super(context, new CustomerMainSubTypeModelRepository());
    }

    public void sync(final UpdateCall call) {
        try {
            deleteAll();
            save(call);
        } catch (DbException e) {
            Timber.e(e);
            call.failure(getContext().getString(R.string.error_deleting_old_data));
        }
    }

    private void save(final UpdateCall call) {
        CustomerApi customerApi = new CustomerApi(getContext());
        customerApi.runWebRequest(customerApi.getCustomerMainSubTypes(), new WebCallBack<List<CustomerMainSubTypeModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<CustomerMainSubTypeModel> result, Request request) {
                if (result.size() > 0) {
                    try {
                        insert(result);
                        Timber.i("Customer main subtypes received");
                        call.success();
                    } catch (ValidationException e) {
                        Timber.e(e);
                        call.failure(getContext().getString(R.string.data_validation_failed));
                    } catch (DbException e) {
                        Timber.e(e);
                        call.failure(getContext().getString(R.string.data_error));
                    }
                } else {
                    Timber.i("Customer main subtype list was empty");
                    call.success();
                }
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, getContext());
                call.failure(err);
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                Timber.e(t);
                call.failure(getContext().getString(R.string.network_error));
            }
        });
    }
}
