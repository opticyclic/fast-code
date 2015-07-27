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
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.IWorkingCopyManager;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class CreateSimilarWithDifferentNameView extends CreateSimilarSupport implements IEditorActionDelegate, IActionDelegate,
		IWorkbenchWindowActionDelegate {

	protected ITextSelection	selection;

	/**
	 * Constructor for Action.
	 */
	public CreateSimilarWithDifferentNameView() {
		super();
		this.differentName = true;
	}

	/**
	 * @param type
	 * @param monitor
	protected ICompilationUnit createSimilarClass(IType type, IMember member, IProgressMonitor monitor) throws Exception {
		return createSimilarClassWithDiffName(type, member, monitor);
	}
	*/

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorActionDelegate#setActiveEditor(org.eclipse.jface.action.IAction, org.eclipse.ui.IEditorPart)
	 */
	@Override
	public void setActiveEditor(final IAction action, final IEditorPart targetEditor) {
		this.editorPart = targetEditor;
		if (this.editorPart == null) {
			return;
		}
		final IWorkingCopyManager manager = JavaUI.getWorkingCopyManager();
		if (manager == null) {
			return;
		}
		this.compUnit = manager.getWorkingCopy(this.editorPart.getEditorInput());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(final IAction action, final ISelection selection) {
		if (selection instanceof ITextSelection) {
			this.selection = (ITextSelection) selection;
		}
	}

	@Override
	protected IMember getSelectedMember() {
		try {
			if (this.selection != null) {
				return (IMember) this.compUnit.getElementAt(this.selection.getOffset());
			}
		} catch (final JavaModelException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fastcode.popup.actions.createsimilar.CreateSimilarSupport#showMember(org.eclipse.jdt.core.ICompilationUnit, org.eclipse.jdt.core.IMember)
	 */
	@Override
	protected void showMember(final ICompilationUnit compilationUnit, final IMember member) {
		// Do nothing
	}

	/**
	 *
	 */
	@Override
	public void dispose() {

	}

	/**
	 * @param window
	 */
	@Override
	public void init(final IWorkbenchWindow window) {
		this.window = window;
	}

}
