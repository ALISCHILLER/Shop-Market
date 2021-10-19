package com.varanegar.vaslibrary.manager.customercall;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.vaslibrary.model.call.temporder.CallOrderLinesQtyTemp;
import com.varanegar.vaslibrary.model.call.temporder.CallOrderLinesQtyTempModel;
import com.varanegar.vaslibrary.model.call.temporder.CallOrderLinesQtyTempModelRepository;
import com.varanegar.vaslibrary.model.call.temporder.CallOrderLinesTemp;
import com.varanegar.vaslibrary.model.call.temporder.CallOrderLinesTempModel;
import com.varanegar.vaslibrary.model.call.temporder.CallOrderLinesTempModelRepository;

import java.util.List;
import java.util.UUID;

/**
 * Created by e.hashemzadeh on 1/25/2021.
 */
public class CallOrderLinesTempManager extends BaseManager<CallOrderLinesTempModel> {
    public CallOrderLinesTempManager(@NonNull Context context) {
        super(context, new CallOrderLinesTempModelRepository());
    }

    public List<CallOrderLinesTempModel> getLines(UUID callOrderId) {
        Query query = new Query().
                from(CallOrderLinesTemp.CallOrderLinesTempTbl);
        if (callOrderId != null)
            query = query.whereAnd(Criteria.equals(CallOrderLinesTemp.OrderUniqueId, callOrderId.toString()));
        return getItems(query);
    }

}
