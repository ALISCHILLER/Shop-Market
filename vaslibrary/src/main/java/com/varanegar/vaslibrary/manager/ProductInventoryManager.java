package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.from.From;
import com.varanegar.framework.database.querybuilder.projection.Projection;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.vaslibrary.model.customerinventory.CustomerInventoryView;
import com.varanegar.vaslibrary.model.customerinventory.ProductInventory;
import com.varanegar.vaslibrary.model.customerinventory.ProductInventoryModel;
import com.varanegar.vaslibrary.model.customerinventory.ProductInventoryModelRepository;
import com.varanegar.vaslibrary.model.product.Product;

import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 9/12/2017.
 */

public class ProductInventoryManager extends BaseManager<ProductInventoryModel> {
    public ProductInventoryManager(@NonNull Context context) {
        super(context, new ProductInventoryModelRepository());
    }

    public List<ProductInventoryModel> getLines(UUID customerId) {
        Query query = baseQuery(customerId);
        return getItems(query);
    }

    public static class CustomerStockCheckType {
        public static UUID Boolean = UUID.fromString("80402C83-67CD-42B5-9AB3-34794CEEE724");
        public static UUID Count = UUID.fromString("17715348-B9F9-4E2E-89C7-49A2A32FF3F3");
    }
    private static Query baseQuery(UUID customerId) {
        Query subQuery = new Query();
        subQuery.select(
                Projection.column(Product.UniqueId).as("UniqueId"),
                Projection.column(Product.ProductName).as("ProductName"),
                Projection.column(Product.ProductCode).as("ProductCode"),
                Projection.column(Product.IsCompetitor).as("IsCompetitor"),
                Projection.column(CustomerInventoryView.CustomerId).as("CustomerId"),
                Projection.column(CustomerInventoryView.UniqueId).as("CustomerInventoryId"),
                Projection.column(CustomerInventoryView.Qty).as("Qty"),
                Projection.column(CustomerInventoryView.UnitName).as("UnitName"),
                Projection.column(CustomerInventoryView.TotalQty).as("TotalQty"),
                Projection.column(CustomerInventoryView.IsAvailable).as("IsAvailable"),
                Projection.column(CustomerInventoryView.IsSold).as("IsSold")
        );
        subQuery.from(From.table(Product.ProductTbl)
                .leftJoin(CustomerInventoryView.CustomerInventoryViewTbl)
                .on(Product.UniqueId, CustomerInventoryView.ProductId)
                .onAnd(Criteria.equals(CustomerInventoryView.CustomerId, customerId.toString())))
                .whereAnd(Criteria.equals(Product.IsForCount, true));
        Query query = new Query().from(From.subQuery(subQuery).as("ProductInventory"));
        return query;
    }

    public static Query getAll(UUID customerId) {
        return baseQuery(customerId);
    }

    public static Query getAll(UUID customerId, String str) {
        str = HelperMethods.persian2Arabic(str);
        str = HelperMethods.convertToEnglishNumbers(str);
        Query query = baseQuery(customerId);
        query.whereAnd(Criteria.contains(ProductInventory.ProductCode, str)
                .or(Criteria.contains(ProductInventory.ProductName, str)));
        return query;
    }

    public ProductInventoryModel getLine(UUID productId, UUID customerId) {
        Query query = baseQuery(customerId);
        query.whereAnd(Criteria.equals(ProductInventory.UniqueId, productId.toString()));
        return getItem(query);
    }
}
