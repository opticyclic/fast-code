package org.fastcode;

import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.preferences.PreferenceConstants.P_ENABLE_AUTO_CHECKIN;

/*import org.eclipse.core.expressions.PropertyTester;*/
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class FastCodePropertyTester /*extends PropertyTester*/ {

	private IPreferenceStore		preferenceStore;

	/*public boolean test(final Object arg0, final String property, final Object[] arg2, final Object arg3) {
		this.preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		if ("versioncontrol".equals(property)) {
			System.out.println(this.preferenceStore.getBoolean(P_ENABLE_AUTO_CHECKIN));
			return this.preferenceStore.getBoolean(P_ENABLE_AUTO_CHECKIN);
			}
		return false;
	}*/

}
