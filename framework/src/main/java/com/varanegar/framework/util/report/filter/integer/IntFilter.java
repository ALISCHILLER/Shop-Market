package com.varanegar.framework.util.report.filter.integer;

import com.varanegar.framework.util.report.filter.Filter;

/**
 * Created by atp on 1/1/2017.
 */
public class IntFilter extends Filter {
    public IntFilterOperator operator;
    public int value;
    public enum IntFilterOperator {
        Equals,
        GreaterThan,
        LessThan
    }
}
