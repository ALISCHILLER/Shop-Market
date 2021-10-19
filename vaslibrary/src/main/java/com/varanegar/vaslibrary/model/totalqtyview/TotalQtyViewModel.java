package com.varanegar.vaslibrary.model.totalqtyview;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by A.Torabi on 5/29/2018.
 */
@Table
public class TotalQtyViewModel extends BaseModel {
    @Column
    public UUID OrderUniqueId;
    @Column
    public UUID ProductUniqueId;
    @Column
    public boolean IsRequestFreeItem;
    @Column
    public BigDecimal TotalQty;
}
