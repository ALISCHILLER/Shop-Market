package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.vaslibrary.model.invoiceLineQty.InvoiceLineQty;
import com.varanegar.vaslibrary.model.invoiceLineQty.InvoiceLineQtyModel;
import com.varanegar.vaslibrary.model.invoiceLineQty.InvoiceLineQtyModelRepository;

import java.util.List;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 8/2/2017.
 */

public class InvoiceLineQtyManager extends BaseManager<InvoiceLineQtyModel> {
    public InvoiceLineQtyManager(@NonNull Context context) {
        super(context, new InvoiceLineQtyModelRepository());
    }

    public List<InvoiceLineQtyModel> getQtyLines(UUID orderLineId) {
        Query query = new Query();
        query.from(InvoiceLineQty.InvoiceLineQtyTbl)
                .whereAnd(Criteria.equals(InvoiceLineQty.OrderLineUniqueId, orderLineId.toString()));
        return getItems(query);
    }


    public List<InvoiceLineQtyModel> getAll() {
        Query query = new Query();
        query.from(InvoiceLineQty.InvoiceLineQtyTbl);
       return getItems(query);
    }


    public InvoiceLineQtyModel getLine(UUID orderLineId) {
        Query query = new Query();
        query.from(InvoiceLineQty.InvoiceLineQtyTbl)
                .whereAnd(Criteria.equals(InvoiceLineQty.OrderLineUniqueId, orderLineId.toString())
                        );
        return getItem(query);
    }
}
