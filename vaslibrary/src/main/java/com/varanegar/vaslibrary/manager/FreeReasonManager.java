package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.freeReason.FreeReason;
import com.varanegar.vaslibrary.model.freeReason.FreeReasonModel;
import com.varanegar.vaslibrary.model.freeReason.FreeReasonModelRepository;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.freereason.FreeReasonApi;

import java.util.List;
import java.util.UUID;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by A.Torabi on 7/26/2017.
 */

public class FreeReasonManager extends BaseManager<FreeReasonModel> {
    public FreeReasonManager(@NonNull Context context) {
        super(context, new FreeReasonModelRepository());
    }

    public List<FreeReasonModel> getAll() {
        return getItems(new Query().from(FreeReason.FreeReasonTbl));
    }

    public void sync(final UpdateCall updateCall) {
        try {
            deleteAll();
            save(updateCall);
        } catch (DbException e) {
            Timber.e(e);
            updateCall.failure(getContext().getString(R.string.error_deleting_old_data));
        }
    }

    private void save(final UpdateCall updateCall) {
        FreeReasonApi customerApi = new FreeReasonApi(getContext());
        customerApi.runWebRequest(customerApi.getAll(), new WebCallBack<List<FreeReasonModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            public void onSuccess(List<FreeReasonModel> result, Request request) {
                if (result.size() > 0) {
                    try {
                        insertOrUpdate(result);
                        Timber.i("Updating free reasons completed");
                        updateCall.success();
                    } catch (ValidationException e) {
                        Timber.e(e);
                        updateCall.failure(getContext().getString(R.string.data_validation_failed));
                    } catch (DbException e) {
                        Timber.e(e);
                        updateCall.failure(getContext().getString(R.string.data_error));
                    }
                } else {
                    Timber.i("Updating free reasons Failed. List of reasons was empty");
                    updateCall.success();
                }

            }

            @Override
            public void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, getContext());
                updateCall.failure(err);
            }

            @Override
            public void onNetworkFailure(Throwable t, Request request) {
                Timber.e(t);
                updateCall.failure(getContext().getString(R.string.network_error));
            }
        });

    }

    public Integer getBackOfficeId(UUID uniqueId) {
        Query query = new Query();
        query.from(FreeReason.FreeReasonTbl)
                .whereAnd(Criteria.equals(FreeReason.UniqueId, uniqueId))
                .select(FreeReason.BackOfficeId);
        return VaranegarApplication.getInstance().getDbHandler().getIntegerSingle(query);
    }


    public FreeReasonModel getByBackofficeId(int backofficeId) {
        Query query = new Query();
        query.from(FreeReason.FreeReasonTbl)
                .whereAnd(Criteria.equals(FreeReason.BackOfficeId, backofficeId));
        return getRepository().getItem(query);
    }
}
