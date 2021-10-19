package com.varanegar.vaslibrary.model.productbatchonhandqtymodel;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 7/10/2018.
 */
@Table
public class ProductBatchOnHandQtyModel extends BaseModel {
    @Column
    public UUID ProductId;
    @Column
    public int BatchRef;
    @Column
    public String BatchNo;
    @Column
    public String ExpDate;
    @Column
    public String InsDate;
    @Column
    public String ProDate;
    @Column
    public Date DateTimeExpDate;
//    @Column
//    public int ItemRef;
    @Column
    public BigDecimal OnHandQty;
}
