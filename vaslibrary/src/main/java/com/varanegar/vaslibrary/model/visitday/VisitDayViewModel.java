package com.varanegar.vaslibrary.model.visitday;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

/**
 * Created by A.Torabi on 7/22/2018.
 */
@Table
public class VisitDayViewModel extends BaseModel{
    @Column
    public int RowIndex;
    @Column
    public String PathTitle;
    @Column
    public int CustomerCount;
}
