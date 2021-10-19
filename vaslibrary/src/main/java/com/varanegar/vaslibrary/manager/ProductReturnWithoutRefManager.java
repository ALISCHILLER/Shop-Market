package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.from.From;
import com.varanegar.framework.database.querybuilder.projection.Projection;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.vaslibrary.model.customer.Customer;
import com.varanegar.vaslibrary.model.customercallreturnview.CustomerCallReturnView;
import com.varanegar.vaslibrary.model.product.Product;
import com.varanegar.vaslibrary.model.productreturn.ProductReturnWithoutRef;
import com.varanegar.vaslibrary.model.productreturn.ProductReturnWithoutRefModel;
import com.varanegar.vaslibrary.model.productreturn.ProductReturnWithoutRefModelRepository;

import java.util.UUID;

/**
 * Created by atp on 4/16/2017.
 */

public class ProductReturnWithoutRefManager extends BaseManager<ProductReturnWithoutRefModel> {
    public ProductReturnWithoutRefManager(Context context) {
        super(context, new ProductReturnWithoutRefModelRepository());
    }

    /**
     * @param customerId
     * @return
     */
    private static Query baseQuery(UUID customerId) {
        Query subQuery = new Query();
        subQuery.select(
                Projection.column(Product.UniqueId).as("UniqueId"),
                Projection.column(Product.ProductName).as("ProductName"),
                Projection.column(Product.ProductCode).as("ProductCode"),
                Projection.column(Product.ProductGroupId).as("ProductGroupId"),
                Projection.column(Product.IsForReturnWithRef).as("IsForReturnWithRef"),
                Projection.column(Product.IsForReturnWithOutRef).as("IsForReturnWithOutRef"),
                Projection.column(Customer.UniqueId).as("CustomerUniqueId"),
                Projection.column(Customer.CustomerName).as("CustomerName"),
                Projection.column(CustomerCallReturnView.TotalReturnQty).as("TotalQty"),
                Projection.column(CustomerCallReturnView.TotalRequestAmount).as("TotalRequestAmount"),
                Projection.column(CustomerCallReturnView.InvoiceId).as("InvoiceId"),
                Projection.column(CustomerCallReturnView.Qty).as("Qty"),
                Projection.column(CustomerCallReturnView.UnitName).as("UnitName"));
        subQuery.from(From.table(Product.ProductTbl).leftJoin(Customer.CustomerTbl)
                .on(Criteria.equals(Customer.UniqueId, customerId.toString()))
                .leftJoin(CustomerCallReturnView.CustomerCallReturnViewTbl)
                .on(Product.UniqueId, CustomerCallReturnView.ProductId)
                .onAnd(Criteria.equals(CustomerCallReturnView.CustomerUniqueId, customerId.toString())
                        .and(Criteria.isNull(CustomerCallReturnView.InvoiceId))));
        subQuery.whereAnd(Criteria.equals(Product.IsForReturnWithRef, true))
                .whereAnd(Criteria.equals(Product.IsActive, true));
        Query query = new Query().from(From.subQuery(subQuery).as("ProductReturnWithoutRef"));
        return query;

    }

    public static Query getAll(@NonNull UUID customerId) {
        Query query = baseQuery(customerId);
        return query;
    }

    public ProductReturnWithoutRefModel getLine(@NonNull UUID customerId, @NonNull UUID productId) {
        Query query = baseQuery(customerId);
        query.whereAnd(Criteria.equals(ProductReturnWithoutRef.CustomerUniqueId, customerId.toString())
                .and(Criteria.equals(ProductReturnWithoutRef.UniqueId, productId.toString())));
        return getItem(query);
    }

    public static Query getAll(@NonNull String productNameOrCode, @NonNull UUID customerId) {
        productNameOrCode = HelperMethods.persian2Arabic(productNameOrCode);
        productNameOrCode = HelperMethods.convertToEnglishNumbers(productNameOrCode);
        Query query = baseQuery(customerId);
        query.whereAnd(Criteria.contains(ProductReturnWithoutRef.ProductName, productNameOrCode)
                .or(Criteria.contains(ProductReturnWithoutRef.ProductCode, productNameOrCode)));
        return query;
    }

    public static Query getItemsOfSubGroups(@NonNull UUID[] groupIds, @NonNull UUID customerId) {
        Query query = baseQuery(customerId);
        String[] ids = new String[groupIds.length];
        for (int i = 0; i < groupIds.length; i++) {
            ids[i] = groupIds[i].toString();
        }
        query.whereAnd(Criteria.in(ProductReturnWithoutRef.ProductGroupId, ids));
        return query;
    }

    public static Query getItemsOfSubGroup(@NonNull UUID groupId, @NonNull UUID customerId) {
        Query query = baseQuery(customerId);
        query.whereAnd(Criteria.equals(ProductReturnWithoutRef.ProductGroupId, groupId.toString()));
        return query;
    }
}
