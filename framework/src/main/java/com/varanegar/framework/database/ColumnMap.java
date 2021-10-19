package com.varanegar.framework.database;

import com.varanegar.framework.database.model.ModelProjection;

/**
 * Created by A.Torabi on 2/25/2018.
 */

public class ColumnMap {
    public ColumnMap(String src, String dest) {
        this.src = src;
        this.dest = dest;
    }
    public ColumnMap(String src, ModelProjection dest){
        this.src = src;
        this.dest = dest.getSimpleName();
    }
    public String getSrc() {
        return src;
    }

    private String src;

    public String getDest() {
        return dest;
    }

    private String dest;
}
