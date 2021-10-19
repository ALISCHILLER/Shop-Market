package com.varanegar.vaslibrary.model.orderLineQtyModel;

import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;
import com.varanegar.vaslibrary.model.BaseQtyModel;

import java.util.UUID;

/**
 * Created by s.foroughi on 21/02/2017.
 */
@Table(name = "CustomerCallOrderLinesOrderQtyDetail")
public class OrderLineQtyModel extends BaseQtyModel {
    @Column
    @NotNull
    public UUID OrderLineUniqueId;

}
