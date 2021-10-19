package com.varanegar.vaslibrary.model.call.temporder;

import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;
import com.varanegar.vaslibrary.model.call.OrderLineBaseModel;

/**
 * Created by e.hashemzadeh on 1/25/2021.
 */
@Table (name = "CustomerCallOrderLinesTemp")
public class CallOrderLinesTempModel extends OrderLineBaseModel {
    @Column
    public Currency PromotionPrice;
}
