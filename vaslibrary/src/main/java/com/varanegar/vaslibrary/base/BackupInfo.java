package com.varanegar.vaslibrary.base;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.vaslibrary.manager.sysconfigmanager.OwnerKeysWrapper;

import java.util.Date;
import java.util.UUID;

/**
 * Created by A.Torabi on 10/3/2017.
 */

public class BackupInfo extends BaseModel {
    public Date Date;
    public int DatabaseVersion;
    public OwnerKeysWrapper OwnerKeys;
    public int AppVersionCode;
    public String PackageName;
    public UUID TourId;
    public int TourNo;
    public String UserName;
    public int BackOfficeId;
    public String AppVersionName;
    public String DeviceModel;
    public String DeviceIMEI;
    public int DeviceSdk;
    public String DeviceBrand;
    public String DeviceManufacturer;
    public Boolean IsFullBackup;
}
