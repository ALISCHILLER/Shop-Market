package com.varanegar.framework.util.prefs;

import java.util.UUID;

public class PrefSet {
    private final String setId;
    private final boolean deleteAfterTour;

    PrefSet(String setId, boolean deleteAfterTour) {
        this.setId = setId;
        this.deleteAfterTour = deleteAfterTour;
    }

    public String getId() {
        return setId;
    }

    public boolean isDeleteAfterTour() {
        return deleteAfterTour;
    }


}
