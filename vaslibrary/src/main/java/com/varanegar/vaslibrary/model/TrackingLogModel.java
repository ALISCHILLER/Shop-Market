package com.varanegar.vaslibrary.model;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.Date;

@Table
public class TrackingLogModel extends BaseModel {
    @Column
    public String ENTime;
    @Column
    public String FATime;
    @Column
    public String EventType;
    @Column
    public String Description;
    @Column
    public String Trace;
    @Column
    public Date Time;
    @Column
    public String Level;
}
