package com.varanegar.framework.util.report.filter;

import com.varanegar.framework.database.model.BaseModel;

import java.io.Serializable;

/**
 * Created by atp on 12/31/2016.
 */
public class Filter extends BaseModel implements Serializable {
    public int columnIndex;
    public int filterIndex;
}
