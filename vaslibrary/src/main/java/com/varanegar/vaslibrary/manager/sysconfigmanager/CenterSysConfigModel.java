package com.varanegar.vaslibrary.manager.sysconfigmanager;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.processor.annotations.Column;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 12/10/2018.
 */

public class CenterSysConfigModel {
    public UUID UniqueId;

    public UUID Scope;

    public String Name;

    public String Value;

    public String Title;
}
