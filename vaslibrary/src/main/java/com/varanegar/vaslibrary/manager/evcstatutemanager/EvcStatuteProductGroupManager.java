package com.varanegar.vaslibrary.manager.evcstatutemanager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.BaseRepository;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.vaslibrary.model.evcstatute.EvcStatuteProductGroup;
import com.varanegar.vaslibrary.model.evcstatute.EvcStatuteProductGroupModel;
import com.varanegar.vaslibrary.model.evcstatute.EvcStatuteProductGroupModelRepository;
import com.varanegar.vaslibrary.model.evcstatute.EvcStatuteProductModel;

import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 11/20/2017.
 */

public class EvcStatuteProductGroupManager extends BaseManager<EvcStatuteProductGroupModel> {
    public EvcStatuteProductGroupManager(@NonNull Context context) {
        super(context, new EvcStatuteProductGroupModelRepository());
    }

    public List<EvcStatuteProductGroupModel> getItems(UUID templateId) {
        Query query = new Query().from(EvcStatuteProductGroup.EvcStatuteProductGroupTbl)
                .whereAnd(Criteria.equals(EvcStatuteProductGroup.TemplateId, templateId.toString()));
        return getItems(query);
    }
}
