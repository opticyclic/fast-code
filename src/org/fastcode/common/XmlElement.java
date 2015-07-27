/**
 *
 */
package org.fastcode.common;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gautam
 *
 */
public class XmlElement {

	final private String						tagName;
	final private List<Pair<String, String>>	attributes	= new ArrayList<Pair<String, String>>();

	/**
	 * @param tagName
	 */
	public XmlElement(final String tagName) {
		this.tagName = tagName;
	}

	/**
	 *
	 * @return
	 */
	public String getTagName() {
		return this.tagName;
	}

	/**
	 * @return the attributes
	 */
	public List<Pair<String, String>> getAttributes() {
		return this.attributes;
	}

	/**
	 *
	 * @param name
	 * @param value
	 */
	public void addAttribute(final String name, final String value) {
		this.attributes.add(new Pair<String, String>(name, value));
	}
}
