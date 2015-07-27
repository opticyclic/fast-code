/**
 *
 */
package org.fastcode.util;

import org.eclipse.core.resources.IResource;

/**
 * @author Gautam
 *
 */
public class FastCodeResource {

	private IResource	resource;
	private boolean		modified;
	private boolean		newResource;

	/**
	 * @param resource
	 * @param newResource
	 */
	public FastCodeResource(final IResource resource, final boolean newResource) {
		this.resource = resource;
		this.newResource = newResource;
	}

	/**
	 * @param resource
	 */
	public FastCodeResource(final IResource resource) {
		this(resource, false);
	}

	/**
	 *
	 * @return
	 */
	public IResource getResource() {
		return this.resource;
	}

	/**
	 * @param modified
	 *            the modified to set
	 */
	public void setModified(final boolean modified) {
		this.modified = modified;
	}

	/**
	 * @return the modified
	 */
	public boolean isModified() {
		return this.modified;
	}

	/**
	 * @return the newResource
	 */
	public boolean isNewResource() {
		return this.newResource;
	}

}
