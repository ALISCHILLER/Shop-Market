package com.varanegar.vaslibrary.manager.dealer;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.dealer.DealerModel;
import com.varanegar.vaslibrary.model.dealer.DealerModelRepository;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.dealer.DealerApi;

import java.util.List;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 9/12/2018.
 */

public class DealerManager extends BaseManager<DealerModel> {
    public DealerManager(@NonNull Context context) {
        super(context, new DealerModelRepository());
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
        DealerApi dealerApi = new DealerApi(getContext());
        dealerApi.runWebRequest(dealerApi.getDealers(), new WebCallBack<List<DealerModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<DealerModel> result, Request request) {
                if (result.size() > 0) {
                    try {
                        insert(result);
                        Timber.i("Updating dealers completed");
                        updateCall.success();
                    } catch (ValidationException e) {
                        Timber.e(e);
                        updateCall.failure(getContext().getString(R.string.data_validation_failed));
                    } catch (DbException e) {
                        Timber.e(e);
                        updateCall.failure(getContext().getString(R.string.data_error));
                    }
                } else {
                    Timber.i("Updating dealers Failed. List of reasons was empty");
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
                Timber.e(t);
                updateCall.failure(getContext().getString(R.string.network_error));
            }
        });
    }

}
