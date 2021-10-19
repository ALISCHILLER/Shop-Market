package com.varanegar.vaslibrary.model.productgroupcatalog;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by A.Torabi on 8/6/2017.
 */
@Table
public class ProductGroupCatalogModel extends BaseModel {
    @Column
    public UUID ProductMainGroupId;
    @Column
    public String CatalogName;
    @Column
    public int RowIndex;
}
