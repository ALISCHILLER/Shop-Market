package com.varanegar.supervisor.webapi.model_old;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 6/7/2018.
 */

public class PersonnelPointsParam {
    public List<UUID> PersonelIds;
    public Date mDate;
    public String FromTime;
    public String ToTime;
    public boolean Order;
    public boolean LackOrder;
    public boolean LackVisit;
    public boolean Wait;
    public boolean GpsPowerOff;
    public boolean InvalidPoint;
    public boolean BattryLow;
    public boolean EnterPath;
    public boolean ExitPath;
    public boolean EnterRigion;
    public boolean ExitRigion;
    public boolean OpernTour;
    public boolean CloseTour;
    public boolean OrderOutOfPath;
    public boolean GpsPowerOn;
    public boolean OrderNewCustomer;
    public boolean ExitCompany;
    public boolean WifiPowerOff;
    public boolean WifiPowerOn;
    public boolean EnterCompany;
    public int StopTime;
    public boolean MobileDataOff;
    public boolean MobileDataOn;
}
