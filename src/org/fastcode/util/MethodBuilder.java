/**
 *
 */
package org.fastcode.util;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;

/**
 * @author Gautam
 *
 */
public interface MethodBuilder {

	/**
	 *
	 * @param toType
	 * @param createSimilarDescriptorClass
	 * @return
	 */
	public IMethod buildMethod(IMethod method, IType toType, CreateSimilarDescriptor createSimilarDescriptor,
			CreateSimilarDescriptorClass createSimilarDescriptorClass) throws Exception;

	/**
	 * @param method
	 * @param toType
	 * @see org.fastcode.util.AbstractMethodBuilder#buildMethod
	 * @throws Exception
	 */
	public IMethod buildMethod(IMethod method, IType toType) throws Exception;
}
