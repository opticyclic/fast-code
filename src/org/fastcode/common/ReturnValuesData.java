/**
 *
 */
package org.fastcode.common;

import static org.fastcode.common.FastCodeConstants.EMPTY_STR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.ui.IEditorPart;
import org.fastcode.util.UnitTestReturnFormatOption;
import org.eclipse.jdt.core.ICompilationUnit;

/**
 * @author ayngaran
 *
 */
public class ReturnValuesData {

	private String[]							returnFormatSelcted;
	private final Map<String, Object>			returnValuesMap				= new HashMap<String, Object>();
	private List<UnitTestReturnFormatOption>	unitTestReturnFormatOption	= new ArrayList<UnitTestReturnFormatOption>();
	private String[]							valueTypes;
	private String[]							labelText;
	private String								shellTitle					= EMPTY_STR;
	private String								defaultValue				= EMPTY_STR;
	private FastCodeAdditionalParams[]			fastCodeAdditionalParams;
	private IJavaProject						javaProject;
	private List<FastCodeReturn>				localVars					= new ArrayList<FastCodeReturn>();
	private IEditorPart							editorPart;
	private ICompilationUnit					compUnit;
	private boolean								unitTest;

	public String[] getReturnFormatSelcted() {
		return this.returnFormatSelcted;
	}

	public void setReturnFormatSelcted(final String[] returnFormatSelcted) {
		this.returnFormatSelcted = returnFormatSelcted;
	}

	public Map<String, Object> getReturnValuesMap() {
		return this.returnValuesMap;
	}

	public void addReturnValuesMap(final String string, final Object object) {
		this.returnValuesMap.put(string, object);
	}

	public List<UnitTestReturnFormatOption> getUnitTestReturnFormatOption() {
		return this.unitTestReturnFormatOption;
	}

	public void setUnitTestReturnFormatOption(final List<UnitTestReturnFormatOption> unitTestReturnFormatOption) {
		this.unitTestReturnFormatOption = unitTestReturnFormatOption;
	}

	public String[] getValueTypes() {
		return this.valueTypes;
	}

	public void setValueTypes(final String[] valueTypes) {
		this.valueTypes = valueTypes;
	}

	public String[] getLabelText() {
		return this.labelText;
	}

	public void setLabelText(final String[] labelText) {
		this.labelText = labelText;
	}

	public String getShellTitle() {
		return this.shellTitle;
	}

	public void setShellTitle(final String shellTitle) {
		this.shellTitle = shellTitle;
	}

	public String getDefaultValue() {
		return this.defaultValue;
	}

	public void setDefaultValue(final String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public FastCodeAdditionalParams[] getFastCodeAdditionalParams() {
		return this.fastCodeAdditionalParams;
	}

	public void setFastCodeAdditionalParams(final FastCodeAdditionalParams[] fastCodeAdditionalParams) {
		this.fastCodeAdditionalParams = fastCodeAdditionalParams;
	}

	public IJavaProject getJavaProject() {
		return this.javaProject;
	}

	public void setJavaProject(final IJavaProject javaProject) {
		this.javaProject = javaProject;
	}

	public void setLocalVars(final List<FastCodeReturn> localVars) {
		this.localVars = localVars;
	}

	public List<FastCodeReturn> getLocalVars() {
		return this.localVars;
	}

	public IEditorPart getEditorPart() {
		return this.editorPart;
	}

	public void setEditorPart(final IEditorPart editorPart) {
		this.editorPart = editorPart;
	}

	/**
	 *
	 * getter method for compUnit
	 * @return
	 *
	 */
	public ICompilationUnit getCompUnit() {
		return this.compUnit;
	}

	/**
	 *
	 * setter method for compUnit
	 * @param compUnit
	 *
	 */
	public void setCompUnit(final ICompilationUnit compUnit) {
		this.compUnit = compUnit;
	}

	/**
	 *
	 * getter method for unitTest
	 * @return
	 *
	 */
	public boolean isUnitTest() {
		return this.unitTest;
	}

	/**
	 *
	 * setter method for unitTest
	 * @param unitTest
	 *
	 */
	public void setUnitTest(final boolean unitTest) {
		this.unitTest = unitTest;
	}

}
