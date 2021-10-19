package com.varanegar.vaslibrary.model.freeReason;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;


/**
 * Created by s.foroughi on 09/01/2017.
 */
@Table
public class FreeReasonModel extends BaseModel{

    @Column
    public String FreeReasonName;
    @Column
    public String FreeReasonCode;
    @Column
    public String PrintTitle;
    @Column
    public int CalcPriceType;
    @Column
    public int DisAccTypeid;

    @Column
    public int BackOfficeId;

    @Override
    public String toString() {
        return FreeReasonName;
    }
}
