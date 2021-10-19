package com.varanegar.vaslibrary.model.customer;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

/**
 * Created by A.Torabi on 1/28/2018.
 */
@Table
public class CustomerActivityModel extends BaseModel {
    @Column
    public int BackOfficeId;
    @Column
    public String CustomerActivityName;

    @Override
    public String toString() {
        return CustomerActivityName;
    }
}
