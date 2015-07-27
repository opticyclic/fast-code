/**
 *
 */
package org.fastcode.util;

import static org.eclipse.jdt.core.Signature.getSignatureSimpleName;
import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.LEFT_CURL_CHAR;
import static org.fastcode.common.FastCodeConstants.LEFT_PAREN;
import static org.fastcode.common.FastCodeConstants.METHOD_NAME_STR;
import static org.fastcode.common.FastCodeConstants.METHOD_PATTERN_DEFAULT;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.common.FastCodeConstants.RIGHT_CURL_CHAR;
import static org.fastcode.common.FastCodeConstants.RIGHT_PAREN;
import static org.fastcode.common.FastCodeConstants.TAB;
import static org.fastcode.common.FastCodeConstants.UNDERSCORE;
import static org.fastcode.preferences.JunitPreferences.getInstance;
import static org.fastcode.preferences.JunitPreferences.getTestProfiles;
import static org.fastcode.util.SourceUtil.findSuperInterfaceType;
import static org.fastcode.util.SourceUtil.getDefaultPathFromProject;
import static org.fastcode.util.StringUtil.createDefaultInstance;
import static org.fastcode.util.StringUtil.createEmbeddedInstance;
import static org.fastcode.util.StringUtil.replacePlaceHolder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.common.FastCodeConstants.JUNIT_TYPE;
import org.fastcode.preferences.JunitPreferences;
import org.fastcode.setting.GlobalSettings;

/**
 * @author Gautam
 *
 */
public class JUnitUtil {

	private static final String[]	primTypes					= { "int", "long", "boolean", "byte", "char" };
	private static final String[][]	primAsserts					= { { "int", "assertTrue(${variable} > 0)" },
			{ "long", "assertTrue(${variable} > 0l)" }, { "boolean", "assertTrue(${variable})" },
			{ "char", "assertTrue(${variable} == 'a')" }		};

	private static final String		TEST_CASE_CLASS				= "junit.framework.TestCase";

	private static String			DEFAULT_TRY_CATCH_TEMPLATE	= "try {\n" + TAB + "${method_invocation}\n"
																		+ "} catch (${exception_type} ${exception_variable}) {\n"
																		+ "${exception_body}\n" + "}\n";

	private static final String		TRY_PTTRN					= "try\\s*\\{.*\\}";

	private static final String		ANY_PTTRN					= ".*";
	private static final String		WHITE_SPC_PTTRN				= "\\s*";
	private static final String		CATCH_PTTRN					= "catch\\s*\\([a-zA-Z]*Exception\\s*[a-zA-Z]*\\)\\s*\\{.*\\}";
	private static final String		FAIL_PTTRN					= "fail\\(.*\\)\\s*;";

	private static final String		negativePattern1			= ANY_PTTRN + TRY_PTTRN + WHITE_SPC_PTTRN + CATCH_PTTRN + WHITE_SPC_PTTRN
																		+ LEFT_PAREN + ANY_PTTRN + FAIL_PTTRN + ANY_PTTRN + RIGHT_PAREN;
	private static Pattern			pattern1;

	private static final String		negativePattern2			= ".*try\\s*\\{.*fail\\(.*\\);\\s*\\}" + WHITE_SPC_PTTRN + CATCH_PTTRN
																		+ ANY_PTTRN;
	private static Pattern			pattern2;
	private static Pattern			catchPattern;

	static {
		try {
			pattern1 = Pattern.compile(negativePattern1, Pattern.DOTALL);
			pattern2 = Pattern.compile(negativePattern2, Pattern.DOTALL);
			catchPattern = Pattern.compile(CATCH_PTTRN, Pattern.DOTALL);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 *
	 * @param type
	 * @return
	 */
	public static boolean isPrimitive(final String type) {
		for (final String primType : primTypes) {
			if (type.equals(primType)) {
				return true;
			}
		}
		return false;
	}

	/**
	 *
	 * @param type
	 * @param variable
	 * @return
	 */
	public static String getAssertForPrimitives(final String type, final String variable) {
		for (final String[] singlePrimAsserts : primAsserts) {
			if (type.equals(singlePrimAsserts[0])) {
				return replacePlaceHolder(singlePrimAsserts[1], "variable", variable);
			}
		}
		return null;
	}

	/**
	 *
	 * @param type
	 * @return
	 */
	public static boolean isJunitTest(final IType type) {
		try {
			final IType[] tmpTypes = type.newSupertypeHierarchy(null).getAllClasses();
			for (final IType tmpType : tmpTypes) {
				if (tmpType.getFullyQualifiedName().equals(TEST_CASE_CLASS)) {
					return true;
				}
			}

			final IImportDeclaration[] imports = type.getCompilationUnit() != null ? type.getCompilationUnit().getImports()
					: new IImportDeclaration[] {};
			for (final IImportDeclaration importDeclaration : imports) {
				final String importName = importDeclaration.getElementName();
				if ("org.junit.Test".equals(importName) || "org.junit.*".equals(importName)) {
					return true;
				} else if ("org.testng.annotations.Test".equals(importName) || "org.testng.annotations.*".equals(importName)) {
					return true;
				}
			}
		} catch (final Exception ex) {
			ex.printStackTrace();
		}

		return false;
	}

	/**
	 *
	 * @param type
	 * @return
	 */
	public static boolean isJunitEnabled(final IType type) {
		try {
			if (type != null && type.exists()) {
				return isJunitEnabled(type.getCompilationUnit().getJavaProject());
			}
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	/**
	 *
	 * @param type
	 * @return
	 */
	public static boolean isJunitEnabled(final IJavaProject javaProject) {
		try {
			final IType testType = javaProject.findType(TEST_CASE_CLASS);
			return testType != null && testType.exists();
		} catch (final Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 *
	 * @param method
	 * @return
	 */
	public static String createTestMethodName(final IMethod method) {
		final JunitPreferences junitPreferences = getInstance(method.getDeclaringType());
		String testMethodName;
		if (junitPreferences.getJunitTestMethod().startsWith(METHOD_PATTERN_DEFAULT)) {
			testMethodName = replacePlaceHolder(junitPreferences.getJunitTestMethod(), METHOD_NAME_STR, method.getElementName());
		} else {
			testMethodName = replacePlaceHolder(junitPreferences.getJunitTestMethod(), METHOD_NAME_STR,
					createEmbeddedInstance(method.getElementName()));
		}
		return testMethodName;
	}

	/**
	 *
	 * @param method
	 * @return
	 */
	public static boolean isNegativeJunit(final IMethod method) {

		try {
			final String source = method.getSource();
			final int off1 = source.indexOf(LEFT_CURL_CHAR);
			final int off2 = source.lastIndexOf(RIGHT_CURL_CHAR);
			final String srcBdy = source.substring(off1 + 1, off2 - 1);

			JUNIT_TYPE junitType = JUNIT_TYPE.JUNIT_TYPE_3;
			final IAnnotation[] annotations = method.getAnnotations();
			for (final IAnnotation annotation : annotations) {
				if (!annotation.getElementName().equals("Test")) {
					continue;
				}
				junitType = JUNIT_TYPE.JUNIT_TYPE_4;
				final IMemberValuePair[] memberValuePairs = annotation.getMemberValuePairs();
				for (final IMemberValuePair memberValuePair : memberValuePairs) {
					final String memberName = memberValuePair.getMemberName();
					if ("expected".equals(memberName) || "expectedExceptions".equals(memberName)) {
						return true;
					}
				}
			}
			if (junitType == JUNIT_TYPE.JUNIT_TYPE_3) {
				if (pattern1 == null || pattern2 == null) {
					return false;
				}
				Matcher matcher = pattern1.matcher(srcBdy);
				if (matcher.matches()) {
					// This is to make sure the fail block is not hiding in
					// another catch.
					final String fail = matcher.group(1);
					// System.out.println("fail " + fail);
					matcher = catchPattern.matcher(fail.trim());
					return !matcher.matches();
				} else {
					matcher = pattern2.matcher(srcBdy);
					return matcher.matches();
				}
			}
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	/**
	 *
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public static JunitPreferencesAndType findJunitPreferences(final IType type) throws Exception {
		JunitPreferences junitPreferences = JunitPreferences.getInstance(type);
		if (junitPreferences != null) {
			return new JunitPreferencesAndType(junitPreferences, type);
		}
		IType typeToWorkOn = type;
		if (type.isClass()) {
			typeToWorkOn = findSuperInterfaceType(type);
			if (typeToWorkOn != null) {
				junitPreferences = JunitPreferences.getInstance(typeToWorkOn);
			}
			return new JunitPreferencesAndType(junitPreferences, typeToWorkOn);
		}
		return null;
	}

	/**
	 *
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public static JunitPreferencesAndType findJunitPreferences(final IType type, final String profileName) throws Exception {
		final JunitPreferences junitPreferences = JunitPreferences.getInstance(profileName);
		if (JunitPreferences.doesMatch(type, junitPreferences.getClassPattern())) {
			return new JunitPreferencesAndType(junitPreferences, type);
		}

		if (type.isClass()) {
			final IType interfaceType = findSuperInterfaceType(type);
			if (interfaceType != null && JunitPreferences.doesMatch(interfaceType, junitPreferences.getClassPattern())) {
				return new JunitPreferencesAndType(junitPreferences, interfaceType);
			}
		}
		return null;
	}

	/**
	 *
	 * @param type
	 * @param junitPreferences1
	 * @return
	 * @throws Exception
	 */
	public static ICompilationUnit findTestUnit(final IType type, JunitPreferences junitPreferences) throws Exception {
		junitPreferences = junitPreferences == null ? JunitPreferences.getInstance(type) : junitPreferences;
		final IJavaProject javaProject = type.getJavaProject();
		// String junitTestLocation =
		// replacePlaceHolder(junitPreferences.getJunitTestLocation(),
		// "project", javaProject.getElementName());
		// junitTestLocation = "/" + junitTestLocation;
		final GlobalSettings globalSettings = GlobalSettings.getInstance();
		String junitTestLocation = globalSettings.isUseDefaultForPath() ? globalSettings.getSourcePathTest() : junitPreferences
				.getJunitTestLocation();
		junitTestLocation = getDefaultPathFromProject(javaProject, "test", junitTestLocation);
		final IPackageFragmentRoot testPackageFragmentRoot = SourceUtil.getPackageRootFromProject(javaProject, junitTestLocation);

		if (testPackageFragmentRoot == null || !testPackageFragmentRoot.exists()) {
			throw new Exception("Path does not exist in the project " + junitTestLocation);
		}

		final String testPkg = type.getPackageFragment().getElementName();

		final IPackageFragment testPackageFragment = testPackageFragmentRoot.getPackageFragment(testPkg);
		if (testPackageFragment == null || !testPackageFragment.exists()) {
			return null;
		}
		IType testType = javaProject.findType(testPkg,
				replacePlaceHolder(junitPreferences.getJunitTestClass(), "class_name", type.getElementName()));

		// If it is not found in the usual place then search all packages.
		if (testType == null || !testType.exists()) {
			if (testPackageFragment.hasChildren()) {
				final IJavaElement[] subPackages = testPackageFragmentRoot.getChildren();
				for (final IJavaElement pack : subPackages) {
					if (!(pack instanceof IPackageFragment)) {
						continue;
					}
					final IPackageFragment fragment = (IPackageFragment) pack;
					testType = javaProject.findType(fragment.getElementName(),
							replacePlaceHolder(junitPreferences.getJunitTestClass(), "class_name", type.getElementName()));
					if (testType != null && testType.exists()) {
						return testType.getCompilationUnit();
					}
				}
			}
		}

		return testType != null && testType.exists() ? testType.getCompilationUnit() : null;
	}

	/**
	 *
	 * @param testMethod
	 * @throws Exception
	 */
	public static IMethod findPossibleMethodForTest(final IMethod testMethod) throws Exception {

		IType type = null;
		final String[][] testProfiles = getTestProfiles();
		String classBeingTested = null;

		for (final String[] testProfile : testProfiles) {

			final JunitPreferences junitPreferences = getInstance(testProfile[0]);
			final IType testType = testMethod.getCompilationUnit().findPrimaryType();
			final String testTypeName = testType.getElementName();
			String remainder = null;

			if (junitPreferences.getJunitTestClass().startsWith("${class_name}")) {
				remainder = junitPreferences.getJunitTestClass().replace("${class_name}", EMPTY_STR);
				final int off = testTypeName.lastIndexOf(remainder);
				classBeingTested = testTypeName.substring(0, off);
			} else if (junitPreferences.getJunitTestClass().endsWith("${class_name}")) {
				remainder = junitPreferences.getJunitTestClass().replace("${class_name}", EMPTY_STR);
				classBeingTested = testTypeName.substring(remainder.length());
			} else {
				return null;
			}

			// classBeingTested = testType.getPackageFragment().getElementName()
			// + "." + classBeingTested;
			final IJavaProject javaProject = testType.getCompilationUnit().getJavaProject();
			type = javaProject.findType(testType.getPackageFragment().getElementName() + DOT + classBeingTested);

			if (type == null || !type.exists()) {
				for (final IPackageFragmentRoot packageFragmentRoot : javaProject.getAllPackageFragmentRoots()) {
					if (!packageFragmentRoot.equals(testType.getPackageFragment().getParent())) {
						for (final IJavaElement packageElement : packageFragmentRoot.getChildren()) {
							final IPackageFragment packageFragment = (IPackageFragment) packageElement;
							type = javaProject.findType(packageFragment.getElementName() + DOT + classBeingTested);
							if (type != null && type.exists()) {
								break;
							}
						}
					}
				}
			}
			if (type != null && type.exists()) {
				break;
			}
		}

		if (type == null || !type.exists()) {
			throw new Exception("Unable to find the class being tested. " + classBeingTested + NEWLINE
					+ "One reason could be you have not configured it. "
					+ "To configure it please go to Window -> Preferences -> Fast Code Preference.");
		}

		final String testMethName = testMethod.getElementName();
		IMethod method = findPossibleMethodForTest(testMethName, type);
		if (method != null) {
			return method;
		}

		final int length = testMethName.length();

		for (int i = length - 1; i > 0; i--) {
			final char ch = testMethName.charAt(i);
			if (Character.isUpperCase(ch) || ch == UNDERSCORE.charAt(0)) {
				final String partialMethName = testMethName.substring(0, i);
				method = findPossibleMethodForTest(partialMethName, type);
				if (method != null) {
					return method;
				}
			}
		}
		return null;
	}

	/**
	 *
	 * @param testMethName
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public static IMethod findPossibleMethodForTest(final String testMethName, final IType type) throws Exception {
		final JunitPreferences junitPreferences = getInstance(type);
		for (final IMethod method : type.getMethods()) {
			String junitTestMethod = junitPreferences.getJunitTestMethod();
			junitTestMethod = createTestMethodName(method);
			if (junitTestMethod.equals(testMethName)) {
				return method;
			}
		}
		return null;
	}

	/**
	 *
	 * @param methInvocation
	 * @param excepType
	 * @param excepVariable
	 * @return
	 * @throws Exception
	 */
	public static String surroundWithTryCatchBlock(final String methInvocation, final IMethod method, final boolean excepTest)
			throws Exception {
		final JunitPreferences junitPreferences = getInstance(method.getDeclaringType());
		return surroundWithTryCatchBlock(methInvocation, excepTest ? junitPreferences.getNegativeBody() : DEFAULT_TRY_CATCH_TEMPLATE,
				method);
	}

	/**
	 *
	 * @param methInvocation
	 * @param tryCatchTemplate
	 * @param excepType
	 * @param excepVariable
	 * @return
	 * @throws Exception
	 */
	public static String surroundWithTryCatchBlock(final String methInvocation, final String tryCatchTemplate, final IMethod method)
			throws Exception {
		String methBody = EMPTY_STR;

		String exceptionType = "Exception";

		final String[] exceptionTypes = method.getExceptionTypes();
		if (exceptionTypes != null && exceptionTypes.length > 0) {
			exceptionType = getSignatureSimpleName(exceptionTypes[0]);
		}
		final String exceptionVariable = createDefaultInstance(exceptionType);

		final JunitPreferences junitPreferences = getInstance(method.getDeclaringType());
		// if (!(methInvocation == null || methInvocation.equals(EMPTY_STR))) {
		// String[] lines = methInvocation.split(NEWLINE);
		// for (String line : lines) {
		// methBody += TAB + TAB + line + NEWLINE;
		// }
		// }

		methBody = replacePlaceHolder(tryCatchTemplate, "method_invocation", methInvocation == null ? EMPTY_STR : methInvocation);
		methBody = replacePlaceHolder(methBody, "exception_type", exceptionType);
		methBody = replacePlaceHolder(methBody, "exception_variable", exceptionVariable);
		methBody = replacePlaceHolder(methBody, "method_name", method.getElementName());
		if (tryCatchTemplate.equals(DEFAULT_TRY_CATCH_TEMPLATE)) {
			final String exceptionBody = makeExceptionBody(junitPreferences.getExceptionBody(), exceptionType, exceptionVariable,
					method.getElementName());
			methBody = replacePlaceHolder(methBody, "exception_body", exceptionBody);
		}

		final String[] lines = methBody.split(NEWLINE);
		methBody = EMPTY_STR;
		int cnt = 0;
		for (final String line : lines) {
			methBody += TAB + line + (cnt < lines.length - 1 ? NEWLINE : EMPTY_STR);
			cnt++;
		}

		// methBody += TAB + "}";

		return methBody;
	}

	/**
	 *
	 * @param body
	 * @param exception
	 * @param method
	 * @return
	 */
	public static String makeExceptionBody(final String body, final String exceptionType, final String exceptionVariable,
			final String methodName) {
		final StringBuilder exceptionBody = new StringBuilder();

		if (body == null || body.equals(EMPTY_STR)) {
			return exceptionBody.toString();
		}
		final String[] lines = body.split(NEWLINE);
		int cnt = 0;
		for (String line : lines) {
			line = replacePlaceHolder(line, "exception_variable", exceptionVariable);
			line = replacePlaceHolder(line, "exception_type", exceptionType);
			line = replacePlaceHolder(line, "method_name", methodName);
			exceptionBody.append(TAB + line + (cnt < lines.length - 1 ? NEWLINE : EMPTY_STR));
			cnt++;
		}
		return exceptionBody.toString();
	}

	/**
	 *
	 * @param preference
	 * @return
	 */
	public static String[] getAnnotationsFromPreference(final String preference) {
		// final IPreferenceStore preferences =
		// Activator.getDefault().getPreferenceStore();
		final IPreferenceStore preferences = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		if (preferences.contains(preference)) {
			final String annotations = preferences.getString(preference);
			if (!annotations.trim().equals(EMPTY_STR)) {
				return annotations.split(NEWLINE);
			}
		}
		return null;
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final String str = "HouseDAOTest";

	}

}
