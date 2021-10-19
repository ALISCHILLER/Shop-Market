package com.varanegar.vaslibrary.model.personnel;

import com.varanegar.framework.database.model.BaseModel;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Table;

/**
 * Created by s.foroughi on 09/01/2017.
 */

@Table
public class PersonnelModel extends BaseModel{

    @Column
    public String BackOfficeId;
    @Column
    public String PersonnelName;
    @Column
    public int Status;
    @Column
    public int IsSalesman;
    @Column
    public int IsDistributer;
    @Column
    public int IsCollector;

}
