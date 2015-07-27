package org.fastcode.util;

public class SQLFunctions {

	String	name;
	int		argCount;
	String	returnType;
	String	insertAt;

	public SQLFunctions(final String name, final int argCount, final String returnType, final String insertAt) {

		this.name = name;
		this.argCount = argCount;
		this.returnType = returnType;
		this.insertAt = insertAt;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public int getCount() {
		return this.argCount;
	}

	public void setCount(final int argCount) {
		this.argCount = argCount;
	}

	public String getReturnType() {
		return this.returnType;
	}

	public void setReturnType(final String returnType) {
		this.returnType = returnType;
	}

	public String getInsertAt() {
		return this.insertAt;
	}

	public void setInsertAt(final String insertAt) {
		this.insertAt = insertAt;
	}

}
