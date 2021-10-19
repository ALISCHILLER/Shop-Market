package com.varanegar.framework.database.querybuilder.order;


import com.varanegar.framework.database.model.ModelProjection;
import com.varanegar.framework.database.querybuilder.projection.AliasedProjection;
import com.varanegar.framework.database.querybuilder.projection.Projection;

import java.util.List;

public abstract class Order implements Cloneable {
    @Override
    public Order clone() throws CloneNotSupportedException {
        if (this instanceof OrderAscending) {
            OrderAscending orderAscending = new OrderAscending(this.projection.clone());
            return orderAscending;
        } else if (this instanceof OrderAscendingIgnoreCase) {
            OrderAscendingIgnoreCase orderAscendingIgnoreCase = new OrderAscendingIgnoreCase(this.projection.clone());
            return orderAscendingIgnoreCase;
        } else if (this instanceof OrderDescending) {
            OrderDescending orderDescending = new OrderDescending(this.projection.clone());
            return orderDescending;
        } else if (this instanceof OrderDescendingIgnoreCase) {
            OrderDescendingIgnoreCase orderDescendingIgnoreCase = new OrderDescendingIgnoreCase(this.projection.clone());
            return orderDescendingIgnoreCase;
        }
        return null;
    }

    public static Order orderByAscending(ModelProjection column) {
        return new OrderAscending(Projection.column(column));
    }

    public static Order orderByDescending(ModelProjection column) {
        return new OrderDescending(Projection.column(column));
    }

    public static Order orderByAscending(Projection projection) {
        return new OrderAscending(projection);
    }

    public static Order orderByDescending(Projection projection) {
        return new OrderDescending(projection);
    }

    public static Order orderByAscendingIgnoreCase(ModelProjection column) {
        return new OrderAscendingIgnoreCase(Projection.column(column));
    }

    public static Order orderByDescendingIgnoreCase(ModelProjection column) {
        return new OrderDescendingIgnoreCase(Projection.column(column));
    }

    public static Order orderByAscendingIgnoreCase(Projection projection) {
        return new OrderAscendingIgnoreCase(projection);
    }

    public static Order orderByDescendingIgnoreCase(Projection projection) {
        return new OrderDescendingIgnoreCase(projection);
    }


    protected Projection projection;

    public Order(Projection projection) {
        this.projection = projection;

        if (this.projection instanceof AliasedProjection)
            this.projection = ((AliasedProjection) this.projection).removeAlias();
    }

    public abstract String build();

    public abstract List<Object> buildParameters();
}
