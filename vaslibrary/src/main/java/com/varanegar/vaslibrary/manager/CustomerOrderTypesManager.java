package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.CustomerOrderType.CustomerOrderType;
import com.varanegar.vaslibrary.model.CustomerOrderType.CustomerOrderTypeModel;
import com.varanegar.vaslibrary.model.CustomerOrderType.CustomerOrderTypeModelRepository;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.customer.CustomerOrderTypeApi;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 05/16/2017.
 */

public class CustomerOrderTypesManager extends BaseManager<CustomerOrderTypeModel> {
    public CustomerOrderTypesManager(Context context) {
        super(context, new CustomerOrderTypeModelRepository());
    }

    // اینا برای SAP درسته
    public static final UUID OrderType24 = UUID.fromString("3BCC634A-06F9-4FC8-BFE6-BA385F4933E6");
    public static final UUID OrderType48 = UUID.fromString("C05011BA-2CB3-4560-8632-05DB3FB6D845");

    public void sync(@NonNull final UpdateCall updateCall) {
        try {
            deleteAll();
            save(updateCall);
        } catch (DbException e) {
            Timber.e(e);
            updateCall.failure(getContext().getString(R.string.error_deleting_old_data));
        }
    }

    private void save(final UpdateCall updateCall) {
        CustomerOrderTypeApi customerOrderTypeApi = new CustomerOrderTypeApi(getContext());
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        SysConfigModel sysConfigModel = sysConfigManager.read(ConfigKey.SettingsId, SysConfigManager.local);
        String dateString = DateHelper.toString(UpdateManager.MIN_DATE, DateFormat.MicrosoftDateTime, Locale.US);
        customerOrderTypeApi.runWebRequest(customerOrderTypeApi.getAll(dateString, sysConfigModel.Value, VaranegarApplication.getInstance().getAppId()), new WebCallBack<List<CustomerOrderTypeModel>>() {
            @Override
            protected void onFinish() {
            }

            @Override
            protected void onSuccess(List<CustomerOrderTypeModel> result, Request request) {
                if (result.size() > 0) {
                    try {
                        insert(result);
                        Timber.i("Updating customer ordertype completed");
                        updateCall.success();
                    } catch (ValidationException e) {
                        Timber.e(e);
                        updateCall.failure(getContext().getString(R.string.data_validation_failed));
                    } catch (DbException e) {
                        Timber.e(e);
                        updateCall.failure(getContext().getString(R.string.data_error));
                    }
                } else {
                    Timber.i("Updating customer ordertype completed. List was empty");
                    updateCall.failure(getContext().getResources().getString(R.string.order_types_can_not_be_empty));
                }
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, getContext());
                updateCall.failure(err);
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                Timber.e(t.getMessage());
                updateCall.failure(getContext().getString(R.string.network_error));
            }
        });
    }

    public List<CustomerOrderTypeModel> getItems() {
        Query q = new Query();
        q.from(CustomerOrderType.CustomerOrderTypeTbl);
        return getItems(q);
    }

}
