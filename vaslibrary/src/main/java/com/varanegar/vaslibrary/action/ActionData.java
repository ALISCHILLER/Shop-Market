package com.varanegar.vaslibrary.action;

import android.content.Context;

import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigMap;
import com.varanegar.vaslibrary.model.customer.CustomerModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;

public class ActionData {
    private final ConfigMap configMap;
    private final Context context;
    private final CustomerModel customer;

    public ActionData(Context context ,CustomerModel customerModel, ConfigMap configMap ){
        this.context  = context;
        this.customer = customerModel;
        this.configMap = configMap;
    }
    public SysConfigModel getCloudConfig(ConfigKey configKey) {
        return configMap.get(configKey);
    }
    public ConfigMap getCloudConfigs() {
        return configMap;
    }

    public CustomerModel getCustomer() {
        return customer;
    }

    public Context getContext() {
        return context;
    }
}
