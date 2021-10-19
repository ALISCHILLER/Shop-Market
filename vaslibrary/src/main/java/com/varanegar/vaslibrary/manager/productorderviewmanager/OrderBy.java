package com.varanegar.vaslibrary.manager.productorderviewmanager;

import androidx.annotation.NonNull;

import com.varanegar.framework.database.model.ModelProjection;
import com.varanegar.vaslibrary.model.productorderview.ProductOrderViewModel;

/**
 * Created by A.Torabi on 12/2/2017.
 */

public class OrderBy {
    public OrderBy(@NonNull ModelProjection orderByColumn, @NonNull OrderType order) {
        this.order = order;
        this.orderByColumn = orderByColumn;
    }

    @NonNull
    private ModelProjection orderByColumn;
    @NonNull
    private OrderType order;

    @NonNull
    public ModelProjection getColumn() {
        return orderByColumn;
    }

    @NonNull
    public OrderType getType() {
        return order;
    }
}
