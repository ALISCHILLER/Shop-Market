package com.varanegar.supervisor.fragment.menuTools_Fragment.model;

public class Item {
    private final String nameItem;
    private final int ItemIcon;

    public Item(String nameItem, int itemIcon) {
        this.nameItem = nameItem;
        ItemIcon = itemIcon;
    }

    public String getNameItem() {
        return nameItem;
    }

    public int getItemIcon() {
        return ItemIcon;
    }
}
