package com.varanegar.vaslibrary.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by m-latifi on 11/12/2022.
 */

public class NeshanModel {

    @SerializedName("status")
    String status;

    @SerializedName("formatted_address")
    String formatted_address;

    @SerializedName("route_name")
    String route_name;

    @SerializedName("route_type")
    String route_type;

    @SerializedName("neighbourhood")
    String neighbourhood;

    @SerializedName("city")
    String city;

    @SerializedName("state")
    String state;

    @SerializedName("place")
    String place;

    @SerializedName("municipality_zone")
    String municipality_zone;

    @SerializedName("in_traffic_zone")
    String in_traffic_zone;

    @SerializedName("in_odd_even_zone")
    String in_odd_even_zone;

    @SerializedName("village")
    String village;

    @SerializedName("district")
    String district;

    public String getStatus() {
        return status;
    }

    public String getFormatted_address() {
        return formatted_address;
    }

    public String getRoute_name() {
        return route_name;
    }

    public String getRoute_type() {
        return route_type;
    }

    public String getNeighbourhood() {
        return neighbourhood;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPlace() {
        return place;
    }

    public String getMunicipality_zone() {
        return municipality_zone;
    }

    public String getIn_traffic_zone() {
        return in_traffic_zone;
    }

    public String getIn_odd_even_zone() {
        return in_odd_even_zone;
    }

    public String getVillage() {
        return village;
    }

    public String getDistrict() {
        return district;
    }
}
