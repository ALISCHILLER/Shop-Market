package com.varanegar.vaslibrary.model.customerbogroup;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

/**
 * Created by A.Jafarzadeh on 3/17/2019.
 */
@Table
public class CustomerBoGroupModel extends BaseModel {
    @Column
    public int backOfficeId;
    @Column
    public int nLeft;
    @Column
    public int nRight;
    @Column
    public int parentRef;
    @Column
    public int nLevel;
}
