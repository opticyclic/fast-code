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

import static org.eclipse.jface.dialogs.MessageDialog.openError;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class CreateJUnitTestAction extends CreateJUnitTestActionSupport implements IObjectActionDelegate {

	/**
	 * Constructor for Action1.
	 */
	public CreateJUnitTestAction() {
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
	 *
	 * @see IActionDelegate#run(IAction)
	 */
	@Override
	public void run(final IAction action) {
		if (!(this.selection instanceof IStructuredSelection)) {
			return;
		}
		Object[] compUnits = ((IStructuredSelection) this.selection).toArray();
		if (compUnits.length > 1) {
			openError(this.page.getWorkbenchWindow().getShell(), "Error", "Please select only class and try again.");
			return;
		}
		try {
			super.runAction((ICompilationUnit) compUnits[0], null);
		} catch (Exception ex) {
			ex.printStackTrace();
			openError(this.page.getWorkbenchWindow().getShell(), "Error", "Some error occurred " + ex.getMessage());
		}
	}

	/**
	 * @param compUnit
	 */
	@Override
	protected IJavaElement findSelectedJavaElement(final ICompilationUnit compUnit) throws Exception {
		return null;
	}

	/**
	 *
	 * @param in
	 */
	private void showEditor(final IEditorInput in) {
		Shell shell = new Shell(SWT.CLOSE | SWT.TITLE | SWT.MIN | SWT.MAX | SWT.RESIZE);
		MessageDialog.openInformation(shell, "Fast Code Plug-in", "Editor : " + in.getClass().getName());
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	@Override
	public void selectionChanged(final IAction action, final ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			this.selection = selection;
		}
	}

	protected CompilationUnit parse(final ICompilationUnit lwUnit) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(lwUnit); // set source
		parser.setResolveBindings(true); // we need bindings later on
		return (CompilationUnit) parser.createAST(null /* IProgressMonitor */); // parse
	}

}
