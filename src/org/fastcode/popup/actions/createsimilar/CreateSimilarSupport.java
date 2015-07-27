/**
 *
 */
package org.fastcode.popup.actions.createsimilar;

import static org.eclipse.jdt.core.IJavaElement.FIELD;
import static org.eclipse.jdt.core.IJavaElement.METHOD;
import static org.eclipse.jface.dialogs.MessageDialog.openError;
import static org.eclipse.jface.dialogs.MessageDialog.openInformation;
import static org.eclipse.jface.dialogs.MessageDialogWithToggle.openYesNoQuestion;
import static org.eclipse.jface.window.Window.CANCEL;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.JAVA_EXTENSION;
import static org.fastcode.common.FastCodeConstants.MESSAGE_DIALOG_RETURN_YES;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_CLASS;
import static org.fastcode.common.FastCodeConstants.SPACE;
import static org.fastcode.common.FastCodeConstants.UNDER_SCORE;
import static org.fastcode.preferences.PreferenceConstants.P_ASK_FOR_CONFIGURE_NOW;
import static org.fastcode.setting.GlobalSettings.getInstance;
import static org.fastcode.util.CreateSimilarDescriptor.getCreateSimilarDescriptor;
import static org.fastcode.util.EditorUtil.findMember;
import static org.fastcode.util.SourceUtil.checkForErrors;
import static org.fastcode.util.SourceUtil.createSimilar;
import static org.fastcode.util.SourceUtil.findSuperInterfaceType;
import static org.fastcode.util.SourceUtil.getPublicMethods;
import static org.fastcode.util.SourceUtil.getRepositoryServiceClass;
import static org.fastcode.util.SourceUtil.getSelectedMembers;
import static org.fastcode.util.SourceUtil.populatecreateDescClassWithUserInput;
import static org.fastcode.util.StringUtil.isAllLettersUpperCase;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.StringUtil.replacePlaceHolders;
import static org.fastcode.util.StringUtil.reverseCamelCase;
import static org.fastcode.util.VersionControlUtil.isPrjConfigured;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.IWorkingCopyManager;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.common.FastCodeEntityHolder;
import org.fastcode.common.FastCodeType;
import org.fastcode.preferences.VersionControlPreferences;
import org.fastcode.setting.GlobalSettings;
import org.fastcode.util.CreateSimilarDescriptor;
import org.fastcode.util.CreateSimilarDescriptorClass;
import org.fastcode.util.FastCodeConsole;
import org.fastcode.util.FastCodeContext;
import org.fastcode.util.FastCodeInput;
import org.fastcode.util.FastCodeResource;
import org.fastcode.util.FastCodeResourceChangeListener;
import org.fastcode.util.MessageUtil;
import org.fastcode.util.RepositoryService;
import org.fastcode.util.SourceUtil;

/**
 * @author Gautam
 *
 */
public abstract class CreateSimilarSupport {
	// Program uses these class variables for the progress monitor to work.

	// Figure out later how to make this more thread safe. progress monitor
	// is not cancelable for this reason.
	protected ICompilationUnit							retCompUnit;
	protected ICompilationUnit							compUnit;
	IMember												member;
	FastCodeConsole										fastCodeConsole	= FastCodeConsole.getInstance();
	String												errorMessage	= null;
	protected IWorkbenchWindow							window;
	protected IEditorPart								editorPart;
	protected String									preferenceId;
	protected boolean									createNew		= false;
	protected boolean									differentName	= false;
	private FastCodeContext								fastCodeContext	= null;
	protected Map<Object, List<FastCodeEntityHolder>>	commitMessage	= new HashMap<Object, List<FastCodeEntityHolder>>();
	protected boolean									autoCheckinEnabled;
	protected boolean									prjShared;
	protected boolean									prjConfigured;

	/**
	 *
	 * @param type
	 * @param monitor
	 * @return
	 * @throws Exception
	 */
	protected ICompilationUnit createSimilarClass(final ICompilationUnit compilationUnit, final IMember member,
			final IProgressMonitor monitor) throws Exception {

		final IPreferenceStore preferences = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		final VersionControlPreferences versionControlPreferences = VersionControlPreferences.getInstance();
		final IType type = this.createNew ? null : compilationUnit.findPrimaryType();
		final CreateSimilarDescriptorAndType createSimilarDescriptorAndType = findCreateSimilarDescriptor(type, member, this.differentName,
				this.createNew);
		final Shell parentShell = MessageUtil.getParentShell();
		final Shell shell = parentShell == null ? new Shell() : parentShell;

		this.autoCheckinEnabled = versionControlPreferences.isEnable();

		if (createSimilarDescriptorAndType == null || createSimilarDescriptorAndType.getCreateSimilarDescriptor() == null) {
			final MessageDialogWithToggle dialogWithToggle = openYesNoQuestion(shell, "Unable To Find Matching Class",
					"Unable To Find A Match According Configuration, Would Like To Configure The Plugin Now?", "Remember Decision", false,
					preferences, P_ASK_FOR_CONFIGURE_NOW);
			if (dialogWithToggle.getReturnCode() != MESSAGE_DIALOG_RETURN_YES) {
				return type.getCompilationUnit();
			}
			final PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(shell, "FastCode.preferences.FastCodePreferencePage",
					null, null);
			dialog.create();
			dialog.setMessage("Fast Code Preference");
			dialog.open();
			return compilationUnit;
		}

		final IType typeToWorkOn = createSimilarDescriptorAndType.getType();
		final IMember memberToWorkOn = createSimilarDescriptorAndType.getMember();
		this.fastCodeContext = new FastCodeContext(typeToWorkOn);
		final CreateSimilarDescriptor createSimilarDescriptor = createSimilarDescriptorAndType.getCreateSimilarDescriptor();
		String[] inputs = null;

		if (this.differentName || this.createNew) {

			final InputDialog inputDialog = new InputDialog(shell, "Create Similar/New", "Enter a new name or names (space separated)", "",
					null);

			if (inputDialog.open() == Window.CANCEL || inputDialog.getValue().trim().equals(EMPTY_STR)) {
				return this.createNew ? null : typeToWorkOn.getCompilationUnit();
			}

			inputs = inputDialog.getValue().split("\\s+");
			final StringBuilder input = new StringBuilder();
			boolean modified = false;
			for (final String inp : inputs) {
				final String modInput = isAllLettersUpperCase(inp) ? reverseCamelCase(inp, UNDER_SCORE) : inp.substring(0, 1).toUpperCase()
						+ inp.substring(1);
				if (!modInput.equals(inp)) {
					modified = true;
				}
				input.append(modInput + SPACE);
			}
			input.trimToSize();
			if (modified) {
				final InputDialog modInpDlg = new InputDialog(shell, "Create Similar/New Modified Input",
						"Some of the names were modified, press ok to accept, cancel to keep the names you typed.", input.toString(), null);
				if (modInpDlg.open() != CANCEL) {
					inputs = input.toString().split("\\s+");
				}
			}
		}

		IMember[] membersToWorkOn = {};
		if (!this.createNew && !typeToWorkOn.equals(type) && checkForErrors(typeToWorkOn.getCompilationUnit().getResource())) {

			if (MessageDialog.openQuestion(shell, "Fast Code Plug-in",
					"There seems to be some problems associated with " + typeToWorkOn.getElementName()
							+ ". It is better to fix those problems and try again. Want to Abort?")) {
				return compilationUnit;
			}
		}

		if (!this.createNew) {
			if (createSimilarDescriptor.isCopyMethod() && (memberToWorkOn == null || memberToWorkOn.getElementType() != METHOD)) {
				final IMethod[] methods = getPublicMethods(typeToWorkOn);
				membersToWorkOn = getMembersToCopy(METHOD, methods);
			} else if (createSimilarDescriptor.isCopyField() && (memberToWorkOn == null || memberToWorkOn.getElementType() != FIELD)) {
				final IField[] fields = SourceUtil.getFieldsOfType(typeToWorkOn);
				membersToWorkOn = getMembersToCopy(FIELD, fields);
			} else if (memberToWorkOn != null) {
				membersToWorkOn = new IMember[1];
				membersToWorkOn[0] = memberToWorkOn;
			}
		}

		if (membersToWorkOn == null) {
			return compilationUnit;
		}

		ICompilationUnit similarClass = null;

		if (this.differentName || this.createNew) {
			populatecreateDescClassWithUserInput(this.fastCodeContext, createSimilarDescriptor, inputs, this.differentName, typeToWorkOn);
			for (final String input : inputs) {
				this.fastCodeContext = this.differentName ? new FastCodeContext(typeToWorkOn) : new FastCodeContext();
				this.fastCodeContext.addToPlaceHolders("input", new FastCodeInput(input));
				/*
				 * if (this.differentName) { final Pattern p =
				 * Pattern.compile(createSimilarDescriptor.getFromPattern());
				 * final Matcher m =
				 * p.matcher(typeToWorkOn.getFullyQualifiedName());
				 *
				 * if (!m.matches()) { continue; }
				 *
				 * final String replatePart = m.group(m.groupCount());
				 * createSimilarDescriptor
				 * .createReplacePartAndValue(replatePart, input); }
				 */// String toPattern = createSimilarDescriptor.getToPattern();
					// toPattern = replacePlaceHolder(toPattern, "input",
					// newClass);
				similarClass = processCreateSimilarClass(this.fastCodeContext, membersToWorkOn, createSimilarDescriptor, monitor);

			}
		} else {
			similarClass = processCreateSimilarClass(this.fastCodeContext, membersToWorkOn, createSimilarDescriptor, monitor);
		}
		return similarClass;
	}

	/**
	 * @param members
	 * @param type
	 * @return
	 */
	private IMember[] getMembersToCopy(final int type, final IMember[] members) {
		return getSelectedMembers(type, members, "Copy", true);
	}

	/**
	 *
	 * @param typeToWorkOn
	 * @param membersToWorkOn
	 * @param createSimilarDescriptor
	 * @param monitor
	 * @return
	 * @throws Exception
	 */
	protected ICompilationUnit processCreateSimilarClass(final FastCodeContext fastCodeContext, final IMember[] membersToWorkOn,
			final CreateSimilarDescriptor createSimilarDescriptor, final IProgressMonitor monitor) throws Exception {

		final IType typeToWorkOn = fastCodeContext.getFromType();

		final boolean createWorkingSet = createSimilarDescriptor.isCreateWorkingSet();
		final List<IAdaptable> workingSetElements = new ArrayList<IAdaptable>();

		if (!this.createNew && !this.differentName && createSimilarDescriptor.isCreateWorkingSet()) {
			workingSetElements.add(typeToWorkOn.getCompilationUnit());
			if (typeToWorkOn.isInterface()) {
				final ITypeHierarchy hierarchy = typeToWorkOn.newTypeHierarchy(typeToWorkOn.getJavaProject(), null, monitor);
				for (final IType subType : hierarchy.getImplementingClasses(typeToWorkOn)) {
					workingSetElements.add(subType);
				}
			} else {
				workingSetElements.add(typeToWorkOn);
			}
		}

		monitor.beginTask("CreateSimilar", 2);
		monitor.subTask(this.createNew ? "New Class" : typeToWorkOn.getFullyQualifiedName());
		createSimilar(fastCodeContext, membersToWorkOn, createSimilarDescriptor, monitor);

		if (createWorkingSet) {
			for (final FastCodeResource resource : fastCodeContext.getResources()) {
				workingSetElements.add(resource.getResource());
			}
			// workingSetElements.add(retPair.getLeft());
			// if (retPair.getRight() != null) {
			// workingSetElements.add(retPair.getRight());
			// }
		}

		ICompilationUnit primaryInterfaceUnit = null;
		ICompilationUnit primaryClassUnit = null;
		final CreateSimilarDescriptorClass[] descriptorClasses = createSimilarDescriptor.getCreateSimilarDescUserChoice() == null ? createSimilarDescriptor
				.getCreateSimilarDescriptorClasses() : createSimilarDescriptor.getCreateSimilarDescUserChoice();
		for (final CreateSimilarDescriptorClass createSimilarDescriptorClass : descriptorClasses) {
			if (createSimilarDescriptorClass == null) {
				continue;
			}
			final ICompilationUnit compilationUnit = fastCodeContext.getCompilationUnitRegsistry(createSimilarDescriptorClass);
			if (compilationUnit != null) {
				if (compilationUnit.findPrimaryType().isInterface()) {
					primaryInterfaceUnit = compilationUnit;
				} else if (compilationUnit.findPrimaryType().isClass()) {
					primaryClassUnit = compilationUnit;
				}
			}
			if (createSimilarDescriptorClass.isCreateUnitTest()) {
				final IType typeToCreateJunitOn = compilationUnit.findPrimaryType();

				for (final IMember memberToWorkOn : membersToWorkOn) {
					final IMethod memberToCreateJunitOn = (IMethod) findMember(compilationUnit, memberToWorkOn);

					// final ICompilationUnit testUnit =
					// findTestUnit(compilationUnit.findPrimaryType());
					// if (testUnit == null) {
					// continue;
					// }

					// testUnit =
					// findTestUnit(retPair.getRight().findPrimaryType());
					// if (testUnit != null) {
					// typeToCreateJunitOn =
					// retPair.getRight().findPrimaryType();
					// memberToCreateJunitOn =
					// (IMethod)findMember(retPair.getRight(), memberToWorkOn);
					// }
					this.fastCodeConsole.writeToConsole("Creating Unit Test for " + typeToCreateJunitOn.getFullyQualifiedName());
					// final IMember testMethod =
					// generateTest(typeToCreateJunitOn, memberToCreateJunitOn);
					// if
					// (workingSetElements.indexOf(testMethod.getCompilationUnit())
					// < 0) {
					// workingSetElements.add(testMethod.getCompilationUnit());
					// }
				}

			}
		}

		monitor.worked(1);

		if (monitor.isCanceled()) {
			return primaryInterfaceUnit;
		}

		final CreateSimilarDescriptor nextDescriptor = createSimilarDescriptor.getNextDescriptor();
		if (nextDescriptor != null) {
			monitor.subTask(primaryInterfaceUnit.findPrimaryType().getFullyQualifiedName());
			createSimilar(fastCodeContext, membersToWorkOn, nextDescriptor, monitor);
		}
		monitor.worked(2);

		if (createWorkingSet) {
			String workingSetName = createSimilarDescriptor.getWorkingSetName();
			workingSetName = replacePlaceHolders(workingSetName, fastCodeContext.getPlaceHolders());

			final IWorkingSetManager workingSetManager = PlatformUI.getWorkbench().getWorkingSetManager();
			IWorkingSet workingSet = workingSetManager.getWorkingSet(workingSetName);
			if (workingSet == null) {
				this.fastCodeConsole.writeToConsole("Creating working set " + workingSetName);
				workingSet = workingSetManager.createWorkingSet(workingSetName, workingSetElements.toArray(new IAdaptable[0]));
				workingSetManager.addWorkingSet(workingSet);
			} else if (workingSet.isEditable()) {
				this.fastCodeConsole.writeErrorToConsole("Working set " + workingSetName + " exists already, modifying as necessary.");
				final List<IAdaptable> wSetElements = new ArrayList<IAdaptable>();
				for (final IAdaptable adaptable : workingSet.getElements()) {
					wSetElements.add(adaptable);
				}
				for (final IAdaptable adaptable : workingSetElements) {
					wSetElements.add(adaptable);
				}
				workingSet.setElements(wSetElements.toArray(new IAdaptable[0]));
			}
			final IWorkingSet[] workingSets = workingSetManager.getAllWorkingSets();
			final IWorkingSet[] newWorkingSets = new IWorkingSet[workingSets.length + 1];
			int i = 0;
			for (final IWorkingSet workingSet2 : workingSets) {
				newWorkingSets[i++] = workingSet2;
			}
			newWorkingSets[i] = workingSet;
			// PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().setWorkingSets(newWorkingSets);
		}

		if (this.autoCheckinEnabled) {
			for (final FastCodeResource fastCodeResource : fastCodeContext.getResources()) {
				if (fastCodeResource.isNewResource()) {
					final IFile file = (IFile) fastCodeResource.getResource();
					final boolean prjShared = !isEmpty(file.getProject().getPersistentProperties());
					final boolean prjConfigured = !isEmpty(isPrjConfigured(file.getProject().getName()));
					if (prjShared && prjConfigured) {
						List<FastCodeEntityHolder> chngesForFile = this.commitMessage.get(file);
						if (chngesForFile == null) {
							chngesForFile = new ArrayList<FastCodeEntityHolder>();
							chngesForFile.add(new FastCodeEntityHolder(PLACEHOLDER_CLASS, new FastCodeType(((ICompilationUnit) JavaCore
									.create(fastCodeResource.getResource())).findPrimaryType().getFullyQualifiedName())));
						}
						this.commitMessage.put(file, chngesForFile);
					}
				}
			}
		}
		return primaryClassUnit;
	}

	/**
	 *
	 * @param type
	 * @param member
	 * @return
	 * @throws Exception
	 */
	private CreateSimilarDescriptorAndType findCreateSimilarDescriptor(IType type, IMember member, final boolean differentName,
			final boolean createNew) throws Exception {
		CreateSimilarDescriptor createSimilarDescriptor;
		final Shell parentShell = MessageUtil.getParentShell();
		final Shell shell = parentShell == null ? new Shell() : parentShell;

		if (createNew) {
			return new CreateSimilarDescriptorAndType(getCreateSimilarDescriptor(this.preferenceId), null, null);
		} else {
			createSimilarDescriptor = getCreateSimilarDescriptor(type, differentName);
		}

		final GlobalSettings globalSettings = getInstance();

		if (createSimilarDescriptor != null) {
			if (type.isInterface() && globalSettings.isShowTips()) {
				openInformation(shell, "Information", "Just so you know, it is possible to do this from the implementation class.");
			}
			return new CreateSimilarDescriptorAndType(createSimilarDescriptor, type, member);
		} else if (type.isInterface()) {
			return null;
		}

		final IType superInterface = findSuperInterfaceType(type);

		if (superInterface != null) {
			createSimilarDescriptor = getCreateSimilarDescriptor(superInterface, differentName);
		}
		type = superInterface;
		if (member != null && member instanceof IMethod) {
			final IMethod method = (IMethod) member;
			member = type.getMethod(member.getElementName(), method.getParameterTypes());
			if (member == null || !member.exists()) {
				throw new Exception("The method you selected in not present in the interface " + type.getFullyQualifiedName());
			}
		}

		if (createSimilarDescriptor != null) {
			return new CreateSimilarDescriptorAndType(createSimilarDescriptor, type, member);
		}

		return null;
	}

	/**
	 *
	 * @author Gautam
	 *
	 */
	private class CreateSimilarDescriptorAndType {
		private final CreateSimilarDescriptor	createSimilarDescriptor;
		private final IType						type;
		private final IMember					member;

		/**
		 *
		 * @param createSimilarDescriptor
		 * @param type
		 */
		public CreateSimilarDescriptorAndType(final CreateSimilarDescriptor createSimilarDescriptor, final IType type, final IMember member) {
			this.createSimilarDescriptor = createSimilarDescriptor;
			this.type = type;
			this.member = member;
		}

		/**
		 *
		 * @return
		 */
		public CreateSimilarDescriptor getCreateSimilarDescriptor() {
			return this.createSimilarDescriptor;
		}

		/**
		 * @return the type
		 */
		public IType getType() {
			return this.type;
		}

		/**
		 * @return the member
		 */
		public IMember getMember() {
			return this.member;
		}

	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(final IAction action) {
		this.errorMessage = null;
		final IWorkingCopyManager manager = JavaUI.getWorkingCopyManager();

		final Shell parentShell = MessageUtil.getParentShell();
		final Shell shell = parentShell == null ? new Shell() : parentShell;
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(FastCodeResourceChangeListener.getInstance());
		if (this.window != null) {
			this.editorPart = this.window.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			if (this.editorPart == null) {
				openError(shell, "Error", "There is no file open in the editor.");
				return;
			}
			if (manager != null) {
				this.compUnit = manager.getWorkingCopy(this.editorPart.getEditorInput());
			} else {
				final IJavaElement element = JavaUI.getEditorInputJavaElement(this.editorPart.getEditorInput());
				this.compUnit = (ICompilationUnit) element.getAdapter(ICompilationUnit.class);
			}
		}

		if (!this.createNew && this.compUnit == null) {
			this.errorMessage = "This is not a java class, cannot continue.";
			if (this.editorPart.getEditorInput().getName().endsWith(JAVA_EXTENSION)) {
				this.errorMessage = "Some error occured. Please try again or submit a bug.";
			}
			openError(this.editorPart.getSite().getShell(), "Error", this.errorMessage);
			return;
		}

		try {
			if (!this.createNew && checkForErrors(this.compUnit.getResource())) {
				if (!MessageDialog
						.openQuestion(
								shell,
								"Fast Code Plug-in",
								"There seems to be some problems associated with this file. It is better to fix those problems and try again. Do you still want to continue?")) {
					return;
				}
			}
			final IRunnableWithProgress op = new IRunnableWithProgress() {
				@Override
				public void run(final IProgressMonitor monitor) {
					try {
						CreateSimilarSupport.this.member = CreateSimilarSupport.this.createNew ? null : getSelectedMember();
						CreateSimilarSupport.this.retCompUnit = createSimilarClass(CreateSimilarSupport.this.compUnit,
								CreateSimilarSupport.this.member, monitor);
					} catch (final Exception ex) {
						ex.printStackTrace();
						CreateSimilarSupport.this.errorMessage = ex.getMessage();
					} finally {
						monitor.done();
					}
				}
			};

			final IWorkbench wb = PlatformUI.getWorkbench();
			final IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
			new ProgressMonitorDialog(shell).run(false, false, op);

			if (this.retCompUnit == null) {
				throw new Exception("Problem creating similar class " + this.errorMessage);
			} else if (!this.retCompUnit.equals(this.compUnit)) {
				IType primaryType = this.retCompUnit.findPrimaryType();
				if (primaryType.isInterface()) {
					for (final ICompilationUnit compilationUnit : this.fastCodeContext.getCompilationUnitRegsistry().values()) {
						final IType pType = compilationUnit.findPrimaryType();
						if (pType != null && pType.exists() && pType.isClass()) {
							primaryType = pType;
							break;
						}
					}
				}
				final IEditorPart javaEditor = JavaUI.openInEditor(primaryType);
				JavaUI.revealInEditor(javaEditor, (IJavaElement) primaryType);
				if (this.member != null) {
					showMember(primaryType.getCompilationUnit(), this.member);
				}

				final List<FastCodeResource> resources = this.fastCodeContext.getResources();

				if (!this.commitMessage.isEmpty()) {
					final boolean addtoCache = false;
					final RepositoryService repositoryService = getRepositoryServiceClass();
					repositoryService.commitToRepository(this.commitMessage, addtoCache);
				}
			}
		} catch (final Exception e) {
			e.printStackTrace();
			this.errorMessage = e.getMessage();
		} finally {
			this.commitMessage.clear();
			ResourcesPlugin.getWorkspace().addResourceChangeListener(FastCodeResourceChangeListener.getInstance());
		}

		if (!isEmpty(this.errorMessage)) {
			MessageDialog.openError(shell, "Error", "Ooops, Action was not executed. " + this.errorMessage);
		}
	}

	protected abstract IMember getSelectedMember() throws Exception;

	protected abstract void showMember(final ICompilationUnit compilationUnit, final IMember member) throws Exception;
}
