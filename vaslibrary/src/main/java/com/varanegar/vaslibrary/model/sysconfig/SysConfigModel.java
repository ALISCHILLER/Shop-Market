package com.varanegar.vaslibrary.model.sysconfig;


import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.framework.validation.annotations.NotNull;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

import java.util.UUID;

/**
 * Created by atp on 8/18/2016.
 */
@Table
public class SysConfigModel extends BaseModel {
    @Column
    @NotNull
    public UUID Scope;
    @Column
    @NotNull
    public String Name;
    @Column
    public String Value;

}