package com.varanegar.vaslibrary.model.target;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

/**
 * Created by A.Jafarzadeh on 2/24/2018.
 */

@Table
public class SaleManProductViewModel extends BaseModel {
    @Column
    public String ProductName;
    @Column
    public int Qty;
    @Column
    public int Achieved;
}
