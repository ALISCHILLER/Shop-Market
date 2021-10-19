package com.varanegar.vaslibrary.manager.goodspackagemanager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.goodsPackage.GoodsPackageModel;
import com.varanegar.vaslibrary.model.goodsPackage.GoodsPackageModelRepository;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.discount.GoodsPackageApi;

import java.util.List;

import okhttp3.Request;
import retrofit2.Call;
import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 12/26/2017.
 */

public class GoodsPackageManager extends BaseManager<GoodsPackageModel> {
    private Call<List<GoodsPackageModel>> call;

    public GoodsPackageManager(@NonNull Context context) {
        super(context, new GoodsPackageModelRepository());
    }

    public void sync(final UpdateCall updateCall) {
        GoodsPackageApi goodsPackageApi = new GoodsPackageApi(getContext());
        call = goodsPackageApi.getAll();
        goodsPackageApi.runWebRequest(call, new WebCallBack<List<GoodsPackageModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<GoodsPackageModel> result, Request request) {
                try {
                    deleteAll();
                    if (result != null && result.size() > 0) {
                        try {
                            insert(result);
                            Timber.i("List of goods package updated");
                            updateCall.success();
                        } catch (ValidationException e) {
                            Timber.e(e);
                            updateCall.failure(getContext().getString(R.string.data_validation_failed));
                        } catch (DbException e) {
                            Timber.e(e);
                            updateCall.failure(getContext().getString(R.string.data_error));
                        }
                    } else {
                        Timber.i("List of goods package is empty");
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
