package com.varanegar.warmsales;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.vaslibrary.base.VasApplication;

public class WarmSalesApplication extends VasApplication {

    @Override
    protected VaranegarApplication.AppId appId() {
        return VaranegarApplication.AppId.WarmSales;
    }

    @Override
    protected VaranegarApplication.GRSAppId grsAppId() {
        return VaranegarApplication.GRSAppId.WarmSales;
    }
    @Override
    protected String getDatabaseName() {
        return "vndb";
    }
}
