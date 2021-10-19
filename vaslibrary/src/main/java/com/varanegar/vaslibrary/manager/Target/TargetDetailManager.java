package com.varanegar.vaslibrary.manager.Target;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.BaseRepository;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.vaslibrary.model.target.TargetDetail;
import com.varanegar.vaslibrary.model.target.TargetDetailModel;
import com.varanegar.vaslibrary.model.target.TargetDetailModelRepository;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 12/16/2017.
 */

public class TargetDetailManager extends BaseManager<TargetDetailModel> {
    public TargetDetailManager(@NonNull Context context) {
        super(context, new TargetDetailModelRepository());
    }

    public static Query getDetail(UUID targetUniqueId) {
        Query query = new Query();
        query.from(TargetDetail.TargetDetailTbl).whereAnd(Criteria.equals(TargetDetail.TargetMasterUniqueId, targetUniqueId));
        return query;
    }
}
