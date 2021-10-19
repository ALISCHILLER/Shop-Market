package com.varanegar.vaslibrary.webapi.tsp;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.maps.android.PolyUtil;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.webapi.BaseApi;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;

public class TspApi extends BaseApi {

    public TspApi(Context context) {
        super(context);
    }

    public interface OnTspResponse {
        void onSuccess(List<LatLng> list, int[] indices);

        void onFailure(String error);
    }

    public void getRoute(List<LatLng> coordinates, OnTspResponse response) {
        StringBuilder points = new StringBuilder();
        for (LatLng coordinate :
                coordinates) {
            String latlng = coordinate.longitude + "," + coordinate.latitude;
            if (points.length() > 0)
                points.append(";").append(latlng);
            else
                points = new StringBuilder(latlng);
        }
        ITspApi iTspApi = getRetrofitBuilder("https://map.ir", new OkHttpClient.Builder().build()).build().create(ITspApi.class);
        Call<JsonObject> call = iTspApi.getRoute(points.toString(), true, "first", "any", true);
        runWebRequest(call, new WebCallBack<JsonObject>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(JsonObject result, Request request) {
                try {
                    JsonArray waypointsArray = result.getAsJsonArray("waypoints");
                    int[] indices = new int[waypointsArray.size()];
                    int i = 0;
                    for (JsonElement je :
                            waypointsArray) {
                        int index = je.getAsJsonObject().get("waypoint_index").getAsInt();
                        indices[i] = index;
                        i++;
                    }
                    List<LatLng> list = new ArrayList<>();
                    JsonArray tripsArray = result.getAsJsonArray("trips");
                    for (JsonElement trip :
                            tripsArray) {
                        JsonObject to = trip.getAsJsonObject();
                        JsonArray legsArray = to.getAsJsonArray("legs");
                        for (JsonElement leg :
                                legsArray) {
                            JsonObject lo = leg.getAsJsonObject();
                            JsonArray stepsArray = lo.getAsJsonArray("steps");
                            for (JsonElement step :
                                    stepsArray) {
                                JsonObject so = step.getAsJsonObject();
                                JsonArray p = so.getAsJsonObject("maneuver").getAsJsonArray("location");
                                double latitude = p.get(1).getAsDouble();
                                double longitude = p.get(0).getAsDouble();
                                list.add(new LatLng(latitude, longitude));
                            }
                        }
                    }

                    response.onSuccess(list, indices);

                } catch (Exception ex) {
                    response.onFailure(ex.getMessage());
                }
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                response.onFailure(WebApiErrorBody.log(error, getContext()));
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                response.onFailure(getContext().getString(R.string.network_error));
            }
        });
    }
}
