package com.varanegar.vaslibrary.model.customeractiontime;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;
import com.varanegar.vaslibrary.manager.customeractiontimemanager.CustomerActions;

import java.util.UUID;

/**
 * Created by A.Torabi on 2/12/2018.
 */
@Table
public class CustomerActionTimeModel extends BaseModel {
    @Column
    public UUID CustomerId;
    @Column
    public java.util.Date Date;
    @Column(isEnum = true)
    public CustomerActions Action;
}
