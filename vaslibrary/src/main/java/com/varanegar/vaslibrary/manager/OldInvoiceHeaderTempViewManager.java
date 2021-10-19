package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.vaslibrary.model.oldinvoiceheaderview.OldInvoiceHeaderTempView;
import com.varanegar.vaslibrary.model.oldinvoiceheaderview.OldInvoiceHeaderTempViewModel;
import com.varanegar.vaslibrary.model.oldinvoiceheaderview.OldInvoiceHeaderTempViewModelRepository;

import java.util.List;
import java.util.UUID;

/**
 * Created by e.hashemzadeh on 14/01/2017.
 */

public class OldInvoiceHeaderTempViewManager extends BaseManager<OldInvoiceHeaderTempViewModel> {
    public OldInvoiceHeaderTempViewManager(Context context) {
        super(context, new OldInvoiceHeaderTempViewModelRepository());
    }

    public static Query getAll() {
        Query query = new Query();
        query.from(OldInvoiceHeaderTempView.OldInvoiceHeaderTempViewTbl);
        return query;
    }

    public static Query getAll(UUID customerId) {
        Query query = new Query();
        query.from(OldInvoiceHeaderTempView.OldInvoiceHeaderTempViewTbl).whereAnd(Criteria.equals(OldInvoiceHeaderTempView.CustomerUniqueId, customerId.toString()));
        return query;
    }

    public List<OldInvoiceHeaderTempViewModel> getNotSettledInvoices(@NonNull UUID customerId, UUID dealerId) {
        Query query = new Query();
        query.from(OldInvoiceHeaderTempView.OldInvoiceHeaderTempViewTbl)
                .whereAnd(Criteria.equals(OldInvoiceHeaderTempView.CustomerUniqueId, customerId.toString())
                        .and(Criteria.greaterThan(OldInvoiceHeaderTempView.RemAmount, 0)))
                .whereAnd(Criteria.equals(OldInvoiceHeaderTempView.DealerId, dealerId));
        return getItems(query);
    }

    public List<OldInvoiceHeaderTempViewModel> getNotSettledInvoices(@NonNull UUID customerId) {
        Query query = new Query();
        query.from(OldInvoiceHeaderTempView.OldInvoiceHeaderTempViewTbl)
                .whereAnd(Criteria.equals(OldInvoiceHeaderTempView.CustomerUniqueId, customerId.toString())
                        .and(Criteria.greaterThan(OldInvoiceHeaderTempView.RemAmount, 0)));
        return getItems(query);
    }
}

