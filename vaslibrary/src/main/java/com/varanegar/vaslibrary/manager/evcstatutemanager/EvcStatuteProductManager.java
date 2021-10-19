package com.varanegar.vaslibrary.manager.evcstatutemanager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.vaslibrary.model.evcstatute.EvcStatuteProduct;
import com.varanegar.vaslibrary.model.evcstatute.EvcStatuteProductModel;
import com.varanegar.vaslibrary.model.evcstatute.EvcStatuteProductModelRepository;

import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 11/20/2017.
 */

public class EvcStatuteProductManager extends BaseManager<EvcStatuteProductModel> {
    public EvcStatuteProductManager(@NonNull Context context) {
        super(context, new EvcStatuteProductModelRepository());
    }

    public List<EvcStatuteProductModel> getItems(UUID templateId) {
        Query query = new Query().from(EvcStatuteProduct.EvcStatuteProductTbl)
                .whereAnd(Criteria.equals(EvcStatuteProduct.TemplateId, templateId.toString()));
        return getItems(query);
    }
}
