package com.varanegar.supervisor.model.reviewreport;

import com.varanegar.framework.database.model.ModelProjection;

public class ItemsView extends ModelProjection<ItemsModel> {
    protected ItemsView(String name) {
        super(name);
    }

    public static ItemsView productCategory = new ItemsView("Items.productCategory");
    public static ItemsView amount = new ItemsView("Items.amount");
    public static ItemsView productCode = new ItemsView("Items.productCode");
    public static ItemsView productName = new ItemsView("Items.productName");
    public static ItemsView productCount = new ItemsView("Items.productCount");
    public static ItemsView productCountStr = new ItemsView("Items.productCountStr");
    public static ItemsView tax = new ItemsView("Items.tax");
    public static ItemsView UniqueId = new ItemsView("Items.UniqueId");
    public static ItemsView ItemsTbl = new ItemsView("Items");
    public static ItemsView ItemsAll = new ItemsView("Items.*");
}
