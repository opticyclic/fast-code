/**
 *
 */
package org.fastcode.popup.actions.snippet;

import static org.fastcode.util.StringUtil.evaluateByVelocity;
import static org.fastcode.util.StringUtil.getGlobalSettings;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.StringUtil.replaceSpecialChars;

import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.IWorkingCopyManager;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.fastcode.util.SourceUtil;
import org.fastcode.util.StringUtil;

/**
 * @author Gautam
 *
 */
public class MethodSnippetCreator implements SnippetCreator {

	/**
	 * @param editorPart
	 * @param placeHolders
	 * @param memberSelection
	 * @param spacesBeforeCursor
	 */
	@Override
	public Object createSnippet(final IEditorPart editorPart, final String template, final Map<String, Object> placeHolders,
			final Map<String, Object> memberSelection, final String spacesBeforeCursor) throws Exception {
		getGlobalSettings(placeHolders);
		final String snppt = evaluateByVelocity(template, placeHolders, memberSelection);
		if (isEmpty(snppt)) {
			throw new Exception("Blank snippet, template may be invalid.");
		}

		final String snippet = replaceSpecialChars(snppt);

		final ICompilationUnit compilationUnit = getCompilationUnitFromEditor(editorPart);
		if (!checkForMethod(snippet, compilationUnit)) {
			return null;
		}

		final IType type = compilationUnit.findPrimaryType();
		IMethod method;
		try {
			method = type.createMethod(snippet, null, false, null);
			if (method == null || !method.exists()) {
				throw new Exception("Unable to create method.");
			}
		} catch (final Exception ex) {
			ex.printStackTrace();
			throw new Exception("Unable to create mehtod, template may be wrong. " + ex.getMessage() + snippet, ex);
		}
		JavaUI.revealInEditor(editorPart, (IJavaElement) method);
		return method;
	}

	/**
	 *
	 * @param snippet
	 * @param compilationUnit
	 * @return
	 * @throws Exception
	 */
	private boolean checkForMethod(final String snippet, final ICompilationUnit compilationUnit) throws Exception {
		final String methodName = StringUtil.parseMethodName(snippet);
		if (methodName == null) {
			if (!MessageDialog.openConfirm(new Shell(), "Warning", "Template may be incorrect. Would you like to proceed?")) {
				return false;
			}
		}
		final IType type = compilationUnit.findPrimaryType();
		if (SourceUtil.doesMethodExistsInType(type, methodName)) {
			if (!MessageDialog.openConfirm(new Shell(), "Warning", "Method with name " + methodName
					+ " already exists. Would you like to proceed?")) {
				return false;
			}
		}
		return true;
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
