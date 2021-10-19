package com.varanegar.vaslibrary.manager.Target;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.vaslibrary.model.targetsalemanproductview.TargetSaleManProductView;
import com.varanegar.vaslibrary.model.targetsalemanproductview.TargetSaleManProductViewModel;
import com.varanegar.vaslibrary.model.targetsalemanproductview.TargetSaleManProductViewModelRepository;

/**
 * Created by A.Jafarzadeh on 1/4/2018.
 */

public class TargetSaleManProductViewManager extends BaseManager<TargetSaleManProductViewModel> {
    public TargetSaleManProductViewManager(@NonNull Context context) {
        super(context, new TargetSaleManProductViewModelRepository());
    }

    public static Query getAll() {
        Query query = new Query();
        query.from(TargetSaleManProductView.TargetSaleManProductViewTbl);
        return query;
    }
}
