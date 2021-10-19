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
import com.varanegar.vaslibrary.model.discoungood.DiscountGoodModel;
import com.varanegar.vaslibrary.model.discoungood.DiscountGoodModelRepository;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.discount.DiscountGoodApi;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Request;
import retrofit2.Call;
import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 3/3/2019.
 */

public class DiscountGoodManager extends BaseManager<DiscountGoodModel> {
    private Call<List<DiscountGoodModel>> call;

    public DiscountGoodManager(@NonNull Context context) {
        super(context, new DiscountGoodModelRepository());
    }

//    public void sync(@NonNull final UpdateCall updateCall) {
//        UpdateManager updateManager = new UpdateManager(getContext());
//        Date date = updateManager.getLog(UpdateKey.DiscountGood);
//        String dateString = DateHelper.toString(date, DateFormat.MicrosoftDateTime, Locale.US);
//        final DiscountGoodApi discountGoodApi = new DiscountGoodApi(getContext());
//        call = discountGoodApi.getDiscountGood(dateString);
//        discountGoodApi.runWebRequest(call, new WebCallBack<List<DiscountGoodModel>>() {
//            @Override
//            protected void onFinish() {
//
//            }
//
//            @Override
//            protected void onSuccess(List<DiscountGoodModel> result, Request request) {
//                if (result.size() > 0) {
//                    try {
//                        sync(result);
//                        new UpdateManager(getContext()).addLog(UpdateKey.DiscountGood);
//                        Timber.i(result.size() + " rows DiscountGood updated");
//                        updateCall.success();
//                    } catch (ValidationException e) {
//                        Timber.e(e);
//                        updateCall.failure(getContext().getString(R.string.data_validation_failed));
//                    } catch (DbException e) {
//                        Timber.e(e);
//                        updateCall.failure(getContext().getString(R.string.data_error));
//                    }
//                } else {
//                    Timber.i("Updating DiscountGood, DiscountGood list was empty");
//                    updateCall.success();
//                }
//            }
//
//            @Override
//            protected void onApiFailure(ApiError error, Request request) {
//                String err = WebApiErrorBody.log(error, getContext());
//                updateCall.failure(err);
//            }
//
//            @Override
//            protected void onNetworkFailure(Throwable t, Request request) {
//                Timber.e(t.getMessage());
//                updateCall.failure(getContext().getString(R.string.network_error));
//            }
//
//            @Override
//            public void onCancel(Request request) {
//                super.onCancel(request);
//                updateCall.failure(getContext().getString(R.string.tour_canceled));
//            }
//        });
//    }
    public  void  cancel(){
        if (call != null && !call.isCanceled())
            call.cancel();
    }
}
