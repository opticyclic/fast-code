package org.fastcode.util;

import java.util.ArrayList;
import java.util.List;

import org.fastcode.common.FastCodeAdditionalParams;

public class TemplateDetailsForVelocity {
	String							templateName;
	String							key;
	String							builtInVariables;
	String							templatePrefix;
	String							ftPlaceholdeName;
	List<FastCodeAdditionalParams>	additionalPram	= new ArrayList<FastCodeAdditionalParams>();
	String							firstTemplateItem;
	String							secondtemplateItem;
	boolean							doValidation	= true;

	public String getTemplateName() {
		return this.templateName;
	}

	public void setTemplateName(final String templateName) {
		this.templateName = templateName;
	}

	public String getKey() {
		return this.key;
	}

	public void setKey(final String key) {
		this.key = key;
	}

	public String getBuiltInVariables() {
		return this.builtInVariables;
	}

	public void setBuiltInVariables(final String builtInVariables) {
		this.builtInVariables = builtInVariables;
	}

	public String getTemplatePrefix() {
		return this.templatePrefix;
	}

	public void setTemplatePrefix(final String templatePrefix) {
		this.templatePrefix = templatePrefix;
	}

	public String getFtPlaceholdeName() {
		return this.ftPlaceholdeName;
	}

	public void setFtPlaceholdeName(final String ftPlaceholdeName) {
		this.ftPlaceholdeName = ftPlaceholdeName;
	}

	public List<FastCodeAdditionalParams> getAdditionalPram() {
		return this.additionalPram;
	}

	public void setAdditionalPram(final List<FastCodeAdditionalParams> additionalPram) {
		this.additionalPram = additionalPram;
	}

	public String getFirstTemplateItem() {
		return this.firstTemplateItem;
	}

	public void setFirstTemplateItem(final String firstTemplateItem) {
		this.firstTemplateItem = firstTemplateItem;
	}

	public String getSecondtemplateItem() {
		return this.secondtemplateItem;
	}

	public void setSecondtemplateItem(final String secondtemplateItem) {
		this.secondtemplateItem = secondtemplateItem;
	}

	public boolean isDoValidation() {
		return this.doValidation;
	}

	public void setDoValidation(final boolean doValidation) {
		this.doValidation = doValidation;
	}
}
