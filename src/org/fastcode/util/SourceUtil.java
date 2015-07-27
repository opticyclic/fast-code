/*
 * Fast Code Plugin for Eclipse Copyright (C) 2008 Gautam Dev This library is
 * free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 2.1 of the License, or (at your option) any later
 * version. This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * Plugin Home Page: http://fast-code.sourceforge.net/
 */
package org.fastcode.util;

import static org.eclipse.jdt.core.Flags.isAbstract;
import static org.eclipse.jdt.core.Flags.isPublic;
import static org.eclipse.jdt.core.Flags.isStatic;
import static org.eclipse.jdt.core.IJavaElement.CLASS_FILE;
import static org.eclipse.jdt.core.IJavaElement.COMPILATION_UNIT;
import static org.eclipse.jdt.core.IJavaElement.FIELD;
import static org.eclipse.jdt.core.IJavaElement.METHOD;
import static org.eclipse.jdt.core.IJavaElement.TYPE;
import static org.eclipse.jdt.core.Signature.getSignatureSimpleName;
import static org.eclipse.jface.dialogs.MessageDialog.openQuestion;
import static org.eclipse.jface.dialogs.MessageDialog.openWarning;
import static org.eclipse.jface.dialogs.MessageDialogWithToggle.openYesNoQuestion;
import static org.eclipse.jface.window.Window.CANCEL;
import static org.fastcode.common.FastCodeConstants.ASTERISK;
import static org.fastcode.common.FastCodeConstants.CLASS_ANNOTATIONS_STR;
import static org.fastcode.common.FastCodeConstants.CLASS_BODY_STR;
import static org.fastcode.common.FastCodeConstants.CLASS_HEADER_STR;
import static org.fastcode.common.FastCodeConstants.CLASS_IMPORTS_STR;
import static org.fastcode.common.FastCodeConstants.CLASS_INSTANCE_STR;
import static org.fastcode.common.FastCodeConstants.CLASS_MODIFIER_STR;
import static org.fastcode.common.FastCodeConstants.CLASS_NAME_STR;
import static org.fastcode.common.FastCodeConstants.CLASS_TYPE_STR;
import static org.fastcode.common.FastCodeConstants.COLON;
import static org.fastcode.common.FastCodeConstants.COMMA;
import static org.fastcode.common.FastCodeConstants.CONVERSION_CAMEL_CASE;
import static org.fastcode.common.FastCodeConstants.CONVERSION_CAMEL_CASE_HYPHEN;
import static org.fastcode.common.FastCodeConstants.CONVERSION_LOWER_CASE;
import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.DOT_CHAR;
import static org.fastcode.common.FastCodeConstants.EMPTY_QUOTE_STR;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.FC_PLUGIN;
import static org.fastcode.common.FastCodeConstants.FIELD_ANNOTATIONS_STR;
import static org.fastcode.common.FastCodeConstants.FIELD_CLASS_STR;
import static org.fastcode.common.FastCodeConstants.FIELD_FULL_CLASS_STR;
import static org.fastcode.common.FastCodeConstants.FIELD_MODIFIER_STR;
import static org.fastcode.common.FastCodeConstants.FIELD_NAME_STR;
import static org.fastcode.common.FastCodeConstants.FORWARD_SLASH;
import static org.fastcode.common.FastCodeConstants.HYPHEN;
import static org.fastcode.common.FastCodeConstants.INITIATED;
import static org.fastcode.common.FastCodeConstants.JAVA_EXTENSION;
import static org.fastcode.common.FastCodeConstants.KEYWORD_FROM_CLASS;
import static org.fastcode.common.FastCodeConstants.KEYWORD_FROM_INSTANCE;
import static org.fastcode.common.FastCodeConstants.KEYWORD_TO_CLASS;
import static org.fastcode.common.FastCodeConstants.KEYWORD_TO_FULL_CLASS;
import static org.fastcode.common.FastCodeConstants.KEYWORD_TO_FULL_IMPL_CLASS;
import static org.fastcode.common.FastCodeConstants.KEYWORD_TO_IMPL_CLASS;
import static org.fastcode.common.FastCodeConstants.KEYWORD_TO_IMPL_PACKAGE;
import static org.fastcode.common.FastCodeConstants.KEYWORD_TO_INSTANCE;
import static org.fastcode.common.FastCodeConstants.KEYWORD_TO_PACKAGE;
import static org.fastcode.common.FastCodeConstants.LEFT_CURL;
import static org.fastcode.common.FastCodeConstants.LEFT_PAREN;
import static org.fastcode.common.FastCodeConstants.MESSAGE_DIALOG_RETURN_YES;
import static org.fastcode.common.FastCodeConstants.METHOD_ANNOTATIONS_STR;
import static org.fastcode.common.FastCodeConstants.METHOD_ARGS_STR;
import static org.fastcode.common.FastCodeConstants.METHOD_BODY_STR;
import static org.fastcode.common.FastCodeConstants.METHOD_COMMENTS_STR;
import static org.fastcode.common.FastCodeConstants.METHOD_EXCEPTIONS_STR;
import static org.fastcode.common.FastCodeConstants.METHOD_MODIFIER_STR;
import static org.fastcode.common.FastCodeConstants.METHOD_NAME_STR;
import static org.fastcode.common.FastCodeConstants.METHOD_RETURN_TYPE_STR;
import static org.fastcode.common.FastCodeConstants.METHOD_RETURN_TYPE_VOID;
import static org.fastcode.common.FastCodeConstants.MODIFIER_PUBLIC;
import static org.fastcode.common.FastCodeConstants.MY_NEW;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.common.FastCodeConstants.PACKAGE_NAME_STR;
import static org.fastcode.common.FastCodeConstants.PACKAGE_STR;
import static org.fastcode.common.FastCodeConstants.RIGHT_CURL;
import static org.fastcode.common.FastCodeConstants.RIGHT_PAREN;
import static org.fastcode.common.FastCodeConstants.SEMICOLON;
import static org.fastcode.common.FastCodeConstants.SPACE;
import static org.fastcode.common.FastCodeConstants.TAB;
import static org.fastcode.common.FastCodeConstants.UNDERSCORE;
import static org.fastcode.common.FastCodeConstants.UNDER_SCORE;
import static org.fastcode.preferences.PreferenceConstants.P_ASK_FOR_CONTINUE;
import static org.fastcode.preferences.PreferenceConstants.P_COMMON_TEMPLATE_PREFIX;
import static org.fastcode.preferences.PreferenceConstants.P_GETTER_SETTER_POSITION;
import static org.fastcode.preferences.PreferenceConstants.P_REPOSITORY_NAME;
import static org.fastcode.setting.GlobalSettings.getInstance;
import static org.fastcode.util.FastCodeUtil.closeInputStream;
import static org.fastcode.util.FastCodeUtil.generateMethodComments;
import static org.fastcode.util.FastCodeUtil.getEmptyListForNull;
import static org.fastcode.util.ImportUtil.doStaticImport;
import static org.fastcode.util.ImportUtil.retriveStaticMembers;
import static org.fastcode.util.JUnitUtil.isJunitEnabled;
import static org.fastcode.util.StringUtil.changeFirstLetterToUpperCase;
import static org.fastcode.util.StringUtil.changeToCamelCase;
import static org.fastcode.util.StringUtil.changeToPlural;
import static org.fastcode.util.StringUtil.containsXmlStructure;
import static org.fastcode.util.StringUtil.createDefaultInstance;
import static org.fastcode.util.StringUtil.createEmbeddedInstance;
import static org.fastcode.util.StringUtil.evaluateByVelocity;
import static org.fastcode.util.StringUtil.formatXml;
import static org.fastcode.util.StringUtil.getGlobalSettings;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.StringUtil.makePlaceHolder;
import static org.fastcode.util.StringUtil.makeWord;
import static org.fastcode.util.StringUtil.parseTokens;
import static org.fastcode.util.StringUtil.parseType;
import static org.fastcode.util.StringUtil.replacePlaceHolder;
import static org.fastcode.util.StringUtil.replacePlaceHolderWithBlank;
import static org.fastcode.util.StringUtil.replacePlaceHolders;
import static org.fastcode.util.StringUtil.resetLineWithTag;
import static org.fastcode.util.TemplateUtil.PROJECT_NAME;
import static org.fastcode.util.TemplateUtil.getTemplateVariationsFromUser;
import static org.fastcode.util.VersionControlUtil.addOrUpdateFileStatusInCache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.IFileBuffer;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.fastcode.Activator;
import org.fastcode.common.ClassSelectionDialog;
import org.fastcode.common.CreateVariableData;
import org.fastcode.common.FastCodeConstants.CLASS_TYPE;
import org.fastcode.common.FastCodeConstants.GETTER_SETTER;
import org.fastcode.common.FastCodeConstants.GETTER_SETTER_FORMAT;
import org.fastcode.common.FastCodeConstants.RELATION_TYPE;
import org.fastcode.common.FastCodeConstants.REPOSITORY;
import org.fastcode.common.FastCodeField;
import org.fastcode.common.FastCodeMethod;
import org.fastcode.common.FastCodeReturn;
import org.fastcode.common.FastCodeSelectionDialog;
import org.fastcode.common.FastCodeType;
import org.fastcode.common.FieldSelectionDialog;
import org.fastcode.common.MethodSelectionDialog;
import org.fastcode.common.PackageSelectionDialog;
import org.fastcode.common.Pair;
import org.fastcode.common.ProjectSelectionDialog;
import org.fastcode.common.StringSelectionDialog;
import org.fastcode.exception.FastCodeRepositoryException;
import org.fastcode.popup.actions.snippet.FastCodeCache;
import org.fastcode.setting.GlobalSettings;
import org.fastcode.setting.TemplateSettings;
import org.fastcode.versioncontrol.FastCodeCheckinCache;

/**
 * @author Gautam Dev
 *
 */
/**
 * @author Biswarup
 *
 */
/**
 * @author Biswarup
 *
 */
public class SourceUtil {

	// public static GlobalSettings globalSettings =
	// GlobalSettings.getInstance();

	public static final String					GET_METHOD_FORMAT			= "public ${field_type} get${converted_field_name}() {\n" + TAB
																					+ "return this.${instance_name};\n " + "}\n";

	public static final String					GET_METHOD_FORMAT_BOOLEAN	= "public ${field_type} is${converted_field_name}() {\n" + TAB
																					+ "return this.${instance_name};\n " + "}\n";

	public static final String					GET_METHOD_JAVADOC_FORMAT	= "/**\n" + SPACE + "*\n" + SPACE + "* @return \n" + SPACE
																					+ "*/\n";

	public static final String					SET_METHOD_FORMAT			= "public void set${converted_field_name}(${field_type} ${instance_name}) {\n"
																					+ TAB
																					+ "this.${instance_name} = ${instance_name};\n"
																					+ "}\n";

	public static final String					SET_METHOD_JAVADOC_FORMAT	= "/**\n" + SPACE + "*\n" + SPACE
																					+ "* @param ${instance_name}\n" + SPACE + "*/\n";
	public static final int						MAX_TRY						= 100;

	private static final Map<String, String>	returnValues				= new HashMap<String, String>();
	private static final Pattern				parmPattern					= Pattern.compile("([A-Za-z]+)\\s*<\\s*([A-Za-z ,]+)\\s*>\\s*");
	private static FastCodeConsole				fastCodeConsole				= FastCodeConsole.getInstance();
	public static final String[]				NATIVE_TYPES				= { "int", "long", "boolean", "byte", "float", "double",
			"short", "char"												};

	static {
		returnValues.put("int", "0");
		returnValues.put("boolean", "true");
		returnValues.put("long", "0l");
		returnValues.put("short", "0");
		returnValues.put("float", "0");
		returnValues.put("double", "0.0");
	}

	/**
	 *
	 * @param project
	 * @param paramType
	 * @return
	 * @throws Exception
	 */
	public static IType getTypeFromProject(final IJavaProject project, final String paramType) throws Exception {
		final IType type = project.findType(paramType);
		if (type != null && type.exists()) {
			return type;
		}
		return null;

		// return (IType) getElementFromProject(project, paramType, null);
	}

	/**
	 *
	 * @param project
	 * @param paramType
	 * @param filter
	 * @return
	 * @throws Exception
	 */
	public static IType getTypeFromProject(final IJavaProject project, final String paramType, final String filter) throws Exception {
		return (IType) getElementFromProject(project, paramType, filter);
	}

	/**
	 *
	 * @param project
	 * @param paramType
	 * @return
	 * @throws Exception
	 */
	public static Object getElementFromProject(final IJavaProject project, final String paramType, final String filter) throws Exception {

		IType type = project.findType(paramType);
		if (type != null && type.exists()) {
			return type;
		}

		IFile file = project.getProject().getFile(paramType);
		if (file != null && file.exists()) {
			return file;
		}

		final IPackageFragmentRoot[] roots = project.getAllPackageFragmentRoots();
		int nClasses = 0;

		for (final IPackageFragmentRoot root : roots) {
			if (root.getElementName() != null && filter != null && root.getElementName().endsWith(filter)) {
				continue;
			}
			final IJavaElement[] elements = root.getChildren();
			for (int i = 0; i < elements.length; i++) {
				final IJavaElement element = elements[i];
				final IPackageFragment fragment = (IPackageFragment) element.getAdapter(IPackageFragment.class);
				if (fragment == null) {
					continue;
				}

				// TODO redo this logic later with JavaCore.create(file)
				String path = fragment.getPath().toString();
				if (path.length() > root.getPath().toString().length()) {
					path = path.substring(root.getPath().toString().length() + 1);
				}

				final String newpath = path.replaceAll(FORWARD_SLASH, "\\.");

				if (paramType.startsWith(newpath)) {
					String tmpName = paramType.replaceFirst(newpath, path);
					tmpName = tmpName.replaceFirst("\\.", FORWARD_SLASH);
					tmpName = root.getPath().toString() + FORWARD_SLASH + tmpName;
					tmpName = tmpName.substring(project.getProject().getName().length() + 1);
					final IResource resource = project.getProject().findMember(tmpName);
					// JavaCore.create(file);

					if (resource != null && resource.exists()) {
						return resource;
					}
				}

				final IJavaElement fes[] = fragment.getChildren();

				for (int j = 0; j < fes.length; j++) {
					final String className = fes[j].getElementName();

					final int elementType = fes[j].getElementType();
					if (elementType == TYPE) {
						type = (IType) fes[j];
					} else if (elementType == COMPILATION_UNIT) {
						type = ((ICompilationUnit) fes[j]).findPrimaryType();
					} else if (elementType == CLASS_FILE) {
						type = ((IClassFile) fes[j]).findPrimaryType();
					} else if (elementType == IResource.FILE) {
						file = (IFile) fes[j];
					}

					if (type != null && type.getFullyQualifiedName().equals(paramType)) {
						return type;
					} else if (file != null && paramType.equals(fragment.getElementName() + DOT + fes[j].getElementName())) {
						return file;
					}

					nClasses++;
				}
			}
		}
		final String projectName = project.getElementName();
		return null;
	}

	/**
	 *
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static IResource getResourceFromWorkspace(final String name) throws Exception {
		final IProject[] projectArr = ResourcesPlugin.getWorkspace().getRoot().getProjects();

		for (final IProject project : projectArr) {
			if (project == null || !project.exists() || !project.isOpen()) {
				continue;
			}
			IResource resource = project.findMember(name);
			if (resource != null && resource.exists()) {
				return resource;
			}

			final IJavaProject javaProject = JavaCore.create(project);

			if (javaProject != null && javaProject.exists()) {
				resource = (IResource) getElementFromProject(javaProject, name, ".jar");
			}

			if (resource != null && resource.exists()) {
				return resource;
			}
		}
		return null;
	}

	/**
	 *
	 * @param paramType
	 * @return
	 * @throws Exception
	 */
	public static IType getTypeFromWorkspace(final String paramType) throws Exception {
		return getTypeFromWorkspace(paramType, null);
	}

	/**
	 *
	 * @param paramType
	 * @param filter
	 * @return
	 * @throws Exception
	 */
	public static IType getTypeFromWorkspace(final String paramType, final String filter) throws Exception {

		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IJavaModel javaModel = JavaCore.create(workspace.getRoot());
		final IJavaProject projects[] = javaModel.getJavaProjects();

		// First try to find from the projects directly
		for (final IJavaProject project : projects) {
			if (project == null || !project.exists() || !project.isOpen()) {
				continue;
			}
			final IType type = project.findType(paramType);
			if (type != null && type.exists()) {
				return type;
			}
		}

		// Is still cannot find it, look through all projects.
		/*for (final IJavaProject project : projects) {
			if (project == null || !project.exists()) {
				continue;
			}
			final IType type = getTypeFromProject(project, paramType, filter);
			if (type != null && type.exists()) {
				return type;
			}
		}*/
		return null;
	}

	/**
	 *
	 * @param project
	 * @return
	 * @throws Exception
	 */
	public static IPackageFragmentRoot[] getPackageRootsFromProject(final IJavaProject project) throws Exception {
		final IPackageFragmentRoot[] roots = project.getAllPackageFragmentRoots();

		for (final IPackageFragmentRoot root : roots) {
			/*
			 * System.out.println("Package name " + root.getElementName());
			 * System.out.println("Package type " + root.getElementType());
			 * System.out.println("Package path " + root.getPath());
			 */
		}
		return roots;
	}

	/**
	 *
	 * @param project
	 * @param srcPath
	 * @return
	 * @throws Exception
	 */
	public static IPackageFragmentRoot getPackageRootFromProject(final IJavaProject project, final String srcPath) throws Exception {
		final IPackageFragmentRoot[] roots = getPackageRootsFromProject(project);
		for (final IPackageFragmentRoot root : roots) {
			// if (root.getPath().toString().equals("/" +
			// project.getElementName() + srcPath)) {
			if (root.getPath().toString().equals(FORWARD_SLASH + project.getElementName() + srcPath)) {
				return root;
			}
		}
		return null;
	}

	/**
	 *
	 * @param project
	 * @param srcPath
	 * @return
	 * @throws Exception
	 */
	public static IPackageFragmentRoot getPackageRootFromProject(final String project, final String srcPath) throws Exception {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IJavaModel javaModel = JavaCore.create(workspace.getRoot());
		final IJavaProject projects[] = javaModel.getJavaProjects();
		for (final IJavaProject javaProject : projects) {
			if (javaProject.getElementName().equals(project)) {
				return getPackageRootFromProject(javaProject, srcPath);
			}
		}
		return null;
	}

	/**
	 * @param filePath
	 */
	public static IFile findFileFromPath(final String filePath) {
		final IPath fullPath = new Path(filePath);//Path.fromOSString(filePath);
		final String fileName = fullPath.lastSegment();
		final IPath folderPath = fullPath.removeLastSegments(1);
		final IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(folderPath);
		if (folder == null || !folder.exists()) {
			return null;
		}
		final IFile namedQueryFile = folder.getFile(fileName);
		return namedQueryFile != null && namedQueryFile.exists() ? namedQueryFile : null;
	}

	/**
	 *
	 * @param paramType
	 * @return
	 * @throws Exception
	 */
	public static IJavaProject[] getProjectsFromWorkspace() throws Exception {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IJavaModel javaModel = JavaCore.create(workspace.getRoot());
		if (javaModel == null || !javaModel.exists()) {
			return new IJavaProject[0];
		}
		final IJavaProject projects[] = javaModel.getJavaProjects();
		return projects;
	}

	/**
	 * @param prjName
	 * @return
	 * @throws Exception
	 */
	public static IJavaProject getIJavaProjectFromName(final String prjName) throws Exception {

		for (final IJavaProject prj : getProjectsFromWorkspace()) {
			if (prj.getElementName().equals(prjName)) {
				return prj;
			}
		}
		return null;

	}

	/**
	 * @param fastCodeContext
	 * @param member
	 * @param createSimilarDescriptor
	 * @param monitor
	 * @return
	 * @throws Exception
	 */
	public static void createSimilar(final FastCodeContext fastCodeContext, final IMember[] members,
			final CreateSimilarDescriptor createSimilarDescriptor, final IProgressMonitor monitor) throws Exception {

		final GlobalSettings globalSettings = getInstance();
		final Shell parentShell = MessageUtil.getParentShell();
		final Shell shell = parentShell == null ? new Shell() : parentShell;

		IPackageFragmentRoot packageFragmentRoot = null;
		String newpkg = EMPTY_STR, newcls = EMPTY_STR;
		String targetProject = null;
		final CreateSimilarDescriptorClass[] descriptorClasses = createSimilarDescriptor.getCreateSimilarDescUserChoice() == null ? createSimilarDescriptor
				.getCreateSimilarDescriptorClasses() : createSimilarDescriptor.getCreateSimilarDescUserChoice();
		for (final CreateSimilarDescriptorClass createSimilarDescriptorClass : descriptorClasses) {

			if (createSimilarDescriptorClass == null) {
				continue;
			}
			final boolean replaceName = createSimilarDescriptor.isReplaceName();
			String toName = createSimilarDescriptorClass.getToPattern();

			// final String classBody =
			// createSimilarDescriptorClass.getClassBody();
			IPackageFragment packageFragment = null;

			if (packageFragmentRoot == null) {
				targetProject = createSimilarDescriptorClass.getProject();
				if (fastCodeContext.getFromType() == null) {

					/*final IJavaProject project = getJavaProject(targetProject);
					if (project != null && project.exists() && !isEmpty(createSimilarDescriptorClass.getPackge())) {
						for (final IPackageFragmentRoot pkgFragmntRoot : project.getPackageFragmentRoots()) {
							packageFragment = pkgFragmntRoot.getPackageFragment(createSimilarDescriptorClass.getPackge());
							if (packageFragment != null && packageFragment.exists()) {
								packageFragmentRoot = pkgFragmntRoot;
								break;
							}
						}
					}
					if (packageFragment == null || !packageFragment.exists()) {
						final SelectionDialog packageDialog = JavaUI.createPackageDialog(shell, project, 0, null);
						if (packageDialog.open() == CANCEL) {
							return;
						}
						packageFragment = (IPackageFragment) packageDialog.getResult()[0];
						packageFragmentRoot = (IPackageFragmentRoot) packageFragment.getParent();
					} else if (isEmpty(createSimilarDescriptorClass.getSubPackage()) && packageFragment.hasSubpackages()) {
						final List<IPackageFragment> subPackages = new ArrayList<IPackageFragment>();
						for (final IJavaElement chldPkgFragment : packageFragmentRoot.getChildren()) {
							if (chldPkgFragment instanceof IPackageFragment
									&& chldPkgFragment.getElementName().startsWith(packageFragment.getElementName())) {
								subPackages.add((IPackageFragment) chldPkgFragment);
							}
						}
						if (!subPackages.isEmpty()) {
							final PackageSelectionDialog selectionDialog = new PackageSelectionDialog(shell, "Sub Package",
									"Choose the sub pacage from below", subPackages.toArray(new IPackageFragment[0]));
							if (selectionDialog.open() != CANCEL) {
								packageFragment = (IPackageFragment) selectionDialog.getFirstResult();
							}
						}
					}*/

					packageFragment = createSimilarDescriptorClass.getUserInputPackage();
					packageFragmentRoot = (IPackageFragmentRoot) packageFragment.getParent();
					newpkg = packageFragment.getElementName();
					newcls = replacePlaceHolders(toName, fastCodeContext.getPlaceHolders());
				} else {
					if (fastCodeContext.isUnitTest()) {
						String sourcePath = globalSettings.isUseDefaultForPath() ? globalSettings.getSourcePathTest()
								: createSimilarDescriptorClass.getSourcePath();
						sourcePath = getDefaultPathFromProject(fastCodeContext.getFromType().getJavaProject(), "test", sourcePath);
						packageFragmentRoot = getPackageRootFromProject(fastCodeContext.getFromType().getJavaProject(), sourcePath);
					} else if (!isEmpty(targetProject)) {
						final String sourcePath = globalSettings.isUseDefaultForPath() ? getPathFromGlobalSettings(fastCodeContext
								.getFromType().getJavaProject().getElementName()) : createSimilarDescriptorClass.getSourcePath();
						;
						packageFragmentRoot = getPackageRootFromProject(createSimilarDescriptorClass.getProject(), sourcePath);
					} else {
						packageFragmentRoot = (IPackageFragmentRoot) fastCodeContext.getFromType().getPackageFragment().getParent();
						targetProject = packageFragmentRoot.getParent().getElementName();
					}
					final String fullname = fastCodeContext.getFromType().getFullyQualifiedName();
					final String fromPattern = createSimilarDescriptor.getFromPattern();
					if (fromPattern != null) {
						parseTokens(fromPattern, fullname, fastCodeContext.getPlaceHolders());
					}
					if (packageFragmentRoot == null || !packageFragmentRoot.exists()) {
						throw new Exception("Unable to find source path for, please check configuration.");
					}
					toName = replacePlaceHolders(toName, fastCodeContext.getPlaceHolders());
					if (createSimilarDescriptor.isDifferentName()) {
						toName = fullname.replaceAll(createSimilarDescriptor.getReplacePart(), createSimilarDescriptor.getReplaceValue());
					}
					final int lastDotPos = toName.lastIndexOf(DOT_CHAR);
					newpkg = lastDotPos != -1 ? toName.substring(0, lastDotPos) : fastCodeContext.getFromType().getPackageFragment()
							.getElementName();
					newcls = lastDotPos != -1 ? toName.substring(lastDotPos + 1) : toName;
					packageFragment = packageFragmentRoot.getPackageFragment(newpkg);
				}
			}

			if (packageFragmentRoot == null || !packageFragmentRoot.exists()) {
				throw new Exception("Unable to find package fragment root for " + toName);
			}

			// fastCodeContext.addToPlaceHolders(toName, newcls);
			final boolean isImplClass = createSimilarDescriptorClass.getParentDescriptor() != null
					&& createSimilarDescriptorClass.getRelationTypeToParent() == RELATION_TYPE.RELATION_TYPE_IMPLEMENTS;

			if (isImplClass) {
				newcls += globalSettings.getImplExtension();
				if (!isEmpty(createSimilarDescriptorClass.getSubPackage())) {
					newpkg += "." + createSimilarDescriptorClass.getSubPackage();
					packageFragment = packageFragmentRoot.getPackageFragment(newpkg);
					if (packageFragment == null || !packageFragment.exists()) {
						packageFragment = packageFragmentRoot.createPackageFragment(newpkg, false, monitor);
					}
				}
			}

			fastCodeContext.addToPlaceHolders(PACKAGE_NAME_STR, newpkg);
			fastCodeContext.addToPlaceHolders(CLASS_NAME_STR, newcls);
			if (createSimilarDescriptorClass.isFinalClass()) {
				fastCodeContext.addToPlaceHolders(CLASS_MODIFIER_STR, "final");
			}
			fastCodeContext.addToPlaceHolders(CLASS_INSTANCE_STR, createDefaultInstance(newcls));
			fastCodeContext.addToPlaceHolders(CLASS_TYPE_STR, createSimilarDescriptorClass.getClassType().value().toLowerCase());

			if (!fastCodeContext.getPlaceHolders().containsKey(KEYWORD_FROM_CLASS) && fastCodeContext.getFromType() != null) {
				fastCodeContext.addToPlaceHolders(KEYWORD_FROM_CLASS, fastCodeContext.getFromType().getElementName());
				fastCodeContext.addToPlaceHolders(KEYWORD_FROM_INSTANCE, createDefaultInstance(fastCodeContext.getFromType()
						.getElementName()));
			}

			if (!fastCodeContext.getPlaceHolders().containsKey(KEYWORD_TO_CLASS)) {
				fastCodeContext.addToPlaceHolders(KEYWORD_TO_CLASS, newcls);
				fastCodeContext.addToPlaceHolders(KEYWORD_TO_PACKAGE, newpkg);
				fastCodeContext.addToPlaceHolders(KEYWORD_TO_FULL_CLASS, newpkg + DOT + newcls);
				fastCodeContext.addToPlaceHolders(KEYWORD_TO_INSTANCE, createDefaultInstance(newcls));
			} else if (isImplClass) {
				fastCodeContext.addToPlaceHolders(KEYWORD_TO_IMPL_PACKAGE, newpkg);
				fastCodeContext.addToPlaceHolders(KEYWORD_TO_IMPL_CLASS, newcls);
				fastCodeContext.addToPlaceHolders(KEYWORD_TO_FULL_IMPL_CLASS, newpkg + DOT + newcls);
			}

			if (replaceName && (packageFragment == null || !packageFragment.exists())) {
				throw new Exception("Fatal error : package must exist.");
			} else if (packageFragment == null || !packageFragment.exists()) {
				final File pkgObj = new File(packageFragmentRoot.getResource().getLocationURI());
				/*final FastCodeCheckinCache checkinCache = FastCodeCheckinCache.getInstance();
				checkinCache.getFilesToCheckIn().add(new FastCodeFileForCheckin(INITIATED, pkgObj.getAbsolutePath()));*/
				addOrUpdateFileStatusInCache(pkgObj);

				packageFragment = packageFragmentRoot.createPackageFragment(newpkg, false, monitor);
			}

			ICompilationUnit compilationUnit = packageFragment.getCompilationUnit(newcls + DOT + JAVA_EXTENSION);
			boolean isNew = false;
			final CompUnitBuilder compUnitBuilder = new SimilarCompUnitBuilder();
			if (compilationUnit == null || !compilationUnit.exists()) {
				fastCodeConsole.writeToConsole("Creating class " + newcls);
				// compilationUnit = createCompUnit(packageFragment,
				// fastCodeContext, createSimilarDescriptorClass, newcls);
				final String prjURI = packageFragment.getResource().getLocationURI().toString();
				final String path = prjURI.substring(prjURI.indexOf(COLON) + 1);
				final File newFileObj = new File(path + FORWARD_SLASH + newcls + DOT + JAVA_EXTENSION);
				final FastCodeCheckinCache checkinCache = FastCodeCheckinCache.getInstance();
				checkinCache.getFilesToCheckIn().add(new FastCodeFileForCheckin(INITIATED, newFileObj.getAbsolutePath()));

				compilationUnit = compUnitBuilder.buildCompUnit(packageFragment, fastCodeContext, createSimilarDescriptorClass, newcls,
						fastCodeConsole);
				isNew = true;
			} else {
				if (!compilationUnit.getResource().isSynchronized(0)) {
					throw new Exception(compilationUnit.getElementName() + " is not Synchronized, please refresh and try again.");
				}
				fastCodeConsole.writeToConsole("Class  " + newcls + " exists already.");
			}
			fastCodeContext.addCompilationUnitToRegsistry(createSimilarDescriptorClass, compilationUnit);

			compilationUnit.becomeWorkingCopy(monitor);
			fastCodeContext.addResource(new FastCodeResource(compilationUnit.getResource(), isNew));

			monitor.subTask("Creating details for " + compilationUnit.findPrimaryType().getFullyQualifiedName());

			try {
				IType pType = compilationUnit.findPrimaryType();
				int tries = 0;
				// hack because findPrimaryType is not available immediately
				// sometimes.
				while (isNew && (tries++ < MAX_TRY || pType == null || !pType.exists())) {
					pType = compilationUnit.findPrimaryType();
					Thread.sleep(100);
				}
				if (pType == null || !pType.exists()) {
					throw new Exception("Unknown exception occured, please try it again.");
				}

				createNewTypeDetails(fastCodeContext, pType, members, createSimilarDescriptor, createSimilarDescriptorClass, monitor);
				if (globalSettings.isAutoSave()) {
					compilationUnit.commitWorkingCopy(false, monitor);
				}
			} finally {
				compilationUnit.discardWorkingCopy();
			}
		}

		// monitor.subTask("Creating configuration for " +
		// cu.findPrimaryType().getFullyQualifiedName());

		boolean createConfig = false;
		if (createSimilarDescriptor.getNoOfInputs() > 1) {
			return;
		}
		for (final CreateSimilarDescriptorConfig descriptorConfig : createSimilarDescriptor.getDescriptorConfigParts()) {
			if (descriptorConfig != null) {
				createConfig = true;
			}
		}

		if (!createConfig) {
			return;
		}

		boolean showFieldsDialog = true;
		final List<FastCodeField> fastCodeFields = new ArrayList<FastCodeField>();
		final Map<String, List<FastCodeField>> fieldsMap = new HashMap<String, List<FastCodeField>>();
		fieldsMap.put("fields", fastCodeFields);

		IType typeForConfiguration = null;
		for (final CreateSimilarDescriptorClass createSimilarDescriptorClass : descriptorClasses) {
			final ICompilationUnit compilationUnit = fastCodeContext.getCompilationUnitRegsistry(createSimilarDescriptorClass);
			if (compilationUnit.findPrimaryType().isClass()) {
				typeForConfiguration = compilationUnit.findPrimaryType();
				break;
			}
		}

		for (final CreateSimilarDescriptorConfig descriptorConfig : createSimilarDescriptor.getDescriptorConfigParts()) {

			if (descriptorConfig == null) {
				continue;
			}

			final IField[] fields = getFieldsOfType(typeForConfiguration);
			if (showFieldsDialog && fields != null && fields.length > 0) {
				final FieldSelectionDialog fieldSelectionDialog = new FieldSelectionDialog(shell, "Fields for configuration",
						"Choose the fields for configuration " + descriptorConfig.getConfigType(), fields, true);

				if (fieldSelectionDialog.open() != CANCEL) {
					final Object[] results = fieldSelectionDialog.getResult();
					for (final Object result : results) {
						final IField field = (IField) result;
						fastCodeFields.add(new FastCodeField(field, field.getElementName()));
					}
					showFieldsDialog = false; // get the fields first time.
				}
			}

			// String fileName = descriptorConfig.getConfigFileName();
			// final String fileName =
			// replacePlaceHolders(descriptorConfig.getConfigFileName(),
			// fastCodeContext.getPlaceHolders());

			createConfigurations(fastCodeContext, descriptorConfig, targetProject, fieldsMap);
		}
	}

	/**
	 *
	 * @param path
	 * @param name
	 *            (file name)
	 * @param configPattern
	 * @param locale
	 * @param configStartPattern
	 * @param configEndPattern
	 *
	 */
	private static void createConfigurations(final FastCodeContext fastCodeContext, final CreateSimilarDescriptorConfig descriptorConfig,
			final String project, final Map<String, List<FastCodeField>> fieldsMap) throws Exception {
		final String[] fileNames = descriptorConfig.getConfigFileName().split("\\s+");

		for (final String fileName : fileNames) {
			createConfiguration(fastCodeContext, fileName, project, descriptorConfig, fieldsMap);
		}
	}

	/**
	 *
	 * @param path
	 * @param name
	 *            (file name)
	 * @param configPattern
	 * @param locale
	 * @param configStartPattern
	 * @param configEndPattern
	 */
	private static void createConfiguration(final FastCodeContext fastCodeContext, final String fileName, final String project,
			final CreateSimilarDescriptorConfig descriptorConfig, final Map<String, List<FastCodeField>> fieldsMap) throws Exception {

		String[] locales = { EMPTY_STR };
		final String locale = descriptorConfig.getConfigLocale();
		if (!isEmpty(locale)) {
			locales = locale.split("\\s+");
		}

		final Map<String, Object> placeHolders = new HashMap<String, Object>(fastCodeContext.getPlaceHolders());
		/*
		 * IType fromType = fastCodeContext.getFromType(); IType toType = null,
		 * toImplType = null; for (FastCodeResource resource :
		 * fastCodeContext.getResources()) { if (resource instanceof IType) {
		 * IType type = (IType) resource; if (toType == null) { toType = type;
		 * // first type is toType } for (String interfaceName :
		 * type.getSuperInterfaceNames()) { if
		 * (interfaceName.equals(type.getElementName())) { toImplType = type;
		 * break; } } if (toType != null && toImplType != null) { break; } } }
		 */
		final IJavaProject targetProj = project != null ? getJavaProject(project) : fastCodeContext.getFromType().getJavaProject();
		if (targetProj == null || !targetProj.exists()) {
			throw new Exception("Project cannot be found for configuration " + project);
		}

		// for (FastCodeResource fastCodeResource :
		// fastCodeContext.getResources()) {
		// project =
		// JavaCore.create(fastCodeResource.getResource().getProject());
		// }

		final IFolder folder = getFolderFromPath(targetProj.getProject(), descriptorConfig.getConfigLocation());
		if (folder == null || !folder.exists()) {
			throw new Exception("Location is wrong or cannot be found for configuration " + descriptorConfig.getConfigLocation());
		} else if (!folder.isSynchronized(0)) {
			throw new Exception(folder.getName() + " is not Synchronized, please refresh and try again.");
		}
		for (final String locle : locales) {
			String file = isEmpty(locle) ? fileName : replaceLocale(fileName, locle);

			file = replacePlaceHolders(file, placeHolders);
			final String convPattern = descriptorConfig.getConversionPattern();
			if (convPattern.equals(CONVERSION_LOWER_CASE)) {
				file = file.toLowerCase();
			} else if (convPattern.equals(CONVERSION_CAMEL_CASE)) {
				file = changeToCamelCase(file, UNDER_SCORE);
			} else if (convPattern.equals(CONVERSION_CAMEL_CASE_HYPHEN)) {
				file = changeToCamelCase(file, '-');
			}
			updateConfigFile(folder, file, placeHolders, fieldsMap, fastCodeContext, descriptorConfig);

			fastCodeContext.addResource(new FastCodeResource(folder.getFile(file)));
		}
	}

	/**
	 *
	 * @param project
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public static IFolder getFolderFromPath(final IProject project, final String path) throws Exception {
		/*final IWorkspaceRoot root = project.getWorkspace().getRoot();

		final IPath searchPath = project.getFullPath().append(path);
		final IFolder folder = root.getFolder(searchPath);*/

		/*if (folder == null || !folder.exists()) {
			// searchPath = root.getFullPath().append(path);
			folder = root.getFolder(searchPath);
		}*/

		/*if (folder == null || !folder.exists()) {
			//if (folder != null) {
				// folder.create(false, true, null);
				createFolder(folder.getFullPath());
			//}
			// throw new Exception("Folder does not exist : " + (folder == null
			// ? EMPTY_STR : folder.getFullPath()));
		}

		return folder;*/
		final IWorkspaceRoot root = project.getWorkspace().getRoot();

		final IPath searchPath = project.getFullPath().append(path);
		final IFolder folder = root.getFolder(searchPath);

		if (!folder.exists()) {

			createFolder(searchPath);
		}

		return folder;
	}

	/**
	 * @param path
	 * @return
	 * @throws CoreException
	 */
	public static IFolder createFolder(final IPath path) throws CoreException {
		final IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(path);

		final IContainer parent = folder.getParent();
		if (parent instanceof IFolder && !parent.exists()) {
			createFolder(parent.getFullPath());
		}
		refreshProject(PROJECT_NAME);
		folder.create(true, true, new NullProgressMonitor());
		return folder;
	}

	/**
	 *
	 * @param folder
	 * @param fileName
	 * @param fieldsMap
	 * @param fastCodeContext
	 * @param descriptorConfig
	 * @param toType
	 * @throws Exception
	 */
	public static void updateConfigFile(final IFolder folder, final String fileName, final Map<String, Object> placeHolders,
			final Map<String, List<FastCodeField>> fieldsMap, final FastCodeContext fastCodeContext,
			final CreateSimilarDescriptorConfig descriptorConfig) throws Exception {
		final IFile file = folder.getFile(fileName);
		String fileContents = getFileContents(file);

		getGlobalSettings(placeHolders);
		final String configSource = evaluateByVelocity(descriptorConfig.getConfigPattern(), placeHolders, fieldsMap);

		final String configHeader = descriptorConfig.getConfigHeaderPattern();
		final String configStart = descriptorConfig.getConfigStartPattern();
		final String configEnd = descriptorConfig.getConfigEndPattern();

		if (!isEmpty(fileContents) && !isEmpty(configStart) && !isEmpty(configEnd) && isEmpty(configHeader)) {
			final int start = fileContents.indexOf(configStart);
			final int end = fileContents.indexOf(configEnd);
			// if (start == -1 || end == -1 || start > end) {
			fastCodeConsole.writeToConsole("File " + fileName + " is not of correct format");
			// throw new Exception("File " + name +
			// " is not of correct format");
			// return;
			// }
			// fileContents = fileContents.substring(start +
			// configStart.length(), end);
		}

		if (fileName.endsWith(".xml") && fileContents != null) {
			if (containsXmlStructure(configSource, configStart, fileContents)) {
				fastCodeConsole.writeToConsole("File " + fileName + " already contains the pattern.");
				return;
			}
		}

		if (file != null && file.exists()) {
			fastCodeConsole.writeToConsole("Modifying Configuration file " + file.getName());
			final int start = fileContents.indexOf(configEnd);
			// fileContents = fileContents.substring(0, start) + configSource +
			// NEWLINE + configEnd;
			final FileEditorInput editorInput = new FileEditorInput(file);
			final IEditorPart editor = getEditorPartFromFile(file);
			final ITextEditor textEditor = (ITextEditor) editor.getAdapter(ITextEditor.class);
			// editor = (IEditorPart)page.openEditor(editorInput, desc.getId());
			final IDocumentProvider documentProvider = textEditor.getDocumentProvider();
			final IDocument document = documentProvider.getDocument(editorInput);
			if (document == null) {
				fastCodeConsole.writeToConsole("Unable to write Configuration file " + file.getName());
				return;
			}
			document.replace(start, 0, formatXml(configSource));
		} else {
			fileContents = configHeader + NEWLINE + configStart + NEWLINE + configSource + NEWLINE + configEnd;
			fastCodeConsole.writeToConsole("Creating Configuration file " + file == null ? EMPTY_STR : file.getName());
			InputStream in = null;
			try {
				in = new StringBufferInputStream(fileContents);
				file.create(in, false, null);
			} finally {
				if (in != null) {
					in.close();
				}
			}
		}
	}

	/**
	 *
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static IEditorPart getEditorPartFromFile(final IFile file) throws Exception {
		final FileEditorInput editorInput = new FileEditorInput(file);
		final IWorkbench wb = PlatformUI.getWorkbench();
		final IWorkbenchPage page = wb.getActiveWorkbenchWindow().getActivePage();
		final IEditorDescriptor desc = wb.getEditorRegistry().getDefaultEditor(file.getName());
		final IEditorPart editor = page.findEditor(editorInput);
		if (editor != null) {
			return editor;
		}

		return page.openEditor(editorInput, desc.getId());
	}

	/**
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String getFileContents(final IFile file) throws Exception {
		InputStream in = null;

		if (file == null || !file.exists()) {
			return null;
		}
		fastCodeConsole.writeToConsoleWithLink("Found configuration file ", file);
		try {
			in = file.getContents();
			final byte[] bytes = new byte[in.available()];
			in.read(bytes);
			return new String(bytes);
		} catch (final Exception ex) {
			// Do nothing
			throw new Exception(ex);
		} finally {
			FastCodeUtil.closeInputStream(in);
		}
		// file.delete(false, null);
	}

	/**
	 * @param fastCodeContext
	 * @param toType
	 * @param member
	 * @param createSimilarDescriptor
	 * @return
	 * @throws Exception
	 */
	private static void createNewTypeDetails(final FastCodeContext fastCodeContext, final IType toType, final IMember[] members,
			final CreateSimilarDescriptor createSimilarDescriptor, final CreateSimilarDescriptorClass createSimilarDescriptorClass,
			final IProgressMonitor monitor) throws Exception {

		final IType fromType = fastCodeContext.getFromType();

		final boolean isClass = createSimilarDescriptorClass.getClassType() == CLASS_TYPE.CLASS;

		final Shell parentShell = MessageUtil.getParentShell();
		final Shell shell = parentShell == null ? new Shell() : parentShell;

		if (isClass && createSimilarDescriptorClass.isCreateDefaultConstructor()) {
			createMethod(fastCodeContext, toType, false, toType.getElementName());
		}

		if (toType.isClass() && createSimilarDescriptorClass.isInclInstance()) {
			final IField field = createInstanceVariable(fastCodeContext, toType, fromType, null, createSimilarDescriptorClass, monitor);
			if (field != null) {
				final FastCodeResource resource = fastCodeContext.findResource(toType.getCompilationUnit());
				resource.setModified(true);
			}
		}
		if (createSimilarDescriptorClass.isCreateFields()) {
			/*
			 * final SelectionDialog selectionDialog =
			 * JavaUI.createTypeDialog(shell, null,
			 * SearchEngine.createWorkspaceScope(),
			 * IJavaElementSearchConstants.CONSIDER_CLASSES_AND_INTERFACES,
			 * true, createSimilarDescriptorClass.getCreateFieldsName());
			 * selectionDialog
			 * .setMessage("Please select one or more classes to create field."
			 * ); selectionDialog.setTitle("Select Class"); if
			 * (selectionDialog.open() != CANCEL) {
			 */for (final Object type : createSimilarDescriptorClass.getUserInputFieldTypes()) {
				final IType fldType = (IType) type;
				/*
				 * if (isAbstract(fldType.getFlags())) { openWarning(shell,
				 * "Warning", "Cannot make an instance of an abstract class " +
				 * fldType.getElementName()); continue; }
				 */final IField field = createInstanceVariable(fastCodeContext, toType, fldType, null, createSimilarDescriptorClass,
						monitor);
				if (field != null) {
					final FastCodeResource resource = fastCodeContext.findResource(toType.getCompilationUnit());
					resource.setModified(true);
					if (!fldType.isBinary() && fldType.getCompilationUnit() != null && fldType.getCompilationUnit().exists()) {
						fastCodeContext.addResource(new FastCodeResource(fldType.getResource()));
						// }
					}
				}
			}
		}

		if (members != null) {
			if (createSimilarDescriptor.isCopyMethod()) {
				monitor.subTask("Copying method to " + toType.getFullyQualifiedName());
				final MethodBuilder methodBuilder;
				if (fastCodeContext.isUnitTest()) {
					methodBuilder = new UnitTestMethodBuilder(fastCodeContext);
				} else {
					methodBuilder = createSimilarDescriptor.isDifferentName() ? new DiffNameSimilarMethodBuilder(fastCodeContext)
							: new SimilarMethodBuilder(fastCodeContext);
					;
				}
				for (final IMember member : members) {
					final IMethod addedMethod = methodBuilder.buildMethod((IMethod) member, toType, createSimilarDescriptor,
							createSimilarDescriptorClass);
					if (addedMethod != null) {
						final FastCodeResource resource = fastCodeContext.findResource(toType.getCompilationUnit());
						if (resource != null) {
							resource.setModified(true);
						}
					}
				}
			} else if (createSimilarDescriptor.isCopyField()) {
				monitor.subTask("Copying fields to " + toType.getFullyQualifiedName());
				final IType[] types = fromType.newTypeHierarchy(monitor).getAllClasses();

				for (int i = types.length - 1; i > -1; i--) {
					copyFields(types[i], toType, (IField[]) members, createSimilarDescriptor, createSimilarDescriptorClass, monitor);
				}
			}
		}

		if (isClass) {
			boolean createConstructor = createSimilarDescriptorClass.isCreateInstanceConstructor();
			int count = 0;
			final IField[] fieldsArr = getFieldsOfType(toType);
			while (fieldsArr != null && fieldsArr.length > 0 && createConstructor) {
				createMethod(fastCodeContext, toType, true, toType.getElementName());
				createConstructor = openQuestion(shell, "Constructor", "Would you like to create more Constructors");
				count++;
			}
			/*
			 * if (createSimilarDescriptorClass.isCreateEqualsHashcode()) {
			 * createMethod(fastCodeContext, toType, true, METHOD_EQUALS,
			 * METHOD_HASHCODE); }
			 *
			 * if (createSimilarDescriptorClass.isCreateToString()) {
			 * createMethod(fastCodeContext, toType, true, METHOD_TOSTRING); }
			 */}

		// Copy the method from interfaces to implement.
		if (fastCodeContext.getPlaceHolders().get("implementTypes") != null) {
			final List<IType> implTypes = (List<IType>) fastCodeContext.getPlaceHolders().get("implementTypes");

			for (final IType impType : implTypes) {
				// final IType impType =
				// toType.getJavaProject().findType(implType);
				if (impType == null || !impType.exists()) {
					continue;
				}
				implementInterfaceMethods(impType, fastCodeContext, toType, createSimilarDescriptor, createSimilarDescriptorClass);
				final IType[] superInterfacesType = getSuperInterfacesType(impType);
				if (superInterfacesType != null && superInterfacesType.length > 0) {
					for (final IType type : superInterfacesType) {
						if (type == null || !type.exists()) {
							continue;
						}
						implementInterfaceMethods(type, fastCodeContext, toType, createSimilarDescriptor, createSimilarDescriptorClass);
					}

				}
				/*
				 * if (createSimilarDescriptorClass.getImplementTypes() != null)
				 * { for (final String implType :
				 * createSimilarDescriptorClass.getImplementTypes()) {
				 * System.out.println(implType); final MethodBuilder
				 * methodBuilder = new SimilarMethodBuilder(fastCodeContext);
				 * final IType impType =
				 * toType.getJavaProject().findType(implType);
				 * System.out.println(impType); if (impType == null ||
				 * !impType.exists()) { continue; } for (final IMethod method :
				 * impType.getMethods()) { final IMethod meth =
				 * methodBuilder.buildMethod(method, toType); if (meth != null)
				 * { final FastCodeResource resource =
				 * fastCodeContext.findResource(toType.getCompilationUnit()); if
				 * (resource != null) { resource.setModified(true); } } } } }
				 */
			}
		}
		return;
	}

	/**
	 *
	 * @param fromType
	 * @param toType
	 * @throws Exception
	 */
	public static void copyImports(final ICompilationUnit fromCompUnit, final ICompilationUnit toCompUnit, final IMethod method)
			throws Exception {
		final Map<String, String> importMap = new HashMap<String, String>();

		for (final IImportDeclaration imp : fromCompUnit.getImports()) {
			if (!imp.getElementName().endsWith(ASTERISK)) {
				final int off = imp.getElementName().lastIndexOf(DOT_CHAR);
				final String className = imp.getElementName().substring(off + 1);
				final IImportDeclaration declaration = toCompUnit.getImport(imp.getElementName());

				if (declaration != null && declaration.exists()) {
					continue;
				}

				if (method != null) {
					boolean matchParameter = false;
					for (final String parameterType : method.getParameterTypes()) {
						if (className.equals(getSignatureSimpleName(parameterType))) {
							matchParameter = true;
							break;
						}
					}
					for (final String exceptionType : method.getExceptionTypes()) {
						if (className.equals(getSignatureSimpleName(exceptionType))) {
							matchParameter = true;
							break;
						}
					}
					if (!matchParameter) {
						continue;
					}
				}

				if (importMap.containsKey(className)) {
					// conflict found, special handling later
					continue;
				}
				boolean match = false;
				// Check for conflicts
				for (final IImportDeclaration toimp : toCompUnit.getImports()) {
					final int off1 = toimp.getElementName().lastIndexOf(DOT_CHAR);
					final String clsName = toimp.getElementName().substring(off1 + 1);
					if (clsName.equals(className)) {
						match = true;
						// conflict found, special handling later
					}
					if (!importMap.containsKey(clsName)) {
						importMap.put(clsName, toimp.getElementName());
					}
					if (match) {
						break;
					}
				}
				if (!match) {
					toCompUnit.createImport(imp.getElementName(), null, null);
				}

			}
		}
	}

	/**
	 * This method will create an instance variable of fromType.
	 *
	 * @param toType
	 * @param fieldType
	 * @param createSimilarDescriptorClass
	 * @throws Exception
	 */
	public static IField createInstanceVariable(final FastCodeContext fastCodeContext, final IType toType, final IType fieldType,
			final String fieldName, final CreateSimilarDescriptorClass createSimilarDescriptorClass, final IProgressMonitor monitor)
			throws Exception {

		final String fldName = fieldName == null ? createDefaultInstance(fieldType.getElementName()) : fieldName;

		IField field = toType.getField(fldName);
		if (field != null && field.exists()) {
			return null;
		}

		final GlobalSettings globalSettings = getInstance();
		final String fieldBody = globalSettings.getFieldBody().trim();
		String annotations = EMPTY_STR;

		final Map<String, Object> placeHolders = new HashMap<String, Object>();
		placeHolders.put(FIELD_CLASS_STR, fieldType.getElementName());
		placeHolders.put(FIELD_FULL_CLASS_STR, fieldType.getFullyQualifiedName());
		placeHolders.put(FIELD_NAME_STR, fldName);
		placeHolders.put(FIELD_MODIFIER_STR, "private");

		final String[] fieldAnnotations = createSimilarDescriptorClass == null ? null : createSimilarDescriptorClass.getFieldAnnotations();
		if (fieldAnnotations != null && fieldAnnotations.length > 0) {
			annotations = createAnnotations(fastCodeContext, toType.getCompilationUnit(), toType.getJavaProject(), fieldAnnotations,
					placeHolders);
		}

		placeHolders.put(FIELD_ANNOTATIONS_STR, annotations);
		final String fieldSrc = replacePlaceHolders(fieldBody, placeHolders);
		// System.out.println("annotations " + annotations);
		// System.out.println("fieldSrc " + fieldSrc);

		field = toType.createField(fieldSrc, null, false, monitor);
		if (field == null || !field.exists()) {
			return null;
		}
		toType.getCompilationUnit().createImport(fieldType.getFullyQualifiedName(), null, monitor);
		if (createSimilarDescriptorClass != null && createSimilarDescriptorClass.isInclGetterSetterForInstance()) {
			String getMethodFrag = createJavadoc(GET_METHOD_JAVADOC_FORMAT, fieldType.getElementName());
			getMethodFrag += createFullMethodDefn(GET_METHOD_FORMAT, fieldType.getElementName());

			String setMethodFrag = createJavadoc(SET_METHOD_JAVADOC_FORMAT, fieldType.getElementName());
			setMethodFrag += createFullMethodDefn(SET_METHOD_FORMAT, fieldType.getElementName());

			toType.createMethod(getMethodFrag, null, false, monitor);
			toType.createMethod(setMethodFrag, null, false, monitor);
		}
		return field;
	}

	/**
	 * General purpose create method used mainly for Constructor, equals,
	 * hashCode, and toString.
	 *
	 * @param type
	 * @param fields
	 * @param methodName
	 * @param isConstructor
	 * @throws Exception
	 */
	public static IMethod createMethod(final FastCodeContext fastCodeContext, final IType type, final boolean showFieldsDialog,
			final String... methodNames) throws Exception {
		if (methodNames == null || methodNames.length == 0) {
			throw new Exception("Need at least one method to construct");
		}
		final Shell parentShell = MessageUtil.getParentShell();
		final Shell shell = parentShell == null ? new Shell() : parentShell;

		final Map<String, Object> placeHolders = new HashMap<String, Object>(fastCodeContext.getPlaceHolders());
		final Map<String, List<FastCodeField>> placeHoldersBody = new HashMap<String, List<FastCodeField>>();
		final List<FastCodeField> fastCodeFields = new ArrayList<FastCodeField>();
		final boolean isConstructor = methodNames[0].equals(type.getElementName());
		IField[] fields = null;
		final GlobalSettings globalSettings = getInstance();

		final StringBuilder displayMethod = new StringBuilder(isConstructor ? "Constructor" : EMPTY_STR);
		if (!isConstructor) {
			for (final String methodName : methodNames) {
				displayMethod.append(methodName + SPACE);
			}
			displayMethod.trimToSize();
		}

		if (showFieldsDialog) {
			final IField[] fieldsArr = getFieldsOfType(type);
			if (fieldsArr == null || fieldsArr.length == 0) {
				return null;
			}

			final FieldSelectionDialog fieldSelectionDialog = new FieldSelectionDialog(shell, "Fields for " + displayMethod,
					"Choose the fields for " + displayMethod, fieldsArr, true);

			if (fieldSelectionDialog.open() == CANCEL) {
				return null;
			}
			final Object[] result = fieldSelectionDialog.getResult();
			fields = new IField[result.length];
			System.arraycopy(result, 0, fields, 0, result.length);
		}

		IMethod method = null;
		for (final String methodName : methodNames) {
			final StringBuilder methodArgs = new StringBuilder();

			/*
			 * if (METHOD_EQUALS.equals(methodName)) {
			 * methodArgs.append((globalSettings.isFinalModifierForMethodArgs()
			 * ? "final Object " : "Object ") +
			 * createDefaultInstance(type.getElementName())); }
			 */
			final StringBuilder methodComments = new StringBuilder("/**" + NEWLINE);
			methodComments.append(SPACE + ASTERISK + NEWLINE);
			methodComments.append(SPACE + "* This is " + (isConstructor ? "Constructor" : methodName) + " for " + type.getElementName()
					+ NEWLINE);

			if (fields != null) {
				int count = 0;
				for (final IField field : fields) {
					final String fieldName = field.getElementName();
					fastCodeFields.add(new FastCodeField(field, fieldName));
					if (isConstructor) {
						methodArgs.append(getSignatureSimpleName(field.getTypeSignature()) + SPACE + fieldName);
						methodArgs.append(count < fields.length - 1 ? COMMA + SPACE : EMPTY_STR);
						methodComments.append(SPACE + "* @param" + SPACE + fieldName + NEWLINE);
						count++;
					}
				}
			}
			methodComments.append(SPACE + "*/");

			placeHolders.put(METHOD_ARGS_STR, methodArgs.toString());
			placeHolders.put(METHOD_NAME_STR, methodName);
			placeHolders.put(METHOD_COMMENTS_STR, methodComments.toString());

			if (!isConstructor) {
				placeHolders.put(METHOD_ANNOTATIONS_STR, "@Override");
			}

			placeHolders.put(METHOD_MODIFIER_STR, MODIFIER_PUBLIC);

			/*
			 * if (METHOD_EQUALS.equals(methodName)) {
			 * placeHolders.put(METHOD_RETURN_TYPE_STR, "boolean"); } else if
			 * (METHOD_HASHCODE.equals(methodName)) {
			 * placeHolders.put(METHOD_RETURN_TYPE_STR, "int"); } else if
			 * (METHOD_TOSTRING.equals(methodName)) {
			 * placeHolders.put(METHOD_RETURN_TYPE_STR, "String"); }
			 */
			placeHoldersBody.put("fields", fastCodeFields);
			String methodBody = null;
			String templateType = null;
			final String templatePrefix = P_COMMON_TEMPLATE_PREFIX;

			if (isConstructor) {
				templateType = templatePrefix + UNDERSCORE + "method.body.Constructor";
				methodBody = globalSettings.getConstructorBody();
				methodBody = resetLineWithTag(methodBody, METHOD_ANNOTATIONS_STR);
			} /*
				* else if (METHOD_EQUALS.equals(methodName)) { templateType =
				* templatePrefix + UNDERSCORE + "method.body." + methodName;
				* methodBody = globalSettings.getEqualsMethodBody(); } else if
				* (METHOD_HASHCODE.equals(methodName)) { templateType =
				* templatePrefix + UNDERSCORE + "method.body." + methodName;
				* methodBody = globalSettings.getHashcodeMethodBody(); } else if
				* (METHOD_TOSTRING.equals(methodName)) { // templateType = TEMPLATE
				* + UNDERSCORE + "method.body." + // methodName; templatePrefix =
				* TEMPLATE; templateType = TEMPLATE_TYPE_CREATE_NEW_TOSTRING;
				* methodBody = globalSettings.getToStringMethodBody(); }
				*/
			final TemplateSettings templateSettings = TemplateSettings.getTemplateSettings(templateType, templatePrefix);

			if (!isEmpty(templateSettings.getTemplateVariationField())) {
				final String[] templateVariations = getTemplateVariationsFromUser(templateSettings);
				if (templateVariations != null && templateVariations.length > 0) {
					placeHolders.put(templateSettings.getTemplateVariationField(), templateVariations[0]);
				}
			}
			getGlobalSettings(placeHolders);
			methodBody = evaluateByVelocity(methodBody, placeHolders, placeHoldersBody);
			placeHolders.put(METHOD_BODY_STR, methodBody);

			// System.out.println("METHOD_BODY " +
			// placeHolders.get(METHOD_BODY_STR));

			// System.out.println("methodBody " + methodBody);
			IMethod sibling = null;
			boolean exist = false;

			if (isConstructor) {
				for (final IMethod meth : type.getMethods()) {
					if (meth.isConstructor()) {
						exist = true;
						if (fastCodeFields.size() != meth.getParameterTypes().length) {
							exist = false;
						}
						int count = 0;
						if (!exist) {
							continue;
						}
						for (final String parameterType : meth.getParameterTypes()) {
							if (!getSignatureSimpleName(parameterType).equals(fastCodeFields.get(count++).getType().getName())) {
								exist = false;
								break;
							}
						}
						if (exist) {
							return null;
						}
					} else if (sibling == null) { // gets the first method after
						// the last constructor
						sibling = meth;
					}
				}
			} else {
				method = type.getMethod(methodName, new String[0]);
				if (method != null && method.exists()) {
					return null;
				}
			}

			String methodSrc = globalSettings.getClassMethodBody();
			methodSrc = replacePlaceHolderWithBlank(methodSrc, "throws", METHOD_EXCEPTIONS_STR, LEFT_CURL);
			if (isConstructor) {
				methodSrc = resetLineWithTag(methodSrc, METHOD_ANNOTATIONS_STR);
				// placeHolders.put(METHOD_ANNOTATIONS_STR, EMPTY_STR);
				methodSrc = replacePlaceHolderWithBlank(methodSrc, EMPTY_STR, METHOD_RETURN_TYPE_STR, makePlaceHolder(methodName));
			}
			getGlobalSettings(placeHolders);
			methodSrc = evaluateByVelocity(methodSrc, placeHolders);
			method = type.createMethod(methodSrc, sibling, false, null);
			if (method == null || !method.exists()) {
				return null;
			}
		}
		return method;
	}

	/**
	 *
	 * @param methodName
	 * @param replacePart
	 * @param replaceValue
	 * @return
	 */
	private static String getReplaceMethodName(final String methodName, final String replacePart, final String replaceValue) {
		String newMethodName = null;
		final String plural = changeToPlural(replacePart);

		if (methodName.contains(plural)) {
			newMethodName = methodName.replaceAll(plural, changeToPlural(replaceValue));
		} else {
			newMethodName = methodName.replaceAll(replacePart, replaceValue);
		}

		return newMethodName;
	}

	/**
	 *
	 * @param fromType
	 * @param modTypeName
	 * @param toType
	 * @return
	 * @throws Exception
	 */
	public static IType findTypeForImport(final IType fromType, final String typeName, final IType toType) throws Exception {
		String importName = null;
		final String modTypeName = typeName.replaceAll("\\[[\\[ \\]]*\\]", EMPTY_STR); // remove

		if (isNativeType(modTypeName)) {
			return null;
		}
		if (fromType.getCompilationUnit() == null || !fromType.getCompilationUnit().exists()
				|| fromType.getCompilationUnit().getImports() == null) {
			return null;
		}

		for (final IImportDeclaration importDeclaration : fromType.getCompilationUnit().getImports()) {
			if (isStatic(importDeclaration.getFlags())) {
				continue;
			}
			importName = importDeclaration.getElementName();
			if (importName.endsWith(ASTERISK)) {
				importName = importName.replace(ASTERISK, modTypeName);
			}
			if (importName.endsWith(modTypeName)) {
				final IType imptType = toType.getCompilationUnit().getJavaProject().findType(importName);
				if (imptType != null && imptType.exists()) {
					return imptType;
				}
			}
		}
		// check if it is the same package as fromType
		final IType imptType = toType.getCompilationUnit().getJavaProject()
				.findType(fromType.getPackageFragment().getElementName() + DOT + typeName);
		if (imptType != null && imptType.exists()) {
			return imptType;
		}

		if (isJavaLangType(modTypeName, toType)) {
			return null;
		}
		// MessageDialog.openInformation(new Shell(), "Warning",
		// "Unable to find " + typeName +
		// " in the classpath, project build path may be incorrect.");
		throw new Exception("Unable to find import for " + modTypeName + " Please correct the errors and try again");
	}

	/**
	 *
	 * @param type
	 * @return
	 */
	public static boolean isNativeType(final String type) {
		for (final String nativeType : NATIVE_TYPES) {
			if (nativeType.equals(type)) {
				return true;
			}
		}
		return false;
	}

	/**
	 *
	 * @param type
	 * @param toType
	 * @return
	 */
	public static boolean isJavaLangType(final String type, final IType toType) {
		try {
			final IType typ = toType.getJavaProject().findType("java.lang." + type);
			return typ != null && typ.exists();
		} catch (final JavaModelException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	/**
	 *
	 * @param type
	 * @param paramType
	 * @param replacePart
	 * @param replaceValue
	 *
	 * @return
	 */
	public static IType convertTypeDifferentName(final IType type, final String paramType, final String replacePart,
			final String replaceValue) {
		IType retType = null;
		try {
			for (final IImportDeclaration importDeclaration : type.getCompilationUnit().getImports()) {
				final String fullImport = importDeclaration.getElementName();
				final int pos = fullImport.lastIndexOf(DOT_CHAR);
				if (pos < 0) {
					continue;
				}
				final String impClass = fullImport.substring(pos + 1);
				if (impClass.startsWith(paramType)) {
					final String newImpClss = fullImport.substring(0, pos) + DOT + impClass.replace(replacePart, replaceValue);
					retType = type.getCompilationUnit().getJavaProject().findType(newImpClss);
					break;
				}
			}
		} catch (final JavaModelException ex) {
			ex.printStackTrace();
			retType = null;
		}
		return retType;
	}

	/**
	 *
	 * @param typesToImport
	 * @return
	 * @throws Exception
	 * @throws Exception
	 */
	public static String createImportAsString(final List<IType> typesToImport) throws Exception {
		final StringBuilder importStr = new StringBuilder();
		boolean creatStaticImport = false;
		final int numStaticImportsMax = 1;

		if (typesToImport == null || typesToImport.isEmpty()) {
			return importStr.toString();
		}

		for (final IType typeToImport : typesToImport) {
			final IMember[] staticMembers = retriveStaticMembers(typeToImport);
			if (staticMembers != null && staticMembers.length > 0 && !creatStaticImport) {
				creatStaticImport = doStaticImport();
			}
			if (staticMembers != null && staticMembers.length > 0) {
				if (creatStaticImport) {
					if (staticMembers.length > numStaticImportsMax) {
						importStr.append("import static " + typeToImport.getFullyQualifiedName() + ".*;" + NEWLINE);
					} else {
						for (final IMember member : staticMembers) {
							importStr.append("import static " + typeToImport.getFullyQualifiedName() + DOT + member.getElementName() + ";"
									+ NEWLINE);
						}
					}
				} else {
					importStr.append("import " + typeToImport.getFullyQualifiedName() + ";" + NEWLINE);
				}
			} else {
				importStr.append("import " + typeToImport.getFullyQualifiedName() + ";" + NEWLINE);
			}
		}
		return importStr.toString();
	}

	/**
	 *
	 * @param javaProject
	 * @param imports
	 * @param classType
	 *
	 * @param typesToImport
	 * @return
	 * @throws Exception
	 */
	public static List<IType> gatherImports(final IJavaProject javaProject, final String[] imports, final int classType,
			final String action, final List<IType> typesToImport) throws Exception {
		final List<IType> impTypes = new ArrayList<IType>();
		boolean showChoice = true;
		final Shell parentShell = MessageUtil.getParentShell();
		final Shell shell = parentShell == null ? new Shell() : parentShell;

		if (imports == null) {
			return impTypes;
		}
		for (final String imp1 : imports) {
			final String imp = imp1.trim();

			if (imp.endsWith(ASTERISK)) {

				final int indx = imp.lastIndexOf(DOT_CHAR);
				String clssName = null;

				if (indx > 0 && imp.length() > indx) {
					clssName = imp.substring(indx + 1);

					final String pkg = imp.substring(0, indx);
					// javaProject.
					final IPackageFragment packageFragment = getPackageFragmentFromWorkspace(pkg);
					if (packageFragment != null) {
						final Pattern pattern = Pattern.compile(clssName.replace(ASTERISK, ".*"));
						for (final ICompilationUnit unit : packageFragment.getCompilationUnits()) {
							final Matcher matcher = pattern.matcher(unit.getElementName());
							if (matcher.matches()) {
								// Make sure it is in project classpath.
								final IType tmpType = javaProject.findType(unit.findPrimaryType().getFullyQualifiedName());
								if (tmpType != null && tmpType.exists()) {
									impTypes.add(unit.findPrimaryType());
								}
							}
						}
					}
				} else {
					final SelectionDialog typeDialog = JavaUI.createTypeDialog(shell, null, SearchEngine.createWorkspaceScope(),
							IJavaElementSearchConstants.CONSIDER_CLASSES_AND_INTERFACES, true, imp);
					final int ret = typeDialog.open();
					final Object[] result = typeDialog.getResult();
					if (ret != CANCEL) {
						for (final Object type1 : result) {
							impTypes.add((IType) type1);
						}
					}
					showChoice = false;
				}
			} else {
				// final FastCodeType fastCodeType = parseType(imp,
				// javaProject.findType(imp).getCompilationUnit());
				final IType type = javaProject.findType(imp);
				if (type != null && type.exists()) {
					impTypes.add(type);
					// showChoice = false;
				}
				/*for (final FastCodeType ftype : fastCodeType.getParameters()) {
					final IType type2 = javaProject.findType(ftype.getName());
					if (type2 != null && type2.exists()) {
						impTypes.add(type2);
					}
				}*/
			}
		}

		if (!impTypes.isEmpty() && showChoice) {
			final IType[] impArray = impTypes.toArray(new IType[impTypes.size()]);
			// IType[] impArray = new IType[impTypes.size()];
			// int count = 0;
			// for (IType iType : impTypes) {
			// impArray[count++] = iType;
			// }
			final ClassSelectionDialog classSelectionDialog = new ClassSelectionDialog(shell, action, "Choose the classes to " + action,
					impArray, true);
			final int ret = classSelectionDialog.open();
			if (ret == CANCEL) {
				impTypes.clear();
				return impTypes;
			} else {
				final Object[] results = classSelectionDialog.getResult();
				impTypes.clear();
				if (results != null) {
					for (final Object result : results) {
						impTypes.add((IType) result);
						typesToImport.add((IType) result);
					}
				}
			}
		} else {
			typesToImport.addAll(impTypes);
		}

		return impTypes;
	}

	/**
	 * @param compilationUnit
	 * @throws CoreException
	 */
	public static boolean checkForErrors(final IResource resource) throws CoreException {
		final int depth = IResource.DEPTH_INFINITE;

		if (resource == null) {
			return false;
		}
		final IMarker[] markers = resource.findMarkers(IMarker.PROBLEM, true, depth);
		if (markers == null) {
			return true;
		}
		for (final IMarker marker : markers) {
			if (marker.getType().equalsIgnoreCase("Java")) {
				continue;
			}
			final Object attr = marker.getAttribute(IMarker.SEVERITY);
			if (attr != null && attr.equals(IMarker.SEVERITY_ERROR)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param compilationUnit
	 * @param annotationsArray
	 * @param placeHolders
	 * @return
	 * @throws Exception
	 */
	public static String createAnnotations(final FastCodeContext fastCodeContext, final ICompilationUnit compilationUnit,
			final IJavaProject javaProject, final String[] annotationsArray, final Map<String, Object> placeHolders) throws Exception {
		final GlobalSettings globalSettings = getInstance();

		if (annotationsArray == null || annotationsArray.length == 0) {
			return EMPTY_STR;
		}

		final StringBuilder annotations = new StringBuilder();

		for (final String annot : annotationsArray) {
			if (annot.trim().equals(EMPTY_STR)) {
				continue;
			}
			final int off = annot.indexOf(LEFT_PAREN);
			String annotName = annot;
			if (off != -1) {
				annotName = annot.substring(0, off);
			}

			if (annotName.startsWith("@")) {
				annotName = annotName.substring(1);
			}
			String annotation = annot.trim();
			if (globalSettings.getAnnotationTypesMap().containsKey(annotName)) {
				final String fullAnotTypeName = globalSettings.getAnnotationTypesMap().get(annotName);
				final IType fullAnotType = javaProject.findType(fullAnotTypeName);
				if (fullAnotType != null && fullAnotType.exists()) {
					if (compilationUnit != null) {
						final IImportDeclaration importDeclaration = compilationUnit.getImport(fullAnotTypeName);
						if (importDeclaration == null || !importDeclaration.exists()) {
							compilationUnit.createImport(fullAnotTypeName, null, null);
						}
					} else if (fastCodeContext != null) {
						String imp = "import " + fullAnotTypeName + ";";
						if (fastCodeContext.getPlaceHolders().containsKey(CLASS_IMPORTS_STR)) {
							imp = fastCodeContext.getPlaceHolders().get(CLASS_IMPORTS_STR) + NEWLINE + imp;
						}
						fastCodeContext.addToPlaceHolders(CLASS_IMPORTS_STR, imp);
					}
				}
			} else {
				final IType fullAnotType = javaProject.findType(annotName);
				if (fullAnotType != null && fullAnotType.exists()) {
					annotation = fullAnotType.getElementName();
				}
			}
			getGlobalSettings(placeHolders);
			annotation = evaluateByVelocity(annotation, placeHolders);
			if (!annotation.startsWith("@")) {
				annotation = "@" + annotation;
			}
			annotations.append(evaluateByVelocity(annotation, placeHolders) + NEWLINE);

		}
		return annotations.toString().trim();
	}

	/**
	 *
	 * @author Gautam
	 * @param fromType
	 * @param toType
	 * @throws JavaModelException
	 */
	public static void copyFields(final IType fromType, final IType toType, final IField[] members,
			final CreateSimilarDescriptor createSimilarDescriptor, final CreateSimilarDescriptorClass createSimilarDescriptorClass,
			final IProgressMonitor monitor) throws Exception {
		final IField selectedField = null;
		// if (member != null && !(member instanceof IField)) {
		// throw new Exception("Selected member is not a field.");
		// }

		// selectedField = (IField) member;

		boolean firstField = true;
		for (final IField field : fromType.getFields()) {
			if (Flags.isStatic(field.getFlags())) {
				continue;
			}
			// if (selectedField != null && !selectedField.equals(field)) {
			// continue;
			// }
			if (!doesMatch(field.getElementName(), createSimilarDescriptor.getIncludePattern(), createSimilarDescriptor.getExcludePattern())) {
				continue;
			}

			String fieldSrc = field.getSource();

			final String comment = firstField ? "/* start fields from " + fromType.getFullyQualifiedName() + " */\n" : EMPTY_STR;
			fieldSrc = firstField ? comment + fieldSrc : fieldSrc;

			final String fieldType = getSignatureSimpleName(field.getTypeSignature());
			if (createSimilarDescriptor.isBreakDateFields() && fieldType.endsWith("Date")) {
				createDependentFieldFromDate(field, toType, "Day", comment, monitor);
				createDependentFieldFromDate(field, toType, "Month", null, monitor);
				createDependentFieldFromDate(field, toType, "Year", null, monitor);
			} else {
				final IField toField = toType.getField(field.getElementName());
				if (toField == null || !toField.exists()) {
					fastCodeConsole.writeToConsole("Creating field " + field.getElementName());
					toType.createField(fieldSrc, null, false, monitor);

					String methodBody = createJavadoc(GET_METHOD_JAVADOC_FORMAT, field.getElementName());
					methodBody += createFullMethodDefn(GET_METHOD_FORMAT, getSignatureSimpleName(field.getTypeSignature()),
							field.getElementName());

					toType.createMethod(methodBody, null, false, monitor);
					methodBody = createJavadoc(SET_METHOD_JAVADOC_FORMAT, field.getElementName());
					methodBody += createFullMethodDefn(SET_METHOD_FORMAT, getSignatureSimpleName(field.getTypeSignature()),
							field.getElementName());
					toType.createMethod(methodBody, null, false, monitor);
				} else {
					fastCodeConsole.writeToConsole("Field " + field.getElementName() + " exists already.");
				}
			}
			firstField = false;
		}
	}

	/**
	 *
	 * @param type
	 * @param fieldName
	 * @param fieldType
	 * @param getter_setter
	 * @throws Exception
	 */
	public static void createGetterSetters(final IType type, final IField field, final IType[] paramTypes, final GETTER_SETTER getter_setter)
			throws Exception {
		String methodBody = EMPTY_STR;
		String methodSrc = EMPTY_STR;

		final String typeSignature = Signature.getSignatureSimpleName(field.getTypeSignature());
		final boolean isFieldBoolean = "boolean".equalsIgnoreCase(typeSignature);

		boolean createGetter = false;
		boolean createSetter = false;
		boolean createAdder = false;

		if (getter_setter == GETTER_SETTER.GETTER_ADDER_EXIST) {
			createGetter = createAdder = true;
		} else if (getter_setter == GETTER_SETTER.GETTER_SETTER_EXIST) {
			createGetter = createSetter = true;
		} else if (getter_setter == GETTER_SETTER.GETTER_EXIST) {
			createGetter = true;
		}

		final List<Pair<String, String>> methArgs = new ArrayList<Pair<String, String>>();
		if (createGetter) {
			methodSrc = createMethodSource((isFieldBoolean ? "is" : "get") + createEmbeddedInstance(field.getElementName()), "return this."
					+ field.getElementName() + ";", "getter method for " + field.getElementName(), typeSignature, null, true, true);
			type.createMethod(methodSrc, null, false, null);
		}
		if (createSetter) {
			methArgs.add(new Pair<String, String>(typeSignature, field.getElementName()));
			methodSrc = createMethodSource("set" + createEmbeddedInstance(field.getElementName()), "this." + field.getElementName() + " = "
					+ field.getElementName() + ";", "setter method for " + field.getElementName(), METHOD_RETURN_TYPE_VOID, methArgs, true,
					true);
			type.createMethod(methodSrc, null, false, null);
		}
		if (createAdder) {
			methodBody = EMPTY_STR;
			final FastCodeType fastCodeType = parseType(typeSignature, field.getCompilationUnit());
			if (paramTypes == null || paramTypes.length == 0) {
				if ("List".equals(fastCodeType.getName())) {
					methodBody = "this." + field.getElementName() + ".add(item);";
					methArgs.add(new Pair<String, String>("Object", "item"));
				} else if ("Set".equals(fastCodeType.getName())) {
					methodBody = "this." + field.getElementName() + ".add(item);";
					methArgs.add(new Pair<String, String>("Object", "item"));
				} else if ("Map".equals(fastCodeType.getName())) {
					methodBody = "this." + field.getElementName() + ".put(key, value);";
					methArgs.add(new Pair<String, String>("Object", "key"));
					methArgs.add(new Pair<String, String>("Object", "value"));
				}
			} else {
				final String param0 = paramTypes[0].getElementName();
				if ("List".equals(fastCodeType.getName())) {
					methArgs.add(new Pair<String, String>(param0, createDefaultInstance(param0)));
					methodBody = "this." + field.getElementName() + ".add(" + createDefaultInstance(param0) + ");";
				} else if ("Set".equals(fastCodeType.getName())) {
					methArgs.add(new Pair<String, String>(param0, createDefaultInstance(param0)));
					methodBody = "this." + field.getElementName() + ".add(" + createDefaultInstance(param0) + ");";
				} else if ("Map".equals(fastCodeType.getName())) {
					if (paramTypes.length > 1) {
						methArgs.add(new Pair<String, String>(param0, createDefaultInstance(param0)));
						final String param1 = paramTypes[1].getElementName();
						final String arg1 = param1.equals(param0) ? createDefaultInstance(param1) + "2" : createDefaultInstance(param1);
						methArgs.add(new Pair<String, String>(param1, arg1));
						methodBody = "this." + field.getElementName() + ".put(" + createDefaultInstance(param1) + ","
								+ createDefaultInstance(param1) + ");";
					} else {
						methodBody = "this." + field.getElementName() + ".put(key, value);";
						methArgs.add(new Pair<String, String>("Object", "key"));
						methArgs.add(new Pair<String, String>("Object", "value"));
					}
				}
			}
			methodSrc = createMethodSource("add" + createEmbeddedInstance(field.getElementName()), methodBody,
					"add method for " + field.getElementName(), METHOD_RETURN_TYPE_VOID, methArgs, true, true);
			type.createMethod(methodSrc, null, false, null);
		}
	}

	/**
	 *
	 * @param type
	 * @param fieldName
	 * @param fieldType
	 * @param getter_setter
	 * @return
	 * @throws Exception
	 */
	public static List<FastCodeMethod> createGetterSetters(final IType type, final IField field, final CreateVariableData createVariableData)
			throws Exception {
		/*
		 * public static void createGetterSetters(final IType type, final IField
		 * field, final IType[] paramTypes, final GETTER_SETTER getter_setter)
		 * throws Exception {
		 */
		String methodBody = EMPTY_STR;
		String methodSrc = EMPTY_STR;

		final String typeSignature = Signature.getSignatureSimpleName(field.getTypeSignature());
		final boolean isFieldBoolean = "boolean".equalsIgnoreCase(typeSignature);

		boolean createGetter = false;
		boolean createSetter = false;
		boolean createAdder = false;

		boolean multiLine = true;
		boolean needComment = true;
		final List<FastCodeMethod> methodsCreated = new ArrayList<FastCodeMethod>();

		if (createVariableData.getGetterSetter() == GETTER_SETTER.GETTER_ADDER_EXIST) { // if
																						// (getter_setter
																						// ==
																						// GETTER_SETTER.GETTER_ADDER_EXIST)
																						// {
																						// //
			createGetter = createAdder = true;
		} else if (createVariableData.getGetterSetter() == GETTER_SETTER.GETTER_SETTER_EXIST) { // else
																								// if
																								// (getter_setter
																								// ==
																								// GETTER_SETTER.GETTER_SETTER_EXIST)
																								// {
																								// //
			createGetter = createSetter = true;
		} else if (createVariableData.getGetterSetter() == GETTER_SETTER.GETTER_EXIST) { // (getter_setter
																							// ==
																							// GETTER_SETTER.GETTER_EXIST)
																							// {
																							// //
			createGetter = true;
		}
		if (createVariableData.getGetterSetterFormat() == GETTER_SETTER_FORMAT.MULTILINE_WITH_COMMENT) {
			multiLine = needComment = true;
		} else if (createVariableData.getGetterSetterFormat() == GETTER_SETTER_FORMAT.MULTILINE_WITHOUT_COMMENT) {
			multiLine = true;
			needComment = false;
		} else if (createVariableData.getGetterSetterFormat() == GETTER_SETTER_FORMAT.SINGLE_LINE) {
			multiLine = needComment = false;
		}

		String argsPrefix = EMPTY_STR;
		String fieldNameWithPrefix = EMPTY_STR;
		if (!isEmpty(createVariableData.getSetterVerPrefix())) {
			argsPrefix = createVariableData.getSetterVerPrefix().toLowerCase();
			fieldNameWithPrefix = argsPrefix + changeFirstLetterToUpperCase(field.getElementName()); // field.getElementName().substring(0,
																										// 1).toUpperCase().concat(field.getElementName().substring(1)));
		} else {
			fieldNameWithPrefix = field.getElementName();
		}

		final List<Pair<String, String>> methArgs = new ArrayList<Pair<String, String>>();
		final String[] methArg = { "QBuilder;" };
		IMethod meth = type.getMethod(type.getElementName(), methArg);
		if (!meth.exists()) {
			meth = null;
		}

		final IPreferenceStore preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		IMethod getterSibling;
		IMethod setterSibling;
		getterSibling = setterSibling = meth;
		final String[] setteArg = new String[1];
		if (preferenceStore.getString(P_GETTER_SETTER_POSITION) != null
				&& preferenceStore.getString(P_GETTER_SETTER_POSITION).equals("getterfirst")) {
			int k = 0;
			while ((getterSibling == null || !getterSibling.exists()) && type.getFields().length > k) {
				setteArg[0] = type.getFields()[k].getTypeSignature();
				getterSibling = type.getMethod("set" + createEmbeddedInstance(type.getFields()[k].getElementName()), setteArg);
				k++;
			}

		}
		if (createVariableData.isVariableModifyAction() && createVariableData.getInsertionPoint() != null) {
			final String nextMethod = "get" + createEmbeddedInstance(createVariableData.getInsertionPoint().getElementName());
			getterSibling = type.getMethod(nextMethod, null);
			if (preferenceStore.getString(P_GETTER_SETTER_POSITION) != null
					&& preferenceStore.getString(P_GETTER_SETTER_POSITION).equals("getterfirst")) {
				setteArg[0] = createVariableData.getInsertionPoint().getTypeSignature();
				setterSibling = type.getMethod("set" + createEmbeddedInstance(createVariableData.getInsertionPoint().getElementName()),
						setteArg);
			}

		}
		if (getterSibling != null && !getterSibling.exists()) {
			getterSibling = null;
		}
		if (setterSibling != null && !setterSibling.exists()) {
			setterSibling = null;
		}
		if (createGetter) {
			methodSrc = createMethodSource((isFieldBoolean ? "is" : "get") + createEmbeddedInstance(field.getElementName()), "return this."
					+ field.getElementName() + ";", "getter method for " + field.getElementName(), typeSignature, null, needComment,
					multiLine);
			/*
			 * if(element!=null && element.exists()){
			 * type.createMethod(methodSrc, element, false, null); }else{
			 */
			/*
			 * final IJavaElement sibling =null;
			 * if(createVariableData.isBuilderReqd()){ sibling= }
			 */
			final IMethod meth1 = type.createMethod(methodSrc, getterSibling, false, null);
			methodsCreated.add(new FastCodeMethod(meth1));
			// }
		}
		if (createSetter) {
			methArgs.add(new Pair<String, String>(typeSignature, fieldNameWithPrefix)); // methArgs.add(new
																						// Pair<String,
																						// String>(typeSignature,
																						// field.getElementName()));
																						// //
			methodSrc = createMethodSource("set" + createEmbeddedInstance(field.getElementName()), "this." + field.getElementName() + " = "
					+ fieldNameWithPrefix + ";", "setter method for " + field.getElementName(), METHOD_RETURN_TYPE_VOID, methArgs,
					needComment, multiLine);
			/*
			 * methodSrc = createMethodSource("set" +
			 * createEmbeddedInstance(field.getElementName()), "this." +
			 * field.getElementName() + " = " + field.getElementName() + ";",
			 * "setter method for " + field.getElementName(),
			 * METHOD_RETURN_TYPE_VOID, methArgs);
			 */
			/*
			 * if(element!=null && element.exists()){
			 * type.createMethod(methodSrc, element, false, null); }else{
			 */
			final IMethod meth1 = type.createMethod(methodSrc, setterSibling, false, null);
			methodsCreated.add(new FastCodeMethod(meth1));
			// }
		}
		if (createAdder) {
			methodBody = EMPTY_STR;
			final FastCodeType fastCodeType = parseType(typeSignature, field.getCompilationUnit());
			if (createVariableData.getFieldParams() == null || createVariableData.getFieldParams().length == 0) {// if
																													// (paramTypes
																													// ==
																													// null
																													// ||
																													// paramTypes.length
																													// ==
																													// 0)
																													// {
																													// //
				if ("List".equals(fastCodeType.getName())) {
					methodBody = "this." + field.getElementName() + ".add(item);";
					methArgs.add(new Pair<String, String>("Object", "item"));
				} else if ("Set".equals(fastCodeType.getName())) {
					methodBody = "this." + field.getElementName() + ".add(item);";
					methArgs.add(new Pair<String, String>("Object", "item"));
				} else if ("Map".equals(fastCodeType.getName())) {
					methodBody = "this." + field.getElementName() + ".put(key, value);";
					methArgs.add(new Pair<String, String>("Object", "key"));
					methArgs.add(new Pair<String, String>("Object", "value"));
				}
			} else {
				// final String param0 = paramTypes[0].getElementName();
				final String param0 = createVariableData.getFieldParams()[0];
				String param0Inst = EMPTY_STR;
				if (!isEmpty(argsPrefix)) {
					param0Inst = argsPrefix + changeFirstLetterToUpperCase(createDefaultInstance(param0));
				} else {
					param0Inst = createDefaultInstance(param0);
				}
				if ("List".equals(fastCodeType.getName())) {
					methArgs.add(new Pair<String, String>(param0, param0Inst));
					methodBody = "this." + field.getElementName() + ".add(" + param0Inst + ");";
				} else if ("Set".equals(fastCodeType.getName())) {
					methArgs.add(new Pair<String, String>(param0, param0Inst));
					methodBody = "this." + field.getElementName() + ".add(" + param0Inst + ");";
				} else if ("Map".equals(fastCodeType.getName())) {
					if (createVariableData.getFieldParams().length > 1) { // if
																			// (paramTypes.length
																			// >
																			// 1)
																			// {
																			// //
						methArgs.add(new Pair<String, String>(param0, param0Inst));
						// final String param1 = paramTypes[1].getElementName();
						final String param1 = createVariableData.getFieldParams()[1];
						String param1Inst = EMPTY_STR;
						if (!isEmpty(argsPrefix)) {
							param1Inst = argsPrefix + changeFirstLetterToUpperCase(createDefaultInstance(param1));
						} else {
							param1Inst = createDefaultInstance(param1);
						}
						final String arg1 = param1.equals(param0) ? param1Inst + "2" : param1Inst;
						methArgs.add(new Pair<String, String>(param1, arg1));
						methodBody = "this." + field.getElementName() + ".put(" + param0Inst + COMMA + param1Inst + ");";
					} else {
						methodBody = "this." + field.getElementName() + ".put(key, value);";
						methArgs.add(new Pair<String, String>("Object", "key"));
						methArgs.add(new Pair<String, String>("Object", "value"));
					}
				}
			}
			methodSrc = createMethodSource("add" + createEmbeddedInstance(field.getElementName()), methodBody,
					"add method for " + field.getElementName(), METHOD_RETURN_TYPE_VOID, methArgs, needComment, multiLine);
			/*
			 * if(element!=null && element.exists()){
			 * type.createMethod(methodSrc,element,false,null); }else{
			 */
			final IMethod meth1 = type.createMethod(methodSrc, meth, false, null);
			methodsCreated.add(new FastCodeMethod(meth1));
			// }
		}

		return methodsCreated;
	}

	/**
	 *
	 * @param methodName
	 * @param methodBody
	 * @param retType
	 * @param methodArgs
	 * @return
	 * @throws Exception
	 */
	public static String createMethodSource(final String methodName, final String methodBody, final String methComment,
			final String retType, final List<Pair<String, String>> methodParms, final boolean needComment, final boolean multiline)
			throws Exception {
		final GlobalSettings globalSettings = getInstance();
		final StringBuilder methodArgs = new StringBuilder();
		int count = 0;
		for (final Pair<String, String> methodParm : getEmptyListForNull(methodParms)) {
			methodArgs.append((globalSettings.isFinalModifierForMethodArgs() ? "final " : EMPTY_STR) + methodParm.getLeft() + SPACE
					+ methodParm.getRight());
			methodArgs.append(count < methodParms.size() - 1 ? COMMA + SPACE : EMPTY_STR);
			count++;
		}
		methodArgs.trimToSize();
		final Map<String, Object> placeHolders = new HashMap<String, Object>();
		placeHolders.put(METHOD_NAME_STR, methodName);
		placeHolders.put(METHOD_MODIFIER_STR, MODIFIER_PUBLIC);
		placeHolders.put(METHOD_RETURN_TYPE_STR, retType);
		placeHolders.put(METHOD_ARGS_STR, methodArgs.toString());

		placeHolders.put(METHOD_COMMENTS_STR, needComment ? generateMethodComments(methComment, retType, methodParms) : EMPTY_STR);

		String fullMethodPattern = globalSettings.getClassMethodBody();
		fullMethodPattern = replacePlaceHolderWithBlank(fullMethodPattern, "throws", METHOD_EXCEPTIONS_STR, LEFT_CURL);
		placeHolders.put(METHOD_BODY_STR, methodBody);
		fullMethodPattern = resetLineWithTag(fullMethodPattern, METHOD_ANNOTATIONS_STR);
		getGlobalSettings(placeHolders);
		String methSnippet = evaluateByVelocity(fullMethodPattern, placeHolders);
		if (!multiline) {
			methSnippet = methSnippet.replaceAll("\n|\r", EMPTY_STR);
			methSnippet = methSnippet.replaceAll("\\s{2,100}", SPACE);
			methSnippet = methSnippet.replaceAll("\\{\\s+", "\\{");
			methSnippet = methSnippet.replaceAll("\\s+\\}", "\\}");
		}
		return methSnippet;
	}

	/**
	 *
	 * @param field
	 * @return
	 * @throws Exception
	 */
	public static GETTER_SETTER doesGetterSetterExist(final IField field) throws Exception {

		final IType type = field.getDeclaringType();
		String methodName = EMPTY_STR;
		if (Signature.getSignatureSimpleName(field.getTypeSignature()).equalsIgnoreCase("boolean")) {
			methodName = "is" + createEmbeddedInstance(field.getElementName());
		} else {
			methodName = "get" + createEmbeddedInstance(field.getElementName());
		}
		final IMethod getMethod = type.getMethod(methodName, null);
		methodName = "set" + createEmbeddedInstance(field.getElementName());
		final String[] parmTypes = { field.getTypeSignature() };
		final IMethod setMethod = type.getMethod(methodName, parmTypes);

		final boolean getMethodExist = getMethod != null && getMethod.exists();
		final boolean setMethodExist = setMethod != null && setMethod.exists();

		if (getMethodExist && setMethodExist) {
			return GETTER_SETTER.GETTER_SETTER_EXIST;
		} else if (getMethodExist) {
			return GETTER_SETTER.GETTER_EXIST;
		}
		return GETTER_SETTER.NONE;
	}

	/**
	 *
	 * @param type
	 * @param field
	 * @return
	 * @throws Exception
	 */
	public static GETTER_SETTER doesGetterSetterExist(final IType type, final String field) throws Exception {
		final IField fld = type.getField(field);
		if (fld == null || !fld.exists()) {
			return null;
		}
		return doesGetterSetterExist(fld);
	}

	/**
	 *
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public static IType findSuperInterfaceType(final IType type) throws Exception {
		final ITypeHierarchy hierarchy = type.newSupertypeHierarchy(null);
		final String[] interfaces = type.getSuperInterfaceNames();

		if (interfaces == null || interfaces.length == 0) {
			return null;
		}

		for (final IType interfaceType : hierarchy.getAllSuperInterfaces(type)) {
			if (!interfaceType.isBinary() && interfaceType.getElementName().equals(interfaces[0])) {
				return interfaceType;
			}
		}
		return null;
	}

	/**
	 *
	 * @param name
	 * @param includePattern
	 * @param excludePattern
	 * @return
	 */
	public static boolean doesMatch(final String name, final String includePattern, final String excludePattern) {
		if (isEmpty(includePattern) && isEmpty(excludePattern)) {
			return true;
		}

		// Check if it can be included
		if (!isEmpty(includePattern) && !StringUtil.doesMatchPatterns(name, includePattern)) {
			return false;
		}

		// Check if it has to be excluded
		return isEmpty(excludePattern) ? true : !StringUtil.doesMatchPatterns(name, excludePattern);
	}

	/**
	 * Creates field initializers of the form : List<E> myList = new
	 * ArrayList<E>();
	 *
	 * @param pair
	 *            (the interface implementation pair.)
	 * @param paramTypes
	 * @param flags
	 */
	public static String createFieldSourceWithParameter(final Pair<IType, IType> pair, final IType[] paramTypes, String fieldName,
			final GETTER_SETTER getter_setter, final int flags) {

		final String fieldDeclaration = pair.getLeft().getElementName();
		final String fieldInitilizer = pair.getRight().getElementName();

		String fieldSrc = EMPTY_STR;
		if (Flags.isPrivate(flags)) {
			fieldSrc = "private ";
		} else if (Flags.isProtected(flags)) {
			fieldSrc = "protected ";
		}

		if (getter_setter == GETTER_SETTER.GETTER_ADDER_EXIST || getter_setter == GETTER_SETTER.GETTER_EXIST) {
			final GlobalSettings globalSettings = GlobalSettings.getInstance();
			if (globalSettings.isFinalModifierForMethodArgs()) {
				fieldSrc += "final ";
			}
		}

		if (isEmpty(fieldName)) {
			fieldName = MY_NEW + pair.getLeft().getElementName();
		}

		if (paramTypes == null || paramTypes.length == 0) {
			fieldSrc += fieldDeclaration + " " + fieldName + " = new " + fieldInitilizer + "();";
		} else {
			int cnt = 0;
			final StringBuilder fieldParamDeclaration = new StringBuilder();

			for (final IType paramType : paramTypes) {
				fieldParamDeclaration.append(paramType.getElementName());
				fieldParamDeclaration.append(cnt < paramTypes.length - 1 ? COMMA + SPACE : EMPTY_STR);
				cnt++;
			}
			fieldSrc += fieldDeclaration + "<" + fieldParamDeclaration.toString() + "> " + fieldName + " = new " + fieldInitilizer + "<"
					+ fieldParamDeclaration.toString() + ">" + "();";
		}
		return fieldSrc;
	}

	/**
	 *
	 * @author Gautam
	 * @param field
	 * @param toType
	 * @param suffix
	 * @throws JavaModelException
	 */
	private static IField createDependentFieldFromDate(final IField field, final IType toType, final String suffix, final String comment,
			final IProgressMonitor monitor) throws JavaModelException {
		String fieldSrc = field.getSource();
		final String fieldType = getSignatureSimpleName(field.getTypeSignature());
		fieldSrc = fieldSrc.replaceFirst(field.getElementName(), field.getElementName() + suffix);
		fieldSrc = fieldSrc.replaceFirst(fieldType, "int");
		if (!isEmpty(comment)) {
			fieldSrc = comment + fieldSrc;
		}
		IField newField = toType.getField(field.getElementName() + suffix);
		if (newField == null || !newField.exists()) {
			newField = toType.createField(fieldSrc, null, false, monitor);
			String methodBody = createFullMethodDefn(GET_METHOD_FORMAT, getSignatureSimpleName(newField.getTypeSignature()),
					newField.getElementName());
			toType.createMethod(methodBody, null, false, monitor);
			methodBody = createFullMethodDefn(SET_METHOD_FORMAT, getSignatureSimpleName(newField.getTypeSignature()),
					newField.getElementName());
			toType.createMethod(methodBody, null, false, monitor);
		}
		return newField;
	}

	/**
	 * @param type
	 * @param members
	 * @param title
	 *            TODO
	 * @param multipleSelection
	 *            TODO
	 * @return
	 */
	public static IMember[] getSelectedMembers(final int type, final IMember[] members, final String title, final boolean multipleSelection) {
		IMember[] membersToWorkOn = {};
		final int ret = 0;
		FastCodeSelectionDialog selectionDialog = null;
		String errorMessage = null;
		String message;
		final IPreferenceStore preferences = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);

		final Shell parentShell = MessageUtil.getParentShell();
		final Shell shell = parentShell == null ? new Shell() : parentShell;

		if (multipleSelection) {
			message = "Select one or more ";
		} else {
			message = "Select a ";
		}

		if (members != null && members.length > 0) {
			if (type == METHOD) {

				selectionDialog = new MethodSelectionDialog(shell, "Method Selection", message + "method(s) for " + title,
						(IMethod[]) members, multipleSelection);
			} else if (type == FIELD) {
				selectionDialog = new FieldSelectionDialog(shell, "Field Selection", message + "field(s) for " + title, (IField[]) members,
						multipleSelection);
			} else {
				return new IMember[0];
			}
			if (selectionDialog.open() == CANCEL) {
				errorMessage = "You pressed cancel";
			}
		} else {
			errorMessage = "There were no methods/fields";
		}

		if (errorMessage != null) {
			errorMessage += " still want to coninue?";
			final MessageDialogWithToggle dialogWithToggle = openYesNoQuestion(shell, "Continue?", errorMessage, "Remember Decision",
					false, preferences, P_ASK_FOR_CONTINUE);
			return dialogWithToggle.getReturnCode() != MESSAGE_DIALOG_RETURN_YES ? null : membersToWorkOn;
		}

		if (type == METHOD) {
			membersToWorkOn = new IMethod[selectionDialog.getResult().length];
		} else if (type == FIELD) {
			membersToWorkOn = new IField[selectionDialog.getResult().length];
		}

		int count = 0;
		for (final Object member : selectionDialog.getResult()) {
			membersToWorkOn[count++] = (IMember) member;
		}
		return membersToWorkOn;
	}

	/**
	 * @param type
	 * @param selectionType
	 * @param members
	 * @param title
	 * @param multipleSelection
	 * @param fieldType
	 * @return
	 */
	public static FastCodeField[] getSelectedMembers(final IType type, final int selectionType, final FastCodeField[] members,
			final String title, final boolean multipleSelection, final String fieldType) {
		FastCodeField[] membersToWorkOn = {};
		final int ret = 0;
		FastCodeSelectionDialog selectionDialog = null;
		final String errorMessage = null;
		String message;
		final IPreferenceStore preferences = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);

		final Shell parentShell = MessageUtil.getParentShell();
		final Shell shell = parentShell == null ? new Shell() : parentShell;

		if (multipleSelection) {
			message = "Select one or more ";
		} else {
			message = "Select a ";
		}

		if (members != null && members.length > 0) {
			/*
			 * if (type == METHOD) {
			 *
			 * selectionDialog = new MethodSelectionDialog(shell,
			 * "Method Selection", message + "method(s) for " + title,
			 * (IMethod[]) members, multipleSelection); } else
			 */
			if (selectionType == FIELD) {
				selectionDialog = new FieldSelectionDialog(shell, makeWord(fieldType), message
						+ makeWord(fieldType).substring(0, makeWord(fieldType).length() - 1) + "(s) from " + type.getFullyQualifiedName()
						+ " for " + title, members, multipleSelection);
			} else {
				return new FastCodeField[0];
			}
			if (selectionDialog.open() == CANCEL) {
				return new FastCodeField[0];
			}

			membersToWorkOn = new FastCodeField[selectionDialog.getResult().length];

			int count = 0;
			for (final Object member : selectionDialog.getResult()) {
				membersToWorkOn[count++] = (FastCodeField) member;
			}
			return membersToWorkOn;
		}
		/*if (selectionDialog.open() == CANCEL) {
			errorMessage = "You pressed cancel";
		}
		} else {
		errorMessage = "There were no methods/fields";
		}

		if (errorMessage != null) {
		errorMessage += " still want to coninue?";
		final MessageDialogWithToggle dialogWithToggle = openYesNoQuestion(shell, "Continue?", errorMessage, "Remember Decision",
				false, preferences, P_ASK_FOR_CONTINUE);
		return dialogWithToggle.getReturnCode() != MESSAGE_DIALOG_RETURN_YES ? null : membersToWorkOn;
		}*/
		return new FastCodeField[0];

	}

	/**
	 * @author Gautam
	 * @param field
	 * @param fieldName
	 * @return
	 * @throws JavaModelException
	 */
	public static String createFieldSource(final IField field, final String fieldName) throws JavaModelException {
		final String typeName = getSignatureSimpleName(field.getTypeSignature());
		final String fieldSource = typeName + SPACE + field.getElementName() + " = null;\n";
		return fieldSource;
	}

	/**
	 * @param type
	 * @throws Exception
	 */
	public static IMethod[] getPublicMethods(final IType type) throws Exception {
		final List<IMethod> retMethods = new ArrayList<IMethod>();
		for (final IMethod method : type.getMethods()) {
			if (!type.isInterface()) {
				if (method.isConstructor() || !isPublic(method.getFlags()) || isStatic(method.getFlags())) {
					continue;
				}
			}
			retMethods.add(method);
		}
		return retMethods.toArray(new IMethod[0]);
	}

	/**
	 *
	 * @param type
	 * @param retFields
	 * @throws Exception
	 */
	public static void getFields(final IType type, final List<IField> retFields) throws Exception {
		for (final IField field : type.getFields()) {
			/*if (isStatic(field.getFlags())) {
				continue;
			}*/
			retFields.add(field);
		}
		final String superclass = type.getSuperclassName();
		if (superclass != null) {
			final IType supType = findTypeForImport(type, superclass, type);
			if (supType != null && supType.exists()) {
				getFields(supType, retFields);
			}
		}
	}

	/**
	 *
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public static IField[] getFieldsOfType(final IType type) throws Exception {
		final List<IField> retFields = new ArrayList<IField>();
		getFields(type, retFields);
		return retFields.toArray(new IField[0]);
	}

	/**
	 *
	 * @param packageName
	 * @return
	 * @throws JavaModelException
	 */
	public static IPackageFragment getPackageFragmentFromWorkspace(final String packageName) throws JavaModelException {
		final IProject[] javaProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (final IProject project : javaProjects) {
			final IJavaProject javaProj = JavaCore.create(project);
			if (javaProj == null || !javaProj.exists() || !javaProj.isOpen()) {
				continue;
			}
			for (final IPackageFragmentRoot packageFragmentRoot : javaProj.getAllPackageFragmentRoots()) {
				final IPackageFragment packageFragment = packageFragmentRoot.getPackageFragment(packageName);
				if (packageFragment != null && packageFragment.exists()) {
					return packageFragment;
				}
			}
		}
		return null;
	}

	/**
	 * @author Gautam
	 * @param srcPath
	 * @return
	 * @throws JavaModelException
	 */
	public static IPackageFragmentRoot getJavaElementFromSourcePathFromWorkspace(final String srcPath, final IJavaProject javaProject)
			throws JavaModelException {
		// Try to find it from the current project first

		IPackageFragmentRoot packageFragmentRoot = getJavaElementFromSourcePathFromProject(srcPath, javaProject, false);
		if (packageFragmentRoot != null) {
			return packageFragmentRoot;
		}

		// Try to find it from the workspace

		final IProject[] javaProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (final IProject project : javaProjects) {
			if (project == null || !project.exists() || !project.isOpen()) {
				continue;
			}

			final IJavaProject javaProj = JavaCore.create(project);
			if (javaProj == null || javaProject.exists() || !srcPath.startsWith("/" + javaProj.getElementName())) {
				continue;
			}

			packageFragmentRoot = getJavaElementFromSourcePathFromProject(srcPath, javaProj, true);
			if (packageFragmentRoot != null) {
				return packageFragmentRoot;
			}
		}
		return null;
	}

	/**
	 *
	 * @param projectName
	 * @param sourcePath
	 * @return
	 * @throws JavaModelException
	 */
	public static IPackageFragmentRoot getJavaElementFromSourcePathFromWorkspace(final String projectName, final String sourcePath)
			throws JavaModelException {
		final IProject[] javaProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (final IProject project : javaProjects) {
			if (project.getName().equals(projectName)) {
				final IJavaProject javaProj = JavaCore.create(project);
				if (!(javaProj == null || !javaProj.exists())) {
					return getJavaElementFromSourcePathFromProject(sourcePath, javaProj, true);
				}
			}
		}
		return null;
	}

	/**
	 *
	 * @param srcPath
	 * @param checkFullPath
	 *
	 * @return
	 * @throws JavaModelException
	 */
	public static IPackageFragmentRoot getJavaElementFromSourcePathFromProject(final String srcPath, final IJavaProject javaProject,
			final boolean checkFullPath) throws JavaModelException {
		for (final IPackageFragmentRoot packageFragmentRoot : javaProject.getAllPackageFragmentRoots()) {
			final String rootPath = packageFragmentRoot.getPath().toString();
			if (srcPath.equals(checkFullPath ? rootPath : rootPath.substring(javaProject.getElementName().length() + 1))) {
				return packageFragmentRoot;
			}
		}
		return null;
	}

	/**
	 *
	 * @param format
	 * @param type
	 * @return
	 */
	public static String createFullMethodDefn(final String format, final String type) {
		return createFullMethodDefn(format, type, createDefaultInstance(type));
	}

	/**
	 *
	 * @param format
	 * @param type
	 * @return
	 */
	public static String createJavadoc(final String format, final String type) {
		return createFullMethodDefn(format, type, createDefaultInstance(type));
	}

	/**
	 *
	 * @param format
	 * @param type
	 * @param field
	 * @return
	 */
	public static String createFullMethodDefn(final String format, final String type, final String field) {
		final String instance = StringUtil.createDefaultInstance(field);
		final String convertedFieldName = StringUtil.createEmbeddedInstance(field);

		String methodBody = format;
		methodBody = replacePlaceHolder(methodBody, "field_type", type);
		methodBody = replacePlaceHolder(methodBody, "converted_field_name", convertedFieldName);
		methodBody = replacePlaceHolder(methodBody, "instance_name", instance);

		return methodBody;
	}

	/**
	 *
	 * @param fromType
	 * @param newTypName
	 * @return
	 * @throws Exception
	 */
	public static boolean doesTypeExists(final IType fromType, final String newTypName) throws Exception {
		final IPackageFragment packageFragment = fromType.getPackageFragment();
		final String fullClass = packageFragment.getElementName() + DOT + newTypName;
		final IType type = packageFragment.getJavaProject().findType(fullClass);
		return type != null && type.exists();
	}

	/**
	 *
	 * @param fieldName
	 * @return
	 * @throws JavaModelException
	 */
	public static boolean doesFieldExistsInType(final IType type, final String fieldName) throws JavaModelException {
		final IField field = type.getField(fieldName);
		return field != null && field.exists();
	}

	/**
	 *
	 * @param type
	 * @param method
	 * @param newMethodName
	 * @return
	 * @throws JavaModelException
	 */
	public static boolean doesMethodExistsInType(final IType type, final IMethod method, final String newMethodName)
			throws JavaModelException {
		final IMethod newMethod = type.getMethod(newMethodName, method.getParameterTypes());
		return newMethod != null && newMethod.exists();
	}

	/**
	 *
	 * @param type
	 * @param newMethodName
	 * @return
	 * @throws JavaModelException
	 */
	public static boolean doesMethodExistsInType(final IType type, final String methodName) throws Exception {
		for (final IMethod method : type.getMethods()) {
			if (method != null) {
				if (method.getElementName().equals(methodName)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 *
	 * @param source
	 * @param fromType
	 * @param toType
	 * @param toImplType
	 * @return
	 */
	public static String replaceAllPlaceHolders1(final String source, final IType fromType, final IType toType, final IType toImplType) {
		String configSource = source;
		if (source.indexOf(KEYWORD_TO_INSTANCE) >= 0) {
			configSource = replacePlaceHolder(configSource, KEYWORD_TO_INSTANCE, createDefaultInstance(toType.getElementName()));
		}
		if (source.indexOf(KEYWORD_TO_FULL_CLASS) >= 0) {
			configSource = replacePlaceHolder(configSource, KEYWORD_TO_FULL_CLASS, toType.getFullyQualifiedName());
		}
		if (source.indexOf(KEYWORD_TO_CLASS) >= 0) {
			configSource = replacePlaceHolder(configSource, KEYWORD_TO_CLASS, toType.getElementName());
		}
		if (source.indexOf(KEYWORD_TO_FULL_IMPL_CLASS) >= 0) {
			configSource = replacePlaceHolder(configSource, KEYWORD_TO_FULL_IMPL_CLASS, toImplType.getFullyQualifiedName());
		}
		if (source.indexOf(KEYWORD_TO_IMPL_CLASS) >= 0) {
			configSource = replacePlaceHolder(configSource, KEYWORD_TO_IMPL_CLASS, toImplType.getElementName());
		}
		if (source.indexOf(KEYWORD_FROM_INSTANCE) >= 0) {
			configSource = replacePlaceHolder(configSource, KEYWORD_FROM_INSTANCE, createDefaultInstance(fromType.getElementName()));
		}

		return configSource;
	}

	/**
	 *
	 * @param annoTypeIType
	 * @return
	 */
	public static String getAnnotationDesc(final IType annoType) {
		if (annoType == null || !annoType.exists()) {
			return null;
		}
		final StringBuilder annotationDesc = new StringBuilder(annoType.getElementName());
		final GlobalSettings globalSettings = getInstance();
		if (globalSettings.getAnnotationValuesMap().containsKey(annoType.getFullyQualifiedName())) {
			String value = globalSettings.getAnnotationValuesMap().get(annoType.getFullyQualifiedName());
			value = value.replace("${1}", annoType.getElementName());
			return value;
		}

		IMethod[] methods;
		try {
			methods = annoType.getMethods();
			if (methods == null || methods.length == 0) {
				return annotationDesc.toString();
			}
			final StringBuilder innerFrgmnt = new StringBuilder();
			int count = 0;
			for (final IMethod method : methods) {
				final String annotParam = method.getElementName();
				final String annotRetType = getSignatureSimpleName(method.getReturnType());
				if (annotParam.equals("value")) {
					count++;
					if (annotRetType != null && annotRetType.equals("String")) {
						innerFrgmnt.append(EMPTY_QUOTE_STR);
					}
					innerFrgmnt.append(count < methods.length - 1 ? COMMA + SPACE : EMPTY_STR);
					continue;
				}
				if (annotRetType != null && annotRetType.equals("String")) {
					innerFrgmnt.append(annotParam + " = " + EMPTY_QUOTE_STR);
				} else if (annotRetType != null) {
					innerFrgmnt.append(annotParam + " = " + EMPTY_STR);
				}
				innerFrgmnt.append(count < methods.length - 1 ? COMMA + SPACE : EMPTY_STR);
				count++;
			}
			annotationDesc.append(LEFT_PAREN + innerFrgmnt.toString() + RIGHT_PAREN);
		} catch (final JavaModelException ex) {
			ex.printStackTrace();
			return null;
		}
		return annotationDesc.toString();
	}

	/**
	 *
	 * @param project
	 * @return
	 */
	public static IJavaProject getJavaProject(final String project) {
		final IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (final IProject prjct : projects) {
			if (prjct.isOpen() && !prjct.isHidden() && prjct.isAccessible()) {
				final IJavaProject javaProj = JavaCore.create(prjct);
				if (javaProj != null && javaProj.exists() && javaProj.getElementName().equals(project)) {
					return javaProj;
				}
			}
		}
		return null;
	}

	/**
	 *
	 * @return
	 */
	public static String[][] getAllProjects() {
		final IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();

		final List<String> projs = new ArrayList<String>();
		for (final IProject project : projects) {
			if (project == null || !project.exists() || !project.isOpen()) {
				continue;
			}
			if (!project.isHidden() && project.isAccessible()) {
				final IJavaProject javaProj = JavaCore.create(project);
				if (javaProj != null && javaProj.exists() && !javaProj.getProject().isHidden()) {
					projs.add(project.getName());
				}
			}
		}

		final String[][] projNames = getStringArrayFromList(projs, true);
		return projNames;
	}

	/**
	 *
	 * @return
	 */
	public static String[][] getAllSourcePathsInWorkspace() {
		return getSourcePathsForProject(null);
	}

	/**
	 *
	 * @param project
	 * @return
	 */
	public static String[][] getSourcePathsForProject(final String project) {
		final IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();

		final List<String> paths = new ArrayList<String>();
		try {
			for (final IProject prjct : projects) {
				if (project == null || prjct.getName().equals(project)) {
					final IJavaProject javaProject = JavaCore.create(prjct);
					if (javaProject == null || !javaProject.exists()) {
						continue;
					}
					if (isProjectBinary(javaProject)) {
						continue;
					}
					for (final IPackageFragmentRoot packageFragmentRoot : javaProject.getAllPackageFragmentRoots()) {
						if (packageFragmentRoot == null || !packageFragmentRoot.exists() || packageFragmentRoot.isArchive()
								|| packageFragmentRoot.isExternal()) {
							continue;
						}
						if (!packageFragmentRoot.getParent().equals(javaProject)) { // discard
							// roots
							// which
							// come
							// from
							// another
							// project.
							continue;
						}
						final String fullSrcPath = packageFragmentRoot.getPath().toString();
						final String srcPath = fullSrcPath.replaceFirst(FORWARD_SLASH + javaProject.getElementName(), EMPTY_STR);
						if (!paths.contains(srcPath)) {
							paths.add(srcPath);
						}
					}
				}
			}
		} catch (final Exception ex) {
			ex.printStackTrace();
		}

		return getStringArrayFromList(paths, true);
	}

	/**
	 *
	 * @param javaProject
	 * @return
	 * @throws Exception
	 */
	private static boolean isProjectBinary(final IJavaProject javaProject) throws Exception {
		for (final IPackageFragmentRoot packageFragmentRoot : javaProject.getAllPackageFragmentRoots()) {
			if (packageFragmentRoot != null && packageFragmentRoot.exists() && !packageFragmentRoot.isArchive()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param paths
	 * @param addEmpty
	 * @return
	 */
	public static String[][] getStringArrayFromList(final List<String> paths, final boolean addEmpty) {
		final String[][] sourcePaths = new String[addEmpty ? paths.size() + 1 : paths.size()][2];

		int count = addEmpty ? 1 : 0;

		if (addEmpty) {
			sourcePaths[0][0] = sourcePaths[0][1] = EMPTY_STR;
		}
		for (final String path : paths) {
			sourcePaths[count][0] = sourcePaths[count][1] = path;
			count++;
		}
		return sourcePaths;
	}

	/**
	 *
	 * @return
	 */
	public static String[][] getAllSourcePaths(final String filter) {
		final IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();

		final List<String> paths = new ArrayList<String>();

		try {
			for (final IProject project : projects) {
				final IJavaProject javaProject = JavaCore.create(project);
				if (javaProject != null && javaProject.exists() && isJunitEnabled(javaProject) && javaProject.isOpen()
						&& !javaProject.isReadOnly()) {
					for (final IPackageFragmentRoot packageFragmentRoot : javaProject.getAllPackageFragmentRoots()) {
						if (!(packageFragmentRoot.isArchive() || packageFragmentRoot.isReadOnly())) {
							String sourcePath = packageFragmentRoot.getPath().toString();
							sourcePath = sourcePath.replace(FORWARD_SLASH + project.getName(), "${project}");
							if (filter == null || sourcePath.toUpperCase().contains(filter.toUpperCase())) {
								boolean include = true;
								for (final String path : paths) {
									if (path.equals(sourcePath)) {
										include = false;
										break;
									}
								}
								if (include) {
									paths.add(sourcePath);
								}
							}
						}
					}
				}
			}
		} catch (final JavaModelException ex) {
			ex.printStackTrace();
			return new String[0][0];
		}

		return getStringArrayFromList(paths, false);
	}

	/**
	 *
	 * @param source
	 * @param fromType
	 * @param toType
	 * @param toImplType
	 * @return
	 */
	public static String replaceLocale(final String name, final String locale) {
		if (name.indexOf("locale") > 0) {
			return replacePlaceHolder(name, "locale", locale);
		}

		return name;
	}

	/**
	 *
	 * @param clsName
	 *            -- QString;
	 * @param compUnit
	 * @return
	 * @throws Exception
	 */
	/*
	 * public static String getFQNameFromClassName(final String clsName, final
	 * ICompilationUnit compUnit) throws Exception { for (final
	 * IImportDeclaration imports : compUnit.getImports()) { final int off =
	 * imports.getElementName().lastIndexOf(DOT); final String className =
	 * imports.getElementName().substring(off + 1); if
	 * (className.equals(getSignatureSimpleName(clsName))) { return
	 * imports.getElementName(); } } return "java.lang." +
	 * getSignatureSimpleName(clsName); }
	 */
	/**
	 *
	 * @param clsName
	 *            -- String
	 * @param compUnit
	 * @return
	 * @throws Exception
	 */
	/*
	 * public static String getFQNameFromSimpleClassName(final String clsName,
	 * final ICompilationUnit compUnit) throws Exception { for (final
	 * IImportDeclaration imports : compUnit.getImports()) { final int off =
	 * imports.getElementName().lastIndexOf(DOT); final String className =
	 * imports.getElementName().substring(off + 1); if
	 * (className.equals(clsName)) { return imports.getElementName(); } } return
	 * "java.lang." + clsName; }
	 */

	/**
	 * @param file
	 * @param fileName
	 * @param folderPath
	 * @throws Exception
	 */
	public static void backUpExistingExportFile(final IFile file, final String fileName, final String folderPath) throws Exception {
		final DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy_HH_mm_ss");
		final String currentDate = dateFormat.format(new Date());// computeDate(GlobalSettings.getInstance().getDateFormat());

		final String oldFileName = fileName.substring(0, fileName.indexOf(DOT)) + UNDERSCORE + currentDate
				+ fileName.substring(fileName.indexOf(DOT), fileName.length());

		//final File filefromIFile = file.getRawLocation().makeAbsolute().toFile();
		//final String filePath = file.getRawLocation().makeAbsolute().toString();
		//final File newFile = new File(filePath.substring(0, filePath.lastIndexOf('/')) + FORWARD_SLASH + oldFileName);
		final IFolder folder = getFolderFromPath(file.getProject(), folderPath);
		if (folder != null && folder.exists()) {
			final IFile backUpFile = folder.getFile(oldFileName);//ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(newFile.getAbsolutePath()));
			final InputStream inputStream = file.getContents();
			if (backUpFile.exists()) {
				backUpFile.setContents(inputStream, false, true, new NullProgressMonitor());
			} else {
				backUpFile.create(inputStream, true, new NullProgressMonitor());
			}
			closeInputStream(inputStream);
		}
		//final boolean rename = filefromIFile.renameTo(newFile);
		//file = ResourcesPlugin.getWorkspace().getRoot().getFile(Path.fromOSString(newFile.getAbsolutePath()));
		refreshProject(PROJECT_NAME);
	}

	/**
	 * @param clsName
	 * @param compUnit
	 * @return
	 * @throws Exception
	 */
	public static String getFQNameFromFieldTypeName(final String clsName, final ICompilationUnit compUnit) throws Exception {
		if (isNativeType(clsName)) {
			return clsName;
		}
		if (compUnit != null) {
			for (final IImportDeclaration imports : compUnit.getImports()) {
				final int off = imports.getElementName().lastIndexOf(DOT);
				final String className = imports.getElementName().substring(off + 1);
				String matchClassName = null;
				if (className.equals(ASTERISK)) {
					matchClassName = imports.getElementName() + DOT + clsName;
				} else if (className.equals(clsName)) {
					matchClassName = imports.getElementName();
				}
				if (matchClassName != null) {
					final IType type = compUnit.getJavaProject().findType(matchClassName);
					if (type != null && type.exists()) {
						return type.getFullyQualifiedName();
					}

				}
			}
			final IPackageFragment pkgFragment = compUnit.findPrimaryType().getPackageFragment();
			final IType type = compUnit.getJavaProject().findType(pkgFragment.getElementName() + DOT + clsName);
			if (type != null && type.exists()) {
				return type.getFullyQualifiedName();
			}

			final IType type1 = compUnit.getJavaProject().findType("java.lang." + clsName);
			final String typeName = type1 != null && type1.exists() ? type1.getFullyQualifiedName() : clsName;
			return typeName;
		}
		return null;
	}

	/**
	 * @param fcType
	 * @param nameIMethodMap
	 * @param mthNameList
	 * @throws Exception
	 */
	public static void getMethodsFromParentClass(final FastCodeType fcType, final Map<String, IMethod> nameIMethodMap,
			final List<String> mthNameList) throws Exception {
		for (final IMethod method : fcType.getiType().getMethods()) {
			if (method != null
					&& !(method.isConstructor() || Modifier.isFinal(method.getFlags()) || Modifier.isStatic(method.getFlags()) || Modifier
							.isPrivate(method.getFlags()))) {
				final StringBuilder methodFullSignature = new StringBuilder();
				final StringBuilder methodParam = new StringBuilder(EMPTY_STR);
				for (int i = 0; i < method.getParameterNames().length; i++) {
					methodParam.append(EMPTY_STR.equals(methodParam.toString()) ? getSignatureSimpleName(method.getParameterTypes()[i])
							+ SPACE + method.getParameterNames()[i] : COMMA + SPACE + getSignatureSimpleName(method.getParameterTypes()[i])
							+ SPACE + method.getParameterNames()[i]);
				}
				methodFullSignature.append(getSignatureSimpleName(method.getReturnType()) + SPACE).append(method.getElementName())
						.append(LEFT_PAREN).append(methodParam.toString()).append(RIGHT_PAREN);
				nameIMethodMap.put(methodFullSignature.toString(), method);
				mthNameList.add(methodFullSignature.toString());
			}
		}
	}

	/**
	 * @param fcType
	 * @param nameIMethodMap
	 * @param consNameList
	 * @throws Exception
	 */
	public static void getConstructorsFromParentClass(final FastCodeType fcType, final Map<String, IMethod> nameIMethodMap,
			final List<String> consNameList) throws Exception {
		for (final IMethod method : fcType.getiType().getMethods()) {
			if (method != null && method.isConstructor()) {
				final StringBuilder methodFullSignature = new StringBuilder();
				final StringBuilder methodParam = new StringBuilder(EMPTY_STR);
				for (int i = 0; i < method.getParameterNames().length; i++) {
					methodParam.append(EMPTY_STR.equals(methodParam.toString()) ? getSignatureSimpleName(method.getParameterTypes()[i])
							+ SPACE + method.getParameterNames()[i] : COMMA + SPACE + getSignatureSimpleName(method.getParameterTypes()[i])
							+ SPACE + method.getParameterNames()[i]);
				}
				methodFullSignature.append(getSignatureSimpleName(method.getReturnType()) + SPACE).append(method.getElementName())
						.append(LEFT_PAREN).append(methodParam.toString()).append(RIGHT_PAREN);
				nameIMethodMap.put(methodFullSignature.toString(), method);
				consNameList.add(methodFullSignature.toString());
			}
		}
	}

	public static void main(final String[] args) {
	}

	/**
	 * @param fastCodeContext
	 * @param createSimilarDescriptor
	 * @param length
	 * @throws Exception
	 */
	public static void populatecreateDescClassWithUserInput(final FastCodeContext fastCodeContext,
			final CreateSimilarDescriptor createSimilarDescriptor, final String[] inputs, final boolean differentName,
			final IType typeToWorkOn) throws Exception {

		final Shell parentShell = MessageUtil.getParentShell();
		final Shell shell = parentShell == null ? new Shell() : parentShell;
		IPackageFragmentRoot packageFragmentRoot = null;
		IPackageFragment tmpPackageFragment = null;
		final GlobalSettings globalSettings = getInstance();
		String superClass = EMPTY_STR;
		IType supType = null;
		int k = 0;
		boolean isMultiple = false;
		if (inputs.length > 1) {
			isMultiple = true;
		}
		createSimilarDescriptor.setNoOfInputs(inputs.length);
		for (final String input : inputs) {
			if (differentName) {
				final Pattern p = Pattern.compile(createSimilarDescriptor.getFromPattern());
				final Matcher m = p.matcher(typeToWorkOn.getFullyQualifiedName());

				if (!m.matches()) {
					continue;
				}

				final String replatePart = m.group(m.groupCount());
				createSimilarDescriptor.createReplacePartAndValue(replatePart, input);
			}
		}

		final CreateSimilarDescriptorClass[] createSimilarDescUserChoice = new CreateSimilarDescriptorClass[createSimilarDescriptor
				.getCreateSimilarDescriptorClasses().length];
		for (final CreateSimilarDescriptorClass createSimilarDescriptorClass : createSimilarDescriptor.getCreateSimilarDescriptorClasses()) {
			IPackageFragment packageFragment = null;
			if (createSimilarDescriptorClass == null) {
				continue;
			}
			String toName = createSimilarDescriptorClass.getToPattern();
			IType[] fldTypeArr = null;
			String targetProject = null;
			if (packageFragmentRoot == null) {
				targetProject = createSimilarDescriptorClass.getProject();
				if (fastCodeContext.getFromType() == null) {
					final IJavaProject project = getJavaProject(targetProject);
					if (project != null && project.exists() && !isEmpty(createSimilarDescriptorClass.getPackge())) {
						for (final IPackageFragmentRoot pkgFragmntRoot : project.getPackageFragmentRoots()) {
							packageFragment = pkgFragmntRoot.getPackageFragment(createSimilarDescriptorClass.getPackge());
							if (packageFragment != null && packageFragment.exists()) {
								packageFragmentRoot = pkgFragmntRoot;
								break;
							}
						}
					}
					if (packageFragment == null || !packageFragment.exists()) {
						final SelectionDialog packageDialog = JavaUI.createPackageDialog(shell, project, 0, null);
						if (packageDialog.open() == CANCEL) {
							return;
						}
						packageFragment = (IPackageFragment) packageDialog.getResult()[0];
						packageFragmentRoot = (IPackageFragmentRoot) packageFragment.getParent();
					} else if (isEmpty(createSimilarDescriptorClass.getSubPackage()) && packageFragment.hasSubpackages()) {
						final List<IPackageFragment> subPackages = new ArrayList<IPackageFragment>();
						for (final IJavaElement chldPkgFragment : packageFragmentRoot.getChildren()) {
							if (chldPkgFragment instanceof IPackageFragment
									&& chldPkgFragment.getElementName().startsWith(packageFragment.getElementName())) {
								subPackages.add((IPackageFragment) chldPkgFragment);
							}
						}
						if (!subPackages.isEmpty()) {
							final PackageSelectionDialog selectionDialog = new PackageSelectionDialog(shell, "Sub Package",
									"Choose the sub pacage from below", subPackages.toArray(new IPackageFragment[0]));
							if (selectionDialog.open() != CANCEL) {
								packageFragment = (IPackageFragment) selectionDialog.getFirstResult();
							}
						}
					}
				} else {
					if (fastCodeContext.isUnitTest()) {
						String sourcePath = globalSettings.isUseDefaultForPath() ? globalSettings.getSourcePathTest()
								: createSimilarDescriptorClass.getSourcePath();
						sourcePath = getDefaultPathFromProject(fastCodeContext.getFromType().getJavaProject(), "test", sourcePath);
						packageFragmentRoot = getPackageRootFromProject(fastCodeContext.getFromType().getJavaProject(), sourcePath);
					} else if (!isEmpty(targetProject)) {
						final String sourcePath = globalSettings.isUseDefaultForPath() ? getPathFromGlobalSettings(fastCodeContext
								.getFromType().getJavaProject().getElementName()) : createSimilarDescriptorClass.getSourcePath();
						;
						packageFragmentRoot = getPackageRootFromProject(createSimilarDescriptorClass.getProject(), sourcePath);
					} else {
						packageFragmentRoot = (IPackageFragmentRoot) fastCodeContext.getFromType().getPackageFragment().getParent();
						targetProject = packageFragmentRoot.getParent().getElementName();
					}
					final String fullname = fastCodeContext.getFromType().getFullyQualifiedName();
					final String fromPattern = createSimilarDescriptor.getFromPattern();
					if (fromPattern != null) {
						parseTokens(fromPattern, fullname, fastCodeContext.getPlaceHolders());
					}
					if (packageFragmentRoot == null || !packageFragmentRoot.exists()) {
						throw new Exception("Unable to find source path for, please check configuration.");
					}
					toName = replacePlaceHolders(toName, fastCodeContext.getPlaceHolders());
					if (createSimilarDescriptor.isDifferentName()) {
						toName = fullname.replaceAll(createSimilarDescriptor.getReplacePart(), createSimilarDescriptor.getReplaceValue());
					}
					final int lastDotPos = toName.lastIndexOf(DOT_CHAR);
					final String newpkg = lastDotPos != -1 ? toName.substring(0, lastDotPos) : fastCodeContext.getFromType()
							.getPackageFragment().getElementName();
					packageFragment = packageFragmentRoot.getPackageFragment(newpkg);

				}
				tmpPackageFragment = packageFragment;

			}

			if (tmpPackageFragment != null) {
				final List<IType> importTypes = new ArrayList<IType>();

				final IJavaProject javaProject = tmpPackageFragment.getJavaProject();
				final String[] impTypes = replacePlaceHolders(createSimilarDescriptorClass.getImportTypes(),
						fastCodeContext.getPlaceHolders());
				final String[] superTypes = replacePlaceHolders(createSimilarDescriptorClass.getSuperTypes(),
						fastCodeContext.getPlaceHolders());
				final String[] implementTypes = replacePlaceHolders(createSimilarDescriptorClass.getImplementTypes(),
						fastCodeContext.getPlaceHolders());
				gatherImports(javaProject, impTypes, IJavaElementSearchConstants.CONSIDER_ALL_TYPES, "import", importTypes);
				final List<IType> implTypes = gatherImports(javaProject, implementTypes, IJavaElementSearchConstants.CONSIDER_INTERFACES,
						"implement", importTypes);
				// createSimilarDescriptorClass.setUserInputInterface(implTypes);
				// createSimilarDescriptorClass.setUserInputImports(importTypes);
				if (superTypes != null && superTypes.length > 0) {

					final FastCodeType[] fastCodeTypes = new FastCodeType[superTypes.length];
					int i = 0;
					for (final String superType : superTypes) {
						fastCodeTypes[i++] = parseType(superType, null);
					}
					final ClassSelectionDialog classSelectionDialog = new ClassSelectionDialog(shell, "Super Class",
							"Choose the classes to extend", fastCodeTypes, false);
					final int ret = classSelectionDialog.open();
					if (ret != CANCEL) {
						// final FastCodeType fastCodeType = (FastCodeType)
						// classSelectionDialog.getResult()[0];
						supType = tmpPackageFragment.getJavaProject().findType(classSelectionDialog.getResult()[0].toString());

						if (supType != null && supType.exists()) {
							// superClass = flattenType(fastCodeType, false);
							superClass = superClass.replace(supType.getFullyQualifiedName(), supType.getElementName());
							// placeHolders.put("super_class", superClass);
							if (!supType.isBinary() && supType.getCompilationUnit() != null && supType.getCompilationUnit().exists()) {
								fastCodeContext.addResource(new FastCodeResource(supType.getResource()));
							}
						}
					}

				}

				if (createSimilarDescriptorClass.isCreateFields()) {
					final SelectionDialog selectionDialog = JavaUI.createTypeDialog(shell, null, SearchEngine.createWorkspaceScope(),
							IJavaElementSearchConstants.CONSIDER_CLASSES_AND_INTERFACES, true,
							createSimilarDescriptorClass.getCreateFieldsName());
					selectionDialog.setMessage("Please select one or more classes to create field.");
					selectionDialog.setTitle("Select Class");

					if (selectionDialog.open() != CANCEL) {
						int i = 0;
						final Object[] tmpArray = selectionDialog.getResult();
						fldTypeArr = new IType[tmpArray.length];
						for (final Object type : tmpArray) {
							final IType fldType = (IType) type;
							if (isAbstract(fldType.getFlags())) {
								openWarning(shell, "Warning", "Cannot make an instance of an abstract class " + fldType.getElementName());
								continue;
							}
							fldTypeArr[i] = fldType;
							i++;
						}

					}
				}
				createSimilarDescUserChoice[k] = new CreateSimilarDescriptorClass.Builder().withUserInputPackage(packageFragment)
						.withUserInputImports(importTypes).withUserInputInterface(implTypes).withSuperClass(superClass)
						.withUserInputFieldTypes(fldTypeArr).withCreateFields(createSimilarDescriptorClass.isCreateFields())
						.withClassAnnotations(createSimilarDescriptorClass.getClassAnnotations())
						.withClassBody(createSimilarDescriptorClass.getClassBody())
						.withClassHeader(createSimilarDescriptorClass.getClassHeader())
						.withClassInsideBody(createSimilarDescriptorClass.getClassInsideBody())
						.withClassType(createSimilarDescriptorClass.getClassType())
						.withConvertMethodParam(createSimilarDescriptorClass.isConvertMethodParam())
						.withConvertMethodParamFrom(createSimilarDescriptorClass.getConvertMethodParamFrom())
						.withConvertMethodParamTo(createSimilarDescriptorClass.getConvertMethodParamTo())
						.withCreateDefaultConstructor(createSimilarDescriptorClass.isCreateDefaultConstructor())
						.withCreateEqualsHashcode(isMultiple ? false : createSimilarDescriptorClass.isCreateEqualsHashcode())
						.withCreateFieldsName(createSimilarDescriptorClass.getCreateFieldsName())
						.withCreateInstanceConstructor(isMultiple ? false : createSimilarDescriptorClass.isCreateInstanceConstructor())
						.withCreateMethodBody(createSimilarDescriptorClass.isCreateMethodBody())
						.withCreateToString(isMultiple ? false : createSimilarDescriptorClass.isCreateToString())
						.withCreateUnitTest(createSimilarDescriptorClass.isCreateUnitTest())
						.withFieldAnnotations(createSimilarDescriptorClass.getFieldAnnotations())
						.withFinalClass(createSimilarDescriptorClass.isFinalClass())
						.withImplementTypes(createSimilarDescriptorClass.getImplementTypes())
						.withImportTypes(createSimilarDescriptorClass.getImportTypes())
						.withInclGetterSetterForInstance(createSimilarDescriptorClass.isInclGetterSetterForInstance())
						.withMethodAnnotations(createSimilarDescriptorClass.getMethodAnnotations())
						.withPackge(createSimilarDescriptorClass.getPackge()).withProject(createSimilarDescriptorClass.getProject())
						.withRelationTypeToParent(createSimilarDescriptorClass.getRelationTypeToParent())
						.withSourcePath(createSimilarDescriptorClass.getSourcePath())
						.withSubPackage(createSimilarDescriptorClass.getSubPackage())
						.withSuperTypes(createSimilarDescriptorClass.getSuperTypes()).withUserInputSuperClass(supType)
						.withToPattern(createSimilarDescriptorClass.getToPattern())
						.withInclInstance(createSimilarDescriptorClass.isInclInstance())
						.withRelatedDescriptors(new ArrayList<CreateSimilarDescriptorClass>()).build();

				k++;

			}
		}
		if (tmpPackageFragment != null) {
			createSimilarDescriptor.setCreateSimilarDescUserChoice(createSimilarDescUserChoice);
			createSimilarDescriptor.numbersOfCreateSimilarDescUserChoiceClasses(createSimilarDescUserChoice);
		}
	}

	/**
	 * @param type
	 * @param field
	 * @param createVariableData
	 * @param setterFormat
	 * @param getterFormat
	 * @return
	 * @throws Exception
	 */
	public static List<FastCodeMethod> createGetterSettersWithCustomFormat(final IType type, final IField field,
			final CreateVariableData createVariableData, final String setterFormat, final String getterFormat) throws Exception {
		final Map<String, Object> placeHolders = new HashMap<String, Object>();
		final FastCodeField fastCodeField = new FastCodeField(field);
		String fldType = fastCodeField.getType().getName();
		if (fastCodeField.isArray()) {
			for (int k = 1; k <= fastCodeField.getArrayDimension(); k++) {
				fldType += "[]";
			}

		}
		placeHolders.put("${field.type}", fldType);
		placeHolders.put("field", fastCodeField);
		boolean createGetter = false;
		boolean createSetter = false;
		boolean createAdder = false;
		final List<FastCodeMethod> methodsCreated = new ArrayList<FastCodeMethod>();

		if (createVariableData.getGetterSetter() == GETTER_SETTER.GETTER_ADDER_EXIST) { // if
																						// (getter_setter
																						// ==
																						// GETTER_SETTER.GETTER_ADDER_EXIST)
																						// {
																						// //
			createGetter = createAdder = true;
		} else if (createVariableData.getGetterSetter() == GETTER_SETTER.GETTER_SETTER_EXIST) { // else
																								// if
																								// (getter_setter
																								// ==
																								// GETTER_SETTER.GETTER_SETTER_EXIST)
																								// {
																								// //
			createGetter = createSetter = true;
		} else if (createVariableData.getGetterSetter() == GETTER_SETTER.GETTER_EXIST) { // (getter_setter
																							// ==
																							// GETTER_SETTER.GETTER_EXIST)
																							// {
																							// //
			createGetter = true;
		}
		final List<Pair<String, String>> methArgs = new ArrayList<Pair<String, String>>();
		final String[] methArg = { "QBuilder;" };
		String methodBody = EMPTY_STR;
		String methodSrc = EMPTY_STR;
		final String typeSignature = Signature.getSignatureSimpleName(field.getTypeSignature());
		String argsPrefix = EMPTY_STR;
		String fieldNameWithPrefix = EMPTY_STR;
		if (!isEmpty(createVariableData.getSetterVerPrefix())) {
			argsPrefix = createVariableData.getSetterVerPrefix().toLowerCase();
			fieldNameWithPrefix = argsPrefix + changeFirstLetterToUpperCase(field.getElementName()); // field.getElementName().substring(0,
																										// 1).toUpperCase().concat(field.getElementName().substring(1)));
		} else {
			fieldNameWithPrefix = field.getElementName();
		}

		IMethod meth = type.getMethod(type.getElementName(), methArg);
		if (!meth.exists()) {
			meth = null;
		}
		final IPreferenceStore preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		IMethod getterSibling;
		IMethod setterSibling;
		getterSibling = setterSibling = meth;
		final String[] setteArg = new String[1];
		if (preferenceStore.getString(P_GETTER_SETTER_POSITION) != null
				&& preferenceStore.getString(P_GETTER_SETTER_POSITION).equals("getterfirst")) {
			int k = 0;
			while ((getterSibling == null || !getterSibling.exists()) && type.getFields().length > k) {
				setteArg[0] = type.getFields()[k].getTypeSignature();
				getterSibling = type.getMethod("set" + createEmbeddedInstance(type.getFields()[k].getElementName()), setteArg);
				k++;
			}

		}
		if (createVariableData.isVariableModifyAction() && createVariableData.getInsertionPoint() != null) {
			final String nextMethod = "get" + createEmbeddedInstance(createVariableData.getInsertionPoint().getElementName());
			getterSibling = type.getMethod(nextMethod, null);
			if (preferenceStore.getString(P_GETTER_SETTER_POSITION) != null
					&& preferenceStore.getString(P_GETTER_SETTER_POSITION).equals("getterfirst")) {
				setteArg[0] = createVariableData.getInsertionPoint().getTypeSignature();
				setterSibling = type.getMethod("set" + createEmbeddedInstance(createVariableData.getInsertionPoint().getElementName()),
						setteArg);
			}

		}
		if (getterSibling != null && !getterSibling.exists()) {
			getterSibling = null;
		}
		if (setterSibling != null && !setterSibling.exists()) {
			setterSibling = null;
		}
		if (createGetter) {
			methodSrc = evaluateByVelocity(getterFormat, placeHolders);
			final IMethod meth1 = type.createMethod(methodSrc, getterSibling, false, null);
			methodsCreated.add(new FastCodeMethod(meth1));
		}
		if (createSetter) {
			methodSrc = evaluateByVelocity(setterFormat, placeHolders);
			final IMethod meth1 = type.createMethod(methodSrc, setterSibling, false, null);
			methodsCreated.add(new FastCodeMethod(meth1));
		}
		if (createAdder) {
			methodBody = EMPTY_STR;
			final FastCodeType fastCodeType = parseType(typeSignature, field.getCompilationUnit());
			if (createVariableData.getFieldParams() == null || createVariableData.getFieldParams().length == 0) {// if
																													// (paramTypes
																													// ==
																													// null
																													// ||
																													// paramTypes.length
																													// ==
																													// 0)
																													// {
																													// //
				if ("List".equals(fastCodeType.getName())) {
					methodBody = "this." + field.getElementName() + ".add(item);";
					methArgs.add(new Pair<String, String>("Object", "item"));
				} else if ("Set".equals(fastCodeType.getName())) {
					methodBody = "this." + field.getElementName() + ".add(item);";
					methArgs.add(new Pair<String, String>("Object", "item"));
				} else if ("Map".equals(fastCodeType.getName())) {
					methodBody = "this." + field.getElementName() + ".put(key, value);";
					methArgs.add(new Pair<String, String>("Object", "key"));
					methArgs.add(new Pair<String, String>("Object", "value"));
				}
			} else {
				// final String param0 = paramTypes[0].getElementName();
				final String param0 = createVariableData.getFieldParams()[0];
				String param0Inst = EMPTY_STR;
				if (!isEmpty(argsPrefix)) {
					param0Inst = argsPrefix + changeFirstLetterToUpperCase(createDefaultInstance(param0));
				} else {
					param0Inst = createDefaultInstance(param0);
				}
				if ("List".equals(fastCodeType.getName())) {
					methArgs.add(new Pair<String, String>(param0, param0Inst));
					methodBody = "this." + field.getElementName() + ".add(" + param0Inst + ");";
				} else if ("Set".equals(fastCodeType.getName())) {
					methArgs.add(new Pair<String, String>(param0, param0Inst));
					methodBody = "this." + field.getElementName() + ".add(" + param0Inst + ");";
				} else if ("Map".equals(fastCodeType.getName())) {
					if (createVariableData.getFieldParams().length > 1) { // if
																			// (paramTypes.length
																			// >
																			// 1)
																			// {
																			// //
						methArgs.add(new Pair<String, String>(param0, param0Inst));
						// final String param1 = paramTypes[1].getElementName();
						final String param1 = createVariableData.getFieldParams()[1];
						String param1Inst = EMPTY_STR;
						if (!isEmpty(argsPrefix)) {
							param1Inst = argsPrefix + changeFirstLetterToUpperCase(createDefaultInstance(param1));
						} else {
							param1Inst = createDefaultInstance(param1);
						}
						final String arg1 = param1.equals(param0) ? param1Inst + "2" : param1Inst;
						methArgs.add(new Pair<String, String>(param1, arg1));
						methodBody = "this." + field.getElementName() + ".put(" + param0Inst + COMMA + param1Inst + ");";
					} else {
						methodBody = "this." + field.getElementName() + ".put(key, value);";
						methArgs.add(new Pair<String, String>("Object", "key"));
						methArgs.add(new Pair<String, String>("Object", "value"));
					}
				}
			}
			methodSrc = createMethodSource("add" + createEmbeddedInstance(field.getElementName()), methodBody,
					"add method for " + field.getElementName(), METHOD_RETURN_TYPE_VOID, methArgs, true, true);
			final IMethod meth1 = type.createMethod(methodSrc, meth, false, null);
			methodsCreated.add(new FastCodeMethod(meth1));
		}
		return methodsCreated;
	}

	/**
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public static IType[] getSuperInterfacesType(final IType type) throws Exception {
		final List<IType> superInterfaceTypes = new ArrayList<IType>();
		if (type != null) {
			final ITypeHierarchy hierarchy = type.newSupertypeHierarchy(null);
			final String[] interfaces = type.getSuperInterfaceNames();

			if (interfaces == null || interfaces.length == 0) {
				return new IType[0];
			}

			for (final IType interfaceType : hierarchy.getAllSuperInterfaces(type)) {
				if (interfaceType != null) {
					superInterfaceTypes.add(interfaceType);
				}
			}
		}
		if (superInterfaceTypes != null && !superInterfaceTypes.isEmpty()) {
			return superInterfaceTypes.toArray(new IType[0]);
		}
		return new IType[0];
	}

	/**
	 * @param impType
	 * @param fastCodeContext
	 * @param toType
	 * @param createSimilarDescriptor
	 * @param createSimilarDescriptorClass
	 * @throws Exception
	 */
	public static void implementInterfaceMethods(final IType impType, final FastCodeContext fastCodeContext, final IType toType,
			final CreateSimilarDescriptor createSimilarDescriptor, final CreateSimilarDescriptorClass createSimilarDescriptorClass)
			throws Exception {
		if (impType == null || !impType.exists()) {
			return;
		}
		final MethodBuilder methodBuilder = new SimilarMethodBuilder(fastCodeContext);
		for (final IMethod method : impType.getMethods()) {
			final IMethod meth = methodBuilder.buildMethod(method, toType, createSimilarDescriptor, createSimilarDescriptorClass);
			if (meth != null) {
				final FastCodeResource resource = fastCodeContext.findResource(toType.getCompilationUnit());
				if (resource != null) {
					resource.setModified(true);
				}
			}
		}
	}

	/**
	 * @param fileName
	 * @return
	 */
	public static boolean isFullNameOfFile(final String fileName) {
		if (fileName == null) {
			return false;
		}
		final String[] parts = fileName.split(FORWARD_SLASH);
		if (parts.length == 1) {
			return false;
		}
		for (final String part : parts) {
			if (part == null) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @throws Exception
	 */
	public static IJavaProject getWorkingJavaProjectFromUser() throws Exception {
		final IJavaProject[] javaProject = getProjectsFromWorkspace();
		final ProjectSelectionDialog projectSelectionDialog = new ProjectSelectionDialog(new Shell(), "Projects in Workspace",
				"Select a project you want to work with", javaProject, IJavaElement.JAVA_PROJECT);
		if (projectSelectionDialog.open() != CANCEL) {
			return (IJavaProject) projectSelectionDialog.getFirstResult();
		}
		return null;
	}

	/**
	 * @param workingJavaProject
	 * @param defaultPath
	 * @return
	 * @throws Exception
	 */
	public static IPackageFragment[] getPackagesInProject(IJavaProject workingJavaProject, String defaultPath, final String type)
			throws Exception {
		final List<IPackageFragment> allPackages = new ArrayList<IPackageFragment>();
		if (workingJavaProject.getElementName().equals(FC_PLUGIN)) {
			workingJavaProject = getWorkingJavaProjectFromUser();
			defaultPath = getDefaultPathFromProject(workingJavaProject, type, EMPTY_STR);
		}
		final IPackageFragmentRoot[] packageFragmentRootArray = workingJavaProject.getJavaProject().getAllPackageFragmentRoots();
		getPackagesForFragment(workingJavaProject, packageFragmentRootArray, allPackages, defaultPath);
		final String reqdPrjs[] = workingJavaProject.getRequiredProjectNames();

		for (final String rqdPrj : reqdPrjs) {
			final IJavaProject javaPrj = getJavaProject(rqdPrj);
			if (javaPrj == null) {
				MessageDialog.openInformation(new Shell(), "Dependent Project Missing",
						"Dependent project is missing, going ahead with the available ones.");
				continue;
			}
			final IPackageFragmentRoot[] pkgFrgmRoots = javaPrj.getAllPackageFragmentRoots();
			getPackagesForFragment(getJavaProject(rqdPrj), pkgFrgmRoots, allPackages, defaultPath);
		}
		final GlobalSettings globalSettings = getInstance();
		if (allPackages.isEmpty()) {
			if (globalSettings.isUseDefaultForPath()) {
				MessageDialog.openInformation(new Shell(), "No Packages Found",
						"There are no packages in the path " + globalSettings.getSourcePathJava());
				return new IPackageFragment[0];
			} else {
				MessageDialog.openInformation(new Shell(), "No Packages Found", "There are no packages found in the project "
						+ workingJavaProject.getElementName());
				return new IPackageFragment[0];
			}
		}

		return allPackages.toArray(new IPackageFragment[0]);
	}

	/**
	 * @param workingJavaProject
	 * @param pkgFrgmRoots
	 * @param allPackages
	 * @param defaultPath
	 * @throws JavaModelException
	 */
	private static void getPackagesForFragment(final IJavaProject workingJavaProject, final IPackageFragmentRoot[] pkgFrgmRoots,
			final List<IPackageFragment> allPackages, final String defaultPath) throws JavaModelException {
		for (final IPackageFragmentRoot packageFragmentRoot : pkgFrgmRoots) {
			if (!packageFragmentRoot.isArchive()) {
				for (final IJavaElement pkg : packageFragmentRoot.getChildren()) {
					if (pkg != null && !isEmpty(pkg.getElementName()) && pkg instanceof IPackageFragment && !(pkg instanceof IFolder)) {
						final IClassFile classFile[] = ((IPackageFragment) pkg).getClassFiles();
						final GlobalSettings globalSettings = getInstance();
						//final String defaultPath = getDefaultPathFromProject(workingJavaProject);
						/*String defaultPath = globalSettings.getSourcePathJava();
						final String[][] entryNamesAndValues = getSourcePathsForProject(workingJavaProject.getElementName());
						for (int i = 0; i < entryNamesAndValues.length; i++) {
							if (!entryNamesAndValues[i][0].trim().equals(EMPTY_STR)) {
								if (defaultPath.contains(entryNamesAndValues[i][0])) {
									defaultPath = entryNamesAndValues[i][0];
									break;

								}
							}
						}*/
						if (globalSettings.isUseDefaultForPath()) {
							if (((IPackageFragment) pkg).getPath().toString().contains(defaultPath)) {
								allPackages.add((IPackageFragment) pkg);
							}
						} else {
							allPackages.add((IPackageFragment) pkg);
						}
					}
				}
			}
		}
	}

	/**
	 * @param javaProject
	 * @param type
	 * @return
	 */
	public static String getDefaultPathFromProject(final IJavaProject javaProject, final String type, final String path) {
		final GlobalSettings globalSettings = getInstance();
		String defaultPath = path;
		if (defaultPath.equals(EMPTY_STR)) {
			if (type.equals("test")) {
				defaultPath = globalSettings.getSourcePathTest();
			} else if (type.equals("resources")) {
				defaultPath = globalSettings.getSourcePathResources();
			} else {
				defaultPath = globalSettings.getSourcePathJava();
			}
		}
		final String[][] entryNamesAndValues = getSourcePathsForProject(javaProject.getElementName());
		final String[] defatPathArray = defaultPath.split(SPACE);
		for (int i = 0; i < entryNamesAndValues.length; i++) {
			if (!entryNamesAndValues[i][0].trim().equals(EMPTY_STR)) {
				for (final String str : defatPathArray) {
					if (str.equalsIgnoreCase(entryNamesAndValues[i][0])) {
						defaultPath = str;
						return defaultPath;

					}
				}
			}
		}
		return defaultPath;

	}

	/**
	 *
	 * @param snippet
	 * @param packageFragment
	 * @param project
	 * @param clasName
	 * @return
	 * @throws Exception
	 */
	public static ICompilationUnit createClass(final String snippet, final IPackageFragment packageFragment, final IJavaProject project,
			final String clasName) throws Exception {
		ICompilationUnit compilationUnitNew = null;
		String finalSnippet = snippet;

		finalSnippet = buildClass(snippet, packageFragment, project, clasName);
		final String className = StringUtil.parseClassName(finalSnippet);
		/*getGlobalSettings(placeHolders);
		final GlobalSettings globalSettings = getInstance();
		String classHeader = evaluateByVelocity(globalSettings.getClassHeader(), placeHolders);
		placeHolders.put(CLASS_HEADER_STR, classHeader);

		String className = null;
		if (!isEmpty(snippet)) {
			className = StringUtil.parseClassName(snippet);
		}
		if (className == null) {
			className = clasName;
		}
		if (className == null) {
			if (!MessageDialog.openConfirm(new Shell(), "Warning", "Template may be incorrect. Would you like to proceed?")) {
				return null;
			}
		}
		if (project == null) {
			project = getWorkingJavaProjectFromUser();
		}
		final String srcPath = getDefaultPathFromProject(project, "source", EMPTY_STR);
		if (packageFragment == null) {
			final PackageSelectionDialog selectionDialog = new PackageSelectionDialog(new Shell(), "Package ",
					"Choose a package from below", getPackagesInProject(project, srcPath, "source"));
			if (selectionDialog.open() != CANCEL) {
				packageFragment = (IPackageFragment) selectionDialog.getFirstResult();
			} else {
				throw new Exception("Can not create the class without package.Select Package and then try again");
			}
		}
		if (!isEmpty(clasName) && isEmpty(snippet)) {
			String classBody = globalSettings.getClassBody();
			placeHolders.put(CLASS_ANNOTATIONS_STR, EMPTY_STR);
			if (!placeHolders.containsKey("super_class")) {
				classBody = replacePlaceHolderWithBlank(classBody, "extends", "super_class", "implements");
			}
			if (!placeHolders.containsKey("interfaces")) {
				classBody = replacePlaceHolderWithBlank(classBody, "implements", "interfaces", "{");
			}
			if (!placeHolders.containsKey(CLASS_MODIFIER_STR)) {
				classBody = replacePlaceHolderWithBlank(classBody, null, CLASS_MODIFIER_STR, makePlaceHolder(CLASS_TYPE_STR));
			}
			placeHolders.put(PACKAGE_NAME_STR, packageFragment.getElementName());
			placeHolders.put(CLASS_NAME_STR, clasName);
			getGlobalSettings(placeHolders);

			placeHolders.put(CLASS_BODY_STR, isEmpty(snippet) ? EMPTY_STR : snippet);
			placeHolders.put(CLASS_TYPE_STR, CLASS_TYPE.CLASS.value().toLowerCase());
			placeHolders.put(CLASS_IMPORTS_STR, EMPTY_STR);

			getGlobalSettings(placeHolders);
			finalSnippet = snippet = evaluateByVelocity(classBody, placeHolders);
		}
		packageDeclaration = PACKAGE_STR + SPACE + packageFragment.getElementName() + SEMICOLON + NEWLINE + NEWLINE;

		if (!snippet.trim().contains(packageDeclaration.trim())) {
			finalSnippet = classHeader + packageDeclaration + snippet;
		}*/
		final IType type = project.findType(packageFragment.getPath().toString() + DOT + className);
		if (type == null || !type.exists()) {
			try {
				compilationUnitNew = packageFragment.createCompilationUnit(className + DOT + JAVA_EXTENSION, finalSnippet, false, null);

				if (compilationUnitNew == null || !compilationUnitNew.exists()) {
					throw new Exception("Unable to create class.");
				}
			} catch (final Exception ex) {
				ex.printStackTrace();
				throw new Exception("Unable to create class, template may be wrong. " + ex.getMessage() + snippet, ex);
			}
			return compilationUnitNew;

		} else {
			openWarning(new Shell(), "Warning", "class, " + className
					+ ", already exists, you can only add more methods. Please use method snippet creator for the same");
			return null;

		}
	}

	/**
	 * @return
	 */
	public static String getPathFromUser() {
		return getPathFromUser("Choose Source Path");
	}

	/**
	 * @return
	 */
	public static String getPathFromUser(final String message) {
		final String[][] sourcePaths = getAllSourcePathsInWorkspace();
		final List<String> sourcePathList = new ArrayList<String>();
		for (int i = 0; i < sourcePaths.length; i++) {
			sourcePathList.add(sourcePaths[i][0]);
		}
		sourcePathList.remove(0);
		final StringSelectionDialog selectionDialog = new StringSelectionDialog(new Shell(), "Source Path", message,
				sourcePathList.toArray(new String[0]), false);
		if (selectionDialog.open() == CANCEL) {
			return null;
		}
		return (String) selectionDialog.getFirstResult();
	}

	/**
	 * @param project
	 * @return
	 */
	public static String getPathFromGlobalSettings(final String project) {
		final IJavaProject javaProject = getJavaProject(project);

		/*final GlobalSettings globalSettings = getInstance();
		String defaultPath = globalSettings.getSourcePathJava();
		final String[][] entryNamesAndValues = getSourcePathsForProject(project);
		for (int i = 0; i < entryNamesAndValues.length; i++) {
			if (!entryNamesAndValues[i][0].trim().equals(EMPTY_STR)) {
				if (defaultPath.contains(entryNamesAndValues[i][0])) {
					defaultPath = entryNamesAndValues[i][0];
					break;

				}
			}
		}*/

		return getDefaultPathFromProject(javaProject, "source", EMPTY_STR);
	}

	/**
	 * @param pkgFrgmnt
	 * @return
	 */
	public static String getAlteredPackageName(final IPackageFragment pkgFrgmnt) {
		final String fullPkgName = pkgFrgmnt.getPath().toString();
		final String primElemt = pkgFrgmnt.getPrimaryElement().getElementName();
		final String alteredPkgFullNme = fullPkgName.replaceAll(FORWARD_SLASH, DOT);
		final String proj = pkgFrgmnt.getJavaProject().getElementName();
		if (isEmpty(primElemt)) {
			return LEFT_PAREN + proj + RIGHT_PAREN;
		}
		String srcPath = alteredPkgFullNme.substring(proj.length() + 2, alteredPkgFullNme.indexOf(primElemt) - 1);
		srcPath = srcPath.replace(DOT, FORWARD_SLASH);
		return pkgFrgmnt.getPrimaryElement().getElementName() + SPACE + LEFT_PAREN + proj + HYPHEN + srcPath + RIGHT_PAREN;
	}

	/**
	 * @param workingJavaProject
	 * @return
	 * @throws Exception
	 */
	public static IPackageFragment getPackageFromUser(final IJavaProject workingJavaProject) throws Exception {
		final IPackageFragment packageFragment = null;

		// if not invoked from java class...list all prj in wrk space...then
		// list pkg in that prj...save the selection in pref..

		/*final IPackageFragmentRoot[] packageFragmentRootArray = this.workingJavaProject.getJavaProject().getAllPackageFragmentRoots();

		final List<IPackageFragment> allPackages = new ArrayList<IPackageFragment>();

		for (final IPackageFragmentRoot packageFragmentRoot : packageFragmentRootArray) {
			// System.out.println("Ele name: " + ipfr.getElementName() +
			// " is archive: " + ipfr.isArchive());
			final IJavaElement[] jea = null;
			if (!packageFragmentRoot.isArchive()) {
				for (final IJavaElement pkg : packageFragmentRoot.getChildren()) {
					System.out.println("Ele name: " + pkg.getElementName());

					if (pkg instanceof IPackageFragment) {
						allPackages.add((IPackageFragment) pkg);
					}
				}
			}

		}*/
		// System.out.println(this.getCompilationUnitFromEditor().);

		final String srcPath = getDefaultPathFromProject(workingJavaProject, "source", EMPTY_STR);
		final PackageSelectionDialog selectionDialog = new PackageSelectionDialog(new Shell(), "Pojo Class Package",
				"Choose the package from below", getPackagesInProject(workingJavaProject, srcPath, "source"));
		if (selectionDialog.open() != CANCEL) {
			return (IPackageFragment) selectionDialog.getFirstResult();
		}

		return null;
	}

	/**
	 * @param method
	 * @param type
	 * @throws Exception
	 */
	public static void overrideConstructor(final IMethod method, final IType type) throws Exception {
		final Map<String, Object> placeHolders = new HashMap<String, Object>();
		final StringBuilder methodArgs = new StringBuilder(LEFT_PAREN);
		final StringBuilder methodBody = new StringBuilder("super" + LEFT_PAREN);

		final String[] paramTypes = method.getParameterTypes();
		final String[] paramNames = method.getParameterNames();
		final String[] exptns = method.getExceptionTypes();

		final StringBuilder methodComments = new StringBuilder("/**" + NEWLINE);
		methodComments.append(SPACE + ASTERISK + NEWLINE);
		/*methodComments.append(SPACE + "* This is Constructor for " + type.getElementName()
				+ NEWLINE);*/

		if (paramTypes != null && paramTypes.length > 0) {
			int count = 0;
			for (final String paramType : paramTypes) {
				methodArgs.append(getSignatureSimpleName(paramType) + SPACE + paramNames[count]);
				methodArgs.append(count < paramTypes.length - 1 ? COMMA + SPACE : EMPTY_STR);
				methodComments.append(SPACE + "* @param" + SPACE + paramNames[count] + NEWLINE);
				methodBody.append(paramNames[count]);
				methodBody.append(count < paramTypes.length - 1 ? COMMA + SPACE : EMPTY_STR);
				count++;
			}
		}
		methodComments.append(SPACE + "*/");
		methodBody.append(RIGHT_PAREN + SEMICOLON);
		methodArgs.append(RIGHT_PAREN);

		placeHolders.put(METHOD_ARGS_STR, methodArgs.toString());
		placeHolders.put(METHOD_NAME_STR, type.getElementName());
		placeHolders.put(METHOD_COMMENTS_STR, methodComments.toString());
		placeHolders.put(METHOD_BODY_STR, methodBody.toString());

		placeHolders.put(METHOD_MODIFIER_STR, MODIFIER_PUBLIC);

		final String newConsSrc = methodComments.toString() + NEWLINE + MODIFIER_PUBLIC + SPACE + type.getElementName()
				+ methodArgs.toString() + LEFT_CURL + NEWLINE + methodBody.toString() + NEWLINE + RIGHT_CURL;
		type.createMethod(newConsSrc, null, true, new NullProgressMonitor());
	}

	/**
	 * @param compilationUnit
	 * @return
	 * @throws Exception
	 */
	public static List<FastCodeReturn> getLocalVarFromCompUnit(final ICompilationUnit compilationUnit, final IEditorPart editorPart)
			throws Exception {
		final ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(compilationUnit);
		parser.setResolveBindings(true);
		final IJavaElement currentMethod = findSelectedJavaElement(compilationUnit, editorPart);
		if (currentMethod == null) {
			return new ArrayList<FastCodeReturn>();
		}
		final CompilationUnit parse = (CompilationUnit) parser.createAST(null);
		final FastCodeVisitor methodVisitor = new FastCodeVisitor(currentMethod.getElementName(), compilationUnit);
		parse.accept(methodVisitor);
		final Shell parentShell = MessageUtil.getParentShell();
		final List<FastCodeReturn> variablesList = methodVisitor.getFastCodeReturns();
		for (final Object obj : methodVisitor.getMethodDecln().parameters()) {
			final VariableDeclaration variableDeclaration = (VariableDeclaration) obj;
			final String type = variableDeclaration.getStructuralProperty(SingleVariableDeclaration.TYPE_PROPERTY).toString();
			final FastCodeReturn fastCodeReturn = new FastCodeReturn(variableDeclaration.getName().toString(), new FastCodeType(
					getFQNameFromFieldTypeName(type.toString(), compilationUnit)));
			variablesList.add(fastCodeReturn);
		}
		return variablesList;
	}

	/**
	 * @param actionSelected
	 * @param variablesList
	 * @return
	 * @throws Exception
	 */
	public static List<FastCodeReturn> getLocalVariablesOfType(final ICompilationUnit compilationUnit, final IEditorPart editorPart,
			final String localVarType) throws Exception {
		final List<FastCodeReturn> variablesList = getLocalVarFromCompUnit(compilationUnit, editorPart);
		final List<FastCodeReturn> membersToWorkOn = new ArrayList<FastCodeReturn>();

		for (final FastCodeReturn fastCodeReturn : variablesList) {
			if (fastCodeReturn.getType().getName().equals(localVarType)) {
				membersToWorkOn.add(fastCodeReturn);
			}
		}
		return membersToWorkOn;
	}

	/**
	 * @param compUnit
	 * @param editorPart
	 * @return
	 * @throws Exception
	 */
	public static IJavaElement findSelectedJavaElement(final ICompilationUnit compUnit, final IEditorPart editorPart) throws Exception {
		IMethod methodSelected = null;
		if (compUnit != null) {
			final ISelection selection = editorPart.getEditorSite().getSelectionProvider().getSelection();
			if (selection != null && selection instanceof ITextSelection) {
				final IJavaElement element = compUnit.getElementAt(((ITextSelection) selection).getOffset());
				if (element != null && element.getElementType() == TYPE) {
					return null;
				} else if (element != null && element.getElementType() == METHOD) {
					methodSelected = (IMethod) element;
				}
			}
		}
		return methodSelected;
	}

	/**
	 *
	 * @param snippet
	 * @param compUnit
	 * @return
	 * @throws Exception
	 */
	public static IType createInnerClass(final String snippet, final ICompilationUnit compUnit) throws Exception {
		final String className = StringUtil.parseClassName(snippet);
		if (className == null) {
			if (!MessageDialog.openConfirm(new Shell(), "Warning", "Template may be incorrect. Would you like to proceed?")) {
				return null;
			}
		}

		return compUnit.findPrimaryType().createType(snippet, null, false, null);
	}

	/**
	 *
	 */
	public static void loadComments() {
		InputStream input = null;
		Properties properties;
		final String propertiesFile = "checkin-comments.properties";
		final FastCodeCheckinCache checkinCache = FastCodeCheckinCache.getInstance();

		try {
			input = FileLocator.openStream(Activator.getDefault().getBundle(), new Path("resources/" + propertiesFile), false);
			properties = new Properties();
			properties.load(input);
			for (final Entry<Object, Object> entry : properties.entrySet()) {
				checkinCache.getCommentKeyDetail().put((String) entry.getKey(), (String) entry.getValue());
				// fastCodeCache.getCommentKey().add((String) entry.getKey());
			}

		} catch (final Exception ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (final IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * @return
	 * @throws FastCodeRepositoryException
	 */
	public static RepositoryService getRepositoryServiceClass() throws FastCodeRepositoryException {
		final IPreferenceStore preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		final String repositoryName = preferenceStore.getString(P_REPOSITORY_NAME);
		switch (REPOSITORY.getRepository(repositoryName)) {
		case SVN:
			return new SVNRepositoryService();
		case CVS:
			return null;
		case GIT:
			return null;
		case PERFORCE:
			return null;
		case MKS:
			return null;
		default:
			break;
		}
		return null;

	}

	/**
	 * @param projectName
	 */
	public static void refreshProject(final String projectName) {
		try {
			System.out.println(ResourcesPlugin.getWorkspace().isTreeLocked());
			final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			//project.
			while (ResourcesPlugin.getWorkspace().isTreeLocked()) {
				Thread.sleep(100);
			}
			ResourcesPlugin.getWorkspace().getRoot().getProject(projectName)
					.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		} catch (final CoreException ex) {
			ex.printStackTrace();
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * @param fileName
	 * @param file
	 * @throws Exception
	 */
	public static boolean isFileSaved(final String fileName, final IFile file) throws Exception {
		for (final IFileBuffer fileBuffer : FileBuffers.getTextFileBufferManager().getFileBuffers()) {
			final IPath path = fileBuffer.getLocation();
			if (path.toString().equals(file.getFullPath().toString())) {
				if (fileBuffer.isDirty()) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * @param compilationUnit
	 * @throws Exception
	 */
	public static void overrideBaseClassMethods(final ICompilationUnit compilationUnit) throws Exception {
		final FastCodeType parentType = new FastCodeType(compilationUnit.findPrimaryType()).getSuperType();
		if (parentType != null) {
			final Map<String, IMethod> nameIMethodMap = new HashMap<String, IMethod>();
			final List<String> mthNameList = new ArrayList<String>();
			getMethodsFromParentClass(parentType, nameIMethodMap, mthNameList);
			if (mthNameList.size() > 0) {
				for (final String methodName : mthNameList) {
					final IMethod method = nameIMethodMap.get(methodName);
					if (doesMethodExistsInType(compilationUnit.findPrimaryType(), method.getElementName())) {
						return;
					}
				}
				final StringSelectionDialog selectionDialog = new StringSelectionDialog(new Shell(), "Methods in Parent class "
						+ parentType.getName(), "Choose methods to override from " + parentType.getName() + DOT,
						mthNameList.toArray(new String[0]), true);
				if (selectionDialog.open() != CANCEL) {

					final Object[] mthsToOverride = selectionDialog.getResult();

					for (final Object mthName : mthsToOverride) {

						final IMethod method = nameIMethodMap.get(mthName);
						final FastCodeContext fastCodeContext = new FastCodeContext(parentType.getiType());
						final MethodBuilder methodBuilder = new SimilarMethodBuilder(fastCodeContext);
						final CreateSimilarDescriptorClass createSimilarDescriptorClass = new CreateSimilarDescriptorClass.Builder()
								.withClassType(CLASS_TYPE.CLASS).build();

						methodBuilder.buildMethod(method, compilationUnit.findPrimaryType(), null, createSimilarDescriptorClass);
					}
				}
			}
		}
	}

	/**
	 *
	 * @param file
	 * @param type
	 * @return
	 * @throws JavaModelException
	 */

	public static boolean isFileReferenced(final IFile file, final int type) throws JavaModelException {
		final IProject prj = file.getProject();
		final IJavaProject javaProject = JavaCore.create(prj);
		/*
		final IPath path = file.getFullPath();
		*/
		final IJavaElement element = JavaCore.create(file);
		if (element instanceof ICompilationUnit) {
			final IType iType = javaProject.findType(((ICompilationUnit) element).findPrimaryType().getFullyQualifiedName());

			final IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
			final List<IJavaElement> javaElements = new SearchUtil().search(iType, type, scope);
			if (javaElements.size() > 0) {
				return true;
				/*for (final IJavaElement element : javaElements) {
					if (type == IJavaSearchConstants.TYPE || type == IJavaSearchConstants.METHOD) {
						return true;
					}
				}*/
			}
		}
		return false;

	}

	public static boolean isFileReferenced(final File file, final int type) throws JavaModelException {
		return isFileReferenced(getIFileFromFile(file), type);

	}

	/**
	 *
	 * @param file
	 * @return
	 */
	public static IFile getIFileFromFile(final File file) {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IPath location = Path.fromOSString(file.getAbsolutePath());
		final IFile ifile = workspace.getRoot().getFileForLocation(location);
		return ifile;
	}

	/**
	 *
	 * @param prj
	 * @return
	 */
	public static IJavaProject getJavaProject(final IProject prj) {
		try {
			if (prj.hasNature(JavaCore.NATURE_ID)) {
				final IJavaProject javaProject = JavaCore.create(prj);
				return javaProject;
			}
		} catch (final CoreException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 *
	 * @param javaProject
	 * @return
	 */
	public static Set<String> getBinaryFoldersForJavaProject(final IJavaProject javaProject) {
		final Set<String> binaryFolders = new TreeSet<String>();

		try {
			// Get default output folder location.
			IPath outputPath = javaProject.getOutputLocation();
			if (outputPath != null) {
				binaryFolders.add(outputPath.toString());
			}

			// Find all the output folders by looking for source folders.
			for (final IClasspathEntry entry : javaProject.getRawClasspath()) {
				if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
					outputPath = entry.getOutputLocation();
					if (outputPath == null) {
						continue;
					}

					binaryFolders.add(outputPath.toString());
				}
			}
		} catch (final JavaModelException e) {
			e.printStackTrace();
		}
		return binaryFolders;
	}

	/**
	 *
	 * @param folder
	 * @return
	 */
	public static boolean isBinaryResource(final IResource resource) {
		final IProject iProject = resource.getProject();
		final IJavaProject javaProject = getJavaProject(iProject);
		if (javaProject == null) {
			return false;
		}

		final Set<String> binaryFolders = getBinaryFoldersForJavaProject(javaProject);
		for (final String binPath : binaryFolders) {
			if (resource.getFullPath().toString().startsWith(binPath)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param project
	 * @return
	 */
	public static Object createCodeFormatter(final IProject project) {
		final IJavaProject javaProject = JavaCore.create(project);
		final Map options = javaProject.getOptions(true);
		return ToolFactory.createCodeFormatter(options);
	}

	/**
	 * @param contents
	 * @param codeFormatter
	 * @return
	 */
	public static String formatCode(String contents, final Object codeFormatter) {
		if (codeFormatter instanceof CodeFormatter) {
			final IDocument doc = new Document(contents);
			final TextEdit edit = ((CodeFormatter) codeFormatter).format(CodeFormatter.K_COMPILATION_UNIT, doc.get(), 0,
					doc.get().length(), 0, null);
			if (edit != null) {
				try {
					edit.apply(doc);
					contents = doc.get();
				} catch (final Exception e) {
					System.out.println(e);
				}
			}
		}
		return contents;
	}

	/**
	 * @param msg
	 * @return
	 */
	public static boolean showWarning(final String msg, final String button1Name, final String button2Name) {
		final int closeWindow = openMessageDilalog(msg, button1Name, button2Name);

		if (closeWindow != -1) {
			if (closeWindow == 0) {

				return true;

			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param msg
	 * @return
	 */
	public static int openMessageDilalog(final String msg, final String button1Name, final String button2Name) {

		final MessageDialog dialog = new MessageDialog(new Shell(), "Warning", null, msg, MessageDialog.WARNING, new String[] {
				button1Name, button2Name }, 0) {

			@Override
			protected void buttonPressed(final int buttonId) {
				setReturnCode(buttonId);
				close();

			}
		};

		dialog.open();

		return dialog.getReturnCode();

	}

	/**
	 * @param snippet
	 * @param packageFragment
	 * @param project
	 * @param clasName
	 * @return
	 * @throws Exception
	 */
	public static String buildClass(String snippet, IPackageFragment packageFragment, IJavaProject project, final String clasName)
			throws Exception {
		String packageDeclaration;
		String finalSnippet = snippet;
		final Map<String, Object> placeHolders = new HashMap<String, Object>();
		getGlobalSettings(placeHolders);
		final GlobalSettings globalSettings = getInstance();
		final String classHeader = evaluateByVelocity(globalSettings.getClassHeader(), placeHolders);
		placeHolders.put(CLASS_HEADER_STR, classHeader);

		String className = null;
		if (!isEmpty(snippet)) {
			className = StringUtil.parseClassName(snippet);
		}
		if (className == null) {
			className = clasName;
		}
		if (className == null) {
			if (!MessageDialog.openConfirm(new Shell(), "Warning", "Template may be incorrect. Would you like to proceed?")) {
				return null;
			}
		}
		if (project == null) {
			project = getWorkingJavaProjectFromUser();
		}
		final String srcPath = getDefaultPathFromProject(project, "source", EMPTY_STR);
		if (packageFragment == null) {
			final PackageSelectionDialog selectionDialog = new PackageSelectionDialog(new Shell(), "Package ",
					"Choose a package from below", getPackagesInProject(project, srcPath, "source"));
			if (selectionDialog.open() != CANCEL) {
				packageFragment = (IPackageFragment) selectionDialog.getFirstResult();
			} else {
				throw new Exception("Can not create the class without package.Select Package and then try again");
			}
		}
		if (!isEmpty(clasName) && isEmpty(snippet)) {
			String classBody = globalSettings.getClassBody();
			placeHolders.put(CLASS_ANNOTATIONS_STR, EMPTY_STR);
			if (!placeHolders.containsKey("super_class")) {
				classBody = replacePlaceHolderWithBlank(classBody, "extends", "super_class", "implements");
			}
			if (!placeHolders.containsKey("interfaces")) {
				classBody = replacePlaceHolderWithBlank(classBody, "implements", "interfaces", "{");
			}
			if (!placeHolders.containsKey(CLASS_MODIFIER_STR)) {
				classBody = replacePlaceHolderWithBlank(classBody, null, CLASS_MODIFIER_STR, makePlaceHolder(CLASS_TYPE_STR));
			}
			placeHolders.put(PACKAGE_NAME_STR, packageFragment.getElementName());
			placeHolders.put(CLASS_NAME_STR, clasName);
			getGlobalSettings(placeHolders);

			placeHolders.put(CLASS_BODY_STR, isEmpty(snippet) ? EMPTY_STR : snippet);
			placeHolders.put(CLASS_TYPE_STR, CLASS_TYPE.CLASS.value().toLowerCase());
			placeHolders.put(CLASS_IMPORTS_STR, EMPTY_STR);

			getGlobalSettings(placeHolders);
			finalSnippet = snippet = evaluateByVelocity(classBody, placeHolders);
		}
		packageDeclaration = PACKAGE_STR + SPACE + packageFragment.getElementName() + SEMICOLON + NEWLINE + NEWLINE;

		if (!snippet.trim().contains(packageDeclaration.trim()) && !isEmpty(packageFragment.getElementName())) {
			finalSnippet = classHeader + packageDeclaration + snippet;
		}
		if (isEmpty(packageFragment.getElementName())) {
			finalSnippet = finalSnippet.replace("package" + SPACE + SEMICOLON, EMPTY_STR).trim();
		}
		return finalSnippet;
	}

	/**
	 * @param elementName
	 * @return
	 */
	public static Image getImagefromFCCacheMap(final String elementName) {
		final FastCodeCache fastCodeCache = FastCodeCache.getInstance();
		Image image = null;
		image = fastCodeCache.getEntityImageMap().get(elementName);
		if (image != null && !image.isDisposed()) {
			return image;
		}
		return image;

	}

	/**
	 * @param elementName
	 * @param elementImage
	 */
	public static void populateFCCacheEntityImageMap(final String elementName, final Image elementImage) {
		final FastCodeCache fastCodeCache = FastCodeCache.getInstance();
		if (!fastCodeCache.getEntityImageMap().containsKey(elementName)) {
			fastCodeCache.getEntityImageMap().put(elementName, elementImage);
		}
	}

	/**
	 * Gets the image.
	 *
	 * @param imageName
	 *            the image name
	 * @return the image
	 */
	public static Image getImage(String imageName) {
		Image image;
		URL url = null;
		if (imageName == null) {
			return null;
		}
		image = PlatformUI.getWorkbench().getSharedImages().getImage(imageName);
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
		image = descriptor.createImage();
		return image;
	}

	public static void checkForJavaProjectInWorkspace() {

		// Warning msg to check if the JavaProject is there in workspace.
					int i=0;
					final IProject[] projectArr = ResourcesPlugin.getWorkspace().getRoot().getProjects();
					if (projectArr ==null || projectArr.length ==0 ){
						Display.getDefault().syncExec(new Runnable() {
							@Override
							public void run() {
						MessageDialog.openWarning(new Shell(), "Warning", "To use fastcode plugin, there should be atleast one java project in workspace");
							}
							});
						return;
						}


					for (final IProject project : projectArr) {

						final IJavaProject javaProject = JavaCore.create(project);
						if (javaProject != null){
							i=1;
							break;
						}
						else if (javaProject == null || !javaProject.exists()) {
							i=2;
							return;
						}
					}
					if(i==2){
					Display.getDefault().syncExec(new Runnable() {
						@Override
						public void run() {
					MessageDialog.openWarning(new Shell(), "Warning", "To use fastcode plugin, there should be atleast one java project in workspace");
						}
					});
					return;
					}
	}

}
