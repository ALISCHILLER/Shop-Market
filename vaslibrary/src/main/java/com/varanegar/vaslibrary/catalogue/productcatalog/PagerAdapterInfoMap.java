package com.varanegar.vaslibrary.catalogue.productcatalog;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by A.Torabi on 8/26/2018.
 */

public class PagerAdapterInfoMap extends HashMap<UUID, PagerAdapterInfo> {
    public boolean isOrder() {
        return IsOrder;
    }

    private boolean IsOrder;

    public PagerAdapterInfoMap(boolean isOrder){
        this.IsOrder = isOrder;
    }
}
