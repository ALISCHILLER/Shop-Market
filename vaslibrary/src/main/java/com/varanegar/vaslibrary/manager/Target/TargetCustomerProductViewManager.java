package com.varanegar.vaslibrary.manager.Target;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.vaslibrary.model.targetcustomerproductview.TargetCustomerProductView;
import com.varanegar.vaslibrary.model.targetcustomerproductview.TargetCustomerProductViewModel;
import com.varanegar.vaslibrary.model.targetcustomerproductview.TargetCustomerProductViewModelRepository;

/**
 * Created by A.Jafarzadeh on 1/4/2018.
 */

public class TargetCustomerProductViewManager extends BaseManager<TargetCustomerProductViewModel> {
    public TargetCustomerProductViewManager(@NonNull Context context) {
        super(context, new TargetCustomerProductViewModelRepository());
    }

    public static Query getAll() {
        Query query = new Query();
        query.from(TargetCustomerProductView.TargetCustomerProductViewTbl);
        return query;
    }
}
