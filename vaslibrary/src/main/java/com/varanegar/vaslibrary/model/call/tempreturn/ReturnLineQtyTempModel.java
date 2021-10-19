package com.varanegar.vaslibrary.model.call.tempreturn;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by atp on 4/11/2017.
 */
@Table(name = "CustomerCallReturnLinesQtyDetailTemp")
public class ReturnLineQtyTempModel extends BaseModel {
    @Column
    public UUID ReturnLineUniqueId;
    @Column
    public UUID ProductUnitId;
    @Column
    public BigDecimal Qty;

}
