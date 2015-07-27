/**
 *
 */
package org.fastcode.popup.actions;

import static org.eclipse.jdt.core.Flags.AccStatic;
import static org.eclipse.jdt.core.search.SearchEngine.createWorkspaceScope;
import static org.eclipse.jdt.ui.IJavaElementSearchConstants.CONSIDER_CLASSES_AND_INTERFACES;
import static org.eclipse.jface.dialogs.MessageDialog.openError;
import static org.fastcode.common.FastCodeConstants.ASTERISK;
import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.DOT_CHAR;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.preferences.PreferenceConstants.P_STATIC_MEMBERS_AND_TYPES;
import static org.fastcode.util.ImportUtil.retriveStaticMembers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.AbstractActionSupport;
import org.fastcode.common.MemberSelectionDialog;
import org.fastcode.common.StringSelectionDialog;

/**
 * @author Gautam
 *
 */
public class CreateImportActionSupport extends AbstractActionSupport {

	private Object[]	userResult;

	/**
	 * @param compUnit
	 * @param javaElement
	 * @param typesToImport
	 *
	 * @see IActionDelegate#run(IAction)
	 */
	@Override
	protected void runAction(final ICompilationUnit compUnit, final IJavaElement javaElement, final IType[] typesToImport) throws Exception {

		if (typesToImport == null || typesToImport.length == 0) {
			return;
		}
		for (final IType typeToImport : typesToImport) {
			createImport(compUnit, typeToImport);
		}
	}

	/**
	 * @param compUnit
	 * @param globalSettings
	 * @param typeToImport
	 * @throws Exception
	 */
	protected void createImport(final ICompilationUnit compUnit, final IType typeToImport) throws Exception {

		final boolean creatStaticImport = true;
		IMember[] finalMembers;
		// List fastCodeMember = new ArrayList<IMember>();
		List<IMember> fcMembers = new ArrayList<IMember>();
		final IMember[] members = retriveStaticMembers(typeToImport);
		final IImportDeclaration[] fastCodeMembers = compUnit.getImports();
		final String imptAllMembers = typeToImport.getFullyQualifiedName() + DOT + ASTERISK;
		final IImportDeclaration allImptHave = compUnit.getImport(imptAllMembers);

		if (members != null && members.length > 0) {
			if (compUnit.findPrimaryType().isInterface()) {
				openError(this.page.getActiveEditor().getSite().getShell(), "Fast Code Plug-in",
						"This class contains one or more static members, need not be impoted into an interface");
				return;
			}
		}

		if (allImptHave != null && allImptHave.exists()) {
			throw new Exception("Import already exists. Not importing anything.");
		}
		fcMembers = checkIfMembersExist(fastCodeMembers, members);
		/*for (final IMember membr : members) {
			boolean found = false;
			for (final IImportDeclaration fcMember : fastCodeMembers) {

				final String classMembers = fcMember.getElementName().substring(fcMember.getElementName().lastIndexOf(DOT) + 1,
						fcMember.getElementName().length());
				if (membr.getElementName().equals(classMembers)) {
					found = true;
					break;
				}
			}
			if (!found) {
				fcMembers.add(membr);
			}
		}*/

		finalMembers = fcMembers.toArray(new IMember[0]);
		// System.out.println(finalMembers.length);

		if (creatStaticImport) {
			String imprtName = null;
			if (finalMembers.length == 1) {
				imprtName = typeToImport.getFullyQualifiedName() + DOT + finalMembers[0].getElementName();
				final IImportDeclaration importToCreate = compUnit.getImport(imprtName);
				if (importToCreate != null && importToCreate.exists()) {
					throw new Exception("Import already exists. Not importing anything.");
					// return;
				}
				compUnit.createImport(imprtName, null, AccStatic, null);

			} else if (finalMembers.length > 1) {
				final Shell shell = this.editorPart == null ? new Shell() : this.editorPart.getSite().getShell();

				final MemberSelectionDialog memberSelectionDialog = new MemberSelectionDialog(shell, "Import Members",
						"Choose  Members To Import", finalMembers, true);
				if (memberSelectionDialog.open() == Window.CANCEL) {
					return;
				} else {
					this.userResult = memberSelectionDialog.getResult();

				}
				for (final Object member : this.userResult) {
					final IMember mem = (IMember) member;
					imprtName = typeToImport.getFullyQualifiedName() + DOT + mem.getElementName();
					final IImportDeclaration importToCreate = compUnit.getImport(imprtName);
					if (importToCreate != null && importToCreate.exists()) {
						//warn --
						throw new Exception("Import already exists. Not importing anything.");
						// return;
					}
					compUnit.createImport(imprtName, null, AccStatic, null);
				}
			} else if (finalMembers.length != 0) {
				imprtName = typeToImport.getFullyQualifiedName() + DOT + ASTERISK;
				final IImportDeclaration importToCreate = compUnit.getImport(imprtName);
				if (importToCreate != null && importToCreate.exists()) {
					throw new Exception("Import already exists. Not importing anything.");
					// return;
				}
				compUnit.createImport(imprtName, null, AccStatic, null);
			}
		} else {
			if (compUnit.findPrimaryType().getPackageFragment().equals(typeToImport.getPackageFragment())) {
				MessageDialog.openError(this.page.getActiveEditor().getSite().getShell(), "Fast Code Plug-in",
						"This class is in the same package as the target, need not be impoted.");
				return;
			}
			compUnit.createImport(typeToImport.getFullyQualifiedName(), null, null);
		}
		MessageDialog.openInformation(new Shell(), "Import Successful", "The static import was done successfully.");

	}

	/**
	 *
	 */
	@Override
	protected boolean canActOnClassesOnly() {
		return false;
	}


	/* (non-Javadoc)
	 * @see org.fastcode.AbstractActionSupport#getTypesFromUser(java.lang.String, java.lang.String)
	 */
	@Override
	protected IType[] getTypesFromUser(final String title, final String description) throws Exception {
		final SelectionDialog selectionDialog = JavaUI.createTypeDialog(this.editorPart.getSite().getShell(), null, createWorkspaceScope(),
				CONSIDER_CLASSES_AND_INTERFACES, true, getInitialText());
		selectionDialog.setTitle("Static Import");
		selectionDialog.setMessage("Choose classes/Interfaces to Import");

		selectionDialog.open();
		final Object[] types = selectionDialog.getResult();
		IType[] typesToReturn = null;
		if (types != null && types.length > 0) {
			typesToReturn = new IType[types.length];
			int count = 0;
			for (final Object type : types) {
				typesToReturn[count++] = (IType) type;
			}
		}

		final IPreferenceStore preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		if (!preferenceStore.getString(P_STATIC_MEMBERS_AND_TYPES).equals(EMPTY_STR)) {
			final StringBuffer elementsToSetInPrefPageBuffer = new StringBuffer();
			final String[] elementsFromPrefPage = preferenceStore.getString(P_STATIC_MEMBERS_AND_TYPES).split(NEWLINE);
			if (typesToReturn != null && typesToReturn.length > 0) {
				for (final IType iType : typesToReturn) {
					boolean found = false;
					final IMember[] members = retriveStaticMembers(iType);
					if (members != null && members.length > 0) {
						for (final String element : elementsFromPrefPage) {
							elementsToSetInPrefPageBuffer.append(element);
							elementsToSetInPrefPageBuffer.append(NEWLINE);
							if (iType.getFullyQualifiedName().equals(element)) {
								found = true;
								break;
							}
						}
					}

					if (!found) {
						elementsToSetInPrefPageBuffer.append(iType.getFullyQualifiedName());
						elementsToSetInPrefPageBuffer.append(NEWLINE);
					}
				}
			}
			final String elementsToSetInPrefPage = elementsToSetInPrefPageBuffer.toString();
			preferenceStore.setValue(P_STATIC_MEMBERS_AND_TYPES, elementsToSetInPrefPage);

		} else if (preferenceStore.getString(P_STATIC_MEMBERS_AND_TYPES).equals(EMPTY_STR)) {
			final StringBuffer elementsToSetInPrefPageBuffer = new StringBuffer();
			if (typesToReturn != null && typesToReturn.length > 0) {
				for (final IType iType : typesToReturn) {
					final IMember[] members = retriveStaticMembers(iType);
					if (members != null && members.length > 0) {
						elementsToSetInPrefPageBuffer.append(iType.getFullyQualifiedName());
						elementsToSetInPrefPageBuffer.append(NEWLINE);
					}
					
				}
			}
			final String elementsToSetInPrefPage = elementsToSetInPrefPageBuffer.toString();
			preferenceStore.setValue(P_STATIC_MEMBERS_AND_TYPES, elementsToSetInPrefPage);
		}
		return typesToReturn;
	}

	@Override
	protected String getInitialText() {
		return this.selection != null && this.selection instanceof ITextSelection ? ((ITextSelection) this.selection).getText() : EMPTY_STR;
	}

	@Override
	protected boolean doesModify() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.fastcode.AbstractActionSupport#runAction(org.eclipse.jdt.core.ICompilationUnit, org.eclipse.jdt.core.IJavaElement)
	 */
	@Override
	protected void runAction(final ICompilationUnit compUnit, final IJavaElement javaElement) throws Exception {
		// Do Nothing
		final IPreferenceStore preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		final IJavaProject workingJavaProject = compUnit.getJavaProject();
		SelectionDialog selectionDialog = null;
		if (!preferenceStore.getString(P_STATIC_MEMBERS_AND_TYPES).equals(EMPTY_STR)) {
			final String[] elementsFromPrefPage = preferenceStore.getString(P_STATIC_MEMBERS_AND_TYPES).split(NEWLINE);
			List<String> elementsExist = new ArrayList<String>();
			for (String element : elementsFromPrefPage) {
				IType iType = null;
				iType = workingJavaProject.findType(element);
				if (iType != null) {
					elementsExist.add(element);
					continue;
				} else {
					iType = workingJavaProject.findType(element.substring(0, element.lastIndexOf(DOT_CHAR)));
					if (iType != null) {
						elementsExist.add(element);
					}
				}
			}
			selectionDialog = new StringSelectionDialog(new Shell(), "Static Import from PreferenceStore",
					"Choose Classes/Members to Import", elementsExist.toArray(new String[0]), true);

			if (selectionDialog.open() == Window.CANCEL) {
				final IType[] typesToImport = getTypesFromUser(EMPTY_STR, EMPTY_STR);
				if (typesToImport == null || typesToImport.length == 0) {
					return;
				}
				for (final IType typeToImport : typesToImport) {
					createImport(compUnit, typeToImport);
				}

			} else {
				final Object[] selectedStaticClasAndMembr = selectionDialog.getResult();
				final List<IType> selectedStaticClass = new ArrayList<IType>();
				final Map<IType, List<IMember>> selectedStaticMemberAndType = new HashMap<IType, List<IMember>>();
				final List<IType> typesOfMembers = new ArrayList<IType>();
				final List<IMember> listOfMembers = new ArrayList<IMember>();
				List<IMember> finalMembers = new ArrayList<IMember>();
				
				final IImportDeclaration[] fastCodeMembers = compUnit.getImports();
				for (final Object selectedMember : selectedStaticClasAndMembr) {
					final String selectedMembr = (String) selectedMember;
					IType iType = null;
					iType = workingJavaProject.findType(selectedMembr);
					if (iType != null) {
						selectedStaticClass.add(iType);
					} else {
						iType = workingJavaProject.findType(selectedMembr.substring(0, selectedMembr.lastIndexOf(DOT_CHAR)));
						if (iType != null) {
							if (!typesOfMembers.contains(iType)) {
								typesOfMembers.add(iType);
							}
							for (final IMember member : retriveStaticMembers(iType)) {
								if (member != null) {
									if (member.getElementName().equals(
											selectedMembr.substring(selectedMembr.lastIndexOf(DOT_CHAR) + 1, selectedMembr.length()))) {
										listOfMembers.add(member);

									}
								}
							}
							finalMembers = checkIfMembersExist(fastCodeMembers, listOfMembers.toArray(new IMember[0]));
						}
						selectedStaticMemberAndType.put(iType, finalMembers);
					}

				}

				if (selectedStaticClass.toArray(new IType[0]) != null && selectedStaticClass.toArray(new IType[0]).length > 0) {
					final IType[] typesToImport = selectedStaticClass.toArray(new IType[0]);

					for (final IType typeToImport : typesToImport) {
						createImport(compUnit, typeToImport);
					}
				}
				if (selectedStaticMemberAndType != null && typesOfMembers.toArray(new IType[0]) != null
						&& typesOfMembers.toArray(new IType[0]).length > 0) {

					final IType[] typesOfMembrs = typesOfMembers.toArray(new IType[0]);

					createMemberImport(compUnit, typesOfMembrs, selectedStaticMemberAndType);

				}
			}
		} else {
			final IType[] typesToImport = getTypesFromUser(EMPTY_STR, EMPTY_STR);
			if (typesToImport == null || typesToImport.length == 0) {
				return;
			}
			for (final IType typeToImport : typesToImport) {
				createImport(compUnit, typeToImport);
			}
		}

	}

	/**
	 * @param fastCodeMembers
	 * @param members
	 * @return
	 */
	private List<IMember> checkIfMembersExist(final IImportDeclaration[] fastCodeMembers, final IMember[] members) {

		final List<IMember> fcMembers = new ArrayList<IMember>();
		if (fastCodeMembers != null && members != null) {
			for (final IMember membr : members) {
				boolean found = false;
				for (final IImportDeclaration fcMember : fastCodeMembers) {

					final String classMembers = fcMember.getElementName().substring(fcMember.getElementName().lastIndexOf(DOT) + 1,
							fcMember.getElementName().length());
					if (membr.getElementName().equals(classMembers)) {
						found = true;
						break;
					}
				}
				if (!found) {
					fcMembers.add(membr);
				}
			}
		}
		return fcMembers;
	}

	/**
	 * @param compUnit
	 * @param typesOfMembrs
	 * @param selectedStaticMemberAndType
	 * @throws Exception
	 */
	private void createMemberImport(final ICompilationUnit compUnit, final IType[] typesOfMembrs,
			final Map<IType, List<IMember>> selectedStaticMemberAndType) throws Exception {

		if (typesOfMembrs == null || selectedStaticMemberAndType == null) {
			return;
		}
		String imprtName = null;
		for (final IType type : typesOfMembrs) {
			if (selectedStaticMemberAndType.containsKey(type)) {
				if (selectedStaticMemberAndType.get(type) == null
						&& selectedStaticMemberAndType.get(type).toArray(new IMember[0]).length == 0) {
					MessageDialog.openWarning(new Shell(), "Warning", "Import already exists");
				}
				for (final IMember member : selectedStaticMemberAndType.get(type)) {
					if (member != null) {
						imprtName = type.getFullyQualifiedName() + DOT + member.getElementName();
						final IImportDeclaration importToCreate = compUnit.getImport(imprtName);
						if (importToCreate != null && importToCreate.exists()) {
							throw new Exception("Import already exists. Not importing anything.");
						}
						compUnit.createImport(imprtName, null, AccStatic, null);
					}
				}
			}
		}
	}

	@Override
	protected boolean doesRequireMoreInfoFromUser() {
		return false;//return true;
	}

	public void dispose() {

	}

	@Override
	public void init(final IWorkbenchWindow window) {
		this.window = window;
	}

}
