package org.fastcode.templates.util;

public class ValidVariables extends FastCodeLocalVariables {

	int varCol;

	public ValidVariables(final String varName, final int varLineNo, final int varCol) {
		super(varName, varLineNo);
		this.varCol = varCol;
	}

	public int getVarCol() {
		return this.varCol;
	}
}
