package com.varanegar.framework.database;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by A.Torabi on 2/25/2018.
 */

public class DbAttachment {
    /***
     *
     * @param dbName database file name
     */
    public DbAttachment(String dbName){
        this.dbName = dbName;
        this.tables = new ArrayList<>();
    }

    public String getDbName() {
        return dbName;
    }

    private String dbName;

    public List<TableMap> getMapping() {
        return tables;
    }

    public void addMapping(TableMap map){
        tables.add(map);
    }
    private List<TableMap> tables;
}
