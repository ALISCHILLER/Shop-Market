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
import com.varanegar.vaslibrary.model.customercallreturnview.CustomerCallReturnAfterDiscountView;
import com.varanegar.vaslibrary.model.customercallreturnview.CustomerCallReturnAfterDiscountViewModel;
import com.varanegar.vaslibrary.model.customercallreturnview.CustomerCallReturnAfterDiscountViewModelRepository;

import java.util.UUID;

/**
 * Created by atp on 4/11/2017.
 */

public class CustomerCallReturnAfterDiscountViewManager extends BaseManager<CustomerCallReturnAfterDiscountViewModel> {
    public CustomerCallReturnAfterDiscountViewManager(Context context) {
        super(context, new CustomerCallReturnAfterDiscountViewModelRepository());
    }

    public static Query getLines(@NonNull UUID customerId, @Nullable Boolean isRef, @Nullable Boolean isFromRequest , boolean returnQty) {
        Query query = new Query();
        if (isRef == null) {
            if (isFromRequest == null)
                query = new Query().from(CustomerCallReturnAfterDiscountView.CustomerCallReturnAfterDiscountViewTbl)
                        .whereAnd(Criteria.equals(CustomerCallReturnAfterDiscountView.CustomerUniqueId, customerId.toString()));
        } else {
            if (isRef)
                query = new Query().from(CustomerCallReturnAfterDiscountView.CustomerCallReturnAfterDiscountViewTbl)
                        .whereAnd(Criteria.equals(CustomerCallReturnAfterDiscountView.CustomerUniqueId, customerId.toString())
                                .and(Criteria.notEquals(CustomerCallReturnAfterDiscountView.InvoiceId, null)));
            else
                query = new Query().from(CustomerCallReturnAfterDiscountView.CustomerCallReturnAfterDiscountViewTbl)
                        .whereAnd(Criteria.equals(CustomerCallReturnAfterDiscountView.CustomerUniqueId, customerId.toString())
                                .and(Criteria.equals(CustomerCallReturnAfterDiscountView.InvoiceId, null)));
        }
        if (isFromRequest != null)
            query.whereAnd(Criteria.equals(CustomerCallReturnAfterDiscountView.IsFromRequest, isFromRequest));

        if (returnQty)
            query.whereAnd(Criteria.greaterThan(CustomerCallReturnAfterDiscountView.TotalReturnQty, 0));

        return query;
    }

    public Currency getTotalAmount(@NonNull UUID customerId, @Nullable Boolean isFromRequest) {
        Query query = new Query().from(CustomerCallReturnAfterDiscountView.CustomerCallReturnAfterDiscountViewTbl)
                .whereAnd(Criteria.equals(CustomerCallReturnAfterDiscountView.CustomerUniqueId, customerId.toString()))
                .select(Projection.sum(CustomerCallReturnAfterDiscountView.TotalRequestNetAmount));
        if (isFromRequest != null)
            query.whereAnd(Criteria.equals(CustomerCallReturnAfterDiscountView.IsFromRequest, isFromRequest));

        Currency currency = VaranegarApplication.getInstance().getDbHandler().getCurrencySingle(query);
        if (currency == null)
            return Currency.ZERO;
        else
            return currency;
    }


}
