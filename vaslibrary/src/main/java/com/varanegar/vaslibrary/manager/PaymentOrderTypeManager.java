package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.from.JoinFrom;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.dealerPaymentType.DealerPaymentType;
import com.varanegar.vaslibrary.model.paymentTypeOrder.PaymentTypeOrder;
import com.varanegar.vaslibrary.model.paymentTypeOrder.PaymentTypeOrderModel;
import com.varanegar.vaslibrary.model.paymentTypeOrder.PaymentTypeOrderModelRepository;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.paymentordertype.PaymentOrderTypeApi;

import java.util.List;
import java.util.UUID;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 6/7/2017.
 */

public class PaymentOrderTypeManager extends BaseManager<PaymentTypeOrderModel> {
    public PaymentOrderTypeManager(Context context) {
        super(context, new PaymentTypeOrderModelRepository());
    }

    public void sync(@NonNull final UpdateCall updateCall) {
        PaymentOrderTypeApi paymentOrderTypeApi = new PaymentOrderTypeApi(getContext());
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        SysConfigModel settingsId = sysConfigManager.read(ConfigKey.SettingsId, SysConfigManager.local);
        paymentOrderTypeApi.runWebRequest(paymentOrderTypeApi.getAll(settingsId.Value), new WebCallBack<List<PaymentTypeOrderModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<PaymentTypeOrderModel> result, Request request) {
                if (result.size() > 0)
                    try {
                        deleteAll();
                        insertOrUpdate(result);
                        Timber.i("Updating payment types completed");
                        updateCall.success();
                    } catch (ValidationException e) {
                        Timber.e(e);
                        updateCall.failure(getContext().getString(R.string.data_validation_failed));
                    } catch (DbException e) {
                        Timber.e(e);
                        updateCall.failure(getContext().getString(R.string.data_error));
                    }
                else {
                    Timber.wtf("Payment order type is empty");
                    updateCall.failure(getContext().getString(R.string.payment_order_type_is_empty));
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
    }

    public List<PaymentTypeOrderModel> getPaymentTypes(List<UUID> paymentTypes) {
        Query query1 = new Query().from(PaymentTypeOrder.PaymentTypeOrderTbl)
                .whereAnd(Criteria.in(PaymentTypeOrder.UniqueId, paymentTypes));
        return getItems(query1);
    }

    public List<PaymentTypeOrderModel> getAll() {
        Query query = new Query().from(PaymentTypeOrder.PaymentTypeOrderTbl);
        return getItems(query);
    }

    public PaymentTypeOrderModel getPaymentType(UUID paymentTypeUniqueId) {
        Query query = new Query().from(PaymentTypeOrder.PaymentTypeOrderTbl)
                .whereAnd(Criteria.equals(PaymentTypeOrder.UniqueId, paymentTypeUniqueId));
        return getItem(query);
    }

    public List<PaymentTypeOrderModel> getDealerPaymentTypes() {
        return getItems(new Query().from(JoinFrom.table(PaymentTypeOrder.PaymentTypeOrderTbl).innerJoin(DealerPaymentType.DealerPaymentTypeTbl).on(DealerPaymentType.PaymentTypeOrderUniqueId, PaymentTypeOrder.UniqueId)));
    }

    public PaymentTypeOrderModel get(String backOfficeId) {
        return getItem(new Query().from(PaymentTypeOrder.PaymentTypeOrderTbl).whereAnd(Criteria.equals(PaymentTypeOrder.BackOfficeId, backOfficeId)));
    }
}
