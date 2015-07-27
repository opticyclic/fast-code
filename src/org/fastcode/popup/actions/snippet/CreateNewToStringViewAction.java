/**
 * @author : Gautam

 * Created : 05/13/2010

 */

package org.fastcode.popup.actions.snippet;

import static org.fastcode.preferences.PreferenceConstants.TEMPLATE;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_TYPE_CREATE_NEW_TOSTRING;

import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class CreateNewToStringViewAction extends AbstractCreateNewSnippetAction implements IEditorActionDelegate, IActionDelegate,
		IWorkbenchWindowActionDelegate {

	/**
	 *
	 */
	public CreateNewToStringViewAction() {
		this.templateType = TEMPLATE_TYPE_CREATE_NEW_TOSTRING;
		this.templatePrefix = TEMPLATE;
	}

	/**
	 * @param type
	 * @param toType
	 * @param fields
	 * @param fieldType
	 *
	 */
	//	@Override
	//	protected Map<String, List<FastCodeField>> getFieldSelection(final IType type, final IType toType, final IField[] fields,
	//			final Map<String, Object> placeHolders, final String fieldType) throws Exception {
	//		final IMethod method = type.getMethod("toString", null);
	//		if (method != null && method.exists()) {
	//			throw new Exception("Method toString already exists");
	//		}
	//		return super.getFieldSelection(type, toType, fields, placeHolders, fieldType);
	//	}

	/**
	 * @param templateType
	 * @param placeHolders
	 * @param fieldSelection
	 * @param spacesBeforeCursor
	 *
	 */
	//	@Override
	//	protected void createSnippet(final Map<String, Object> placeHolders, final Map<String, Object> fieldSelection,
	//			final String spacesBeforeCursor) throws Exception {
	//		final String snppt = evaluateByVelocity(this.templateSettings.getTemplateBody(), placeHolders, fieldSelection);
	//		final String snippet = format(snppt, spacesBeforeCursor);
	//		createToString(replaceSpecialChars(snippet));
	//	}

	/**
	 * @param snippet
	 * @throws Exception
	 */
	//	protected void createToString(final String snippet) throws Exception {
	//		//		System.out.println("snippet " + snippet);
	//		final ICompilationUnit compilationUnit = super.getCompilationUnitFromEditor();
	//		final IType type = compilationUnit.findPrimaryType();
	//		IMethod method;
	//		try {
	//			method = type.createMethod(snippet, null, false, null);
	//			if (method == null || !method.exists()) {
	//				throw new Exception("Unable to create method toString.");
	//			}
	//		} catch (final Exception ex) {
	//			ex.printStackTrace();
	//			throw new Exception("Unable to create mehtod, template may be wrong. " + ex.getMessage() + snippet, ex);
	//		}
	//		JavaUI.revealInEditor(this.editorPart, (IJavaElement)method);
	//	}


}