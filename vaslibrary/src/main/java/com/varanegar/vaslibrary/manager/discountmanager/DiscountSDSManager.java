package com.varanegar.vaslibrary.manager.discountmanager;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.UpdateKey;
import com.varanegar.vaslibrary.model.discountSDS.DiscountSDS;
import com.varanegar.vaslibrary.model.discountSDS.DiscountSDSModel;
import com.varanegar.vaslibrary.model.discountSDS.DiscountSDSModelRepository;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.discount.DiscountApi;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Request;
import retrofit2.Call;
import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 6/24/2017.
 */

public class DiscountSDSManager extends BaseManager<DiscountSDSModel> {

    private Call<List<DiscountSDSModel>> call;

    public DiscountSDSManager(@NonNull Context context) {
        super(context, new DiscountSDSModelRepository());
    }

//    private void save(final UpdateCall updateCall) {
//        DiscountApi discountSDSApi = new DiscountApi(getContext());
//        UpdateManager updateManager = new UpdateManager(getContext());
//        Date date = updateManager.getLog(UpdateKey.DiscountSDS);
//        String dateString = DateHelper.toString(date, DateFormat.MicrosoftDateTime, Locale.US);
//        call = discountSDSApi.getDiscountSDS(dateString);
//        discountSDSApi.runWebRequest(call, new WebCallBack<List<DiscountSDSModel>>() {
//            @Override
//            protected void onFinish() {
//
//            }
//
//            @Override
//            protected void onSuccess(List<DiscountSDSModel> result, Request request) {
//                if (result == null || result.size() == 0) {
//                    Timber.i("discount sds list was empty");
//                    updateCall.success();
//                } else {
//                    try {
//                        sync(result);
//                        new UpdateManager(getContext()).addLog(UpdateKey.DiscountSDS);
//                        Timber.i("Updating discountSDS completed");
//                        updateCall.success();
//                    } catch (ValidationException e) {
//                        Timber.e(e);
//                        updateCall.failure(getContext().getString(R.string.data_validation_failed));
//                    } catch (DbException e) {
//                        Timber.e(e);
//                        updateCall.failure(getContext().getString(R.string.data_error));
//                    }
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
//                updateCall.failure(getContext().getString(R.string.request_canceled));
//            }
//        });
//    }

//    public void sync(final UpdateCall updateCall) {
//        try {
//            deleteAll();
//            save(updateCall);
//        } catch (DbException e) {
//            Timber.e(e);
//            updateCall.failure(getContext().getString(R.string.error_deleting_discount_sds));
//        }
//    }

    @Nullable
    public String getIdByBackofficeId(int backofficeId) {
        Query query = new Query();
        query.from(DiscountSDS.DiscountSDSTbl)
                .whereAnd(Criteria.equals(DiscountSDS.BackOfficeId, backofficeId))
                .select(DiscountSDS.UniqueId);
        return VaranegarApplication.getInstance().getDbHandler().getStringSingle(query);
    }

    @Nullable
    public int getPrizePackageRef(int disId) {
        Query query = new Query();
        query.from(DiscountSDS.DiscountSDSTbl).select(DiscountSDS.PrizePackageRef);
        return VaranegarApplication.getInstance().getDbHandler().getIntegerSingle(query);
    }

    public void cancelSync() {
        if (call != null && !call.isCanceled() && call.isExecuted())
            call.cancel();
    }

    public void clearAdditionalData() {
        Calendar from = Calendar.getInstance();
        from.add(Calendar.MONTH, -3); // 3 month ago
        String date = DateHelper.toString(from.getTime(), DateFormat.Date, VasHelperMethods.getSysConfigLocale(getContext()));
        try {
            delete(Criteria.lesserThan(DiscountSDS.EndDate, date));
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
