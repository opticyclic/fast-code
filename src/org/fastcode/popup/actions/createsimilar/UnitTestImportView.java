package org.fastcode.popup.actions.createsimilar;

import static org.fastcode.util.XMLUtil.importXML;
import java.io.InputStream;
import org.eclipse.ui.ide.ResourceUtil;

public class UnitTestImportView extends UnitTestImportExportView {
	//private static String	profile;

	/* (non-Javadoc)
	 * @see org.fastcode.popup.actions.createsimilar.UnitTestImportExportView#processUnitTest(java.lang.String, java.lang.String)
	 */
	@Override
	protected void processUnitTest(final String fileName, final String templatePreferenceName, final String folderName) throws Exception {
		//processXML(fileName, templatePreferenceName, folderName);
		importXML(fileName, templatePreferenceName, folderName);
	}

	//not in use
	/*private void importUnitTestPreferences(final String fileName, final String templatePreferenceName) throws Exception {
		final IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		final IFile file = findOrcreateTemplate(fileName);

		if (file == null || !file.exists()) {
			throw new Exception("Template File " + fileName + " does not exist, please export and try again.");
		}

		if (checkForErrors(file)) {
			throw new Exception("Template File " + fileName + " has some errors, please fix them try again.");
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

			if (templatePreferenceName.equals(P_GLOBAL_PROPS)) {
				InputStream input = null;
				final String propertiesFile = "fast-code.properties";

				try {
					input = FileLocator.openStream(Activator.getDefault().getBundle(), new Path("resources/" + propertiesFile), false);
					this.properties = new Properties();
					this.properties.load(input);
				} catch (final Exception ex) {
					ex.printStackTrace();
				}
			} else {
			tv.validateTemplate(file.getContents());
			updateUnitTestPreferenceStore(inputStream);
			//}
		} finally {
			FastCodeUtil.closeInputStream(inputStream);
		}
	}

	private static void updateUnitTestPreferenceStore(final InputStream unitTestFileStream) {
		//final List<JunitPreferences> junitPreferences = new ArrayList<JunitPreferences>();
		final Map<String, List<JunitPreferences>> unitTestMap = new LinkedHashMap<String, List<JunitPreferences>>();

		//JunitPreferences preferences=new JunitPreferences(profileName);
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			final DocumentBuilder docBuilder = factory.newDocumentBuilder();
			final Document document = docBuilder.parse(unitTestFileStream);
			final NodeList parentNodeList = document.getElementsByTagName("UnitTest-Preferences");
			final NodeList resultFormatNodeList = document.getElementsByTagName("unitTest");

			for (int k = 0; k <= resultFormatNodeList.getLength(); k++) {
				Node node = resultFormatNodeList.item(k);
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
			try {
				throw new Exception(ex.getMessage(), ex);
			} catch (Exception ex1) {
				ex1.printStackTrace();
			}
		} finally {

		}
	}

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
		boolean alwaysCreateTryCatch = false;
		boolean createInstance = false;
		String exceptionBody = null;
		String negativeBody = null;
		String[] methodAnnotations = null;
		String[] classAnnotations = null;
		boolean reload = true;

		Node node = unitTestNode.getFirstChild();
		while (node != null) {
			if (node.getNodeType() != ELEMENT_NODE) {
				node = node.getNextSibling();
				continue;
			}
			String nodeName = node.getNodeName();
			String nodeContent = node.getTextContent();
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

				List<String> classImportList = new ArrayList<String>();
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

			} else if (nodeName.equals("junitAlwaysCreateTryCatch")) {
				alwaysCreateTryCatch = Boolean.parseBoolean(nodeContent);
			} else if (nodeName.equals("junitAlwaysCreateInstance")) {
				createInstance = Boolean.parseBoolean(nodeContent);
			} else if (nodeName.equals("junitExceptionBody")) {
				exceptionBody = nodeContent;
			} else if (nodeName.equals("junitNegativeBody")) {
				negativeBody = nodeContent;
			} else if (nodeName.equals("method-annotations")) {

				List<String> methodAnnotationList = new ArrayList<String>();
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

				List<String> classAnnotationList = new ArrayList<String>();
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
			}

			node = node.getNextSibling();
		}
		preferences = new JunitPreferences(junitType, junitBaseType, testProfile, testProject, junitTestClass, classPattern, junitTestMethod, insideBody,
				createMethodBody, junitTestLocation, classImports, alwaysCreateTryCatch, createInstance, exceptionBody, negativeBody, methodAnnotations,
				classAnnotations, reload);
		preferences.settingToPreferenceStore(preferences, profile);
		junitPreferences.add(preferences);
		return junitPreferences;
	}*/

	/* (non-Javadoc)
	 * @see org.fastcode.popup.actions.createsimilar.UnitTestImportExportView#getType()
	 */
	@Override
	protected String getType() {
		// TODO Auto-generated method stub
		return "Import";
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {
		InputStream inputStream = null;
		final String unitTestResultFormatFile = "UnitTestPreferences.xml";
		try {
			inputStream = ResourceUtil.class.getClassLoader().getResourceAsStream(unitTestResultFormatFile);
			if (inputStream == null) {
				throw new IllegalArgumentException("Invalid file " + unitTestResultFormatFile);
			}

			//updateUnitTestPreferenceStore(inputStream);

		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}
}
