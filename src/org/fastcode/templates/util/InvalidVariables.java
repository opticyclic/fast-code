package org.fastcode.templates.util;

public class InvalidVariables extends FastCodeLocalVariables {

	int varCol;

	public InvalidVariables(final String varName, final int varLineNo, final int varCol) {
		super(varName, varLineNo);
		this.varCol = varCol;
	}

	public int getVarCol() {
		return this.varCol;
	}


}
