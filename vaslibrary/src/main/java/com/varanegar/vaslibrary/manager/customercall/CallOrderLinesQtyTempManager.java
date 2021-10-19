package com.varanegar.vaslibrary.manager.customercall;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.vaslibrary.model.call.temporder.CallOrderLinesQtyTemp;
import com.varanegar.vaslibrary.model.call.temporder.CallOrderLinesQtyTempModel;
import com.varanegar.vaslibrary.model.call.temporder.CallOrderLinesQtyTempModelRepository;

import java.util.List;
import java.util.UUID;

/**
 * Created by e.hashemzadeh on 1/25/2021.
 */
public class CallOrderLinesQtyTempManager extends BaseManager<CallOrderLinesQtyTempModel> {
    public CallOrderLinesQtyTempManager(@NonNull Context context) {
        super(context, new CallOrderLinesQtyTempModelRepository());
    }

    public List<CallOrderLinesQtyTempModel> getLines(UUID callOrderLineUniqueId) {
        Query query = new Query().from(CallOrderLinesQtyTemp.CallOrderLinesQtyTempTbl);
        if (callOrderLineUniqueId != null)
            query = query.whereAnd(Criteria.equals(CallOrderLinesQtyTemp.OrderLineUniqueId, callOrderLineUniqueId.toString()));
        return getItems(query);
    }

    public static Query getOrderQtyDetail(UUID callOrderLineUniqueId, UUID productUnitId) {
        Query query = new Query().from(CallOrderLinesQtyTemp.CallOrderLinesQtyTempTbl);
        if (callOrderLineUniqueId != null && productUnitId != null)
            query = query.whereAnd(Criteria.equals(CallOrderLinesQtyTemp.OrderLineUniqueId, callOrderLineUniqueId.toString()))
                    .whereAnd(Criteria.equals(CallOrderLinesQtyTemp.ProductUnitId, productUnitId.toString()));
        return query;
    }
}
