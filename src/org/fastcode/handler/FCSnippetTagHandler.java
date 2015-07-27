/**
 * This class has been generated by Fast Code Eclipse Plugin
 * For more information please go to http://fast-code.sourceforge.net/
 * @author : Biswarup
 * Created : 09/08/2014 04:13:34
 */
package org.fastcode.handler;

import static org.eclipse.jdt.ui.JavaUI.openInEditor;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.ENCLOSING_CLASS_STR;
import static org.fastcode.common.FastCodeConstants.ENCLOSING_FILE_STR;
import static org.fastcode.common.FastCodeConstants.FORWARD_SLASH;
import static org.fastcode.common.FastCodeConstants.HASH;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_PROJECT;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_TARGET;
import static org.fastcode.common.FastCodeConstants.RIGHT_CURL;
import static org.fastcode.common.FastCodeConstants.XML_END;
import static org.fastcode.common.FastCodeConstants.XML_EXTENSION;
import static org.fastcode.common.FastCodeConstants.XML_START;
import static org.fastcode.util.FastCodeUtil.findEditor;
import static org.fastcode.util.SourceUtil.findFileFromPath;
import static org.fastcode.util.SourceUtil.getEditorPartFromFile;
import static org.fastcode.util.SourceUtil.getFileContents;
import static org.fastcode.util.StringUtil.format;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.StringUtil.replaceSpecialChars;

import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.fastcode.common.Action;
import org.fastcode.common.FastCodeConstants.ACTION_ENTITY;
import org.fastcode.common.FastCodeConstants.ACTION_TYPE;
import org.fastcode.common.FastCodeConstants.TARGET;
import org.fastcode.common.FastCodeConstants.TemplateTag;
import org.fastcode.common.FastCodeFile;
import org.fastcode.common.FastCodeProject;
import org.fastcode.common.FastCodeType;
import org.fastcode.popup.actions.snippet.TemplateTagsProcessor;
import org.w3c.dom.Document;

public class FCSnippetTagHandler implements FCTagHandler {

	@Override
	public Action populateTagAction(final TemplateTag tagFound, final String tagBody, final String insideTagBody,
			final ICompilationUnit compUnit, final boolean hasSubAction1, final Map<String, Object> placeHolders,
			final Map<String, Object> contextMap, final String spacesBeforeCursor, final Map<String, String> attributes,
			final StringBuilder existingMembersBuilder, final List<Action> actionList) throws Exception {
		final ACTION_TYPE actionType = ACTION_TYPE.Create;
		final String source = replaceSpecialChars(insideTagBody);
		final String targtClass = attributes.containsKey(TARGET.clas.getValue()) ? attributes.get(TARGET.clas.getValue()) : null;
		final String targtFile = attributes.containsKey(TARGET.file.getValue()) ? attributes.get(TARGET.file.getValue()) : null;
		String target = null;

		if (isEmpty(targtClass) && isEmpty(targtFile)) {
			target = attributes.containsKey(PLACEHOLDER_TARGET) ? attributes.get(PLACEHOLDER_TARGET) : null;
		}
		/*final Object openFile = placeHolders.containsKey(ENCLOSING_CLASS_STR) ? ((FastCodeType) placeHolders.get(ENCLOSING_CLASS_STR))
				.getFullyQualifiedName() : ((FastCodeFile) placeHolders.get(ENCLOSING_FILE_STR)).getFullPath();*/
		if (!isEmpty(targtClass) && !isEmpty(targtFile)) {
			throw new Exception(
					"Please provide either attribute \"class\" or attribute \"file\" in <fc:snippet> tag in the xml and try again.");
		}
		Object targetObj = null;
		String lblmsg = null;
		FastCodeType fCodeType = null;
		IJavaProject javaPrj = null;

		if (!isEmpty(targtClass)) {
			if (compUnit == null) {
				final FastCodeProject codeProject = (FastCodeProject) placeHolders.get(PLACEHOLDER_PROJECT);
				if (codeProject.getJavaProject() != null) {
					javaPrj = codeProject.getJavaProject();
				}
			} else {
				javaPrj = compUnit.getJavaProject();
			}
			if (javaPrj == null) {
				throw new Exception("As <fc:snippet> tag target is a class ,it will works on Java classes.");
			}
			//have to check if class is there in placeholder
			fCodeType = targtClass.startsWith(HASH) ? (FastCodeType) placeHolders.get(targtClass.replace(HASH, EMPTY_STR).trim())
					: new FastCodeType(javaPrj.findType(targtClass.trim()));
			if (fCodeType.getiType() == null) {
				throw new Exception("Class/Target " + targtClass
						+ " does not exist,Please try again with an existing Class/Target as an attribute in <fc:snippet> tag.");
				/*	//have to put code for creating the class

					createTargetClas = true;
					fCodeType = new FastCodeType(targtClass);*/

			}
		}/* else {
			fCodeType = (FastCodeType) placeHolders.get(ENCLOSING_CLASS_STR);
			}*/
		if (fCodeType != null) {
			targetObj = fCodeType;
		}
		final TemplateTagsProcessor templateTagsProcessor = new TemplateTagsProcessor();
		if (!isEmpty(targtClass)) {
			templateTagsProcessor.validateTargetClassType(targtClass, fCodeType.getiType());
		}
		FastCodeFile fastCodeFile = null;
		if (!isEmpty(targtFile)) {
			fastCodeFile = targtFile.startsWith(HASH) ? (FastCodeFile) placeHolders.get(targtFile.replace(HASH, EMPTY_STR).trim())
					: new FastCodeFile(findFileFromPath(targtFile));

			if (fastCodeFile != null && fastCodeFile.getFile() == null) {
				throw new Exception("File/Target " + targtFile
						+ " does not exist,Please try again with an existing File/Target as an attribute in <fc:snippet> tag.");
			}

		} /*else {
			fastCodeFile = (FastCodeFile) placeHolders.get(ENCLOSING_FILE_STR);
			}*/
		if (fastCodeFile != null && fastCodeFile.getFile() != null) {
			targetObj = fastCodeFile;
		}

		if (targetObj == null && isEmpty(targtClass) && isEmpty(targtFile)) {
			if (!isEmpty(target) && target.startsWith(HASH)) {
				targetObj = placeHolders.get(target.replace(HASH, EMPTY_STR).trim());
				if (targetObj != null && targetObj instanceof FastCodeType) {
					targetObj = ((FastCodeType) targetObj).getiType();
					lblmsg = ((IType) targetObj).getFullyQualifiedName();
				} else if (targetObj != null && targetObj instanceof FastCodeFile) {
					targetObj = ((FastCodeFile) targetObj).getFile();
					lblmsg = ((IFile) targetObj).getFullPath().toString();
				}
			} else {
				targetObj = placeHolders.get(ENCLOSING_CLASS_STR);
				if (targetObj == null) {
					targetObj = placeHolders.get(ENCLOSING_FILE_STR);
				}
			}
		}
		if (targetObj instanceof FastCodeType) {
			lblmsg = ((FastCodeType) targetObj).getFullyQualifiedName();
		} else if (targetObj instanceof FastCodeFile) {
			lblmsg = ((FastCodeFile) targetObj).getFullPath();
		}
		//if target does not contain # ,it can be file/class.How to check that?

		/*final Object trgtObj = !isEmpty(targetFile) && targetFile.startsWith(HASH) ? ((FastCodeFile) placeHolders.get(targetFile.replace(HASH, EMPTY_STR).trim()))
				.getFile(): ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(targetFile).makeAbsolute());
		final String fileFullPath = !isEmpty(targetFile) && targetFile.startsWith(HASH) ? file.getFullPath().toString() : targetFile;*/

		final Action actionSnippet = new Action.Builder().withEntity(ACTION_ENTITY.Snippet).withType(actionType).withSource(source.trim())
				.withLabelMsg("Create Snippet in " + lblmsg).withTarget(targetObj).build();
		return actionSnippet;
	}

	/**
	 * @param snippet
	 * @param target
	 * @param editorPart
	 * @param spacesBeforeCursor
	 * @param placeHolders
	 * @param contextMap
	 * @param compUnit
	 * @throws Exception
	 */
	public void createSnippetFromTag(String snippet, final Object target, IEditorPart editorPart, final String spacesBeforeCursor,
			final Map<String, Object> contextMap, final Map<String, Object> placeHolders, final ICompilationUnit compUnit) throws Exception {
		snippet = format(snippet, spacesBeforeCursor);
		int positionToInsertSnippet = -1;
		boolean addNewLine = false;
		if (target != null) {
			IEditorPart edPart = null;
			if (target instanceof IFile) {
				edPart = getEditorPartFromFile((IFile) target);
				positionToInsertSnippet = getPositionInFile((IFile) target, positionToInsertSnippet);
				addNewLine = true;
			} else if (target instanceof FastCodeFile) {
				edPart = getEditorPartFromFile(((FastCodeFile) target).getFile());
				if ((FastCodeFile) placeHolders.get(ENCLOSING_FILE_STR) != (FastCodeFile) target) {
					positionToInsertSnippet = getPositionInFile(((FastCodeFile) target).getFile(), positionToInsertSnippet);
				}
				addNewLine = true;
			} else if (target instanceof IType) {
				edPart = openInEditor((IType) target);//findEditor((IType) target);
				positionToInsertSnippet = ((IType) target).getCompilationUnit().getSource().lastIndexOf(RIGHT_CURL) - 1;
			} else if (target instanceof FastCodeType) {
				if ((FastCodeType) placeHolders.get(ENCLOSING_CLASS_STR) != (FastCodeType) target) {
					positionToInsertSnippet = ((FastCodeType) target).getiType().getCompilationUnit().getSource().lastIndexOf(RIGHT_CURL) - 1;
					edPart = openInEditor(((FastCodeType) target).getiType());
				} else {
					edPart = findEditor(((FastCodeType) target).getiType());
				}
			}
			if (edPart != null) {
				editorPart = edPart;
			}
		}

		final ITextSelection selection = (ITextSelection) editorPart.getEditorSite().getSelectionProvider().getSelection();
		final ITextEditor editor = (ITextEditor) editorPart.getAdapter(ITextEditor.class);
		final IDocumentProvider documentProvider = editor.getDocumentProvider();
		final IDocument document = documentProvider.getDocument(editor.getEditorInput());
		final String finalSnippet = addNewLine ? NEWLINE + snippet.trim() : snippet.trim();
		//document.replace(selection.getOffset(), 0, finalSnippet);
		positionToInsertSnippet = positionToInsertSnippet == -1 ? selection.getOffset() : positionToInsertSnippet;
		document.replace(positionToInsertSnippet, 0, finalSnippet);
		editorPart.doSave(new NullProgressMonitor());
	}

	/**
	 * @param target
	 * @param positionToInsertSnippet
	 * @return
	 * @throws Exception
	 */
	private int getPositionInFile(final IFile target, int positionToInsertSnippet) throws Exception {
		if (target.getFileExtension().equals(XML_EXTENSION)) {
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder docBuilder = factory.newDocumentBuilder();
			final Document document = docBuilder.parse(target.getContents());
			final String rootNodeName = document.getDocumentElement().getNodeName();
			positionToInsertSnippet = getFileContents(target).indexOf(XML_START + FORWARD_SLASH + rootNodeName + XML_END) - 1;
		} else {
			positionToInsertSnippet = getFileContents(target).length();
		}
		return positionToInsertSnippet;
	}
}
