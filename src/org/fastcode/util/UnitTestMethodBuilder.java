/**
 * @author : Gautam

 * Created : 09/18/2010

 */

package org.fastcode.util;

import static org.eclipse.jdt.core.Signature.getSignatureSimpleName;
import static org.fastcode.common.FastCodeConstants.ASTERISK;
import static org.fastcode.common.FastCodeConstants.COMMA;
import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.HASH;
import static org.fastcode.common.FastCodeConstants.LEFT_PAREN;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.common.FastCodeConstants.RIGHT_PAREN;
import static org.fastcode.common.FastCodeConstants.SPACE;
import static org.fastcode.common.FastCodeConstants.TAB;
import static org.fastcode.util.FastCodeUtil.getEmptyListForNull;
import static org.fastcode.util.JUnitUtil.surroundWithTryCatchBlock;
import static org.fastcode.util.MethodArgUtil.getArgValue;
import static org.fastcode.util.StringUtil.evaluateByVelocity;
import static org.fastcode.util.StringUtil.getGlobalSettings;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.StringUtil.parseType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.fastcode.common.CreateUnitTestData;
import org.fastcode.common.FastCodeAdditionalParams;
import org.fastcode.common.FastCodeConstants.JUNIT_TYPE;
import org.fastcode.common.FastCodeConstants.RETURN_TYPES;
import org.fastcode.common.FastCodeConstants.UNIT_TEST_TYPE;
import org.fastcode.common.FastCodeReturn;
import org.fastcode.common.FastCodeType;
import org.fastcode.common.Pair;
import org.fastcode.common.ReturnValuesData;
import org.fastcode.dialog.ReturnValuesDialog;
import org.fastcode.preferences.JunitPreferences;

public class UnitTestMethodBuilder extends AbstractMethodBuilder implements MethodBuilder {

	//private final FastCodeConsole	fastCodeConsole	= FastCodeConsole.getInstance();
	private boolean	excepTest;

	/**
	 * @param fastCodeContext
	 */
	public UnitTestMethodBuilder(final FastCodeContext fastCodeContext) {
		super(fastCodeContext);
	}

	/**
	 * @param method
	 * @param toType
	 */
	@Override
	public IMethod buildMethod(final IMethod method, final IType toType) throws Exception {
		return null;
	}

	/**
	 * @param method
	 * @param instance
	 * @param toType
	 * @param methodParms
	 * @param createSimilarDescriptorClass
	 *
	 */
	@Override
	protected String getMethodBody(final IMethod method, final String instance, final IType toType,
			final List<Pair<String, String>> methodParms, final CreateSimilarDescriptorClass createSimilarDescriptorClass) throws Exception {
		final JunitPreferences junitPreferences = this.fastCodeContext.getJunitPreferences();
		final StringBuilder methBody = new StringBuilder();
		final StringBuilder methInvocation = new StringBuilder();

		final String retType = method.getReturnType();
		final boolean isReturnTypeVoid = retType == null || getSignatureSimpleName(retType).equals("void");

		int cnt = 0;
		for (final String pname : method.getParameterNames()) {
			String methArgref = EMPTY_STR;
			methArgref = getArgValue(getSignatureSimpleName(method.getParameterTypes()[cnt]));
			if (methArgref == null) {
				final String methArgrefType = getSignatureSimpleName(method.getParameterTypes()[cnt]);
				methBody.append(TAB + methArgrefType + SPACE + pname + " = new " + methArgrefType + "();\n");
				methArgref = pname;
			}

			methInvocation.append(methArgref + (cnt == method.getParameterNames().length - 1 ? EMPTY_STR : COMMA));
			cnt++;
		}

		String methodInvcFrag = instance + DOT + method.getElementName() + LEFT_PAREN + methInvocation + RIGHT_PAREN + ";";

		final String nameOfResultVar = "result";
		final String nameOfRetObj = getObjectNameFromMethod(method.getElementName());
		final JUNIT_TYPE junitType = junitPreferences.getJunitType();
		if (!isReturnTypeVoid) {
			methodInvcFrag = getSignatureSimpleName(retType) + SPACE + nameOfRetObj + " = " + methodInvcFrag + NEWLINE;
			final UnitTestReturnFormatSettings unitTestReturnFormatSettings = UnitTestReturnFormatSettings.getInstance();
			final Map<String, List<UnitTestReturnFormatOption>> returnFormatMap = unitTestReturnFormatSettings.getResultFormatMap().get(
					junitType.toString());
			final ReturnValuesData returnValuesData = getReturnValues(junitPreferences, junitType, retType, unitTestReturnFormatSettings,
					returnFormatMap);
			final Map<String, Object> plcHolders = new HashMap<String, Object>();
			String assertSnippet = EMPTY_STR;
			if (junitType == JUNIT_TYPE.JUNIT_TYPE_4 || junitType == JUNIT_TYPE.JUNIT_TYPE_3 || junitType == JUNIT_TYPE.JUNIT_TYPE_TESTNG) {

				final FastCodeType fastCodeType = parseType(junitPreferences.getCreateUnitTestData().getMethodReturnType(),
						junitPreferences.getCreateUnitTestData().getFromType().getCompilationUnit());
				List<UnitTestReturnFormatOption> returnTypeOptionList = returnFormatMap.get(fastCodeType.getName());
				if (returnTypeOptionList == null) {
					returnTypeOptionList = returnFormatMap.get(unitTestReturnFormatSettings.getParentMap().get(fastCodeType.getName()));
				}
				if (junitPreferences.getCreateUnitTestData().getUnitTestRsltFormatSelected() != null
						&& junitPreferences.getCreateUnitTestData().getUnitTestRsltFormatSelected().length > 0) {
					for (final String optionSelected : junitPreferences.getCreateUnitTestData().getUnitTestRsltFormatSelected()) {
						for (final UnitTestReturnFormatOption formatOption : returnTypeOptionList
								.toArray(new UnitTestReturnFormatOption[0])) {
							if (optionSelected.equals(formatOption.getName())) {
								final FastCodeReturn fastCodeReturn = new FastCodeReturn(nameOfRetObj, new FastCodeType(method
										.getCompilationUnit().findPrimaryType()));
								if (formatOption.getRequireValue()) {
									// call returnValuesDialog box
									plcHolders.put(nameOfResultVar, fastCodeReturn);
									plcHolders.put("value", returnValuesData.getReturnValuesMap().get(formatOption.getName()));
								} else {
									plcHolders.put(nameOfResultVar, fastCodeReturn);
								}

								getGlobalSettings(plcHolders);
								assertSnippet = evaluateByVelocity(formatOption.getMethodBody(), plcHolders);
								methodInvcFrag += TAB + assertSnippet;
								break;
							}
						}
					}
				} else {
					if (junitType == JUNIT_TYPE.JUNIT_TYPE_4 || junitType == JUNIT_TYPE.JUNIT_TYPE_3) {
						methodInvcFrag += TAB + "assertNotNull(\"" + nameOfRetObj + " cannot be null\", " + nameOfRetObj + ");";
					} else if (junitType == JUNIT_TYPE.JUNIT_TYPE_TESTNG) {
						methodInvcFrag += TAB + "assertNotNull(" + nameOfRetObj + ", \"" + nameOfRetObj + " cannot be null\");";
					}
				}

			} /*else if (junitType == JUNIT_TYPE.JUNIT_TYPE_TESTNG) {//same code as if block.

				final FastCodeType fastCodeType = parseType(junitPreferences.getCreateUnitTestData().getMethodReturnType(),
						junitPreferences.getCreateUnitTestData().getFromType().getCompilationUnit());
				List<UnitTestReturnFormatOption> returnTypeOptionList = returnFormatMap.get(fastCodeType.getName());
				if (returnTypeOptionList == null) {
					returnTypeOptionList = returnFormatMap.get(unitTestReturnFormatSettings.getParentMap().get(fastCodeType.getName()));
				}
				if (junitPreferences.getCreateUnitTestData().getUnitTestRsltFormatSelected() != null
						&& junitPreferences.getCreateUnitTestData().getUnitTestRsltFormatSelected().length > 0) {
					for (final String optionSelected : junitPreferences.getCreateUnitTestData().getUnitTestRsltFormatSelected()) {
						for (final UnitTestReturnFormatOption formatOption : returnTypeOptionList
								.toArray(new UnitTestReturnFormatOption[0])) {
							if (optionSelected.equals(formatOption.getName())) {
								final FastCodeReturn fastCodeReturn = new FastCodeReturn(nameOfRetObj, new FastCodeType(method
										.getCompilationUnit().findPrimaryType()));
								if (formatOption.getRequireValue()) {
									// call returnValuesDialog box
									plcHolders.put(nameOfResultVar, fastCodeReturn);
									plcHolders.put("value", returnValuesData.getReturnValuesMap().get(formatOption.getName()));
								} else {
									plcHolders.put(nameOfResultVar, fastCodeReturn);
								}

								getGlobalSettings(plcHolders);
								assertSnippet = evaluateByVelocity(formatOption.getMethodBody(), plcHolders);
								methodInvcFrag += TAB + assertSnippet;
								break;
							}
						}
					}
				} else {
					// methodInvcFrag += TAB + "assertNotNull(" +
					// nameOfResultVar + ", \"" + nameOfResultVar +
					// " cannot be null\");";
				}
				}*/
		}

		// final boolean alwaysCreateTryCatch =
		// junitPreferences.isAlwaysCreateTryCatch();
		if (this.excepTest && junitType == JUNIT_TYPE.JUNIT_TYPE_3) {
			methBody.append(surroundWithTryCatchBlock(methodInvcFrag, junitPreferences.getNegativeBody(), method));
		}/*
			* else if (!this.excepTest && alwaysCreateTryCatch) {
			* methBody.append(surroundWithTryCatchBlock(methodInvcFrag, method,
			* false)); }
			*/else {
			methBody.append(TAB + methodInvcFrag);
		}
		return methBody.toString().trim();
	}

	/**
	 * @param junitPreferences
	 * @param junitType
	 * @param retType
	 * @param unitTestReturnFormatSettings
	 * @param returnFormatMap2
	 * @return
	 * @throws Exception
	 */
	private ReturnValuesData getReturnValues(final JunitPreferences junitPreferences, final JUNIT_TYPE junitType, final String retType,
			final UnitTestReturnFormatSettings unitTestReturnFormatSettings,
			final Map<String, List<UnitTestReturnFormatOption>> returnFormatMap2) throws Exception {
		final Map<String, List<UnitTestReturnFormatOption>> returnFormatMap = unitTestReturnFormatSettings.getResultFormatMap().get(
				junitType.toString());
		/*
		 * String returnType = getSignatureSimpleName(retType);
		 * if(returnType.contains(ANGLE_BRACKET_LEFT)) { returnType =
		 * returnType.substring(0,
		 * returnType.indexOf(ANGLE_BRACKET_LEFT)).trim(); }
		 */
		final FastCodeType fastCodeType = parseType(getSignatureSimpleName(retType), junitPreferences.getCreateUnitTestData().getFromType()
				.getCompilationUnit());
		List<UnitTestReturnFormatOption> returnTypeOptionList = returnFormatMap.get(fastCodeType.getName());
		if (returnTypeOptionList == null) {
			returnTypeOptionList = returnFormatMap.get(unitTestReturnFormatSettings.getParentMap().get(fastCodeType.getName()));
		}

		String valueType = EMPTY_STR;
		// final StringBuilder labelTextStr = new StringBuilder();
		final List<FastCodeAdditionalParams> fcAdditnlParamList = new ArrayList<FastCodeAdditionalParams>();
		boolean getReturnValues = false;
		for (final String returnFomat : junitPreferences.getCreateUnitTestData().getUnitTestRsltFormatSelected()) {
			for (final UnitTestReturnFormatOption formatOption : getEmptyListForNull(returnTypeOptionList)) {
				if (returnFomat.equals(formatOption.getName())) {
					if (formatOption.getRequireValue()) {
						if (formatOption.getValueType() == null) {
							valueType = "String";
						} else {
							valueType = formatOption.getValueType();
						}
						final FastCodeAdditionalParams additionalParams = new FastCodeAdditionalParams(formatOption.getName(),
								RETURN_TYPES.getReturnType(valueType), EMPTY_STR, "true", "true");
						fcAdditnlParamList.add(additionalParams);
						getReturnValues = true;
					}
				}
			}
		}
		final ReturnValuesData returnValuesData = new ReturnValuesData();
		if (junitPreferences.getCreateUnitTestData().getUnitTestRsltFormatSelected() != null
				&& junitPreferences.getCreateUnitTestData().getUnitTestRsltFormatSelected().length > 0) {
			// returnValuesData.setReturnFormatSelcted(junitPreferences.getResultFormatList().toArray(new
			// String[0]));
			// returnValuesData.setUnitTestReturnFormatOption(returnTypeOptionList);
			if (getReturnValues) {
				returnValuesData.setFastCodeAdditionalParams(fcAdditnlParamList.toArray(new FastCodeAdditionalParams[0]));
				// returnValuesData.setLabelText(labelTextStr.toString().split(SPACE));
				// returnValuesData.setValueTypes(valueType.toString().split(SPACE));
				returnValuesData.setShellTitle("Please enter Return Values for the following formats");
				returnValuesData.setUnitTest(true);
				final ReturnValuesDialog returnValuesDialog = new ReturnValuesDialog(new Shell(), returnValuesData);
				if (returnValuesDialog.open() == Window.CANCEL) {
					return null;
				}
				return returnValuesDialog.getReturnValuesData();
			}
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.fastcode.util.AbstractMethodBuilder#findMethodsibling(org.eclipse
	 * .jdt.core.IType, org.eclipse.jdt.core.IMethod,
	 * org.fastcode.util.CreateSimilarDescriptorClass)
	 */
	@Override
	protected IMethod findMethodsibling(final IType toType, final IMethod method,
			final CreateSimilarDescriptorClass createSimilarDescriptorClass) {
		return null;
	}

	/**
	 * @param toType
	 * @param createSimilarDescriptorClass
	 * @param placeHolders
	 *
	 * @throws Exception
	 */
	@Override
	protected String getAnnotations(final IMethod method, final IType toType, final String[] methodAnnotations,
			final Map<String, Object> placeHolders) throws Exception {
		final StringBuilder methAnnotations = new StringBuilder();

		String excep = null;
		final String timeout = null;
		String annotations = SourceUtil.createAnnotations(this.fastCodeContext, toType.getCompilationUnit(), toType.getJavaProject(),
				methodAnnotations, placeHolders);
		this.excepTest = this.fastCodeContext.getJunitPreferences().getCreateUnitTestData().getUnitTestType() == UNIT_TEST_TYPE.EXCEPTION_TEST;
		if (this.excepTest) {
			final String[] exceptionTypes = method.getExceptionTypes();
			final JUNIT_TYPE junitType = this.fastCodeContext.getJunitPreferences().getJunitType();
			final StringBuilder testAnnotation = new StringBuilder();

			methAnnotations.append(isEmpty(annotations) ? EMPTY_STR : annotations + NEWLINE);
			if (junitType == JUNIT_TYPE.JUNIT_TYPE_4 || junitType == JUNIT_TYPE.JUNIT_TYPE_TESTNG) {
				excep = exceptionTypes == null || exceptionTypes.length == 0 ? this.fastCodeContext.getJunitPreferences()
						.getCreateUnitTestData().getExcepClassName() : getSignatureSimpleName(exceptionTypes[0]);
				excep += ".class";
				if (junitType == JUNIT_TYPE.JUNIT_TYPE_4) {
					// final InputDialog inputDialog = new InputDialog(new
					// Shell(), "Timeout",
					// "Do you put a test for timeout? If so please enter a value in ms",
					// "100", null);
					// inputDialog.open();
					// if (!isEmpty(inputDialog.getValue())) {
					// timeout = inputDialog.getValue();
					// }
				}
			}
			if (excep == null && timeout == null) {
				if (junitType == JUNIT_TYPE.JUNIT_TYPE_4 || junitType == JUNIT_TYPE.JUNIT_TYPE_TESTNG) {
					// methAnnotations = "@Test\n" + methAnnotations;
					testAnnotation.append("@Test\n");
				}
			} else if (excep != null && timeout != null) {
				if (junitType == JUNIT_TYPE.JUNIT_TYPE_4) {
					testAnnotation.append("@Test(expected = " + excep + ", timeout = " + timeout + RIGHT_PAREN + NEWLINE);
				} else if (junitType == JUNIT_TYPE.JUNIT_TYPE_TESTNG) {
					testAnnotation.append("@Test(expectedExceptions = {" + excep + "}, timeOut = " + timeout + RIGHT_PAREN + NEWLINE);
				}
			} else if (excep != null) {
				if (junitType == JUNIT_TYPE.JUNIT_TYPE_4) {
					testAnnotation.append("@Test(expected = " + excep + RIGHT_PAREN + NEWLINE);
				} else if (junitType == JUNIT_TYPE.JUNIT_TYPE_TESTNG) {
					testAnnotation.append("@Test(expectedExceptions = " + excep + RIGHT_PAREN + NEWLINE);
				}
			} else {
				if (junitType == JUNIT_TYPE.JUNIT_TYPE_4) {
					testAnnotation.append("@Test(timeout = " + timeout + RIGHT_PAREN + NEWLINE);
				}

			}
			if (this.fastCodeContext.getJunitPreferences().getJunitType() == JUNIT_TYPE.JUNIT_TYPE_TESTNG) {
				String annotation = null;
				if (excep != null) {
					annotation = modifyAnnotationForNegativeTestNG(testAnnotation.toString().trim(), excep, timeout);
				} else if (excep == null && timeout == null) {
					annotation = modifyAnnotationForTestNG(testAnnotation.toString().trim());
				}

				methAnnotations.append(isEmpty(annotation) ? EMPTY_STR : annotation + NEWLINE);
			} else {
				methAnnotations.append(isEmpty(testAnnotation.toString().trim()) ? EMPTY_STR : testAnnotation.toString().trim() + NEWLINE);
			}

		} else {
			/*
			 * String annotations =
			 * SourceUtil.createAnnotations(this.fastCodeContext,
			 * toType.getCompilationUnit(), toType.getJavaProject(),
			 * methodAnnotations, placeHolders);
			 */
			if (this.fastCodeContext.getJunitPreferences().getJunitType() == JUNIT_TYPE.JUNIT_TYPE_TESTNG) {
				annotations = modifyAnnotationForTestNG(annotations);
			}

			methAnnotations.append(isEmpty(annotations) ? EMPTY_STR : annotations);
		}
		return methAnnotations.toString().trim();
	}

	/**
	 * @param annotations
	 * @param excep
	 * @param timeOut
	 * @return
	 */
	private String modifyAnnotationForNegativeTestNG(final String annotations, final String excep, final String timeOut) {
		final StringBuilder annotationsStringBuilder = new StringBuilder();
		final CreateUnitTestData createUnitTestData = this.fastCodeContext.getJunitPreferences().getCreateUnitTestData();
		if (annotations != null) {
			final String[] annotationArray = annotations.split(NEWLINE);
			for (String annot : annotationArray) {
				// System.out.println(annot);
				if (annot != null && annot.startsWith("@Test")) {

					String dataProvider = null;
					final StringBuilder testAnnotationBuilder = new StringBuilder();
					if (createUnitTestData.getSelectedDataProvider() != null) {

						dataProvider = createUnitTestData.getSelectedDataProvider();
					}
					if (createUnitTestData.getSelectedDependsOnMethod() != null) {

						for (final String annotation : createUnitTestData.getSelectedDependsOnMethod()) {
							if (annotation != null) {
								testAnnotationBuilder.append(annotation + COMMA);
							}
						}
						testAnnotationBuilder.deleteCharAt(testAnnotationBuilder.lastIndexOf(COMMA));
					}
					if (dataProvider != null && testAnnotationBuilder.length() != 0 && excep != null) {
						if (timeOut != null) {
							annot = "@Test(expectedExceptions = {" + excep + "}, timeOut = " + timeOut + "dataProvider = \"" + dataProvider
									+ "\" , " + "dependsOnMethods = {\"" + testAnnotationBuilder.toString().trim() + "\" } )";
						} else {
							annot = "@Test(expectedExceptions = " + excep + ", dataProvider = \"" + dataProvider + "\" , "
									+ "dependsOnMethods = {\"" + testAnnotationBuilder.toString().trim() + "\" } )";
						}
					} else if (dataProvider == null && testAnnotationBuilder.length() != 0 && excep != null) {
						if (timeOut != null) {
							annot = "@Test(expectedExceptions = {" + excep + "}, timeOut = " + timeOut + "dependsOnMethods =  {\" "
									+ testAnnotationBuilder.toString().trim() + "\"} )";
						} else {
							annot = "@Test(expectedExceptions = " + excep + ", dependsOnMethods =  {\" "
									+ testAnnotationBuilder.toString().trim() + "\"} )";
						}
					} else if (dataProvider != null && testAnnotationBuilder.length() == 0 && excep != null) {
						if (timeOut != null) {
							annot = "@Test(expectedExceptions = {" + excep + "}, timeOut = " + timeOut + "dataProvider = \"" + dataProvider
									+ "\" )";
						} else {
							annot = "@Test(expectedExceptions = " + excep + ", dataProvider = \"" + dataProvider + "\" )";
						}
					}
				}
				annotationsStringBuilder.append(annot + NEWLINE);

			}
		}
		return annotationsStringBuilder.toString().trim();
	}

	/**
	 * @param annotations
	 * @return
	 */
	private String modifyAnnotationForTestNG(final String annotations) {
		final StringBuilder annotationsStringBuilder = new StringBuilder();

		if (annotations != null) {

			final String[] annotationArray = annotations.split(NEWLINE);
			final CreateUnitTestData createUnitTestData = this.fastCodeContext.getJunitPreferences().getCreateUnitTestData();
			for (String annot : annotationArray) {
				if (annot != null && annot.equals("@org.testng.annotations.Test") || annot != null
						&& createUnitTestData.getUnitTestType() == UNIT_TEST_TYPE.EXCEPTION_TEST) {
					String dataProvider = null;
					final StringBuilder testAnnotationBuilder = new StringBuilder();

					if (createUnitTestData.getSelectedDataProvider() != null) {

						dataProvider = createUnitTestData.getSelectedDataProvider();
					}
					if (createUnitTestData.getSelectedDependsOnMethod() != null) {

						for (final String annotation : createUnitTestData.getSelectedDependsOnMethod()) {
							testAnnotationBuilder.append(annotation + COMMA);
						}
						testAnnotationBuilder.deleteCharAt(testAnnotationBuilder.lastIndexOf(COMMA));
					}
					if (dataProvider != null && testAnnotationBuilder.length() != 0) {
						annot = "@Test(dataProvider = \"" + dataProvider + "\" , " + "dependsOnMethods = {\""
								+ testAnnotationBuilder.toString().trim() + "\" } )";
					} else if (dataProvider == null && testAnnotationBuilder.length() != 0) {
						annot = "@Test(dependsOnMethods =  {\" " + testAnnotationBuilder.toString().trim() + "\"} )";
					} else if (dataProvider != null && testAnnotationBuilder.length() == 0) {
						annot = "@Test(dataProvider = \"" + dataProvider + "\" )";
					}

				}
				annotationsStringBuilder.append(annot + NEWLINE);
			}

		}
		return annotationsStringBuilder.toString().trim();
	}

	/**
	 * @param createSimilarDescriptorClass
	 * @param method
	 * @param toType
	 *
	 */
	@Override
	protected List<Pair<String, String>> makeMethodParms(final CreateSimilarDescriptor CreateSimilarDescriptor,
			final CreateSimilarDescriptorClass createSimilarDescriptorClass, final IMethod method, final IType toType) throws Exception {
		return new ArrayList<Pair<String, String>>();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.fastcode.util.AbstractMethodBuilder#makeMethodeturnType(org.eclipse
	 * .jdt.core.IType, org.eclipse.jdt.core.IMethod,
	 * org.fastcode.util.CreateSimilarDescriptor,
	 * org.fastcode.util.CreateSimilarDescriptorClass)
	 */
	@Override
	protected String makeMethodeturnType(final IType toType, final IMethod method, final CreateSimilarDescriptor createSimilarDescriptor,
			final CreateSimilarDescriptorClass createSimilarDescriptorClass) throws Exception {
		return "void";
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.fastcode.util.AbstractMethodBuilder#getMethodComments(org.eclipse
	 * .jdt.core.IMethod, org.eclipse.jdt.core.IType, java.util.List,
	 * org.fastcode.util.CreateSimilarDescriptor, java.util.Map)
	 */
	@Override
	protected String getMethodComments(final IMethod method, final IType toType, final List<Pair<String, String>> methodParms,
			final CreateSimilarDescriptor createSimilarDescriptor, final Map<String, Object> placeHolders) throws Exception {
		final StringBuilder methodComments = new StringBuilder("/**" + NEWLINE);

		methodComments.append(SPACE + ASTERISK + NEWLINE);
		methodComments.append(SPACE + "* @see " + method.getDeclaringType().getFullyQualifiedName() + HASH + method.getElementName()
				+ LEFT_PAREN);
		int count = 0;
		for (final String pType : method.getParameterTypes()) {
			methodComments.append(Signature.getSignatureSimpleName(pType));
			methodComments.append(count < method.getNumberOfParameters() - 1 ? COMMA : EMPTY_STR);
			count++;
		}
		methodComments.append(RIGHT_PAREN + NEWLINE);

		methodComments.append(" */");

		return methodComments.toString();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.fastcode.util.AbstractMethodBuilder#makeMethodName(org.eclipse.jdt
	 * .core.IMethod, org.eclipse.jdt.core.IType,
	 * org.fastcode.util.CreateSimilarDescriptor,
	 * org.fastcode.util.CreateSimilarDescriptorClass)
	 */
	@Override
	protected String makeMethodName(final IMethod method, final IType toType, final CreateSimilarDescriptor createSimilarDescriptor,
			final CreateSimilarDescriptorClass createSimilarDescriptorClass) {
		final JunitPreferences junitPreferences = this.fastCodeContext.getJunitPreferences();
		final String testMethName = junitPreferences.getCreateUnitTestData().getTestMethodName();// junitPreferences.getJunitTestMethod().startsWith("${method_name}")
																									// ?
																									// replacePlaceHolder(junitPreferences.getJunitTestMethod(),
																									// "method_name",
																									// method.getElementName())
																									// :
																									// replacePlaceHolder(junitPreferences.getJunitTestMethod(),
																									// "method_name",
																									// createEmbeddedInstance(method.getElementName()));

		//final Shell parentShell = MessageUtil.getParentShell();
		//final Shell shell = parentShell == null ? new Shell() : parentShell;

		final IMethod testMethod = toType.getMethod(testMethName, null);
		if (testMethod == null || !testMethod.exists()) {
			return testMethName;
		}
		/*
		 * final StringBuilder tstMethName = new StringBuilder(testMethName +
		 * EXTENSION_OTHER); int i = 1;
		 */
		this.excepTest = junitPreferences.getCreateUnitTestData().getUnitTestType() == UNIT_TEST_TYPE.EXCEPTION_TEST; // openQuestion(shell,
																														// "Exception",
																														// "Do you want to test that this method throws exception?");
		/*
		 * while (true) { testMethod = toType.getMethod(tstMethName.toString(),
		 * null); if ((testMethod == null) || !testMethod.exists()) { return
		 * tstMethName.toString(); } tstMethName.append(i++); }
		 */
		return junitPreferences.getCreateUnitTestData().getAddnlTestMethodName();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.fastcode.util.AbstractMethodBuilder#getMethodArgs(org.eclipse.jdt
	 * .core.IMethod, org.eclipse.jdt.core.IType, java.util.List, java.util.Map)
	 */
	@Override
	protected String getMethodArgs(final IMethod method, final IType toType, final List<Pair<String, String>> methodParms,
			final Map<String, Object> placeHolders) throws Exception {
		return EMPTY_STR;
	}
}
