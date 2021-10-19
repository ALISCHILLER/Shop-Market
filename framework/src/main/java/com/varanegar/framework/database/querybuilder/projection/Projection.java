package com.varanegar.framework.database.querybuilder.projection;


import com.varanegar.framework.database.model.ModelProjection;
import com.varanegar.framework.database.querybuilder.Query;

import java.util.List;

public abstract class Projection implements Cloneable {
    @Override
    public Projection clone() throws CloneNotSupportedException {
        if (this instanceof ColumnProjection) {
            ColumnProjection cp = (ColumnProjection) this;
            ColumnProjection projection = new ColumnProjection(cp.getTable(), cp.getColumn());
            return projection;
        } else if (this instanceof ConstantProjection) {
            ConstantProjection cp = (ConstantProjection) this;
            ConstantProjection projection = new ConstantProjection(cp.getConstant());
            return projection;
        } else if (this instanceof AggregateProjection) {
            AggregateProjection p = (AggregateProjection) this;
            AggregateProjection projection = new AggregateProjection(p.getProjection().clone(), p.getType());
            return projection;
        } else if (this instanceof OperatorProjection) {
            OperatorProjection p = (OperatorProjection) this;
            OperatorProjection projection = new OperatorProjection(p.getColumn1().clone(), p.getColumn2().clone(), p.getType());
            return projection;
        } else if (this instanceof OperatorProjection) {
            OperatorProjection1 p = (OperatorProjection1) this;
            OperatorProjection1 projection = new OperatorProjection1(p.getColumn(), p.getValue(), p.getType());
            return projection;
        } else if (this instanceof WhenProjection) {
            WhenProjection p = (WhenProjection) this;
            WhenProjection projection = new WhenProjection(p.whens);
            return projection;
        } else if (this instanceof SubQueryProjection) {
            SubQueryProjection p = (SubQueryProjection) this;
            SubQueryProjection projection = new SubQueryProjection(p.getSubQuery().clone());
            return projection;
        } else if (this instanceof AliasedProjection) {
            AliasedProjection p = (AliasedProjection) this;
            AliasedProjection projection = new AliasedProjection(p.getProjection(), p.getAlias());
            return projection;
        } else if (this instanceof CastRealProjection) {
            CastRealProjection p = (CastRealProjection) this;
            CastRealProjection projection = new CastRealProjection(p.getProjection().clone());
            return projection;
        } else if (this instanceof CastIntProjection) {
            CastIntProjection p = (CastIntProjection) this;
            CastIntProjection projection = new CastIntProjection(p.getProjection().clone());
            return projection;
        } else if (this instanceof CastStringProjection) {
            CastStringProjection p = (CastStringProjection) this;
            CastStringProjection projection = new CastStringProjection(p.getProjection().clone());
            return projection;
        }
        return null;
    }

    // Simple column
    public static ColumnProjection column(ModelProjection column) {
        return new ColumnProjection(null, column);
    }

    public static ColumnProjection column(ModelProjection table, ModelProjection column) {
        return new ColumnProjection(table, column);
    }


    // Constant
    public static ConstantProjection constant(Object constant) {
        return new ConstantProjection(constant);
    }


    // Aggregate functions
    public static AggregateProjection min(ModelProjection column) {

        return min(column(column));
    }

    public static AggregateProjection max(ModelProjection column) {
        return max(column(column));
    }

    public static AggregateProjection sum(ModelProjection column) {
        return sum(column(column));
    }

    public static AggregateProjection avg(ModelProjection column) {
        return avg(column(column));
    }

    public static AggregateProjection count(ModelProjection column) {
        return count(column(column));
    }

    public static AggregateProjection countRows() {
        return count(column(ModelProjection.All));
    }

    public static AggregateProjection min(Projection projection) {
        return new AggregateProjection(projection, AggregateProjection.Type.MIN);
    }

    public static AggregateProjection max(Projection projection) {
        return new AggregateProjection(projection, AggregateProjection.Type.MAX);
    }

    public static AggregateProjection sum(Projection projection) {
        return new AggregateProjection(projection, AggregateProjection.Type.SUM);
    }

    public static AggregateProjection avg(Projection projection) {
        return new AggregateProjection(projection, AggregateProjection.Type.AVG);
    }

    public static AggregateProjection count(Projection projection) {
        return new AggregateProjection(projection, AggregateProjection.Type.COUNT);
    }


    // SubQuery
    public static SubQueryProjection subQuery(Query subQuery) {
        return new SubQueryProjection(subQuery);
    }

    public Projection as(String alias) {
        return new AliasedProjection(this, alias);
    }

    public Projection castAsReal() {
        return new CastRealProjection(this);
    }

    public Projection castAsInt() {
        return new CastIntProjection(this);
    }

    public Projection castAsBoolean() {
        return new CastIntProjection(this);
    }

    public Projection castAsString() {
        return new CastStringProjection(this);
    }

    public abstract String build();

    public abstract List<Object> buildParameters();

    public static Projection subtract(ModelProjection column1, ModelProjection column2) {
        return new OperatorProjection(column(column1), column(column2), OperatorProjection.Type.SUBTRACT);
    }

    public static Projection subtract(Projection column1, Projection column2) {
        return new OperatorProjection(column1, column2, OperatorProjection.Type.SUBTRACT);
    }

    public static Projection add(ModelProjection column1, ModelProjection column2) {
        return new OperatorProjection(column(column1), column(column2), OperatorProjection.Type.ADD);
    }

    public static Projection add(Projection column1, Projection column2) {
        return new OperatorProjection(column1, column2, OperatorProjection.Type.ADD);
    }

    public static Projection caseWhen(When... when) {
        return new WhenProjection(when);
    }

    public static Projection multiply(Projection column, Object value) {
        return new OperatorProjection1(column, value, OperatorProjection1.Type.Multiply);
    }

    public static Projection multiply(ModelProjection column, Object value) {
        return new OperatorProjection1(column(column), value, OperatorProjection1.Type.Multiply);
    }

    public static Projection divide(Projection column1, Projection column2) {
        return new OperatorProjection(column1, column2, OperatorProjection.Type.Divide);
    }

    public static Projection divide(ModelProjection column1, ModelProjection column2) {
        return new OperatorProjection(column(column1), column(column2), OperatorProjection.Type.Divide);
    }

    public static Projection add(ModelProjection column, Object value) {
        return new OperatorProjection1(column(column), value, OperatorProjection1.Type.ADD);
    }

    public static Projection add(Projection column, Object value) {
        return new OperatorProjection1(column, value, OperatorProjection1.Type.ADD);
    }

    public static Projection ifNull(ColumnProjection column, Object value) {
        return new OperatorProjection1(column, value, OperatorProjection1.Type.IFNULL);
    }

    public static Projection groupConcat(ColumnProjection column, String separator) {
        return new OperatorProjection1(column, separator, OperatorProjection1.Type.GROUP_CONCAT);
    }

    public static Projection groupConcat(ModelProjection column, String separator) {
        return groupConcat(Projection.column(column), separator);
    }
}
