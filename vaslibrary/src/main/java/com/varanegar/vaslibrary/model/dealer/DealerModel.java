package com.varanegar.vaslibrary.model.dealer;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

/**
 * Created by A.Jafarzadeh on 9/12/2018.
 */
@Table
public class DealerModel extends BaseModel {
    @Column
    public String Name;
    @Override
    public String toString() {
        return Name;
    }
}
