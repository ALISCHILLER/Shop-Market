package com.varanegar.vaslibrary.manager.customeractiontimemanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.model.customeractiontime.CustomerActionTime;
import com.varanegar.vaslibrary.model.customeractiontime.CustomerActionTimeModel;
import com.varanegar.vaslibrary.model.customeractiontime.CustomerActionTimeModelRepository;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 8/6/2017.
 */

public class CustomerActionTimeManager extends BaseManager<CustomerActionTimeModel> {


    public CustomerActionTimeManager(@NonNull Context context) {
        super(context, new CustomerActionTimeModelRepository());
    }

    public Date save(@NonNull UUID customerId, @NonNull CustomerActions action) throws ValidationException, DbException {
        Date now = new Date();
        CustomerActionTimeModel log = getItem(new Query().from(CustomerActionTime.CustomerActionTimeTbl)
                .whereAnd(Criteria.equals(CustomerActionTime.Action, action.ordinal())
                        .and(Criteria.equals(CustomerActionTime.CustomerId, customerId.toString()))));
        if (log == null) {
            log = new CustomerActionTimeModel();
            log.UniqueId = UUID.randomUUID();
            log.CustomerId = customerId;
            log.Action = action;
        }
        log.Date = now;
        insertOrUpdate(log);
        return now;
    }

    @Nullable
    public Date get(@NonNull UUID customerId, @NonNull CustomerActions action) {
        CustomerActionTimeModel log = getItem(new Query().from(CustomerActionTime.CustomerActionTimeTbl)
                .whereAnd(Criteria.equals(CustomerActionTime.CustomerId, customerId.toString())
                        .and(Criteria.equals(CustomerActionTime.Action, action.ordinal()))));
        return log == null ? null : log.Date;
    }

    public void delete(UUID customerId, CustomerActions action) throws DbException {
        delete(Criteria.equals(CustomerActionTime.CustomerId, customerId.toString())
                .and(Criteria.equals(CustomerActionTime.Action, action.ordinal())));
    }


    public interface TimerCallBack {
        void onUpdate(long timeOffset, String timeOffsetStr);
    }

    private static Thread timerThread;
    private static final Object lock = new Object();

    public static void startVisitTimer(@NonNull final Context context, @NonNull final UUID customerId, @Nullable final TimerCallBack callBack) {
        if (timerThread != null)
            timerThread.interrupt();
        timerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                long timeOffset = getTimeOffset(context, customerId);
                while (!Thread.interrupted()) {
                    SystemClock.sleep(1000);
                    timeOffset++;
                    setTimeOffset(context, customerId, timeOffset);
                    final String str = DateHelper.getTimeSpanString(timeOffset);
                    if (callBack != null)
                        callBack.onUpdate(timeOffset, str);
                }
                synchronized (lock) {
                    lock.notify();
                }
            }
        });
        timerThread.start();
    }

    public static void stopVisitTimer() {
        if (timerThread != null)
            timerThread.interrupt();
    }

    public static void clearVisitTimer(@NonNull final Context context, @NonNull final UUID customerId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (lock) {
                        stopVisitTimer();
                        lock.wait();
                        removeTimeOffset(context, customerId);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static void setTimeOffset(@NonNull Context context, @NonNull UUID customerId, long timeOffset) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("CustomerActionTimeManager", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(customerId.toString(), timeOffset);
        editor.apply();
    }

    private static void removeTimeOffset(@NonNull Context context, @NonNull UUID customerId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("CustomerActionTimeManager", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(customerId.toString());
        editor.apply();
    }

    public static void removeTimeOffset(@NonNull Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("CustomerActionTimeManager", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    private static long getTimeOffset(@NonNull Context context, @NonNull UUID customerId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("CustomerActionTimeManager", Context.MODE_PRIVATE);
        return sharedPreferences.getLong(customerId.toString(), 0);
    }

    public static long getCustomerCallTimes(@NonNull Context context) {
        try {
            long time = 0;
            SharedPreferences sharedPreferences = context.getSharedPreferences("CustomerActionTimeManager", Context.MODE_PRIVATE);
            Map<String, ?> keys = sharedPreferences.getAll();
            for (Map.Entry<String, ?> entry : keys.entrySet()) {
                time += Long.parseLong(entry.getValue().toString());
            }
            return time;
        } catch (Exception ex) {
            return 0;
        }
    }

    public static long getCustomerCallTime(@NonNull Context context, @NonNull UUID customerId) {
        return getTimeOffset(context, customerId);
    }
}
