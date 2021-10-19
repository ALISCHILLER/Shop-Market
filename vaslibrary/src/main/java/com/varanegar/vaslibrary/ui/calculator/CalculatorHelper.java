package com.varanegar.vaslibrary.ui.calculator;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.Linq;
import com.varanegar.vaslibrary.manager.OrderLineQtyManager;
import com.varanegar.vaslibrary.manager.ProductType;
import com.varanegar.vaslibrary.manager.ProductUnitViewManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.UnknownBackOfficeException;
import com.varanegar.vaslibrary.model.BaseQtyModel;
import com.varanegar.vaslibrary.model.RequestItemLines.RequestLineModel;
import com.varanegar.vaslibrary.model.customerCallOrderOrderView.CustomerCallOrderOrderViewModel;
import com.varanegar.vaslibrary.model.orderLineQtyModel.OrderLineQtyModel;
import com.varanegar.vaslibrary.model.product.ProductModel;
import com.varanegar.vaslibrary.model.productUnitView.ProductUnitViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 7/26/2017.
 */

public final class CalculatorHelper {
    private final Context context;

    public CalculatorHelper(Context context) {
        this.context = context;
    }

    protected BaseQtyModel[] toArray(@NonNull List<? extends BaseQtyModel> qtyModels) {
        BaseQtyModel[] arr = new BaseQtyModel[qtyModels.size()];
        return qtyModels.toArray(arr);
    }

    public CalculatorUnits generateCalculatorUnits(@NonNull UUID productId, @NonNull List<? extends BaseQtyModel> qtyList, @Nullable BaseUnit requestBulkUnit, ProductType productType) throws ProductUnitViewManager.UnitNotFoundException {
        BaseQtyModel[] qtys = toArray(qtyList);
        final List<ProductUnitViewModel> productUnits = new ProductUnitViewManager(context).getProductUnits(productId, productType);
        final List<ProductUnitViewModel> discreteProductUnits = Linq.findAll(productUnits, new Linq.Criteria<ProductUnitViewModel>() {
            @Override
            public boolean run(ProductUnitViewModel item) {
                return item.Decimal == 0;
            }
        });
        List<DiscreteUnit> units = new ArrayList<>();
        for (final ProductUnitViewModel productUnitViewModel :
                discreteProductUnits) {
            DiscreteUnit discreteUnit = new DiscreteUnit();
            discreteUnit.ConvertFactor = productUnitViewModel.ConvertFactor;
            discreteUnit.ProductUnitId = productUnitViewModel.UniqueId;
            discreteUnit.Name = productUnitViewModel.UnitName;
            if (productType == ProductType.isForReturn)
                discreteUnit.IsDefault = productUnitViewModel.IsReturnDefault;
            else
                discreteUnit.IsDefault = productUnitViewModel.IsDefault;
            BaseQtyModel qtyModel = Linq.findFirst(qtys, new Linq.Criteria<BaseQtyModel>() {
                @Override
                public boolean run(BaseQtyModel item) {
                    return item.ProductUnitId.equals(productUnitViewModel.UniqueId);
                }
            });
            if (qtyModel != null) {
                discreteUnit.value = HelperMethods.bigDecimalToDouble(qtyModel.Qty);
            }
            units.add(discreteUnit);
        }


        final ProductUnitViewModel bulkProductUnit = Linq.findFirst(productUnits, new Linq.Criteria<ProductUnitViewModel>() {
            @Override
            public boolean run(ProductUnitViewModel item) {
                return item.Decimal > 0;
            }
        });
        BaseUnit bulkUnit = null;
        if (bulkProductUnit != null) {
            bulkUnit = new BaseUnit();
            if (requestBulkUnit != null)
                bulkUnit.value = HelperMethods.bigDecimalToDouble(requestBulkUnit.getQty());
            bulkUnit.ProductUnitId = bulkProductUnit.UniqueId;
            bulkUnit.Name = bulkProductUnit.UnitName;
            if (productType == ProductType.isForReturn)
                bulkUnit.IsDefault = bulkProductUnit.IsReturnDefault;
            else
                bulkUnit.IsDefault = bulkProductUnit.IsDefault;
            try {
                BackOfficeType backOfficeType = new SysConfigManager(context).getBackOfficeType();
                if (discreteProductUnits.size()>0 && VaranegarApplication.is(VaranegarApplication.AppId.PreSales) && backOfficeType == BackOfficeType.Rastak)
                    bulkUnit.Readonly = true;
            } catch (UnknownBackOfficeException e) {
                e.printStackTrace();
            }

        }
        return new CalculatorUnits(units, bulkUnit);

    }

    public CalculatorUnits generateCalculatorUnits(@NonNull UUID productId, ProductType productType, boolean supportBulk) throws ProductUnitViewManager.UnitNotFoundException {
        final List<ProductUnitViewModel> productUnits = new ProductUnitViewManager(context).getProductUnits(productId, productType);
        final List<ProductUnitViewModel> discreteProductUnits = Linq.findAll(productUnits, new Linq.Criteria<ProductUnitViewModel>() {
            @Override
            public boolean run(ProductUnitViewModel item) {
                return item.Decimal == 0;
            }
        });
        final ProductUnitViewModel bulkProductUnit = Linq.findFirst(productUnits, new Linq.Criteria<ProductUnitViewModel>() {
            @Override
            public boolean run(ProductUnitViewModel item) {
                return item.Decimal > 0;
            }
        });

        List<DiscreteUnit> units = new ArrayList<>();
        for (final ProductUnitViewModel productUnitViewModel :
                discreteProductUnits) {
            DiscreteUnit discreteUnit = new DiscreteUnit();
            discreteUnit.ConvertFactor = productUnitViewModel.ConvertFactor;
            discreteUnit.ProductUnitId = productUnitViewModel.UniqueId;
            discreteUnit.Name = productUnitViewModel.UnitName;
            if (productType == ProductType.isForReturn)
                discreteUnit.IsDefault = productUnitViewModel.IsReturnDefault;
            else
                discreteUnit.IsDefault = productUnitViewModel.IsDefault;
            units.add(discreteUnit);
        }
        BaseUnit bulkUnit = null;
        if (bulkProductUnit != null && supportBulk) {
            bulkUnit = new BaseUnit();
            bulkUnit.ProductUnitId = bulkProductUnit.UniqueId;
            bulkUnit.Name = bulkProductUnit.UnitName;
            if (productType == ProductType.isForReturn)
                bulkUnit.IsDefault = bulkProductUnit.IsReturnDefault;
            else
                bulkUnit.IsDefault = bulkProductUnit.IsDefault;
            try {
                BackOfficeType backOfficeType = new SysConfigManager(context).getBackOfficeType();
                if (discreteProductUnits.size()>0 && VaranegarApplication.is(VaranegarApplication.AppId.PreSales) && backOfficeType == BackOfficeType.Rastak)
                    bulkUnit.Readonly = true;
            } catch (UnknownBackOfficeException e) {
                e.printStackTrace();
            }
        }
        return new CalculatorUnits(units, bulkUnit);
    }

    @Nullable
    public BaseUnit getBulkQtyUnit(@Nullable CustomerCallOrderOrderViewModel customerCallOrderOrderViewModel) throws ProductUnitViewManager.UnitNotFoundException {
        if (customerCallOrderOrderViewModel == null)
            return null;
        if (customerCallOrderOrderViewModel.RequestBulkQty != null) {
            ProductUnitViewManager productUnitManager = new ProductUnitViewManager(context);
            ProductUnitViewModel productUnitModel = productUnitManager.getUnit(customerCallOrderOrderViewModel.RequestBulkQtyUnitUniqueId);
            BaseUnit bulkUnit = new BaseUnit();
            bulkUnit.ProductUnitId = customerCallOrderOrderViewModel.RequestBulkQtyUnitUniqueId;
            bulkUnit.Unit = productUnitModel.UnitName;
            bulkUnit.IsDefault = productUnitModel.IsDefault;
            bulkUnit.value = HelperMethods.bigDecimalToDouble(customerCallOrderOrderViewModel.RequestBulkQty);
            return bulkUnit;
        } else {
            OrderLineQtyManager orderLineQtyManager = new OrderLineQtyManager(context);
            List<OrderLineQtyModel> qtyModels = orderLineQtyManager.getQtyLines(customerCallOrderOrderViewModel.UniqueId);
            if (qtyModels.size() == 1) {
                OrderLineQtyModel orderLineQtyModel = qtyModels.get(0);
                ProductUnitViewManager productUnitManager = new ProductUnitViewManager(context);
                ProductUnitViewModel productUnitModel = productUnitManager.getUnit(orderLineQtyModel.ProductUnitId);
                if (productUnitModel.Decimal != 0) {
                    BaseUnit bulkUnit = new BaseUnit();
                    bulkUnit.ProductUnitId = qtyModels.get(0).ProductUnitId;
                    bulkUnit.Unit = productUnitModel.UnitName;
                    bulkUnit.IsDefault = productUnitModel.IsDefault;
                    bulkUnit.value = HelperMethods.bigDecimalToDouble(orderLineQtyModel.Qty);
                    return bulkUnit;
                }
            }
            return null;
        }
    }

    @Nullable
    public BaseUnit getBulkQtyUnit(@Nullable RequestLineModel requestLineModel) throws ProductUnitViewManager.UnitNotFoundException {
        if (requestLineModel == null)
            return null;
        if (requestLineModel.BulkQty != null) {
            ProductUnitViewManager productUnitManager = new ProductUnitViewManager(context);
            ProductUnitViewModel productUnitModel = productUnitManager.getUnit(requestLineModel.BulkQtyUnitUniqueId);
            BaseUnit bulkUnit = new BaseUnit();
            bulkUnit.ProductUnitId = requestLineModel.BulkQtyUnitUniqueId;
            bulkUnit.Unit = productUnitModel.UnitName;
            bulkUnit.IsDefault = productUnitModel.IsDefault;
            bulkUnit.value = HelperMethods.bigDecimalToDouble(requestLineModel.BulkQty);
            return bulkUnit;
        } else {
            OrderLineQtyManager orderLineQtyManager = new OrderLineQtyManager(context);
            List<OrderLineQtyModel> qtyModels = orderLineQtyManager.getQtyLines(requestLineModel.UniqueId);
            if (qtyModels.size() == 1) {
                OrderLineQtyModel orderLineQtyModel = qtyModels.get(0);
                ProductUnitViewManager productUnitManager = new ProductUnitViewManager(context);
                ProductUnitViewModel productUnitModel = productUnitManager.getUnit(orderLineQtyModel.ProductUnitId);
                if (productUnitModel.Decimal != 0) {
                    BaseUnit bulkUnit = new BaseUnit();
                    bulkUnit.ProductUnitId = qtyModels.get(0).ProductUnitId;
                    bulkUnit.Unit = productUnitModel.UnitName;
                    bulkUnit.IsDefault = productUnitModel.IsDefault;
                    bulkUnit.value = HelperMethods.bigDecimalToDouble(orderLineQtyModel.Qty);
                    return bulkUnit;
                }
            }
            return null;
        }
    }

}
