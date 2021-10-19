package com.varanegar.vaslibrary.manager.discountmanager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.framework.validation.Validator;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.UpdateKey;
import com.varanegar.vaslibrary.model.discountSDS.DiscountItemCount;
import com.varanegar.vaslibrary.model.discountSDS.DiscountItemCountModel;
import com.varanegar.vaslibrary.model.discountSDS.DiscountItemCountModelRepository;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.discount.DiscountApi;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Request;
import retrofit2.Call;
import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 12/21/2017.
 */

public class DiscountItemCountManager extends BaseManager<DiscountItemCountModel> {
    private Call<List<DiscountItemCountModel>> call1;

    public DiscountItemCountManager(@NonNull Context context) {
        super(context, new DiscountItemCountModelRepository());
    }

//    public void sync(@NonNull final UpdateCall updateCall) {
//        try {
//            deleteAll();
//            save(updateCall);
//        } catch (DbException e) {
//            Timber.e(e);
//            updateCall.failure(getContext().getString(R.string.error_deleting_old_data));
//        }
//    }

//    public void save(final UpdateCall call) {
//        DiscountApi discountSDSApi = new DiscountApi(getContext());
//        UpdateManager updateManager = new UpdateManager(getContext());
//        Date date = updateManager.getLog(UpdateKey.DiscountItemCount);
//        String dateString = DateHelper.toString(date, DateFormat.MicrosoftDateTime, Locale.US);
//        call1 = discountSDSApi.getDiscountItemCounts(dateString);
//        discountSDSApi.runWebRequest(call1, new WebCallBack<List<DiscountItemCountModel>>() {
//            @Override
//            protected void onFinish() {
//
//            }
//
//            @Override
//            protected void onSuccess(List<DiscountItemCountModel> result, Request request) {
//                if (result != null && result.size() > 0) {
//                    try {
//                        sync(result);
//                        new UpdateManager(getContext()).addLog(UpdateKey.DiscountItemCount);
//                        Timber.i("List of discount item count updated");
//                        call.success();
//                    } catch (ValidationException e) {
//                        Timber.e(e);
//                        call.failure(getContext().getString(R.string.data_validation_failed));
//                    } catch (DbException e) {
//                        Timber.e(e);
//                        call.failure(getContext().getString(R.string.data_error));
//                    }
//                } else {
//                    Timber.i("List of discount item count was empty");
//                    call.success();
//                }
//            }
//
//            @Override
//            protected void onApiFailure(ApiError error, Request request) {
//                String err = WebApiErrorBody.log(error, getContext());
//                call.failure(err);
//            }
//
//            @Override
//            protected void onNetworkFailure(Throwable t, Request request) {
//                Timber.e(t);
//                call.failure(getContext().getString(R.string.network_error));
//            }
//        });
//    }

    public static Query getAllDiscountItems(int disRef) {
        Query query = new Query();
        query.from(DiscountItemCount.DiscountItemCountTbl).whereAnd(Criteria.equals(DiscountItemCount.DisRef, disRef));
        return query;
    }

    public static Query getAllDiscountItems() {
        Query query = new Query();
        query.from(DiscountItemCount.DiscountItemCountTbl);
        return query;
    }

}