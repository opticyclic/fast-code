/**
 *
 */
package org.fastcode.util;

import static org.fastcode.common.FastCodeConstants.ASTERISK;
import static org.fastcode.common.FastCodeConstants.JAVADOC_COMMENT_END;
import static org.fastcode.common.FastCodeConstants.JAVADOC_COMMENT_START;
import static org.fastcode.common.FastCodeConstants.METHOD_RETURN_TYPE_VOID;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.common.FastCodeConstants.SPACE;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.ITerminalSymbols;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.fastcode.common.FastCodeAnnotation;
import org.fastcode.common.FastCodeType;
import org.fastcode.common.Pair;
import org.fastcode.preferences.Constants;

/**
 * @author Gautam
 *
 */
public class FastCodeUtil {

	/**
	 *
	 * @param inputList
	 * @return
	 */
	public static <T> List<T> getEmptyListForNull(final List<T> inputList) {
		return inputList == null ? Collections.EMPTY_LIST : inputList;
	}

	/**
	 *
	 * @param inputStream
	 */
	public static void closeInputStream(final InputStream inputStream) {
		if (inputStream == null) {
			return;
		}
		try {
			inputStream.close();
		} catch (final IOException ex) {
		}
	}

	/**
	 *
	 * @param methComment
	 * @param retType
	 * @param methodParms
	 * @return
	 */
	public static String generateMethodComments(final String methComment, final String retType, final List<Pair<String, String>> methodParms) {
		final StringBuilder methodComments = new StringBuilder(JAVADOC_COMMENT_START + NEWLINE);

		methodComments.append(SPACE + ASTERISK + NEWLINE);
		methodComments.append(SPACE + "* " + methComment + NEWLINE);
		for (final Pair<String, String> methodParm : getEmptyListForNull(methodParms)) {
			methodComments.append(SPACE + "* @param" + SPACE + methodParm.getRight() + NEWLINE);
		}
		if (!METHOD_RETURN_TYPE_VOID.endsWith(retType)) {
			methodComments.append(SPACE + "* @return" + NEWLINE);
		}
		methodComments.append(SPACE + ASTERISK + NEWLINE);
		methodComments.append(JAVADOC_COMMENT_END);
		return methodComments.toString();
	}

	/**
	 * @param annotationName
	 * @return
	 */
	public static List<FastCodeAnnotation> getFCAnnotationList(final String annotationName) {
		final List<FastCodeAnnotation> annotations = new ArrayList<FastCodeAnnotation>();
		annotations.add(new FastCodeAnnotation(null, new FastCodeType(annotationName)));
		return annotations;

	}

	/**
	 *
	 * @param inputList
	 * @return
	 */
	public static String[] getEmptyArrayForNull(final String[] inputArray) {
		return inputArray == null ? new String[] {} : inputArray;
	}

	/**
	 *
	 * @param inputList
	 * @return
	 */
	public static Object[] getEmptyArrayForNull(final Object[] inputArray) {
		return inputArray == null ? new Object[] {} : inputArray;
	}

	/**
	 * Check, if token is comment.
	 *
	 * @param token the token
	 * @return true, if token is comment
	 */
	public static final boolean isComment(final int token) {
		return token == ITerminalSymbols.TokenNameCOMMENT_BLOCK || token == ITerminalSymbols.TokenNameCOMMENT_JAVADOC
				|| token == ITerminalSymbols.TokenNameCOMMENT_LINE;
	}

	/**
	 * Checks if is single line comment.
	 *
	 * @param token the token
	 * @return true, if is single line comment
	 */
	public static final boolean isSingleLineComment(final int token) {
		return token == ITerminalSymbols.TokenNameCOMMENT_LINE;
	}

	/**
	 * Checks if is multi line comment.
	 *
	 * @param token the token
	 * @return true, if is multi line comment
	 */
	public static final boolean isMultiLineComment(final int token) {
		return token == ITerminalSymbols.TokenNameCOMMENT_BLOCK;
	}

	/**
	 * Check, if token is javadoc comment.
	 *
	 * @param token the token
	 * @return true, if token is javadoc comment
	 */
	public static final boolean isJavadocComment(final int token) {
		return token == ITerminalSymbols.TokenNameCOMMENT_JAVADOC;
	}

	/**
	 * Trim string list by removing empty strings at start and end.
	 *
	 * @param list the list
	 * @return the list
	 */
	public static List<String> trimStringList(final List<String> list) {
		if (list.isEmpty()) {
			return list;
		}

		int startIndex = 0;
		while (startIndex < list.size() && list.get(startIndex).length() == 0) {
			++startIndex;
		}

		int endIndex = list.size();
		while (0 < endIndex && list.get(endIndex - 1).length() == 0) {
			--endIndex;
		}

		if (startIndex > endIndex) {
			startIndex = endIndex;
		}
		return list.subList(startIndex, endIndex);
	}

	/**
	 * Checks if replacement is needed.
	 *
	 * @param member the member
	 * @param replacement the replacement
	 * @param document the document
	 * @param docRange the Javadoc range
	 * @param inherited true, if inherited doc
	 * @return true, if needs replacement
	 */
	public static boolean needsReplacement(final IDocument document, final IMember member, final ISourceRange docRange,
			final String replacement, final boolean inherited) {
		boolean needsReplacement = true;
		try {
			if (inherited) {
				final String text = document.get(member.getSourceRange().getOffset(), member.getSourceRange().getLength()).trim();
				needsReplacement = !startsWithInheritedDoc(text, replacement);
			} else if (docRange.getLength() > 0) {
				final String text = document.get(docRange.getOffset(), docRange.getLength()).trim();
				needsReplacement = !text.startsWith(replacement);
			}
		} catch (final Exception e) {
			//            Activator.getDefault().handleException(e);
		}

		return needsReplacement;
	}

	/**
	 * Checks, if the given text already starts with inherited doc comment.
	 *
	 * @param text the text
	 * @param inheritedDoc the inherited doc
	 * @return true, if starts with inherited doc
	 */
	public static boolean startsWithInheritedDoc(final String text, final String inheritedDoc) {
		final boolean nonJavadoc = inheritedDoc.indexOf(Constants.NON_JAVADOC_TAG) != -1;
		final boolean inheritDoc = !nonJavadoc && inheritedDoc.indexOf(Constants.INHERIT_DOC_TAG) != -1;

		if (nonJavadoc) {
			return text.indexOf(Constants.NON_JAVADOC_TAG) != -1;
		}

		if (inheritDoc) {
			return text.indexOf(Constants.INHERIT_DOC_TAG) != -1;
		}

		// !nonJavadoc && !inheritDoc -> search for equal start sequence. don't
		// use String.startsWith() in order to ignore formatting differences
		String startOfText = null;
		String startOfDoc = null;

		// Pattern to find the first five or more characters,
		// which are different from '/', '*' and space.
		final Pattern pattern = Pattern.compile("[^/*\\s]{5,}");

		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			startOfText = matcher.group();
		}

		matcher = pattern.matcher(inheritedDoc);
		if (matcher.find()) {
			startOfDoc = matcher.group();
		}

		return startOfText != null && startOfDoc != null && startOfText.equals(startOfDoc);
	}

	/**
	 * Checks, if we need to add an empty line in front of the comment.
	 *
	 * @param member the member
	 * @param document the document
	 * @param docRange the Javadoc range
	 * @return true, if needs leading empty line
	 */
	public static boolean needsLeadingEmptyLine(final IDocument document, final IMember member, final ISourceRange docRange) {
		try {
			// other comment in front?
			final int memberOffset = member.getSourceRange().getOffset();
			if (memberOffset < docRange.getOffset()) {
				return false;
			}

			// empty line in front?
			final int lineNumber = document.getLineOfOffset(memberOffset);
			final IRegion region = document.getLineInformation(lineNumber - 1);
			final String text = document.get(region.getOffset(), region.getLength()).trim();
			return text.length() > 0;
		} catch (final Exception e) {
			//            Activator.getDefault().handleException(e);
		}

		return false;
	}

	/**
	 * Returns the document partition in which the position is located.
	 *
	 * @param document the document
	 * @param offset the offset
	 * @return the partition
	 */
	public static String getPartition(final IDocument document, final int offset) {
		String partition = IDocument.DEFAULT_CONTENT_TYPE;
		try {
			final ITypedRegion region = document.getPartition(offset);
			if (region != null) {
				partition = region.getType();
			}
		} catch (final BadLocationException e) {/* ignore */
		}

		return partition;
	}

	/**
	 * Gets the document offset for the give line and column.
	 *
	 * @param document the document
	 * @param line the line
	 * @param column the column
	 * @return the document offset
	 */
	public static int getDocumentOffset(final IDocument document, final int line, final int column) {
		if (line < 0 || column < 0) {
			return -1;
		}

		try {
			return document.getLineOffset(line) + column;
		} catch (final BadLocationException e) {/* ignore */
		}

		return -1;
	}

	/**
	 * Gets the working copy for the given compilation unit.
	 *
	 * @param editorPart the editor part
	 * @param compUnit the compilation unit
	 * @return the working copy
	 * @throws JavaModelException the java model exception
	 */
	public static ICompilationUnit getWorkingCopy(final ICompilationUnit compUnit, final IEditorPart editorPart) throws JavaModelException {
		ICompilationUnit workingCopy = null;

		if (editorPart != null) {
			workingCopy = JavaUI.getWorkingCopyManager().getWorkingCopy(editorPart.getEditorInput());
		}

		if (workingCopy == null) {
			workingCopy = compUnit.isWorkingCopy() ? compUnit : compUnit.getWorkingCopy(null);
		}

		return workingCopy;
	}

	/**
	 * Gets the editor input.
	 *
	 * @param element the element
	 * @return the editor input
	 * @throws JavaModelException the java model exception
	 */
	public static IEditorInput getEditorInput(IJavaElement element) throws JavaModelException {
		while (element != null) {
			if (element instanceof ICompilationUnit) {
				final ICompilationUnit unit = ((ICompilationUnit) element).getPrimary();
				final IResource resource = unit.getResource();
				if (resource instanceof IFile) {
					return new FileEditorInput((IFile) resource);
				}
			}
			element = element.getParent();
		}

		return null;
	}

	/**
	 * Opens the element in editor.
	 *
	 * @param inputElement the input element
	 * @return the editor part
	 * @throws PartInitException the part init exception
	 * @throws JavaModelException the java model exception
	 */
	public static IEditorPart openInEditor(final IJavaElement inputElement) throws JavaModelException, PartInitException {
		final FileEditorInput input = (FileEditorInput) getEditorInput(inputElement);
		if (input == null || input.getFile() == null) {
			return null;
		}

		final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (page != null) {
			final IEditorPart editorPart = IDE.openEditor(page, input.getFile(), true);
			return editorPart;
		}

		return null;
	}

	/**
	 * Finds an editor.
	 *
	 * @param inputElement the input element
	 * @return the editor part
	 * @throws JavaModelException the java model exception
	 */
	public static IEditorPart findEditor(final IJavaElement inputElement) throws JavaModelException {
		final IEditorInput input = getEditorInput(inputElement);
		if (input == null) {
			return null;
		}

		IEditorPart editor = null;
		final IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		for (int i = 0; i < windows.length && editor == null; ++i) {
			final IWorkbenchPage[] pages = windows[i].getPages();
			for (int j = 0; j < pages.length && editor == null; ++j) {
				editor = pages[j].findEditor(input);
			}
		}
		return editor;
	}

	public static <E> Set<E> asSet(final E[] array) {
		final Set<E> set = new HashSet<E>(array.length);
		for (final E element : array) {
			set.add(element);
		}
		return set;
	}

	/**
	 * Close input stream quietly.
	 *
	 * @param is the input stream
	 */
	public static void close(final InputStream is) {
		try {
			if (is != null) {
				is.close();
			}
		} catch (final IOException e) {/* ignore */
		}
	}

	/**
	 * Close ouput stream quietly.
	 *
	 * @param os the input stream
	 */
	public static void close(final OutputStream os) {
		try {
			if (os != null) {
				os.close();
			}
		} catch (final IOException e) {/* ignore */
		}
	}

	/**
	 * Close writer quietly.
	 *
	 * @param writer the writer
	 */
	public static void close(final Writer writer) {
		try {
			if (writer != null) {
				writer.close();
			}
		} catch (final IOException e) {/* ignore */
		}
	}

	/**
	 * Close reader quietly.
	 *
	 * @param reader the reader
	 */
	public static void close(final Reader reader) {
		try {
			if (reader != null) {
				reader.close();
			}
		} catch (final IOException e) {/* ignore */
		}
	}
}
