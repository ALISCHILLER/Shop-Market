package com.varanegar.vaslibrary.model.newmodel.locationconfirm;
import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.List;

@Table
public class LocationConfirm extends BaseModel {

    @Column
    public List<String> Lat ;
    @Column
    public List<String> Long ;

}
