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

import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.MAP;
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
public class NewHashMapCreateAction extends NewMemberCreateActionSupport implements IEditorActionDelegate, IActionDelegate,
		IWorkbenchWindowActionDelegate {

	//CreateVariableData createVariableData = null;//new CreateVariableData();

	/* (non-Javadoc)
	 * @see org.fastcode.popup.actions.easycreate.NewMemberCreateActionSupport#getTypesFromUser(java.lang.String, java.lang.String)
	 */
	@Override
	protected IType[] getTypesFromUser(final String title, final String description) throws Exception {
		final IType[] retTypes = new IType[2];

		final IType[] tmpTypes = super.getTypesFromUser(SELECT_PARAMETER_TITLE, "Key");

		if (tmpTypes == null) {
			return null;
		}
		retTypes[0] = tmpTypes[0];
		final IType tmpType = super.openTypeDialog(SELECT_PARAMETER_TITLE, "value");

		if (tmpType == null) {
			return null;
		}
		retTypes[1] = tmpType;
		return retTypes;
	}

	/* (non-Javadoc)
	 * @see org.fastcode.popup.actions.easycreate.NewMemberCreateActionSupport#getCreateVariableData(org.eclipse.jdt.core.ICompilationUnit)
	 */
	@Override
	protected CreateVariableData getCreateVariableData(final ICompilationUnit compUnit) throws Exception {
		this.createVariableData = new CreateVariableData();
		super.getCreateVariableData(compUnit);
		/*final String fileName = this.editorPart.getEditorInput().getName();
		this.createVariableData.setCompUnitType(fileName.substring(fileName.lastIndexOf(DOT) + 1,fileName.length()));
		if (this.preferenceStore.contains(P_GETTER_SETTER_FORMAT)) {
			this.createVariableData.setGetterSetterFormat(GETTER_SETTER_FORMAT.getGetterSetterFormat(this.preferenceStore.getString(P_GETTER_SETTER_FORMAT)));
		}

		if (this.preferenceStore.contains(P_SETTER_VAR_PREFIX)) {
			this.createVariableData.setSetterVerPrefix(this.preferenceStore.getString(P_SETTER_VAR_PREFIX));
		}
		*/
		final IType[] types = getTypesFromUser(EMPTY_STR, EMPTY_STR);
		if (types == null || types.length == 0) {
			return null;
		}

		final String[] fieldParams = { types[0].getElementName(), types[1].getElementName() };

		this.createVariableData.setFieldParams(fieldParams);

		final String defaultFieldName = createDefaultInstance(types[0].getElementName());
		super.openInputDialog(defaultFieldName);
		/*		final InputDialog inputDialog = new InputDialog(new Shell(), "Create Field", "Enter a name or names (space separated) for the field", defaultFieldName ,
						new IInputValidator() {

					public String isValid(final String newText) {
						if (isEmpty(newText)) {
							return "Input cannot be blank";
						}
						return null;
					}
				});

				final int retCode = inputDialog.open();
				final String[] fieldNames = inputDialog.getValue().split("\\s+");
				if (retCode == Window.CANCEL || fieldNames == null || fieldNames.length == 0) {
					return null;
				}

		*///this.createVariableData.setGetterSetter(super.getGetterSetterChoice(this.isSimpleType()));

		//		this.createVariableData.setFieldNames(fieldNames);
		this.createVariableData.addImportTypes(types[0].getFullyQualifiedName());
		this.createVariableData.addImportTypes(types[1].getFullyQualifiedName());
		this.createVariableData.addImportTypes(this.createVariableData.classFQNameMap.get(MAP));
		this.createVariableData.addImportTypes(this.createVariableData.classFQNameMap.get("HashMap"));

		this.createVariableData.setMap(true);
		this.createVariableData.setMapType("HashMap");

		return this.createVariableData;
	}

	/*@Override
	protected CreateVariableData getCreateVariableData(final ICompilationUnit compUnit) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}*/
}
