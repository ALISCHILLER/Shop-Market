package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.from.From;
import com.varanegar.framework.database.querybuilder.from.JoinFrom;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.dealerPaymentType.DealerPaymentType;
import com.varanegar.vaslibrary.model.dealerPaymentType.DealerPaymentTypeModel;
import com.varanegar.vaslibrary.model.dealerPaymentType.DealerPaymentTypeModelRepository;
import com.varanegar.vaslibrary.model.paymentTypeOrder.PaymentTypeOrder;
import com.varanegar.vaslibrary.model.paymentTypeOrder.PaymentTypeOrderModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.personnel.DealerPaymentTypeAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by g.aliakbar on 13/03/2018.
 */

public class DealerPaymentTypeManager extends BaseManager<DealerPaymentTypeModel> {
    public DealerPaymentTypeManager(@NonNull Context context) {
        super(context, new DealerPaymentTypeModelRepository());
    }

    public void sync(@NonNull final UpdateCall updateCall) {
        String personnelId = UserManager.readFromFile(getContext()).UniqueId.toString();
        DealerPaymentTypeAPI dealerPaymentTypeAPI = new DealerPaymentTypeAPI(getContext());
        dealerPaymentTypeAPI.runWebRequest(dealerPaymentTypeAPI.getDealerPaymentType(personnelId), new WebCallBack<List<DealerPaymentTypeModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<DealerPaymentTypeModel> result, Request request) {
                try {
                    deleteAll();
                    if (result.size() > 0) {
                        try {
                            insert(result);
                            Timber.i("Updating Dealer Payment Type completed");
                            updateCall.success();
                        } catch (ValidationException e) {
                            Timber.e(e);
                            updateCall.failure(getContext().getString(R.string.data_validation_failed));
                        } catch (DbException e) {
                            Timber.e(e);
                            updateCall.failure(getContext().getString(R.string.data_error));
                        }
                    } else {
                        Timber.i("List of Dealer Payment Type was empty");
                        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
                            PaymentOrderTypeManager paymentOrderTypeManager = new PaymentOrderTypeManager(getContext());
                            List<PaymentTypeOrderModel> paymentTypeOrderModels = paymentOrderTypeManager.getAll();
                            result = new ArrayList<>();
                            for (PaymentTypeOrderModel paymentTypeOrderModel :
                                    paymentTypeOrderModels) {
                                DealerPaymentTypeModel dealerPaymentTypeModel = new DealerPaymentTypeModel();
                                dealerPaymentTypeModel.UniqueId = UUID.randomUUID();
                                dealerPaymentTypeModel.PaymentTypeOrderUniqueId = paymentTypeOrderModel.UniqueId;
                                result.add(dealerPaymentTypeModel);
                            }
                            try {
                                insert(result);
                                Timber.i("Updating Dealer Payment Type completed");
                                updateCall.success();
                            } catch (ValidationException e) {
                                Timber.e(e);
                                updateCall.failure(getContext().getString(R.string.data_validation_failed));
                            } catch (DbException e) {
                                Timber.e(e);
                                updateCall.failure(getContext().getString(R.string.data_error));
                            }
                        } else {
                            ///  updateCall.success();
                            updateCall.failure(getContext().getString(R.string.dealerPayment_error));
                        }
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
                Timber.e(t);
                updateCall.failure(getContext().getString(R.string.network_error));
            }
        });
    }

}
