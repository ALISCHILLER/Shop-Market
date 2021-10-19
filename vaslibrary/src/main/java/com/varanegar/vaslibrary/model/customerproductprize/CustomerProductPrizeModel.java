package com.varanegar.vaslibrary.model.customerproductprize;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by A.Torabi on 9/18/2017.
 */
@Table
public class CustomerProductPrizeModel extends BaseModel {
    @Column
    public UUID ProductId;
    @Column
    public UUID CustomerId;
    @Column
    public String Comment = "";
}
