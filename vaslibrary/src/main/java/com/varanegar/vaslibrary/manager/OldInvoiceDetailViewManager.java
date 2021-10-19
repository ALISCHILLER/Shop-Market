package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.vaslibrary.model.oldinvoicedetailview.OldInvoiceDetailView;
import com.varanegar.vaslibrary.model.oldinvoicedetailview.OldInvoiceDetailViewModel;
import com.varanegar.vaslibrary.model.oldinvoicedetailview.OldInvoiceDetailViewModelRepository;

import java.util.UUID;

/**
 * Created by atp on 4/10/2017.
 */

public class OldInvoiceDetailViewManager extends BaseManager<OldInvoiceDetailViewModel> {
    public OldInvoiceDetailViewManager(Context context) {
        super(context, new OldInvoiceDetailViewModelRepository());
    }

    public static Query getAll(UUID customerId) {
        Query query = new Query();
        query.from(OldInvoiceDetailView.OldInvoiceDetailViewTbl).whereAnd(Criteria.equals(OldInvoiceDetailView.CustomerId, customerId.toString()));
        return query;
    }

    public static Query getAll(@NonNull String productNameOrCode, @NonNull UUID customerId) {
        productNameOrCode = HelperMethods.persian2Arabic(productNameOrCode);
        productNameOrCode = HelperMethods.convertToEnglishNumbers(productNameOrCode);
        Query query = new Query();
        query.from(OldInvoiceDetailView.OldInvoiceDetailViewTbl).whereAnd(Criteria.equals(OldInvoiceDetailView.CustomerId, customerId.toString())).whereAnd(Criteria.contains(OldInvoiceDetailView.ProductName, productNameOrCode)
                .or(Criteria.contains(OldInvoiceDetailView.ProductCode, productNameOrCode)));
        return query;
    }

    public static Query getAllCustomerLines(UUID customerId) {
        Query query = new Query();
        query.from(OldInvoiceDetailView.OldInvoiceDetailViewTbl).whereAnd(Criteria.equals(OldInvoiceDetailView.CustomerId, customerId.toString()));
        return query;
    }

    public OldInvoiceDetailViewModel getLine(UUID customerId, UUID productId, UUID invoiceId) {
        Query query = new Query();
        query.from(OldInvoiceDetailView.OldInvoiceDetailViewTbl).whereAnd(Criteria.equals(OldInvoiceDetailView.CustomerId, customerId.toString())
                .and(Criteria.equals(OldInvoiceDetailView.ProductId, productId.toString())
                        .and(Criteria.equals(OldInvoiceDetailView.SaleId, invoiceId.toString()))));
        return getItem(query);
    }

    public static Query getInvoicesOfProduct(UUID productId, UUID customerId) {
        Query query = new Query();
        query.from(OldInvoiceDetailView.OldInvoiceDetailViewTbl).whereAnd(Criteria.equals(OldInvoiceDetailView.ProductId, productId.toString())
                .and(Criteria.equals(OldInvoiceDetailView.CustomerId, customerId.toString())));
        return query;
    }

    public static Query getItemsOfSubGroups(@NonNull UUID[] groupIds, @NonNull UUID customerId) {
        Query query = new Query();
        String[] ids = new String[groupIds.length];
        for (int i = 0; i < groupIds.length; i++) {
            ids[i] = groupIds[i].toString();
        }
        query.from(OldInvoiceDetailView.OldInvoiceDetailViewTbl)
                .whereAnd(Criteria.equals(OldInvoiceDetailView.CustomerId, customerId.toString())
                        .and(Criteria.in(OldInvoiceDetailView.ProductGroupId, ids)));
        return query;
    }

    public static Query getItemsOfSubGroup(@NonNull UUID groupId, @NonNull UUID customerId) {
        Query query = new Query();
        query.from(OldInvoiceDetailView.OldInvoiceDetailViewTbl)
                .whereAnd(Criteria.equals(OldInvoiceDetailView.ProductGroupId, groupId.toString())
                        .and(Criteria.equals(OldInvoiceDetailView.CustomerId, customerId.toString())));
        return query;
    }
}
