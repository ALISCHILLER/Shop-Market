package com.varanegar.vaslibrary.util.multispinnerfilter;

import java.util.UUID;

public class KeyPairBoolData {
	private UUID id;
	private String name;
	private boolean isSelected;
	private Object object;

	public KeyPairBoolData() {
	}

	public KeyPairBoolData(String name, boolean isSelected) {
		this.name = name;
		this.isSelected = isSelected;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	/**
	 * @return the id
	 */
	public UUID getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(UUID id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the isSelected
	 */
	public boolean isSelected() {
		return isSelected;
	}

	/**
	 * @param isSelected the isSelected to set
	 */
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
}
