package com.varanegar.vaslibrary.model.customercall;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by A.Torabi on 1/21/2018.
 */
@Table
public class TaskPriorityModel extends BaseModel {
    @Column
    public int Priority;
    @Column
    public UUID DeviceTaskUniqueId;
    @Column
    public boolean IsEnabled;
}
