/**
 * @author : Gautam

 * Created : 05/03/2010

 */

package org.fastcode.popup.actions.snippet;

import static org.fastcode.preferences.PreferenceConstants.TEMPLATE;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_TYPE_COPY_CLASS;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class CreateNewCopyBeanViewAction extends AbstractCreateNewSnippetAction implements IEditorActionDelegate, IActionDelegate,
		IWorkbenchWindowActionDelegate {

	/**
	 *
	 */
	public CreateNewCopyBeanViewAction() {
		this.templateType = TEMPLATE_TYPE_COPY_CLASS;
		this.templatePrefix = TEMPLATE;
	}

	/**
	 *
	 * @param snippet
	 * @throws Exception
	 */
	protected void createCopyMethod(final String snippet) throws Exception {
		final Pattern pattern = Pattern.compile(
				".*(public|protected|private)\\s+.* \\s*([a-z0-1_]*)\\s*\\([a-z0-1_, ]*\\)\\s*\\{.*\\}\\s*", Pattern.CASE_INSENSITIVE
						| Pattern.DOTALL | Pattern.MULTILINE);
		final Matcher matcher = pattern.matcher(snippet);
		if (!matcher.matches()) {
			MessageDialog.openWarning(new Shell(), "Warning", "There may be some problem with the template.");
			//			return;
		}
		final ICompilationUnit compilationUnit = super.getCompilationUnitFromEditor();
		final IType type = compilationUnit.findPrimaryType();
		try {
			final String methodName = matcher.group(1);
			for (final IMethod method : type.getMethods()) {
				if (method.getElementName().equals(methodName)) {
					if (!MessageDialog.openConfirm(new Shell(), "Warning", "Method with name " + methodName
							+ " already exists. Would you like to proceed?")) {
						return;
					}
					break;
				}
			}
		} catch (final Exception exception) {
			exception.printStackTrace();
		}
		final IMethod method = type.createMethod(snippet, null, false, null);
		JavaUI.revealInEditor(this.editorPart, (IJavaElement) method);
	}
}