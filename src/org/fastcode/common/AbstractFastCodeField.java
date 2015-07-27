/**
 *
 */
package org.fastcode.common;

/**
 * @author Gautam
 *
 */
public abstract class AbstractFastCodeField {

	protected String	name;
	protected String	value;

	public AbstractFastCodeField() {
	}

	/**
	 *
	 * @param name
	 * @param type
	 * @param value
	 */
	public AbstractFastCodeField(final String name, final String value) {
		super();
		this.name = name;
		this.value = value;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return this.value;
	}

	/*	@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (this.name == null ? 0 : this.name.hashCode());
			//result = prime * result + (this.type == null ? 0 : this.type.hashCode());
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final AbstractFastCodeField other = (AbstractFastCodeField) obj;
			if (this.name == null) {
				if (other.name != null) {
					return false;
				}
			} else if (!this.name.equals(other.name)) {
				return false;
			}
			if (this.type == null) {
				if (other.type != null) {
					return false;
				}
			} else if (!this.type.equals(other.type)) {
				return false;
			}
			return true;
		}*/

}
