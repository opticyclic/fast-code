/**
 *
 */
package org.fastcode.util;

import org.eclipse.jdt.core.IType;
import org.fastcode.preferences.JunitPreferences;

/**
 * @author Gautam
 *
 */
public class JunitPreferencesAndType {

	final private JunitPreferences	junitPreferences;
	final private IType				type;

	/**
	 * @param junitPreferences
	 * @param type
	 */
	public JunitPreferencesAndType(final JunitPreferences junitPreferences, final IType type) {
		this.junitPreferences = junitPreferences;
		this.type = type;
	}

	/**
	 *
	 * @return
	 */
	public JunitPreferences getJunitPreferences() {
		return this.junitPreferences;
	}

	/**
	 *
	 * @return
	 */
	public IType getType() {
		return this.type;
	}
}
