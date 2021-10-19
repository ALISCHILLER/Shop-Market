package com.varanegar.vaslibrary.model.dealerPaymentType;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by g.aliakbar on 14/03/2018.
 */

@Table
public class DealerPaymentTypeModel extends BaseModel {

    @Column
    public UUID PaymentTypeOrderUniqueId;
}
