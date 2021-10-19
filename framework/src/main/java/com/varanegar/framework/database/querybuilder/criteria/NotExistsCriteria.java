package com.varanegar.framework.database.querybuilder.criteria;


import com.varanegar.framework.database.querybuilder.Query;

public class NotExistsCriteria extends ExistsCriteria {
	public NotExistsCriteria(Query subQuery) {
		super(subQuery);
	}
	
	@Override
	public String build() {
		return "NOT " + super.build();
	}
}
