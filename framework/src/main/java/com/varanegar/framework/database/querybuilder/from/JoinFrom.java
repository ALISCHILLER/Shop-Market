package com.varanegar.framework.database.querybuilder.from;


import com.varanegar.framework.database.model.ModelProjection;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.projection.Projection;

import java.util.ArrayList;
import java.util.List;

public class JoinFrom extends From {
	public From getLeft() {
		return left;
	}

	public From getRight() {
		return right;
	}

	public String getJoinType() {
		return joinType;
	}

	public Criteria getCriteria() {
		return criteria;
	}

	private From left;
	private From right;
	private String joinType;
	private Criteria criteria;

	public JoinFrom(From left, From right, String joinType, Criteria criteria) {
		this.left = left;
		this.right = right;
		this.joinType = joinType;
		this.criteria = criteria;
	}
	
	public JoinFrom onOr(ModelProjection leftColumn, ModelProjection rightColumn) {
		return onOr(Criteria.equals(Projection.column(leftColumn), Projection.column(rightColumn)));
	}
	
	public JoinFrom onAnd(ModelProjection leftColumn, ModelProjection rightColumn) {
		return onAnd(Criteria.equals(Projection.column(leftColumn), Projection.column(rightColumn)));
	}
	
	public JoinFrom onAnd(Criteria criteria) {
		this.criteria = (this.criteria != null ? this.criteria.and(criteria) : criteria);		
		return this;
	}
	
	public JoinFrom onOr(Criteria criteria) {
		this.criteria = (this.criteria != null ? this.criteria.or(criteria) : criteria);		
		return this;
	}

	@Override
	public String build() {
		String leftSide = (left != null ? left.build() : "");
		String rightSide = (right != null ? right.build() : "");
		String joinCriteria = (criteria != null ? criteria.build() : "");
		
		return "(" + leftSide + " " + joinType + " " + rightSide + " ON " + joinCriteria + ")";
	}

	@Override
	public List<Object> buildParameters() {
		List<Object> ret = new ArrayList<Object>();
		
		if(left != null)
			ret.addAll(left.buildParameters());
		
		if(right != null)
			ret.addAll(right.buildParameters());
		
		if(criteria != null)
			ret.addAll(criteria.buildParameters());
		
		return ret;
	}
}
