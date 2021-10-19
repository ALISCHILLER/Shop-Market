package com.varanegar.vaslibrary.model.priceclass;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

/**
 * Created by A.Jafarzadeh on 2/21/2018.
 */
@Table
public class PriceClassVnLiteModel extends BaseModel {
    @Column
    public int PriceClassRef;
    @Column
    public String PriceClassName;

    @Override
    public String toString() {
        return PriceClassName;
    }
}
