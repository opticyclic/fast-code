/**
 *
 */
package org.fastcode.util;

import static org.eclipse.jdt.core.Signature.getSignatureSimpleName;
import static org.fastcode.common.FastCodeConstants.COMMA;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.SPACE;
import static org.fastcode.util.StringUtil.createDefaultInstance;
import static org.fastcode.util.StringUtil.parseType;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.fastcode.common.FastCodeType;
import org.fastcode.common.Pair;

/**
 * @author Gautam
 * 
 */
public class DiffNameSimilarMethodBuilder extends SimilarMethodBuilder implements MethodBuilder {

	/**
	 * 
	 * @param fastCodeContext
	 */
	public DiffNameSimilarMethodBuilder(final FastCodeContext fastCodeContext) {
		super(fastCodeContext);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fastcode.util.AbstractMethodBuilder#createMethodBody(org.eclipse.
	 * jdt.core.IMethod, java.lang.String, org.eclipse.jdt.core.IType,
	 * org.fastcode.util.CreateSimilarDescriptorClass)
	 */
	@Override
	protected String getMethodBody(final IMethod method, final String instance, final IType toType,
			final List<Pair<String, String>> methodParms, final CreateSimilarDescriptorClass createSimilarDescriptorClass) throws Exception {
		final String returnType = getSignatureSimpleName(method.getReturnType());
		if (returnType == null || returnType.equals("void")) {
			return "return;";
		} else {
			return "return null;";
		}
	}

	/**
	 * @param createSimilarDescriptor
	 * @param createSimilarDescriptorClass
	 * @param method
	 * 
	 */
	@Override
	protected List<Pair<String, String>> makeMethodParms(final CreateSimilarDescriptor createSimilarDescriptor,
			final CreateSimilarDescriptorClass createSimilarDescriptorClass, final IMethod method, final IType toType) throws Exception {
		final List<Pair<String, String>> methodParms = new ArrayList<Pair<String, String>>();
		int count = 0;
		final String replacePart = createSimilarDescriptor.getReplacePart();
		final String replaceValue = createSimilarDescriptor.getReplaceValue();

		for (final String type : method.getParameterTypes()) {

			StringBuilder newType;
			String newName = method.getParameterNames()[count];
			final FastCodeType fastCodeType = parseType(getSignatureSimpleName(type), method.getCompilationUnit());

			final IType modType = SourceUtil.convertTypeDifferentName(method.getDeclaringType(), fastCodeType.getName(), replacePart,
					replaceValue);
			if (modType != null) {
				newType = new StringBuilder(modType.getElementName());
				newName = createDefaultInstance(modType.getElementName());
				insertImport(toType, modType);

				if (!fastCodeType.getParameters().isEmpty()) {
					newType.append("<");
					int count1 = 0;
					for (final FastCodeType parm : fastCodeType.getParameters()) {
						final IType modParmType = SourceUtil.convertTypeDifferentName(method.getDeclaringType(), parm.getName(),
								replacePart, replaceValue);
						if (modParmType != null) {
							newType.append(modParmType.getElementName());
							if (modParmType != null) {
								insertImport(toType, modParmType);
							}
						} else {
							newType.append(parm.getName());
						}
						newType.append(count1 < fastCodeType.getParameters().size() - 1 ? COMMA + SPACE : EMPTY_STR);
						count1++;
					}
					newType.append(">");
				}
			} else {
				newType = new StringBuilder(getSignatureSimpleName(type));
			}
			methodParms.add(new Pair<String, String>(newType.toString(), newName));
			count++;
		}

		return methodParms;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fastcode.util.AbstractMethodBuilder#findMethodsibling(org.eclipse
	 * .jdt.core.IType, org.eclipse.jdt.core.IMethod,
	 * org.fastcode.util.CreateSimilarDescriptorClass)
	 */
	@Override
	protected IMethod findMethodsibling(final IType toType, final IMethod method,
			final CreateSimilarDescriptorClass createSimilarDescriptorClass) {
		return null;
	}

}
