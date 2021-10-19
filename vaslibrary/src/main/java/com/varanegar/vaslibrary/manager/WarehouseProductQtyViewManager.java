package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.vaslibrary.model.WarehouseProductQtyView.WarehouseProductQtyView;
import com.varanegar.vaslibrary.model.WarehouseProductQtyView.WarehouseProductQtyViewModel;
import com.varanegar.vaslibrary.model.WarehouseProductQtyView.WarehouseProductQtyViewModelRepository;

import java.util.UUID;

/**
 * Created by s.foroughi on 16/01/2017.
 */

public class WarehouseProductQtyViewManager extends BaseManager<WarehouseProductQtyViewModel> {
    public WarehouseProductQtyViewManager(Context context) {
        super(context, new WarehouseProductQtyViewModelRepository());
    }

    public static Query search(@Nullable String key, @Nullable Boolean inStock) {
        Query query = new Query().from(WarehouseProductQtyView.WarehouseProductQtyViewTbl);
        if (key != null && !key.isEmpty()) {
            key = HelperMethods.persian2Arabic(key);
            key = HelperMethods.convertToEnglishNumbers(key);
            query.whereAnd(Criteria.contains(WarehouseProductQtyView.ProductName, key)
                            .or(Criteria.contains(WarehouseProductQtyView.ProductCode, key))
                    );
        }

        if (inStock != null) {
            if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
                if (inStock)
                    return query.whereAnd(Criteria.greaterThan(WarehouseProductQtyView.OnHandQty, 0));
                else
                    return query.whereAnd(Criteria.lesserThanOrEqual(WarehouseProductQtyView.OnHandQty, 0));
            } else {
                if (inStock)
                    return query.whereAnd(Criteria.greaterThan(WarehouseProductQtyView.RemainedQty, 0));
                else
                    return query.whereAnd(Criteria.lesserThanOrEqual(WarehouseProductQtyView.RemainedQty, 0));
            }
        } else
            return query;
    }

    public static Query getAll () {
        Query query = new Query();
        query.from(WarehouseProductQtyView.WarehouseProductQtyViewTbl);
        return query;
    }

    public static Query getWarehouseProductQty (UUID productId) {
        Query query = new Query();
        query.from(WarehouseProductQtyView.WarehouseProductQtyViewTbl);
        query.whereAnd(Criteria.equals(WarehouseProductQtyView.UniqueId, productId));
        return query;
    }
}

