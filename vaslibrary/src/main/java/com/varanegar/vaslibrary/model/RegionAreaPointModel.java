package com.varanegar.vaslibrary.model;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by A.Torabi on 4/29/2018.
 */
@Table
public class RegionAreaPointModel extends BaseModel {
    @Column
    public int Priority;
    @Column
    public double Latitude;
    @Column
    public double Longitude;
    @Column
    public UUID VisitPathTypeUniqueId;
}
