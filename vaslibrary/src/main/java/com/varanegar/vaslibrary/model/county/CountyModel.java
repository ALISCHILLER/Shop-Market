package com.varanegar.vaslibrary.model.county;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

/**
 * Created by A.Jafarzadeh on 10/13/2018.
 */
@Table
public class CountyModel extends BaseModel {
    @Column
    public int BackOfficeId;
    @Column
    public String CountyName;
    @Column
    public String CountyCode;

    @Override
    public String toString() {
        return CountyName;
    }
}
