package com.varanegar.vaslibrary.model.state;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

/**
 * Created by A.Jafarzadeh on 10/13/2018.
 */
@Table
public class StateModel extends BaseModel {
    @Column
    public int BackOfficeId;
    @Column
    public String StateName;
    @Column
    public String StateCode;

    @Override
    public String toString() {
        return StateName;
    }
}
