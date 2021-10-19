package com.varanegar.framework.database.querybuilder;

import java.util.List;

/**
 * Created by A.Torabi on 2/19/2019.
 */

public class TableMismatchException extends RuntimeException {

    public String getSrc() {
        return src;
    }

    public String getDest() {
        return dest;
    }

    public List<String> getSourceUnmappedColumns() {
        return leftColumns;
    }

    public List<String> getDestUnmappedColumns() {
        return rightColumns;
    }

    public String listUnmappedColumns() {
        StringBuffer leftlist = new StringBuffer();
        int i = 0;
        for (String left :
                leftColumns) {
            if (i == 0)
                leftlist = leftlist.append(left);
            else
                leftlist = leftlist.append("," + left);
            i++;
        }

        StringBuffer rightlist = new StringBuffer();
        i = 0;
        for (String left :
                rightColumns) {
            if (i == 0)
                rightlist = rightlist.append(left);
            else
                rightlist = rightlist.append("," + left);
            i++;
        }

        return "Source columns = {" + leftlist.toString() + "}. Destination columns = {" + rightlist.toString() + "}";
    }

    private final String src;
    private final String dest;
    private final List<String> leftColumns;
    private final List<String> rightColumns;

    public TableMismatchException(List<String> srcColumns, List<String> destColumns, String src, String dest) {
        super("Source table: " + src + " destination table: " + dest + ". these tables does not match. " +
                (srcColumns.size() > 0 ? "There are " + srcColumns.size() + " columns in source that do not exist in destination table." : "") +
                (destColumns.size() > 0 ? "There are " + destColumns.size() + " columns in destination that do not exist in source table." : ""));
        this.leftColumns = srcColumns;
        this.rightColumns = destColumns;
        this.src = src;
        this.dest = dest;
    }
}
