package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.vaslibrary.model.call.ReturnLineQtyRequest;
import com.varanegar.vaslibrary.model.call.ReturnLineQtyRequestModel;
import com.varanegar.vaslibrary.model.call.ReturnLineQtyRequestModelRepository;

import java.util.List;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 8/2/2017.
 */

public class ReturnLineQtyRequestManager extends BaseManager<ReturnLineQtyRequestModel> {
    public ReturnLineQtyRequestManager(@NonNull Context context) {
        super(context, new ReturnLineQtyRequestModelRepository());
    }

    public List<ReturnLineQtyRequestModel> getQtyLines(UUID returnLineId) {
        Query query = new Query();
        query.from(ReturnLineQtyRequest.ReturnLineQtyRequestTbl)
                .whereAnd(Criteria.equals(ReturnLineQtyRequest.ReturnLineUniqueId, returnLineId.toString()));
        return getItems(query);
    }
}
