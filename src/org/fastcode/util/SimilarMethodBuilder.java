package org.fastcode.util;

import static org.eclipse.jdt.core.Signature.getSignatureSimpleName;
import static org.fastcode.common.FastCodeConstants.ASTERISK;
import static org.fastcode.common.FastCodeConstants.COMMA;
import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.LEFT_PAREN;
import static org.fastcode.common.FastCodeConstants.METHOD_RETURN_TYPE_VOID;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.common.FastCodeConstants.RIGHT_PAREN;
import static org.fastcode.common.FastCodeConstants.SEMICOLON;
import static org.fastcode.common.FastCodeConstants.SPACE;
import static org.fastcode.setting.GlobalSettings.getInstance;
import static org.fastcode.util.SourceUtil.isJavaLangType;
import static org.fastcode.util.SourceUtil.isNativeType;
import static org.fastcode.util.StringUtil.createDefaultInstance;
import static org.fastcode.util.StringUtil.parseType;
import static org.fastcode.util.StringUtil.replacePlaceHolders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.Signature;
import org.fastcode.common.FastCodeType;
import org.fastcode.common.Pair;
import org.fastcode.setting.GlobalSettings;

public class SimilarMethodBuilder extends AbstractMethodBuilder implements MethodBuilder {

	/**
	 *
	 * @param fastCodeContext
	 */
	public SimilarMethodBuilder(final FastCodeContext fastCodeContext) {
		super(fastCodeContext);
	}

	/**
	 *
	 * @param method
	 * @param instance
	 * @param createSimilarDescriptorClass
	 * @return
	 * @throws Exception
	 */
	@Override
	protected String getMethodBody(final IMethod method, final String instance, final IType toType,
			final List<Pair<String, String>> methodParms, final CreateSimilarDescriptorClass createSimilarDescriptorClass) throws Exception {
		final StringBuilder methodBody = new StringBuilder();
		final String returnType = getSignatureSimpleName(method.getReturnType());
		if (createSimilarDescriptorClass == null || !createSimilarDescriptorClass.isCreateMethodBody()) {
			return METHOD_RETURN_TYPE_VOID.equals(returnType) ? "return;" : "return null;";
		}

		final StringBuilder methodInvcFrag = new StringBuilder(instance + DOT + method.getElementName());
		methodInvcFrag.append(LEFT_PAREN);
		int count = 0;
		final StringBuilder methodConvBody = new StringBuilder();

		final String[] parameterTypes = method.getParameterTypes();

		for (final Pair<String, String> pair : methodParms) {
			if (createSimilarDescriptorClass.isConvertMethodParam()
					&& !pair.getLeft().equals(getSignatureSimpleName(parameterTypes[count]))) {

				methodConvBody
						.append(convertMethodFragment(pair.getLeft(), method.getParameterNames()[count],
								getSignatureSimpleName(parameterTypes[count]),
								createDefaultInstance(getSignatureSimpleName(parameterTypes[count]))));

				methodInvcFrag.append(createDefaultInstance(getSignatureSimpleName(parameterTypes[count])));
			} else {
				methodInvcFrag.append(pair.getRight());
			}
			methodInvcFrag.append(methodParms.indexOf(pair) < methodParms.size() - 1 ? COMMA + SPACE : EMPTY_STR);
			count++;
		}

		methodInvcFrag.append(RIGHT_PAREN + SEMICOLON + NEWLINE);
		String methodPostInvcFrag = EMPTY_STR;
		final GlobalSettings globalSettings = getInstance();

		if (!METHOD_RETURN_TYPE_VOID.equals(returnType)) {
			final String originalVariableName = getObjectNameFromMethod(method.getElementName());
			String returnVariable = originalVariableName;
			if (!createSimilarDescriptorClass.isConvertMethodParam() || isNativeType(returnType) || isJavaLangType(returnType, toType)) {
				methodInvcFrag.insert(0, returnType + SPACE + returnVariable + " = ");
				methodInvcFrag.append(NEWLINE);
			} else {
				final FastCodeType paramType = parseType(returnType, method.getCompilationUnit());
				IType convType = convertType(paramType.getName(), method.getDeclaringType(), toType, createSimilarDescriptorClass);
				methodInvcFrag.insert(0, returnType + SPACE + returnVariable + " = ");
				methodInvcFrag.append(NEWLINE);
				if (convType != null) {
					final Matcher matcher = parmPattern.matcher(returnType);
					if (matcher.matches()) {
						final String baseType = matcher.group(1);
						final String parmType = matcher.group(2);
						if (!isJavaLangType(parmType, toType)) {
							if (baseType.equals("List")) {
								// methodPostInvcFrag =
								// globalSettings.getTemplateForList();
								convType = convertType(parmType, method.getDeclaringType(), toType, createSimilarDescriptorClass);
								if (convType != null) {
									returnVariable = "newList";
									final Map<String, Object> placeHolders = new HashMap<String, Object>();
									// toType.getCompilationUnit().createImport("",
									// null, null);
									placeHolders.put("original_param_class", parmType);
									placeHolders.put("original_param_name", createDefaultInstance(parmType));
									placeHolders.put("original_var_name", originalVariableName);
									placeHolders.put("converted_var_name", returnVariable);
									placeHolders.put("converted_param_class", convType.getElementName());
									placeHolders.put("converted_param_name", createDefaultInstance(convType.getElementName()));
									final String convertMethodFragment = convertMethodFragment(parmType, createDefaultInstance(parmType),
											convType.getElementName(), createDefaultInstance(convType.getElementName()));
									placeHolders.put("converted_var_name", returnVariable);
									placeHolders.put("conversion_code", convertMethodFragment);
									methodPostInvcFrag = replacePlaceHolders(globalSettings.getTemplateForList(), placeHolders);
								}
							} else if (baseType.equals("Map")) {

							}
						}
					} else {

						returnVariable = createDefaultInstance(convType.getElementName());
						methodPostInvcFrag = convertMethodFragment(returnType, originalVariableName, convType.getElementName(),
								returnVariable);
						methodPostInvcFrag += SEMICOLON + NEWLINE;
					}
				}
			}
			methodPostInvcFrag += "return " + returnVariable + SEMICOLON + NEWLINE;
		}

		methodBody.append(methodConvBody.toString() + methodInvcFrag + methodPostInvcFrag);

		return methodBody.toString();
	}

	/**
	 *
	 * @param toType
	 * @param method
	 * @param createSimilarDescriptorClass
	 * @return
	 */
	@Override
	protected IMethod findMethodsibling(final IType toType, final IMethod method,
			final CreateSimilarDescriptorClass createSimilarDescriptorClass) {
		return null;
	}

	/**
	 *
	 * @param method
	 * @param toType
	 * @param methodParms
	 * @param placeHolders
	 *
	 * @return
	 *
	 */
	@Override
	protected String getMethodComments(final IMethod method, final IType toType, final List<Pair<String, String>> methodParms,
			final CreateSimilarDescriptor createSimilarDescriptor, final Map<String, Object> placeHolders) throws Exception {
		final StringBuilder methodComments = new StringBuilder("/**" + NEWLINE);

		methodComments.append(SPACE + ASTERISK + NEWLINE);
		if (methodParms != null) {
			for (final Pair<String, String> pair : methodParms) {
				methodComments.append(SPACE + "* @param" + SPACE + pair.getRight() + NEWLINE);
			}
		}
		if (createSimilarDescriptor != null && !createSimilarDescriptor.isDifferentName()) {
			methodComments.append(SPACE + "* @see " + method.getDeclaringType().getFullyQualifiedName() + LEFT_PAREN);
			int count = 0;
			for (final String pType : method.getParameterTypes()) {
				methodComments.append(Signature.getSignatureSimpleName(pType));
				methodComments.append(count < method.getNumberOfParameters() - 1 ? COMMA : EMPTY_STR);
				count++;
			}
			methodComments.append(RIGHT_PAREN + NEWLINE);
		}
		methodComments.append(" */");

		return methodComments.toString();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.fastcode.util.AbstractMethodBuilder#getMethodArgs(org.eclipse.jdt
	 * .core.IMethod, org.eclipse.jdt.core.IType, java.util.List, java.util.Map)
	 */
	@Override
	protected String getMethodArgs(final IMethod method, final IType toType, final List<Pair<String, String>> methodParms,
			final Map<String, Object> placeHolders) throws Exception {
		final StringBuilder methodArgs = new StringBuilder();
		final GlobalSettings globalSettings = GlobalSettings.getInstance();

		if (methodParms != null && !methodParms.isEmpty()) {
			for (final Pair<String, String> pair : methodParms) {
				if (globalSettings.isFinalModifierForMethodArgs()) {
					methodArgs.append("final ");
				}
				methodArgs.append(pair.getLeft() + SPACE + pair.getRight());
				methodArgs.append(methodParms.indexOf(pair) < methodParms.size() - 1 ? COMMA + SPACE : EMPTY_STR);
			}
		}

		return methodArgs.toString();
	}

	/**
	 * @param method
	 * @param toType
	 * @param createSimilarDescriptorClass
	 * @param placeHolders
	 *
	 *
	 */
	@Override
	protected String getAnnotations(final IMethod method, final IType toType, final String[] methodAnnotations,
			final Map<String, Object> placeHolders) throws Exception {
		if (methodAnnotations == null || methodAnnotations.length == 0) {
			return null;
		}
		return SourceUtil.createAnnotations(this.fastCodeContext, toType.getCompilationUnit(), toType.getJavaProject(), methodAnnotations,
				placeHolders);
	}

}
