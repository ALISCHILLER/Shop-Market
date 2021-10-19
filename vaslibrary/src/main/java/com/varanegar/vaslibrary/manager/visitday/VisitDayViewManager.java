package com.varanegar.vaslibrary.manager.visitday;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.projection.ColumnProjection;
import com.varanegar.framework.database.querybuilder.projection.Projection;
import com.varanegar.vaslibrary.model.visitday.VisitDayView;
import com.varanegar.vaslibrary.model.visitday.VisitDayViewModel;
import com.varanegar.vaslibrary.model.visitday.VisitDayViewModelRepository;

import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 7/22/2018.
 */

public class VisitDayViewManager extends BaseManager<VisitDayViewModel> {
    public VisitDayViewManager(@NonNull Context context) {
        super(context, new VisitDayViewModelRepository());
    }

    public List<VisitDayViewModel> getAll() {
        return getItems(new Query().from(VisitDayView.VisitDayViewTbl).orderByAscending(VisitDayView.RowIndex));
    }

    public int getCustomersCount(@NonNull UUID visitDayPathId) {
        Integer count = VaranegarApplication.getInstance().getDbHandler().getIntegerSingle(new Query().select(ColumnProjection.column(VisitDayView.CustomerCount)).from(VisitDayView.VisitDayViewTbl)
                .whereAnd(Criteria.equals(VisitDayView.UniqueId, visitDayPathId.toString())));
        return count == null ? 0 : count;
    }
}
