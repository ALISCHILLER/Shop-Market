package com.varanegar.vaslibrary.model.unit;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

/**
 * Created by s.foroughi on 21/02/2017.
 */
@Table
public class UnitModel extends BaseModel {

    @Column
    public String UnitName;
    @Column
    public int BackOfficeId;
}
