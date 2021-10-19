package com.varanegar.framework.database.model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by atp on 8/10/2016.
 */
public abstract class BaseModel implements Serializable {

    @Nullable
    @SerializedName("uniqueId")
    @Expose
    public UUID UniqueId;


    public void setProperties() {
    }

    public boolean isRemoved;
}
