package com.varanegar.framework.util.report;

import com.varanegar.framework.database.model.BaseModel;

/**
 * Created by atp on 12/27/2016.
 */
public class ReportTemplate<T extends BaseModel> {
    protected ReportTemplate(String name){
        this.name = name;
    }
    String name;
    public String getName(){
        return name;
    }
}
