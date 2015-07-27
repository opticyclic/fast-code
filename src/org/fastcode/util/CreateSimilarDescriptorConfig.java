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

/**
 * @author Gautam
 *
 */
public class CreateSimilarDescriptorConfig {

	private final String	configType;
	private final String	configLocation;
	private final String	configFileName;
	private final String	configLocale;
	private final String	configPattern;
	private final String	conversionPattern;
	private final String	configStartPattern;
	private final String	configHeaderPattern;
	private final String	configEndPattern;

	/**
	 * @param configType
	 * @param configFileName
	 * @param conversionPattern
	 * @param configLocation
	 * @param configLocale
	 * @param configPattern
	 * @param configHeaderPattern
	 * @param configStartPattern
	 * @param configEndPattern
	 *
	 */
	public CreateSimilarDescriptorConfig(final String configType, final String configFileName, final String conversionPattern,
			final String configLocation, final String configLocale, final String configHeaderPattern, final String configStartPattern,
			final String configPattern, final String configEndPattern) {
		this.configType = configType;
		this.configFileName = configFileName;
		this.conversionPattern = conversionPattern;
		this.configLocale = configLocale;
		this.configPattern = configPattern;
		this.configHeaderPattern = configHeaderPattern;
		this.configStartPattern = configStartPattern;
		this.configLocation = configLocation;
		this.configEndPattern = configEndPattern;
	}

	/**
	 * @return the configLocation
	 */
	public String getConfigLocation() {
		return this.configLocation;
	}

	/**
	 * @return the configFileName
	 */
	public String getConfigFileName() {
		return this.configFileName;
	}

	/**
	 * @return the configPattern
	 */
	public String getConfigPattern() {
		return this.configPattern;
	}

	/**
	 * @return the configStartPattern
	 */
	public String getConfigStartPattern() {
		return this.configStartPattern;
	}

	/**
	 * @return the configEndPattern
	 */
	public String getConfigEndPattern() {
		return this.configEndPattern;
	}

	public String getConfigType() {
		return this.configType;
	}

	public String getConfigLocale() {
		return this.configLocale;
	}

	/**
	 *
	 * @return
	 */
	public String getConversionPattern() {
		return this.conversionPattern;
	}

	/**
	 *
	 * @return
	 */
	public String getConfigHeaderPattern() {
		return this.configHeaderPattern;
	}
}
