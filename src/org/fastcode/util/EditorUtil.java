/**
 *
 */
package org.fastcode.util;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;

/**
 * @author Gautam
 *
 */
public class EditorUtil {

	/**
	 * @param compUnit
	 * @param member
	 * @return
	 */
	public static IMember findMember(final ICompilationUnit compUnit, final IMember member) {
		final IType type = compUnit.findPrimaryType();
		if (member == null) {
			return null;
		}
		switch (member.getElementType()) {
		case IJavaElement.FIELD:
			return type.getField(member.getElementName());
		case IJavaElement.METHOD:
			final IMethod method = (IMethod) member;
			return type.getMethod(member.getElementName(), method.getParameterTypes());
		default:
			return null;
		}
	}
}
