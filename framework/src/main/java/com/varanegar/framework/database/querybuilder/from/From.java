package com.varanegar.framework.database.querybuilder.from;


import com.varanegar.framework.database.model.ModelProjection;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.projection.Projection;

import java.util.List;

public abstract class From implements Cloneable {
    @Override
    public From clone() throws CloneNotSupportedException {
        if (this instanceof TableFrom) {
            TableFrom from = (TableFrom) this;
            TableFrom tableFrom = new TableFrom(from.getTable());
            return tableFrom;
        } else if (this instanceof SubQueryFrom) {
            SubQueryFrom from = (SubQueryFrom) this;
            SubQueryFrom subQueryFrom = new SubQueryFrom(from.getSubQuery().clone());
            subQueryFrom.alias = from.alias;
            return subQueryFrom;
        } else if (this instanceof JoinFrom) {
            JoinFrom from = (JoinFrom) this;
            JoinFrom joinFrom = new JoinFrom(from.getLeft(), from.getRight(), from.getJoinType(), from.getCriteria());
            return joinFrom;
        }
        return null;
    }

    public static class PartialJoin {
        private String joinType;
        private From left;
        private From right;

        protected PartialJoin(From left, From right, String joinType) {
            this.joinType = joinType;
            this.left = left;
            this.right = right;
        }

        public JoinFrom on(ModelProjection leftColumn, ModelProjection rightColumn) {
            return on(Criteria.equals(Projection.column(leftColumn), Projection.column(rightColumn)));
        }

        public JoinFrom on(Criteria criteria) {
            return new JoinFrom(left, right, joinType, criteria);
        }
    }

    public static TableFrom table(ModelProjection table) {
        return new TableFrom(table);
    }

    public static SubQueryFrom subQuery(Query subQuery) {
        return new SubQueryFrom(subQuery);
    }

    public PartialJoin innerJoin(ModelProjection table) {
        return innerJoin(From.table(table));
    }

    public PartialJoin innerJoin(Query subQuery) {
        return innerJoin(From.subQuery(subQuery));
    }

    public PartialJoin innerJoin(From table) {
        return new PartialJoin(this, table, "INNER JOIN");
    }

    public PartialJoin leftJoin(ModelProjection table) {
        return leftJoin(From.table(table));
    }

    public PartialJoin leftJoin(Query subQuery) {
        return leftJoin(From.subQuery(subQuery));
    }

    public PartialJoin leftJoin(From table) {
        return new PartialJoin(this, table, "LEFT JOIN");
    }

    public abstract String build();

    public abstract List<Object> buildParameters();
}
