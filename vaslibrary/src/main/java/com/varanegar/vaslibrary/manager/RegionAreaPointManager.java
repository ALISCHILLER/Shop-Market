package com.varanegar.vaslibrary.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.RegionAreaPoint;
import com.varanegar.vaslibrary.model.RegionAreaPointModel;
import com.varanegar.vaslibrary.model.RegionAreaPointModelRepository;
import com.varanegar.vaslibrary.model.tour.TourModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.regionapi.RegionAreaPointApi;

import java.util.List;
import java.util.UUID;

import okhttp3.Request;
import timber.log.Timber;

/**
 * Created by A.Torabi on 4/29/2018.
 */

public class RegionAreaPointManager extends BaseManager<RegionAreaPointModel> {
    public static final UUID RegionPath = UUID.fromString("8309B13C-03A3-4E9D-8138-055285967B4D");
    public static final UUID VisitDayPath = UUID.fromString("E12E3EDC-B7B5-4C12-BEAC-960B54E34880");
    public static final UUID CompanyPath = UUID.fromString("FCB4AD96-6177-41CD-8C7E-75EE7040B8EC");

    public RegionAreaPointManager(@NonNull Context context) {
        super(context, new RegionAreaPointModelRepository());
    }

    public void sync(final UpdateCall updateCall) {
        removeRegion();
        final RegionAreaPointApi api = new RegionAreaPointApi(getContext());
        TourManager tourManager = new TourManager(getContext());
        final TourModel tourModel = tourManager.loadTour();
        try {
            deleteAll();
            api.runWebRequest(api.getAll(tourModel.UniqueId.toString()), new WebCallBack<List<RegionAreaPointModel>>() {
                @Override
                protected void onFinish() {

                }

                @Override
                protected void onSuccess(List<RegionAreaPointModel> result, Request request) {
                    if (result.size() > 0) {
                        try {
                            insert(result);
                            Timber.i("Updating region area points completed");
                            updateCall.success();
                        } catch (ValidationException e) {
                            Timber.e(e);
                            updateCall.failure(getContext().getString(R.string.data_validation_failed));
                        } catch (DbException e) {
                            Timber.e(e);
                            updateCall.failure(getContext().getString(R.string.data_error));
                        }
                    } else {
                        Timber.i("Updating region area points completed. list was empty");
                        updateCall.success();
                    }
                }

                @Override
                protected void onApiFailure(ApiError error, Request request) {
                    String err = WebApiErrorBody.log(error, getContext());
                    updateCall.failure(err);
                }

                @Override
                protected void onNetworkFailure(Throwable t, Request request) {
                    Timber.e(t.getMessage());
                    updateCall.failure(getContext().getString(R.string.network_error));
                }
            });
        } catch (DbException e) {
            Timber.e(e);
            updateCall.failure(getContext().getString(R.string.error_deleting_old_data));
        }
    }

    public Area getDayPath() {
        Query query = new Query().from(RegionAreaPoint.RegionAreaPointTbl)
                .whereAnd(Criteria.equals(RegionAreaPoint.VisitPathTypeUniqueId, VisitDayPath.toString()))
                .orderByAscending(RegionAreaPoint.Priority);
        List<RegionAreaPointModel> points = getItems(query);
        if (points.size() > 2)
            return new Area(points);
        else
            return null;
    }

    public Area getCompanyPath() {
        Query query = new Query().from(RegionAreaPoint.RegionAreaPointTbl)
                .whereAnd(Criteria.equals(RegionAreaPoint.VisitPathTypeUniqueId, CompanyPath.toString()))
                .orderByAscending(RegionAreaPoint.Priority);
        List<RegionAreaPointModel> points = getItems(query);
        if (points.size() > 2)
            return new Area(points);
        else
            return null;
    }

    public Area getRegion() {
        Query query = new Query().from(RegionAreaPoint.RegionAreaPointTbl)
                .whereAnd(Criteria.equals(RegionAreaPoint.VisitPathTypeUniqueId, RegionPath.toString()))
                .orderByAscending(RegionAreaPoint.Priority);
        List<RegionAreaPointModel> points = getItems(query);
        if (points.size() > 2)
            return new Area(points);
        else
            return null;
    }

    @Nullable
    public Transition isTransition(Location location, Area dayPath, Area region, Area company) {
        boolean isInRegion = false;
        boolean isInDayPath = false;
        if (dayPath != null)
            isInDayPath = dayPath.pointIsInRegion(location.getLatitude(), location.getLongitude());

        if (region != null)
            isInRegion = region.pointIsInRegion(location.getLatitude(), location.getLongitude());

        boolean isInCompanyPath = false;
        if (company != null)
            isInCompanyPath = company.pointIsInRegion(location.getLatitude(), location.getLongitude());

        Transition transition = null;
        if (isInDayPath && checkRegion(VisitDayPath) == Status.Out)
            transition = new Transition(TransitionType.Enter, VisitDayPath);
        else if (isInRegion && checkRegion(RegionPath) == Status.Out)
            transition = new Transition(TransitionType.Enter, RegionPath);
        else if (isInCompanyPath && checkRegion(CompanyPath) == Status.Out)
            transition = new Transition(TransitionType.Enter, CompanyPath);
        else if (!isInDayPath && checkRegion(VisitDayPath) == Status.In)
            transition = new Transition(TransitionType.Exit, VisitDayPath);
        else if (!isInRegion && checkRegion(RegionPath) == Status.In)
            transition = new Transition(TransitionType.Exit, RegionPath);
        else if (!isInCompanyPath && checkRegion(CompanyPath) == Status.In)
            transition = new Transition(TransitionType.Exit, CompanyPath);

        setInRegion(VisitDayPath, isInDayPath);
        setInRegion(RegionPath, isInRegion);
        setInRegion(CompanyPath, isInCompanyPath);
        return transition;
    }

    private void setInRegion(@NonNull UUID region, boolean isInRegion) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("RegionArea", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(region.toString(), isInRegion);
        editor.apply();
    }

    private enum Status {
        In,
        Out,
        Unknown
    }

    private Status checkRegion(@NonNull UUID region) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("RegionArea", Context.MODE_PRIVATE);
        if (!sharedPreferences.contains(region.toString()))
            return Status.Unknown;
        boolean s = sharedPreferences.getBoolean(region.toString(), false);
        return s ? Status.In : Status.Out;
    }

    private void removeRegion() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("RegionArea", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("RegionArea").apply();
    }


}
