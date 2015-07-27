/**
 *
 */
package org.fastcode.popup.actions.easycreate;

import static org.eclipse.jdt.core.Flags.isPublic;
import static org.eclipse.jdt.core.IJavaElement.METHOD;
import static org.eclipse.jface.dialogs.MessageDialog.openError;
import static org.eclipse.jface.dialogs.MessageDialog.openQuestion;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.setting.GlobalSettings.getInstance;
import static org.fastcode.util.JUnitCreator.generateTest;
import static org.fastcode.util.JUnitUtil.findTestUnit;
import static org.fastcode.util.SourceUtil.copyImports;
import static org.fastcode.util.SourceUtil.getRepositoryServiceClass;
import static org.fastcode.util.StringUtil.replacePlaceHolder;

import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.fastcode.AbstractActionSupport;
import org.fastcode.setting.GlobalSettings;
import org.fastcode.util.FastCodeContext;
import org.fastcode.util.MethodBuilder;
import org.fastcode.util.RepositoryService;
import org.fastcode.util.SimilarMethodBuilder;

/**
 * @author Gautam
 *
 */
public class SynchronizedMemberView extends AbstractActionSupport implements IEditorActionDelegate, IActionDelegate,
		IWorkbenchWindowActionDelegate {

	protected ITextSelection	selection;

	/**
	 *
	 *
	 * @param compUnit
	 * @param selectedElement
	 *
	 *
	 */
	@Override
	protected void runAction(final ICompilationUnit compUnit, final IJavaElement selectedElement) throws Exception {

		final GlobalSettings globalSettings = getInstance();
		final IType primaryType = compUnit.findPrimaryType();

		final Map<String, String> relatedClassMap = globalSettings.getRelatedClassMap();

		if (!relatedClassMap.isEmpty()) {
			String classExtsn = EMPTY_STR;
			final String typeName = primaryType.getElementName();
			if (!typeName.toUpperCase().endsWith(classExtsn.toUpperCase())) {
				openError(null, "Error", "The class you selected does have this extension.");
				return;
			}
			final int extLen = classExtsn.length();
			classExtsn = typeName.substring(typeName.length() - extLen);

			final IPackageFragment packageFragment = (IPackageFragment) compUnit.getPackageDeclarations()[0]
					.getAdapter(IPackageFragment.class);
			for (final ICompilationUnit unit : packageFragment.getCompilationUnits()) {
				final IType pType = unit.findPrimaryType();
				final ITypeHierarchy typeHierarchy = pType.newSupertypeHierarchy(null);
				for (final IType intfType : typeHierarchy.getAllInterfaces()) {
					final ICompilationUnit intfUnit = intfType.getCompilationUnit();
					if (intfUnit != null && intfUnit.exists()) {
						final String intfTypeFullName = intfType.getFullyQualifiedName();
					}
				}
			}
		}
		String relatedTypeName = null;
		final boolean found = false;
		for (final Entry<String, String> entry : relatedClassMap.entrySet()) {
			String value = entry.getValue();

			final Pattern pattern = Pattern.compile(entry.getKey());
			final Matcher matcher = pattern.matcher(primaryType.getFullyQualifiedName());
			if (matcher.matches()) {
				continue;
			}

			for (int i = 0; i < matcher.groupCount(); i++) {
				final String token = matcher.group(i);
				value = replacePlaceHolder(value, i + EMPTY_STR, token);
			}
			relatedTypeName = value;
			break;
		}
		/*
				IType relatedType = compUnit.getJavaProject().findType(relatedTypeName);

				if (primaryType.isClass()) {
					if (relatedType == null || !relatedType.exists()) {
						// Create it

					}
					int offset = relatedType.getNameRange().getOffset();
					int length = relatedType.getNameRange().getLength();
					String[] superInterfaceNames = relatedType.getSuperInterfaceNames();
					if (superInterfaceNames == null || superInterfaceNames.length == 0) {
						ITextEditor editor = (ITextEditor) editorPart;
						IDocumentProvider documentProvider = editor.getDocumentProvider();
						IDocument document = documentProvider.getDocument(editor.getEditorInput());
						document.replace(offset, 0, "implements " + relatedType.getElementName());
					}
				}
		*/
		if (selectedElement == null || selectedElement.getElementType() != METHOD || ((IMethod) selectedElement).isConstructor()
				|| !isPublic(selectedElement.getElementType())) {
			throw new Exception("Need to select a public method for this action.");
		}

		final IMethod method = (IMethod) selectedElement;

		final IType type = primaryType;
		final ITypeHierarchy hierarchy = type.newSupertypeHierarchy(null);
		final String[] interfaces = type.getSuperInterfaceNames();

		if (interfaces == null || interfaces.length == 0) {
			throw new Exception("No super interface found.");
		}

		IType superInterface = null;
		for (final IType interfaceType : hierarchy.getAllSuperInterfaces(type)) {
			if (interfaceType.getElementName().equals(interfaces[0])) {
				superInterface = interfaceType;
				break;
			}
		}

		if (superInterface == null || !superInterface.exists()) {
			throw new Exception("No super interface found.");
		}

		final ICompilationUnit superInterfaceCompUnit = superInterface.getCompilationUnit();

		if (superInterfaceCompUnit.isReadOnly() || superInterfaceCompUnit.getResource().isReadOnly()) {
			throw new Exception(superInterface.getElementName() + " is read only.");
		}

		boolean becomeWorkingCopy = false;
		if (!superInterfaceCompUnit.isWorkingCopy()) {
			becomeWorkingCopy = true;
			superInterfaceCompUnit.becomeWorkingCopy(null);
		}
		try {
			copyImports(compUnit, superInterfaceCompUnit, method);
			final FastCodeContext fastCodeContext = new FastCodeContext(type);
			final MethodBuilder methodBuilder = new SimilarMethodBuilder(fastCodeContext);
			methodBuilder.buildMethod(method, superInterfaceCompUnit.findPrimaryType());
			superInterfaceCompUnit.commitWorkingCopy(false, null);
			if (!openQuestion(null, "Junit", "Would you like to create the junit test as well?")) {
				return;
			}
			boolean createByClass = true;
			ICompilationUnit testUnit = findTestUnit(type, null);
			if (testUnit == null) {
				testUnit = findTestUnit(superInterface, null);
				if (testUnit != null) {
					createByClass = false;
				}
			}
			if (testUnit != null) {
				IMethod intMethod = null;
				if (!createByClass) {
					intMethod = superInterface.getMethod(method.getElementName(), method.getParameterTypes());
				}
				generateTest(createByClass ? type : superInterface, this.commitMessage, createByClass ? method : intMethod);

				if (this.autoCheckinEnabled) {
					if (!this.commitMessage.isEmpty()) {
						final RepositoryService checkin = getRepositoryServiceClass();
						checkin.commitToRepository(this.commitMessage, true);
					}
				}
			}
		} finally {
			if (becomeWorkingCopy) {
				superInterfaceCompUnit.discardWorkingCopy();
			}
		}

	}

	@Override
	public void dispose() {

	}

	@Override
	protected boolean doesModify() {
		return false;
	}

	@Override
	protected boolean doesRequireMoreInfoFromUser() {
		return false;
	}

	@Override
	protected IType[] getTypesFromUser(final String title, final String description) throws Exception {
		return null;
	}

	@Override
	protected void runAction(final ICompilationUnit compUnit, final IJavaElement javaElement, final IType[] types) throws Exception {
		// Do nothing.
	}

}
