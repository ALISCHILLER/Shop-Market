package com.varanegar.framework.database.querybuilder.projection;


import com.varanegar.framework.database.querybuilder.Utils;

import java.util.List;

public class CastIntProjection extends Projection {
	public Projection getProjection() {
		return projection;
	}

	private Projection projection;

	public CastIntProjection(Projection projection) {
		this.projection = projection;
	}

	@Override
	public String build() {
		String ret = (projection != null ? projection.build() : "");
		return "CAST(" + ret + " AS INTEGER)";
	}

	@Override
	public List<Object> buildParameters() {
		if (projection != null)
			return projection.buildParameters();
		else
			return Utils.EMPTY_LIST;
	}
}
