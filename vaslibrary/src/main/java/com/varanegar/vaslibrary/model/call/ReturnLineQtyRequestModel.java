package com.varanegar.vaslibrary.model.call;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by atp on 4/11/2017.
 */
@Table(name = "CustomerCallReturnLinesQtyDetailRequest")
public class ReturnLineQtyRequestModel extends BaseModel {
    @Column
    public UUID ReturnLineUniqueId;
    @Column
    public UUID ProductUnitId;
    @Column
    public BigDecimal Qty;

}
