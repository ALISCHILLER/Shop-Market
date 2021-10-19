package com.varanegar.vaslibrary.model.validpaytype;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by A.Jafarzadeh on 4/22/2018.
 */
@Table
public class ValidPayTypeModel extends BaseModel {
    @Column
    public int BackOfficeId;
    @Column
    public int PayTypeRef;
    @Column
    public UUID PayTypeId;
    @Column
    public int BuyTypeRef;
    @Column
    public UUID BuyTypeId;
}
