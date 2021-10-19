package com.varanegar.contractor;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.vaslibrary.base.VasApplication;

public class ContractorApplication extends VasApplication {

    @Override
    protected VaranegarApplication.AppId appId() {
        return VaranegarApplication.AppId.Contractor;
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
