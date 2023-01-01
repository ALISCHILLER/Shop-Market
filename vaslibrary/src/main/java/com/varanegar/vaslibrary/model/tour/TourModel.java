package com.varanegar.vaslibrary.model.tour;

import com.varanegar.framework.database.model.BaseModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 7/2/2017.
 */
public class TourModel extends BaseModel {
    public Date StartTime;
    public UUID DayVisitPathId;
    public int TourNo;
    public String AgentName;
    public String TourStatusName;
    public String DriverName;
    public String VehicleName;
    public String StockName;
    public String AgentId;
    public UUID LocalId;
    public TourStatus Status;
    public boolean IsFromBackup;
    public boolean IsVirtual;
    public String AgentMobile;
    public List<PinHelper> Pins;
    public String ZarNotificationToken ;
    public TourModel() {
        Pins=new ArrayList<>();
    }
}



