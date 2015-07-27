/**
 *
 */
package org.fastcode.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.fastcode.preferences.JunitPreferences;

/**
 * @author Gautam
 *
 */
public class FastCodeContext {

	private final List<FastCodeResource>								resources					= new ArrayList<FastCodeResource>();
	private final Map<CreateSimilarDescriptorClass, ICompilationUnit>	compilationUnitRegsistry	= new HashMap<CreateSimilarDescriptorClass, ICompilationUnit>();
	private final IType													fromType;
	private final Map<String, Object>									placeHolders				= new HashMap<String, Object>();
	private final boolean												unitTest;
	private final JunitPreferences										junitPreferences;

	/**
	 *
	 */
	public FastCodeContext() {
		this(null, false, null);
	}

	/**
	 * @param fromType
	 */
	public FastCodeContext(final IType fromType) {
		this(fromType, false, null);
	}

	/**
	 * @param fromType
	 * @param unitTest
	 */
	public FastCodeContext(final IType fromType, final boolean unitTest, final JunitPreferences junitPreferences) {
		this.fromType = fromType;
		this.unitTest = unitTest;
		this.junitPreferences = junitPreferences;
	}

	/**
	 *
	 * @return
	 */
	public List<FastCodeResource> getResources() {
		return this.resources;
	}

	/**
	 * @param compilationUnit
	 * @return
	 */
	public FastCodeResource findResource(final ICompilationUnit compilationUnit) {
		for (final FastCodeResource fastCodeResource : this.resources) {
			if (fastCodeResource.getResource().equals(compilationUnit.getResource())) {
				return fastCodeResource;
			}
		}
		return null;
	}

	/**
	 *
	 * @param fastCodeResource
	 */
	public void addResource(final FastCodeResource fastCodeResource) {
		if (this.resources.contains(fastCodeResource)) {
			return;
		}
		for (final FastCodeResource resource : this.resources) {
			if (resource.getResource().equals(fastCodeResource.getResource())) {
				return;
			}
		}
		this.resources.add(fastCodeResource);
	}

	/**
	 *
	 * @return
	 */
	public IType getFromType() {
		return this.fromType;
	}

	/**
	 * @return the placeHolders
	 */
	public Map<String, Object> getPlaceHolders() {
		return this.placeHolders;
	}

	/**
	 *
	 * @param name
	 * @param value
	 */
	public void addToPlaceHolders(final String name, final Object value) {
		if (this.placeHolders.containsKey(name)) {
			// throw new RuntimeException("Name is already present" + name);
		}
		this.placeHolders.put(name, value);
	}

	/**
	 * @return the compilationUnitRegsistry
	 */
	public ICompilationUnit getCompilationUnitRegsistry(final CreateSimilarDescriptorClass createSimilarDescriptorClass) {
		return this.compilationUnitRegsistry.get(createSimilarDescriptorClass);
	}

	/**
	 *
	 * @return
	 */
	public Map<CreateSimilarDescriptorClass, ICompilationUnit> getCompilationUnitRegsistry() {
		return this.compilationUnitRegsistry;
	}

	/**
	 *
	 * @param createSimilarDescriptorClass
	 * @param compilationUnit
	 * @return
	 */
	public void addCompilationUnitToRegsistry(final CreateSimilarDescriptorClass createSimilarDescriptorClass,
			final ICompilationUnit compilationUnit) {
		if (!this.compilationUnitRegsistry.containsKey(createSimilarDescriptorClass)) {
			// compilationUnitRegsistry.put(createSimilarDescriptorClass, new
			// FastCodeResource(compilationUnit.getResource()));
			this.compilationUnitRegsistry.put(createSimilarDescriptorClass, compilationUnit);
		}
	}

	/**
	 * @return the unitTest
	 */
	public boolean isUnitTest() {
		return this.unitTest;
	}

	/**
	 * @return the junitPreferences
	 */
	public JunitPreferences getJunitPreferences() {
		return this.junitPreferences;
	}
}
