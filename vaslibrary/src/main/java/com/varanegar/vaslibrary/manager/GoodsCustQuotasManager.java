package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.goodscustquotas.GoodsCustQuotasModel;
import com.varanegar.vaslibrary.model.goodscustquotas.GoodsCustQuotasModelRepository;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.goodscustquotas.GoodsCustQuotasApi;

import java.util.List;

import okhttp3.Request;
import retrofit2.Call;
import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 11/19/2018.
 */

public class GoodsCustQuotasManager extends BaseManager<GoodsCustQuotasModel> {
    private Call<List<GoodsCustQuotasModel>> call;

    public GoodsCustQuotasManager(@NonNull Context context) {
        super(context, new GoodsCustQuotasModelRepository());
    }

    public void sync(@NonNull final UpdateCall updateCall) {
        GoodsCustQuotasApi goodsCustQuotasApi = new GoodsCustQuotasApi(getContext());
        call = goodsCustQuotasApi.getGoodsCustQuotas(UserManager.readFromFile(getContext()).UniqueId.toString());
        goodsCustQuotasApi.runWebRequest(call, new WebCallBack<List<GoodsCustQuotasModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<GoodsCustQuotasModel> result, Request request) {
                try {
                    deleteAll();
                    if (result.size() > 0) {
                        try {
                            insert(result);
                            Timber.i("List of picture subjects updated");
                            updateCall.success();
                        } catch (ValidationException e) {
                            e.printStackTrace();
                            updateCall.failure(getContext().getString(R.string.data_validation_failed));
                        } catch (DbException e) {
                            e.printStackTrace();
                            updateCall.failure(getContext().getString(R.string.data_error));
                        }
                    } else {
                        updateCall.success();
                        Timber.i("Updating goods customer quitas Failed. List of reasons was empty");
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
