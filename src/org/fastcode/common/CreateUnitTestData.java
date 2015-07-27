package org.fastcode.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.fastcode.common.FastCodeConstants.HANDLE_EXCEPTION;
import org.fastcode.common.FastCodeConstants.UNIT_TEST_CHOICE;
import org.fastcode.common.FastCodeConstants.UNIT_TEST_TYPE;
import org.fastcode.util.JunitPreferencesAndType;
import org.eclipse.jdt.core.ICompilationUnit;

public class CreateUnitTestData {
	private String						junitTestProfileName;
	private List<IMethod>				classMethodsSelected	= new ArrayList<IMethod>();
	private UNIT_TEST_TYPE				unitTestType;
	private String[]					unitTestRsltFormatSelected;
	private List<String>				unitTestProfiles		= new ArrayList<String>();
	private List<IMethod>				classMethodsList		= new ArrayList<IMethod>();
	private IType						fromType;
	private JunitPreferencesAndType		junitPreferencesAndType;
	private IType						typeToWorkOn;
	private UNIT_TEST_CHOICE			unitTestChoice;
	private Map<String, FastCodeMethod>	stubMethodsMap			= new HashMap<String, FastCodeMethod>();
	private List<FastCodeMethod>		selectedStubMethodsList	= new ArrayList<FastCodeMethod>();
	private boolean						testClssExists;
	private String						methodReturnType;
	private final List<IMethod>			classMethodsToShow		= new ArrayList<IMethod>();
	private HANDLE_EXCEPTION			handleException;
	private boolean						invokedFromMethod;
	private String						testMethodName;
	private String						addnlTestMethodName;
	private ICompilationUnit			unitTestCU;
	private String						excepClassName			= "RuntimeException";
	private IType						exceptnIType			= null;
	private IMethod[]					existingTestIMethods;
	private String[]					selectedDependsOnMethod;
	private String						selectedDataProvider;

	/**
	 * @param testMethodName
	 *            the testMethodName to set
	 */
	public void setClassMethodsSelected(final List<IMethod> classMethodsSelected) {
		this.classMethodsSelected = classMethodsSelected;
	}

	/**
	 * @return the testMethodName
	 */
	public List<IMethod> getClassMethodsSelected() {
		return this.classMethodsSelected;
	}

	/**
	 * @param junitTestType
	 *            the junitTestType to set
	 */
	public void setUnitTestType(final UNIT_TEST_TYPE unitTestType) {
		this.unitTestType = unitTestType;
	}

	/**
	 * @return the junitTestType
	 */
	public UNIT_TEST_TYPE getUnitTestType() {
		return this.unitTestType;
	}

	/**
	 * @param junitTestProfileName
	 *            the junitTestProfileName to set
	 */
	public void setJunitTestProfileName(final String junitTestProfileName) {
		this.junitTestProfileName = junitTestProfileName;
	}

	/**
	 * @return the junitTestProfileName
	 */
	public String getJunitTestProfileName() {
		return this.junitTestProfileName;
	}

	/**
	 * @param unitTestRsltFormatSelected
	 *            the unitTestRsltFormatSelected to set
	 */
	public void setUnitTestRsltFormatSelected(final String[] unitTestRsltFormatSelected) {
		this.unitTestRsltFormatSelected = unitTestRsltFormatSelected;
	}

	/**
	 * @return the unitTestRsltFormatSelected
	 */
	public String[] getUnitTestRsltFormatSelected() {
		return this.unitTestRsltFormatSelected;
	}

	/**
	 * @return the unitTestProfiles
	 */
	public List<String> getUnitTestProfiles() {
		return this.unitTestProfiles;
	}

	/**
	 * @param unitTestProfiles the unitTestProfiles to set
	 */
	public void setUnitTestProfiles(final List<String> unitTestProfiles) {
		this.unitTestProfiles = unitTestProfiles;
	}

	/**
	 *
	 * getter method for testMethodsList
	 * @return
	 *
	 */
	public List<IMethod> getClassMethodsList() {
		return this.classMethodsList;
	}

	/**
	 *
	 * setter method for testMethodsList
	 * @param testMethodsList
	 *
	 */
	public void setClassMethodsList(final List<IMethod> classMethodsList) {
		this.classMethodsList = classMethodsList;
	}

	/**
	 *
	 * getter method for fromType
	 * @return
	 *
	 */
	public IType getFromType() {
		return this.fromType;
	}

	/**
	 *
	 * setter method for fromType
	 * @param fromType
	 *
	 */
	public void setFromType(final IType fromType) {
		this.fromType = fromType;
	}

	/**
	 *
	 * getter method for junitPreferencesAndType
	 * @return
	 *
	 */
	public JunitPreferencesAndType getJunitPreferencesAndType() {
		return this.junitPreferencesAndType;
	}

	/**
	 *
	 * setter method for junitPreferencesAndType
	 * @param junitPreferencesAndType
	 *
	 */
	public void setJunitPreferencesAndType(final JunitPreferencesAndType junitPreferencesAndType) {
		this.junitPreferencesAndType = junitPreferencesAndType;
	}

	/**
	 *
	 * getter method for typeToWorkOn
	 * @return
	 *
	 */
	public IType getTypeToWorkOn() {
		return this.typeToWorkOn;
	}

	/**
	 *
	 * setter method for typeToWorkOn
	 * @param typeToWorkOn
	 *
	 */
	public void setTypeToWorkOn(final IType typeToWorkOn) {
		this.typeToWorkOn = typeToWorkOn;
	}

	/**
	 * @return the unitTestChoice
	 */
	public UNIT_TEST_CHOICE getUnitTestChoice() {
		return this.unitTestChoice;
	}

	/**
	 * @param unitTestChoice the unitTestChoice to set
	 */
	public void setUnitTestChoice(final UNIT_TEST_CHOICE unitTestChoice) {
		this.unitTestChoice = unitTestChoice;
	}

	/**
	 * @return the stubMethodsList
	 */
	public Map<String, FastCodeMethod> getStubMethodsMap() {
		return this.stubMethodsMap;
	}

	/**
	 * @param stubMethodsList the stubMethodsList to set
	 */
	public void setStubMethodsMap(final Map<String, FastCodeMethod> stubMethodsMap) {
		this.stubMethodsMap = stubMethodsMap;
	}

	/**
	 *
	 * getter method for selectedStubMethodsList
	 * @return
	 *
	 */
	public List<FastCodeMethod> getSelectedStubMethodsList() {
		return this.selectedStubMethodsList;
	}

	/**
	 *
	 * setter method for selectedStubMethodsList
	 * @param selectedStubMethodsList
	 *
	 */
	public void setSelectedStubMethodsList(final List<FastCodeMethod> selectedStubMethodsList) {
		this.selectedStubMethodsList = selectedStubMethodsList;
	}

	/**
	 * @return the testClssExists
	 */
	public boolean isTestClssExists() {
		return this.testClssExists;
	}

	/**
	 * @param testClssExists the testClssExists to set
	 */
	public void setTestClssExists(final boolean testClssExists) {
		this.testClssExists = testClssExists;
	}

	/**
	 *
	 * getter method for methodReturnType
	 * @return
	 *
	 */
	public String getMethodReturnType() {
		return this.methodReturnType;
	}

	/**
	 *
	 * setter method for methodReturnType
	 * @param methodReturnType
	 *
	 */
	public void setMethodReturnType(final String methodReturnType) {
		this.methodReturnType = methodReturnType;
	}

	/**
	 * @return the classMethodsToShow
	 */
	public List<IMethod> getClassMethodsToShow() {
		return this.classMethodsToShow;
	}

	/**
	 *
	 * getter method for handleException
	 * @return
	 *
	 */
	public HANDLE_EXCEPTION getHandleException() {
		return this.handleException;
	}

	/**
	 *
	 * setter method for handleException
	 * @param handeException
	 *
	 */
	public void setHandleException(final HANDLE_EXCEPTION handeException) {
		this.handleException = handeException;
	}

	public boolean isInvokedFromMethod() {
		return this.invokedFromMethod;
	}

	public void setInvokedFromMethod(final boolean invokedFromMethod) {
		this.invokedFromMethod = invokedFromMethod;
	}

	public String getTestMethodName() {
		return this.testMethodName;
	}

	public void setTestMethodName(final String testMethodName) {
		this.testMethodName = testMethodName;
	}

	public String getAddnlTestMethodName() {
		return this.addnlTestMethodName;
	}

	public void setAddnlTestMethodName(final String addnlTestMethodName) {
		this.addnlTestMethodName = addnlTestMethodName;
	}

	public ICompilationUnit getUnitTestCU() {
		return this.unitTestCU;
	}

	public void setUnitTestCU(final ICompilationUnit unitTestCU) {
		this.unitTestCU = unitTestCU;
	}

	public String getExcepClassName() {
		return this.excepClassName;
	}

	public void setExcepClassName(final String excepClassName) {
		this.excepClassName = excepClassName;
	}

	public IType getExceptnIType() {
		return this.exceptnIType;
	}

	public void setExceptnIType(final IType exceptnIType) {
		this.exceptnIType = exceptnIType;
	}

	public void setSelectedDataProvider(final String selectedDataProvider) {
		this.selectedDataProvider = selectedDataProvider;
	}

	public String getSelectedDataProvider() {
		return this.selectedDataProvider;
	}

	public void setSelectedDependsOnMethod(final String[] selectedDependsOnMethod) {
		this.selectedDependsOnMethod = selectedDependsOnMethod;
	}

	public String[] getSelectedDependsOnMethod() {
		return this.selectedDependsOnMethod;
	}

	public void setExistingTestIMethods(final IMethod[] existingTestIMethods) {
		this.existingTestIMethods = existingTestIMethods;
	}

	public IMethod[] getExistingTestIMethods() {
		return this.existingTestIMethods;
	}

}
