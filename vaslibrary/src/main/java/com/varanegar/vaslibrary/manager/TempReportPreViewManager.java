package com.varanegar.vaslibrary.manager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.vaslibrary.model.tempreportpreview.TempReportPreView;
import com.varanegar.vaslibrary.model.tempreportpreview.TempReportPreViewModel;
import com.varanegar.vaslibrary.model.tempreportpreview.TempReportPreViewModelRepository;

/**
 * Created by A.Jafarzadeh on 7/17/2017.
 */

public class TempReportPreViewManager extends BaseManager<TempReportPreViewModel> {
    public TempReportPreViewManager(@NonNull Context context) {
        super(context, new TempReportPreViewModelRepository());
    }

    public static Query getAll()
    {
        Query query = new Query();
        query.from(TempReportPreView.TempReportPreViewTbl);
        return query;
    }
}
