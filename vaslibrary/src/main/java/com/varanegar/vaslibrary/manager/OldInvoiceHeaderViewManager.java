package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.vaslibrary.model.oldinvoiceheaderview.OldInvoiceHeaderView;
import com.varanegar.vaslibrary.model.oldinvoiceheaderview.OldInvoiceHeaderViewModel;
import com.varanegar.vaslibrary.model.oldinvoiceheaderview.OldInvoiceHeaderViewModelRepository;

import java.util.List;
import java.util.UUID;

/**
 * Created by s.foroughi on 14/01/2017.
 */

public class OldInvoiceHeaderViewManager extends BaseManager<OldInvoiceHeaderViewModel> {
    public OldInvoiceHeaderViewManager(Context context) {
        super(context, new OldInvoiceHeaderViewModelRepository());
    }

    public static Query getAll() {
        Query query = new Query();
        query.from(OldInvoiceHeaderView.OldInvoiceHeaderViewTbl);
        return query;
    }

    public static Query getAll(UUID customerId) {
        Query query = new Query();
        query.from(OldInvoiceHeaderView.OldInvoiceHeaderViewTbl).whereAnd(Criteria.equals(OldInvoiceHeaderView.CustomerUniqueId, customerId.toString()));
        return query;
    }

    public List<OldInvoiceHeaderViewModel> getInvoicesByCustomerId(@NonNull UUID customerId) {
        return getItems(OldInvoiceHeaderViewManager.getAll(customerId));
    }

    public List<OldInvoiceHeaderViewModel> getNotSettledInvoices(@NonNull UUID customerId, UUID dealerId) {
        Query query = new Query();
        query.from(OldInvoiceHeaderView.OldInvoiceHeaderViewTbl)
                .whereAnd(Criteria.equals(OldInvoiceHeaderView.CustomerUniqueId, customerId.toString())
                        .and(Criteria.greaterThan(OldInvoiceHeaderView.RemAmount, 0)))
                .whereAnd(Criteria.equals(OldInvoiceHeaderView.DealerId, dealerId));
        return getItems(query);
    }

    public List<OldInvoiceHeaderViewModel> getNotSettledInvoices(@NonNull UUID customerId) {
        Query query = new Query();
        query.from(OldInvoiceHeaderView.OldInvoiceHeaderViewTbl)
                .whereAnd(Criteria.equals(OldInvoiceHeaderView.CustomerUniqueId, customerId.toString())
                        .and(Criteria.greaterThan(OldInvoiceHeaderView.RemAmount, 0)));
        return getItems(query);
    }
}

