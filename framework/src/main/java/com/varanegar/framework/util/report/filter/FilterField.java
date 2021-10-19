package com.varanegar.framework.util.report.filter;

import java.io.Serializable;

/**
 * Created by atp on 1/7/2017.
 */
public class FilterField implements Serializable {
    public String title;
    public FieldType type;
    public int columnIndex;

    @Override
    public String toString() {
        return title;
    }

}
