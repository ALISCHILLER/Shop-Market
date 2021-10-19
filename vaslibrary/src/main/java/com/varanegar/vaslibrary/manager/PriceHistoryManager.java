package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.UpdateKey;
import com.varanegar.vaslibrary.model.customerprice.CustomerPriceModel;
import com.varanegar.vaslibrary.model.customerprice.CustomerPriceModelRepository;
import com.varanegar.vaslibrary.model.priceHistory.PriceHistory;
import com.varanegar.vaslibrary.model.priceHistory.PriceHistoryModel;
import com.varanegar.vaslibrary.model.priceHistory.PriceHistoryModelRepository;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.price.PriceHistoryApi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import okhttp3.Request;
import retrofit2.Call;
import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 6/12/2017.
 */

public class PriceHistoryManager extends BaseManager<PriceHistoryModel> {
    private Call<List<PriceHistoryModel>> call;

    public PriceHistoryManager(@NonNull Context context) {
        super(context, new PriceHistoryModelRepository());
    }

    public void sync(@NonNull final UpdateCall updateCall) {
        PriceHistoryApi priceHistoryApi = new PriceHistoryApi(getContext());
        UpdateManager updateManager = new UpdateManager(getContext());
        Date date = updateManager.getLog(UpdateKey.PriceHistory);
        String dateString = DateHelper.toString(date, DateFormat.MicrosoftDateTime, Locale.US);
        call = priceHistoryApi.getAll(dateString);
        priceHistoryApi.runWebRequest(call, new WebCallBack<List<PriceHistoryModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<PriceHistoryModel> result, Request request) {
                if (result.size() > 0) {
                    try {
                        sync(result);
                        new UpdateManager(getContext()).addLog(UpdateKey.PriceHistory);
                        Timber.i("Updating price history completed");
                        updateCall.success();
                    } catch (ValidationException e) {
                        Timber.e(e);
                        updateCall.failure(getContext().getString(R.string.data_validation_failed));
                    } catch (DbException e) {
                        Timber.e(e);
                        updateCall.failure(getContext().getString(R.string.data_error));
                    }
                } else {
                    Timber.i("Price history list was empty");
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
                updateCall.failure(getContext().getString(R.string.request_canceled));
            }
        });
    }

    public void cancelSync() {
        if (call != null && !call.isCanceled() && call.isExecuted())
            call.cancel();
    }

    public List<CustomerPriceModel> getReturnPriceFromPriceHistory(UUID customerUniqueId) {
        SysConfigModel sysConfigModel = new SysConfigManager(getContext()).read(ConfigKey.DcRef, SysConfigManager.cloud);
        Integer dcRef = SysConfigManager.getIntegerValue(sysConfigModel, null);
        List<CustomerPriceModel> customerPriceModels = new ArrayList<>();
        String sql = "SELECT\n" +
                "PriceHistory.UniqueId AS PriceId," +
                "'" + String.valueOf(customerUniqueId) + "' AS CustomerUniqueId, " +
                "Product.UniqueId AS ProductUniqueId, " +
                "PriceHistory.SalePrice AS Price, " +
                "PriceHistory.UserPrice AS UserPrice, " +
                "null as CallOrderId FROM PriceHistory " +
                "JOIN Product ON PriceHistory.GoodsRef = Product.BackOfficeId " +
                "WHERE ((SELECT Value FROM SysConfig WHERE UniqueId = '94E76D64-62B7-430D-B5F9-420695C8DB66') BETWEEN IFNULL(PriceHistory.StartDate, (SELECT Value FROM SysConfig WHERE UniqueId = '94E76D64-62B7-430D-B5F9-420695C8DB66')) AND IFNULL(PriceHistory.EndDate, (SELECT Value FROM SysConfig WHERE UniqueId = '94E76D64-62B7-430D-B5F9-420695C8DB66'))) " +
                "AND ( IFNULL( IFNULL( PriceHistory.DcRef, " + dcRef + "), 1 ) = IFNULL( " + dcRef + ", 1 ) ) " +
                "AND ( IFNULL( IFNULL( PriceHistory.GoodsCtgrRef, CAST ( Product.ProductCtgrRef AS INTEGER ) ), 1 ) = IFNULL( CAST ( Product.ProductCtgrRef AS INTEGER ), 1 ) ) " +
                "GROUP BY PriceHistory.GoodsRef " +
                "HAVING MIN( CASE WHEN PriceHistory.DcRef IS NOT NULL AND PriceHistory.GoodsCtgrRef IS NOT NULL THEN 1 WHEN PriceHistory.DcRef IS NULL AND PriceHistory.GoodsCtgrRef IS NULL THEN 3 ELSE 2 END )";
        customerPriceModels = new CustomerPriceModelRepository().getItems(sql, null);
        return customerPriceModels;
    }

    public void clearAdditionalData() {
        String date = DateHelper.toString(new Date(), DateFormat.Date, VasHelperMethods.getSysConfigLocale(getContext()));
        try {
            delete(Criteria.lesserThan(PriceHistory.EndDate, date));
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

}
