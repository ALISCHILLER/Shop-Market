package com.varanegar.vaslibrary.model.update;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;


/**
 * Created by atp on 8/21/2016.
 */
@Table
public class UpdateLogModel extends BaseModel {

    @Column
    public String Name;
    @Column
    public java.util.Date Date;

}
