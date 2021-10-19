package com.varanegar.vaslibrary.model.customerinventory;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;
import com.varanegar.vaslibrary.model.BaseQtyModel;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by A.Torabi on 9/12/2017.
 */
@Table
public class CustomerInventoryQtyModel extends BaseQtyModel {
    @Column
    public UUID CustomerInventoryId;
}
