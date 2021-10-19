package com.varanegar.vaslibrary.model.emphaticproductcount;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by A.Torabi on 7/16/2017.
 */
@Table
public class EmphaticProductCountModel extends BaseModel {
    @Column
    public int ProductCount;
    @Column
    public UUID ProductId;
    @Column
    public UUID RuleId;
    @Column
    public String ProductName;
}
