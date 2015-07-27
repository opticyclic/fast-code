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

import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.util.MessageUtil.showStatus;

import java.util.Iterator;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.IWorkingCopyManager;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.fastcode.util.CreateSimilarDescriptor;
import org.fastcode.util.FastCodeConsole;

public class DepencyInjectAction implements IObjectActionDelegate {

	protected StructuredSelection	selection;
	protected IWorkbenchPage		page;
	protected FastCodeConsole		fastCodeConsole	= FastCodeConsole.getInstance();

	/**
	 * Constructor for Action.
	 */
	public DepencyInjectAction() {
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
	 * @see IActionDelegate#run(IAction)
	 */
	@Override
	public void run(final IAction action) {
		String errorMessage = EMPTY_STR;
		final IWorkingCopyManager manager = JavaUI.getWorkingCopyManager();

		final IEditorPart editorPart = this.page.getActiveEditor();
		ICompilationUnit compUnit = null;
		final Shell shell = new Shell();

		final ICompilationUnit targetUnit = manager.getWorkingCopy(editorPart.getEditorInput());

		if (this.selection.getFirstElement() instanceof ICompilationUnit) {
			compUnit = (ICompilationUnit) this.selection.getFirstElement();
		}

		if (targetUnit == null || !targetUnit.exists()) {
			MessageDialog.openError(shell, "Fast Code Plug-in", "Ooops, No class in open in editor. ");
			return;
		}

		boolean becmeWrkngCpy = false;
		try {
			if (!targetUnit.isWorkingCopy()) {
				becmeWrkngCpy = true;
				targetUnit.becomeWorkingCopy(null);
			}
			if (targetUnit.findPrimaryType().isInterface()) {
				MessageDialog.openError(shell, "Fast Code Plug-in", "Cannot inject in an interface. ");
				return;
			}

			if (!checkIfEligibleForDependency(compUnit.findPrimaryType(), targetUnit)) {

				if (!MessageDialog.openQuestion(shell, "Spring Dependency",
						"It is recommended that this class not be injected into the current editor, Do you still want to proceed?")) {
					return;
				}
			}

			final Iterator<ICompilationUnit> it = this.selection.iterator();
			final IType[] typesToInject = new IType[this.selection.size()];

			int numInjected = 0;
			while (it.hasNext()) {

				compUnit = it.next();
				typesToInject[numInjected++] = compUnit.findPrimaryType();

				targetUnit.createImport(compUnit.findPrimaryType().getFullyQualifiedName(), null, null);
				final CreateSimilarDescriptor createSimilarDescriptor = CreateSimilarDescriptor.getCreateSimilarDescriptor(compUnit
						.findPrimaryType());
				//createInstanceVariable(targetUnit.findPrimaryType(), compUnit.findPrimaryType(), createSimilarDescriptor.getCreateSimilarDescriptorClasses()[0], null);
			}
			if (numInjected > 0) {
				doAfterInjectDependency(typesToInject, targetUnit);
			}
		} catch (final JavaModelException ex) {
			ex.printStackTrace();
			errorMessage = ex.getMessage();
		} catch (final Exception ex) {
			errorMessage = ex.getMessage();
			ex.printStackTrace();
		} finally {
			try {
				//targetUnit.commitWorkingCopy(false, null);
				if (becmeWrkngCpy) {
					targetUnit.discardWorkingCopy();
				}
			} catch (final JavaModelException ex) {
				ex.printStackTrace();
			}
		}

		showStatus(shell, "Fast Code Plug-in", errorMessage);
	}

	/**
	 *
	 * @param typesToInject
	 * @param targetUnit
	 * @throws Exception
	 */
	public void doAfterInjectDependency(final IType[] typesToInject, final ICompilationUnit targetUnit) throws Exception {
		// Do nothing in this class
	}

	/**
	 *
	 * @param typeToInject
	 * @param targetUnit
	 * @return
	 * @throws Exception
	 */
	public boolean checkIfEligibleForDependency(final IType typeToInject, final ICompilationUnit targetUnit) {
		// Do nothing in this class
		return true;
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	@Override
	public void selectionChanged(final IAction action, final ISelection selection) {
		this.selection = (StructuredSelection) selection;
	}

}
