/**
 *
 */
package org.fastcode.common;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IType;

/**
 * @author Gautam
 *
 */
public class ClassDescriptor {

	private IType			type;
	private List<String>	interfaces	= new ArrayList<String>();
	private List<String>	annotations	= new ArrayList<String>();

	/**
	 * @return the interfaces
	 */
	public List<String> getInterfaces() {
		return this.interfaces;
	}

	/**
	 * @param interfaces the interfaces to set
	 */
	public void setInterfaces(final List<String> interfaces) {
		this.interfaces = interfaces;
	}

	/**
	 * @return the annotations
	 */
	public List<String> getAnnotations() {
		return this.annotations;
	}

	/**
	 * @param annotations the annotations to set
	 */
	public void setAnnotations(final List<String> annotations) {
		this.annotations = annotations;
	}

}
