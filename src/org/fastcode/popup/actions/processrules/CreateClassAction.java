package org.fastcode.popup.actions.processrules;

import static org.eclipse.jdt.ui.JavaUI.openInEditor;
import static org.eclipse.jdt.ui.JavaUI.revealInEditor;
import static org.eclipse.jface.dialogs.MessageDialog.openWarning;
import static org.eclipse.jface.window.Window.CANCEL;
import static org.fastcode.common.FastCodeConstants.ACTON_SELECTED_STR;
import static org.fastcode.common.FastCodeConstants.COMMA;
import static org.fastcode.common.FastCodeConstants.CREATE_SIMILAR_CLASSES_ACTION;
import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.JAVA_EXTENSION;
import static org.fastcode.common.FastCodeConstants.LEFT_CURL;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_ABSTRACT;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_BASE_CLASS;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_IMPL_SUB_PACKAGE;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_INPUT;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_PACKAGE;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_PROJECT;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_RESULT;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_SOURCE;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_SRC_PATH;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_SUPER_INTERFACE;
import static org.fastcode.common.FastCodeConstants.SEMICOLON;
import static org.fastcode.common.FastCodeConstants.SPACE;
import static org.fastcode.common.FastCodeConstants.TRUE_STR;
import static org.fastcode.setting.GlobalSettings.getInstance;
import static org.fastcode.util.SourceUtil.getConstructorsFromParentClass;
import static org.fastcode.util.SourceUtil.getJavaProject;
import static org.fastcode.util.SourceUtil.getPackageFragmentFromWorkspace;
import static org.fastcode.util.SourceUtil.getPackageRootFromProject;
import static org.fastcode.util.SourceUtil.getPathFromGlobalSettings;
import static org.fastcode.util.SourceUtil.getPathFromUser;
import static org.fastcode.util.SourceUtil.overrideConstructor;
import static org.fastcode.util.StringUtil.changeFirstLetterToUpperCase;
import static org.fastcode.util.StringUtil.evaluateByVelocity;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.StringUtil.replaceSpecialChars;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.fastcode.common.FastCodeType;
import org.fastcode.common.StringSelectionDialog;
import org.fastcode.setting.GlobalSettings;

public class CreateClassAction extends FastCodeRuleProcessorAction {

	/**
	 * @param name
	 * @param placeHolders
	 * @param contextMap
	 * @throws Exception
	 */
	public void createClass(final String name, final Map<String, Object> placeHolders, final Map<String, Object> contextMap)
			throws Exception {
		ICompilationUnit newClass = null;
		IPackageFragment classPackage = null;
		IType parentIType = null;
		FastCodeType parentFCType = null;

		String path = (String) placeHolders.get(PLACEHOLDER_SRC_PATH);

		if (placeHolders.get(PLACEHOLDER_BASE_CLASS) != null) {
			parentFCType = new FastCodeType((String) placeHolders.get(PLACEHOLDER_BASE_CLASS));
			placeHolders.put(PLACEHOLDER_BASE_CLASS, parentFCType);
		}

		final String classSnippet = evaluateByVelocity((String) placeHolders.get(PLACEHOLDER_SOURCE), placeHolders); // getValueForPlaceHolder((String)
																														// placeHolders.get(PLACEHOLDER_SOURCE),
																														// placeHolders);
																														// //
		if (isEmpty(classSnippet)) {
			clearPlaceHolderMap(placeHolders);
			throw new Exception("Blank snippet, source may be invalid.");
		}
		final String classSrc = replaceSpecialChars(classSnippet);

		/*
		 * if(target != null){ classPackage =
		 * getPackageFragmentFromWorkspace(target); } else
		 */
		IJavaProject workingJavaProject = null;
		if (placeHolders.get(PLACEHOLDER_PACKAGE) != null) {
			if (placeHolders.containsKey(PLACEHOLDER_IMPL_SUB_PACKAGE)) {
				final String implSubPckage = evaluateByVelocity((String) placeHolders.get(PLACEHOLDER_IMPL_SUB_PACKAGE), placeHolders); // getValueForPlaceHolder((String)
																																		// placeHolders.get(PLACEHOLDER_IMPL_SUB_PACKAGE),
																																		// placeHolders);
																																		// //
				classPackage = getPackageFragmentFromWorkspace(implSubPckage);
			} else {
				final String packageName = evaluateByVelocity((String) placeHolders.get(PLACEHOLDER_PACKAGE), placeHolders); // getValueForPlaceHolder((String)
																																// placeHolders.get(PLACEHOLDER_PACKAGE),
																																// placeHolders);
				classPackage = getPackageFragmentFromWorkspace(packageName);
			}
			workingJavaProject = getJavaProject((String) placeHolders.get(PLACEHOLDER_PROJECT));
		} else if (placeHolders.get(PLACEHOLDER_PROJECT) != null) {
			workingJavaProject = getJavaProject((String) placeHolders.get(PLACEHOLDER_PROJECT));
			classPackage = getPackageFromUser(workingJavaProject);
		}

		if (classPackage == null) {
			classPackage = getPackageFromUser(getWorkingJavaProject());
		}

		// System.out.println(classPackage.getElementName());

		if (workingJavaProject == null || !workingJavaProject.exists()) {
			workingJavaProject = getWorkingJavaProject();
		}

		if (workingJavaProject == null || !workingJavaProject.exists()) {
			MessageDialog.openError(new Shell(), "Error", "Cannot proceed without working java project...exiting..");
			clearPlaceHolderMap(placeHolders);
			return;
		}

		if (classPackage == null) {
			IPackageFragmentRoot packageFragmentRoot = null;
			final String packageName = getValueForPlaceHolder((String) placeHolders.get(PLACEHOLDER_PACKAGE), placeHolders);

			if (path == null) {
				final GlobalSettings globalSettings = getInstance();
				path = globalSettings.isUseDefaultForPath() ? getPathFromGlobalSettings(workingJavaProject.getElementName())
						: getPathFromUser("Choose Source Path to create Package- " + packageName);

				if (path == null) {
					MessageDialog.openError(new Shell(), "Error", "Cannot proceed without path...exiting..");
					clearPlaceHolderMap(placeHolders);
					return;
				}

			}
			packageFragmentRoot = getPackageRootFromProject(workingJavaProject, path);
			// packageFragmentRoot =
			// getPackageRootFromProject(workingJavaProject, "/src");
			// //workingJavaProject.findPackageFragmentRoot(new
			// Path(packageName));

			classPackage = packageFragmentRoot.createPackageFragment(packageName, true, null);
		}

		final String packageDeclaration = "package " + classPackage.getElementName() + SEMICOLON + NEWLINE + NEWLINE;
		final String className = changeFirstLetterToUpperCase(getValueForPlaceHolder(name, placeHolders)); // placeHolders.get(key)evaluateByVelocity(name,
																				// placeHolders);

		final IType type = classPackage.getJavaProject().findType(classPackage.getElementName() + DOT + className);

		if (type == null || !type.exists()) {
			if (placeHolders.get(PLACEHOLDER_ABSTRACT) != null && TRUE_STR.equals(placeHolders.get(PLACEHOLDER_ABSTRACT))) {
				newClass = classPackage.createCompilationUnit("Abstract" + className + DOT + JAVA_EXTENSION, packageDeclaration + classSrc,
						false, null);
			} else {
				newClass = classPackage.createCompilationUnit(className + DOT + JAVA_EXTENSION, packageDeclaration + classSrc, false, null);
			}
		} else {
			newClass = type.getCompilationUnit();
			openWarning(new Shell(), "Warning", "Class, " + className + ", already exist. Will Skip and proceed.");
			clearPlaceHolderMap(placeHolders);
			return;
		}

		if (placeHolders.get(PLACEHOLDER_BASE_CLASS) != null) {
			parentIType = workingJavaProject.findType(parentFCType.getFullyQualifiedName());
			if (parentIType == null) {
				parentIType = getTypeFromUser("Base class specified in the XML is not found.Please choose another base class.",
						"Select Base Class ", "Select the Base Class to extend");
				if (parentIType == null) {
					// show message??
					clearPlaceHolderMap(placeHolders);
					return;
				}
				placeHolders.put(PLACEHOLDER_BASE_CLASS, parentIType.getFullyQualifiedName());
			}
			super.addImport(newClass, parentIType);

			final Map<String, IMethod> nameIMethodMap = new HashMap<String, IMethod>();
			final List<String> consNameList = new ArrayList<String>();
			getConstructorsFromParentClass(new FastCodeType(parentIType), nameIMethodMap, consNameList);
			System.out.println(consNameList.size());
			if (consNameList.size() > 0) {
				final StringSelectionDialog selectionDialog = new StringSelectionDialog(new Shell(), "Constructors in Parent class",
						"Choose constructor to override.", consNameList.toArray(new String[0]), true);
				if (selectionDialog.open() != CANCEL) {

					final Object[] consToOverride = selectionDialog.getResult();

					for (final Object consName : consToOverride) {

						final IMethod consMethod = nameIMethodMap.get(consName);
						overrideConstructor(consMethod, newClass.findPrimaryType());
						/*final FastCodeContext fastCodeContext = new FastCodeContext(parentIType);
						final MethodBuilder methodBuilder = new SimilarMethodBuilder(fastCodeContext);
						methodBuilder.buildMethod(consMethod, newClass.findPrimaryType());*/
					}
				}
			}
			// typeToImport = parentIType;
			// placeHolders.put(input, parentIType);
		}

		final String superInterfaces = (String) placeHolders.get(PLACEHOLDER_SUPER_INTERFACE);

		StringBuilder interfaceToImpl = new StringBuilder();
		StringBuilder superInterfaceUpd = new StringBuilder();
		if (superInterfaces != null) {
			final String[] superInterfacesArr = superInterfaces.split(COMMA);
			for (String superInterface : superInterfacesArr) {
				IType interfaceType = workingJavaProject.findType(superInterface);
				if (interfaceType == null) {
					interfaceType = getTypeFromUser("Interface specified in the XML " + superInterface
							+ " is not found.Please choose another interface class.", "Select Interface",
							"Select the Interface to implement");
					if (interfaceType == null) {
						// show message??
						// clearPlaceHolderMap(placeHolders);
						// return;
						continue;
					}
					superInterface = interfaceType.getFullyQualifiedName();
				}

				final IMethod[] methods = interfaceType.getMethods();
				super.implementMethods(methods, newClass.findPrimaryType(), interfaceType);
				interfaceToImpl = EMPTY_STR.equals(interfaceToImpl.toString()) ? interfaceToImpl.append(superInterface
						.substring(superInterface.lastIndexOf(DOT) + 1)) : interfaceToImpl.append(COMMA
						+ superInterface.substring(superInterface.lastIndexOf(DOT) + 1));
				superInterfaceUpd = EMPTY_STR.equals(superInterfaceUpd.toString()) ? superInterfaceUpd.append(superInterface)
						: superInterfaceUpd.append(COMMA + superInterface);
				super.addImport(newClass, interfaceType);
				placeHolders.put(PLACEHOLDER_SUPER_INTERFACE, superInterfaceUpd.toString());
				/*commented for the sake of create_class_and_implement_interface...need superInterface full name for input in import*/
				/*need this in cse one or more of the specified interface is wrong, need to update the list with wht user chooses*/
			}
		}

		if (superInterfaces != null) {

		}

		if (superInterfaces != null) {
			// StringBuilder interfaceToImpl = new StringBuilder();
			// final String[] superInterfacesArr = superInterfaces.split(COMMA);
			/*
			 * for (final String superInterface : superInterfacesArr) {
			 * interfaceToImpl = EMPTY_STR.equals(interfaceToImpl.toString()) ?
			 * interfaceToImpl
			 * .append(superInterface.substring(superInterface.lastIndexOf(DOT)
			 * +1)) : interfaceToImpl.append(COMMA +
			 * superInterface.substring(superInterface.lastIndexOf(DOT) +1)); }
			 */
			// final IType interfaceType =
			// getTypeFromProject(workingJavaProject, (String)
			// placeHolders.get(PLACEHOLDER_SUPER_INTERFACE));
			String classSource = newClass.getSource().toString();
			final String classPart1 = classSource.substring(0, classSource.indexOf(LEFT_CURL));
			final String classPart2 = classSource.substring(classSource.indexOf(LEFT_CURL));
			if (classSource.contains("implements")) {
				classSource = classPart1 + COMMA + interfaceToImpl.toString() + classPart2;
			} else {
				classSource = classPart1 + SPACE + "implements" + SPACE + interfaceToImpl.toString() + classPart2;
			}

			newClass.delete(false, null);
			newClass = classPackage.createCompilationUnit(className + DOT + JAVA_EXTENSION, classSource, false, null);
			// typeToImport = parentIType;
			// placeHolders.put(input, parentIType);
		}

		/*
		 * if (placeHolders.get("defaultMethods") != null &&
		 * ((String)placeHolders.get("defaultMethods")).equals(TRUE_STR)) {
		 * //placeHolders.get(PLACEHOLDER_INPUT); final IType interfaceType =
		 * getTypeFromProject(workingJavaProject,
		 * ((FastCodeType)placeHolders.get
		 * (PLACEHOLDER_INPUT)).getFullyQualifiedName()); final IMethod[]
		 * methods = interfaceType.getMethods(); super.implementMethods(methods,
		 * newClass, interfaceType); }
		 */

		if (placeHolders.get(PLACEHOLDER_RESULT) != null) {
			placeHolders.put((String) placeHolders.get(PLACEHOLDER_RESULT), new FastCodeType(newClass.findPrimaryType()
					.getFullyQualifiedName()));
			contextMap.put((String) placeHolders.get(PLACEHOLDER_RESULT), new FastCodeType(newClass.findPrimaryType()
					.getFullyQualifiedName()));
		}

		/*
		 * if (placeHolders.get(PLACEHOLDER_IMPL_SUB_PACKAGE) != null) { final
		 * Object[] methodsToCreate = (Object[])
		 * placeHolders.get("defaultMethods");
		 *
		 * if(methodsToCreate != null) { for (final Object method :
		 * methodsToCreate) { final String implMethod =
		 * inbuiltMethodsMap.get(method.toString()); final String methodSrc =
		 * evaluateByVelocity(implMethod, placeHolders);
		 * newClass.findPrimaryType().createMethod(methodSrc, null, false,
		 * null); } } }
		 */
		this.fastCodeTypeSet.add(new FastCodeType(newClass.findPrimaryType().getFullyQualifiedName()));

		if (placeHolders.get(ACTON_SELECTED_STR) != CREATE_SIMILAR_CLASSES_ACTION) {
			final IEditorPart javaEditor = openInEditor(newClass);
			revealInEditor(javaEditor, (IJavaElement) newClass.findPrimaryType());
		}

		clearPlaceHolderMap(placeHolders);

	}

	/**
	 * @param placeHolders
	 */
	private void clearPlaceHolderMap(final Map<String, Object> placeHolders) {
		placeHolders.remove(PLACEHOLDER_SOURCE);
		placeHolders.remove(PLACEHOLDER_IMPL_SUB_PACKAGE);
		placeHolders.remove(PLACEHOLDER_RESULT);
		placeHolders.remove(PLACEHOLDER_INPUT);
		// placeHolders.remove(PLACEHOLDER_SUPER_INTERFACE);
		placeHolders.remove("defaultMethods");
		placeHolders.remove("superInterface"); // -- commented for final the
												// sake final of
												// create_class_and_implement_interface...final
												// need superInterface for final
												// input in import // need not
												// comment -- doing import in
												// this class itself
	}

}
