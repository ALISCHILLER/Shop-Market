package com.varanegar.vaslibrary.manager.customercallmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.SubsystemType;
import com.varanegar.vaslibrary.base.SubsystemTypeId;
import com.varanegar.vaslibrary.base.SubsystemTypes;
import com.varanegar.vaslibrary.manager.CustomerInventoryManager;
import com.varanegar.vaslibrary.manager.NoSaleReasonManager;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallInvoiceManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallOrderManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallReturnManager;
import com.varanegar.vaslibrary.manager.customercall.OrderInitException;
import com.varanegar.vaslibrary.manager.orderprizemanager.OrderPrizeManager;
import com.varanegar.vaslibrary.manager.paymentmanager.PaymentManager;
import com.varanegar.vaslibrary.manager.picture.PictureCustomerManager;
import com.varanegar.vaslibrary.manager.questionnaire.QuestionnaireAnswerManager;
import com.varanegar.vaslibrary.model.call.CustomerCallInvoiceModel;
import com.varanegar.vaslibrary.model.call.CustomerCallOrderModel;
import com.varanegar.vaslibrary.model.customercall.CustomerCall;
import com.varanegar.vaslibrary.model.customercall.CustomerCallModel;
import com.varanegar.vaslibrary.model.customercall.CustomerCallModelRepository;
import com.varanegar.vaslibrary.model.customercall.CustomerCallType;
import com.varanegar.vaslibrary.model.noSaleReason.NoSaleReasonModel;
import com.varanegar.vaslibrary.model.orderprize.OrderPrize;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Torabi on 9/27/2017.
 */

public class CustomerCallManager extends BaseManager<CustomerCallModel> {
    public CustomerCallManager(@NonNull Context context) {
        super(context, new CustomerCallModelRepository());
    }

    public static final class OrderVisitStatus {
        public static final UUID EnRoute = UUID.fromString("C48CD05D-6510-40C2-823A-5491D1B81B12");
        public static final UUID Delivered = UUID.fromString("7F5D3042-FE1C-47FC-BAC8-9E6F87A47B69");
        public static final UUID PartiallyDelivered = UUID.fromString("39683980-3C5B-4DD0-B4B5-8A2B03BAA608");
        public static final UUID Undelivered = UUID.fromString("25AA0828-E3B8-49A1-BAE5-2B91E383EAD9");
    }

    public static final class CustomerVisitStatus {
        public static final UUID Certain = UUID.fromString("d9d57619-7c52-4e7c-9990-aec98ad80360");
        public static final UUID NotVisited = UUID.fromString("3e15475f-b2df-4b17-9f13-7f9dfd630489");
        public static final UUID NotOrdered = UUID.fromString("e052e752-e095-423e-ac44-48dab42b5c13");
        public static final UUID NotDelivered = UUID.fromString("4e6f11f3-f65e-407e-84f9-83c2520c1235");
        public static final UUID FullyDelivered = UUID.fromString("1fbea13c-be67-47d3-94d4-37b0069efa6f");
        public static final UUID FullyReturned = UUID.fromString("CACA0D2B-D3EB-43DD-9E7B-BBAB23E32FC0");
        public static final UUID PartiallyDelivered = UUID.fromString("39683980-3C5B-4DD0-B4B5-8A2B03BAA608");
    }

    private long insertOrUpdate(CustomerCallType type,
                                UUID customerId,
                                @Nullable String extra1,
                                boolean confirm) throws ValidationException, DbException {
        CustomerCallModel callModel = getItem(new Query().from(CustomerCall.CustomerCallTbl)
                .whereAnd(Criteria.equals(CustomerCall.CallType, type.ordinal()).and(
                        Criteria.equals(CustomerCall.CustomerId, customerId.toString())
                )));
        if (callModel == null) {
            callModel = new CustomerCallModel();
            callModel.UniqueId = UUID.randomUUID();
            callModel.CallType = type;
            callModel.CustomerId = customerId;
            callModel.CreatedTime = new Date();
        }
        callModel.ConfirmStatus = confirm;
        callModel.UpdatedTime = new Date();
        callModel.ExtraField1 = extra1;
        return insertOrUpdate(callModel);
    }

    private long insertOrUpdate(CustomerCallType type,
                                UUID customerId,
                                @Nullable String extra1,
                                @Nullable String extra2,
                                boolean confirm) throws ValidationException, DbException {
        CustomerCallModel callModel = getItem(new Query().from(CustomerCall.CustomerCallTbl)
                .whereAnd(Criteria.equals(CustomerCall.CallType, type.ordinal()).and(
                        Criteria.equals(CustomerCall.CustomerId, customerId.toString())
                )));
        if (callModel == null) {
            callModel = new CustomerCallModel();
            callModel.UniqueId = UUID.randomUUID();
            callModel.CallType = type;
            callModel.CustomerId = customerId;
            callModel.CreatedTime = new Date();
        }
        callModel.ConfirmStatus = confirm;
        callModel.UpdatedTime = new Date();
        callModel.ExtraField1 = extra1;
        callModel.ExtraField2 = extra2;
        return insertOrUpdate(callModel);
    }

    private void insert(CustomerCallType type,
                        UUID customerId,
                        @Nullable String extra1,
                        boolean confirm) throws ValidationException, DbException {
        CustomerCallModel callModel = new CustomerCallModel();
        callModel.UniqueId = UUID.randomUUID();
        callModel.CallType = type;
        callModel.CustomerId = customerId;
        callModel.CreatedTime = new Date();
        callModel.ConfirmStatus = confirm;
        callModel.UpdatedTime = new Date();
        callModel.ExtraField1 = extra1;
        insert(callModel);
    }

    private void insert(CustomerCallType type,
                        UUID customerId,
                        @Nullable String extra1,
                        @Nullable String extra2,
                        boolean confirm) throws ValidationException, DbException {
        CustomerCallModel callModel = new CustomerCallModel();
        callModel.UniqueId = UUID.randomUUID();
        callModel.CallType = type;
        callModel.CustomerId = customerId;
        callModel.CreatedTime = new Date();
        callModel.ConfirmStatus = confirm;
        callModel.UpdatedTime = new Date();
        callModel.ExtraField1 = extra1;
        callModel.ExtraField2 = extra2;
        insert(callModel);
    }

    public long addOrConfirmCall(CustomerCallType type,
                                 UUID customerId,
                                 @Nullable String extra1) throws ValidationException, DbException {
        return insertOrUpdate(type, customerId, extra1, true);
    }

    /**
     * @param type
     * @param customerId
     * @param extra1
     */
    private void addOrResetCall(CustomerCallType type,
                                @NonNull UUID customerId,
                                @Nullable String extra1) throws ValidationException, DbException {
        insertOrUpdate(type, customerId, extra1, false);
    }

    public void unConfirmAllCalls(final UUID customerId) throws ValidationException, DbException {
        List<CustomerCallModel> callModels = loadCalls(customerId);
        if (callModels.size() > 0) {
            for (CustomerCallModel callModel :
                    callModels) {
                callModel.ConfirmStatus = false;
                callModel.UpdatedTime = new Date();
            }
            insertOrUpdate(callModels);
        }
    }


    public void confirmAll(@NonNull UUID customerId) throws ValidationException, DbException {
        final List<CustomerCallModel> calls = loadCalls(customerId);
        for (final CustomerCallModel call :
                calls) {
            if (call.CallType == CustomerCallType.SendData) {
                CustomerCallOrderManager callOrderManager = new CustomerCallOrderManager(getContext());
                List<CustomerCallOrderModel> orders = callOrderManager.getCustomerCallOrders(customerId);
                boolean notAllSent = Linq.exists(orders, order -> !order.IsSent);
                if (!notAllSent) {
                    call.ConfirmStatus = true;
                    call.UpdatedTime = new Date();
                    update(call);
                }
            } else {
                call.ConfirmStatus = true;
                call.UpdatedTime = new Date();
                update(call);
            }
        }
    }

    public long removeCalls(@NonNull UUID customerId, CustomerCallType... types) throws DbException {
        List<Integer> ordinals = new ArrayList<>();
        if (types != null && types.length > 0) {
            for (CustomerCallType customerCallType :
                    types) {
                ordinals.add(customerCallType.ordinal());
            }
        }
        Criteria criteria = null;
        if (ordinals.size() > 0)
            criteria = Criteria.equals(CustomerCall.CustomerId, customerId.toString())
                    .and(Criteria.in(CustomerCall.CallType, ordinals));
        else
            criteria = Criteria.equals(CustomerCall.CustomerId, customerId.toString());
        return delete(criteria);
    }

    public long removeCall(CustomerCallType type, @NonNull UUID customerId) throws DbException {
        return delete(
                Criteria.equals(CustomerCall.CallType, type.ordinal())
                        .and(Criteria.equals(CustomerCall.CustomerId, customerId.toString()))
        );
    }

    @Nullable
    public CustomerCallModel loadCall(CustomerCallType type, @NonNull UUID customerId) {
        List<CustomerCallModel> calls = getItems(new Query().from(CustomerCall.CustomerCallTbl)
                .whereAnd(Criteria.equals(CustomerCall.CallType, type.ordinal()).and(
                        Criteria.equals(CustomerCall.CustomerId, customerId.toString())
                )));
        if (calls.size() > 1)
            throw new RuntimeException("Call type: " + type.name() + ". This type of call could be saved more than once, so you have to use loadCalls()");
        else if (calls.size() == 1)
            return calls.get(0);
        else
            return null;
    }

    public List<CustomerCallModel> loadCalls(@NonNull UUID customerId) {
        return getItems(new Query().from(CustomerCall.CustomerCallTbl)
                .whereAnd(Criteria.equals(CustomerCall.CustomerId, customerId.toString())));
    }

    public List<CustomerCallModel> loadCalls(@NonNull CustomerCallType callType) {
        return getItems(new Query().from(CustomerCall.CustomerCallTbl)
                .whereAnd(Criteria.equals(CustomerCall.CallType, callType.ordinal())));
    }

    // SendData Action
    public long saveSendCall(UUID customerId) throws ValidationException, DbException {
        CustomerCallOrderManager callOrderManager = new CustomerCallOrderManager(getContext());
        List<CustomerCallOrderModel> callOrderModels = callOrderManager.getCustomerCallOrders(customerId);
        for (CustomerCallOrderModel callOrderModel :
                callOrderModels) {
            callOrderModel.IsSent = true;
        }
        if (callOrderModels.size() > 0)
            callOrderManager.update(callOrderModels);
        return addOrConfirmCall(CustomerCallType.SendData, customerId, null);
    }

    public boolean isDataSent(List<CustomerCallModel> calls, @Nullable Boolean confirmed) {
        if (confirmed != null)
            return Linq.exists(calls, item -> item.CallType == CustomerCallType.SendData && item.ConfirmStatus == confirmed);
        else
            return Linq.exists(calls, item -> item.CallType == CustomerCallType.SendData);
    }

    // Confirm action
    public boolean isConfirmed(List<CustomerCallModel> calls) {
        if (calls.size() == 0)
            return false;
        return Linq.all(calls, item -> item.ConfirmStatus);
    }

    public boolean isConfirmed(List<CustomerCallModel> calls, UUID callOrderId) {
        if (calls.size() == 0)
            return false;
        return Linq.exists(calls, item -> item.ConfirmStatus && callOrderId.toString().equalsIgnoreCase(item.ExtraField1));
    }

    // order
    @SubsystemTypes(ids = {SubsystemTypeId.HotSales, SubsystemTypeId.PreSales})
    public boolean isLackOfOrder(List<CustomerCallModel> calls) {
        return Linq.exists(calls, new Linq.Criteria<CustomerCallModel>() {
            @Override
            public boolean run(CustomerCallModel item) {
                return item.CallType == CustomerCallType.LackOfOrder;
            }
        });
    }

    /**
     * NeedImage
     * عکس از دلیل برگشتی
     * چک عکس دلیل برگشتی و عدم ویزئیت
     * @param calls
     * @return
     */
    @SubsystemTypes(ids = {SubsystemTypeId.HotSales, SubsystemTypeId.PreSales,SubsystemTypeId.Dist})
    public boolean isLackOfOrderAndNeedImage(List<CustomerCallModel> calls) {
        NoSaleReasonManager noSaleReasonManager = new NoSaleReasonManager(getContext());
        return Linq.exists(calls, new Linq.Criteria<CustomerCallModel>() {
            @Override
            public boolean run(CustomerCallModel item) {
                if (item.CallType == CustomerCallType.LackOfOrder
                        ||item.CallType == CustomerCallType.CompleteLackOfDelivery
                ) {
                    NoSaleReasonModel noSaleReasonModel = noSaleReasonManager.getItem(UUID.fromString(item.ExtraField1));
                    return (noSaleReasonModel!=null && noSaleReasonModel.NeedImage);
                }
                return false;
            }
        });
    }

    @SubsystemTypes(ids = {SubsystemTypeId.HotSales, SubsystemTypeId.PreSales,SubsystemTypeId.Dist})
    public boolean isLackOfVisitAndNeedImage(List<CustomerCallModel> calls) {
        NoSaleReasonManager noSaleReasonManager = new NoSaleReasonManager(getContext());
        return Linq.exists(calls, new Linq.Criteria<CustomerCallModel>() {
            @Override
            public boolean run(CustomerCallModel item) {
                if (item.CallType == CustomerCallType.LackOfVisit) {
                    NoSaleReasonModel noSaleReasonModel = noSaleReasonManager.getItem(UUID.fromString(item.ExtraField1));
                    return (noSaleReasonModel!=null && noSaleReasonModel.NeedImage);
                }
                return false;
            }
        });
    }

    @SubsystemTypes(ids = {SubsystemTypeId.HotSales, SubsystemTypeId.PreSales,SubsystemTypeId.Dist})
    public boolean isNeedImage(List<CustomerCallModel> calls) {
        NoSaleReasonManager noSaleReasonManager = new NoSaleReasonManager(getContext());
        return Linq.exists(calls, new Linq.Criteria<CustomerCallModel>() {
            @Override
            public boolean run(CustomerCallModel item) {
                if (item.CallType == CustomerCallType.LackOfOrder ||
                        item.CallType == CustomerCallType.LackOfVisit
                        ||item.CallType == CustomerCallType.CompleteLackOfDelivery
                ) {
                    NoSaleReasonModel noSaleReasonModel = noSaleReasonManager.getItem(UUID.fromString(item.ExtraField1));
                    return (noSaleReasonModel!=null && noSaleReasonModel.NeedImage);
                }
                return false;
            }
        });
    }


    public boolean isLackOfVisit(List<CustomerCallModel> calls) {
        return Linq.exists(calls, new Linq.Criteria<CustomerCallModel>() {
            @Override
            public boolean run(CustomerCallModel item) {
                return item.CallType == CustomerCallType.LackOfVisit;
            }
        });
    }

    public boolean isCompleteLackOfDelivery(List<CustomerCallModel> calls) {
        return Linq.exists(calls, new Linq.Criteria<CustomerCallModel>() {
            @Override
            public boolean run(CustomerCallModel item) {
                return item.CallType == CustomerCallType.CompleteLackOfDelivery;
            }
        });
    }

    public boolean isCompleteReturnDelivery(List<CustomerCallModel> calls) {
        return Linq.exists(calls, new Linq.Criteria<CustomerCallModel>() {
            @Override
            public boolean run(CustomerCallModel item) {
                return item.CallType == CustomerCallType.CompleteReturnDelivery;
            }
        });
    }

    /**
     * آیا سفارش سفارش فروش یا برگشتی دارد.
     * check db
     *
     */
    @SubsystemTypes(ids = {SubsystemTypeId.PreSales, SubsystemTypeId.HotSales})
    public boolean hasOrderOrReturnCall(List<CustomerCallModel> calls) {
        return Linq.exists(calls, new Linq.Criteria<CustomerCallModel>() {
            @Override
            public boolean run(CustomerCallModel item) {
                return (item.CallType == CustomerCallType.SaveOrderRequest ||
                        item.CallType == CustomerCallType.SaveReturnRequestWithoutRef ||
                        item.CallType == CustomerCallType.SaveReturnRequestWithRef);
            }
        });
    }

    /**
     * آیا سفارش تحویل کامل یا تحویل قسمتی یا برگشت با یا بدون مبنا دارد.
     * check db
     *
     */
    @SubsystemType(id = SubsystemTypeId.Dist)
    public boolean hasDeliveryOrReturnCall(List<CustomerCallModel> calls) {
        return Linq.exists(calls, new Linq.Criteria<CustomerCallModel>() {
            @Override
            public boolean run(CustomerCallModel item) {
                return (item.CallType == CustomerCallType.OrderDelivered ||
                        item.CallType == CustomerCallType.OrderPartiallyDelivered ||
                        item.CallType == CustomerCallType.SaveReturnRequestWithoutRef ||
                        item.CallType == CustomerCallType.SaveReturnRequestWithRef);
            }
        });
    }

    @SubsystemTypes(ids = {SubsystemTypeId.PreSales, SubsystemTypeId.HotSales})
    public boolean hasOrderAndReturnCall(List<CustomerCallModel> calls) {
        boolean orderCall = hasOrderCall(calls, null);
        boolean returnCall = hasReturnCall(calls, null, false);
        return returnCall && orderCall;
    }

    @SubsystemTypes(ids = {SubsystemTypeId.PreSales, SubsystemTypeId.HotSales})
    public boolean hasOrderCall(List<CustomerCallModel> calls, @Nullable final UUID callOrderId) {
        return Linq.exists(calls, new Linq.Criteria<CustomerCallModel>() {
            @Override
            public boolean run(CustomerCallModel item) {
                if (callOrderId == null)
                    return (item.CallType == CustomerCallType.SaveOrderRequest);
                else
                    return (item.CallType == CustomerCallType.SaveOrderRequest && callOrderId.toString().equals(item.ExtraField1));
            }
        });
    }

    @SubsystemType(id = SubsystemTypeId.Dist)
    public boolean hasDeliveryCall(List<CustomerCallModel> calls, @Nullable final UUID callOrderId, @Nullable final Boolean partial) {
        return Linq.exists(calls, new Linq.Criteria<CustomerCallModel>() {
            @Override
            public boolean run(CustomerCallModel item) {
                if (callOrderId == null) {
                    if (partial == null)
                        return (item.CallType == CustomerCallType.OrderPartiallyDelivered || item.CallType == CustomerCallType.OrderDelivered);
                    else if (partial)
                        return (item.CallType == CustomerCallType.OrderPartiallyDelivered);
                    else
                        return (item.CallType == CustomerCallType.OrderDelivered);
                } else {
                    if (partial == null)
                        return (item.CallType == CustomerCallType.OrderPartiallyDelivered || item.CallType == CustomerCallType.OrderDelivered) && (callOrderId.toString().equals(item.ExtraField1));
                    else if (partial)
                        return (item.CallType == CustomerCallType.OrderPartiallyDelivered && callOrderId.toString().equals(item.ExtraField1));
                    else
                        return (item.CallType == CustomerCallType.OrderDelivered && callOrderId.toString().equals(item.ExtraField1));
                }
            }
        });
    }

    @SubsystemType(id = SubsystemTypeId.Dist)
    public boolean hasDistCall(List<CustomerCallModel> calls) {
        return Linq.exists(calls, new Linq.Criteria<CustomerCallModel>() {
            @Override
            public boolean run(CustomerCallModel item) {
                return (item.CallType == CustomerCallType.OrderPartiallyDelivered || item.CallType == CustomerCallType.OrderDelivered || item.CallType == CustomerCallType.OrderLackOfDelivery || item.CallType == CustomerCallType.OrderReturn);
            }
        });
    }


    @SubsystemType(id = SubsystemTypeId.Dist)
    public boolean hasDistCall(List<CustomerCallModel> calls, @NonNull final UUID callOrderId) {
        return Linq.exists(calls, new Linq.Criteria<CustomerCallModel>() {
            @Override
            public boolean run(CustomerCallModel item) {
                return callOrderId.toString().equals(item.ExtraField1) && (item.CallType == CustomerCallType.OrderPartiallyDelivered || item.CallType == CustomerCallType.OrderDelivered || item.CallType == CustomerCallType.OrderLackOfDelivery || item.CallType == CustomerCallType.OrderReturn);
            }
        });
    }

    @SubsystemType(id = SubsystemTypeId.Dist)
    public boolean hasDistCall(List<CustomerCallModel> calls, @Nullable final UUID callOrderId, final CustomerCallType callType) {
        return Linq.exists(calls, new Linq.Criteria<CustomerCallModel>() {
            @Override
            public boolean run(CustomerCallModel item) {
                if (callOrderId == null)
                    return item.CallType == callType;
                else
                    return callOrderId.toString().equals(item.ExtraField1) && (item.CallType == callType);
            }
        });
    }

    public boolean hasReturnCall(@NonNull List<CustomerCallModel> calls, final Boolean withRef, @Nullable final Boolean isFromRequest) {
        return Linq.exists(calls, new Linq.Criteria<CustomerCallModel>() {
            @Override
            public boolean run(CustomerCallModel item) {
                if (withRef == null) {
                    if (isFromRequest == null)
                        return (item.CallType == CustomerCallType.SaveReturnRequestWithoutRef || item.CallType == CustomerCallType.SaveReturnRequestWithRef);
                    else if (isFromRequest)
                        return ("isFromRequest".equals(item.ExtraField1) && (item.CallType == CustomerCallType.SaveReturnRequestWithoutRef || item.CallType == CustomerCallType.SaveReturnRequestWithRef));
                    else
                        return (item.ExtraField1 == null && (item.CallType == CustomerCallType.SaveReturnRequestWithoutRef || item.CallType == CustomerCallType.SaveReturnRequestWithRef));
                } else {
                    CustomerCallType callType = withRef ? CustomerCallType.SaveReturnRequestWithRef : CustomerCallType.SaveReturnRequestWithoutRef;
                    if (isFromRequest == null)
                        return (item.CallType == callType);
                    else if (isFromRequest)
                        return (item.CallType == callType && "isFromRequest".equals(item.ExtraField1));
                    else
                        return (item.CallType == callType && item.ExtraField1 == null);
                }
            }
        });
    }

    @SubsystemTypes(ids = {SubsystemTypeId.PreSales, SubsystemTypeId.HotSales})
    private long removeLackOfVisitAndLackOfOrder(UUID customerId) throws DbException {
        long i = removeCalls(customerId, CustomerCallType.LackOfOrder, CustomerCallType.LackOfVisit);
        PictureCustomerManager pictureCustomerManager = new PictureCustomerManager(getContext());
        pictureCustomerManager.deleteLackOfOrderPictures(customerId);
        return i;
    }

    @SubsystemTypes(ids = {SubsystemTypeId.PreSales, SubsystemTypeId.HotSales})
    public void saveLackOfOrder(final UUID customerId, final UUID noSaleReasonId) throws ValidationException, DbException {
        removeOrders(customerId);
        removeCalls(customerId,
                CustomerCallType.LackOfVisit,
                CustomerCallType.SaveOrderRequest);
        addOrResetCall(CustomerCallType.LackOfOrder, customerId, noSaleReasonId.toString());
        SharedPreferences customerSharedPreferences = getContext().getSharedPreferences("customerStatusShared", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = customerSharedPreferences.edit();
        editor.putBoolean(customerId.toString(), true);
        editor.apply();
    }

    public void saveLackOfVisit(final UUID customerId, final UUID noSaleReasonId) throws DbException, ValidationException {
        removeAllCalls(customerId);
        delete(Criteria.equals(CustomerCall.CustomerId, customerId.toString())
                .and(Criteria.notEquals(CustomerCall.CallType, CustomerCallType.ChangeLocation.ordinal())));
        addOrResetCall(CustomerCallType.LackOfVisit, customerId, noSaleReasonId.toString());
        PaymentManager paymentManager = new PaymentManager(getContext());
        paymentManager.deleteAllPayments(customerId);
        PictureCustomerManager pictureCustomerManager = new PictureCustomerManager(getContext());
        pictureCustomerManager.deleteLackOfOrderPictures(customerId);
    }

    @SubsystemType(id = SubsystemTypeId.Dist)
    public void saveCompleteLackOfDelivery(@NonNull UUID customerId, @NonNull List<UUID> invoiceIds, @NonNull UUID lackOfDeliveryReasonId) throws DbException, ValidationException, OrderInitException {
        removeOrders(customerId);
        removeCalls(customerId,
                CustomerCallType.LackOfVisit,
                CustomerCallType.CompleteReturnDelivery,
                CustomerCallType.OrderReturn,
                CustomerCallType.OrderLackOfDelivery,
                CustomerCallType.OrderDelivered,
                CustomerCallType.OrderPartiallyDelivered);
        addOrResetCall(CustomerCallType.CompleteLackOfDelivery, customerId, lackOfDeliveryReasonId.toString());
        if (invoiceIds.size() > 0) {
            CustomerCallOrderManager callOrderManager = new CustomerCallOrderManager(getContext());
            for (UUID invoiceId :
                    invoiceIds) {
                callOrderManager.initCall(invoiceId, true);
                CustomerCallModel callModel = getItem(new Query().from(CustomerCall.CustomerCallTbl)
                        .whereAnd(Criteria.equals(CustomerCall.CustomerId, customerId.toString()))
                        .whereAnd(Criteria.equals(CustomerCall.ExtraField1, invoiceId.toString())));
                if (callModel == null)
                    insert(CustomerCallType.OrderLackOfDelivery, customerId, invoiceId.toString(), lackOfDeliveryReasonId.toString(), false);
                else {
                    callModel.CallType = CustomerCallType.OrderLackOfDelivery;
                    callModel.ExtraField2 = lackOfDeliveryReasonId.toString();
                    callModel.ConfirmStatus = false;
                    callModel.UpdatedTime = new Date();
                    update(callModel);
                }
            }
        }
    }

    @SubsystemType(id = SubsystemTypeId.Dist)
    public void saveCompleteReturn(@NonNull UUID customerId, @NonNull List<UUID> invoiceIds, @NonNull UUID returnReasonId) throws DbException, ValidationException, OrderInitException {
        removeOrders(customerId);
        removeCalls(customerId,
                CustomerCallType.LackOfVisit,
                CustomerCallType.CompleteLackOfDelivery,
                CustomerCallType.OrderReturn,
                CustomerCallType.OrderLackOfDelivery,
                CustomerCallType.OrderDelivered,
                CustomerCallType.OrderPartiallyDelivered);
        addOrResetCall(CustomerCallType.CompleteReturnDelivery, customerId, returnReasonId.toString());
        if (invoiceIds.size() > 0) {
            CustomerCallOrderManager callOrderManager = new CustomerCallOrderManager(getContext());
            for (UUID invoiceId :
                    invoiceIds) {
                callOrderManager.initCall(invoiceId, true);
                CustomerCallModel callModel = getItem(new Query().from(CustomerCall.CustomerCallTbl)
                        .whereAnd(Criteria.equals(CustomerCall.CustomerId, customerId.toString()))
                        .whereAnd(Criteria.equals(CustomerCall.ExtraField1, invoiceId.toString())));
                if (callModel == null)
                    insert(CustomerCallType.OrderReturn, customerId, invoiceId.toString(), returnReasonId.toString(), false);
                else {
                    callModel.CallType = CustomerCallType.OrderReturn;
                    callModel.ExtraField2 = returnReasonId.toString();
                    callModel.ConfirmStatus = false;
                    callModel.UpdatedTime = new Date();
                    update(callModel);
                }
            }
        }
    }

    @SubsystemType(id = SubsystemTypeId.Dist)
    public boolean hasConfirmedAllInvoices(List<CustomerCallModel> calls, UUID customerId) {
        CustomerCallManager callManager = new CustomerCallManager(getContext());
        CustomerCallInvoiceManager manager = new CustomerCallInvoiceManager(getContext());
        List<CustomerCallInvoiceModel> customerDistInvoices = manager.getCustomerCallInvoices(customerId);
        for (CustomerCallInvoiceModel invoice :
                customerDistInvoices) {
            if (!callManager.hasDistCall(calls, invoice.UniqueId))
                return false;
        }
        return true;
    }

    @SubsystemType(id = SubsystemTypeId.Dist)
    private boolean hasConfirmedAllInvoices(List<CustomerCallInvoiceModel> customerDistInvoices, List<CustomerCallModel> calls) {
        CustomerCallManager callManager = new CustomerCallManager(getContext());
        for (CustomerCallInvoiceModel invoice :
                customerDistInvoices) {
            if (!callManager.hasDistCall(calls, invoice.UniqueId))
                return false;
        }
        return true;
    }

    @SubsystemType(id = SubsystemTypeId.Dist)
    public String checkCustomerDistributionStatus(List<CustomerCallInvoiceModel> invoices, List<CustomerCallModel> calls) {
        boolean isCompleteLackOfDelivery = isCompleteLackOfDelivery(calls);
        boolean isCompleteReturnDelivery = isCompleteReturnDelivery(calls);
        boolean isLackOfVisit = isLackOfVisit(calls);
        boolean hasConfirmedAllInvoices = hasConfirmedAllInvoices(invoices, calls);
        if (isCompleteLackOfDelivery || isCompleteReturnDelivery || isLackOfVisit || hasConfirmedAllInvoices)
            return null;
        else
            return getContext().getString(R.string.customer_has_undeciced_order);

    }

    /***
     * Removes Customer Order, Returns, Pictures, Surveys, Customer Stock Level and all calls
     * @param customerId
     */
    public void removeAllCalls(final UUID customerId) throws DbException, ValidationException {
        CustomerCallOrderManager callOrderManager = new CustomerCallOrderManager(getContext());
        callOrderManager.cancelCustomerOrders(customerId);
        CustomerCallReturnManager callReturnManager = new CustomerCallReturnManager(getContext());
        callReturnManager.cancelAllReturns(customerId, null);
        PictureCustomerManager pictureCustomerManager = new PictureCustomerManager(getContext());
        pictureCustomerManager.deletePicturesAndFiles(customerId);
        QuestionnaireAnswerManager questionnaireAnswerManager = new QuestionnaireAnswerManager(getContext());
        questionnaireAnswerManager.deleteAnswers(customerId);
        CustomerInventoryManager inventoryManager = new CustomerInventoryManager(getContext());
        inventoryManager.deleteLines(customerId);
        OrderPrizeManager orderPrizeManager = new OrderPrizeManager(getContext());
        orderPrizeManager.delete(Criteria.equals(OrderPrize.CustomerId, customerId));
        CustomerManager customerManager = new CustomerManager(getContext());
        customerManager.resetEditedCustomer(customerId);
    }

    /***
     * Removes customer order and returns.
     * @param customerId
     */
    public void removeOrders(final UUID customerId) throws DbException, ValidationException {
        CustomerCallOrderManager callOrderManager = new CustomerCallOrderManager(getContext());
        callOrderManager.cancelCustomerOrders(customerId);
    }

    public void removeCallOrder(@NonNull final UUID customerId, @Nullable UUID callOrderId) throws DbException, ValidationException {
        Criteria criteria = null;
        if (callOrderId != null)
            criteria = Criteria.equals(CustomerCall.CustomerId, customerId.toString())
                    .and(Criteria.equals(CustomerCall.ExtraField1, callOrderId));
        else
            criteria = Criteria.equals(CustomerCall.CustomerId, customerId.toString());
        delete(criteria);
        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist))
            removeCalls(customerId, CustomerCallType.CompleteReturnDelivery, CustomerCallType.CompleteLackOfDelivery);
        unConfirmAllCalls(customerId);
    }

    @SubsystemTypes(ids = {SubsystemTypeId.PreSales, SubsystemTypeId.HotSales})
    public void saveOrderCall(@NonNull final UUID customerId, @NonNull UUID callOrderId) throws ValidationException, DbException {
        insert(CustomerCallType.SaveOrderRequest, customerId, callOrderId.toString(), false);
        removeLackOfVisitAndLackOfOrder(customerId);
        SharedPreferences customerSharedPreferences = getContext().getSharedPreferences("customerStatusShared", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = customerSharedPreferences.edit();
        editor.putBoolean(customerId.toString(), true);
        editor.apply();
    }

    @SubsystemType(id = SubsystemTypeId.Dist)
    public void saveDistDeliveryCall(@NonNull final UUID customerId, @NonNull UUID callOrderId) throws ValidationException, DbException {
        CustomerCallModel callModel = getItem(new Query().from(CustomerCall.CustomerCallTbl)
                .whereAnd(Criteria.equals(CustomerCall.CustomerId, customerId.toString()))
                .whereAnd(Criteria.equals(CustomerCall.ExtraField1, callOrderId.toString())));
        if (callModel == null)
            insert(CustomerCallType.OrderDelivered, customerId, callOrderId.toString(), false);
        else {
            callModel.CallType = CustomerCallType.OrderDelivered;
            callModel.ConfirmStatus = false;
            callModel.UpdatedTime = new Date();
            update(callModel);
        }
        removeCalls(customerId, CustomerCallType.CompleteReturnDelivery, CustomerCallType.CompleteLackOfDelivery, CustomerCallType.LackOfVisit);
    }

    @SubsystemType(id = SubsystemTypeId.Dist)
    public void saveDistPartialDeliveryCall(@NonNull final UUID customerId, @NonNull UUID callOrderId, @NonNull UUID reasonUniqueId) throws ValidationException, DbException {
        CustomerCallModel callModel = getItem(new Query().from(CustomerCall.CustomerCallTbl)
                .whereAnd(Criteria.equals(CustomerCall.CustomerId, customerId.toString()))
                .whereAnd(Criteria.equals(CustomerCall.ExtraField1, callOrderId.toString())));
        if (callModel == null)
            insert(CustomerCallType.OrderPartiallyDelivered, customerId, callOrderId.toString(), reasonUniqueId.toString(), false);
        else {
            callModel.CallType = CustomerCallType.OrderPartiallyDelivered;
            callModel.ExtraField2 = reasonUniqueId.toString();
            callModel.ConfirmStatus = false;
            callModel.UpdatedTime = new Date();
            update(callModel);
        }
        removeCalls(customerId, CustomerCallType.CompleteReturnDelivery, CustomerCallType.CompleteLackOfDelivery, CustomerCallType.LackOfVisit);
        /**
         * save location
         * ذخیره لوکیشن برای تحویل سفارش
         */
        SharedPreferences customerSharedPreferences = getContext().getSharedPreferences("customerStatusShared", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = customerSharedPreferences.edit();
        editor.putBoolean(customerId.toString(), true);
        editor.apply();
    }

    @SubsystemType(id = SubsystemTypeId.Dist)
    public void saveDistReturnCall(UUID customerId, UUID callOrderId, UUID reasonUniqueId) throws DbException, ValidationException, OrderInitException {
        CustomerCallOrderManager callOrderManager = new CustomerCallOrderManager(getContext());
        callOrderManager.cancelCustomerOrder(customerId, callOrderId);
        removeCalls(customerId, CustomerCallType.CompleteLackOfDelivery, CustomerCallType.CompleteReturnDelivery);
        callOrderManager.initCall(callOrderId, true);
        CustomerCallModel callModel = getItem(new Query().from(CustomerCall.CustomerCallTbl)
                .whereAnd(Criteria.equals(CustomerCall.CustomerId, customerId.toString()))
                .whereAnd(Criteria.equals(CustomerCall.ExtraField1, callOrderId.toString())));
        if (callModel == null)
            insert(CustomerCallType.OrderReturn, customerId, callOrderId.toString(), reasonUniqueId.toString(), false);
        else {
            callModel.CallType = CustomerCallType.OrderReturn;
            callModel.ExtraField2 = reasonUniqueId.toString();
            callModel.ConfirmStatus = false;
            callModel.UpdatedTime = new Date();
            update(callModel);
        }
        removeCalls(customerId, CustomerCallType.CompleteReturnDelivery, CustomerCallType.CompleteLackOfDelivery, CustomerCallType.LackOfVisit);
    }

    @SubsystemType(id = SubsystemTypeId.Dist)
    public void saveDistLackOfDeliveryCall(UUID customerId, UUID callOrderId, UUID reasonId) throws DbException, ValidationException, OrderInitException {
        CustomerCallOrderManager callOrderManager = new CustomerCallOrderManager(getContext());
        callOrderManager.cancelCustomerOrder(customerId, callOrderId);
        removeCalls(customerId, CustomerCallType.CompleteLackOfDelivery, CustomerCallType.CompleteReturnDelivery);
        callOrderManager.initCall(callOrderId, true);
        CustomerCallModel callModel = getItem(new Query().from(CustomerCall.CustomerCallTbl)
                .whereAnd(Criteria.equals(CustomerCall.CustomerId, customerId.toString()))
                .whereAnd(Criteria.equals(CustomerCall.ExtraField1, callOrderId.toString())));
        if (callModel == null)
            insert(CustomerCallType.OrderLackOfDelivery, customerId, callOrderId.toString(), reasonId.toString(), false);
        else {
            callModel.CallType = CustomerCallType.OrderLackOfDelivery;
            callModel.ExtraField2 = reasonId.toString();
            callModel.ConfirmStatus = false;
            callModel.UpdatedTime = new Date();
            update(callModel);
        }
        removeCalls(customerId, CustomerCallType.CompleteReturnDelivery, CustomerCallType.CompleteLackOfDelivery, CustomerCallType.LackOfVisit);
    }

    public void saveReturnCall(final UUID customerId, boolean witRef, boolean isFromRequest) throws ValidationException, DbException {
        CustomerCallType type;
        if (witRef)
            type = CustomerCallType.SaveReturnRequestWithRef;
        else
            type = CustomerCallType.SaveReturnRequestWithoutRef;
        addOrResetCall(type, customerId, isFromRequest ? "isFromRequest" : null);
        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist))
            removeLackOfVisit(customerId);
        else
            removeLackOfVisitAndLackOfOrder(customerId);
        unConfirmAllCalls(customerId);
    }

    public void removeCallReturn(UUID customerId, @Nullable Boolean withRef, @Nullable Boolean isFromRequest) throws DbException, ValidationException {
        CustomerCallType type;
        if (withRef == null)
            type = null;
        else if (withRef)
            type = CustomerCallType.SaveReturnRequestWithRef;
        else
            type = CustomerCallType.SaveReturnRequestWithoutRef;

        if (isFromRequest == null) {
            if (type == null)
                delete(Criteria.equals(CustomerCall.CallType, CustomerCallType.SaveReturnRequestWithRef.ordinal())
                        .or(Criteria.equals(CustomerCall.CallType, CustomerCallType.SaveReturnRequestWithoutRef.ordinal()))
                        .and(Criteria.equals(CustomerCall.CustomerId, customerId.toString())));
            else
                delete(Criteria.equals(CustomerCall.CallType, type.ordinal())
                        .and(Criteria.equals(CustomerCall.CustomerId, customerId.toString())));
        } else if (isFromRequest) {
            if (type == null)
                delete(Criteria.equals(CustomerCall.CallType, CustomerCallType.SaveReturnRequestWithRef.ordinal())
                        .or(Criteria.equals(CustomerCall.CallType, CustomerCallType.SaveReturnRequestWithoutRef.ordinal()))
                        .and(Criteria.equals(CustomerCall.CustomerId, customerId.toString()))
                        .and(Criteria.equals(CustomerCall.ExtraField1, "isFromRequest")));
            else
                delete(Criteria.equals(CustomerCall.CallType, type.ordinal())
                        .and(Criteria.equals(CustomerCall.CustomerId, customerId.toString()))
                        .and(Criteria.equals(CustomerCall.ExtraField1, "isFromRequest")));
        } else {
            if (type == null)
                delete(Criteria.equals(CustomerCall.CallType, CustomerCallType.SaveReturnRequestWithRef.ordinal())
                        .or(Criteria.equals(CustomerCall.CallType, CustomerCallType.SaveReturnRequestWithoutRef.ordinal()))
                        .and(Criteria.equals(CustomerCall.CustomerId, customerId.toString()))
                        .and(Criteria.isNull(CustomerCall.ExtraField1)));
            else
                delete(Criteria.equals(CustomerCall.CallType, type.ordinal())
                        .and(Criteria.equals(CustomerCall.CustomerId, customerId.toString()))
                        .and(Criteria.isNull(CustomerCall.ExtraField1)));
        }

        unConfirmAllCalls(customerId);
    }

    private long removeLackOfVisit(UUID customerId) throws DbException {
        return removeCalls(customerId, CustomerCallType.LackOfVisit);
    }

    // customer edit
    public void saveEditCustomerCall(UUID customerId) throws ValidationException, DbException {
        addOrResetCall(CustomerCallType.EditCustomer, customerId, null);
    }

    public boolean isEdited(List<CustomerCallModel> calls) {
        return Linq.exists(calls, new Linq.Criteria<CustomerCallModel>() {
            @Override
            public boolean run(CustomerCallModel item) {
                return (item.CallType == CustomerCallType.EditCustomer);
            }
        });
    }

    // customer location
    public void saveLocationCall(UUID customerId) throws ValidationException, DbException {
        addOrResetCall(CustomerCallType.ChangeLocation, customerId, null);
    }

    public boolean isLocationChanged(List<CustomerCallModel> calls) {
        return Linq.exists(calls, item -> (item.CallType == CustomerCallType.ChangeLocation));
    }

    // questionnaire
    public void saveQuestionnaireCall(UUID customerId) throws ValidationException, DbException {
        addOrResetCall(CustomerCallType.Questionnaire, customerId, null);
    }

    public boolean isQuestionnaireAnswered(List<CustomerCallModel> calls) {
        return Linq.exists(calls, item -> (item.CallType == CustomerCallType.Questionnaire));
    }

    public void removeQuestionnaireCall(UUID customerId) throws DbException {
        delete(Criteria.equals(CustomerCall.CallType, CustomerCallType.Questionnaire.ordinal())
                .and(Criteria.equals(CustomerCall.CustomerId, customerId.toString())));
    }

    // camera
    public void saveCameraCall(UUID customerId) throws ValidationException, DbException {
        addOrResetCall(CustomerCallType.Camera, customerId, null);
    }

    public boolean hasCameraCall(List<CustomerCallModel> calls) {
        return Linq.exists(calls, item -> item.CallType == CustomerCallType.Camera);
    }

    public void removeCameraCall(UUID customerId) throws DbException {
        delete(Criteria.equals(CustomerCall.CallType, CustomerCallType.Camera.ordinal())
                .and(Criteria.equals(CustomerCall.CustomerId, customerId.toString())));
    }

    // customer inventory
    public void saveCustomerInventoryCall(UUID customerId) throws ValidationException, DbException {
        addOrResetCall(CustomerCallType.CustomerInventory, customerId, null);
    }

    public boolean hasInventoryCall(List<CustomerCallModel> calls) {
        return Linq.exists(calls, new Linq.Criteria<CustomerCallModel>() {
            @Override
            public boolean run(CustomerCallModel item) {
                return item.CallType == CustomerCallType.CustomerInventory;
            }
        });
    }

    // payment

    public void savePaymentCall(UUID customerId) throws ValidationException, DbException {
        addOrResetCall(CustomerCallType.Payment, customerId, null);
    }

    public boolean hasPaymentCall(List<CustomerCallModel> calls) {
        return Linq.exists(calls, new Linq.Criteria<CustomerCallModel>() {
            @Override
            public boolean run(CustomerCallModel item) {
                return item.CallType == CustomerCallType.Payment;
            }
        });
    }


    public void removePaymentCall(@NonNull UUID customerId) throws DbException, ValidationException {
        unConfirmAllCalls(customerId);
        delete(Criteria.equals(CustomerCall.CallType, CustomerCallType.Payment.ordinal())
                .and(Criteria.equals(CustomerCall.CustomerId, customerId.toString())));
        Timber.i("Payment call removed");
    }


    public String getName(List<CustomerCallModel> calls, boolean isContractor) {
        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            String name = "";
            boolean hasDeliveryCall = hasDeliveryCall(calls, null, null);
            boolean hasReturnCall = hasReturnCall(calls, null, null);
            boolean isLackOfVisit = isLackOfVisit(calls);
            boolean isCompleteLackOfDelivery = isCompleteLackOfDelivery(calls);
            boolean isCompleteReturnDelivery = isCompleteReturnDelivery(calls);
            boolean orderDelivered = hasDistCall(calls, null, CustomerCallType.OrderDelivered);
            boolean orderPartiallyDelivered = hasDistCall(calls, null, CustomerCallType.OrderPartiallyDelivered);
            boolean orderLackOfDelivery = hasDistCall(calls, null, CustomerCallType.OrderLackOfDelivery);
            boolean orderReturn = hasDistCall(calls, null, CustomerCallType.OrderReturn);
            if (isDataSent(calls, true))
                name = getContext().getString(R.string.is_sent);
            else if ((hasDeliveryCall || orderReturn || orderLackOfDelivery
                    || hasReturnCall || isLackOfVisit || isCompleteLackOfDelivery
                    || isCompleteReturnDelivery) && !isConfirmed(calls) && !isContractor)
                name = getContext().getString(R.string.incomplete_operation);
            else if (hasDeliveryCall || orderReturn || orderLackOfDelivery) {
                if (orderDelivered && !orderPartiallyDelivered && !orderLackOfDelivery && !orderReturn)
                    name = getContext().getString(R.string.has_delivery);
                else if (!orderDelivered && orderPartiallyDelivered && !orderLackOfDelivery && !orderReturn)
                    name = getContext().getString(R.string.delivered_partially);
                else if (!orderDelivered && !orderPartiallyDelivered && orderLackOfDelivery && !orderReturn)
                    name = getContext().getString(R.string.lack_of_delivery);
                else if (!orderDelivered && !orderPartiallyDelivered && !orderLackOfDelivery && orderReturn)
                    name = getContext().getString(R.string.complete_return);
                else
                    name = getContext().getString(R.string.delivered_partially);
            } else if (hasReturnCall)
                name = getContext().getString(R.string.has_return_order);
            else if (isLackOfVisit)
                name = getContext().getString(R.string.not_visited_call_status);
            else if (isCompleteLackOfDelivery)
                name = getContext().getString(R.string.not_delivered_call_status);
            else if (isCompleteReturnDelivery)
                name = getContext().getString(R.string.returned_call_status);
            else
                name = getContext().getString(R.string.unKnown);

            return name;
        } else {
            String name = "";
            boolean hasOrderAndReturnCall = hasOrderAndReturnCall(calls);
            boolean hasOrderCall = hasOrderCall(calls, null);
            boolean hasReturnCall = hasReturnCall(calls, null, null);
            boolean isLackOfOrder = isLackOfOrder(calls);
            boolean isLackOfVisit = isLackOfVisit(calls);
            if (isDataSent(calls, true))
                name = getContext().getString(R.string.is_sent);
            else if ((hasOrderAndReturnCall || hasOrderCall || hasReturnCall || isLackOfOrder || isLackOfVisit) && !isConfirmed(calls) && !isContractor)
                name = getContext().getString(R.string.incomplete_operation);
            else if (hasOrderAndReturnCall)
                name = getContext().getString(R.string.order_and_return);
            else if (hasOrderCall)
                name = getContext().getString(R.string.has_order);
            else if (hasReturnCall)
                name = getContext().getString(R.string.has_return_order);
            else if (isLackOfOrder)
                name = getContext().getString(R.string.not_ordered_call_status);
            else if (isLackOfVisit)
                name = getContext().getString(R.string.not_visited_call_status);
            else
                name = getContext().getString(R.string.unKnown);

            return name;
        }
    }

    public CustomerCallType getCustomerCallTypeFromId(String id) {
        if (id.equals("0"))
            return CustomerCallType.SaveOrderRequest;
        else if (id.equals("1"))
            return CustomerCallType.SaveReturnRequestWithRef;
        else if (id.equals("2"))
            return CustomerCallType.SaveReturnRequestWithoutRef;
        else if (id.equals("3"))
            return CustomerCallType.LackOfVisit;
        else if (id.equals("4"))
            return CustomerCallType.LackOfOrder;
        else if (id.equals("5"))
            return CustomerCallType.Camera;
        else if (id.equals("6"))
            return CustomerCallType.EditCustomer;
        else if (id.equals("7"))
            return CustomerCallType.CustomerInventory;
        else if (id.equals("8"))
            return CustomerCallType.Questionnaire;
        else if (id.equals("9"))
            return CustomerCallType.ChangeLocation;
        else if (id.equals("10"))
            return CustomerCallType.SendData;
        else
            return CustomerCallType.Payment;
    }

    public int getColor(List<CustomerCallModel> calls, boolean isContractor) {
        int coral = Color.parseColor("#FF7F50");
        int orange = Color.parseColor("#FF4500");
        int darkGrey = Color.parseColor("#A9A9A9");
        int darkGreen = Color.parseColor("#006400");
        int olive = Color.parseColor("#808000");
        int purple = Color.parseColor("#CB77FD");
        int blue = Color.parseColor("#204dd6");
        int darkSea = Color.parseColor("#7A9179");
        int lightBlue = Color.parseColor("#00BCD4");
        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            String name = "";
            boolean hasDeliveryCall = hasDeliveryCall(calls, null, null);
            boolean hasReturnCall = hasReturnCall(calls, null, null);
            boolean isLackOfVisit = isLackOfVisit(calls);
            boolean isCompleteLackOfDelivery = isCompleteLackOfDelivery(calls);
            boolean isCompleteReturnDelivery = isCompleteReturnDelivery(calls);
            boolean orderDelivered = hasDistCall(calls, null, CustomerCallType.OrderDelivered);
            boolean orderPartiallyDelivered = hasDistCall(calls, null, CustomerCallType.OrderPartiallyDelivered);
            boolean orderLackOfDelivery = hasDistCall(calls, null, CustomerCallType.OrderLackOfDelivery);
            boolean orderReturn = hasDistCall(calls, null, CustomerCallType.OrderReturn);
            if (isDataSent(calls, true))
                return purple;
            else if ((hasDeliveryCall || orderReturn || orderLackOfDelivery
                    || hasReturnCall || isLackOfVisit || isCompleteLackOfDelivery
                    || isCompleteReturnDelivery) && !isConfirmed(calls) && !isContractor)
                return darkSea;
            else if (isCompleteLackOfDelivery)
                return coral;
            else if (isCompleteReturnDelivery)
                return orange;
            else if (hasDeliveryCall || orderReturn || orderLackOfDelivery) {
                if (orderDelivered && !orderPartiallyDelivered && !orderLackOfDelivery && !orderReturn)
                    return darkGreen;
                else if (!orderDelivered && orderPartiallyDelivered && !orderLackOfDelivery && !orderReturn)
                    return lightBlue;
                else if (!orderDelivered && !orderPartiallyDelivered && orderLackOfDelivery && !orderReturn)
                    return coral;
                else if (!orderDelivered && !orderPartiallyDelivered && !orderLackOfDelivery && orderReturn)
                    return orange;
                else
                    return lightBlue;
            } else if (hasReturnCall)
                return olive;
            else if (isLackOfVisit)
                return coral;
            else
                return darkGrey;
        } else {
            boolean hasOrderAndReturnCall = hasOrderAndReturnCall(calls);
            boolean hasOrderCall = hasOrderCall(calls, null);
            boolean hasReturnCall = hasReturnCall(calls, null, null);
            boolean isLackOfOrder = isLackOfOrder(calls);
            boolean isLackOfVisit = isLackOfVisit(calls);
            if (isDataSent(calls, true))
                return purple;
            else if ((hasOrderAndReturnCall || hasOrderCall || hasReturnCall || isLackOfOrder || isLackOfVisit) && !isConfirmed(calls) && !isContractor)
                return darkSea;
            else if (hasOrderAndReturnCall)
                return blue;
            else if (hasOrderCall)
                return darkGreen;
            else if (hasReturnCall)
                return olive;
            else if (isLackOfVisit)
                return coral;
            else if (isLackOfOrder)
                return orange;
            else
                return darkGrey;
        }
    }

    @DrawableRes
    public int getIcon(List<CustomerCallModel> calls) {
        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            if (hasDeliveryOrReturnCall(calls))
                return R.drawable.ic_location_on_green_a700_36dp;
            else if (isLackOfVisit(calls))
                return R.drawable.ic_location_on_amber_a700_36dp;
            else if (isCompleteLackOfDelivery(calls))
                return R.drawable.ic_location_on_orange_a700_36dp;
            else if (isCompleteReturnDelivery(calls))
                return R.drawable.ic_location_on_orange_a700_36dp;
            else if (isDataSent(calls, true))
                return R.drawable.ic_location_on_purple_a700_36dp;
            else
                return R.drawable.ic_location_on_grey_500_36dp;
        } else {
            if (hasOrderOrReturnCall(calls))
                return R.drawable.ic_location_on_green_a700_36dp;
            else if (isLackOfVisit(calls))
                return R.drawable.ic_location_on_amber_a700_36dp;
            else if (isLackOfOrder(calls))
                return R.drawable.ic_location_on_orange_a700_36dp;
            else if (isDataSent(calls, true))
                return R.drawable.ic_location_on_purple_a700_36dp;
            else
                return R.drawable.ic_location_on_grey_500_36dp;
        }

    }

    public List<CustomerCallModel> getUnconfirmedCalls() {
        Query subQuery = new Query();
        subQuery.from(CustomerCall.CustomerCallTbl).whereAnd(Criteria.equals(CustomerCall.ConfirmStatus, false));
        return new CustomerCallModelRepository().getItems(subQuery);
    }


}
