package org.fastcode.common;

import org.eclipse.jdt.core.IMember;
import org.eclipse.swt.widgets.Shell;

public class MemberSelectionDialog extends FastCodeSelectionDialog {

	/**
	 * @param parent
	 * @param title
	 * @param message
	 * @param members
	 * @param multipleSelection
	 */
	public MemberSelectionDialog(final Shell parent, final String title, final String message, final IMember[] members,
			final boolean multipleSelection) {
		super(parent, title, message, members, 0, multipleSelection);
	}

}
