package com.varanegar.supervisor.tracking;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.util.Linq;
import com.varanegar.supervisor.model.VisitorManager;
import com.varanegar.supervisor.model.VisitorModel;
import com.varanegar.supervisor.webapi.StatusType;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by A.Torabi on 6/19/2018.
 */

public class TrackingConfig {
    private final Context context;
    private final SharedPreferences sharedPreferences;
    private Date statusdate;
    private Date trackingdate;
    private int tohour;
    private int tominute;
    private int fromhour;
    private int fromminute;
    private int waitTime;
    private boolean map;
    private boolean tracking;
    private StatusType statusType;

    public TrackingConfig(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("TrackingConfig", Context.MODE_PRIVATE);

        long statusDateConfig = sharedPreferences.getLong("statusdateconfig", 0);
        if (new Date().getDay() == new Date(statusDateConfig).getDay()) {
            long s = sharedPreferences.getLong("statusdate", 0);
            statusdate = s == 0 ? null : new Date(s);
        } else
            statusdate = new Date();

        long trackingDateConfig = sharedPreferences.getLong("trackingdateconfig", 0);
        if (new Date().getDay() == new Date(trackingDateConfig).getDay()) {
            long t = sharedPreferences.getLong("trackingdate", 0);
            trackingdate = t == 0 ? null : new Date(t);
        } else
            trackingdate = new Date();


        long fromTimeConfig = sharedPreferences.getLong("fromtimeconfig", 0);
        if (new Date().getDay() == new Date(fromTimeConfig).getDay()) {
            fromhour = sharedPreferences.getInt("fromhour", -1);
            fromminute = sharedPreferences.getInt("fromminute", -1);

        } else {
            fromhour = 6;
            fromminute = 00;
        }


        long toTimeConfig = sharedPreferences.getLong("totimeconfig", 0);
        if (new Date().getDay() == new Date(toTimeConfig).getDay()) {
            tohour = sharedPreferences.getInt("tohour", -1);
            tominute = sharedPreferences.getInt("tominute", -1);
        } else {
            tohour = 23;
            tominute = 55;
        }
        waitTime = sharedPreferences.getInt("waitTime", 30);
        map = sharedPreferences.getBoolean("map", true);
        tracking = sharedPreferences.getBoolean("tracking", false);
        int type = sharedPreferences.getInt("statusType", 0);
        statusType = type == 0 ? StatusType.Point : StatusType.Event;
    }

    @Nullable
    public Date getStatusDate() {
        return statusdate;
    }

    public void setTrackingDate(@NonNull Date date) {
        trackingdate = date;
        sharedPreferences.edit().putLong("trackingdate", date.getTime()).apply();
        sharedPreferences.edit().putLong("trackingdateconfig", new Date().getTime()).apply();
    }


    public void setStatusDate(@NonNull Date date) {
        statusdate = date;
        sharedPreferences.edit().putLong("statusdate", date.getTime()).apply();
        sharedPreferences.edit().putLong("statusdateconfig", new Date().getTime()).apply();
    }

    @Nullable
    public Date getTrackingDate() {
        return trackingdate;
    }


    public void setToTime(int hour, int minute) {
        tohour = hour;
        tominute = minute;
        sharedPreferences.edit().putInt("tohour", hour).apply();
        sharedPreferences.edit().putInt("tominute", minute).apply();
        sharedPreferences.edit().putLong("totimeconfig", new Date().getTime()).apply();
    }

    public void setFromTime(int hour, int minute) {
        fromhour = hour;
        fromminute = minute;
        sharedPreferences.edit().putInt("fromhour", hour).apply();
        sharedPreferences.edit().putInt("fromminute", minute).apply();
        sharedPreferences.edit().putLong("fromtimeconfig", new Date().getTime()).apply();
    }

    public String getFromTime() {
        if (fromhour == -1 && fromminute == -1)
            return null;
        String h = String.valueOf(fromhour);
        String m = String.valueOf(fromminute);
        if (fromhour < 10)
            h = "0" + h;
        if (fromminute < 10)
            m = "0" + m;
        return h + ":" + m;
    }

    public String getToTime() {
        if (tohour == -1 && tominute == -1)
            return null;
        String h = String.valueOf(tohour);
        String m = String.valueOf(tominute);
        if (tohour < 10)
            h = "0" + h;
        if (tominute < 10)
            m = "0" + m;
        return h + ":" + m;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
        sharedPreferences.edit().putInt("waitTime", waitTime).apply();
    }

    public int getWaitTime() {
        return waitTime;
    }

    public boolean isMap() {
        return map;
    }

    public void isMap(boolean map) {
        this.map = map;
        sharedPreferences.edit().putBoolean("map", map).apply();
    }

    public void setPersonnelIds2(List<UUID> visitors) {
        List<String> set = Linq.map(visitors, new Linq.Map<UUID, String>() {
            @Override
            public String run(UUID item) {
                return item.toString();
            }
        });
        sharedPreferences.edit().putStringSet("visitors", new HashSet<>(set)).apply();
    }

    public void setPersonnelIds(List<VisitorModel> visitors) {
        List<String> set = Linq.map(visitors, new Linq.Map<VisitorModel, String>() {
            @Override
            public String run(VisitorModel item) {
                return item.UniqueId.toString();
            }
        });
        sharedPreferences.edit().putStringSet("visitors", new HashSet<>(set)).apply();
    }

    public void selectAllPersonnels() {
        sharedPreferences.edit().putBoolean("all_visitors", true).apply();
    }

    public void removePersonnelIds() {
        sharedPreferences.edit().remove("visitors").apply();
        sharedPreferences.edit().remove("all_visitors").apply();
    }

    public List<UUID> getPersonnelIds() {
        boolean allVisitors = sharedPreferences.getBoolean("all_visitors", false);
        if (allVisitors)
        {
            List<VisitorModel> visitorModels = new VisitorManager(context).getAll();
            return Linq.map(visitorModels, new Linq.Map<VisitorModel, UUID>() {
                @Override
                public UUID run(VisitorModel item) {
                    return item.UniqueId;
                }
            });
        }
        Set<String> set = sharedPreferences.getStringSet("visitors", new HashSet<String>());
        List<UUID> list = new ArrayList<>();
        for (String id :
                set) {
            list.add(UUID.fromString(id));
        }
        if (list.size() == 0)
        {
            List<VisitorModel> visitorModels = new VisitorManager(context).getAll();
            return Linq.map(visitorModels, new Linq.Map<VisitorModel, UUID>() {
                @Override
                public UUID run(VisitorModel item) {
                    return item.UniqueId;
                }
            });
        }
        return list;
    }


    public StatusType getStatusType() {
        return statusType;
    }


    public void setStatusType(StatusType type) {
        statusType = type;
        sharedPreferences.edit().putInt("statusType", type.ordinal()).apply();
    }


    public boolean isTracking() {
        return tracking;
    }

    public void isTracking(boolean tracking) {
        this.tracking = tracking;
        sharedPreferences.edit().putBoolean("tracking", tracking).apply();
    }

    public Set<UUID> getTrackingActivityTypes() {
        Set<String> set = sharedPreferences.getStringSet("activities", new HashSet<String>());
        Set<UUID> ids = new HashSet<>();
        for (String id :
                set) {
            ids.add(UUID.fromString(id));
        }
        return ids;
    }

    public void setTrackingActivityType(VisitorsPathConfigTabFragment.PointType pointType) {
        Set<String> set = sharedPreferences.getStringSet("activities", new HashSet<String>());
        if (!set.contains(pointType.TypeId.toString())) {
            set.add(pointType.TypeId.toString());
        }
        sharedPreferences.edit().putStringSet("activities", set).apply();
    }

    public void removeTrackingActivityType(VisitorsPathConfigTabFragment.PointType pointType) {
        Set<String> set = sharedPreferences.getStringSet("activities", new HashSet<String>());
        if (set.contains(pointType.TypeId.toString())) {
            set.remove(pointType.TypeId.toString());
        }
        sharedPreferences.edit().putStringSet("activities", set).apply();
    }

}
