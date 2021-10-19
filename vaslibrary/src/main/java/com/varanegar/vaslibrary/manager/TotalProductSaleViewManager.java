package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.from.From;
import com.varanegar.framework.database.querybuilder.projection.Projection;
import com.varanegar.vaslibrary.model.customeroldInvoice.CustomerOldInvoiceDetail;
import com.varanegar.vaslibrary.model.customeroldInvoice.CustomerOldInvoiceHeader;
import com.varanegar.vaslibrary.model.totalproductsaleview.TotalProductSaleViewModel;
import com.varanegar.vaslibrary.model.totalproductsaleview.TotalProductSaleViewModelRepository;

import java.util.UUID;

/**
 * Created by A.Torabi on 9/28/2017.
 */

public class TotalProductSaleViewManager extends BaseManager<TotalProductSaleViewModel> {
    public TotalProductSaleViewManager(@NonNull Context context) {
        super(context, new TotalProductSaleViewModelRepository());
    }

    /**
     * SELECT sum(ifnull(CustomerOldInvoiceDetail.TotalQty,0)) AS TotalQty,
     * CustomerOldInvoiceDetail.ProductId,
     * CustomerOldInvoiceDetail.ProductId AS UniqueId,
     * CustomerOldInvoiceHeader.CustomerId,
     * count(*) AS InvoiceCount
     * FROM
     * "CustomerOldInvoiceDetail"
     * JOIN CustomerOldInvoiceHeader ON CustomerOldInvoiceDetail.SaleId = CustomerOldInvoiceHeader.UniqueId
     * AND CustomerOldInvoiceHeader.CustomerId = 'feaa1b4c-5bb2-4059-a2af-72eb57797ac5'
     * GROUP BY
     * ProductId
     *
     * @return
     */
    public static Query baseQuery(UUID customerId) {
        Query subQuery = new Query();
        subQuery.select(
                Projection.sum(
                        Projection.ifNull(Projection.column(CustomerOldInvoiceDetail.TotalQty), 0)
                ).as("TotalQty"),
                Projection.column(CustomerOldInvoiceDetail.ProductId).as("ProductId"),
                Projection.column(CustomerOldInvoiceDetail.ProductId).as("UniqueId"),
                Projection.column(CustomerOldInvoiceHeader.CustomerId).as("CustomerId"),
                Projection.count(CustomerOldInvoiceDetail.All).as("InvoiceCount"));
        subQuery.from(From.table(CustomerOldInvoiceDetail.CustomerOldInvoiceDetailTbl)
                .innerJoin(CustomerOldInvoiceHeader.CustomerOldInvoiceHeaderTbl)
                .on(CustomerOldInvoiceDetail.SaleId, CustomerOldInvoiceHeader.UniqueId)
                .onAnd(Criteria.equals(CustomerOldInvoiceHeader.CustomerId, customerId.toString())));
        subQuery.groupBy(CustomerOldInvoiceDetail.ProductId);
        Query query = new Query().from(From.subQuery(subQuery).as("TotalProductSaleView"));
        return query;
    }
}
