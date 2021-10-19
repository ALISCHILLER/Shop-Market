package com.varanegar.vaslibrary.manager.oldinvoicemanager;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.from.JoinFrom;
import com.varanegar.framework.database.querybuilder.projection.Projection;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.model.customeroldInvoice.CustomerOldInvoiceDetail;
import com.varanegar.vaslibrary.model.customeroldInvoice.CustomerOldInvoiceDetailModel;
import com.varanegar.vaslibrary.model.customeroldInvoice.CustomerOldInvoiceDetailModelRepository;
import com.varanegar.vaslibrary.model.customeroldInvoice.CustomerOldInvoiceHeader;

import java.util.Date;
import java.util.List;
import java.util.UUID;


/**
 * Created by atp on 4/5/2017.
 */

public class CustomerOldInvoiceDetailManager extends BaseManager<CustomerOldInvoiceDetailModel> {
    public CustomerOldInvoiceDetailManager(Context context) {
        super(context, new CustomerOldInvoiceDetailModelRepository());
    }

    public static Query getItems(UUID invoiceId) {
        Query query = new Query();
        query.from(CustomerOldInvoiceDetail.CustomerOldInvoiceDetailTbl).whereAnd(Criteria.equals(CustomerOldInvoiceDetail.SaleId, invoiceId.toString()));
        return query;
    }

    @Nullable
    public Currency getNetAmountInPeriod(Date startTime, Date endTime) {
        Query query = new Query();
        query.select(Projection.sum(CustomerOldInvoiceDetail.AmountNut))
                .from(JoinFrom.table(CustomerOldInvoiceDetail.CustomerOldInvoiceDetailTbl)
                        .innerJoin(CustomerOldInvoiceHeader.CustomerOldInvoiceHeaderTbl)
                        .on(CustomerOldInvoiceDetail.SaleId, CustomerOldInvoiceHeader.UniqueId)
                        .onAnd(Criteria.between(CustomerOldInvoiceHeader.SaleDate, startTime, endTime)));
        return VaranegarApplication.getInstance().getDbHandler().getCurrencySingle(query);
    }

    @Nullable
    public Integer getItemsInPeriod(Date startTime, Date endTime) {
        Query query = new Query();
        query.select(Projection.countRows())
                .from(JoinFrom.table(CustomerOldInvoiceDetail.CustomerOldInvoiceDetailTbl)
                        .innerJoin(CustomerOldInvoiceHeader.CustomerOldInvoiceHeaderTbl)
                        .on(CustomerOldInvoiceDetail.SaleId, CustomerOldInvoiceHeader.UniqueId)
                        .onAnd(Criteria.between(CustomerOldInvoiceHeader.SaleDate, startTime, endTime)));
        return VaranegarApplication.getInstance().getDbHandler().getIntegerSingle(query);
    }

    public static Query getSoldItems(@NonNull UUID customerId) {
        Query query = new Query();
        query.distinct().select(CustomerOldInvoiceDetail.ProductId)
                .from(JoinFrom.table(CustomerOldInvoiceDetail.CustomerOldInvoiceDetailTbl)
                        .innerJoin(CustomerOldInvoiceHeader.CustomerOldInvoiceHeaderTbl)
                        .on(CustomerOldInvoiceDetail.SaleId, CustomerOldInvoiceHeader.UniqueId))
                .whereAnd(Criteria.equals(CustomerOldInvoiceHeader.CustomerId, customerId))
                .whereAnd(Criteria.isNull(CustomerOldInvoiceDetail.PrizeType).or(Criteria.equals(CustomerOldInvoiceDetail.PrizeType, 0)))
                .whereAnd(Criteria.isNull(CustomerOldInvoiceDetail.FreeReasonId).or(Criteria.equals(CustomerOldInvoiceDetail.FreeReasonId, 0)));
        return query;
    }

    public List<CustomerOldInvoiceDetailModel> getOldInvoiceItems(UUID saleId) {
        return getItems(new Query().from(CustomerOldInvoiceDetail.CustomerOldInvoiceDetailTbl).whereAnd(Criteria.equals(CustomerOldInvoiceDetail.SaleId , saleId)));
    }
}