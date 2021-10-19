package com.varanegar.framework.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.database.model.ModelProjection;
import com.varanegar.framework.database.querybuilder.TableMismatchException;
import com.varanegar.framework.database.querybuilder.TableNotFoundException;
import com.varanegar.framework.util.Linq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import timber.log.Timber;

/**
 * Created by A.Torabi on 2/25/2018.
 */

public class TableMap {

    @Nullable
    private ModelProjection srcProjection;
    @Nullable
    private ModelProjection destProjection;


    private String src;

    public boolean throwIfSourceMissed() {
        return tsrc;
    }

    public boolean throwIfDestMissed() {
        return tdest;
    }

    private boolean tsrc;
    private boolean tdest;

    public String getSrc() {
        return src;
    }

    private String dest;

    public String getDest() {
        return dest;
    }

    private Set<String> ignores;

    public TableMap(@NonNull String src, @NonNull ModelProjection dest) {
        this.src = src;
        this.destProjection = dest;
        this.dest = destProjection.getName();
        columns = new ArrayList<>();
        ignores = new HashSet<>();
    }

    public TableMap(String src, String dest) {
        this.src = src;
        this.dest = dest;
        columns = new ArrayList<>();
        ignores = new HashSet<>();
    }

    public TableMap(@NonNull ModelProjection src, @NonNull ModelProjection dest) {
        this.srcProjection = src;
        this.destProjection = dest;
        this.src = src.getName();
        this.dest = dest.getName();
        columns = new ArrayList<>();
        ignores = new HashSet<>();
    }

    public List<ColumnMap> getColumns() {
        return columns;
    }

    public int getColumnsCount() {
        return columns.size();
    }

    private List<ColumnMap> columns;

    public TableMap addColumn(String src, String dest) {
        this.columns.add(new ColumnMap(src, dest));
        return this;
    }

    public TableMap addColumn(String src, ModelProjection dest) {
        ColumnMap map = new ColumnMap(src, dest);
        this.columns.add(map);
        return this;
    }

    public TableMap addColumn(ModelProjection src, ModelProjection dest) {
        ColumnMap map = new ColumnMap(src.getSimpleName(), dest);
        this.columns.add(map);
        return this;
    }

    public TableMap addAllColumns() {
        if (srcProjection == null || destProjection == null) {
            throw new UnsupportedOperationException("To use this method you should use TableMap(ModelProjection src, ModelProjection dest)");
        }
        HashMap<String, ModelProjection> srcProjections = srcProjection.getColumns();
        HashMap<String, ModelProjection> destProjections = destProjection.getColumns();
        Set<String> srcProjectionNames = srcProjections.keySet();
        for (String name :
                srcProjectionNames) {
            if (destProjections.containsKey(name)) {
                addColumn(srcProjections.get(name), destProjections.get(name));
            }
        }
        return this;
    }

    /**
     * Use this method if you want to automatically map tables from table structures
     *
     * @param srcHandler  DbHandler for source
     * @param destHandler DbHandler for destination
     * @param tsrc        throws TableMismatchException if some columns from source table remained unmapped
     * @param tdest       throws TableMismatchException if some columns from destination table remained unmapped
     * @return TableMap
     */
    public TableMap addAllColumns(@NonNull DbHandler srcHandler, @NonNull DbHandler destHandler, boolean tsrc, boolean tdest) {
        this.tsrc = tsrc;
        this.tdest = tdest;
        Set<String> srcColumns = new HashSet<>();
        Set<String> destColumns = new HashSet<>();
        try {
            srcColumns = srcHandler.getListOfColumns(src);
        } catch (TableNotFoundException ex) {
            Timber.e(ex);
            if (tsrc)
                throw ex;
        }

        try {
            destColumns = destHandler.getListOfColumns(dest);
        } catch (TableNotFoundException ex) {
            Timber.e(ex);
            if (tsrc)
                throw ex;
        }

        if (srcColumns.size() > 0 && destColumns.size() > 0) {
            List<String> mappedColumns = new ArrayList<>();
            List<String> srcUnmappedColumns = new ArrayList<>();
            List<String> destUnmappedColumns = new ArrayList<>();
            for (final String name :
                    srcColumns) {
                if (!isColumnIgnored(name)) {
                    if (destColumns.contains(name)) {
                        addColumn(name, name);
                        mappedColumns.add(name);
                    } else {
                        boolean exists = Linq.exists(columns, new Linq.Criteria<ColumnMap>() {
                            @Override
                            public boolean run(ColumnMap item) {
                                return item.getSrc().equals(name);
                            }
                        });
                        if (!exists)
                            srcUnmappedColumns.add(name);
                    }
                }
            }
            for (final String name :
                    destColumns) {
                if (!mappedColumns.contains(name)) {
                    boolean exists = Linq.exists(columns, new Linq.Criteria<ColumnMap>() {
                        @Override
                        public boolean run(ColumnMap item) {
                            return item.getDest().equals(name);
                        }
                    });
                    if (!exists)
                        destUnmappedColumns.add(name);
                }
            }
            if (tsrc && srcUnmappedColumns.size() > 0)
                throw new TableMismatchException(srcUnmappedColumns, destUnmappedColumns, src, dest);
            else if (tdest && destUnmappedColumns.size() > 0)
                throw new TableMismatchException(srcUnmappedColumns, destUnmappedColumns, src, dest);
        }
        return this;
    }

    public TableMap ignoreColumn(String columnName) {
        this.ignores.add(columnName);
        return this;
    }

    public TableMap ignoreColumn(ModelProjection column) {
        this.ignores.add(column.getSimpleName());
        return this;
    }

    public boolean isColumnIgnored(String columnName) {
        return this.ignores.contains(columnName);
    }
}
