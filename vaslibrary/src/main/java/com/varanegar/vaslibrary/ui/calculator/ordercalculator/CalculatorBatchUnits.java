package com.varanegar.vaslibrary.ui.calculator.ordercalculator;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.Nullable;

import com.varanegar.framework.util.Linq;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.base.VasHelperMethods;
import com.varanegar.vaslibrary.manager.CallOrderLineBatchQtyDetailManager;
import com.varanegar.vaslibrary.manager.CustomerBatchPriceManager;
import com.varanegar.vaslibrary.manager.ProductManager;
import com.varanegar.vaslibrary.manager.ProductType;
import com.varanegar.vaslibrary.manager.ProductUnitViewManager;
import com.varanegar.vaslibrary.model.CallOrderLineBatchQtyDetailModel;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderViewModel;
import com.varanegar.vaslibrary.model.customerbatchprice.CustomerBatchPriceModel;
import com.varanegar.vaslibrary.model.product.ProductModel;
import com.varanegar.vaslibrary.model.productUnitView.ProductUnitViewModel;
import com.varanegar.vaslibrary.model.productorderview.ProductOrderViewModel;
import com.varanegar.vaslibrary.ui.calculator.CalculatorHelper;
import com.varanegar.vaslibrary.ui.calculator.CalculatorUnits;
import com.varanegar.vaslibrary.ui.calculator.DiscreteUnit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by a.jafarzadeh on 7/22/2018.
 */

public class CalculatorBatchUnits {
    public CalculatorUnits calculatorUnits;
    public int BatchRef;
    public String ExpireDate;
    public BigDecimal BatchOnHandQty;
    public Currency Price;
    public String BatchNo;
    public int ItemRef;
    public UUID PriceId;
    public Currency UserPrice;

    public BigDecimal getTotalQty() {
        if (calculatorUnits == null)
            return BigDecimal.ZERO;
        else
            return calculatorUnits.getTotalQty();
    }



    public static List<CalculatorBatchUnits> generate(Context context, ProductOrderViewModel productOrderViewModel, @Nullable UUID orderLineId, Currency price, UUID priceId, Currency userPrice) throws ProductUnitViewManager.UnitNotFoundException {
        SharedPreferences sharedPreferences = context.getSharedPreferences("InStockProducts", Context.MODE_PRIVATE);
        boolean inStock = sharedPreferences.getBoolean("InStock", false);

        CallOrderLineBatchQtyDetailManager callOrderLineBatchQtyDetailManager = new CallOrderLineBatchQtyDetailManager(context);
        List<CallOrderLineBatchQtyDetailModel> batchQtys = null;
        if (orderLineId != null)
            batchQtys = callOrderLineBatchQtyDetailManager.getBatchLines(orderLineId);

        ProductManager productManager = new ProductManager(context);
        ProductModel productModel = productManager.getItem(productOrderViewModel.UniqueId);

        ProductUnitViewManager productUnitViewManager = new ProductUnitViewManager(context);
        List<ProductUnitViewModel> unitViewModels = productUnitViewManager.getProductUnits(productModel.UniqueId, ProductType.isForSale);

        List<CalculatorBatchUnits> list = new ArrayList<>();
        String[] dates = productOrderViewModel.ExpDate.split(":");
        String[] onHandQtys = productOrderViewModel.BatchOnHandQty.split(":");
        String[] batchRefs = productOrderViewModel.BatchRef.split(":");
        String[] batchNos = productOrderViewModel.BatchNo.split(":");
        CustomerBatchPriceManager customerBatchPriceManager = new CustomerBatchPriceManager(context);
        List<CustomerBatchPriceModel> customerBatchPriceModels = customerBatchPriceManager.getCustomerBatchPrice(productOrderViewModel.CustomerId);
        HashMap<Integer, CustomerBatchPriceModel> batchHashMap = new HashMap<>();
        for (CustomerBatchPriceModel customerBatchPriceModel: customerBatchPriceModels) {
            batchHashMap.put(customerBatchPriceModel.BatchRef, customerBatchPriceModel);
        }
        for (int i = 0; i < batchRefs.length; i++) {
            final int batchRef = Integer.parseInt(batchRefs[i]);
            CalculatorBatchUnits calculatorBatchUnits = new CalculatorBatchUnits();
            calculatorBatchUnits.ExpireDate = dates[i];
            calculatorBatchUnits.BatchOnHandQty = new BigDecimal(onHandQtys[i]);
            calculatorBatchUnits.BatchRef = batchRef;
            calculatorBatchUnits.BatchNo = batchNos[i];
            calculatorBatchUnits.Price = batchHashMap.get(batchRef) == null ? Currency.ZERO : batchHashMap.get(batchRef).Price;
            calculatorBatchUnits.UserPrice = batchHashMap.get(batchRef) == null ? Currency.ZERO : batchHashMap.get(batchRef).UserPrice;
            calculatorBatchUnits.PriceId = batchHashMap.get(batchRef) == null ? UUID.fromString("00000000-0000-0000-0000-000000000000") :batchHashMap.get(batchRef).PriceId;
            if (calculatorBatchUnits.Price == null || calculatorBatchUnits.Price.compareTo(Currency.ZERO) == 0) {
                calculatorBatchUnits.Price = price;
                calculatorBatchUnits.PriceId = priceId;
                calculatorBatchUnits.UserPrice = userPrice;
            }
            CalculatorHelper calculatorHelper = new CalculatorHelper(context);
            CalculatorUnits calculatorUnits = calculatorHelper.generateCalculatorUnits(productModel.UniqueId, ProductType.isForSale, true);
            if (orderLineId != null) {
                CallOrderLineBatchQtyDetailModel batch = Linq.findFirst(batchQtys, new Linq.Criteria<CallOrderLineBatchQtyDetailModel>() {
                    @Override
                    public boolean run(CallOrderLineBatchQtyDetailModel item) {
                        return item.BatchRef == batchRef;
                    }
                });
                if (batch != null) {
                    if (calculatorUnits.getBulkUnit() != null && calculatorUnits.getDiscreteUnits().size() == 0) {
                        calculatorUnits.getBulkUnit().value = batch.Qty.doubleValue();
                        calculatorUnits.setUnits(calculatorUnits.getDiscreteUnits(), calculatorUnits.getBulkUnit());
                    } else if (calculatorUnits.getBulkUnit() != null) {
                        final List<ProductUnitViewModel> discreteProductUnits = Linq.findAll(unitViewModels, new Linq.Criteria<ProductUnitViewModel>() {
                            @Override
                            public boolean run(ProductUnitViewModel item) {
                                return item.Decimal == 0;
                            }
                        });
                        List<DiscreteUnit> units = VasHelperMethods.chopTotalQty(batch.Qty, discreteProductUnits , false);
                        calculatorUnits.getBulkUnit().value = batch.Qty.doubleValue();
                        calculatorUnits.setUnits(units, calculatorUnits.getBulkUnit());
                    } else {
                        List<DiscreteUnit> units = VasHelperMethods.chopTotalQty(batch.Qty, unitViewModels , false);
                        calculatorUnits.setUnits(units, null);
                    }
                }
            }
            calculatorBatchUnits.calculatorUnits = calculatorUnits;
            inStock = true; // tebghe sohbate aghaye ahmadi felan faghat batch hayi neshun dade mishe ke mojudi daran
            if (inStock) {
                if (new BigDecimal(onHandQtys[i]).compareTo(BigDecimal.ZERO)>0)
                    list.add(calculatorBatchUnits);
            } else
                list.add(calculatorBatchUnits);
        }
        if (list.size() == 0) {
            final int batchRef = Integer.parseInt(batchRefs[0]);
            CalculatorBatchUnits calculatorBatchUnits = new CalculatorBatchUnits();
            calculatorBatchUnits.ExpireDate = dates[0];
            calculatorBatchUnits.BatchOnHandQty = new BigDecimal(onHandQtys[0]);
            calculatorBatchUnits.BatchRef = batchRef;
            calculatorBatchUnits.BatchNo = batchNos[0];
            calculatorBatchUnits.Price = batchHashMap.get(batchRef) == null ? Currency.ZERO : batchHashMap.get(batchRef).Price;
            calculatorBatchUnits.UserPrice = batchHashMap.get(batchRef) == null ? Currency.ZERO : batchHashMap.get(batchRef).UserPrice;
            calculatorBatchUnits.PriceId = batchHashMap.get(batchRef) == null ? UUID.fromString("00000000-0000-0000-0000-000000000000") :batchHashMap.get(batchRef).PriceId;
            if (calculatorBatchUnits.Price != null && calculatorBatchUnits.Price.compareTo(Currency.ZERO) == 0) {
                calculatorBatchUnits.Price = price;
                calculatorBatchUnits.PriceId = priceId;
            }
            CalculatorHelper calculatorHelper = new CalculatorHelper(context);
            CalculatorUnits calculatorUnits = calculatorHelper.generateCalculatorUnits(productModel.UniqueId, ProductType.isForSale, true);
            if (orderLineId != null) {
                CallOrderLineBatchQtyDetailModel batch = Linq.findFirst(batchQtys, new Linq.Criteria<CallOrderLineBatchQtyDetailModel>() {
                    @Override
                    public boolean run(CallOrderLineBatchQtyDetailModel item) {
                        return item.BatchRef == batchRef;
                    }
                });
                if (batch != null) {
                    List<DiscreteUnit> units = VasHelperMethods.chopTotalQty(batch.Qty, unitViewModels , false);
                    calculatorUnits.setUnits(units, null);
                }
            }
            calculatorBatchUnits.calculatorUnits = calculatorUnits;
            list.add(calculatorBatchUnits);
        }
        return list;
    }

    public static List<CalculatorBatchUnits> generate(Context context, CustomerCallOrderOrderViewModel callOrderOrderViewModel) throws ProductUnitViewManager.UnitNotFoundException {
        SharedPreferences sharedPreferences = context.getSharedPreferences("InStockProducts", Context.MODE_PRIVATE);
        boolean inStock = sharedPreferences.getBoolean("InStock", false);

        CallOrderLineBatchQtyDetailManager callOrderLineBatchQtyDetailManager = new CallOrderLineBatchQtyDetailManager(context);
        List<CallOrderLineBatchQtyDetailModel> batchQtys = null;
        batchQtys = callOrderLineBatchQtyDetailManager.getBatchLines(callOrderOrderViewModel.UniqueId);

        ProductManager productManager = new ProductManager(context);
        ProductModel productModel = productManager.getItem(callOrderOrderViewModel.ProductId);

        ProductUnitViewManager productUnitViewManager = new ProductUnitViewManager(context);
        List<ProductUnitViewModel> unitViewModels = productUnitViewManager.getProductUnits(productModel.UniqueId, ProductType.isForSale);

        List<CalculatorBatchUnits> list = new ArrayList<>();
        String[] dates = callOrderOrderViewModel.ExpDate.split(":");
        String[] onHandQtys = callOrderOrderViewModel.BatchOnHandQty.split(":");
        String[] batchRefs = callOrderOrderViewModel.BatchRef.split(":");
        String[] batchNos = callOrderOrderViewModel.BatchNo.split(":");
        CustomerBatchPriceManager customerBatchPriceManager = new CustomerBatchPriceManager(context);
        List<CustomerBatchPriceModel> customerBatchPriceModels = customerBatchPriceManager.getCustomerBatchPrice(callOrderOrderViewModel.CustomerUniqueId);
        HashMap<Integer, CustomerBatchPriceModel> batchHashMap = new HashMap<>();
        for (CustomerBatchPriceModel customerBatchPriceModel: customerBatchPriceModels) {
            batchHashMap.put(customerBatchPriceModel.BatchRef, customerBatchPriceModel);
        }
        for (int i = 0; i < batchRefs.length; i++) {
            final int batchRef = Integer.parseInt(batchRefs[i]);
            CalculatorBatchUnits calculatorBatchUnits = new CalculatorBatchUnits();
            calculatorBatchUnits.ExpireDate = dates[i];
            calculatorBatchUnits.BatchOnHandQty = new BigDecimal(onHandQtys[i]);
            calculatorBatchUnits.BatchRef = batchRef;
            calculatorBatchUnits.BatchNo = batchNos[i];
            calculatorBatchUnits.Price = batchHashMap.get(batchRef) == null ? Currency.ZERO : batchHashMap.get(batchRef).Price;
            calculatorBatchUnits.UserPrice = batchHashMap.get(batchRef) == null ? Currency.ZERO : batchHashMap.get(batchRef).UserPrice;
            calculatorBatchUnits.PriceId = batchHashMap.get(batchRef) == null ? UUID.fromString("00000000-0000-0000-0000-000000000000") :batchHashMap.get(batchRef).PriceId;
            CalculatorHelper calculatorHelper = new CalculatorHelper(context);
            CalculatorUnits calculatorUnits = calculatorHelper.generateCalculatorUnits(productModel.UniqueId, ProductType.isForSale, true);
            CallOrderLineBatchQtyDetailModel batch = Linq.findFirst(batchQtys, new Linq.Criteria<CallOrderLineBatchQtyDetailModel>() {
                @Override
                public boolean run(CallOrderLineBatchQtyDetailModel item) {
                    return item.BatchRef == batchRef;
                }
            });
            if (batch != null) {
                List<DiscreteUnit> units = VasHelperMethods.chopTotalQty(batch.Qty, unitViewModels , false);
                calculatorUnits.setUnits(units, null);
            }
            calculatorBatchUnits.calculatorUnits = calculatorUnits;
            inStock = true; // tebghe sohbate aghaye ahmadi felan faghat batch hayi neshun dade mishe ke mojudi daran
            if (inStock) {
                if (new BigDecimal(onHandQtys[i]).compareTo(BigDecimal.ZERO)>0)
                    list.add(calculatorBatchUnits);
            } else
                list.add(calculatorBatchUnits);
        }
        return list;
    }
}
