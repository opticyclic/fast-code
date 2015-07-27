package org.fastcode.templates.util;

import java.util.List;

public class TagAttributeList {
	String tagName;
	List<FcTagAttributes> attributesList;

	public TagAttributeList(final String tagName, final List<FcTagAttributes> attributesList) {
		this.attributesList = attributesList;
		this.tagName = tagName;
	}

	public String getTagName() {
		return this.tagName;
	}

	public List<FcTagAttributes> getAttributesList() {
		return this.attributesList;
	}

}
