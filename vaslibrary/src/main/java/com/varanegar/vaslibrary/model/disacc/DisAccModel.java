package com.varanegar.vaslibrary.model.disacc;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

/**
 * Created by A.Jafarzadeh on 3/4/2019.
 */
@Table
public class DisAccModel extends BaseModel {
    @Column
    public int BackOfficeId;
    @Column
    public String Code;
    @Column
    public String Name;
    @Column
    public int IsDiscount;
    @Column
    public int IsDefault;
}
