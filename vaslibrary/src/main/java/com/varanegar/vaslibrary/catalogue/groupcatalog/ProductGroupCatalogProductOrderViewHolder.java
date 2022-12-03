package com.varanegar.vaslibrary.catalogue.groupcatalog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.component.cutemessagedialog.CuteMessageDialog;
import com.varanegar.framework.util.component.cutemessagedialog.Icon;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.OnHandQtyManager;
import com.varanegar.vaslibrary.manager.OrderLineQtyManager;
import com.varanegar.vaslibrary.manager.ProductType;
import com.varanegar.vaslibrary.manager.ProductUnitViewManager;
import com.varanegar.vaslibrary.manager.ProductUnitsViewManager;
import com.varanegar.vaslibrary.manager.customercall.CallOrderLineManager;
import com.varanegar.vaslibrary.manager.productorderviewmanager.OnHandQtyError;
import com.varanegar.vaslibrary.manager.productorderviewmanager.OnHandQtyWarning;
import com.varanegar.vaslibrary.manager.productorderviewmanager.ProductOrderViewManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderView;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderViewModel;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderViewModelRepository;
import com.varanegar.vaslibrary.model.onhandqty.OnHandQtyStock;
import com.varanegar.vaslibrary.model.orderLineQtyModel.OrderLineQtyModel;
import com.varanegar.vaslibrary.model.productorderview.ProductOrderViewModel;
import com.varanegar.vaslibrary.model.productunitsview.ProductUnitsViewModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.ui.calculator.BaseUnit;
import com.varanegar.vaslibrary.ui.calculator.CalculatorHelper;
import com.varanegar.vaslibrary.ui.calculator.DiscreteUnit;
import com.varanegar.vaslibrary.ui.calculator.ordercalculator.BatchQty;
import com.varanegar.vaslibrary.ui.calculator.ordercalculator.CalculatorBatchUnits;
import com.varanegar.vaslibrary.ui.calculator.ordercalculator.OrderCalculatorForm;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Torabi on 8/5/2017.
 */

public class ProductGroupCatalogProductOrderViewHolder extends BaseViewHolder<ProductOrderViewModel> {
    private final TextView productNameTextView;
    private final TextView productTotalQtyTextView;
    private final View deleteView;
    private final UUID customerId;
    private final TextView productPriceTextView;
    private final TextView stockLevelTextView;
    private final SysConfigModel orderPointCheckType;
    private final SysConfigModel showStockLevel;
    private final TextView customerPriceTextView;
    private UUID callOrderId;

    public ProductGroupCatalogProductOrderViewHolder(View itemView, BaseRecyclerAdapter<ProductOrderViewModel> recyclerAdapter, Context context, SysConfigModel showStockLevel, SysConfigModel orderPointCheckType, UUID callOrderId, UUID customerId) {
        super(itemView, recyclerAdapter, context);
        this.showStockLevel = showStockLevel;
        this.orderPointCheckType = orderPointCheckType;
        productNameTextView = (TextView) itemView.findViewById(R.id.product_name_text_view);
        productTotalQtyTextView = (TextView) itemView.findViewById(R.id.product_total_qty_text_view);
        productPriceTextView = (TextView) itemView.findViewById(R.id.product_price_text_view);
        stockLevelTextView = (TextView) itemView.findViewById(R.id.stock_level_text_view);
        customerPriceTextView = (TextView) itemView.findViewById(R.id.product_customer_price_text_view);


        deleteView = itemView.findViewById(R.id.delete_image_view);
        this.callOrderId = callOrderId;
        this.customerId = customerId;
    }

    @Override
    public void bindView(int position) {
        final ProductOrderViewModel productOrderViewModel = recyclerAdapter.get(position);

        if (productOrderViewModel == null)
            return;


        productNameTextView.setText(productOrderViewModel.ProductCode);
        productPriceTextView.setText(HelperMethods.currencyToString(productOrderViewModel.Price));
        customerPriceTextView.setText(HelperMethods.currencyToString(productOrderViewModel.UserPrice));


        if (productOrderViewModel.OnHandQty == null)
            productOrderViewModel.OnHandQty = BigDecimal.ZERO;
        if (productOrderViewModel.RemainedAfterReservedQty == null)
            productOrderViewModel.RemainedAfterReservedQty = BigDecimal.ZERO;
        if (productOrderViewModel.ProductTotalOrderedQty == null)
            productOrderViewModel.ProductTotalOrderedQty = BigDecimal.ZERO;
        if (productOrderViewModel.TotalQty == null)
            productOrderViewModel.TotalQty = BigDecimal.ZERO;
        if (productOrderViewModel.Price == null)
            productOrderViewModel.Price = Currency.ZERO;
        if (productOrderViewModel.OrderPoint == null)
            productOrderViewModel.OrderPoint = BigDecimal.ZERO;


        BigDecimal remained = productOrderViewModel.OnHandQty.subtract(productOrderViewModel.ProductTotalOrderedQty);
        if (SysConfigManager.compare(showStockLevel, true)) {
            itemView.findViewById(R.id.stock_level_layout).setVisibility(View.VISIBLE);
            if (SysConfigManager.compare(orderPointCheckType, OnHandQtyManager.OrderPointCheckType.ShowQty)) {
                if (productOrderViewModel.OnHandQty.compareTo(productOrderViewModel.OrderPoint) <= 0) {
                    stockLevelTextView.setTextColor(getContext().getResources().getColor(R.color.red));
                    stockLevelTextView.setText(R.string.multiplication_sign);
                } else {
                    if (remained.compareTo(productOrderViewModel.OrderPoint) >= 0) {
                        stockLevelTextView.setTextColor(getContext().getResources().getColor(R.color.green));
                        stockLevelTextView.setText(HelperMethods.bigDecimalToString(productOrderViewModel.OnHandQty));
                    } else {
                        stockLevelTextView.setTextColor(getContext().getResources().getColor(R.color.orange));
                        stockLevelTextView.setText(HelperMethods.bigDecimalToString(productOrderViewModel.OnHandQty));
                    }
                }
            } else {
                if (productOrderViewModel.OnHandQty.compareTo(productOrderViewModel.OrderPoint) <= 0) {
                    stockLevelTextView.setTextColor(getContext().getResources().getColor(R.color.red));
                    stockLevelTextView.setText(R.string.multiplication_sign);
                } else {
                    if (remained.compareTo(productOrderViewModel.OrderPoint) >= 0) {
                        stockLevelTextView.setTextColor(getContext().getResources().getColor(R.color.green));
                        stockLevelTextView.setText(R.string.check_sign);
                    } else {
                        stockLevelTextView.setTextColor(getContext().getResources().getColor(R.color.orange));
                        stockLevelTextView.setText(R.string.multiplication_sign);
                    }
                }
            }
        } else {
            itemView.findViewById(R.id.stock_level_layout).setVisibility(View.GONE);
        }


        productTotalQtyTextView.setText(productOrderViewModel.TotalQty == null ? "0" : productOrderViewModel.TotalQty.toString());
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemView.setClickable(false);
                if (productOrderViewModel.UniqueId == null) {
                    Timber.wtf("ProductUnitId of product order view is null");
                } else {
                    CustomerCallOrderOrderViewModel customerCallOrderOrderViewModel = new CustomerCallOrderOrderViewModelRepository().getItem(
                            new Query().from(CustomerCallOrderOrderView.CustomerCallOrderOrderViewTbl)
                                    .whereAnd(Criteria.equals(CustomerCallOrderOrderView.OrderUniqueId, callOrderId))
                                    .whereAnd(Criteria.equals(CustomerCallOrderOrderView.ProductId, productOrderViewModel.UniqueId))
                    );
                    List<OrderLineQtyModel> orderLineQtyModels = new ArrayList<>();
                    OrderLineQtyManager orderLineQtyManager = new OrderLineQtyManager(getContext());
                    if (customerCallOrderOrderViewModel != null)
                        orderLineQtyModels = orderLineQtyManager
                                .getQtyLines(customerCallOrderOrderViewModel.UniqueId);
                    OrderCalculatorForm orderCalculatorForm = new OrderCalculatorForm();
                    try {
                        final OnHandQtyStock onHandQtyStock = new OnHandQtyStock();
                        ProductUnitsViewManager productUnitsViewManager = new ProductUnitsViewManager(getContext());
                        ProductUnitsViewModel productUnitsViewModel = productUnitsViewManager.getItem(productOrderViewModel.UniqueId);
                        onHandQtyStock.ConvertFactors = productUnitsViewModel.ConvertFactor;
                        onHandQtyStock.UnitNames = productUnitsViewModel.UnitName;
                        if (productOrderViewModel.OnHandQty == null)
                            productOrderViewModel.OnHandQty = BigDecimal.ZERO;
                        onHandQtyStock.OnHandQty = productOrderViewModel.OnHandQty;
                        if (productOrderViewModel.RemainedAfterReservedQty == null)
                            productOrderViewModel.RemainedAfterReservedQty = BigDecimal.ZERO;
                        onHandQtyStock.RemainedAfterReservedQty = productOrderViewModel.RemainedAfterReservedQty;
                        if (productOrderViewModel.OrderPoint == null)
                            productOrderViewModel.OrderPoint = BigDecimal.ZERO;
                        onHandQtyStock.OrderPoint = productOrderViewModel.OrderPoint;
                        if (productOrderViewModel.ProductTotalOrderedQty == null)
                            productOrderViewModel.ProductTotalOrderedQty = BigDecimal.ZERO;
                        onHandQtyStock.ProductTotalOrderedQty = productOrderViewModel.ProductTotalOrderedQty;
                        if (productOrderViewModel.RequestBulkQty == null)
                            onHandQtyStock.TotalQty = productOrderViewModel.TotalQty == null ? BigDecimal.ZERO : productOrderViewModel.TotalQty;
                        else
                            onHandQtyStock.TotalQty = productOrderViewModel.TotalQtyBulk == null ? BigDecimal.ZERO : productOrderViewModel.TotalQtyBulk;
                        onHandQtyStock.HasAllocation = productOrderViewModel.HasAllocation;
                        CalculatorHelper calculatorHelper = new CalculatorHelper(getContext());
                        BaseUnit bulkUnit = calculatorHelper.getBulkQtyUnit(customerCallOrderOrderViewModel);
                        if (productOrderViewModel.ExpDate == null)
                            orderCalculatorForm.setArguments(productOrderViewModel.UniqueId, productOrderViewModel.ProductName,
                                    calculatorHelper.generateCalculatorUnits(productOrderViewModel.UniqueId,
                                            orderLineQtyModels, bulkUnit, ProductType.isForSale),
                                    productOrderViewModel.Price, productOrderViewModel.UserPrice,
                                    onHandQtyStock, customerId, callOrderId,productOrderViewModel.PrizeComment);
                        else
                            orderCalculatorForm.setArguments(productOrderViewModel.UniqueId,
                                    productOrderViewModel.ProductName, CalculatorBatchUnits.generate(getContext(),
                                            productOrderViewModel,
                                            customerCallOrderOrderViewModel == null ? null : customerCallOrderOrderViewModel.UniqueId,
                                            productOrderViewModel.Price, productOrderViewModel.PriceId, productOrderViewModel.UserPrice),
                                    productOrderViewModel.UserPrice, onHandQtyStock, customerId, callOrderId
                                    ,productOrderViewModel.PrizeComment);
                        orderCalculatorForm.onCalcFinish = new OrderCalculatorForm.OnCalcFinish() {
                            @Override
                            public void run(List<DiscreteUnit> discreteUnits, BaseUnit bulkUnit, @Nullable List<BatchQty> batchQtyList) {
                                onAdd(productOrderViewModel, discreteUnits, bulkUnit, batchQtyList, onHandQtyStock);
                            }
                        };
                        orderCalculatorForm.show(recyclerAdapter.getActivity().getSupportFragmentManager(), "dc38bc80-72d4-4f10-8a1b-0d6c02e663bf");
                    } catch (ProductUnitViewManager.UnitNotFoundException e) {
                        Toast.makeText(getContext(), R.string.no_unit_for_product, Toast.LENGTH_SHORT).show();
                        Timber.e(e, "product unit not found in product group fragment");
                    }
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (itemView != null)
                            itemView.setClickable(true);
                    }
                }, 1500);
            }
        });
        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            CallOrderLineManager callOrderLineManager = new CallOrderLineManager(getContext());
                            callOrderLineManager.deleteProduct(callOrderId, productOrderViewModel.UniqueId, false);
                            ProductOrderViewManager productOrderViewManager = new ProductOrderViewManager(getContext());
                            ProductOrderViewModel updatedProductOrderViewModel = productOrderViewManager.getItem(ProductOrderViewManager.get(productOrderViewModel.UniqueId, customerId, callOrderId, false));
                            productOrderViewModel.ProductTotalOrderedQty = updatedProductOrderViewModel.ProductTotalOrderedQty;
                            productOrderViewModel.TotalQty = BigDecimal.ZERO;
                            productOrderViewModel.Qty = "";
                            recyclerAdapter.notifyDataSetChanged();
                        } catch (Exception ex) {
                            Timber.e(ex);
                        }
                    }
                });
                builder.setNegativeButton(R.string.no, null);
                builder.setMessage(R.string.are_you_sure);
                builder.show();
            }
        });

        if (productOrderViewModel.TotalQty != null && productOrderViewModel.TotalQty.compareTo(BigDecimal.ZERO) > 0)
            deleteView.setVisibility(View.VISIBLE);
        else
            deleteView.setVisibility(View.GONE);

    }

    private void onAdd(final ProductOrderViewModel productOrderViewModel,
                       final List<DiscreteUnit> discreteUnits, final BaseUnit bulkUnit,
                       @Nullable final List<BatchQty> batchQtyList,
                       @NonNull OnHandQtyStock onHandQtyStock) {

        try {
            ProductOrderViewManager.checkOnHandQty(getContext(), onHandQtyStock, discreteUnits, bulkUnit);
            add(productOrderViewModel, discreteUnits, bulkUnit, batchQtyList);
        } catch (OnHandQtyWarning ex) {
            Timber.e(ex);
            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
            dialog.setTitle(R.string.warning);
            dialog.setMessage(ex.getMessage());
            dialog.setIcon(Icon.Warning);
            dialog.setPositiveButton(R.string.reserved_qty_error, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        add(productOrderViewModel, discreteUnits, bulkUnit, batchQtyList);
                    } catch (Exception e) {
                        Timber.e(e);
                        CuteMessageDialog dialog = new CuteMessageDialog(getContext());
                        dialog.setTitle(R.string.error);
                        dialog.setMessage(R.string.error_saving_request);
                        dialog.setIcon(Icon.Error);
                        dialog.setPositiveButton(R.string.ok, null);
                        dialog.show();
                    }
                }
            });
            dialog.setNegativeButton(R.string.cancel, null);
            dialog.show();
        } catch (OnHandQtyError ex) {
            Timber.e(ex);
            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
            dialog.setTitle(R.string.error);
            dialog.setMessage(ex.getMessage());
            dialog.setIcon(Icon.Error);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        } catch (Exception ex) {
            Timber.e(ex);
            CuteMessageDialog dialog = new CuteMessageDialog(getContext());
            dialog.setTitle(R.string.error);
            dialog.setMessage(R.string.error_saving_request);
            dialog.setIcon(Icon.Error);
            dialog.setPositiveButton(R.string.ok, null);
            dialog.show();
        }
    }

    private void add(final ProductOrderViewModel productOrderViewModel, List<DiscreteUnit> discreteUnits, BaseUnit bulkUnit, @Nullable List<BatchQty> batchQtyList) throws ValidationException, DbException {
        CallOrderLineManager callOrderLineManager = new CallOrderLineManager(getContext());
        callOrderLineManager.addOrUpdateQty(productOrderViewModel.UniqueId, discreteUnits, bulkUnit, callOrderId, null, batchQtyList, false);
        ProductOrderViewManager productOrderViewManager = new ProductOrderViewManager(getContext());
        ProductOrderViewModel updatedProductOrderViewModel = productOrderViewManager.getItem(ProductOrderViewManager.get(productOrderViewModel.UniqueId, customerId, callOrderId, false));
        productOrderViewModel.TotalQty = updatedProductOrderViewModel.TotalQty;
        productOrderViewModel.Qty = updatedProductOrderViewModel.Qty;
        productOrderViewModel.UnitName = updatedProductOrderViewModel.UnitName;
        productOrderViewModel.ProductTotalOrderedQty = updatedProductOrderViewModel.ProductTotalOrderedQty;
        recyclerAdapter.notifyDataSetChanged();
    }

}
