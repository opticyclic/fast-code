package org.fastcode.util;

import static org.fastcode.common.FastCodeConstants.CLASS_ANNOTATIONS_STR;
import static org.fastcode.common.FastCodeConstants.CLASS_BODY_STR;
import static org.fastcode.common.FastCodeConstants.CLASS_HEADER_STR;
import static org.fastcode.common.FastCodeConstants.CLASS_IMPORTS_STR;
import static org.fastcode.common.FastCodeConstants.CLASS_MODIFIER_STR;
import static org.fastcode.common.FastCodeConstants.CLASS_TYPE_STR;
import static org.fastcode.common.FastCodeConstants.COMMA;
import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.DOT_CHAR;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.JAVA_EXTENSION;
import static org.fastcode.common.FastCodeConstants.LEFT_CURL;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.common.FastCodeConstants.SPACE;
import static org.fastcode.setting.GlobalSettings.getInstance;
import static org.fastcode.util.SourceUtil.createAnnotations;
import static org.fastcode.util.SourceUtil.createImportAsString;
import static org.fastcode.util.StringUtil.evaluateByVelocity;
import static org.fastcode.util.StringUtil.getGlobalSettings;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.StringUtil.makePlaceHolder;
import static org.fastcode.util.StringUtil.replacePlaceHolder;
import static org.fastcode.util.StringUtil.replacePlaceHolderWithBlank;
import static org.fastcode.util.StringUtil.replacePlaceHolders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.swt.widgets.Shell;
import org.fastcode.common.FastCodeConstants.CLASS_TYPE;
import org.fastcode.common.FastCodeConstants.RELATION_TYPE;
import org.fastcode.setting.GlobalSettings;

/**
 * @author DELL
 *
 */
public class SimilarCompUnitBuilder implements CompUnitBuilder {

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.fastcode.util.CompUnitBuilder#buildCompUnit(org.eclipse.jdt.core.
	 * IPackageFragment, org.fastcode.util.FastCodeContext,
	 * org.fastcode.util.CreateSimilarDescriptorClass, java.lang.String,
	 * org.fastcode.util.FastCodeConsole)
	 */
	@Override
	public ICompilationUnit buildCompUnit(final IPackageFragment packageFragment, final FastCodeContext fastCodeContext,
			final CreateSimilarDescriptorClass createSimilarDescriptorClass, final String className, final FastCodeConsole fastCodeConsole)
			throws Exception {

		final IType fromType = fastCodeContext.getFromType();
		String extn = JAVA_EXTENSION;
		if (fromType != null) {
			final IFile file = (IFile) fromType.getCompilationUnit().getResource(); // .getAdapter(IFile.class);
			final String name = file.getName();
			final int index = name.lastIndexOf(DOT_CHAR);
			extn = name.substring(index + 1);
		}
		final ICompilationUnit cu = packageFragment.getCompilationUnit(className + DOT + extn);

		final Shell parentShell = MessageUtil.getParentShell();
		final Shell shell = parentShell == null ? new Shell() : parentShell;

		if (cu != null && cu.exists()) {
			fastCodeConsole.writeToConsole("Class " + className + " exists already.");
			return cu;
		}

		final Map<String, Object> placeHolders = new HashMap<String, Object>(fastCodeContext.getPlaceHolders());

		final GlobalSettings globalSettings = getInstance();

		getGlobalSettings(placeHolders);
		final String classHeader = evaluateByVelocity(globalSettings.getClassHeader(), placeHolders);
		placeHolders.put(CLASS_HEADER_STR, classHeader);

		final String[] impTypes = replacePlaceHolders(createSimilarDescriptorClass.getImportTypes(), fastCodeContext.getPlaceHolders());
		final String[] superTypes = replacePlaceHolders(createSimilarDescriptorClass.getSuperTypes(), fastCodeContext.getPlaceHolders());
		final String[] implementTypes = replacePlaceHolders(createSimilarDescriptorClass.getImplementTypes(),
				fastCodeContext.getPlaceHolders());

		final List<IType> importTypes = new ArrayList<IType>();

		final IJavaProject javaProject = packageFragment.getJavaProject();

		// gatherImports(javaProject, impTypes,
		// IJavaElementSearchConstants.CONSIDER_ALL_TYPES, "import",
		// importTypes);
		// List<IType> supTypes = gatherImports(javaProject, superTypes,
		// IJavaElementSearchConstants.CONSIDER_CLASSES, "extend", importTypes);
		// final List<IType> implTypes = gatherImports(javaProject,
		// implementTypes, IJavaElementSearchConstants.CONSIDER_INTERFACES,
		// "implement", importTypes);
		// placeHolders.put("implementTypes", implTypes);
		final List<IType> implTypes = createSimilarDescriptorClass.getUserInputInterface();
		if (createSimilarDescriptorClass.getUserInputImports() != null) {
			for (final IType tmptype : createSimilarDescriptorClass.getUserInputImports()) {
				importTypes.add(tmptype);
			}

		}
		fastCodeContext.addToPlaceHolders("implementTypes", implTypes);

		if (createSimilarDescriptorClass.getParentDescriptor() != null && createSimilarDescriptorClass.getRelationTypeToParent() != null) {
			if (!isEmpty(createSimilarDescriptorClass.getSubPackage())) {
				importTypes.add(fastCodeContext.getCompilationUnitRegsistry(createSimilarDescriptorClass.getParentDescriptor())
						.findPrimaryType());
			}
		}

		String classBody = globalSettings.getClassBody();

		/*
		 * if (superTypes != null && superTypes.length > 0) { String superClass
		 * = EMPTY_STR; final FastCodeParamType[] fastCodeTypes = new
		 * FastCodeParamType[superTypes.length]; int i = 0; for (final String
		 * superType : superTypes) { fastCodeTypes[i++] = parseType(superType);
		 * } final ClassSelectionDialog classSelectionDialog = new
		 * ClassSelectionDialog(shell, "Super Class",
		 * "Choose the classes to extend", fastCodeTypes, false); final int ret
		 * = classSelectionDialog.open(); if (ret != CANCEL) { final
		 * FastCodeParamType fastCodeType = (FastCodeParamType)
		 * classSelectionDialog.getResult()[0]; final IType supType =
		 * packageFragment.getJavaProject().findType(fastCodeType.getType());
		 *
		 * if (supType != null && supType.exists()) { superClass =
		 * flattenType(fastCodeType); superClass =
		 * superClass.replace(supType.getFullyQualifiedName(),
		 * supType.getElementName()); placeHolders.put("super_class",
		 * superClass); if (!supType.isBinary() && supType.getCompilationUnit()
		 * != null && supType.getCompilationUnit().exists()) {
		 * fastCodeContext.addResource(new
		 * FastCodeResource(supType.getResource())); } } } }
		 */
		if (!createSimilarDescriptorClass.getSuperClass().equals(EMPTY_STR)) {
			placeHolders.put("super_class", createSimilarDescriptorClass.getSuperClass());
			importTypes.add(createSimilarDescriptorClass.getUserInputSuperClass());
		}

		placeHolders.put(CLASS_ANNOTATIONS_STR, EMPTY_STR);
		if (createSimilarDescriptorClass.getClassAnnotations() != null && createSimilarDescriptorClass.getClassAnnotations().length > 0) {
			placeHolders
					.put(CLASS_ANNOTATIONS_STR,
							createAnnotations(fastCodeContext, null, javaProject, createSimilarDescriptorClass.getClassAnnotations(),
									placeHolders));
		}
		if (createSimilarDescriptorClass.getClassType() == CLASS_TYPE.CLASS) {
			final StringBuilder interfaces = new StringBuilder();

			if (createSimilarDescriptorClass.getRelationTypeToParent() == RELATION_TYPE.RELATION_TYPE_IMPLEMENTS) {
				// IResource resource =
				// fastCodeContext.getCompilationUnitRegsistry().get(createSimilarDescriptorClass.getParentDescriptor()).getResource();
				if (createSimilarDescriptorClass.getParentDescriptor() != null
						&& fastCodeContext.getCompilationUnitRegsistry(createSimilarDescriptorClass.getParentDescriptor()) != null) {
					final IType intfType = fastCodeContext.getCompilationUnitRegsistry(createSimilarDescriptorClass.getParentDescriptor())
							.findPrimaryType();
					interfaces.append(intfType.getElementName());
				}
			}
			if (implTypes != null && !implTypes.isEmpty()) {
				if (!isEmpty(interfaces.toString())) {
					interfaces.append(COMMA + SPACE);
				}
				for (final IType type : implTypes) {
					interfaces.append(type.getElementName() + (implTypes.indexOf(type) < implTypes.size() - 1 ? COMMA + SPACE : EMPTY_STR));
					if (!type.isBinary() && type.getCompilationUnit() != null && type.getCompilationUnit().exists()) {
						fastCodeContext.addResource(new FastCodeResource(type.getResource()));
					}
				}
			}
			if (!isEmpty(interfaces.toString())) {
				System.out.println(interfaces);
				placeHolders.put("interfaces", interfaces.toString());
			}
		}

		String importAsString = createImportAsString(importTypes);
		if (fastCodeContext.getPlaceHolders().containsKey(CLASS_IMPORTS_STR)) {
			importAsString += NEWLINE + fastCodeContext.getPlaceHolders().get(CLASS_IMPORTS_STR);
		}

		if (!placeHolders.containsKey("super_class")) {
			classBody = replacePlaceHolderWithBlank(classBody, "extends", "super_class", "implements");
		}
		if (!placeHolders.containsKey("interfaces")) {
			classBody = replacePlaceHolderWithBlank(classBody, "implements", "interfaces", LEFT_CURL);
		}
		if (!placeHolders.containsKey(CLASS_MODIFIER_STR)) {
			classBody = replacePlaceHolderWithBlank(classBody, null, CLASS_MODIFIER_STR, makePlaceHolder(CLASS_TYPE_STR));
		}

		classBody = replacePlaceHolder(classBody, CLASS_IMPORTS_STR, importAsString);

		final String classInsideBody = createSimilarDescriptorClass.getClassInsideBody();

		placeHolders.put(CLASS_BODY_STR, EMPTY_STR);
		if (createSimilarDescriptorClass.getClassType() == CLASS_TYPE.CLASS && !isEmpty(classInsideBody)) {
			getGlobalSettings(placeHolders);
			placeHolders.put(CLASS_BODY_STR, evaluateByVelocity(classInsideBody.trim(), placeHolders));
		}

		getGlobalSettings(placeHolders);
		final String source = evaluateByVelocity(classBody, placeHolders);

		final ICompilationUnit compilationUnit = packageFragment.createCompilationUnit(className + DOT + JAVA_EXTENSION, source, false,
				null);

		return compilationUnit;
	}

}
