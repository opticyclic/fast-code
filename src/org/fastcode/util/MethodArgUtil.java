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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Gautam
 *
 */
public class MethodArgUtil {

	private static Map<String, String>	argValueMap	= new HashMap<String, String>();
	static {
		argValueMap.put("String", "\"\"");
		argValueMap.put("int", "1");
		argValueMap.put("long", "1l");
		argValueMap.put("boolean", "false");
		argValueMap.put("Long", "1l");
		argValueMap.put("Integer", "1");
		argValueMap.put("Boolean", "false");
		argValueMap.put("String[]", "new String[1]");
	}

	/**
	 * @param argType
	 * @return
	 */
	public static String getArgValue(final String argType) {
		return argValueMap.get(argType);
	}
}
