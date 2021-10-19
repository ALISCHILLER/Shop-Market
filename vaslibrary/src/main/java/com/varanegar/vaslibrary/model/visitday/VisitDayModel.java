package com.varanegar.vaslibrary.model.visitday;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

/**
 * Created by s.foroughi on 08/01/2017.
 */

@Table
public class VisitDayModel extends BaseModel
{
    @Column
    public int RowIndex;
    @Column
    public String PathTitle;

}
