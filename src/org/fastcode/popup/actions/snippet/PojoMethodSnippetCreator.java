package org.fastcode.popup.actions.snippet;

import static org.fastcode.common.FastCodeConstants.COMMA;
import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.JAVA_EXTENSION;
import static org.fastcode.common.FastCodeConstants.POJO;
import static org.fastcode.common.FastCodeConstants.TABLE;
import static org.fastcode.util.FastCodeUtil.closeInputStream;
import static org.fastcode.util.StringUtil.evaluateByVelocity;
import static org.fastcode.util.StringUtil.getGlobalSettings;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.StringUtil.replaceSpecialChars;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.IWorkingCopyManager;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.fastcode.util.SourceUtil;
import org.fastcode.util.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class PojoMethodSnippetCreator implements SnippetCreator {

	List<String>		setMethodSnippetList	= new ArrayList<String>();
	List<String>		getMethodSnippetList	= new ArrayList<String>();
	List<String>		fieldSnippetList		= new ArrayList<String>();
	String[]			classToImport			= null;
	ICompilationUnit	compilationUnit			= null;

	/**
	 * @param editorPart
	 * @param placeHolders
	 * @param memberSelection
	 * @param spacesBeforeCursor
	 */
	@Override
	public Object createSnippet(final IEditorPart editorPart, final String template, final Map<String, Object> placeHolders,
			final Map<String, Object> memberSelection, final String spacesBeforeCursor) throws Exception {
		getGlobalSettings(placeHolders);
		final String snppt = evaluateByVelocity(template, placeHolders);
		if (isEmpty(snppt)) {
			throw new Exception("Blank snippet, template may be invalid.");
		}

		String snippet = replaceSpecialChars(snppt);

		final String tableName = (String) placeHolders.get(TABLE);

		final String pojoClassName = tableName + POJO + DOT + JAVA_EXTENSION;
		String className = null;

		this.compilationUnit = getCompilationUnitFromEditor(editorPart);

		if (this.compilationUnit != null) {
			className = this.compilationUnit.getElementName();
		} else	if (!pojoClassName.equalsIgnoreCase(className)) {
			//error
			return null;
		}
		final TemplateTagsProcessor templateTagsProcessor = new TemplateTagsProcessor();
		snippet = templateTagsProcessor.processTemplateTags(this.compilationUnit, snippet.trim(), editorPart, false, null,
				spacesBeforeCursor);

		if (isEmpty(snippet.trim())) {
			return null;
		}
		parseSnippet(snippet);
		for (final String fieldSnippet : this.fieldSnippetList) {
			if (!isEmpty(fieldSnippet)) {
				createFields(fieldSnippet, this.compilationUnit.findPrimaryType());
			}
		}

		for (final String getMethodSnippet : this.getMethodSnippetList) {
			if (!isEmpty(getMethodSnippet)) {
				createMethods(getMethodSnippet, this.compilationUnit.findPrimaryType());
			}
		}

		for (final String setMethodSnippet : this.setMethodSnippetList) {
			if (!isEmpty(setMethodSnippet)) {
				createMethods(setMethodSnippet, this.compilationUnit.findPrimaryType());
			}
		}

		for (final String typeToImport : this.classToImport) {
			if (!isEmpty(typeToImport)) {
				createImports(typeToImport.trim());
			}
		}
		// JavaUI.revealInEditor(editorPart, (IJavaElement)method);
		return this.compilationUnit;
	}

	/**
	 *
	 * @param snippet
	 * @param compilationUnit
	 * @return
	 * @throws Exception
	 */
	private boolean checkForMethod(final String snippet, final ICompilationUnit compilationUnit) throws Exception {
		final String methodName = StringUtil.parseMethodName(snippet);
		if (methodName == null) {
			if (!MessageDialog.openConfirm(new Shell(), "Warning", "Template may be incorrect. Would you like to proceed?")) {
				return false;
			}
		}

		final IType type = compilationUnit.findPrimaryType();
		if (SourceUtil.doesMethodExistsInType(type, methodName)) {
			if (!MessageDialog.openConfirm(new Shell(), "Warning", "Method with name " + methodName
					+ " already exists. Would you like to proceed?")) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param snippet
	 * @throws Exception
	 */
	private void parseSnippet(final String snippet) throws Exception {

		InputStream inputStream = null;

		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			inputStream = new ByteArrayInputStream(snippet.getBytes("UTF-8"));
			final DocumentBuilder docBuilder = factory.newDocumentBuilder();
			final Document document = docBuilder.parse(inputStream);
			final NodeList fieldNodeList = document.getElementsByTagName("pojo_field_list");
			final NodeList getterNodeList = document.getElementsByTagName("pojo_get_method_list");
			final NodeList setterNodeList = document.getElementsByTagName("pojo_set_method_list");
			final NodeList classesToImport = document.getElementsByTagName("pojo_import_list");

			for (int i = 0; i < fieldNodeList.getLength(); i++) {
				// final Node fieldNode = ;

				this.fieldSnippetList.add(fieldNodeList.item(i).getFirstChild().getTextContent());
				this.getMethodSnippetList.add(getterNodeList.item(i).getFirstChild().getTextContent());
				this.setMethodSnippetList.add(setterNodeList.item(i).getFirstChild().getTextContent());
			}

			if (classesToImport != null) {
				this.classToImport = classesToImport.item(0).getTextContent().split(COMMA);
			}
		} catch (final Exception ex) {
			throw ex;
		} finally {
			closeInputStream(inputStream);
		}

	}

	/**
	 * @param methodSnippet
	 * @param type
	 * @throws Exception
	 */
	private void createMethods(final String methodSnippet, final IType type) throws Exception {
		if (!checkForMethod(methodSnippet, type.getCompilationUnit())) {
			return;
		}
		IMethod method;
		try {
			method = type.createMethod(methodSnippet, null, false, null);
			if (method == null || !method.exists()) {
				throw new Exception("Unable to create method.");
			}
		} catch (final Exception ex) {
			ex.printStackTrace();
			throw new Exception("Unable to create mehtod, template may be wrong. " + ex.getMessage() + methodSnippet, ex);
		}
	}

	/**
	 * @param fieldSnippet
	 * @param type
	 * @throws Exception
	 */
	private void createFields(final String fieldSnippet, final IType type) throws Exception {
		if (!checkForField(fieldSnippet, type.getCompilationUnit())) {
			return;
		}
		IField field;
		try {
			field = type.createField(fieldSnippet, null, false, null);
			if (field == null || !field.exists()) {
				throw new Exception("Unable to create Field.");
			}
		} catch (final Exception ex) {
			ex.printStackTrace();
			throw new Exception("Unable to create field, template may be wrong. " + ex.getMessage() + fieldSnippet, ex);
		}
	}

	/**
	 * @param snippet
	 * @param compilationUnit
	 * @return
	 * @throws Exception
	 */
	private boolean checkForField(final String snippet, final ICompilationUnit compilationUnit) throws Exception {
		final String fieldName = StringUtil.parseFieldName(snippet);
		if (fieldName == null) {
			if (!MessageDialog.openConfirm(new Shell(), "Warning", "Template may be incorrect. Would you like to proceed?")) {
				return false;
			}
		}
		final IType type = compilationUnit.findPrimaryType();
		if (SourceUtil.doesFieldExistsInType(type, fieldName)) {
			if (!MessageDialog.openConfirm(new Shell(), "Warning", "Field with name " + fieldName
					+ " already exists. Would you like to proceed?")) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param editorPart
	 * @return
	 */
	protected ICompilationUnit getCompilationUnitFromEditor(final IEditorPart editorPart) {
		final IWorkingCopyManager manager = JavaUI.getWorkingCopyManager();
		final ICompilationUnit compilationUnit = manager.getWorkingCopy(editorPart.getEditorInput());
		return compilationUnit != null && compilationUnit.exists() ? compilationUnit : null;
	}

	/**
	 * @param typeToImport
	 * @throws Exception
	 */
	private void createImports(final String typeToImport) throws Exception {
		if (typeToImport != null) {
			final IImportDeclaration imprt = this.compilationUnit.getImport(typeToImport);

			if (imprt == null || !imprt.exists()) {
				this.compilationUnit.createImport(typeToImport, null, null);
			}
		}
	}
}
