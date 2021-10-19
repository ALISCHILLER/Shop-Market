package com.varanegar.framework.database.querybuilder.projection;


import com.varanegar.framework.database.querybuilder.Utils;

import java.util.List;

public class OperatorProjection1 extends Projection {
    public Object getValue() {
        return value;
    }

    private final Object value;

    public Projection getColumn() {
        return column;
    }

    private final Projection column;

    public static class Type {
        public static final int ADD = 1;
        public static final int SUBTRACT = 2;
        public static int Multiply = 3;
        public static int IFNULL = 4;
        public static int GROUP_CONCAT = 5;
    }


    public int getType() {
        return type;
    }

    private int type;

    public OperatorProjection1(Projection column, Object value, int type) {
        this.type = type;
        this.column = column;
        this.value = value;
    }

    @Override
    public String build() {
        if (type == Type.ADD)
            return " ifnull(" + column.build() + ",0)" + " + " + value;
        else if (type == Type.Multiply)
            return " ifnull(" + column.build() + ",0)" + " * " + value;
        else if (type == Type.SUBTRACT)
            return " ifnull(" + column.build() + ",0)" + " - " + value;
        else if (type == Type.IFNULL)
            return " ifnull(" + column.build() + ", " + value + ") ";
        else if (type == Type.GROUP_CONCAT) {
            if (value == null)
                return " group_concat(" + column.build() + ") ";
            else
                return " group_concat(" + column.build() + ", '" + value + "') ";
        } else
            throw new IllegalArgumentException();
    }

    @Override
    public List<Object> buildParameters() {
        return Utils.EMPTY_LIST;
    }
}
