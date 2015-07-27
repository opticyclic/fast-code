package org.fastcode.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UnitTestReturnFormatSettings {

	Map<String, Map<String, List<UnitTestReturnFormatOption>>>	resultFormatMap					= new LinkedHashMap<String, Map<String, List<UnitTestReturnFormatOption>>>();

	private static UnitTestReturnFormatSettings					unitTestReturnFormatSettings	= new UnitTestReturnFormatSettings();
	Map<String, String>											parentMap						= new HashMap<String, String>();

	private UnitTestReturnFormatSettings() {

	}

	/**
	 *
	 * @return
	 */
	public static UnitTestReturnFormatSettings getInstance() {
		return unitTestReturnFormatSettings;
	}

	/**
	 * @return the resultFormatMap
	 */
	public Map<String, Map<String, List<UnitTestReturnFormatOption>>> getResultFormatMap() {
		return this.resultFormatMap;
	}

	public Map<String, String> getParentMap() {
		return this.parentMap;
	}

}
