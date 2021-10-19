package com.varanegar.presale;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.vaslibrary.base.VasApplication;

/**
 * Created by atp on 2/25/2017.
 */

public class PresalesApplication extends VasApplication {

    @Override
    protected VaranegarApplication.AppId appId() {
        return VaranegarApplication.AppId.PreSales;
    }

    @Override
    protected VaranegarApplication.GRSAppId grsAppId() {
        return VaranegarApplication.GRSAppId.PreSales;
    }
    @Override
    protected String getDatabaseName() {
        return "vndb";
    }
}
