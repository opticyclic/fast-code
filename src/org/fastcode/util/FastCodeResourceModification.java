package org.fastcode.util;

/**
 *
 * @author Gautam
 *
 */
public class FastCodeResourceModification {

	final private ModificationType	modificationType;
	final private Object			element;

	/**
	 *
	 * @param modificationType
	 * @param element
	 */
	public FastCodeResourceModification(final ModificationType modificationType, final Object element) {
		this.modificationType = modificationType;
		this.element = element;
	}

	/**
	 * @return the modificationType
	 */
	public ModificationType getModificationType() {
		return this.modificationType;
	}

	/**
	 * @return the element
	 */
	public Object getElement() {
		return this.element;
	}

	public static enum ModificationType {
		Add, Modify, Delete
	}
}
