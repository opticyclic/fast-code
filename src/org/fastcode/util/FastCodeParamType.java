package org.fastcode.util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Gautam
 *
 */
public class FastCodeParamType {
	final private String					type;
	final private List<FastCodeParamType>	params	= new ArrayList<FastCodeParamType>();

	/**
	 * @param type
	 *
	 */
	public FastCodeParamType(final String type) {
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * @return the params
	 */
	public List<FastCodeParamType> getParams() {
		return this.params;
	}

	/**
	 *
	 * @param param
	 */
	public void addParam(final FastCodeParamType fastCodeType) {
		this.params.add(fastCodeType);
	}

}
