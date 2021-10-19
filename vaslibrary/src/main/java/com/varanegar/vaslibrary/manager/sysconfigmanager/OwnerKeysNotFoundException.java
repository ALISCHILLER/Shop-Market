package com.varanegar.vaslibrary.manager.sysconfigmanager;

/**
 * Created by A.Torabi on 10/3/2017.
 */

public class OwnerKeysNotFoundException extends RuntimeException {
    public OwnerKeysNotFoundException() {
        super("OwnerKey, DataOwnerKey and DataOwnerCenterKey Not found");
    }
}
