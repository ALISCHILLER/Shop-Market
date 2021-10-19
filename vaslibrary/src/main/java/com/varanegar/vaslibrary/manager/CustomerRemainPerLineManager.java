package com.varanegar.vaslibrary.manager;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.customerremainperline.CustomerRemainPerLine;
import com.varanegar.vaslibrary.model.customerremainperline.CustomerRemainPerLineModel;
import com.varanegar.vaslibrary.model.customerremainperline.CustomerRemainPerLineModelRepository;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.model.tour.TourModel;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.customerremainperLine.CustomerRemainPerLineApi;

import java.util.List;
import java.util.UUID;

import okhttp3.Request;
import retrofit2.Call;
import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 8/20/2018.
 */

public class CustomerRemainPerLineManager extends BaseManager<CustomerRemainPerLineModel> {
    private Call<List<CustomerRemainPerLineModel>> call;

    public CustomerRemainPerLineManager(@NonNull Context context) {
        super(context, new CustomerRemainPerLineModelRepository());
    }

    public void sync(@Nullable final UUID customerId, @NonNull final UpdateCall updateCall) {
        CustomerRemainPerLineApi customerRemainPerLineApi = new CustomerRemainPerLineApi(getContext());
        UserModel userModel = UserManager.readFromFile(getContext());
        if (userModel == null || userModel.UniqueId == null) {
            updateCall.failure(getContext().getString(R.string.user_not_found));
            return;
        }
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        SysConfigModel sysConfigModel = sysConfigManager.read(ConfigKey.SettingsId, SysConfigManager.local);
        if (sysConfigModel == null) {
            updateCall.failure(getContext().getString(R.string.settings_id_not_found));
            return;
        }
        TourModel tourModel = new TourManager(getContext()).loadTour();
        call = customerRemainPerLineApi.getAll(userModel.UniqueId.toString(),
                sysConfigModel.Value,
                customerId == null ? null : customerId.toString(),
                tourModel == null ? 0 : tourModel.TourNo,
                VaranegarApplication.getInstance().getAppId());
        customerRemainPerLineApi.runWebRequest(call, new WebCallBack<List<CustomerRemainPerLineModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<CustomerRemainPerLineModel> result, Request request) {
                try {
                    if (customerId == null)
                        deleteAll();
                    else
                        delete(Criteria.equals(CustomerRemainPerLine.CustomerId, customerId));
                    if (result.size() > 0) {
                        try {
                            sync(result);
                            Timber.i("customer remain per lines updated complete");
                            updateCall.success();
                        } catch (ValidationException e) {
                            Timber.e(e);
                            updateCall.failure(getContext().getString(R.string.data_validation_failed));
                        } catch (DbException e) {
                            Timber.e(e);
                            updateCall.failure(getContext().getString(R.string.data_error));
                        }
                    } else {
                        Timber.i("customer remain per line is empty");
                        updateCall.success();
                    }
                } catch (DbException e) {
                    Timber.e(e);
                    updateCall.failure(getContext().getString(R.string.error_deleting_old_data));
                }
            }

            @Override
            protected void onApiFailure(ApiError e, Request request) {
                String err = WebApiErrorBody.log(e, getContext());
                updateCall.failure(err);
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                Timber.e(t);
                updateCall.failure(getContext().getString(R.string.network_error));
            }

            @Override
            public void onCancel(Request request) {
                super.onCancel(request);
                updateCall.failure(getContext().getString(R.string.request_canceled));
            }
        });

    }

    public CustomerRemainPerLineModel getCustomerRemainPerLine(UUID customerId) {
        Query query = new Query();
        query.from(CustomerRemainPerLine.CustomerRemainPerLineTbl).whereAnd(Criteria.equals(CustomerRemainPerLine.CustomerId, customerId.toString()));
        return getItem(query);
    }

    public void cancelSync() {
        if (call != null && !call.isCanceled() && call.isExecuted())
            call.cancel();
    }
}
