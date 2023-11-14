package com.varanegar.vaslibrary.model.newmodel.locationconfirmModel;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;
@Table
public class LocationConfirmTrackingModel extends BaseModel {
    @Column
    public String Lat;
    @Column
    public String Long;
    @Column
    public String StrCreateDate ;
}