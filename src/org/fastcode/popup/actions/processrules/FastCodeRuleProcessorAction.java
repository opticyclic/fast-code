package org.fastcode.popup.actions.processrules;

import static org.eclipse.jdt.core.Flags.AccStatic;
import static org.eclipse.jface.dialogs.MessageDialog.openWarning;
import static org.eclipse.jface.window.Window.CANCEL;
import static org.fastcode.common.FastCodeConstants.ACTON_SELECTED_STR;
import static org.fastcode.common.FastCodeConstants.COMMA;
import static org.fastcode.common.FastCodeConstants.CREATE_SIMILAR_CLASSES_ACTION;
import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FORWARD_SLASH;
import static org.fastcode.common.FastCodeConstants.HASH;
import static org.fastcode.common.FastCodeConstants.HASH_CHAR;
import static org.fastcode.common.FastCodeConstants.INSTANCE_STR;
import static org.fastcode.common.FastCodeConstants.LEFT_CURL;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_FIELD;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_INPUT;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_NAME;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_PACKAGE;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_PROJECT;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_RESULT;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_SOURCE;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_SRC_PATH;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_STATIC_IMPORT;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_SUPER_INTERFACE;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_TARGET;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_TITLE;
import static org.fastcode.common.FastCodeConstants.RIGHT_CURL;
import static org.fastcode.common.FastCodeConstants.TRUE_STR;
import static org.fastcode.setting.GlobalSettings.getInstance;
import static org.fastcode.util.SourceUtil.backUpExistingExportFile;
import static org.fastcode.util.SourceUtil.checkForErrors;
import static org.fastcode.util.SourceUtil.createFolder;
import static org.fastcode.util.SourceUtil.getAllSourcePathsInWorkspace;
import static org.fastcode.util.SourceUtil.getJavaProject;
import static org.fastcode.util.SourceUtil.getPackageRootFromProject;
import static org.fastcode.util.SourceUtil.getPathFromGlobalSettings;
import static org.fastcode.util.SourceUtil.getPathFromUser;
import static org.fastcode.util.SourceUtil.getProjectsFromWorkspace;
import static org.fastcode.util.SourceUtil.getSuperInterfacesType;
import static org.fastcode.util.SourceUtil.getTypeFromWorkspace;
import static org.fastcode.util.SourceUtil.implementInterfaceMethods;
import static org.fastcode.util.StringUtil.containsAnyPlaceHolder;
import static org.fastcode.util.StringUtil.evaluateByVelocity;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.StringUtil.replacePlaceHolder;
import static org.fastcode.util.TemplateUtil.findOrcreateTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sourceforge.fastCode.xsd.fastCodeRules.ActionsDocument;
import net.sourceforge.fastCode.xsd.fastCodeRules.ActionsDocument.Actions;
import net.sourceforge.fastCode.xsd.fastCodeRules.ActionsDocument.Actions.Action;
import net.sourceforge.fastCode.xsd.fastCodeRules.ActionsDocument.Actions.Action.ActionRef;
import net.sourceforge.fastCode.xsd.fastCodeRules.ActionsDocument.Actions.Action.ActionRef.Parameters;
import net.sourceforge.fastCode.xsd.fastCodeRules.ActionsDocument.Actions.Action.ActionRef.Parameters.Parameter;
import net.sourceforge.fastCode.xsd.fastCodeRules.ActionsDocument.Actions.Action.Rules;
import net.sourceforge.fastCode.xsd.fastCodeRules.Properties;
import net.sourceforge.fastCode.xsd.fastCodeRules.Properties.Property;
import net.sourceforge.fastCode.xsd.fastCodeRules.Rule;
import net.sourceforge.fastCode.xsd.fastCodeRules.Rule.RuleActions;
import net.sourceforge.fastCode.xsd.fastCodeRules.Rule.RuleActions.RuleAction;
import net.sourceforge.fastCode.xsd.fastCodeRules.Select;
import ognl.Ognl;
import ognl.OgnlContext;

import org.apache.xmlbeans.XmlException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.IWorkingCopyManager;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.fastcode.Activator;
import org.fastcode.common.FastCodeConstants.ACTION_ENTITY;
import org.fastcode.common.FastCodeConstants.ACTION_TYPE;
import org.fastcode.common.FastCodeConstants.CLASS_TYPE;
import org.fastcode.common.FastCodeField;
import org.fastcode.common.FastCodeMethod;
import org.fastcode.common.FastCodeType;
import org.fastcode.common.PackageSelectionDialog;
import org.fastcode.common.ProjectSelectionDialog;
import org.fastcode.common.StringSelectionDialog;
import org.fastcode.popup.actions.snippet.DefaultSnippetCreator;
import org.fastcode.setting.GlobalSettings;
import org.fastcode.util.CreateSimilarDescriptorClass;
import org.fastcode.util.FastCodeContext;

public class FastCodeRuleProcessorAction implements IEditorActionDelegate, IActionDelegate, IWorkbenchWindowActionDelegate {
	protected IWorkbenchWindow			window;
	protected IEditorPart				editorPart;

	public static final String			ABS_DAO_GET_METHOD_FORMAT		= "public ${name}Vo get${name}ById(final Long ${instance}Id);";

	public static final String			ABS_DAO_RETRIEVE_METHOD_FORMAT	= "public ${name}Vo retrieve${name}(final String str);";

	public static final String			ABS_DAO_SAVE_METHOD_FORMAT		= "public void save${name}(${name}Vo ${instance}Vo);";

	public static String[]				inbuiltMethods					= { ABS_DAO_GET_METHOD_FORMAT, ABS_DAO_RETRIEVE_METHOD_FORMAT,
			ABS_DAO_SAVE_METHOD_FORMAT									};
	public static Map<String, String>	inbuiltMethodsMap				= new HashMap<String, String>();

	protected Set<FastCodeType>			fastCodeTypeSet					= new HashSet<FastCodeType>();
	protected Set<FastCodeMethod>		fastCodeMethodSet				= new HashSet<FastCodeMethod>();
	protected Set<FastCodeField>		fastCodeFieldSet				= new HashSet<FastCodeField>();

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(final IWorkbenchWindow window) {
		this.window = window;
	}

	@Override
	public void run(final IAction arg0) {
		try {
			if (this.window != null) {
				this.editorPart = this.window.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			}

			processRulesActions();
		} catch (final XmlException ex) {
			ex.printStackTrace();
		} catch (final IOException ex) {
			ex.printStackTrace();
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * @throws Exception
	 */
	private void processRulesActions() throws Exception {
		InputStream inputStream = null;
		final String fastCodeRulesFile = "fast-code-rules.xml";
		final String folderName = "rules";
		final Map<String, Object> placeHolders = new HashMap<String, Object>();
		try {
			final IFile rulesFile = findOrcreateTemplate(fastCodeRulesFile, folderName);
			if (rulesFile == null || !rulesFile.exists()) {
				inputStream = FileLocator.openStream(Activator.getDefault().getBundle(), new Path("resources/rules/" + fastCodeRulesFile),
						false);
			} else {
				inputStream = rulesFile.getContents();
				if (checkForErrors(rulesFile)) {
					throw new Exception("Fast Code Rules File " + rulesFile + " has some errors, please fix them try again.");
				}
			}

			if (inputStream == null) {
				throw new IllegalArgumentException("Invalid file " + fastCodeRulesFile);
			}

			//final File ruleXML = new File(inputStream.);

			placeHolders.put("suffix", EMPTY_STR);
			final ActionsDocument actionsDoc = ActionsDocument.Factory.parse(inputStream);
			final Actions actions = actionsDoc.getActions();
			final Action[] actionArr = actions.getActionArray();

			final List<String> actionNameList = new ArrayList<String>();
			final Map<String, Action> actionNameActionMap = new HashMap<String, ActionsDocument.Actions.Action>();
			for (final Action action : actionArr) {
				if (action.getType() == null) { // && !action.getType().equals("abstract")) {
					actionNameList.add(action.getName());
				}
				actionNameActionMap.put(action.getName(), action);
			}

			final StringSelectionDialog selectionDialog = new StringSelectionDialog(new Shell(), "Actions", "Choose Action",
					actionNameList.toArray(new String[0]), false);
			if (selectionDialog.open() == CANCEL) {
				return;
			}

			final Action actionSelected = actionNameActionMap.get(selectionDialog.getFirstResult());

			processAction(actionSelected, actionNameActionMap, placeHolders);

		} catch (final Exception ex) {
			ex.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (final IOException ex) {
					ex.printStackTrace();
				}

			}

			placeHolders.clear();
		}

	}

	/**
	 * @param actionSelected
	 * @param actionNameActionMap
	 * @param placeHolders
	 * @throws Exception
	 */
	private void processAction(final Action actionSelected, final Map<String, Action> actionNameActionMap,
			final Map<String, Object> placeHolders) throws Exception {

		boolean reference = false;

		final ActionRef[] actionRefArr = actionSelected.getActionRefArray();

		placeHolders.put(ACTON_SELECTED_STR, actionSelected);
		final Properties properties = actionSelected.getProperties();
		if (properties != null) {
			final Property propertyArr[] = properties.getPropertyArray();
			for (final Property property : propertyArr) {
				placeHolders.put(property.getName().getStringValue(), property.getValue().getStringValue());
			}
		}
		if (actionRefArr != null) {
			reference = true;
			for (final ActionRef actionRef : actionRefArr) {
				final String actionName = actionRef.getActionName();
				final Parameters parameters = actionRef.getParameters();
				if (parameters != null) {
					final Parameter[] parameterArr = parameters.getParameterArray();
					for (final Parameter parameter : parameterArr) {
						placeHolders.put(parameter.getName(), parameter.getValue());
					}
					final Map<String, Object> contextMap = new HashMap<String, Object>();
					processRules(actionNameActionMap.get(actionName), null, placeHolders, contextMap);
					if (actionRef.getName() != null) {
						placeHolders.put(actionRef.getName(), contextMap);
					}
				}
			}
		}

		final Rules rules = actionSelected.getRules();

		if (rules != null) {
			final Map<String, Object> contextMap = new HashMap<String, Object>();
			processRules(null, rules, placeHolders, contextMap);
		}

		if (actionSelected.getName().equals(CREATE_SIMILAR_CLASSES_ACTION)) {
			createDefaultMethods(placeHolders);
			MessageDialog.openInformation(new Shell(), "Success", CREATE_SIMILAR_CLASSES_ACTION + " action was successfully completed.");
		}

	}

	/**
	 * @param action
	 * @param rules
	 * @param placeHolders
	 * @param contextMap
	 * @throws Exception
	 */
	private void processRules(final Action action, final Rules rules, final Map<String, Object> placeHolders,
			final Map<String, Object> contextMap) throws Exception {
		Rule[] ruleArr = null;

		if (action != null) {
			/*if (action.getActionRefArray() != null) {
				//loop thru each refer action -- call ProcessRules
				processAction(action, actionNameActionMap,placeHolders);
			}*/
			if (action.getRules() == null) {
				MessageDialog.openError(new Shell(), "Rule not Found", "Rule not found for action " + action.getName());
			}
			ruleArr = action.getRules().getRuleArray();
		} else if (rules != null) {

			ruleArr = rules.getRuleArray();
		} else {
			MessageDialog.openError(new Shell(), "Rule not Found", "Rule not found");
		}

		/*final List<String> ruleActionsNameList = new ArrayList<String>();
		final Map<String, RuleActions> ruleActionsNameRuleActionsMap = new HashMap<String, RuleActions>();
		for(final Rule rule : ruleArr){
			final RuleActions ruleActions = rule.getRuleActions();


			ruleActionsNameList.add(ruleActions.getName().getStringValue());
			ruleActionsNameRuleActionsMap.put(ruleActions.getName().getStringValue(), ruleActions);
		}


		final StringSelectionDialog selectionDialog = new StringSelectionDialog(new Shell(), "Actions", "Choose Action", ruleActionsNameList.toArray(new String[0]), false);
		if (selectionDialog.open() == CANCEL) {
			return;
		}

		final RuleActions  ruleActionsSelected = ruleActionsNameRuleActionsMap.get(selectionDialog.getFirstResult());
		final String ruleActionsName = (String) selectionDialog.getFirstResult();
		final RuleAction[] ruleActionArr = ruleActionsSelected.getRuleActionArray();*/

		for (final Rule rule : ruleArr) {
			final RuleActions ruleActions = rule.getRuleActions();
			final RuleAction[] ruleActionArr = ruleActions.getRuleActionArray();

			for (final RuleAction ruleAction : ruleActionArr) {
				final String actionType = ruleAction.getActionType() != null ? ruleAction.getActionType().toString() : null;
				//				final String input = ruleAction.getInput() != null ? ruleAction.getInput().getStringValue() : null;
				//				final String target = ruleAction.getTarget() != null ? ruleAction.getTarget().getStringValue() : null;
				final String actionEntity = ruleAction.getActionEntity() != null ? ruleAction.getActionEntity().toString() : null;
				//				final String result = ruleAction.getResult() != null ? ruleAction.getResult().getStringValue() : null;
				final String name = ruleAction.getName() != null ? ruleAction.getName().getStringValue() : null;
				//final String source = ruleAction.getSource();

				if (ruleAction.getSource() != null && !placeHolders.containsKey(PLACEHOLDER_SOURCE)) {
					placeHolders.put(PLACEHOLDER_SOURCE, ruleAction.getSource());
				}

				if (ruleAction.getPackage() != null && !placeHolders.containsKey(PLACEHOLDER_PACKAGE)) {
					placeHolders.put(PLACEHOLDER_PACKAGE, ruleAction.getPackage().getStringValue());
				}

				if (ruleAction.getProject() != null && !placeHolders.containsKey(PLACEHOLDER_PROJECT)) {
					placeHolders.put(PLACEHOLDER_PROJECT, ruleAction.getProject().getStringValue());
				}

				if (ruleAction.getSrcPath() != null && !placeHolders.containsKey(PLACEHOLDER_SRC_PATH)) {
					placeHolders.put(PLACEHOLDER_SRC_PATH, ruleAction.getSrcPath().getStringValue());
				}

				if (ruleAction.getTarget() != null && !placeHolders.containsKey(PLACEHOLDER_TARGET)) {
					placeHolders.put(PLACEHOLDER_TARGET, ruleAction.getTarget().getStringValue());
				}

				if (ruleAction.getInput() != null && !placeHolders.containsKey(PLACEHOLDER_INPUT)) {
					placeHolders.put(PLACEHOLDER_INPUT, ruleAction.getInput().getStringValue());
				}

				if (ruleAction.getResult() != null && !placeHolders.containsKey(PLACEHOLDER_RESULT)) {
					placeHolders.put(PLACEHOLDER_RESULT, ruleAction.getResult().getStringValue());
				}

				/*if (ruleAction.getName() != null && !placeHolders.containsKey("name")) {
					placeHolders.put("name", ruleAction.getName().getStringValue());
				}*/

				replaceReferenceWithValue(placeHolders);

				final Properties properties = ruleAction.getProperties();
				String propertyName = null;
				String propertyValue = null;

				if (properties != null) {
					final Property[] propertyArr = properties.getPropertyArray();

					for (final Property property : propertyArr) {
						propertyName = property.getName() != null ? property.getName().getStringValue() : null;
						propertyValue = property.getValue() != null ? property.getValue().getStringValue() : null;
						if (propertyName != null && !placeHolders.containsKey(propertyName)) {
							//workingJavaProject = getJavaProject(propertyValue);
							placeHolders.put(propertyName, propertyValue); //getJavaProject(propertyValue));
						}

						/*if (propertyName != null && propertyName.equalsIgnoreCase("project")) {
							if(!placeHolders.containsKey("project")) {
								//workingJavaProject = getJavaProject(propertyValue);
								placeHolders.put("project", propertyValue); //getJavaProject(propertyValue));
							} else {
								workingJavaProject = getJavaProject((String) placeHolders.get("project"));
								placeHolders.put("project", workingJavaProject);
							}
						} else if (propertyName != null && propertyName.equalsIgnoreCase("package")) {
							//packageName = propertyValue;
							if(!placeHolders.containsKey("package")) {
								placeHolders.put("package", propertyValue);
							}
						} else if (propertyName != null && propertyName.equalsIgnoreCase("static")) {
							//doStatic = propertyValue.equals("true");
							if(!placeHolders.containsKey("static")) {
								placeHolders.put("static", propertyValue); //.equals("true"));
							} else {
								doStatic = placeHolders.get("static").equals("true");
								placeHolders.put("static", doStatic);
							}
						} else if (propertyName != null && propertyName.equalsIgnoreCase("abstract")) {
							//createAbstract = propertyValue.equals("true");
							if(!placeHolders.containsKey("abstract")) {
								placeHolders.put("abstract", propertyValue); //.equals("true"));
							} else {
								createAbstract = placeHolders.get("abstract").equals("true");
								placeHolders.put("abstract", createAbstract);
							}
						} else if (propertyName != null && propertyName.equalsIgnoreCase("extends")) {

							if(!placeHolders.containsKey("extends")) {
								parentFCType = new FastCodeType(propertyValue);
								placeHolders.put("parentClass", parentFCType.getName());
								//createChildClass = true;
								placeHolders.put("createChildClass", true);
								placeHolders.put("extends", parentFCType);
							} else {
								parentFCType = new FastCodeType((String) placeHolders.get("extends"));
								placeHolders.put("parentClass", parentFCType.getName());
								//createChildClass = true;
								placeHolders.put("createChildClass", true);
								placeHolders.put("extends", parentFCType);
							}
						} else if (propertyName != null && propertyName.equalsIgnoreCase("path")) {
							//path = propertyValue;
							if(!placeHolders.containsKey("path")) {
								placeHolders.put("path", propertyValue);
							}
						} else if (propertyName != null && propertyName.equalsIgnoreCase("classPackage")) {
							//classPackageName = propertyValue;
							if(!placeHolders.containsKey("classPackage")) {
								placeHolders.put("classPackage", propertyValue);
							}
						}*/
						/*final Options options = property.getOptions();
						final Optional[] optionArr = (Optional[]) options.getOptionArray();*/

					}
				}

				if (actionType != null) {
					if (actionType.equals(ACTION_TYPE.Prompt.getValue())) {
						getNameFromUser(placeHolders); //ruleAction, result,

					} else if (actionType.equals(ACTION_TYPE.Create.getValue())) {
						if (actionEntity != null && actionEntity.equalsIgnoreCase(ACTION_ENTITY.Field.getValue())) {
							final CreateFieldAction createFieldAction = new CreateFieldAction();
							createFieldAction.createField(placeHolders, contextMap); //target, result,

						} else if (actionEntity != null && actionEntity.equalsIgnoreCase(ACTION_ENTITY.Snippet.getValue())) {
							//createSnippet( placeHolders);
							final DefaultSnippetCreator snippetCreator = new DefaultSnippetCreator();
							final Object snippet = snippetCreator.createSnippet(this.editorPart,
									(String) placeHolders.get(PLACEHOLDER_SOURCE), placeHolders, new HashMap(), EMPTY_STR);
							placeHolders.remove(PLACEHOLDER_SOURCE);
							placeHolders.remove(PLACEHOLDER_INPUT);

						} else if (actionEntity != null && actionEntity.equalsIgnoreCase(ACTION_ENTITY.Class.getValue())) {
							final CreateClassAction createClassAction = new CreateClassAction();
							createClassAction.createClass(name, placeHolders, contextMap); //name, input, result,

						} else if (actionEntity != null && actionEntity.equalsIgnoreCase(ACTION_ENTITY.Method.getValue())) {
							/*if(!isEmpty(target) && workingJavaProject == null ) { //if (propertyName != null && propertyName.equalsIgnoreCase("project") && isEmpty(propertyValue)) {
								workingJavaProject = getWorkingJavaProject();
							}

							type = getTargetIType(workingJavaProject, target);*/
							//type.createMethod(arg0, arg1, arg2, arg3);
						} else if (actionEntity != null && actionEntity.equalsIgnoreCase(ACTION_ENTITY.Interface.getValue())) {
							final CreateInterfaceAction createInterfaceAction = new CreateInterfaceAction();
							createInterfaceAction.createInterface(name, placeHolders, contextMap); //name, result,

						} else if (actionEntity != null && actionEntity.equalsIgnoreCase(ACTION_ENTITY.File.getValue())) {
							createFileAction(placeHolders);
						} else if (actionEntity != null && actionEntity.equalsIgnoreCase(ACTION_ENTITY.Folder.getValue())) {
							createFolderAction(name, placeHolders);

						} else if (actionEntity != null && actionEntity.equalsIgnoreCase(ACTION_ENTITY.Package.getValue())) {
							createPackageAction(placeHolders);
						}

					} else if (actionType.equals(ACTION_TYPE.Import.getValue())) {
						doImport(placeHolders);

					} else if (actionType.equals(ACTION_TYPE.Select.getValue())) {
						if (actionEntity != null && actionEntity.equalsIgnoreCase(ACTION_ENTITY.Class.getValue())) {
							//kk;
							final Select[] selectArr = ruleAction.getSelectArray();
							if (selectArr != null && selectArr.length > 0) {
								for (final Select select : selectArr) {
									placeHolders.put("selectName", select.getName().getStringValue());
									placeHolders.put("selection-message", select.getSelectionMessage().getStringValue());
									placeHolders.put("selection-count", select.getSelectionCount() != null ? select.getSelectionCount()
											.toString() : "");
									placeHolders.put("selection-type", select.getType() != null ? select.getType().toString() : EMPTY_STR);
									if (select.getValue().getRestriction() != null) {
										placeHolders.put("restriction", select.getValue().getRestriction().toString());
										placeHolders.put(select.getValue().getRestriction().toString(), select.getValue().getStringValue());
									}

									/*final String valueArr = select.getValue2().getStringValue();
									restriction = select.getValue().getRestriction().toString();
									restrictTo = select.getValue().getStringValue();
									restrictToIType = getTypeFromWorkspace(restrictTo);
									//restrictToIType.ne
									for (final Value value : valueArr){
										restriction = value.getRestriction().toString();
										name = value.toString();//"org.eclipse.jface.preference.FieldEditor"; //value.getName();
										type = getTypeFromWorkspace(name);
									}*/

									selectClass(ruleAction, actionType, placeHolders);//result,
								}

							} else {
								selectClass(ruleAction, actionType, placeHolders);
							}

						}

					} else if (actionType.equals(ACTION_TYPE.Update.getValue())) {

					}
				}

			}
		}
	}

	/**
	 * @param placeHolders
	 * @throws Exception
	 */
	public void createFileAction(final Map<String, Object> placeHolders) throws Exception {
		String path = (String) placeHolders.get(PLACEHOLDER_SRC_PATH); //this shud include project and file name ex: TestFc/src/jsp/Myfile.jsp
		String source = (String) placeHolders.get(PLACEHOLDER_SOURCE);
		//final IPackageFragmentRoot packageFragmentRoot = null;
		String project = (String) placeHolders.get(PLACEHOLDER_PROJECT);

		if (path == null) {
			final GlobalSettings globalSettings = getInstance();
			path = globalSettings.isUseDefaultForPath() ? getPathFromGlobalSettings(project) : getPathFromUser();

			if (path == null) {
				MessageDialog.openError(new Shell(), "Error", "Cannot proceed without path...exiting..");
				return;
			}
		}

		if (source == null) {
			source = "";
		}

		IJavaProject javaProject;
		if (project == null) {
			javaProject = getWorkingJavaProject();
			project = javaProject.getElementName();
		}
		final InputStream content = new ByteArrayInputStream(source.getBytes());

		//packageFragmentRoot = getPackageRootFromProject(javaProject , path);
		//return packageFragmentRoot.createPackageFragment((String)placeHolders.get(PLACEHOLDER_NAME), true, new NullProgressMonitor());

		final IFile file = ResourcesPlugin.getWorkspace().getRoot()
				.getFile(new Path(project + path + FORWARD_SLASH + (String) placeHolders.get(PLACEHOLDER_NAME)));

		if (file.exists()) {
			final boolean overWrite = MessageDialog.openQuestion(new Shell(), "Overwrite File", file.getName()
					+ " is already present ,Would you like to overwrite?");
			if (overWrite) {
				file.setContents(content, false, true, new NullProgressMonitor());
			} else {
				System.out.println(file.getFullPath().toString().substring(0, file.getFullPath().toString().lastIndexOf(FORWARD_SLASH)));
				backUpExistingExportFile(file, file.getName(),
						file.getFullPath().toString().substring(0, file.getFullPath().toString().lastIndexOf(FORWARD_SLASH)));
				//createFileAction(placeHolders);
				file.setContents(content, false, true, new NullProgressMonitor());
			}
		} else {
			file.create(content, true, null);
		}

		//final IEditorPart editorPart = getEditorPartFromFile(file);
		//revealInEditor(editorPart, (IJavaElement) file);
		//openInEditor(file.getr);
		try {
			content.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param name
	 * @param placeHolders
	 * @throws Exception
	 */
	private void createFolderAction(final String name, final Map<String, Object> placeHolders) throws Exception {
		String path = (String) placeHolders.get(PLACEHOLDER_SRC_PATH);
		String project = (String) placeHolders.get(PLACEHOLDER_PROJECT);

		IJavaProject javaProject;
		if (project == null) {
			javaProject = getWorkingJavaProject();
			project = javaProject.getElementName();
		}

		if (project == null) {
			MessageDialog.openError(new Shell(), "Error", "Cannot proceed without project...exiting..");
			return;
		}

		if (path == null) {
			final GlobalSettings globalSettings = getInstance();
			path = globalSettings.isUseDefaultForPath() ? getPathFromGlobalSettings(project) : getPathFromUser();

			if (path == null) {
				MessageDialog.openError(new Shell(), "Error", "Cannot proceed without path...exiting..");
				return;
			}
		}

		/*IJavaProject javaProject;
		if (project == null) {
			javaProject = getWorkingJavaProject();
			project = javaProject.getElementName();
		}

		if (project == null) {
			MessageDialog.openError(new Shell(), "Error", "Cannot proceed without project...exiting..");
			return;
		}*/
		final IFolder newFolder = createFolder(new Path(project + path + "/" + (String) placeHolders.get(PLACEHOLDER_NAME)));
		MessageDialog.openInformation(new Shell(), "Success", "Folder created successfully.");

	}

	/**
	 * @param placeHolders
	 * @return
	 * @throws Exception
	 */
	private void createPackageAction(final Map<String, Object> placeHolders) throws Exception {
		IJavaProject workingJavaProject = null;
		IPackageFragmentRoot packageFragmentRoot = null;
		String path = (String) placeHolders.get(PLACEHOLDER_SRC_PATH);
		if (placeHolders.get(PLACEHOLDER_PROJECT) != null) {
			workingJavaProject = getJavaProject((String) placeHolders.get(PLACEHOLDER_PROJECT));
		} else {
			workingJavaProject = getWorkingJavaProject();
		}

		if (placeHolders.get(PLACEHOLDER_SRC_PATH) == null) {
			final String[][] sourcePaths = getAllSourcePathsInWorkspace();
			final List<String> sourcePathList = new ArrayList<String>();

			for (int i = 0; i < sourcePaths.length; i++) {
				sourcePathList.add(sourcePaths[i][0]);
			}

			sourcePathList.remove(0);

			final StringSelectionDialog selectionDialog = new StringSelectionDialog(new Shell(), "Source Path",
					"Choose Source Path to create package- " + (String) placeHolders.get(PLACEHOLDER_NAME),
					sourcePathList.toArray(new String[0]), false);
			if (selectionDialog.open() == CANCEL) {
				return;
			}

			path = (String) selectionDialog.getFirstResult();
		}

		packageFragmentRoot = getPackageRootFromProject(workingJavaProject, path);
		final IPackageFragment packageFragment = packageFragmentRoot.createPackageFragment((String) placeHolders.get(PLACEHOLDER_NAME),
				true, new NullProgressMonitor());
		MessageDialog.openInformation(new Shell(), "Success", "Package created successfully.");

	}

	/**
	 * @param actionRef
	 * @param actionNameActionMap
	 * @param placeHolders
	 */
	private void processRules(final ActionRef actionRef, final Map<String, Action> actionNameActionMap,
			final Map<String, Object> placeHolders) {

	}

	/**
	 * @param ruleAction
	 * @param actionType
	 * @param placeHolders
	 * @throws Exception
	 */
	private void selectClass(final RuleAction ruleAction, final String actionType, final Map<String, Object> placeHolders) throws Exception {// final String result,
		final Select[] selectArr = ruleAction.getSelectArray();

		final boolean multipleSelection = false;
		final String selectName = null;
		final String selectMessage = null;
		final String restriction = (String) placeHolders.get("restriction");
		final String restrictTo;
		final IType restrictToIType = restriction == null ? null : getTypeFromWorkspace(evaluateByVelocity(
				(String) placeHolders.get(restriction), placeHolders));
		/*for (final Select select : selectArr){
			selectName = select.getName().getStringValue();
			selectMessage = select.getSelectionMessage().getStringValue();
			multipleSelection = select.getSelectionCount().toString().equalsIgnoreCase("multiple");
			final String valueArr = select.getValue2().getStringValue();
			restriction = select.getValue().getRestriction().toString();
			restrictTo = select.getValue().getStringValue();
			restrictToIType = getTypeFromWorkspace(restrictTo);
			//restrictToIType.ne
			for (final Value value : valueArr){
				restriction = value.getRestriction().toString();
				name = value.toString();//"org.eclipse.jface.preference.FieldEditor"; //value.getName();
				type = getTypeFromWorkspace(name);
			}
		}*/
		SelectionDialog selectionDialog = null;

		if (restriction != null && restrictToIType == null) {
			openWarning(new Shell(), "Warning", "Restrict To Type not found.");
		}
		if (restriction != null && restrictToIType != null
				&& (restriction.equalsIgnoreCase("subclassOf") || restriction.equalsIgnoreCase("implementationOf"))) {
			selectionDialog = JavaUI.createTypeDialog(new Shell(), null, SearchEngine.createHierarchyScope(restrictToIType),
					IJavaElementSearchConstants.CONSIDER_ALL_TYPES, multipleSelection, EMPTY_STR);
		} else if (restriction != null && restriction.equalsIgnoreCase("containedIn")) {

		} else {
			selectionDialog = JavaUI.createTypeDialog(new Shell(), null, SearchEngine.createWorkspaceScope(),
					IJavaElementSearchConstants.CONSIDER_ALL_TYPES, multipleSelection, EMPTY_STR);
		}
		selectionDialog.setTitle(actionType);
		selectionDialog.setMessage((String) placeHolders.get("selection-message"));

		if (selectionDialog.open() == CANCEL) {
			return;
		}

		final IType classSelected = (IType) selectionDialog.getResult()[0];

		final FastCodeType fastCodeType = new FastCodeType(classSelected.getFullyQualifiedName());
		//placeHolders.put(selectName, fastCodeType);
		placeHolders.put((String) placeHolders.get(PLACEHOLDER_RESULT), fastCodeType);
		//placeHolders.put("selectedClass", ofClass);
		//final String source = ruleAction.getSource();
		//placeHolders.put("classSelected", ofClass);
		placeHolders.remove(PLACEHOLDER_RESULT);
		placeHolders.remove(restriction);
		placeHolders.remove("restriction");
	}

	/**
	 * @param placeHolders
	 * @throws Exception
	 */
	private void doImport(final Map<String, Object> placeHolders) throws Exception { //final String target, final String input,
		final String targetStr;
		final FastCodeType targetFCT;
		IType type = null;

		final String inputStr;
		final FastCodeType inputFCT;
		IType inputType = null;

		//final FastCodeType target = (FastCodeType) placeHolders.get(PLACEHOLDER_TARGET);
		//final FastCodeType input = (FastCodeType) placeHolders.get(PLACEHOLDER_INPUT);
		IJavaProject workingJavaProject = getJavaProject((String) placeHolders.get(PLACEHOLDER_PROJECT));
		if (placeHolders.get(PLACEHOLDER_TARGET) != null && workingJavaProject == null) { //if (propertyName != null && propertyName.equalsIgnoreCase("project") && isEmpty(propertyValue)) {
			workingJavaProject = getWorkingJavaProject();
		}
		if (placeHolders.get(PLACEHOLDER_TARGET) instanceof String) {
			targetStr = (String) placeHolders.get(PLACEHOLDER_TARGET);
			if (targetStr.contains(HASH)) {
				final String targetClassStr = targetStr.substring(0, targetStr.lastIndexOf(HASH_CHAR));
				type = workingJavaProject.findType(targetClassStr);
			} else {
				type = workingJavaProject.findType(targetStr);
			}
		} else if (placeHolders.get(PLACEHOLDER_TARGET) instanceof FastCodeType) {
			targetFCT = (FastCodeType) placeHolders.get(PLACEHOLDER_TARGET);
			type = workingJavaProject.findType(targetFCT.getFullyQualifiedName());
		}

		if (type == null) {
			type = getTypeFromUser("Target class specified in the XML is not found.Please choose another class.", "Select Class",
					"Select the Target class");
			if (type == null) {
				//show message??
				clearPlaceHolderMap(placeHolders);
				return;
			}
			placeHolders.put(PLACEHOLDER_TARGET, type.getFullyQualifiedName());
			placeHolders.put("targetClass", type.getFullyQualifiedName());
		}

		if (placeHolders.get(PLACEHOLDER_INPUT) instanceof String) {
			inputStr = (String) placeHolders.get(PLACEHOLDER_INPUT);
			if (inputStr.contains(COMMA)) {
				final String[] typesToImport = inputStr.split(COMMA);
				for (final String imprtType : typesToImport) {
					inputType = workingJavaProject.findType(imprtType);
					addImport(type.getCompilationUnit(), inputType);
				}
			} else {
				inputType = workingJavaProject.findType(inputStr);
				addImport(type.getCompilationUnit(), inputType);
			}

		} else if (placeHolders.get(PLACEHOLDER_INPUT) instanceof FastCodeType) {
			inputFCT = (FastCodeType) placeHolders.get(PLACEHOLDER_INPUT);
			inputType = workingJavaProject.findType(inputFCT.getFullyQualifiedName());
			addImport(type.getCompilationUnit(), inputType);
		}

		//final IType type = getTargetIType(workingJavaProject, target.getFullyQualifiedName()); //((FastCodeType) placeHolders.get(target))

		if (TRUE_STR.equals(placeHolders.get(PLACEHOLDER_STATIC_IMPORT))) {
			type.getCompilationUnit().createImport(
					inputType.getFullyQualifiedName() + DOT + ((FastCodeField) placeHolders.get(PLACEHOLDER_FIELD)).getName(), null,
					AccStatic, null);
		} /*else {
			addImport(type.getCompilationUnit(), inputType); //getTypeFromProject(workingJavaProject, input.getFullyQualifiedName())); //((FastCodeType)placeHolders.get(input))
			}*/

		clearPlaceHolderMap(placeHolders);
		placeHolders.remove(PLACEHOLDER_SUPER_INTERFACE);
	}

	/*	private void createInterface(final String name, final String result, final Map<String, Object> placeHolders) throws Exception {
			IPackageFragment	interfacePackage = null;

			final String interfaceSnippet = evaluateByVelocity((String) placeHolders.get("source"), placeHolders);
			if (isEmpty(interfaceSnippet)){
				throw new Exception("Blank snippet, source may be invalid.");
			}

			final String interfaceSrc = replaceSpecialChars(interfaceSnippet);

			if(target != null){
				classPackage = getPackageFragmentFromWorkspace(target);
			} else
			if (placeHolders.get("package") != null){
				interfacePackage = getPackageFragmentFromWorkspace((String) placeHolders.get("package"));
			} else if (placeHolders.get("project") != null) {
				interfacePackage = getPackageFromUser(getJavaProject((String) placeHolders.get("project")));
			} else {
				interfacePackage = getPackageFromUser(getWorkingJavaProject());
			}

			final String packageDeclaration = "package " + interfacePackage.getElementName() + SEMICOLON + NEWLINE + NEWLINE;
			final String interfaceName = evaluateByVelocity(name, placeHolders);

			final ICompilationUnit newInterface = interfacePackage.createCompilationUnit(interfaceName + DOT + JAVA_EXTENSION, packageDeclaration + interfaceSrc, false, null);
			System.out.println(newInterface.getElementName());
			System.out.println(newInterface.findPrimaryType().getElementName());
			if (result != null) {
				placeHolders.put(result, new FastCodeType(newInterface.findPrimaryType().getFullyQualifiedName()));
			}
			placeHolders.remove("source");
		}*/

	/*private void createClass(final String name, final String input, final String result, final Map<String, Object> placeHolders) throws Exception {
		ICompilationUnit newClass = null;
		IPackageFragment	classPackage = null;
		IType parentIType = null;
		FastCodeType parentFCType = null;

		String path = (String) placeHolders.get("path");

		if(placeHolders.get("baseClass") != null) {
			parentFCType =  new FastCodeType((String) placeHolders.get("baseClass"));
			placeHolders.put("baseClass", parentFCType);
		}

		final String classSnippet = evaluateByVelocity((String) placeHolders.get("source"), placeHolders);
		if (isEmpty(classSnippet)){
			throw new Exception("Blank snippet, source may be invalid.");
		}
		final String classSrc = replaceSpecialChars(classSnippet);

		if(target != null){
			classPackage = getPackageFragmentFromWorkspace(target);
		} else
		IJavaProject workingJavaProject = null;
		if (placeHolders.get("package") != null){
			if(placeHolders.containsKey("implSubPckage")){
				final String implSubPckage = evaluateByVelocity((String) placeHolders.get("implSubPckage"), placeHolders);
				classPackage = getPackageFragmentFromWorkspace(implSubPckage);
			} else {
				classPackage = getPackageFragmentFromWorkspace((String) placeHolders.get("package"));
			}
			workingJavaProject = getJavaProject((String) placeHolders.get("project"));
		}  else if (placeHolders.get("project") != null) {
			workingJavaProject = getJavaProject((String) placeHolders.get("project"));
			classPackage = getPackageFromUser(workingJavaProject);
		} else {
			classPackage = getPackageFromUser(getWorkingJavaProject());
		}

		//System.out.println(classPackage.getElementName());


		if (classPackage == null) {
			IPackageFragmentRoot packageFragmentRoot = null;
			//System.out.println(workingJavaProject.getPath());
			if (path == null) {
				final String[][] sourcePaths = getAllSourcePathsInWorkspace();
				final List<String> sourcePathList = new ArrayList<String>();

				for (int i=0; i<sourcePaths.length; i++) {
					sourcePathList.add(sourcePaths[i][0]);
				}

				sourcePathList.remove(0);


				final StringSelectionDialog selectionDialog = new StringSelectionDialog(new Shell(), "Source Path", "Choose Source Path", sourcePathList.toArray(new String[0]) , false);
				if (selectionDialog.open() == CANCEL) {
					return;
				}

				path = (String) selectionDialog.getFirstResult();
			}
			packageFragmentRoot = getPackageRootFromProject(workingJavaProject , path);
			//packageFragmentRoot = getPackageRootFromProject(workingJavaProject, "/src"); //workingJavaProject.findPackageFragmentRoot(new Path(packageName));
			classPackage = packageFragmentRoot.createPackageFragment((String) placeHolders.get("package"), false, null);
		}

		final String packageDeclaration = "package " + classPackage.getElementName() + SEMICOLON + NEWLINE + NEWLINE;

		final String className = evaluateByVelocity(name, placeHolders);
		if(placeHolders.get("abstract") != null && TRUE_STR.equals(placeHolders.get("abstract"))) {
			newClass = classPackage.createCompilationUnit("Abstract"+ className + DOT + JAVA_EXTENSION, packageDeclaration + classSrc, false, null);
		} else {

			newClass = classPackage.createCompilationUnit(className + DOT + JAVA_EXTENSION, packageDeclaration + classSrc, false, null);
		}

		if(placeHolders.get("baseClass") != null) {
			parentIType = getTargetIType(workingJavaProject, parentFCType.getFullyQualifiedName());
			for(final IMethod method : parentIType.getMethods()) {
				if (method.isConstructor()) {
					final FastCodeContext fastCodeContext = new FastCodeContext(parentIType);
					final MethodBuilder methodBuilder = new SimilarMethodBuilder(fastCodeContext);
					methodBuilder.buildMethod(method, newClass.findPrimaryType());
				}
			}
			//typeToImport = parentIType;
			//placeHolders.put(input, parentIType);
		}
		if (result != null) {
			placeHolders.put(result, new FastCodeType(newClass.findPrimaryType().getFullyQualifiedName()));
		}
		placeHolders.remove("source");
		placeHolders.remove("implSubPckage");
	}
	*/
	/*private void createSnippet(final Map<String, Object> placeHolders) throws Exception {
		final DefaultSnippetCreator snippetCreator = new DefaultSnippetCreator();
		final Object snippet = snippetCreator.createSnippet(this.editorPart, (String) placeHolders.get("source"), placeHolders, new HashMap(), "");
		placeHolders.remove("source");
	}*/

	/**
	 * @param target
	 * @param result
	 * @param placeHolders
	 * @throws Exception
	 */
	private void createField(final String target, final String result, final Map<String, Object> placeHolders) throws Exception {

	}

	/**
	 * @param placeHolders
	 */
	private void getNameFromUser(final Map<String, Object> placeHolders) { //, final String result
		String title = "Enter Name";
		if (!isEmpty((String) placeHolders.get(PLACEHOLDER_TITLE))) {
			title = (String) placeHolders.get(PLACEHOLDER_TITLE);
		}
		final InputDialog inputDialog = new InputDialog(new Shell(), title, title, EMPTY_STR, null);
		if (inputDialog.open() == Window.CANCEL) {
			return;
		}
		final String inputValue = inputDialog.getValue();
		placeHolders.put((String) placeHolders.get("result"), inputValue);
		placeHolders.put(INSTANCE_STR, inputValue.substring(0, 1).toLowerCase() + inputValue.substring(1));
		placeHolders.remove("result");
	}

	/**
	 * @return
	 * @throws Exception
	 */
	protected IJavaProject getWorkingJavaProject() throws Exception {
		IJavaProject workingJavaProject = null;
		final IJavaProject[] javaProject = getProjectsFromWorkspace();

		final ProjectSelectionDialog projectSelectionDialog = new ProjectSelectionDialog(new Shell(), "Projects in Workspace",
				"Select a project you want to work with", javaProject, IJavaElement.JAVA_PROJECT);

		if (projectSelectionDialog.open() != CANCEL) {
			workingJavaProject = (IJavaProject) projectSelectionDialog.getFirstResult();
		}
		return workingJavaProject;
	}

	/**
	 * @param workingJavaProject
	 * @param target
	 * @return
	 * @throws Exception
	 */
	private IType getTargetIType(final IJavaProject workingJavaProject, final String target) throws Exception {
		IType type = null;
		if (workingJavaProject != null && target != null) {
			type = workingJavaProject.findType(target);
			//placeHolders.put("targetIType", type);
		}
		if (type == null) {
			final ICompilationUnit compilationUnit = getCompilationUnitFromEditor();
			type = compilationUnit.findPrimaryType();
		}
		return type;
	}

	@Override
	public void selectionChanged(final IAction arg0, final ISelection arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setActiveEditor(final IAction arg0, final IEditorPart arg1) {
		// TODO Auto-generated method stub

	}

	/**
	 * @return
	 */
	/**
	 * @return
	 */
	protected ICompilationUnit getCompilationUnitFromEditor() {
		final IWorkingCopyManager manager = JavaUI.getWorkingCopyManager();
		final ICompilationUnit compilationUnit = manager.getWorkingCopy(this.editorPart.getEditorInput());
		return compilationUnit != null && compilationUnit.exists() ? compilationUnit : null;
	}

	/**
	 *
	 * @param compUnit
	 * @param type
	 * @throws JavaModelException
	 */
	protected void addImport(final ICompilationUnit compUnit, final IType type) throws JavaModelException {

		final String pkg = type.getPackageFragment().getElementName();
		if (pkg.equals("java.lang")) {
			return;
		}

		IImportDeclaration imp = compUnit.getImport(pkg + ".*");

		if (imp == null || !imp.exists()) {
			imp = compUnit.getImport(type.getFullyQualifiedName());
			if (imp == null || !imp.exists()) {
				compUnit.createImport(type.getFullyQualifiedName(), null, null);
			}
		}
	}

	/**
	 * @param workingJavaProject
	 * @return
	 * @throws JavaModelException
	 */
	protected IPackageFragment getPackageFromUser(final IJavaProject workingJavaProject) throws JavaModelException {
		IPackageFragment packageFragment = null;

		// if not invoked from java class...list all prj in wrk space...then
		// list pkg in that prj...save the selection in pref..

		final IPackageFragmentRoot[] packageFragmentRootArray = workingJavaProject.getJavaProject().getAllPackageFragmentRoots();

		final List<IPackageFragment> allPackages = new ArrayList<IPackageFragment>();

		for (final IPackageFragmentRoot packageFragmentRoot : packageFragmentRootArray) {
			// System.out.println("Ele name: " + ipfr.getElementName() +
			// " is archive: " + ipfr.isArchive());
			final IJavaElement[] jea = null;
			if (!packageFragmentRoot.isArchive()) {
				for (final IJavaElement pkg : packageFragmentRoot.getChildren()) {

					if (pkg instanceof IPackageFragment) {
						allPackages.add((IPackageFragment) pkg);
					}
				}
			}

		}
		// System.out.println(this.getCompilationUnitFromEditor().);
		final PackageSelectionDialog selectionDialog = new PackageSelectionDialog(new Shell(), "Pojo Class Package",
				"Choose the package from below", allPackages.toArray());
		if (selectionDialog.open() != CANCEL) {
			packageFragment = (IPackageFragment) selectionDialog.getFirstResult();
		}

		return packageFragment;

	}

	/**
	 * @param placeHolders
	 * @throws Exception
	 */
	private void replaceReferenceWithValue(final Map<String, Object> placeHolders) throws Exception {

		/*for each key1 value1 pair,
		1.) check if the value1 has any place holder
		2.) if yes, get the array of all the place holders
			if the place holder has a . then parse the 1st part of the string, that will be the key, get the object with that key,
			if it is FCType then do the get of 2nd part of the strng....ex: ${interface.name}.Impl --- interface is a key in placeHolders map with FCType as value
		3.) find the refered value of each place holder from the map and replace in the value1
		4.) put back key1 with value1
		*/

		for (final Entry<String, Object> entry : placeHolders.entrySet()) {
			Object newValue = null;
			if (entry.getKey() != null
					&& (entry.getKey().equals(PLACEHOLDER_TARGET) || entry.getKey().equals(PLACEHOLDER_INPUT) || entry.getKey().equals(
							"superInterface"))) {
				Object value = entry.getValue();
				if (value instanceof String) {
					if (containsAnyPlaceHolder((String) value)) {
						final String[] referedKeys = getAllPlcHldrNmes((String) value);
						for (final String referedKey : referedKeys) {
							if (referedKey != null) {
								if (referedKey.contains(DOT)) { //debug this if....not checked
									//newValue  = evaluateByVelocity("${" + referedKey + "}", placeHolders);
									//final String newValue1  = evaluateByVelocity("${" + referedKey + "}", placeHolders);
									final Object expr1 = Ognl.parseExpression(referedKey);
									final OgnlContext ctx1 = new OgnlContext();
									final Object value1 = Ognl.getValue(expr1, ctx1, placeHolders);
									if (value1 instanceof FastCodeType) {
										newValue = replacePlaceHolder((String) value, referedKey,
												((FastCodeType) value1).getFullyQualifiedName());
									}
								} else {
									final Object referedValue = placeHolders.get(referedKey);
									if (referedValue instanceof String) {
										newValue = replacePlaceHolder((String) value, referedKey, (String) referedValue);
									} else {
										newValue = referedValue;
									}
								}
								placeHolders.put(entry.getKey(), newValue);
								value = newValue;
							}
						}
					}
				}
			}
			/*if (entry.getKey().equals(PLACEHOLDER_TARGET) || entry.getKey().equals(PLACEHOLDER_INPUT) || entry.getKey().equals("superInterface")) {
				final String value = (String) entry.getValue();


				if (value != null && value.contains("$")) {
					final String referedKey = value.substring(value.indexOf("{") + 1,value.indexOf("}"));
					final Object referedValue = placeHolders.get(referedKey);
					if (referedValue instanceof String  && referedKey.equals("name")) { //or is referedKey != class or interface or something else
						final String valueNew = value.replace("${" + referedKey + "}", (String) referedValue);
						placeHolders.put(entry.getKey(), valueNew);
					} else {
						placeHolders.put(entry.getKey(), referedValue);
					}
				}
			}*/
		}
	}

	/**
	 * @param value
	 * @return
	 */
	private String[] getAllPlcHldrNmes(final String value) {
		StringBuilder parsedValues = new StringBuilder();
		final String[] valueArr = value.split("\\$\\{");
		for (final String val : valueArr) {
			if (val.contains(RIGHT_CURL)) {
				parsedValues = EMPTY_STR.equals(parsedValues.toString()) ? parsedValues.append(val.substring(0, val.indexOf(RIGHT_CURL)))
						: parsedValues.append(COMMA + val.substring(0, val.indexOf(RIGHT_CURL)));
			}
		}
		return parsedValues.toString().split(COMMA);
	}

	/**
	 * @param key
	 * @param placeHolders
	 * @return
	 * @throws Exception
	 */
	protected String getValueForPlaceHolder(String key, final Map<String, Object> placeHolders) throws Exception {
		String newValueForKey = null;

		if (containsAnyPlaceHolder(key)) {
			final String[] referedKeys = getAllPlcHldrNmes(key);
			for (final String referedKey : referedKeys) {
				if (referedKey != null) {
					if (referedKey.contains(DOT)) {
						final String newValue = evaluateByVelocity("$" + LEFT_CURL + referedKey + RIGHT_CURL, placeHolders);
						newValueForKey = replacePlaceHolder(key, referedKey, newValue);
					} else {
						final Object referedValue = placeHolders.get(referedKey);
						newValueForKey = replacePlaceHolder(key, referedKey, (String) referedValue);

					}
					key = newValueForKey;
				}
			}
			return newValueForKey;
		}
		return key;
	}

	/**
	 * @param placeHolders
	 * @throws Exception
	 */
	private void createDefaultMethods(final Map<String, Object> placeHolders) throws Exception {
		if (!MessageDialog.openConfirm(new Shell(), EMPTY_STR, "Do you want to create default methods?")) {
			return;
		}

		StringBuilder inBuiltMethod = new StringBuilder();
		for (final String method : inbuiltMethods) {
			inBuiltMethod = EMPTY_STR.equals(inBuiltMethod.toString()) ? inBuiltMethod.append(getValueForPlaceHolder(method, placeHolders))
					: inBuiltMethod.append(COMMA + getValueForPlaceHolder(method, placeHolders));
		}

		final StringSelectionDialog selectionDialog = new StringSelectionDialog(new Shell(), "In build methods", "Choose methods ",
				inBuiltMethod.toString().split(COMMA), true);
		if (selectionDialog.open() == CANCEL) {
			return;
		}

		final Object[] methodsToCreate = selectionDialog.getResult();
		IType type = null;
		final OgnlContext ctx1 = new OgnlContext();
		IJavaProject workingJavaProject = getJavaProject((String) placeHolders.get(PLACEHOLDER_PROJECT));
		if (workingJavaProject == null || !workingJavaProject.exists()) { //if (propertyName != null && propertyName.equalsIgnoreCase("project") && isEmpty(propertyValue)) {
			workingJavaProject = getWorkingJavaProject();
		}
		for (final Object method : methodsToCreate) {
			//final IMethod[] existingMethods = type.getMethods();
			try {
				Object expr1 = Ognl.parseExpression("DAO.interface");
				Object value1 = Ognl.getValue(expr1, ctx1, placeHolders);
				type = workingJavaProject.findType(((FastCodeType) value1).getFullyQualifiedName());
				type.createMethod((String) method, null, false, null);
				expr1 = Ognl.parseExpression("Vo.class");
				value1 = Ognl.getValue(expr1, ctx1, placeHolders);
				addImport(type.getCompilationUnit(), workingJavaProject.findType(((FastCodeType) value1).getFullyQualifiedName()));
			} catch (final Exception ex) {
				MessageDialog.openError(new Shell(), "Error generating method", ex.getMessage() + "\nWill move on.");
				continue;
			}
		}
		final Object expr1 = Ognl.parseExpression("DAO.class");
		final Object value1 = Ognl.getValue(expr1, ctx1, placeHolders);
		implementMethods(type.getMethods(), workingJavaProject.findType(((FastCodeType) value1).getFullyQualifiedName()), type);

	}

	/**
	 * @param methods
	 * @param classType
	 * @param interfaceType
	 * @throws Exception
	 */
	public void implementMethods(final IMethod[] methods, final IType classType, final IType interfaceType) throws Exception {
		if (methods != null) {
			for (final IMethod method : methods) {
				final FastCodeContext fastCodeContext = new FastCodeContext(interfaceType);

				final CreateSimilarDescriptorClass createSimilarDescriptorClass = new CreateSimilarDescriptorClass.Builder().withClassType(
						CLASS_TYPE.CLASS).build();
				implementInterfaceMethods(interfaceType, fastCodeContext, classType, null, createSimilarDescriptorClass);

				final IType[] superInterfaceType = getSuperInterfacesType(interfaceType);
				if (superInterfaceType != null) {
					for (final IType type : superInterfaceType) {
						if (type == null || !type.exists()) {
							continue;
						}
						final FastCodeContext context = new FastCodeContext(type);
						implementInterfaceMethods(type, context, classType, null, createSimilarDescriptorClass);
					}
				}
				/*final MethodBuilder methodBuilder = new SimilarMethodBuilder(fastCodeContext);
				try {
					methodBuilder.buildMethod(method, classType);
				} catch (final Exception ex) {
					MessageDialog.openError(new Shell(),"Error generating method", ex.getMessage() + "\nWill move on.");
					continue; //ex.printStackTrace();
				}*/
			}
		}
	}

	/**
	 * @param errorMessage
	 * @param title
	 * @param dialogMessage
	 * @return
	 * @throws Exception
	 */
	protected IType getTypeFromUser(final String errorMessage, final String title, final String dialogMessage) throws Exception {
		MessageDialog.openError(new Shell(), "Error", errorMessage);
		final SelectionDialog selectionDialog = JavaUI.createTypeDialog(new Shell(), null, SearchEngine.createWorkspaceScope(),
				IJavaElementSearchConstants.CONSIDER_ALL_TYPES, false, EMPTY_STR);
		selectionDialog.setTitle(title);
		selectionDialog.setMessage(dialogMessage);

		if (selectionDialog.open() == CANCEL) {
			return null;
		}
		return (IType) selectionDialog.getResult()[0];
	}

	/**
	 * @param placeHolders
	 */
	private void clearPlaceHolderMap(final Map<String, Object> placeHolders) {
		placeHolders.remove(PLACEHOLDER_INPUT);
		placeHolders.remove(PLACEHOLDER_TARGET);
	}
}
