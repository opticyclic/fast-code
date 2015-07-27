package org.fastcode.templates.util;


public class FcTagAttributes extends FastCodeLocalVariables {

	int varCol;

	public FcTagAttributes(final String varName, final int varLineNo, final int varCol) {
		super(varName, varLineNo);
		this.varCol = varCol;
	}

	public int getVarCol() {
		return this.varCol;
	}
}
