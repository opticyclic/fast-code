package org.fastcode.common;

import java.util.ArrayList;
import java.util.List;

public class FastCodeAnnotation {

	public FastCodeType	type;
	private List<Pair>	parameterList	= new ArrayList<Pair>();

	public FastCodeAnnotation(final List<Pair> parameterList, final FastCodeType type) {
		super();
		this.parameterList = parameterList;
		this.type = type;
	}

	public FastCodeType getType() {
		return this.type;
	}

	public void setType(final FastCodeType type) {
		this.type = type;
	}

	public List<Pair> getParameterList() {
		return this.parameterList;
	}

	public void setParameterList(final List<Pair> parameterList) {
		this.parameterList = parameterList;
	}

	//List<Pair> parameterList = new ArrayList<Pair>();

	/*List -- Pair annotNameValue;
	FastCodeType --

	public FastCodeAnnotation(final Pair pair) {
		super();
		this.pair = pair;
	}

	public Pair getPair() {
		return this.pair;
	}
	*/

}
