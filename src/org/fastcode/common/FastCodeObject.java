package org.fastcode.common;

public class FastCodeObject {

	private Object	object;
	private String	type;

	public FastCodeObject(final Object object, final String type) {
		super();
		this.object = object;
		this.type = type;
	}

	public Object getObject() {
		return this.object;
	}

	public void setObject(final Object object) {
		this.object = object;
	}

	public String getType() {
		return this.type;
	}

	public void setType(final String type) {
		this.type = type;
	}

}
