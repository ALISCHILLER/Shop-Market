package com.varanegar.vaslibrary.model.productunitsview;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

/**
 * Created by A.Torabi on 4/10/2018.
 */
@Table
public class ProductUnitsViewModel extends BaseModel {
    @Column
    public String IsForReturn;
    @Column
    public String IsForSale;
    @Column
    public String IsDefault;
    @Column
    public String ConvertFactor;
    @Column
    public String UnitName;
    @Column
    public String ProductUnitId;
}
