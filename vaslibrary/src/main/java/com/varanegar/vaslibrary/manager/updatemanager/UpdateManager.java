package com.varanegar.vaslibrary.manager.updatemanager;


import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.vaslibrary.model.UpdateKey;
import com.varanegar.vaslibrary.model.update.UpdateLog;
import com.varanegar.vaslibrary.model.update.UpdateLogModel;
import com.varanegar.vaslibrary.model.update.UpdateLogModelRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by atp on 12/19/2016.
 */

public class UpdateManager extends BaseManager<UpdateLogModel> {

    public static final Date MIN_DATE = new Date(87, 3, 1);

    public UpdateManager(Context context) {
        super(context, new UpdateLogModelRepository());
    }

    public Date getLog(@NonNull UpdateKey updateKey) {
        UpdateLogModel update = baseRepository.getItem(getLatestUpdate(updateKey));
        if (update != null) {
            return update.Date;
        } else {
            return MIN_DATE;
        }
    }

    public void addLog(@NonNull UpdateKey updateKey) {
        UpdateLogModel model = new UpdateLogModel();
        model.UniqueId = UUID.randomUUID();
        model.Date = Calendar.getInstance().getTime();
        model.Name = updateKey.getName();
        baseRepository.insert(model);
    }

    public void removeLog(@NonNull UpdateKey updateKey) {
        baseRepository.delete(Criteria.equals(UpdateLog.Name, updateKey.getName()));
    }

    private static Query getLatestUpdate(final UpdateKey updateKey) {
        Query query = new Query();
        return query.from(UpdateLog.UpdateLogTbl).whereAnd(Criteria.equals(UpdateLog.Name, updateKey.getName()))
                .orderByDescending(UpdateLog.Date);
    }

    public void saveBarcode(String text) {
        UpdateLogModel updateLogModel = new UpdateLogModel();
        updateLogModel.UniqueId = UUID.fromString("d183e58f-1949-42fa-8798-e3f2573cf341");
        updateLogModel.Name = text;
        updateLogModel.Date = new Date();
        try {
            insertOrUpdate(updateLogModel);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public String readBarcode() {
        UpdateLogModel model = getItem(UUID.fromString("d183e58f-1949-42fa-8798-e3f2573cf341"));
        if (model != null)
            return model.Name;
        else
            return null;
    }

    public void removeBarcode() {
        try {
            delete(UUID.fromString("d183e58f-1949-42fa-8798-e3f2573cf341"));
        } catch (DbException e) {
            Timber.e(e);
        }
    }

    public void saveImageInfoLog (String imageName) {
        UpdateLogModel updateLogModel = new UpdateLogModel();
        updateLogModel.UniqueId = UUID.randomUUID();
        updateLogModel.Name = "IMG_" + imageName;
        updateLogModel.Date = Calendar.getInstance().getTime();
        try {
            insertOrUpdate(updateLogModel);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public Date getImageInfoLog (String imageName) {
        return getLog(new UpdateKey("IMG_" + imageName));
    }

}
