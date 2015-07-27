/**
 *
 */
package org.fastcode.popup.actions.snippet;

import static org.fastcode.common.FastCodeConstants.CLASS_HEADER_STR;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.common.FastCodeConstants.TEMPLATE_TYPE;
import static org.fastcode.common.FastCodeConstants.XML_EXTENSION;

import static org.fastcode.setting.GlobalSettings.getInstance;
import static org.fastcode.util.StringUtil.evaluateByVelocity;
import static org.fastcode.util.StringUtil.format;
import static org.fastcode.util.StringUtil.formatXml;
import static org.fastcode.util.StringUtil.getGlobalSettings;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.StringUtil.isValidXml;
import static org.fastcode.util.StringUtil.replaceSpecialChars;

import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.ui.IWorkingCopyManager;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.fastcode.setting.GlobalSettings;
import org.fastcode.util.FastCodeFileForCheckin;

/**
 * @author Gautam
 *
 */
public class DefaultSnippetCreator implements SnippetCreator {

	/**
	 * @param editorPart
	 * @param template
	 * @param placeHolders
	 * @param memberSelection
	 * @param spacesBeforeCursor
	 * @throws Exception
	 *
	 */
	@Override
	public Object createSnippet(final IEditorPart editorPart, final String template, final Map<String, Object> placeHolders,
			final Map<String, Object> memberSelection, final String spacesBeforeCursor) throws Exception {

		getGlobalSettings(placeHolders);
		final GlobalSettings globalSettings = getInstance();
		final String classHeader = evaluateByVelocity(globalSettings.getClassHeader(), placeHolders);
		placeHolders.put(CLASS_HEADER_STR, classHeader);

		final String snppt = evaluateByVelocity(template, placeHolders, memberSelection);
		if (isEmpty(snppt)) {
			throw new Exception("Blank snippet, template may be invalid.");

		}

		String snippet = replaceSpecialChars(snppt);

		final ICompilationUnit compUnit = getCompilationUnitFromEditor(editorPart);
		final TemplateTagsProcessor templateTagsProcessor = new TemplateTagsProcessor();
		snippet = templateTagsProcessor.processTemplateTags(compUnit, snippet.trim(), editorPart, false, placeHolders,spacesBeforeCursor);

		if (isEmpty(snippet.trim())) {
			return null;
		}

		/*final FastCodeCheckinCache checkinCache = FastCodeCheckinCache.getInstance();
		checkinCache.getFilesToCheckIn().add(new FastCodeFileForCheckin(INITIATED, fileObj.getAbsolutePath()));*/

		final String fileName = editorPart.getEditorInput().getName();

		if (formatSnippet((String) placeHolders.get(TEMPLATE_TYPE))) {
			if (fileName.endsWith(XML_EXTENSION)) {
				snippet = isValidXml(snippet) ? formatXml(snippet) : format(snippet, spacesBeforeCursor);
			} else {
				snippet = format(snippet, spacesBeforeCursor);
			}
		}
		final ITextSelection selection = (ITextSelection) editorPart.getEditorSite().getSelectionProvider().getSelection();
		final ITextEditor editor = (ITextEditor) editorPart.getAdapter(ITextEditor.class);
		final IDocumentProvider documentProvider = editor.getDocumentProvider();
		final IDocument document = documentProvider.getDocument(editor.getEditorInput());
		final String finalSnippet = snippet.trim();
		document.replace(selection.getOffset(), 0, finalSnippet);

		if (highlightSnippet()) {

			final ITextSelection highlightSelection = new ITextSelection() {

				@Override
				public boolean isEmpty() {
					return false;
				}

				@Override
				public String getText() {
					return null;
				}

				@Override
				public int getStartLine() {
					return selection.getStartLine();
				}

				@Override
				public int getOffset() {
					return selection.getOffset();
				}

				@Override
				public int getLength() {
					return finalSnippet.length();
				}

				@Override
				public int getEndLine() {
					return selection.getEndLine() + finalSnippet.split(NEWLINE).length;
				}
			};
			editorPart.getEditorSite().getSelectionProvider().setSelection(highlightSelection);
		}

		return snippet.trim();
	}

	/**
	 *
	 * @return
	 */
	private boolean highlightSnippet() {
		final GlobalSettings globalSettings = GlobalSettings.getInstance();
		return globalSettings.getPropertyValue("TEMPLATE_HIGHLIGHT_SNIPPET", "true").equalsIgnoreCase("true");
	}

	/**
	 *
	 * @param templateType
	 * @param templatePrefix
	 * @return
	 */
	private boolean formatSnippet(final String templateType) {
		final GlobalSettings globalSettings = GlobalSettings.getInstance();
		return globalSettings.getPropertyValue(templateType + "_FORMAT_SNIPPET", "true").equalsIgnoreCase("true");
	}

	/**
	 *
	 * @param editorPart
	 * @return
	 */
	private ICompilationUnit getCompilationUnitFromEditor(final IEditorPart editorPart) {
		final IWorkingCopyManager manager = JavaUI.getWorkingCopyManager();
		final ICompilationUnit compilationUnit = manager.getWorkingCopy(editorPart.getEditorInput());
		return compilationUnit != null && compilationUnit.exists() ? compilationUnit : null;
	}
}
