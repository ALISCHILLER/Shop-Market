package com.varanegar.framework.database.querybuilder.projection;

import com.varanegar.framework.database.querybuilder.criteria.Criteria;

/**
 * Created by A.Torabi on 9/16/2017.
 */

public class When {
    Criteria criteria;
    Object value;
    ColumnProjection column;

    public static When When(Criteria criteria){
        When when = new When();
        when.criteria = criteria;
        return when;
    }
    public When then(Object value){
        this.value = value;
        return this;
    }
    public When then(ColumnProjection column){
        this.column = column;
        return this;
    }
}
