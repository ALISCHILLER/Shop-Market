package com.varanegar.framework.database.querybuilder.criteria;


import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.Utils;

import java.util.List;

public class ExistsCriteria extends Criteria {
	public Query getSubQuery() {
		return subQuery;
	}

	private Query subQuery;
	
	public ExistsCriteria(Query subQuery) {
		this.subQuery = subQuery;
	}

	@Override
	public String build() {
		String ret = "EXISTS(";
		
		if(subQuery != null)
			ret = ret + subQuery.build();
		
		ret = ret + ")";
		return ret;
	}

	@Override
	public List<Object> buildParameters() {
		if(subQuery != null)
			return subQuery.buildParameters();
		else
			return Utils.EMPTY_LIST;
	}
}
