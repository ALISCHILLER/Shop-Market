package com.varanegar.framework.database.querybuilder.criteria;


import com.varanegar.framework.database.model.ModelProjection;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.BasicCriteria.Operators;
import com.varanegar.framework.database.querybuilder.projection.Projection;

import java.util.Date;
import java.util.List;

public abstract class Criteria implements Cloneable {
    @Override
    public Criteria clone() throws CloneNotSupportedException {
        if (this instanceof AndCriteria) {
            AndCriteria criteria = (AndCriteria) this;
            AndCriteria andCriteria = new AndCriteria(criteria.getLeft().clone(), criteria.getRight().clone());
            return andCriteria;
        } else if (this instanceof BasicCriteria) {
            BasicCriteria criteria = (BasicCriteria) this;
            BasicCriteria basicCriteria = new BasicCriteria(criteria.getProjection().clone(), criteria.getOperator(), criteria.getValue());
            return basicCriteria;
        } else if (this instanceof BetweenCriteria) {
            BetweenCriteria criteria = (BetweenCriteria) this;
            BetweenCriteria betweenCriteria = new BetweenCriteria(criteria.getProjection().clone(), criteria.getValueStart(), criteria.getValueEnd());
            return betweenCriteria;
        } else if (this instanceof ExistsCriteria) {
            ExistsCriteria criteria = (ExistsCriteria) this;
            ExistsCriteria existsCriteria = new ExistsCriteria(criteria.getSubQuery().clone());
            return existsCriteria;
        } else if (this instanceof InCriteria) {
            InCriteria criteria = (InCriteria) this;
            if (criteria.getValuesList() != null) {
                InCriteria inCriteria = new InCriteria(criteria.getProjection().clone(), criteria.getValuesList());
                return inCriteria;
            } else if (criteria.getValuesArray() != null) {
                InCriteria inCriteria = new InCriteria(criteria.getProjection().clone(), criteria.getValuesArray());
                return inCriteria;
            } else if (criteria.inQuery != null) {
                InCriteria inCriteria = new InCriteria(criteria.getProjection().clone(), criteria.inQuery);
                return inCriteria;
            }
        } else if (this instanceof NotExistsCriteria) {
            NotExistsCriteria criteria = (NotExistsCriteria) this;
            NotExistsCriteria notExistsCriteria = new NotExistsCriteria(criteria.getSubQuery().clone());
            return notExistsCriteria;
        } else if (this instanceof NotInCriteria) {
            NotInCriteria criteria = (NotInCriteria) this;
            if (criteria.getValuesList() != null) {
                NotInCriteria notInCriteria = new NotInCriteria(criteria.getProjection().clone(), criteria.getValuesList());
                return notInCriteria;
            } else if (criteria.getValuesArray() != null) {
                NotInCriteria notInCriteria = new NotInCriteria(criteria.getProjection().clone(), criteria.getValuesArray());
                return notInCriteria;
            }
        } else if (this instanceof OrCriteria) {
            OrCriteria criteria = (OrCriteria) this;
            OrCriteria orCriteria = new OrCriteria(criteria.getLeft().clone(), criteria.getRight().clone());
            return orCriteria;
        } else if (this instanceof ValueBetweenCriteria) {
            ValueBetweenCriteria criteria = (ValueBetweenCriteria) this;
            ValueBetweenCriteria valueBetweenCriteria = new ValueBetweenCriteria(criteria.getValue(), criteria.getProjectionStart().clone(), criteria.getProjectionEnd().clone());
            return valueBetweenCriteria;
        }
        return null;
    }

    // Null
    public static Criteria isNull(ModelProjection column) {
        return new BasicCriteria(Projection.column(column), Operators.IS_NULL, null);
    }

    public static Criteria notIsNull(ModelProjection column) {
        return new BasicCriteria(Projection.column(column), Operators.IS_NOT_NULL, null);
    }

    public static Criteria isNull(Projection projection) {
        return new BasicCriteria(projection, Operators.IS_NULL, null);
    }

    public static Criteria notIsNull(Projection projection) {
        return new BasicCriteria(projection, Operators.IS_NOT_NULL, null);
    }


    // Basic criterias
    public static Criteria equals(ModelProjection column, Object value) {
        return new BasicCriteria(Projection.column(column), Operators.EQUALS, value);
    }

    public static Criteria equalsColumn(ModelProjection column1, ModelProjection column2) {
        return new BasicCriteria(Projection.column(column1), Operators.EQUALS, Projection.column(column2));
    }

    public static Criteria notEquals(ModelProjection column, Object value) {
        return new BasicCriteria(Projection.column(column), Operators.NOT_EQUALS, value);
    }

    public static Criteria greaterThan(ModelProjection column, Object value) {
        return new BasicCriteria(Projection.column(column), Operators.GREATER, value);
    }

    public static Criteria greaterThan(ModelProjection column1, ModelProjection column2) {
        return new BasicCriteria(Projection.column(column1), Operators.GREATER, Projection.column(column2));
    }

    public static Criteria lesserThan(ModelProjection column, Object value) {
        return new BasicCriteria(Projection.column(column), Operators.LESSER, value);
    }

    public static Criteria lesserThan(ModelProjection column1, ModelProjection column2) {
        return new BasicCriteria(Projection.column(column1), Operators.LESSER, Projection.column(column2));
    }

    public static Criteria greaterThanOrEqual(ModelProjection column, Object value) {
        return new BasicCriteria(Projection.column(column), Operators.GREATER_OR_EQUALS, value);
    }

    public static Criteria greaterThanOrEqual(ModelProjection column1, ModelProjection column2) {
        return new BasicCriteria(Projection.column(column1), Operators.GREATER_OR_EQUALS, Projection.column(column2));
    }

    public static Criteria lesserThanOrEqual(ModelProjection column, Object value) {
        return new BasicCriteria(Projection.column(column), Operators.LESSER_OR_EQUALS, value);
    }

    public static Criteria lesserThanOrEqual(ModelProjection column1, ModelProjection column2) {
        return new BasicCriteria(Projection.column(column1), Operators.LESSER_OR_EQUALS, Projection.column(column2));
    }

    public static Criteria equals(Projection column, Object value) {
        return new BasicCriteria(column, Operators.EQUALS, value);
    }

    public static Criteria notEquals(Projection column, Object value) {
        return new BasicCriteria(column, Operators.NOT_EQUALS, value);
    }

    public static Criteria greaterThan(Projection column, Object value) {
        return new BasicCriteria(column, Operators.GREATER, value);
    }

    public static Criteria lesserThan(Projection column, Object value) {
        return new BasicCriteria(column, Operators.LESSER, value);
    }

    public static Criteria greaterThanOrEqual(Projection column, Object value) {
        return new BasicCriteria(column, Operators.GREATER_OR_EQUALS, value);
    }

    public static Criteria lesserThanOrEqual(Projection column, Object value) {
        return new BasicCriteria(column, Operators.LESSER_OR_EQUALS, value);
    }


    // String-only criterias
    public static Criteria startsWith(ModelProjection column, String value) {
        return new BasicCriteria(Projection.column(column), Operators.LIKE, value + "%");
    }

    public static Criteria notStartsWith(ModelProjection column, String value) {
        return new BasicCriteria(Projection.column(column), Operators.NOT_LIKE, value + "%");
    }

    public static Criteria endsWith(ModelProjection column, String value) {
        return new BasicCriteria(Projection.column(column), Operators.LIKE, "%" + value);
    }

    public static Criteria notEndsWith(ModelProjection column, String value) {
        return new BasicCriteria(Projection.column(column), Operators.NOT_LIKE, "%" + value);
    }

    public static Criteria contains(ModelProjection column, String value) {
        return new BasicCriteria(Projection.column(column), Operators.LIKE, "%" + value + "%");
    }

    public static Criteria notContains(ModelProjection column, String value) {
        return new BasicCriteria(Projection.column(column), Operators.NOT_LIKE, "%" + value + "%");
    }

    public static Criteria startsWith(Projection column, String value) {
        return new BasicCriteria(column, Operators.LIKE, value + "%");
    }

    public static Criteria notStartsWith(Projection column, String value) {
        return new BasicCriteria(column, Operators.NOT_LIKE, value + "%");
    }

    public static Criteria endsWith(Projection column, String value) {
        return new BasicCriteria(column, Operators.LIKE, "%" + value);
    }

    public static Criteria notEndsWith(Projection column, String value) {
        return new BasicCriteria(column, Operators.NOT_LIKE, "%" + value);
    }

    public static Criteria contains(Projection column, String value) {
        return new BasicCriteria(column, Operators.LIKE, "%" + value + "%");
    }

    public static Criteria notContains(Projection column, String value) {
        return new BasicCriteria(column, Operators.NOT_LIKE, "%" + value + "%");
    }


    // Between
    public static Criteria between(ModelProjection column, Object valueMin, Object valueMax) {
        return new BetweenCriteria(Projection.column(column), valueMin, valueMax);
    }

    public static Criteria valueBetween(Object value, ModelProjection columnMin, ModelProjection columnMax) {
        return new ValueBetweenCriteria(value, Projection.column(columnMin), Projection.column(columnMax));
    }

    public static Criteria between(Projection column, Object valueMin, Object valueMax) {
        return new BetweenCriteria(column, valueMin, valueMax);
    }

    public static Criteria valueBetween(Object value, Projection columnMin, Projection columnMax) {
        return new ValueBetweenCriteria(value, columnMin, columnMax);
    }


    // Exists
    public static Criteria exists(Query subQuery) {
        return new ExistsCriteria(subQuery);
    }

    public static Criteria notExists(Query subQuery) {
        return new NotExistsCriteria(subQuery);
    }


    // In
    public static Criteria in(ModelProjection column, Object[] values) {
        return new InCriteria(Projection.column(column), values);
    }

    public static Criteria notIn(ModelProjection column, Object[] values) {
        return new NotInCriteria(Projection.column(column), values);
    }

    public static Criteria in(ModelProjection column, List values) {
        return new InCriteria(Projection.column(column), values);
    }

    public static Criteria in(ModelProjection column1, Query in) {
        return new InCriteria(Projection.column(column1), in);
    }


    public static Criteria notIn(ModelProjection column, List<Object> values) {
        return new NotInCriteria(Projection.column(column), values);
    }

    public static Criteria in(Projection column, Object[] values) {
        return new InCriteria(column, values);
    }

    public static Criteria notIn(Projection column, Object[] values) {
        return new NotInCriteria(column, values);
    }

    public static Criteria in(Projection column, List<Object> values) {
        return new InCriteria(column, values);
    }

    public static Criteria notIn(Projection column, List<Object> values) {
        return new NotInCriteria(column, values);
    }

    public abstract String build();

    public abstract List<Object> buildParameters();

    public String[] buildStringParameters() {
        List<Object> objects = buildParameters();
        if (objects.size() == 0)
            return null;
        String[] strings = new String[objects.size()];
        for (int i = 0; i < objects.size(); i++) {
            Object obj = objects.get(i);
            if (obj instanceof Date)
                strings[i] = Long.toString(((Date) obj).getTime());
            else if (obj instanceof Boolean)
                strings[i] = (boolean) obj ? "1" : "0";
            else
                strings[i] = obj.toString();
        }
        return strings;
    }

    public AndCriteria and(Criteria criteria) {
        return new AndCriteria(this, criteria);
    }

    public OrCriteria or(Criteria criteria) {
        return new OrCriteria(this, criteria);
    }
}
