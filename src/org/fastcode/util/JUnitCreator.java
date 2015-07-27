/*
 * Fast Code Plugin for Eclipse
 *
 * Copyright (C) 2008  Gautam Dev
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA.
 *
 * Plugin Home Page: http://fast-code.sourceforge.net/
 */

package org.fastcode.util;

import static org.eclipse.jdt.core.Flags.AccStatic;
import static org.eclipse.jdt.core.Flags.isAbstract;
import static org.eclipse.jdt.core.Flags.isPrivate;
import static org.fastcode.common.FastCodeConstants.CLASS_NAME_STR;
import static org.fastcode.common.FastCodeConstants.COLON;
import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FORWARD_SLASH;
import static org.fastcode.common.FastCodeConstants.INITIATED;
import static org.fastcode.common.FastCodeConstants.JAVA_EXTENSION;
import static org.fastcode.common.FastCodeConstants.LEFT_CURL;
import static org.fastcode.common.FastCodeConstants.METHOD_ANNOTATIONS_STR;
import static org.fastcode.common.FastCodeConstants.METHOD_ARGS_STR;
import static org.fastcode.common.FastCodeConstants.METHOD_BODY_STR;
import static org.fastcode.common.FastCodeConstants.METHOD_COMMENTS_STR;
import static org.fastcode.common.FastCodeConstants.METHOD_EXCEPTIONS_STR;
import static org.fastcode.common.FastCodeConstants.METHOD_MODIFIER_STR;
import static org.fastcode.common.FastCodeConstants.METHOD_NAME_STR;
import static org.fastcode.common.FastCodeConstants.METHOD_RETURN_TYPE_STR;
import static org.fastcode.common.FastCodeConstants.MODIFIER_PUBLIC;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_CLASS;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_STUBMETHODS;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_TESTMETHODS;
import static org.fastcode.common.FastCodeConstants.UNDERSCORE;
import static org.fastcode.preferences.PreferenceConstants.P_COMMON_TEMPLATE_PREFIX;
import static org.fastcode.setting.GlobalSettings.getInstance;
import static org.fastcode.util.JUnitUtil.createTestMethodName;
import static org.fastcode.util.JUnitUtil.findTestUnit;
import static org.fastcode.util.SourceUtil.checkForErrors;
import static org.fastcode.util.SourceUtil.createSimilar;
import static org.fastcode.util.SourceUtil.findSuperInterfaceType;
import static org.fastcode.util.SourceUtil.getDefaultPathFromProject;
import static org.fastcode.util.SourceUtil.getRepositoryServiceClass;
import static org.fastcode.util.SourceUtil.getSelectedMembers;
import static org.fastcode.util.StringUtil.createDefaultInstance;
import static org.fastcode.util.StringUtil.evaluateByVelocity;
import static org.fastcode.util.StringUtil.getGlobalSettings;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.StringUtil.replacePlaceHolder;
import static org.fastcode.util.StringUtil.replacePlaceHolderWithBlank;
import static org.fastcode.util.VersionControlUtil.addOrUpdateFileStatusInCache;
import static org.fastcode.util.VersionControlUtil.isPrjConfigured;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.fastcode.common.CreateUnitTestData;
import org.fastcode.common.FastCodeAnnotation;
import org.fastcode.common.FastCodeConstants.CLASS_TYPE;
import org.fastcode.common.FastCodeConstants.JUNIT_TYPE;
import org.fastcode.common.FastCodeConstants.UNIT_TEST_CHOICE;
import org.fastcode.common.FastCodeConstants.UNIT_TEST_TYPE;
import org.fastcode.common.FastCodeEntityHolder;
import org.fastcode.common.FastCodeMethod;
import org.fastcode.common.FastCodeType;
import org.fastcode.common.MethodSelectionDialog;
import org.fastcode.dialog.CreateUnitTestDialog;
import org.fastcode.preferences.JunitPreferences;
import org.fastcode.preferences.VersionControlPreferences;
import org.fastcode.setting.GlobalSettings;
import org.fastcode.setting.TemplateSettings;
import org.fastcode.versioncontrol.FastCodeCheckinCache;

/**
 * The Class JUnitGenerator.
 *
 * @author gdev
 */
public class JUnitCreator {

	public static final String	LONG_ONE			= "(1)";

	public static final String	VOID_STR			= "()";

	protected boolean			overwrite			= false;

	protected String			velcityMacroFile	= "junit.vm";

	/**
	 *
	 * @param type
	 * @param method
	 * @param newParam
	 * @return
	 * @throws Exception
	 */
	public static IMethod[] findTestMethods(final IType type, final IMethod method, final JunitPreferences junitPreferences)
			throws Exception {
		final ICompilationUnit testCU = findTestUnit(type, junitPreferences);

		if (testCU != null && testCU.exists() && method != null) {
			final String testMethName = createTestMethodName(method);

			final List<IMethod> similarMethods = new ArrayList<IMethod>();
			for (final IMethod similarMethod : type.getMethods()) {
				if (similarMethod.equals(method) || similarMethod.getElementName().equals(method.getElementName())) {
					continue;
				}
				if (similarMethod.getElementName().startsWith(method.getElementName())) {
					similarMethods.add(similarMethod);
				}
			}

			final IMethod[] allTestMethods = testCU.findPrimaryType().getMethods();
			final List<IMethod> testMethods = new ArrayList<IMethod>();
			for (final IMethod meth : allTestMethods) {
				boolean canBeTestForAnotherMethod = false;
				if (meth.getElementName().startsWith(testMethName)) {
					for (final IMethod similarMethod : similarMethods) {
						String anoTestMethName;
						anoTestMethName = createTestMethodName(similarMethod);
						if (meth.getElementName().startsWith(anoTestMethName)) {
							canBeTestForAnotherMethod = true;

							break;
						}
					}
					if (!canBeTestForAnotherMethod) {
						testMethods.add(meth);
					}
				}
			}
			return testMethods.toArray(new IMethod[0]);
		}
		return null;
	}

	/**
	 *
	 * @param typeToWorkOn
	 * @param method
	 * @return
	 * @throws Exception
	 */
	public static IMember generateTest(final IType type, final Map<Object, List<FastCodeEntityHolder>> commitMessage,
			final IMethod... methods) throws Exception {

		final VersionControlPreferences versionControlPreferences = VersionControlPreferences.getInstance();

		if (JunitPreferences.getInstance(type) == null) {
			throw new Exception("Please select the correct project in Windows->Preferences->Fast Code->Unit Test and try again.");
		}
		final CreateUnitTestData createUnitTestData = getCreateUnitTestData(type, methods);

		if (createUnitTestData == null) {
			return null;
		}
		final JunitPreferencesAndType junitPreferencesAndType = createUnitTestData.getJunitPreferencesAndType(); // JUnitUtil.findJunitPreferences(type,
																													// createUnitTestData.getJunitTestProfileName());
		/*
		 * if (junitPreferencesAndType == null) { throw new
		 * Exception("This class you selected does not match any profile. " +
		 * "Please configure it first by going to Window -> Preference -> Fast Code Pereference."
		 * ); }
		 */
		JunitPreferences junitPreferences = junitPreferencesAndType.getJunitPreferences();
		IType typeToWorkOn = junitPreferencesAndType.getType();
		/*
		 * junitPreferences.setExpTest(createUnitTestData.getUnitTestType() ==
		 * UNIT_TEST_TYPE.EXCEPTION_TEST);
		 * junitPreferences.setHandleException(createUnitTestData
		 * .getHandleException());
		 * junitPreferences.setResultFormatList(Arrays.asList
		 * (createUnitTestData.getUnitTestRsltFormatSelected()));
		 * junitPreferences
		 * .setMethodReturnType(createUnitTestData.getMethodReturnType());
		 */
		junitPreferences.setCreateUnitTestData(createUnitTestData);

		// String junitTestLocation =
		// replacePlaceHolder(junitPreferences.getJunitTestLocation(),
		// "project", type.getJavaProject().getElementName());
		// junitTestLocation = "/" + junitTestLocation;

		final GlobalSettings globalSettings = getInstance();

		String junitTestLocation = globalSettings.isUseDefaultForPath() ? globalSettings.getSourcePathTest() : junitPreferences
				.getJunitTestLocation();
		junitTestLocation = getDefaultPathFromProject(typeToWorkOn.getJavaProject(), "test", junitTestLocation);
		final IPackageFragmentRoot testPackageFragmentRoot = SourceUtil.getPackageRootFromProject(typeToWorkOn.getJavaProject(),
				junitTestLocation);

		if (testPackageFragmentRoot == null || !testPackageFragmentRoot.exists()) {
			throw new Exception("Path does not exist in the project " + junitTestLocation);
		}

		// final String junitTestClassName =
		// junitPreferences.getJunitTestClass();
		final String junitBase = junitPreferences.getJunitBaseType();
		// final boolean createMethodBody =
		// junitPreferences.isCreateMethodBody();
		// final boolean alwaysCreateTryCatch =
		// junitPreferences.isAlwaysCreateTryCatch();
		// final boolean createInstance = junitPreferences.isCreateInstance();
		// final String[] methodAnnotations =
		// junitPreferences.getMethodAnnotations();

		ICompilationUnit unitTestCU = findTestUnit(typeToWorkOn, junitPreferences);
		// find the interface if unit test is null
		if (unitTestCU == null && typeToWorkOn.isClass()) {
			final IType tmpType = findSuperInterfaceType(typeToWorkOn);
			if (tmpType != null) {
				final JunitPreferences junitPref = JunitPreferences.getInstance(tmpType);
				if (junitPref != null) {
					unitTestCU = findTestUnit(tmpType, junitPref);
					if (unitTestCU != null) {
						junitPreferences = junitPref;
						typeToWorkOn = tmpType;
						/*
						 * junitPreferences.setExpTest(createUnitTestData.
						 * getUnitTestType() == UNIT_TEST_TYPE.EXCEPTION_TEST);
						 * junitPreferences
						 * .setHandleException(createUnitTestData
						 * .getHandleException());
						 * junitPreferences.setResultFormatList
						 * (Arrays.asList(createUnitTestData
						 * .getUnitTestRsltFormatSelected()));
						 * junitPreferences.setMethodReturnType
						 * (createUnitTestData.getMethodReturnType());
						 */
						junitPreferences.setCreateUnitTestData(createUnitTestData);
					}
				}
			}
		}

		if (!typeToWorkOn.equals(type) && checkForErrors(typeToWorkOn.getCompilationUnit().getResource())) {
			if (!MessageDialog.openQuestion(new Shell(), "Error",
					"There seems to be some problems associated with " + typeToWorkOn.getElementName()
							+ ". It is better to fix those problems and try again. Do you want to continue?")) {
				return null;
			}
		}

		IType junitBaseType = null;

		final JUNIT_TYPE junitType = junitPreferences.getJunitType();
		if (junitType == JUNIT_TYPE.JUNIT_TYPE_3) {
			junitBaseType = typeToWorkOn.getJavaProject().findType(junitBase);
			if (junitBaseType == null || !junitBaseType.exists()) {
				throw new Exception("Unable to find Junit Base Class " + junitBase + " in the current project. "
						+ "Make sure you have configured it properly by going to Windows -> Preference ->Fast Code Preference");
			}
		}

		final String testPkg = typeToWorkOn.getPackageFragment().getElementName();
		IPackageFragment testPackageFragment = testPackageFragmentRoot.getPackageFragment(testPkg);

		if (testPackageFragment == null || !testPackageFragment.exists()) {
			testPackageFragment = testPackageFragmentRoot.createPackageFragment(testPkg, false, null);
		}

		//final String testFormat = junitPreferences.getTestFormat();
		final String instance = createDefaultInstance(typeToWorkOn.getElementName());
		final CreateSimilarDescriptor createSimilarDescriptor = makeCreateSimilarDescriptor(junitPreferences, typeToWorkOn);
		final FastCodeContext fastCodeContext = new FastCodeContext(typeToWorkOn, true, junitPreferences);
		final boolean testClassExst = unitTestCU != null && unitTestCU.exists();
		final CreateSimilarDescriptorClass createSimilarDescriptorClass = createSimilarDescriptor.getCreateSimilarDescriptorClasses()[0];
		IFile unitTestFile = null;
		boolean createFileAlone = false;
		if (!testClassExst) {
			final String prjURI = testPackageFragment.getResource().getLocationURI().toString();
			final String path = prjURI.substring(prjURI.indexOf(COLON) + 1);
			final File newFileObj = new File(path + FORWARD_SLASH + createSimilarDescriptor.getToPattern() + DOT + JAVA_EXTENSION);
			final boolean prjShared = !isEmpty(testPackageFragment.getResource().getProject().getPersistentProperties());
			final boolean prjConfigured = !isEmpty(isPrjConfigured(testPackageFragment.getResource().getProject().getName()));
			if (versionControlPreferences.isEnable() && prjShared && prjConfigured) {
				final RepositoryService repositoryService = getRepositoryServiceClass();
				if (repositoryService.isFileInRepository(newFileObj)) { // && !MessageDialog.openQuestion(new Shell(), "File present in repository", "File already present in repository. Click yes to overwrite")) {
					/*MessageDialog.openWarning(new Shell(), "File present in repository", junitPreferences.getJunitTestClass() + " is already present in repository. Please synchronise and try again.");
					return null;*/
					createFileAlone = MessageDialog.openQuestion(new Shell(), "File present in repository", "File " + newFileObj.getName()
							+ " already present in repository. Click yes to just create the file, No to return without any action.");
					if (!createFileAlone) {
						return null;
					}
				}
			} else {
				createFileAlone = true;
			}
			/*final FastCodeCheckinCache checkinCache = FastCodeCheckinCache.getInstance();
			checkinCache.getFilesToCheckIn().add(new FastCodeFileForCheckin(INITIATED, newFileObj.getAbsolutePath()));*/
			addOrUpdateFileStatusInCache(newFileObj);

			createSimilar(fastCodeContext, null, createSimilarDescriptor, new NullProgressMonitor());
			unitTestCU = fastCodeContext.getCompilationUnitRegsistry(createSimilarDescriptorClass);
			if (!createFileAlone) {
				unitTestFile = (IFile) unitTestCU.findPrimaryType().getResource();//.getLocationURI());
				List<FastCodeEntityHolder> chngsForType = commitMessage.get(unitTestFile);
				if (chngsForType == null) {
					chngsForType = new ArrayList<FastCodeEntityHolder>();
					chngsForType.add(new FastCodeEntityHolder(PLACEHOLDER_CLASS, new FastCodeType(unitTestCU.findPrimaryType())));
				}
				commitMessage.put(unitTestFile, chngsForType);
			}

		} else {
			createFileAlone = true;
		}

		if (testClassExst) {
			if (!unitTestCU.getResource().isSynchronized(0)) {
				throw new Exception(unitTestCU.getElementName() + " is not Synchronized, please refresh and try again.");
			}
			unitTestFile = (IFile) unitTestCU.findPrimaryType().getResource(); //.getLocationURI());
		}
		final Map<String, FastCodeMethod> stubMethods = createUnitTestData.getStubMethodsMap(); // FastCodeMethodRegistry.getRegisteredUnitTestStubMethods(junitType);
		if (stubMethods != null) { // && !testClassExst) {
			/*
			 * final FastCodeMethodSelectionDialog methodSelectionDialog = new
			 * FastCodeMethodSelectionDialog(new Shell(), "Select Methods",
			 * "Select One of more Stub Methods from Below", stubMethods, true);
			 * methodSelectionDialog.open(); final Object[] regularMethods =
			 * methodSelectionDialog.getResult();
			 */
			//unitTestFile = (IFile) unitTestCU.findPrimaryType().getResource();
			if (!createFileAlone) {
				final File newFileObj = new File(unitTestFile.getLocationURI().toString());
				final FastCodeCheckinCache checkinCache = FastCodeCheckinCache.getInstance();
				checkinCache.getFilesToCheckIn().add(new FastCodeFileForCheckin(INITIATED, newFileObj.getAbsolutePath()));
			}
			if (createUnitTestData.getSelectedStubMethodsList() != null) {
				for (final FastCodeMethod fastCodeMethod : createUnitTestData.getSelectedStubMethodsList().toArray(new FastCodeMethod[0])) {
					createStubMethod(unitTestCU.findPrimaryType(), fastCodeMethod);
					if (!createFileAlone) {
						List<FastCodeEntityHolder> chngesForType = commitMessage.get(unitTestFile);
						if (chngesForType == null) {
							chngesForType = new ArrayList<FastCodeEntityHolder>();
							final List<Object> fastCodeMethodList = new ArrayList<Object>();
							fastCodeMethodList.add(fastCodeMethod);
							chngesForType.add(new FastCodeEntityHolder(PLACEHOLDER_STUBMETHODS, fastCodeMethodList));
						} else {
							boolean isNew = true;
							Object fastCodeMethodList = null;
							for (final FastCodeEntityHolder fcEntityHolder : chngesForType) {
								if (fcEntityHolder.getEntityName().equals(PLACEHOLDER_STUBMETHODS)) {
									fastCodeMethodList = fcEntityHolder.getFastCodeEntity();
									isNew = false;
									break;
								}
							}

							if (isNew) {
								fastCodeMethodList = new ArrayList<Object>();
								((List<Object>) fastCodeMethodList).add(fastCodeMethod);
								chngesForType.add(new FastCodeEntityHolder(PLACEHOLDER_STUBMETHODS, fastCodeMethodList));
							} else {
								((List<Object>) fastCodeMethodList).add(fastCodeMethod);
							}
						}
						commitMessage.put(unitTestFile, chngesForType);
					}
				}
			}
		}

		if (junitType == JUNIT_TYPE.JUNIT_TYPE_3 || junitType == JUNIT_TYPE.JUNIT_TYPE_CUSTOM) {
			if (junitBaseType != null && junitBaseType.exists()) {
				unitTestCU.createImport(junitBaseType.getFullyQualifiedName(), null, null);
			}
		} else if (junitType == JUNIT_TYPE.JUNIT_TYPE_4) {
			unitTestCU.createImport("org.junit.*", null, null);
			unitTestCU.createImport("org.junit.Assert.*", null, AccStatic, null);
		} else if (junitType == JUNIT_TYPE.JUNIT_TYPE_TESTNG) {
			unitTestCU.createImport("org.testng.annotations.*", null, null);
			unitTestCU.createImport("org.testng.Assert.*", null, AccStatic, null);
		}

		if (unitTestCU == null || !unitTestCU.exists() || createUnitTestData.getClassMethodsSelected() == null || methods.length == 0) {
			return unitTestCU != null && unitTestCU.exists() ? unitTestCU.findPrimaryType() : null;
		}

		unitTestCU.createImport(typeToWorkOn.getFullyQualifiedName(), null, null);

		final List<IMethod> retMethods = new ArrayList<IMethod>();
		final UnitTestMethodBuilder methodBuilder = new UnitTestMethodBuilder(fastCodeContext);

		boolean becomeWorkingCopy = false;
		if (!unitTestCU.isWorkingCopy()) {
			becomeWorkingCopy = true;
			unitTestCU.becomeWorkingCopy(null);
		}

		try {
			for (final IMethod method : createUnitTestData.getClassMethodsSelected().toArray(new IMethod[0])) {

				if (EMPTY_STR.equals(createUnitTestData.getTestMethodName())) {
					createUnitTestData.setTestMethodName(method.getElementName());
				}
				IMethod methodToWorkOn = method;
				if (!type.equals(typeToWorkOn)) {
					methodToWorkOn = typeToWorkOn.getMethod(method.getElementName(), method.getParameterTypes());
					if (methodToWorkOn == null || !methodToWorkOn.exists()) {
						MessageDialog.openError(new Shell(), "Error", "Method " + method.getElementName() + " does not exist in "
								+ typeToWorkOn.getElementName());
						continue;
					}
				}
				final IMethod[] testMethods = testClassExst ? findTestMethods(typeToWorkOn, methodToWorkOn, junitPreferences) : null;
				// boolean testMethodExist = false, createAotherTestMethod =
				// false;
				// testMethodExist = createAotherTestMethod = false;

				if (testMethods != null && testMethods.length > 0) {
					// testMethodExist = createAotherTestMethod = true;
					/*
					 * final String[] choices = {"Create an additional test",
					 * "Do Nothing", "Jump To The Test"};
					 *
					 * final String choice = getChoiceFromMultipleValues(new
					 * Shell(), "Junit Test Exists for method " +
					 * methodToWorkOn.getElementName(), "Would You Like To",
					 * choices); final int result = findInStringArray(choice,
					 * choices);
					 */

					if (createUnitTestData.getUnitTestChoice().equals(UNIT_TEST_CHOICE.CREATEADDITIONALTEST)) {
						// break;
					} else if (createUnitTestData.getUnitTestChoice().equals(UNIT_TEST_CHOICE.JUMPTOTEST)) {
						if (methods.length == 1) {
							if (testMethods.length == 1) {
								return testMethods[0];
							}
							final MethodSelectionDialog methodSelectionDialog = new MethodSelectionDialog(new Shell(),
									"Select Test Method", "Multiple tests found for the method you selected, "
											+ "please select one from the list below.", testMethods, false);
							methodSelectionDialog.open();
							return methodSelectionDialog.getResult() == null || methodSelectionDialog.getResult().length == 0 ? null
									: (IMethod) methodSelectionDialog.getFirstResult();
						}
					}
					/*
					 * switch (result) { case 0: // createAotherTestMethod =
					 * true; break; case 1: continue; case 2:
					 *
					 * }
					 */

					// if (createAotherTestMethod) {
					// final MessageDialogWithToggle dialogForMethodBody =
					// openYesNoQuestion(new Shell(),
					// "Create Method Body", "Do you create method body?",
					// "Remember Decision", false,
					// Activator.getDefault().getPreferenceStore(),
					// P_JUNIT_TEST_ASK_FOR_METHOD_BODY);
					// if (dialogForMethodBody.getReturnCode() !=
					// MESSAGE_DIALOG_RETURN_YES) {
					// createMethodBody = false;
					// alwaysCreateTryCatch = false;
					// }
					// }
				}

				if (junitBaseType != null) {
					for (final IMethod meth : junitBaseType.getMethods()) {
						if (isAbstract(meth.getFlags())) {
							// add methods here.
							// copyMethods(junitBaseType,
							// testCU.findPrimaryType(), meth, null,
							// METHOD_PATTERN_DEFAULT, null,
							// RETURN_TYPE.RETURN_TYPE_PASS_THRU, null, null,
							// false, null, false, false, null, null, null);
						}
					}
				}

				// if (junitPreferences.isCreateInstance()) {
				// final IField field =
				// testCU.findPrimaryType().getField(instance);
				// if (field == null || !field.exists()) {
				// testCU.findPrimaryType().createField("protected " +
				// type.getElementName() + SPACE + instance + ";\n", null,
				// false, null);
				// }
				// }
				// if (!testMethodExist || createAotherTestMethod) {
				// copyImports(type.getCompilationUnit(), testCU, method);
				// copyMethods(type, testCU.findPrimaryType(), method, instance,
				// junitPreferences.getJunitTestMethod(),
				// createAotherTestMethod ? EXTENSION_OTHER : null,
				// RETURN_TYPE.RETURN_TYPE_CONSUME, null, null, false, "result",
				// createMethodBody, alwaysCreateTryCatch,
				// methodAnnotations, null, null);
				// }

				/**
				 * if (method == null) { for (final IMethod meth:
				 * testCU.findPrimaryType().getMethods()) { if
				 * (!meth.isConstructor()) { testMethod = meth; break; } } }
				 * else { String testMethName =
				 * replacePlaceHolder(junitPreferences.getJunitTestMethod(),
				 * "method_name", method.getElementName()); if
				 * (!junitPreferences
				 * .getJunitTestMethod().startsWith("${method_name}")) {
				 * testMethName =
				 * replacePlaceHolder(junitPreferences.getJunitTestMethod(),
				 * "method_name",
				 * createEmbeddedInstance(method.getElementName())); }
				 *
				 * testMethod = testCU.findPrimaryType().getMethod(testMethName
				 * + (createAotherTestMethod ? EXTENSION_OTHER : EMPTY_STR),
				 * null); }
				 */
				if (!createFileAlone) {
					final File newFileObj = new File(unitTestFile.getLocationURI().toString());
					final FastCodeCheckinCache checkinCache = FastCodeCheckinCache.getInstance();
					checkinCache.getFilesToCheckIn().add(new FastCodeFileForCheckin(INITIATED, newFileObj.getAbsolutePath()));
				}
				final IMethod tstMethod = methodBuilder.buildMethod(methodToWorkOn, unitTestCU.findPrimaryType(), createSimilarDescriptor,
						createSimilarDescriptorClass);
				createUnitTestData.setTestMethodName(EMPTY_STR);
				if (tstMethod != null) {
					retMethods.add(tstMethod);
					if (!createFileAlone) {
						List<FastCodeEntityHolder> chngesForType = commitMessage.get(unitTestFile);
						if (chngesForType == null) {
							chngesForType = new ArrayList<FastCodeEntityHolder>();
							final List<Object> fastCodeMethodList = new ArrayList<Object>();
							fastCodeMethodList.add(new FastCodeMethod(tstMethod));
							chngesForType.add(new FastCodeEntityHolder(PLACEHOLDER_TESTMETHODS, fastCodeMethodList));
						} else {

							boolean isNew = true;
							Object fastCodeMethodList = null;
							for (final FastCodeEntityHolder fcEntityHolder : chngesForType) {
								if (fcEntityHolder.getEntityName().equals(PLACEHOLDER_TESTMETHODS)) {
									fastCodeMethodList = fcEntityHolder.getFastCodeEntity();
									isNew = false;
									break;
								}
							}

							if (isNew) {
								fastCodeMethodList = new ArrayList<Object>();
								((List<Object>) fastCodeMethodList).add(new FastCodeMethod(tstMethod));
								chngesForType.add(new FastCodeEntityHolder(PLACEHOLDER_TESTMETHODS, fastCodeMethodList));
							} else {
								((List<Object>) fastCodeMethodList).add(new FastCodeMethod(tstMethod));
							}
						}
						commitMessage.put(unitTestFile, chngesForType);
					}
				}
			}
		} catch (final Exception ex) {
			ex.printStackTrace();
			throw new Exception(ex.getMessage(), ex);
		} finally {
			// if (testClassExst) {
			if (!unitTestCU.hasResourceChanged()) {
				unitTestCU.commitWorkingCopy(false, null);
			}
			if (becomeWorkingCopy) {
				unitTestCU.discardWorkingCopy();
			}
		}

		if (retMethods.isEmpty()) {
			return unitTestCU.findPrimaryType();
		} else if (retMethods.size() == 1) {
			return retMethods.get(0);
		} else {
			final IMember[] selectedMembers = getSelectedMembers(IJavaElement.METHOD, retMethods.toArray(new IMethod[0]),
					"Unit Test to Jump", false);
			return selectedMembers == null || selectedMembers.length == 0 ? retMethods.get(0) : (IMethod) selectedMembers[0];
		}
	}

	/**
	 * @param type
	 * @param methods2
	 * @return
	 * @throws JavaModelException
	 */
	private static CreateUnitTestData getCreateUnitTestData(final IType type, final IMethod[] methods2) throws JavaModelException {
		final CreateUnitTestData createUnitTestData = new CreateUnitTestData();

		// final IMethod[] methods;
		final List<IMethod> methodsList = new ArrayList<IMethod>();

		if (methods2[0] == null) {
			for (final IMethod meth : type.getMethods()) {
				if (meth.isConstructor() || isPrivate(meth.getFlags())) {
					// if (!openQuestion(new Shell(), "Warning!",
					// "It is not a public method, do you wish to continue?" +
					// "Press Yes to continue, No to bail out.")) {
					// }
					continue;
				}
				methodsList.add(meth);
			}
			createUnitTestData.setInvokedFromMethod(false);
			// final IMember[] selectedMethods = methodsList.isEmpty() ? null :
			// getSelectedMembers(IJavaElement.METHOD, methodsList.toArray(new
			// IMethod[0]), "Unit Test", true);

			// methods = selectedMethods == null || selectedMethods.length == 0
			// ? null : (IMethod[]) selectedMethods;
		} else {
			// methods = new IMethod[1];
			// methods[0] = method;
			methodsList.add(methods2[0]);
			createUnitTestData.setInvokedFromMethod(true);
		}

		createUnitTestData.setClassMethodsList(methodsList);
		createUnitTestData.setUnitTestProfiles(JunitPreferences.getMatchingProfilesForType(type));
		if (createUnitTestData.getUnitTestProfiles() == null) {
			MessageDialog.openError(new Shell(), "No Test Profile available.",
					"No Test Profile available. Please go to JUnit Preference and set the profile.");
			return null;
		}

		createUnitTestData.setFromType(type);
		CreateUnitTestDialog createJUnitDialog;
		try {
			createJUnitDialog = new CreateUnitTestDialog(new Shell(), createUnitTestData);
			if (createJUnitDialog.open() == Window.CANCEL) {
				return null;
			}
		} catch (final Exception ex) {
			MessageDialog.openError(new Shell(), "Some error occured, check the preference and try again.", ex.getMessage());
			return null;
			// ex.printStackTrace();
		}
		return createJUnitDialog.getCreateUnitTestData();
	}

	/**
	 *
	 * @param findPrimaryType
	 * @param fastCodeMethod
	 *
	 * @return
	 * @throws Exception
	 */
	private static IMethod createStubMethod(final IType testType, final FastCodeMethod fastCodeMethod) throws Exception {
		final Map<String, Object> placeHolders = new HashMap<String, Object>();
		final String methodName = fastCodeMethod.getName();
		final IMethod method = testType.getMethod(methodName, null);
		if (method != null && method.exists()) {
			return method;
		}

		final boolean isConstructor = "Constructor".equals(methodName);
		placeHolders.put(METHOD_NAME_STR, isConstructor ? testType.getElementName() : methodName);
		placeHolders.put(METHOD_MODIFIER_STR, MODIFIER_PUBLIC);
		final String returnType = isConstructor ? EMPTY_STR : fastCodeMethod.getReturnType() == null ? "void" : fastCodeMethod
				.getReturnType().getName();
		placeHolders.put(METHOD_RETURN_TYPE_STR, fastCodeMethod.isStaticMethod() ? "static " + returnType : returnType);
		placeHolders.put(METHOD_COMMENTS_STR, EMPTY_STR);
		placeHolders.put(METHOD_EXCEPTIONS_STR, fastCodeMethod.getExceptions() == null ? EMPTY_STR : fastCodeMethod.getExceptions().get(0)
				.getName());
		placeHolders.put(METHOD_ARGS_STR, isConstructor ? "final String name" : EMPTY_STR);

		final StringBuilder annotation = new StringBuilder();
		for (final FastCodeAnnotation annot : fastCodeMethod.getAnnotations().toArray(new FastCodeAnnotation[0])) {
			annotation.append("@" + annot.getType().getName() + NEWLINE);
		}
		placeHolders.put(METHOD_ANNOTATIONS_STR, annotation.toString().trim());
		final TemplateSettings templateSettings = TemplateSettings.getTemplateSettings(P_COMMON_TEMPLATE_PREFIX + UNDERSCORE
				+ "method.body." + methodName, P_COMMON_TEMPLATE_PREFIX);
		if (isConstructor) {
			placeHolders.put(METHOD_BODY_STR, "super(name);");
		} else {
			placeHolders.put(METHOD_BODY_STR, templateSettings == null ? EMPTY_STR : templateSettings.getTemplateBody());
		}

		final GlobalSettings globalSettings = GlobalSettings.getInstance();
		String methodTemplate = globalSettings.getClassMethodBody();

		if (isConstructor || fastCodeMethod.getExceptions() == null || fastCodeMethod.getExceptions().size() == 0) {
			methodTemplate = replacePlaceHolderWithBlank(methodTemplate, "throws", METHOD_EXCEPTIONS_STR, LEFT_CURL);
		}
		getGlobalSettings(placeHolders);
		final String snippet = evaluateByVelocity(methodTemplate, placeHolders);
		final IMethod createdMethod = testType.createMethod(snippet, null, false, null);
		return createdMethod != null && createdMethod.exists() ? createdMethod : null;
	}

	/**
	 *
	 * @param junitPreferences
	 * @param type
	 * @return
	 */
	private static CreateSimilarDescriptor makeCreateSimilarDescriptor(final JunitPreferences junitPreferences, final IType type)
			throws JavaModelException {
		String[] methodAnnotations = junitPreferences.getMethodAnnotations();
		final JUNIT_TYPE junitType = junitPreferences.getJunitType();
		if (!(junitPreferences.getCreateUnitTestData().getUnitTestType() == UNIT_TEST_TYPE.EXCEPTION_TEST)) {
			if (junitType == JUNIT_TYPE.JUNIT_TYPE_4) {
				methodAnnotations = insertIntoArray(methodAnnotations, "org.junit.Test");
			} else if (junitType == JUNIT_TYPE.JUNIT_TYPE_TESTNG) {
				methodAnnotations = insertIntoArray(methodAnnotations, "org.testng.annotations.Test");
			}
		}
		IType superclassType = null;
		String superClass = EMPTY_STR;
		if (junitType == JUNIT_TYPE.JUNIT_TYPE_3) {
			superClass = junitPreferences.getJunitBaseType();
			superclassType = type.getJavaProject().findType(superClass);
			superClass = superClass.substring(superClass.lastIndexOf('.') + 1);
		}
		String testClassName = junitPreferences.getJunitTestClass();
		testClassName = replacePlaceHolder(testClassName, CLASS_NAME_STR, type.getElementName());

		String[] junitBaseType = { junitPreferences.getJunitBaseType() };
		if (isEmpty(junitPreferences.getJunitBaseType())) {
			junitBaseType = null;
		}

		final CreateSimilarDescriptorClass createSimilarDescriptorClass = new CreateSimilarDescriptorClass.Builder()
				.withClassType(CLASS_TYPE.CLASS).withToPattern(testClassName).withSourcePath(junitPreferences.getJunitTestLocation())
				.withSuperTypes(junitBaseType).withClassInsideBody(junitPreferences.getInsideBody())
				.withInclInstance(junitPreferences.isCreateInstance()).withImportTypes(junitPreferences.getClassImports())
				.withClassAnnotations(junitPreferences.getClassAnnotations()).withFieldAnnotations(junitPreferences.getFieldAnnotations())
				.withCreateMethodBody(junitPreferences.isCreateMethodBody()).withMethodAnnotations(methodAnnotations)
				.withSuperClass(superClass).withUserInputSuperClass(superclassType).build();
		// new CreateSimilarDescriptorClass(CLASS_TYPE.CLASS, testClassName,
		// null, null,junitPreferences.getJunitTestLocation(), null, null,
		// junitBaseType, null, junitPreferences.getInsideBody(), false, false,
		// false,junitPreferences.isCreateInstance(), false, false, false, null,
		// false, null,
		// junitPreferences.getClassImports(),junitPreferences.getClassAnnotations(),
		// junitPreferences.getFieldAnnotations(),
		// junitPreferences.isCreateMethodBody(), methodAnnotations,false, null,
		// false, false, null, null);
		final CreateSimilarDescriptor createSimilarDescriptor = new CreateSimilarDescriptor.Builder()
				.withMethodNamePattern(junitPreferences.getJunitTestMethod())
				.withCreateSimilarDescriptorClasses(new CreateSimilarDescriptorClass[] { createSimilarDescriptorClass }).build();
		// new CreateSimilarDescriptor(null, false, null, null, null, true,
		// false, false, junitPreferences.getJunitTestMethod(),
		// false,createSimilarDescriptorClass);
		createSimilarDescriptor.numbersOfCreateSimilarDescriptorClasses(createSimilarDescriptorClass);

		return createSimilarDescriptor;

	}

	/**
	 *
	 * @param annotations
	 * @param annotation
	 * @return
	 */
	private static String[] insertIntoArray(final String[] annotations, final String annotation) {
		final String[] retAnnotations;
		if (annotations == null || annotations.length == 0) {
			retAnnotations = new String[1];
			retAnnotations[0] = annotation;
		} else {
			for (final String annot : annotations) {
				if (annot.equals(annotation)) {
					return annotations;
				}
			}
			retAnnotations = new String[annotations.length + 1];
			System.arraycopy(annotations, 0, retAnnotations, 0, annotations.length);
			retAnnotations[annotations.length] = annotation;
		}
		return retAnnotations;
	}
}
