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
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.returnReason.ReturnReason;
import com.varanegar.vaslibrary.model.returnReason.ReturnReasonModel;
import com.varanegar.vaslibrary.model.returnReason.ReturnReasonModelRepository;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.returnreason.ReturnReasonApi;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 7/4/2017.
 */

public class ReturnReasonManager extends BaseManager<ReturnReasonModel> {
    public ReturnReasonManager(@NonNull Context context) {
        super(context, new ReturnReasonModelRepository());
    }

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
        Date date = UpdateManager.MIN_DATE;
        String dateString = DateHelper.toString(date, DateFormat.MicrosoftDateTime, Locale.US);
        ReturnReasonApi returnReasonApi = new ReturnReasonApi(getContext());
        returnReasonApi.runWebRequest(returnReasonApi.getAll(dateString, VaranegarApplication.getInstance().getAppId()), new WebCallBack<List<ReturnReasonModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<ReturnReasonModel> result, Request request) {
                if (result.size() > 0) {
                    try {
                        insert(result);
                        Timber.i("Updating returnreason completed");
                        updateCall.success();
                    } catch (ValidationException e) {
                        Timber.e(e);
                        updateCall.failure(getContext().getString(R.string.data_validation_failed));
                    } catch (DbException e) {
                        Timber.e(e);
                        updateCall.failure(getContext().getString(R.string.data_error));
                    }
                } else {
                    Timber.i("Updating return reason. return reason list was empty");
                    updateCall.success();
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

    public List<ReturnReasonModel> getAll() {
        Query query = new Query().from(ReturnReason.ReturnReasonTbl);
        return getItems(query);
    }
}
