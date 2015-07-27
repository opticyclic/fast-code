/**
 *
 */
package org.fastcode.popup.actions.easycreate;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.fastcode.common.CreateVariableData;

/**
 * @author Gautam
 *
 */
public class SimpleNewFieldCreateAction extends DetailedNewMemberCreateAction implements IActionDelegate, IWorkbenchWindowActionDelegate {


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
		this.createVariableData.setCreateFieldSimple(true);
		return super.getCreateVariableData(compUnit);

	}

}
