package com.varanegar.vaslibrary.model.RequestItemLines;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.vaslibrary.model.BaseQtyModel;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by s.foroughi on 21/02/2017.
 */
@Table
public class RequestLineQtyModel extends BaseQtyModel {
    @Column
    @NotNull
    public UUID RequestLineUniqueId;
}
