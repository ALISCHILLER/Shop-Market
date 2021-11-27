package com.varanegar.supervisor.model.reviewreport;

import com.varanegar.framework.database.model.ModelProjection;

public class ItemsView extends ModelProjection<items> {
    protected ItemsView(String name) {
        super(name);
    }

    public static ItemsView productCategory = new ItemsView("ItemsView.productCategory");
    public static ItemsView amount = new ItemsView("ItemsView.amount");
    public static ItemsView productCode = new ItemsView("ItemsView.productCode");
    public static ItemsView productName = new ItemsView("ItemsView.productName");
    public static ItemsView productCount = new ItemsView("ItemsView.productCount");
    public static ItemsView productCountStr = new ItemsView("ItemsView.productCountStr");
    public static ItemsView tax = new ItemsView("ItemsView.tax");
}
