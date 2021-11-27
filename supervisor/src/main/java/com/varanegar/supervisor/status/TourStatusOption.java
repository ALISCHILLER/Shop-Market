package com.varanegar.supervisor.status;

/**
 * Created by A.Torabi on 7/3/2018.
 */

class TourStatusOption {
    final OptionId id;
    boolean value;


    private String name;

    @Override
    public String toString() {
        return name;
    }

    TourStatusOption(OptionId id, String name, boolean value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }
}
