package com.varanegar.supervisor.status;

import android.content.Context;

import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.supervisor.model.reviewreport.ReviewreportModel;
import com.varanegar.supervisor.model.reviewreport.ReviewreportModelRepository;
import com.varanegar.supervisor.model.reviewreport.ReviewreportView;

import java.util.List;

public class ToursStatusManager  extends BaseManager<ReviewreportModel> {
    public ToursStatusManager(@NonNull Context context) {
        super(context,new ReviewreportModelRepository());
    }
    public List<ReviewreportModel> getAll(){
        return getItems(new Query().from(ReviewreportView.ReviewreportTbl));
    }
}
