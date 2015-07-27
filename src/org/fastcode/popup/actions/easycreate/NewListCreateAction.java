/*
 * Fast Code Plugin for Eclipse
 *
 * Copyright (C) 2008  Gautam Dev
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA.
 *
 * Plugin Home Page: http://fast-code.sourceforge.net/
 */

package org.fastcode.popup.actions.easycreate;

import static org.fastcode.common.FastCodeConstants.LIST;
import static org.fastcode.common.FastCodeConstants.SELECT_PARAMETER_TITLE;
import static org.fastcode.util.StringUtil.createDefaultInstance;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.fastcode.common.CreateVariableData;

/**
 * @author Gautam
 *
 */
public class NewListCreateAction extends NewMemberCreateActionSupport implements IEditorActionDelegate, IActionDelegate,
		IWorkbenchWindowActionDelegate {

	/* (non-Javadoc)
	 * @see org.fastcode.popup.actions.easycreate.NewMemberCreateActionSupport#getCreateVariableData(org.eclipse.jdt.core.ICompilationUnit)
	 */
	@Override
	protected CreateVariableData getCreateVariableData(final ICompilationUnit compUnit) throws Exception {
		this.createVariableData = new CreateVariableData();
		super.getCreateVariableData(compUnit);
		final IType[] types = super.getTypesFromUser(SELECT_PARAMETER_TITLE, LIST);
		if (types == null || types.length == 0) {
			return null;
		}

		this.createVariableData.setFieldParams(new String[] { types[0].getElementName() }); //getFullyQualifiedName()});
		this.createVariableData.setFieldType("java.util.List");

		final String defaultFieldName = createDefaultInstance(types[0].getElementName());
		super.openInputDialog(defaultFieldName);
		this.createVariableData.addImportTypes(types[0].getFullyQualifiedName());
		this.createVariableData.addImportTypes(this.createVariableData.classFQNameMap.get(LIST));
		this.createVariableData.addImportTypes(this.createVariableData.classFQNameMap.get("ArrayList"));

		this.createVariableData.setList(true);
		this.createVariableData.setListType("ArrayList");
		this.createVariableData.setInitialized(true);
		return this.createVariableData;
	}
}
