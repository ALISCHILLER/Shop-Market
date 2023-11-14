package com.varanegar.vaslibrary.manager;

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
import com.varanegar.vaslibrary.base.SubsystemType;
import com.varanegar.vaslibrary.base.SubsystemTypeId;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.noSaleReason.NoSaleReason;
import com.varanegar.vaslibrary.model.noSaleReason.NoSaleReasonModel;
import com.varanegar.vaslibrary.model.noSaleReason.NoSaleReasonModelRepository;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.nosalereason.NoSaleReasonApi;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by s.foroughi on 11/03/2017.
 */

public class NoSaleReasonManager extends BaseManager<NoSaleReasonModel> {
    public static final UUID PAYMENT_TYPE_CHANGED_ID = UUID.fromString("f784e273-b069-44e9-baf9-08b5faeaf328");

    public static final class CustomerCall {
        public static final UUID NONE_VISIT_REASON = UUID.fromString("231b61fb-41ff-4409-90be-239ed4c9e838");
        public static final UUID NONE_ORDER_REASON = UUID.fromString("e2a7ab67-5948-4460-a3d7-1b1fee06861e");
        //        public static final UUID RETURN_REASON = UUID.fromString("9B511FB9-6B06-49B7-BEEC-9AFC5C9A516A");
//        public static final UUID NONE_DELIVER_REASON = UUID.fromString("96549FCD-06AB-4EDD-9843-4D1F6D029A1B");
        public static final UUID NONE_DELIVER_REASON = UUID.fromString("87213136-753F-467A-B94B-7CAEEAD2E9A2");
        public static final UUID RETURN_REASON = UUID.fromString("556F9B10-BFC9-4A95-A63F-F430D3271F0A");
    }
//
//    public static final class CustomerCallOrder {
//        public static final UUID NONE_DELIVER_REASON = UUID.fromString("87213136-753F-467A-B94B-7CAEEAD2E9A2");
//        public static final UUID RETURN_REASON = UUID.fromString("556F9B10-BFC9-4A95-A63F-F430D3271F0A");
//
//    }


    public NoSaleReasonManager(Context context) {
        super(context, new NoSaleReasonModelRepository());
    }


    public void sync(@NonNull final UpdateCall updateCall) {
        Date date = UpdateManager.MIN_DATE;
        String dateString = DateHelper.toString(date, DateFormat.MicrosoftDateTime, Locale.US);
        NoSaleReasonApi noSaleReasonApi = new NoSaleReasonApi(getContext());
        noSaleReasonApi.runWebRequest(noSaleReasonApi.getAll(dateString), new WebCallBack<List<NoSaleReasonModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(final List<NoSaleReasonModel> result, Request request) {
                try {
                    deleteAll();
                    if (result.size() > 0) {
                        try {
                            if (VaranegarApplication.is(VaranegarApplication.AppId.Dist))
                                result.add(createPaymentTypeChangedReason());
                            insertOrUpdate(result);
                            Timber.i("Updating no sale reason completed");
                            updateCall.success();
                        } catch (ValidationException e) {
                            Timber.e(e);
                            updateCall.failure(getContext().getString(R.string.data_validation_failed));
                        } catch (DbException ex) {
                            Timber.e(ex);
                            updateCall.failure(getContext().getString(R.string.data_error));
                        }
                    } else {
                        Timber.i("Updating no sale reason, no sale reason list was empty");
                        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
                            result.add(createPaymentTypeChangedReason());
                            try {
                                insertOrUpdate(result);
                            } catch (ValidationException e) {
                                Timber.e(e);
                                updateCall.failure(getContext().getString(R.string.data_validation_failed));
                            }
                        }
                        updateCall.success();
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
                Timber.e(t.getMessage());
                updateCall.failure(getContext().getString(R.string.network_error));
            }
        });
    }

    private NoSaleReasonModel createPaymentTypeChangedReason() {
        NoSaleReasonModel noSaleReasonModel = new NoSaleReasonModel();
        noSaleReasonModel.NoSaleReasonName = getContext().getString(R.string.payment_type_changed);
        noSaleReasonModel.NoSaleReasonTypeId = PAYMENT_TYPE_CHANGED_ID;
        noSaleReasonModel.UniqueId = PAYMENT_TYPE_CHANGED_ID;
        return noSaleReasonModel;
    }

    public static Query getAll() {
        Query query = new Query();
        query.from(NoSaleReason.NoSaleReasonTbl);
        return query;
    }

    public List<NoSaleReasonModel> getNoneVisitReasons() {
        Query query = new Query();
        query.from(NoSaleReason.NoSaleReasonTbl).whereAnd(Criteria.equals(NoSaleReason.NoSaleReasonTypeId, CustomerCall.NONE_VISIT_REASON));
        return getItems(query);
    }

    public List<NoSaleReasonModel> getNonOrderReason() {
        Query query = new Query();
        query.from(NoSaleReason.NoSaleReasonTbl).whereAnd(Criteria.equals(NoSaleReason.NoSaleReasonTypeId, CustomerCall.NONE_ORDER_REASON));
        return getItems(query);
    }

    @SubsystemType(id = SubsystemTypeId.Dist)
    public List<NoSaleReasonModel> getDistReturnReason() {
        Query query = new Query();
        query.from(NoSaleReason.NoSaleReasonTbl).whereAnd(Criteria.equals(NoSaleReason.NoSaleReasonTypeId, CustomerCall.RETURN_REASON));
        return getItems(query);
    }

    @SubsystemType(id = SubsystemTypeId.Dist)
    public List<NoSaleReasonModel> getNonDeliveryReason() {
        Query query = new Query();
        query.from(NoSaleReason.NoSaleReasonTbl).whereAnd(Criteria.equals(NoSaleReason.NoSaleReasonTypeId, CustomerCall.NONE_DELIVER_REASON));
        return getItems(query);
    }

    @SubsystemType(id = SubsystemTypeId.Dist)
    public List<NoSaleReasonModel> getDistOrderReturnReason() {
        Query query = new Query();
        query.from(NoSaleReason.NoSaleReasonTbl).whereAnd(Criteria.equals(NoSaleReason.NoSaleReasonTypeId, CustomerCall.RETURN_REASON));
        return getItems(query);
    }

    @SubsystemType(id = SubsystemTypeId.Dist)
    public List<NoSaleReasonModel> getNonOrderDeliveryReason() {
        Query query = new Query();
        query.from(NoSaleReason.NoSaleReasonTbl).whereAnd(Criteria.equals(NoSaleReason.NoSaleReasonTypeId, CustomerCall.NONE_DELIVER_REASON));
        return getItems(query);
    }


    @SubsystemType(id = SubsystemTypeId.PreSales)
    public NoSaleReasonModel getItem(UUID id){
        Query query = new Query();
        query.from(NoSaleReason.NoSaleReasonTbl).whereAnd(Criteria.equals(NoSaleReason.UniqueId, id));
        return getItem(query);
    }

    @SubsystemType(id = SubsystemTypeId.PreSales)
    public NoSaleReasonModel getItemUniqueId(UUID id){
        Query query = new Query();
        query.from(NoSaleReason.NoSaleReasonTbl).whereAnd(Criteria.equals(NoSaleReason.UniqueId, id));
        return getItem(query);
    }
}
