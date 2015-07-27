package org.fastcode.popup.actions.snippet;

import static org.eclipse.jdt.ui.JavaUI.openInEditor;
import static org.eclipse.jface.window.Window.CANCEL;
import static org.fastcode.common.FastCodeConstants.COMMA;
import static org.fastcode.common.FastCodeConstants.DATABASE_NAME;
import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.ENTITY_STR;
import static org.fastcode.common.FastCodeConstants.EXIT_KEY;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.INSTANCE_STR;
import static org.fastcode.common.FastCodeConstants.NAMED_QUERIES_ANNOT_1;
import static org.fastcode.common.FastCodeConstants.NAMED_QUERIES_ANNOT_2;
import static org.fastcode.common.FastCodeConstants.NAMED_QUERIES_STR;
import static org.fastcode.common.FastCodeConstants.NAMED_QUERY_ANNOTATION_STR;
import static org.fastcode.common.FastCodeConstants.NAMED_QUERY_ANNOT_1;
import static org.fastcode.common.FastCodeConstants.NAMED_QUERY_ANNOT_2;
import static org.fastcode.common.FastCodeConstants.NAMED_QUERY_ANNOT_3;
import static org.fastcode.common.FastCodeConstants.NAMED_QUERY_STR;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_TARGET;
import static org.fastcode.common.FastCodeConstants.QUERY_NAME_STR;
import static org.fastcode.common.FastCodeConstants.SCHEMA;
import static org.fastcode.common.FastCodeConstants.SELECTED_POJO_CLASS_ITYPE;
import static org.fastcode.common.FastCodeConstants.TABLE;
import static org.fastcode.common.FastCodeConstants.XML_END;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_ENABLE_TEMPLATE_BODY_IN_DIALOG_BOXES;
import static org.fastcode.preferences.PreferenceConstants.P_HQL_NAMED_QUERY_FILE_LOCATION;
import static org.fastcode.util.DatabaseUtil.getSchemaFromDb;
import static org.fastcode.util.SourceUtil.findFileFromPath;
import static org.fastcode.util.StringUtil.containsXmlStructure;
import static org.fastcode.util.StringUtil.evaluateByVelocity;
import static org.fastcode.util.StringUtil.getGlobalSettings;
import static org.fastcode.util.StringUtil.getTemplateTagEnd;
import static org.fastcode.util.StringUtil.getTemplateTagStart;
import static org.fastcode.util.StringUtil.isEmpty;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.internal.ide.dialogs.OpenResourceDialog;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.fastcode.Activator;
import org.fastcode.common.CreateQueryData;
import org.fastcode.common.CreateSnippetData;
import org.fastcode.common.FastCodeConstants.TemplateTag;
import org.fastcode.common.StringSelectionDialog;
import org.fastcode.common.XmlElement;
import org.fastcode.dialog.CreateQueryDialog;
import org.fastcode.preferences.DatabaseConnectionSettings;
import org.fastcode.setting.GlobalSettings;
import org.fastcode.setting.TemplateSettings;
import org.fastcode.util.MessageUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public abstract class CreateSqlGenericNamedQueryViewAction extends AbstractCreateNewDatabaseSnippetAction implements IEditorActionDelegate,
		IActionDelegate, IWorkbenchWindowActionDelegate {

	/**
	 * @param templateSettings
	 * @param placeHolders
	 *
	 *
	 */
	@Override
	protected void initializePlaceHolders(final TemplateSettings templateSettings, final Map<String, Object> placeHolders) throws Exception {
		final boolean namedQuerySnippet = false;
		final Shell shell = this.editorPart == null || this.editorPart.getSite() == null ? new Shell() : this.editorPart.getSite()
				.getShell();

		/*if (isEmpty(this.templateSettings.getTemplateVariationField())) {
			final GlobalSettings globalSettings = GlobalSettings.getInstance();
			if ("named-query".equals(globalSettings.getPropertyValue("hql.variation.default", EMPTY_STR))) {
				namedQuerySnippet = true;
			}
		} else {
			final Object variation = placeHolders.get(this.templateSettings.getTemplateVariationField());
			if ("named-query".equals(variation)) {
				namedQuerySnippet = true;
			}
		}

		if (!namedQuerySnippet) {
			return;
		}*/

		final CreateQueryData createQueryData = getCreateQueryData();

		if (createQueryData == null) {
			placeHolders.put(EXIT_KEY, Boolean.TRUE);
			return;
		}

		final IFile namedQueryFile = createQueryData.getiNamedqueryFile(); //this.getNamedQueryFile();
		if (namedQueryFile == null) {
			MessageDialog.openError(shell, "Error", "Unable to find name query file.");
			return;
		}

		if (!namedQueryFile.isSynchronized(0)) {
			throw new Exception(namedQueryFile.getName() + " is not Synchronized, please refresh and try again.");
		}
		placeHolders.put("namedQueryFile", namedQueryFile);
		placeHolders.put(PLACEHOLDER_TARGET, namedQueryFile.getFullPath().toString());

		if (createQueryData.getModifiedTemplateBody() != null) {
			placeHolders.put("ModifiedTemplateBody", createQueryData.getModifiedTemplateBody());
		}

		//final String[] choices = {"Create new named query", "Use an existing named query", "Do not use named query this time."};

		//		final String choice = getChoiceFromMultipleValues(shell, "Query Choice", "Do you want to?", choices);
		final int result = createQueryData.getChoice();//findInStringArray(choice, choices);

		String queryName = null;
		String existingQueryName = null;
		/*final GlobalSettings globalSettings = GlobalSettings.getInstance();
		final String namedQueryRootNodeName = globalSettings.getPropertyValue("named.query.rootnode", "entity-mappings").trim();

		final String namedQueryNodeName = globalSettings.getPropertyValue("named.query.node", "named-query").trim();

		final String namedQueryFileContents = getFileContents(namedQueryFile);
		final String[] namedQueries = StringUtil.findAttributes(namedQueryNodeName, "name", namedQueryFileContents);*/
		final String[] namedQueries = createQueryData.getExistingNamedQueries();
		placeHolders.put("namedQueryFileContents", createQueryData.getNamedQueryFileContent()); //namedQueryFileContents);
		placeHolders.put(TABLE, createQueryData.getTableName());
		placeHolders.put(INSTANCE_STR, createQueryData.getTableName().substring(0, 1).toLowerCase());
		placeHolders.put(SCHEMA, createQueryData.getSchemaSelected());
		placeHolders.put(SELECTED_POJO_CLASS_ITYPE, createQueryData.getiSelectPojoClassType());
		placeHolders.put(DATABASE_NAME, createQueryData.getSelectedDatabaseName());
		switch (result) {
		case 0:
			existingQueryName = createQueryData.getExistingNamedQueryType();
			queryName = createQueryData.getNewNamedQueryType();
			//queryName = this.getNamedQueryName(namedQueryFileContents, namedQueryRootNodeName, namedQueries, placeHolders);
			if (queryName == null) {
				return;
			}

			placeHolders.put("existingNamedQuery", existingQueryName);
			placeHolders.put("queryName", queryName);
			//placeHolders.put("where_qualifier", createQueryData.getWhereClauseQualifier().getValue());
			placeHolders.put("where_separator", createQueryData.getWhereClauseSeparator());

			break;
		case 1:
			if (namedQueries == null || namedQueries.length == 0) {
				MessageUtil.showWarning("No named queies exists. Please try again.", "Warning");
				return;
			}
			queryName = createQueryData.getExistingNamedQueryType();
			//queryName = this.getSelectedNamedQuery(shell, "Select the named query to use", namedQueries);
			if (queryName == null) {
				return;
			}
			placeHolders.put("queryName", queryName);
			placeHolders.put("useExistingNamedQuery", Boolean.TRUE);
			//placeHolders.put("where_qualifier", createQueryData.getWhereClauseQualifier().getValue());
			placeHolders.put("where_separator", createQueryData.getWhereClauseSeparator());

			break;
		case 2:
			//placeHolders.put("where_qualifier", createQueryData.getWhereClauseQualifier().getValue());
			placeHolders.put("where_separator", createQueryData.getWhereClauseSeparator());

			break;
		}
		super.initializePlaceHolders(templateSettings, placeHolders);
	}

	/**
	 * @param templateType
	 * @param placeHolders
	 * @param fieldSelection
	 * @param spacesBeforeCursor
	 *
	 */
	@Override
	protected void createSnippet(final Map<String, Object> placeHolders, final Map<String, Object> fieldSelection,
			final String spacesBeforeCursor) throws Exception {
		final Shell shell = this.editorPart == null || this.editorPart.getSite() == null ? new Shell() : this.editorPart.getSite()
				.getShell();

		final GlobalSettings globalSettings = GlobalSettings.getInstance();
		final String namedQueryNodeName = globalSettings.getPropertyValue("named.query.node", "entity-mappings").trim();

		final boolean namedQuerySnippet = placeHolders.containsKey("queryName");
		if (!namedQuerySnippet) {
			super.createSnippet(placeHolders, fieldSelection, spacesBeforeCursor);
			return;
		}

		final String namedQueryRootNode = globalSettings.getPropertyValue("named.query.rootnode", "entity-mappings");
		final String queryRootNodeEndTag = "</" + namedQueryRootNode + ">";
		final String namedQueryFileContents = (String) placeHolders.get("namedQueryFileContents");

		/*final TemplateSettings templateSettingsNamedQry = TemplateSettings.getTemplateSettings(getTemplateTypeForNamedQuery(),
				P_ADDITIONAL_DATABASE_TEMPLATE_PREFIX);
		if (templateSettingsNamedQry == null) {
			MessageDialog.openError(new Shell(), "Error", "Unable for find template for " + getTemplateTypeForNamedQuery());
		}*/
		/*the below if and the create annotation can be removed after fc:annotation is implemented*/
		final IPreferenceStore preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		if (this.createSnippetData.getTemplateBodyFromSnippetDialog() == null
				&& preferenceStore.getBoolean(P_GLOBAL_ENABLE_TEMPLATE_BODY_IN_DIALOG_BOXES)) {
			final String modifiedTemplateBody = (String) placeHolders.get("ModifiedTemplateBody");
			this.createSnippetData.setTemplateBodyFromSnippetDialog(modifiedTemplateBody);
		} else if (!preferenceStore.getBoolean(P_GLOBAL_ENABLE_TEMPLATE_BODY_IN_DIALOG_BOXES)) {
			this.createSnippetData.setTemplateBodyFromSnippetDialog(this.templateSettings.getTemplateBody());
		}
		String snippet = EMPTY_STR;
		final String templateBody = this.createSnippetData.getTemplateBodyFromSnippetDialog(); //templateSettingsNamedQry.getTemplateBody();
		getGlobalSettings(placeHolders);
		snippet = evaluateByVelocity(templateBody, placeHolders, fieldSelection);
		snippet = snippet.replace("&lt;", "<");
		snippet = snippet.replace("&gt;", ">");
		if (snippet.contains(getTemplateTagStart(TemplateTag.XML))) {
			final int startTag = snippet.indexOf(getTemplateTagStart(TemplateTag.XML), 0);
			final int startTagEnd = snippet.indexOf(XML_END, startTag);
			final int endTag = snippet.indexOf(getTemplateTagEnd(TemplateTag.XML), startTag);
			final String insideTagBody = snippet.substring(startTagEnd + 1, endTag);
			/*if (!placeHolders.containsKey("useExistingNamedQuery")) {
				int pos = -1;
				final String exstNamedQuery = (String) placeHolders.get("existingNamedQuery");
				if (placeHolders.containsKey("existingNamedQuery")) {
					pos = findNodePosition(namedQueryRootNode, namedQueryNodeName, namedQueryFileContents, "name", exstNamedQuery);
				}

				final int positionToInsertQuery = pos == -1 ? namedQueryFileContents.indexOf(queryRootNodeEndTag) : findPositionToInsertQuery(
						namedQueryFileContents, namedQueryNodeName, exstNamedQuery, pos);

				if (positionToInsertQuery == -1) {
					throw new Exception("Named query File Does not seem right.");
				}

				final String templateBody = templateSettingsNamedQry.getTemplateBody();
				getGlobalSettings(placeHolders);
				snippet = evaluateByVelocity(templateBody, placeHolders, fieldSelection);
				snippet = snippet.replace("&lt;", "<");
				snippet = snippet.replace("&gt;", ">");

				final IFile namedQueryFile = (IFile) placeHolders.get("namedQueryFile");
				final IEditorPart editorPart = getEditorPartFromFile(namedQueryFile);
				final ITextEditor editor = (ITextEditor) editorPart.getAdapter(ITextEditor.class);
				final IDocumentProvider documentProvider = editor.getDocumentProvider();
				final IDocument document = documentProvider.getDocument(editor.getEditorInput());
				document.replace(positionToInsertQuery, 0, NEWLINE + formatXmlWithCDATA(snippet));
			}*/
			if (requireAnnotation() && !isEmpty(insideTagBody)) {
				createAnnotations(insideTagBody, placeHolders);
				snippet = snippet.substring(0, startTag);
				placeHolders.put("ModifiedTemplateBody", snippet.trim());
			}
		}
		super.createSnippet(placeHolders, fieldSelection, spacesBeforeCursor);
	}

	/**
	 * @param snippet
	 * @param placeHolders
	 * @throws Exception
	 */
	private void createAnnotations(final String snippet, final Map<String, Object> placeHolders) throws Exception {
		//final ICompilationUnit compUnit = getCompilationUnitFromEditor();
		final IType pojoClassType = (IType) placeHolders.get(SELECTED_POJO_CLASS_ITYPE);//compUnit.getJavaProject().findType("com.example.pojo.booksPOJO");//take this from cq data
		final IAnnotation[] annotations = pojoClassType.getAnnotations();
		IAnnotation entityAnnotation = null;
		IAnnotation namedQueryAnnotation = null;

		if (annotations != null && annotations.length > 0) {
			for (final IAnnotation annotation : annotations) {
				if (annotation.getElementName().equals(NAMED_QUERIES_STR)) {
					namedQueryAnnotation = annotation;
				}

				if (annotation.getElementName().equals(ENTITY_STR)) {
					entityAnnotation = annotation;
				}
			}
		}
		String addAnnotationSnippet = EMPTY_STR;
		int pos = -1;
		if (namedQueryAnnotation == null) {
			if (entityAnnotation != null) {
				pos = pojoClassType.getCompilationUnit().getSource().indexOf(entityAnnotation.getSource()) - 1;
			}
			addAnnotationSnippet = "import javax.persistence.NamedQueries;\nimport javax.persistence.NamedQuery;\n" + NAMED_QUERIES_ANNOT_1
					+ NAMED_QUERY_ANNOT_1 + placeHolders.get(TABLE) + DOT + placeHolders.get(QUERY_NAME_STR) + NAMED_QUERY_ANNOT_2
					+ parseQueryFromSnippet(snippet) + NAMED_QUERY_ANNOT_3 + NAMED_QUERIES_ANNOT_2;

		} else {
			pos = pojoClassType.getCompilationUnit().getSource().indexOf(namedQueryAnnotation.getSource())
					+ namedQueryAnnotation.getSource().length() - 2;
			addAnnotationSnippet = COMMA + NAMED_QUERY_ANNOT_1 + placeHolders.get(TABLE) + DOT + placeHolders.get(QUERY_NAME_STR)
					+ NAMED_QUERY_ANNOT_2 + parseQueryFromSnippet(snippet) + NAMED_QUERY_ANNOT_3;

		}
		//final ITextSelection selection = (ITextSelection) this.editorPart.getEditorSite().getSelectionProvider().getSelection();
		final IEditorPart editorPart = openInEditor(pojoClassType.getCompilationUnit());
		final ITextEditor editor = (ITextEditor) editorPart.getAdapter(ITextEditor.class);
		final IDocumentProvider documentProvider = editor.getDocumentProvider();
		final IDocument document = documentProvider.getDocument(editor.getEditorInput());
		final String finalSnippet = addAnnotationSnippet.trim();
		document.replace(pos, 0, finalSnippet);
		editorPart.doSave(new NullProgressMonitor());

	}

	/**
	 * @param snippet
	 * @return
	 * @throws Exception
	 */
	private String parseQueryFromSnippet(final String snippet) throws Exception {
		InputStream inputStream = null;

		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			inputStream = new ByteArrayInputStream(snippet.getBytes("UTF-8"));
			final DocumentBuilder docBuilder = factory.newDocumentBuilder();
			final Document document = docBuilder.parse(inputStream);
			//final NodeList queryNodeList = document.getElementsByTagName("query");
			final Node queryNode = document.getFirstChild();
			return queryNode.getTextContent().trim();

		} catch (final Exception ex) {
			throw ex;
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}

	/**
	 *
	 * @param namedQueryFileContents
	 * @param namedQueryNodeName
	 * @param exstNamedQuery
	 * @param n
	 * @return
	 */
	private int findPositionToInsertQuery(final String namedQueryFileContents, final String namedQueryNodeName,
			final String exstNamedQuery, final int n) {
		int off = 0;
		final String queryEndTag = "</" + namedQueryNodeName + ">";
		int i = 0;
		while (i <= n) {
			off = namedQueryFileContents.indexOf(queryEndTag, off);
			if (off == -1) {
				return -1;
			}
			off += queryEndTag.length();
			i++;
		}
		return off;
	}

	/**
	 *
	 * @param namedQueryFileContents
	 * @param namedQueryRootNode
	 * @param placeHolders
	 * @param placeHolders
	 * @param namedQueryFile
	 * @param fieldSelection
	 * @return
	 * @throws Exception
	 */
	private String getNamedQueryName(final String namedQueryFileContents, final String namedQueryRootNode, final String[] exstNamedQueries,
			final Map<String, Object> placeHolders) throws Exception {

		final Shell shell = this.editorPart == null || this.editorPart.getSite() == null ? new Shell() : this.editorPart.getSite()
				.getShell();

		final String exstNamedQuery = getSelectedNamedQuery(shell, "Select the named query to put next to", exstNamedQueries);

		while (true) {
			final InputDialog inputDialog = new InputDialog(shell, "Query Name", "Enter Name of the Query",
					exstNamedQuery == null ? EMPTY_STR : exstNamedQuery, new IInputValidator() {
						@Override
						public String isValid(final String newText) {
							if (newText.trim().equals(EMPTY_STR)) {
								return "Name of the Query cannot be an empty string.";
							} else if (newText.trim().equals(exstNamedQuery)) {
								return "Name of the Query cannot be same as an existing one.";
							}
							return null;
						}
					});
			if (inputDialog.open() == CANCEL) {
				return null;
			}

			final String queryName = inputDialog.getValue().trim();
			if (doesQueryNameExists(queryName, namedQueryFileContents, namedQueryRootNode)) {
				MessageUtil.showWarning("Named Query Exists, please select another name.", "Warning");
				continue;
			}
			if (exstNamedQuery != null) {
				placeHolders.put("existingNamedQuery", exstNamedQuery);
			}
			return queryName;
		}
	}

	/**
	 *
	 * @param shell
	 * @param title
	 * @param namedQueries
	 * @return
	 */
	private String getSelectedNamedQuery(final Shell shell, final String title, final String[] namedQueries) {
		final StringSelectionDialog selectionDialog = new StringSelectionDialog(shell, "Named Query", title, namedQueries, false);
		if (selectionDialog.open() == CANCEL) {
			return null;
		}
		return (String) selectionDialog.getFirstResult();
	}

	/**
	 * @param placeHolders
	 * @param fileContents
	 * @param namedQueryRootNode
	 * @throws Exception
	 */
	private boolean doesQueryNameExists(final String queryName, final String fileContents, final String namedQueryRootNode)
			throws Exception {
		final GlobalSettings globalSettings = GlobalSettings.getInstance();

		final String namedQueryNode = globalSettings.getPropertyValue("named.query.node", "sql-query");
		final XmlElement element = new XmlElement(namedQueryNode == null ? "sql-query" : namedQueryNode);
		element.addAttribute("name", queryName);

		return containsXmlStructure(element, namedQueryRootNode, fileContents);
	}

	/**
	 *
	 * @return
	 */
	public IFile getNamedQueryFile() {
		final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		if (preferenceStore.contains(P_HQL_NAMED_QUERY_FILE_LOCATION)) {
			final IFile namedQueryFile = findFileFromPath(preferenceStore.getString(P_HQL_NAMED_QUERY_FILE_LOCATION));
			if (namedQueryFile != null) {
				return namedQueryFile;
			}
		}

		final GlobalSettings globalSettings = GlobalSettings.getInstance();
		final String namedQueryFileLocation = globalSettings.getPropertyValue("named.query.file.location", EMPTY_STR).trim();
		if (!isEmpty(namedQueryFileLocation)) {
			final IFile namedQueryFile = findFileFromPath(namedQueryFileLocation);
			if (namedQueryFile != null) {
				preferenceStore.setValue(P_HQL_NAMED_QUERY_FILE_LOCATION, namedQueryFileLocation);
				return namedQueryFile;
			}
		}

		final OpenResourceDialog resourceDialog = new OpenResourceDialog(new Shell(), ResourcesPlugin.getWorkspace().getRoot(),
				IResource.FILE);
		resourceDialog.setTitle("Named Query File");
		resourceDialog.setMessage("Choose Named Query File");
		final int res = resourceDialog.open();
		if (res == CANCEL || resourceDialog.getResult() == null || resourceDialog.getResult().length == 0) {
			return null;
		}
		final IFile namedQueryFile = (IFile) resourceDialog.getFirstResult();
		final IPath fullPath = namedQueryFile.getFullPath();
		preferenceStore.setValue(P_HQL_NAMED_QUERY_FILE_LOCATION, fullPath.toString());
		return namedQueryFile;
	}

	@Override
	protected boolean doShowEmbeddedFields(final TemplateSettings templateSettings, final String templateType) {
		return true;
	}

	//public abstract String getTemplateTypeForNamedQuery();

	protected abstract String[] getFieldTypesForHql();

	/**
	 *
	 * @return
	 * @throws Exception
	 */
	protected CreateQueryData getCreateQueryData() throws Exception {
		final DatabaseConnectionSettings databaseConnectionSettings = DatabaseConnectionSettings.getInstance();
		final CreateQueryData createQueryData = new CreateQueryData();
		this.isNamedQuery = true;
		// getTableFromDb(this.con);
		// createQueryData.setTablesInDB(this.databaseCache.getDbTableListMap().get(databaseConnectionSettings.getTypesofDabases()));
		getSchemaFromDb(this.con, databaseConnectionSettings.getTypesofDabases());
		createQueryData.setSchemasInDB(this.databaseCache.getDbSchemaListMap().get(databaseConnectionSettings.getTypesofDabases()));
		createQueryData.setTemplatePrefix(this.templatePrefix);
		createQueryData.setTemplateType(this.templateType);
		createQueryData.setTemplateSettings(getTemplateSettings(this.templateType));
		final CreateQueryDialog createQueryDialog = new CreateQueryDialog(new Shell(), createQueryData);
		if (createQueryDialog.open() == Window.CANCEL) {
			if (this.con != null) {
				this.con.close();
			}
			return null;
		}
		//this.tableName = createQueryData.getTableName();
		return createQueryDialog.getCreateQueryData();
	}

	/* (non-Javadoc)
	 * @see org.fastcode.popup.actions.snippet.AbstractCreateNewDatabaseSnippetAction#getCreateSnippetData(java.lang.String)
	 */
	@Override
	public CreateSnippetData getCreateSnippetData(final String fileName) throws Exception {
		final CreateSnippetData createSnippetData = new CreateSnippetData();
		createSnippetData.setTemplatePrefix(this.templatePrefix);
		createSnippetData.setTemplateType(this.templateType);
		createSnippetData.setTemplateSettings(getTemplateSettings(this.templateType));
		createSnippetData.setTemplateVariationField(getTemplateVariationField());

		if (this.templateType.endsWith(NAMED_QUERY_STR)) {
			createSnippetData.setVariationsSelected(new String[] { "named-query" });
		} else if (this.templateType.endsWith(NAMED_QUERY_ANNOTATION_STR)) {
			createSnippetData.setVariationsSelected(new String[] { "named-query-annotation" });
		}
		return createSnippetData;
	}

}
