package com.varanegar.vaslibrary.manager.dissaleprizepackagesdsmanager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.dissaleprizepackagesds.DisSalePrizePackageSDSModel;
import com.varanegar.vaslibrary.model.dissaleprizepackagesds.DisSalePrizePackageSDSModelRepository;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.discount.DisSalePrizePackageSDSApi;

import java.util.List;

import okhttp3.Request;
import retrofit2.Call;
import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 12/26/2017.
 */

public class DisSalePrizePackageSDSManager extends BaseManager<DisSalePrizePackageSDSModel> {
    private Call<List<DisSalePrizePackageSDSModel>> call;

    public DisSalePrizePackageSDSManager(@NonNull Context context) {
        super(context, new DisSalePrizePackageSDSModelRepository());
    }

    public void sync(final UpdateCall updateCall) {
        DisSalePrizePackageSDSApi disSalePrizePackageSDSApi = new DisSalePrizePackageSDSApi(getContext());
        call = disSalePrizePackageSDSApi.getAll();
        disSalePrizePackageSDSApi.runWebRequest(call, new WebCallBack<List<DisSalePrizePackageSDSModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(final List<DisSalePrizePackageSDSModel> result, Request request) {
                try {
                    deleteAll();
                    if (result != null && result.size() > 0) {
                        try {
                            insert(result);
                            Timber.i("List of dis sale prize package sds manager updated");
                            updateCall.success();
                        } catch (ValidationException e) {
                            Timber.e(e);
                            updateCall.failure(getContext().getString(R.string.data_validation_failed));
                        } catch (DbException e) {
                            Timber.e(e);
                            updateCall.failure(getContext().getString(R.string.data_error));
                        }
                    } else {
                        Timber.i("List of dis sale prize package sds is empty");
                        updateCall.success();
                    }
                } catch (DbException e) {
                    Timber.e(e);
                    updateCall.failure(getContext().getString(R.string.error_deleting_old_data));
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
