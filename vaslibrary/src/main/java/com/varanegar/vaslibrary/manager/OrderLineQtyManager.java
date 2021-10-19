package com.varanegar.vaslibrary.manager;

import android.content.Context;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.manager.customercall.CallOrderLineManager;
import com.varanegar.vaslibrary.model.call.CallOrderLineModel;
import com.varanegar.vaslibrary.model.invoiceLineQty.InvoiceLineQtyModel;
import com.varanegar.vaslibrary.model.orderLineQtyModel.OrderLineQty;
import com.varanegar.vaslibrary.model.orderLineQtyModel.OrderLineQtyModel;
import com.varanegar.vaslibrary.model.orderLineQtyModel.OrderLineQtyModelRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by s.foroughi on 28/02/2017.
 */

public class OrderLineQtyManager extends BaseManager<OrderLineQtyModel> {
    public OrderLineQtyManager(Context context) {
        super(context, new OrderLineQtyModelRepository());
    }


    public static Query getAll() {
        Query query = new Query();
        query.from(OrderLineQty.OrderLineQtyTbl);
        return query;
    }


    public static Query getOrderQtyDetail(UUID orderLineUniqueId, UUID productUnitId) {
        Query query = new Query();
        query.from(OrderLineQty.OrderLineQtyTbl)
                .whereAnd(Criteria.equals(OrderLineQty.OrderLineUniqueId, orderLineUniqueId.toString()))
                .whereAnd(Criteria.equals(OrderLineQty.ProductUnitId, productUnitId.toString()));
        return query;
    }

    private static Query getOrderQtyLines(UUID orderLineUniqueId) {
        Query query = new Query();
        query.from(OrderLineQty.OrderLineQtyTbl)
                .whereAnd(Criteria.equals(OrderLineQty.OrderLineUniqueId, orderLineUniqueId.toString()));
        return query;
    }

    public List<OrderLineQtyModel> getQtyLines(UUID orderLineUniqueId) {
        return getItems(getOrderQtyLines(orderLineUniqueId));
    }

    public void initDetails(UUID callOrderId) throws ValidationException, DbException {
        CallOrderLineManager callOrderLineManager = new CallOrderLineManager(getContext());
        InvoiceLineQtyManager invoiceLineQtyManager = new InvoiceLineQtyManager(getContext());
        List<CallOrderLineModel> lines = callOrderLineManager.getOrderLines(callOrderId);
        List<OrderLineQtyModel> orderLineQtyModels = new ArrayList<>();
        for (CallOrderLineModel callOrderLineModel :
                lines) {
            List<InvoiceLineQtyModel> invoiceLineQtyModels = invoiceLineQtyManager.getQtyLines(callOrderLineModel.UniqueId);
            for (InvoiceLineQtyModel invoiceLineQtyModel :
                    invoiceLineQtyModels) {
                OrderLineQtyModel orderLineQtyModel = new OrderLineQtyModel();
                orderLineQtyModel.OrderLineUniqueId = invoiceLineQtyModel.OrderLineUniqueId;
                orderLineQtyModel.ProductUnitId = invoiceLineQtyModel.ProductUnitId;
                orderLineQtyModel.UniqueId = invoiceLineQtyModel.UniqueId;
                orderLineQtyModel.Qty = invoiceLineQtyModel.Qty;
                orderLineQtyModels.add(orderLineQtyModel);
            }
        }
        if (orderLineQtyModels.size() > 0)
            insertOrUpdate(orderLineQtyModels);
    }

//    public void deleteAllCustomerOrderDetails(UUID customerId) throws DbException {
//        CustomerCallOrderOrderViewManager callOrderOrderViewManager = new CustomerCallOrderOrderViewManager(getContext());
//        List<CustomerCallOrderOrderViewModel> callOrderOrderViewModels = callOrderOrderViewManager.getItems(new Query()
//                .from(CustomerCallOrderOrderView.CustomerCallOrderOrderViewTbl)
//                .whereAnd(Criteria.equals(CustomerCallOrderOrderView.CustomerUniqueId, customerId.toString())));
//        String[] orderLineIds = new String[callOrderOrderViewModels.size()];
//        for (int i = 0; i < callOrderOrderViewModels.size(); i++) {
//            orderLineIds[i] = callOrderOrderViewModels.get(i).UniqueId.toString();
//        }
//        delete(Criteria.in(OrderLineQty.OrderLineUniqueId, orderLineIds));
//
//    }
//
//    public void deleteOrderDetails(UUID callOrderId) throws DbException {
//        CustomerCallOrderOrderViewManager callOrderOrderViewManager = new CustomerCallOrderOrderViewManager(getContext());
//        List<CustomerCallOrderOrderViewModel> callOrderOrderViewModels = callOrderOrderViewManager.getItems(new Query()
//                .from(CustomerCallOrderOrderView.CustomerCallOrderOrderViewTbl)
//                .whereAnd(Criteria.equals(CustomerCallOrderOrderView.OrderUniqueId, callOrderId.toString())));
//        String[] orderLineIds = new String[callOrderOrderViewModels.size()];
//        for (int i = 0; i < callOrderOrderViewModels.size(); i++) {
//            orderLineIds[i] = callOrderOrderViewModels.get(i).UniqueId.toString();
//        }
//        delete(Criteria.in(OrderLineQty.OrderLineUniqueId, orderLineIds));
//    }

    public Query getProductUnitLines (UUID productUnitId) {
        Query query = new Query();
        query.from(OrderLineQty.OrderLineQtyTbl)
                .whereAnd(Criteria.equals(OrderLineQty.ProductUnitId, productUnitId.toString()));
        return query;
    }
}
