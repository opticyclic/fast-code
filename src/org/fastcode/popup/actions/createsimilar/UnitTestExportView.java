package org.fastcode.popup.actions.createsimilar;

import static org.fastcode.util.XMLUtil.exportXML;

public class UnitTestExportView extends UnitTestImportExportView {
	public UnitTestExportView() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.fastcode.popup.actions.createsimilar.UnitTestImportExportView#processUnitTest(java.lang.String, java.lang.String)
	 */
	@Override
	protected void processUnitTest(final String fileName, final String templatePreferenceName, final String folderName) throws Exception {
		//processXML(fileName, templatePreferenceName, folderName);
		exportXML(fileName, templatePreferenceName, folderName);
	}

	/* (non-Javadoc)
	 * @see org.fastcode.popup.actions.createsimilar.UnitTestImportExportView#getType()
	 */
	@Override
	protected String getType() {
		// TODO Auto-generated method stub
		return "Export";
	}

	//not in use
	/*private void exportUnitTestPreferences(final String fileName, final String templatePreferenceName) throws Exception {
		//final String templatePrefix = null;

		final IFile file = findOrcreateTemplate(fileName);

		final String templateBuffer = getAllJunitPreference();// getAllTemplates(templatePreferenceName,
																// TEMPLATE_PREFERENCE_NAME.getTemplatePrefix(templatePreferenceName));

		if (file == null) {
			throw new Exception("Unknown Exception : Please try again.");
		}
		final InputStream inputStream = new ByteArrayInputStream(templateBuffer.getBytes());
		try {
			if (file.exists()) {
				boolean overWrite = MessageDialog.openQuestion(new Shell(), "Overwrite File", "File is already exported ,Would you like to overwrite?");
				if (overWrite) {
					file.setContents(inputStream, false, true, new NullProgressMonitor());
				} else {
					renameExistingExportFileName(file, fileName);
					exportUnitTestPreferences(fileName, templatePreferenceName);
				}
				//file.setContents(inputStream, false, true, new NullProgressMonitor());
			} else {
				file.create(inputStream, false, new NullProgressMonitor());
			}
		} catch (final Exception ex) {
			ex.printStackTrace();
			throw new Exception("Template could not be saved " + ex.getMessage(), ex);
		} finally {
			FastCodeUtil.closeInputStream(inputStream);
		}
	}

	private String getAllJunitPreference() {

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
					this.junitType = JUNIT_TYPE.JUNIT_TYPE_3;
				} else if (P_JUNIT_TYPE_4.equals(jUnitType)) {
					this.junitType = JUNIT_TYPE.JUNIT_TYPE_4;
				} else if (P_JUNIT_TYPE_TESTNG.equals(jUnitType)) {
					this.junitType = JUNIT_TYPE.JUNIT_TYPE_TESTNG;
				}
				unitTestPrefBuffer.append(TAB + "<unitTest profile=\"" + prof + "\">\n");
				unitTestPrefBuffer.append(this.makeNode(changeFirstLetterToLowerCase(P_JUNIT_TYPE.replaceAll("\\s*", EMPTY_STR)), P_JUNIT_TYPE, prof, TAB2,
						false, String.class) + NEWLINE);
				unitTestPrefBuffer.append(this.makeNode(changeFirstLetterToLowerCase(P_BASE_TEST.replaceAll("\\s*", EMPTY_STR)), P_BASE_TEST, prof, TAB2,
						false, String.class) + NEWLINE);
				unitTestPrefBuffer
						.append(this.makeNode(changeFirstLetterToLowerCase(P_JUNIT_TEST_PROFILE.replaceAll("\\s*", EMPTY_STR)), prof, TAB2) + NEWLINE);
				unitTestPrefBuffer.append(this.makeNode(P_JUNIT_TEST_PROJECT.replaceAll("\\s*", EMPTY_STR), P_JUNIT_TEST_PROJECT, prof, TAB2, false,
						String.class) + NEWLINE);
				unitTestPrefBuffer.append(this.makeNode(changeFirstLetterToLowerCase(P_JUNIT_TEST_CLASS.replaceAll("\\s*", EMPTY_STR)), P_JUNIT_TEST_CLASS,
						prof, TAB2, false, String.class) + NEWLINE);
				//unitTestPrefBuffer.append(this.makeNode(P_JUNIT_PROFILE_NAME.replaceAll("\\s*", EMPTY_STR), P_JUNIT_PROFILE_NAME, prof, TAB2,false, String.class)+NEWLINE);
				unitTestPrefBuffer.append(this.makeNode(P_JUNIT_PROFILE_PATTERN.replaceAll("\\s*", EMPTY_STR), P_JUNIT_PROFILE_PATTERN, prof, TAB2, false,
						String.class) + NEWLINE);
				unitTestPrefBuffer.append(this.makeNode(changeFirstLetterToLowerCase(P_JUNIT_TEST_METHOD.replaceAll("\\s*", EMPTY_STR)), P_JUNIT_TEST_METHOD,
						prof, TAB2, false, String.class) + NEWLINE);
				unitTestPrefBuffer.append(this.makeNode(P_JUNIT_CLASS_INSIDE_BODY.replaceAll("\\s*", EMPTY_STR), P_JUNIT_CLASS_INSIDE_BODY, prof, TAB2, false,
						String.class) + NEWLINE);
				unitTestPrefBuffer.append(this.makeNode(P_JUNIT_CREATE_METHOD_BODY.replaceAll("\\s*", EMPTY_STR), P_JUNIT_CREATE_METHOD_BODY, prof, TAB2,
						false, Boolean.class) + NEWLINE);
				this.junitTestLocation = preferenceStore.getString(getPreferenceName(P_JUNIT_TEST_LOCATION, prof));
				unitTestPrefBuffer.append(this.makeNode(P_JUNIT_TEST_LOCATION.replaceAll("\\s*", EMPTY_STR), P_JUNIT_TEST_LOCATION, prof, TAB2, false,
						String.class) + NEWLINE);
				unitTestPrefBuffer.append(TAB2 + "<class-imports>" + NEWLINE);
				final String clsImports = preferenceStore.getString(getPreferenceName(P_JUNIT_CLASS_IMPORTS, prof));
				if (!isEmpty(clsImports)) {
					this.classImports = clsImports.split(NEWLINE);
				}

				if (this.classImports != null) {
					for (final String classImport : this.classImports) {
						unitTestPrefBuffer.append(TAB + this.makeNode(CLASS_IMPORT_TAG, classImport, TAB2) + NEWLINE);
					}
				}

				unitTestPrefBuffer.append(TAB2 + "</class-imports>" + NEWLINE);
				if (containsPlaceHolder(this.junitTestLocation, "project")) {
					this.junitTestLocation = this.junitTestLocation.replace("${project}", EMPTY_STR);
					if (!this.junitTestLocation.startsWith("/")) {
						this.junitTestLocation = "/" + this.junitTestLocation;
					}
					preferenceStore.setValue(P_JUNIT_TEST_LOCATION, this.junitTestLocation);
				}

				unitTestPrefBuffer.append(this.makeNode(P_JUNIT_ALWAYS_CREATE_TRY_CATCH.replaceAll("\\s*", EMPTY_STR), P_JUNIT_ALWAYS_CREATE_TRY_CATCH, prof,
						TAB2, false, Boolean.class) + NEWLINE);
				unitTestPrefBuffer.append(this.makeNode(P_JUNIT_ALWAYS_CREATE_INSTANCE.replaceAll("\\s*", EMPTY_STR), P_JUNIT_ALWAYS_CREATE_INSTANCE, prof,
						TAB2, false, Boolean.class) + NEWLINE);
				unitTestPrefBuffer.append(this.makeNode(P_JUNIT_EXCEPTION_BODY.replaceAll("\\s*", EMPTY_STR), P_JUNIT_EXCEPTION_BODY, prof, TAB2, true,
						String.class) + NEWLINE);
				unitTestPrefBuffer.append(this.makeNode(P_JUNIT_NEGATIVE_BODY.replaceAll("\\s*", EMPTY_STR), P_JUNIT_NEGATIVE_BODY, prof, TAB2, true,
						String.class) + NEWLINE);
				unitTestPrefBuffer.append(TAB2 + "<method-annotations>" + NEWLINE);
				this.methodAnnotations = getAnnotationsFromPreference(getPreferenceName(P_JUNIT_METHOD_ANNOTATIONS, prof));
				if (this.methodAnnotations != null) {
					for (final String methodAnnotation : this.methodAnnotations) {
						unitTestPrefBuffer.append(TAB + this.makeNode(METHOD_ANNOTATION_TAG, methodAnnotation, TAB2) + NEWLINE);
					}
				}
				unitTestPrefBuffer.append(TAB2 + "</method-annotations>" + NEWLINE);
				unitTestPrefBuffer.append(TAB2 + "<class-annotations>" + NEWLINE);
				this.classAnnotations = getAnnotationsFromPreference(getPreferenceName(P_JUNIT_CLASS_ANNOTATIONS, prof));
				if (this.classAnnotations != null) {
					for (final String classAnnotation : this.classAnnotations) {
						unitTestPrefBuffer.append(TAB + this.makeNode(CLASS_ANNOTATION_TAG, classAnnotation, TAB2) + NEWLINE);
					}
				}
				unitTestPrefBuffer.append(TAB2 + "</class-annotations>" + NEWLINE);
				unitTestPrefBuffer.append("</unitTest>" + NEWLINE);
			}
		}
		unitTestPrefBuffer.append("</UnitTest-Preferences>" + NEWLINE);

		return unitTestPrefBuffer.toString();

	}

	private String makeNode(final String nodeName, final String preferenceValue, final String leadingWhiteSpace) {
		final String node = leadingWhiteSpace + "<" + nodeName + ">" + preferenceValue + "</" + nodeName + ">";
		return node;
	}

	private String makeNode(final String nodeName, final String preferenceKey, final String profileName, final String leadingWhiteSpace, final boolean cdata,
			final Class classType) {
		final IPreferenceStore preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);

		final String preferenceName = getPreferenceName(preferenceKey, profileName);

		Object preferenceValue = null;
		if (Boolean.class.equals(classType)) {
			preferenceValue = preferenceStore.getBoolean(preferenceName);
		} else if (String.class.equals(classType)) {
			preferenceValue = preferenceStore.getString(preferenceName);
		}
		return cdata ? leadingWhiteSpace + "<" + nodeName + ">\n" + leadingWhiteSpace + TAB + "<![CDATA[\n"
				+ formatCDATA(leadingWhiteSpace + TAB + TAB, (String) preferenceValue) + leadingWhiteSpace + TAB + "]]>\n" + leadingWhiteSpace + "</"
				+ nodeName + ">" : leadingWhiteSpace + "<" + nodeName + ">" + preferenceValue + "</" + nodeName + ">";
	}*/

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}
}
