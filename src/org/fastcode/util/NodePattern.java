/**
 *
 */
package org.fastcode.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gautam
 *
 */
public class NodePattern {

	private NodePattern			parentNode;

	private String				node;

	private NodeAttribute		attribute;

	private List<NodeAttribute>	childAttributes	= new ArrayList<NodeAttribute>();

	/**
	 *
	 * @return
	 */
	public String getNode() {
		return this.node;
	}

	/**
	 *
	 * @param node
	 */
	public void setNode(final String node) {
		this.node = node;
	}

	/**
	 *
	 * @return
	 */
	public NodeAttribute getAttribute() {
		return this.attribute;
	}

	/**
	 *
	 * @param attribute
	 */
	public void setAttribute(final NodeAttribute attribute) {
		this.attribute = attribute;
	}

	/**
	 *
	 * @return
	 */
	public NodePattern getParentNode() {
		return this.parentNode;
	}

	/**
	 *
	 * @param parentNode
	 */
	public void setParentNode(final NodePattern parentNode) {
		this.parentNode = parentNode;
	}

	/**
	 * @param childAttributes
	 *            the childAttributes to set
	 */
	public void setChildAttributes(final List<NodeAttribute> childAttributes) {
		this.childAttributes = childAttributes;
	}

	/**
	 * @return the childAttributes
	 */
	public List<NodeAttribute> getChildAttributes() {
		return this.childAttributes;
	}

}
