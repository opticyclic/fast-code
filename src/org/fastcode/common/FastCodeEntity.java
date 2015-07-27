package org.fastcode.common;

import static org.fastcode.common.FastCodeConstants.DOT;

public class FastCodeEntity {

	public boolean isEmpty;
	public String name;

	public boolean isEmpty() {
		return isEmpty;
	}

	public void setEmpty(boolean isEmpty) {
		this.isEmpty = isEmpty;
	}

	/**
	 * 
	 * getter method for name
	 * 
	 * @return
	 * 
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * 
	 * setter method for name
	 * 
	 * @param name
	 * 
	 */
	public void setName(final String name) {
		this.name = name;
	}
}
