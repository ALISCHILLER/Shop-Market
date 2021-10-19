package com.varanegar.vaslibrary.manager.productrequest;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.BaseRepository;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.vaslibrary.model.RequestItemLines.RequestLine;
import com.varanegar.vaslibrary.model.RequestItemLines.RequestLineQty;
import com.varanegar.vaslibrary.model.RequestItemLines.RequestLineQtyModel;
import com.varanegar.vaslibrary.model.RequestItemLines.RequestLineQtyModelRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 3/25/2018.
 */

public class RequestLineQtyManager extends BaseManager<RequestLineQtyModel> {
    public RequestLineQtyManager(@NonNull Context context) {
        super(context, new RequestLineQtyModelRepository());
    }

    public static Query getRequestQtyDetail(UUID productUnitId) {
        Query query = new Query();
        query.from(RequestLineQty.RequestLineQtyTbl)
                .whereAnd(Criteria.equals(RequestLineQty.ProductUnitId, productUnitId.toString()));
        return query;
    }

    public List<RequestLineQtyModel> getQtyLines(UUID requestLineId) {
        if (requestLineId == null)
            return new ArrayList<>();
        Query query = new Query();
        query.from(RequestLineQty.RequestLineQtyTbl)
                .whereAnd(Criteria.equals(RequestLineQty.RequestLineUniqueId, requestLineId.toString()));
        return getItems(query);
    }
}
