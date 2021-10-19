package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.from.From;
import com.varanegar.framework.database.querybuilder.projection.Projection;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;
import com.varanegar.vaslibrary.model.customeremphaticproduct.CustomerEmphaticProduct;
import com.varanegar.vaslibrary.model.productorderqtyhistoryview.CustomerProductOrderQtyHistoryModel;
import com.varanegar.vaslibrary.model.productorderqtyhistoryview.CustomerProductOrderQtyHistoryModelRepository;
import com.varanegar.vaslibrary.model.productorderqtyhistoryview.ProductOrderQtyHistoryView;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Torabi on 9/13/2017.
 */

public class CustomerProductOrderQtyHistoryManager extends BaseManager<CustomerProductOrderQtyHistoryModel> {
    public CustomerProductOrderQtyHistoryManager(@NonNull Context context) {
        super(context, new CustomerProductOrderQtyHistoryModelRepository());
    }

    /**
     * SELECT	ld.ProductId,	ld.TotalQty AS LdQty,	sd.TotalQty AS SdQty
     * FROM
     * ( SELECT sum(TotalQty) AS TotalQty, ProductId
     * FROM ProductSaleQtyHistory
     * WHERE CustomerId = '5098170e-07c5-40f7-b09f-235a5b81458a' AND SaleDate BETWEEN '92-06-04' AND '95-06-04'
     * GROUP BY ProductId) ld
     * LEFT JOIN
     * ( SELECT sum(TotalQty) AS TotalQty, ProductId
     * FROM ProductSaleQtyHistory
     * WHERE CustomerId = '5098170e-07c5-40f7-b09f-235a5b81458a' AND SaleDate BETWEEN '92-06-04' AND '94-06-04'
     * GROUP BY ProductId ) sd
     * ON sd.ProductId = ld.ProductId
     *
     * @param customerId
     * @return
     */
    private static Query baseQuery(UUID customerId) {
        Query dangerQuery = new Query()
                .select(
                        Projection.sum(ProductOrderQtyHistoryView.TotalQty).as("TotalQty"),
                        Projection.column(CustomerEmphaticProduct.ProductId).as("ProductId"),
                        Projection.column(CustomerEmphaticProduct.CustomerId).as("CustomerId"))
                .whereAnd(Criteria.equals(CustomerEmphaticProduct.CustomerId, customerId.toString()))
                .groupBy(CustomerEmphaticProduct.ProductId)
                .from(From.table(CustomerEmphaticProduct.CustomerEmphaticProductTbl)
                        .leftJoin(ProductOrderQtyHistoryView.ProductOrderQtyHistoryViewTbl)
                        .on(CustomerEmphaticProduct.ProductId, ProductOrderQtyHistoryView.ProductId)
                        .onAnd(Criteria.equals(ProductOrderQtyHistoryView.CustomerId, customerId.toString()))
                        .onAnd(Criteria.greaterThan(ProductOrderQtyHistoryView.SaleDate, CustomerEmphaticProduct.DangerDate)));

        Query warningQuery = new Query()
                .select(
                        Projection.sum(ProductOrderQtyHistoryView.TotalQty).as("TotalQty"),
                        Projection.column(CustomerEmphaticProduct.ProductId).as("ProductId"),
                        Projection.column(CustomerEmphaticProduct.CustomerId).as("CustomerId"))
                .whereAnd(Criteria.equals(CustomerEmphaticProduct.CustomerId, customerId.toString()))
                .groupBy(CustomerEmphaticProduct.ProductId)
                .from(From.table(CustomerEmphaticProduct.CustomerEmphaticProductTbl)
                        .leftJoin(ProductOrderQtyHistoryView.ProductOrderQtyHistoryViewTbl)
                        .on(CustomerEmphaticProduct.ProductId, ProductOrderQtyHistoryView.ProductId)
                        .onAnd(Criteria.equals(ProductOrderQtyHistoryView.CustomerId, customerId.toString()))
                        .onAnd(Criteria.greaterThan(ProductOrderQtyHistoryView.SaleDate, CustomerEmphaticProduct.WarningDate)));

        Query query = new Query()
                .select(
                        Projection.column(DangerQty.CustomerId).as("CustomerId"),
                        Projection.column(DangerQty.ProductId).as("ProductId"),
                        Projection.ifNull(Projection.column(WarningQty.TotalQty), 0).as("WarningQty"),
                        Projection.ifNull(Projection.column(DangerQty.TotalQty), 0).as("DangerQty")
                )
                .from(From.subQuery(dangerQuery).as("DangerQty").leftJoin(From.subQuery(warningQuery).as("WarningQty"))
                        .on(DangerQty.ProductId, WarningQty.ProductId));
        Query query2 = new Query().from(From.subQuery(query).as("ProductOrderQtyHistory"));
        return query2;
    }

    @Table
    static class WarningQtyModel extends BaseModel {
        @Column
        public BigDecimal TotalQty;
        @Column
        public UUID ProductId;
        @Column
        public UUID CustomerId;

    }

    @Table
    static class DangerQtyModel extends BaseModel {
        @Column
        public BigDecimal TotalQty;
        @Column
        public UUID ProductId;
        @Column
        public UUID CustomerId;
    }

    public void calculate(final UUID customerId) throws DbException, ValidationException {
        final List<CustomerProductOrderQtyHistoryModel> items = getItems(baseQuery(customerId));
        Linq.forEach(items, new Linq.Consumer<CustomerProductOrderQtyHistoryModel>() {
            @Override
            public void run(CustomerProductOrderQtyHistoryModel item) {
                item.UniqueId = UUID.randomUUID();
            }
        });
        deleteAll();
        if (items.size() > 0) {
            insert(items);
            Timber.i("Customer Product Order Qty History calculated for customer id = " + customerId.toString());
        }
    }


}
