package com.varanegar.vaslibrary.manager.productorderviewmanager;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.from.From;
import com.varanegar.framework.database.querybuilder.from.JoinFrom;
import com.varanegar.framework.database.querybuilder.projection.Projection;
import com.varanegar.framework.database.querybuilder.projection.When;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.customercall.CustomerCallOrderManager;
import com.varanegar.vaslibrary.manager.oldinvoicemanager.CustomerOldInvoiceDetailManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.ProductBatchView.ProductBatchView;
import com.varanegar.vaslibrary.model.call.CustomerCallOrderModel;
import com.varanegar.vaslibrary.model.catalog.Catalog;
import com.varanegar.vaslibrary.model.customer.Customer;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.OrderView;
import com.varanegar.vaslibrary.model.customeremphaticproduct.CustomerEmphaticProduct;
import com.varanegar.vaslibrary.model.customeremphaticproduct.EmphasisType;
import com.varanegar.vaslibrary.model.customerinventory.CustomerInventoryView;
import com.varanegar.vaslibrary.model.customerprice.CustomerPrice;
import com.varanegar.vaslibrary.model.customerproductprize.CustomerProductPrize;
import com.varanegar.vaslibrary.model.onhandqty.OnHandQty;
import com.varanegar.vaslibrary.model.onhandqty.OnHandQtyStock;
import com.varanegar.vaslibrary.model.product.Product;
import com.varanegar.vaslibrary.model.productorderqtyhistoryview.CustomerProductOrderQtyHistory;
import com.varanegar.vaslibrary.model.productorderview.ProductOrderView;
import com.varanegar.vaslibrary.model.productorderview.ProductOrderViewModel;
import com.varanegar.vaslibrary.model.productorderview.ProductOrderViewModelRepository;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.model.totalproductsaleview.CustomerTotalProductSale;
import com.varanegar.vaslibrary.model.totalqtyview.TotalQtyView;
import com.varanegar.vaslibrary.ui.calculator.BaseUnit;
import com.varanegar.vaslibrary.ui.calculator.DiscreteUnit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by s.foroughi on 11/01/2017.
 */

public class ProductOrderViewManager extends BaseManager<ProductOrderViewModel> {
    public ProductOrderViewManager(Context context) {
        super(context, new ProductOrderViewModelRepository());
    }

    public static void checkOnHandQty(Context context, OnHandQtyStock onHandQtyStock,
                                      List<DiscreteUnit> discreteUnits, BaseUnit bulkUnit)
            throws InventoryError, AllocationError, OnHandQtyWarning {
        BigDecimal total = BigDecimal.ZERO;
        if (discreteUnits.size() > 0) {
            for (DiscreteUnit discreteUnit :
                    discreteUnits) {
                BigDecimal qty = new BigDecimal(discreteUnit.value * discreteUnit.ConvertFactor);
                total = total.add(qty);
            }
        } else if (discreteUnits.size() == 0 && bulkUnit != null)
            total = bulkUnit.getQty();
        if (onHandQtyStock.ProductTotalOrderedQty == null)
            onHandQtyStock.ProductTotalOrderedQty = BigDecimal.ZERO;
        if (onHandQtyStock.OnHandQty == null)
            onHandQtyStock.OnHandQty = BigDecimal.ZERO;
        if (onHandQtyStock.RemainedAfterReservedQty == null)
            onHandQtyStock.RemainedAfterReservedQty = onHandQtyStock.OnHandQty;
        if (onHandQtyStock.OrderPoint == null)
            onHandQtyStock.OrderPoint = BigDecimal.ZERO;
        if (onHandQtyStock.TotalQty == null)
            onHandQtyStock.TotalQty = BigDecimal.ZERO;
        SysConfigManager sysConfigManager = new SysConfigManager(context);
        SysConfigModel applyCurrentOrdersInInventory = sysConfigManager.read(ConfigKey.ApplyCurrentOrdersInInventory, SysConfigManager.cloud);
        if (SysConfigManager.compare(applyCurrentOrdersInInventory, true) || !VaranegarApplication.is(VaranegarApplication.AppId.PreSales))
            total = total.add(onHandQtyStock.ProductTotalOrderedQty).subtract(onHandQtyStock.TotalQty);
        if (VaranegarApplication.is(VaranegarApplication.AppId.HotSales)) {
            if (onHandQtyStock.OnHandQty.subtract(total).compareTo(onHandQtyStock.OrderPoint) < 0) {
                throw new InventoryError(context.getString(R.string.stock_level_is_not_enough));
            }
        } else if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {

            SysConfigModel sysConfigModel = sysConfigManager.read(ConfigKey.InventoryControl,
                    SysConfigManager.cloud);
            if (SysConfigManager.compare(sysConfigModel, true) &&
                    onHandQtyStock.OnHandQty.subtract(total).compareTo(onHandQtyStock.OrderPoint) < 0) {
                throw new InventoryError(context.getString(R.string.stock_level_is_not_enough));
            }
            if (onHandQtyStock.HasAllocation && onHandQtyStock.OnHandQty.subtract(total).compareTo(BigDecimal.ZERO) < 0) {
                throw new AllocationError(context.getString(R.string.dealer_qty_error) + " " + onHandQtyStock.OnHandQty);
            }

            SysConfigModel showInventoryMinusUnconfirmedRequests = sysConfigManager.read(ConfigKey.ShowInventoryMinusUnconfirmedRequests,
                    SysConfigManager.cloud);
            if (SysConfigManager.compare(sysConfigModel, true) && SysConfigManager.compare(showInventoryMinusUnconfirmedRequests, true)) {
                if (onHandQtyStock.RemainedAfterReservedQty.subtract(total).compareTo(onHandQtyStock.OrderPoint) < 0) {
                    throw new OnHandQtyWarning(context.getString(R.string.reserved_qty_error) + "\n" + context.getString(R.string.onhand_qty) + " " + context.getString(R.string.onhand_qty_after_reserve) + " = " + VasHelperMethods.bigDecimalToString(onHandQtyStock.RemainedAfterReservedQty));
                }
            }
        }
    }

    @Table
    static class TotalProductOrderQtyViewModel extends BaseModel {
        @Column
        public BigDecimal TotalQty;
        @Column
        public UUID ProductId;

    }

    private static Query baseQuery(@NonNull UUID customerId, @Nullable
            UUID orderId, @Nullable OrderBy orderBy, boolean freeRequestItems,
                                   @Nullable Boolean inStock) {
        Query subQuery = new Query();
        Projection p = Projection.subtract(
                Projection.add(
                        Projection.column(OrderView.TotalQty),
                        Projection.add(
                                Projection.subtract(CustomerProductOrderQtyHistory.WarningQty, CustomerEmphaticProduct.ProductCount),
                                Projection.add(CustomerProductOrderQtyHistory.WarningQty, CustomerProductOrderQtyHistory.DangerQty))
                ),
                Projection.column(CustomerEmphaticProduct.ProductCount));

        Projection remainedQty = Projection.add(Projection.column(OrderView.TotalQty),
                Projection.subtract(Projection.column(OnHandQty.OnHandQty), Projection.column(TotalProductOrderQtyView.TotalQty))).castAsReal();
        subQuery.select(
                Projection.add(
                        Projection.multiply(CustomerEmphaticProduct.Type, 10),
                        Projection.caseWhen(
                                When.When(Criteria.greaterThan(p, 0)).then(1),
                                When.When(Criteria.equals(p, 0)).then(2),
                                When.When(Criteria.lesserThan(p, 0)).then(3)))
                        .as("EmphaticPriority"),
                Projection.column(Product.UniqueId).as("UniqueId"),
                Projection.column(Product.IsForSale).as("IsForSale"),
                Projection.column(Product.ProductName).as("ProductName"),
                Projection.column(Product.ProductCode).as("ProductCode"),
                Projection.column(Product.ProductGroupId).as("ProductGroupId"),
                Projection.column(Product.IsFreeItem).as("IsFreeItem"),
                Projection.column(Product.OrderPoint).as("OrderPoint"),
                Projection.column(Product.PayDuration).as("PayDuration"),
                Projection.column(Customer.UniqueId).as("CustomerId"),
                Projection.column(OrderView.TotalQty).as("TotalQty"),
                Projection.column(OrderView.Qty).as("Qty"),
                Projection.column(OrderView.UnitName).as("UnitName"),
                Projection.column(OrderView.ProductUnitId).as("ProductUnitId"),
                Projection.column(OrderView.RequestAmount).as("RequestAmount"),
                Projection.column(OrderView.IsRequestFreeItem).as("IsRequestFreeItem"),
                Projection.column(OrderView.ConvertFactor).as("ConvertFactor"),
                Projection.column(OrderView.RequestBulkQty).as("RequestBulkQty"),
                Projection.column(OrderView.TotalQtyBulk).as("TotalQtyBulk"),
                Projection.column(OrderView.Description).as("Description"),
                Projection.column(OrderView.UniqueId).as("OrderLineId"),
                Projection.column(CustomerPrice.Price).as("Price"),
                Projection.column(CustomerPrice.PriceId).as("PriceId"),
                Projection.column(CustomerPrice.UserPrice).as("UserPrice"),
                Projection.ifNull(Projection.column(CustomerEmphaticProduct.Type), EmphasisType.NotEmphatic.ordinal()).as("EmphaticType"),
                Projection.column(CustomerEmphaticProduct.ProductCount).as("EmphaticProductCount"),
                Projection.column(OnHandQty.OnHandQty).as("OnHandQty"),
                Projection.column(OnHandQty.HasAllocation).as("HasAllocation"),
                Projection.subtract(
                        Projection.column(OnHandQty.OnHandQty),
                        Projection.caseWhen(
                                When.When(
                                        Criteria.greaterThanOrEqual(OnHandQty.OnHandQty, OnHandQty.ReservedQty))
                                        .then(Projection.column(OnHandQty.ReservedQty)),
                                When.When(
                                        Criteria.lesserThan(OnHandQty.OnHandQty, OnHandQty.ReservedQty))
                                        .then(Projection.column(OnHandQty.OnHandQty))))
                        .as("RemainedAfterReservedQty"),
                remainedQty.as("RemainedQty"),
                Projection.divide(
                        CustomerTotalProductSale.TotalQty,
                        CustomerTotalProductSale.InvoiceCount).as("Average"),
                Projection.column(CustomerTotalProductSale.InvoiceCount).as("InvoiceCount"),
                Projection.column(CustomerProductOrderQtyHistory.DangerQty).as("DangerQty"),
                Projection.column(CustomerProductOrderQtyHistory.WarningQty).as("WarningQty"),
                Projection.column(CustomerProductOrderQtyHistory.WarningQty).as("WarningQty"),
                Projection.column(TotalProductOrderQtyView.TotalQty).as("ProductTotalOrderedQty"),
                Projection.column(CustomerProductPrize.Comment).as("PrizeComment"),
                Projection.column(CustomerInventoryView.IsAvailable).as("CustomerInventoryIsAvailable"),
                Projection.column(CustomerInventoryView.TotalQty).as("CustomerInventoryTotalQty"),
                Projection.column(ProductBatchView.BatchNo).as("BatchNo"),
                Projection.column(ProductBatchView.ExpDate).as("ExpDate"),
                Projection.column(ProductBatchView.OnHandQty).as("BatchOnHandQty"),
                Projection.column(ProductBatchView.ItemRef).as("ItemRef"),
                Projection.column(ProductBatchView.BatchRef).as("BatchRef"),
                Projection.groupConcat(Catalog.CatalogId, ":").as("CatalogId"),
                Projection.column(Catalog.OrderOf).as("CatalogOrderOf"));

        JoinFrom from = From.table(Product.ProductTbl).leftJoin(Customer.CustomerTbl)
                .on(Criteria.equals(Customer.UniqueId, customerId.toString()))
                .leftJoin(OrderView.OrderViewTbl)
                .on(Product.UniqueId, OrderView.ProductId)
                .onAnd(Criteria.equals(OrderView.CustomerUniqueId, customerId.toString()))
                .onAnd(Criteria.equals(OrderView.IsRequestFreeItem, freeRequestItems));

        if (orderId != null)
            from = from.onAnd(Criteria.equals(OrderView.OrderUniqueId, orderId.toString()));

        if (orderId != null)
            from = from.innerJoin(CustomerPrice.CustomerPriceTbl)
                    .on(Product.UniqueId, CustomerPrice.ProductUniqueId)
                    .onAnd(Criteria.equals(CustomerPrice.CallOrderId, orderId.toString()));
        else
            from = from.innerJoin(CustomerPrice.CustomerPriceTbl)
                    .on(Product.UniqueId, CustomerPrice.ProductUniqueId)
                    .onAnd(Criteria.isNull(CustomerPrice.CallOrderId));

        from = from.leftJoin(CustomerEmphaticProduct.CustomerEmphaticProductTbl)
                .on(CustomerEmphaticProduct.ProductId, Product.UniqueId);

        if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales))
            from = from.leftJoin(OnHandQty.OnHandQtyTbl)
                    .on(OnHandQty.ProductId, Product.UniqueId);
        else
            from = from.innerJoin(OnHandQty.OnHandQtyTbl)
                    .on(OnHandQty.ProductId, Product.UniqueId);

        from = from.leftJoin(TotalProductOrderQtyView.TotalProductOrderQtyViewTbl)
                .on(TotalProductOrderQtyView.ProductId, Product.UniqueId)
                .leftJoin(CustomerProductOrderQtyHistory.CustomerProductOrderQtyHistoryTbl)
                .on(Product.UniqueId, CustomerProductOrderQtyHistory.ProductId)
                .onAnd(Criteria.equals(CustomerProductOrderQtyHistory.CustomerId, customerId.toString()))
                .leftJoin(CustomerInventoryView.CustomerInventoryViewTbl)
                .on(CustomerInventoryView.ProductId, Product.UniqueId)
                .onAnd(Criteria.equals(CustomerInventoryView.CustomerId, customerId.toString()))
                .leftJoin(CustomerProductPrize.CustomerProductPrizeTbl)
                .on(CustomerProductPrize.ProductId, Product.UniqueId)
                .onAnd(Criteria.equals(CustomerProductPrize.CustomerId, customerId.toString()))
                .leftJoin(CustomerTotalProductSale.CustomerTotalProductSaleTbl)
                .on(CustomerTotalProductSale.ProductId, Product.UniqueId)
                .onAnd(Criteria.equals(CustomerTotalProductSale.CustomerId, customerId.toString()))
                .leftJoin(Catalog.CatalogTbl)
                .on(Product.UniqueId, Catalog.ProductId)
                .leftJoin(ProductBatchView.ProductBatchViewTbl)
                .on(Product.UniqueId, ProductBatchView.ProductId);

        subQuery.from(from);
        subQuery.whereAnd(Criteria.equals(Product.IsForSale, true))
                .whereAnd(Criteria.equals(Product.IsActive, true));
        if (inStock != null) {
            if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
                if (inStock)
                    subQuery.whereAnd(Criteria.greaterThan(OnHandQty.OnHandQty, 0));
                else
                    subQuery.whereAnd(Criteria.lesserThanOrEqual(OnHandQty.OnHandQty, 0));
            } else {
                if (inStock)
                    subQuery.whereAnd(Criteria.greaterThan(remainedQty, 0));
                else
                    subQuery.whereAnd(Criteria.lesserThanOrEqual(remainedQty, 0));
            }
        }
        if (freeRequestItems)
            subQuery.whereAnd(Criteria.equals(Product.IsFreeItem, true));
        subQuery.groupBy(Product.UniqueId);
        Query query = new Query().from(From.subQuery(subQuery).as("ProductOrderView"));

        if (orderBy != null) {
            if (orderBy.getType() == OrderType.ASC)
                query.orderByAscending(orderBy.getColumn());
            else
                query.orderByDescending(orderBy.getColumn());
        }

        return query;
    }

    public static Query getAll(@Nullable String productNameOrCode, @NonNull UUID customerId,
                               @Nullable UUID orderId, @Nullable UUID[] groupIds,
                               @Nullable Boolean inStock, boolean freeRequestItems,
                               @Nullable OrderBy orderBy) {
        Query query = baseQuery(customerId, orderId, orderBy, freeRequestItems, inStock);

        if (productNameOrCode != null && !productNameOrCode.isEmpty()) {
            productNameOrCode = HelperMethods.persian2Arabic(productNameOrCode);
            productNameOrCode = HelperMethods.convertToEnglishNumbers(productNameOrCode);
        }

        if (productNameOrCode != null && !productNameOrCode.isEmpty()) {
            List<String> regex = new ArrayList<>();
            String[] splits = productNameOrCode.split("\\s+");
            for (int i = 0; i < splits.length; i++) {
                StringBuilder outPut = new StringBuilder();
                outPut.append(splits[i]);
                for (int j = 0; j < splits.length; j++) {
                    if (i != j)
                        outPut.append("%").append(splits[j]);
                }
                regex.add(outPut.toString());
            }


            for (String reg : regex) {
                query.whereOr(Criteria.contains(ProductOrderView.ProductName, reg)
                        .or(Criteria.contains(ProductOrderView.ProductCode, reg)));
            }
        }

        if (groupIds == null)
            groupIds = new UUID[0];
        if (groupIds.length > 1) {
            String[] ids = new String[groupIds.length];
            for (int i = 0; i < groupIds.length; i++) {
                ids[i] = groupIds[i].toString();
            }
            query.whereAnd(Criteria.in(ProductOrderView.ProductGroupId, ids));
        } else if (groupIds.length == 1) {
            query.whereAnd(Criteria.equals(ProductOrderView.ProductGroupId, groupIds[0].toString()));
        }
        return query;
    }

    public static Query getAllFreeItems(@Nullable String productNameOrCode,
                                        @NonNull UUID customerId, @Nullable UUID orderId,
                                        @Nullable UUID[] groupIds, @Nullable Boolean inStock,
                                        @Nullable OrderBy orderBy) {
        return getAll(productNameOrCode, customerId, orderId, groupIds, inStock, true, orderBy);
    }

    public static Query getAllEmphaticItems(@Nullable String productNameOrCode, @NonNull UUID customerId,
                                            @Nullable UUID orderId, @Nullable UUID[] groupIds,
                                            @Nullable Boolean inStock, @Nullable OrderBy orderBy) {
        Query query = getAll(productNameOrCode, customerId, orderId, groupIds, inStock, false, orderBy);
        query.whereAnd(Criteria.notEquals(ProductOrderView.EmphaticType, EmphasisType.NotEmphatic));
        return query;
    }

    public static Query getAllUnSoldItems(@Nullable String productNameOrCode, @NonNull UUID customerId, @Nullable UUID orderId, @Nullable UUID[] groupIds, @Nullable Boolean inStock, @Nullable OrderBy orderBy) {
        Query query = getAll(productNameOrCode, customerId, orderId, groupIds, inStock, false, orderBy);
        List<UUID> soldProductIds = VaranegarApplication.getInstance().getDbHandler().getUUID(CustomerOldInvoiceDetailManager.getSoldItems(customerId));
        query.whereAnd(Criteria.notIn(ProductOrderView.UniqueId, soldProductIds.toArray()));
        return query;
    }

    public static Query get(@NonNull UUID productId, @NonNull UUID customerId, @Nullable UUID orderId, boolean isFreeRequestItem) {
        Query query = baseQuery(customerId, orderId, null, isFreeRequestItem, null);
        query.whereAnd(Criteria.equals(ProductOrderView.UniqueId, productId.toString()));
        return query;
    }

    /**
     * Important note: This query is faster than baseQuery but the important thing is that most of the columns are not populated!
     *
     * @param customerId
     * @return
     */
    public static Query fastQuery(@NonNull UUID customerId, @Nullable UUID callOrderId) {
        Query subQuery = new Query();

        subQuery.select(
                Projection.constant(0).as("EmphaticPriority"),
                Projection.column(Product.UniqueId).as("UniqueId"),
                Projection.column(Product.IsForSale).as("IsForSale"),
                Projection.column(Product.ProductName).as("ProductName"),
                Projection.column(Product.ProductCode).as("ProductCode"),
                Projection.column(Product.ProductGroupId).as("ProductGroupId"),
                Projection.column(Product.IsFreeItem).as("IsFreeItem"),
                Projection.column(Product.OrderPoint).as("OrderPoint"),
                Projection.column(Customer.UniqueId).as("CustomerId"),
                Projection.constant(0).as("TotalQty"),
                Projection.constant("").as("Qty"),
                Projection.constant("").as("UnitName"),
                Projection.constant(0).as("RequestAmount"),
                Projection.constant(0).as("IsRequestFreeItem"),
                Projection.constant(0).as("ConvertFactor"),
                Projection.column(CustomerPrice.Price).as("Price"),
                Projection.column(CustomerPrice.UserPrice).as("UserPrice"),
                Projection.constant(0).as("EmphaticType"),
                Projection.constant(0).as("EmphaticProductCount"),
                Projection.constant(0).as("OnHandQty"),
                Projection.constant(false).as("HasAllocation"),
                Projection.constant(0).as("RemainedQty"),
                Projection.constant(0).as("Average"),
                Projection.constant(0).as("DangerQty"),
                Projection.constant(0).as("WarningQty"),
                Projection.constant(0).as("WarningQty"),
                Projection.constant(0).as("ProductTotalOrderedQty"),
                Projection.constant("").as("PrizeComment"),
                Projection.constant(0).as("CustomerInventoryIsAvailable"),
                Projection.constant(0).as("CustomerInventoryTotalQty"),
                Projection.constant(0).as("BatchOnHandQty"),
                Projection.constant(0).as("BatchNo"),
                Projection.constant(0).as("ExpDate"),
                Projection.constant(0).as("BatchRef"),
                Projection.constant(0).as("ItemRef"),
                Projection.constant("").as("ProductUnitId"),
                Projection.column(Product.PayDuration).as("PayDuration"),
                Projection.constant(null).as("CatalogId"),
                Projection.constant(null).as("CatalogOrderOf"));

        JoinFrom from = From.table(Product.ProductTbl).leftJoin(Customer.CustomerTbl)
                .on(Criteria.equals(Customer.UniqueId, customerId.toString()));

        if (callOrderId != null)
            from = from.innerJoin(CustomerPrice.CustomerPriceTbl)
                    .on(Product.UniqueId, CustomerPrice.ProductUniqueId)
                    .onAnd(Criteria.equals(CustomerPrice.CallOrderId, callOrderId.toString()));
        else
            from = from.innerJoin(CustomerPrice.CustomerPriceTbl)
                    .on(Product.UniqueId, CustomerPrice.ProductUniqueId)
                    .onAnd(Criteria.isNull(CustomerPrice.CallOrderId));

        if (!VaranegarApplication.is(VaranegarApplication.AppId.PreSales))
            from = from.innerJoin(OnHandQty.OnHandQtyTbl)
                    .on(OnHandQty.ProductId, Product.UniqueId);

        subQuery.from(from);
        subQuery.whereAnd(Criteria.equals(Product.IsForSale, true));
        subQuery.groupBy(Product.UniqueId);
        Query query = new Query().from(From.subQuery(subQuery).as("ProductOrderView"));
        return query;
    }

    public List<ProductOrderViewModel> getCatalogItems(UUID orderId, UUID customerId) {
        Query subQuery = new Query();

        subQuery.select(
                Projection.constant(0).as("EmphaticPriority"),
                Projection.constant("").as("ProductUnitId"),
                Projection.column(Product.UniqueId).as("UniqueId"),
                Projection.column(Product.IsForSale).as("IsForSale"),
                Projection.column(Product.ProductName).as("ProductName"),
                Projection.column(Product.ProductCode).as("ProductCode"),
                Projection.column(Product.ProductGroupId).as("ProductGroupId"),
                Projection.column(Product.IsFreeItem).as("IsFreeItem"),
                Projection.column(Product.OrderPoint).as("OrderPoint"),
                Projection.column(Customer.UniqueId).as("CustomerId"),
                Projection.column(TotalQtyView.TotalQty).as("TotalQty"),
                Projection.constant("").as("Qty"),
                Projection.constant("").as("UnitName"),
                Projection.constant(0).as("RequestAmount"),
                Projection.constant(0).as("IsRequestFreeItem"),
                Projection.constant(0).as("ConvertFactor"),
                Projection.column(CustomerPrice.Price).as("Price"),
                Projection.column(CustomerPrice.UserPrice).as("UserPrice"),
                Projection.constant(0).as("EmphaticType"),
                Projection.constant(0).as("EmphaticProductCount"),
                Projection.column(OnHandQty.OnHandQty).as("OnHandQty"),
                Projection.column(OnHandQty.HasAllocation).as("HasAllocation"),
                Projection.constant(0).as("RemainedQty"),
                Projection.constant(0).as("Average"),
                Projection.constant(0).as("DangerQty"),
                Projection.constant(0).as("WarningQty"),
                Projection.constant(0).as("WarningQty"),
                Projection.constant(0).as("ProductTotalOrderedQty"),
                Projection.constant("").as("PrizeComment"),
                Projection.constant(0).as("CustomerInventoryIsAvailable"),
                Projection.constant(0).as("CustomerInventoryTotalQty"),
                Projection.constant(0).as("BatchOnHandQty"),
                Projection.constant(0).as("BatchNo"),
                Projection.constant(0).as("ExpDate"),
                Projection.constant(0).as("BatchRef"),
                Projection.constant(0).as("ItemRef"),
                Projection.column(Product.PayDuration).as("PayDuration"),
                Projection.column(Catalog.CatalogId).as("CatalogId"),
                Projection.column(Catalog.OrderOf).as("CatalogOrderOf"));

        JoinFrom from = From.table(Product.ProductTbl).leftJoin(Customer.CustomerTbl)
                .on(Criteria.equals(Customer.UniqueId, customerId.toString()));

        from = from.innerJoin(CustomerPrice.CustomerPriceTbl)
                .on(Product.UniqueId, CustomerPrice.ProductUniqueId)
                .onAnd(Criteria.equals(CustomerPrice.CustomerUniqueId, customerId.toString()))
                .onAnd(Criteria.equals(CustomerPrice.CallOrderId, orderId.toString()));

        if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales))
            from = from.leftJoin(OnHandQty.OnHandQtyTbl)
                    .on(OnHandQty.ProductId, Product.UniqueId);
        else
            from = from.innerJoin(OnHandQty.OnHandQtyTbl)
                    .on(OnHandQty.ProductId, Product.UniqueId);

        from = from.innerJoin(Catalog.CatalogTbl)
                .on(Catalog.ProductId, Product.UniqueId)
                .leftJoin(TotalQtyView.TotalQtyViewTbl).on(TotalQtyView.ProductUniqueId, Product.UniqueId)
                .onAnd(Criteria.equals(TotalQtyView.OrderUniqueId, orderId.toString()))
                .onAnd(Criteria.equals(TotalQtyView.IsRequestFreeItem, false));
        subQuery.from(from);
        subQuery.whereAnd(Criteria.equals(Product.IsForSale, true));
        subQuery.orderByAscending(Catalog.CatalogId, Catalog.OrderOf);
        Query query = new Query().from(From.subQuery(subQuery).as("ProductOrderView"));
        return getItems(query);
    }

    /**
     * Important note: This query is faster than baseQuery but the important thing is that most of the columns are not populated!
     *
     * @param productNameOrCode
     * @param customerId
     * @return
     */
    public static Query fastSearch(String productNameOrCode, @NonNull UUID customerId, @Nullable UUID orderId) {
        Timber.d(productNameOrCode);
        Query query = fastQuery(customerId, orderId);
        if (productNameOrCode != null && !productNameOrCode.isEmpty()) {
            productNameOrCode = HelperMethods.persian2Arabic(productNameOrCode);
            productNameOrCode = HelperMethods.convertToEnglishNumbers(productNameOrCode);
        }

        if (productNameOrCode != null && !productNameOrCode.isEmpty()) {
            List<String> regex = new ArrayList<>();
            String[] splits = productNameOrCode.split("\\s+");
            for (int i = 0; i < splits.length; i++) {
                StringBuilder outPut = new StringBuilder();
                outPut.append(splits[i]);
                for (int j = 0; j < splits.length; j++) {
                    if (i != j)
                        outPut.append("%").append(splits[j]);
                }
                regex.add(outPut.toString());
            }

            for (String reg : regex) {
                query.whereOr(Criteria.contains(ProductOrderView.ProductName, reg)
                        .or(Criteria.contains(ProductOrderView.ProductCode, reg)));
            }
        }

        return query;
    }
//
//    public static Query getItemsOfSubGroup(@Nullable String productNameOrCode, @NonNull UUID groupId, @NonNull UUID customerId, @Nullable UUID orderId, boolean inStock, boolean freeRequestItems, @Nullable OrderBy orderBy) {
//        Query query = getAll(productNameOrCode, customerId, orderId, inStock, freeRequestItems, orderBy);
//        query.whereAnd(Criteria.equals(ProductOrderView.ProductGroupId, groupId.toString()));
//        return query;
//    }
//
//    public static Query getItemsOfSubGroups(@Nullable String productNameOrCode, @NonNull UUID[] groupIds, @NonNull UUID customerId, @Nullable UUID orderId, boolean inStock, boolean freeRequestItems, @Nullable OrderBy orderBy) {
//        Query query = getAll(productNameOrCode, customerId, orderId, inStock, freeRequestItems, orderBy);
//        String[] ids = new String[groupIds.length];
//        for (int i = 0; i < groupIds.length; i++) {
//            ids[i] = groupIds[i].toString();
//        }
//        query.whereAnd(Criteria.in(ProductOrderView.ProductGroupId, ids));
//        return query;
//    }

    public ProductOrderViewModel getLine(UUID customerId, UUID orderId, UUID productId, boolean isFreeRequestItem) {
        return getItem(get(productId, customerId, orderId, isFreeRequestItem));
    }

    @Nullable
    public Boolean getSPD(UUID customerId) {
        CustomerCallOrderManager customerCallOrderManager = new CustomerCallOrderManager(getContext());
        List<CustomerCallOrderModel> customerCallOrderModels = customerCallOrderManager.getCustomerCallOrders(customerId);
        if (customerCallOrderModels.size() == 0)
            return null;
        List<ProductOrderViewModel> productOrderViewModels = getItems(ProductOrderViewManager.getAllEmphaticItems(null, customerId, null, new UUID[0], null, null));
        boolean spd = true;
        for (ProductOrderViewModel productOrderViewModel :
                productOrderViewModels) {
            double wQty = HelperMethods.bigDecimalToDouble(productOrderViewModel.WarningQty);
            double qty = HelperMethods.bigDecimalToDouble(productOrderViewModel.EmphaticProductCount);
            double totalQty = HelperMethods.bigDecimalToDouble(productOrderViewModel.TotalQty);
            if (wQty + totalQty < qty) {
                spd = false;
                break;
            }
        }
        return spd;
    }

    public boolean getSPD() {
        CustomerManager customerManager = new CustomerManager(getContext());
        List<CustomerModel> customerModels = customerManager.getVisitedCustomers();
        boolean spd = true;
        for (CustomerModel customer :
                customerModels) {
            List<ProductOrderViewModel> productOrderViewModels = getItems(ProductOrderViewManager
                    .getAllEmphaticItems(null, customer.UniqueId,
                            null, new UUID[0], null, null));
            for (ProductOrderViewModel productOrderViewModel :
                    productOrderViewModels) {
                double wQty = HelperMethods.bigDecimalToDouble(productOrderViewModel.WarningQty);
                double qty = HelperMethods.bigDecimalToDouble(productOrderViewModel.EmphaticProductCount);
                double totalQty = HelperMethods.bigDecimalToDouble(productOrderViewModel.TotalQty);
                if (wQty + totalQty < qty) {
                    spd = false;
                    break;
                }
            }
        }
        return spd;
    }


}