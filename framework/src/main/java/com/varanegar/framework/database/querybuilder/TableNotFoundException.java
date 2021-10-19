package com.varanegar.framework.database.querybuilder;

/**
 * Created by A.Torabi on 2/19/2019.
 */

public class TableNotFoundException extends RuntimeException {

    public TableNotFoundException(String dbName, String tblName) {
        super("Table '" + tblName + "' not found in database " + dbName);
    }
}
