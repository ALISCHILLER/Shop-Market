package com.varanegar.vaslibrary.manager.orderprizeview;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.vaslibrary.model.orderprizeview.OrderPrizeView;
import com.varanegar.vaslibrary.model.orderprizeview.OrderPrizeViewModel;
import com.varanegar.vaslibrary.model.orderprizeview.OrderPrizeViewModelRepository;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 12/24/2017.
 */

public class OrderPrizeViewManager extends BaseManager<OrderPrizeViewModel> {
    public OrderPrizeViewManager(@NonNull Context context) {
        super(context, new OrderPrizeViewModelRepository());
    }
    public static Query getCustomerOrderPrizes(UUID customerId, int disRef, UUID callOrderId) {
        Query query = new Query();
        query.from(OrderPrizeView.OrderPrizeViewTbl)
                .whereAnd(Criteria.equals(OrderPrizeView.CustomerId, customerId))
                .whereAnd(Criteria.equals(OrderPrizeView.DisRef, disRef))
                .whereAnd(Criteria.equals(OrderPrizeView.CallOrderId, callOrderId));
        return query;
    }

    public static Query getCustomerOrderPrizes(UUID customerId) {
        Query query = new Query();
        query.from(OrderPrizeView.OrderPrizeViewTbl)
                .whereAnd(Criteria.equals(OrderPrizeView.CustomerId, customerId));
        return query;
    }

}
