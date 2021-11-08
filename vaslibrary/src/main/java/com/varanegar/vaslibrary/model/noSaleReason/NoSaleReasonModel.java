package com.varanegar.vaslibrary.model.noSaleReason;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;


import java.util.UUID;

/**
 * Created by s.foroughi on 09/01/2017.
 */
@Table
public class NoSaleReasonModel extends BaseModel{

    @Column
    public UUID NoSaleReasonTypeId;

    @Column
    public String NoSaleReasonName;


    @Column
    public boolean NeedImage;

    @Override
    public String toString() {
        return NoSaleReasonName;
    }
}
