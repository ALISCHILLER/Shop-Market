package com.varanegar.vaslibrary.catalogue.productcatalog;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by A.Torabi on 8/26/2018.
 */

public class PagerAdapterInfoMap extends HashMap<UUID, PagerAdapterInfo> {
    @Nullable
    private final UUID CustomerId;
    @Nullable
    private final UUID CallOrderId;

    public boolean isOrder() {
        return IsOrder;
    }

    private boolean IsOrder;

    public PagerAdapterInfoMap(UUID customerId, UUID callOrderId) {
        this.IsOrder = customerId != null && callOrderId != null;
        this.CustomerId = customerId;
        this.CallOrderId = callOrderId;
    }

    @Nullable
    public UUID getCustomerId() {
        return CustomerId;
    }

    @Nullable
    public UUID getCallOrderId() {
        return CallOrderId;
    }
}
