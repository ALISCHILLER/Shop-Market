package com.varanegar.vaslibrary.manager;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.model.ModelProjection;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.from.From;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.Linq;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallOrderManager;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.productorderviewmanager.OrderBy;
import com.varanegar.vaslibrary.manager.productorderviewmanager.OrderType;
import com.varanegar.vaslibrary.model.call.CustomerCallOrderModel;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderView;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderViewModel;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderViewModelRepository;
import com.varanegar.vaslibrary.model.customercall.CustomerCallModel;
import com.varanegar.vaslibrary.model.customercall.CustomerCallType;

import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 8/7/2017.
 */

public class CustomerCallOrderOrderViewManager extends BaseManager<CustomerCallOrderOrderViewModel> {
    public CustomerCallOrderOrderViewManager(@NonNull Context context) {
        super(context, new CustomerCallOrderOrderViewModelRepository());
    }

    private void setOrderBy(OrderBy orderBy) {
        SharedPreferences.Editor editor = getContext()
                .getSharedPreferences("CustomerCallOrderOrderViewManager", Context.MODE_PRIVATE).edit();
        if (orderBy.getType() == OrderType.ASC)
            editor.putString("orderType", "ASC");
        else
            editor.putString("orderType", "DESC");
        editor.putString("orderColumn", orderBy.getColumn().getSimpleName());
        editor.apply();
    }

    @Nullable
    public OrderBy getOrderBy() {
        try {
            SharedPreferences sharedPreferences = getContext()
                    .getSharedPreferences("CustomerCallOrderOrderViewManager", Context.MODE_PRIVATE);
            String orderType = sharedPreferences.getString("orderType", "ASC");
            String orderColumn = sharedPreferences.getString("orderColumn", "ProductName");
            HashMap<String, ModelProjection<CustomerCallOrderOrderViewModel>> columns = CustomerCallOrderOrderView.CustomerCallOrderOrderViewTbl.getColumns();
            if (columns.containsKey(orderColumn))
                return new OrderBy(columns.get(orderColumn), orderType.equals("DESC") ? OrderType.DESC : OrderType.ASC);
            else
                return null;
        } catch (Exception ex) {
            return null;
        }
    }

    public Query getLinesQuery(UUID callOrderId, @Nullable OrderBy orderBy) {
        if (orderBy == null)
            orderBy = getOrderBy();
        else
            setOrderBy(orderBy);

        Query query = new Query().
                from(CustomerCallOrderOrderView.CustomerCallOrderOrderViewTbl);
        if (callOrderId != null)
                query = query.whereAnd(Criteria.equals(CustomerCallOrderOrderView.OrderUniqueId, callOrderId.toString()));
        if (orderBy == null)
            return query.orderByDescending(CustomerCallOrderOrderView.ProductName);
        else {
            if (orderBy.getType() == OrderType.ASC)
                return query.orderByAscending(orderBy.getColumn());
            else
                return query.orderByDescending(orderBy.getColumn());
        }

    }

    public CustomerCallOrderOrderViewModel getFreeOrderLine(@NonNull UUID orderId, @NonNull UUID productId, @NonNull UUID freeReasonId) {
        Query query = new Query().from(CustomerCallOrderOrderView.CustomerCallOrderOrderViewTbl)
                .whereAnd(Criteria.equals(CustomerCallOrderOrderView.OrderUniqueId, orderId.toString())
                        .and(Criteria.equals(CustomerCallOrderOrderView.ProductId, productId.toString()))
                        .and(Criteria.equals(CustomerCallOrderOrderView.FreeReasonId, freeReasonId.toString())));
        return getItem(query);
    }

    public List<CustomerCallOrderOrderViewModel> getOrderLines(@NonNull UUID orderId, @NonNull UUID productId) {
        Query query = new Query().from(CustomerCallOrderOrderView.CustomerCallOrderOrderViewTbl)
                .whereAnd(Criteria.equals(CustomerCallOrderOrderView.OrderUniqueId, orderId.toString())
                        .and(Criteria.equals(CustomerCallOrderOrderView.ProductId, productId.toString())));
        return getItems(query);
    }


    public List<CustomerCallOrderOrderViewModel> getFreeOrderLines(@NonNull UUID orderId, @NonNull UUID productId) {
        Query query = new Query().from(CustomerCallOrderOrderView.CustomerCallOrderOrderViewTbl)
                .whereAnd(Criteria.equals(CustomerCallOrderOrderView.OrderUniqueId, orderId.toString())
                        .and(Criteria.equals(CustomerCallOrderOrderView.ProductId, productId.toString()))
                        .and(Criteria.notIsNull(CustomerCallOrderOrderView.FreeReasonId)));
        return getItems(query);
    }


    public List<CustomerCallOrderOrderViewModel> getLines(@NonNull UUID callOrderId, @Nullable OrderBy orderBy) {
        return getItems(getLinesQuery(callOrderId, orderBy));
    }

    public int calculateUsanceDayAverage(UUID callOrderId, String customerId) {
        List<CustomerCallOrderOrderViewModel> callOrderOrderViewModels = getLines(callOrderId, null);
        Currency totalOrderAmount = Currency.ZERO;
        Currency totalOrderAmountMultiplyDays = Currency.ZERO;
        for (CustomerCallOrderOrderViewModel customerCallOrderOrderViewModel : callOrderOrderViewModels) {
            if (!customerCallOrderOrderViewModel.IsRequestFreeItem) {
                Currency reauestAmount = customerCallOrderOrderViewModel.RequestAmount == null ? Currency.ZERO : customerCallOrderOrderViewModel.RequestAmount;
                totalOrderAmount = totalOrderAmount.add(reauestAmount);
                int payDuration;
                final SharedPreferences sharedPreferences = getContext().getSharedPreferences("UsanceDaySharedPrefences", Context.MODE_PRIVATE);
                if (sharedPreferences.getBoolean(callOrderId.toString() + customerId + "CheckBoxChecked", false) && customerCallOrderOrderViewModel.PayDuration > 0)
                    payDuration = customerCallOrderOrderViewModel.PayDuration;
                else {
                    if (customerCallOrderOrderViewModel.ProductPayDuration == 0)
                        payDuration = 1;
                    else
                        payDuration = customerCallOrderOrderViewModel.ProductPayDuration;
                }
                totalOrderAmountMultiplyDays = totalOrderAmountMultiplyDays.add(reauestAmount.multiply(new Currency(payDuration)));
            }
        }
        return HelperMethods.currencyToInt(totalOrderAmountMultiplyDays.divide( totalOrderAmount.compareTo(Currency.ZERO) == 0 ? Currency.ONE : totalOrderAmount, RoundingMode.HALF_UP));
    }

    public OrderAmount calculateTotalAmount(UUID callOrderId) {
        List<CustomerCallOrderOrderViewModel> callOrderOrderViewModels = getLines(callOrderId, null);
        Currency totalNetAmount = Currency.ZERO;
        Currency totalRequestAmount = Currency.ZERO;
        Currency totalDiscountAmount = Currency.ZERO;
        Currency totalAddAmount = Currency.ZERO;
        Currency totalAdd1Amount = Currency.ZERO;
        Currency totalAdd2Amount = Currency.ZERO;
        Currency totalOtherAddAmount = Currency.ZERO;
        Currency totalTaxAmount = Currency.ZERO;
        Currency totalChargeAmount = Currency.ZERO;
        Currency totalDis1Amount = Currency.ZERO;
        Currency totalDis2Amount = Currency.ZERO;
        Currency totalDis3Amount = Currency.ZERO;
        Currency totalOtherDisAmount = Currency.ZERO;
        for (CustomerCallOrderOrderViewModel customerCallOrderOrderViewModel :
                callOrderOrderViewModels) {
            if (!customerCallOrderOrderViewModel.IsRequestFreeItem) {
                Currency requestAmount;
                if (customerCallOrderOrderViewModel.IsPromoLine)
                    requestAmount = customerCallOrderOrderViewModel.PromotionPrice == null ? Currency.ZERO : customerCallOrderOrderViewModel.PromotionPrice;
                else
                    requestAmount = customerCallOrderOrderViewModel.RequestAmount == null ? Currency.ZERO : customerCallOrderOrderViewModel.RequestAmount;
                Currency requestDis1Amount = customerCallOrderOrderViewModel.RequestDis1Amount == null ? Currency.ZERO : customerCallOrderOrderViewModel.RequestDis1Amount;
                Currency requestDis2Amount = customerCallOrderOrderViewModel.RequestDis2Amount == null ? Currency.ZERO : customerCallOrderOrderViewModel.RequestDis2Amount;
                Currency requestDis3Amount = customerCallOrderOrderViewModel.RequestDis3Amount == null ? Currency.ZERO : customerCallOrderOrderViewModel.RequestDis3Amount;
                Currency requestDisOtherAmount = customerCallOrderOrderViewModel.RequestOtherDiscountAmount == null ? Currency.ZERO : customerCallOrderOrderViewModel.RequestOtherDiscountAmount;
                Currency requestAdd1Amount = customerCallOrderOrderViewModel.RequestAdd1Amount == null ? Currency.ZERO : customerCallOrderOrderViewModel.RequestAdd1Amount;
                Currency requestAdd2Amount = customerCallOrderOrderViewModel.RequestAdd2Amount == null ? Currency.ZERO : customerCallOrderOrderViewModel.RequestAdd2Amount;
                Currency requestAddOtherAmount = customerCallOrderOrderViewModel.RequestAddOtherAmount == null ? Currency.ZERO : customerCallOrderOrderViewModel.RequestAddOtherAmount;
                Currency requestChargeAmount = customerCallOrderOrderViewModel.RequestChargeAmount == null ? Currency.ZERO : customerCallOrderOrderViewModel.RequestChargeAmount;
                Currency requestTaxAmount = customerCallOrderOrderViewModel.RequestTaxAmount == null ? Currency.ZERO : customerCallOrderOrderViewModel.RequestTaxAmount;
                totalRequestAmount = totalRequestAmount.add(requestAmount);
                totalDiscountAmount = totalDiscountAmount.add(requestDis1Amount).add(requestDis2Amount).add(requestDis3Amount).add(requestDisOtherAmount);
                totalDis1Amount = totalDis1Amount.add(requestDis1Amount);
                totalDis2Amount = totalDis2Amount.add(requestDis2Amount);
                totalDis3Amount = totalDis3Amount.add(requestDis3Amount);
                totalOtherDisAmount = totalOtherDisAmount.add(requestDisOtherAmount);
                totalAddAmount = totalAddAmount.add(requestAdd1Amount).add(requestAdd2Amount).add(requestChargeAmount).add(requestTaxAmount).add(requestAddOtherAmount);
                totalAdd1Amount = totalAdd1Amount.add(requestAdd1Amount);
                totalAdd2Amount = totalAdd2Amount.add(requestAdd2Amount);
                totalTaxAmount = totalTaxAmount.add(requestTaxAmount);
                totalChargeAmount = totalChargeAmount.add(requestChargeAmount);
                totalOtherAddAmount = totalOtherAddAmount.add(requestAddOtherAmount);
                totalNetAmount = totalNetAmount.add(requestAmount)
                        .subtract(requestDis1Amount)
                        .subtract(requestDis2Amount)
                        .subtract(requestDis3Amount)
                        .subtract(requestDisOtherAmount)
                        .add(requestAdd1Amount)
                        .add(requestAdd2Amount)
                        .add(requestAddOtherAmount)
                        .add(requestChargeAmount)
                        .add(requestTaxAmount);

            }
        }
        OrderAmount orderAmount = new OrderAmount();
        orderAmount.NetAmount = totalNetAmount;
        orderAmount.AddAmount = totalAddAmount;
        orderAmount.DiscountAmount = totalDiscountAmount;
        orderAmount.TotalAmount = totalRequestAmount;
        orderAmount.Add1Amount = totalAdd1Amount;
        orderAmount.Add2Amount = totalAdd2Amount;
        orderAmount.AddOtherAmount = totalOtherAddAmount;
        orderAmount.TaxAmount = totalTaxAmount;
        orderAmount.ChargeAmount = totalChargeAmount;
        orderAmount.Dis1Amount = totalDis1Amount;
        orderAmount.Dis2Amount = totalDis2Amount;
        orderAmount.Dis3Amount = totalDis3Amount;
        orderAmount.DisOtherAmount = totalOtherDisAmount;
        return orderAmount;
    }

    public Currency calculateTotalAmountOfAllOrders(UUID customerId, boolean withDiscount) {
        CustomerCallOrderManager callOrderManager = new CustomerCallOrderManager(getContext());
        List<CustomerCallOrderModel> customerCallOrderModels = callOrderManager.getCustomerCallOrders(customerId);
        Currency totalPrice = Currency.ZERO;
        List<CustomerCallModel> callModels = new CustomerCallManager(getContext()).loadCalls(customerId);
        for (final CustomerCallOrderModel customerCallOrderModel :
                customerCallOrderModels) {
            boolean checkOrder = true;
            if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
                checkOrder = Linq.exists(callModels, new Linq.Criteria<CustomerCallModel>() {
                    @Override
                    public boolean run(CustomerCallModel item) {
                        boolean checkOrder = item.CallType == CustomerCallType.OrderDelivered || item.CallType == CustomerCallType.OrderPartiallyDelivered;
                        checkOrder = checkOrder && customerCallOrderModel.UniqueId.toString().equals(item.ExtraField1);
                        return checkOrder;
                    }
                });
            }
            if (checkOrder) {
                if (withDiscount)
                    totalPrice = totalPrice.add(calculateTotalAmount(customerCallOrderModel.UniqueId).NetAmount);
                else
                    totalPrice = totalPrice.add(calculateTotalAmount(customerCallOrderModel.UniqueId).TotalAmount);
            }
        }
        return totalPrice;
    }

    public static Query getAll() {
        return new Query().from(CustomerCallOrderOrderView.CustomerCallOrderOrderViewTbl);
    }
    public CustomerCallOrderOrderViewModel getOrderLine(@NonNull UUID productId) {
        Query query=new Query();
        query.from(From.table(CustomerCallOrderOrderView.CustomerCallOrderOrderViewTbl)).whereAnd(Criteria.contains(
                CustomerCallOrderOrderView.ProductId, String.valueOf(productId)));
       return getItem(query) ;
    }
    public int getLinesCount(UUID orderId) {
        Query query = new Query();
        query.from(CustomerCallOrderOrderView.CustomerCallOrderOrderViewTbl).whereAnd(Criteria.equals(CustomerCallOrderOrderView.OrderUniqueId, orderId.toString()));
        List<CustomerCallOrderOrderViewModel> customerCallOrderOrderViewModels = getItems(query);
        if (customerCallOrderOrderViewModels == null)
            return 0;
        else
            return customerCallOrderOrderViewModels.size();
    }
}
