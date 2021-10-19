package com.varanegar.vaslibrary.manager.customercallreturnview;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.projection.Projection;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.model.customercallreturnview.CustomerCallReturnView;
import com.varanegar.vaslibrary.model.customercallreturnview.CustomerCallReturnViewModel;
import com.varanegar.vaslibrary.model.customercallreturnview.CustomerCallReturnViewModelRepository;

import java.util.List;
import java.util.UUID;

/**
 * Created by atp on 4/11/2017.
 */

public class CustomerCallReturnViewManager extends BaseManager<CustomerCallReturnViewModel> {
    public CustomerCallReturnViewManager(Context context) {
        super(context, new CustomerCallReturnViewModelRepository());
    }


    public static Query getLines(@NonNull UUID customerId, @Nullable Boolean isRef, @Nullable Boolean isFromRequest) {
        Query query = new Query();
        if (isRef == null) {
            if (isFromRequest == null)
                query = new Query().from(CustomerCallReturnView.CustomerCallReturnViewTbl)
                        .whereAnd(Criteria.equals(CustomerCallReturnView.CustomerUniqueId, customerId.toString()));
        } else {
            if (isRef)
                query = new Query().from(CustomerCallReturnView.CustomerCallReturnViewTbl)
                        .whereAnd(Criteria.equals(CustomerCallReturnView.CustomerUniqueId, customerId.toString())
                                .and(Criteria.notEquals(CustomerCallReturnView.InvoiceId, null)));
            else
                query = new Query().from(CustomerCallReturnView.CustomerCallReturnViewTbl)
                        .whereAnd(Criteria.equals(CustomerCallReturnView.CustomerUniqueId, customerId.toString())
                                .and(Criteria.equals(CustomerCallReturnView.InvoiceId, null)));
        }
        if (isFromRequest != null)
            query.whereAnd(Criteria.equals(CustomerCallReturnView.IsFromRequest, isFromRequest));
        return query;
    }

    public CustomerCallReturnViewModel getLine(UUID customerId, UUID productId, @Nullable UUID invoiceId, @Nullable Boolean isFromRequest) {
        Query query;
        if (invoiceId != null)
            query = new Query().from(CustomerCallReturnView.CustomerCallReturnViewTbl)
                    .whereAnd(Criteria.equals(CustomerCallReturnView.CustomerUniqueId, customerId.toString())
                            .and(Criteria.equals(CustomerCallReturnView.ProductId, productId.toString())
                                    .and(Criteria.equals(CustomerCallReturnView.InvoiceId, invoiceId.toString()))));
        else
            query = new Query().from(CustomerCallReturnView.CustomerCallReturnViewTbl)
                    .whereAnd(Criteria.equals(CustomerCallReturnView.CustomerUniqueId, customerId.toString())
                            .and(Criteria.equals(CustomerCallReturnView.ProductId, productId.toString())
                                    .and(Criteria.equals(CustomerCallReturnView.InvoiceId, null))));

        if (isFromRequest != null)
            query.whereAnd(Criteria.equals(CustomerCallReturnView.IsFromRequest, isFromRequest));
        return getItem(query);
    }

    public List<CustomerCallReturnViewModel> getCustomerCallReturns(UUID customerUniqueId, @Nullable Boolean withRef, @Nullable Boolean isFromRequest) {
        Query query = new Query();
        query.from(CustomerCallReturnView.CustomerCallReturnViewTbl).whereAnd(Criteria.equals(CustomerCallReturnView.CustomerUniqueId, customerUniqueId.toString()));
        if (isFromRequest != null)
            query.whereAnd(Criteria.equals(CustomerCallReturnView.IsFromRequest, isFromRequest));

        if (withRef != null) {
            if (withRef)
                query = query.whereAnd(Criteria.notEquals(CustomerCallReturnView.InvoiceId, null));
            else
                query = query.whereAnd(Criteria.equals(CustomerCallReturnView.InvoiceId, null));
        }

        return getItems(query);
    }

    @Nullable
    public CustomerCallReturnViewModel getCustomerCallReturn(@NonNull UUID returnId) {
        return getItem(new Query().from(CustomerCallReturnView.CustomerCallReturnViewTbl).whereAnd(Criteria.equals(CustomerCallReturnView.ReturnUniqueId,returnId.toString())));
    }
}
