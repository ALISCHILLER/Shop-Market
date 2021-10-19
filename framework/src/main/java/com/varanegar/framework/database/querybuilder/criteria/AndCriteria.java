package com.varanegar.framework.database.querybuilder.criteria;


import java.util.ArrayList;
import java.util.List;

public class AndCriteria extends Criteria implements Cloneable {
    @Override
    public Criteria clone() throws CloneNotSupportedException {
        AndCriteria criteria = new AndCriteria(this.left.clone(), this.right.clone());
        return criteria;
    }

    private Criteria left;
    private Criteria right;

    public Criteria getLeft() {
        return left;
    }

    public Criteria getRight() {
        return right;
    }

    public AndCriteria(Criteria left, Criteria right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String build() {
        String ret = " AND ";

        if (left != null)
            ret = left.build() + ret;

        if (right != null)
            ret = ret + right.build();

        return "(" + ret.trim() + ")";
    }

    @Override
    public List<Object> buildParameters() {
        List<Object> ret = new ArrayList<Object>();

        if (left != null)
            ret.addAll(left.buildParameters());

        if (right != null)
            ret.addAll(right.buildParameters());

        return ret;
    }
}