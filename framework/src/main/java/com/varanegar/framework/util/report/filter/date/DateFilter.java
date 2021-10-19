package com.varanegar.framework.util.report.filter.date;

import com.varanegar.framework.util.report.filter.Filter;

import java.util.Date;

/**
 * Created by atp on 3/5/2017.
 */

public class DateFilter extends Filter {
    public Date value;
    public DateFilterOperator operator;
    public enum DateFilterOperator {
        Equals,
        After,
        Before
    }
}
