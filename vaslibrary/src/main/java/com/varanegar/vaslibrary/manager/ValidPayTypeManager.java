package com.varanegar.vaslibrary.manager;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.from.From;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.enums.ThirdPartyPaymentTypes;
import com.varanegar.vaslibrary.manager.paymentmanager.paymenttypes.PaymentType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.UnknownBackOfficeException;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.UpdateKey;
import com.varanegar.vaslibrary.model.call.CustomerCallOrderModel;
import com.varanegar.vaslibrary.model.dealerPaymentType.DealerPaymentType;
import com.varanegar.vaslibrary.model.oldinvoiceheaderview.OldInvoiceHeaderViewModel;
import com.varanegar.vaslibrary.model.paymentTypeOrder.PaymentTypeOrder;
import com.varanegar.vaslibrary.model.paymentTypeOrder.PaymentTypeOrderModel;
import com.varanegar.vaslibrary.model.validpaytype.ValidPayType;
import com.varanegar.vaslibrary.model.validpaytype.ValidPayTypeModel;
import com.varanegar.vaslibrary.model.validpaytype.ValidPayTypeModelRepository;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.validpaytype.ValidPayTypeApi;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by A.Jafarzadeh on 4/22/2018.
 */

public class ValidPayTypeManager extends BaseManager<ValidPayTypeModel> {
    public ValidPayTypeManager(@NonNull Context context) {
        super(context, new ValidPayTypeModelRepository());
    }

    public void sync(final UpdateCall call) {
        UpdateManager updateManager = new UpdateManager(getContext());
        Date date = updateManager.getLog(UpdateKey.ValidPayType);
        String dateString = DateHelper.toString(date, DateFormat.MicrosoftDateTime, Locale.US);
        ValidPayTypeApi validPayTypeApi = new ValidPayTypeApi(getContext());
        validPayTypeApi.runWebRequest(validPayTypeApi.getAll(dateString), new WebCallBack<List<ValidPayTypeModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<ValidPayTypeModel> result, Request request) {
                if (result.size() > 0) {
                    try {
                        insertOrUpdate(result);
                        new UpdateManager(getContext()).addLog(UpdateKey.ValidPayType);
                        Timber.i("Updating validpaytype completed");
                        call.success();
                    } catch (ValidationException e) {
                        Timber.e(e);
                        call.failure(getContext().getString(R.string.data_validation_failed));
                    } catch (DbException e) {
                        Timber.e(e);
                        call.failure(getContext().getString(R.string.data_error));
                    }
                } else {
                    Timber.i("Updating validpaytype completed. List of customers was empty");
                    call.success();
                }
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, getContext());
                call.failure(err);
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                Timber.e(t);
                call.failure(getContext().getString(R.string.network_error));
            }
        });
    }

    public Set<UUID> getValidPayTypes(@NonNull CustomerCallOrderModel customerCallOrderModel) {
        List<CustomerCallOrderModel> customerCallOrderModels = new ArrayList<>();
        customerCallOrderModels.add(customerCallOrderModel);
        return getValidPayTypes(customerCallOrderModels, null, false);
    }

    public Set<UUID> getValidPayTypes(@Nullable List<CustomerCallOrderModel> customerCallOrderModels, @Nullable List<OldInvoiceHeaderViewModel> openInvoices, boolean allowSurplusInvoiceSettlement) {
        BackOfficeType backOfficeType = null;
        Set<UUID> ids = new HashSet<>();
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        try {
            backOfficeType = sysConfigManager.getBackOfficeType();
        } catch (UnknownBackOfficeException e) {
            Timber.e(e);
        }
        if (backOfficeType == BackOfficeType.ThirdParty) {
            @Nullable List<CustomerCallOrderModel> newCustomerCallOrderModels = new ArrayList<>();
            if (customerCallOrderModels != null && customerCallOrderModels.size() > 0)
                newCustomerCallOrderModels.add(customerCallOrderModels.get(0));
            else
                newCustomerCallOrderModels = customerCallOrderModels;
            List<PaymentTypeOrderModel> paymentTypeOrderModels = getPaymentTypeOrders(newCustomerCallOrderModels, null);
            for (PaymentTypeOrderModel paymentTypeOrderModel : paymentTypeOrderModels) {
                if (paymentTypeOrderModel.BackOfficeId.equalsIgnoreCase(ThirdPartyPaymentTypes.PT01.toString())) {
                    ids.add(PaymentType.Cash);
                    ids.add(PaymentType.Card);
                } else if (paymentTypeOrderModel.BackOfficeId.equalsIgnoreCase(ThirdPartyPaymentTypes.PT02.toString())) {
                    ids.add(PaymentType.Cash);
                    ids.add(PaymentType.Card);
                    ids.add(PaymentType.Check);
                    ids.add(PaymentType.Recipt);
                } else if (paymentTypeOrderModel.BackOfficeId.equalsIgnoreCase(ThirdPartyPaymentTypes.PTCH.toString()) || paymentTypeOrderModel.BackOfficeId.equalsIgnoreCase(ThirdPartyPaymentTypes.PTCA.toString())) {
                    ids.add(PaymentType.Cash);
                    ids.add(PaymentType.Card);
                    ids.add(PaymentType.Check);
                }
            }
        } else {
            List<PaymentTypeOrderModel> paymentTypeOrderModels = getPaymentTypeOrders(customerCallOrderModels, openInvoices);
            for (PaymentTypeOrderModel paymentTypeOrderModel : paymentTypeOrderModels) {
                if (paymentTypeOrderModel.AllowReceipt == 1)
                    ids.add(UUID.fromString("da575ef0-da25-46a6-a07b-74625a818071"));
            }
            List<UUID> groupIds = Linq.map(paymentTypeOrderModels, new Linq.Map<PaymentTypeOrderModel, UUID>() {
                @Override
                public UUID run(PaymentTypeOrderModel item) {
                    return item.PaymentTypeOrderGroupUniqueId;
                }
            });

            Query query;
            if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales))
                query = new Query().from(ValidPayType.ValidPayTypeTbl);
            else
                query = new Query().from(ValidPayType.ValidPayTypeTbl)
                        .whereAnd(Criteria.in(ValidPayType.BuyTypeId, groupIds));

            List<ValidPayTypeModel> list = getItems(query);

            for (ValidPayTypeModel validPayTypeModel :
                    list) {
                if (!ids.contains(validPayTypeModel.PayTypeId))
                    ids.add(validPayTypeModel.PayTypeId);
            }
            if (ids.isEmpty() && allowSurplusInvoiceSettlement) {
                Set<UUID> dealerValidPayTypes = getDealerValidPayTypes();
                if (dealerValidPayTypes.contains(PaymentType.Cash))
                    ids.add(PaymentType.Cash);
                if (dealerValidPayTypes.contains(PaymentType.Card))
                    ids.add(PaymentType.Card);
                if (dealerValidPayTypes.contains(PaymentType.Check))
                    ids.add(PaymentType.Check);
            }
        }
        return ids;
    }

    public Set<UUID> getValidPayTypes(@NonNull UUID orderPaymentTypeId) {
        List<UUID> list = new ArrayList<>();
        list.add(orderPaymentTypeId);
        return getValidPayTypes(list);
    }

    public Set<UUID> getValidPayTypes(@NonNull List<UUID> orderPaymentTypeIds) {
        Set<UUID> ids = new HashSet<>();
        List<PaymentTypeOrderModel> paymentTypeOrderModels = new PaymentOrderTypeManager(getContext()).getPaymentTypes(orderPaymentTypeIds);
        for (PaymentTypeOrderModel paymentTypeOrderModel : paymentTypeOrderModels) {
            if (paymentTypeOrderModel.AllowReceipt == 1)
                ids.add(UUID.fromString("da575ef0-da25-46a6-a07b-74625a818071"));
        }
        List<UUID> groupIds = Linq.map(paymentTypeOrderModels, new Linq.Map<PaymentTypeOrderModel, UUID>() {
            @Override
            public UUID run(PaymentTypeOrderModel item) {
                return item.PaymentTypeOrderGroupUniqueId;
            }
        });

        Query query;
        if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales))
            query = new Query().from(ValidPayType.ValidPayTypeTbl);
        else
            query = new Query().from(ValidPayType.ValidPayTypeTbl)
                    .whereAnd(Criteria.in(ValidPayType.BuyTypeId, groupIds));

        List<ValidPayTypeModel> list = getItems(query);
        for (ValidPayTypeModel validPayTypeModel :
                list) {
            if (!ids.contains(validPayTypeModel.PayTypeId))
                ids.add(validPayTypeModel.PayTypeId);
        }

        return ids;
    }

    public List<PaymentTypeOrderModel> getPaymentTypeOrders(@Nullable List<CustomerCallOrderModel> customerCallOrderModels, @Nullable List<OldInvoiceHeaderViewModel> openInvoices) {
        List<UUID> paymentTypes = new ArrayList<>();
        if (customerCallOrderModels != null)
            paymentTypes = Linq.map(customerCallOrderModels, new Linq.Map<CustomerCallOrderModel, UUID>() {
                @Override
                public UUID run(CustomerCallOrderModel item) {
                    return item.OrderPaymentTypeUniqueId;
                }
            });
        if (openInvoices != null) {
            List<UUID> oldPaymentTypes = Linq.map(openInvoices, new Linq.Map<OldInvoiceHeaderViewModel, UUID>() {
                @Override
                public UUID run(OldInvoiceHeaderViewModel item) {
                    return item.PaymentTypeOrderUniqueId;
                }
            });
            paymentTypes.addAll(oldPaymentTypes);
        }
        return new PaymentOrderTypeManager(getContext()).getPaymentTypes(paymentTypes);
    }

    public List<PaymentTypeOrderModel> getPaymentTypeCustomerCallOrders(@Nullable List<CustomerCallOrderModel> customerCallOrderModels) {
        List<UUID> paymentTypes = new ArrayList<>();
        if (customerCallOrderModels != null)
            paymentTypes = Linq.map(customerCallOrderModels, new Linq.Map<CustomerCallOrderModel, UUID>() {
                @Override
                public UUID run(CustomerCallOrderModel item) {
                    return item.OrderPaymentTypeUniqueId;
                }
            });
        return new PaymentOrderTypeManager(getContext()).getPaymentTypes(paymentTypes);
    }

    public Set<UUID> getDealerValidPayTypes() {
        Query query = new Query().select(ValidPayType.ValidPayTypeAll).from(From.table(DealerPaymentType.DealerPaymentTypeTbl)
                .innerJoin(PaymentTypeOrder.PaymentTypeOrderTbl).on(DealerPaymentType.PaymentTypeOrderUniqueId, PaymentTypeOrder.UniqueId)
                .innerJoin(ValidPayType.ValidPayTypeTbl).on(PaymentTypeOrder.PaymentTypeOrderGroupUniqueId, ValidPayType.BuyTypeId))
                .groupBy(ValidPayType.PayTypeId);
        List<ValidPayTypeModel> list = getItems(query);
        Set<UUID> ids = new HashSet<>();
        for (ValidPayTypeModel validPayTypeModel :
                list) {
            ids.add(validPayTypeModel.PayTypeId);
        }
        return ids;
    }
}
