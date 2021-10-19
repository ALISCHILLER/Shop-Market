package com.varanegar.vaslibrary.model.dataforregister;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

// NGT-4023 Zar makaron
@Table
public class DataForRegisterModel extends BaseModel {
    @Column
    public String FieldName;
    @Column
    public String FieldKey;
    @Column
    public String FieldValue;

    public String FieldLabel;

    @Override
    public String toString() {
        return FieldValue;
    }
}
