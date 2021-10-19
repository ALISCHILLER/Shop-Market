package com.varanegar.dist;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.vaslibrary.base.VasApplication;

/**
 * Created by atp on 2/25/2017.
 */

public class DistApplication extends VasApplication {
    @Override
    protected VaranegarApplication.AppId appId() {
        return VaranegarApplication.AppId.Dist;
    }

    @Override
    protected VaranegarApplication.GRSAppId grsAppId() {
        return VaranegarApplication.GRSAppId.Dist;
    }

    @Override
    protected String getDatabaseName() {
        return "vndb";
    }
}
