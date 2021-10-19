package com.varanegar.vaslibrary.manager.updatemanager;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.vaslibrary.model.tour.TourModel;
import com.varanegar.vaslibrary.model.update.TourUpdateLog;
import com.varanegar.vaslibrary.model.update.TourUpdateLogModel;
import com.varanegar.vaslibrary.model.update.TourUpdateLogModelRepository;

import java.util.Date;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Torabi on 1/24/2018.
 */

public class TourUpdateLogManager extends BaseManager<TourUpdateLogModel> {
    public TourUpdateLogManager(@NonNull Context context) {
        super(context, new TourUpdateLogModelRepository());
    }

    public void save(TourModel tour, String name, String group) throws Exception {
        if (tour == null)
            throw new Exception();

        TourUpdateLogModel updateCallModel = new TourUpdateLogModel();
        updateCallModel.LocalTourId = tour.LocalId;
        updateCallModel.TourId = tour.UniqueId;
        updateCallModel.StartDate = new Date();
        updateCallModel.Name = name;
        updateCallModel.GroupName = group;
        updateCallModel.UniqueId = UUID.randomUUID();
        insert(updateCallModel);
    }

    public void saveError(@NonNull TourModel tour, String name, String group, String error) {
        Query query = new Query();
        query.from(TourUpdateLog.TourUpdateLogTbl)
                .whereAnd(Criteria.equals(TourUpdateLog.GroupName, group)
                        .and(Criteria.equals(TourUpdateLog.Name, name))
                        .and(Criteria.equals(TourUpdateLog.LocalTourId, tour.LocalId.toString())));
        TourUpdateLogModel updateCallModel = getItem(query);
        if (updateCallModel != null) {
            updateCallModel.Error = error;
            updateCallModel.FinishDate = new Date();
            try {
                update(updateCallModel);
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    public void saveSuccess(@NonNull TourModel tour, String name, String group) {
        Query query = new Query();
        query.from(TourUpdateLog.TourUpdateLogTbl)
                .whereAnd(Criteria.equals(TourUpdateLog.GroupName, group)
                        .and(Criteria.equals(TourUpdateLog.Name, name))
                        .and(Criteria.equals(TourUpdateLog.LocalTourId, tour.LocalId.toString())));
        TourUpdateLogModel updateCallModel = getItem(query);
        if (updateCallModel != null) {
            updateCallModel.Error = null;
            updateCallModel.FinishDate = new Date();
            try {
                update(updateCallModel);
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    @Nullable
    public String getLastError(@NonNull TourModel tour) {
        Query query = new Query();
        query.from(TourUpdateLog.TourUpdateLogTbl)
                .whereAnd(Criteria.equals(TourUpdateLog.LocalTourId, tour.LocalId.toString())
                        .and(Criteria.notIsNull(TourUpdateLog.Error)));
        TourUpdateLogModel updateCallModel = getItem(query);
        return updateCallModel == null ? null : updateCallModel.Error;
    }
}
