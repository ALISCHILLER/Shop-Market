package com.varanegar.framework.database.querybuilder.from;


import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.Utils;

import java.util.List;

public class SubQueryFrom extends AliasableFrom<SubQueryFrom> {
	public Query getSubQuery() {
		return subQuery;
	}

	private Query subQuery;

	public SubQueryFrom(Query subQuery) {
		this.subQuery = subQuery;
	}
	
	@Override
	public String build() {
		String ret = (subQuery != null ? "(" + subQuery.build() + ")" : "");
		
		if(!Utils.isNullOrWhiteSpace(alias))
			ret = ret + " AS " + alias;
		
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
