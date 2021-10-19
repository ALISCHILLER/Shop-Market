package com.varanegar.framework.database.querybuilder.projection;


import com.varanegar.framework.database.querybuilder.Utils;

import java.util.ArrayList;
import java.util.List;

public class ConstantProjection extends Projection {
	public Object getConstant() {
		return constant;
	}

	private Object constant;
	
	public ConstantProjection(Object constant) {
		this.constant = constant;
	}

	@Override
	public String build() {
		if(constant != null)
			return "?";
		else
			return "NULL";
	}

	@Override
	public List<Object> buildParameters() {
		if(constant != null) {
			List<Object> ret = new ArrayList<Object>();
			ret.add(constant);
			
			return ret;
		} else {
			return Utils.EMPTY_LIST;
		}
	}
}
