package com.varanegar.framework.database.querybuilder.from;


import com.varanegar.framework.database.model.ModelProjection;
import com.varanegar.framework.database.querybuilder.Utils;

import java.util.List;

public class TableFrom extends AliasableFrom<TableFrom> {
	private ModelProjection table;

	public ModelProjection getTable() {
		return table;
	}

	public TableFrom(ModelProjection table) {
		this.table = table;
	}

	@Override
	public String build() {
		String ret = (!Utils.isNullOrWhiteSpace(table.getName()) ? table.getName() : "");

		if(!Utils.isNullOrWhiteSpace(alias))
			ret = ret + " AS " + alias;
		
		return ret;
	}

	@Override
	public List<Object> buildParameters() {
		return Utils.EMPTY_LIST;
	}
}
