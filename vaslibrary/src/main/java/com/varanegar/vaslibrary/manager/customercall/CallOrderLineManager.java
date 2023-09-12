package com.varanegar.vaslibrary.manager.customercall;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.projection.Projection;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.SubsystemType;
import com.varanegar.vaslibrary.base.SubsystemTypeId;
import com.varanegar.vaslibrary.base.SubsystemTypes;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.CallOrderLineBatchQtyDetailManager;
import com.varanegar.vaslibrary.manager.EVCItemStatuesCustomersManager;
import com.varanegar.vaslibrary.manager.InvoiceLineQtyManager;
import com.varanegar.vaslibrary.manager.OrderLineQtyManager;
import com.varanegar.vaslibrary.manager.ProductManager;
import com.varanegar.vaslibrary.manager.ProductType;
import com.varanegar.vaslibrary.manager.ProductUnitViewManager;
import com.varanegar.vaslibrary.manager.PromotionException;
import com.varanegar.vaslibrary.model.CallOrderLineBatchQtyDetail;
import com.varanegar.vaslibrary.model.call.CallInvoiceLineModel;
import com.varanegar.vaslibrary.model.call.CallOrderLine;
import com.varanegar.vaslibrary.model.call.CallOrderLineModel;
import com.varanegar.vaslibrary.model.call.CallOrderLineModelRepository;
import com.varanegar.vaslibrary.model.call.temporder.CallOrderLinesQtyTempModel;
import com.varanegar.vaslibrary.model.call.temporder.CallOrderLinesTemp;
import com.varanegar.vaslibrary.model.call.temporder.CallOrderLinesTempModel;
import com.varanegar.vaslibrary.model.evcitemstatuessdscustomers.EVCItemStatuesCustomersModel;
import com.varanegar.vaslibrary.model.freeReason.FreeReasonModel;
import com.varanegar.vaslibrary.model.invoiceLineQty.InvoiceLineQtyModel;
import com.varanegar.vaslibrary.model.orderLineQtyModel.OrderLineQty;
import com.varanegar.vaslibrary.model.orderLineQtyModel.OrderLineQtyModel;
import com.varanegar.vaslibrary.model.product.ProductModel;
import com.varanegar.vaslibrary.model.productUnitView.ProductUnitViewModel;
import com.varanegar.vaslibrary.promotion.CustomerCallOrderLinePromotion;
import com.varanegar.vaslibrary.promotion.CustomerCallOrderPromotion;
import com.varanegar.vaslibrary.ui.calculator.BaseUnit;
import com.varanegar.vaslibrary.ui.calculator.DiscreteUnit;
import com.varanegar.vaslibrary.ui.calculator.ordercalculator.BatchQty;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;
import varanegar.com.discountcalculatorlib.viewmodel.DiscountEvcItemStatuteData;

/**
 * Created by s.foroughi on 28/02/2017.
 */

public class CallOrderLineManager extends BaseManager<CallOrderLineModel> {
    public CallOrderLineManager(Context context) {
        super(context, new CallOrderLineModelRepository());
    }

    public List<CallOrderLineModel> getOrderLines(@NonNull UUID orderId) {
        Query query = new Query();
        query.from(CallOrderLine.CallOrderLineTbl).whereAnd(Criteria.equals(CallOrderLine.OrderUniqueId, orderId.toString()));
        return getItems(query);
    }

    @SubsystemTypes(ids = {SubsystemTypeId.HotSales, SubsystemTypeId.PreSales, SubsystemTypeId.Supervisor})
    public UUID addOrUpdateQty(final UUID productId, @NonNull List<DiscreteUnit> productQtyLines, @Nullable BaseUnit bulkQty, @NonNull UUID callOrderId, @Nullable final FreeReasonModel freeReason, @Nullable List<BatchQty> batchQtyList, boolean isSimpleMode) throws ValidationException, DbException {
        final UUID freeReasonId = freeReason == null ? null : freeReason.UniqueId;
        if (productQtyLines.size() == 0 && bulkQty == null)
            throw new IllegalArgumentException("empty qty is not supported");
        if (productQtyLines.size() > 1 && bulkQty == null && !isSimpleMode) {
            double totalQty = 0;
            List<ProductUnitViewModel> productUnits = new ArrayList<>();
            for (DiscreteUnit productQtyLine :
                    productQtyLines) {
                totalQty += productQtyLine.getTotalQty();
                productUnits.add(new ProductUnitViewManager(getContext()).getItem(productQtyLine.ProductUnitId));
            }
            productQtyLines = VasHelperMethods.chopTotalQty(BigDecimal.valueOf(totalQty), productUnits, VaranegarApplication.is(VaranegarApplication.AppId.Dist));
        }
        final List<CallOrderLineModel> customerCallOrderLines = getOrderLines(callOrderId);
        final CallOrderLineModel customerCallOrderLine = Linq.findFirst(customerCallOrderLines, new Linq.Criteria<CallOrderLineModel>() {
            @Override
            public boolean run(CallOrderLineModel item) {
                if (freeReasonId == null) {
                    return item.ProductUniqueId.equals(productId) && item.FreeReasonId == null;
                }
                else {
                    return item.ProductUniqueId.equals(productId) && item.FreeReasonId != null && item.FreeReasonId.equals(freeReasonId);
                }
            }
        });
        UUID callOrderLineId = null;
        if (customerCallOrderLine == null) {
            callOrderLineId = addCustomerOrderLine(productQtyLines, bulkQty, callOrderId, productId, freeReason);
        } else {
            callOrderLineId = updateCustomerOrderLine(customerCallOrderLine, productQtyLines, bulkQty, null);
        }
        CallOrderLineBatchQtyDetailManager callOrderLineBatchQtyDetailManager = new CallOrderLineBatchQtyDetailManager(getContext());
        if (batchQtyList != null)
            callOrderLineBatchQtyDetailManager.addOrUpdateQty(callOrderLineId, batchQtyList);
        return callOrderLineId;
    }

    @SubsystemType(id = SubsystemTypeId.Dist)
    public void updateQty(final UUID orderLineId, @NonNull List<DiscreteUnit> productQtyLines, @Nullable BaseUnit bulkQty, @Nullable List<BatchQty> batchQtyList, boolean isSimpleMode, @Nullable UUID editReasonId) throws ValidationException, DbException {
        if (productQtyLines.size() == 0 && bulkQty == null)
            throw new IllegalArgumentException("empty qty is not supported");
        if (productQtyLines.size() > 1 && bulkQty == null && !isSimpleMode) {
            double totalQty = 0;
            List<ProductUnitViewModel> productUnits = new ArrayList<>();
            for (DiscreteUnit productQtyLine :
                    productQtyLines) {
                totalQty += productQtyLine.getTotalQty();
                productUnits.add(new ProductUnitViewManager(getContext()).getItem(productQtyLine.ProductUnitId));
            }
            productQtyLines = VasHelperMethods.chopTotalQty(BigDecimal.valueOf(totalQty), productUnits, VaranegarApplication.is(VaranegarApplication.AppId.Dist));
        }
        final CallOrderLineModel customerCallOrderLine = getItem(orderLineId);
        updateCustomerOrderLine(customerCallOrderLine, productQtyLines, bulkQty, editReasonId);
        CallOrderLineBatchQtyDetailManager callOrderLineBatchQtyDetailManager = new CallOrderLineBatchQtyDetailManager(getContext());
        if (batchQtyList != null)
            callOrderLineBatchQtyDetailManager.addOrUpdateQty(orderLineId, batchQtyList);
    }

    private UUID addCustomerOrderLine(
            final List<DiscreteUnit> productQtyLines,
            final BaseUnit bulkUnit,
            final UUID callOrderId,
            UUID productId,
            FreeReasonModel freeReason) throws ValidationException, DbException {
        final CallOrderLineModel newCustomerCallOrderLine = new CallOrderLineModel();
        newCustomerCallOrderLine.UniqueId = UUID.randomUUID();
        newCustomerCallOrderLine.OrderUniqueId = callOrderId;
        newCustomerCallOrderLine.ProductUniqueId = productId;
        newCustomerCallOrderLine.IsRequestFreeItem = freeReason != null;
        newCustomerCallOrderLine.FreeReasonId = freeReason != null ? freeReason.UniqueId : null;

        Query query = new Query().from(CallOrderLine.CallOrderLineTbl).select(Projection.max(CallOrderLine.SortId))
                .whereAnd(Criteria.equals(CallOrderLine.OrderUniqueId, callOrderId.toString()));
        Integer last = VaranegarApplication.getInstance().getDbHandler().getIntegerSingle(query);
        if (last == null) last = 0;
        last++;
        newCustomerCallOrderLine.SortId = last;
        if (bulkUnit != null && productQtyLines.size() > 0) {
            newCustomerCallOrderLine.RequestBulkQty = new BigDecimal(bulkUnit.value);
            newCustomerCallOrderLine.RequestBulkQtyUnitUniqueId = bulkUnit.ProductUnitId;
        }
        long affectedRows = insert(newCustomerCallOrderLine);
        if (productQtyLines.size() > 0) {
            final int[] no = {0};
            for (DiscreteUnit qtyLine :
                    productQtyLines) {
                affectedRows += insertOrUpdateOrderLineQty(qtyLine, newCustomerCallOrderLine.UniqueId);
            }
        } else if (bulkUnit != null) {
            DiscreteUnit u = new DiscreteUnit();
            u.value = bulkUnit.value;
            u.Name = bulkUnit.Name;
            u.ProductUnitId = bulkUnit.ProductUnitId;
            affectedRows += insertOrUpdateOrderLineQty(u, newCustomerCallOrderLine.UniqueId);
            Timber.i("Number of affected rows = " + affectedRows);
        }
        return newCustomerCallOrderLine.UniqueId;
    }

    private UUID updateCustomerOrderLine(final CallOrderLineModel customerCallOrderLine, final List<DiscreteUnit> qtyLines, final BaseUnit bulkUnit, @Nullable UUID editReasonId) throws ValidationException, DbException {
        CallOrderLineManager callOrderLineManager = new CallOrderLineManager(getContext());
        if (bulkUnit != null && qtyLines.size() > 0) {
            customerCallOrderLine.RequestBulkQty = new BigDecimal(bulkUnit.value);
            customerCallOrderLine.RequestBulkQtyUnitUniqueId = bulkUnit.ProductUnitId;
        }
        customerCallOrderLine.EditReasonId = editReasonId;
        callOrderLineManager.update(customerCallOrderLine);
        OrderLineQtyManager qtyManager = new OrderLineQtyManager(getContext());
        qtyManager.delete(Criteria.equals(OrderLineQty.OrderLineUniqueId, customerCallOrderLine.UniqueId));
        if (qtyLines.size() > 0) {
            for (DiscreteUnit qtyLine :
                    qtyLines) {
                insertOrUpdateOrderLineQty(qtyLine, customerCallOrderLine.UniqueId);
            }
        } else if (bulkUnit != null) {
            DiscreteUnit u = new DiscreteUnit();
            u.value = bulkUnit.value;
            u.Name = bulkUnit.Name;
            u.ProductUnitId = bulkUnit.ProductUnitId;
            insertOrUpdateOrderLineQty(u, customerCallOrderLine.UniqueId);
        }
        return customerCallOrderLine.UniqueId;
    }

    private long insertOrUpdateOrderLineQty(DiscreteUnit qtyLine, UUID orderLineUniqueId) throws ValidationException, DbException {
        OrderLineQtyManager qtyManager = new OrderLineQtyManager(getContext());
        OrderLineQtyModel qty = qtyManager.getItem(OrderLineQtyManager.getOrderQtyDetail(orderLineUniqueId, qtyLine.ProductUnitId));
        if (qty != null) {
            qty.Qty = qtyLine.getQty();
            return qtyManager.update(qty);
        } else {
            qty = new OrderLineQtyModel();
            qty.UniqueId = UUID.randomUUID();
            qty.Qty = qtyLine.getQty();
            qty.OrderLineUniqueId = orderLineUniqueId;
            qty.ProductUnitId = qtyLine.ProductUnitId;
            return qtyManager.insert(qty);
        }

    }

    public long deleteProduct(UUID orderId, UUID productId, boolean isRequestFreeItem) throws DbException {
        return delete(Criteria.equals(CallOrderLine.IsRequestFreeItem, isRequestFreeItem)
                .and(Criteria.equals(CallOrderLine.ProductUniqueId, productId.toString())
                        .and(Criteria.equals(CallOrderLine.OrderUniqueId, orderId.toString()))));
    }

    @SubsystemType(id = SubsystemTypeId.PreSales)
    public void updateLineWithPromotionForPreSales(@NonNull UUID callOrderId, @NonNull CustomerCallOrderPromotion customerCallOrderPromotion)
            throws PromotionException, ProductUnitViewManager.UnitNotFoundException, ValidationException, DbException {
        List<CallOrderLineModel> lines = getOrderLines(callOrderId);
        HashMap<UUID, CallOrderLineModel> linesMap = new HashMap<>();
        for (CallOrderLineModel callOrderLine :
                lines) {
            linesMap.put(callOrderLine.UniqueId, callOrderLine);
        }
        CallOrderLinesTempManager callOrderLinesTempManager = new CallOrderLinesTempManager(getContext());
        callOrderLinesTempManager.delete(Criteria.equals(CallOrderLinesTemp.OrderUniqueId, callOrderId.toString()));
        for (CustomerCallOrderLinePromotion customerCallOrderLinePromotion :
                customerCallOrderPromotion.LinesWithPromo) {
            try {
                if (linesMap.containsKey(customerCallOrderLinePromotion.UniqueId)) {
                    CallOrderLineModel line = linesMap.get(customerCallOrderLinePromotion.UniqueId);
                    line.PresalesAdd1Amount = customerCallOrderLinePromotion.InvoiceAdd1;
                    line.PresalesAdd2Amount = customerCallOrderLinePromotion.InvoiceAdd2;
                    line.PresalesOtherAddAmount = customerCallOrderLinePromotion.InvoiceAddOther;
                    line.PresalesTaxAmount = customerCallOrderLinePromotion.InvoiceTax;
                    line.PresalesChargeAmount = customerCallOrderLinePromotion.InvoiceCharge;
                    line.PresalesDis1Amount = customerCallOrderLinePromotion.InvoiceDis1;
                    line.PresalesDis2Amount = customerCallOrderLinePromotion.InvoiceDis2;
                    line.PresalesDis3Amount = customerCallOrderLinePromotion.InvoiceDis3;
                    line.PresalesOtherDiscountAmount = customerCallOrderLinePromotion.InvoiceDisOther;
                    update(line);
                    linesMap.remove(customerCallOrderLinePromotion.UniqueId);
                } else {
                    insertPromotionLineTemp(customerCallOrderLinePromotion, callOrderId);
                    linesMap.remove(customerCallOrderLinePromotion.UniqueId);
                }
            } catch (Exception e) {
                Timber.e(e);
                throw e;
            }
        }
    }

    @SubsystemType(id = SubsystemTypeId.HotSales)
    private void updateLineWithPromotion(@NonNull CallOrderLineModel
                                                 callOrderLineModel, @NonNull CustomerCallOrderLinePromotion customerCallOrderLinePromotion) throws
            ValidationException, DbException {
        callOrderLineModel.RequestAdd1Amount = customerCallOrderLinePromotion.InvoiceAdd1;
        callOrderLineModel.RequestAdd2Amount = customerCallOrderLinePromotion.InvoiceAdd2;
        callOrderLineModel.RequestOtherAddAmount = customerCallOrderLinePromotion.InvoiceAddOther;
        callOrderLineModel.RequestTaxAmount = customerCallOrderLinePromotion.InvoiceTax;
        callOrderLineModel.RequestChargeAmount = customerCallOrderLinePromotion.InvoiceCharge;
        callOrderLineModel.RequestDis1Amount = customerCallOrderLinePromotion.InvoiceDis1;
        callOrderLineModel.RequestDis2Amount = customerCallOrderLinePromotion.InvoiceDis2;
        callOrderLineModel.RequestDis3Amount = customerCallOrderLinePromotion.InvoiceDis3;
        callOrderLineModel.RequestOtherDiscountAmount = customerCallOrderLinePromotion.InvoiceDisOther;
        callOrderLineModel.EVCId = customerCallOrderLinePromotion.EVCId;
        callOrderLineModel.DiscountRef = customerCallOrderLinePromotion.DiscountRef;
        callOrderLineModel.DiscountId = customerCallOrderLinePromotion.DiscountId;
        callOrderLineModel.PayDuration = customerCallOrderLinePromotion.PayDuration;
        callOrderLineModel.RuleNo = customerCallOrderLinePromotion.RuleNo;
        update(callOrderLineModel);
    }

    @SubsystemType(id = SubsystemTypeId.Dist)
    private void updateLineWithPromotionForDist(@NonNull CallOrderLineModel
                                                        callOrderLineModel, @NonNull CustomerCallOrderLinePromotion customerCallOrderLinePromotion, boolean isPromoLine) throws
            ValidationException, DbException, ProductUnitViewManager.UnitNotFoundException, PromotionException {
        callOrderLineModel.RequestAdd1Amount = customerCallOrderLinePromotion.InvoiceAdd1;
        callOrderLineModel.RequestAdd2Amount = customerCallOrderLinePromotion.InvoiceAdd2;
        callOrderLineModel.RequestOtherAddAmount = customerCallOrderLinePromotion.InvoiceAddOther;
        callOrderLineModel.RequestTaxAmount = customerCallOrderLinePromotion.InvoiceTax;
        callOrderLineModel.RequestChargeAmount = customerCallOrderLinePromotion.InvoiceCharge;
        callOrderLineModel.RequestDis1Amount = customerCallOrderLinePromotion.InvoiceDis1;
        callOrderLineModel.RequestDis2Amount = customerCallOrderLinePromotion.InvoiceDis2;
        callOrderLineModel.RequestDis3Amount = customerCallOrderLinePromotion.InvoiceDis3;
        callOrderLineModel.RequestOtherDiscountAmount = customerCallOrderLinePromotion.InvoiceDisOther;
        callOrderLineModel.EVCId = customerCallOrderLinePromotion.EVCId;
        callOrderLineModel.DiscountRef = customerCallOrderLinePromotion.DiscountRef;
        callOrderLineModel.DiscountId = customerCallOrderLinePromotion.DiscountId;
        callOrderLineModel.PayDuration = customerCallOrderLinePromotion.PayDuration;
        callOrderLineModel.RuleNo = customerCallOrderLinePromotion.RuleNo;
        callOrderLineModel.PromotionPrice = customerCallOrderLinePromotion.InvoiceAmount;
        update(callOrderLineModel);
        ProductUnitViewManager productUnitViewManager = new ProductUnitViewManager(getContext());
        List<ProductUnitViewModel> units = productUnitViewManager.getProductUnits(customerCallOrderLinePromotion.ProductId, ProductType.isForSale);
        final ProductUnitViewModel bulkProductUnit = Linq.findFirst(units, new Linq.Criteria<ProductUnitViewModel>() {
            @Override
            public boolean run(ProductUnitViewModel item) {
                return item.Decimal > 0;
            }
        });
        List<DiscreteUnit> qtyLines = new ArrayList<>();
        if (bulkProductUnit != null) {
            DiscreteUnit u = new DiscreteUnit();
            u.value = customerCallOrderLinePromotion.TotalRequestQty.doubleValue();
            u.Name = bulkProductUnit.UnitName;
            u.ConvertFactor = bulkProductUnit.ConvertFactor;
            u.ProductUnitId = bulkProductUnit.UniqueId;
            qtyLines.add(u);
        } else {
            qtyLines = VasHelperMethods.chopTotalQty(customerCallOrderLinePromotion.TotalRequestQty, units, true);
        }
        if (qtyLines.size() > 0) {
            for (BaseUnit qtyLine :
                    qtyLines) {
                insertOrUpdateOrderLineQty((DiscreteUnit) qtyLine, callOrderLineModel.UniqueId);
            }
        } else {
            throw new PromotionException(getContext().getString(R.string.error_saving_request));
        }
        if (isPromoLine) {
            CallOrderLineBatchQtyDetailManager callOrderLineBatchQtyDetailManager = new CallOrderLineBatchQtyDetailManager(getContext());
            callOrderLineBatchQtyDetailManager.updatePromoLineBatchQty(callOrderLineModel.UniqueId, customerCallOrderLinePromotion.TotalRequestQty);
        }
    }

    private void insertPromotionLine(
            @NonNull final CustomerCallOrderLinePromotion customerCallOrderLinePromotion,
            @NonNull UUID callOrderId) throws
            PromotionException, ProductUnitViewManager.UnitNotFoundException, ValidationException, DbException {
        if (customerCallOrderLinePromotion.TotalRequestQty == null || customerCallOrderLinePromotion.TotalRequestQty.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PromotionException(getContext().getString(R.string.promotion_qty_is_wrong));
        }
        final CallOrderLineModel callOrderLineModel = new CallOrderLineModel();
        callOrderLineModel.UniqueId = UUID.randomUUID();
        callOrderLineModel.OrderUniqueId = callOrderId;
        callOrderLineModel.ProductUniqueId = customerCallOrderLinePromotion.ProductId;
        Query query = new Query().from(CallOrderLine.CallOrderLineTbl).select(Projection.max(CallOrderLine.SortId))
                .whereAnd(Criteria.equals(CallOrderLine.OrderUniqueId, callOrderId.toString()));
        Integer last = VaranegarApplication.getInstance().getDbHandler().getIntegerSingle(query);
        if (last == null) last = 0;
        last++;
        callOrderLineModel.SortId = last;
        callOrderLineModel.RequestAdd1Amount = customerCallOrderLinePromotion.InvoiceAdd1;
        callOrderLineModel.RequestAdd2Amount = customerCallOrderLinePromotion.InvoiceAdd2;
        callOrderLineModel.RequestOtherAddAmount = customerCallOrderLinePromotion.InvoiceAddOther;
        callOrderLineModel.RequestTaxAmount = customerCallOrderLinePromotion.InvoiceTax;
        callOrderLineModel.RequestChargeAmount = customerCallOrderLinePromotion.InvoiceCharge;
        callOrderLineModel.RequestDis1Amount = customerCallOrderLinePromotion.InvoiceDis1;
        callOrderLineModel.RequestDis2Amount = customerCallOrderLinePromotion.InvoiceDis2;
        callOrderLineModel.RequestDis3Amount = customerCallOrderLinePromotion.InvoiceDis3;
        callOrderLineModel.RequestOtherDiscountAmount = customerCallOrderLinePromotion.InvoiceDisOther;
        callOrderLineModel.EVCId = customerCallOrderLinePromotion.EVCId;
        callOrderLineModel.DiscountRef = customerCallOrderLinePromotion.DiscountRef;
        callOrderLineModel.DiscountId = customerCallOrderLinePromotion.DiscountId;
        callOrderLineModel.PromotionPrice = customerCallOrderLinePromotion.InvoiceAmount;
        callOrderLineModel.IsPromoLine = true;
        insert(callOrderLineModel);
        ProductUnitViewManager productUnitViewManager = new ProductUnitViewManager(getContext());
        List<ProductUnitViewModel> units = productUnitViewManager.getProductUnits(customerCallOrderLinePromotion.ProductId, ProductType.isForSale);
        final List<DiscreteUnit> qtyLines = VasHelperMethods.chopTotalQty(customerCallOrderLinePromotion.TotalRequestQty, units, false);
        if (qtyLines.size() > 0) {
            for (BaseUnit qtyLine :
                    qtyLines) {
                insertOrUpdateOrderLineQty((DiscreteUnit) qtyLine, callOrderLineModel.UniqueId);
            }
        } else {
            throw new PromotionException(getContext().getString(R.string.error_saving_request));
        }
        // TODO: 4/9/2018 add bulk unit

    }

    public void removePromotions(@NonNull final UUID callOrderId) throws
            DbException, ValidationException {
        if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales))
            return;

        try {
            VaranegarApplication.getInstance().getDbHandler().beginTransaction();
            if (VaranegarApplication.is(VaranegarApplication.AppId.HotSales))
                delete(Criteria.equals(CallOrderLine.OrderUniqueId, callOrderId.toString()).and(Criteria.equals(CallOrderLine.IsPromoLine, true)));
            final List<CallOrderLineModel> linesAfter = getOrderLines(callOrderId);
            if (linesAfter.size() > 0) {
                if (VaranegarApplication.is(VaranegarApplication.AppId.HotSales)) {
                    for (CallOrderLineModel callOrderLineModel :
                            linesAfter) {
                        callOrderLineModel.RequestAdd1Amount = Currency.ZERO;
                        callOrderLineModel.RequestAdd2Amount = Currency.ZERO;
                        callOrderLineModel.RequestOtherAddAmount = Currency.ZERO;
                        callOrderLineModel.RequestTaxAmount = Currency.ZERO;
                        callOrderLineModel.RequestChargeAmount = Currency.ZERO;
                        callOrderLineModel.RequestDis1Amount = Currency.ZERO;
                        callOrderLineModel.RequestDis2Amount = Currency.ZERO;
                        callOrderLineModel.RequestDis3Amount = Currency.ZERO;
                        callOrderLineModel.RequestOtherDiscountAmount = Currency.ZERO;
                        callOrderLineModel.IsPromoLine = false;
                        callOrderLineModel.DiscountRef = 0;
                        callOrderLineModel.DiscountId = null;
                    }
                } else if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
                    CallInvoiceLineManager callInvoiceLineManager = new CallInvoiceLineManager(getContext());
                    List<CallInvoiceLineModel> invoiceLines = callInvoiceLineManager.getLines(callOrderId);
                    for (final CallOrderLineModel callOrderLineModel :
                            linesAfter) {
                        CallInvoiceLineModel lineBefore = Linq.findFirst(invoiceLines, new Linq.Criteria<CallInvoiceLineModel>() {
                            @Override
                            public boolean run(CallInvoiceLineModel item) {
                                return item.UniqueId.equals(callOrderLineModel.UniqueId);
                            }
                        });
                        callOrderLineModel.RequestAdd1Amount = lineBefore.RequestAdd1Amount;
                        callOrderLineModel.RequestAdd2Amount = lineBefore.RequestAdd2Amount;
                        callOrderLineModel.RequestOtherAddAmount = lineBefore.RequestOtherAddAmount;
                        callOrderLineModel.RequestTaxAmount = lineBefore.RequestTaxAmount;
                        callOrderLineModel.RequestChargeAmount = lineBefore.RequestChargeAmount;
                        callOrderLineModel.RequestDis1Amount = lineBefore.RequestDis1Amount;
                        callOrderLineModel.RequestDis2Amount = lineBefore.RequestDis2Amount;
                        callOrderLineModel.RequestDis3Amount = lineBefore.RequestDis3Amount;
                        callOrderLineModel.RequestOtherDiscountAmount = lineBefore.RequestOtherDiscountAmount;
                        callOrderLineModel.DiscountRef = lineBefore.DiscountRef;
                        callOrderLineModel.DiscountId = lineBefore.DiscountId;
                        callOrderLineModel.RequestBulkQty = lineBefore.RequestBulkQty;
                        callOrderLineModel.RequestBulkQtyUnitUniqueId = lineBefore.RequestBulkQtyUnitUniqueId;
                        if (callOrderLineModel.IsPromoLine || !callOrderLineModel.cart.isEmpty()) {
                            OrderLineQtyManager orderLineQtyManager = new OrderLineQtyManager(getContext());
                            orderLineQtyManager.delete(Criteria.equals(OrderLineQty.OrderLineUniqueId, callOrderLineModel.UniqueId));
                            InvoiceLineQtyManager invoiceLineQtyManager = new InvoiceLineQtyManager(getContext());
                            List<InvoiceLineQtyModel> qtys = invoiceLineQtyManager.getQtyLines(callOrderLineModel.UniqueId);
                            for (InvoiceLineQtyModel qty :
                                    qtys) {
                                OrderLineQtyModel orderLineQtyModel = new OrderLineQtyModel();
                                orderLineQtyModel.Qty = qty.Qty;
                                orderLineQtyModel.ProductUnitId = qty.ProductUnitId;
                                orderLineQtyModel.OrderLineUniqueId = qty.OrderLineUniqueId;
                                orderLineQtyModel.UniqueId = qty.UniqueId;
                                orderLineQtyManager.insert(orderLineQtyModel);
                            }
                        }
                    }
                }
                update(linesAfter);
            }
            VaranegarApplication.getInstance().getDbHandler().setTransactionSuccessful();
        } catch (Exception ex) {
            Timber.e(ex);
            throw ex;
        } finally {
            VaranegarApplication.getInstance().getDbHandler().endTransaction();
        }

    }

    public void insertOrUpdatePromoLines(@NonNull UUID
                                                 callOrderId, @NonNull CustomerCallOrderPromotion customerCallOrderPromotion, @NonNull UUID
                                                 customerId) throws
            ValidationException, DbException, ProductUnitViewManager.UnitNotFoundException, PromotionException {
        if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
            try {
                VaranegarApplication.getInstance().getDbHandler().beginTransaction();
                List<CallOrderLineModel> lines = getOrderLines(callOrderId);
                HashMap<UUID, CallOrderLineModel> linesMap = new HashMap<>();
                HashMap<String, CallOrderLineModel> promoLinesMap = new HashMap<>();
                List<CallOrderLineModel> removeLines = new ArrayList<>();
                for (CallOrderLineModel callOrderLine :
                        lines) {
                    ProductModel productModel=new ProductManager(getContext()).getItemProduct(callOrderLine.ProductUniqueId);
                    String key=productModel.ProductCode;
                    if (!callOrderLine.cart.isEmpty())
                        key=callOrderLine.cart+productModel.ProductCode;

                    if (callOrderLine.IsPromoLine || !callOrderLine.cart.isEmpty())
                        promoLinesMap.put(key, callOrderLine);
                    else
                        linesMap.put(callOrderLine.UniqueId, callOrderLine);
                }

                if (customerCallOrderPromotion.LinesWithPromo != null && customerCallOrderPromotion.LinesWithPromo.size() > 0) {
                    for (CustomerCallOrderLinePromotion customerCallOrderLinePromotion :
                            customerCallOrderPromotion.LinesWithPromo) {
                        String cartkey=customerCallOrderLinePromotion.ProductCode;
                        if (!customerCallOrderLinePromotion.cart.isEmpty())
                            cartkey=customerCallOrderLinePromotion.cart+customerCallOrderLinePromotion.ProductCode;

                        if (linesMap.containsKey(customerCallOrderLinePromotion.UniqueId)
                                &&customerCallOrderLinePromotion.cart.isEmpty()) {
                            updateLineWithPromotionForDist(linesMap.get(customerCallOrderLinePromotion.UniqueId), customerCallOrderLinePromotion, false);
                            linesMap.remove(customerCallOrderLinePromotion.UniqueId);
                        } else if (promoLinesMap.containsKey(cartkey)) {
                            updateLineWithPromotionForDist(promoLinesMap.get(cartkey), customerCallOrderLinePromotion, true);
                            promoLinesMap.remove(cartkey);
                        }
                    }
                    removeLines.addAll(linesMap.values());
                    removeLines.addAll(promoLinesMap.values());
                    if (removeLines.size() > 0) {
                        for (CallOrderLineModel callOrderLineModel : removeLines) {
                            callOrderLineModel.RequestAdd1Amount = Currency.valueOf(0);
                            callOrderLineModel.RequestAdd2Amount = Currency.valueOf(0);
                            callOrderLineModel.RequestOtherAddAmount = Currency.valueOf(0);
                            callOrderLineModel.RequestTaxAmount = Currency.valueOf(0);
                            callOrderLineModel.RequestChargeAmount = Currency.valueOf(0);
                            callOrderLineModel.RequestDis1Amount = Currency.valueOf(0);
                            callOrderLineModel.RequestDis2Amount = Currency.valueOf(0);
                            callOrderLineModel.RequestDis3Amount = Currency.valueOf(0);
                            callOrderLineModel.RequestOtherDiscountAmount = Currency.valueOf(0);
                            callOrderLineModel.RequestOtherDiscountAmount = Currency.valueOf(0);
                            callOrderLineModel.PayDuration = 0;
                            update(callOrderLineModel);
                            ProductUnitViewManager productUnitViewManager = new ProductUnitViewManager(getContext());
                            List<ProductUnitViewModel> units = productUnitViewManager.getProductUnits(callOrderLineModel.ProductUniqueId, ProductType.isForSale);
                            final List<DiscreteUnit> qtyLines = VasHelperMethods.chopTotalQty(BigDecimal.ZERO, units, true);
                            if (qtyLines.size() > 0) {
                                for (BaseUnit qtyLine :
                                        qtyLines) {
                                    insertOrUpdateOrderLineQty((DiscreteUnit) qtyLine, callOrderLineModel.UniqueId);
                                }
                            } else {
                                throw new PromotionException(getContext().getString(R.string.error_saving_request));
                            }
                            CallOrderLineBatchQtyDetailManager callOrderLineBatchQtyDetailManager = new CallOrderLineBatchQtyDetailManager(getContext());
                            callOrderLineBatchQtyDetailManager.delete(Criteria.equals(CallOrderLineBatchQtyDetail.CustomerCallOrderLineUniqueId, callOrderLineModel.ProductUniqueId.toString()));
                        }
                    }
                    List<EVCItemStatuesCustomersModel> evcItemStatuesCustomersModels = new ArrayList<>();
                    EVCItemStatuesCustomersManager evcItemStatuesCustomersManager = new EVCItemStatuesCustomersManager(getContext());
                    CallOrderLineManager callOrderLineManager = new CallOrderLineManager(getContext());
                    List<CallOrderLineModel> callOrderLineModels = callOrderLineManager.getOrderLines(callOrderId);
                    for (CallOrderLineModel item:
                            callOrderLineModels) {
                        evcItemStatuesCustomersManager.deleteEVCItemStatus(customerId, item.UniqueId);
                    }
                    if (customerCallOrderPromotion.discountEvcItemStatuteData != null && customerCallOrderPromotion.discountEvcItemStatuteData.size() > 0) {
                        for (DiscountEvcItemStatuteData discountEvcItemStatuteData : customerCallOrderPromotion.discountEvcItemStatuteData) {
                            EVCItemStatuesCustomersModel evcItemStatuesCustomersModel = new EVCItemStatuesCustomersModel();
                            evcItemStatuesCustomersModel.UniqueId = UUID.randomUUID();
                            evcItemStatuesCustomersModel.AddAmount = discountEvcItemStatuteData.AddAmount;
                            evcItemStatuesCustomersModel.Discount = discountEvcItemStatuteData.Discount;
                            evcItemStatuesCustomersModel.CustomerId = customerId;
                            evcItemStatuesCustomersModel.EVCItemRef = discountEvcItemStatuteData.EvcItemRef;
                            evcItemStatuesCustomersModel.SupAmount = discountEvcItemStatuteData.SupAmount;
                            evcItemStatuesCustomersModel.OrderLineId = UUID.fromString(discountEvcItemStatuteData.OrderLineId);
                            evcItemStatuesCustomersModel.DisRef = discountEvcItemStatuteData.DisRef;
                            evcItemStatuesCustomersModels.add(evcItemStatuesCustomersModel);
                        }
                        evcItemStatuesCustomersManager.insertOrUpdateEVCItemStatus(evcItemStatuesCustomersModels, customerId);
                    }
                }
                VaranegarApplication.getInstance().getDbHandler().setTransactionSuccessful();
            } catch (Exception e) {
                Timber.e(e);
                throw e;
            } finally {
                VaranegarApplication.getInstance().getDbHandler().endTransaction();
            }
        } else {
            try {
                VaranegarApplication.getInstance().getDbHandler().beginTransaction();
                List<CallOrderLineModel> lines = getOrderLines(callOrderId);
                HashMap<UUID, CallOrderLineModel> linesMap = new HashMap<>();
                for (CallOrderLineModel callOrderLine :
                        lines) {
                    linesMap.put(callOrderLine.UniqueId, callOrderLine);
                }

                if (customerCallOrderPromotion.LinesWithPromo != null && customerCallOrderPromotion.LinesWithPromo.size() > 0) {
                    for (CustomerCallOrderLinePromotion customerCallOrderLinePromotion :
                            customerCallOrderPromotion.LinesWithPromo) {
                        if (linesMap.containsKey(customerCallOrderLinePromotion.UniqueId)) {
                            updateLineWithPromotion(linesMap.get(customerCallOrderLinePromotion.UniqueId), customerCallOrderLinePromotion);
                        } else {
                            insertPromotionLine(customerCallOrderLinePromotion, callOrderId);
                        }
                    }
                    List<EVCItemStatuesCustomersModel> evcItemStatuesCustomersModels = new ArrayList<>();
                    EVCItemStatuesCustomersManager evcItemStatuesCustomersManager = new EVCItemStatuesCustomersManager(getContext());
                    CallOrderLineManager callOrderLineManager = new CallOrderLineManager(getContext());
                    List<CallOrderLineModel> callOrderLineModels = callOrderLineManager.getOrderLines(callOrderId);
                    for (CallOrderLineModel item:
                            callOrderLineModels) {
                        evcItemStatuesCustomersManager.deleteEVCItemStatus(customerId, item.UniqueId);
                    }
                    if (customerCallOrderPromotion.discountEvcItemStatuteData != null && customerCallOrderPromotion.discountEvcItemStatuteData.size() > 0) {
                        for (DiscountEvcItemStatuteData discountEvcItemStatuteData : customerCallOrderPromotion.discountEvcItemStatuteData) {
                            EVCItemStatuesCustomersModel evcItemStatuesCustomersModel = new EVCItemStatuesCustomersModel();
                            evcItemStatuesCustomersModel.UniqueId = UUID.randomUUID();
                            evcItemStatuesCustomersModel.AddAmount = discountEvcItemStatuteData.AddAmount;
                            evcItemStatuesCustomersModel.Discount = discountEvcItemStatuteData.Discount;
                            evcItemStatuesCustomersModel.CustomerId = customerId;
                            evcItemStatuesCustomersModel.EVCItemRef = discountEvcItemStatuteData.EvcItemRef;
                            evcItemStatuesCustomersModel.SupAmount = discountEvcItemStatuteData.SupAmount;
                            evcItemStatuesCustomersModel.OrderLineId = UUID.fromString(discountEvcItemStatuteData.OrderLineId);
                            evcItemStatuesCustomersModel.DisRef = discountEvcItemStatuteData.DisRef;
                            evcItemStatuesCustomersModels.add(evcItemStatuesCustomersModel);
                        }
                        evcItemStatuesCustomersManager.insertOrUpdateEVCItemStatus(evcItemStatuesCustomersModels, customerId);
                    }
                }
                VaranegarApplication.getInstance().getDbHandler().setTransactionSuccessful();
            } catch (Exception e) {
                Timber.e(e);
                throw e;
            } finally {
                VaranegarApplication.getInstance().getDbHandler().endTransaction();
            }
        }
    }
    private void insertPromotionLineTemp(
            @NonNull final CustomerCallOrderLinePromotion customerCallOrderLinePromotion,
            @NonNull UUID callOrderId) throws
            PromotionException, ProductUnitViewManager.UnitNotFoundException, ValidationException, DbException {
        if (customerCallOrderLinePromotion.TotalRequestQty == null || customerCallOrderLinePromotion.TotalRequestQty.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PromotionException(getContext().getString(R.string.promotion_qty_is_wrong));
        }
        final CallOrderLinesTempModel callOrderLineTempModel = new CallOrderLinesTempModel();
        callOrderLineTempModel.UniqueId = UUID.randomUUID();
        callOrderLineTempModel.OrderUniqueId = callOrderId;
        callOrderLineTempModel.ProductUniqueId = customerCallOrderLinePromotion.ProductId;
        Query query = new Query().from(CallOrderLinesTemp.CallOrderLinesTempTbl).select(Projection.max(CallOrderLinesTemp.SortId))
                .whereAnd(Criteria.equals(CallOrderLinesTemp.OrderUniqueId, callOrderId.toString()));
        Integer last = VaranegarApplication.getInstance().getDbHandler().getIntegerSingle(query);
        if (last == null) last = 0;
        last++;
        callOrderLineTempModel.SortId = last;
        callOrderLineTempModel.RequestAdd1Amount = customerCallOrderLinePromotion.InvoiceAdd1;
        callOrderLineTempModel.RequestAdd2Amount = customerCallOrderLinePromotion.InvoiceAdd2;
        callOrderLineTempModel.RequestOtherAddAmount = customerCallOrderLinePromotion.InvoiceAddOther;
        callOrderLineTempModel.RequestTaxAmount = customerCallOrderLinePromotion.InvoiceTax;
        callOrderLineTempModel.RequestChargeAmount = customerCallOrderLinePromotion.InvoiceCharge;
        callOrderLineTempModel.RequestDis1Amount = customerCallOrderLinePromotion.InvoiceDis1;
        callOrderLineTempModel.RequestDis2Amount = customerCallOrderLinePromotion.InvoiceDis2;
        callOrderLineTempModel.RequestDis3Amount = customerCallOrderLinePromotion.InvoiceDis3;
        callOrderLineTempModel.RequestOtherDiscountAmount = customerCallOrderLinePromotion.InvoiceDisOther;
        callOrderLineTempModel.EVCId = customerCallOrderLinePromotion.EVCId;
        callOrderLineTempModel.DiscountRef = customerCallOrderLinePromotion.DiscountRef;
        callOrderLineTempModel.DiscountId = customerCallOrderLinePromotion.DiscountId;
        callOrderLineTempModel.PromotionPrice = customerCallOrderLinePromotion.InvoiceAmount;
        callOrderLineTempModel.IsPromoLine = true;
        CallOrderLinesTempManager callOrderLinesTempManager = new CallOrderLinesTempManager(getContext());
        callOrderLinesTempManager.insert(callOrderLineTempModel);
        ProductUnitViewManager productUnitViewManager = new ProductUnitViewManager(getContext());
        List<ProductUnitViewModel> units = productUnitViewManager.getProductUnits(customerCallOrderLinePromotion.ProductId, ProductType.isForSale);
        final List<DiscreteUnit> qtyLines = VasHelperMethods.chopTotalQty(customerCallOrderLinePromotion.TotalRequestQty, units, false);
        if (qtyLines.size() > 0) {
            for (BaseUnit qtyLine :
                    qtyLines) {
                insertOrUpdateOrderLineQtyTemp((DiscreteUnit) qtyLine, callOrderLineTempModel.UniqueId);
            }
        } else {
            throw new PromotionException(getContext().getString(R.string.error_saving_request));
        }
        // TODO: 4/9/2018 add bulk unit
    }

    private long insertOrUpdateOrderLineQtyTemp(DiscreteUnit qtyLine, UUID orderLineUniqueId) throws ValidationException, DbException {
        CallOrderLinesQtyTempManager callOrderLinesQtyTempManager = new CallOrderLinesQtyTempManager(getContext());
        CallOrderLinesQtyTempModel qtyTemp = callOrderLinesQtyTempManager.getItem(CallOrderLinesQtyTempManager.getOrderQtyDetail(orderLineUniqueId, qtyLine.ProductUnitId));
        if (qtyTemp != null) {
            qtyTemp.Qty = qtyLine.getQty();
            return callOrderLinesQtyTempManager.update(qtyTemp);
        } else {
            qtyTemp = new CallOrderLinesQtyTempModel();
            qtyTemp.UniqueId = UUID.randomUUID();
            qtyTemp.Qty = qtyLine.getQty();
            qtyTemp.OrderLineUniqueId = orderLineUniqueId;
            qtyTemp.ProductUnitId = qtyLine.ProductUnitId;
            return callOrderLinesQtyTempManager.insert(qtyTemp);
        }
    }
}

