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

import static org.fastcode.common.FastCodeConstants.DOT_CHAR;
import static org.fastcode.preferences.PreferenceConstants.P_GETTER_SETTER_FORMAT;
import static org.fastcode.preferences.PreferenceConstants.P_SETTER_VAR_PREFIX;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.fastcode.common.CreateVariableData;
import org.fastcode.common.FastCodeConstants.GETTER_SETTER_FORMAT;

/**
 * @author Gautam
 *
 */
public class NewTreeMapCreateAction extends NewHashMapCreateAction implements IEditorActionDelegate, IActionDelegate,
		IWorkbenchWindowActionDelegate {

	/* (non-Javadoc)
	 * @see org.fastcode.popup.actions.easycreate.NewHashMapCreateAction#getCreateVariableData(org.eclipse.jdt.core.ICompilationUnit)
	 */
	@Override
	protected CreateVariableData getCreateVariableData(final ICompilationUnit compUnit) throws Exception {
		super.getCreateVariableData(compUnit);
		this.createVariableData.addImportTypes(this.createVariableData.classFQNameMap.get("TreeMap"));
		this.createVariableData.setMapType("TreeMap");
		final String fileName = this.editorPart.getEditorInput().getName();
		this.createVariableData.setCompUnitType(fileName.substring(fileName.lastIndexOf(DOT_CHAR) + 1, fileName.length()));
		if (this.preferenceStore.contains(P_GETTER_SETTER_FORMAT)) {
			this.createVariableData.setGetterSetterFormat(GETTER_SETTER_FORMAT.getGetterSetterFormat(this.preferenceStore
					.getString(P_GETTER_SETTER_FORMAT)));
		}

		if (this.preferenceStore.contains(P_SETTER_VAR_PREFIX)) {
			this.createVariableData.setSetterVerPrefix(this.preferenceStore.getString(P_SETTER_VAR_PREFIX));
		}

		return this.createVariableData;
	}

}
