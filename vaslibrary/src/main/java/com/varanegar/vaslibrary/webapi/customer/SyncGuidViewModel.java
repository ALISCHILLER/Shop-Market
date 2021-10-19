package com.varanegar.vaslibrary.webapi.customer;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.validation.annotations.NotNull;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 9/17/2017.
 */

public class SyncGuidViewModel {
    @NotNull
    public UUID UniqueId;
}
