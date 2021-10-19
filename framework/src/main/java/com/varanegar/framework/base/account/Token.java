package com.varanegar.framework.base.account;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by atp on 6/6/2017.
 */

public class Token implements Serializable {
    @SerializedName("access_token")
    public String accessToken;
    @SerializedName("token_type")
    public String tokenType;
    @SerializedName("expires_in")
    public Integer expiresIn;

}
