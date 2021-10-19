package com.varanegar.hotsales;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.vaslibrary.base.VasApplication;

/**
 * Created by A.Jafarzadeh on 3/6/2018.
 */

public class HotSalesApplication extends VasApplication {
    @Override
    protected VaranegarApplication.AppId appId() {
        return VaranegarApplication.AppId.HotSales;
    }

    @Override
    protected VaranegarApplication.GRSAppId grsAppId() {
        return VaranegarApplication.GRSAppId.HotSales;
    }
    @Override
    protected String getDatabaseName() {
        return "vndb";
    }
}
