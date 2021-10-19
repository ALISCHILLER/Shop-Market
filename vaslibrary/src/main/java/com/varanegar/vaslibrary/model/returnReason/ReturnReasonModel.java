package com.varanegar.vaslibrary.model.returnReason;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import com.varanegar.framework.validation.annotations.NotNull;

/**
 * Created by s.foroughi on 09/01/2017.
 */
@Table
public class ReturnReasonModel extends BaseModel {

    @Column
    public String ReturnReasonName;

    @Override
    public String toString() {
        return ReturnReasonName;
    }
}
