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

import static org.fastcode.util.EditorUtil.findMember;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.ui.IWorkingCopyManager;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class CreateSimilarViewAction extends CreateSimilarSupport implements IEditorActionDelegate, IActionDelegate,
		IWorkbenchWindowActionDelegate {

	protected ITextSelection	selection;

	/**
	 * Constructor for Action.
	 */
	public CreateSimilarViewAction() {
		super();
	}

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
	protected IMember getSelectedMember() throws Exception {
		if (this.selection == null || this.selection.isEmpty()) {
			return null;
		}
		final IJavaElement element = this.compUnit.getElementAt(this.selection.getOffset());
		if (element instanceof IMethod || element instanceof IField) {
			return (IMember) element;
		}
		return null;
	}

	/**
	 * @param compilationUnit
	 * @param member
	 * @throws Exception
	 *
	 */
	@Override
	protected void showMember(final ICompilationUnit compilationUnit, final IMember member) throws Exception {
		final IMember newMember = findMember(compilationUnit, member);
		if (newMember == null) {
			return;
		}
		final IWorkbenchPage page = this.editorPart.getSite().getPage();
		final ITextSelection sel = new TextSelection(newMember.getNameRange().getOffset(), newMember.getNameRange().getLength());

		page.getActiveEditor().getEditorSite().getSelectionProvider().setSelection(sel);
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
