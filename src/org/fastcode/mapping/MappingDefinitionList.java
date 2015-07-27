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
package org.fastcode.mapping;

import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.MAPPING;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * @author Gautam
 *
 */
public class MappingDefinitionList {

	private List<MappingDefinition>			mappingDefinitions		= new ArrayList<MappingDefinition>();

	private static MappingDefinitionList	mappingDefinitionList	= new MappingDefinitionList();

	private final Set						changeListeners			= new HashSet();

	//	IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	final IPreferenceStore					preferenceStore			= new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);

	public static final String[]			columnNames				= { "category", "source", "destination" };

	/**
	 *
	 */
	protected MappingDefinitionList() {

		this.mappingDefinitions = new ArrayList<MappingDefinition>();
		this.mappingDefinitions.add(getMappingDefinitionDefinitionByCategory("Hibernate-pojo-hbm", 1,
				"(${ANY_PACKAGE}.domain).(${ANY_CLASS})", "${1}.${2}.hbm.xml"));
		this.mappingDefinitions.add(getMappingDefinitionDefinitionByCategory("Hibernate-hbm-pojo", 2,
				"(${ANY_PACKAGE}.domain).(${ANY_CLASS}).hbm.xml", "${1}.${2}"));
		this.mappingDefinitions.add(getMappingDefinitionDefinitionByCategory("Dao-Service", 3, "(${ANY_PACKAGE}).dao.(${ANY_CLASS})DAO",
				"${1}.service.${2}Service"));
		this.mappingDefinitions.add(getMappingDefinitionDefinitionByCategory("Service-Dao", 4,
				"(${ANY_PACKAGE}).service.(${ANY_CLASS})Service", "${1}.dao.${2}DAO"));
		this.mappingDefinitions.add(getMappingDefinitionDefinitionByCategory("Service-UI", 5,
				"(${ANY_PACKAGE}).service.(${ANY_CLASS})Service", "${1}.ui.action.${2}Handler"));
		this.mappingDefinitions.add(getMappingDefinitionDefinitionByCategory("UI-Service", 6,
				"(${ANY_PACKAGE}).ui.action.(${ANY_CLASS})Handler", "${1}.service.${2}Service"));
		this.mappingDefinitions.add(getMappingDefinitionDefinitionByCategory("Dao-UI", 3, "(${ANY_PACKAGE}).dao.(${ANY_CLASS})DAO",
				"${1}.ui.action.${2}Action"));
		this.mappingDefinitions.add(getMappingDefinitionDefinitionByCategory("UI-Dao", 4,
				"(${ANY_PACKAGE}).ui.action.(${ANY_CLASS})ui.action", "${1}.dao.${2}DAO"));
	}

	/**
	 *
	 * @return
	 */
	public static MappingDefinitionList getInstance() {

		return mappingDefinitionList;
	}

	/**
	 *
	 * @param mappingDefinition
	 */
	public void addMappingDefinition(final MappingDefinition mappingDefinition) {
		this.mappingDefinitions.add(mappingDefinition);
		final Iterator<IMappingListViewer> it = this.changeListeners.iterator();
		while (it.hasNext()) {
			it.next().addMappingDefinition(mappingDefinition);
		}
	}

	/**
	 *
	 * @param mappingDefinition
	 */
	public void removeMappingDefinition(final MappingDefinition mappingDefinition) {
		this.mappingDefinitions.remove(mappingDefinition);
		final Iterator<IMappingListViewer> it = this.changeListeners.iterator();
		while (it.hasNext()) {
			it.next().removeMappingDefinition(mappingDefinition);
		}
	}

	/**
	 *
	 * @param mappingDefinition
	 */
	public void updateMappingDefinition(final MappingDefinition mappingDefinition) {
		final Iterator it = this.changeListeners.iterator();
		while (it.hasNext()) {
			((IMappingListViewer) it.next()).updateMappingDefinition(mappingDefinition);
		}

		saveMapping(mappingDefinition, "source", mappingDefinition.getSource());
		saveMapping(mappingDefinition, "destination", mappingDefinition.getDestination());
	}

	/**
	 *
	 * @param category
	 * @param row
	 * @param defaultSource
	 * @param defaultDestination
	 * @return
	 */
	private MappingDefinition getMappingDefinitionDefinitionByCategory(final String category, final int row, final String defaultSource,
			final String defaultDestination) {

		String source = null, destination = null;

		if (this.preferenceStore.getInt(MAPPING + category) == 0) {
			this.preferenceStore.setValue(MAPPING + category, row);
			this.preferenceStore.setValue(MAPPING + row + "source", defaultSource);
			this.preferenceStore.setValue(MAPPING + row + "destination", defaultDestination);
		} else {
			source = this.preferenceStore.getString(MAPPING + row + "source");
			destination = this.preferenceStore.getString(MAPPING + row + "destination");
		}

		return new MappingDefinition(category, source == null ? defaultSource : source, destination == null ? defaultDestination
				: destination);
	}

	/**
	 *
	 * @param mappingDefinition
	 * @param row
	 * @param column
	 */
	private void saveMapping(final MappingDefinition mappingDefinition, final String column, final String newValue) {
		final int row = this.preferenceStore.getInt(MAPPING + mappingDefinition.getCategory());
		final String oldValue = this.preferenceStore.getString(MAPPING + row + column);
		if (!oldValue.equals(newValue)) {
			this.preferenceStore.setValue(MAPPING + row + column, newValue);
		}
	}

	/**
	 * @param viewer
	 */
	public void removeChangeListener(final IMappingListViewer viewer) {
		this.changeListeners.remove(viewer);
	}

	/**
	 * @param viewer
	 */
	public void addChangeListener(final IMappingListViewer viewer) {
		this.changeListeners.add(viewer);
	}

	public List<MappingDefinition> getMappingDefinitions() {
		return this.mappingDefinitions;
	}

	public void setMappingDefinitions(final List<MappingDefinition> mappingDefinitions) {
		this.mappingDefinitions = mappingDefinitions;
	}
}
