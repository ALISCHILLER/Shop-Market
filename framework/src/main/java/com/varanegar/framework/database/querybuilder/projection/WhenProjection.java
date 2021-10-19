package com.varanegar.framework.database.querybuilder.projection;


import com.varanegar.framework.database.querybuilder.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WhenProjection extends Projection {

    List<When> whens = new ArrayList<>();

    public WhenProjection(When... when) {
        Collections.addAll(whens, when);
    }

    public WhenProjection(List<When> whens) {
        this.whens = whens;
    }

    @Override
    public String build() {

        String c = "";
        for (When when :
                this.whens) {
            if (when.value != null)
                c += " when " + when.criteria.build() + " then " + when.value + " ";
            else
                c += " when " + when.criteria.build() + " then " + when.column.getColumn().getName() + " ";
            List<Object> parameters = when.criteria.buildParameters();
            for (Object parameter :
                    parameters) {
                c = c.replaceFirst("\\?", parameter.toString());
            }
        }
        return " case " + c + " end ";
    }

    @Override
    public List<Object> buildParameters() {
        return Utils.EMPTY_LIST;
    }
}
