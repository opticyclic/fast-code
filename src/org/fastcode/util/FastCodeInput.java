/**
 * @author : Gautam

 * Created : 11/25/2010

 */

package org.fastcode.util;

public class FastCodeInput {

	private final String	value;

	/**
	 * @param value
	 */
	public FastCodeInput(final String value) {
		this.value = value;
	}

	/**
	 *
	 * @return
	 */
	public String pluralize() {
		return StringUtil.changeToPlural(this.value);
	}

	/**
	 *
	 * getter method for value
	 *
	 * @return
	 *
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * @param arg0
	 * @return
	 * @see java.lang.String#endsWith(java.lang.String)
	 */
	public boolean endsWith(final String arg0) {
		return this.value.endsWith(arg0);
	}

	/**
	 * @param arg0
	 * @return
	 * @see java.lang.String#indexOf(java.lang.String)
	 */
	public int indexOf(final String arg0) {
		return this.value.indexOf(arg0);
	}

	/**
	 * @param arg0
	 * @return
	 * @see java.lang.String#lastIndexOf(java.lang.String)
	 */
	public int lastIndexOf(final String arg0) {
		return this.value.lastIndexOf(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @see java.lang.String#replace(java.lang.CharSequence,
	 *      java.lang.CharSequence)
	 */
	public String replace(final CharSequence arg0, final CharSequence arg1) {
		return this.value.replace(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @see java.lang.String#replaceAll(java.lang.String, java.lang.String)
	 */
	public String replaceAll(final String arg0, final String arg1) {
		return this.value.replaceAll(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @see java.lang.String#replaceFirst(java.lang.String, java.lang.String)
	 */
	public String replaceFirst(final String arg0, final String arg1) {
		return this.value.replaceFirst(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @see java.lang.String#split(java.lang.String, int)
	 */
	public String[] split(final String arg0, final int arg1) {
		return this.value.split(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @return
	 * @see java.lang.String#split(java.lang.String)
	 */
	public String[] split(final String arg0) {
		return this.value.split(arg0);
	}

	/**
	 * @param arg0
	 * @return
	 * @see java.lang.String#startsWith(java.lang.String)
	 */
	public boolean startsWith(final String arg0) {
		return this.value.startsWith(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 * @see java.lang.String#substring(int, int)
	 */
	public String substring(final int arg0, final int arg1) {
		return this.value.substring(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @return
	 * @see java.lang.String#substring(int)
	 */
	public String substring(final int arg0) {
		return this.value.substring(arg0);
	}

	/**
	 * @return
	 * @see java.lang.String#toLowerCase()
	 */
	public String toLowerCase() {
		return this.value.toLowerCase();
	}

	/**
	 * @return
	 * @see java.lang.String#toUpperCase()
	 */
	public String toUpperCase() {
		return this.value.toUpperCase();
	}

	/**
	 * @return
	 * @see java.lang.String#trim()
	 */
	public String trim() {
		return this.value.trim();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.value == null ? 0 : this.value.hashCode();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof FastCodeInput)) {
			return false;
		}
		return this.value != null && this.value.equals(((FastCodeInput) obj).value);
	}

	/**
	 * @return
	 * @see java.lang.String#length()
	 */
	public int length() {
		return this.value == null ? 0 : this.value.length();
	}

	/**
	 * @param regex
	 * @return
	 * @see java.lang.String#matches(java.lang.String)
	 */
	public boolean matches(final String regex) {
		return this.value.matches(regex);
	}

	/**
	 * @return
	 * @see java.lang.String#toString()
	 */
	@Override
	public String toString() {
		return this.value.toString();
	}

}