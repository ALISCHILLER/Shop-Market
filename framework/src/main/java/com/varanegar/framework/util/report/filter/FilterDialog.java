package com.varanegar.framework.util.report.filter;

import com.varanegar.framework.util.component.CuteDialogWithToolbar;

/**
 * Created by atp on 12/31/2016.
 */
public abstract class FilterDialog extends CuteDialogWithToolbar {

    public interface OnFilterChangeHandler {
        void run(Filter filter);
    }
    public OnFilterChangeHandler onFilterChange;
}
