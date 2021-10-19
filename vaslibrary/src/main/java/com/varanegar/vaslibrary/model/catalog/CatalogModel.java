package com.varanegar.vaslibrary.model.catalog;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by s.foroughi on 11/01/2017.
 */
@Table
public class CatalogModel extends BaseModel {
    @NotNull
    @Column
    public UUID ProductId;
    @Column
    public UUID CatalogId;
    @Column
    public String CatalogName;
    @Column
    public int OrderOf;
}
