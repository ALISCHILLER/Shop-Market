package com.varanegar.vaslibrary.model.dataforregister;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

// NGT-4023 Zar makaron
@Table
public class CustomerDataForRegisterModel extends DataForRegisterModel {
    @Column
    public UUID CustomerId;
}
