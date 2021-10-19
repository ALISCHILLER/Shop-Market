package com.varanegar.framework.database.querybuilder.projection;


import com.varanegar.framework.database.model.ModelProjection;
import com.varanegar.framework.database.querybuilder.Utils;

import java.util.List;

public class OperatorProjection extends Projection {
    public Projection getColumn1() {
        return column1;
    }

    public Projection getColumn2() {
        return column2;
    }

    private final Projection column1;
    private final Projection column2;

    public static class Type {
        public static final int ADD = 1;
        public static final int SUBTRACT = 2;
        public static int Divide = 3;
        public static int Multiply = 4;
    }


    public int getType() {
        return type;
    }

    private int type;

    public OperatorProjection(Projection column1, Projection column2, int type) {
        this.type = type;
        this.column1 = column1;
        this.column2 = column2;
    }

    @Override
    public String build() {
        if (type == Type.ADD)
            return " ifnull(" + column1.build() + ",0)" + " + ifnull(" + column2.build() + ",0) ";
        else if (type == Type.SUBTRACT)
            return " ifnull(" + column1.build() + ",0)" + " - ifnull(" + column2.build() + ",0) ";
        else if (type == Type.Multiply)
            return " ifnull(" + column1.build() + ",0)" + " * ifnull(" + column2.build() + ",0) ";
        else
            return " ifnull(" + column1.build() + ",0)" + " / ifnull(" + column2.build() + ",1) ";
    }

    @Override
    public List<Object> buildParameters() {
        return Utils.EMPTY_LIST;
    }
}
