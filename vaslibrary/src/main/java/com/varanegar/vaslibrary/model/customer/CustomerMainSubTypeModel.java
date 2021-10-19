package com.varanegar.vaslibrary.model.customer;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

/**
 * Created by A.Torabi on 12/23/2017.
 */
@Table
public class CustomerMainSubTypeModel extends BaseModel {
    @Column
    public int Id;
    @Column
    public int CustRef;
    @Column
    public int MainTypeRef;
    @Column
    public int SubTypeRef;
}
