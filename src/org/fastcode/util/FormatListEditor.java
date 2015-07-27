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
package org.fastcode.util;

import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.NEWLINE;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Gautam
 *
 */
public class FormatListEditor extends ListEditor {

	/**
	 * @param name
	 * @param labelText
	 * @param parent
	 */
	public FormatListEditor(final String name, final String labelText, final Composite parent) {
		super(name, labelText, parent);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.jface.preference.ListEditor#createList(java.lang.String[])
	 */
	@Override
	protected String createList(final String[] items) {
		String ret = EMPTY_STR;
		int cnt = 0;
		for (final String item : items) {
			ret += cnt < items.length - 1 ? item + NEWLINE : item;
			cnt++;
		}
		return ret;
	}

	@Override
	protected DialogPage getPage() {
		// TODO Auto-generated method stub
		return super.getPage();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.preference.ListEditor#getNewInputObject()
	 */
	@Override
	protected String getNewInputObject() {
		// TODO Auto-generated method stub
		return "";
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.jface.preference.ListEditor#parseString(java.lang.String)
	 */
	@Override
	protected String[] parseString(final String stringList) {
		return stringList.split("\n");
	}

}
