package com.varanegar.vaslibrary.model.call;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by A.Torabi on 2/19/2018.
 */
@Table
public class CustomerCallOrderPreviewModel extends BaseModel {
    @Column
    public UUID CustomerUniqueId;
    @Column
    public BigDecimal TotalQty;
    @Column
    public Currency TotalPrice;
    @Column
    public String LocalPaperNo;
    @Column
    public String Comment;
}
