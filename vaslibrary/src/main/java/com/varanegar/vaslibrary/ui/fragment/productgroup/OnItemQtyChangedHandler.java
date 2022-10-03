package com.varanegar.vaslibrary.ui.fragment.productgroup;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.Linq;
import com.varanegar.vaslibrary.manager.productUnit.UnitOfProductManager;
import com.varanegar.vaslibrary.model.productUnit.UnitOfProductModel;
import com.varanegar.vaslibrary.model.productorderview.ProductOrderViewModel;
import com.varanegar.vaslibrary.ui.calculator.DiscreteUnit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public abstract class OnItemQtyChangedHandler {
    HashMap<UUID, List<ItemInfo>> qtys = new HashMap<>();

    public abstract boolean run(ProductOrderViewModel productOrderViewModel, QtyChange change);

    public synchronized void plusQty(int position, UUID productId, @NonNull final DiscreteUnit unit, Context context) {
        if (qtys.containsKey(productId)) {
            List<ItemInfo> qtyItems = qtys.get(productId);
            ItemInfo qtyItem = Linq.findFirst(qtyItems, new Linq.Criteria<ItemInfo>() {
                @Override
                public boolean run(ItemInfo item) {
                    return item.unit.ProductUnitId.equals(unit.ProductUnitId);
                }
            });
            if (qtyItem == null) {
                qtyItem = new ItemInfo(unit, position, unit.value);
                qtyItems.add(qtyItem);
            } else {
                qtyItem.qty = unit.value;
            }
        } else {
            //find product units
            //add main unit
            //add other units
            List<ItemInfo> l = new ArrayList<>();
            l.add(new ItemInfo(unit, position, unit.value));
            List<UnitOfProductModel> units = new UnitOfProductManager(context).getUnitsOfProduct(productId);
            for (UnitOfProductModel item : units)
                if (!item.productUnitId.equals(unit.ProductUnitId)){
                    DiscreteUnit discreteUnit = new DiscreteUnit();
                    discreteUnit.ConvertFactor =
                            HelperMethods.bigDecimalToDouble(item.ConvertFactor);
                    discreteUnit.IsDefault = item.IsDefault;
                    discreteUnit.Name = item.UnitName;
                    discreteUnit.ProductUnitId = item.productUnitId;
                    discreteUnit.value = 0;
                    l.add(new ItemInfo(discreteUnit, position, discreteUnit.value));
                }

/*            if (otherUnit != null)
                l.add(new ItemInfo(otherUnit, position, otherUnit.value));*/
            qtys.put(productId, l);
        }
    }

    public synchronized void minusQty(int position, UUID productId, @NonNull final DiscreteUnit unit, Context context) {
        if (qtys.containsKey(productId)) {
            List<ItemInfo> qtyItems = qtys.get(productId);
            ItemInfo qtyItem = Linq.findFirst(qtyItems, new Linq.Criteria<ItemInfo>() {
                @Override
                public boolean run(ItemInfo item) {
                    return item.unit.ProductUnitId.equals(unit.ProductUnitId);
                }
            });
            if (qtyItem == null) {
                qtyItem = new ItemInfo(unit, position, unit.value);
                qtyItems.add(qtyItem);
            } else {
                qtyItem.qty = unit.value;
            }
        } else {
            //find product units
            //add main unit
            //add other units
            List<ItemInfo> l = new ArrayList<>();
            l.add(new ItemInfo(unit, position, unit.value));
            List<UnitOfProductModel> units = new UnitOfProductManager(context).getUnitsOfProduct(productId);
            for (UnitOfProductModel item : units)
                if (!item.productUnitId.equals(unit.ProductUnitId)){
                    DiscreteUnit discreteUnit = new DiscreteUnit();
                    discreteUnit.ConvertFactor =
                            HelperMethods.bigDecimalToDouble(item.ConvertFactor);
                    discreteUnit.IsDefault = item.IsDefault;
                    discreteUnit.Name = item.UnitName;
                    discreteUnit.ProductUnitId = item.productUnitId;
                    discreteUnit.value = 0;
                    l.add(new ItemInfo(discreteUnit, position, discreteUnit.value));
                }

/*            if (otherUnit != null)
                l.add(new ItemInfo(otherUnit, position, otherUnit.value));*/
            qtys.put(productId, l);
        }

    }

    public synchronized void start(ProductOrderViewModel productOrderViewModel) {
        for (UUID key : qtys.keySet()) {
            List<ItemInfo> itemInfo = qtys.get(key);
            run(productOrderViewModel, new QtyChange(key, itemInfo.get(0).position, itemInfo));
        }

//        HashMap<UUID, List<ItemInfo>> nQtys = new HashMap<>();
//        iterator = qtys.keySet().iterator();
//        while (iterator.hasNext()) {
//            UUID key = iterator.next();
//            List<ItemInfo> itemInfo = qtys.get(key);
//            if (!list.contains(key))
//                nQtys.put(key, itemInfo);
//        }
//        qtys.clear();
//        qtys.putAll(nQtys);
    }

    public class QtyChange {
        public QtyChange(UUID productId, int position, List<ItemInfo> qtys) {
            this.productId = productId;
            this.position = position;
            discreteUnits = new ArrayList<>();
            for (ItemInfo qty :
                    qtys) {
                DiscreteUnit discreteUnit = new DiscreteUnit();
                discreteUnit.ConvertFactor = qty.unit.ConvertFactor;
                discreteUnit.IsDefault = qty.unit.IsDefault;
                discreteUnit.Name = qty.unit.Name;
                discreteUnit.ProductUnitId = qty.unit.ProductUnitId;
                discreteUnit.value = qty.qty;
                discreteUnits.add(discreteUnit);
            }
        }

        public UUID productId;
        public int position;
        public List<DiscreteUnit> discreteUnits;
    }

    private class ItemInfo {
        public ItemInfo(DiscreteUnit unit, int position, double qty) {
            this.unit = unit;
            this.position = position;
            this.qty = qty;
        }

        public DiscreteUnit unit;
        public int position;
        public double qty;
    }
}
