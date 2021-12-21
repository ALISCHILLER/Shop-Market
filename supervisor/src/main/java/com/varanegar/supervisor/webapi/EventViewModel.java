package com.varanegar.supervisor.webapi;

import java.util.Date;
import java.util.UUID;

/**
 * Created by A.Torabi on 6/7/2018.
 */

public class EventViewModel {
    public UUID Id;
    public UUID uniqueId;
    public String JData;
    public String Desc;
    public String Lable;
    public double Longitude;
    public double Latitude;
    public UUID PointType;
    public int SubType;
    public UUID MasterId;
    public UUID ReferId;
    public boolean IsLeaf;
    public float Speed;
    public float Accurancy;
    public UUID EventTypeId;
    public double Distance;
    public Date ActivityDate;
    public String ActivityPDate;
    public double Duration;
}
