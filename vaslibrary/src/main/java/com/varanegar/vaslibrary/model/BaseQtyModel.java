package com.varanegar.vaslibrary.model;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.processor.annotations.Column;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by A.Torabi on 11/11/2017.
 */

public abstract class BaseQtyModel extends BaseModel {
    @Column
    public BigDecimal Qty;
    @Column
    @NotNull
    public UUID ProductUnitId;
}
