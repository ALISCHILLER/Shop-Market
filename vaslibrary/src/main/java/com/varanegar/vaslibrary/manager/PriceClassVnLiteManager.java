package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.priceclass.PriceClassVnLite;
import com.varanegar.vaslibrary.model.priceclass.PriceClassVnLiteModel;
import com.varanegar.vaslibrary.model.priceclass.PriceClassVnLiteModelRepository;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.price.PriceClassApi;

import java.util.List;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 2/21/2018.
 */

public class PriceClassVnLiteManager extends BaseManager<PriceClassVnLiteModel> {
    public PriceClassVnLiteManager(@NonNull Context context) {
        super(context, new PriceClassVnLiteModelRepository());
    }

    public void sync(@NonNull final UpdateCall updateCall) {
        try {
            deleteAll();
            PriceClassApi priceClassApi = new PriceClassApi(getContext());
            priceClassApi.runWebRequest(priceClassApi.getAll(), new WebCallBack<List<PriceClassVnLiteModel>>() {
                @Override
                protected void onFinish() {

                }

                @Override
                protected void onSuccess(List<PriceClassVnLiteModel> result, Request request) {
                    if (result.size() > 0) {
                        try {
                            insert(result);
                            Timber.i("Updating price class completed");
                            updateCall.success();
                        } catch (ValidationException e) {
                            Timber.e(e);
                            updateCall.failure(getContext().getString(R.string.data_validation_failed));
                        } catch (DbException e) {
                            Timber.e(e);
                            updateCall.failure(getContext().getString(R.string.data_error));
                        }
                    } else {
                        Timber.i("Price class list was empty");
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
        } catch (DbException e) {
            updateCall.failure(getContext().getString(R.string.error_deleting_old_data));
        }
    }

    public List<PriceClassVnLiteModel> getPriceClasses() {
        Query q = new Query();
        q.from(PriceClassVnLite.PriceClassVnLiteTbl);
        return getItems(q);
    }

}
