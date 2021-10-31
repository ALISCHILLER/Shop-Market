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

    public boolean isMihanKish() {
        if (DataOwnerKey == null)
            return false;
        return DataOwnerKey.equalsIgnoreCase("8638C58A-F145-467D-960E-817EA44EBF45");
    }
    public boolean isPoober() {
        if (DataOwnerKey == null)
            return false;
        return DataOwnerKey.equalsIgnoreCase("23383A6C-308E-4F63-AF42-37C09496208B");
    }
    public String OwnerKey;
    public String DataOwnerKey;
    public String DataOwnerCenterKey;
}
