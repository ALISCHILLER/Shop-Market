package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.BaseRepository;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.UpdateKey;
import com.varanegar.vaslibrary.model.customerownertype.CustomerOwnerType;
import com.varanegar.vaslibrary.model.customerownertype.CustomerOwnerTypeModel;
import com.varanegar.vaslibrary.model.customerownertype.CustomerOwnerTypeModelRepository;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.customerownertype.CustomerOwnerTypeApi;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 10/14/2018.
 */

public class CustomerOwnerTypeManager extends BaseManager<CustomerOwnerTypeModel> {
    public CustomerOwnerTypeManager(@NonNull Context context) {
        super(context, new CustomerOwnerTypeModelRepository());
    }

    public void sync(@NonNull final UpdateCall call) {
        UpdateManager updateManager = new UpdateManager(getContext());
        Date date = updateManager.getLog(UpdateKey.CustomerOwnerType);
        String dateString = DateHelper.toString(date, DateFormat.MicrosoftDateTime, Locale.US);
        CustomerOwnerTypeApi api = new CustomerOwnerTypeApi(getContext());
        api.runWebRequest(api.getCustomerOwnerTypes(dateString), new WebCallBack<List<CustomerOwnerTypeModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<CustomerOwnerTypeModel> result, Request request) {
                if (result.size() > 0) {
                    try {
                        sync(result);
                        new UpdateManager(getContext()).addLog(UpdateKey.CustomerOwnerType);
                        Timber.i("Updating customerownertypes completed");
                        call.success();
                    } catch (ValidationException e) {
                        Timber.e(e);
                        call.failure(getContext().getString(R.string.data_validation_failed));
                    } catch (DbException e) {
                        Timber.e(e);
                        call.failure(getContext().getString(R.string.data_error));
                    }
                } else {
                    Timber.i("Updating bank completed. List of city was empty");
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

    public List<CustomerOwnerTypeModel> getAll() {
        return getItems(new Query().from(CustomerOwnerType.CustomerOwnerTypeTbl));
    }
}
