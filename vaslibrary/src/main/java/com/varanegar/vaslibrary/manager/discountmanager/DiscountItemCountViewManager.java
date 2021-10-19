package com.varanegar.vaslibrary.manager.discountmanager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.vaslibrary.model.discountSDS.DiscountItemCountView;
import com.varanegar.vaslibrary.model.discountSDS.DiscountItemCountViewModel;
import com.varanegar.vaslibrary.model.discountSDS.DiscountItemCountViewModelRepository;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 12/24/2017.
 */

public class DiscountItemCountViewManager extends BaseManager<DiscountItemCountViewModel> {
    public DiscountItemCountViewManager(@NonNull Context context) {
        super(context, new DiscountItemCountViewModelRepository());
    }

    public static Query getAllDiscountItems(int disRef) {
        Query query = new Query();
        query.from(DiscountItemCountView.DiscountItemCountViewTbl).whereAnd(Criteria.equals(DiscountItemCountView.DisRef, disRef));
        return query;
    }
}
