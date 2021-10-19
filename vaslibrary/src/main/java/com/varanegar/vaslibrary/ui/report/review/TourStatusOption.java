package com.varanegar.vaslibrary.ui.report.review;

import com.varanegar.vaslibrary.ui.report.review.OptionId;

/**
 * Created by A.Torabi on 7/3/2018.
 */

public class TourStatusOption {
    public final OptionId id;
    public boolean value;

    private String name;

    @Override
    public String toString() {
        return name;
    }

    public TourStatusOption(OptionId id, String name, boolean value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }
}
