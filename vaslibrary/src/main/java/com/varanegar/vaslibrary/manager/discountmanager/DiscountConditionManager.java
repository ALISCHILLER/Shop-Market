package com.varanegar.vaslibrary.manager.discountmanager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.from.From;
import com.varanegar.framework.database.querybuilder.projection.Projection;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.UnknownBackOfficeException;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.UpdateKey;
import com.varanegar.vaslibrary.model.discountSDS.DiscountCondition;
import com.varanegar.vaslibrary.model.discountSDS.DiscountConditionModel;
import com.varanegar.vaslibrary.model.discountSDS.DiscountConditionModelRepository;
import com.varanegar.vaslibrary.model.discountSDS.DiscountItemCount;
import com.varanegar.vaslibrary.model.discountSDS.DiscountSDS;
import com.varanegar.vaslibrary.model.discountSDS.DiscountVnLt;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.discount.DiscountApi;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Request;
import retrofit2.Call;
import timber.log.Timber;
import varanegar.com.vdmclient.call.Discount;

/**
 * Created by A.Jafarzadeh on 12/21/2017.
 */

public class DiscountConditionManager extends BaseManager<DiscountConditionModel> {
    private Call<List<DiscountConditionModel>> call;

    public DiscountConditionManager(@NonNull Context context) {
        super(context, new DiscountConditionModelRepository());
    }

//    private void save(final UpdateCall updateCall) {
//        DiscountApi discountSDSApi = new DiscountApi(getContext());
//        UpdateManager updateManager = new UpdateManager(getContext());
//        Date date = updateManager.getLog(UpdateKey.DiscountCondition);
//        String dateString = DateHelper.toString(date, DateFormat.MicrosoftDateTime, Locale.US);
//        call = discountSDSApi.getDiscountConditions(dateString);
//        discountSDSApi.runWebRequest(call, new WebCallBack<List<DiscountConditionModel>>() {
//            @Override
//            protected void onFinish() {
//
//            }
//
//            @Override
//            protected void onSuccess(List<DiscountConditionModel> result, Request request) {
//                if (result != null && result.size() > 0) {
//                    try {
//                        sync(result);
//                        Timber.i("List of discount condition updated");
//                        updateCall.success();
//                        new UpdateManager(getContext()).addLog(UpdateKey.DiscountCondition);
//                    } catch (ValidationException e) {
//                        Timber.e(e);
//                        updateCall.failure(getContext().getString(R.string.data_validation_failed));
//                    } catch (DbException e) {
//                        Timber.e(e);
//                        updateCall.failure(getContext().getString(R.string.data_error));
//                    }
//                } else {
//                    Timber.i("List of discount condition was empty");
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
//                Timber.e(t);
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
//            updateCall.failure(getContext().getString(R.string.error_deleting_old_data));
//        }
//    }

    public void clearAdditionalData() {
        try {
            VaranegarApplication.getInstance().getDbHandler().execSql("DELETE FROM DiscountCondition WHERE DiscountRef NOT IN (SELECT BackOfficeId FROM DiscountSDS)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelSync() {
        if (call != null && !call.isCanceled() && call.isExecuted())
            call.cancel();
    }

    public boolean existDiscountBaseOnPaymentType(boolean checkInItemCount) {
        Query query = new Query();
        Calendar cal = Calendar.getInstance();
        String date = DateHelper.toString(cal.getTime(), DateFormat.Date, VasHelperMethods.getSysConfigLocale(getContext()));
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        try {
            if (sysConfigManager.getBackOfficeType().equals(BackOfficeType.Varanegar)) {
                query = query.select(DiscountCondition.DiscountConditionAll)
                        .from(From.table(DiscountCondition.DiscountConditionTbl).innerJoin(DiscountSDS.DiscountSDSTbl).on(DiscountSDS.BackOfficeId, DiscountCondition.DiscountRef))
                        .whereAnd(Criteria.lesserThanOrEqual(DiscountSDS.StartDate, date)
                                .and(Criteria.equals(DiscountSDS.EndDate, null).or(Criteria.greaterThanOrEqual(DiscountSDS.EndDate, date)))
                                .and(Criteria.equals(DiscountSDS.IsActive, 1))
                                .and(Criteria.notIsNull(DiscountCondition.PayType).or(Criteria.notIsNull(DiscountCondition.PaymentUsanceRef))));
                if (checkInItemCount)
                    query = query.whereAnd(Criteria.exists(new Query().distinct().select(DiscountItemCount.DisRef).from(DiscountItemCount.DiscountItemCountTbl).whereAnd(Criteria.equalsColumn(DiscountItemCount.DisRef, DiscountCondition.DiscountRef))));
            } else {
                query = query.select(DiscountCondition.DiscountConditionAll)
                        .from(From.table(DiscountCondition.DiscountConditionTbl).innerJoin(DiscountVnLt.DiscountVnLtTbl).on(DiscountVnLt.BackOfficeId, DiscountCondition.DiscountRef))
                        .whereAnd(Criteria.lesserThanOrEqual(DiscountVnLt.StartDate, date)
                                .and(Criteria.equals(DiscountVnLt.EndDate, null).or(Criteria.greaterThanOrEqual(DiscountVnLt.EndDate, date)))
                                .and(Criteria.notIsNull(DiscountCondition.PayType).or(Criteria.notIsNull(DiscountCondition.PaymentUsanceRef))));
                if (checkInItemCount)
                    query = query.whereAnd(Criteria.exists(new Query().distinct().select(DiscountItemCount.DisRef).from(DiscountItemCount.DiscountItemCountTbl).whereAnd(Criteria.equalsColumn(DiscountItemCount.DisRef, DiscountCondition.DiscountRef))));
            }
        } catch (UnknownBackOfficeException e) {
            Timber.e(e);
        }
        return getItems(query).size() > 0;
    }
}
