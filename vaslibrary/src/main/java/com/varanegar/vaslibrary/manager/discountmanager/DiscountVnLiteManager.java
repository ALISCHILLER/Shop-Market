package com.varanegar.vaslibrary.manager.discountmanager;

import android.content.Context;
import androidx.annotation.NonNull;

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
import com.varanegar.vaslibrary.model.discountSDS.DiscountVnLt;
import com.varanegar.vaslibrary.model.discountSDS.DiscountVnLtModel;
import com.varanegar.vaslibrary.model.discountSDS.DiscountVnLtModelRepository;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.discount.DiscountApi;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Request;
import timber.log.Timber;

import static com.varanegar.vaslibrary.model.UpdateKey.DiscountVnLite;

/**
 * Created by A.Jafarzadeh on 6/24/2017.
 */

public class DiscountVnLiteManager extends BaseManager<DiscountVnLtModel> {
    public DiscountVnLiteManager(@NonNull Context context) {
        super(context, new DiscountVnLtModelRepository());
    }

    private void save(final UpdateCall updateCall) {
        DiscountApi discountApi = new DiscountApi(getContext());
        UpdateManager updateManager = new UpdateManager(getContext());
        Date date = updateManager.getLog(DiscountVnLite);
        String dateString = DateHelper.toString(date, DateFormat.MicrosoftDateTime, Locale.US);
        discountApi.runWebRequest(discountApi.getDiscountVnLite(dateString), new WebCallBack<List<DiscountVnLtModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<DiscountVnLtModel> result, Request request) {
                if (result == null || result.size() == 0) {
                    Timber.i("discount vnlite list was empty");
                    updateCall.success();
                } else {
                    try {
                        sync(result);
                        new UpdateManager(getContext()).addLog(DiscountVnLite);
                        Timber.i("Updating discountVnLite completed");
                        updateCall.success();
                    } catch (ValidationException e) {
                        Timber.e(e);
                        updateCall.failure(getContext().getString(R.string.data_validation_failed));
                    } catch (DbException e) {
                        Timber.e(e);
                        updateCall.failure(getContext().getString(R.string.data_error));
                    }
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

    public void sync(final UpdateCall updateCall) {
        try {
            deleteAll();
            save(updateCall);
        } catch (DbException e) {
            Timber.e(e);
            updateCall.failure(getContext().getString(R.string.error_deleting_discount_sds));
        }
    }

    public String getIdByBackofficeId(int backofficeId) {
        Query query = new Query();
        query.from(DiscountVnLt.DiscountVnLtTbl)
                .whereAnd(Criteria.equals(DiscountVnLt.PromotionDetailId, backofficeId))
                .select(DiscountVnLt.UniqueId);
        return VaranegarApplication.getInstance().getDbHandler().getStringSingle(query);
    }

    public void clearAdditionalData() {
        Calendar from = Calendar.getInstance();
        from.add(Calendar.MONTH, -3); // 3 month ago
        String date = DateHelper.toString(from.getTime(), DateFormat.Date, VasHelperMethods.getSysConfigLocale(getContext()));
        try {
            delete(Criteria.lesserThan(DiscountVnLt.EndDate, date));
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
