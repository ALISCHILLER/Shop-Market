package com.varanegar.vaslibrary.webapi.apiNew.modelNew.customer_not_allowed_product;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;
import java.util.UUID;


@Table
public class CustomerNotAllowProductModel extends BaseModel {
    @Column
    public UUID ProductId;
    @Column
    public UUID CustomerId;
}
