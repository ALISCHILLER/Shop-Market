package com.varanegar.vaslibrary.model.productBoGroup;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import com.varanegar.framework.validation.annotations.NotNull;

/**
 * Created by s.foroughi on 09/01/2017.
 */
@Table
public class ProductBoGroupModel extends BaseModel {

    @Column
    public int ParentRef;
    @Column
    public String GoodsGroupName;
    @Column
    public String BarCode;
    @Column
    public String DLCode;
    @Column
    public int NLeft;
    @Column
    public int NRight;
    @Column
    public int NLevel;
    @Column
    public int BackOfficeId;

}
