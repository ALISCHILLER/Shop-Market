package com.varanegar.vaslibrary.model.bank;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

/**
 * Created by s.foroughi on 08/01/2017.
 */

@Table
public class BankModel extends BaseModel{

    @Column
    public String BankName;

    @Override
    public String toString() {
        return BankName;
    }
}
