package com.varanegar.vaslibrary.manager.locationmanager;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.vaslibrary.model.TrackingLog;
import com.varanegar.vaslibrary.model.TrackingLogModel;
import com.varanegar.vaslibrary.model.TrackingLogModelRepository;
import com.varanegar.vaslibrary.model.location.Location;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import timber.log.Timber;

public class TrackingLogManager extends BaseManager<TrackingLogModel> {

    public TrackingLogManager(@NonNull Context context) {
        super(context, new TrackingLogModelRepository());
    }

    public static void addLog(@NonNull Context context, @NonNull LogType eventType, @NonNull LogLevel level) {
        TrackingLogManager.addLog(context, eventType, level, null);
    }

    public static void addLog(@NonNull Context context, @NonNull LogType eventType, @NonNull LogLevel level, @StringRes int res) {
        TrackingLogManager.addLog(context, eventType, level, context.getString(res), null);
    }

    public static void addLog(@NonNull Context context, @NonNull LogType eventType, @NonNull LogLevel level, @Nullable String description) {
        TrackingLogManager.addLog(context, eventType, level, description, null);
    }

    public static void addLog(@NonNull Context context, @NonNull LogType eventType, @NonNull LogLevel level, @StringRes int res, @StringRes int trace) {
        TrackingLogManager.addLog(context, eventType, level, context.getString(res), context.getString(trace));
    }

    public static void addLog(@NonNull Context context, @NonNull LogType eventType, @NonNull LogLevel level, @Nullable String description, @Nullable String trace) {
        TrackingLogManager trackingLogManager = new TrackingLogManager(context);
        TrackingLogModel trackingLogModel = new TrackingLogModel();
        trackingLogModel.UniqueId = UUID.randomUUID();
        trackingLogModel.Description = description;
        trackingLogModel.EventType = eventType.toString();
        trackingLogModel.ENTime = DateHelper.toString(new Date(), DateFormat.Complete, Locale.US);
        trackingLogModel.FATime = DateHelper.toString(new Date(), DateFormat.Complete, DateHelper.fa);
        trackingLogModel.Trace = trace;
        trackingLogModel.Level = level.toString();
        trackingLogModel.Time = new Date();
        try {
            trackingLogManager.insert(trackingLogModel);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public List<TrackingLogModel> getLogs(@Nullable String logType, @Nullable String logLevel, @Nullable Date from, @Nullable Date to) {
        Query query = new Query().from(TrackingLog.TrackingLogTbl);
        if (logType != null && logType != "*")
            query.whereAnd(Criteria.contains(TrackingLog.EventType, logType));
        if (logLevel != null && logLevel != "*")
            query.whereAnd(Criteria.equals(TrackingLog.Level, logLevel));
        if (from != null)
            query.whereAnd(Criteria.greaterThan(TrackingLog.Time, from));
        if (to != null)
            query.whereAnd(Criteria.lesserThan(TrackingLog.Time, to));
        return getItems(query);
    }

    public long clearLogs() throws DbException {
        long time = new Date().getTime() - 345600000;
        return delete(Criteria.lesserThan(TrackingLog.Time, new Date(time)));
    }
}
