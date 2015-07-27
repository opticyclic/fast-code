/**
 *
 */
package org.fastcode.util;

import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.GLOBAL_ADD_TYPE_END;
import static org.fastcode.common.FastCodeConstants.GLOBAL_MAKE_PLURAL_ADD_TYPE_END;
import static org.fastcode.common.FastCodeConstants.SPACE;
import static org.fastcode.setting.GlobalSettings.getInstance;

import org.eclipse.jdt.core.IType;
import org.fastcode.setting.GlobalSettings;

/**
 * @author Gautam
 *
 */
public class ParamNameMaker {

	/**
	 *
	 * @param paramType
	 * @param type
	 * @return
	 */
	public static String makeName(final IType paramType, final IType type) {
		String varName = paramType.getElementName().substring(0, 1).toLowerCase() + paramType.getElementName().substring(1);
		final GlobalSettings globalSettings = getInstance();

		final String[] extensions = globalSettings.getParameterizedIgnoreExtensions().split(SPACE);
		if (extensions != null) {
			for (final String extension : extensions) {
				if (varName.endsWith(extension)) {
					varName = varName.replaceAll(extension + "$", EMPTY_STR);
					break;
				}
			}
		}

		if (globalSettings.getParameterizedNameStrategy().equals(GLOBAL_ADD_TYPE_END)) {
			varName += type.getElementName();
		} else {
			if (varName.endsWith("y")) {
				varName = varName.replaceAll("y$", "ies");
			} else if (varName.endsWith("s")) {
				varName += "es";
			} else {
				varName += "s";
			}
			if (globalSettings.getParameterizedNameStrategy().equals(GLOBAL_MAKE_PLURAL_ADD_TYPE_END)) {
				varName += type.getElementName();
			}
		}

		return varName;
	}
}
