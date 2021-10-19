package com.varanegar.vaslibrary.manager.orderprizemanager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.projection.Projection;
import com.varanegar.vaslibrary.model.orderprize.OrderPrize;
import com.varanegar.vaslibrary.model.orderprize.OrderPrizeModel;
import com.varanegar.vaslibrary.model.orderprize.OrderPrizeModelRepository;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 12/23/2017.
 */

public class OrderPrizeManager extends BaseManager<OrderPrizeModel> {

    public OrderPrizeManager(@NonNull Context context) {
        super(context, new OrderPrizeModelRepository());
    }

    public static Query getCustomerOrderPrizes(UUID customerId, UUID callOrderId) {
        Query query = new Query();
        query.from(OrderPrize.OrderPrizeTbl).whereAnd(Criteria.equals(OrderPrize.CustomerId, customerId))
                .whereAnd(Criteria.equals(OrderPrize.CallOrderId, callOrderId));
        return query;
    }

    public static Query getCustomerOrderPrizes(UUID customerId, int disRef, UUID callOrderId) {
        Query query = new Query();
        query.from(OrderPrize.OrderPrizeTbl)
                .whereAnd(Criteria.equals(OrderPrize.CustomerId, customerId))
                .whereAnd(Criteria.equals(OrderPrize.DisRef, disRef))
                .whereAnd(Criteria.equals(OrderPrize.CallOrderId, callOrderId));
        return query;
    }

    public static Query getCustomerDisRefOrderPrizes(UUID customerId, UUID callOrderId) {
        Query query = new Query();
        query.from(OrderPrize.OrderPrizeTbl).whereAnd(Criteria.equals(OrderPrize.CustomerId, customerId))
                .whereAnd(Criteria.equals(OrderPrize.CallOrderId, callOrderId))
                .groupBy(OrderPrize.DisRef);
        return query;
    }

    public static BigDecimal totalQtyOrderPrize(UUID customerId, int disRef, UUID callOrderId) {
        Query query = new Query();
        query.from(OrderPrize.OrderPrizeTbl)
                .whereAnd(Criteria.equals(OrderPrize.CustomerId, customerId))
                .whereAnd(Criteria.equals(OrderPrize.DisRef, disRef))
                .whereAnd(Criteria.equals(OrderPrize.CallOrderId, callOrderId))
                .select(Projection.sum(OrderPrize.TotalQty));
        return VaranegarApplication.getInstance().getDbHandler().getBigDecimalSingle(query);
    }
}
