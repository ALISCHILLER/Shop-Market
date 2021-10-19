package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.vaslibrary.model.DistWarehouseProductQtyView;
import com.varanegar.vaslibrary.model.DistWarehouseProductQtyViewModel;
import com.varanegar.vaslibrary.model.DistWarehouseProductQtyViewModelRepository;

/**
 * Created by A.Jafarzadeh on 3/29/2020.
 */

public class DistWarehouseProductQtyViewManager extends BaseManager<DistWarehouseProductQtyViewModel> {
    public DistWarehouseProductQtyViewManager(@NonNull Context context) {
        super(context, new DistWarehouseProductQtyViewModelRepository());
    }

    public static Query search(@Nullable String key, @Nullable boolean inStock, boolean print) {
        Query query = new Query().from(DistWarehouseProductQtyView.DistWarehouseProductQtyViewTbl);
        if (key != null && !key.isEmpty()) {
            key = HelperMethods.persian2Arabic(key);
            key = HelperMethods.convertToEnglishNumbers(key);
            query.whereAnd(Criteria.contains(DistWarehouseProductQtyView.ProductName, key)
                    .or(Criteria.contains(DistWarehouseProductQtyView.ProductCode, key))
            );
        }
        if (inStock) {
//            if (print) {
//                return query.whereAnd(Criteria.greaterThan(DistWarehouseProductQtyView.OnHandQty, 0)).orderByDescending(DistWarehouseProductQtyView.OnHandQty);
//            } else {
//                return query.whereAnd(Criteria.greaterThan(DistWarehouseProductQtyView.OnHandQty, 0).or(Criteria.greaterThan(DistWarehouseProductQtyView.WasteReturnQty, 0)).or(Criteria.greaterThan(DistWarehouseProductQtyView.WellReturnQty, 0))).orderByDescending(DistWarehouseProductQtyView.OnHandQty);
//        } 
            return query.whereAnd(Criteria.greaterThan(DistWarehouseProductQtyView.WarehouseProductQty, 0)).orderByDescending(DistWarehouseProductQtyView.OnHandQty, DistWarehouseProductQtyView.WellReturnQty, DistWarehouseProductQtyView.WasteReturnQty);
        } else
            return query.whereAnd(Criteria.greaterThan(DistWarehouseProductQtyView.OnHandQty, 0).or(Criteria.greaterThan(DistWarehouseProductQtyView.WasteReturnQty, 0)).or(Criteria.greaterThan(DistWarehouseProductQtyView.WellReturnQty, 0))).orderByDescending(DistWarehouseProductQtyView.OnHandQty, DistWarehouseProductQtyView.WellReturnQty, DistWarehouseProductQtyView.WasteReturnQty);
    }
}
