package com.varanegar.vaslibrary.model.msl;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Create by Mehrdad Latifi on 8/30/2022
 */

@Table
public class MslModel extends BaseModel {

    @Column
    @NotNull
    public UUID CustomerLevelId;

    @Column
    @NotNull
    public UUID ProductId;

    @Column
    @NotNull
    public boolean IsForce;

    public MslModel() {
    }

    public MslModel(UUID customerLevelId, UUID productId, boolean isForce) {
        CustomerLevelId = customerLevelId;
        ProductId = productId;
        IsForce = isForce;
    }

}
