package com.varanegar.framework.util.report;

import com.varanegar.framework.database.model.ModelProjection;

/**
 * Created by A.Torabi on 7/29/2017.
 */
class RowModelProjection extends ModelProjection<SimpleReportAdapter.RowModel> {
    public static RowModelProjection Row = new RowModelProjection("Row.row");

    RowModelProjection(String name) {
        super(name);
    }
}
