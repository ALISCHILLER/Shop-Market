package com.varanegar.vaslibrary.model.call;

import androidx.annotation.Nullable;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.vaslibrary.model.call.tempreturn.ReturnLinesTempModel;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * Created by s.foroughi on 11/01/2017.
 */
@Table(name = "CustomerCallReturnLines")
public class ReturnLinesModel extends ReturnLineBaseModel {
    @Nullable
    @Column
    public UUID EditReasonId;
}
