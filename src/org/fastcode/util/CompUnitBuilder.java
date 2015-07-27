package org.fastcode.util;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;

public interface CompUnitBuilder {
	/**
	 *
	 * @param toType
	 * @param createSimilarDescriptorClass
	 * @return
	 */
	public ICompilationUnit buildCompUnit(final IPackageFragment packageFragment, final FastCodeContext fastCodeContext,
			final CreateSimilarDescriptorClass createSimilarDescriptorClass, final String className, FastCodeConsole fastCodeConsole)
			throws Exception;
}
