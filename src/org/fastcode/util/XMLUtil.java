package org.fastcode.util;

import static org.fastcode.common.FastCodeConstants.CLASS_ANNOTATION_TAG;
import static org.fastcode.common.FastCodeConstants.CLASS_IMPORT_TAG;
import static org.fastcode.common.FastCodeConstants.COLON;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.FIELD_ANNOTATION_TAG;
import static org.fastcode.common.FastCodeConstants.FORWARD_SLASH;
import static org.fastcode.common.FastCodeConstants.METHOD_ANNOTATION_TAG;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.common.FastCodeConstants.TAB;
import static org.fastcode.preferences.JunitPreferences.getPreferenceName;
import static org.fastcode.preferences.PreferenceConstants.P_BASE_TEST;
import static org.fastcode.preferences.PreferenceConstants.P_EXPORT_SETTINGS;
import static org.fastcode.preferences.PreferenceConstants.P_GETTER_CUSTOM_FORMAT;
import static org.fastcode.preferences.PreferenceConstants.P_GETTER_SETTER_FORMAT;
import static org.fastcode.preferences.PreferenceConstants.P_GETTER_SETTER_POSITION;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_PROPS;
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
import static org.fastcode.preferences.PreferenceConstants.P_SETTER_CUSTOM_FORMAT;
import static org.fastcode.preferences.PreferenceConstants.P_SETTER_VAR_PREFIX;
import static org.fastcode.preferences.PreferenceConstants.P_VARIABLE_ANNOTATION;
import static org.fastcode.util.JUnitUtil.getAnnotationsFromPreference;
import static org.fastcode.util.SourceUtil.backUpExistingExportFile;
import static org.fastcode.util.SourceUtil.checkForErrors;
import static org.fastcode.util.SourceUtil.isFileSaved;
import static org.fastcode.util.StringUtil.changeFirstLetterToLowerCase;
import static org.fastcode.util.StringUtil.containsPlaceHolder;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.TemplateUtil.findOrcreateTemplate;
import static org.fastcode.util.TemplateUtil.formatCDATA;
import static org.fastcode.util.TemplateUtil.getAllTemplates;
import static org.fastcode.util.TemplateUtil.updateTemplateStore;
import static org.w3c.dom.Node.ELEMENT_NODE;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.Activator;
import org.fastcode.common.FastCodeConstants.EXPORT_OPTIONS;
import org.fastcode.common.FastCodeConstants.GETTER_SETTER_FORMAT;
import org.fastcode.common.FastCodeConstants.GETTER_SETTER_POSITION;
import org.fastcode.common.FastCodeConstants.JUNIT_TYPE;
import org.fastcode.common.FastCodeConstants.TEMPLATE_PREFERENCE_NAME;
import org.fastcode.preferences.JunitPreferences;
import org.fastcode.preferences.TemplateValidator;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLUtil {

	private static final int	ALL	= 0;
	private static Properties	properties;
	private static JUNIT_TYPE	junitType;
	private static String[]		classImports;
	private static String[]		classAnnotations;
	private static String[]		methodAnnotations;
	private static String[]		fieldAnnotations;
	private static String		junitTestLocation;
	private static String		profile;

	/**
	 * @param fileName
	 * @param templatePreferenceName
	 * @throws Exception
	 */
	public static void importXML(final String fileName, final String templatePreferenceName, final String folderName) throws Exception {
		final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		// final String templatePrefix = null;

		final IFile file = findOrcreateTemplate(fileName, folderName);
		boolean validation = true;
		if (file == null || !file.exists()) {
			throw new Exception("Template File \"" + fileName + "\" does not exist, please export and try again.");
		}
		processXML(file);
		if (checkForErrors(file)) {
			throw new Exception("Template File \"" + fileName + "\" has some errors, please fix them try again.");
		}

		final InputStream inputStream = file.getContents();
		if (inputStream == null || inputStream.available() == 0) {
			return;
		}
		try {
			if (inputStream.available() < 100) {
				throw new Exception("File " + fileName + " is too small. Please check the file and try again.");
			}

			final TemplateValidator tv = new TemplateValidator();
			if (fileName.equals("UnitTestPreferences.xml")) {

				updateUnitTestPreferenceStore(inputStream);
			} else if (fileName.equals("CreateVariablePreferences.xml")) {
				updateCreateVariablePreferenceStore(store, inputStream);
			} else {
				if (templatePreferenceName.equals(P_GLOBAL_PROPS)) {
					InputStream input = null;
					final String propertiesFile = "fast-code.properties";

					try {
						input = FileLocator.openStream(Activator.getDefault().getBundle(), new Path("resources/" + propertiesFile), false);
						properties = new Properties();
						properties.load(input);
					} catch (final Exception ex) {
						ex.printStackTrace();
					}
				} else {
					validation = tv.validateTemplate(file.getContents(), templatePreferenceName);
					if (validation) {
						updateTemplateStore(store, inputStream, TEMPLATE_PREFERENCE_NAME.getTemplatePrefix(templatePreferenceName),
								templatePreferenceName, false);
					}
				}
			}
			if (validation) {
				MessageDialog.openInformation(new Shell(), "Success", "Import was successfully completed.");
			}
		} finally {
			FastCodeUtil.closeInputStream(inputStream);
		}
	}

	/**
	 * @param fileName
	 * @param templatePreferenceName
	 * @throws Exception
	 */
	public static void exportXML(final String fileName, final String templatePreferenceName, final String folderName) throws Exception {
		// final String templatePrefix = null;
		boolean exported = false;
		final IFile file = findOrcreateTemplate(fileName, folderName);

		processXML(file);
		String templateBuffer = null;
		if (fileName.equals("UnitTestPreferences.xml")) {
			templateBuffer = getAllJunitPreference();
		} else if (fileName.equals("CreateVariablePreferences.xml")) {
			templateBuffer = getAllVariablePrefences();
		} else {
			templateBuffer = getAllTemplates(templatePreferenceName, TEMPLATE_PREFERENCE_NAME.getTemplatePrefix(templatePreferenceName));

		}
		if (file == null) {
			throw new Exception("Unknown Exception : Please try again.");
		}
		final InputStream inputStream = new ByteArrayInputStream(templateBuffer.getBytes());

		try {
			if (file.exists()) {
				final IPreferenceStore preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
				final String exportOption = preferenceStore.getString(P_EXPORT_SETTINGS);
				String folderPath = "resources" + FORWARD_SLASH + folderName;
				// final boolean backup =
				// exportOption.equals(EXPORT_OPTIONS.BACKUP.getValue());
				if (exportOption.equals(EXPORT_OPTIONS.ASK_TO_OVERWRITE_OR_BACKUP.getValue())) {
					final MessageDialog exportMessageDialog = new MessageDialog(
							null,
							"Overwrite File",
							null,
							file.getName()
									+ " is already exported, Would you like to create a back up before it overwrite?\n If No, no back up of existing file will be created.",
							MessageDialog.QUESTION, new String[] { "Yes", "No" }, 0) {
						@Override
						protected void buttonPressed(final int buttonId) {
							setReturnCode(buttonId);
							close();

						}
					};
					exportMessageDialog.open();

					if (exportMessageDialog.getReturnCode() != -1) {
						if (exportMessageDialog.getReturnCode() == 0) {
							backUpExistingExportFile(file, fileName, folderPath);
						}
						file.setContents(inputStream, false, true, new NullProgressMonitor());
						exported = true;
					}
				} else if (exportOption.equals(EXPORT_OPTIONS.BACKUP.getValue())) {
					backUpExistingExportFile(file, fileName, folderPath);
					file.setContents(inputStream, false, true, new NullProgressMonitor());
					exported = true;
				} else {
					file.setContents(inputStream, false, true, new NullProgressMonitor());
					exported = true;
				}

			} else {
				file.create(inputStream, false, new NullProgressMonitor());
				exported = true;
			}
			/*if (isFileOpenInEditor) {
				final IWorkbench wb = PlatformUI.getWorkbench();
				final IWorkbenchPage page = wb.getActiveWorkbenchWindow().getActivePage();
				IDE.openEditor(page, file);
			}*/
			if (exported) {
				MessageDialog.openInformation(new Shell(), "Success", "Export was successfully done to Fast Code Eclipse Plugin/"
						+ folderName + " folder.");
			}
		} catch (final Exception ex) {
			ex.printStackTrace();
			throw new Exception("Template could not be saved " + ex.getMessage(), ex);
		} finally {
			FastCodeUtil.closeInputStream(inputStream);
		}
	}

	/**
	 * @param fileName
	 * @param templatePreferenceName
	 * @throws Exception
	 */
	public static void processXML(final IFile file) throws Exception {

		//final IFile file = findOrcreateTemplate(fileName, folderName);

		if (!file.isSynchronized(ALL)) {
			throw new Exception(file.getName() + " is not Synchronized, please refresh and try again.");
		}

		if (!isFileSaved(file.getName(), file)) {
			throw new Exception(file.getName() + "  is not saved, please save and try again");
		}

	}

	/**
	 * @return
	 */
	private static String getAllVariablePrefences() {
		final IPreferenceStore preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		final StringBuilder variablePrefBuffer = new StringBuilder();
		final String TAB2 = TAB + TAB;
		variablePrefBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		variablePrefBuffer.append("<Variable-Preferences>\n");
		variablePrefBuffer.append(makeVariableNode("getter-setter-format", P_GETTER_SETTER_FORMAT, false, TAB2) + NEWLINE);
		variablePrefBuffer.append(makeVariableNode("getter-setter-position", P_GETTER_SETTER_POSITION, false, TAB2) + NEWLINE);
		variablePrefBuffer.append(makeVariableNode("setter-var-prefix", P_SETTER_VAR_PREFIX, false, TAB2) + NEWLINE);
		variablePrefBuffer.append(makeVariableNode("getter-custom-format", P_GETTER_CUSTOM_FORMAT, true, TAB2) + NEWLINE);
		variablePrefBuffer.append(makeVariableNode("setter-custom-format", P_SETTER_CUSTOM_FORMAT, true, TAB2) + NEWLINE);
		variablePrefBuffer.append(TAB2 + "<variable-annotations>" + NEWLINE);
		final String varAnnotations = preferenceStore.getString(P_VARIABLE_ANNOTATION);
		String[] variableAnnotations = null;
		if (varAnnotations != null) {
			variableAnnotations = varAnnotations.split(NEWLINE);
		}
		if (variableAnnotations != null) {
			for (final String annotation : variableAnnotations) {
				variablePrefBuffer.append(TAB2 + makeNode("variable-annotation", annotation, TAB2) + NEWLINE);
			}
		}
		variablePrefBuffer.append(TAB2 + "</variable-annotations>" + NEWLINE);
		variablePrefBuffer.append("</Variable-Preferences>\n");
		return variablePrefBuffer.toString();
	}

	/**
	 * @param store
	 * @param variablePrefStream
	 */
	private static void updateCreateVariablePreferenceStore(final IPreferenceStore store, final InputStream variablePrefStream) {
		GETTER_SETTER_FORMAT getterSetterFormat = null;
		String setterVarPrefix = null;
		final List<String> variableAnnotationList = new ArrayList<String>();
		String variableAnnotations = null;
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			final DocumentBuilder docBuilder = factory.newDocumentBuilder();
			final Document document = docBuilder.parse(variablePrefStream);
			final NodeList parentNodeList = document.getElementsByTagName("Variable-Preferences");

			for (int k = 0; k <= parentNodeList.getLength(); k++) {
				Node node = parentNodeList.item(k);
				while (node != null) {
					if (node.getNodeType() != ELEMENT_NODE) {
						node = node.getNextSibling();
						continue;
					}
					if ("Variable-Preferences".equals(node.getNodeName())) {
						/*
						 * final NamedNodeMap attributes = node.getAttributes();
						 * if (attributes == null || attributes.getLength() ==
						 * 0) { continue; }
						 */

						Node childNode = node.getFirstChild();
						while (childNode != null) {
							if (childNode.getNodeType() != ELEMENT_NODE) {
								childNode = childNode.getNextSibling();
								continue;
							}
							final String nodeName = childNode.getNodeName();
							final String nodeContent = childNode.getTextContent().trim();
							if (nodeName.equals("getter-setter-format")) {
								getterSetterFormat = GETTER_SETTER_FORMAT.getGetterSetterFormat(nodeContent);
								store.setValue(P_GETTER_SETTER_FORMAT, getterSetterFormat.getValue());
							} else if (nodeName.equals("getter-setter-position")) {
								store.setValue(P_GETTER_SETTER_POSITION, GETTER_SETTER_POSITION.getGetterSetterPosition(nodeContent)
										.getValue());
							} else if (nodeName.equals("setter-var-prefix")) {
								setterVarPrefix = nodeContent;
								store.setValue(P_SETTER_VAR_PREFIX, setterVarPrefix);
							} else if (nodeName.equals("getter-custom-format")) {
								store.setValue(P_GETTER_CUSTOM_FORMAT, nodeContent);
							} else if (nodeName.equals("setter-custom-format")) {
								store.setValue(P_SETTER_CUSTOM_FORMAT, nodeContent);
							} else if (nodeName.equals("variable-annotations")) {

								Node nodeVariableAnnotation = childNode.getFirstChild();
								while (nodeVariableAnnotation != null) {
									if (nodeVariableAnnotation.getNodeType() != ELEMENT_NODE) {
										nodeVariableAnnotation = nodeVariableAnnotation.getNextSibling();
										continue;
									}

									variableAnnotationList.add(nodeVariableAnnotation.getTextContent());
									nodeVariableAnnotation = nodeVariableAnnotation.getNextSibling();

								}
								final StringBuffer variableAnnotationsBuffer = new StringBuffer();
								for (final String annot : variableAnnotationList) {
									variableAnnotationsBuffer.append(annot);
									variableAnnotationsBuffer.append(NEWLINE);
								}
								variableAnnotations = variableAnnotationsBuffer.toString();
								if (variableAnnotations != null && !isEmpty(variableAnnotations)) {
									store.setValue(P_VARIABLE_ANNOTATION, variableAnnotations);
								} else {
									store.setValue(P_VARIABLE_ANNOTATION, EMPTY_STR);
								}
							}
							childNode = childNode.getNextSibling();
						}

					}
					node = node.getNextSibling();
				}
			}

		} catch (final Exception ex) {
			ex.printStackTrace();
			// MessageDialog.openError(new Shell(), "Error",
			// "There are some error:" + ex.getMessage());
			try {
				throw new Exception(ex.getMessage(), ex);

			} catch (final Exception ex1) {
				ex1.printStackTrace();
			}

		} finally {
			FastCodeUtil.closeInputStream(variablePrefStream);
		}
	}

	/**
	 * @return
	 */
	private static String getAllJunitPreference() {

		final IPreferenceStore preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		final StringBuilder unitTestPrefBuffer = new StringBuilder();
		final String TAB2 = TAB + TAB;
		unitTestPrefBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		unitTestPrefBuffer.append("<UnitTest-Preferences>\n");
		if (!preferenceStore.contains(P_JUNIT_ALL_TEST_PROFILES)) {
			return null;
		}

		final String profiles = preferenceStore.getString(P_JUNIT_ALL_TEST_PROFILES);
		final String[] profilesArr = profiles.split(COLON);
		if (profilesArr != null) {
			for (final String prof : profilesArr) {
				final String jUnitType = preferenceStore.getString(P_JUNIT_TYPE);
				if (P_JUNIT_TYPE_3.equals(jUnitType)) {
					junitType = JUNIT_TYPE.JUNIT_TYPE_3;
				} else if (P_JUNIT_TYPE_4.equals(jUnitType)) {
					junitType = JUNIT_TYPE.JUNIT_TYPE_4;
				} else if (P_JUNIT_TYPE_TESTNG.equals(jUnitType)) {
					junitType = JUNIT_TYPE.JUNIT_TYPE_TESTNG;
				}
				unitTestPrefBuffer.append(TAB + "<unitTest profile=\"" + prof + "\">\n");
				unitTestPrefBuffer.append(makeNode(changeFirstLetterToLowerCase(P_JUNIT_TYPE.replaceAll("\\s*", EMPTY_STR)), P_JUNIT_TYPE,
						prof, TAB2, false, String.class) + NEWLINE);
				unitTestPrefBuffer.append(makeNode(changeFirstLetterToLowerCase(P_BASE_TEST.replaceAll("\\s*", EMPTY_STR)), P_BASE_TEST,
						prof, TAB2, false, String.class) + NEWLINE);
				unitTestPrefBuffer.append(makeNode(changeFirstLetterToLowerCase(P_JUNIT_TEST_PROFILE.replaceAll("\\s*", EMPTY_STR)), prof,
						TAB2) + NEWLINE);
				unitTestPrefBuffer.append(makeNode(P_JUNIT_TEST_PROJECT.replaceAll("\\s*", EMPTY_STR), P_JUNIT_TEST_PROJECT, prof, TAB2,
						false, String.class) + NEWLINE);
				unitTestPrefBuffer.append(makeNode(changeFirstLetterToLowerCase(P_JUNIT_TEST_CLASS.replaceAll("\\s*", EMPTY_STR)),
						P_JUNIT_TEST_CLASS, prof, TAB2, false, String.class) + NEWLINE);
				//unitTestPrefBuffer.append(this.makeNode(P_JUNIT_PROFILE_NAME.replaceAll("\\s*", EMPTY_STR), P_JUNIT_PROFILE_NAME, prof, TAB2,false, String.class)+NEWLINE);
				unitTestPrefBuffer.append(makeNode(P_JUNIT_PROFILE_PATTERN.replaceAll("\\s*", EMPTY_STR), P_JUNIT_PROFILE_PATTERN, prof,
						TAB2, false, String.class) + NEWLINE);
				unitTestPrefBuffer.append(makeNode(changeFirstLetterToLowerCase(P_JUNIT_TEST_METHOD.replaceAll("\\s*", EMPTY_STR)),
						P_JUNIT_TEST_METHOD, prof, TAB2, false, String.class) + NEWLINE);
				unitTestPrefBuffer.append(makeNode(P_JUNIT_CLASS_INSIDE_BODY.replaceAll("\\s*", EMPTY_STR), P_JUNIT_CLASS_INSIDE_BODY,
						prof, TAB2, false, String.class) + NEWLINE);
				unitTestPrefBuffer.append(makeNode(P_JUNIT_CREATE_METHOD_BODY.replaceAll("\\s*", EMPTY_STR), P_JUNIT_CREATE_METHOD_BODY,
						prof, TAB2, false, Boolean.class) + NEWLINE);
				junitTestLocation = preferenceStore.getString(getPreferenceName(P_JUNIT_TEST_LOCATION, prof));
				unitTestPrefBuffer.append(makeNode(P_JUNIT_TEST_LOCATION.replaceAll("\\s*", EMPTY_STR), P_JUNIT_TEST_LOCATION, prof, TAB2,
						false, String.class) + NEWLINE);
				unitTestPrefBuffer.append(TAB2 + "<class-imports>" + NEWLINE);
				final String clsImports = preferenceStore.getString(getPreferenceName(P_JUNIT_CLASS_IMPORTS, prof));
				if (!isEmpty(clsImports)) {
					classImports = clsImports.split(NEWLINE);
				}

				if (classImports != null) {
					for (final String classImport : classImports) {
						unitTestPrefBuffer.append(TAB + makeNode(CLASS_IMPORT_TAG, classImport, TAB2) + NEWLINE);
					}
				}

				unitTestPrefBuffer.append(TAB2 + "</class-imports>" + NEWLINE);
				if (containsPlaceHolder(junitTestLocation, "project")) {
					junitTestLocation = junitTestLocation.replace("${project}", EMPTY_STR);
					if (!junitTestLocation.startsWith("/")) {
						junitTestLocation = "/" + junitTestLocation;
					}
					preferenceStore.setValue(P_JUNIT_TEST_LOCATION, junitTestLocation);
				}

				//unitTestPrefBuffer.append(makeNode(P_JUNIT_ALWAYS_CREATE_TRY_CATCH.replaceAll("\\s*", EMPTY_STR), P_JUNIT_ALWAYS_CREATE_TRY_CATCH, prof, TAB2,false, Boolean.class) + NEWLINE);
				unitTestPrefBuffer.append(makeNode(P_JUNIT_ALWAYS_CREATE_INSTANCE.replaceAll("\\s*", EMPTY_STR),
						P_JUNIT_ALWAYS_CREATE_INSTANCE, prof, TAB2, false, Boolean.class) + NEWLINE);
				unitTestPrefBuffer.append(makeNode(P_JUNIT_EXCEPTION_BODY.replaceAll("\\s*", EMPTY_STR), P_JUNIT_EXCEPTION_BODY, prof,
						TAB2, true, String.class) + NEWLINE);
				unitTestPrefBuffer.append(makeNode(P_JUNIT_NEGATIVE_BODY.replaceAll("\\s*", EMPTY_STR), P_JUNIT_NEGATIVE_BODY, prof, TAB2,
						true, String.class) + NEWLINE);
				unitTestPrefBuffer.append(TAB2 + "<method-annotations>" + NEWLINE);
				methodAnnotations = getAnnotationsFromPreference(getPreferenceName(P_JUNIT_METHOD_ANNOTATIONS, prof));
				if (methodAnnotations != null) {
					for (final String methodAnnotation : methodAnnotations) {
						unitTestPrefBuffer.append(TAB + makeNode(METHOD_ANNOTATION_TAG, methodAnnotation, TAB2) + NEWLINE);
					}
				}
				unitTestPrefBuffer.append(TAB2 + "</method-annotations>" + NEWLINE);
				unitTestPrefBuffer.append(TAB2 + "<class-annotations>" + NEWLINE);
				classAnnotations = getAnnotationsFromPreference(getPreferenceName(P_JUNIT_CLASS_ANNOTATIONS, prof));
				if (classAnnotations != null) {
					for (final String classAnnotation : classAnnotations) {
						unitTestPrefBuffer.append(TAB + makeNode(CLASS_ANNOTATION_TAG, classAnnotation, TAB2) + NEWLINE);
					}
				}
				unitTestPrefBuffer.append(TAB2 + "</class-annotations>" + NEWLINE);
				unitTestPrefBuffer.append(TAB2 + "<field-annotations>" + NEWLINE);
				fieldAnnotations = getAnnotationsFromPreference(getPreferenceName(P_JUNIT_FIELD_ANNOTATIONS, prof));
				if (fieldAnnotations != null) {
					for (final String fieldAnnotation : fieldAnnotations) {
						unitTestPrefBuffer.append(TAB + makeNode(FIELD_ANNOTATION_TAG, fieldAnnotation, TAB2) + NEWLINE);
					}
				}
				unitTestPrefBuffer.append(TAB2 + "</field-annotations>" + NEWLINE);
				unitTestPrefBuffer.append("</unitTest>" + NEWLINE);
			}
		}
		unitTestPrefBuffer.append("</UnitTest-Preferences>" + NEWLINE);

		return unitTestPrefBuffer.toString();

	}

	/**
	 * @param unitTestFileStream
	 */
	private static void updateUnitTestPreferenceStore(final InputStream unitTestFileStream) {
		// final List<JunitPreferences> junitPreferences = new
		// ArrayList<JunitPreferences>();
		final Map<String, List<JunitPreferences>> unitTestMap = new LinkedHashMap<String, List<JunitPreferences>>();

		// JunitPreferences preferences=new JunitPreferences(profileName);
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			final DocumentBuilder docBuilder = factory.newDocumentBuilder();
			final Document document = docBuilder.parse(unitTestFileStream);
			final NodeList parentNodeList = document.getElementsByTagName("UnitTest-Preferences");
			final NodeList unitTestNodeList = document.getElementsByTagName("unitTest");

			for (int k = 0; k <= unitTestNodeList.getLength(); k++) {
				Node node = unitTestNodeList.item(k);
				while (node != null) {
					if (node.getNodeType() != ELEMENT_NODE) {
						node = node.getNextSibling();
						continue;
					}
					if ("unitTest".equals(node.getNodeName())) {
						final NamedNodeMap attributes = node.getAttributes();
						if (attributes == null || attributes.getLength() == 0) {
							continue;
						}

						for (int i = 0; i < attributes.getLength(); i++) {
							if ("profile".equalsIgnoreCase(attributes.item(i).getNodeName())) {
								profile = attributes.item(i).getNodeValue();
							}
						}

					}
					unitTestMap.put(profile, getAllUnitTestTags(node, profile));
					node = node.getNextSibling();
				}
			}
		} catch (final Exception ex) {
			ex.printStackTrace();
			// MessageDialog.openError(new Shell(), "Error",
			// "There are some error:" + ex.getMessage());
			try {
				throw new Exception(ex.getMessage(), ex);
			} catch (final Exception ex1) {
				ex1.printStackTrace();
			}
		} finally {
			FastCodeUtil.closeInputStream(unitTestFileStream);
		}
	}

	/**
	 * @param unitTestNode
	 * @param profile
	 * @return
	 */
	private static List<JunitPreferences> getAllUnitTestTags(final Node unitTestNode, final String profile) {
		final List<JunitPreferences> junitPreferences = new ArrayList<JunitPreferences>();
		JunitPreferences preferences = null;
		JUNIT_TYPE junitType = null;
		String junitBaseType = null;
		String testProfile = null;
		String testProject = null;
		String junitTestClass = null;
		String classPattern = null;
		String junitTestMethod = null;
		String insideBody = null;
		boolean createMethodBody = false;
		String junitTestLocation = null;
		String[] classImports = null;
		// boolean alwaysCreateTryCatch = false;
		boolean createInstance = false;
		String exceptionBody = null;
		String negativeBody = null;
		String[] methodAnnotations = null;
		String[] classAnnotations = null;
		String[] fieldAnnotations = null;
		final boolean reload = true;

		Node node = unitTestNode.getFirstChild();
		while (node != null) {
			if (node.getNodeType() != ELEMENT_NODE) {
				node = node.getNextSibling();
				continue;
			}
			final String nodeName = node.getNodeName();
			final String nodeContent = node.getTextContent().trim().replaceAll(TAB, EMPTY_STR);
			if (nodeName.equals("junitType")) {
				if (P_JUNIT_TYPE_3.equals(nodeContent)) {
					junitType = JUNIT_TYPE.JUNIT_TYPE_3;
				} else if (P_JUNIT_TYPE_4.equals(nodeContent)) {
					junitType = JUNIT_TYPE.JUNIT_TYPE_4;
				} else if (P_JUNIT_TYPE_TESTNG.equals(nodeContent)) {
					junitType = JUNIT_TYPE.JUNIT_TYPE_TESTNG;
				}
			} else if (nodeName.equals("junitBaseType")) {
				junitBaseType = nodeContent;
			} else if (nodeName.equals("junitTestProfile")) {
				testProfile = nodeContent;
			} else if (nodeName.equals("junitTestProject")) {
				testProject = nodeContent;
			} else if (nodeName.equals("junitTestClass")) {
				junitTestClass = nodeContent;
			} else if (nodeName.equals("junitProfilePattern")) {
				classPattern = nodeContent;
			} else if (nodeName.equals("junitTestMethod")) {
				junitTestMethod = nodeContent;
			} else if (nodeName.equals("junitClassInsideBody")) {
				insideBody = nodeContent;
			} else if (nodeName.equals("junitCreateMethodBody")) {
				createMethodBody = Boolean.parseBoolean(nodeContent);
			} else if (nodeName.equals("junitTestLocation")) {
				junitTestLocation = nodeContent;
			} else if (nodeName.equals("class-imports")) {

				final List<String> classImportList = new ArrayList<String>();
				Node nodeClassImport = node.getFirstChild();
				while (nodeClassImport != null) {
					if (nodeClassImport.getNodeType() != ELEMENT_NODE) {
						nodeClassImport = nodeClassImport.getNextSibling();
						continue;
					}
					classImportList.add(nodeClassImport.getTextContent());
					nodeClassImport = nodeClassImport.getNextSibling();
				}
				classImports = classImportList.toArray(new String[0]);

				/*
				 * } else if (nodeName.equals("junitAlwaysCreateTryCatch")) {
				 * alwaysCreateTryCatch = Boolean.parseBoolean(nodeContent);
				 */
			} else if (nodeName.equals("junitAlwaysCreateInstance")) {
				createInstance = Boolean.parseBoolean(nodeContent);
			} else if (nodeName.equals("junitExceptionBody")) {
				exceptionBody = nodeContent;
			} else if (nodeName.equals("junitNegativeBody")) {
				negativeBody = nodeContent;
			} else if (nodeName.equals("method-annotations")) {

				final List<String> methodAnnotationList = new ArrayList<String>();
				Node nodeMethodAnnotation = node.getFirstChild();
				while (nodeMethodAnnotation != null) {
					if (nodeMethodAnnotation.getNodeType() != ELEMENT_NODE) {
						nodeMethodAnnotation = nodeMethodAnnotation.getNextSibling();
						continue;
					}

					methodAnnotationList.add(nodeMethodAnnotation.getTextContent());
					nodeMethodAnnotation = nodeMethodAnnotation.getNextSibling();

				}
				methodAnnotations = methodAnnotationList.toArray(new String[0]);

			} else if (nodeName.equals("class-annotations")) {

				final List<String> classAnnotationList = new ArrayList<String>();
				Node nodeClassAnnotation = node.getFirstChild();
				while (nodeClassAnnotation != null) {
					if (nodeClassAnnotation.getNodeType() != ELEMENT_NODE) {
						nodeClassAnnotation = nodeClassAnnotation.getNextSibling();
						continue;
					}

					classAnnotationList.add(nodeClassAnnotation.getTextContent());
					nodeClassAnnotation = nodeClassAnnotation.getNextSibling();

				}
				classAnnotations = classAnnotationList.toArray(new String[0]);
			} else if (nodeName.equals("field-annotations")) {
				final List<String> fieldAnnotationList = new ArrayList<String>();
				Node nodeFieldAnnotation = node.getFirstChild();
				while (nodeFieldAnnotation != null) {
					if (nodeFieldAnnotation.getNodeType() != ELEMENT_NODE) {
						nodeFieldAnnotation = nodeFieldAnnotation.getNextSibling();
						continue;
					}
					fieldAnnotationList.add(nodeFieldAnnotation.getTextContent());
					nodeFieldAnnotation = nodeFieldAnnotation.getNextSibling();
				}
				fieldAnnotations = fieldAnnotationList.toArray(new String[0]);
			}

			node = node.getNextSibling();
		}
		preferences = new JunitPreferences(junitType, junitBaseType, testProfile, testProject, junitTestClass, classPattern,
				junitTestMethod, insideBody, createMethodBody, junitTestLocation, classImports, createInstance, fieldAnnotations,
				exceptionBody, negativeBody, methodAnnotations, classAnnotations, reload);
		preferences.settingToPreferenceStore(preferences, profile);
		junitPreferences.add(preferences);
		return junitPreferences;
	}

	/**
	 * @param nodeName
	 * @param preferenceValue
	 * @param leadingWhiteSpace
	 * @return
	 */
	private static String makeNode(final String nodeName, final String preferenceValue, final String leadingWhiteSpace) {
		final String node = leadingWhiteSpace + "<" + nodeName + ">" + preferenceValue + "</" + nodeName + ">";
		return node;
	}

	/**
	 * @param nodeName
	 * @param preferenceKey
	 * @param profileName
	 * @param leadingWhiteSpace
	 * @param cdata
	 * @param classType
	 * @return
	 */
	private static String makeNode(final String nodeName, final String preferenceKey, final String profileName,
			final String leadingWhiteSpace, final boolean cdata, final Class classType) {
		final IPreferenceStore preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);

		final String preferenceName = getPreferenceName(preferenceKey, profileName);

		Object preferenceValue = null;
		if (Boolean.class.equals(classType)) {
			preferenceValue = preferenceStore.getBoolean(preferenceName);
		} else if (String.class.equals(classType)) {
			preferenceValue = preferenceStore.getString(preferenceName).replaceAll(TAB, EMPTY_STR);
		}
		return cdata ? leadingWhiteSpace + "<" + nodeName + ">\n" + leadingWhiteSpace + TAB + "<![CDATA[\n"
				+ formatCDATA(leadingWhiteSpace + TAB + TAB, (String) preferenceValue) + leadingWhiteSpace + TAB + "]]>\n"
				+ leadingWhiteSpace + "</" + nodeName + ">" : leadingWhiteSpace + "<" + nodeName + ">" + preferenceValue + "</" + nodeName
				+ ">";
	}

	/**
	 * @param nodeName
	 * @param preferenceKey
	 * @param cdata
	 * @param leadingWhiteSpace
	 * @return
	 */
	private static String makeVariableNode(final String nodeName, final String preferenceKey, final boolean cdata,
			final String leadingWhiteSpace) {
		final IPreferenceStore preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		final Object preferenceValue = preferenceStore.getString(preferenceKey);

		return cdata ? leadingWhiteSpace + "<" + nodeName + ">\n" + leadingWhiteSpace + TAB + "<![CDATA[\n"
				+ formatCDATA(leadingWhiteSpace + TAB + TAB, (String) preferenceValue) + leadingWhiteSpace + TAB + "]]>\n"
				+ leadingWhiteSpace + "</" + nodeName + ">" : leadingWhiteSpace + "<" + nodeName + ">" + preferenceValue + "</" + nodeName
				+ ">";
	}
}
