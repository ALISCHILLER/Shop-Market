package com.varanegar.vaslibrary.webapi.tracking;

import com.varanegar.vaslibrary.manager.locationmanager.BaseLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.LackOfOrderLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.LackOfVisitLocationViewModel;
import com.varanegar.vaslibrary.manager.locationmanager.viewmodel.OrderLocationViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 8/12/2017.
 */

public class TrackingRequestModel {
    public String IMEI;
    public String BackOfficeType;
    public String ConsoleType;
    public UUID PersonnelDailyActivityVisitTypeId;
    public List<BaseLocationViewModel> pointEvent = new ArrayList<>();
    public List<LackOfVisitLocationViewModel> lackOfVisitEvent = new ArrayList<>();
    public List<LackOfOrderLocationViewModel> lackOfOrderEvent = new ArrayList<>();
    public List<OrderLocationViewModel> orderEvent = new ArrayList<>();
}
