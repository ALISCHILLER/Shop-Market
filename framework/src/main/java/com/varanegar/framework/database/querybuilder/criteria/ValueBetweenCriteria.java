package com.varanegar.framework.database.querybuilder.criteria;


import com.varanegar.framework.database.querybuilder.projection.AliasedProjection;
import com.varanegar.framework.database.querybuilder.projection.Projection;

import java.util.ArrayList;
import java.util.List;

public class ValueBetweenCriteria extends Criteria {
	public Object getValue() {
		return value;
	}

	public Projection getProjectionStart() {
		return projectionStart;
	}

	public Projection getProjectionEnd() {
		return projectionEnd;
	}

	private Object value;
	private Projection projectionStart;
	private Projection projectionEnd;

	public ValueBetweenCriteria(Object value, Projection projectionStart, Projection projectionEnd) {
		this.value = value;
		this.projectionStart = projectionStart;
		this.projectionEnd = projectionEnd;

		if(this.projectionStart instanceof AliasedProjection)
			this.projectionStart = ((AliasedProjection)this.projectionStart).removeAlias();

		if(this.projectionEnd instanceof AliasedProjection)
			this.projectionEnd = ((AliasedProjection)this.projectionEnd).removeAlias();
	}

	@Override
	public String build() {
		StringBuilder sb = new StringBuilder();

		sb.append((" '" + value+ "' "));
		sb.append(" BETWEEN ");
		sb.append((projectionStart != null ? projectionStart.build() : "NULL"));
		sb.append(" AND ");
		sb.append((projectionEnd != null ? projectionEnd.build() : "NULL"));

		return sb.toString();
	}

	@Override
	public List<Object> buildParameters() {
		List<Object> ret = new ArrayList<Object>();

//		if(value != null)
//			ret.add(value);
//
//		if(projectionStart != null)
//			ret.addAll(projectionStart.buildParameters());
//
//		if(projectionEnd != null)
//			ret.addAll(projectionEnd.buildParameters());

		return ret;
	}
}
