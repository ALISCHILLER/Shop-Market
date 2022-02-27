package com.varanegar.vaslibrary.manager.c_shipToparty;

import android.content.Context;

import androidx.annotation.NonNull;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.UpdateKey;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.model.tour.TourModel;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.customer.CustomerApi;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import okhttp3.Request;
import retrofit2.Call;
import timber.log.Timber;

public class CustomerShipToPartyManager extends BaseManager<CustomerShipToPartyModel> {

    private CustomerShipToPartyModel  shipToPartyModel;
    private Call<List<CustomerShipToPartyModel>> call;

    public CustomerShipToPartyManager(@NonNull Context context) {
        super(context, new CustomerShipToPartyModelRepository());
    }


    public List<CustomerShipToPartyModel> getItems() {
        Query q = new Query();
        q.from(CustomerShipToParty.CustomerShipToPartyTbl);
        return getItems(q);
    }

    public List<CustomerShipToPartyModel> getItems(UUID customerid) {
        Query q = new Query();
        q.from(CustomerShipToParty.CustomerShipToPartyTbl)
                .whereAnd(Criteria.equals(CustomerShipToParty.CustomerUniqueId, customerid));
        return getItems(q);
    }

    public void sync(@NonNull final UpdateCall updateCall, final boolean isTourUpdateFlow) {
     //   clearCache();
        UpdateManager updateManager = new UpdateManager(getContext());
        Date date = updateManager.getLog(UpdateKey.Customer);
        String dateString = DateHelper.toString(date, DateFormat.MicrosoftDateTime, Locale.US);
        final CustomerApi customerApi = new CustomerApi(getContext());
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        final SysConfigModel settingsId = sysConfigManager.read(ConfigKey.SettingsId, SysConfigManager.local);
        if (settingsId == null) {
            updateCall.failure(getContext().getString(R.string.settings_id_not_found));
            return;
        }
        UserModel userModel = UserManager.readFromFile(getContext());
        if (userModel == null || userModel.UniqueId == null) {
            updateCall.failure(getContext().getString(R.string.user_not_found));
            return;
        }

        final String dealerId = userModel.UniqueId.toString();
        TourModel tourModel = new TourManager(getContext()).loadTour();
        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist))
            call = customerApi.getShipToParty(tourModel.UniqueId.toString());
        else
            call = customerApi.getShipToParty(dateString, dealerId, null, settingsId.Value);
        try {
            deleteAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        customerApi.runWebRequest(call, new WebCallBack<List<CustomerShipToPartyModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<CustomerShipToPartyModel> result, Request request) {
                if (result.size() > 0) {

                    try {
                        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist) && isTourUpdateFlow) {
                            deleteAll();
                            insert(result);
                        } else
                            sync(result);
                        updateCall.success();
                    } catch (ValidationException e) {
                        Timber.e(e);
                        updateCall.failure(getContext().getString(R.string.data_validation_failed));
                    } catch (DbException e) {
                        Timber.e(e);
                        updateCall.failure(getContext().getString(R.string.data_error));
                    }

                }else {
                    updateCall.failure("خطا در اطلاعات موقعیت مشتری");
                }
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

            @Override
            public void onCancel(Request request) {
                super.onCancel(request);
                updateCall.failure(getContext().getString(R.string.request_canceled));
            }
        });

    }

    public void cancelSync() {
        if (call != null && !call.isCanceled() && call.isExecuted())
            call.cancel();
    }

}
