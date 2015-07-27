/**
 *
 */
package org.fastcode.popup.actions.easycreate;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.fastcode.common.CreateVariableData;
import org.fastcode.common.FastCodeConstants.GETTER_SETTER;

/**
 * @author Gautam
 *
 */
public class SimpleNewStringMemberCreateAction extends DetailedNewMemberCreateAction implements IActionDelegate,
		IWorkbenchWindowActionDelegate {

	@Override
	protected boolean isSimpleType() {
		return true;
	}

	/**
	 * @param compUnit
	 *
	 */
	/* (non-Javadoc)
	 * @see org.fastcode.popup.actions.easycreate.NewMemberCreateActionSupport#getCreateVariableData(org.eclipse.jdt.core.ICompilationUnit)
	 */
	@Override
	protected CreateVariableData getCreateVariableData(final ICompilationUnit compUnit) throws Exception {
		this.createVariableData = new CreateVariableData();
		this.createVariableData.setFieldType("String");
		this.createVariableData.setGetterSetter(GETTER_SETTER.GETTER_SETTER_EXIST);
		return super.getCreateVariableData(compUnit);

	}

}
