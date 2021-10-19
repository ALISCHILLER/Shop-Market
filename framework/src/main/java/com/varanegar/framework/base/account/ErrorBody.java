package com.varanegar.framework.base.account;

import com.google.gson.annotations.SerializedName;

/**
 * Created by atp on 6/6/2017.
 */

public class ErrorBody {
    @SerializedName("error")
    public String error;
    @SerializedName("error_description")
    public String description;
}
