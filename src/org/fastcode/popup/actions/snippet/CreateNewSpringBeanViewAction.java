/**
 * @author : Gautam

 * Created : 05/12/2010

 */

package org.fastcode.popup.actions.snippet;

import static org.eclipse.jface.window.Window.CANCEL;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_TYPE_CREATE_NEW_SPRING_BEAN;
import static org.fastcode.util.SourceUtil.findSuperInterfaceType;
import static org.fastcode.util.StringUtil.createDefaultInstance;

import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class CreateNewSpringBeanViewAction extends AbstractCreateNewSnippetAction implements IEditorActionDelegate, IActionDelegate,
		IWorkbenchWindowActionDelegate {

	/**
	 *
	 */
	public CreateNewSpringBeanViewAction() {
		this.templateType = TEMPLATE_TYPE_CREATE_NEW_SPRING_BEAN;
		this.templatePrefix = TEMPLATE;
	}

	/**
	 * @param type
	 * @throws Exception
	 */
	@Override
	protected String getInstanceName(final IType type) throws Exception {
		final IType superInterfaceType = findSuperInterfaceType(type);
		String instanceName;
		if (superInterfaceType == null) {
			instanceName = createDefaultInstance(type.getElementName());
		} else {
			instanceName = createDefaultInstance(superInterfaceType.getElementName());
		}
		final InputDialog typeInputDialog = new InputDialog(this.editorPart.getSite().getShell(), "Spring Bean Name",
				"Enter a name of spring bean" + type.getElementName(), instanceName, null);
		return typeInputDialog.open() == CANCEL ? null : typeInputDialog.getValue();
	}
}