package com.varanegar.vaslibrary.model.customer;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by Elnaz on 6/8/2020.
 */

@Table
public class CustomerBarcodeModel extends BaseModel {
    @Column
    public UUID CustomerUniqueId;
    @Column
    public String Barcode;
}
