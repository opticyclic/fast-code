/**
 * @author : Gautam

 * Created : 01/02/2011

 */

package org.fastcode.common;

public class FastCodeProperty {

	private final String	name;
	private final String	value;

	/**
	 * @param name
	 * @param value
	 */
	public FastCodeProperty(final String name, final String value) {
		this.name = name;
		this.value = value;
	}

	/**
	 *
	 * getter method for name
	 * @return
	 *
	 */
	public String getName() {
		return this.name;
	}

	/**
	 *
	 * getter method for value
	 * @return
	 *
	 */
	public String getValue() {
		return this.value;
	}

}