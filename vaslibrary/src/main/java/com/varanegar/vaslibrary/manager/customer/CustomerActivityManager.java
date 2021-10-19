package com.varanegar.vaslibrary.manager.customer;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.customer.CustomerActivity;
import com.varanegar.vaslibrary.model.customer.CustomerActivityModel;
import com.varanegar.vaslibrary.model.customer.CustomerActivityModelRepository;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.customer.CustomerApi;

import java.util.List;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by A.Torabi on 1/28/2018.
 */

public class CustomerActivityManager extends BaseManager<CustomerActivityModel> {
    public CustomerActivityManager(@NonNull Context context) {
        super(context, new CustomerActivityModelRepository());
    }

    public void sync(final UpdateCall call) {
        try {
            deleteAll();
            CustomerApi api = new CustomerApi(getContext());
            api.runWebRequest(api.getCustomerActivities(), new WebCallBack<List<CustomerActivityModel>>() {
                @Override
                protected void onFinish() {

                }

                @Override
                protected void onSuccess(List<CustomerActivityModel> result, Request request) {
                    if (result.size() == 0) {
                        Timber.i("List of customer activities was empty");
                        call.success();
                    } else {
                        Timber.i("List of customer activities has " + result.size() + " rows.");
                        try {
                            long affectedRows = insert(result);
                            Timber.i(affectedRows + " rows inserted in to table customer activity");
                            call.success();
                        } catch (ValidationException e) {
                            Timber.e(e);
                            call.failure(getContext().getString(R.string.data_validation_failed));
                        } catch (DbException e) {
                            Timber.e(e);
                            call.failure(getContext().getString(R.string.data_error));
                        }
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
        } catch (DbException e) {
            Timber.e(e);
            getContext().getString(R.string.error_deleting_old_data);
        }

    }

    public List<CustomerActivityModel> getAll() {
        return getItems(new Query().from(CustomerActivity.CustomerActivityTbl));
    }
}
