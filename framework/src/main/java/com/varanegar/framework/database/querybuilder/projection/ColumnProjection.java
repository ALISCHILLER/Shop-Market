package com.varanegar.framework.database.querybuilder.projection;


import com.varanegar.framework.database.model.ModelProjection;
import com.varanegar.framework.database.querybuilder.Utils;

import java.util.List;

public class ColumnProjection extends Projection {
    public ModelProjection getTable() {
        return table;
    }

    public ModelProjection getColumn() {
        return column;
    }

    private ModelProjection table;
    private ModelProjection column;

    public ColumnProjection(ModelProjection table, ModelProjection column) {
        this.table = table;
        this.column = column;
    }

    @Override
    public String build() {
        String ret = "";
        if (table != null)
            if (!Utils.isNullOrWhiteSpace(table.getName()))
                ret = ret + table.getName() + ".";
        if (column != null)
            if (!Utils.isNullOrWhiteSpace(column.getName()))
                ret = ret + column.getName();

        return ret;
    }

    @Override
    public List<Object> buildParameters() {
        return Utils.EMPTY_LIST;
    }
}
