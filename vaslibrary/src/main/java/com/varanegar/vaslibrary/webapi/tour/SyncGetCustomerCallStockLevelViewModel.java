package com.varanegar.vaslibrary.webapi.tour;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 11/4/2017.
 */

public class SyncGetCustomerCallStockLevelViewModel {
    public UUID UniqueId;
    public UUID CustomerCallUniqueId;
    public String Comment;
    public List<SyncGetCustomerCallStockLevelLineViewModel> CustomerCallStockLevelLines = new ArrayList<>();
}
