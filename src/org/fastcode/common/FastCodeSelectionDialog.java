/**
 *
 */
package org.fastcode.common;

import static org.eclipse.jdt.core.Flags.isPrivate;
import static org.eclipse.jdt.core.Flags.isProtected;
import static org.eclipse.jdt.core.Flags.isPublic;
import static org.eclipse.jdt.core.IJavaElement.FIELD;
import static org.eclipse.jdt.core.IJavaElement.JAVA_PROJECT;
import static org.eclipse.jdt.core.IJavaElement.METHOD;
import static org.eclipse.jdt.core.IJavaElement.PACKAGE_FRAGMENT;
import static org.eclipse.jdt.core.IJavaElement.TYPE;
import static org.eclipse.jdt.core.IMemberValuePair.K_CLASS;
import static org.eclipse.jdt.core.IMemberValuePair.K_STRING;
import static org.eclipse.jdt.core.Signature.getSignatureSimpleName;
import static org.fastcode.common.FastCodeConstants.ASTERISK;
import static org.fastcode.common.FastCodeConstants.COLON;
import static org.fastcode.common.FastCodeConstants.COMMA;
import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.DOT_CHAR;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.ENUM;
import static org.fastcode.common.FastCodeConstants.EQUAL;
import static org.fastcode.common.FastCodeConstants.EQUAL_WITH_SPACE;
import static org.fastcode.common.FastCodeConstants.LEFT_PAREN;
import static org.fastcode.common.FastCodeConstants.METHOD_PRIVATE;
import static org.fastcode.common.FastCodeConstants.METHOD_PROTECTED;
import static org.fastcode.common.FastCodeConstants.METHOD_PUBLIC;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_CLASS;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_ENUM;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_FILE;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_FOLDER;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_INTERFACE;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_JAVA_PROJECT;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_PACKAGE;
import static org.fastcode.common.FastCodeConstants.QUOTE_STR;
import static org.fastcode.common.FastCodeConstants.RIGHT_PAREN;
import static org.fastcode.common.FastCodeConstants.SPACE;
import static org.fastcode.common.FastCodeConstants.UNDERSCORE;
import static org.fastcode.common.FastCodeConstants.VALUE;
import static org.fastcode.util.JUnitUtil.isJunitTest;
import static org.fastcode.util.JUnitUtil.isNegativeJunit;
import static org.fastcode.util.SourceUtil.getAlteredPackageName;
import static org.fastcode.util.SourceUtil.getImagefromFCCacheMap;
import static org.fastcode.util.SourceUtil.isFileSaved;
import static org.fastcode.util.SourceUtil.populateFCCacheEntityImageMap;
import static org.fastcode.util.StringUtil.flattenType;
import static org.fastcode.util.StringUtil.makeWord;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.fastcode.Activator;
import org.fastcode.popup.actions.snippet.FastCodeCache;
import org.fastcode.setting.GlobalSettings;
import org.fastcode.util.FastCodeFileForCheckin;

// TODO: Auto-generated Javadoc
/**
 * The Class FastCodeSelectionDialog.
 *
 * @author Gautam
 */
public abstract class FastCodeSelectionDialog extends ElementListSelectionDialog {

	/** The type. */
	final protected int	type;

	/**
	 * Instantiates a new fast code selection dialog.
	 *
	 * @param parent
	 *            the parent
	 * @param title
	 *            the title
	 * @param message
	 *            the message
	 * @param elements
	 *            the elements
	 * @param type
	 *            the type
	 * @param multipleSelection
	 *            the multiple selection
	 */
	public FastCodeSelectionDialog(final Shell parent, final String title, final String message, final Object[] elements, final int type,
			final boolean multipleSelection) {

		super(parent, new FastCodeLabelProvider(type));
		setAllowDuplicates(false);
		setElements(elements);
		setMultipleSelection(multipleSelection);
		setTitle(title);
		setMessage(message);
		setEmptyListMessage("You need to select at least one item.");
		this.type = type;
	}

	/**
	 * Instantiates a new fast code selection dialog.
	 *
	 * @param parent
	 *            the parent
	 * @param title
	 *            the title
	 * @param message
	 *            the message
	 * @param elements
	 *            the elements
	 * @param type
	 *            the type
	 * @param multipleSelection
	 *            the multiple selection
	 * @param filter
	 *            the filter
	 */
	public FastCodeSelectionDialog(final Shell parent, final String title, final String message, final Object[] elements, final int type,
			final boolean multipleSelection, final String filter) {

		super(parent, new FastCodeLabelProvider(type));
		setAllowDuplicates(false);
		setElements(elements);
		setMultipleSelection(multipleSelection);
		setTitle(title);
		setMessage(message);
		setEmptyListMessage("You need to select at least one item.");
		setFilter(filter);
		this.type = type;
	}

	/**
	 * The Class FastCodeLabelProvider.
	 *
	 * @author Gautam
	 */
	private static class FastCodeLabelProvider implements ILabelProvider {

		/** The image. */
		private Image		image;

		/** The type. */
		final protected int	type;
		FastCodeCache		fastCodeCache	= FastCodeCache.getInstance();

		/**
		 * Instantiates a new fast code label provider.
		 *
		 * @param type
		 *            the type
		 */
		public FastCodeLabelProvider(final int type) {
			this.type = type;
		}

		/**
		 * Gets the image.
		 *
		 * @param element
		 *            the element
		 * @return the image
		 */
		@Override
		public Image getImage(final Object element) {
			try {
				final GlobalSettings globalSettings = GlobalSettings.getInstance();
				String image = null;
				Image elementImage = null;
				if (this.type == TYPE) {
					return element instanceof IType ? getImageForFCType(new FastCodeType((IType) element)) : null;
				} else if (this.type == FIELD) {
					return getImageForField(new FastCodeField((IField) element));
				} else if (this.type == METHOD) {
					return getImageForMethod((IMethod) element);
				} else if (this.type == PACKAGE_FRAGMENT) {
					return getImageForPackage((IPackageFragment) element);
				} else if (this.type == JAVA_PROJECT) {
					if (this.fastCodeCache.getEntityImageMap().containsKey(PLACEHOLDER_JAVA_PROJECT)) {
						return getImagefromFCCacheMap(PLACEHOLDER_JAVA_PROJECT);
					}
					image = globalSettings.getPropertyValue(PLACEHOLDER_JAVA_PROJECT.toUpperCase() + UNDERSCORE + "IMAGE", EMPTY_STR);
					elementImage = getImage(image);
					populateFCCacheEntityImageMap(PLACEHOLDER_JAVA_PROJECT, elementImage);
					return elementImage;
				} else if (element instanceof FastCodeType) {
					return getImageForFCType((FastCodeType) element);
				} else if (element instanceof File) {
					if (this.fastCodeCache.getEntityImageMap().containsKey(PLACEHOLDER_FILE)) {
						return getImagefromFCCacheMap(PLACEHOLDER_FILE);
					}
					image = globalSettings.getPropertyValue(PLACEHOLDER_FILE.toUpperCase() + UNDERSCORE + "IMAGE", EMPTY_STR);
					elementImage = getImage(image);
					populateFCCacheEntityImageMap(PLACEHOLDER_FILE, elementImage);
					return elementImage;//("file_obj.gif");
				} else if (element instanceof FastCodeField) {
					return this.getImageForField((FastCodeField) element);
				} else if (element instanceof FastCodeFileForCheckin) {
					return getImageForCheckinFile((FastCodeFileForCheckin) element);
				}
			} catch (final Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}

		/**
		 * @param element
		 * @return
		 */
		private Image getImageForPackage(final IPackageFragment element) {
			final GlobalSettings globalSettings = GlobalSettings.getInstance();
			String imageName = null;
			Image image = null;
			try {
				if (element.getClassFiles() == null) {
					if (this.fastCodeCache.getEntityImageMap().containsKey("empty" + PLACEHOLDER_PACKAGE)) {
						return getImagefromFCCacheMap("empty" + PLACEHOLDER_PACKAGE);
					}
					imageName = globalSettings.getPropertyValue("EMPTY" + PLACEHOLDER_PACKAGE.toUpperCase() + UNDERSCORE + "IMAGE",
							EMPTY_STR);
					image = getImage(imageName);
					populateFCCacheEntityImageMap("empty" + PLACEHOLDER_PACKAGE, image);

				} else {
					if (this.fastCodeCache.getEntityImageMap().containsKey(PLACEHOLDER_PACKAGE)) {
						return getImagefromFCCacheMap(PLACEHOLDER_PACKAGE);
					}
					imageName = globalSettings.getPropertyValue(PLACEHOLDER_PACKAGE.toUpperCase() + UNDERSCORE + "IMAGE", EMPTY_STR);
					image = getImage(imageName);
					populateFCCacheEntityImageMap(PLACEHOLDER_PACKAGE, image);
				}
			} catch (final JavaModelException ex) {
				ex.printStackTrace();
			}

			return image;
		}

		/**
		 * @param element
		 * @return
		 */
		private Image getImageForCheckinFile(final FastCodeFileForCheckin element) {
			final GlobalSettings globalSettings = GlobalSettings.getInstance();
			String imageName;
			Image image = null;
			if (element.getFile().isDirectory()) {
				if (this.fastCodeCache.getEntityImageMap().containsKey(PLACEHOLDER_FOLDER)) {
					return getImagefromFCCacheMap(PLACEHOLDER_FOLDER);
				}
				imageName = globalSettings.getPropertyValue(PLACEHOLDER_FOLDER.toUpperCase() + UNDERSCORE + "IMAGE", EMPTY_STR);
				image = getImage(imageName);
				populateFCCacheEntityImageMap(PLACEHOLDER_FOLDER, image);
			} else {
				if (this.fastCodeCache.getEntityImageMap().containsKey(PLACEHOLDER_FILE)) {
					return getImagefromFCCacheMap(PLACEHOLDER_FILE);
				}
				imageName = globalSettings.getPropertyValue(PLACEHOLDER_FILE.toUpperCase() + UNDERSCORE + "IMAGE", EMPTY_STR);
				image = getImage(imageName);
				populateFCCacheEntityImageMap(PLACEHOLDER_FILE, image);
			}
			return image;//("file_obj.gif");
		}

		/**
		 * @param element
		 * @return
		 * @throws Exception
		 */
		private Image getImageForField(final FastCodeField element) throws Exception {
			String imageName = null;
			final GlobalSettings globalSettings = GlobalSettings.getInstance();
			Image image = null;
			if (isPrivate(element.getField().getFlags())) {
				if (this.fastCodeCache.getEntityImageMap().containsKey("field_private")) {
					return getImagefromFCCacheMap("field_private");
				}
				imageName = ISharedImages.IMG_FIELD_PRIVATE;
				image = getImage(imageName);//"int_obj.gif";
				populateFCCacheEntityImageMap("field_private", image);
			} else if (isProtected(element.getField().getFlags())) {
				if (this.fastCodeCache.getEntityImageMap().containsKey("field_protected")) {
					return getImagefromFCCacheMap("field_protected");
				}
				imageName = ISharedImages.IMG_FIELD_PROTECTED;
				image = getImage(imageName);//"int_obj.gif";
				populateFCCacheEntityImageMap("field_protected", image);
			} else if (isPublic(element.getField().getFlags())) {
				if (this.fastCodeCache.getEntityImageMap().containsKey("field_public")) {
					return getImagefromFCCacheMap("field_public");
				}
				imageName = ISharedImages.IMG_FIELD_PUBLIC;
				image = getImage(imageName);//"int_obj.gif";
				populateFCCacheEntityImageMap("field_public", image);
			} else if (element.getField().isEnumConstant()) {
				if (this.fastCodeCache.getEntityImageMap().containsKey("field_enum")) {
					return getImagefromFCCacheMap("field_enum");
				}
				imageName = globalSettings.getPropertyValue(PLACEHOLDER_ENUM.toUpperCase() + UNDERSCORE + "IMAGE", EMPTY_STR);
				image = getImage(imageName);//"int_obj.gif";
				populateFCCacheEntityImageMap("field_enum", image);
			} else {
				if (this.fastCodeCache.getEntityImageMap().containsKey("field")) {
					return getImagefromFCCacheMap("field");
				}
				imageName = ISharedImages.IMG_FIELD_DEFAULT;
				image = getImage(imageName);//"int_obj.gif";
				populateFCCacheEntityImageMap("field", image);
			}
			return image;
		}

		/**
		 * Gets the image for type.
		 *
		 * @param type
		 *            the type
		 * @return the image for type
		 * @throws Exception
		 *             the exception
		 */
		private Image getImageForType(final IType type) throws Exception {
			String imageName = null;
			final GlobalSettings globalSettings = GlobalSettings.getInstance();
			Image image = null;
			if (type.isInterface()) {
				if (this.fastCodeCache.getEntityImageMap().containsKey(PLACEHOLDER_INTERFACE)) {
					return getImagefromFCCacheMap(PLACEHOLDER_INTERFACE);
				}
				imageName = globalSettings.getPropertyValue(PLACEHOLDER_INTERFACE.toUpperCase() + UNDERSCORE + "IMAGE", EMPTY_STR);
				image = getImage(imageName);//"int_obj.gif";
				populateFCCacheEntityImageMap(PLACEHOLDER_INTERFACE, image);

			} else if (type.isClass()) {
				if (this.fastCodeCache.getEntityImageMap().containsKey(PLACEHOLDER_CLASS)) {
					return getImagefromFCCacheMap(PLACEHOLDER_CLASS);
				}
				imageName = globalSettings.getPropertyValue(PLACEHOLDER_CLASS.toUpperCase() + UNDERSCORE + "IMAGE", EMPTY_STR);
				image = getImage(imageName);//"classs_obj.gif";
				populateFCCacheEntityImageMap(PLACEHOLDER_CLASS, image);
			}
			return image;
		}

		/**
		 * Gets the image for FCtype.
		 *
		 * @param FCtype
		 *            the type
		 * @return the image for type
		 * @throws Exception
		 *             the exception
		 */
		private Image getImageForFCType(final FastCodeType type) throws Exception {
			String imageName = null;
			final GlobalSettings globalSettings = GlobalSettings.getInstance();
			Image image = null;
			if (type.isInterface()) {
				if (this.fastCodeCache.getEntityImageMap().containsKey(PLACEHOLDER_INTERFACE)) {
					return getImagefromFCCacheMap(PLACEHOLDER_INTERFACE);
				}
				imageName = globalSettings.getPropertyValue(PLACEHOLDER_INTERFACE.toUpperCase() + UNDERSCORE + "IMAGE", EMPTY_STR);
				image = getImage(imageName);//"int_obj.gif";
				populateFCCacheEntityImageMap(PLACEHOLDER_INTERFACE, image);
			} else if (type.isClass()) {
				if (this.fastCodeCache.getEntityImageMap().containsKey(PLACEHOLDER_CLASS)) {
					return getImagefromFCCacheMap(PLACEHOLDER_CLASS);
				}
				imageName = globalSettings.getPropertyValue(PLACEHOLDER_CLASS.toUpperCase() + UNDERSCORE + "IMAGE", EMPTY_STR);
				image = getImage(imageName);//"classs_obj.gif";
				populateFCCacheEntityImageMap(PLACEHOLDER_CLASS, image);
			} else if (type.isEnum()) {
				if (this.fastCodeCache.getEntityImageMap().containsKey(ENUM)) {
					return getImagefromFCCacheMap(ENUM);
				}
				imageName = globalSettings.getPropertyValue(ENUM.toUpperCase() + UNDERSCORE + "IMAGE", EMPTY_STR);
				image = getImage(imageName);//"classs_obj.gif";
				populateFCCacheEntityImageMap("enum", image);
			}
			return image;
		}

		/**
		 * Gets the image for field.
		 *
		 * @param field
		 *            the field
		 * @return the image for field
		 * @throws Exception
		 *             the exception
		 */
		private Image getImageForField(final IField field) throws Exception {
			String imageName = null;

			if (isPrivate(field.getFlags())) {
				imageName = ISharedImages.IMG_FIELD_PRIVATE;
			} else if (isProtected(field.getFlags())) {
				imageName = ISharedImages.IMG_FIELD_PROTECTED;
			} else if (isPublic(field.getFlags())) {
				imageName = ISharedImages.IMG_FIELD_PUBLIC;
			}

			return this.getImage(imageName);
		}

		/**
		 * Gets the image for method.
		 *
		 * @param method
		 *            the method
		 * @return the image for method
		 * @throws Exception
		 *             the exception
		 */
		private Image getImageForMethod(final IMethod method) throws Exception {
			String imageName = null;
			final GlobalSettings globalSettings = GlobalSettings.getInstance();
			Image image = null;
			if (isJunitTest(method.getDeclaringType())) {
				if (isNegativeJunit(method)) {
					if (this.fastCodeCache.getEntityImageMap().containsKey("method_negative_junit")) {
						return getImagefromFCCacheMap("method_negative_junit");
					}
					imageName = "junit-negative-small.gif";
					image = getImage(imageName);
					populateFCCacheEntityImageMap("method_negative_junit", image);
				} else {
					if (this.fastCodeCache.getEntityImageMap().containsKey("method_positive_junit")) {
						return getImagefromFCCacheMap("method_positive_junit");
					}
					imageName = "junit-positive-small.gif";
					image = getImage(imageName);
					populateFCCacheEntityImageMap("method_positive_junit", image);
				}
			} else {
				if (isPrivate(method.getFlags())) {
					if (this.fastCodeCache.getEntityImageMap().containsKey("method_private")) {
						return getImagefromFCCacheMap("method_private");
					}
					imageName = globalSettings.getPropertyValue(METHOD_PRIVATE.toUpperCase() + UNDERSCORE + "IMAGE", EMPTY_STR);
					image = getImage(imageName);
					populateFCCacheEntityImageMap("method_private", image);
				} else if (isProtected(method.getFlags())) {
					if (this.fastCodeCache.getEntityImageMap().containsKey("method_protected")) {
						return getImagefromFCCacheMap("method_protected");
					}
					imageName = globalSettings.getPropertyValue(METHOD_PROTECTED.toUpperCase() + UNDERSCORE + "IMAGE", EMPTY_STR);
					image = getImage(imageName);
					populateFCCacheEntityImageMap("method_protected", image);
				} else if (isPublic(method.getFlags())) {
					if (this.fastCodeCache.getEntityImageMap().containsKey("method_public")) {
						return getImagefromFCCacheMap("method_public");
					}
					imageName = globalSettings.getPropertyValue(METHOD_PUBLIC.toUpperCase() + UNDERSCORE + "IMAGE", EMPTY_STR);
					image = getImage(imageName);
					populateFCCacheEntityImageMap("method_public", image);
				}
			}
			return image;
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

		/**
		 * Gets the text.
		 *
		 * @param element
		 *            the element
		 * @return the text
		 */
		@Override
		public String getText(final Object element) {
			try {
				if (this.type == METHOD) {
					return this.createMethodLabel((IMethod) element);
				} else if (this.type == TYPE) {
					return element instanceof FastCodeType ? flattenType((FastCodeType) element, false) : createClassLabel((IType) element);
				} else if (this.type == FIELD) {
					return createFieldLabel((IField) element);
				} else if (this.type == PACKAGE_FRAGMENT) {
					//return ((IPackageFragment) element).getElementName();
					//return ((IPackageFragment) element).getPath().toString();
					return getAlteredPackageName((IPackageFragment) element);
				} else if (this.type == JAVA_PROJECT) {
					return ((IJavaProject) element).getElementName();
				} else if (element instanceof FastCodeMethod) {
					return this.createMethodLabel((FastCodeMethod) element);
				} else if (element instanceof FastCodeProperty) {
					return createPropertyLabel((FastCodeProperty) element);
				} else if (element instanceof FastCodeFile) {
					return createFileLabel((FastCodeFile) element);
				} else if (element instanceof FastCodeType) {
					return createTypeLabel((FastCodeType) element);
				} else if (element instanceof IMember) {
					return createMemberLabel((IMember) element);
				} else if (element instanceof FastCodeField) {
					return createFastCodeFieldLabel((FastCodeField) element);
				} else if (element instanceof FastCodeReturn) {
					return createFastCodeReturnLabel((FastCodeReturn) element);
				} else if (element instanceof File) {
					return createFileLabel((File) element);
				} else if (element instanceof IProject) {
					return ((IProject) element).getName();
				} else if (element instanceof FastCodeFileForCheckin) {
					return createCheckinFileLabel((FastCodeFileForCheckin) element);
				} else {

					final String value = (String) element;
					// return FastCodeSelectionDialog.this.convert(value);
					return value.equals(value.toUpperCase()) ? makeWord(value) : value;
				}
			} catch (final Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}

		private String createCheckinFileLabel(final FastCodeFileForCheckin element) throws Exception {
			return createFileLabel(element.getFile());
		}

		/**
		 * @param file
		 * @return
		 * @throws Exception
		 */
		public String createFileLabel(final File file) throws Exception {
			final IWorkspace workspace = ResourcesPlugin.getWorkspace();
			final IPath location = Path.fromOSString(file.getAbsolutePath());
			final IFile ifile = workspace.getRoot().getFileForLocation(location);
			if (!isFileSaved(ifile.getName(), ifile)) {
				return file.getName() + ASTERISK;
			}
			return file.getName();
		}

		private String createMemberLabel(final IMember element) {
			return element.getElementName();
		}

		/**
		 * Creates the file label.
		 *
		 * @param property
		 *            the property
		 * @return the string
		 */
		private String createFileLabel(final FastCodeFile file) {
			return file.getName();
		}

		/**
		 * Creates the type label.
		 *
		 * @param type
		 *            the type
		 * @return the string
		 */
		private String createTypeLabel(final FastCodeType type) {
			return type.getName();
		}

		/**
		 * Creates the property label.
		 *
		 * @param property
		 *            the property
		 * @return the string
		 */
		private String createPropertyLabel(final FastCodeProperty property) {
			final String propertyName = property.getName();
			final int off = propertyName.lastIndexOf(DOT);
			final String leading;
			if (off != -1) {
				leading = propertyName.substring(off + 1);
			} else {
				leading = propertyName;
			}
			return leading + "[" + propertyName + EQUAL + property.getValue() + "]";
		}

		/**
		 * Creates the method label.
		 *
		 * @param method
		 *            the method
		 * @return the string
		 * @throws JavaModelException
		 *             the java model exception
		 */
		private String createMethodLabel(final IMethod method) throws JavaModelException {

			final StringBuilder label = new StringBuilder();

			for (final IAnnotation annotation : method.getAnnotations()) {
				// label = annotation.getElementName();
				final StringBuilder annoAttrs = new StringBuilder();
				int cnt = 0;
				for (final IMemberValuePair memberValuePair : annotation.getMemberValuePairs()) {
					if (memberValuePair.getMemberName().equals(VALUE)) {
						annoAttrs.append(memberValuePair.getValue());
					} else {
						if (memberValuePair.getValueKind() == K_STRING) {
							annoAttrs.append(memberValuePair.getMemberName() + EQUAL_WITH_SPACE + QUOTE_STR + memberValuePair.getValue()
									+ QUOTE_STR);
						} else if (memberValuePair.getValueKind() == K_CLASS) {
							annoAttrs.append(memberValuePair.getMemberName() + EQUAL_WITH_SPACE + memberValuePair.getValue() + ".class");
						} else {
							annoAttrs.append(memberValuePair.getMemberName() + EQUAL_WITH_SPACE + memberValuePair.getValue());
						}
					}
					annoAttrs.append(cnt < annotation.getMemberValuePairs().length - 1 ? SPACE : EMPTY_STR);
					cnt++;
				}
				if (!EMPTY_STR.equals(annoAttrs.toString())) {
					annoAttrs.insert(0, LEFT_PAREN).append(RIGHT_PAREN);
				}
				// annoAttrs = new StringBuilder(annoAttrs.equals(EMPTY_STR) ?
				// annoAttrs : LEFT_PAREN + annoAttrs + RIGHT_PAREN);
				// label += annoAttrs + "\r" + NEWLINE;
			}
			label.append(SPACE + SPACE + method.getElementName());
			label.append(LEFT_PAREN);
			int count = 0;
			for (final String type : method.getParameterTypes()) {
				if (count != 0) {
					label.append(COMMA + SPACE);
				}
				label.append(getSignatureSimpleName(type) + SPACE + method.getParameterNames()[count++]);
			}
			label.append(RIGHT_PAREN);
			return label.toString().trim();
		}

		/**
		 * Creates the method label.
		 *
		 * @param method
		 *            the method
		 * @return the string
		 * @throws Exception
		 *             the exception
		 */
		private String createMethodLabel(final FastCodeMethod method) throws Exception {
			return method.getName();
		}

		/**
		 * Creates the class label.
		 *
		 * @param type
		 *            the type
		 * @return the string
		 * @throws JavaModelException
		 *             the java model exception
		 */
		private String createClassLabel(final IType type) throws JavaModelException {
			return type.getFullyQualifiedName();
		}

		/**
		 * Creates the fast code class label.
		 *
		 * @param fastCodeType
		 *            the fast code type
		 * @return the string
		 * @throws JavaModelException
		 *             the java model exception
		 */
		private String createFastCodeClassLabel(final FastCodeType fastCodeType) throws JavaModelException {
			return fastCodeType.toString();
		}

		/**
		 * Creates the field label.
		 *
		 * @param field
		 *            the field
		 * @return the string
		 * @throws JavaModelException
		 *             the java model exception
		 */
		private String createFieldLabel(final IField field) throws JavaModelException {
			// return field.getElementName() + "( " +
			// Signature.getSignatureSimpleName(field.getTypeSignature()) +
			// " : " + field.getTypeSignature() + ")";
			final String className = field.getCompilationUnit() == null ? field.getClassFile().getElementName() : field
					.getCompilationUnit().getElementName();
			return field.getElementName() + LEFT_PAREN + SPACE + Signature.getSignatureSimpleName(field.getTypeSignature()) + COLON
					+ className.substring(0, className.indexOf(DOT_CHAR)) + RIGHT_PAREN;
		}

		/**
		 * Creates the field label.
		 *
		 * @param field
		 *            the fast code field
		 * @return the string
		 * @throws JavaModelException
		 *             the java model exception
		 */
		private String createFastCodeFieldLabel(final FastCodeField field) throws JavaModelException {
			// return field.getElementName() + "( " +
			// Signature.getSignatureSimpleName(field.getTypeSignature()) +
			// " : " + field.getTypeSignature() + ")";
			final String className = field.getField().getCompilationUnit() == null ? field.getField().getClassFile().getElementName()
					: field.getField().getCompilationUnit().getElementName();
			return field.getFullName() + LEFT_PAREN + SPACE + Signature.getSignatureSimpleName(field.getField().getTypeSignature()) + COLON
					+ className.substring(0, className.indexOf(DOT_CHAR)) + RIGHT_PAREN;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
		 */
		@Override
		public void dispose() {
			/*	if (this.image != null && !this.image.isDisposed()) {
					this.image.dispose();
				}*/
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse
		 * .jface.viewers.ILabelProviderListener)
		 */
		@Override
		public void addListener(final ILabelProviderListener listener) {

		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java
		 * .lang.Object, java.lang.String)
		 */
		@Override
		public boolean isLabelProperty(final Object element, final String property) {
			if (element instanceof IMethod) {
				return property.equals("elementName");
			}
			return false;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse
		 * .jface.viewers.ILabelProviderListener)
		 */
		@Override
		public void removeListener(final ILabelProviderListener listener) {
		}

		private String createFastCodeReturnLabel(final FastCodeReturn fastCodeReturn) {
			return fastCodeReturn.getName() + LEFT_PAREN + fastCodeReturn.getType().getName() + RIGHT_PAREN;
		}
	}
}
