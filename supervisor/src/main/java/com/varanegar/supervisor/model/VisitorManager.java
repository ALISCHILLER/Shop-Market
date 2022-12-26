package com.varanegar.supervisor.model;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.BaseRepository;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;

import java.util.List;

/**
 * Created by A.Torabi on 7/8/2018.
 */

public class VisitorManager extends BaseManager<VisitorModel> {
    public VisitorManager(@NonNull Context context) {
        super(context, new VisitorModelRepository());
    }
    public List<VisitorModel> getAll(){
        return getItems(new Query().from(Visitor.VisitorTbl));
    }


    public VisitorModel getVisitor(String visitorId){
        return getItem(new Query().from(Visitor.VisitorTbl)
                .whereAnd(Criteria.contains(Visitor.UniqueId,visitorId)));
    }
}
