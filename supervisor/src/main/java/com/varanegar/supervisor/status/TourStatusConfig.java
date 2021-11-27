package com.varanegar.supervisor.status;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.supervisor.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by A.Torabi on 7/3/2018.
 */

class TourStatusConfig {

    private Date fromdate;
    private Date todate;
    List<TourStatusOption> tourStatusOptions = new ArrayList<>();
    List<RequestStatusOption> requestStatusOptions = new ArrayList<>();
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences shared;
    private final Context context;
    private String statusType;

    TourStatusConfig(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("TourStatusConfig", Context.MODE_PRIVATE);
        shared= context.getSharedPreferences("TourStatus", Context.MODE_PRIVATE);
        boolean ReadyToSend = sharedPreferences.getBoolean(OptionId.ReadyToSend.name(), false);
        tourStatusOptions.add(new TourStatusOption(OptionId.ReadyToSend, context.getString(R.string.ready_to_send), ReadyToSend));

        boolean Sent = sharedPreferences.getBoolean(OptionId.Sent.name(), true);
        tourStatusOptions.add(new TourStatusOption(OptionId.Sent, context.getString(R.string.sent), Sent));

//        boolean InProgress = sharedPreferences.getBoolean(OptionId.InProgress.name(), true);
     //   tourStatusOptions.add(new TourStatusOption(OptionId.InProgress, context.getString(R.string.in_progress), InProgress));

        boolean Received = sharedPreferences.getBoolean(OptionId.Received.name(), true);
        tourStatusOptions.add(new TourStatusOption(OptionId.Received, context.getString(R.string.received), Received));

        boolean Finished = sharedPreferences.getBoolean(OptionId.Finished.name(), false);
        tourStatusOptions.add(new TourStatusOption(OptionId.Finished, context.getString(R.string.finished), Finished));

        boolean Canceled = sharedPreferences.getBoolean(OptionId.Canceled.name(), false);
        tourStatusOptions.add(new TourStatusOption(OptionId.Canceled, context.getString(R.string.canceled), Canceled));

        boolean Deactivated = sharedPreferences.getBoolean(OptionId.Deactivated.name(), false);
        tourStatusOptions.add(new TourStatusOption(OptionId.Deactivated, context.getString(R.string.Deactivated), Deactivated));


        boolean Confirmed = sharedPreferences.getBoolean(OptionId.Verified.name(), true);
        requestStatusOptions.add(new RequestStatusOption(OptionId.Verified, context.getString(R.string.confirmed), Confirmed,"Verified"));

        boolean NotConfirmed = sharedPreferences.getBoolean(OptionId.NotVerified.name(), true);
       // requestStatusOptions.add(new RequestStatusOption(OptionId.NotVerified, context.getString(R.string.not_confirmed), NotConfirmed,"NotVerified"));

        boolean Revoked = sharedPreferences.getBoolean(OptionId.InProgress.name(), false);
        requestStatusOptions.add(new RequestStatusOption(OptionId.InProgress, context.getString(R.string.not_stustos), Revoked,"InProgress"));

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
        this.statusType = sharedPreferences.getString("type", "Request");
    }

    public List<TourStatusOption> getTourStatusOptions() {
        return tourStatusOptions;
    }

    public HashMap<OptionId, TourStatusOption> getMapOfTourStatusOptions() {
        HashMap<OptionId, TourStatusOption> map = new HashMap<>();
        for (TourStatusOption option :
                tourStatusOptions) {
            map.put(option.id, option);
        }
        return map;
    }

    public List<RequestStatusOption> getRequestStatusOptions() {
        return requestStatusOptions;
    }

    public HashMap<OptionId, RequestStatusOption> getMapOfRequestStatusOptions() {
        HashMap<OptionId, RequestStatusOption> map = new HashMap<>();
        for (RequestStatusOption option :
                requestStatusOptions) {
            map.put(option.id, option);
        }
        return map;
    }

    public void saveTourOption(TourStatusOption option) {
        sharedPreferences.edit().putBoolean(option.id.name(), option.value).apply();
    }

    public void saveRequestOption(RequestStatusOption option) {
        sharedPreferences.edit().putBoolean(option.id.name(), option.value).apply();
        shared.edit().putString(option.id.name(), option.nameid).apply();
    }

    public void setFromDate(@NonNull Date date) {
        fromdate = date;
        sharedPreferences.edit().putLong("fromconfigtime", new Date().getTime()).apply();
        sharedPreferences.edit().putLong("fromdate", date.getTime()).apply();
    }

    public void setToDate(@NonNull Date date) {
        todate = date;
        sharedPreferences.edit().putLong("toconfigtime", new Date().getTime()).apply();
        sharedPreferences.edit().putLong("todate", date.getTime()).apply();
    }

    @Nullable
    public Date getFromDate() {
        return fromdate;
    }

    @Nullable
    public Date getToDate() {
        return todate;
    }

    public void setStatusType(String type) {
        this.statusType = type;
        sharedPreferences.edit().putString("type", type).apply();
    }

    public String getStatusType() {
        if (this.statusType != null) {
            return this.statusType;
        }
        return sharedPreferences.getString("type", "Request");
    }
}
