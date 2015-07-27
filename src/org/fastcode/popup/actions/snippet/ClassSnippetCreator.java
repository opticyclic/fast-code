package org.fastcode.popup.actions.snippet;

import static org.eclipse.jdt.ui.JavaUI.openInEditor;
import static org.eclipse.jdt.ui.JavaUI.revealInEditor;
import static org.fastcode.common.FastCodeConstants.COMMA;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.preferences.PreferenceConstants.P_DB_PACKAGE_FOR_POJO_CLASS;
import static org.fastcode.preferences.PreferenceConstants.P_POJO_BASE_CLASS;
import static org.fastcode.preferences.PreferenceConstants.P_POJO_IMPLEMENT_INTERFACES;
import static org.fastcode.preferences.PreferenceConstants.P_WORKING_JAVA_PROJECT;
import static org.fastcode.util.SourceUtil.createClass;
import static org.fastcode.util.SourceUtil.createCodeFormatter;
import static org.fastcode.util.SourceUtil.formatCode;
import static org.fastcode.util.SourceUtil.getDefaultPathFromProject;
import static org.fastcode.util.SourceUtil.getJavaProject;
import static org.fastcode.util.SourceUtil.getPackageFromUser;
import static org.fastcode.util.SourceUtil.getPackagesInProject;
import static org.fastcode.util.SourceUtil.getSuperInterfacesType;
import static org.fastcode.util.SourceUtil.getWorkingJavaProjectFromUser;
import static org.fastcode.util.SourceUtil.implementInterfaceMethods;
import static org.fastcode.util.StringUtil.evaluateByVelocity;
import static org.fastcode.util.StringUtil.getGlobalSettings;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.StringUtil.replaceSpecialChars;

import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IEditorPart;
import org.fastcode.Activator;
import org.fastcode.common.FastCodeConstants.CLASS_TYPE;
import org.fastcode.common.FastCodeType;
import org.fastcode.util.CreateSimilarDescriptorClass;
import org.fastcode.util.FastCodeContext;

/**
 * package org.fastcode.popup.actions.snippet;
 *
 * import static org.fastcode.util.StringUtil.evaluateByVelocity;
 *
 * /**
 *
 * @author Gautam
 *
 */
public class ClassSnippetCreator implements SnippetCreator {
	ICompilationUnit			compilationUnit;
	IJavaProject[]				javaProject;
	IPackageFragment[]			packagefragment;
	private IPackageFragment	pojoClassPackage;
	IJavaProject				workingJavaProject;

	/**
	 * @param editorPart
	 * @param placeHolders
	 * @param memberSelection
	 * @param spacesBeforeCursor
	 */
	@Override
	public Object createSnippet(final IEditorPart editorPart, final String template, final Map<String, Object> placeHolders,
			final Map<String, Object> memberSelection, final String spacesBeforeCursor) throws Exception {
		final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

		getGlobalSettings(placeHolders);
		FastCodeType baseClass = null;
		if (preferenceStore.contains(P_WORKING_JAVA_PROJECT)) {
			this.workingJavaProject = getJavaProject(preferenceStore.getString(P_WORKING_JAVA_PROJECT));
		} else {
			this.workingJavaProject = getWorkingJavaProjectFromUser();
			preferenceStore.setValue(P_WORKING_JAVA_PROJECT, this.workingJavaProject.getElementName());
		}
		if (preferenceStore.contains(P_DB_PACKAGE_FOR_POJO_CLASS)) {
			final String srcPath = getDefaultPathFromProject(this.workingJavaProject, "source", EMPTY_STR);
			for (final IPackageFragment pkgFragment : getPackagesInProject(this.workingJavaProject, srcPath, "source")) {
				if (pkgFragment.getElementName().equals(preferenceStore.getString(P_DB_PACKAGE_FOR_POJO_CLASS))) {
					this.pojoClassPackage = pkgFragment;
					break;
				}
			}
			//this.pojoClassPackage = getPackageFragmentFromWorkspace(preferenceStore.getString(P_DB_PACKAGE_FOR_POJO_CLASS));
		} else {
			this.pojoClassPackage = getPackageFromUser(this.workingJavaProject);
			preferenceStore.setValue(P_DB_PACKAGE_FOR_POJO_CLASS, this.pojoClassPackage.getElementName());
		}
		if (preferenceStore.getString(P_POJO_BASE_CLASS) != null && preferenceStore.getString(P_POJO_BASE_CLASS).length() != 0) {
			baseClass = new FastCodeType(this.workingJavaProject.findType(preferenceStore.getString(P_POJO_BASE_CLASS)));
		}
		final String interfacesToImpl = preferenceStore.getString(P_POJO_IMPLEMENT_INTERFACES);
		String[] interfacesArr = null;
		final StringBuilder interfaceNames = new StringBuilder();
		if (!isEmpty(interfacesToImpl)) {
			interfacesArr = interfacesToImpl.split(NEWLINE);

			for (final String interfaceName : interfacesArr) {
				interfaceNames
						.append(isEmpty(interfaceNames.toString()) ? new FastCodeType(this.workingJavaProject.findType(interfaceName))
								.getName() : COMMA + new FastCodeType(this.workingJavaProject.findType(interfaceName)).getName());
			}
		}

		placeHolders.put("baseClass", baseClass == null ? EMPTY_STR : "extends " + baseClass.getName());
		placeHolders.put("implementInterface", isEmpty(interfaceNames.toString()) ? EMPTY_STR : "implements "
				+ interfaceNames.toString().trim());

		final String snppt = evaluateByVelocity(template, placeHolders);
		if (isEmpty(snppt)) {
			throw new Exception("Blank snippet, template may be invalid.");
		}

		String snippet = replaceSpecialChars(snppt);

		//format(snippet.trim(), spacesBeforeCursor);
		final Object codeFormatter = createCodeFormatter(this.workingJavaProject.getProject());
		snippet = formatCode(snippet.trim(), codeFormatter);
		final ICompilationUnit compilationUnitNew = createClass(snippet.trim(), this.pojoClassPackage, this.workingJavaProject, null);

		if (!isEmpty(interfacesToImpl)) {
			for (final String importType : interfacesArr) {

				final IType interfaceType = this.workingJavaProject.findType(importType);
				if (interfaceType == null || !interfaceType.exists()) {
					continue;
				}
				addImport(compilationUnitNew, interfaceType);
				final FastCodeContext fastCodeContext = new FastCodeContext(interfaceType);
				final CreateSimilarDescriptorClass createSimilarDescriptorClass = new CreateSimilarDescriptorClass.Builder().withClassType(
						CLASS_TYPE.CLASS).build();
				implementInterfaceMethods(interfaceType, fastCodeContext, compilationUnitNew.findPrimaryType(), null,
						createSimilarDescriptorClass);

				final IType[] superInterfaceType = getSuperInterfacesType(interfaceType);
				if (superInterfaceType != null) {
					for (final IType type : superInterfaceType) {
						if (type == null || !type.exists()) {
							continue;
						}
						final FastCodeContext context = new FastCodeContext(type);
						implementInterfaceMethods(type, context, compilationUnitNew.findPrimaryType(), null, createSimilarDescriptorClass);
					}
				}

			}
		}
		if (baseClass != null && !isEmpty(baseClass.getName())) {
			addImport(compilationUnitNew, compilationUnitNew.getJavaProject().findType(baseClass.getFullyQualifiedName()));
		}

		final IEditorPart javaEditor = openInEditor(compilationUnitNew);

		revealInEditor(javaEditor, (IJavaElement) compilationUnitNew.findPrimaryType());

		return compilationUnitNew;
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

}
