package com.varanegar.vaslibrary.model.call.temporder;

import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;
import com.varanegar.vaslibrary.model.BaseQtyModel;

import java.util.UUID;

/**
 * Created by e.hashemzadeh on 1/25/2021.
 */
@Table (name = "CustomerCallOrderLinesQtyTemp")
public class CallOrderLinesQtyTempModel extends BaseQtyModel {
    @Column
    @NotNull
    public UUID OrderLineUniqueId;
}
