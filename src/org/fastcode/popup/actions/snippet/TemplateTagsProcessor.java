package org.fastcode.popup.actions.snippet;

import static org.eclipse.jdt.ui.JavaUI.openInEditor;
import static org.eclipse.jdt.ui.JavaUI.revealInEditor;
import static org.fastcode.common.FastCodeConstants.COLON;
import static org.fastcode.common.FastCodeConstants.COMMA;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FC_OBJ_CREATED;
import static org.fastcode.common.FastCodeConstants.FORWARD_SLASH;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.common.FastCodeConstants.OPTIONAL;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_CLASS;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_INTERFACE;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_MESSAGE;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_PROJECT;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_TITLE;
import static org.fastcode.common.FastCodeConstants.SPACE;
import static org.fastcode.common.FastCodeConstants.TEMPLATE_TAG_PREFIX;
import static org.fastcode.common.FastCodeConstants.UNDERSCORE;
import static org.fastcode.common.FastCodeConstants.XML_COMPLETE_END;
import static org.fastcode.common.FastCodeConstants.XML_END;
import static org.fastcode.common.FastCodeConstants.XML_START;
import static org.fastcode.setting.GlobalSettings.getInstance;
import static org.fastcode.util.SourceUtil.getDefaultPathFromProject;
import static org.fastcode.util.SourceUtil.getImagefromFCCacheMap;
import static org.fastcode.util.SourceUtil.getJavaProject;
import static org.fastcode.util.SourceUtil.getPackageRootFromProject;
import static org.fastcode.util.SourceUtil.getPackagesInProject;
import static org.fastcode.util.SourceUtil.getPathFromUser;
import static org.fastcode.util.SourceUtil.getRepositoryServiceClass;
import static org.fastcode.util.SourceUtil.getWorkingJavaProjectFromUser;
import static org.fastcode.util.SourceUtil.populateFCCacheEntityImageMap;
import static org.fastcode.util.StringUtil.getAttributes;
import static org.fastcode.util.StringUtil.getTemplateTagEnd;
import static org.fastcode.util.StringUtil.getTemplateTagStart;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.VersionControlUtil.addOrUpdateFileStatusInCache;
import static org.fastcode.util.VersionControlUtil.isPrjConfigured;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;
import org.fastcode.Activator;
import org.fastcode.common.Action;
import org.fastcode.common.Actions;
import org.fastcode.common.FastCodeConstants.ACTION_ENTITY;
import org.fastcode.common.FastCodeConstants.ACTION_TYPE;
import org.fastcode.common.FastCodeConstants.TemplateTag;
import org.fastcode.common.FastCodeEntityHolder;
import org.fastcode.common.FastCodeObject;
import org.fastcode.common.FastCodeProject;
import org.fastcode.common.FastCodeSelectionDialog;
import org.fastcode.common.FastCodeType;
import org.fastcode.common.StringSelectionDialog;
import org.fastcode.exception.FastCodeRepositoryException;
import org.fastcode.handler.FCClassTagHandler;
import org.fastcode.handler.FCFieldTagHandler;
import org.fastcode.handler.FCFileTagHandler;
import org.fastcode.handler.FCFolderTagHandler;
import org.fastcode.handler.FCImportTagHandler;
import org.fastcode.handler.FCMethodTagHandler;
import org.fastcode.handler.FCPackageTagHandler;
import org.fastcode.handler.FCProjectTagHandler;
import org.fastcode.handler.FCPropertyTagHandler;
import org.fastcode.handler.FCSnippetTagHandler;
import org.fastcode.handler.FCTagHandler;
import org.fastcode.handler.FCXmlTagHandler;
import org.fastcode.preferences.VersionControlPreferences;
import org.fastcode.setting.GlobalSettings;
import org.fastcode.util.RepositoryService;

public class TemplateTagsProcessor {
	final List<Action>			subActions					= new ArrayList<Action>();

	StringBuilder				existingMembersBuilder		= new StringBuilder();
	IJavaProject				javaProject					= null;
	VersionControlPreferences	versionControlPreferences	= VersionControlPreferences.getInstance();
	boolean						overWrite					= false;
	final static List<Action>	actionList					= new ArrayList<Action>();

	/**
	 * This method will parse the snippet and search for <fc:import>,<fc:method
	 * name="",target="">,<fc:field> ,<fc:file>,<fc:class>,<fc:message>,<fc:exit>,<fc:folder>,<fc:package>,<fc:project> tags and createImport,createMethod
	 * ,createField,createFile,createClass,craeteFolder,cretaePackage,createProject according to the tag.
	 *
	 * @param compUnit
	 * @param snippet
	 * @param editorPart
	 * @param hasSubAction
	 * @param placeHolders
	 * @param spacesBeforeCursor
	 * @return
	 * @throws Exception
	 */
	public String processTemplateTags(final ICompilationUnit compUnit, final String snippet, final IEditorPart editorPart,
			final boolean hasSubAction, final Map<String, Object> placeHolders, final String spacesBeforeCursor) throws Exception {

		final StringBuilder snippetBuilder = new StringBuilder();
		final Map<String, Object> contextMap = new HashMap<String, Object>();
		Map<Object, List<FastCodeEntityHolder>> fastCodeObjectsMap = new HashMap<Object, List<FastCodeEntityHolder>>();
		contextMap.put(FC_OBJ_CREATED, fastCodeObjectsMap);
		contextMap.put("changes_for_File", fastCodeObjectsMap);

		final List<Action> messsageActionList = new ArrayList<Action>();
		int start = 0;
		boolean simpleTagFound = false;
		while (true) {

			TemplateTag tagFound = null;
			String tagBody = null;

			int startTag = snippet.indexOf(XML_START + TEMPLATE_TAG_PREFIX + COLON, start);
			int endTag = 0;

			if (startTag == -1) {
				snippetBuilder.append(snippet.substring(start));
				break;
			}

			for (final TemplateTag templateTag : TemplateTag.values()) {

				if (startTag == snippet.indexOf(getTemplateTagStart(templateTag), startTag)) {
					snippetBuilder.append(snippet.substring(start, startTag));

					final int startTagEnd = snippet.indexOf(XML_END, startTag);
					final int startCompleteTagEnd = snippet.indexOf(XML_COMPLETE_END, startTag);

					simpleTagFound = startCompleteTagEnd == startTagEnd - 1;

					if (simpleTagFound) {
						endTag = startTagEnd + 1;
					} else {
						endTag = snippet.indexOf(getTemplateTagEnd(templateTag), startTag);
						if (endTag == -1) {
							throw new Exception("Unable to find end tag for " + getTemplateTagStart(templateTag));
						}
					}

					tagBody = simpleTagFound ? snippet.substring(startTag, startTagEnd + 1) : snippet.substring(startTag, endTag
							+ getTemplateTagEnd(templateTag).length());

					final String insideTagBody = startCompleteTagEnd == startTagEnd - 1 ? null : snippet.substring(startTagEnd + 1, endTag);
					tagFound = templateTag;
					final Action action = populateAction(tagFound, tagBody, insideTagBody, compUnit, hasSubAction, placeHolders,
							contextMap, spacesBeforeCursor);

					if (!hasSubAction && action != null) {
						final boolean found = action.getEntity() == ACTION_ENTITY.Import ? isDuplicateImportAction(actionList, action)
								: false;
						if (!found) {
							actionList.add(action);
						}
					}
					startTag = simpleTagFound ? endTag : endTag + getTemplateTagEnd(tagFound).length();
				}

			}
			if (start == startTag) {
				throw new Exception("Unable to find tag , Please provide correct fc tag in the xml and try again.");
			}
			start = startTag;//simpleTagFound ? endTag : endTag + getTemplateTagEnd(tagFound).length();

			if (start == snippet.length()) {
				break;
			}
			// Keep going Until you hit the end of line. This is to remove the
			// blank line problem.
			char ch = snippet.charAt(start);
			while (Character.isWhitespace(ch) && start < snippet.length()) {
				start++;
				ch = snippet.charAt(start);
				if (ch == NEWLINE.charAt(0)) {
					start++;
					break;
				}
			}

		}
		if (!hasSubAction && this.existingMembersBuilder.length() != 0) {
			MessageDialog.openWarning(new Shell(), "Warning", "Member(s):  " + this.existingMembersBuilder.toString() + "already exist");

		}

		//final IPreferenceStore preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		//this.autoCheckinEnabled = preferenceStore.getBoolean(P_ENABLE_AUTO_CHECKIN);

		if (!hasSubAction && !actionList.isEmpty()) {
			for (final Action action : actionList) {
				if (action.getEntity() == ACTION_ENTITY.Message) {
					createMessageFromTag(action.getEntityName(), action.getSource());
					messsageActionList.add(action);
				} else if (action.getEntity() == ACTION_ENTITY.Exit) {
					createMessageFromTag(action.getType().name(), action.getLabelMsg());
					messsageActionList.add(action);
					//return EMPTY_STR;
				} else if (action.getEntity() == ACTION_ENTITY.Info) {
					messsageActionList.add(action);
					MessageDialog.openInformation(new Shell(), "Information", action.getSource());
				}
			}
			if (!messsageActionList.isEmpty()) {
				for (final Action action : messsageActionList) {
					actionList.remove(action);
				}
			}
			if (!actionList.isEmpty()) {
				final Actions actions = new Actions.Builder().withActions(actionList.toArray(new Action[0])).build();
				// actions.setActions(actionList.toArray(new Action[0]));
				final CheckedTreeSelectionDialog checkedTreeSelectionDialog = new CheckedTreeSelectionDialog(new Shell(),
						new ActionLabelProvider(), new ActionContentProvider());
				checkedTreeSelectionDialog.setTitle("Action(s) Selection");
				checkedTreeSelectionDialog.setMessage("Select the action(s)");
				checkedTreeSelectionDialog.setInput(actions);
				checkedTreeSelectionDialog.setInitialElementSelections(filterActionList(actionList));//checkedTreeSelectionDialog.setInitialElementSelections(actionList);
				checkedTreeSelectionDialog.setExpandedElements(actionList.toArray(new Action[0]));
				checkedTreeSelectionDialog.setContainerMode(true);

				if (checkedTreeSelectionDialog.open() == Window.CANCEL) {
					actionList.clear();
					return EMPTY_STR;
				}
				final StringBuilder existWarningBuilder = new StringBuilder();

				if (checkedTreeSelectionDialog.getResult() != null) {
					for (final Object selection : checkedTreeSelectionDialog.getResult()) {
						final Action actionSelected = (Action) selection;
						if (actionSelected.isExist()) {
							if (actionSelected.getEntity() == ACTION_ENTITY.File || actionSelected.getEntity() == ACTION_ENTITY.Class) {
								existWarningBuilder.append(actionSelected.getEntity() + SPACE + actionSelected.getEntityName() + SPACE
										+ COMMA);
							}
						}

					}
					if (existWarningBuilder.length() != 0) {
						this.overWrite = MessageDialog.openQuestion(new Shell(), "Overwrite", existWarningBuilder.toString()
								+ "already exist , Would you like to overwrite?");
					}
				}

				/*checkedTreeSelectionDialog.getTreeViewer().addCheckStateListener(new ICheckStateListener() {

					public void checkStateChanged(CheckStateChangedEvent event) {
						if (event.getChecked()) {
							System.out.println(event.getElement());
						}
					}
				});*/
				if (checkedTreeSelectionDialog.getResult() != null) {
					for (final Object selection : checkedTreeSelectionDialog.getResult()) {
						final Action actionSelected = (Action) selection;
						if (actionSelected.getSubAction() != null) {
							for (final Action subAction : actionSelected.getSubAction()) {
								if (subAction.getEntity() == ACTION_ENTITY.Exit) {
									return EMPTY_STR;
								}
								createActionEntity(subAction, compUnit, editorPart, contextMap, placeHolders, spacesBeforeCursor);
							}
						}
						if (actionSelected.getEntity() == ACTION_ENTITY.Exit) {
							return EMPTY_STR;
						}
						createActionEntity(actionSelected, compUnit, editorPart, contextMap, placeHolders, spacesBeforeCursor);
					}
				}
			}
		}

		final List<String> classNameList = new ArrayList<String>();
		for (final Entry<String, Object> entry : contextMap.entrySet()) {
			if (entry.getValue() instanceof FastCodeObject) {
				final Object obj = ((FastCodeObject) entry.getValue()).getObject();
				if (obj instanceof ICompilationUnit) {
					classNameList.add(((ICompilationUnit) obj).getElementName());
				}
			}
		}
		if (classNameList.size() > 0) {
			String className = null;
			final FastCodeSelectionDialog classNameSelectionDialog = new StringSelectionDialog(new Shell(), "Select Class",
					"Select Class to jump to", classNameList.toArray(new String[0]), false);
			if (classNameSelectionDialog.open() != Window.CANCEL) {
				className = (String) classNameSelectionDialog.getResult()[0];
			}

			for (final Entry<String, Object> entry : contextMap.entrySet()) {
				if (entry.getValue() instanceof FastCodeObject) {
					final Object obj = ((FastCodeObject) entry.getValue()).getObject();
					if (obj instanceof ICompilationUnit) {
						if (!isEmpty(className) && className.equals(((ICompilationUnit) obj).getElementName())) {
							final IEditorPart javaEditor = openInEditor((ICompilationUnit) obj);
							revealInEditor(javaEditor, (IJavaElement) (ICompilationUnit) obj);
							break;
						}
					}
				}
			}
		}

		fastCodeObjectsMap = (Map<Object, List<FastCodeEntityHolder>>) contextMap.get(FC_OBJ_CREATED);

		if (!fastCodeObjectsMap.isEmpty()) {
			try {
				final boolean addToCache = false;
				final RepositoryService repositoryService = getRepositoryServiceClass();
				repositoryService.commitToRepository(fastCodeObjectsMap, addToCache);
			} catch (final FastCodeRepositoryException ex) {
				ex.printStackTrace();
			}
		}
		/*for (final Map.Entry<Object, StringBuilder> entry:commitMessageMap.entrySet()) {
			if (entry.getKey() instanceof IType) {
				//((IType)entry.getKey()).getUnderlyingResource().touch(new NullProgressMonitor());
				final File file = new File(((IType)entry.getKey()).getResource().getLocationURI());
				checkIn(file, entry.getValue().toString());
			}
		}*/
		//this.subActions.clear();
		actionList.clear();
		return snippetBuilder.toString().trim();
	}

	/**
	 * @param tagFound
	 * @param tagBody
	 * @param insideTagBody
	 * @param compUnit
	 * @param placeHolders
	 * @param spacesBeforeCursor
	 * @param contextMap
	 * @param hasSubAction
	 * @return
	 * @throws Exception
	 */
	private Action populateAction(final TemplateTag tagFound, final String tagBody, final String insideTagBody, final ICompilationUnit compUnit,
			final boolean hasSubAction1, final Map<String, Object> placeHolders, final Map<String, Object> contextMap, final String spacesBeforeCursor)
			throws Exception {

		final int startTagEnd = tagBody.indexOf(XML_END);

		final Map<String, String> attributes;
		boolean optional = false;
		ACTION_TYPE actionType = ACTION_TYPE.Create;

		final int tagEnd = tagFound == TemplateTag.IMPORT ? getTemplateTagStart(tagFound).length() + 1 : getTemplateTagStart(tagFound)
				.length();
		if (startTagEnd > tagEnd) {
			attributes = getAttributes(tagBody.substring(tagFound == TemplateTag.IMPORT ? getTemplateTagStart(tagFound).length() + 1
					: getTemplateTagStart(tagFound).length(), startTagEnd));
		} else {
			attributes = Collections.EMPTY_MAP;
		}
		FCTagHandler tagHandler;
		switch (tagFound) {
		case IMPORT:
			tagHandler = new FCImportTagHandler();

			final Action actionImport = tagHandler.populateTagAction(tagFound, tagBody, insideTagBody, compUnit, hasSubAction1,
					placeHolders, contextMap, spacesBeforeCursor, attributes, this.existingMembersBuilder, actionList);
			if (hasSubAction1 && actionImport != null) {
				this.subActions.add(actionImport);
				actionImport.setTarget(compUnit);
			}
			return actionImport;
		case METHOD:
			tagHandler = new FCMethodTagHandler();
			return tagHandler.populateTagAction(tagFound, tagBody, insideTagBody, compUnit, hasSubAction1, placeHolders, contextMap,
					spacesBeforeCursor, attributes, this.existingMembersBuilder, actionList);

		case FIELD:
			tagHandler = new FCFieldTagHandler();
			return tagHandler.populateTagAction(tagFound, tagBody, insideTagBody, compUnit, hasSubAction1, placeHolders, contextMap,
					spacesBeforeCursor, attributes, this.existingMembersBuilder, actionList);

		case CLASS:
		case CLASSES:
			tagHandler = new FCClassTagHandler();
			return tagHandler.populateTagAction(tagFound, tagBody, insideTagBody, compUnit, hasSubAction1, placeHolders, contextMap,
					spacesBeforeCursor, attributes, this.existingMembersBuilder, actionList);

		case PACKAGE:
			tagHandler = new FCPackageTagHandler();
			return tagHandler.populateTagAction(tagFound, tagBody, insideTagBody, compUnit, hasSubAction1, placeHolders, contextMap,
					spacesBeforeCursor, attributes, this.existingMembersBuilder, actionList);

		case PROJECT:
			tagHandler = new FCProjectTagHandler();
			return tagHandler.populateTagAction(tagFound, tagBody, insideTagBody, compUnit, hasSubAction1, placeHolders, contextMap,
					spacesBeforeCursor, attributes, this.existingMembersBuilder, actionList);

		case FILE:
		case FILES:
			tagHandler = new FCFileTagHandler();
			return tagHandler.populateTagAction(tagFound, tagBody, insideTagBody, compUnit, hasSubAction1, placeHolders, contextMap,
					spacesBeforeCursor, attributes, this.existingMembersBuilder, actionList);
		case XML:
			tagHandler = new FCXmlTagHandler();
			return tagHandler.populateTagAction(tagFound, tagBody, insideTagBody, compUnit, hasSubAction1, placeHolders, contextMap,
					spacesBeforeCursor, attributes, this.existingMembersBuilder, actionList);
		case PROPERTY:

			tagHandler = new FCPropertyTagHandler();
			return tagHandler.populateTagAction(tagFound, tagBody, insideTagBody, compUnit, hasSubAction1, placeHolders, contextMap,
					spacesBeforeCursor, attributes, this.existingMembersBuilder, actionList);
		case FOLDER:
			tagHandler = new FCFolderTagHandler();
			return tagHandler.populateTagAction(tagFound, tagBody, insideTagBody, compUnit, hasSubAction1, placeHolders, contextMap,
					spacesBeforeCursor, attributes, this.existingMembersBuilder, actionList);

		case SNIPPET:
			tagHandler = new FCSnippetTagHandler();
			return tagHandler.populateTagAction(tagFound, tagBody, insideTagBody, compUnit, hasSubAction1, placeHolders, contextMap,
					spacesBeforeCursor, attributes, this.existingMembersBuilder, actionList);
		case MESSAGE:

			final String title = attributes.containsKey(PLACEHOLDER_TITLE) ? attributes.get(PLACEHOLDER_TITLE) : null;
			optional = attributes.containsKey(OPTIONAL) ? Boolean.valueOf(attributes.get(OPTIONAL)) : false;
			if (!isEmpty(insideTagBody) && insideTagBody.contains(XML_START + TEMPLATE_TAG_PREFIX + COLON)) {
				throw new Exception("There should not be any other tags inside <fc:message>,  exiting....");
			}
			final Action actionMessage = new Action.Builder().withEntity(ACTION_ENTITY.Message).withType(actionType).withEntityName(title)
					.withSource(isEmpty(insideTagBody) ? insideTagBody : insideTagBody.trim()).withLabelMsg("Show  " + title)
					.withOptional(optional).build();

			return actionMessage;
			//break;

		case EXIT:

			final String message = attributes.containsKey(PLACEHOLDER_MESSAGE) ? attributes.get(PLACEHOLDER_MESSAGE) : null;
			//optional = attributes.containsKey(OPTIONAL) ? Boolean.valueOf(attributes.get(OPTIONAL)) : false;
			actionType = ACTION_TYPE.Prompt;
			if (!isEmpty(insideTagBody) && insideTagBody.contains(XML_START + TEMPLATE_TAG_PREFIX + COLON)) {
				throw new Exception("There should not be any other tags inside <fc:exit>,  exiting....");
			}
			final Action actionExit = new Action.Builder().withEntity(ACTION_ENTITY.Exit).withType(actionType)
					.withLabelMsg("Exit : " + message).build();

			return actionExit;
			//break;

		case INFO:
			final Action actionInfo = new Action.Builder().withEntity(ACTION_ENTITY.Info).withType(ACTION_TYPE.Prompt)
					.withSource(insideTagBody.trim()).build();
			return actionInfo;

		}
		//break;
		return null;

	}

	/**
	 * @param actionSelected
	 * @param compUnit
	 * @param editorPart
	 * @param contextMap
	 * @param placeHolders
	 * @param spacesBeforeCursor
	 * @return
	 * @throws Exception
	 */
	private void createActionEntity(final Action actionSelected, final ICompilationUnit compUnit, final IEditorPart editorPart,
			final Map<String, Object> contextMap, final Map<String, Object> placeHolders, final String spacesBeforeCursor) throws Exception {
		final ACTION_ENTITY actionEntity = actionSelected.getEntity();

		switch (actionEntity) {
		case Import:
			final ICompilationUnit compUnitNew = (ICompilationUnit) (actionSelected.getTarget() == null ? compUnit : actionSelected
					.getTarget());
			final FCImportTagHandler fcImportTagHandler = new FCImportTagHandler();
			fcImportTagHandler.createImportFromTag(actionSelected.getSource(), compUnitNew);
			break;
		case Method:
			final FCMethodTagHandler fcMethodTagHandler = new FCMethodTagHandler();

			fcMethodTagHandler.createMethodFromTag(actionSelected.getEntityName(), actionSelected.getSource(),
					(FastCodeType) actionSelected.getTarget(), actionSelected.getTypeToCreate(), actionSelected.getImports(), contextMap,
					spacesBeforeCursor, placeHolders, compUnit);
			break;
		case Field:
			final FCFieldTagHandler fcFieldTagHandler = new FCFieldTagHandler();

			fcFieldTagHandler.createFieldFromTag(actionSelected.getEntityName(), actionSelected.getSource(),
					(FastCodeType) actionSelected.getTarget(), actionSelected.getClassToImport(), actionSelected.getImports(), contextMap,
					spacesBeforeCursor, placeHolders, compUnit);
			break;
		case Class:
			final FCClassTagHandler fcClassTagHandler = new FCClassTagHandler();

			fcClassTagHandler.createClassFromTag(actionSelected.getEntityName(), actionSelected.getPackge(), actionSelected.getProject(),
					actionSelected.getSource(), contextMap, placeHolders, compUnit, actionSelected.getTypeToCreate(), spacesBeforeCursor,
					actionSelected.isOverrideMethods(), actionSelected.isExist(), this.overWrite);
			break;
		case Classes:
			final FCClassTagHandler classTagHandler = new FCClassTagHandler();
			for (final String className : actionSelected.getEntityName().split(actionSelected.getDelimiter())) {
				classTagHandler.createClassFromTag(className, actionSelected.getPackge(), actionSelected.getProject(),
						actionSelected.getSource(), contextMap, placeHolders, compUnit, actionSelected.getTypeToCreate(),
						spacesBeforeCursor, actionSelected.isOverrideMethods(), actionSelected.isExist(), this.overWrite);
			}
			break;
		case Package:
			final FCPackageTagHandler fcPackageTagHandler = new FCPackageTagHandler();
			fcPackageTagHandler.createPackageFromTag(actionSelected.getEntityName(), actionSelected.getTypeToCreate(),
					actionSelected.getProject(), contextMap, placeHolders);
			break;
		case Project:
			final FCProjectTagHandler fcProjectTagHandler = new FCProjectTagHandler();
			fcProjectTagHandler.createProjectFromTag(actionSelected.getEntityName(), actionSelected.getTypeToCreate(),
					actionSelected.getProjectSrcPath());
			break;
		case File:
			final FCFileTagHandler fcFileTagHandler = new FCFileTagHandler();
			fcFileTagHandler.createFileFromTag(actionSelected.getEntityName(), actionSelected.getFolderPath(), actionSelected.getSource(),
					contextMap, placeHolders, actionSelected.isExist(), actionSelected.getProject(), this.overWrite);
			break;
		case Files:
			final FCFileTagHandler fileTagHandler = new FCFileTagHandler();
			for (final String fileName : actionSelected.getEntityName().split(actionSelected.getDelimiter())) {
				fileTagHandler.createFileFromTag(fileName.trim(), actionSelected.getFolderPath(), actionSelected.getSource(), contextMap,
						placeHolders, actionSelected.isExist(), actionSelected.getProject(), this.overWrite);
			}
			break;
		case Xml:
			final FCXmlTagHandler fcXmlTagHandler = new FCXmlTagHandler();
			fcXmlTagHandler.createXMLTag(actionSelected.getNodeName(), actionSelected.getRootNodeName(), actionSelected.getSource(),
					(String) actionSelected.getTarget(), editorPart, contextMap, placeHolders);
			break;
		case Property:
			final FCPropertyTagHandler fcPropertyTagHandler = new FCPropertyTagHandler();
			fcPropertyTagHandler.createPropertyFromTag(actionSelected.getTarget(), actionSelected.getSource());
			break;
		case Folder:
			final FCFolderTagHandler fcFolderTagHandler = new FCFolderTagHandler();
			fcFolderTagHandler.createFolderFromTag(actionSelected.getFolderPath(), actionSelected.getProject(), placeHolders, contextMap);//createFolder(new Path(actionSelected.getDir()));
			break;
		case Snippet:
			final FCSnippetTagHandler fcSnippetTagHandler = new FCSnippetTagHandler();
			fcSnippetTagHandler.createSnippetFromTag(actionSelected.getSource(), actionSelected.getTarget(), editorPart,
					spacesBeforeCursor, contextMap, placeHolders, compUnit);
			break;
		/*case LocalVar:
		selectLocalVariables(actionSelected, compUnit, editorPart, placeHolders);
		break;*/

		default:
			break;

		}
	}

	/**
	 * @author
	 *
	 */
	private class ActionLabelProvider implements ILabelProvider {

		private Image	image;
		FastCodeCache	fastCodeCache	= FastCodeCache.getInstance();

		@Override
		public void addListener(final ILabelProviderListener labelProviderListener) {

		}

		@Override
		public void dispose() {
			/*	if (this.image != null && !this.image.isDisposed()) {
					this.image.dispose();
				}*/
		}

		@Override
		public boolean isLabelProperty(final Object arg0, final String arg1) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void removeListener(final ILabelProviderListener arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public Image getImage(final Object input) {
			if (input instanceof Action) {
				return getImageForAction((Action) input);
			}
			if (input instanceof Actions) {
				for (final Action action : ((Actions) input).getActions()) {
					return getImageForAction(action);
				}
			}
			return null;
		}

		/**
		 * @param input
		 * @return
		 */
		private Image getImageForAction(final Action input) {
			final ACTION_ENTITY actionEntity = input.getEntity();
			Image entityImage = null;
			final GlobalSettings globalSettings = GlobalSettings.getInstance();
			String image = globalSettings.getPropertyValue(actionEntity.getValue().toUpperCase() + UNDERSCORE + "IMAGE", EMPTY_STR);

			if (actionEntity == ACTION_ENTITY.Import) {
				if (this.fastCodeCache.getEntityImageMap().containsKey(actionEntity.getValue())) {
					return getImagefromFCCacheMap(actionEntity.getValue());
				}
				entityImage = this.getImage(image);
				populateFCCacheEntityImageMap(actionEntity.getValue(), entityImage);
				return entityImage;//("imp_obj.gif");
			} else if (actionEntity == ACTION_ENTITY.Method) {
				if (this.fastCodeCache.getEntityImageMap().containsKey(actionEntity.getValue())) {
					return getImagefromFCCacheMap(actionEntity.getValue());
				}
				entityImage = this.getImage(image);
				populateFCCacheEntityImageMap(actionEntity.getValue(), entityImage);
				return entityImage;//("method.gif");
			} else if (actionEntity == ACTION_ENTITY.Field) {
				if (this.fastCodeCache.getEntityImageMap().containsKey(actionEntity.getValue())) {
					return getImagefromFCCacheMap(actionEntity.getValue());
				}
				entityImage = this.getImage(image);
				populateFCCacheEntityImageMap(actionEntity.getValue(), entityImage);
				return entityImage;//("field.gif");
			} else if (actionEntity == ACTION_ENTITY.File || actionEntity == ACTION_ENTITY.Files || actionEntity == ACTION_ENTITY.Property) {
				if (this.fastCodeCache.getEntityImageMap().containsKey(actionEntity.getValue())) {
					return getImagefromFCCacheMap(actionEntity.getValue());
				}
				entityImage = this.getImage(image);
				populateFCCacheEntityImageMap(actionEntity.getValue(), entityImage);
				return entityImage;//("file_obj.gif");
			} else if (actionEntity == ACTION_ENTITY.Xml) {
				if (this.fastCodeCache.getEntityImageMap().containsKey(actionEntity.getValue())) {
					return getImagefromFCCacheMap(actionEntity.getValue());
				}
				entityImage = this.getImage(image);
				populateFCCacheEntityImageMap(actionEntity.getValue(), entityImage);
				return entityImage;//("xml_image.gif");
			} else if (actionEntity == ACTION_ENTITY.Class || actionEntity == ACTION_ENTITY.Classes) {
				if (actionEntity == ACTION_ENTITY.Classes) {
					if (input.getTypeToCreate().equals(PLACEHOLDER_CLASS)
							&& this.fastCodeCache.getEntityImageMap().containsKey(input.getTypeToCreate())) {
						return getImagefromFCCacheMap(PLACEHOLDER_CLASS);
					} else if (input.getTypeToCreate().equals(ACTION_ENTITY.Test.getValue())
							&& this.fastCodeCache.getEntityImageMap().containsKey(input.getTypeToCreate() + UNDERSCORE + PLACEHOLDER_CLASS)) {
						return getImagefromFCCacheMap(input.getTypeToCreate() + UNDERSCORE + PLACEHOLDER_CLASS);
					}
					image = globalSettings.getPropertyValue(PLACEHOLDER_CLASS.toUpperCase() + UNDERSCORE + "IMAGE", EMPTY_STR);
					entityImage = this.getImage(image);
					if (input.getTypeToCreate().equals(PLACEHOLDER_CLASS)) {
						populateFCCacheEntityImageMap(input.getTypeToCreate(), entityImage);
					} else if (input.getTypeToCreate().equals(ACTION_ENTITY.Test.getValue())) {
						populateFCCacheEntityImageMap(input.getTypeToCreate() + UNDERSCORE + PLACEHOLDER_CLASS, entityImage);
					}
					return entityImage;

				}
				if (input.getTypeToCreate().equals(PLACEHOLDER_CLASS) || input.getTypeToCreate().equals(ACTION_ENTITY.Test.getValue())) {
					if (input.getTypeToCreate().equals(PLACEHOLDER_CLASS)
							&& this.fastCodeCache.getEntityImageMap().containsKey(input.getTypeToCreate())) {
						return getImagefromFCCacheMap(actionEntity.getValue());
					} else if (input.getTypeToCreate().equals(ACTION_ENTITY.Test.getValue())
							&& this.fastCodeCache.getEntityImageMap().containsKey(input.getTypeToCreate() + UNDERSCORE + PLACEHOLDER_CLASS)) {
						return getImagefromFCCacheMap(input.getTypeToCreate() + UNDERSCORE + PLACEHOLDER_CLASS);
					}
					entityImage = this.getImage(image);
					if (input.getTypeToCreate().equals(PLACEHOLDER_CLASS)) {
						populateFCCacheEntityImageMap(input.getTypeToCreate(), entityImage);
					} else if (input.getTypeToCreate().equals(ACTION_ENTITY.Test.getValue())) {
						populateFCCacheEntityImageMap(input.getTypeToCreate() + UNDERSCORE + PLACEHOLDER_CLASS, entityImage);
					}
					return entityImage;//("classs_obj.gif");
				} else if (input.getTypeToCreate().equals(PLACEHOLDER_INTERFACE)) {
					if (this.fastCodeCache.getEntityImageMap().containsKey(input.getTypeToCreate())) {
						return getImagefromFCCacheMap(input.getTypeToCreate());
					}
					image = globalSettings.getPropertyValue(PLACEHOLDER_INTERFACE.toUpperCase() + UNDERSCORE + "IMAGE", EMPTY_STR);
					entityImage = this.getImage(image);
					populateFCCacheEntityImageMap(input.getTypeToCreate(), entityImage);
					return entityImage;//("int_obj.gif");
				}
			} else if (actionEntity == ACTION_ENTITY.Folder) {
				if (this.fastCodeCache.getEntityImageMap().containsKey(actionEntity.getValue())) {
					return getImagefromFCCacheMap(actionEntity.getValue());
				}
				entityImage = this.getImage(image);
				populateFCCacheEntityImageMap(actionEntity.getValue(), entityImage);
				return entityImage;//("fldr_obj.gif");
			} else if (actionEntity == ACTION_ENTITY.Package) {
				if (this.fastCodeCache.getEntityImageMap().containsKey("new" + UNDERSCORE + actionEntity.getValue())) {
					return getImagefromFCCacheMap("new" + UNDERSCORE + actionEntity.getValue());
				}
				image = globalSettings.getPropertyValue("NEW" + UNDERSCORE + actionEntity.getValue().toUpperCase() + UNDERSCORE + "IMAGE",
						EMPTY_STR);
				entityImage = this.getImage(image);
				populateFCCacheEntityImageMap("new" + UNDERSCORE + actionEntity.getValue(), entityImage);
				return entityImage;//("package_obj.gif");
			} else if (actionEntity == ACTION_ENTITY.Project) {
				if (input.getTypeToCreate().equals("java")) {
					if (this.fastCodeCache.getEntityImageMap().containsKey(input.getTypeToCreate() + UNDERSCORE + actionEntity.getValue())) {
						return getImagefromFCCacheMap(input.getTypeToCreate() + UNDERSCORE + actionEntity.getValue());
					}
					image = globalSettings.getPropertyValue(input.getTypeToCreate().toUpperCase() + UNDERSCORE
							+ actionEntity.getValue().toUpperCase() + UNDERSCORE + "IMAGE", EMPTY_STR);
					entityImage = this.getImage(image);
					populateFCCacheEntityImageMap(input.getTypeToCreate() + UNDERSCORE + actionEntity.getValue(), entityImage);
					return entityImage;//("projects.gif");
				} else if (input.getTypeToCreate().equals("groovy")) {
					if (this.fastCodeCache.getEntityImageMap().containsKey(input.getTypeToCreate() + UNDERSCORE + actionEntity.getValue())) {
						return getImagefromFCCacheMap(input.getTypeToCreate() + UNDERSCORE + actionEntity.getValue());
					}
					image = globalSettings.getPropertyValue(input.getTypeToCreate().toUpperCase() + UNDERSCORE
							+ actionEntity.getValue().toUpperCase() + UNDERSCORE + "IMAGE", EMPTY_STR);
					entityImage = this.getImage(image);
					populateFCCacheEntityImageMap(input.getTypeToCreate() + UNDERSCORE + actionEntity.getValue(), entityImage);
					return entityImage;//("newgroovyprj_wiz.gif");
				} else if (input.getTypeToCreate().endsWith("php")) {
					if (this.fastCodeCache.getEntityImageMap().containsKey(input.getTypeToCreate() + UNDERSCORE + actionEntity.getValue())) {
						return getImagefromFCCacheMap(input.getTypeToCreate() + UNDERSCORE + actionEntity.getValue());
					}
					image = globalSettings.getPropertyValue(input.getTypeToCreate().toUpperCase() + UNDERSCORE
							+ actionEntity.getValue().toUpperCase() + UNDERSCORE + "IMAGE", EMPTY_STR);
					entityImage = this.getImage(image);
					populateFCCacheEntityImageMap(input.getTypeToCreate() + UNDERSCORE + actionEntity.getValue(), entityImage);
					return entityImage;//("add_php_project.gif");
				} else {
					if (this.fastCodeCache.getEntityImageMap().containsKey(actionEntity.getValue())) {
						return getImagefromFCCacheMap(actionEntity.getValue());
					}
					entityImage = this.getImage(image);
					populateFCCacheEntityImageMap(actionEntity.getValue(), entityImage);
					return entityImage;//("prj_obj.gif");
				}
			} /*else if (actionEntity == ACTION_ENTITY.LocalVar) {
				}*/
			return null;
		}

		/**
		 * Gets the image.
		 *
		 * @param imageName
		 *            the image name
		 * @return the image
		 */
		private Image getImage(String imageName) {
			URL url = null;
			if (imageName == null) {
				return null;
			}
			final Image image = PlatformUI.getWorkbench().getSharedImages().getImage(imageName);
			if (image != null && !image.isDisposed()) {
				// this.image = null;
				return image;
			}
			try {
				if (imageName.startsWith("org.eclipse.jdt.ui.")) {
					imageName = imageName.substring("org.eclipse.jdt.ui.".length());
				}
				url = new URL(Activator.getDefault().getDescriptor().getInstallURL(), "icons/" + imageName);
			} catch (final MalformedURLException ex) {
				ex.printStackTrace();
				return null;
			}

			final ImageDescriptor descriptor = ImageDescriptor.createFromURL(url);
			this.image = descriptor.createImage();
			return this.image;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(final Object input) {
			if (input instanceof Action) {
				return ((Action) input).getLabelMsg();
			}
			if (input instanceof Actions) {
				for (final Action action : ((Actions) input).getActions()) {
					return action.getLabelMsg();
				}
			}
			return null;
		}

	}

	/**
	 * @author
	 *
	 */
	private class ActionContentProvider implements ITreeContentProvider {

		@Override
		public void dispose() {
			// TODO Auto-generated method stub

		}

		@Override
		public void inputChanged(final Viewer arg0, final Object arg1, final Object arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public Object[] getChildren(final Object input) {
			if (!(input instanceof Actions || input instanceof Action)) {
				return null;
			}
			Action action = null;
			Action[] subActions = null;
			try {
				if (input instanceof Action) {
					action = (Action) input;
					if (action.getSubAction() != null) {
						subActions = action.getSubAction().isEmpty() ? null : action.getSubAction().toArray(new Action[0]);
						return subActions;
					}
				}
				if (input instanceof Actions) {
					return ((Actions) input).getActions();
				}

			} catch (final Exception ex) {
				ex.printStackTrace();
				return null;
			}
			return null;
		}

		@Override
		public Object[] getElements(final Object input) {
			return getChildren(input);
		}

		@Override
		public Object getParent(final Object input) {
			return input instanceof Action ? input : null;
		}

		@Override
		public boolean hasChildren(final Object input) {
			if (!(input instanceof Action || input instanceof Actions)) {
				return false;
			}
			try {
				if (input instanceof Action) {
					if (((Action) input).getSubAction() != null && !((Action) input).getSubAction().isEmpty()) {
						return true;
					}
				}
			} catch (final Exception ex) {
				ex.printStackTrace();
			}
			return false;

		}

	}

	/**
	 * @param actionSelected
	 * @param compUnit
	 * @param editorPart
	 * @param placeHolders
	 * @throws Exception
	 */
	/*
	private void selectLocalVariables(final Action actionSelected, final ICompilationUnit compUnit, final IEditorPart editorPart,
		final Map<String, Object> placeHolders) throws Exception {
	final List<FastCodeReturn> selectedFields = new ArrayList<FastCodeReturn>();
	final List<FastCodeReturn> membersToWorkOn = getLocalVariablesOfType(compUnit, editorPart, actionSelected.getLocalVarType());

	FastCodeSelectionDialog selectionDialog = null;
	if (membersToWorkOn.size() > 0) {
		selectionDialog = new VariableSelectionDialog(new Shell(), "Variable Selection", "Select local variable(s)",
				membersToWorkOn.toArray(new FastCodeReturn[0]), Boolean.valueOf(actionSelected.getLocalVarSelectionMode()));
		if (selectionDialog.open() == CANCEL) {
			return;
		}

		for (final Object member : selectionDialog.getResult()) {
			selectedFields.add((FastCodeReturn) member);
		}

		placeHolders.put(actionSelected.getLocalVarName(), Boolean.valueOf(actionSelected.getLocalVarSelectionMode()) ? selectedFields
				: selectedFields.get(0));
	}
	}*/
	/**
	 * @param actionList
	 * @param action
	 * @param found
	 * @return
	 */
	private boolean isDuplicateImportAction(final List<Action> actionList, final Action action) {
		for (final Action importAction : actionList) {
			if (importAction != null) {
				if (importAction.getSource().equals(action.getSource())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param message
	 * @param tagBody
	 */
	private void createMessageFromTag(final String title, final String message) {
		/*
		 * final int startMethodTagEnd = tagBody.indexOf(XML_END);
		 *
		 * Map<String, String> attributes = null; if (startMethodTagEnd >
		 * getTemplateTagStart(templateTag).length() + 1) { attributes =
		 * getAttributes
		 * (tagBody.substring(getTemplateTagStart(templateTag).length() + 1,
		 * startMethodTagEnd)); }
		 */
		MessageDialog.openWarning(new Shell(), title, message);
	}

	/**
	 * @param prj
	 * @param packageName
	 * @param typeToCreate
	 * @param contextMap
	 * @return
	 * @throws Exception
	 */
	public IPackageFragment createPackage(final IJavaProject prj, final String packageName, final String typeToCreate,
			final Map<String, Object> contextMap) throws Exception {
		IPackageFragmentRoot packageFragmentRoot = null;
		final GlobalSettings globalSettings = getInstance();
		final String path = globalSettings.isUseDefaultForPath() ? getDefaultPathFromProject(prj, typeToCreate, EMPTY_STR)
				: getPathFromUser("Choose Source Path to create package- " + packageName);

		if (path == null) {
			MessageDialog.openError(new Shell(), "Error",
					"Cannot proceed without providing path for src/resource/test folder of the peoject " + prj.getElementName()
							+ "...exiting..");
			return null;
		}

		packageFragmentRoot = getPackageRootFromProject(prj, path);
		final IPackageFragment packageFragment = packageFragmentRoot.createPackageFragment(packageName, true, null);
		return packageFragment;
	}

	/**
	 * @param targetClass
	 * @param type
	 * @throws Exception
	 */

	public void validateTargetClassType(final String targetClass, final IType type) throws Exception {
		if (!isEmpty(targetClass) && targetClass.contains(FORWARD_SLASH)) {
			return;
		}
		//final IType type = targetClass == null ? compUnit.findPrimaryType() : compUnit.getJavaProject().findType(targetClass.trim());
		if (type == null || !type.exists()) {
			//throw new Exception("Target class:   " + targetClass + "   specified in the XML is not found.Please choose another class");
		} else if (type.isBinary()) {
			throw new Exception("Target class:   " + targetClass
					+ "   specified in the XML is a binary type.Can not modify.Please choose another class");
		} /*else if (type.isInterface()) {
			throw new Exception("Target class:   " + targetClass
					+ "   specified in the XML is an interface.Can not create.Please choose another class");
			}*/else if (type.isReadOnly()) {
			throw new Exception("Target class:   " + targetClass
					+ "   specified in the XML is read only.Can not modify.Please choose another class.");
		}
	}

	/**
	 * @param compUnit
	 * @return
	 */
	public boolean validateCompUnit(final ICompilationUnit compUnit) {
		return compUnit != null;

	}

	/**
	 * @param tagBody
	 * @param templateTag
	 * @return
	 */
	private Map<String, String> getAttributesForTag(final String tagBody, final TemplateTag templateTag) {
		final int startMethodTagEnd = tagBody.indexOf(XML_END);

		Map<String, String> attributes = null;
		if (startMethodTagEnd > getTemplateTagStart(templateTag).length()) {
			attributes = getAttributes(tagBody.substring(getTemplateTagStart(templateTag).length(), startMethodTagEnd));
		}

		return attributes;
	}

	/**
	 * @param actionList
	 * @return
	 */
	private List<Action> filterActionList(final List<Action> actionList) {
		final List<Action> initialSelectionActionList = new ArrayList<Action>();
		for (final Action action : actionList) {
			if (action != null) {
				if (!(action.isOptional() || action.isExist())) {
					initialSelectionActionList.add(action);
				}
			}
		}
		return initialSelectionActionList;
	}

	/**
	 * @param dir
	 * @param project
	 * @param placeHolders
	 * @return
	 * @throws Exception
	 */
	public String validateFolderPath(String dir, final String project, final Map<String, Object> placeHolders) throws Exception {
		if (dir != null) {
			IJavaProject javaProject = null;
			if (project != null) {
				javaProject = getJavaProject(project);
				boolean createProject = false;
				if (javaProject == null) {
					for (final Action action : actionList) {
						if (action.getEntity() == ACTION_ENTITY.Project) {
							if (action.getEntityName().equals(project)) {
								javaProject = null;
								createProject = true;
								dir = project + FORWARD_SLASH + dir;
								return dir;
							}
						}
					}
					if (!createProject) {
						if (placeHolders.containsKey(PLACEHOLDER_PROJECT)) {
							javaProject = getJavaProject(placeHolders.get(PLACEHOLDER_PROJECT) instanceof FastCodeProject ? ((FastCodeProject) placeHolders
									.get(PLACEHOLDER_PROJECT)).getName() : (String) placeHolders.get(PLACEHOLDER_PROJECT));
						} else {
							javaProject = getWorkingJavaProjectFromUser();
						}

						dir = javaProject.getElementName() + FORWARD_SLASH + dir;
						return dir.trim();
					}
				}
				dir = javaProject.getElementName() + FORWARD_SLASH + dir;
				return dir;

			} else {
				if (placeHolders.containsKey(PLACEHOLDER_PROJECT)) {
					javaProject = getJavaProject(placeHolders.get(PLACEHOLDER_PROJECT) instanceof FastCodeProject ? ((FastCodeProject) placeHolders
							.get(PLACEHOLDER_PROJECT)).getName() : (String) placeHolders.get(PLACEHOLDER_PROJECT));
				} else {
					javaProject = getWorkingJavaProjectFromUser();
				}
				dir = javaProject.getElementName() + FORWARD_SLASH + dir;
				return dir;
			}
		}
		return dir;
	}

	/**
	 * @param javaProject
	 * @param defaultPath
	 * @param pkgName
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public IPackageFragment getPackageFragment(final IJavaProject javaProject, final String defaultPath, final String pkgName, final String type)
			throws Exception {
		IPackageFragment pkgFrgmt = null;
		for (final IPackageFragment packageFragment : getPackagesInProject(javaProject, defaultPath, type)) {
			if (packageFragment.getElementName().equals(pkgName)) {
				pkgFrgmt = packageFragment;
				break;
			}
		}
		return pkgFrgmt;
	}

	/**
	 * @param newFileObj
	 * @param project
	 * @return
	 * @throws Exception
	 */
	public static boolean proceedWithAutoCheckin(final File newFileObj, final IProject project) throws Exception {
		final VersionControlPreferences versionControlPreferences = VersionControlPreferences.getInstance();
		if (newFileObj != null) {
			addOrUpdateFileStatusInCache(newFileObj);
		}
		//checkinCache.getFilesToCheckIn().add(new FastCodeFileForCheckin(INITIATED, newFileObj.getAbsolutePath()));
		final boolean prjShared = !isEmpty(project.getPersistentProperties());
		final boolean prjConfigured = !isEmpty(isPrjConfigured(project.getName()));

		return versionControlPreferences.isEnable() && prjShared && prjConfigured;
	}

	public List<Action> getSubActions() {
		return this.subActions;
	}
	/*public class FastCodeCheckedTreeSelectionDialog extends CheckedTreeSelectionDialog {

		public FastCodeCheckedTreeSelectionDialog(Shell parent, ILabelProvider labelProvider, ITreeContentProvider contentProvider) {
			super(parent, labelProvider, contentProvider);

		}

		@Override
		protected CheckboxTreeViewer getTreeViewer() {
			CheckboxTreeViewer treeViewer = super.getTreeViewer();
			treeViewer.getTree().addListener(SWT.Selection, new Listener() {

				public void handleEvent(Event e) {
					if (e.detail == SWT.CHECK) {
						System.out.println(e.item);
					}
				}
			});
			return treeViewer;
		}
	}*/
}
