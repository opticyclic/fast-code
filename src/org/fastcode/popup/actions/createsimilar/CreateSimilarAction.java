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

package org.fastcode.popup.actions.createsimilar;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;

public class CreateSimilarAction extends CreateSimilarSupport implements IObjectActionDelegate {

	protected StructuredSelection	selection;
	protected IWorkbenchPage		page;

	/**
	 * Constructor for Action.
	 */
	public CreateSimilarAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	@Override
	public void setActivePart(final IAction action, final IWorkbenchPart targetPart) {
		this.page = targetPart.getSite().getPage();
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	@Override
	public void selectionChanged(final IAction action, final ISelection selection) {
		this.selection = (StructuredSelection) selection;
		if (this.selection.getFirstElement() instanceof ICompilationUnit) {
			this.compUnit = (ICompilationUnit) this.selection.getFirstElement();
		}
	}

	@Override
	protected IMember getSelectedMember() throws Exception {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fastcode.popup.actions.createsimilar.CreateSimilarSupport#showMember(org.eclipse.jdt.core.ICompilationUnit, org.eclipse.jdt.core.IMember)
	 */
	@Override
	protected void showMember(final ICompilationUnit compilationUnit, final IMember member) {
		// Do nothing here.
	}

}
