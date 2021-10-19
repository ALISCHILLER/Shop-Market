package com.varanegar.vaslibrary.model.update;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.Date;
import java.util.UUID;

/**
 * Created by A.Torabi on 1/24/2018.
 */
@Table
public class TourUpdateLogModel extends BaseModel {
    @Column
    public String Name;
    @Column
    public String GroupName;
    @Column
    public String Error;
    @Column
    public Date StartDate;
    @Column
    public Date FinishDate;
    @Column
    public UUID LocalTourId;
    @Column
    public UUID TourId;
}
