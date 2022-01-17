package com.varanegar.supervisor.customreport.orderstatus;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Date;
import java.util.UUID;

public class OrderReportConfig {
    private final Context context;
    private final SharedPreferences sharedPreferences;
    //    private boolean allvisitors;
    private Date fromdate;
    private Date todate;
    private int mreport;


    public OrderReportConfig(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("ReportConfig", Context.MODE_PRIVATE);
        long fromConfigTime = sharedPreferences.getLong("fromconfigtime", 0);
        long toConfigTime = sharedPreferences.getLong("toconfigtime", 0);
        if (new Date().getDay() == new Date(fromConfigTime).getDay()) {
            long f = sharedPreferences.getLong("fromdate", 0);
            fromdate = f == 0 ? null : new Date(f);
        } else {
            fromdate = new Date();
        }
        if (new Date().getDay() == new Date(toConfigTime).getDay()) {
            long t = sharedPreferences.getLong("todate", 0);
            todate = t == 0 ? null : new Date(t);
        } else {
            todate = new Date();
        }

        // selectedVisitors = sharedPreferences.getStringSet("visitors", new HashSet<String>());
        // allvisitors = sharedPreferences.getBoolean("allvisitors", false);
    }

    public void setFromDate(@NonNull Date date) {
        fromdate = date;
        sharedPreferences.edit().putLong("fromdate", date.getTime()).apply();
        sharedPreferences.edit().putLong("fromconfigtime", new Date().getTime()).apply();
    }

    public void setToDate(@NonNull Date date) {
        todate = date;
        sharedPreferences.edit().putLong("todate", date.getTime()).apply();
        sharedPreferences.edit().putLong("toconfigtime", new Date().getTime()).apply();
    }

    public void setReportitem( int report){
        sharedPreferences.edit().putInt("report",report).apply();
    }
    @Nullable
    public Date getFromDate() {
        return fromdate;
    }

    @Nullable
    public Date getToDate() {
        return todate;
    }

    @Nullable
    public int getToReport() {
        mreport=sharedPreferences.getInt("report",0);
        return mreport;
    }
    public void selectVisitor(@NonNull UUID uniqueId) {
        // selectedVisitors.add(uniqueId.toString());
        sharedPreferences.edit().putString("visitor", uniqueId.toString()).apply();
    }

    public String getSelectedVisitorId() {
        return sharedPreferences.getString("visitor", null);
    }

    /*
    public void selectAllVisitors() {
        allvisitors = true;
        sharedPreferences.edit().putBoolean("allvisitors", allvisitors).apply();
    }

    public void deSelectAllVisitors() {
        allvisitors = false;
        selectedVisitors = new HashSet<>();
        sharedPreferences.edit().remove("visitors").apply();
        sharedPreferences.edit().putBoolean("allvisitors", allvisitors).apply();
    }

    public boolean isAllVisitorsSelected() {
        return allvisitors;
    }

    public void deselectVisitor(@NonNull UUID userId) {
        selectedVisitors.remove(userId.toString());
        sharedPreferences.edit().putStringSet("visitors", selectedVisitors).apply();
    }
     */
}
