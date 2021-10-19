package com.varanegar.framework.database.querybuilder;

import com.varanegar.framework.database.model.ModelProjection;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.from.From;
import com.varanegar.framework.database.querybuilder.order.Order;
import com.varanegar.framework.database.querybuilder.projection.AliasedProjection;
import com.varanegar.framework.database.querybuilder.projection.Projection;
import com.varanegar.framework.util.Linq;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Query implements Cloneable {
    private List<Projection> projections;
    private From from;
    private Criteria criteria;
    private Criteria hcriteria;
    private List<Projection> groupBy;
    private List<Order> orderBy;
    private int skip;
    private int take;
    private boolean distinct;

    private List<Query> unionQueries;
    private boolean unionAll;

    @Override
    public Query clone() throws CloneNotSupportedException {
        Query builder = new Query();
        if (this.projections != null) {
            builder.projections = new ArrayList<>();
            for (Projection projection : this.projections
                    ) {
                builder.projections.add(projection.clone());
            }
        }
        if (this.from != null)
            builder.from = this.from.clone();
        if (this.criteria != null)
            builder.criteria = this.criteria.clone();
        if (this.groupBy != null) {
            builder.groupBy = new ArrayList<>();
            for (Projection projection : this.groupBy
                    ) {
                builder.groupBy.add(projection.clone());
            }
        }
        if (this.orderBy != null) {
            builder.orderBy = new ArrayList<>();
            for (Order order : this.orderBy
                    ) {
                builder.orderBy.add(order.clone());
            }
        }
        builder.skip = this.skip;
        builder.take = this.take;
        builder.distinct = this.distinct;
        if (this.unionQueries != null) {
            builder.unionQueries = new ArrayList<>();
            for (Query union : this.unionQueries
                    ) {
                builder.unionQueries.add(union.clone());
            }
        }
        builder.unionAll = this.unionAll;
        return builder;
    }

    public Query() {
        projections = new ArrayList<Projection>();
        from = null;
        criteria = null;
        groupBy = new ArrayList<Projection>();
        orderBy = new ArrayList<Order>();
        skip = -1;
        take = -1;
        distinct = false;

        unionQueries = new ArrayList<Query>();
        unionAll = false;

    }


    public Query select(ModelProjection... columns) {
        if (columns == null)
            return this;

        return select(Utils.buildColumnProjections(columns));
    }

    public Query select(Projection... projections) {
        if (projections == null)
            return this;

        for (int i = 0; i < projections.length; i++) {
            this.projections.add(projections[i]);
        }

        return this;
    }

    public Query from(ModelProjection table) {
        return from(From.table(table));
    }

    public Query from(Query subQuery) {
        return from(From.subQuery(subQuery));
    }

    public Query from(From from) {
        if (from != null)
            this.from = from;

        return this;
    }

    public Query whereAnd(Criteria criteria) {
        if (criteria != null) {
            if (this.criteria == null)
                this.criteria = criteria;
            else
                this.criteria = this.criteria.and(criteria);
        }

        return this;
    }

    public Query whereOr(Criteria criteria) {
        if (criteria != null) {
            if (this.criteria == null)
                this.criteria = criteria;
            else
                this.criteria = this.criteria.or(criteria);
        }

        return this;
    }

    public Query havingAnd(Criteria criteria) {
        if (criteria != null) {
            if (this.hcriteria == null)
                this.hcriteria = criteria;
            else
                this.hcriteria = this.hcriteria.and(criteria);
        }

        return this;
    }

    public Query havingOr(Criteria criteria) {
        if (criteria != null) {
            if (this.hcriteria == null)
                this.hcriteria = criteria;
            else
                this.hcriteria = this.hcriteria.or(criteria);
        }

        return this;
    }

    public Query groupBy(ModelProjection... columns) {
        if (columns == null)
            return this;

        return groupBy(Utils.buildColumnProjections(columns));
    }

    public Query groupBy(Projection... projections) {
        if (projections == null)
            return this;

        for (int i = 0; i < projections.length; i++) {
            this.groupBy.add(projections[i]);
        }

        return this;
    }

    public Query clearGroupBy() {
        this.groupBy.clear();
        return this;
    }

    public Query orderByAscending(ModelProjection... columns) {
        if (columns == null)
            return this;

        return orderByAscending(Utils.buildColumnProjections(columns));
    }

    public Query orderByAscending(Projection... projections) {
        if (projections == null)
            return this;

        for (int i = 0; i < projections.length; i++) {
            this.orderBy.add(Order.orderByAscending(projections[i]));
        }

        return this;
    }

    public Query orderByDescending(ModelProjection... columns) {
        if (columns == null)
            return this;

        return orderByDescending(Utils.buildColumnProjections(columns));
    }

    public Query orderByDescending(Projection... projections) {
        if (projections == null)
            return this;

        for (int i = 0; i < projections.length; i++) {
            this.orderBy.add(Order.orderByDescending(projections[i]));
        }

        return this;
    }

    public Query orderByAscendingIgnoreCase(ModelProjection... columns) {
        if (columns == null)
            return this;

        return orderByAscendingIgnoreCase(Utils.buildColumnProjections(columns));
    }

    public Query orderByAscendingIgnoreCase(Projection... projections) {
        if (projections == null)
            return this;

        for (int i = 0; i < projections.length; i++) {
            this.orderBy.add(Order.orderByAscendingIgnoreCase(projections[i]));
        }

        return this;
    }

    public Query orderByDescendingIgnoreCase(ModelProjection... columns) {
        if (columns == null)
            return this;

        return orderByDescendingIgnoreCase(Utils.buildColumnProjections(columns));
    }

    public Query orderByDescendingIgnoreCase(Projection... projections) {
        if (projections == null)
            return this;

        for (int i = 0; i < projections.length; i++) {
            this.orderBy.add(Order.orderByDescendingIgnoreCase(projections[i]));
        }

        return this;
    }

    public Query clearOrderBy() {
        this.orderBy.clear();
        return this;
    }

    public Query skip(int skip) {
        this.skip = skip;
        return this;
    }

    public Query skipNone() {
        this.skip = -1;
        return this;
    }

    public Query take(int take) {
        this.take = take;
        return this;
    }

    public boolean hasLimit() {
        return take > 0;
    }

    public Query takeAll() {
        this.take = -1;
        return this;
    }

    public Query distinct() {
        this.distinct = true;
        return this;
    }

    public Query notDistinct() {
        this.distinct = false;
        return this;
    }

    public Query union(Query query) {
        query.unionAll = false;
        unionQueries.add(query);

        return this;
    }

    public Query unionAll(Query query) {
        query.unionAll = true;
        unionQueries.add(query);

        return this;
    }

    public String build() {
        StringBuilder sb = new StringBuilder();

        buildSelectClause(sb);

        buildFromClause(sb);

        buildWhereClause(sb);

        buildGroupByClause(sb);

        buildHavingClause(sb);

        buildUnionClause(sb);

        buildOrderByClause(sb);

        buildTakeClause(sb);

        buildSkipClause(sb);

        if (hcriteria != null) {
            List<Object> parameters = new ArrayList<>();
            if (hcriteria != null) {
                parameters.addAll(hcriteria.buildParameters());
                if (parameters.size() > 0) {
                    String saida = sb.toString();
                    int wpc = 0;
                    if (criteria != null)
                        wpc = criteria.buildParameters().size();
                    for (int i = 0; i < wpc; i++)
                        saida = saida.replaceFirst("\\?", "###");
                    for (Object p : parameters) {
                        if (p == null)
                            saida = saida.replaceFirst("\\?", "NULL");
                        else {
                            if (p.getClass() == String.class)
                                saida = saida.replaceFirst("\\?", escapeSQLString(Utils.toString(p)));
                            else
                                saida = saida.replaceFirst("\\?", Utils.toString(p));
                        }
                    }
                    for (int i = 0; i < wpc; i++)
                        saida = saida.replaceFirst("###", "\\?");
                    return saida;
                }
            }
        }
        return sb.toString();
    }

    private void buildSkipClause(StringBuilder sb) {
        if (skip > 0) {
            sb.append(" OFFSET ");
            sb.append(skip);
        }
    }

    private void buildTakeClause(StringBuilder sb) {
        if (take > 0) {
            sb.append(" LIMIT ");
            sb.append(take);
        }
    }

    private void buildOrderByClause(StringBuilder sb) {
        if (orderBy.size() > 0) {
            sb.append(" ORDER BY ");

            for (Order o : orderBy) {
                sb.append(o.build());
                sb.append(", ");
            }

            sb.setLength(sb.length() - 2); // removes the ", " from the last entry
        }
    }

    private void buildUnionClause(StringBuilder sb) {
        List<Order> oldOrderBy;
        int oldSkip;
        int oldTake;

        for (Query union : unionQueries) {
            sb.append(union.unionAll ? " UNION ALL " : " UNION ");

            oldOrderBy = union.orderBy;
            oldSkip = union.skip;
            oldTake = union.take;

            union.orderBy = new ArrayList<Order>();
            union.skip = -1;
            union.take = -1;

            sb.append(union.build());

            union.orderBy = oldOrderBy;
            union.skip = oldSkip;
            union.take = oldTake;
        }
    }

    private void buildGroupByClause(StringBuilder sb) {
        if (groupBy.size() > 0) {
            sb.append(" GROUP BY ");

            for (Projection p : groupBy) {
                if (p instanceof AliasedProjection)
                    p = ((AliasedProjection) p).removeAlias();

                sb.append(p.build());
                sb.append(", ");
            }

            sb.setLength(sb.length() - 2); // removes the ", " from the last entry
        }
    }

    private void buildHavingClause(StringBuilder sb) {
        if (hcriteria != null) {
            sb.append(" HAVING ");
            sb.append(hcriteria.build());
        }
    }

    private void buildWhereClause(StringBuilder sb) {
        if (criteria != null) {
            sb.append(" WHERE ");
            sb.append(criteria.build());
        }
    }

    private void buildFromClause(StringBuilder sb) {
        if (from != null) {
            sb.append("FROM ");
            sb.append(from.build());
            sb.append(" ");
        }
    }

    private void buildSelectClause(StringBuilder sb) {
        sb.append("SELECT ");

        if (distinct)
            sb.append("DISTINCT ");

        if (projections.size() <= 0) {
            sb.append("*");
        } else {
            for (Projection p : projections) {
                sb.append(p.build());
                sb.append(", ");
            }

            sb.setLength(sb.length() - 2); // removes the ", " from the last entry
        }

        sb.append(" ");
    }

    public List<Object> buildParameters() {
        List<Object> ret = new ArrayList<Object>();
        List<Order> oldOrderBy;
        int oldSkip;
        int oldTake;

        buildSelectClauseParameters(ret);


        if (from != null)
            ret.addAll(from.buildParameters());

        if (criteria != null)
            ret.addAll(criteria.buildParameters());

        for (Projection p : groupBy) {
            ret.addAll(p.buildParameters());
        }

        for (Query union : unionQueries) {
            oldOrderBy = union.orderBy;
            oldSkip = union.skip;
            oldTake = union.take;

            union.orderBy = new ArrayList<Order>();
            union.skip = -1;
            union.take = -1;

            ret.addAll(union.buildParameters());

            union.orderBy = oldOrderBy;
            union.skip = oldSkip;
            union.take = oldTake;
        }

        for (Order o : orderBy) {
            ret.addAll(o.buildParameters());
        }

        return ret;
    }

    private void buildSelectClauseParameters(List<Object> ret) {
        for (Projection p : projections) {
            ret.addAll(p.buildParameters());
        }
    }

    public String toDebugSqlString() {
        List<Object> parameters = buildParameters();
        String saida = build();

        if (parameters != null) {
            for (Object p : parameters) {
                if (p == null)
                    saida = saida.replaceFirst("\\?", "NULL");
                else
                    saida = saida.replaceFirst("\\?", escapeSQLString(Utils.toString(p)));
            }
        }

        return saida;
    }

    private String escapeSQLString(String sqlString) {
        // Copied from Android source: DatabaseUtils.appendEscapedSQLString
        StringBuilder sb = new StringBuilder();
        sb.append('\'');

        if (sqlString.indexOf('\'') != -1) {
            int length = sqlString.length();
            for (int i = 0; i < length; i++) {
                char c = sqlString.charAt(i);
                if (c == '\'') {
                    sb.append('\'');
                }
                sb.append(c);
            }
        } else
            sb.append(sqlString);

        sb.append('\'');
        return sb.toString();
    }

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

    @Override
    public String toString() {
        String[] parameters = buildStringParameters();
        List<String> paras = new ArrayList<>();
        for (String p :
                parameters) {
            paras.add(p);
        }
        return "Query = " + build() +
                System.getProperty("line.separator") +
                "parameters : " +
                System.getProperty("line.separator") +
                Linq.merge(paras
                        , new Linq.Merge<String>() {
                            @Override
                            public String run(String item1, String item2) {
                                return item1 + System.getProperty("line.separator") + item2;
                            }
                        });
    }
}
