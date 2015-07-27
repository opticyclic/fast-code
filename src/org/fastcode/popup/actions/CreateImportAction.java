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

package org.fastcode.popup.actions;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;

public class CreateImportAction extends CreateImportActionSupport implements IObjectActionDelegate {

	/**
	 * Constructor for Action1.
	 */
	public CreateImportAction() {
		super();
	}

	/**
	 * @param compilationUnit
	 * @param javaElement
	 *
	 * @author Gautam
	 * @throws Exception
	 */
	@Override
	protected void runAction(final ICompilationUnit compilationUnit, final IJavaElement javaElement) throws Exception {
		if (this.selection instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection) this.selection;
			if (selection.isEmpty()) {
				return;
			}
			for (Object cmpUnit : selection.toArray()) {
				super.createImport(compilationUnit, ((ICompilationUnit) cmpUnit).findPrimaryType());
			}
		}
	}

	@Override
	protected boolean doesRequireMoreInfoFromUser() {
		return false;
	}

}
