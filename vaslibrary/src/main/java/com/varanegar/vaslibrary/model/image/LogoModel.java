package com.varanegar.vaslibrary.model.image;

import com.varanegar.framework.database.model.BaseModel;

import java.util.UUID;

/**
 * Created by e.hashemzadeh on 6/11/2019.
 */

public class LogoModel extends BaseModel {
    public UUID TokenId;
    public String ImageName;
    public boolean IsDefault;
    public String ImageType;
}
