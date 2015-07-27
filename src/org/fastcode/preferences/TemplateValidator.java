package org.fastcode.preferences;

import static org.fastcode.common.FastCodeConstants.COLON;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.HYPHEN;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_BASE_CLASS;
import static org.fastcode.common.FastCodeConstants.SPACE;
import static org.fastcode.common.FastCodeConstants.TEMPLATES_FOLDER;
import static org.fastcode.common.FastCodeConstants.UNDERSCORE;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.StringUtil.parseAdditonalParam;
import static org.fastcode.util.TemplateUtil.findOrcreateTemplate;
import static org.fastcode.util.TemplateUtil.makeTemplateSettings;
import static org.w3c.dom.Node.ELEMENT_NODE;

import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.ParseException;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.IField;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.Activator;
import org.fastcode.common.FastCodeConstants;
import org.fastcode.common.FastCodeConstants.FIRST_TEMPLATE;
import org.fastcode.common.FastCodeConstants.SECOND_TEMPLATE;
import org.fastcode.common.FastCodeConstants.TEMPLATE_PREFERENCE_NAME;
import org.fastcode.common.FastCodeField;
import org.fastcode.common.FastCodeFile;
import org.fastcode.common.FastCodePackage;
import org.fastcode.common.FastCodeReturn;
import org.fastcode.common.FastCodeType;
import org.fastcode.common.test.MockBundle;
import org.fastcode.common.test.MockDBField;
import org.fastcode.common.test.MockFastCodeFile;
import org.fastcode.common.test.MockFastCodeFolder;
import org.fastcode.common.test.MockFastCodeType;
import org.fastcode.common.test.MockField;
import org.fastcode.common.test.MockMethod;
import org.fastcode.exception.FastCodeException;
import org.fastcode.setting.TemplateSettings;
import org.fastcode.util.DefaultTemplatesManager;
import org.fastcode.util.TemplateUtil.FastCodeResolver;
import org.fastcode.util.VelocityUtil;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TemplateValidator {

	static String					MULTIPLE_ENTRY	= " -Multiple Entry";
	static String					HAS_PLACEHOLDER	= " Has PlaceHolder";
	static String					newLine			= "\n";

	static StringBuilder			allSnippets		= new StringBuilder(EMPTY_STR);
	private ScopedPreferenceStore	store;

	/**
	 *
	 * @param templateBody
	 * @param placeHolder
	 * @return
	 * @throws Exception
	 */
	public String validateWithVelocity(final String templateBody, final Map<String, Object> placeHolder) throws Exception {
		final String codeSnippet = EMPTY_STR;
		final StringBuilder allTemplateErrors = new StringBuilder(EMPTY_STR);
		//final StringBuilder allTemplateWarn = new StringBuilder(EMPTY_STR);
		final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(VelocityUtil.class.getClassLoader());

		try {
			RuntimeSingleton.parse(new StringReader(templateBody), "TemplateValidation");
			/*codeSnippet = evaluateByVelocity(templateBody, placeHolder);
			//final boolean hasPlaceHolder = containsAnyPlaceHolder(codeSnippet);
			if (containsAnyPlaceHolder(codeSnippet)) {
				allTemplateWarn.append(EMPTY_STR.equals(allTemplateWarn.toString()) ? placeHolder.get("template_name") + codeSnippet
						+ HAS_PLACEHOLDER : COLON + placeHolder.get("template_name") + codeSnippet + HAS_PLACEHOLDER);
				MessageDialog.openWarning(new Shell(), "Template body has Error.", placeHolder.get("template_name")
						+ " template body contains placeholders.");
				// throw new Exception("Error in template " +
				// placeHolder.get("template_name") + newLine +
				// allTemplateWarn.toString());

			}*/
			allSnippets.append(EMPTY_STR.equals(allSnippets.toString()) ? placeHolder.get("template_name") + newLine + codeSnippet
					: newLine + placeHolder.get("template_name") + newLine + codeSnippet);
		} catch (final ParseException pe) {
			pe.printStackTrace();
			if (pe.currentToken != null && pe.currentToken.next != null) {
				int lineNo = pe.currentToken.next.beginLine - 1;
				final int colNo = pe.currentToken.next.beginColumn - 1;
				final InputStream inputStream = findOrcreateTemplate("templates-config.xml", TEMPLATES_FOLDER).getContents();
				lineNo = lineNo + 1 + PositionalXMLReader.readXML(inputStream, placeHolder.get("template_name").toString());
				inputStream.close();
				allTemplateErrors.append(" Line :" + lineNo + " and column: " + colNo);
				throw new Exception("Error in template " + placeHolder.get("template_name") + newLine + allTemplateErrors.toString());
			}
		} catch (final Exception ex) {
			// TODO Auto-generated catch block
			//ex.printStackTrace();
			allTemplateErrors.append(EMPTY_STR.equals(allTemplateErrors.toString()) ? ex.getMessage() : COLON + ex.getMessage());
			throw new Exception("Error in template " + placeHolder.get("template_name") + newLine + allTemplateErrors.toString());

		} finally {
			Thread.currentThread().setContextClassLoader(contextClassLoader);
		}

		return allSnippets.toString();
	}

	/**
	 *
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> makeMockPlaceHolder(final String templatePreferenceName) throws Exception {

		final Map<String, Object> placeHolder = new HashMap<String, Object>();
		final List<FastCodeField> fastCodeFields = new ArrayList<FastCodeField>();
		final List<FastCodeField> myFastCodeFields = new ArrayList<FastCodeField>();

		final MockField fieldFN = new MockField("firstName", "String", "QString;");
		final MockField fieldLN = new MockField("lastName", "String", "QString;");
		final MockField fieldDoorNumber = new MockField("doorNumber", "Integer", "QInteger;");
		final MockField fieldAddress = new MockField("address", "Map", "QMap<QString;QString;>;");
		final MockField fieldMyFN = new MockField("myFirstName", "String", "QString;");
		final MockField fieldMyLN = new MockField("myLastName", "String", "QString;");

		final IField[] fields = { fieldFN, fieldLN, fieldDoorNumber, fieldAddress };

		final IField[] myFields = { fieldMyFN, fieldMyLN };

		placeHolder.put("instance", "sample");
		placeHolder.put("class_instance", "sample");
		placeHolder.put("from_class", new FastCodeType("com.test.sample.Car"));
		placeHolder.put("from_instance", "car");
		placeHolder.put("to_class", new FastCodeType("com.test.sample.Person"));
		placeHolder.put("to_instance", "person");

		if (fields != null) {
			for (final IField field : fields) {
				fastCodeFields.add(new FastCodeField(field, field.getElementName()));
			}
		}
		placeHolder.put("fields", fastCodeFields);
		placeHolder.put("from_fields", fastCodeFields);

		if (myFields != null) {
			for (final IField field : myFields) {
				myFastCodeFields.add(new FastCodeField(field, field.getElementName()));
			}
		}

		placeHolder.put("to_fields", myFastCodeFields);

		placeHolder.put("property", new MockBundle("testing"));
		placeHolder.put("method", new MockMethod("", "myMethod"));
		placeHolder.put("prefix", "MYPREFIX");
		placeHolder.put("file", new FastCodeFile("Sample", "com/test"));
		placeHolder.put("queryName", "SELECT");
		placeHolder.put("field", new FastCodeField(fieldFN, fieldFN.getElementName()));

		/*		final FastCodeDataBaseField empName = new FastCodeDataBaseField("Emp_Name", "varchar", "\"\"", 50, false,"EmpName",null);
				final FastCodeDataBaseField empId = new FastCodeDataBaseField("Emp_ID", "number", "0", 0 , false, "EmpId", new FastCodeDataBaseField("Emp_ID", "number", "0", 0 , false));
				final FastCodeDataBaseField empDept = new FastCodeDataBaseField("Dept", "varchar", "\"\"", 50, false,"Dept",null);
		*/
		final MockDBField empName = new MockDBField("Emp_Name", "EmployeeName", "varchar", "\"\"", 50, false, "empName", null,
				"java.lang.String", "Person");
		final MockDBField empId = new MockDBField("Emp_ID", "EmployeeID", "number", "0", 0, false, "empId", new MockDBField("Emp_ID",
				"EmployeeID", "number", "0", 0, false, "java.lang.String", "Person"), "java.lang.String", "Person");
		final MockDBField empDept = new MockDBField("empDept", "EmployeeDept", "varchar", "\"\"", 20, false, "empDept", null,
				"java.lang.String", "Person");

		final List<MockDBField> select_fields = new ArrayList<MockDBField>();
		select_fields.add(empName);
		select_fields.add(empId);

		placeHolder.put("selected_fields", select_fields);

		final List<MockDBField> join_fields = new ArrayList<MockDBField>();
		join_fields.add(empDept);

		placeHolder.put("join_fields", join_fields);

		final List<MockDBField> where_fields = new ArrayList<MockDBField>();
		where_fields.add(empDept);
		where_fields.add(empId);

		placeHolder.put("where_fields", where_fields);

		placeHolder.put("where_qualifier", "=");
		placeHolder.put("where_separator", "and");

		placeHolder.put("update_fields", select_fields);

		placeHolder.put("queryName", "selectCat");

		final List<MockDBField> pojo_fields = new ArrayList<MockDBField>();
		pojo_fields.add(empDept);
		pojo_fields.add(empId);

		placeHolder.put("pojo_fields", pojo_fields);

		final List<MockDBField> insert_fields = new ArrayList<MockDBField>();
		insert_fields.add(empDept);
		insert_fields.add(empId);

		placeHolder.put("insert_fields", select_fields);

		placeHolder.put("table", "books");
		placeHolder.put("package", new FastCodePackage("com.test", "Sample"));

		placeHolder.put("folder", new MockFastCodeFolder("", "com\test"));
		placeHolder.put("XmlFolder", new MockFastCodeFolder("", "com\test"));

		placeHolder.put("file", new MockFastCodeFile("Sample", "com/test"));
		placeHolder.put("target", new MockFastCodeFile("Sample", "com/test"));
		placeHolder.put("fileName", "test.java");
		placeHolder.put("localvar", new FastCodeReturn("car", new FastCodeType("com.test.sample.Car")));
		placeHolder.put("testing", "test");
		placeHolder.put("servletname", "testservlet");
		placeHolder.put("url", "/testservlet");
		placeHolder.put("project", ResourcesPlugin.getWorkspace().getRoot().getProjects()[0]);
		final String classHeader = "/**" + "* This class has been generated by Fast Code Eclipse Plugin "
				+ " * For more information please go to http://fast-code.sourceforge.net/";
		placeHolder.put("class_header", classHeader);
		placeHolder.put("targetClass", new FastCodeType("com.test.Sample"));
		placeHolder.put("fieldName", "fname");
		placeHolder.put("module", "product");
		placeHolder.put("value", "");
		placeHolder.put("enum", FastCodeConstants.CLASS_TYPE.class);
		placeHolder.put("scripts", new MockFastCodeFolder("", "com\test"));
		placeHolder.put("pageContext.request.contextPath", "test");
		placeHolder.put("webInf", new MockFastCodeFolder("", "com\test"));
		placeHolder.put("schema", "test");
		placeHolder.put("methodName", "getSample");
		placeHolder.put("targetPackage", new FastCodePackage("com.test", "Sample"));
		if (templatePreferenceName.equals(TEMPLATE_PREFERENCE_NAME.ALL_DATABASE_TEMPLATES.name())) {
			placeHolder.put("fields", select_fields);
			placeHolder.put("TargetPackage", new FastCodePackage("com.test", "Sample"));
		}
		final Map<String, String> setMethodFieldTypeMap = new HashMap<String, String>();
		final Map<String, String> getMethodFieldTypeMap = new HashMap<String, String>();
		setMethodFieldTypeMap.put("number", "setInt");
		getMethodFieldTypeMap.put("varchar", "getString");
		getMethodFieldTypeMap.put("number", "getInt");
		setMethodFieldTypeMap.put("varchar", "setString");
		placeHolder.put("MethodFieldMap", setMethodFieldTypeMap);
		placeHolder.put("GetMethodFieldMap", getMethodFieldTypeMap);
		placeHolder.put("MethodWhereFieldMap", setMethodFieldTypeMap);
		placeHolder.put(PLACEHOLDER_BASE_CLASS, new FastCodeType("com.test.Sample"));
		placeHolder.put("implementInterface", "");

		final Map<String, String> fieldNameMethodMap = new HashMap<String, String>();
		fieldNameMethodMap.put("Emp_ID", "getEmpID");
		fieldNameMethodMap.put("Emp_Name", "getEmpName");
		placeHolder.put("nameMethodMap", fieldNameMethodMap);
		placeHolder.put("strfields", "");
		placeHolder.put("pojo_class_instance", "person");
		placeHolder.put("javaTableName", "person");
		placeHolder.put("ClassName", "Sample");
		placeHolder.put("enclosing_file", new MockFastCodeFile("Sample", "com/test"));
		placeHolder.put("SelectedText", "java");
		placeHolder.put("class", new MockFastCodeType("com.test.sample.Car"));
		return placeHolder;

	}

	/**
	 *
	 * @param templateBody
	 * @param templateVariations
	 * @param multipleVariation
	 * @param placeHolder
	 * @return
	 * @throws Exception
	 */
	public String callValidate(final String templateBody, final String[] templateVariations, final boolean multipleVariation,
			final Map<String, Object> placeHolder) throws Exception {
		String snippet = EMPTY_STR;

		if (multipleVariation) {
			final List<String> tempVarList = new ArrayList<String>();
			for (final String var : templateVariations) {
				tempVarList.add(var);
			}
			placeHolder.put("_template_variation", tempVarList);
			snippet = validateWithVelocity(templateBody, placeHolder);

		} else {
			if (templateVariations != null) {
				for (final String variationString : templateVariations) {
					placeHolder.put("_template_variation", variationString);
					snippet = validateWithVelocity(templateBody, placeHolder);
				}
			} else {
				snippet = validateWithVelocity(templateBody, placeHolder);
			}
		}
		return snippet;
	}

	/**
	 *
	 * @param inputStream
	 * @param templatePrefix
	 * @throws Exception
	 */
	public boolean validateTemplate(final InputStream inputStream, final String templatePreferenceName) throws Exception {
		Map templateNameBody = new HashMap<String, TemplateSettings>();
		final String templateBody = EMPTY_STR;
		final String variation = EMPTY_STR;
		String templateType;
		TemplateSettings templateSettings;
		final Map<String, Object> placeHolder;
		final String snippet = EMPTY_STR;
		boolean varValidation = true;
		templateNameBody = getTemplateBody(inputStream, TEMPLATE_PREFERENCE_NAME.getTemplatePrefix(templatePreferenceName));

		if (templateNameBody != null) {
			// placeHolder = makeMockPlaceHolder(templatePreferenceName);

			final Iterator templateNameBodyItr = templateNameBody.entrySet().iterator();
			while (templateNameBodyItr.hasNext()) {
				final Map.Entry templateNameBodyPair = (Map.Entry) templateNameBodyItr.next();
				templateType = (String) templateNameBodyPair.getKey();
				templateSettings = (TemplateSettings) templateNameBodyPair.getValue();
				//	placeHolder.put("template_name", templateSettings.getTemplateName());
				validateVelocitySyntax(templateSettings.getTemplateBody(), templateSettings.getTemplateName());
				final VelocityUtil velocityUtil = VelocityUtil.getInstance();
				final String additionalParam = templateSettings.getAdditionalParamaters(); //EMPTY_STR;
				/*if (templateSettings.getAdditionalParamaters() != null) {
					for (int i = 0; i < templateSettings.getAdditionalParamaters().length; i++) {
						additionalParam = additionalParam + SPACE + templateSettings.getAdditionalParamaters()[i];
					}
				}*/
				final boolean showErrorMessage = true;
				varValidation = velocityUtil.validateVariablesAndMethods(templateSettings.getTemplateBody(), templateSettings
						.getFirstTemplateItem().getValue(), templateSettings.getSecondTemplateItem().getValue(), additionalParam,
						templateSettings.getTemplateName(), TEMPLATE_PREFERENCE_NAME.getTemplatePrefix(templatePreferenceName),
						new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID),null, showErrorMessage);
				velocityUtil.reset();
				if (!varValidation) {
					break;
				}
			}
		} else {
			varValidation = false;
		}
		return varValidation;

		/*snippet = callValidate(templateSettings.getTemplateBody(), templateSettings.getTemplateVariations(),
				templateSettings.isAllowMultipleVariation(), placeHolder);

		}*/

	}

	/**
	 *
	 * @param inputStream
	 * @return
	 * @throws Exception
	 */
	public Map<String, TemplateSettings> getTemplateBody(final InputStream inputStream, final String templatePrefix) throws Exception {
		final Map<String, TemplateSettings> templateNameBody = new HashMap<String, TemplateSettings>();
		//final String xmlFile = "templates-config.xml";
		//final String dtdFile = "template.dtd";
		String allTemplates = EMPTY_STR;
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		InputStream entityStream = null;
		//final InputStream inputStreamLocal = inputStream;

		/* final InputStream stream = ResourceUtil.class.getClassLoader().getResourceAsStream(xmlFile);
		    if (stream == null) {
		        throw new IllegalArgumentException("Invalid file " + xmlFile);
		    }*/
		//try {
		final DocumentBuilder docBuilder = factory.newDocumentBuilder();
		entityStream = FileLocator.openStream(Activator.getDefault().getBundle(), new Path("resources/template.dtd"), false);
		docBuilder.setEntityResolver(new FastCodeResolver(entityStream));
		final Document document = docBuilder.parse(inputStream);
		final NodeList parentNodeList = document.getElementsByTagName("templates");
		final Node configNode = parentNodeList.item(0);
		Node node = configNode.getFirstChild();
		//	final int numTemplates = document.getElementsByTagName("template").getLength();
		while (node != null) {
			if (node.getNodeType() != ELEMENT_NODE) {
				node = node.getNextSibling();
				continue;
			}
			if ("template".equals(node.getNodeName())) {
				final NamedNodeMap attributes = node.getAttributes();
				if (attributes == null || attributes.getLength() == 0) {
					continue;
				}
				final String templateName = attributes.getNamedItem("name").getNodeValue();

				if (templateNameBody.containsKey(templateName)) {
					throw new Exception("Duplicate Template entry " + templateName);
				}

				allTemplates = allTemplates.equals(EMPTY_STR) ? templatePrefix + UNDERSCORE + templateName : allTemplates.concat(COLON
						+ templatePrefix + UNDERSCORE + templateName);

				final TemplateSettings templateSettings = makeTemplateSettings(node, templateName);
				final String[] variations = templateSettings.getTemplateVariations();
				final Set<String> found = new LinkedHashSet<String>();
				if (variations != null) {
					for (int i = 0; i < variations.length; i++) {
						if (!found.add(variations[i])) {
							//variations[i] = null;
							throw new Exception("Duplicate variation entry " + variations[i] + " in template " + templateName);
						}
					}
				}
				String choice1 = EMPTY_STR;
				if (templateSettings.getFirstTemplateItem() != null) {
					choice1 = templateSettings.getFirstTemplateItem().getValue();
					if (isEmpty(choice1)
							|| !(choice1.equalsIgnoreCase(FIRST_TEMPLATE.Class.getValue())
									|| choice1.equalsIgnoreCase(FIRST_TEMPLATE.File.getValue())
									|| choice1.equalsIgnoreCase(FIRST_TEMPLATE.Folder.getValue())
									|| choice1.equalsIgnoreCase(FIRST_TEMPLATE.Package.getValue())
									|| choice1.equalsIgnoreCase(FIRST_TEMPLATE.None.getValue()) || choice1
										.equalsIgnoreCase(FIRST_TEMPLATE.Enumeration.getValue()))) {
						throw new Exception(templateName
								+ " : 1st template item must have one of these values class/file/package/folder/enum/none.");
					}
				} else {
					throw new Exception(templateName
							+ " : 1st template item must have one of these values class/file/package/folder/enum/none.");
				}
				if (templateSettings.getSecondTemplateItem() != null) {
					final String choice2 = templateSettings.getSecondTemplateItem().getValue();
					if (isEmpty(choice2)
							|| choice1.equalsIgnoreCase(FIRST_TEMPLATE.Class.getValue())
							&& !(choice2.equalsIgnoreCase(SECOND_TEMPLATE.method.getValue())
									|| choice2.equalsIgnoreCase(SECOND_TEMPLATE.field.getValue())
									|| choice2.equalsIgnoreCase(SECOND_TEMPLATE.both.getValue())
									|| choice2.equalsIgnoreCase(SECOND_TEMPLATE.custom.getValue()) || choice2
										.equalsIgnoreCase(SECOND_TEMPLATE.none.getValue()))) {
						throw new Exception(
								templateName
										+ " : when 1st template item is - class - 2nd template item must have one of these values method/field/both/custom/none.");
					}
					if (!isEmpty(choice2)
							&& choice1.equalsIgnoreCase(FIRST_TEMPLATE.File.getValue())
							&& !(choice2.equalsIgnoreCase(SECOND_TEMPLATE.none.getValue())
									|| choice2.equalsIgnoreCase(SECOND_TEMPLATE.property.getValue()) || choice2
										.equalsIgnoreCase(SECOND_TEMPLATE.data.getValue()))) {
						throw new Exception(templateName
								+ " : when 1st template item is - file - 2nd template item can  be property/data/none.");
					}
					if (isEmpty(choice2)
							|| choice1.equalsIgnoreCase(FIRST_TEMPLATE.Package.getValue())
							&& !(choice2.equalsIgnoreCase(SECOND_TEMPLATE.none.getValue()) || choice2
									.equalsIgnoreCase(SECOND_TEMPLATE.Class.getValue()))) {
						throw new Exception(templateName + " : when 1st template item is - package - 2nd template item can be class/none.");
					}
					if (isEmpty(choice2)
							|| choice1.equalsIgnoreCase(FIRST_TEMPLATE.Folder.getValue())
							&& !(choice2.equalsIgnoreCase(SECOND_TEMPLATE.none.getValue()) || choice2.equalsIgnoreCase(SECOND_TEMPLATE.file
									.getValue()))) {
						throw new Exception(templateName + " : when 1st template item is - folder - 2nd template item can be file/none.");
					}
					if (isEmpty(choice2)
							|| choice1.equalsIgnoreCase(FIRST_TEMPLATE.Enumeration.getValue())
							&& !(choice2.equalsIgnoreCase(SECOND_TEMPLATE.none.getValue()) || choice2
									.equalsIgnoreCase(SECOND_TEMPLATE.field.getValue()))) {
						throw new Exception(templateName + " : when 1st template item is - enum - 2nd template item can be field/none.");
					}
				} else {
					throw new Exception(templateName
							+ " : 2st template item must have one of these values method/field/both/custom/class/file/none.");
				}
				//List<FastCodeAdditionalParams> fcAdditnlParamList = new ArrayList<FastCodeAdditionalParams>();
				if (!isEmpty(templateSettings.getAdditionalParamaters())) {
					try {
						parseAdditonalParam(templateSettings.getAdditionalParamaters());
					} catch (final FastCodeException fastCodeException) {
						throw new FastCodeException(templateName + SPACE + HYPHEN + SPACE + fastCodeException.getMessage());
					}
				}

				//final String[] additionalParametes = templateSettings.getAdditionalParamaters();
				/*if (fcAdditnlParamList != null && fcAdditnlParamList.length > 0) {
					final Map<String, String> addtnParamMap = new HashMap<String, String>();
					for (final FastCodeAdditionalParams params : fcAdditnlParamList.toArray(new FastCodeAdditionalParams[0])) {
						params.getReturnTypes().equals(other)
						if (params.contains(COLON)) {
							final String parseParam[] = params.split(COLON);
							System.out.println(parseParam.length);
							if (parseParam.length == 2) {
								final String type = parseParam[1].trim();
								if (isEmpty(type)
										|| !(type.equalsIgnoreCase(FIRST_TEMPLATE.Class.getValue())
												|| type.equalsIgnoreCase(FIRST_TEMPLATE.File.getValue())
												|| type.equalsIgnoreCase(FIRST_TEMPLATE.Package.getValue())
												|| type.equalsIgnoreCase(FIRST_TEMPLATE.Folder.getValue())
												|| type.equalsIgnoreCase(RETURN_TYPES.PROJECT.getValue())
												|| type.equalsIgnoreCase(RETURN_TYPES.JAVAPROJECT.getValue())
												|| type.equalsIgnoreCase(RETURN_TYPES.LOCALVAR.getValue()) || type
													.equalsIgnoreCase(RETURN_TYPES.BOOLEAN.getValue()))) {
									throw new Exception(
											" - "
													+ templateName
													+ " - Invalid value - "
													+ type
													+ ".\nAdditional Parameter type can be only class/file/package/folder/project/javaProject/localvar/boolean.");
								}
								if (addtnParamMap.containsKey(parseParam[0])) {
									throw new Exception("Duplicate Additional Parameter name " + parseParam[0]);
								}
								addtnParamMap.put(parseParam[0], type);
							} else if (parseParam.length == 1) {
								throw new Exception(
										templateName
												+ " : Additional Parameter type can be only class/file/package/folder/project/javaProject/localvar/boolean.");
							}
						}
					}
				}*/

				if (templateSettings.getGetterSetterRequired() == null) {
					throw new Exception(templateName + " : Getter/setter can be gettersetter/getter/setter/none only.");
				}

				if (templateSettings.getNumberRequiredItems() < 0 || templateSettings.getNumberRequiredItems() > 2) {
					throw new Exception(templateName + " : Number of Required items can be 0, 1 or 2.");
				}
				templateNameBody.put(templateName, templateSettings);

			}
			node = node.getNextSibling();
		}
		/*if (templatePrefix.equals("FILE_TEMPLATE") && allTemplates.split(COLON).length > 1) {
			throw new Exception("There can be only one file template. No additional user defined templates are allowed");

		}*/
		final ArrayList<String> defaultTemplateNames = DefaultTemplatesManager.getInstance().getDefaultTemplates(templatePrefix);
		/*this.store = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		final ArrayList<String> defaultTemplateNames = new ArrayList<String>(Arrays.asList(this.store.getDefaultString(templatePrefix)
				.split(":")));
		*/
		final ArrayList<String> allTemplatesList = new ArrayList<String>(Arrays.asList(allTemplates.toString().split(":")));
		final List result = new ArrayList<String>(defaultTemplateNames);
		result.removeAll(allTemplatesList);
		if (!result.isEmpty()) {
			final MessageDialog warningMessageDialog = new MessageDialog(null, "Default template renamed or deleted", null,
					"        Default template " + ((String) result.get(0)).substring(templatePrefix.length() + 1)
							+ " is renamed or deleted.", MessageDialog.WARNING, new String[] { "Proceed Anyway", "Cancel" }, 0) {
				@Override
				protected void buttonPressed(final int buttonId) {
					setReturnCode(buttonId);
					close();

				}
			};
			warningMessageDialog.open();

			if (warningMessageDialog.getReturnCode() == -1 || warningMessageDialog.getReturnCode() == 1) {

				return null;
			}
		}
		return templateNameBody;
	}

	/**
	 *
	 * @param templateBody
	 * @param placeHolder
	 * @return
	 * @throws Exception
	 */
	private String validateVelocitySyntax(final String templateBody, final String template_name) throws Exception {
		final StringBuilder allTemplateErrors = new StringBuilder(EMPTY_STR);

		final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(VelocityUtil.class.getClassLoader());

		try {
			RuntimeSingleton.parse(new StringReader(templateBody), "TemplateValidation");

		} catch (final ParseException pe) {
			pe.printStackTrace();
			if (pe.currentToken != null && pe.currentToken.next != null) {
				int lineNo = pe.currentToken.next.beginLine - 1;
				final int colNo = pe.currentToken.next.beginColumn - 1;
				final InputStream inputStream = findOrcreateTemplate("templates-config.xml", TEMPLATES_FOLDER).getContents();
				lineNo = lineNo + 1 + PositionalXMLReader.readXML(inputStream, template_name.toString());
				inputStream.close();
				allTemplateErrors.append(" Line :" + lineNo + " and column: " + colNo);
				throw new Exception("Error in template " + template_name + newLine + allTemplateErrors.toString());
			}
		} catch (final Exception ex) {
			// TODO Auto-generated catch block
			//ex.printStackTrace();
			allTemplateErrors.append(EMPTY_STR.equals(allTemplateErrors.toString()) ? ex.getMessage() : COLON + ex.getMessage());
			throw new Exception("Error in template " + template_name + newLine + allTemplateErrors.toString());

		} finally {
			Thread.currentThread().setContextClassLoader(contextClassLoader);
		}
		return allSnippets.toString();
	}
}
