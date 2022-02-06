package com.varanegar.supervisor.model.tracking;

import java.util.List;

public class RouteMapPostModel {

    public List<String> personelIds ;
    public String date;
    public String toDate;
    public String toTime;
    public String fromTime;
    public Integer stopTime;
    public Boolean invalidPoint;
    public Boolean battryLow;
    public Boolean wait;
    public Boolean removeTransaction;
    public Boolean orderOutOfPath;
    public Boolean order;
    public Boolean opernTour;
    public Boolean lackVisit;
    public Boolean lackOrder;
    public Boolean gpsPowerOn;
    public Boolean gpsPowerOff;
    public Boolean editeOrder;
    public Boolean closeTour;
    public String dataOwnerCenterKey;


}
