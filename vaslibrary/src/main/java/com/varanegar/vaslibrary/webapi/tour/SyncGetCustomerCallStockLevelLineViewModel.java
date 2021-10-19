package com.varanegar.vaslibrary.webapi.tour;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 11/4/2017.
 */

public class SyncGetCustomerCallStockLevelLineViewModel {
    public UUID UniqueId;
    public UUID CustomerCallStockLevelUniqueId;
    public boolean IsPurchased;
    public UUID ProductUniqueId;
    public boolean HasLevel;
    public List<SyncGetCustomerCallStockLevelLineQtyDetailViewModel> CustomerCallStockLevelLineQtyDetails = new ArrayList<>();
}
