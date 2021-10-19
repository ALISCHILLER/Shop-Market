package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.BaseRepository;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.UpdateKey;
import com.varanegar.vaslibrary.model.goodsfixunit.GoodsFixUnitModel;
import com.varanegar.vaslibrary.model.goodsfixunit.GoodsFixUnitModelRepository;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.goodsfixunit.GoodsFixUnitApi;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Request;
import retrofit2.Call;
import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 3/4/2019.
 */

public class GoodsFixUnitManager extends BaseManager<GoodsFixUnitModel> {
    private Call<List<GoodsFixUnitModel>> call;

    public GoodsFixUnitManager(@NonNull Context context) {
        super(context, new GoodsFixUnitModelRepository());
    }

    public void sync(@NonNull final UpdateCall updateCall) {
        UpdateManager updateManager = new UpdateManager(getContext());
        Date date = updateManager.getLog(UpdateKey.GoodsFixUnit);
        String dateString = DateHelper.toString(date, DateFormat.MicrosoftDateTime, Locale.US);
        GoodsFixUnitApi goodsFixUnitApi = new GoodsFixUnitApi(getContext());
        call = goodsFixUnitApi.getGoodsFixUnit(dateString);
        goodsFixUnitApi.runWebRequest(call, new WebCallBack<List<GoodsFixUnitModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<GoodsFixUnitModel> result, Request request) {
                if (result.size() > 0) {
                    try {
                        sync(result);
                        new UpdateManager(getContext()).addLog(UpdateKey.GoodsFixUnit);
                        Timber.i(result.size() + " rows GoodsFixUnit updated");
                        updateCall.success();
                    } catch (ValidationException e) {
                        Timber.e(e);
                        updateCall.failure(getContext().getString(R.string.data_validation_failed));
                    } catch (DbException e) {
                        Timber.e(e);
                        updateCall.failure(getContext().getString(R.string.data_error));
                    }
                } else {
                    Timber.i("Updating GoodsFixUnit, GoodsFixUnit list was empty");
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

            @Override
            public void onCancel(Request request) {
                super.onCancel(request);
                updateCall.failure(getContext().getString(R.string.tour_canceled));
            }
        });
    }

    public  void  cancel(){
        if (call != null && !call.isCanceled())
            call.cancel();
    }
}
