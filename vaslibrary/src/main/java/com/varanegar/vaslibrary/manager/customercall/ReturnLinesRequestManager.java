package com.varanegar.vaslibrary.manager.customercall;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.vaslibrary.model.call.ReturnLinesRequest;
import com.varanegar.vaslibrary.model.call.ReturnLinesRequestModel;
import com.varanegar.vaslibrary.model.call.ReturnLinesRequestModelRepository;

import java.util.List;
import java.util.UUID;

/**
 * Created by s.foroughi on 28/02/2017.
 */

public class ReturnLinesRequestManager extends BaseManager<ReturnLinesRequestModel> {
    public ReturnLinesRequestManager(Context context) {
        super(context, new ReturnLinesRequestModelRepository());
    }

    public List<ReturnLinesRequestModel> getLines(@NonNull UUID returnId) {
        Query query = new Query();
        query.from(ReturnLinesRequest.ReturnLinesRequestTbl).whereAnd(Criteria.equals(ReturnLinesRequest.ReturnUniqueId, returnId.toString()));
        return getItems(query);
    }
    public List<ReturnLinesRequestModel> getSalesItem(@NonNull UUID returnId,UUID productId) {
        Query query = new Query();
        query.from(ReturnLinesRequest.ReturnLinesRequestTbl)
                .whereAnd(Criteria.equals(ReturnLinesRequest.ReturnUniqueId, returnId.toString())
                ).whereAnd(Criteria.equals(ReturnLinesRequest.ProductUniqueId, productId.toString())
                );
        return getItems(query);
    }

}
