package com.varanegar.vaslibrary.model.VisitTemplatePathCustomer;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 6/28/2017.
 */
@Table
public class VisitTemplatePathCustomerModel extends BaseModel
{
    @Column
    public UUID VisitTemplatePathId;
    @Column
    public UUID CustomerId;
    @Column
    public int PathRowId;
}
