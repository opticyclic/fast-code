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

package org.fastcode.popup.actions.easyjump;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author Gautam
 *
 */
public abstract class JumpToActionObjectSupport extends JumpToActionSupport implements IObjectActionDelegate {

	//protected StructuredSelection	selection;

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	@Override
	public void setActivePart(final IAction action, final IWorkbenchPart targetPart) {
		this.page = targetPart.getSite().getPage();
	}

	@Override
	public void run(final IAction action) {
		final Object firstElement = ((StructuredSelection) this.selection).getFirstElement();
		if (!(firstElement instanceof ICompilationUnit)) {
			return;
		}
		super.run((ICompilationUnit) firstElement);
	}

	@Override
	protected boolean doesModify() {
		return false;
	}

	@Override
	protected boolean doesRequireMoreInfoFromUser() {
		return false;
	}

	@Override
	protected IJavaElement findSelectedJavaElement(final ICompilationUnit compUnit) throws Exception {
		return null;
	}

	@Override
	protected IType[] getTypesFromUser(final String title, final String description) throws Exception {
		return null;
	}

	@Override
	protected void runAction(final ICompilationUnit compUnit, final IJavaElement javaElement) throws Exception {

	}

	@Override
	protected void runAction(final ICompilationUnit compUnit, final IJavaElement javaElement, final IType[] types) throws Exception {

	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	@Override
	public void selectionChanged(final IAction action, final ISelection selection) {
		if (selection instanceof StructuredSelection) {
			this.selection = selection;
		}
	}

}
