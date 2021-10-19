package com.varanegar.vaslibrary.manager.sysconfigmanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;

import java.text.ParseException;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by A.Torabi on 5/8/2018.
 */

public class ConfigMap {
    private HashMap<String, SysConfigModel> map = new HashMap<>();

    public void put(SysConfigModel sysConfigModel) {
        map.put(sysConfigModel.Name, sysConfigModel);
    }

    public boolean compare(ConfigKey configKey, boolean value) {
        SysConfigModel sysConfigModel = map.get(configKey.getKey());
        if (sysConfigModel == null || sysConfigModel.Value == null)
            return false;
        if (value)
            return sysConfigModel.Value.equalsIgnoreCase("True");
        else
            return sysConfigModel.Value.equalsIgnoreCase("False");
    }

    public boolean compare(ConfigKey configKey, String value) {
        SysConfigModel sysConfigModel = map.get(configKey.getKey());
        if (sysConfigModel == null || sysConfigModel.Value == null) {
            return value == null;
        }
        return sysConfigModel.Value.equalsIgnoreCase(value);
    }

    public boolean compare(ConfigKey configKey, UUID value) {
        SysConfigModel sysConfigModel = map.get(configKey.getKey());
        if (sysConfigModel == null || sysConfigModel.Value == null) {
            return value == null;
        }
        return value != null && sysConfigModel.Value.equals(value.toString());
    }

    public int compare(ConfigKey configKey, Currency value) throws NullConfigException, ParseException {
        if (value == null) value = Currency.ZERO;
        SysConfigModel sysConfigModel = map.get(configKey.getKey());
        if (sysConfigModel == null || sysConfigModel.Value == null)
            throw new NullConfigException();
        else
            return Currency.parse(sysConfigModel.Value).compareTo(value);
    }

    public int compare(ConfigKey configKey, int value) throws NullConfigException, ParseException {
        SysConfigModel sysConfigModel = map.get(configKey.getKey());
        if (sysConfigModel == null || sysConfigModel.Value == null)
            throw new NullConfigException();
        else {
            int configValue = Integer.parseInt(sysConfigModel.Value);
            if (configValue == value)
                return 0;
            else if (configValue < value)
                return -1;
            else
                return 1;
        }
    }

    public boolean isNullOrEmpty(ConfigKey configKey) {
        SysConfigModel sysConfigModel = map.get(configKey.getKey());
        return sysConfigModel == null || sysConfigModel.Value == null || sysConfigModel.Value.isEmpty();
    }

    @Nullable
    public String getStringValue(@NonNull ConfigKey configKey, @Nullable String defaultValue) {
        SysConfigModel sysConfigModel = map.get(configKey.getKey());
        if (sysConfigModel == null || sysConfigModel.Value == null)
            return defaultValue;
        return sysConfigModel.Value;
    }

    public SysConfigModel get(ConfigKey key) {
        return map.get(key.getKey());
    }

    public class NullConfigException extends Exception{

    }

}
