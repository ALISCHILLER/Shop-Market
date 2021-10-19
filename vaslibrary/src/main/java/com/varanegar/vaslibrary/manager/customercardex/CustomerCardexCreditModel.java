package com.varanegar.vaslibrary.manager.customercardex;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

/**
 * Created by A.Jafarzadeh on 3/19/2018.
 */
@Table
public class CustomerCardexCreditModel extends BaseModel {
    @Column
    public Currency BedAmount;
    @Column
    public Currency BesAmount;
}
