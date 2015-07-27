/**
 *
 */
package org.fastcode.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;

/**
 * @author Gautam
 *
 */
public class CreateSimilarClassPart {

	protected RadioGroupFieldEditor		classTypeRadio;
	protected RadioGroupFieldEditor		relationTypeRadio;
	protected StringFieldEditor			classBodyPattern;

	protected BooleanFieldEditor		createDefaultConstructor;
	protected BooleanFieldEditor		createInstanceConstructor;

	protected BooleanFieldEditor		copyMethodCheckBox;
	protected StringFieldEditor			subPkg;

	protected BooleanFieldEditor		createMethodBody;

	protected BooleanFieldEditor		copyFieldsCheckBox;
	protected ListEditor				methodAnnotations;
	protected ListEditor				fieldAnnotations;
	protected ListEditor				classAnnotations;
	protected BooleanFieldEditor		assignReturnCheckBox;
	protected StringFieldEditor			returnVariableName;

	protected BooleanFieldEditor		breakDateFieldsCheckBox;
	protected BooleanFieldEditor		inclInstanceCheckBox;
	protected BooleanFieldEditor		inclGetterSetterCheckBox;
	protected BooleanFieldEditor		createRelatedCheckBox;
	protected StringButtonFieldEditor	superClass;
	protected ListEditor				implementClasses;
	protected StringFieldEditor			includePattern;
	protected StringFieldEditor			excludePattern;

	/**
	 * @return the classTypeRadio
	 */
	public RadioGroupFieldEditor getClassTypeRadio() {
		return this.classTypeRadio;
	}

	/**
	 * @param classTypeRadio
	 *            the classTypeRadio to set
	 */
	public void setClassTypeRadio(final RadioGroupFieldEditor classTypeRadio) {
		this.classTypeRadio = classTypeRadio;
	}

	/**
	 * @return the relationTypeRadio
	 */
	public RadioGroupFieldEditor getRelationTypeRadio() {
		return this.relationTypeRadio;
	}

	/**
	 * @param relationTypeRadio
	 *            the relationTypeRadio to set
	 */
	public void setRelationTypeRadio(final RadioGroupFieldEditor relationTypeRadio) {
		this.relationTypeRadio = relationTypeRadio;
	}

	/**
	 * @return the classBodyPattern
	 */
	public StringFieldEditor getClassBodyPattern() {
		return this.classBodyPattern;
	}

	/**
	 * @param classBodyPattern
	 *            the classBodyPattern to set
	 */
	public void setClassBodyPattern(final StringFieldEditor classBodyPattern) {
		this.classBodyPattern = classBodyPattern;
	}

	/**
	 * @return the createDefaultConstructor
	 */
	public BooleanFieldEditor getCreateDefaultConstructor() {
		return this.createDefaultConstructor;
	}

	/**
	 * @param createDefaultConstructor
	 *            the createDefaultConstructor to set
	 */
	public void setCreateDefaultConstructor(final BooleanFieldEditor createDefaultConstructor) {
		this.createDefaultConstructor = createDefaultConstructor;
	}

	/**
	 * @return the createInstanceConstructor
	 */
	public BooleanFieldEditor getCreateInstanceConstructor() {
		return this.createInstanceConstructor;
	}

	/**
	 * @param createInstanceConstructor
	 *            the createInstanceConstructor to set
	 */
	public void setCreateInstanceConstructor(final BooleanFieldEditor createInstanceConstructor) {
		this.createInstanceConstructor = createInstanceConstructor;
	}

	/**
	 * @return the copyMethodCheckBox
	 */
	public BooleanFieldEditor getCopyMethodCheckBox() {
		return this.copyMethodCheckBox;
	}

	/**
	 * @param copyMethodCheckBox
	 *            the copyMethodCheckBox to set
	 */
	public void setCopyMethodCheckBox(final BooleanFieldEditor copyMethodCheckBox) {
		this.copyMethodCheckBox = copyMethodCheckBox;
	}

	/**
	 * @return the subPkg
	 */
	public StringFieldEditor getSubPkg() {
		return this.subPkg;
	}

	/**
	 * @param subPkg
	 *            the subPkg to set
	 */
	public void setSubPkg(final StringFieldEditor subPkg) {
		this.subPkg = subPkg;
	}

	/**
	 * @return the createMethodBody
	 */
	public BooleanFieldEditor getCreateMethodBody() {
		return this.createMethodBody;
	}

	/**
	 * @param createMethodBody
	 *            the createMethodBody to set
	 */
	public void setCreateMethodBody(final BooleanFieldEditor createMethodBody) {
		this.createMethodBody = createMethodBody;
	}

	/**
	 * @return the copyFieldsCheckBox
	 */
	public BooleanFieldEditor getCopyFieldsCheckBox() {
		return this.copyFieldsCheckBox;
	}

	/**
	 * @param copyFieldsCheckBox
	 *            the copyFieldsCheckBox to set
	 */
	public void setCopyFieldsCheckBox(final BooleanFieldEditor copyFieldsCheckBox) {
		this.copyFieldsCheckBox = copyFieldsCheckBox;
	}

	/**
	 * @return the methodAnnotations
	 */
	public ListEditor getMethodAnnotations() {
		return this.methodAnnotations;
	}

	/**
	 * @param methodAnnotations
	 *            the methodAnnotations to set
	 */
	public void setMethodAnnotations(final ListEditor methodAnnotations) {
		this.methodAnnotations = methodAnnotations;
	}

	/**
	 * @return the fieldAnnotations
	 */
	public ListEditor getFieldAnnotations() {
		return this.fieldAnnotations;
	}

	/**
	 * @param fieldAnnotations
	 *            the fieldAnnotations to set
	 */
	public void setFieldAnnotations(final ListEditor fieldAnnotations) {
		this.fieldAnnotations = fieldAnnotations;
	}

	/**
	 * @return the classAnnotations
	 */
	public ListEditor getClassAnnotations() {
		return this.classAnnotations;
	}

	/**
	 * @param classAnnotations
	 *            the classAnnotations to set
	 */
	public void setClassAnnotations(final ListEditor classAnnotations) {
		this.classAnnotations = classAnnotations;
	}

	/**
	 * @return the assignReturnCheckBox
	 */
	public BooleanFieldEditor getAssignReturnCheckBox() {
		return this.assignReturnCheckBox;
	}

	/**
	 * @param assignReturnCheckBox
	 *            the assignReturnCheckBox to set
	 */
	public void setAssignReturnCheckBox(final BooleanFieldEditor assignReturnCheckBox) {
		this.assignReturnCheckBox = assignReturnCheckBox;
	}

	/**
	 * @return the returnVariableName
	 */
	public StringFieldEditor getReturnVariableName() {
		return this.returnVariableName;
	}

	/**
	 * @param returnVariableName
	 *            the returnVariableName to set
	 */
	public void setReturnVariableName(final StringFieldEditor returnVariableName) {
		this.returnVariableName = returnVariableName;
	}

	/**
	 * @return the breakDateFieldsCheckBox
	 */
	public BooleanFieldEditor getBreakDateFieldsCheckBox() {
		return this.breakDateFieldsCheckBox;
	}

	/**
	 * @param breakDateFieldsCheckBox
	 *            the breakDateFieldsCheckBox to set
	 */
	public void setBreakDateFieldsCheckBox(final BooleanFieldEditor breakDateFieldsCheckBox) {
		this.breakDateFieldsCheckBox = breakDateFieldsCheckBox;
	}

	/**
	 * @return the inclInstanceCheckBox
	 */
	public BooleanFieldEditor getInclInstanceCheckBox() {
		return this.inclInstanceCheckBox;
	}

	/**
	 * @param inclInstanceCheckBox
	 *            the inclInstanceCheckBox to set
	 */
	public void setInclInstanceCheckBox(final BooleanFieldEditor inclInstanceCheckBox) {
		this.inclInstanceCheckBox = inclInstanceCheckBox;
	}

	/**
	 * @return the inclGetterSetterCheckBox
	 */
	public BooleanFieldEditor getInclGetterSetterCheckBox() {
		return this.inclGetterSetterCheckBox;
	}

	/**
	 * @param inclGetterSetterCheckBox
	 *            the inclGetterSetterCheckBox to set
	 */
	public void setInclGetterSetterCheckBox(final BooleanFieldEditor inclGetterSetterCheckBox) {
		this.inclGetterSetterCheckBox = inclGetterSetterCheckBox;
	}

}
