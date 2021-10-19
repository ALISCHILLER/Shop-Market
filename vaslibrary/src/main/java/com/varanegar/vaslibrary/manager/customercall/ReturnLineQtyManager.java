package com.varanegar.vaslibrary.manager.customercall;

import android.content.Context;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.vaslibrary.model.call.ReturnLineQty;
import com.varanegar.vaslibrary.model.call.ReturnLineQtyModel;
import com.varanegar.vaslibrary.model.call.ReturnLineQtyModelRepository;

import java.util.List;
import java.util.UUID;

/**
 * Created by atp on 4/11/2017.
 */

public class ReturnLineQtyManager extends BaseManager<ReturnLineQtyModel> {
    public ReturnLineQtyManager(Context context) {
        super(context ,new ReturnLineQtyModelRepository());
    }

    public static Query getAll(UUID returnLineId) {
        Query query = new Query();
        query.from(ReturnLineQty.ReturnLineQtyTbl).whereAnd(Criteria.equals(ReturnLineQty.ReturnLineUniqueId, returnLineId.toString()));
        return query;
    }

    public static Query getReturnLines(UUID returnLineId)
    {
        Query query = new Query();
        query.from(ReturnLineQty.ReturnLineQtyTbl).whereAnd(Criteria.equals(ReturnLineQty.ReturnLineUniqueId, returnLineId.toString()));
        return query;
    }

    public static Query getQtyLineQuery(UUID returnLineId, UUID productUnitId) {
        Query query = new Query();
        query.from(ReturnLineQty.ReturnLineQtyTbl).whereAnd(Criteria.equals(ReturnLineQty.ReturnLineUniqueId, returnLineId.toString()).and(Criteria.equals(ReturnLineQty.ProductUnitId, productUnitId.toString())));
        return query;
    }

    public List<ReturnLineQtyModel> getQtyLines(UUID returnLineId) {
        return getItems(getAll(returnLineId));
    }

    public ReturnLineQtyModel getQtyLine(UUID returnLineId, UUID productUnitId) {
        return getItem(getQtyLineQuery(returnLineId, productUnitId));
    }

    public Query getProductUnitLines (UUID productUnitId) {
        Query query = new Query();
        query.from(ReturnLineQty.ReturnLineQtyTbl)
                .whereAnd(Criteria.equals(ReturnLineQty.ProductUnitId, productUnitId.toString()));
        return query;
    }
}
