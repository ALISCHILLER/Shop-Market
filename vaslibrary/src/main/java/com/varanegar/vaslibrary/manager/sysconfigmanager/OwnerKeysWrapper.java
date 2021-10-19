package com.varanegar.vaslibrary.manager.sysconfigmanager;

/**
 * Created by A.Torabi on 10/3/2017.
 */

public class OwnerKeysWrapper {
    public boolean isZarMakaron() {
        if (DataOwnerKey == null)
            return false;
        return DataOwnerKey.equalsIgnoreCase("62d0ac38-68bf-430a-8d38-f44fcd2159fb");
    }

    public String OwnerKey;
    public String DataOwnerKey;
    public String DataOwnerCenterKey;
}
