/**
 *
 */
package org.fastcode.preferences;

import static org.fastcode.common.FastCodeConstants.ASTERISK;
import static org.fastcode.common.FastCodeConstants.COLON;
import static org.fastcode.common.FastCodeConstants.DEFAULT;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.FORWARD_SLASH;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.common.FastCodeConstants.UNDERSCORE;
import static org.fastcode.common.FastCodeConstants.UNDER_SCORE;
import static org.fastcode.preferences.PreferenceConstants.P_BASE_TEST;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_ALL_TEST_PROFILES;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_ALWAYS_CREATE_INSTANCE;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_CLASS_ANNOTATIONS;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_CLASS_IMPORTS;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_CLASS_INSIDE_BODY;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_CREATE_METHOD_BODY;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_EXCEPTION_BODY;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_FIELD_ANNOTATIONS;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_METHOD_ANNOTATIONS;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_NEGATIVE_BODY;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_PROFILE_PATTERN;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_TEST_CLASS;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_TEST_LOCATION;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_TEST_METHOD;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_TEST_PROFILE;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_TEST_PROJECT;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_TYPE;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_TYPE_3;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_TYPE_4;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_TYPE_TESTNG;
import static org.fastcode.util.JUnitUtil.getAnnotationsFromPreference;
import static org.fastcode.util.StringUtil.containsPlaceHolder;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.StringUtil.reverseCamelCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.common.CreateUnitTestData;
import org.fastcode.common.FastCodeConstants.JUNIT_TYPE;

/**
 * @author Gautam
 *
 */
public class JunitPreferences {

	private static final String							PROFILE_DEFAULT		= DEFAULT;
	private static final String							PROFILE_DELIMITER	= COLON;

	//	private static JunitPreferences instance = new JunitPreferences();

	private JUNIT_TYPE									junitType;
	//private String										testFormat;
	private String										testProfile;

	private String										testProject;
	private String										classPattern;

	private String[]									classImports;
	private String										exceptionBody;
	private String										insideBody;

	private String										negativeBody;

	private boolean										createMethodBody;
	//private boolean										alwaysCreateTryCatch;
	private boolean										createInstance;
	private String[]									fieldAnnotations;
	private String[]									classAnnotations;
	private String[]									methodAnnotations;

	private String										junitBaseType;
	private String										junitTestClass;
	private String										junitTestLocation;
	private String										junitTestMethod;
	private boolean										reload				= true;

	final private static Map<String, JunitPreferences>	preferencesMap		= new HashMap<String, JunitPreferences>();

	final private static Map<String, Pattern>			patternMap			= new HashMap<String, Pattern>();
	/*private boolean expTest;
	private List<String> resultFormatList = new ArrayList<String>();
	private HANDLE_EXCEPTION	handleException;
	private String methodReturnType;*/
	private CreateUnitTestData							createUnitTestData;

	/**
	 *
	 */
	//	private JunitPreferences() {
	//		load(null);
	//	}

	/**
	 *
	 * @param profName
	 */
	private JunitPreferences(final String profName) {
		this.reload = true;
		this.load(profName);
	}

	/**
	 * @param profName
	 * @param force
	 */
	public void load(final String profName, final boolean force) {
		if (force) {
			this.reload = true;
			this.load(profName);
		}
	}

	/**
	 *
	 * @param profName
	 *
	 */
	private void load(final String profName) {
		if (!this.reload) {
			return;
		}
		//		final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		final IPreferenceStore preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		final String junitType = preferenceStore.getString(getPreferenceName(P_JUNIT_TYPE, profName));

		if (P_JUNIT_TYPE_3.equals(junitType)) {
			this.junitType = JUNIT_TYPE.JUNIT_TYPE_3;
		} else if (P_JUNIT_TYPE_4.equals(junitType)) {
			this.junitType = JUNIT_TYPE.JUNIT_TYPE_4;
		} else if (P_JUNIT_TYPE_TESTNG.equals(junitType)) {
			this.junitType = JUNIT_TYPE.JUNIT_TYPE_TESTNG;
		}

		this.junitBaseType = preferenceStore.getString(getPreferenceName(P_BASE_TEST, profName));
		this.testProfile = preferenceStore.getString(getPreferenceName(P_JUNIT_TEST_PROFILE, profName));
		this.classPattern = preferenceStore.getString(getPreferenceName(P_JUNIT_PROFILE_PATTERN, profName));
		this.testProject = preferenceStore.getString(getPreferenceName(P_JUNIT_TEST_PROJECT, profName));
		this.junitTestClass = preferenceStore.getString(getPreferenceName(P_JUNIT_TEST_CLASS, profName));
		this.junitTestMethod = preferenceStore.getString(getPreferenceName(P_JUNIT_TEST_METHOD, profName));
		//		this.testFormat = preferences.getString(getPreferenceName(P_JUNIT_TEST_FORMAT, profName));
		this.insideBody = preferenceStore.getString(getPreferenceName(P_JUNIT_CLASS_INSIDE_BODY, profName));
		this.createMethodBody = preferenceStore.getBoolean(getPreferenceName(P_JUNIT_CREATE_METHOD_BODY, profName));
		this.junitTestLocation = preferenceStore.getString(getPreferenceName(P_JUNIT_TEST_LOCATION, profName));
		final String clsImports = preferenceStore.getString(getPreferenceName(P_JUNIT_CLASS_IMPORTS, profName));
		if (!isEmpty(clsImports)) {
			this.classImports = clsImports.split(NEWLINE);
		}
		if (containsPlaceHolder(this.junitTestLocation, "project")) {
			this.junitTestLocation = this.junitTestLocation.replace("${project}", EMPTY_STR);
			if (!this.junitTestLocation.startsWith(FORWARD_SLASH)) {
				this.junitTestLocation = FORWARD_SLASH + this.junitTestLocation;
			}
			preferenceStore.setValue(getPreferenceName(P_JUNIT_TEST_LOCATION, profName), this.junitTestLocation);
		}
		//	this.alwaysCreateTryCatch = preferenceStore.getBoolean(getPreferenceName(P_JUNIT_ALWAYS_CREATE_TRY_CATCH, profName));
		this.createInstance = preferenceStore.getBoolean(getPreferenceName(P_JUNIT_ALWAYS_CREATE_INSTANCE, profName));
		this.fieldAnnotations = getAnnotationsFromPreference(getPreferenceName(P_JUNIT_FIELD_ANNOTATIONS, profName));
		this.exceptionBody = preferenceStore.getString(getPreferenceName(P_JUNIT_EXCEPTION_BODY, profName));
		this.negativeBody = preferenceStore.getString(getPreferenceName(P_JUNIT_NEGATIVE_BODY, profName));

		this.methodAnnotations = getAnnotationsFromPreference(getPreferenceName(P_JUNIT_METHOD_ANNOTATIONS, profName));
		this.classAnnotations = getAnnotationsFromPreference(getPreferenceName(P_JUNIT_CLASS_ANNOTATIONS, profName));

		this.reload = false;
	}

	/**
	 *
	 * @param profName
	 */
	public static void reload(final String profName) {
		if (preferencesMap.containsKey(profName)) {
			preferencesMap.remove(profName);
		}
		final JunitPreferences junitPreferences = new JunitPreferences(profName);
		preferencesMap.put(profName, junitPreferences);
	}

	/**
	 *
	 * @return
	 */
	public static JunitPreferences getInstance() {
		return getInstance(PROFILE_DEFAULT);
	}

	/**
	 *
	 * @param profName
	 * @return
	 */
	public static JunitPreferences getInstance(final String profName) {
		if (!preferencesMap.containsKey(profName)) {
			final JunitPreferences junitPreferences = new JunitPreferences(profName);
			preferencesMap.put(profName, junitPreferences);
		}
		return preferencesMap.get(profName);
	}

	/**
	 *
	 * @param type
	 * @return
	 */
	public static JunitPreferences getInstance(final IType type) {
		final IPreferenceStore preferences = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		//		final IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();

		if (!preferences.contains(P_JUNIT_ALL_TEST_PROFILES)) {
			return null;
		}
		final String profiles = preferences.getString(P_JUNIT_ALL_TEST_PROFILES);
		final String[] profilesArr = profiles.split(COLON);
		for (final String prof : profilesArr) {
			final String profPattrn = preferences.getString(getPreferenceName(P_JUNIT_PROFILE_PATTERN, prof));
			if (doesMatch(type, profPattrn)) {
				final JunitPreferences junitPreferences = getInstance(prof);
				final String tstProjct = junitPreferences.getTestProject();
				if (isEmpty(tstProjct) || type.getJavaProject().getElementName().equals(tstProjct)) {
					return junitPreferences;
				}
			}
		}
		return null;
	}

	/**
	 * @param type
	 * @return
	 */
	public static List<String> getMatchingProfilesForType(final IType type) {
		final IPreferenceStore preferences = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		//		final IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();

		final List<String> matchingProfilesList = new ArrayList<String>();

		if (!preferences.contains(P_JUNIT_ALL_TEST_PROFILES)) {
			return null;
		}
		final String profiles = preferences.getString(P_JUNIT_ALL_TEST_PROFILES);
		final String[] profilesArr = profiles.split(COLON);
		for (final String prof : profilesArr) {
			final String profPattrn = preferences.getString(getPreferenceName(P_JUNIT_PROFILE_PATTERN, prof));
			if (doesMatch(type, profPattrn)) {
				matchingProfilesList.add(prof);
			}
		}
		return matchingProfilesList;
	}

	/**
	 * @return
	 */
	public static String[][] getTestProfiles() {
		final IPreferenceStore preferences = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		//		final IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();
		if (!preferences.contains(P_JUNIT_ALL_TEST_PROFILES)) {
			return new String[][] { { DEFAULT, DEFAULT } };
		}
		final String testProfiles = preferences.getString(P_JUNIT_ALL_TEST_PROFILES);
		final String[] testProfilesArr = testProfiles.split(PROFILE_DELIMITER);
		final String[][] testProfilesToReturn = new String[testProfilesArr.length][2];
		//testProfilesToReturn[0][0] = testProfilesToReturn[0][1] = DEFAULT;
		int count = 0;
		for (final String tstProf : testProfilesArr) {
			testProfilesToReturn[count][0] = testProfilesToReturn[count][1] = tstProf;
			count++;
		}
		return testProfilesToReturn;
	}

	/**
	 *
	 * @param type
	 * @param profPattern
	 * @return
	 */
	public static boolean doesMatch(final IType type, final String profPattern) {
		if (!patternMap.containsKey(profPattern)) {
			final Pattern pattrn = Pattern.compile(profPattern.replace(ASTERISK, ".*"), Pattern.CASE_INSENSITIVE);
			patternMap.put(profPattern, pattrn);
		}

		final Pattern pattern = patternMap.get(profPattern);

		final Matcher matcher = pattern.matcher(type.getFullyQualifiedName());
		return matcher.matches();
	}

	/**
	 *
	 * @param preference
	 * @param profile
	 * @return
	 */
	public static String getPreferenceName(final String preference, final String profileName) {
		return profileName == null || profileName.equals(PROFILE_DEFAULT) ? preference : preference + UNDERSCORE + profileName;
	}

	/**
	 * @return the junit_type
	 */
	public JUNIT_TYPE getJunitType() {
		return this.junitType;
	}

	/**
	 * @return the testFormat
	 */
	/*
	public String getTestFormat() {
	return this.testFormat;
	}*/

	/**
	 * @return the createMethodBody
	 */
	public boolean isCreateMethodBody() {
		return this.createMethodBody;
	}

	/**
	 * @return the alwaysCreateTryCatch
	 */
	/*
	public boolean isAlwaysCreateTryCatch() {
	return this.alwaysCreateTryCatch;
	}
	*/
	/**
	 * @return the createInstance
	 */
	public boolean isCreateInstance() {
		return this.createInstance;
	}

	/**
	 * @return the methodAnnotations
	 */
	public String[] getMethodAnnotations() {
		return this.methodAnnotations;
	}

	/**
	 * @return the junitBaseType
	 */
	public String getJunitBaseType() {
		return this.junitBaseType;
	}

	/**
	 *
	 * @return
	 */
	public String getJunitTestClass() {
		return this.junitTestClass;
	}

	/**
	 *
	 * @return
	 */
	public String getJunitTestMethod() {
		return this.junitTestMethod;
	}

	/**
	 *
	 * @return
	 */
	public String getJunitTestLocation() {
		return this.junitTestLocation;
	}

	/**
	 *
	 * @return
	 */
	public String[] getClassAnnotations() {
		return this.classAnnotations;
	}

	/**
	 *
	 * @return
	 */
	public String getNegativeBody() {
		return this.negativeBody;
	}

	/**
	 *
	 * @return
	 */
	public String getExceptionBody() {
		return this.exceptionBody;
	}

	/**
	 *
	 * @return
	 */
	public String getTestProfile() {
		return this.testProfile;
	}

	/**
	 *
	 * @return
	 */
	public String getClassPattern() {
		return this.classPattern;
	}

	/**
	 * @return the insideBody
	 */
	public String getInsideBody() {
		return this.insideBody;
	}

	/**
	 *
	 * @return
	 */
	public String getTestProject() {
		return this.testProject;
	}

	/**
	 *
	 * @return
	 */
	public String[] getClassImports() {
		return this.classImports;
	}

	public String[] getFieldAnnotations() {
		return this.fieldAnnotations;
	}

	/**
	 *
	 * setter method for expTest
	 * @param expTest
	 *
	 */
	/*
	public void setExpTest(final boolean expTest) {
	this.expTest = expTest;
	}

	*//**
		*
		* getter method for resultFormatList
		* @return
		*
		*/
	/*
	public List<String> getResultFormatList() {
	return this.resultFormatList;
	}

	*//**
		*
		* setter method for resultFormatList
		* @param resultFormatList
		*
		*/
	/*
	public void setResultFormatList(final List<String> resultFormatList) {
	this.resultFormatList = resultFormatList;
	}

	*//**
		* @return the handleExeption
		*/
	/*
	public HANDLE_EXCEPTION getHandleException() {
	return this.handleException;
	}

	*//**
		* @param handleExeption the handleExeption to set
		*/
	/*
	public void setHandleException(final HANDLE_EXCEPTION handleException) {
	this.handleException = handleException;
	}

	*//**
		* @return the expTest
		*/
	/*
	public boolean isExpTest() {
	return this.expTest;
	}

	*//**
		*
		* getter method for methodReturnType
		* @return
		*
		*/
	/*
	public String getMethodReturnType() {
	return this.methodReturnType;
	}

	*//**
		*
		* setter method for methodReturnType
		* @param methodReturnType
		*
		*/
	/*
	public void setMethodReturnType(final String methodReturnType) {
	this.methodReturnType = methodReturnType;
	}
	*/
	/**
	 * @return the createUnitTestData
	 */
	public CreateUnitTestData getCreateUnitTestData() {
		return this.createUnitTestData;
	}

	/**
	 * @param createUnitTestData the createUnitTestData to set
	 */
	public void setCreateUnitTestData(final CreateUnitTestData createUnitTestData) {
		this.createUnitTestData = createUnitTestData;
	}

	public JunitPreferences(final JUNIT_TYPE junitType, final String junitBaseType, final String testProfile, final String testProject,
			final String junitTestClass, final String classPattern, final String junitTestMethod, final String insideBody,
			final boolean createMethodBody, final String junitTestLocation, final String[] classImports, final boolean createInstance,
			final String[] fieldAnnotations, final String exceptionBody, final String negativeBody, final String[] methodAnnotations,
			final String[] classAnnotations, final boolean reload) {
		super();
		this.junitType = junitType;
		this.junitBaseType = junitBaseType;
		this.testProfile = testProfile;
		this.testProject = testProject;
		this.junitTestClass = junitTestClass;
		this.classPattern = classPattern;
		this.junitTestMethod = junitTestMethod;
		this.insideBody = insideBody;
		this.createMethodBody = createMethodBody;
		this.junitTestLocation = junitTestLocation;
		this.classImports = classImports;
		//this.alwaysCreateTryCatch = alwaysCreateTryCatch;
		this.createInstance = createInstance;
		this.fieldAnnotations = fieldAnnotations;
		this.exceptionBody = exceptionBody;
		this.negativeBody = negativeBody;
		this.methodAnnotations = methodAnnotations;
		this.classAnnotations = classAnnotations;

		this.reload = reload;

	}

	/**
	 * @param junitPreferences
	 * @param profileName
	 */
	public void settingToPreferenceStore(final JunitPreferences junitPreferences, final String profileName) {
		final IPreferenceStore preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		final String junitype = reverseCamelCase(junitPreferences.junitType.toString(), UNDER_SCORE);
		if (P_JUNIT_TYPE_3.replaceAll("\\s*", EMPTY_STR).equals(junitype)) {
			preferenceStore.setValue(getPreferenceName(P_JUNIT_TYPE, profileName), P_JUNIT_TYPE_3);
		} else if (P_JUNIT_TYPE_4.replaceAll("\\s*", EMPTY_STR).equals(junitype)) {
			preferenceStore.setValue(getPreferenceName(P_JUNIT_TYPE, profileName), P_JUNIT_TYPE_4);
		} else if (P_JUNIT_TYPE_TESTNG.replaceAll("\\s*", EMPTY_STR).equals(junitype)) {
			preferenceStore.setValue(getPreferenceName(P_JUNIT_TYPE, profileName), P_JUNIT_TYPE_TESTNG);
		}
		preferenceStore.setValue(getPreferenceName(P_BASE_TEST, profileName), junitPreferences.junitBaseType);
		/*final String allTestProfiles = preferenceStore.getString(P_JUNIT_ALL_TEST_PROFILES);
		String[] profilesArr = null;
		profilesArr = allTestProfiles.split(PROFILE_DELIMITER);
		String nextProfile = null;
		StringBuilder newAllTestProfiles = new StringBuilder();
		int count = 0;
		for (final String profile : profilesArr) {
			if (profile.equals(junitPreferences.testProfile)) {
				nextProfile = count < profilesArr.length  - 1 ? profilesArr[count+1] : profilesArr[0];
			} else {
				newAllTestProfiles.append(profile + (count < profilesArr.length  - 1 ? PROFILE_DELIMITER : EMPTY_STR));
			}
			count++;
		}
		if (newAllTestProfiles.toString().endsWith(PROFILE_DELIMITER)) {
			newAllTestProfiles = new StringBuilder(newAllTestProfiles.substring(0, newAllTestProfiles.length() - 1));
		}
		preferenceStore.setValue(getPreferenceName(P_JUNIT_TEST_PROFILE,profileName), nextProfile);
		preferenceStore.setValue(getPreferenceName(P_JUNIT_ALL_TEST_PROFILES, profileName), newAllTestProfiles.toString());*/
		preferenceStore.setValue(getPreferenceName(P_JUNIT_TEST_PROFILE, profileName), junitPreferences.testProfile);
		preferenceStore.setValue(getPreferenceName(P_JUNIT_TEST_PROJECT, profileName), junitPreferences.testProject);
		preferenceStore.setValue(getPreferenceName(P_JUNIT_TEST_CLASS, profileName), junitPreferences.junitTestClass);
		preferenceStore.setValue(getPreferenceName(P_JUNIT_PROFILE_PATTERN, profileName), junitPreferences.classPattern);
		preferenceStore.setValue(getPreferenceName(P_JUNIT_TEST_METHOD, profileName), junitPreferences.junitTestMethod);
		preferenceStore.setValue(getPreferenceName(P_JUNIT_CLASS_INSIDE_BODY, profileName), junitPreferences.insideBody);
		preferenceStore.setValue(getPreferenceName(P_JUNIT_CREATE_METHOD_BODY, profileName), junitPreferences.createMethodBody);
		preferenceStore.setValue(getPreferenceName(P_JUNIT_TEST_LOCATION, profileName), junitPreferences.junitTestLocation);
		final String classToImport = junitPreferences.classImports != null && junitPreferences.classImports.length > 0 ? convertStringArrayToString(junitPreferences.classImports)
				: EMPTY_STR;
		preferenceStore.setValue(getPreferenceName(P_JUNIT_CLASS_IMPORTS, profileName), classToImport);
		/*final StringBuffer classImportBuffer = new StringBuffer();
			for (final String clasImport : junitPreferences.classImports) {
				classImportBuffer.append(clasImport);
				classImportBuffer.append(NEWLINE);
			}
			final String classImprts = classImportBuffer.toString();
			preferenceStore.setValue(getPreferenceName(P_JUNIT_CLASS_IMPORTS, profileName), classImprts);*/
		//preferenceStore.setValue(getPreferenceName(P_JUNIT_ALWAYS_CREATE_TRY_CATCH, profileName), junitPreferences.alwaysCreateTryCatch);
		preferenceStore.setValue(getPreferenceName(P_JUNIT_ALWAYS_CREATE_INSTANCE, profileName), junitPreferences.createInstance);
		/*final StringBuffer fieldAnnotationBuffer = new StringBuffer();
		for (final String fieldAnnotation : junitPreferences.fieldAnnotations) {
			fieldAnnotationBuffer.append(fieldAnnotation);
			fieldAnnotationBuffer.append(NEWLINE);
		}
		final String fieldAnnotations = fieldAnnotationBuffer.toString();*/

		final String fieldAnnotations = junitPreferences.fieldAnnotations != null && junitPreferences.fieldAnnotations.length > 0 ? convertStringArrayToString(junitPreferences.fieldAnnotations)
				: EMPTY_STR;
		preferenceStore.setValue(getPreferenceName(P_JUNIT_FIELD_ANNOTATIONS, profileName), fieldAnnotations);

		preferenceStore.setValue(getPreferenceName(P_JUNIT_EXCEPTION_BODY, profileName), junitPreferences.exceptionBody);
		preferenceStore.setValue(getPreferenceName(P_JUNIT_NEGATIVE_BODY, profileName), junitPreferences.negativeBody);
		/*final StringBuffer methodAnnotationBuffer = new StringBuffer();
		for (final String methodAnnotation : junitPreferences.methodAnnotations) {
			methodAnnotationBuffer.append(methodAnnotation);
			methodAnnotationBuffer.append(NEWLINE);
		}
		final String methdAnotations = methodAnnotationBuffer.toString();*/

		final String methdAnotations = junitPreferences.methodAnnotations != null && junitPreferences.methodAnnotations.length > 0 ? convertStringArrayToString(junitPreferences.methodAnnotations)
				: EMPTY_STR;
		preferenceStore.setValue(getPreferenceName(P_JUNIT_METHOD_ANNOTATIONS, profileName), methdAnotations);
		/*final StringBuffer classAnnotationBuffer = new StringBuffer();
		for (final String clasAnnotation : junitPreferences.classAnnotations) {
			classAnnotationBuffer.append(clasAnnotation);
			classAnnotationBuffer.append(NEWLINE);
		}
		final String clasAnnotations = classAnnotationBuffer.toString();*/
		final String clasAnnotations = junitPreferences.classAnnotations != null && junitPreferences.classAnnotations.length > 0 ? convertStringArrayToString(junitPreferences.classAnnotations)
				: EMPTY_STR;
		preferenceStore.setValue(getPreferenceName(P_JUNIT_CLASS_ANNOTATIONS, profileName), clasAnnotations);

	}

	/**
	 * @param stringArray
	 * @return
	 */
	private String convertStringArrayToString(final String[] stringArray) {
		if (stringArray != null) {
			final StringBuffer stringBuffer = new StringBuffer();
			for (final String value : stringArray) {
				if (value != null) {
					stringBuffer.append(value);
					stringBuffer.append(NEWLINE);
				}

			}
			return stringBuffer.toString();
		}

		return null;
	}

}
