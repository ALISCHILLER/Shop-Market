package com.varanegar.vaslibrary.model.customercall;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by g.aliakbar on 10/04/2018.
 */
@Table
public class CustomerPrintCountModel extends BaseModel {

    @Column
    public Integer PrintCounts;
    @Column
    public UUID CustomerId;
}
