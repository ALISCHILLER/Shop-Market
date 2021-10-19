package com.varanegar.framework.database.querybuilder.projection;

import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.Utils;

import java.util.List;

public class SubQueryProjection extends Projection {
	public Query getSubQuery() {
		return subQuery;
	}

	private Query subQuery;
	
	public SubQueryProjection(Query subQuery) {
		this.subQuery = subQuery;
	}

	@Override
	public String build() {
		if(subQuery != null)
			return "(" + subQuery.build() + ")";
		else
			return "";
	}

	@Override
	public List<Object> buildParameters() {
		if(subQuery != null)
			return subQuery.buildParameters();
		else
			return Utils.EMPTY_LIST;
	}
}
