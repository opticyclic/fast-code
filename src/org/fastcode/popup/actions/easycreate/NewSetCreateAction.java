/**
 * @author : Gautam

 * Created : 10/04/2010

 */

package org.fastcode.popup.actions.easycreate;

import static org.fastcode.common.FastCodeConstants.SELECT_PARAMETER_TITLE;
import static org.fastcode.common.FastCodeConstants.SET;
import static org.fastcode.util.StringUtil.createDefaultInstance;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.fastcode.common.CreateVariableData;

public class NewSetCreateAction extends NewMemberCreateActionSupport implements IEditorActionDelegate, IActionDelegate,
		IWorkbenchWindowActionDelegate {

	/* (non-Javadoc)
	 * @see org.fastcode.popup.actions.easycreate.NewMemberCreateActionSupport#getCreateVariableData(org.eclipse.jdt.core.ICompilationUnit)
	 */
	@Override
	protected CreateVariableData getCreateVariableData(final ICompilationUnit compUnit) throws Exception {
		this.createVariableData = new CreateVariableData();
		super.getCreateVariableData(compUnit);
		final IType[] types = super.getTypesFromUser(SELECT_PARAMETER_TITLE, SET);
		if (types == null || types.length == 0) {
			return null;
		}

		this.createVariableData.setFieldParams(new String[] { types[0].getElementName() });
		this.createVariableData.setFieldType("java.util.Set");

		final String defaultFieldName = createDefaultInstance(types[0].getElementName());
		super.openInputDialog(defaultFieldName);
		this.createVariableData.addImportTypes(types[0].getFullyQualifiedName());
		this.createVariableData.addImportTypes(this.createVariableData.classFQNameMap.get(SET));
		this.createVariableData.addImportTypes(this.createVariableData.classFQNameMap.get("HashSet"));

		this.createVariableData.setSet(true);
		this.createVariableData.setSetType("HashSet");
		this.createVariableData.setInitialized(true);
		return this.createVariableData;
	}
}